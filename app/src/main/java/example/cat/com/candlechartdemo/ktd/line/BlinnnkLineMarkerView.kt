package example.cat.com.candlechartdemo.ktd.line

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import example.cat.com.candlechartdemo.R

/**
 * @date: 2018/8/6.
 * @author: yanglihai
 * @description: 线性表详情这是marker
 */
class BlinnnkLineMarkerView : MarkerView {
  private var tvContent: TextView = findViewById(R.id.textview_content)
  
  constructor(context: Context) : super(context, R.layout.chart_marker_vier)
  
  override fun refreshContent(e: Entry?, highlight: Highlight?) {
    if (e != null) {
      tvContent.text =  "收盘：" + e.y
    }
    super.refreshContent(e, highlight)
  }
  
  override fun getOffset(): MPPointF {
    return MPPointF((-width / 2).toFloat(), -height.toFloat())
  }
}
