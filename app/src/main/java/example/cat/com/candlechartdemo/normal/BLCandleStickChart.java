
package example.cat.com.candlechartdemo.normal;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.interfaces.dataprovider.CandleDataProvider;

import java.util.List;


/**
 * Financial chart type that draws candle-sticks (OHCL chart).
 *
 * @author Philipp Jahoda
 */
public class BLCandleStickChart extends BarLineChartBase<CandleData> implements CandleDataProvider {


int labelColor = Color.rgb(152,152,152);

  public BLCandleStickChart(Context context) {
    super(context);
  }

  public BLCandleStickChart(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public BLCandleStickChart(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }



  @Override
  protected void init() {
    super.init();

    mXAxisRenderer =  new BLXAxisRenderer(mViewPortHandler, mXAxis, mLeftAxisTransformer);
    mRenderer = new BLCandleStickChartRenderer(this, mAnimator, mViewPortHandler);

    getXAxis().setSpaceMin(0.5f);
    getXAxis().setSpaceMax(0.5f);
  }


  public void resetData(List<CandleEntry> dataSet) {

    resetTracking();
    initAxisStyle();

    CandleDataSet set1 = new CandleDataSet(dataSet, "Data Set");

    set1.setDrawIcons(false);
    set1.setAxisDependency(YAxis.AxisDependency.LEFT);
    //        set1.setColor(Color.rgb(80, 80, 80));
    set1.setShadowColor(Color.DKGRAY);
    set1.setShadowWidth(1f);
    set1.setDecreasingColor(Color.rgb(219,74,76));
    set1.setDecreasingPaintStyle(Paint.Style.FILL);
    set1.setIncreasingColor(Color.rgb(67,200,135));
    set1.setIncreasingPaintStyle(Paint.Style.FILL);

    set1.setNeutralColor(Color.BLUE);
    set1.setDrawValues(false);
    set1.setBarSpace(0.2f);
    set1.setShowCandleBar(true);
    set1.setShadowWidth(2);
    //set1.setHighlightLineWidth(1f);
    set1.setShadowColorSameAsCandle(true);

    CandleData data = new CandleData(set1);
  //    data.setValueTextSize(50f);

    setData(data);
    setVisibleXRangeMaximum(20);
    setVisibleXRangeMinimum(20);
    invalidate();
  }

  private void initAxisStyle() {

    setScaleEnabled(false);
    setPinchZoom(true);
    setDragEnabled(true);
    getLegend().setEnabled(false);
    getDescription().setEnabled(false);

    getXAxis().setTextColor(labelColor);
    getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
    getXAxis().setLabelCount(9);
    getXAxis().setLabelRotationAngle(320);
    getXAxis().setValueFormatter(new BLXValueFormatter(this));
    getXAxis().setDrawGridLines(true);

    getAxisLeft().setDrawAxisLine(true);
    getAxisLeft().setDrawLabels(false);
    getAxisRight().setTextColor(labelColor);
    getAxisRight().setAxisLineColor(labelColor);
//    getAxisRight().setDrawGridLines(false);
//    getAxisLeft().setDrawGridLines(false);
//
    BLMarkerView markerView = new BLMarkerView(getContext());
    markerView.setChartView(this);
    setMarker(markerView);
  }

  @Override
  public CandleData getCandleData() {
    return mData;
  }
}
