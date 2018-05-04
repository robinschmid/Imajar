package net.rs.lamsi.general.settings.image.selection;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.Point2D;
import org.apache.batik.ext.awt.geom.Polygon2D;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import net.rs.lamsi.general.datamodel.image.Image2D;

public class SettingsPolygonSelection extends SettingsShapeSelection<Polygon2D> {
  // do not change the version!
  private static final long serialVersionUID = 1L;

  public SettingsPolygonSelection(Image2D currentImage, ROI roi, SelectionMode mode) {
    super(currentImage, roi, mode, new Polygon2D());
  }

  /**
   * 
   * @param currentImage
   * @param roi
   * @param mode
   * @param s shape is going to be flattened {@link FlatteningPathIterator}
   */
  public SettingsPolygonSelection(Image2D currentImage, ROI roi, SelectionMode mode, Shape s) {
    this(currentImage, roi, mode);
    setPolygonFromShape(s);
  }

  public SettingsPolygonSelection(Image2D currentImage, ROI roi, SelectionMode mode, float x,
      float y) {
    super(currentImage, roi, mode, new Polygon2D());
    shape.addPoint(x, y);
  }

  public SettingsPolygonSelection(SelectionMode mode) {
    super(mode, new Polygon2D());
  }

  public SettingsPolygonSelection() {
    this(SelectionMode.SELECT);
  }

  @Override
  public void resetAll() {}

  @Override
  public Class getSuperClass() {
    return SettingsShapeSelection.class;
  }

  /**
   * Flatten shape to polygon with {@link FlatteningPathIterator}
   * 
   * @param s
   */
  public void setPolygonFromShape(Shape s) {
    shape.reset();

    FlatteningPathIterator iter =
        new FlatteningPathIterator(s.getPathIterator(new AffineTransform()), 1);
    float[] coords = new float[6];
    while (!iter.isDone()) {
      iter.currentSegment(coords);
      float x = coords[0];
      float y = coords[1];
      shape.addPoint(x, y);
      iter.next();
    }
  }

  // ##########################################################
  // xml input/output
  /**
   * load shape from xml
   * 
   * @param nextElement
   * @return
   */
  protected Polygon2D loadShapeFromXML(Element nextElement) {
    shape = new Polygon2D();
    int i = 0;
    while (true) {
      String sx = nextElement.getAttribute("x" + i);
      if (sx.length() == 0)
        break;
      else {
        // add point
        float x = Float.valueOf(sx);
        float y = Float.valueOf(nextElement.getAttribute("y" + i));
        shape.addPoint(x, y);
        i++;
      }
    }
    return shape;
  }

  /**
   * save shape to xml
   */
  @Override
  protected void saveShapeToXML(Element elParent, Document doc, Polygon2D shape) {
    elParent.setNodeValue(SHAPE.POLYGON.toString());

    String[] att = new String[shape.npoints * 2];
    Object[] val = new Object[shape.npoints * 2];

    for (int i = 0; i < shape.npoints * 2; i += 2) {
      att[i] = "x" + i / 2;
      att[i + 1] = "y" + i / 2;

      val[i] = shape.xpoints[i / 2];
      val[i + 1] = shape.ypoints[i / 2];
    }

    toXML(elParent, doc, "shape", "", att, val);
  }

  public void addPoint(float x, float y) {
    shape.addPoint(x, y);
  }

  public void clear() {
    shape.reset();
  }

  public void setBounds(float x, float y, float w, float h) {}

  public void setSize(float w, float h) {}

  public void setPosition(float x, float y) {}

  /**
   * translate / shift rect by distance
   * 
   * @param px
   * @param py
   */
  public void translate(float px, float py) {
    Polygon2D poly = new Polygon2D();
    for (int i = 0; i < shape.npoints; i++) {
      poly.addPoint(shape.xpoints[i] + px, shape.ypoints[i] + py);
    }
    shape = poly;
  }

  /**
   * grow or shrink(if negative)
   * 
   * @param px
   * @param py
   */
  public void grow(float px, float py) {
    setSize((float) shape.getBounds2D().getWidth() + px,
        (float) shape.getBounds2D().getHeight() + py);
  }

  @Override
  public void setSecondAnchor(float x, float y) {}

  public void setFirstAndSecondMouseEvent(float x0, float y0, float x1, float y1) {
    addPoint(x1, y1);
  }

  @Override
  public void transform(AffineTransform at) {
    float[] x = shape.xpoints;
    float[] y = shape.ypoints;
    float[] rx = new float[x.length];
    float[] ry = new float[y.length];
    Point2D.Float p;

    for (int i = 0; i < shape.npoints; i++) {
      p = new Point2D.Float(x[i], y[i]);
      at.transform(p, p);
      rx[i] = p.x;
      ry[i] = p.y;
    }

    shape = new Polygon2D(rx, ry, shape.npoints);
  }
}
