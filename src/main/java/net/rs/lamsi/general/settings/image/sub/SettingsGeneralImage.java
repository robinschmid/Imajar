package net.rs.lamsi.general.settings.image.sub;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.function.DoubleFunction;
import org.jfree.chart.plot.XYPlot;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.heatmap.Heatmap;
import net.rs.lamsi.general.heatmap.dataoperations.BilinearInterpolator;
import net.rs.lamsi.general.heatmap.dataoperations.DPReduction;
import net.rs.lamsi.general.heatmap.dataoperations.DPReduction.Mode;
import net.rs.lamsi.general.heatmap.dataoperations.FastGaussianBlur;
import net.rs.lamsi.general.heatmap.dataoperations.PostProcessingOp;


public class SettingsGeneralImage extends SettingsGeneralCollecable2D {

  public enum XUNIT {
    DP, s
  }

  // do not change the version!
  private static final long serialVersionUID = 1L;
  //

  public enum IMAGING_MODE {
    MODE_IMAGING_ONEWAY, MODE_IMAGING_TWOWAYS;

    public static IMAGING_MODE getMode(String value) {
      if (value.equals("0"))
        return MODE_IMAGING_ONEWAY;
      else if (value.equals("1"))
        return MODE_IMAGING_TWOWAYS;
      else
        return IMAGING_MODE.valueOf(value);
    }
  }

  /**
   * Transformation of intensity values
   *
   */
  public enum Transformation {
    NONE, INVERSE, SQRT, CUBEROOT, LOG10;

    @Override
    public String toString() {
      switch (this) {
        default:
          return super.toString();
      }
    }

    public DoubleFunction<Double> getFunction() {
      switch (this) {
        case SQRT:
          return val -> Math.sqrt(val);
        case CUBEROOT:
          return val -> Math.cbrt(val);
        case LOG10:
          return val -> val >= 0 ? Math.log10(val) : Double.NaN;
        case INVERSE:
          return val -> val != 0 ? 1.0 / val : Double.NaN;
        case NONE:
          return val -> val;
      }
      return val -> val;
    }

    public double apply(double val) {
      switch (this) {
        case SQRT:
          return Math.sqrt(val);
        case CUBEROOT:
          return Math.cbrt(val);
        case LOG10:
          return val >= 0 ? Math.log10(val) : Double.NaN;
        case INVERSE:
          return val != 0 ? 1.0 / val : Double.NaN;
        case NONE:
          return val;
      }
      return val;
    }
  }

  public static int MODE_SCANS_PER_LINE = 0, MODE_TIME_PER_LINE = 1;

  protected String filepath = "";

  protected float velocity, spotsize;
  protected double intensityFactor;
  // crop marks
  protected double x0 = 0, x1 = 0, y0 = 0, y1 = 0;
  // Imaging Mode
  protected SettingsGeneralRotation rotation;
  // rotate 0 90 180 270
  //
  protected double timePerLine = 1;
  protected int modeTimePerLine = MODE_TIME_PER_LINE;
  protected boolean isTriggered = false;

  protected boolean allFiles, isBinaryData = false;
  // Metadata
  protected String metadata = "";

  // interpolation and data reduction
  protected boolean useInterpolation;
  protected int interpolation;

  protected boolean useReduction;
  protected int reduction;
  protected Mode reductionMode;


  protected boolean useBlur;
  protected double blurRadius;

  protected boolean isCropDataToMin;

  protected Transformation trans = Transformation.NONE;


  public SettingsGeneralImage(String path, String fileEnding) {
    super("SettingsGeneralImage", path, fileEnding);
    rotation = new SettingsGeneralRotation();
    resetAll();
  }

  public SettingsGeneralImage() {
    this("/Settings/GeneralImage/", "setGIMG");
  }

  @Override
  public void resetAll() {
    super.resetAll();
    velocity = 50;
    spotsize = 50;
    allFiles = true;
    title = "";
    showShortTitle = true;
    isTriggered = false;
    timePerLine = 60;
    deleteCropMarks();

    if (rotation == null)
      rotation = new SettingsGeneralRotation();
    else
      rotation.resetAll();
    isBinaryData = false;
    interpolation = 1;
    useInterpolation = false;
    useBlur = false;
    blurRadius = 2;
    isCropDataToMin = true;
    intensityFactor = 1;
    trans = Transformation.NONE;
    reduction = 1;
    useReduction = false;
    reductionMode = Mode.AVG;
  }


  public void setAll(String title, String shortTitle, boolean useShortTitle, float xPos, float yPos,
      float velocity, float spotsize, IMAGING_MODE imagingMode, boolean reflectHoriz,
      boolean reflectVert, int rotationOfData, boolean isBinaryData, boolean useInterpolation,
      int interpolation, boolean useBlur, double blurRadius, boolean isCropDataToMin,
      boolean keepAspectRatio, boolean useReduction, int reduction, Mode redMode) {
    this.velocity = velocity;
    this.spotsize = spotsize;
    this.isBinaryData = isBinaryData;
    rotation.setAll(imagingMode, reflectHoriz, reflectVert, rotationOfData);
    this.interpolation = interpolation;
    this.useInterpolation = useInterpolation;
    this.blurRadius = blurRadius;
    this.useBlur = useBlur;
    this.isCropDataToMin = isCropDataToMin;
    this.reduction = reduction;
    this.useReduction = useReduction;
    this.reductionMode = redMode;

    super.setAll(title, shortTitle, useShortTitle, xPos, yPos, keepAspectRatio);
  }


  @Override
  public void applyToImage(Collectable2D c) throws Exception {
    SettingsGeneralImage old =
        (SettingsGeneralImage) c.getSettingsByClass(SettingsGeneralImage.class);

    super.applyToImage(c);

    if (old != null) {
      // new settings object
      SettingsGeneralImage sett =
          (SettingsGeneralImage) c.getSettingsByClass(SettingsGeneralImage.class);

      String path = old.getRAWFilepath();
      sett.setRAWFilepath(path);
    }
  }

  // ##########################################################
  // xml input/output
  @Override
  public void appendSettingsValuesToXML(Element elParent, Document doc) {
    super.appendSettingsValuesToXML(elParent, doc);
    toXML(elParent, doc, "velocity", velocity);
    toXML(elParent, doc, "spotsize", spotsize);
    toXML(elParent, doc, "allFiles", allFiles);
    toXML(elParent, doc, "isTriggered", isTriggered);
    toXML(elParent, doc, "timePerLine", timePerLine);
    toXML(elParent, doc, "isBinaryData", isBinaryData);
    toXML(elParent, doc, "filepath", filepath);
    toXML(elParent, doc, "interpolation", interpolation);
    toXML(elParent, doc, "useInterpolation", useInterpolation);
    toXML(elParent, doc, "useBlur", useBlur);
    toXML(elParent, doc, "blurRadius", blurRadius);
    toXML(elParent, doc, "isCropDataToMin", isCropDataToMin);
    toXML(elParent, doc, "intensityFactor", intensityFactor);
    toXML(elParent, doc, "trans", trans);

    toXML(elParent, doc, "reduction", reduction);
    toXML(elParent, doc, "useReduction", useReduction);
    toXML(elParent, doc, "reductionMode", reductionMode);

    rotation.appendSettingsToXML(elParent, doc);
  }

  @Override
  public void loadValuesFromXML(Element el, Document doc) {
    super.loadValuesFromXML(el, doc);
    NodeList list = el.getChildNodes();
    for (int i = 0; i < list.getLength(); i++) {
      if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
        Element nextElement = (Element) list.item(i);
        String paramName = nextElement.getNodeName();
        if (paramName.equals("allFiles"))
          allFiles = booleanFromXML(nextElement);
        else if (paramName.equals("velocity"))
          velocity = floatFromXML(nextElement);
        else if (paramName.equals("spotsize"))
          spotsize = floatFromXML(nextElement);
        else if (paramName.equals("intensityFactor"))
          intensityFactor = doubleFromXML(nextElement);
        else if (paramName.equals("isTriggered"))
          isTriggered = booleanFromXML(nextElement);
        else if (paramName.equals("timePerLine"))
          timePerLine = doubleFromXML(nextElement);
        else if (paramName.equals("isBinaryData"))
          isBinaryData = booleanFromXML(nextElement);
        else if (paramName.equals("reduction"))
          reduction = intFromXML(nextElement);
        else if (paramName.equals("useReduction"))
          useReduction = booleanFromXML(nextElement);
        else if (paramName.equals("reductionMode"))
          reductionMode = Mode.valueOf(nextElement.getTextContent());
        else if (paramName.equals("interpolation")) {
          // until version 3.39: Was double <0 - reduction
          Double r = doubleFromXML(nextElement);
          if (r == null)
            interpolation = 1;
          else if (r < 0) {
            reduction = (int) (1 / r);
            interpolation = 1;
          } else
            interpolation = r.intValue();
        } else if (paramName.equals("useInterpolation"))
          useInterpolation = booleanFromXML(nextElement);
        else if (paramName.equals("blurRadius"))
          blurRadius = doubleFromXML(nextElement);
        else if (paramName.equals("useBlur"))
          useBlur = booleanFromXML(nextElement);
        else if (paramName.equals("filepath"))
          filepath = nextElement.getTextContent();
        else if (paramName.equals("isCropDataToMin"))
          isCropDataToMin = booleanFromXML(nextElement);
        else if (paramName.equals("trans"))
          trans = Transformation.valueOf(nextElement.getTextContent());
        else {
          try {
            if (isSettingsNode(nextElement, rotation.getSuperClass()))
              rotation.loadValuesFromXML(nextElement, doc);
          } catch (Exception e) {
          }
        }
      }
    }
  }

  public boolean setTransform(Transformation t) {
    if (!t.equals(trans)) {
      trans = t;
      return true;
    } else
      return false;
  }

  public Transformation getTransform() {
    return trans;
  }

  public DoubleFunction<Double> getTransformFunction() {
    return trans.getFunction();
  }

  public float getVelocity() {
    return velocity;
  }

  public void setVelocity(float velocity) {
    this.velocity = velocity;
  }

  public float getSpotsize() {
    return spotsize;
  }

  public boolean isKeepAspectRatio() {
    return keepAspectRatio;
  }

  public void setKeepAspectRatio(boolean keepAspectRatio) {
    this.keepAspectRatio = keepAspectRatio;
  }

  public void setSpotsize(float spotsize) {
    this.spotsize = spotsize;
  }


  public boolean isAllFiles() {
    return allFiles;
  }

  public void setAllFiles(boolean allFiles) {
    this.allFiles = allFiles;
  }

  public String getRAWFilepath() {
    return filepath;
  }

  public String getRAWFolder() {
    File f = new File(filepath);
    return f.isDirectory() ? f.getAbsolutePath() : f.getParent();
  }

  public File getRAWFolderPath() {
    File f = new File(filepath);
    return f.isDirectory() ? f : f.getParentFile();
  }

  public String getRAWFolderName() {
    return getRAWFolderPath().getName();
  }

  public void setRAWFilepath(String filepath) {
    this.filepath = filepath;
  }

  @Override
  public String toListName() {
    return getTitle() + "; " + getRAWFileName() + "; " + getRAWFilepath();
  }

  public String getMetadata() {
    return metadata;
  }

  public void setMetadata(String metadata) {
    this.metadata = metadata;
  }

  public double getTimePerLine() {
    return timePerLine;
  }

  public void setTimePerLine(double timePerLine) {
    this.timePerLine = timePerLine;
  }

  public boolean isTriggered() {
    return isTriggered;
  }

  public void setTriggered(boolean isTriggert) {
    this.isTriggered = isTriggert;
  }


  public int getModeTimePerLine() {
    return modeTimePerLine;
  }

  public void setModeTimePerLine(int modeTimePerLine) {
    this.modeTimePerLine = modeTimePerLine;
  }

  public IMAGING_MODE getImagingMode() {
    return rotation.getImagingMode();
  }


  public void setImagingMode(IMAGING_MODE imagingMode) {
    rotation.setImagingMode(imagingMode);
  }


  public boolean isReflectHorizontal() {
    return rotation.isReflectHorizontal();
  }


  public void setReflectHorizontal(boolean reflectHorizontal) {
    rotation.setReflectHorizontal(reflectHorizontal);
  }


  public boolean isReflectVertical() {
    return rotation.isReflectVertical();
  }


  public void setReflectVertical(boolean reflectVertical) {
    rotation.setReflectVertical(reflectVertical);
  }


  public int getRotationOfData() {
    return rotation.getRotationOfData();
  }


  public void setRotationOfData(int rotationOfData) {
    rotation.setRotationOfData(rotationOfData);
  }

  public boolean isRotated() {
    return rotation.isRotated();
  }

  public void deleteCropMarks() {
    x0 = 0;
    x1 = 0;
    y0 = 0;
    y1 = 0;
  }

  public void applyCropMarks(double x0, double x1, double y0, double y1) {
    this.x0 = x0;
    this.x1 = x1;
    this.y0 = y0;
    this.y1 = y1;
  }

  public void applyCropMarksOnImage(Heatmap heat) {
    XYPlot plot = heat.getPlot();
    plot.getDomainAxis().setLowerBound(x0);
    plot.getDomainAxis().setUpperBound(x1);
    plot.getRangeAxis().setLowerBound(y0);
    plot.getRangeAxis().setUpperBound(y1);
  }


  /**
   * the raw file name of the raw path file.extension
   * 
   * @return
   */
  public String getRAWFileName() {
    return new File(getRAWFilepath()).getName();
  }


  public boolean isBinaryData() {
    return isBinaryData;
  }


  public void setBinaryData(boolean isBinaryData) {
    this.isBinaryData = isBinaryData;
  }

  public boolean isUseInterpolation() {
    return useInterpolation;
  }

  public int getInterpolation() {
    return interpolation;
  }

  public void setUseInterpolation(boolean useInterpolation) {
    this.useInterpolation = useInterpolation;
  }

  public void setInterpolation(int interpolation) {
    this.interpolation = interpolation;
  }

  public boolean isUseBlur() {
    return useBlur;
  }

  public double getBlurRadius() {
    return blurRadius;
  }

  public void setUseBlur(boolean useBlur) {
    this.useBlur = useBlur;
  }

  public void setBlurRadius(double blurRadius) {
    this.blurRadius = blurRadius;
  }

  public boolean isCropDataToMin() {
    return isCropDataToMin || isUseBlur();
  }


  public boolean isCropDataToMinGetRealValue() {
    return isCropDataToMin;
  }

  public void setCropDataToMin(boolean isCropDataToMin) {
    this.isCropDataToMin = isCropDataToMin;
  }

  public double getIntensityFactor() {
    return intensityFactor;
  }

  /**
   * 
   * @param intensityFactor
   * @return true if value has changed
   */
  public boolean setIntensityFactor(double intensityFactor) {
    if (Math.abs(intensityFactor - this.intensityFactor) > 0.0001) {
      this.intensityFactor = intensityFactor;
      return true;
    }
    return false;
  }

  public boolean isUseReduction() {
    return useReduction;
  }

  public int getReduction() {
    return reduction;
  }

  public Mode getReductionMode() {
    return reductionMode;
  }

  public void setReduction(int reduction) {
    this.reduction = reduction;
  }

  public void setReductionMode(Mode reductionMode) {
    this.reductionMode = reductionMode;
  }

  public void setUseReduction(boolean useReduction) {
    this.useReduction = useReduction;
  }

  /**
   * List of post processing operations, e.g., blur, interpolation, reduction ...
   * 
   * @return
   */
  public List<PostProcessingOp> getPostProcessingOp() {
    List<PostProcessingOp> op = new LinkedList<>();
    // xor
    boolean inter = isUseInterpolation() && getInterpolation() > 1;
    boolean red = isUseReduction() && getReduction() > 1;
    if (inter ^ red) {
      if (inter)
        op.add(new BilinearInterpolator(getInterpolation()));
      else
        op.add(new DPReduction(getReduction(), getReductionMode(), isRotated()));
    }
    // blur
    if (isUseBlur()) {
      op.add(new FastGaussianBlur(getBlurRadius()));
    }
    return op;
  }

}
