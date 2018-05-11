package net.rs.lamsi.general.settings.image.visualisation;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import net.rs.lamsi.general.datamodel.image.interf.DataCollectable2D;
import net.rs.lamsi.general.framework.basics.multislider.JMultiRangeSlider;
import net.rs.lamsi.general.settings.Settings;


public class SettingsPaintScale extends Settings {
  // do not change the version!
  private static final long serialVersionUID = 1L;
  //
  // for creation of standards
  public static final int S_RAINBOW = 0, S_BLACK_RED_YE_W = 1, S_BLACK_BLUE_GR_W = 2, S_RED = 3,
      S_GREEN = 4, S_BLUE = 5, S_YELLOW = 6, S_PURPLE = 7, S_GREY = 8, S_RAINBOW_BRIGHT = 9,
      S_RAINBOW_INVERSE = 10, S_KARST_RAINBOW_INVERSE = 11, S_KARST_RAINBOW_INVERSE_NEW = 12,
      S_MAGENTA = 13;

  public enum ScaleType {
    GREYSCALE, HUESCALE, MONOCHROME, COLORLIST;
  }

  /**
   * mode for values like minimum and maximum
   */
  public enum ValueMode {
    ABSOLUTE, RELATIVE, PERCENTILE
  }

  private ScaleType scaleType = ScaleType.HUESCALE;
  private int levels = 256;
  private boolean isMonochrom = false, isGrey = false;
  private boolean isInverted, usesBAsMax, usesWAsMin;
  private boolean usesMinMax, usesMinAsInvisible, usesMaxAsInvisible;

  // min max values or filter (cut off filter)
  private boolean usesMinMaxFromSelection;
  // relative, absolute or percentile?
  private ValueMode modeMin, modeMax;
  // either a percentage (0-100) or absolute value
  private double min, max;
  // in percentage
  private float minFilter, maxFilter;

  // LOD monochrome
  private boolean isLODMonochrome = false;
  private double LOD = 0;

  // brightnessFactor for black and white as min / max
  // defines how fast brightness and saturation will be increased /decreased
  private float brightnessFactor;

  private Color minColor, maxColor;

  // list of colors
  private List<Color> list;

  // hue positions:
  private float[] hue = null, position = null;


  public SettingsPaintScale() {
    super("Paintscale", "/Settings/PaintScales/", "setPaintScale");
    resetAll();
  }

  public void setAll(ScaleType scaleType, int levels, boolean isMonochrom, boolean isInverted,
      boolean usesBAsMax, boolean usesWAsMin, boolean usesMinMax, boolean usesMinAsInvisible,
      boolean usesMaxAsInvisible, ValueMode modeMin, ValueMode modeMax, double min, double max,
      Color minColor, Color maxColor, float brightnessFactor, float minFilter, float maxFilter,
      boolean isGrey, boolean usesMinMaxFromSelection, boolean isLODMonochrome, double LOD,
      float[] hue, float[] position, List<Color> list) {
    this.scaleType = scaleType;
    this.levels = levels;
    this.isMonochrom = isMonochrom;
    this.isInverted = isInverted;
    this.usesWAsMin = usesWAsMin;
    this.usesBAsMax = usesBAsMax;
    this.usesMinMax = usesMinMax;
    this.usesMinAsInvisible = usesMinAsInvisible;
    this.usesMaxAsInvisible = usesMaxAsInvisible;
    this.min = min;
    this.max = max;
    this.minColor = minColor;
    this.maxColor = maxColor;
    this.brightnessFactor = brightnessFactor;
    this.minFilter = minFilter;
    this.maxFilter = maxFilter;
    this.modeMin = modeMin;
    this.modeMax = modeMax;
    this.isGrey = isGrey;
    this.usesMinMaxFromSelection = usesMinMaxFromSelection;
    this.isLODMonochrome = isLODMonochrome;
    this.LOD = LOD;
    this.hue = hue;
    this.position = position;
    this.list = list;
  }

  /**
   * this method should be used to receive the correct value
   * 
   * @param img
   * @return
   */
  public double getMinIAbs(DataCollectable2D img) {
    switch (modeMin) {
      case ABSOLUTE:
        return min;
      case RELATIVE:
        return img.getIAbs(min, usesMinMaxFromSelection);
      case PERCENTILE:
        return min;
    }
    return 0;
  }

  /**
   * this method should be used to receive the correct value
   * 
   * @param img
   * @return
   */
  public double getMinIRel(DataCollectable2D img) {
    switch (modeMin) {
      case ABSOLUTE:
        return img.getIPercentage(min, usesMinMaxFromSelection);
      case RELATIVE:
        return min;
      case PERCENTILE:
        return img.getIPercentage(min, usesMinMaxFromSelection);
    }
    return 0;
  }

  /**
   * this method should be used to receive the correct value
   * 
   * @param img
   * @return
   */
  public double getMaxIAbs(DataCollectable2D img) {
    switch (modeMax) {
      case ABSOLUTE:
        return max;
      case RELATIVE:
        return img.getIAbs(max, usesMinMaxFromSelection);
      case PERCENTILE:
        return max;
    }
    return 0;
  }

  /**
   * this method should be used to receive the correct value
   * 
   * @param img
   * @return
   */
  public double getMaxIRel(DataCollectable2D img) {
    switch (modeMax) {
      case ABSOLUTE:
        return img.getIPercentage(max, usesMinMaxFromSelection);
      case RELATIVE:
        return max;
      case PERCENTILE:
        return img.getIPercentage(max, usesMinMaxFromSelection);
    }
    return 0;
  }


  /**
   * use this to get the correct value
   * 
   * @param totalmin
   * @param totalmax
   * @return
   */
  public double getMinIAbs(double totalmin, double totalmax) {
    switch (modeMin) {
      case ABSOLUTE:
        return min;
      case RELATIVE:
        return (totalmax - totalmin) * this.min / 100.0;
      case PERCENTILE:
        return min;
    }
    return 0;
  }

  /**
   * use this to get the correct value
   * 
   * @param totalmin
   * @param totalmax
   * @return
   */
  public double getMaxIAbs(double totalmin, double totalmax) {
    switch (modeMax) {
      case ABSOLUTE:
        return max;
      case RELATIVE:
        return (totalmax - totalmin) * this.max / 100.0;
      case PERCENTILE:
        return max;
    }
    return 0;
  }


  public boolean isInIRange(DataCollectable2D img, double intensity) {
    return intensity >= getMinIAbs(img) && intensity <= getMaxIAbs(img);
  }

  @Override
  public void resetAll() {
    levels = 256;
    this.isMonochrom = false;
    this.isInverted = false;
    this.usesWAsMin = true;
    this.usesBAsMax = true;
    this.usesMinMax = true;
    this.usesMinAsInvisible = false;
    this.usesMaxAsInvisible = false;
    this.min = 0;
    this.max = 0;
    this.minColor = Color.CYAN;
    this.maxColor = Color.RED;
    brightnessFactor = 4;
    this.minFilter = 2.5f;
    this.maxFilter = 0.2f;
    this.modeMax = ValueMode.PERCENTILE;
    this.modeMin = ValueMode.PERCENTILE;
    usesMinMaxFromSelection = false;
    isLODMonochrome = false;
    scaleType = ScaleType.HUESCALE;
  }

  /**
   * S_KARST_RAINBOW_INVERSE_NEW is returned
   * 
   * @return
   */
  public static SettingsPaintScale createStandardSettings() {
    return createSettings(S_KARST_RAINBOW_INVERSE_NEW);
  }

  // ##############################################################################################
  // create standards
  public static SettingsPaintScale createSettings(int style) {
    SettingsPaintScale scale = new SettingsPaintScale();
    scale.setInverted(true);
    scale.setUsesBWAsMinMax(true, true);
    scale.setScaleType(ScaleType.HUESCALE);
    switch (style) {
      case S_RAINBOW:
        scale.setBrightnessFactor(5);
        scale.setMinColor(Color.BLUE);
        scale.setMaxColor(Color.RED);
        break;
      case S_KARST_RAINBOW_INVERSE_NEW:
        scale.setLODMonochrome(true);
        scale.setBrightnessFactor(25);
        scale.setMinColor(Color.RED);
        scale.setMaxColor(new Color(175, 0, 255));
        scale.setUsesBWAsMinMax(false, true);
        // hue and
        Color[] markers = JMultiRangeSlider.MARKERS;
        float[] hue = new float[markers.length - 1];
        for (int i = 1; i < markers.length; i++) {
          hue[i - 1] = JMultiRangeSlider.getHue(markers[i]);
        }
        // for blue, cyan, green, yellow, orange, red
        float[] position = new float[] {0.178f, 0.368f, 0.520f, 0.662f, 0.813f, 0.980f};
        scale.setHuePositions(position, hue);
        break;
      case S_KARST_RAINBOW_INVERSE:
        scale.setLODMonochrome(true);
        scale.setBrightnessFactor(18);
        scale.setMinColor(Color.RED);
        scale.setMaxColor(new Color(175, 0, 255));
        scale.setUsesBWAsMinMax(false, true);
        break;
      case S_RAINBOW_INVERSE:
        scale.setBrightnessFactor(6.5f);
        scale.setMinColor(Color.RED);
        scale.setMaxColor(Color.BLUE);
        break;
      case S_RAINBOW_BRIGHT:
        scale.setBrightnessFactor(5);
        scale.setMinColor(Color.CYAN);
        scale.setMaxColor(Color.RED);
        break;
      case S_BLACK_BLUE_GR_W:
        scale.setBrightnessFactor(5);
        scale.setMinColor(Color.GREEN);
        scale.setMaxColor(new Color(115, 0, 255));
        break;
      case S_BLACK_RED_YE_W:
        scale.setBrightnessFactor(5);
        scale.setMinColor(Color.YELLOW);
        scale.setMaxColor(Color.RED);
        break;
      case S_BLUE:
        scale.setScaleType(ScaleType.MONOCHROME);
        scale.setMinColor(Color.BLUE);
        scale.setMaxColor(Color.BLUE);
        break;
      case S_GREEN:
        scale.setScaleType(ScaleType.MONOCHROME);
        scale.setMinColor(Color.GREEN);
        scale.setMaxColor(Color.GREEN);
        break;
      case S_PURPLE:
        scale.setScaleType(ScaleType.MONOCHROME);
        scale.setMinColor(new Color(186, 0, 255));
        scale.setMaxColor(Color.RED);
        break;
      case S_RED:
        scale.setScaleType(ScaleType.MONOCHROME);
        scale.setMinColor(Color.RED);
        scale.setMaxColor(Color.RED);
        break;
      case S_YELLOW:
        scale.setScaleType(ScaleType.MONOCHROME);
        scale.setMinColor(Color.YELLOW);
        scale.setMaxColor(Color.YELLOW);
        break;
      case S_MAGENTA:
        scale.setScaleType(ScaleType.MONOCHROME);
        scale.setMinColor(Color.MAGENTA);
        scale.setMaxColor(Color.MAGENTA);
        break;
      case S_GREY:
        scale.setScaleType(ScaleType.GREYSCALE);
        break;
    }
    return scale;
  }
  // done with standards
  // ##############################################################################################

  // ##############################################################################################
  // import export
  @Override
  public void appendSettingsValuesToXML(Element elParent, Document doc) {
    toXML(elParent, doc, "levels", levels);
    toXML(elParent, doc, "isMonochrom", isMonochrom);
    toXML(elParent, doc, "isInverted", isInverted);
    toXML(elParent, doc, "usesWAsMin", usesWAsMin);
    toXML(elParent, doc, "usesBAsMax", usesBAsMax);
    toXML(elParent, doc, "usesMinMax", usesMinMax);
    toXML(elParent, doc, "usesMinAsInvisible", usesMinAsInvisible);
    toXML(elParent, doc, "min", min);
    toXML(elParent, doc, "max", max);
    toXML(elParent, doc, "minColor", minColor);
    toXML(elParent, doc, "maxColor", maxColor);
    toXML(elParent, doc, "brightnessFactor", brightnessFactor);
    toXML(elParent, doc, "minFilter", minFilter);
    toXML(elParent, doc, "maxFilter", maxFilter);
    toXML(elParent, doc, "modeMax", modeMax);
    toXML(elParent, doc, "modeMin", modeMin);
    toXML(elParent, doc, "isGrey", isGrey);
    toXML(elParent, doc, "usesMinMaxFromSelection", usesMinMaxFromSelection);
    toXML(elParent, doc, "isLODMonochrome", isLODMonochrome);
    toXML(elParent, doc, "LOD", LOD);
    toXML(elParent, doc, "usesMaxAsInvisible", usesMaxAsInvisible);
    toXMLArray(elParent, doc, "hue", hue);
    toXMLArray(elParent, doc, "position", position);
    toXML(elParent, doc, "scaleType", scaleType);
  }

  @Override
  public void loadValuesFromXML(Element el, Document doc) {
    NodeList list = el.getChildNodes();
    for (int i = 0; i < list.getLength(); i++) {
      if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
        Element nextElement = (Element) list.item(i);
        String paramName = nextElement.getNodeName();
        if (paramName.equals("levels"))
          levels = intFromXML(nextElement);
        else if (paramName.equals("isMonochrom"))
          isMonochrom = booleanFromXML(nextElement);
        else if (paramName.equals("scaleType"))
          scaleType = ScaleType.valueOf(nextElement.getTextContent());
        else if (paramName.equals("isInverted"))
          isInverted = booleanFromXML(nextElement);
        else if (paramName.equals("usesWAsMin"))
          usesWAsMin = booleanFromXML(nextElement);
        else if (paramName.equals("usesBAsMax"))
          usesBAsMax = booleanFromXML(nextElement);
        else if (paramName.equals("usesMinMax"))
          usesMinMax = booleanFromXML(nextElement);
        else if (paramName.equals("usesMinAsInvisible"))
          usesMinAsInvisible = booleanFromXML(nextElement);
        else if (paramName.equals("usesMaxAsInvisible"))
          usesMaxAsInvisible = booleanFromXML(nextElement);
        else if (paramName.equals("min"))
          min = doubleFromXML(nextElement);
        else if (paramName.equals("max"))
          max = doubleFromXML(nextElement);
        else if (paramName.equals("minColor"))
          minColor = colorFromXML(nextElement);
        else if (paramName.equals("maxColor"))
          maxColor = colorFromXML(nextElement);
        else if (paramName.equals("brightnessFactor"))
          brightnessFactor = floatFromXML(nextElement);
        else if (paramName.equals("minFilter"))
          minFilter = floatFromXML(nextElement);
        else if (paramName.equals("maxFilter"))
          maxFilter = floatFromXML(nextElement);
        else if (paramName.equals("modeMax"))
          modeMax = ValueMode.valueOf(nextElement.getTextContent());
        else if (paramName.equals("modeMin"))
          modeMin = ValueMode.valueOf(nextElement.getTextContent());
        else if (paramName.equals("isGrey"))
          isGrey = booleanFromXML(nextElement);
        else if (paramName.equals("usesMinMaxFromSelection"))
          usesMinMaxFromSelection = booleanFromXML(nextElement);
        else if (paramName.equals("isLODMonochrome"))
          isLODMonochrome = booleanFromXML(nextElement);
        else if (paramName.equals("LOD"))
          LOD = doubleFromXML(nextElement);
        else if (paramName.equals("hue"))
          hue = floatArrayFromXML(nextElement);
        else if (paramName.equals("position"))
          position = floatArrayFromXML(nextElement);
      }
    }
  }


  // ##############################################################################################
  // GETTER AND SETTER
  public int getLevels() {
    return levels;
  }

  public void setLevels(int levels) {
    this.levels = levels;
  }

  public boolean isMonochrom() {
    return scaleType.equals(ScaleType.MONOCHROME);
  }

  public boolean isInverted() {
    return isInverted;
  }

  public void setInverted(boolean isInverted) {
    this.isInverted = isInverted;
  }

  public boolean isUsesBAsMax() {
    return usesBAsMax;
  }

  public void setUsesBAsMax(boolean usesBAsMax) {
    this.usesBAsMax = usesBAsMax;
  }

  public boolean isUsesWAsMin() {
    return usesWAsMin;
  }

  public void setUsesWAsMin(boolean usesWAsMin) {
    this.usesWAsMin = usesWAsMin;
  }

  public void setUsesBWAsMinMax(boolean usesWAsMin, boolean usesBAsMax) {
    this.usesWAsMin = usesWAsMin;
    this.usesBAsMax = usesBAsMax;
  }

  public boolean isUsesMinMax() {
    return usesMinMax;
  }

  public void setUsesMinMax(boolean usesMinMax) {
    this.usesMinMax = usesMinMax;
  }

  /**
   * absolute, relative, percentile
   * 
   * @return
   */
  public double getMin() {
    return min;
  }


  /**
   * absolute, relative, percentile
   * 
   * @param min
   */
  public void setMin(double min) {
    this.min = min;
  }

  /**
   * absolute, relative, percentile
   * 
   * @return
   */
  public double getMax() {
    return max;
  }

  /**
   * absolute, relative, percentile
   * 
   * @param max
   */
  public void setMax(double max) {
    this.max = max;
  }

  public Color getMinColor() {
    return minColor;
  }

  public void setMinColor(Color minColor) {
    this.minColor = minColor;
  }

  public Color getMaxColor() {
    return maxColor;
  }

  public void setMaxColor(Color maxColor) {
    this.maxColor = maxColor;
  }

  public boolean isUsesMinAsInvisible() {
    return usesMinAsInvisible;
  }

  public void setUsesMinAsInvisible(boolean usesMinAsInvisible) {
    this.usesMinAsInvisible = usesMinAsInvisible;
  }

  public boolean isUsesMaxAsInvisible() {
    return usesMaxAsInvisible;
  }

  public void setUsesMaxAsInvisible(boolean usesMaxAsInvisible) {
    this.usesMaxAsInvisible = usesMaxAsInvisible;
  }

  public float getBrightnessFactor() {
    return brightnessFactor;
  }

  public void setBrightnessFactor(float bf) {
    brightnessFactor = bf;
  }


  public ValueMode getModeMin() {
    return modeMin;
  }

  public void setModeMin(ValueMode modeMin) {
    this.modeMin = modeMin;
  }

  public ValueMode getModeMax() {
    return modeMax;
  }

  public void setModeMax(ValueMode modeMax) {
    this.modeMax = modeMax;
  }

  public float getMinFilter() {
    return minFilter;
  }

  public void setMinFilter(float minFilter) {
    this.minFilter = minFilter;
  }

  public float getMaxFilter() {
    return maxFilter;
  }

  public void setMaxFilter(float maxFilter) {
    this.maxFilter = maxFilter;
  }

  public boolean isGrey() {
    return scaleType.equals(ScaleType.GREYSCALE);
  }

  public boolean isHueScale() {
    return scaleType.equals(ScaleType.HUESCALE);
  }

  public boolean isListScale() {
    return scaleType.equals(ScaleType.COLORLIST);
  }

  public boolean isUsesMinMaxFromSelection() {
    return usesMinMaxFromSelection;
  }

  public void setUsesMinMaxFromSelection(boolean usesMinMaxFromSelection) {
    this.usesMinMaxFromSelection = usesMinMaxFromSelection;
  }

  public boolean isLODMonochrome() {
    return isLODMonochrome;
  }

  public void setLODMonochrome(boolean isLODMonochrome) {
    this.isLODMonochrome = isLODMonochrome;
  }

  public double getLOD() {
    return LOD;
  }

  public void setLOD(double lOD) {
    LOD = lOD;
  }

  public void setHuePositions(float[] position, float[] hue) {
    this.position = position;
    this.hue = hue;
  }

  public float[] getHue() {
    return hue;
  }

  public float[] getPosition() {
    return position;
  }

  public void setHue(float[] hue) {
    this.hue = hue;
  }

  public void setPosition(float[] position) {
    this.position = position;
  }

  public ScaleType getScaleType() {
    return scaleType;
  }

  public void setScaleType(ScaleType scaleType) {
    this.scaleType = scaleType;
  }

  public List<Color> getColorList() {
    if (list == null || list.isEmpty())
      initColorList();
    return list;
  }

  public void setColorList(List<Color> list) {
    this.list = list;
  }

  public void setScaleTypeAndReset(ScaleType type) {
    if (!type.equals(this.scaleType)) {
      setScaleType(type);
    }
  }

  /**
   * 
   * @param i form 0-1 (0: first color; 1 last color)
   * @return Color.MAGENTA if color is not specified (empty list, index out of range)
   */
  public Color getListColor(double i) {
    // list is sorted the opposite way. 0 is last color
    if (list == null || list.isEmpty()) {
      initColorList();
    } else {
      int index = (int) ((list.size() - 1) * (1.0 - i));
      if (index < list.size() && index >= 0)
        return list.get(index);
    }
    return Color.MAGENTA;
  }

  /**
   * Init list with standard colors
   */
  private void initColorList() {
    Color[] c = new Color[] {Color.black, new Color(204, 0, 255), Color.BLUE, Color.cyan,
        Color.green, Color.YELLOW, Color.orange, Color.RED};
    list = new ArrayList<>();
    for (Color g : c)
      list.add(g);
  }

  /**
   * The lists size or 1 for an empty list, as this is not allowed.
   * 
   * @return Size will always be >=1
   */
  public int getListColorSize() {
    return list == null || list.isEmpty() ? 1 : list.size();
  }

}
