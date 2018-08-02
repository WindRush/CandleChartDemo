package example.cat.com.candlechartdemo.ktd

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.dataprovider.CandleDataProvider

/**
 * @date: 2018/8/1.
 * @author: yanglihai
 * @description: 蜡烛统计图view
 */
class BlinnnkCandleStickChart : BarLineChartBase<CandleData>, CandleDataProvider {
  
  
  private val labelColor = Color.rgb(152, 152, 152)
  private val shadowColor = Color.DKGRAY
  private val decreasingColor = Color.rgb(219, 74, 76)
  private val increasingColor = Color.rgb(67, 200, 135)
  private val labelCount = 9
  private val labelRotationAngle = 320f
  
  private val neutralColor = Color.BLUE
  private val barSpace = 0.2f
  private val shadowWidth = 2f
  
  private val xRangeVisibleNum = 20f
  
  private val xAxinSpace = 0.5f
  
  private lateinit var blinnnkXValueFormatter: BlinnnkXValueFormatter
  
  private lateinit var blinnnkMarkerView: BlinnnkMarkerView
  
  constructor(context: Context) : super(context)
  
  constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
  
  constructor(
    context: Context,
    attrs: AttributeSet,
    defStyle: Int
  ) : super(context, attrs, defStyle)
  
  
  override fun init() {
    super.init()
    
    blinnnkMarkerView = BlinnnkMarkerView(context)
    blinnnkMarkerView.chartView = this
  
    blinnnkXValueFormatter = BlinnnkXValueFormatter(this@BlinnnkCandleStickChart)
    mXAxisRenderer = BlinnnkXAxisRenderer(mViewPortHandler, mXAxis, mLeftAxisTransformer)
    mRenderer = BlinnnkCandleStickChartRenderer(this, mAnimator, mViewPortHandler)
    
    post {
      resetAxisStyle()
      setEmptyData()
    }
    
  }
  
  
  fun resetData(dataRows: List<CandleEntry>) {
    
    resetTracking()
    clear()
    
    val dataSet = CandleDataSet(dataRows, "Data Set")
    
    dataSet.apply {
      setDrawIcons(false)
      axisDependency = YAxis.AxisDependency.LEFT
      //        set1.setColor(Color.rgb(80, 80, 80));
      shadowColor = this@BlinnnkCandleStickChart.shadowColor
      shadowWidth = this@BlinnnkCandleStickChart.shadowWidth
      decreasingColor = this@BlinnnkCandleStickChart.decreasingColor
      decreasingPaintStyle = Paint.Style.FILL
      increasingColor = this@BlinnnkCandleStickChart.increasingColor
      increasingPaintStyle = Paint.Style.FILL
      neutralColor = this@BlinnnkCandleStickChart.neutralColor
      setDrawValues(false)
      barSpace = this@BlinnnkCandleStickChart.barSpace
      showCandleBar = true
      shadowColorSameAsCandle = true
    }
    
    val data = CandleData(dataSet)
    
    setData(data)
    setVisibleXRangeMaximum(this@BlinnnkCandleStickChart.xRangeVisibleNum)
    setVisibleXRangeMinimum(this@BlinnnkCandleStickChart.xRangeVisibleNum)
    invalidate()
    post {
    }
  }
  
  private fun resetAxisStyle() {
    
    setScaleEnabled(false)
    setPinchZoom(true)
    isDragEnabled = true
    legend.isEnabled = false
    description.isEnabled = false
    
    with(xAxis) {
      textColor = this@BlinnnkCandleStickChart.labelColor
      position = XAxis.XAxisPosition.BOTTOM
      labelCount = this@BlinnnkCandleStickChart.labelCount
      labelRotationAngle = this@BlinnnkCandleStickChart.labelRotationAngle
      valueFormatter = blinnnkXValueFormatter
      setDrawGridLines(true)
      spaceMin = xAxinSpace
      spaceMax = xAxinSpace
    }
    
    with(axisLeft) {
      setDrawAxisLine(true)
      setDrawLabels(false)
    }
    with(axisRight) {
      textColor = this@BlinnnkCandleStickChart.labelColor
      axisLineColor = this@BlinnnkCandleStickChart.labelColor
    }
    
    marker = this@BlinnnkCandleStickChart.blinnnkMarkerView
  }
  
  override fun getCandleData(): CandleData {
    return mData
  }
  
  private fun setEmptyData() {
    val candleEntrySet = mutableListOf<CandleEntry>()
    for (i in 0 until 20) {
      candleEntrySet.add(CandleEntry(java.lang.Float.valueOf(i.toFloat()),
        0f,0f,0f,0f,java.lang.Long.valueOf(0)))
    
    }
    resetData(candleEntrySet)
  }
  
  override fun onDraw(canvas: Canvas?) {
    super.onDraw(canvas)
  }
}