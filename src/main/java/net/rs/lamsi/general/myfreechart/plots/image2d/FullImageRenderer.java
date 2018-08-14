package net.rs.lamsi.general.myfreechart.plots.image2d;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.util.Args;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetUtils;
import org.jfree.data.xy.XYDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.rs.lamsi.general.datamodel.image.interf.DataCollectable2D;
import net.rs.lamsi.general.myfreechart.plots.image2d.datasets.DataCollectable2DDataset;
import net.rs.lamsi.general.myfreechart.plots.image2d.datasets.DataCollectable2DListDataset;
import net.rs.lamsi.general.myfreechart.plots.image2d.merge.ImageMergeItem;
import net.rs.lamsi.general.settings.image.merge.SettingsSingleMerge;
import net.rs.lamsi.general.settings.image.visualisation.SettingsAlphaMap;
import net.rs.lamsi.general.settings.image.visualisation.SettingsAlphaMap.State;
import net.rs.lamsi.general.settings.image.visualisation.SettingsBackgroundImg;
import net.rs.lamsi.utils.useful.graphics2d.blending.BlendComposite;

public class FullImageRenderer extends AbstractXYItemRenderer
    implements XYItemRenderer, Cloneable, PublicCloneable, Serializable {
  private final Logger logger = LoggerFactory.getLogger(getClass());


  //
  protected SettingsAlphaMap sett;

  protected DataCollectable2D img;
  private boolean useImageAsXYItem = false;


  /**
   * The anchor point used to align each block to its (x, y) location. The default value is
   * {@code RectangleAnchor.CENTER}.
   */
  private RectangleAnchor blockAnchor = RectangleAnchor.CENTER;

  /** Temporary storage for the x-offset used to align the block anchor. */
  private double xOffset;

  /** Temporary storage for the y-offset used to align the block anchor. */
  private double yOffset;

  /** The paint scale. */
  private PaintScale[] paintScale;

  // only for range detection
  private float avgBlockWidth = 0;
  private float avgBlockHeight = 0;

  private int c = 0;

  /**
   * Creates a new {@code XYBlockRenderer} instance with default attributes.
   * 
   * @param img2
   */
  public FullImageRenderer() {
    this.paintScale = new PaintScale[] {new LookupPaintScale()};
  }

  /**
   * Returns the anchor point used to align a block at its (x, y) location. The default values is
   * {@link RectangleAnchor#CENTER}.
   *
   * @return The anchor point (never {@code null}).
   *
   * @see #setBlockAnchor(RectangleAnchor)
   */
  public RectangleAnchor getBlockAnchor() {
    return this.blockAnchor;
  }

  /**
   * Sets the anchor point used to align a block at its (x, y) location and sends a
   * {@link RendererChangeEvent} to all registered listeners.
   *
   * @param anchor the anchor.
   *
   * @see #getBlockAnchor()
   */
  public void setBlockAnchor(RectangleAnchor anchor) {
    Args.nullNotPermitted(anchor, "anchor");
    if (this.blockAnchor.equals(anchor)) {
      return; // no change
    }
    this.blockAnchor = anchor;
    updateOffsets();
    fireChangeEvent();
  }

  /**
   * Returns the paint scale used by the renderer.
   *
   * @return The paint scale (never {@code null}).
   *
   * @see #setPaintScale(PaintScale)
   * @since 1.0.4
   */
  public PaintScale getPaintScale(int series) {
    if (paintScale.length == 1)
      return paintScale[0];
    else
      return this.paintScale[series];
  }

  /**
   * Sets the paint scale used by the renderer and sends a {@link RendererChangeEvent} to all
   * registered listeners.
   *
   * @param scale the scale ({@code null} not permitted).
   *
   * @see #getPaintScale()
   * @since 1.0.4
   */
  public void setPaintScale(PaintScale[] scale) {
    Args.nullNotPermitted(scale, "scale");
    this.paintScale = scale;
    fireChangeEvent();
  }

  /**
   * Sets the paint scale used by the renderer and sends a {@link RendererChangeEvent} to all
   * registered listeners.
   *
   * @param scale the scale ({@code null} not permitted).
   *
   * @see #getPaintScale()
   * @since 1.0.4
   */
  public void setPaintScale(PaintScale scale) {
    Args.nullNotPermitted(scale, "scale");
    this.paintScale = new PaintScale[] {scale};
    fireChangeEvent();
  }

  /**
   * Updates the offsets to take into account the block width, height and anchor.
   */
  private void updateOffsets() {
    // always bottom left
    xOffset = 0.0;
    yOffset = 0.0;
  }

  /**
   * Returns the lower and upper bounds (range) of the x-values in the specified dataset.
   *
   * @param dataset the dataset ({@code null} permitted).
   *
   * @return The range ({@code null} if the dataset is {@code null} or empty).
   *
   * @see #findRangeBounds(XYDataset)
   */
  @Override
  public Range findDomainBounds(XYDataset dataset) {
    if (dataset == null) {
      return null;
    }
    Range r = DatasetUtils.findDomainBounds(dataset, false);
    if (r == null) {
      return null;
    }
    return new Range(r.getLowerBound() + this.xOffset, r.getUpperBound() + this.xOffset);
  }

  /**
   * Returns the range of values the renderer requires to display all the items from the specified
   * dataset.
   *
   * @param dataset the dataset ({@code null} permitted).
   *
   * @return The range ({@code null} if the dataset is {@code null} or empty).
   *
   * @see #findDomainBounds(XYDataset)
   */
  @Override
  public Range findRangeBounds(XYDataset dataset) {
    if (dataset != null) {
      Range r = DatasetUtils.findRangeBounds(dataset, false);
      if (r == null) {
        return null;
      } else {
        return new Range(r.getLowerBound() + this.yOffset, r.getUpperBound() + this.yOffset);
      }
    } else {
      return null;
    }
  }

  /**
   * Tests this {@code XYBlockRenderer} for equality with an arbitrary object. This method returns
   * {@code true} if and only if:
   * <ul>
   * <li>{@code obj} is an instance of {@code XYBlockRenderer} (not {@code null});</li>
   * <li>{@code obj} has the same field values as this {@code XYBlockRenderer};</li>
   * </ul>
   *
   * @param obj the object ({@code null} permitted).
   *
   * @return A boolean.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof FullImageRenderer)) {
      return false;
    }
    FullImageRenderer that = (FullImageRenderer) obj;
    if (!this.blockAnchor.equals(that.blockAnchor)) {
      return false;
    }
    if (!this.paintScale.equals(that.paintScale)) {
      return false;
    }
    return super.equals(obj);
  }

  /**
   * Returns a clone of this renderer.
   *
   * @return A clone of this renderer.
   *
   * @throws CloneNotSupportedException if there is a problem creating the clone.
   */
  @Override
  public Object clone() throws CloneNotSupportedException {
    FullImageRenderer clone = (FullImageRenderer) super.clone();
    clone.paintScale = paintScale.clone();
    clone.setImage(img);
    return clone;
  }

  /**
   * Draws the block representing the specified item.
   *
   * @param g2 the graphics device.
   * @param state the state.
   * @param dataArea the data area.
   * @param info the plot rendering info.
   * @param plot the plot.
   * @param domainAxis the x-axis.
   * @param rangeAxis the y-axis.
   * @param dataset the dataset.
   * @param series the series index.
   * @param item the item index.
   * @param crosshairState the crosshair state.
   * @param pass the pass index.
   */
  @Override
  public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea,
      PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis,
      XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
    DataCollectable2DDataset data = null;
    SettingsSingleMerge settMerge = null;
    if (dataset instanceof DataCollectable2DDataset) {
      logger.debug("draw image of DataCollectable2DDataset");
      data = (DataCollectable2DDataset) dataset;
      DataCollectable2D img = data.getImage();
      setImage(img);
    } else if (dataset instanceof DataCollectable2DListDataset) {
      logger.debug("draw image #{} of ImageMerge (ListDataset)", series);
      DataCollectable2DListDataset list = (DataCollectable2DListDataset) dataset;
      data = list.getDataset(series);
      DataCollectable2D img = data.getImage();
      setImage(img);

      // affine transform for this image
      settMerge = list.getSettings(img.getImageGroup().getName());
    }

    if (data != null && (settMerge == null || settMerge.isVisible())) {
      // height
      double yy0 = rangeAxis.valueToJava2D(0, dataArea, plot.getRangeAxisEdge());
      double yy1 = rangeAxis.valueToJava2D(avgBlockHeight, dataArea, plot.getRangeAxisEdge());
      double bh = Math.abs(yy1 - yy0);

      // width
      double xx0 = domainAxis.valueToJava2D(0, dataArea, plot.getDomainAxisEdge());
      double xx1 = domainAxis.valueToJava2D(avgBlockWidth, dataArea, plot.getDomainAxisEdge());
      double bw = Math.abs(xx1 - xx0);

      // all same dp width?
      if (img.hasOneDPWidth()) {
        logger.info("All data points: Equal widths");
        // draw with one block width and height
        drawImage(g2, state, dataArea, plot, domainAxis, rangeAxis, crosshairState, data, series,
            bw, bh, settMerge);
      } else {
        logger.info("All data points: Different widths");
        // draw with one block height but different widths
        drawImageFixedBlockHeight(g2, state, dataArea, plot, domainAxis, rangeAxis, crosshairState,
            data, series, bw, bh, settMerge);
      }
    } else {
      logger.error("no data for series {} in dataset", series);
    }
  }


  /**
   * Draw image with avgDPWidth and Height
   * 
   * @param g2
   * @param state
   * @param dataArea
   * @param plot
   * @param domainAxis
   * @param rangeAxis
   * @param crosshairState
   * @param data
   * @param bw valueToJava2D BlockWidth
   * @param bh valueToJava2D BlockHeight
   */
  private void drawImage(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea,
      XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, CrosshairState crosshairState,
      DataCollectable2DDataset data, int series, double bw, double bh,
      SettingsSingleMerge settMerge) {
    float width = img.getAvgBlockWidth(true);
    float height = img.getAvgBlockHeight(true);
    Range domain = domainAxis.getRange();
    Range range = rangeAxis.getRange();

    BlendComposite blend = BlendComposite.Normal;

    // Blend from background image settings
    SettingsBackgroundImg sbg =
        (SettingsBackgroundImg) img.getSettingsByClass(SettingsBackgroundImg.class);
    if (sbg != null && sbg.isVisible())
      blend = BlendComposite.getInstance(sbg.getBlend());

    c = 0;
    int cAll = 0;

    g2.setComposite(blend);
    // draw full image
    for (int l = 0; l < data.getLineCount(); l++) {
      lastx1 = -1;
      for (int dp = 0; dp < data.getLineLength(); dp++) {
        // only if in map or if there is no map
        if (sett != null) {
          State dpstate = sett.getMapValue(l, dp);
          if (sett.isActive() && dpstate.isFalse()) {
            // do not paint if false
            continue;
          } else if (sett.isDrawMarks() && dpstate.isMarked() && sett.getAlpha() < 1.f) {
            // paint with transparency if marked by roi
            // skip paint if alpha = 0
            if (sett.getAlpha() < 0.0001)
              continue;
            // set transparency if used
            g2.setComposite(blend.derive(sett.getAlpha()));
          } else {
            // reset
            g2.setComposite(blend);
          }
        }
        // not in map so paint... if
        // paint block
        // try to get intensity - NaN == no data point
        double z = data.getZ(false, l, dp);
        if (!Double.isNaN(z)) {
          double x = data.getX(false, l, dp);
          double y = data.getY(false, l, dp);

          // translate by settings
          if (settMerge != null) {
            x += settMerge.getDX();
            y += settMerge.getDY();
            g2.setComposite(settMerge.getBlendComposite());
          }

          // check whether block is in range of axes
          if (inRanges(x, y, x + width, y + height, domain, range)) {

            Paint p = this.getPaintScale(series).getPaint(z);
            double xx0 =
                domainAxis.valueToJava2D(x + this.xOffset, dataArea, plot.getDomainAxisEdge());
            double yy0 =
                rangeAxis.valueToJava2D(y + this.yOffset, dataArea, plot.getRangeAxisEdge());

            // paint
            drawBlockItem(g2, state, dataArea, plot, domainAxis, rangeAxis, data, crosshairState,
                xx0, xx0 + bw, yy0, yy0 - bh, p, settMerge);
            cAll++;
          }
        }
        // reset
        // g2.setComposite(BlendComposite.Normal);
      }
    }
    logger.debug("drawn dp={} of all {} data points", c, cAll);
  }


  /**
   * Checks if at least one coordinate of x and y are in range and domain
   * 
   * @param x0
   * @param y0
   * @param x1
   * @param y1
   * @param domain
   * @param range
   * @return
   */
  private boolean inRanges(double x0, double y0, double x1, double y1, Range domain, Range range) {
    return (domain.contains(x0) || domain.contains(x1))
        && (range.contains(y0) || range.contains(y1));
  }

  double lastx1 = -1;

  /**
   * Draw image with a different width for each dp avgDPHeight is used
   * 
   * @param g2
   * @param state
   * @param dataArea
   * @param plot
   * @param domainAxis
   * @param rangeAxis
   * @param crosshairState
   * @param data
   * @param bh valueToJava2D BlockHeight
   */
  private void drawImageFixedBlockHeight(Graphics2D g2, XYItemRendererState state,
      Rectangle2D dataArea, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis,
      CrosshairState crosshairState, DataCollectable2DDataset data, int series, double bw,
      double bh, SettingsSingleMerge settMerge) {
    // draw full image
    Paint lastPaint = null;
    // last
    double lxx0 = -1;
    double lyy0 = -1;
    // current
    Paint currentPaint = null;
    double cxx0 = -1;
    double cyy0 = -1;
    boolean currentNoDP = false;

    BlendComposite blend = BlendComposite.Normal;

    // Blend from background image settings
    SettingsBackgroundImg sbg =
        (SettingsBackgroundImg) img.getSettingsByClass(SettingsBackgroundImg.class);
    if (sbg != null && sbg.isVisible())
      blend = BlendComposite.getInstance(sbg.getBlend());

    g2.setComposite(blend);
    for (int l = 0; l < data.getLineCount(); l++) {
      lastx1 = -1;
      // reset last
      lxx0 = -1;
      lyy0 = -1;
      lastPaint = null;
      for (int dp = 0; dp < data.getLineLength(); dp++) {
        // reset current
        currentNoDP = false;
        currentPaint = null;
        cxx0 = -1;
        cyy0 = -1;

        // only if in map or if there is no map
        if (sett != null) {
          State dpstate = sett.getMapValue(l, dp);
          if (isMapActive() && dpstate.isFalse()) {
            // do not paint this block if false
            currentNoDP = true;
          } else if (dpstate.isMarked() && sett.getAlpha() < 1.f) {
            // skip paint if alpha = 0
            if (sett.getAlpha() < 0.0001)
              currentNoDP = true;
            else {
              // set transparency if used
              g2.setComposite(blend.derive(sett.getAlpha()));
            }
          } else {
            // reset
            g2.setComposite(blend);
          }
        }
        // current is a dp
        if (!currentNoDP || lastPaint != null) {
          // not in map so paint... if
          // paint block
          // try to get intensity - NaN == no data point
          double z = data.getZ(false, l, dp);
          if (!Double.isNaN(z)) {
            double x = data.getX(false, l, dp);
            double y = data.getY(false, l, dp);

            // translate by settings
            if (settMerge != null) {
              x += settMerge.getDX();
              y += settMerge.getDY();
              g2.setComposite(settMerge.getBlendComposite());
            }

            currentPaint = this.getPaintScale(series).getPaint(z);
            cxx0 = domainAxis.valueToJava2D(x + this.xOffset, dataArea, plot.getDomainAxisEdge());
            cyy0 = rangeAxis.valueToJava2D(y + this.yOffset, dataArea, plot.getRangeAxisEdge());
          } else
            currentNoDP = true;
        }

        // always paint the previous dp
        if (lastPaint != null) {
          // paint with bw if current dp was
          double xx1 = currentPaint == null ? lxx0 + bw : cxx0;

          // paint previous dp
          drawBlockItem(g2, state, dataArea, plot, domainAxis, rangeAxis, data, crosshairState,
              lxx0, xx1, lyy0, lyy0 - bh, lastPaint, settMerge);
        }

        // convert current to last
        lxx0 = cxx0;
        lyy0 = cyy0;
        lastPaint = currentNoDP ? null : currentPaint;
      }
      // special case for last dp paint with bw and bh
      // paint last dp
      if (lastPaint != null) {
        // paint dp
        drawBlockItem(g2, state, dataArea, plot, domainAxis, rangeAxis, data, crosshairState, lxx0,
            lxx0 + bw, lyy0, lyy0 - bh, lastPaint, settMerge);
      }
      // reset
      g2.setComposite(BlendComposite.Normal);
    }

  }


  protected void drawBlockItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea,
      XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, DataCollectable2DDataset data,
      CrosshairState crosshairState, double xx0, double xx1, double yy0, double yy1, Paint p,
      SettingsSingleMerge settMerge) {
    Rectangle2D block;

    // round to full pixel
    xx0 = Math.round(xx0);
    xx1 = Math.round(xx1);
    yy0 = Math.round(yy0);
    yy1 = Math.round(yy1);

    double w, h;

    PlotOrientation orientation = plot.getOrientation();
    if (orientation.equals(PlotOrientation.HORIZONTAL)) {
      w = Math.abs(yy1 - yy0);
      h = Math.abs(xx0 - xx1);
      block = new Rectangle2D.Double(Math.min(yy0, yy1), Math.min(xx0, xx1), w, h);
    } else {
      h = Math.abs(yy1 - yy0);
      w = Math.abs(xx0 - xx1);
      block = new Rectangle2D.Double(Math.min(xx0, xx1), Math.min(yy0, yy1), w, h);
    }

    // blocks should always have the same edge if very close
    // enlarge width by 1 if rounding error was detected
    // if dist 1 px
    double dist = Math.abs(lastx1 - xx0);
    if (lastx1 != -1 && dist > 0.5 && dist < 1.5) {
      block = new Rectangle2D.Double(Math.min(xx0, xx1) - 1, Math.min(yy0, yy1), w + 1, h);
    }

    lastx1 = xx1;
    // paint
    drawBlockItem(g2, state, dataArea, plot, domainAxis, rangeAxis, data, crosshairState, block, p,
        settMerge);
  }

  protected void drawBlockItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea,
      XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, DataCollectable2DDataset data,
      CrosshairState crosshairState, Rectangle2D block, Paint p, SettingsSingleMerge settMerge) {

    // do only paint if inside rect
    if (block.getWidth() > 0 && block.getHeight() > 0 && dataArea.intersects(block)) {
      g2.setPaint(p);

      g2.fill(block);
      // count
      c++;

      // g2.setStroke(new BasicStroke(1.0f));
      // g2.draw(block);

      // int datasetIndex = plot.indexOf(data);
      // double transX = domainAxis.valueToJava2D(x, dataArea, plot.getDomainAxisEdge());
      // double transY = rangeAxis.valueToJava2D(y, dataArea, plot.getRangeAxisEdge());
      // updateCrosshairValues(crosshairState, x, y, datasetIndex, transX, transY, orientation);

      if (!useImageAsXYItem) {
        EntityCollection entities = state.getEntityCollection();
        if (entities != null) {
          Rectangle2D intersect = block.createIntersection(dataArea);
          addEntity(entities, intersect, data, 0, 0, intersect.getCenterX(), intersect.getCenterY(),
              settMerge);
        }
      }
    }
  }

  protected void drawBlockItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea,
      XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, DataCollectable2DDataset data,
      int series, int line, int dp, CrosshairState crosshairState, SettingsSingleMerge settMerge) {

    // try to get intensity - NaN == no data point
    double z = data.getZ(false, line, dp);
    if (!Double.isNaN(z)) {

      double x = data.getX(false, line, dp);
      double y = data.getY(false, line, dp);

      Paint p = this.getPaintScale(series).getPaint(z);
      double xx0 = domainAxis.valueToJava2D(x + this.xOffset, dataArea, plot.getDomainAxisEdge());
      double yy0 = rangeAxis.valueToJava2D(y + this.yOffset, dataArea, plot.getRangeAxisEdge());
      double xx1 = domainAxis.valueToJava2D(x + avgBlockWidth + this.xOffset, dataArea,
          plot.getDomainAxisEdge());
      double yy1 = rangeAxis.valueToJava2D(y + avgBlockHeight + this.yOffset, dataArea,
          plot.getRangeAxisEdge());
      Rectangle2D block;

      // round to full pixel
      xx0 = Math.round(xx0);
      xx1 = Math.round(xx1);
      yy0 = Math.round(yy0);
      yy1 = Math.round(yy1);

      double w, h;

      PlotOrientation orientation = plot.getOrientation();
      if (orientation.equals(PlotOrientation.HORIZONTAL)) {
        w = Math.abs(yy1 - yy0);
        h = Math.abs(xx0 - xx1);
        block = new Rectangle2D.Double(Math.min(yy0, yy1), Math.min(xx0, xx1), w, h);
      } else {
        h = Math.abs(yy1 - yy0);
        w = Math.abs(xx0 - xx1);
        block = new Rectangle2D.Double(Math.min(xx0, xx1), Math.min(yy0, yy1), w, h);
      }

      // blocks should always have the same edge if very close
      // enlarge width by 1 if rounding error was detected
      // if dist 1 px
      double dist = Math.abs(lastx1 - xx0);
      if (lastx1 != -1 && dist > 0.5 && dist < 1.5)
        w++;

      lastx1 = xx1;


      // // PlotOrientation orientation = plot.getOrientation();
      // if (orientation.equals(PlotOrientation.HORIZONTAL)) {
      // block = new Rectangle2D.Double(Math.min(yy0, yy1), Math.min(xx0, xx1), Math.abs(yy1 - yy0),
      // Math.abs(xx0 - xx1));
      // } else {
      // block = new Rectangle2D.Double(Math.min(xx0, xx1), Math.min(yy0, yy1), Math.abs(xx1 - xx0),
      // Math.abs(yy1 - yy0));
      // }

      // do only paint if inside rect
      if (w > 0 && h > 0 && dataArea.intersects(block)) {
        g2.setPaint(p);

        g2.fill(block);

        // g2.setStroke(new BasicStroke(1.0f));
        // g2.draw(block);

        int datasetIndex = plot.indexOf(data);
        double transX = domainAxis.valueToJava2D(x, dataArea, plot.getDomainAxisEdge());
        double transY = rangeAxis.valueToJava2D(y, dataArea, plot.getRangeAxisEdge());
        updateCrosshairValues(crosshairState, x, y, datasetIndex, transX, transY, orientation);

        if (!useImageAsXYItem) {
          EntityCollection entities = state.getEntityCollection();
          if (entities != null) {
            Rectangle2D intersect = block.createIntersection(dataArea);
            addEntity(entities, intersect, data, 0, 0, intersect.getCenterX(),
                intersect.getCenterY(), settMerge);
          }
        }
      }
    }
  }

  /**
   * Adds an entity to the collection. Note the the {@code entityX} and {@code entityY} coordinates
   * are in Java2D space, should already be adjusted for the plot orientation, and will only be used
   * if {@code hotspot} is {@code null}.
   *
   * @param entities the entity collection being populated.
   * @param hotspot the entity area (if {@code null} a default will be used).
   * @param dataset the dataset.
   * @param series the series.
   * @param item the item.
   * @param entityX the entity x-coordinate (in Java2D space, only used if {@code hotspot} is
   *        {@code null}).
   * @param entityY the entity y-coordinate (in Java2D space, only used if {@code hotspot} is
   *        {@code null}).
   */
  protected void addEntity(EntityCollection entities, Shape hotspot, XYDataset dataset, int series,
      int item, double entityX, double entityY, SettingsSingleMerge settMerge) {

    if (!getItemCreateEntity(series, item)) {
      return;
    }

    // if not hotspot is provided, we create a default based on the
    // provided data coordinates (which are already in Java2D space)
    if (hotspot == null) {
      double r = getDefaultEntityRadius();
      double w = r * 2;
      hotspot = new Ellipse2D.Double(entityX - r, entityY - r, w, w);
    }
    String tip = null;
    XYToolTipGenerator generator = getToolTipGenerator(series, item);
    if (generator != null) {
      tip = generator.generateToolTip(dataset, series, item);
    }
    String url = null;
    if (getURLGenerator() != null) {
      url = getURLGenerator().generateURL(dataset, series, item);
    }
    XYItemEntity entity;
    if (settMerge != null)
      entity = new ImageMergeItem(hotspot, dataset, series, item, tip, url, settMerge, null);
    else
      entity = new XYItemEntity(hotspot, dataset, series, item, tip, url);
    entities.add(entity);
  }

  /**
   * only draw item if true
   * 
   * @param item
   * @return
   */
  public boolean isMapActive() {
    return sett != null && sett.isActive();
  }

  public void setImage(DataCollectable2D img) {
    if (this.img != img) {
      this.img = img;
      if (img.getImageGroup() != null)
        this.sett = img.getImageGroup().getSettAlphaMap();
      else
        this.sett = null;
      // block width and height for range detection
      setAvgBlockSize(img.getAvgBlockWidth(true), img.getAvgBlockHeight(true));
    }
  }

  private void setAvgBlockSize(float w, float h) {
    avgBlockHeight = h;
    avgBlockWidth = w;
  }


}
