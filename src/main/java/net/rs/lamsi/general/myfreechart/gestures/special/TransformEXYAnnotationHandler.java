package net.rs.lamsi.general.myfreechart.gestures.special;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
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

public class TransformEXYAnnotationHandler extends ChartGestureDragDiffHandler
    implements Consumer<ChartGestureDragDiffEvent> {

  public enum Transform {
    TRANSLATE, SCALE, ROTATE;
  }

  private ChartPanel chart;
  private Transform trans;

  public TransformEXYAnnotationHandler(Transform transf, ChartPanel chart, Entity e, Button b,
      Key k) {
    super(e, b, k);
    this.chart = chart;
    setDragDiffHandler(this);
    this.trans = transf;
  }

  public TransformEXYAnnotationHandler(Transform transf, ChartPanel chart, Entity e, Button b,
      Key[] k) {
    super(e, b, k);
    this.chart = chart;
    setDragDiffHandler(this);
    this.trans = transf;
  }

  @Override
  public void accept(ChartGestureDragDiffEvent drag) {
    ChartEntity ce = drag.getFirstEvent().getEntity();
    if (ce != null) {
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

      if (ce instanceof EXYAnnotationEntity
          && ((EXYAnnotationEntity) ce).getAnnotation() instanceof EXYShapeAnnotation) {
        EXYShapeAnnotation ann = (EXYShapeAnnotation) ((EXYAnnotationEntity) ce).getAnnotation();
        // setup affine transform
        AffineTransform at = getAffineTransform(drag, ann.getShape().getBounds2D(), x, y);
        // transform
        ann.transform(at);
        // fire change event
        drag.getChartPanel().getChart().fireChartChanged();
      }
    }
  }

  protected AffineTransform getAffineTransform(ChartGestureDragDiffEvent drag, Rectangle2D bounds,
      double dx, double dy) {
    Point2D p = ChartLogics.mouseXYToPlotXY(drag.getChartPanel(), drag.getLatestEvent().getX(),
        drag.getLatestEvent().getY());
    double x = p.getX();
    double y = p.getY();
    double cx = bounds.getCenterX();
    double cy = bounds.getCenterY();
    // angle
    double angle = Math.atan(Math.toRadians(2));

    switch (trans) {
      case TRANSLATE:
        return AffineTransform.getTranslateInstance(dx, dy);
      case SCALE:
        double sx = Math.abs(cx - x) * 2 / bounds.getWidth();
        double sy = Math.abs(cy - y) * 2 / bounds.getHeight();
        AffineTransform at = new AffineTransform();
        at.translate(cx, cy);
        at.scale(sx, sy);
        at.translate(-cx, -cy);
        return at;
      case ROTATE:
        return AffineTransform.getRotateInstance(angle, cx, cy);
    }
    return new AffineTransform();
  }

}
