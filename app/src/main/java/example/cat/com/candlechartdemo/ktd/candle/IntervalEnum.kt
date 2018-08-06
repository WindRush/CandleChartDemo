package example.cat.com.candlechartdemo.ktd.candle

/**
 * @date: 2018/8/2.
 * @author: yanglihai
 * @description:
 */
enum class IntervalEnum(val interval: String) {
//
//  val interval: String
//
//  init {
//    this.interval = interval
//  }
//
  ONE_MINUTE("1m"),
  THREE_MINUTES("3m"),
  FIVE_MINUTES("5m"),
  FIFTEEN_MINUTES("15m"),
  HALF_HOUR("30m"),
  ONE_HOUR("1h"),
  TWO_HOURS("2h"),
  FOUR_HOURS("4h"),
  SIX_HOURS("6h"),
  EIGHT_HOURS("8h"),
  TWENTEEN_HOURS("12h"),
  ONE_DAY("1d"),
  THREE_DAYS("3d"),
  ONE_WEEK("1w"),
  ONE_MONTH("1M");
  
  
  
}
