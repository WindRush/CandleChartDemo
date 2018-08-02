package example.cat.com.candlechartdemo.ktd

import android.content.Context
import android.widget.TextView

import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import example.cat.com.candlechartdemo.R


/**
 * @date: 2018/8/1.
 * @author: yanglihai
 * @description: 点击candleChartView的item的时候展示的具体详情
 */

class BlinnnkMarkerView : MarkerView {
  private var tvContent: TextView
  
  constructor(context: Context) : super(context, R.layout.chart_marker_vier) {
    tvContent = findViewById(R.id.textview_content)
  }
  
  
  override fun refreshContent(e: Entry?, highlight: Highlight?) {
    
    val entry = e as CandleEntry?
    
    if (entry != null) {
      tvContent.text = "最高：" + entry.high + "\n" + "最低：" + entry.low + "\n" + "开盘：" + entry.open + "\n" + "收盘：" + entry.close
    }
    super.refreshContent(e, highlight)
  }
  
  override fun getOffset(): MPPointF {
    return MPPointF((-width / 2).toFloat(), -height.toFloat())
  }
}

