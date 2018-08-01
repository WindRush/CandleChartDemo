package example.cat.com.candlechartdemo.normal;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import example.cat.com.candlechartdemo.R;


/**
 * Created by melon on 2017/8/10.
 * xy show data
 */

public class BLMarkerView extends MarkerView {

    private TextView tvContent;

    public BLMarkerView(Context context) {
        super(context, R.layout.chart_marker_vier);
        tvContent =  findViewById(R.id.textview_content);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {

      CandleEntry entry = (CandleEntry) e;

        if (entry != null) {
            tvContent.setText("最高："+entry.getHigh()+"\n" +
              "最低："+entry.getLow()+"\n" +
              "开盘："+entry.getOpen()+"\n" +
              "收盘："+entry.getClose() );
        }
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}

