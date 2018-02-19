package net.rs.lamsi.general.settings.image.sub;

import org.jfree.chart.plot.XYPlot;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.heatmap.Heatmap;
import net.rs.lamsi.general.myfreechart.plot.XYSquaredPlot;
import net.rs.lamsi.general.myfreechart.plot.XYSquaredPlot.Scale;
import net.rs.lamsi.general.settings.Settings;


public class SettingsGeneralCollecable2D extends Settings {

  // do not change the version!
  private static final long serialVersionUID = 1L;
  //

  protected String title = "NODEF", shortTitle = "";
  protected boolean showShortTitle = true;
  protected float xPosTitle = 0.9f, yPosTitle = 0.9f;

  protected boolean keepAspectRatio = true;

  public SettingsGeneralCollecable2D(String desc, String path, String fileEnding) {
    super(desc, path, fileEnding);
    resetAll();
  }

  public SettingsGeneralCollecable2D() {
    this("SettingsGeneralCollecable2D", "/Settings/GeneralImage/", "setGeneralC2D");
  }

  @Override
  public void resetAll() {
    title = "";
    shortTitle = "";
    showShortTitle = true;
    xPosTitle = 0.9f;
    yPosTitle = 0.9f;
    keepAspectRatio = true;
  }


  public void setAll(String title, String shortTitle, boolean useShortTitle, float xPos, float yPos,
      boolean keepAspectRatio) {
    this.shortTitle = shortTitle;
    this.title = title;
    this.showShortTitle = useShortTitle;
    this.xPosTitle = xPos;
    this.yPosTitle = yPos;
    this.keepAspectRatio = keepAspectRatio;
  }


  /**
   * All sub settings should have this as super class / mapped class
   */
  @Override
  public Class getSuperClass() {
    return SettingsGeneralCollecable2D.class;
  }


  @Override
  public void applyToImage(Collectable2D c) throws Exception {
    SettingsGeneralCollecable2D old =
        (SettingsGeneralCollecable2D) c.getSettingsByClass(SettingsGeneralCollecable2D.class);

    if (old != null) {
      // dont copy name
      String name = old.getTitle();
      String shortTitle = old.getShortTitle();

      super.applyToImage(c);

      // new settings object
      SettingsGeneralCollecable2D sett =
          (SettingsGeneralCollecable2D) c.getSettingsByClass(SettingsGeneralCollecable2D.class);
      // reset to old short title only if not the same title
      if (!name.equals(old.getTitle())) {
        sett.setShortTitle(shortTitle);
      }
      // reset to old title
      sett.setTitle(name);
    } else
      super.applyToImage(c);
  }

  @Override
  public void applyToHeatMap(Heatmap heat) {
    super.applyToHeatMap(heat);
    // TODO apply to title in heat
    heat.setShortTitle(xPosTitle, yPosTitle, showShortTitle);

    // Square plot
    XYPlot p = heat.getChart().getXYPlot();
    if (p instanceof XYSquaredPlot)
      ((XYSquaredPlot) p).setScaleMode(keepAspectRatio ? Scale.DYNAMIC : Scale.IGNORE);
  }

  // ##########################################################
  // xml input/output
  @Override
  public void appendSettingsValuesToXML(Element elParent, Document doc) {
    toXML(elParent, doc, "title", title);
    toXML(elParent, doc, "shortTitle", shortTitle);
    toXML(elParent, doc, "showShortTitle", showShortTitle);
    toXML(elParent, doc, "xPosTitle", xPosTitle);
    toXML(elParent, doc, "yPosTitle", yPosTitle);
    toXML(elParent, doc, "keepAspectRatio", keepAspectRatio);
  }

  @Override
  public void loadValuesFromXML(Element el, Document doc) {
    NodeList list = el.getChildNodes();
    for (int i = 0; i < list.getLength(); i++) {
      if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
        Element nextElement = (Element) list.item(i);
        String paramName = nextElement.getNodeName();
        if (paramName.equals("xPosTitle"))
          xPosTitle = floatFromXML(nextElement);
        else if (paramName.equals("yPosTitle"))
          yPosTitle = floatFromXML(nextElement);
        else if (paramName.equals("title"))
          title = nextElement.getTextContent();
        else if (paramName.equals("shortTitle"))
          shortTitle = nextElement.getTextContent();
        else if (paramName.equals("showShortTitle"))
          showShortTitle = booleanFromXML(nextElement);
        else if (paramName.equals("keepAspectRatio"))
          keepAspectRatio = booleanFromXML(nextElement);
      }
    }
  }


  public boolean isKeepAspectRatio() {
    return keepAspectRatio;
  }

  public void setKeepAspectRatio(boolean keepAspectRatio) {
    this.keepAspectRatio = keepAspectRatio;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String toListName() {
    return getTitle();
  }

  public String getShortTitle() {
    return shortTitle;
  }

  public boolean isShowShortTitle() {
    return showShortTitle;
  }

  public void setShortTitle(String shortTitle) {
    this.shortTitle = shortTitle;
  }

  public void setShowShortTitle(boolean showShortTitle) {
    this.showShortTitle = showShortTitle;
  }

  public float getXPosTitle() {
    return xPosTitle;
  }

  public float getYPosTitle() {
    return yPosTitle;
  }

  public void setXPosTitle(float xPosTitle) {
    this.xPosTitle = xPosTitle;
  }

  public void setYPosTitle(float yPosTitle) {
    this.yPosTitle = yPosTitle;
  }

}
