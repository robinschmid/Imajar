package net.rs.lamsi.general.myfreechart.plots.image2d.merge;

import java.util.function.Consumer;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import net.rs.lamsi.general.datamodel.image.ImageMerge;
import net.rs.lamsi.general.myfreechart.ChartLogics;
import net.rs.lamsi.general.myfreechart.gestures.ChartGesture.Button;
import net.rs.lamsi.general.myfreechart.gestures.ChartGesture.Entity;
import net.rs.lamsi.general.myfreechart.gestures.ChartGesture.Key;
import net.rs.lamsi.general.myfreechart.gestures.ChartGestureDragDiffEvent;
import net.rs.lamsi.general.myfreechart.gestures.ChartGestureDragDiffHandler;
import net.rs.lamsi.general.settings.image.merge.SettingsSingleMerge;

public class MergeDragDiffHandler extends ChartGestureDragDiffHandler
    implements Consumer<ChartGestureDragDiffEvent> {

  private ChartPanel chart;

  public MergeDragDiffHandler(ImageMerge img, ChartPanel chart, Entity e, Button b, Key k) {
    super(e, b, k);
    this.chart = chart;
    setDragDiffHandler(this);

  }

  @Override
  public void accept(ChartGestureDragDiffEvent drag) {
    ChartEntity ce = drag.getFirstEvent().getEntity();
    if (ce != null && ce instanceof ImageMergeItem) {
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
      // find image to shift
      SettingsSingleMerge s = ((ImageMergeItem) ce).getSettings();
      // shift image
      s.translate(x, y);
      chart.getChart().fireChartChanged();
      chart.repaint();
    }
  }

}
