package example.cat.com.candlechartdemo.ktd.candle

import android.util.Log
import org.json.JSONObject

/**
 * @date: 2018/8/8.
 * @author: yanglihai
 * @description: 服务器返回的蜡烛图item的类
 */
class CandleChartModel (
	val high: String,
	val low: String,
	val close: String,
	val open: String,
	val time: String
) {
	constructor(data: JSONObject) : this(
		data.safeGet("high"), data.safeGet("low"),
			data.safeGet("close"), data.safeGet("open"),
		data.safeGet("time")
	
	)
}

fun JSONObject.safeGet(key: String): String {
	return try {
		get(key).toString()
	} catch (error: Exception) {
		Log.e("ERROR", "function: safeGet $error")
		""
	}
}