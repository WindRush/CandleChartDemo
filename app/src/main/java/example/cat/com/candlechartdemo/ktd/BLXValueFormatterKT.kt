package example.cat.com.candlechartdemo.ktd


import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*

/**
 * @date: 2018/8/1.
 * @author: yanglihai
 * @description: x轴的坐标展示的时间字符串转换器
 */

class BLXValueFormatterKT(private val chart: BarLineChartBase<*>) : IAxisValueFormatter {
  
  
  override fun getFormattedValue(
    value: Float,
    axis: AxisBase
  ): String {
    val position = value.toInt()
    var values = (this.chart.data.getDataSetByIndex(0) as CandleDataSet).values
    if (position >= values.size) return ""
    val entry = values[position]
    val formater = SimpleDateFormat("yyyy-MM-dd")
    var str = formater.format(Date(entry.data as Long))
    return str
    
  }
  
  
}

