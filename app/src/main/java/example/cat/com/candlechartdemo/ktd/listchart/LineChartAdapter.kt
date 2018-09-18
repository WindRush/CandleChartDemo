package example.cat.com.candlechartdemo.ktd.listchart

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.github.mikephil.charting.data.Entry
import example.cat.com.candlechartdemo.R
import example.cat.com.candlechartdemo.ktd.line.BlinnnkLineChart

/**
 * @date: 2018/8/8.
 * @author: yanglihai
 * @description:
 */
class LineChartAdapter : BaseAdapter {
  
  private lateinit var entryDataList: List<List<Entry>>
  private lateinit var context: Context
  
  constructor(context: Context, entryDataList: List<List<Entry>>) : super() {
    this.context = context
    this.entryDataList = entryDataList
  }
  
  override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
    var lineHolder: LineHolder
    var root: View
    if (convertView == null) {
      root = View.inflate(context, R.layout.item_linechart,null)
      lineHolder = LineHolder()
      lineHolder.blinnnkLineChart = root.findViewById(R.id.linechart)
      root.setTag(lineHolder)
    }else {
      root = convertView
      lineHolder = root.getTag() as LineHolder
    }
    
    lineHolder.blinnnkLineChart.resetData(entryDataList.get(position))
    return root
  }
  
  override fun getItem(position: Int): Any {
    return entryDataList.get(position)
  }
  
  override fun getItemId(position: Int): Long {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
  
  override fun getCount(): Int {
    return entryDataList.size
  }
  
  private class LineHolder {
    lateinit var blinnnkLineChart: BlinnnkLineChart
  }
}
