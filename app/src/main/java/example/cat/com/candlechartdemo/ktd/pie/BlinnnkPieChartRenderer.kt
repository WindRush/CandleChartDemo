package example.cat.com.candlechartdemo.ktd.pie


import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Align
import android.graphics.Paint.Style
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint

import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data .Entry
import com.github.mikephil.charting.data .PieData
import com.github.mikephil.charting.data .PieDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet
import com.github.mikephil.charting.renderer.DataRenderer
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler

import java.lang.ref.WeakReference

/**
 * @date: 2018/9/18.
 * @author: yanglihai
 * @description:
 */
class BlinnnkPieChartRenderer(
  private var mChart: PieChart,
  animator: ChartAnimator,
  viewPortHandler: ViewPortHandler
) : DataRenderer(
  animator,
  viewPortHandler
) {
  
  /**
   * paint for the hole in the center of the pie chart and the transparent
   * circle
   */
  var paintHole: Paint
    protected set
  var paintTransparentCircle: Paint
    protected set
  protected var mValueLinePaint: Paint
  
  /**
   * paint object for the text that can be displayed in the center of the
   * chart
   */
  val paintCenterText: TextPaint
  
  /**
   * paint object used for drwing the slice-text
   */
  val paintEntryLabels: Paint
  
  private var mCenterTextLayout: StaticLayout? = null
  private var mCenterTextLastValue: CharSequence? = null
  private val mCenterTextLastBounds = RectF()
  private val mRectBuffer = arrayOf(
    RectF(),
    RectF(),
    RectF()
  )
  
  /**
   * Bitmap for drawing the center hole
   */
  protected var mDrawBitmap: WeakReference<Bitmap>? = null
  
  protected var mBitmapCanvas: Canvas? = null
  
  private val mPathBuffer = Path()
  private val mInnerRectBuffer = RectF()
  
  private val mHoleCirclePath = Path()
  
  protected var mDrawCenterTextPathBuffer = Path()
  
  protected var mDrawHighlightedRectF = RectF()
  
  init {
    
    paintHole = Paint(Paint.ANTI_ALIAS_FLAG)
    paintHole.color = Color.WHITE
    paintHole.style = Paint.Style.FILL
    
    paintTransparentCircle = Paint(Paint.ANTI_ALIAS_FLAG)
    paintTransparentCircle.color = Color.WHITE
    paintTransparentCircle.style = Paint.Style.FILL
    paintTransparentCircle.alpha = 105
    
    paintCenterText = TextPaint(Paint.ANTI_ALIAS_FLAG)
    paintCenterText.color = Color.BLACK
    paintCenterText.textSize = Utils.convertDpToPixel(12f)
    
    mValuePaint.textSize = Utils.convertDpToPixel(13f)
    mValuePaint.color = Color.WHITE
    mValuePaint.textAlign = Paint.Align.CENTER
    
    paintEntryLabels = Paint(Paint.ANTI_ALIAS_FLAG)
    paintEntryLabels.color = Color.WHITE
    paintEntryLabels.textAlign = Paint.Align.CENTER
    paintEntryLabels.textSize = Utils.convertDpToPixel(13f)
    
    mValueLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    mValueLinePaint.style = Paint.Style.STROKE
  }
  
  override fun initBuffers() {
    // TODO Auto-generated method stub
  }
  
  override fun drawData(c: Canvas) {
    
    val width = mViewPortHandler.chartWidth.toInt()
    val height = mViewPortHandler.chartHeight.toInt()
    
    if (mDrawBitmap == null || mDrawBitmap!!.get()!!.width != width || mDrawBitmap!!.get()!!.height != height) {
      
      if (width > 0 && height > 0) {
        
        mDrawBitmap = WeakReference(
          Bitmap.createBitmap(
            width,
            height,
            Bitmap.Config.ARGB_4444
          )
        )
        mBitmapCanvas = Canvas(mDrawBitmap!!.get()!!)
      } else return
    }
    
    mDrawBitmap!!.get()!!.eraseColor(Color.TRANSPARENT)
    
    val pieData = mChart.data
    
    for (set in pieData.dataSets) {
      
      if (set.isVisible && set.entryCount > 0) drawDataSet(
        c,
        set
      )
    }
  }
  
  protected fun calculateMinimumRadiusForSpacedSlice(
    center: MPPointF,
    radius: Float,
    angle: Float,
    arcStartPointX: Float,
    arcStartPointY: Float,
    startAngle: Float,
    sweepAngle: Float
  ): Float {
    val angleMiddle = startAngle + sweepAngle / 2f
    
    // Other point of the arc
    val arcEndPointX = center.x + radius * Math.cos(((startAngle + sweepAngle) * Utils.FDEG2RAD).toDouble()).toFloat()
    val arcEndPointY = center.y + radius * Math.sin(((startAngle + sweepAngle) * Utils.FDEG2RAD).toDouble()).toFloat()
    
    // Middle point on the arc
    val arcMidPointX = center.x + radius * Math.cos((angleMiddle * Utils.FDEG2RAD).toDouble()).toFloat()
    val arcMidPointY = center.y + radius * Math.sin((angleMiddle * Utils.FDEG2RAD).toDouble()).toFloat()
    
    // This is the base of the contained triangle
    val basePointsDistance = Math.sqrt(
      Math.pow(
        (arcEndPointX - arcStartPointX).toDouble(),
        2.0
      ) + Math.pow(
        (arcEndPointY - arcStartPointY).toDouble(),
        2.0
      )
    )
    
    // After reducing space from both sides of the "slice",
    //   the angle of the contained triangle should stay the same.
    // So let's find out the height of that triangle.
    val containedTriangleHeight = (basePointsDistance / 2.0 * Math.tan((180.0 - angle) / 2.0 * Utils.DEG2RAD)).toFloat()
    
    // Now we subtract that from the radius
    var spacedRadius = radius - containedTriangleHeight
    
    // And now subtract the height of the arc that's between the triangle and the outer circle
    spacedRadius -= Math.sqrt(
      Math.pow(
        (arcMidPointX - (arcEndPointX + arcStartPointX) / 2f).toDouble(),
        2.0
      ) + Math.pow(
        (arcMidPointY - (arcEndPointY + arcStartPointY) / 2f).toDouble(),
        2.0
      )
    ).toFloat()
    
    return spacedRadius
  }
  
  /**
   * Calculates the sliceSpace to use based on visible values and their size compared to the set sliceSpace.
   *
   * @param dataSet
   * @return
   */
  protected fun getSliceSpace(dataSet: IPieDataSet): Float {
    
    if (!dataSet.isAutomaticallyDisableSliceSpacingEnabled) return dataSet.sliceSpace
    
    val spaceSizeRatio = dataSet.sliceSpace / mViewPortHandler.smallestContentExtension
    val minValueRatio = dataSet.yMin / mChart.data.yValueSum * 2
    
    return if (spaceSizeRatio > minValueRatio) 0f else dataSet.sliceSpace
  }
  
  protected fun drawDataSet(
    c: Canvas,
    dataSet: IPieDataSet
  ) {
    
    var angle = 0f
    val rotationAngle = mChart.rotationAngle
    
    val phaseX = mAnimator.phaseX
    val phaseY = mAnimator.phaseY
    
    val circleBox = mChart.circleBox
    
    val entryCount = dataSet.entryCount
    val drawAngles = mChart.drawAngles
    val center = mChart.centerCircleBox
    val radius = mChart.radius
    val drawInnerArc = mChart.isDrawHoleEnabled && !mChart.isDrawSlicesUnderHoleEnabled
    val userInnerRadius = if (drawInnerArc) radius * (mChart.holeRadius / 100f)
    else 0f
    
    var visibleAngleCount = 0
    for (j in 0 until entryCount) {
      // draw only if the value is greater than zero
      if (Math.abs(dataSet.getEntryForIndex(j).y) > Utils.FLOAT_EPSILON) {
        visibleAngleCount++
      }
    }
    
    val sliceSpace = if (visibleAngleCount <= 1) 0f else getSliceSpace(dataSet)
    
    for (j in 0 until entryCount) {
      
      val sliceAngle = drawAngles[j]
      var innerRadius = userInnerRadius
      
      val e = dataSet.getEntryForIndex(j)
      
      // draw only if the value is greater than zero
      if (Math.abs(e.y) > Utils.FLOAT_EPSILON) {
        
        if (!mChart.needsHighlight(j)) {
          
          val accountForSliceSpacing = sliceSpace > 0f && sliceAngle <= 180f
          
          mRenderPaint.color = dataSet.getColor(j)
          
          val sliceSpaceAngleOuter = if (visibleAngleCount == 1) 0f
          else sliceSpace / (Utils.FDEG2RAD * radius)
          val startAngleOuter = rotationAngle + (angle + sliceSpaceAngleOuter / 2f) * phaseY
          var sweepAngleOuter = (sliceAngle - sliceSpaceAngleOuter) * phaseY
          if (sweepAngleOuter < 0f) {
            sweepAngleOuter = 0f
          }
          
          mPathBuffer.reset()
          
          val arcStartPointX = center.x + radius * Math.cos((startAngleOuter * Utils.FDEG2RAD).toDouble()).toFloat()
          val arcStartPointY = center.y + radius * Math.sin((startAngleOuter * Utils.FDEG2RAD).toDouble()).toFloat()
          
          if (sweepAngleOuter >= 360f && sweepAngleOuter % 360f <= Utils.FLOAT_EPSILON) {
            // Android is doing "mod 360"
            mPathBuffer.addCircle(
              center.x,
              center.y,
              radius,
              Path.Direction.CW
            )
          } else {
            
            mPathBuffer.moveTo(
              arcStartPointX,
              arcStartPointY
            )
            
            mPathBuffer.arcTo(
              circleBox,
              startAngleOuter,
              sweepAngleOuter
            )
          }
          
          // API < 21 does not receive floats in addArc, but a RectF
          mInnerRectBuffer.set(
            center.x - innerRadius,
            center.y - innerRadius,
            center.x + innerRadius,
            center.y + innerRadius
          )
          
          if (drawInnerArc && (innerRadius > 0f || accountForSliceSpacing)) {
            
            if (accountForSliceSpacing) {
              var minSpacedRadius = calculateMinimumRadiusForSpacedSlice(
                center,
                radius,
                sliceAngle * phaseY,
                arcStartPointX,
                arcStartPointY,
                startAngleOuter,
                sweepAngleOuter
              )
              
              if (minSpacedRadius < 0f) minSpacedRadius = -minSpacedRadius
              
              innerRadius = Math.max(
                innerRadius,
                minSpacedRadius
              )
            }
            
            val sliceSpaceAngleInner = if (visibleAngleCount == 1 || innerRadius == 0f) 0f
            else sliceSpace / (Utils.FDEG2RAD * innerRadius)
            val startAngleInner = rotationAngle + (angle + sliceSpaceAngleInner / 2f) * phaseY
            var sweepAngleInner = (sliceAngle - sliceSpaceAngleInner) * phaseY
            if (sweepAngleInner < 0f) {
              sweepAngleInner = 0f
            }
            val endAngleInner = startAngleInner + sweepAngleInner
            
            if (sweepAngleOuter >= 360f && sweepAngleOuter % 360f <= Utils.FLOAT_EPSILON) {
              // Android is doing "mod 360"
              mPathBuffer.addCircle(
                center.x,
                center.y,
                innerRadius,
                Path.Direction.CCW
              )
            } else {
              
              mPathBuffer.lineTo(
                center.x + innerRadius * Math.cos((endAngleInner * Utils.FDEG2RAD).toDouble()).toFloat(),
                center.y + innerRadius * Math.sin((endAngleInner * Utils.FDEG2RAD).toDouble()).toFloat()
              )
              
              mPathBuffer.arcTo(
                mInnerRectBuffer,
                endAngleInner,
                -sweepAngleInner
              )
            }
          } else {
            
            if (sweepAngleOuter % 360f > Utils.FLOAT_EPSILON) {
              if (accountForSliceSpacing) {
                
                val angleMiddle = startAngleOuter + sweepAngleOuter / 2f
                
                val sliceSpaceOffset = calculateMinimumRadiusForSpacedSlice(
                  center,
                  radius,
                  sliceAngle * phaseY,
                  arcStartPointX,
                  arcStartPointY,
                  startAngleOuter,
                  sweepAngleOuter
                )
                
                val arcEndPointX = center.x + sliceSpaceOffset * Math.cos((angleMiddle * Utils.FDEG2RAD).toDouble()).toFloat()
                val arcEndPointY = center.y + sliceSpaceOffset * Math.sin((angleMiddle * Utils.FDEG2RAD).toDouble()).toFloat()
                
                mPathBuffer.lineTo(
                  arcEndPointX,
                  arcEndPointY
                )
                
              } else {
                mPathBuffer.lineTo(
                  center.x,
                  center.y
                )
              }
            }
            
          }
          
          mPathBuffer.close()
          
          mBitmapCanvas!!.drawPath(
            mPathBuffer,
            mRenderPaint
          )
        }
      }
      
      angle += sliceAngle * phaseX
    }
    
    MPPointF.recycleInstance(center)
  }
  
  override fun drawValues(c: Canvas) {
    
    val center = mChart.centerCircleBox
    
    // get whole the radius
    val radius = mChart.radius
    val rotationAngle = mChart.rotationAngle
    val drawAngles = mChart.drawAngles
    val absoluteAngles = mChart.absoluteAngles
    
    val phaseX = mAnimator.phaseX
    val phaseY = mAnimator.phaseY
    
    val holeRadiusPercent = mChart.holeRadius / 100f
    var labelRadiusOffset = radius / 10f * 3.6f
    
    if (mChart.isDrawHoleEnabled) {
      labelRadiusOffset = (radius - radius * holeRadiusPercent) / 2f
    }
    
    val labelRadius = radius - labelRadiusOffset
    
    val data = mChart.data
    val dataSets = data.dataSets
    
    val yValueSum = data.yValueSum
    
    val drawEntryLabels = mChart.isDrawEntryLabelsEnabled
    
    var angle: Float
    var xIndex = 0
    
    c.save()
    
    val offset = Utils.convertDpToPixel(5f)
    
    for (i in dataSets.indices) {
      
      val dataSet = dataSets[i]
      
      val drawValues = dataSet.isDrawValuesEnabled
      
      if (!drawValues && !drawEntryLabels) continue
      
      val xValuePosition = dataSet.xValuePosition
      val yValuePosition = dataSet.yValuePosition
      
      // apply the text-styling defined by the DataSet
      applyValueTextStyle(dataSet)
      
      val lineHeight = Utils.calcTextHeight(
        mValuePaint,
        "Q"
      ) + Utils.convertDpToPixel(4f)
      
      val formatter = dataSet.valueFormatter
      
      val entryCount = dataSet.entryCount
      
      mValueLinePaint.color = dataSet.valueLineColor
      mValueLinePaint.strokeWidth = Utils.convertDpToPixel(dataSet.valueLineWidth)
      
      val sliceSpace = getSliceSpace(dataSet)
      
      val iconsOffset = MPPointF.getInstance(dataSet.iconsOffset)
      iconsOffset.x = Utils.convertDpToPixel(iconsOffset.x)
      iconsOffset.y = Utils.convertDpToPixel(iconsOffset.y)
      
      for (j in 0 until entryCount) {
        
        val entry = dataSet.getEntryForIndex(j)
        
        if (xIndex == 0) angle = 0f
        else angle = absoluteAngles[xIndex - 1] * phaseX
        
        val sliceAngle = drawAngles[xIndex]
        val sliceSpaceMiddleAngle = sliceSpace / (Utils.FDEG2RAD * labelRadius)
        
        // offset needed to center the drawn text in the slice
        val angleOffset = (sliceAngle - sliceSpaceMiddleAngle / 2f) / 2f
        
        angle = angle + angleOffset
        
        val transformedAngle = rotationAngle + angle * phaseY
        
        val value = if (mChart.isUsePercentValuesEnabled) entry.y / yValueSum * 100f
        else entry.y
        
        val sliceXBase = Math.cos((transformedAngle * Utils.FDEG2RAD).toDouble()).toFloat()
        val sliceYBase = Math.sin((transformedAngle * Utils.FDEG2RAD).toDouble()).toFloat()
        
        val drawXOutside = drawEntryLabels && xValuePosition == PieDataSet.ValuePosition.OUTSIDE_SLICE
        val drawYOutside = drawValues && yValuePosition == PieDataSet.ValuePosition.OUTSIDE_SLICE
        val drawXInside = drawEntryLabels && xValuePosition == PieDataSet.ValuePosition.INSIDE_SLICE
        val drawYInside = drawValues && yValuePosition == PieDataSet.ValuePosition.INSIDE_SLICE
        
        if (drawXOutside || drawYOutside) {
          
          val valueLineLength1 = dataSet.valueLinePart1Length
          val valueLineLength2 = dataSet.valueLinePart2Length
          val valueLinePart1OffsetPercentage = dataSet.valueLinePart1OffsetPercentage / 100f
          
          val pt2x: Float
          val pt2y: Float
          val labelPtx: Float
          val labelPty: Float
          
          val line1Radius: Float
          
          if (mChart.isDrawHoleEnabled) line1Radius = (radius - radius * holeRadiusPercent) * valueLinePart1OffsetPercentage + radius * holeRadiusPercent
          else line1Radius = radius * valueLinePart1OffsetPercentage
          
          val polyline2Width = if (dataSet.isValueLineVariableLength) labelRadius * valueLineLength2 * Math.abs(
            Math.sin(
              (transformedAngle * Utils.FDEG2RAD).toDouble()
            )
          ).toFloat()
          else labelRadius * valueLineLength2
          
          val pt0x = line1Radius * sliceXBase + center.x
          val pt0y = line1Radius * sliceYBase + center.y
          
          val pt1x = labelRadius * (1 + valueLineLength1) * sliceXBase + center.x
          val pt1y = labelRadius * (1 + valueLineLength1) * sliceYBase + center.y
          
          if (transformedAngle % 360.0 >= 90.0 && transformedAngle % 360.0 <= 270.0) {
            pt2x = pt1x - polyline2Width
            pt2y = pt1y
            
            mValuePaint.textAlign = Paint.Align.RIGHT
            
            if (drawXOutside) paintEntryLabels.textAlign = Paint.Align.RIGHT
            
            labelPtx = pt2x - offset
            labelPty = pt2y
          } else {
            pt2x = pt1x + polyline2Width
            pt2y = pt1y
            mValuePaint.textAlign = Paint.Align.LEFT
            
            if (drawXOutside) paintEntryLabels.textAlign = Paint.Align.LEFT
            
            labelPtx = pt2x + offset
            labelPty = pt2y
          }
          
          if (dataSet.valueLineColor != ColorTemplate.COLOR_NONE) {
            c.drawLine(
              pt0x,
              pt0y,
              pt1x,
              pt1y,
              mValueLinePaint
            )
            c.drawLine(
              pt1x,
              pt1y,
              pt2x,
              pt2y,
              mValueLinePaint
            )
          }
          
          // draw everything, depending on settings
          if (drawXOutside && drawYOutside) {
            
            drawValue(
              c,
              formatter,
              value,
              entry,
              0,
              labelPtx,
              labelPty,
              dataSet.getValueTextColor(j)
            )
            
            if (j < data.entryCount && entry.label != null) {
              drawEntryLabel(
                c,
                entry.label,
                labelPtx,
                labelPty + lineHeight
              )
            }
            
          } else if (drawXOutside) {
            if (j < data.entryCount && entry.label != null) {
              drawEntryLabel(
                c,
                entry.label,
                labelPtx,
                labelPty + lineHeight / 2f
              )
            }
          } else if (drawYOutside) {
            
            drawValue(
              c,
              formatter,
              value,
              entry,
              0,
              labelPtx,
              labelPty + lineHeight / 2f,
              dataSet.getValueTextColor(j)
            )
          }
        }
        
        if (drawXInside || drawYInside) {
          // calculate the text position
          val x = labelRadius * sliceXBase + center.x
          val y = labelRadius * sliceYBase + center.y
          
          mValuePaint.textAlign = Paint.Align.CENTER
          
          // draw everything, depending on settings
          if (drawXInside && drawYInside) {
            
            drawValue(
              c,
              formatter,
              value,
              entry,
              0,
              x,
              y,
              dataSet.getValueTextColor(j)
            )
            
            if (j < data.entryCount && entry.label != null) {
              drawEntryLabel(
                c,
                entry.label,
                x,
                y + lineHeight
              )
            }
            
          } else if (drawXInside) {
            if (j < data.entryCount && entry.label != null) {
              drawEntryLabel(
                c,
                entry.label,
                x,
                y + lineHeight / 2f
              )
            }
          } else if (drawYInside) {
            
            drawValue(
              c,
              formatter,
              value,
              entry,
              0,
              x,
              y + lineHeight / 2f,
              dataSet.getValueTextColor(j)
            )
          }
        }
        
        if (entry.icon != null && dataSet.isDrawIconsEnabled) {
          
          val icon = entry.icon
          
          val x = (labelRadius + iconsOffset.y) * sliceXBase + center.x
          var y = (labelRadius + iconsOffset.y) * sliceYBase + center.y
          y += iconsOffset.x
          
          Utils.drawImage(
            c,
            icon,
            x.toInt(),
            y.toInt(),
            icon.intrinsicWidth,
            icon.intrinsicHeight
          )
        }
        
        xIndex++
      }
      
      MPPointF.recycleInstance(iconsOffset)
    }
    MPPointF.recycleInstance(center)
    c.restore()
  }
  
  /**
   * Draws an entry label at the specified position.
   *
   * @param c
   * @param label
   * @param x
   * @param y
   */
  protected fun drawEntryLabel(
    c: Canvas,
    label: String,
    x: Float,
    y: Float
  ) {
    c.drawText(
      label,
      x,
      y,
      paintEntryLabels
    )
  }
  
  override fun drawExtras(c: Canvas) {
    // drawCircles(c);
    drawHole(c)
    c.drawBitmap(
      mDrawBitmap!!.get()!!,
      0f,
      0f,
      null
    )
    drawCenterText(c)
  }
  
  /**
   * draws the hole in the center of the chart and the transparent circle /
   * hole
   */
  protected fun drawHole(c: Canvas) {
    
    if (mChart.isDrawHoleEnabled && mBitmapCanvas != null) {
      
      val radius = mChart.radius
      val holeRadius = radius * (mChart.holeRadius / 100)
      val center = mChart.centerCircleBox
      
      if (Color.alpha(paintHole.color) > 0) {
        // draw the hole-circle
        mBitmapCanvas!!.drawCircle(
          center.x,
          center.y,
          holeRadius,
          paintHole
        )
      }
      
      // only draw the circle if it can be seen (not covered by the hole)
      if (Color.alpha(paintTransparentCircle.color) > 0 && mChart.transparentCircleRadius > mChart.holeRadius) {
        
        val alpha = paintTransparentCircle.alpha
        val secondHoleRadius = radius * (mChart.transparentCircleRadius / 100)
        
        paintTransparentCircle.alpha = (alpha.toFloat() * mAnimator.phaseX * mAnimator.phaseY).toInt()
        
        // draw the transparent-circle
        mHoleCirclePath.reset()
        mHoleCirclePath.addCircle(
          center.x,
          center.y,
          secondHoleRadius,
          Path.Direction.CW
        )
        mHoleCirclePath.addCircle(
          center.x,
          center.y,
          holeRadius,
          Path.Direction.CCW
        )
        mBitmapCanvas!!.drawPath(
          mHoleCirclePath,
          paintTransparentCircle
        )
        
        // reset alpha
        paintTransparentCircle.alpha = alpha
      }
      MPPointF.recycleInstance(center)
    }
  }
  
  /**
   * draws the description text in the center of the pie chart makes most
   * sense when center-hole is enabled
   */
  protected fun drawCenterText(c: Canvas) {
    
    val centerText = mChart.centerText
    
    if (mChart.isDrawCenterTextEnabled && centerText != null) {
      
      val center = mChart.centerCircleBox
      val offset = mChart.centerTextOffset
      
      val x = center.x + offset.x
      val y = center.y + offset.y
      
      val innerRadius = if (mChart.isDrawHoleEnabled && !mChart.isDrawSlicesUnderHoleEnabled) mChart.radius * (mChart.holeRadius / 100f)
      else mChart.radius
      
      val holeRect = mRectBuffer[0]
      holeRect.left = x - innerRadius
      holeRect.top = y - innerRadius
      holeRect.right = x + innerRadius
      holeRect.bottom = y + innerRadius
      val boundingRect = mRectBuffer[1]
      boundingRect.set(holeRect)
      
      val radiusPercent = mChart.centerTextRadiusPercent / 100f
      if (radiusPercent > 0.0) {
        boundingRect.inset(
          (boundingRect.width() - boundingRect.width() * radiusPercent) / 2f,
          (boundingRect.height() - boundingRect.height() * radiusPercent) / 2f
        )
      }
      
      if (centerText != mCenterTextLastValue || boundingRect != mCenterTextLastBounds) {
        
        // Next time we won't recalculate StaticLayout...
        mCenterTextLastBounds.set(boundingRect)
        mCenterTextLastValue = centerText
        
        val width = mCenterTextLastBounds.width()
        
        // If width is 0, it will crash. Always have a minimum of 1
        mCenterTextLayout = StaticLayout(
          centerText,
          0,
          centerText.length,
          paintCenterText,
          Math.max(
            Math.ceil(width.toDouble()),
            1.0
          ).toInt(),
          Layout.Alignment.ALIGN_CENTER,
          1f,
          0f,
          false
        )
      }
      
      //float layoutWidth = Utils.getStaticLayoutMaxWidth(mCenterTextLayout);
      val layoutHeight = mCenterTextLayout!!.height.toFloat()
      
      c.save()
      if (Build.VERSION.SDK_INT >= 18) {
        val path = mDrawCenterTextPathBuffer
        path.reset()
        path.addOval(
          holeRect,
          Path.Direction.CW
        )
        c.clipPath(path)
      }
      
      c.translate(
        boundingRect.left,
        boundingRect.top + (boundingRect.height() - layoutHeight) / 2f
      )
      mCenterTextLayout!!.draw(c)
      
      c.restore()
      
      MPPointF.recycleInstance(center)
      MPPointF.recycleInstance(offset)
    }
  }
  
  override fun drawHighlighted(
    c: Canvas,
    indices: Array<Highlight>
  ) {
    
    val phaseX = mAnimator.phaseX
    val phaseY = mAnimator.phaseY
    
    var angle: Float
    val rotationAngle = mChart.rotationAngle
    
    val drawAngles = mChart.drawAngles
    val absoluteAngles = mChart.absoluteAngles
    val center = mChart.centerCircleBox
    val radius = mChart.radius
    val drawInnerArc = mChart.isDrawHoleEnabled && !mChart.isDrawSlicesUnderHoleEnabled
    val userInnerRadius = if (drawInnerArc) radius * (mChart.holeRadius / 100f)
    else 0f
    
    val highlightedCircleBox = mDrawHighlightedRectF
    highlightedCircleBox.set(
      0f,
      0f,
      0f,
      0f
    )
    
    for (i in indices.indices) {
      
      // get the index to highlight
      val index = indices[i].x.toInt()
      
      if (index >= drawAngles.size) continue
      
      val set = mChart.data.getDataSetByIndex(
        indices[i].dataSetIndex
      )
      
      if (set == null || !set.isHighlightEnabled) continue
      
      val entryCount = set.entryCount
      var visibleAngleCount = 0
      for (j in 0 until entryCount) {
        // draw only if the value is greater than zero
        if (Math.abs(set.getEntryForIndex(j).y) > Utils.FLOAT_EPSILON) {
          visibleAngleCount++
        }
      }
      
      if (index == 0) angle = 0f
      else angle = absoluteAngles[index - 1] * phaseX
      
      val sliceSpace = if (visibleAngleCount <= 1) 0f else set.sliceSpace
      
      val sliceAngle = drawAngles[index]
      var innerRadius = userInnerRadius
      
      val shift = set.selectionShift
      val highlightedRadius = radius + shift
      highlightedCircleBox.set(mChart.circleBox)
      highlightedCircleBox.inset(
        -shift,
        -shift
      )
      
      val accountForSliceSpacing = sliceSpace > 0f && sliceAngle <= 180f
      
      mRenderPaint.color = set.getColor(index)
      
      val sliceSpaceAngleOuter = if (visibleAngleCount == 1) 0f
      else sliceSpace / (Utils.FDEG2RAD * radius)
      
      val sliceSpaceAngleShifted = if (visibleAngleCount == 1) 0f
      else sliceSpace / (Utils.FDEG2RAD * highlightedRadius)
      
      val startAngleOuter = rotationAngle + (angle + sliceSpaceAngleOuter / 2f) * phaseY
      var sweepAngleOuter = (sliceAngle - sliceSpaceAngleOuter) * phaseY
      if (sweepAngleOuter < 0f) {
        sweepAngleOuter = 0f
      }
      
      val startAngleShifted = rotationAngle + (angle + sliceSpaceAngleShifted / 2f) * phaseY
      var sweepAngleShifted = (sliceAngle - sliceSpaceAngleShifted) * phaseY
      if (sweepAngleShifted < 0f) {
        sweepAngleShifted = 0f
      }
      
      mPathBuffer.reset()
      
      if (sweepAngleOuter >= 360f && sweepAngleOuter % 360f <= Utils.FLOAT_EPSILON) {
        // Android is doing "mod 360"
        mPathBuffer.addCircle(
          center.x,
          center.y,
          highlightedRadius,
          Path.Direction.CW
        )
      } else {
        
        mPathBuffer.moveTo(
          center.x + highlightedRadius * Math.cos((startAngleShifted * Utils.FDEG2RAD).toDouble()).toFloat(),
          center.y + highlightedRadius * Math.sin((startAngleShifted * Utils.FDEG2RAD).toDouble()).toFloat()
        )
        
        mPathBuffer.arcTo(
          highlightedCircleBox,
          startAngleShifted,
          sweepAngleShifted
        )
      }
      
      var sliceSpaceRadius = 0f
      if (accountForSliceSpacing) {
        sliceSpaceRadius = calculateMinimumRadiusForSpacedSlice(
          center,
          radius,
          sliceAngle * phaseY,
          center.x + radius * Math.cos((startAngleOuter * Utils.FDEG2RAD).toDouble()).toFloat(),
          center.y + radius * Math.sin((startAngleOuter * Utils.FDEG2RAD).toDouble()).toFloat(),
          startAngleOuter,
          sweepAngleOuter
        )
      }
      
      // API < 21 does not receive floats in addArc, but a RectF
      mInnerRectBuffer.set(
        center.x - innerRadius,
        center.y - innerRadius,
        center.x + innerRadius,
        center.y + innerRadius
      )
      
      if (drawInnerArc && (innerRadius > 0f || accountForSliceSpacing)) {
        
        if (accountForSliceSpacing) {
          var minSpacedRadius = sliceSpaceRadius
          
          if (minSpacedRadius < 0f) minSpacedRadius = -minSpacedRadius
          
          innerRadius = Math.max(
            innerRadius,
            minSpacedRadius
          )
        }
        
        val sliceSpaceAngleInner = if (visibleAngleCount == 1 || innerRadius == 0f) 0f
        else sliceSpace / (Utils.FDEG2RAD * innerRadius)
        val startAngleInner = rotationAngle + (angle + sliceSpaceAngleInner / 2f) * phaseY
        var sweepAngleInner = (sliceAngle - sliceSpaceAngleInner) * phaseY
        if (sweepAngleInner < 0f) {
          sweepAngleInner = 0f
        }
        val endAngleInner = startAngleInner + sweepAngleInner
        
        if (sweepAngleOuter >= 360f && sweepAngleOuter % 360f <= Utils.FLOAT_EPSILON) {
          // Android is doing "mod 360"
          mPathBuffer.addCircle(
            center.x,
            center.y,
            innerRadius,
            Path.Direction.CCW
          )
        } else {
          
          mPathBuffer.lineTo(
            center.x + innerRadius * Math.cos((endAngleInner * Utils.FDEG2RAD).toDouble()).toFloat(),
            center.y + innerRadius * Math.sin((endAngleInner * Utils.FDEG2RAD).toDouble()).toFloat()
          )
          
          mPathBuffer.arcTo(
            mInnerRectBuffer,
            endAngleInner,
            -sweepAngleInner
          )
        }
      } else {
        
        if (sweepAngleOuter % 360f > Utils.FLOAT_EPSILON) {
          
          if (accountForSliceSpacing) {
            val angleMiddle = startAngleOuter + sweepAngleOuter / 2f
            
            val arcEndPointX = center.x + sliceSpaceRadius * Math.cos((angleMiddle * Utils.FDEG2RAD).toDouble()).toFloat()
            val arcEndPointY = center.y + sliceSpaceRadius * Math.sin((angleMiddle * Utils.FDEG2RAD).toDouble()).toFloat()
            
            mPathBuffer.lineTo(
              arcEndPointX,
              arcEndPointY
            )
            
          } else {
            
            mPathBuffer.lineTo(
              center.x,
              center.y
            )
          }
          
        }
        
      }
      
      mPathBuffer.close()
      
      mBitmapCanvas!!.drawPath(
        mPathBuffer,
        mRenderPaint
      )
    }
    
    MPPointF.recycleInstance(center)
  }
  
  /**
   * This gives all pie-slices a rounded edge.
   *
   * @param c
   */
  protected fun drawRoundedSlices(c: Canvas) {
    
    if (!mChart.isDrawRoundedSlicesEnabled) return
    
    val dataSet = mChart.data.dataSet
    
    if (!dataSet.isVisible) return
    
    val phaseX = mAnimator.phaseX
    val phaseY = mAnimator.phaseY
    
    val center = mChart.centerCircleBox
    val r = mChart.radius
    
    // calculate the radius of the "slice-circle"
    val circleRadius = (r - r * mChart.holeRadius / 100f) / 2f
    
    val drawAngles = mChart.drawAngles
    var angle = mChart.rotationAngle
    
    for (j in 0 until dataSet.entryCount) {
      
      val sliceAngle = drawAngles[j]
      
      val e = dataSet.getEntryForIndex(j)
      
      // draw only if the value is greater than zero
      if (Math.abs(e.y) > Utils.FLOAT_EPSILON) {
        
        val x = ((r - circleRadius) * Math.cos(Math.toRadians(((angle + sliceAngle) * phaseY).toDouble())) + center.x).toFloat()
        val y = ((r - circleRadius) * Math.sin(Math.toRadians(((angle + sliceAngle) * phaseY).toDouble())) + center.y).toFloat()
        
        mRenderPaint.color = dataSet.getColor(j)
        mBitmapCanvas!!.drawCircle(
          x,
          y,
          circleRadius,
          mRenderPaint
        )
      }
      
      angle += sliceAngle * phaseX
    }
    MPPointF.recycleInstance(center)
  }
  
  /**
   * Releases the drawing bitmap. This should be called when [LineChart.onDetachedFromWindow].
   */
  fun releaseBitmap() {
    if (mBitmapCanvas != null) {
      mBitmapCanvas!!.setBitmap(null)
      mBitmapCanvas = null
    }
    if (mDrawBitmap != null) {
      mDrawBitmap!!.get()!!.recycle()
      mDrawBitmap!!.clear()
      mDrawBitmap = null
    }
  }
}
