package net.rs.lamsi.general.myfreechart.plot;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Predicate;
import org.jfree.chart.ChartHints;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.JFreeChartEntity;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.title.Title;
import org.jfree.chart.ui.Align;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JFreeSquaredChart extends JFreeChart {
  private static final long serialVersionUID = 1L;
  private final Logger logger = LoggerFactory.getLogger(getClass());
  private static final int MAX_ITERATIONS = 10;
  // is only reset after finished paint
  private int iteration = 0;
  private Rectangle2D origChartArea;
  private Rectangle2D size;
  private Predicate<Rectangle2D> sizeChangedListener;


  public JFreeSquaredChart(String title, XYSquaredPlot plot) {
    super(title, plot);
  }

  public void setScaleMode(XYSquaredPlot.Scale scale) {
    if (getXYPlot() instanceof XYSquaredPlot) {
      boolean repaint = ((XYSquaredPlot) getXYPlot()).setScaleMode(scale);
      if (repaint)
        fireChartChanged();
    }
  }

  /**
   * Listen for size changes e.g. to resize ChartPanel/ChartView
   * 
   * @param sizeChangedListener if this predicate returns false the iteration counter is reset. True
   *        for keeping the interation counter. Could be false if the whole charpanel/chartViewer
   *        size was changed to restart the process
   */
  public void addSizeChangedListener(Predicate<Rectangle2D> sizeChangedListener) {
    this.sizeChangedListener = sizeChangedListener;
  }

  /**
   * Draws the chart on a Java 2D graphics device (such as the screen or a printer).
   * <P>
   * This method is the focus of the entire JFreeChart library.
   *
   * @param g2 the graphics device.
   * @param chartArea the area within which the chart should be drawn.
   * @param anchor the anchor point (in Java2D space) for the chart ({@code null} permitted).
   * @param info records info about the drawing (null means collect no info).
   */
  public void draw(Graphics2D g2, Rectangle2D chartArea, Point2D anchor, ChartRenderingInfo info) {
    if (iteration == 0)
      origChartArea = (Rectangle2D) chartArea.clone();

    notifyListeners(new ChartProgressEvent(this, this, ChartProgressEvent.DRAWING_STARTED, 0));

    if (this.getElementHinting()) {
      Map m = new HashMap<String, String>();
      if (this.getID() != null) {
        m.put("id", this.getID());
      }
      m.put("ref", "JFREECHART_TOP_LEVEL");
      g2.setRenderingHint(ChartHints.KEY_BEGIN_ELEMENT, m);
    }

    EntityCollection entities = null;
    // record the chart area, if info is requested...
    if (info != null) {
      info.clear();
      info.setChartArea(chartArea);
      entities = info.getEntityCollection();
    }
    if (entities != null) {
      entities.add(new JFreeChartEntity((Rectangle2D) chartArea.clone(), this));
    }

    // ensure no drawing occurs outside chart area...
    Shape savedClip = g2.getClip();
    g2.clip(chartArea);

    g2.addRenderingHints(this.getRenderingHints());

    // draw the chart background...
    if (this.getBackgroundPaint() != null) {
      g2.setPaint(this.getBackgroundPaint());
      g2.fill(chartArea);
    }

    if (this.getBackgroundImage() != null) {
      Composite originalComposite = g2.getComposite();
      g2.setComposite(
          AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.getBackgroundImageAlpha()));
      Rectangle2D dest = new Rectangle2D.Double(0.0, 0.0, this.getBackgroundImage().getWidth(null),
          this.getBackgroundImage().getHeight(null));
      Align.align(dest, chartArea, this.getBackgroundImageAlignment());
      g2.drawImage(this.getBackgroundImage(), (int) dest.getX(), (int) dest.getY(),
          (int) dest.getWidth(), (int) dest.getHeight(), null);
      g2.setComposite(originalComposite);
    }

    if (isBorderVisible()) {
      Paint paint = getBorderPaint();
      Stroke stroke = getBorderStroke();
      if (paint != null && stroke != null) {
        Rectangle2D borderArea = new Rectangle2D.Double(chartArea.getX(), chartArea.getY(),
            chartArea.getWidth() - 1.0, chartArea.getHeight() - 1.0);
        g2.setPaint(paint);
        g2.setStroke(stroke);
        g2.draw(borderArea);
      }
    }

    // draw the title and subtitles...
    Rectangle2D nonTitleArea = new Rectangle2D.Double();
    nonTitleArea.setRect(chartArea);
    this.getPadding().trim(nonTitleArea);
    TextTitle title = getTitle();
    if (title != null && title.isVisible()) {
      EntityCollection e = drawTitle(title, g2, nonTitleArea, (entities != null));
      if (e != null && entities != null) {
        entities.addAll(e);
      }
    }

    Iterator iterator = getSubtitles().iterator();
    while (iterator.hasNext()) {
      Title currentTitle = (Title) iterator.next();
      if (currentTitle.isVisible()) {
        EntityCollection e = drawTitle(currentTitle, g2, nonTitleArea, (entities != null));
        if (e != null && entities != null) {
          entities.addAll(e);
        }
      }
    }

    Rectangle2D plotArea = nonTitleArea;

    // draw the plot (axes and data visualisation)
    PlotRenderingInfo plotInfo = null;
    if (info != null) {
      plotInfo = info.getPlotInfo();
    }
    XYSquaredPlot plot = (XYSquaredPlot) this.getPlot();
    iteration++; // first iteration in plot is 1
    if (iteration > MAX_ITERATIONS)
      logger.debug("MAX IT 10");
    else
      logger.debug("IT {}", iteration);

    Rectangle2D newChartArea = plot.calc(g2, chartArea, plotArea, plotInfo, iteration);
    // too small
    if (newChartArea.equals(chartArea) || iteration > MAX_ITERATIONS) {
      // final paint
      logger.debug("Final PAINT at iteration: {}", iteration);
      iteration = 0;
      this.getPlot().draw(g2, plotArea, anchor, null, plotInfo);
    } else if (plotArea.getWidth() < 30 || plotArea.getHeight() < 30) {
      iteration = Integer.MAX_VALUE - 1;
      // draw in full size
      g2.setClip(savedClip);
      draw(g2, origChartArea, anchor, info);
    } else {
      // chart area has changed repaint!
      Composite tmp = g2.getComposite();
      g2.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
      g2.fill(chartArea);
      g2.setComposite(tmp);

      double x = chartArea.getX() + (chartArea.getWidth() - newChartArea.getWidth()) / 2.0;
      double y = chartArea.getY() + (chartArea.getHeight() - newChartArea.getHeight()) / 2.0;
      newChartArea.setRect(x, y, newChartArea.getWidth(), newChartArea.getHeight());

      // too small
      if (newChartArea.getWidth() < 50 || newChartArea.getHeight() < 10) {
        iteration = Integer.MAX_VALUE - 1;
        // draw in full size
        g2.setClip(savedClip);
        draw(g2, origChartArea, anchor, info);
      } else {
        g2.setClip(savedClip);
        // else try to draw chart with new size
        draw(g2, newChartArea, anchor, info);
      }
      return;
    }

    g2.setClip(savedClip);
    if (this.getElementHinting()) {
      g2.setRenderingHint(ChartHints.KEY_END_ELEMENT, Boolean.TRUE);
    }

    notifyListeners(new ChartProgressEvent(this, this, ChartProgressEvent.DRAWING_FINISHED, 100));
  }

}
