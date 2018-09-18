package example.cat.com.candlechartdemo.ktd.candle

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.dataprovider.CandleDataProvider
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.utils.Utils
import example.cat.com.candlechartdemo.ktd.BlinnnkXAxisRenderer
import example.cat.com.candlechartdemo.ktd.BlinnnkXValueFormatter
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import java.util.ArrayList

/**
 * @date: 2018/8/1.
 * @author: yanglihai
 * @description: 蜡烛统计图view
 */
class BlinnnkCandleStickChart : BarLineChartBase<CandleData>, CandleDataProvider {
  
  private val labelColor = Color.rgb(152, 152, 152)
  private val shadowColor = Color.DKGRAY//蜡烛柄颜色
  private val decreasingColor = Color.rgb(219, 74, 76)
  private val increasingColor = Color.rgb(67, 200, 135)
  private val labelRotationAngle = 320f
  
  private val neutralColor = Color.BLUE
  private val barSpace = 0.2f
  private val shadowWidth = 2f//蜡烛柄宽度
  
  private val xRangeVisibleNum = 30f
  
  private val xAxinSpace = 0.5f
  
  private lateinit var blinnnkXValueFormatter: BlinnnkXValueFormatter
  
  private lateinit var blinnnkMarkerView: BlinnnkMarkerView
  
  private var isGetLeftZero = false
  
  private var realData = arrayListOf<CandleEntry>()
  
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
    blinnnkMarkerView.setChartView(this)
  
    blinnnkXValueFormatter = BlinnnkXValueFormatter(this@BlinnnkCandleStickChart)
    mXAxisRenderer = BlinnnkXAxisRenderer(mViewPortHandler,
      mXAxis,
      mLeftAxisTransformer)
    mRenderer = BlinnnkCandleStickChartRenderer(this, mAnimator, mViewPortHandler)
    
    post {
      resetAxisStyle()
      initGestureListener()
      setEmptyData()
    }
    
  }
  
  private val getLeftRunnable = Runnable { isGetLeftZero = false }
  
  
  fun resetData(dataRows: ArrayList<CandleEntry>) {
    realData = dataRows
    isGetLeftZero = true
    
    resetTracking()
    clear()
    mXAxis.labelCount = if (dataRows.size > xRangeVisibleNum) xRangeVisibleNum.toInt()/3 else dataRows.size/3
    
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
    
//    calcuteHandler.removeCallbacks(calcuteRunnable)
//    calcuteHandler.post(calcuteRunnable)
  }
  
  private fun resetAxisStyle() {
  
    dragDecelerationFrictionCoef = 0.5f
    
    setScaleEnabled(false)
    setPinchZoom(true)
    isDragEnabled = true
    legend.isEnabled = false
    description.isEnabled = false
    
    with(xAxis) {
      textColor = this@BlinnnkCandleStickChart.labelColor
      position = XAxis.XAxisPosition.BOTTOM
//      labelCount = this@BlinnnkCandleStickChart.labelCount
      labelRotationAngle = this@BlinnnkCandleStickChart.labelRotationAngle
      valueFormatter = blinnnkXValueFormatter
      setDrawGridLines(true)
      spaceMin = xAxinSpace
      spaceMax = xAxinSpace
    }
    
    with(axisLeft) {
      textColor = this@BlinnnkCandleStickChart.labelColor
      axisLineColor = this@BlinnnkCandleStickChart.labelColor
      labelCount = 4
      setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
    }
    with(axisRight) {
      setDrawAxisLine(true)
      setDrawLabels(false)
    }
    
    marker = this@BlinnnkCandleStickChart.blinnnkMarkerView
  }
  
  override fun getCandleData(): CandleData {
    return mData
  }
  
  fun setEmptyData() {
    val candleEntrySet = arrayListOf<CandleEntry>()
    var size = (Math.random() * 50).toInt()
    for (i in 0 until size ) {
      candleEntrySet.add(CandleEntry(java.lang.Float.valueOf(i.toFloat()),
        (Math.random() * 30).toFloat(), (Math.random() * 30).toFloat(),(Math.random() * 10).toFloat(),(Math.random() * 10).toFloat(),java.lang.Long.valueOf(1533718053)))
    
    }
    resetData(candleEntrySet)
  }
  
  /**
   * @date: 2018/8/22
   * @author: yanglihai
   * @description: 计算显示在屏幕上蜡烛的最高值和最低值
   */
  fun resetMaxMin(firstVisibleIndex: Int) {
    val endIndex: Int = firstVisibleIndex + xRangeVisibleNum.toInt()
    val max = if (endIndex > realData.size) realData.size else endIndex
    var high = realData[firstVisibleIndex].high
    var low = realData[firstVisibleIndex].low
    (firstVisibleIndex until max).forEachIndexed {
      i, _ ->
      if (realData[firstVisibleIndex+i].low < low) {
        low = realData[firstVisibleIndex+i].low
      }
      if (realData[firstVisibleIndex+i].high > high) {
        high = realData[firstVisibleIndex+i].high
      }
    }
    
    
    val distance = (high - low) /20
    context.runOnUiThread {
      with(axisLeft) {
        axisMinimum = low - distance
        axisMaximum = high + distance
        setLabelCount(4, true)
      }
      resetData(realData)
    }
  }
  
  /**
   * @date: 2018/8/22
   * @author: yanglihai
   * @description: 计算图标显示的第一个蜡烛的下标
   */
  private fun calcuteVisibleIndes() {
    val trans = getTransformer(mData.dataSets[0].axisDependency)
    val buffers = FloatArray(4)
    realData.forEachIndexed {
        index, candleEntry ->
      buffers[0] = candleEntry.x - 0.5f + barSpace
      buffers[1] = candleEntry.close
      buffers[2] = buffers[0]
      buffers[3] = candleEntry.open
      trans.pointValuesToPixel(buffers)
      
      val measurePaint = Paint()
      measurePaint.textSize = axisLeft.textSize
      val rect = Rect()
      measurePaint.getTextBounds(axisLeft.longestLabel, 0, axisLeft.longestLabel.length, rect)
      if (buffers[0] > rect.width()) {
        resetMaxMin(index)
        return
      }
    }
  }
  
  private val calcuteHandler = Handler()
  
  private val calcuteRunnable = Runnable {
    doAsync {
      calcuteVisibleIndes()
    }
  }
  
  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    calcuteHandler.removeCallbacks(calcuteRunnable)
  }
  
  /**
   * @date: 2018/8/22
   * @author: yanglihai
   * @description: 不清除marker，需要重写clear
   */
  override fun clear() {
    var tempHighlight: Array<Highlight>? = null
    mIndicesToHighlight?.apply {
      tempHighlight = arrayOf(this[0])
    }
    
    super.clear()
    
    tempHighlight?.apply {
      mIndicesToHighlight = this
    }
  }
  
  override fun onDraw(canvas: Canvas?) {
    mAxisLeft.xOffset = 0f
    super.onDraw(canvas)
  }
  
  private val mOffsetsBuffer = RectF()
  
  override fun calculateOffsets() {
    
    var offsetLeft = 0f
    var offsetRight = 0f
    var offsetTop = 0f
    var offsetBottom = 0f
    
    calculateLegendOffsets(mOffsetsBuffer)
    
    offsetLeft += mOffsetsBuffer.left
    offsetTop += mOffsetsBuffer.top
    offsetRight += mOffsetsBuffer.right
    offsetBottom += mOffsetsBuffer.bottom
    
    // offsets for y-labels
    if (mAxisLeft.needsOffset()) {
      offsetLeft += mAxisLeft.getRequiredWidthSpace(mAxisRendererLeft.paintAxisLabels)
    }
    
    if (mAxisRight.needsOffset()) {
      offsetRight += mAxisRight.getRequiredWidthSpace(mAxisRendererRight.paintAxisLabels)
    }
    
    if (mXAxis.isEnabled && mXAxis.isDrawLabelsEnabled) {
      
      val xlabelheight = mXAxis.mLabelRotatedHeight + mXAxis.yOffset
      
      // offsets for x-labels
      if (mXAxis.position == XAxis.XAxisPosition.BOTTOM) {
        
        offsetBottom += xlabelheight
        
      } else if (mXAxis.position == XAxis.XAxisPosition.TOP) {
        
        offsetTop += xlabelheight
        
      } else if (mXAxis.position == XAxis.XAxisPosition.BOTH_SIDED) {
        
        offsetBottom += xlabelheight
        offsetTop += xlabelheight
      }
    }
    
    offsetTop += extraTopOffset
    offsetRight += extraRightOffset
    offsetBottom += extraBottomOffset
    offsetLeft += extraLeftOffset
    
    val minOffset = 0f
    
    mViewPortHandler.restrainViewPort(Math.max(minOffset, offsetLeft),
      Math.max(minOffset, offsetTop),
      Math.max(minOffset, offsetRight),
      Math.max(minOffset, offsetBottom))
    
    if (mLogEnabled) {
      Log.i(Chart.LOG_TAG,
        "offsetLeft: " + offsetLeft + ", offsetTop: " + offsetTop + ", offsetRight: " + offsetRight + ", offsetBottom: " + offsetBottom)
      Log.i(Chart.LOG_TAG, "Content: " + mViewPortHandler.contentRect.toString())
    }
  
  
    prepareOffsetMatrix()
    prepareValuePxMatrix()
  }
  
  override fun onTouchEvent(event: MotionEvent?): Boolean {
    return super.onTouchEvent(event)
  }
  
  fun initGestureListener() {
    
    onChartGestureListener = object : OnChartGestureListener {
      override fun onChartGestureStart(
        me: MotionEvent?,
        lastPerformedGesture: ChartTouchListener.ChartGesture?
      ) {
      }
  
      override fun onChartGestureEnd(
        me: MotionEvent?,
        lastPerformedGesture: ChartTouchListener.ChartGesture?
      ) {
      }
  
      override fun onChartLongPressed(me: MotionEvent?) {
      }
  
      override fun onChartDoubleTapped(me: MotionEvent?) {
      }
  
      override fun onChartSingleTapped(me: MotionEvent?) {
      }
  
      override fun onChartFling(
        me1: MotionEvent?,
        me2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
      ) {
      }
  
      override fun onChartScale(
        me: MotionEvent?,
        scaleX: Float,
        scaleY: Float
      ) {
      }
  
      override fun onChartTranslate(
        me: MotionEvent?,
        dX: Float,
        dY: Float
      ) {
        Log.e("BlinnnkCandleStickChart","oncharttranslate x=$dX")
      }
    }
  
//    val touchListener = object : ChartTouchListener<BlinnnkCandleStickChart>(this@BlinnnkCandleStickChart) {
//      override fun onTouch(
//        v: View?,
//        event: MotionEvent?
//      ): Boolean {
//        return false
//      }
//    }
//    onTouchListener = touchListener
  }
  
  
  
  
}
