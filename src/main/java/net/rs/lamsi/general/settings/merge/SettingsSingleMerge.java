package net.rs.lamsi.general.settings.merge;

import java.awt.geom.Point2D;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import net.rs.lamsi.general.settings.Settings;

public class SettingsSingleMerge extends Settings {
  private static final long serialVersionUID = 1L;

  // distance to shift as x y unit
  private float dx;
  private float dy;
  // angle as
  private float angle;
  // rotation anchor
  private Point2D.Float anchor;

  public SettingsSingleMerge(float dx, float dy, float angle, Point2D.Float anchor) {
    super("SettingsImageMerge", "/Settings/operations/", "setMerge");
    this.dx = dx;
    this.dy = dy;
    this.angle = angle;
    this.anchor = anchor;
  }

  public SettingsSingleMerge(float dx, float dy, float angle) {
    this(dx, dy, angle, new Point2D.Float(0.5f, 0.5f));
  }

  public SettingsSingleMerge() {
    this(0, 0, 0);
  }

  public void setAll(float dx, float dy, float angle, Point2D.Float anchor) {
    this.dx = dx;
    this.dy = dy;
    this.angle = angle;
    this.anchor = anchor;
  }

  @Override
  public void resetAll() {
    dx = 0;
    dy = 0;
    angle = 0;
    anchor = new Point2D.Float(0.5f, 0.5f);
  }

  // ##########################################################
  // xml input/output
  @Override
  public void appendSettingsValuesToXML(Element elParent, Document doc) {
    toXML(elParent, doc, "dx", dx);
    toXML(elParent, doc, "dy", dy);
    toXML(elParent, doc, "angle", angle);
    toXML(elParent, doc, "anchor.x", anchor.x);
    toXML(elParent, doc, "anchor.y", anchor.y);
  }

  @Override
  public void loadValuesFromXML(Element el, Document doc) {
    float ax = 0.5f;
    float ay = 0.5f;
    NodeList list = el.getChildNodes();
    for (int i = 0; i < list.getLength(); i++) {
      if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
        Element nextElement = (Element) list.item(i);
        String paramName = nextElement.getNodeName();
        if (paramName.equals("dx"))
          dx = floatFromXML(nextElement);
        else if (paramName.equals("dy"))
          dy = floatFromXML(nextElement);
        else if (paramName.equals("angle"))
          angle = floatFromXML(nextElement);
        else if (paramName.equals("anchor.x"))
          ax = floatFromXML(nextElement);
        else if (paramName.equals("anchor.y"))
          ay = floatFromXML(nextElement);
      }
    }
    anchor = new Point2D.Float(ax, ay);
  }

  public float getDX() {
    return dx;
  }

  public void setDX(float dx) {
    this.dx = dx;
  }

  public float getDY() {
    return dy;
  }

  public void setDY(float dy) {
    this.dy = dy;
  }

  public float getAngle() {
    return angle;
  }

  public void setAngle(float angle) {
    this.angle = angle;
  }

  public Point2D.Float getAnchor() {
    return anchor;
  }

  public void setAnchor(Point2D.Float anchor) {
    this.anchor = anchor;
  }
}
