package example.cat.com.candlechartdemo.ktd.candle

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.widget.LinearLayout
import com.alibaba.fastjson.JSON
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.utils.Utils
import example.cat.com.candlechartdemo.normal.KlinePresenter
import org.jetbrains.anko.*
import org.jetbrains.anko.design.tabLayout
import java.io.IOException

class CandleChartActivity : AppCompatActivity() {
  private lateinit var candleStickChart: BlinnnkCandleStickChart
  private val normalTabTextColor = Color.rgb(152, 152, 152)
  private val selectedTabTextColor = Color.rgb(67, 200, 135)
  private val tabIndicatorColor = selectedTabTextColor
  private val tabHeight = Utils.convertDpToPixel(2f).toInt()
  private var dataSet = mutableListOf<Array<String>>()
  private val candleEntrySet = arrayListOf<CandleEntry>()
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    verticalLayout {
      tabLayout {
        tabMode = TabLayout.MODE_SCROLLABLE
        setTabTextColors(normalTabTextColor,selectedTabTextColor)
        setSelectedTabIndicatorColor(tabIndicatorColor)
        setSelectedTabIndicatorHeight(tabHeight)
        for (interval in IntervalEnum.values()) {
          val tab = newTab()
          tab.text = interval.interval
          tab.tag = interval.interval
          addTab(tab)
        }
        
        addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
          override fun onTabReselected(tab: TabLayout.Tab?) {
          }
  
          override fun onTabUnselected(tab: TabLayout.Tab?) {
          }
  
          override fun onTabSelected(tab: TabLayout.Tab?) {
//            candleStickChart.setEmptyData()
            setBtcData()
          }
  
        })
      }
      candleStickChart = BlinnnkCandleStickChart(this@CandleChartActivity)
      candleStickChart.apply {
        backgroundColor = Color.BLACK
      }
      candleStickChart.layoutParams =
        LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Utils.convertDpToPixel(300f).toInt())
      addView(candleStickChart)
    }
    
    requestData(IntervalEnum.ONE_DAY.interval)
    
  }
  
  private fun requestData(interval: String) {
    object : Thread() {
      override fun run() {
        super.run()
        try {
          val result = KlinePresenter.requestData(interval)
          dataSet.clear()
          dataSet.addAll(JSON.parseArray(result, Array<String>::class.java))
          runOnUiThread { setData() }
          
        } catch (e: IOException) {
          e.printStackTrace()
          runOnUiThread {
            toast(e.toString())
          }
        }
        
      }
    }.start()
  }
  
  internal fun setData() {
    
    candleEntrySet.clear()
    for (i in 0 until dataSet.size) {
      candleEntrySet.add(CandleEntry(java.lang.Float.valueOf(i.toFloat()),
        java.lang.Float.valueOf(dataSet[i][2]),
        java.lang.Float.valueOf(dataSet[i][3]),
        java.lang.Float.valueOf(dataSet[i][1]),
        java.lang.Float.valueOf(dataSet[i][4]),
        java.lang.Long.valueOf(dataSet[i][0])))
      
    }
    candleStickChart.resetData(candleEntrySet)
  }
  
  fun setBtcData() {
    candleEntrySet.clear()
    BtcCandlePresenter.parseData {
      it.mapIndexedNotNull { index, candleChartModel ->
        candleEntrySet.add(CandleEntry(index.toFloat(),
          java.lang.Float.valueOf(candleChartModel.high),
          java.lang.Float.valueOf(candleChartModel.low),
          java.lang.Float.valueOf(candleChartModel.open),
          java.lang.Float.valueOf(candleChartModel.close),
          java.lang.Long.valueOf(candleChartModel.time)))
      }.let {
        candleStickChart.resetData(candleEntrySet)
      }
    }
  }
}
