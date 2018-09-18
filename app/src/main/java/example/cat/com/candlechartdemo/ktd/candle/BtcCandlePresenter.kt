package example.cat.com.candlechartdemo.ktd.candle

import org.json.JSONArray
import org.json.JSONObject

/**
 * @date: 2018/8/21.
 * @author: yanglihai
 * @description:
 */
object BtcCandlePresenter {
  
  fun parseData(callback: (List<CandleChartModel>) -> Unit) {
    val jsonArray = JSONArray(CandleConstants.jsonData)
    // 把数据转换成需要的格式
    (0 until jsonArray.length()).map {
      CandleChartModel(JSONObject(jsonArray[it]?.toString()))
    }.let {
      callback(it)
    }
  }
  
}