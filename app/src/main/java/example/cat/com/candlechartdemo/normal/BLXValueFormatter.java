package example.cat.com.candlechartdemo.normal;


import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by melon on 2017/8/8.
 * x 轴
 */

public class BLXValueFormatter implements IAxisValueFormatter {

  //    private List<IncomeBean> stringList;
  private BarLineChartBase<?> chart;

  public BLXValueFormatter(BarLineChartBase<?> chart) {
    this.chart = chart;
  //        this.stringList = stringList;
  }


  @Override
  public String getFormattedValue(float value, AxisBase axis) {
    int position = (int) value;
    CandleEntry entry = ((CandleDataSet)this.chart.getData().getDataSetByIndex(0)).getValues().get(position);
    String str = "";
    SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
    str = formater.format(new Date((Long) entry.getData()));
    return str;

  }


}

