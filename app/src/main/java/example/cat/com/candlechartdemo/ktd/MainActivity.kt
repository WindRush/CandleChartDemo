package example.cat.com.candlechartdemo.ktd

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

class MainActivity : AppCompatActivity() {
  private lateinit var candleStickChart: BlinnnkCandleStickChart
  private val normalTabTextColor = Color.rgb(152, 152, 152)
  private val selectedTabTextColor = Color.rgb(67, 200, 135)
  private val tabIndicatorColor = selectedTabTextColor
  private val tabHeight = Utils.convertDpToPixel(2f).toInt()
  private var dataSet = mutableListOf<Array<String>>()
  private val candleEntrySet = mutableListOf<CandleEntry>()
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    verticalLayout {
      backgroundColor = Color.BLACK
      tabLayout {
        tabMode = TabLayout.MODE_SCROLLABLE
        setTabTextColors(normalTabTextColor,selectedTabTextColor)
        setSelectedTabIndicatorColor(tabIndicatorColor)
        setSelectedTabIndicatorHeight(tabHeight)
        for (interval in IntervalEnum.values()) {
          val tab = newTab()
          tab.setText(interval.interval)
          tab.setTag(interval.interval)
          addTab(tab)
        }
        
        addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
          override fun onTabReselected(tab: TabLayout.Tab?) {
          }
  
          override fun onTabUnselected(tab: TabLayout.Tab?) {
          }
  
          override fun onTabSelected(tab: TabLayout.Tab?) {
            requestData(tab?.tag as String)
          }
  
        })
      }
      candleStickChart = BlinnnkCandleStickChart(this@MainActivity)
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
    candleStickChart.notifyData(candleEntrySet)
  }
}
