package example.cat.com.candlechartdemo.ktd.pie

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.widget.RelativeLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.relativeLayout

/**
 * @date: 2018/9/18.
 * @author: yanglihai
 * @description:
 */
class PieChartActivity: AppCompatActivity() {
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    relativeLayout {
      addView(
        BlinnnkPieChart(this@PieChartActivity).apply {
        layoutParams = RelativeLayout.LayoutParams(matchParent, 1200)
        }
      )
    }
  }
  
  
}