package example.cat.com.candlechartdemo.ktd.listchart

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.mikephil.charting.data.Entry
import org.jetbrains.anko.listView
import java.lang.Long

/**
 * @date: 2018/8/8.
 * @author: yanglihai
 * @description:
 */

class LineChartListActivity : AppCompatActivity() {
  
  private lateinit var mAdapter: LineChartAdapter
  private val data: MutableList<MutableList<Entry>> = mutableListOf()
  
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    mAdapter = LineChartAdapter(this, data)
    listView {
      adapter = mAdapter
    }
    
    initData()
  }
  
  
  fun initData() {
    data.clear()
    for (j in 0 .. 10) {
      val candleEntrySet = mutableListOf<Entry>()
      for (i in 0 until 19) {
        candleEntrySet.add(Entry(i.toFloat(),(Math.random()*10).toFloat(), Long.valueOf(1533638365)))
      }
      data.add(candleEntrySet)
    }
    mAdapter.notifyDataSetChanged()
    
  }
}
