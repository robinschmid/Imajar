package net.rs.lamsi.general.settings.image.selection;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import net.rs.lamsi.general.datamodel.image.interf.DataCollectable2D;
import net.rs.lamsi.general.myfreechart.general.annotations.EXYShapeAnnotation;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.general.settings.gui2d.SettingsBasicStroke;
import net.rs.lamsi.general.settings.image.visualisation.SettingsAlphaMap.State;
import net.rs.lamsi.multiimager.Frames.dialogs.selectdata.SelectionTableRow;

public abstract class SettingsShapeSelection<T extends Shape> extends Settings {
  // do not change the version!
  private static final long serialVersionUID = 1L;
  private static final Logger logger = LoggerFactory.getLogger(SettingsShapeSelection.class);

  // select,
  private static Map<SelectionMode, Color> colors = new HashMap<SelectionMode, Color>();
  private static Map<ROI, SettingsBasicStroke> strokes = new HashMap<ROI, SettingsBasicStroke>();

  /**
   * current shape is given by combobox
   *
   */
  public enum SHAPE {
    RECT, ELIPSE, POLYGON, FREEHAND
  }
  /**
   * what to draw or do
   *
   */
  public enum SelectionMode {
    SELECT("Sel"), EXCLUDE("Excl"), INFO("Info");

    private final String shortTitle;

    public String getShortTitle() {
      return shortTitle;
    }

    private SelectionMode(String shortTitle) {
      this.shortTitle = shortTitle;
    }
  }
  /**
   * defines the task for this ROI
   */
  public enum ROI {
    SAMPLE, QUANTIFIER, BLANK_LINES, BLANK_COLUMNS;

    /**
     * is blank or something else?
     * 
     * @return
     */
    public boolean isBlank() {
      return equals(BLANK_LINES) || equals(BLANK_COLUMNS);
    }
  }

  // selectionmode
  protected SelectionMode mode;
  protected ROI roi = ROI.SAMPLE;
  protected SelectionTableRow stats;
  protected SelectionTableRow statsRegardingExclusion;
  protected SelectionTableRow statsRegardingExclusionAndMap;
  protected Color color;
  protected SettingsBasicStroke stroke;
  // order for quantifiers
  protected int orderNumber = 0;
  // concentration for quantifier / qualifier
  protected double concentration = 0;

  protected transient DataCollectable2D currentImg;


  // highlight selection
  protected boolean isHighlighted = false;
  // the Shape
  protected T shape;
  private boolean isFinished;


  public SettingsShapeSelection(DataCollectable2D currentImage, ROI roi, SelectionMode mode,
      T shape) {
    super("SettingsShapeSelection", "/Settings/Selections/Shapes/", "setSelShape");

    this.shape = shape;
    this.mode = mode;
    this.roi = roi;
    this.currentImg = currentImage;
    color = getColorForSelectionMode(mode);
    stroke = getStrokeForROI(roi);

    stats = new SelectionTableRow();
    statsRegardingExclusion = new SelectionTableRow();
    statsRegardingExclusionAndMap = new SelectionTableRow();
  }

  public SettingsShapeSelection(SelectionMode mode, T shape) {
    super("SettingsShapeSelection", "/Settings/Selections/Shapes/", "setSelShape");
    this.shape = shape;
    this.mode = mode;
    color = getColorForSelectionMode(mode);
    // default roi
    stroke = getStrokeForROI(roi);
    stats = new SelectionTableRow();
    statsRegardingExclusion = new SelectionTableRow();
    statsRegardingExclusionAndMap = new SelectionTableRow();
  }

  // #############################################################################
  // TABLE MODEL
  /**
   * called for table
   * 
   * @return
   */
  public Object[] getRowData(boolean useMap) {
    float y0 = getY0();
    float x0 = getX0();
    float y1 = getY1();
    float x1 = getX1();

    SelectionTableRow r = getDefaultTableRow(useMap);

    return new Object[] {orderNumber, mode.toString(), roi.toString(), concentration, x0, y0, x1,
        y1, r.getN(), r.getSum(), r.getMin(), r.getMax(), r.getAvg(), r.getMedian(), r.getP99(),
        r.getSdev(), r.getSdevRel(), r.getHisto()};
  }

  /**
   * called for data export
   * 
   * @return without histogram
   */
  public Object[] getRowDataExport(boolean useMap) {
    float y0 = getY0();
    float x0 = getX0();
    float y1 = getY1();
    float x1 = getX1();

    SelectionTableRow r = getDefaultTableRow(useMap);

    return new Object[] {orderNumber, mode.toString(), roi.toString(), concentration, x0, y0, x1,
        y1, r.getN(), r.getSum(), r.getMin(), r.getMax(), r.getAvg(), r.getMedian(), r.getP99(),
        r.getSdev(), r.getSdevRel()};
  }

  /**
   * array for title line export without histo
   */
  public static Object[] getTitleArrayExport() {
    return new Object[] {"Order", "Mode", "ROI", "conc.", "x0", "y0", "x1", "y1", "n", "sum",
        "I min", "I max", "I avg", "I median", "I 99%", "Stdev", "Stdev rel (%)"};
  }

  public float getX0() {
    return (float) shape.getBounds2D().getMinX();
  }

  public float getX1() {
    return (float) shape.getBounds2D().getMaxX();
  }

  public float getCenterX() {
    return (float) shape.getBounds2D().getCenterX();
  }

  public float getY0() {
    return (float) shape.getBounds2D().getMinY();
  }

  public float getY1() {
    return (float) shape.getBounds2D().getMaxY();
  }

  public float getCenterY() {
    return (float) shape.getBounds2D().getCenterY();
  }


  public double getWidth() {
    return shape.getBounds2D().getWidth();
  }

  public double getHeight() {
    return shape.getBounds2D().getHeight();
  }

  // #############################################################################
  //

  @Override
  public void resetAll() {}

  @Override
  public Class getSuperClass() {
    return SettingsShapeSelection.class;
  }

  public void setCurrentImage(DataCollectable2D img) {
    if (img != null && !img.equals(currentImg)) {
      currentImg = img;
    }
  }


  /**
   * load settings instance from xml
   * 
   * @param parent element has information about the shape
   * @return
   */
  public static SettingsShapeSelection loadSettingsFromXML(Element elParent, Document doc) {
    Class c = null;
    try {
      c = Settings.getRealClassFromXML(elParent);
    } catch (Exception e) {
      logger.warn("Cannot create SettingsShapeSelection from {}", elParent.toString(), e);
    }
    if (c != null) {
      SettingsShapeSelection s = (SettingsShapeSelection) Settings.createSettings(c);
      if (s != null) {
        // load shape and mode
        s.loadValuesFromXML(elParent, doc);
      }
      return s;
    } else
      return null;
  }


  /**
   * save shape to xml
   */
  protected abstract void saveShapeToXML(Element elParent, Document doc, T shape);

  /**
   * load shape from xml
   * 
   * @param nextElement
   * @return
   */
  protected abstract T loadShapeFromXML(Element nextElement);

  // ##########################################################
  // xml input/output

  @Override
  public void appendSettingsValuesToXML(Element elParent, Document doc) {
    saveShapeToXML(elParent, doc, shape);
    toXML(elParent, doc, "selectionMode", mode);
    toXML(elParent, doc, "roi", roi);
    toXML(elParent, doc, "concentration", concentration);
    toXML(elParent, doc, "orderNumber", orderNumber);
    toXML(elParent, doc, "color", color);
    toXML(elParent, doc, "stroke", stroke);
  }

  @Override
  public void loadValuesFromXML(Element el, Document doc) {
    NodeList list = el.getChildNodes();
    for (int i = 0; i < list.getLength(); i++) {
      if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
        Element nextElement = (Element) list.item(i);
        String paramName = nextElement.getNodeName();
        if (paramName.equals("shape"))
          setShape(loadShapeFromXML(nextElement));
        else if (paramName.equals("selectionMode"))
          setMode(SelectionMode.valueOf(nextElement.getTextContent()));
        else if (paramName.equals("roi"))
          setRoi(ROI.valueOf(nextElement.getTextContent()));
        else if (paramName.equals("concentration"))
          concentration = doubleFromXML(nextElement);
        else if (paramName.equals("orderNumber"))
          orderNumber = intFromXML(nextElement);
        else if (paramName.equals("color"))
          color = colorFromXML(nextElement);
        else if (paramName.equals("stroke"))
          stroke = strokeFromXML(nextElement);
      }
    }
  }


  // ##########################################################
  // shape logic
  public abstract void setBounds(float x, float y, float w, float h);

  /**
   * sets the second anchor (end point) calculate size and position new
   * 
   * @param x
   * @param y
   */
  public abstract void setSecondAnchor(float x, float y);

  /**
   * sets the second anchor (end point) calculate size and position new
   * 
   * @param x
   * @param y
   */
  public void setSecondAnchor(double x, double y) {
    setSecondAnchor((float) x, (float) y);
  }


  public void setSize(float w, float h) {
    Rectangle2D r = shape.getBounds2D();
    setBounds((float) r.getX(), (float) r.getY(), w, h);
  }

  public void setPosition(float x, float y) {
    Rectangle2D r = shape.getBounds2D();
    setBounds(x, y, (float) r.getWidth(), (float) r.getHeight());
  }

  /**
   * translate / shift rect by distance
   * 
   * @param px
   * @param py
   */
  public void translate(float px, float py) {
    Rectangle2D r = shape.getBounds2D();
    setPosition((float) r.getX() + px, (float) r.getY() + py);
  }

  /**
   * grow or shrink(if negative)
   * 
   * @param px
   * @param py
   */
  public void grow(float px, float py) {
    Rectangle2D r = shape.getBounds2D();
    setSize((float) r.getWidth() + px, (float) r.getHeight() + py);
  }

  public abstract void transform(AffineTransform at);

  // ##########################################################
  // statistics

  /**
   * 
   * @param x left edge of data point
   * @param y bottom of data point
   * @param w width of data point (to calculate centre point)
   * @param h height of data point
   * @param i
   * @param isExcluded
   * @return
   */
  public boolean check(double x, double y, double i, float w, float h, boolean isExcluded,
      State dpstate) {
    return check((float) x, (float) y, i, w, h, isExcluded, dpstate);
  }

  public boolean check(float x, float y, double i, float w, float h, boolean isExcluded,
      State dpstate) {
    if (contains(x + w / 2.f, y + h / 2.f)) {
      // add data point
      stats.addValue(i);
      if (!isExcluded) {
        statsRegardingExclusion.addValue(i);
        if (!dpstate.isFalse())
          statsRegardingExclusionAndMap.addValue(i);
      }

      return true;
    } else
      return false;
  }

  /**
   * final stats calculation after all data points were added via check
   */
  public void calculateStatistics() {
    stats.calculateStatistics();
    statsRegardingExclusion.calculateStatistics();
    statsRegardingExclusionAndMap.calculateStatistics();
  }

  /**
   * checks if the point is inside the shape
   * 
   * @param p coordinates in the given processed data space
   * @return
   */
  public boolean contains(Point2D p) {
    return shape.contains(p);
  }

  /**
   * checks if the point is inside the shape
   * 
   * @param x coordinate in the given processed data space
   * @param y coordinate in the given processed data space
   * @return
   */
  public boolean contains(float x, float y) {
    return shape.contains(x, y);
  }

  /**
   * checks if the point is inside the shape
   * 
   * @param x coordinate in the given processed data space
   * @param y coordinate in the given processed data space
   * @return
   */
  public boolean contains(double x, double y) {
    return contains((float) x, (float) y);
  }

  public void setShape(T shape) {
    this.shape = shape;
  }

  public SelectionMode getMode() {
    return mode;
  }

  public void setMode(SelectionMode mode) {
    this.mode = mode;
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public T getShape() {
    return shape;
  }

  public static Color getColorForSelectionMode(SelectionMode mode) {
    if (colors.isEmpty()) {
      colors.put(SelectionMode.SELECT, Color.GREEN);
      colors.put(SelectionMode.INFO, Color.GRAY);
      colors.put(SelectionMode.EXCLUDE, Color.RED);
    }
    return colors.get(mode);
  }

  public static Map<SelectionMode, Color> getColors() {
    if (colors.isEmpty()) {
      colors.put(SelectionMode.SELECT, Color.GREEN);
      colors.put(SelectionMode.INFO, Color.GRAY);
      colors.put(SelectionMode.EXCLUDE, Color.RED);
    }
    return colors;
  }


  public static SettingsBasicStroke getStrokeForROI(ROI roi) {
    if (strokes.isEmpty()) {
      createStandardStrokes();
    }
    return strokes.get(roi);
  }

  public static Map<ROI, SettingsBasicStroke> getStrokes() {
    if (strokes.isEmpty()) {
      createStandardStrokes();
    }
    return strokes;
  }

  private static void createStandardStrokes() {
    strokes.put(ROI.QUANTIFIER, new SettingsBasicStroke(1.5f, BasicStroke.CAP_ROUND,
        BasicStroke.JOIN_MITER, 2f, new float[] {10f, 5f, 2.5f, 5f}, 0f));
    strokes.put(ROI.SAMPLE,
        new SettingsBasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 2f));
    strokes.put(ROI.BLANK_COLUMNS, new SettingsBasicStroke(1.5f, BasicStroke.CAP_ROUND,
        BasicStroke.JOIN_MITER, 2f, new float[] {15f, 7.5f}, 0f));
    strokes.put(ROI.BLANK_LINES, new SettingsBasicStroke(1.5f, BasicStroke.CAP_ROUND,
        BasicStroke.JOIN_MITER, 2f, new float[] {15f, 7.5f}, 0f));
  }

  /**
   * shape annotation with basic stroke
   * 
   * @return
   */
  public EXYShapeAnnotation createXYShapeAnnotation() {
    Color c = getColor();
    BasicStroke s = createStroke();
    EXYShapeAnnotation ann = new EXYShapeAnnotation(this.getShape(), s, c) {
      @Override
      public void draw(Graphics2D g2, XYPlot plot, Rectangle2D dataArea, ValueAxis domainAxis,
          ValueAxis rangeAxis, int rendererIndex, PlotRenderingInfo info) {
        // set blendComposite
        // g2.setComposite(BlendComposite.Difference);
        super.draw(g2, plot, dataArea, domainAxis, rangeAxis, rendererIndex, info);
        // g2.setComposite(BlendComposite.Normal);
        //
      }
    };
    ann.addTransformationListener(at -> transform(at));
    return ann;
  }

  /**
   * Creates a BasicStroke object that is highlighted or not
   * 
   * @return
   */
  public BasicStroke createStroke() {
    BasicStroke s = getStrokeSettings().getStroke();
    if (isHighlighted)
      s = new BasicStroke(4f, s.getEndCap(), s.getLineJoin(), s.getMiterLimit(), s.getDashArray(),
          s.getDashPhase());
    return s;
  }

  /**
   * default table row is in regards to exclusion for selections and the other for exclude and info
   * 
   * @return
   */
  public SelectionTableRow getDefaultTableRow(boolean useMap) {
    if (mode.equals(SelectionMode.SELECT))
      return useMap ? statsRegardingExclusionAndMap : statsRegardingExclusion;
    else
      return stats;
  }

  /**
   * table row is in regards to exclusion for selections and the other for exclude and info
   * 
   * @return
   */
  public SelectionTableRow getStats() {
    return stats;
  }

  /**
   * table row is in regards to exclusion for selections and the other for exclude and info
   * 
   * @return
   */
  public SelectionTableRow getStatsRegardingExclusions() {
    return statsRegardingExclusion;
  }

  public SelectionTableRow getStatsRegardingExclusionAndMap() {
    return statsRegardingExclusionAndMap;
  }

  /**
   * set the bounds by two mouse events
   * 
   * @param x0
   * @param y0
   * @param x1
   * @param y1
   */
  public void setFirstAndSecondMouseEvent(float x0, float y0, float x1, float y1) {
    setBounds(Math.min(x0, x1), Math.min(y0, y1), Math.abs(x1 - x0), Math.abs(y0 - y1));
  }

  public ROI getRoi() {
    return roi;
  }

  public void setRoi(ROI roi) {
    this.roi = roi;
  }

  public int getOrderNumber() {
    return orderNumber;
  }

  public double getConcentration() {
    return concentration;
  }

  public void setOrderNumber(int orderNumber) {
    this.orderNumber = orderNumber;
  }

  public void setConcentration(double concentration) {
    this.concentration = concentration;
  }

  public void setHighlighted(boolean b) {
    isHighlighted = b;
  }

  public boolean isHighlighted() {
    return isHighlighted;
  }

  public SettingsBasicStroke getStrokeSettings() {
    return stroke;
  }

  public void setStroke(SettingsBasicStroke stroke) {
    this.stroke = stroke;
  }

  public void clearData() {
    if (stats != null)
      stats.clearData();
    if (statsRegardingExclusion != null)
      statsRegardingExclusion.clearData();
    if (statsRegardingExclusionAndMap != null)
      statsRegardingExclusionAndMap.clearData();
  }

  public void setFinished(boolean b) {
    isFinished = b;
  }

  /**
   * 
   * @return
   */
  public boolean isFinished() {
    return isFinished;
  }

  /**
   * Rotate around center
   * 
   * @param degreeAngle degree 0-360
   */
  public void rotate(double degreeAngle) {
    // angle
    double angle = Math.toRadians(degreeAngle);
    AffineTransform at = AffineTransform.getRotateInstance(angle, getCenterX(), getCenterY());
    transform(at);
  }

  /**
   * Reflect horizontally
   * 
   */
  public void reflectH() {
    AffineTransform at = new AffineTransform();
    at.translate(getCenterX(), getCenterY());
    at.scale(-1, 1);
    at.translate(-getCenterX(), -getCenterY());
    transform(at);
  }

  /**
   * Reflect vertically
   * 
   */
  public void reflectV() {
    AffineTransform at = new AffineTransform();
    at.translate(getCenterX(), getCenterY());
    at.scale(1, -1);
    at.translate(-getCenterX(), -getCenterY());
    transform(at);
  }

  /**
   * translate (shift) ROI
   */
  public void translate(double tx, double ty) {
    transform(AffineTransform.getTranslateInstance(tx, ty));
  }

}
