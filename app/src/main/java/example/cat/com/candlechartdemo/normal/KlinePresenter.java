package example.cat.com.candlechartdemo.normal;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @date: 2018/7/31.
 * @author: yanglihai
 * @description:
 */
public class KlinePresenter {

  public static String interval = "1d";

  public static String getData() throws IOException{
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder()
      .url("https://api.binance.com/api/v1/klines?symbol=LTCBTC&interval="+interval)
      .build();

    Response response = client.newCall(request).execute();
    return response.body().string();

  }

  public static String requestData(String s) throws IOException{
    interval = s;
    return getData();
  }
}
