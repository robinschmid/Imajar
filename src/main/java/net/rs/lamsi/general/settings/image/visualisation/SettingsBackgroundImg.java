package net.rs.lamsi.general.settings.image.visualisation;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.general.settings.interf.GroupSettings;
import net.rs.lamsi.utils.useful.graphics2d.blending.BlendComposite.BlendingMode;

public class SettingsBackgroundImg extends Settings implements GroupSettings {
  // do not change the version!
  private static final long serialVersionUID = 1L;
  private final Logger logger = LoggerFactory.getLogger(getClass());

  // original width for scale factor
  protected Dimension dimOriginal;
  protected AffineTransform at;

  // blending mode of overlayed images
  protected BlendingMode blend = BlendingMode.NORMAL;
  //
  protected boolean isVisible = false;
  protected File pathBGImage = null;
  // width, offset and angle of bg image
  protected Point2D offset;
  protected double bgWidth = 0;
  // angle in degree
  protected double angle = 0;



  // ##########################################################
  // xml input/output
  @Override
  public void appendSettingsValuesToXML(Element elParent, Document doc) {
    toXML(elParent, doc, "isVisible", isVisible);
    toXML(elParent, doc, "pathBGImage", pathBGImage);
    toXML(elParent, doc, "bgWidth", bgWidth);
    toXML(elParent, doc, "offset", offset);
    toXML(elParent, doc, "angle", angle);
    toXML(elParent, doc, "blend", blend);
  }

  @Override
  public void loadValuesFromXML(Element el, Document doc) {
    NodeList list = el.getChildNodes();
    for (int i = 0; i < list.getLength(); i++) {
      if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
        Element nextElement = (Element) list.item(i);
        String paramName = nextElement.getNodeName();
        if (paramName.equals("isVisible"))
          isVisible = booleanFromXML(nextElement);
        else if (paramName.equals("pathBGImage"))
          pathBGImage = new File(nextElement.getTextContent());
        else if (paramName.equals("bgWidth"))
          bgWidth = doubleFromXML(nextElement);
        else if (paramName.equals("angle"))
          angle = doubleFromXML(nextElement);
        else if (paramName.equals("offset"))
          offset = point2DDoubleFromXML(nextElement);
        else if (paramName.equals("blend"))
          blend = BlendingMode.valueOf(nextElement.getTextContent());
      }
    }
  }

  // settings
  public SettingsBackgroundImg() {
    super("SettingsBackgroundImg", "Settings/Visualization/", "setBGImg");
    resetAll();
  }


  @Override
  public void resetAll() {
    isVisible = false;
    pathBGImage = null;
    bgWidth = 0;
    offset = new Point2D.Float(0, 0);
    dimOriginal = null;
    angle = 0;
    at = null;
    blend = BlendingMode.NORMAL;
  }

  private void updateAT() {
    at = new AffineTransform();
    // 3. do the actual rotation
    at.rotate(getAngleRad());

    // 2. just a scale because this image is big
    double f = getScaleFactor();
    at.scale(f, f);

    // 1. translate the object so that you rotate it around the center
    Dimension dim = getDimOriginal();
    at.translate(-dim.getWidth() / 2.0, -dim.getHeight() / 2.0);
  }

  public AffineTransform getAffineTransform() {
    if (at == null)
      updateAT();
    return at;
  }

  /**
   * one scaling factor for width and height
   * 
   * @return
   */
  public double getScaleFactor() {
    if (bgWidth == 0) {
      return 1.0;
    } else {
      Dimension dim = getDimOriginal();
      if (dim != null) {
        return bgWidth / dim.getWidth();
      } else {
        logger.warn(
            "Cannot calculate scaling factor for background image. Check original width and width parameter.");
        return 1.0;
      }
    }
  }

  public BlendingMode getBlend() {
    return blend;
  }

  public void setBlend(BlendingMode blend) {
    this.blend = blend;
  }

  /**
   * reads the original width of the image
   * 
   * @return the dimension or null
   */
  private Dimension getDimOriginal() {
    if (pathBGImage == null)
      return null;
    if (dimOriginal == null) {
      try (ImageInputStream in = ImageIO.createImageInputStream(pathBGImage)) {
        final Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
        if (readers.hasNext()) {
          ImageReader reader = readers.next();
          try {
            reader.setInput(in);
            dimOriginal = new Dimension(reader.getWidth(0), reader.getHeight(0));
          } finally {
            reader.dispose();
          }
        }
      } catch (IOException e) {
        logger.warn("Cannot read image width of {} in {}", pathBGImage.getName(),
            pathBGImage.getParent(), e);
      }
    }
    return dimOriginal;
  }

  public void setPathBGImage(File pathBGImage) {
    if (this.pathBGImage == null || !this.pathBGImage.equals(pathBGImage))
      at = null;
    this.pathBGImage = pathBGImage;
    // reset original width
    dimOriginal = null;
  }

  public File getPathBGImage() {
    return pathBGImage;
  }

  public double getBgWidth() {
    return bgWidth;
  }

  public boolean isVisible() {
    return isVisible;
  }

  public void setVisible(boolean isVisible) {
    this.isVisible = isVisible;
  }

  public void setBgWidth(double bgWidth) {
    if (this.bgWidth != bgWidth)
      at = null;
    this.bgWidth = bgWidth;
  }

  public Point2D getOffset() {
    return offset;
  }

  public void setOffset(Point2D offset) {
    if (this.offset == null || this.offset.getX() != offset.getX()
        || this.offset.getY() != offset.getY())
      at = null;
    this.offset = offset;
  }

  public double getAngle() {
    return angle;
  }

  public double getAngleRad() {
    return Math.toRadians(angle);
  }

  public void setAngle(double angle) {
    if (this.angle != angle)
      at = null;
    this.angle = angle;
  }

  public void setOffset(double x, double y) {
    setOffset(new Point2D.Double(x, y));
  }

}
