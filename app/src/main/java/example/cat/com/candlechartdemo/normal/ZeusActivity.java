package example.cat.com.candlechartdemo.normal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.github.mikephil.charting.data.CandleEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import example.cat.com.candlechartdemo.R;

/**
 * @date: 2018/7/31.
 * @author: yanglihai
 * @description:
 */
public class ZeusActivity extends AppCompatActivity {

  BLCandleStickChart candleStickChart;


  private List<String[]> dataSet;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    candleStickChart = findViewById(R.id.candle_chart);
    requestData();

  }

  void requestData(){
    new Thread(){
      @Override
      public void run() {
        super.run();
        try {
          String result = KlinePresenter.getData();
          dataSet =  JSON.parseArray(result,String[].class);
          runOnUiThread(new Runnable() {
            @Override
            public void run() {
              setData();
            }
          });

        } catch (IOException e) {
          e.printStackTrace();
          Toast.makeText(ZeusActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
        }
      }
    }.start();
  }

  void setData() {

    List<CandleEntry> yVals1 = new ArrayList<>();
    for (int i = 0; i < (dataSet.size()>200?200: dataSet.size()); i++) {
      yVals1.add(new CandleEntry(Float.valueOf(i),
        Float.valueOf(dataSet.get(i)[2]),
        Float.valueOf(dataSet.get(i)[3]),
        Float.valueOf(dataSet.get(i)[1]),
        Float.valueOf(dataSet.get(i)[4]),
        Long.valueOf(dataSet.get(i)[0])));

    }
    candleStickChart.resetData(yVals1);
  }

}
