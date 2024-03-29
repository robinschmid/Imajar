package net.rs.lamsi.general.myfreechart.plots.image2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;
import net.rs.lamsi.utils.useful.graphics2d.blending.BlendComposite;


public class ImageOverlayRenderer extends ImageRenderer {

  protected double[] blockWidths, blockHeights;
  protected PaintScale scales[];

  protected BlendComposite blend = BlendComposite.Add;
  // paint all to image and then flush to g2d
  protected BufferedImage image = null;
  protected BufferedImage finalimage = null;
  protected Graphics2D gimg, gimgFinal;


  public ImageOverlayRenderer(int size, BlendComposite blend) {
    super();
    this.blend = blend;
    blockWidths = new double[size];
    blockHeights = new double[size];
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

    // create new graphics 2d
    if (item == state.getFirstItemIndex()) {
      int w = (int) dataArea.getWidth();
      int h = (int) dataArea.getHeight();
      image = new BufferedImage(w * 3, h * 3, BufferedImage.TYPE_INT_ARGB);
      gimg = image.createGraphics();
      // set block width and heigh
      // setBlockWidth(getBlockWidth(series));
      // setBlockHeight(getBlockHeight(series));
      // setPaintScale(getPaintScale(series));
    }

    // only if in map or if there is no map
    // if(isMapTrue(item)) {
    // background is either an image or black
    gimg.setComposite(BlendComposite.Normal);
    // drawItem(gimg, state, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item,
    // crosshairState, pass, BlendComposite.Normal);
    drawBlockItem(gimg, state, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item,
        crosshairState, pass);
    // }

    // last item?
    if (state.getLastItemIndex() == item) {
      // if(blend.getMode().equals(BlendingMode.ADD)) {
      // flush in mode
      g2.setComposite(BlendComposite.Add);
      g2.drawImage(image, 0, 0, null);
      g2.setComposite(BlendComposite.Normal);

      image = null;
      gimg.finalize();
      // }
    }
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
  public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea,
      PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis,
      XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass,
      BlendComposite blend) {

    double x = dataset.getXValue(series, item);
    double y = dataset.getYValue(series, item);
    double z = 0.0;
    if (dataset instanceof XYZDataset) {
      z = ((XYZDataset) dataset).getZValue(series, item);
    }

    // do not paint if in BlendMode Normal and < lowerBound
    Paint p = this.getPaintScale(series).getPaint(z);
    double xx0 = domainAxis.valueToJava2D(x, dataArea, plot.getDomainAxisEdge());
    double yy0 = rangeAxis.valueToJava2D(y, dataArea, plot.getRangeAxisEdge());
    double xx1 = domainAxis.valueToJava2D(x + this.getBlockWidth(series), dataArea,
        plot.getDomainAxisEdge());
    double yy1 =
        rangeAxis.valueToJava2D(y + this.getBlockHeight(series), dataArea, plot.getRangeAxisEdge());
    Rectangle2D block;
    PlotOrientation orientation = plot.getOrientation();
    if (orientation.equals(PlotOrientation.HORIZONTAL)) {
      block = new Rectangle2D.Double(Math.min(yy0, yy1), Math.min(xx0, xx1), Math.abs(yy1 - yy0),
          Math.abs(xx0 - xx1));
    } else {
      block = new Rectangle2D.Double(Math.min(xx0, xx1), Math.min(yy0, yy1), Math.abs(xx1 - xx0),
          Math.abs(yy1 - yy0));
    }
    // do only paint if inside rect
    if (dataArea.intersects(block)) {
      g2.setPaint(p);
      g2.setComposite(blend);
      g2.fill(block);

      int datasetIndex = plot.indexOf(dataset);
      double transX = domainAxis.valueToJava2D(x, dataArea, plot.getDomainAxisEdge());
      double transY = rangeAxis.valueToJava2D(y, dataArea, plot.getRangeAxisEdge());

      updateCrosshairValues(crosshairState, x, y, datasetIndex, transX, transY, orientation);

      EntityCollection entities = state.getEntityCollection();
      if (entities != null) {

        Rectangle2D intersect = block.createIntersection(dataArea);
        addEntity(entities, intersect, dataset, series, item, intersect.getCenterX(),
            intersect.getCenterY());
      }
    }
  }

  public void eraseBlackPixel(BufferedImage image, Graphics2D g2d) {
    int newrgb = new Color(0, 0, 0, 0).getRGB();

    final int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    final int width = image.getWidth();
    final int height = image.getHeight();
    final boolean hasAlphaChannel = image.getAlphaRaster() != null;

    int[][] result = new int[height][width];
    if (hasAlphaChannel) {
      final int pixelLength = 4;
      for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
        // int argb = 0;
        // argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
        // argb += ((int) pixels[pixel + 1] & 0xff); // blue
        // argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
        // argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red

        int b = ((int) pixels[pixel + 1] & 0xff); // blue
        int g = (((int) pixels[pixel + 2] & 0xff) << 8); // green
        int r = (((int) pixels[pixel + 3] & 0xff) << 16); // red

        if (r == 0 && b == 0 && g == 0)
          image.setRGB(col, row, newrgb);

        col++;
        if (col == width) {
          col = 0;
          row++;
        }
      }
    }
  }

  public PaintScale getPaintScale(int i) {
    if (scales == null)
      return null;
    return scales[i];
  }

  public PaintScale[] getPaintScales() {
    return scales;
  }

  public void setPaintScales(PaintScale[] scales) {
    this.scales = scales;
  }

  public void setBlockWidth(int i, double maxBlockWidth) {
    blockWidths[i] = maxBlockWidth;
  }

  public double getBlockWidth(int i) {
    return i < blockWidths.length && i >= 0 ? blockWidths[i] : 0;
  }

  public void setBlockHeight(int i, double maxBlockH) {
    blockHeights[i] = maxBlockH;
  }

  public double getBlockHeight(int i) {
    return i < blockHeights.length && i >= 0 ? blockHeights[i] : 0;
  }
}
