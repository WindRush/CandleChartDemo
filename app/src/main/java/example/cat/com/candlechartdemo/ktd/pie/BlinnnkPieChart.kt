package example.cat.com.candlechartdemo.ktd.pie

import android.content.Context
import android.graphics.*
import android.text.SpannableString
import android.text.style.*
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.util.ArrayList

/**
 * @date: 2018/9/18.
 * @author: yanglihai
 * @description:
 */
class BlinnnkPieChart(context: Context) : PieChart(context) {
  
  
  override fun init() {
    super.init()
    mRenderer = BlinnnkPieChartRenderer(
      this,
      mAnimator,
      mViewPortHandler
    )
    post {
      initAttrs()
      resetData()
    }
    
  }
  
  private fun initAttrs() {
    setUsePercentValues(true)
    description.isEnabled = false
    setExtraOffsets(
      5f,
      10f,
      5f,
      5f
    )
    
    dragDecelerationFrictionCoef = 0.95f

//    setCenterTextTypeface(mTfLight)
    centerText = "成交分布" // generateCenterSpannableText()
    setCenterTextSize(28f)
    
    isDrawHoleEnabled = true
    setHoleColor(Color.TRANSPARENT)
    
    setTransparentCircleColor(Color.WHITE)
    setTransparentCircleAlpha(0)
    
    holeRadius = 60f
    transparentCircleRadius = 61f
    
    setDrawCenterText(true)
    
    rotationAngle = 0f
    // enable rotation of the chart by touch
    isRotationEnabled = true
    isHighlightPerTapEnabled = true
    
    // setUnit(" €");
    // setDrawUnitsInChart(true);
    
    // add a selection listener
//    setOnChartValueSelectedListener(this)
    
    animateY(
      1400,
      Easing.EasingOption.EaseInOutQuad
    )
    // spin(2000, 0, 360);
    
    val l = legend
    l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
    l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
    l.orientation = Legend.LegendOrientation.VERTICAL
    l.setDrawInside(false)
    l.isEnabled = false
    
    
    // entry label styling
    setEntryLabelColor(Color.WHITE)
//    setEntryLabelTypeface(mTfRegular)
    setEntryLabelTextSize(12f)
  }
  
  private fun generateCenterSpannableText(): SpannableString {
    
    val s = SpannableString("MPAndroidChart\ndeveloped by Philipp Jahoda")
    s.setSpan(
      RelativeSizeSpan(1.7f),
      0,
      14,
      0
    )
    s.setSpan(
      StyleSpan(Typeface.NORMAL),
      14,
      s.length - 15,
      0
    )
    s.setSpan(
      ForegroundColorSpan(Color.GRAY),
      14,
      s.length - 15,
      0
    )
    s.setSpan(
      RelativeSizeSpan(.8f),
      14,
      s.length - 15,
      0
    )
    s.setSpan(
      StyleSpan(Typeface.ITALIC),
      s.length - 14,
      s.length,
      0
    )
    s.setSpan(
      ForegroundColorSpan(ColorTemplate.getHoloBlue()),
      s.length - 14,
      s.length,
      0
    )
    return s
  }
  
  private fun resetData() {
    val mult = 100
    
    val entries = ArrayList<PieEntry>()
    
    // NOTE: The order of the entries when being added to the entries array determines their position around the center of
    // the chart.
    for (i in 0 until 4) {
      entries.add(
        PieEntry(
          (Math.random() * mult).toFloat() + mult / 5,
          "2344k"
        )
      )
    }
    
    entries.add(PieEntry(0f, ""))
    
    val dataSet = PieDataSet(
      entries,
      "Election Results"
    )
    dataSet.sliceSpace = 3f
    dataSet.selectionShift = 5f
    
    // add a lot of colors
    
    val colors = ArrayList<Int>()
    
    for (c in ColorTemplate.VORDIPLOM_COLORS) colors.add(c)
    
    for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)
    
    for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)
    
    for (c in ColorTemplate.LIBERTY_COLORS) colors.add(c)
    
    for (c in ColorTemplate.PASTEL_COLORS) colors.add(c)
    
    colors.add(ColorTemplate.getHoloBlue())
    
    dataSet.colors = colors
    dataSet.sliceSpace = 0f // 每一块之间的间隙
    dataSet.selectionShift = 10f // 点击每一个item放大的区域
    
    dataSet.valueLinePart1OffsetPercentage = 80f
    dataSet.valueLinePart1Length = 0.5f
    dataSet.valueLinePart2Length = 0.4f
    dataSet.valueLineColor = Color.GREEN
    dataSet.xValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE
    dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
    
    val data = PieData(dataSet)
    data.setValueFormatter(PercentFormatter())
    data.setValueTextSize(11f)
    data.setValueTextColor(Color.BLACK)
//    data.setValueTypeface(tf)
    setData(data)
    
    // undo all highlights
    highlightValues(null)
    
    invalidate()
  }
  
  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
  }
}