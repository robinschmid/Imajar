package net.rs.lamsi.general.myfreechart.plots.image2d.merge;

import java.util.function.Consumer;
import org.jfree.chart.ChartPanel;
import net.rs.lamsi.general.datamodel.image.ImageMerge;
import net.rs.lamsi.general.myfreechart.gestures.ChartGesture.Button;
import net.rs.lamsi.general.myfreechart.gestures.ChartGesture.Entity;
import net.rs.lamsi.general.myfreechart.gestures.ChartGesture.Key;
import net.rs.lamsi.general.myfreechart.gestures.ChartGestureDragDiffEvent;
import net.rs.lamsi.general.myfreechart.gestures.ChartGestureDragDiffHandler;

public class MergeDragDiffHandler extends ChartGestureDragDiffHandler
    implements Consumer<ChartGestureDragDiffEvent> {

  private ImageMerge img;
  private ChartPanel chart;

  public MergeDragDiffHandler(ImageMerge img, ChartPanel chart, Entity e, Button b, Key k) {
    super(e, b, k);
    this.img = img;
    this.chart = chart;
    setDragDiffHandler(this);

  }

  @Override
  public void accept(ChartGestureDragDiffEvent e) {
    // find image to shift


    // shift image

  }

}
