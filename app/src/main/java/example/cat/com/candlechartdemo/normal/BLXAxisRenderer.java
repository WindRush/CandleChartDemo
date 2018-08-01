
package example.cat.com.candlechartdemo.normal;

import android.graphics.Canvas;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class BLXAxisRenderer extends XAxisRenderer {


  public BLXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
    super(viewPortHandler, xAxis, trans);
  }

  @Override
  public void renderAxisLine(Canvas c) {
    super.renderAxisLine(c);
    c.drawLine(mViewPortHandler.contentLeft(),
      mViewPortHandler.contentTop(), mViewPortHandler.contentRight(),
      mViewPortHandler.contentTop(), mAxisLinePaint);
  }
}
