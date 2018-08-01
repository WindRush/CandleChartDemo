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
class BLCandleStickChartKT : BarLineChartBase<CandleData>, CandleDataProvider {
  
  
  internal var labelColor = Color.rgb(152, 152, 152)
  
  constructor(context: Context) : super(context) {}
  
  constructor(
    context: Context,
    attrs: AttributeSet
  ) : super(context, attrs)
  
  constructor(
    context: Context,
    attrs: AttributeSet,
    defStyle: Int
  ) : super(context, attrs, defStyle)
  
  
  override fun init() {
    super.init()
    
    mXAxisRenderer = BLXAxisRendererKT(mViewPortHandler, mXAxis, mLeftAxisTransformer)
    mRenderer = BLCandleStickChartRendererKT(this, mAnimator, mViewPortHandler)
    
    xAxis.spaceMin = 0.5f
    xAxis.spaceMax = 0.5f
  }
  
  
  fun resetData(dataSet: List<CandleEntry>) {
    
    resetTracking()
    initAxisStyle()
    
    clear()
    
    val set1 = CandleDataSet(dataSet, "Data Set")
    
    set1.setDrawIcons(false)
    set1.axisDependency = YAxis.AxisDependency.LEFT
    //        set1.setColor(Color.rgb(80, 80, 80));
    set1.shadowColor = Color.DKGRAY
    set1.shadowWidth = 1f
    set1.decreasingColor = Color.rgb(219, 74, 76)
    set1.decreasingPaintStyle = Paint.Style.FILL
    set1.increasingColor = Color.rgb(67, 200, 135)
    set1.increasingPaintStyle = Paint.Style.FILL
    
    set1.neutralColor = Color.BLUE
    set1.setDrawValues(false)
    set1.barSpace = 0.2f
    set1.showCandleBar = true
    set1.shadowWidth = 2f
    //set1.setHighlightLineWidth(1f);
    set1.shadowColorSameAsCandle = true
    
    val data = CandleData(set1)
    //    data.setValueTextSize(50f);
    
    setData(data)
    setVisibleXRangeMaximum(20f)
    setVisibleXRangeMinimum(20f)
    invalidate()
  }
  
  private fun initAxisStyle() {
    
    setScaleEnabled(false)
    setPinchZoom(true)
    isDragEnabled = true
    legend.isEnabled = false
    description.isEnabled = false
    
    xAxis.textColor = labelColor
    xAxis.position = XAxis.XAxisPosition.BOTTOM
    xAxis.labelCount = 9
    xAxis.labelRotationAngle = 320f
    xAxis.valueFormatter = BLXValueFormatterKT(this)
    xAxis.setDrawGridLines(true)
    
    axisLeft.setDrawAxisLine(true)
    axisLeft.setDrawLabels(false)
    axisRight.textColor = labelColor
    axisRight.axisLineColor = labelColor
    //    getAxisRight().setDrawGridLines(false);
    //    getAxisLeft().setDrawGridLines(false);
    //
    val markerView = BLMarkerViewKT(context)
    markerView.chartView = this
    marker = markerView
  }
  
  override fun getCandleData(): CandleData {
    return mData
  }
  
  override fun onDraw(canvas: Canvas?) {
    super.onDraw(canvas)
  }
}
