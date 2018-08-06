package example.cat.com.candlechartdemo.ktd.line

import android.content.Context
import android.graphics.Color
import android.graphics.DashPathEffect
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.renderer.LineChartRenderer
import com.github.mikephil.charting.utils.Utils
import example.cat.com.candlechartdemo.R
import example.cat.com.candlechartdemo.ktd.candle.*
import java.util.*
import kotlin.math.absoluteValue

/**
 * @date: 2018/8/6.
 * @author: yanglihai
 * @description:
 */
class BlinnnkLineChart : BarLineChartBase<LineData> ,LineDataProvider {
  
  
  private lateinit var blinnnkMarkerView: BlinnnkLineMarkerView
  private lateinit var blinnnkXValueFormatter: BlinnnkXValueFormatter
  private val xRangeVisibleNum = 10f
  private val lineYValueFormatter = LineYValueFormatter()
  
  private val lineColor = Color.RED
  private val pointColor = Color.BLACK
  
  constructor(context: Context) : super(context)
  
  constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
  
  constructor(context: Context, attrs: AttributeSet, defStyle: Int) :
    super(context, attrs, defStyle)
  
  override fun init() {
    super.init()
    mRenderer = LineChartRenderer(this, mAnimator, mViewPortHandler)
    mXAxisRenderer = BlinnnkXAxisRenderer(mViewPortHandler, mXAxis, mLeftAxisTransformer)
    
    blinnnkMarkerView = BlinnnkLineMarkerView(context)
    blinnnkMarkerView.chartView = this
    blinnnkXValueFormatter = BlinnnkXValueFormatter(this@BlinnnkLineChart)
    
    post {
      initAxisStyle()
    }
    
    
  }
  
  fun initAxisStyle() {
//    setOnChartGestureListener(this)
//    setOnChartValueSelectedListener(this)
  
    isScaleXEnabled = false
    isScaleYEnabled = false
    mPinchZoomEnabled = true
    isDragEnabled = true
    legend.isEnabled = false
    description.isEnabled = false
    setDrawGridBackground(false)
  
    marker = this@BlinnnkLineChart.blinnnkMarkerView
  
    xAxis.apply {
//      enableGridDashedLine(10f, 10f, 0f)//虚线
      valueFormatter = blinnnkXValueFormatter
      position = XAxis.XAxisPosition.BOTTOM
      setDrawAxisLine(false)
      setDrawLabels(true)
    }
    mAxisLeft.apply {
      enableGridDashedLine(10f, 10f, 0f)
      setDrawZeroLine(false)
      setDrawLimitLinesBehindData(true)
      mAxisMaximum = 50f
      axisMinimum = -50f
    
    }
  
    axisRight.apply {
//      setEnabled(true)
      isEnabled = true
      setDrawLabels(false)
    }
    
    animateY(1000)
    // modify the legend ...
    legend.setForm(Legend.LegendForm.LINE)
  }
  
  fun notifyData(dataRows: List<Entry>) {
    
    val dataSet: LineDataSet
    
    if (mData != null && mData.dataSetCount > 0) {
      dataSet = mData.getDataSetByIndex(0) as LineDataSet
      dataSet.values = dataRows
      mData.notifyDataChanged()
      notifyDataSetChanged()
    } else {
      // create a dataset and give it a type
      dataSet = LineDataSet(dataRows, "DataSet 1")
      dataSet.apply {
        valueFormatter = lineYValueFormatter

        //平划的曲线
        mode = LineDataSet.Mode.CUBIC_BEZIER
        cubicIntensity = 0.2f
        
        setDrawIcons(false)
        setDrawValues(false)
        color = lineColor
        setCircleColor(pointColor)
        lineWidth = 3f
        circleRadius = 5f
        circleHoleRadius = 3f
        setDrawCircleHole(true)
        valueTextSize = 9f
        setDrawFilled(true)
        formLineWidth = 1f
        formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
        formSize = 15f
  
        if (Utils.getSDKInt() >= 18) {
          // fill drawable only supported on api level 18 and above
          fillDrawable= ContextCompat.getDrawable(context, R.drawable.fade_red)
        } else {
          fillColor = Color.RED
        }
      }
      
      
      val dataSets = ArrayList<ILineDataSet>()
      dataSets.add(dataSet) // add the datasets
      
      // create a data object with the datasets
      val data = LineData(dataSets)
      
      // set data
      setData(data)
      setVisibleXRangeMaximum(this@BlinnnkLineChart.xRangeVisibleNum)
      setVisibleXRangeMinimum(this@BlinnnkLineChart.xRangeVisibleNum)
    }
  }
  
  override fun getLineData(): LineData {
    return mData
  }
  
  override fun onDetachedFromWindow() {
    // releases the bitmap in the renderer to avoid oom error
    if (mRenderer != null && mRenderer is LineChartRenderer) {
      (mRenderer as LineChartRenderer).releaseBitmap()
    }
    super.onDetachedFromWindow()
  }
  
  fun setEmptyData() {
    val candleEntrySet = mutableListOf<Entry>()
    for (i in 0 until 19) {
      
      candleEntrySet.add(Entry(i.toFloat(),(Math.random()*10).toFloat(),java.lang.Long.valueOf(1533549860)))
      
//      candleEntrySet.add(CandleEntry(java.lang.Float.valueOf(i.toFloat()),
//        0f,0f,0f,i.toFloat(),java.lang.Long.valueOf(0)))
    }
    notifyData(candleEntrySet)
  }
}
