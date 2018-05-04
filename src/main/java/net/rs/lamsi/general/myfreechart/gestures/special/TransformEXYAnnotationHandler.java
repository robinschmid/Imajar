package net.rs.lamsi.general.myfreechart.gestures.special;

import java.awt.geom.AffineTransform;
import java.util.function.Consumer;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import net.rs.lamsi.general.myfreechart.ChartLogics;
import net.rs.lamsi.general.myfreechart.general.annotations.EXYAnnotationEntity;
import net.rs.lamsi.general.myfreechart.general.annotations.EXYShapeAnnotation;
import net.rs.lamsi.general.myfreechart.gestures.ChartGesture.Button;
import net.rs.lamsi.general.myfreechart.gestures.ChartGesture.Entity;
import net.rs.lamsi.general.myfreechart.gestures.ChartGesture.Key;
import net.rs.lamsi.general.myfreechart.gestures.ChartGestureDragDiffEvent;
import net.rs.lamsi.general.myfreechart.gestures.ChartGestureDragDiffHandler;
import net.rs.lamsi.general.myfreechart.plots.image2d.EImage2DChartPanel;

public class TransformEXYAnnotationHandler extends ChartGestureDragDiffHandler
    implements Consumer<ChartGestureDragDiffEvent> {

  private ChartPanel chart;

  public TransformEXYAnnotationHandler(ChartPanel chart, Entity e, Button b, Key k) {
    super(e, b, k);
    this.chart = chart;
    setDragDiffHandler(this);
  }

  public TransformEXYAnnotationHandler(EImage2DChartPanel chart, Entity e, Button b, Key[] k) {
    super(e, b, k);
    this.chart = chart;
    setDragDiffHandler(this);
  }

  @Override
  public void accept(ChartGestureDragDiffEvent drag) {
    ChartEntity ce = drag.getFirstEvent().getEntity();
    if (ce != null) {
      AffineTransform at = new AffineTransform();
      // event not on an axis (e.g. on plot)
      double x = (drag.getLatestEvent().getX() - drag.getLastEvent().getX());
      double y = (drag.getLatestEvent().getY() - drag.getLastEvent().getY()) * -1;
      x = ChartLogics.screenValueToPlotValue(drag.getChartPanel(), (int) x).getX();
      y = ChartLogics.screenValueToPlotValue(drag.getChartPanel(), (int) y).getY();
      // apply xy diff to range and domain axis
      XYPlot p = drag.getChartPanel().getChart().getXYPlot();
      if (p.getOrientation().equals(PlotOrientation.HORIZONTAL)) {
        double tmp = x;
        x = y;
        y = tmp;
      }
      // setup affine transform
      at.translate(x, y);

      if (ce instanceof EXYAnnotationEntity
          && ((EXYAnnotationEntity) ce).getAnnotation() instanceof EXYShapeAnnotation) {
        EXYShapeAnnotation ann = (EXYShapeAnnotation) ((EXYAnnotationEntity) ce).getAnnotation();
        // transform
        ann.transform(at);
        // fire change event
        drag.getChartPanel().getChart().fireChartChanged();
      }
    }
  }
}
