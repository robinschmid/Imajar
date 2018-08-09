package net.rs.lamsi.general.settings.image.merge;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.utils.useful.graphics2d.blending.BlendComposite;
import net.rs.lamsi.utils.useful.graphics2d.blending.BlendComposite.BlendingMode;

public class SettingsSingleMerge extends Settings {
  private static final long serialVersionUID = 1L;

  private boolean visible;
  // distance to shift as x y unit
  private float dx;
  private float dy;
  // angle as
  private float angle;
  // rotation anchor
  private Point2D.Float anchor;

  // add or normal?
  private BlendingMode blendMode = BlendingMode.NORMAL;
  private float transparency = 1f;

  public SettingsSingleMerge(float dx, float dy, float angle, Point2D.Float anchor,
      boolean visible) {
    super("SettingsImageMerge", "/Settings/operations/", "setMerge");
    this.dx = dx;
    this.dy = dy;
    this.angle = angle;
    this.anchor = anchor;
    this.visible = visible;
  }

  public SettingsSingleMerge(float dx, float dy, float angle, boolean visible) {
    this(dx, dy, angle, new Point2D.Float(0.5f, 0.5f), visible);
  }

  public SettingsSingleMerge() {
    this(0, 0, 0, true);
  }

  public void setAll(float dx, float dy, float angle, Point2D.Float anchor, boolean visible) {
    this.dx = dx;
    this.dy = dy;
    this.angle = angle;
    this.anchor = anchor;
    this.visible = visible;
  }

  @Override
  public void resetAll() {
    dx = 0;
    dy = 0;
    angle = 0;
    anchor = new Point2D.Float(0.5f, 0.5f);
    visible = true;
    blendMode = BlendingMode.NORMAL;
    transparency = 1f;
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
    toXML(elParent, doc, "visible", visible);
    toXML(elParent, doc, "overlay", blendMode);
    toXML(elParent, doc, "transparency", transparency);
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
        else if (paramName.equals("visible"))
          visible = booleanFromXML(nextElement);
        else if (paramName.equals("overlay"))
          blendMode = BlendingMode.valueOf(nextElement.getTextContent());
        else if (paramName.equals("transparency"))
          transparency = floatFromXML(nextElement);
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

  public boolean isVisible() {
    return visible;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  public float getTransparency() {
    return transparency;
  }

  public void setTransparency(float transparency) {
    this.transparency = transparency;
  }

  /**
   * Transparency and blend mode
   * 
   * @return
   */
  public BlendComposite getBlendComposite() {
    return BlendComposite.getInstance(blendMode, transparency);
  }

  public void setBlendingMode(BlendingMode blendMode) {
    this.blendMode = blendMode;
  }

  public AffineTransform getAffineTransform() {
    // angle
    double angle = Math.toRadians(this.angle);
    AffineTransform at = AffineTransform.getRotateInstance(angle, 0, 0);
    at.translate(dx, dy);
    return at;
  }

  public void setAnchor(float x, float y) {
    anchor.setLocation(x, y);
  }

  public BlendingMode getBlendMode() {
    return blendMode;
  }

  public void translate(double x, double y) {
    this.translate((float) x, (float) y);
  }

  public void translate(float x, float y) {
    setDX(dx + x);
    setDY(dy + y);
  }
}
