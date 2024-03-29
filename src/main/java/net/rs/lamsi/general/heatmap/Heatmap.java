package net.rs.lamsi.general.heatmap;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.data.xy.XYZDataset;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.myfreechart.general.annotations.EXYShapeAnnotation;
import net.rs.lamsi.general.myfreechart.plots.image2d.EImage2DChartPanel;
import net.rs.lamsi.general.myfreechart.plots.image2d.annot.ImageTitle;
import net.rs.lamsi.general.myfreechart.plots.image2d.annot.ScaleInPlot;
import net.rs.lamsi.general.myfreechart.swing.EChartPanel;
import net.rs.lamsi.general.settings.image.selection.SettingsShapeSelection;
import net.rs.lamsi.multiimager.FrameModules.sub.dataoperations.ModuleSelectExcludeData;


public class Heatmap {
  // Variablen
  protected XYZDataset dataset;
  // Title

  // just a bunch of visual stuff
  private EChartPanel chartPanel;
  private PaintScale[] paintScale;
  private JFreeChart chart;
  private XYItemRenderer renderer;
  private XYPlot plot;
  private PaintScaleLegend legend;
  // the raw data and settings
  private Collectable2D image;
  private ScaleInPlot scaleInPlot;
  // stats
  private boolean isShowingSelectedExcludedRects = false, isShowingBlankMinMax = false;
  // list of annotations for later removing
  private ArrayList<EXYShapeAnnotation> annSelections;

  private ImageTitle shortTitle;

  // blank domain marker for lower and upper bound
  private ValueMarker lowerMarker, upperMarker;

  // Construct
  public Heatmap(XYZDataset dataset, int colorSteps, EImage2DChartPanel chartPanel,
      PaintScale paintScale, JFreeChart chart, XYPlot plot, PaintScaleLegend legend,
      Collectable2D image, XYItemRenderer renderer, ScaleInPlot scaleInPlot,
      ImageTitle shortTitle) {
    super();
    this.dataset = dataset;
    this.chartPanel = chartPanel;
    this.paintScale = new PaintScale[] {paintScale};
    this.chart = chart;
    this.plot = plot;
    this.legend = legend;
    this.setImage(image);
    this.renderer = renderer;
    this.scaleInPlot = scaleInPlot;
    this.shortTitle = shortTitle;
    //
    if (image != null && image.isImage2D())
      showBlankMinMax(
          ((Image2D) image).getSettings().getOperations().getBlankQuantifier().isShowInChart());
  }

  public Heatmap(XYZDataset dataset, EChartPanel chartPanel, PaintScale[] paintScale,
      JFreeChart chart, XYPlot plot,
      // PaintScaleLegend legend,
      Collectable2D image, XYItemRenderer renderer, ScaleInPlot scaleInPlot,
      ImageTitle shortTitle2) {
    super();
    this.dataset = dataset;
    this.chartPanel = chartPanel;
    this.paintScale = paintScale;
    this.chart = chart;
    this.plot = plot;
    // this.legend = legend;
    this.setImage(image);
    this.renderer = renderer;
    this.scaleInPlot = scaleInPlot;
    this.shortTitle = shortTitle2;
  }


  public XYZDataset getDataset() {
    return dataset;
  }

  public void setDataset(IXYZDataset dataset) {
    this.dataset = dataset;
  }

  public EChartPanel getChartPanel() {
    return chartPanel;
  }

  public void setChartPanel(EImage2DChartPanel chartPanel) {
    this.chartPanel = chartPanel;
  }

  public PaintScale getPaintScale(int i) {
    return paintScale[i];
  }

  public PaintScale[] getPaintScales() {
    return paintScale;
  }

  public void setPaintScales(PaintScale[] paintScale) {
    this.paintScale = paintScale;
  }

  public void setPaintScales(PaintScale paintScale, int i) {
    this.paintScale[i] = paintScale;
  }

  public JFreeChart getChart() {
    return chart;
  }

  public void setChart(JFreeChart chart) {
    this.chart = chart;
  }

  public XYPlot getPlot() {
    return plot;
  }

  public void setPlot(XYPlot plot) {
    this.plot = plot;
  }

  public PaintScaleLegend getLegend() {
    return legend;
  }

  public void setLegend(PaintScaleLegend legend) {
    this.legend = legend;
  }



  public void setImage(Collectable2D image) {
    this.image = image;
  }

  public Collectable2D getImage() {
    return image;
  }

  public boolean isImage2D() {
    return Image2D.class.isInstance(image);
  }

  public XYItemRenderer getRenderer() {
    return renderer;
  }

  /**
   * update the rects in the chartpanel
   */
  public void updateSelectedExcludedRects() {
    if (isImage2D()) { // TODO
      Image2D image = (Image2D) this.image;
      // remove all annotations
      if (annSelections != null)
        for (EXYShapeAnnotation a : annSelections)
          plot.removeAnnotation(a, false);
      // add them
      if (isShowingSelectedExcludedRects) {
        annSelections.clear();

        ArrayList<SettingsShapeSelection> selections =
            image.getSettings().getSettSelections().getSelections();
        if (selections != null) {
          for (Iterator iterator = selections.iterator(); iterator.hasNext();) {
            SettingsShapeSelection sel = (SettingsShapeSelection) iterator.next();
            EXYShapeAnnotation ann = sel.createXYShapeAnnotation();
            annSelections.add(ann);
            plot.addAnnotation(ann, false);
          }
        }
      } else {
        annSelections = null;
      }
      // fire change event
      chart.fireChartChanged();
    }
  }

  /**
   * Show the rects in the heatmap / chartpanel gets called by Action in
   * {@link ModuleSelectExcludeData}
   * 
   * @param show
   */
  public void showSelectedExcludedRects(boolean show) {
    isShowingSelectedExcludedRects = show;
    // init
    if (annSelections == null) {
      annSelections = new ArrayList<EXYShapeAnnotation>();
    }
    // update
    updateSelectedExcludedRects();
  }


  public void showBlankMinMax(boolean state) {
    isShowingBlankMinMax = state;
    // update
    updateShowBlankMinMax();
  }

  public void updateShowBlankMinMax() {
    if (isImage2D()) { // TODO
      Image2D image = (Image2D) this.image;
      // remove
      if (upperMarker != null)
        plot.removeDomainMarker(upperMarker);
      if (lowerMarker != null)
        plot.removeDomainMarker(lowerMarker);

      if (isShowingBlankMinMax) {
        // add
        double lower = image.getX(false, 0, image.getSettings().getOperations().getBlankQuantifier()
            .getQSameImage().getLowerBound());
        plot.addDomainMarker(
            lowerMarker = new ValueMarker(lower, Color.BLUE, new BasicStroke(1.5f)));

        if (image.getSettings().getOperations().getBlankQuantifier().getQSameImage().isUseEnd()) {
          double upper = image.getX(false, 0, image.getSettings().getOperations()
              .getBlankQuantifier().getQSameImage().getUpperBound());
          plot.addDomainMarker(
              upperMarker = new ValueMarker(upper, Color.BLUE, new BasicStroke(1.5f)));
        }
      }

      // fire change event
      chart.fireChartChanged();
    }
  }

  /**
   * renews the short title
   * 
   * @param x
   * @param y
   * @param visible
   */
  public void setShortTitle(float x, float y, boolean visible) {
    plot.removeAnnotation(shortTitle.getAnnotation());
    shortTitle.setPosition(x, y);
    shortTitle.setVisible(visible);
    plot.addAnnotation(shortTitle.getAnnotation());
  }



  public ScaleInPlot getScaleInPlot() {
    return scaleInPlot;
  }



  public void setScaleInPlot(ScaleInPlot scaleInPlot) {
    setScaleInPlot(scaleInPlot);
  }

  public ImageTitle getShortTitle() {
    return shortTitle;
  }

  public void setShortTitle(ImageTitle shortTitle) {
    this.shortTitle = shortTitle;
  }
}
