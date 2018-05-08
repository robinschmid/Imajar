package net.rs.lamsi.general.settings.image.operations.quantifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import net.rs.lamsi.general.datamodel.image.Collectable2DPlaceHolderLink;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.framework.modules.ModuleTree;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;

public class SettingsImage2DQuantifierIS extends SettingsImage2DQuantifier {
  // do not change the version!
  private static final long serialVersionUID = 1L;
  private final Logger logger = LoggerFactory.getLogger(getClass());

  public enum THRESHOLD_MODE {
    REPLACE_AVG("Replace IS value by avg"), REPLACE_MIN_NON_ZERO(
        "Replace IS value by non-zero min"), REPLACE_MIN("Replace IS value by min"), REPLACE_MAX(
            "Replace IS value by max"), SET_TO_ZERO(
                "Set result to zero"), SET_TO_VALUE("Set result to value...");

    private String text;

    private THRESHOLD_MODE(String text) {
      this.text = text;
    }

    @Override
    public String toString() {
      return text;
    }

    public static THRESHOLD_MODE getMode(String text) {
      for (THRESHOLD_MODE m : THRESHOLD_MODE.values())
        if (m.text.equals(text))
          return m;
      return null;
    }
  }

  // image for IS
  protected Image2D imgIS;
  // after import there is only a link
  protected transient Collectable2DPlaceHolderLink link = null;

  // double
  protected double concentrationFactor = 1;
  // internal standard intensity threshold (to not devide by 0/too small values
  protected double threshold = 0;
  protected double replacementValue = 0;
  protected boolean onlySelected = false, useISMinimumAsThreshold = true;

  // the mode to replace/set a value if Intensity(IS) <= threshold
  protected THRESHOLD_MODE mode = THRESHOLD_MODE.REPLACE_AVG;



  public SettingsImage2DQuantifierIS() {
    super(MODE.IS, "SettingsImage2DQuantifierIS", "/Settings/operations/IS/", "setISDiv");
  }

  public SettingsImage2DQuantifierIS(Image2D imgIS) {
    super(MODE.IS, "SettingsImage2DQuantifierIS", "/Settings/operations/IS/", "setISDiv");
    this.imgIS = imgIS;
  }

  @Override
  public void resetAll() {
    super.resetAll();
    imgIS = null;
  }

  /**
   * 
   */
  @Override
  public double calcIntensity(Image2D img, int line, int dp, double intensity) {
    if (!isActive() || !isApplicable())
      return intensity;
    else {
      double is = imgIS.getI(false, line, dp);

      // error - not in dimension - return NaN
      if (Double.isNaN(is))
        return Double.NaN;
      if (is == 0) {
        // thresholding
        double t =
            useISMinimumAsThreshold ? imgIS.getSettings().getSettPaintScale().getMinIAbs(imgIS)
                : threshold;

        if (is < t) {
          // handle <threshold with mode
          switch (mode) {
            case REPLACE_AVG:
              is = imgIS.getAverageIntensity(onlySelected);
              break;
            case REPLACE_MAX:
              is = imgIS.getMaxIntensity(onlySelected);
              break;
            case REPLACE_MIN:
              is = imgIS.getMinIntensity(onlySelected);
              break;
            case REPLACE_MIN_NON_ZERO:
              is = imgIS.getMinNonZeroIntensity(onlySelected);
              break;
            case SET_TO_VALUE:
              return replacementValue;
            case SET_TO_ZERO:
              return 0;
          }
        }
      }

      // either is was in range or was replaced
      return intensity / is * concentrationFactor;
    }
  }

  /**
   * force blank
   * 
   * @param img
   * @param line
   * @param dp
   * @param intensity
   * @param blank
   * @return
   */
  public double calcIntensity(Image2D img, int line, int dp, double intensity, boolean blank) {
    if (isApplicable() && line < imgIS.getLineCount(dp) && dp < imgIS.getLineLength(line)) {
      if (blank) {
        SettingsImage2DBlankSubtraction b =
            imgIS.getSettings().getOperations().getBlankQuantifier();
        boolean tmp = b.isActive();
        b.setActive(blank);
        double is = imgIS.getI(false, line, dp);
        if (is == 0)
          return 0;
        //
        intensity = intensity / is * concentrationFactor;
        b.setActive(tmp);
        return intensity;
      }
    }
    return intensity;
  }

  // TODO same data dimensions?
  public boolean isApplicable() {
    return (imgIS != null && imgIS.getData() != null);
  }

  public Image2D getImgIS() {
    // try to replace?
    if (imgIS == null && link != null) {
      try {
        ModuleTree<Collectable2D> tree = ImageEditorWindow.getEditor().getLogicRunner().getTree();
        imgIS = (Image2D) tree.getCollectable2DFromPlaceHolder(link);
        if (imgIS != null)
          link = null;
      } catch (Exception e) {
      }
    }
    return imgIS;
  }

  public void setImgIS(Image2D imgIS) {
    this.imgIS = imgIS;
  }

  public double getConcentrationFactor() {
    return concentrationFactor;
  }

  public void setConcentrationFactor(double concentrationFactor) {
    this.concentrationFactor = concentrationFactor;
  }


  @Override
  public void appendSettingsValuesToXML(Element elParent, Document doc) {
    super.appendSettingsValuesToXML(elParent, doc);
    toXML(elParent, doc, "factor", concentrationFactor);
    toXML(elParent, doc, "threshold", threshold);
    toXML(elParent, doc, "replacementValue", replacementValue);
    toXML(elParent, doc, "onlySelected", onlySelected);
    toXML(elParent, doc, "mode", mode);
    if (imgIS != null)
      toXML(elParent, doc, "externalSTDImage", imgIS);
    else if (link != null)
      toXML(elParent, doc, "externalSTDImage", link);
  }

  @Override
  public void loadValuesFromXML(Element el, Document doc) {
    super.loadValuesFromXML(el, doc);
    NodeList list = el.getChildNodes();
    for (int i = 0; i < list.getLength(); i++) {
      if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
        Element nextElement = (Element) list.item(i);
        String paramName = nextElement.getNodeName();
        if (paramName.equals("externalSTDImage"))
          link = c2dFromXML(nextElement);
        else if (paramName.equals("factor"))
          concentrationFactor = doubleFromXML(nextElement);
        else if (paramName.equals("mode"))
          mode = THRESHOLD_MODE.getMode(nextElement.getTextContent());
        else if (paramName.equals("threshold"))
          threshold = doubleFromXML(nextElement);
        else if (paramName.equals("replacementValue"))
          replacementValue = doubleFromXML(nextElement);
        else if (paramName.equals("onlySelected"))
          onlySelected = booleanFromXML(nextElement);
      }
    }
  }

  @Override
  public void replacePlaceHoldersInSettings(ModuleTree<Collectable2D> tree) {
    super.replacePlaceHoldersInSettings(tree);
    if (link != null) {
      try {
        imgIS = (Image2D) tree.getCollectable2DFromPlaceHolder(link);
        if (imgIS != null)
          link = null;
      } catch (Exception e) {
        logger.error("Cannot replace img placeholder {}", link.toString(), e);
      }
    }
  }

  @Override
  public Settings copy() throws Exception {
    // do not copy image2d
    SettingsImage2DQuantifierIS sett = (SettingsImage2DQuantifierIS) super.copy();
    sett.setImgIS(imgIS);
    return sett;
  }

  public double getThreshold() {
    return threshold;
  }

  public double getReplacementValue() {
    return replacementValue;
  }

  public boolean isOnlySelected() {
    return onlySelected;
  }

  public THRESHOLD_MODE getISMode() {
    return mode;
  }

  public void setThreshold(double threshold) {
    this.threshold = threshold;
  }

  public void setReplacementValue(double replacementValue) {
    this.replacementValue = replacementValue;
  }

  public void setOnlySelected(boolean onlySelected) {
    this.onlySelected = onlySelected;
  }

  public boolean isUseISMinimumAsThreshold() {
    return useISMinimumAsThreshold;
  }

  public void setUseISMinimumAsThreshold(boolean useISMinimumAsThreshold) {
    this.useISMinimumAsThreshold = useISMinimumAsThreshold;
  }

  public void setMode(THRESHOLD_MODE mode) {
    this.mode = mode;
  }
}
