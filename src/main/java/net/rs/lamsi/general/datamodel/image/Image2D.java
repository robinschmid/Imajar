package net.rs.lamsi.general.datamodel.image;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.jfree.chart.renderer.PaintScale;
import net.rs.lamsi.general.datamodel.image.data.interf.ImageDataset;
import net.rs.lamsi.general.datamodel.image.data.interf.MDDataset;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.DatasetContinuousMD;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.DatasetLinesMD;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.ScanLineMD;
import net.rs.lamsi.general.datamodel.image.data.twodimensional.XYIDataMatrix;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.datamodel.image.interf.DataCollectable2D;
import net.rs.lamsi.general.datamodel.image.listener.RawDataChangedListener;
import net.rs.lamsi.general.heatmap.PaintScaleGenerator;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.general.settings.image.SettingsImage2D;
import net.rs.lamsi.general.settings.image.operations.SettingsImage2DOperations;
import net.rs.lamsi.general.settings.image.operations.quantifier.SettingsImage2DQuantifier;
import net.rs.lamsi.general.settings.image.operations.quantifier.SettingsImage2DQuantifierIS;
import net.rs.lamsi.general.settings.image.selection.SettingsSelections;
import net.rs.lamsi.general.settings.image.sub.SettingsGeneralImage;
import net.rs.lamsi.general.settings.image.sub.SettingsGeneralImage.IMAGING_MODE;
import net.rs.lamsi.general.settings.image.sub.SettingsGeneralImage.Transformation;
import net.rs.lamsi.general.settings.image.sub.SettingsImageContinousSplit;
import net.rs.lamsi.general.settings.image.visualisation.SettingsAlphaMap;
import net.rs.lamsi.general.settings.image.visualisation.SettingsAlphaMap.State;
import net.rs.lamsi.general.settings.image.visualisation.SettingsPaintScale;
import net.rs.lamsi.general.settings.importexport.SettingsImageDataImportTxt.ModeData;
import net.rs.lamsi.general.settings.interf.DatasetSettings;
import net.rs.lamsi.massimager.MyMZ.MZChromatogram;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow.LOG;
import net.rs.lamsi.utils.mywriterreader.BinaryWriterReader;

// XY raw data!
// have to be multiplied with velocity and spot size
public class Image2D extends DataCollectable2D<SettingsImage2D> implements Serializable {
  // do not change the version!
  private static final long serialVersionUID = 1L;


  // Parent image with master settings
  protected Image2D parent;


  // ############################################################
  // Settings
  // protected SettingsImage2D settings;

  // ############################################################
  // data
  protected ImageDataset data;

  // index of image in data set: (multidimensional data set)
  protected int index = 0;

  // store total dp count

  public Image2D() {
    super((new SettingsImage2D()));
  }

  public Image2D(SettingsImage2D settings) {
    super(settings);
  }

  public Image2D(ImageDataset data) {
    this();
    this.data = data;
  }

  public Image2D(ImageDataset data, int index) {
    this();
    this.data = data;
    this.index = index;
  }

  public Image2D(ImageDataset data, int index, SettingsImage2D sett) {
    this(sett);
    this.data = data;
    this.index = index;
  }

  public Image2D(ImageDataset data, SettingsImage2D sett) {
    this(data, 0, sett);
  }

  // #####################################################################################################
  // Generators STATIC
  // MZChrom Generation
  // MS image from raw data
  public static Image2D generateImage2DFrom(SettingsPaintScale settPaintScale,
      SettingsGeneralImage setImage, MZChromatogram[] mzchrom) {
    // TODO mehrere MZ machen
    // Alle Spec
    // Erst Messpunkteanzahl ausrechnen
    // lines listLines
    Vector<ScanLineMD> listLines = new Vector<ScanLineMD>(mzchrom.length);
    // Lines durchgehen
    for (int i = 0; i < mzchrom.length; i++) {
      int scanpoints = mzchrom[i].getItemCount();
      float startTime = mzchrom[i].getX(0).floatValue();
      float[] x = new float[scanpoints];
      Double[] z = new Double[scanpoints];
      // x, i datapoints
      for (int d = 0; d < scanpoints; d++) {
        x[d] = mzchrom[i].getX(d).floatValue() - startTime;
        z[d] = mzchrom[i].getY(d).doubleValue();
      }

      // new line
      listLines.add(new ScanLineMD(x, z));
    }
    // Image erstellen
    return new Image2D(new DatasetLinesMD(listLines), 0,
        new SettingsImage2D(settPaintScale, setImage));
  }

  // Discontinous MS-Image
  // Generate Heatmap with Continous Data WIDHTOUT Triggerin every Line
  public static Image2D generateImage2DFromCon(SettingsPaintScale settPaintScale,
      SettingsGeneralImage setImage, SettingsImageContinousSplit settSplit,
      MZChromatogram mzchrom) {
    // startTime
    float startTime = mzchrom.getX(0).floatValue();
    // data points
    int scanpoints = mzchrom.getItemCount();
    float[] x = new float[scanpoints];
    Double[] z = new Double[scanpoints];
    // Daten eintragen und zwischen speichern
    for (int i = 0; i < scanpoints; i++) {
      x[i] = mzchrom.getX(i).floatValue() - startTime;
      z[i] = mzchrom.getY(i).doubleValue();
    }
    ScanLineMD line = new ScanLineMD(x, z);

    // generate Image
    return new Image2D(new DatasetContinuousMD(line, settSplit), 0,
        new SettingsImage2D(settPaintScale, setImage));
  }


  /**
   * y values in respect to rotation reflection imaging mode
   * 
   * @param raw
   * @param l
   * @param dp
   * @return
   */
  @Override
  public float getY(boolean raw, int l, int dp) {
    return getY(raw, l, dp, settings.getSettImage().getImagingMode(),
        settings.getSettImage().getRotationOfData(), settings.getSettImage().isReflectHorizontal(),
        settings.getSettImage().isReflectVertical());
  }

  /**
   * the processed/raw y with no respect to rotation
   * 
   * @param raw
   * @param l
   * @param dp
   * @return
   */
  public float getYRaw(boolean raw, int l) {
    return l * yFactor(raw);
  }

  /**
   * intensity values in respect to rotation reflection imaging mode
   * 
   * @param raw
   * @param l
   * @param dp
   * @return
   */
  @Override
  public double getI(boolean raw, int l, int dp) {
    return getI(raw, true, l, dp);
  }

  /**
   * intensity values in respect to rotation reflection imaging mode
   * 
   * @param raw
   * @param useSettings use rotation and reflection...
   * @param l
   * @param dp
   * @return
   */
  public double getI(boolean raw, boolean useSettings, int l, int dp) {
    // check for update in parent i processing
    checkForUpdateInParentIProcessing();

    SettingsGeneralImage sett = settings.getSettImage();
    // get raw i
    double i = !useSettings ? getIRaw(l, dp)
        : getI(l, dp, settings.getSettImage().getImagingMode(), sett.getRotationOfData(),
            sett.isReflectHorizontal(), sett.isReflectVertical());

    // TODO process intensity
    if (raw || Double.isNaN(i))
      return i;
    else {
      // intensity factor

      i *= sett.getIntensityFactor();

      // transformation
      if (!sett.getTransform().equals(Transformation.NONE)) {
        i = sett.getTransform().apply(i);
      }

      // subtract blank
      SettingsSelections sel = settings.getSettSelections();
      if (sel != null) {
        i = sel.calcIntensity(this, l, dp, i);
      }

      // apply IS
      SettingsImage2DQuantifierIS isq = settings.getInternalQuantifierIS();
      if (isq != null) {
        i = isq.calcIntensity(this, l, dp, i);
      }
      // quantify
      SettingsImage2DQuantifier q = settings.getQuantifier();
      if (q != null && q.isActive()) {
        i = q.calcIntensity(this, l, dp, i);
      }
      return i;
    }
  }

  /**
   * intensity values in respect to rotation reflection imaging mode
   * 
   * @param raw
   * @param l
   * @param dp
   * @return
   */
  public double getIRaw(boolean raw, int l, int dp) {
    // check for update in parent i processing
    checkForUpdateInParentIProcessing();

    // get raw i
    double i = getIRaw(l, dp);

    // TODO process intensity
    if (raw || Double.isNaN(i))
      return i;
    else {
      // TODO dead end / replace!
      // subtract blank and apply IS
      if (settings.getOperations() != null) {
        i = settings.getOperations().calcIntensity(this, l, dp, i);
      }
      // quantify
      if (settings.getQuantifier() != null && settings.getQuantifier().isActive()) {
        i = settings.getQuantifier().calcIntensity(this, l, dp, i);
      }
      return i;
    }
  }


  /**
   * The processed/raw intensity. blank reduced, internal standard normalization and quantification
   * 
   * @param l
   * @param dp
   * @param blank
   * @param IS
   * @param quantify
   * @return
   */
  public double getIRaw(int l, int dp, boolean blank, boolean IS, boolean quantify) {
    // check for update in parent i processing
    checkForUpdateInParentIProcessing();
    // get raw i
    double i = getI(l, dp, settings.getSettImage().getImagingMode(),
        settings.getSettImage().getRotationOfData(), settings.getSettImage().isReflectHorizontal(),
        settings.getSettImage().isReflectVertical());

    // TODO dead end / replace!
    // subtract blank and apply IS
    if (settings.getOperations() != null) {
      i = settings.getOperations().calcIntensity(this, l, dp, i, blank, IS);
    }
    // quantify
    if (settings.getQuantifier() != null && quantify) {
      boolean tmp = settings.getQuantifier().isActive();
      settings.getQuantifier().setActive(true);
      i = settings.getQuantifier().calcIntensity(this, l, dp, i);
      settings.getQuantifier().setActive(tmp);
    }
    return i;
  }

  /**
   * The processed/raw intensity. with no respect to rotation blank reduced, internal standard
   * normalization and quantification
   * 
   * @param l
   * @param dp
   * @return the intensity or Double.NaN if out of data space
   */
  public double getIRaw(int l, int dp) {
    if (l < 0 || dp < 0 || l >= data.getLinesCount() || dp >= data.getLineLength(l))
      return Double.NaN;
    return data.getI(index, l, dp);
  }

  /**
   * x values with respect to ration
   * 
   * @param l
   * @param dp
   * @return
   */
  @Override
  public float getX(boolean raw, int l, int dp) {
    return getX(raw, l, dp, settings.getSettImage().getImagingMode(),
        settings.getSettImage().getRotationOfData(), settings.getSettImage().isReflectHorizontal(),
        settings.getSettImage().isReflectVertical());
  }

  /**
   * The processed/raw x with no respect to rotation
   * 
   * @param l
   * @param dp
   * @return
   */
  public float getXRaw(boolean raw, int l, int dp) {
    int line = l < data.getLinesCount() ? l : data.getLinesCount() - 1;
    if (dp < data.getLineLength(line))
      return data.getX(line, dp) * (raw ? 1 : settings.getSettImage().getVelocity());
    // end of data x (right edge of last datapoint)
    else if (dp == data.getLineLength(line))
      return data.getRightEdgeX(l) * (raw ? 1 : settings.getSettImage().getVelocity());
    else {
      // for the maximum processed line length
      int overMax = (data.getLineLength(line) - dp + 1);
      ImageEditorWindow.log("ask for a dp>then line in getXProcessed", LOG.DEBUG);
      // return (((data.getX(line, data.getLineLength(line)-1) + getLine(line).getWidthDP()*overMax)
      // * settImage.getVelocity()));
      // tmp change
      return (((data.getX(line, data.getLineLength(line) - 1))
          * (raw ? 1 : settings.getSettImage().getVelocity())));
    }
  }

  /**
   * handles rotation, imaging mode and reflection for intensities return Double.NaN for out of
   * specified data
   * 
   * @param raw
   * @param l
   * @param dp
   * @param imgMode
   * @param rotation
   * @param reflectH
   * @param reflectV
   * @return if there is no value at l, dp then return Double.NaN else return value
   */
  private double getI(int l, int dp, IMAGING_MODE imgMode, int rotation, boolean reflectH,
      boolean reflectV) {
    // the usual case
    if (imgMode == IMAGING_MODE.MODE_IMAGING_ONEWAY && rotation == 0 && reflectH == false
        && reflectV == false) {
      if (dp >= 0 && dp < data.getLineLength(l))
        return getIRaw(l, dp);
      else
        return Double.NaN;
    } else if (rotation == 180) {
      // invert reflection
      return getI(l, dp, imgMode, 0, !reflectH, !reflectV);
    } else {
      // first rotation:
      // 90°
      if (rotation == 90) {
        // rotate back to 0
        // dp -> l
        // line length(dp) -1 -l -> dp
        return getI(dp, data.getMaxDP() - 1 - l, imgMode, 0, reflectH, reflectV);
      }
      // -90° = 270°
      else if (rotation == 270 || rotation == -90) {
        // l -> dp
        // data.linescount -1 -dp
        return getI(data.getLinesCount() - 1 - dp, l, imgMode, 0, reflectH, reflectV);
      } else {
        int cx = dp;
        int cy = l;
        // THEN! reflect horizontally
        if (reflectH)
          cy = data.getLinesCount() - 1 - cy;
        // reflect vertically xor
        // meander imaging (two ways)
        if (reflectV ^ (imgMode == IMAGING_MODE.MODE_IMAGING_TWOWAYS && cy % 2 != 0))
          cx = data.getMaxDP() - 1 - cx;

        // return only if x is in range
        if (cx >= 0 && cx < data.getLineLength(cy))
          return getIRaw(cy, cx);
        else
          return Double.NaN;
      }
    }
  }

  /**
   * handles rotation, imgaging mode reflection for x values
   * 
   * @param raw
   * @param l
   * @param dp
   * @param imgMode
   * @param rotation
   * @param reflectH
   * @param reflectV
   * @return returns the value or Float.NaN if there is no value
   */
  public float getX(boolean raw, int l, int dp, IMAGING_MODE imgMode, int rotation,
      boolean reflectH, boolean reflectV) {
    // the usual case
    if (rotation == 90 || rotation == 270 || rotation == -90) {
      // return line height of line: dp
      return getY(raw, dp, l, imgMode, 0, reflectH, reflectV);
    } else if (rotation == 180) {
      // invert reflection
      return getX(raw, l, dp, imgMode, 0, !reflectH, !reflectV);
    } else {
      int cx = dp;
      int cy = l;
      // THEN! reflect horizontally
      if (reflectH)
        cy = data.getLinesCount() - 1 - cy;
      // reflect vertically xor
      // meander imaging (two ways)
      if (reflectV ^ (imgMode == IMAGING_MODE.MODE_IMAGING_TWOWAYS && cy % 2 != 0))
        cx = data.getMaxDP() - 1 - cx;

      // only if cx is in range
      if (cx >= 0 && cx < data.getLineLength(cy)) {
        float value = getXRaw(raw, cy, cx);
        // xor ^
        // imagecreation mode: if twoways -> first reflect every 2. line (x values)
        if (reflectV ^ (imgMode == IMAGING_MODE.MODE_IMAGING_TWOWAYS && cy % 2 != 0)) {
          // reflect x
          float width = getMaxXRaw(raw);
          value += distPercent(width, value) * width;
        }
        return value;
      } else
        return Float.NaN;
    }
  }

  /**
   * handles rotation, imgaging mode reflection for y values
   * 
   * @param raw
   * @param l
   * @param dp
   * @param imgMode
   * @param rotation
   * @param reflectH
   * @param reflectV
   * @return
   */
  public float getY(boolean raw, int l, int dp, IMAGING_MODE imgMode, int rotation,
      boolean reflectH, boolean reflectV) {
    // the usual case
    if (rotation == 0 || rotation == 180 || rotation == 360) {
      return getYRaw(raw, l);
    } else {
      // 90°
      if (rotation == 90) {
        // rotate back to 0
        // dp -> l
        // line length(dp) -1 -l -> dp
        // return getX(raw, dp, data.getMaxDP()-1-l, imgMode, 0, reflectH,reflectV);
        return getX(raw, dp, l, imgMode, 0, reflectH, !reflectV);
      }
      // -90° = 270°
      else {
        // l -> dp
        // data.linescount -1 -dp
        return getX(raw, data.getLinesCount() - 1 - dp, l, imgMode, 0, reflectH, reflectV);
      }
    }
  }


  /**
   * minimum line length in regards to rotation columns of the image
   * 
   * @return
   */
  @Override
  public int getMinLineLength() {
    if (!isRotated()) {
      return data.getMinDP();
    } else {
      return data.getLinesCount();
    }
  }

  /**
   * maximum line length in regards to rotation columns (dp) of the image
   * 
   * @return
   */
  @Override
  public int getMaxLineLength() {
    if (!isRotated()) {
      return data.getMaxDP();
    } else {
      return data.getLinesCount();
    }
  }

  /**
   * minimum lines count in regards to rotation rows of the image
   * 
   * @return
   */
  @Override
  public int getMinLinesCount() {
    if (!isRotated()) {
      return data.getLinesCount();
    } else {
      return data.getMinDP();
    }
  }

  /**
   * maximum lines count in regards to rotation rows of the image
   * 
   * @return
   */
  @Override
  public int getMaxLinesCount() {
    if (!isRotated()) {
      return data.getLinesCount();
    } else {
      return data.getMaxDP();
    }
  }

  /**
   * rotation == 0 | 180 | 360
   * 
   * @return
   */
  public boolean isRotated() {
    int rot = settings.getSettImage().getRotationOfData();
    return !(rot == 0 || rot == 180 || rot == 360);
  }

  /**
   * the length of lines in respect to reflection and rotation
   * 
   * @return
   */
  @Override
  public int getLineLength(int l) {
    return getLineLength(l, settings.getSettImage().getRotationOfData(),
        settings.getSettImage().isReflectHorizontal());
  }

  /**
   * the length of lines in respect to reflection and rotation
   * 
   * @param l
   * @param rotation 0-270
   * @param reflectH
   * @return
   */
  private int getLineLength(int l, int rotation, boolean reflectH) {
    if ((rotation == 0 && !reflectH) || (reflectH && rotation == 180))
      return data.getLineLength(l);
    else if (rotation == 90 || rotation == 270 || rotation == -90)
      return data.getLinesCount();
    // xor
    else if (rotation == 180 ^ reflectH)
      return data.getLineLength(data.getLinesCount() - 1 - l);
    else // should not end here
      return -1;
  }

  /**
   * the count of lines in respect to reflection and rotation
   * 
   * @return
   */
  public int getLineCount(int dp) {
    return getLineCount(dp, settings.getSettImage().getRotationOfData(),
        settings.getSettImage().isReflectHorizontal());
  }

  /**
   * the count of lines in respect to reflection and rotation
   * 
   * @param dp
   * @param rotation 0-270
   * @param reflectV reflect lines
   * @return
   */
  private int getLineCount(int dp, int rotation, boolean reflectH) {
    if (rotation == 0 || rotation == 180)
      return data.getLinesCount();
    else if ((rotation == 90 && !reflectH) || ((rotation == 270 || rotation == -90) && reflectH))
      return data.getLineLength(dp);
    else if ((rotation == 90 && reflectH) || ((rotation == 270 || rotation == -90) && !reflectH))
      return data.getLineLength(data.getLinesCount() - 1 - dp);
    else // should not end here
      return -1;
  }


  // #########################################################################################################
  // TO ARRAY LISTS

  /**
   * distace percentage of x to the middle of width
   * 
   * @param width
   * @param x
   * @return
   */
  private double distPercent(double width, double x) {
    return (width / 2 - x) / width * 2;
  }

  /**
   * to xyi array in regards to the rotation, reflection and imaging mode
   * 
   * @param raw
   * @param setImg
   * @return
   */
  public double[][] toXYIArray(boolean raw, boolean useSettings) {
    if (useSettings) {
      SettingsGeneralImage s = settings.getSettImage();
      int diff = data.getMaxDP() - data.getMinDP();
      // crop?
      if (s.isCropDataToMin() && diff != 0) {
        return toXYIDataMatrix(raw, useSettings).toLinearArray();
      } else {
        // no crop
        return toXYIArray(raw, s.getImagingMode(), s.getRotationOfData(), s.isReflectHorizontal(),
            s.isReflectVertical());
      }
    } else
      return toXYIArrayNoRot();
  }

  /**
   * returns [rows][columns (3 : xyz)] of xyz data processed or not processed
   * 
   * @param sett
   * @return
   */
  public Object[][] toXYIMatrix(boolean raw, boolean useSettings) {
    double[][] data = toXYIArray(raw, useSettings);

    Object[][] real = new Object[data[2].length][3];
    for (int i = 0; i < real.length; i++) {
      real[i][0] = data[0][i];
      real[i][1] = data[1][i];
      real[i][2] = data[2][i];
    }
    return real;
  }

  /**
   * to xyi array in regards to the rotation, reflection and imaging mode
   * 
   * @param raw
   * @param imgMode
   * @param rotation
   * @param reflectH
   * @param reflectV
   * @return
   */
  public double[][] toXYIArray(boolean raw, IMAGING_MODE imgMode, int rotation, boolean reflectH,
      boolean reflectV) {
    // count scan points
    int scanpoints = getTotalDPCount();
    // Datenerstellen
    double[] x = new double[scanpoints];
    double[] y = new double[scanpoints];
    double[] z = new double[scanpoints];
    //
    int lines = getMaxLinesCount();
    int maxdp = getMaxLineLength();
    int currentdp = 0;

    // uses rotation
    boolean usesRot = !(imgMode == IMAGING_MODE.MODE_IMAGING_ONEWAY && rotation == 0
        && reflectH == false && reflectV == false);

    for (int iy = 0; iy < lines; iy++) {
      for (int ix = 0; ix < maxdp; ix++) {
        // x = time; NOT distance;
        // iy,ix are out of range? --> x is -1
        double tmp = getI(raw, usesRot, iy, ix);
        if (!Double.isNaN(tmp)) {
          z[currentdp] = tmp;
          if (usesRot) {
            y[currentdp] = Double.valueOf(String.valueOf((getY(raw, iy, ix))));
            x[currentdp] = Double.valueOf(String.valueOf(getX(raw, iy, ix)));
          } else {
            y[currentdp] = Double.valueOf(String.valueOf((getYRaw(raw, iy))));
            x[currentdp] = Double.valueOf(String.valueOf(getXRaw(raw, iy, ix)));
          }
          currentdp++;
        }
      }
    }
    //
    return new double[][] {x, y, z};
  }


  /**
   * xyi array without rotation, reflection, imaging mode
   * 
   * @param raw
   * @return
   */
  private double[][] toXYIArrayNoRot() {
    // Erst Messpunkteanzahl ausrechnen
    int scanpoints = data.getTotalDPCount();
    // Datenerstellen
    double[] x = new double[scanpoints];
    double[] y = new double[scanpoints];
    double[] z = new double[scanpoints];
    //
    int currentdp = 0;
    //
    for (int iy = 0; iy < data.getLinesCount(); iy++) {
      //
      for (int ix = 0; ix < data.getLineLength(iy); ix++) {
        // x = time; NOT distance;
        x[currentdp] = Double.valueOf(String.valueOf(getXRaw(true, iy, ix)));
        y[currentdp] = Double.valueOf(String.valueOf((getYRaw(true, iy))));
        z[currentdp] = getIRaw(iy, ix);
        currentdp++;
      }
    }
    //
    return new double[][] {x, y, z};
  }

  // ###############################################################################################
  /**
   * Creates an array of x and intensity data (raw or processed) for data export
   * 
   * @param sett
   * @param raw
   * @param useSettings rotation, reflection, imaging mode
   * @return data [rows][columns]
   */
  public Object[][] toDataArray(ModeData mode, boolean raw, boolean useSettings) {
    // export with rotation etc
    if (useSettings) {
      // columns in the data sheet are lines / x values here
      // time only once?
      int cols = getMaxLinesCount();
      // rows in data sheet = data points here
      int rows = getMaxLineLength();

      // more columns for x values?
      if (mode.equals(ModeData.XYYY))
        cols += 1;
      else if (mode.equals(ModeData.XYXY_ALTERN))
        cols += 2;

      Object[][] dataExp = new Object[rows][cols];
      int l = 0;
      for (int c = 0; c < cols; c++) {
        if ((mode.equals(ModeData.XYYY) && c == 0)
            || (mode.equals(ModeData.XYXY_ALTERN) && c % 2 == 0)) {
          // write X
          for (int r = 0; r < rows; r++) {
            dataExp[r][c] = isDP(l, r) ? getX(raw, l, r) : "";
          }
        } else {
          // write intensity
          for (int r = 0; r < rows; r++) {
            // only if not null
            double tmp = getI(raw, l, r);
            dataExp[r][c] = !Double.isNaN(tmp) ? tmp : "";
          }
          // increment l line
          l++;
        }
      }
      return dataExp;
    }
    // export without rotation etc
    else {
      // columns in the data sheet are lines / x values here
      // time only once?
      int cols = mode.equals(ModeData.XYYY) ? data.getLinesCount() + 1 : data.getLinesCount() * 2;
      if (mode.equals(ModeData.ONLY_Y))
        cols = data.getLinesCount();
      // rows in data sheet = data points here
      int rows = data.getMaxDP();
      Object[][] dataExp = new Object[rows][cols];
      int l = 0;
      for (int c = 0; c < cols; c++) {
        if ((mode.equals(ModeData.XYYY) && c == 0)
            || (mode.equals(ModeData.XYXY_ALTERN) && c % 2 == 0)) {
          // write X
          for (int r = 0; r < rows; r++) {
            dataExp[r][c] = r < data.getLineLength(l) ? getXRaw(raw, l, r) : "";
          }
        } else {
          // write intensity
          for (int r = 0; r < rows; r++) {
            // only if not null
            dataExp[r][c] = r < data.getLineLength(l) ? getIRaw(l, r) : "";
          }
          // increment l line
          l++;
        }
      }
      return dataExp;
    }
  }



  /**
   * generate XYI matrices [line][dp]
   * 
   * @param raw
   * @param useSettings rotation and imaging mode
   * @return
   */
  @Override
  public XYIDataMatrix toXYIDataMatrix(boolean raw, boolean useSettings) {
    SettingsGeneralImage sett = settings.getSettImage();
    return toXYIDataMatrix(raw, useSettings, sett.isCropDataToMin());
  }

  public XYIDataMatrix toXYIDataMatrix(boolean raw, boolean useSettings, boolean cropDataToMin) {
    if (useSettings) {
      SettingsGeneralImage sett = settings.getSettImage();
      int diff = data.getMaxDP() - data.getMinDP();
      // apply crop?
      if (cropDataToMin && diff != 0) {
        int rot = sett.getRotationOfData();
        boolean reflectH = sett.isReflectHorizontal();
        // exclude last line? if shorter than 0.8 of avg

        // get full matrix
        final int cols = getMaxLinesCount();
        final int rows = getMaxLineLength();

        Double[][] z = new Double[cols][rows];
        Float[][] x = new Float[cols][rows], y = new Float[cols][rows];

        // track first dp / first line / last dp/line index
        int firstDP = 0, lastDP = rows;

        // c for lines
        for (int c = 0; c < cols; c++) {
          int l = c;
          // increment l
          for (int r = 0; r < rows; r++) {
            int dp = r;
            // only if not null: write Intensity
            double tmp = getI(raw, l, dp);
            z[c][r] = tmp;
            // NaN?
            if (!Double.isNaN(tmp)) {
              x[c][r] = getX(raw, l, dp);
              y[c][r] = getY(raw, l, dp);
            } else {
              x[c][r] = Float.NaN;
              y[c][r] = Float.NaN;

              // set last and first dp
              if (r == firstDP)
                firstDP++;
              else if (r > firstDP && r < lastDP)
                lastDP = r;
            }
          }
        }

        int firstLine = 0;
        int lastLine = z.length;
        // find first and last line
        for (int l = 0; l < z.length && lastLine == z.length; l++) {
          for (int dp = firstDP; dp < lastDP; dp++) {
            // one NaN in line?
            if (Double.isNaN(z[l][dp])) {
              if (l == firstLine)
                firstLine++;
              else if (l > firstLine && l < lastLine)
                lastLine = l;
              // end line
              break;
            }
          }
        }

        // new size: between first and last dp/line
        int w = lastDP - firstDP;
        int h = lastLine - firstLine;

        // shift by start x and y
        float startx = x[firstLine][firstDP], starty = y[firstLine][firstDP];

        double[][] newz = new double[h][w];
        float[][] newx = new float[h][w], newy = new float[h][w];

        for (int l = 0; l < h; l++) {
          for (int dp = 0; dp < w; dp++) {
            newz[l][dp] = z[l + firstLine][dp + firstDP];
            newx[l][dp] = x[l + firstLine][dp + firstDP] - startx;
            newy[l][dp] = y[l + firstLine][dp + firstDP] - starty;
          }
        }
        // return data
        return new XYIDataMatrix(newx, newy, newz);
      } else {
        // no cropping
        int cols = getMaxLinesCount();
        int rows = getMaxLineLength();

        double[][] z = new double[cols][rows];
        float[][] x = new float[cols][rows], y = new float[cols][rows];

        // c for lines
        for (int c = 0; c < cols; c++) {
          // increment l
          for (int r = 0; r < rows; r++) {
            // only if not null: write Intensity
            double tmp = getI(raw, c, r);
            z[c][r] = tmp;
            // NaN?
            x[c][r] = !Double.isNaN(tmp) ? getX(raw, c, r) : Float.NaN;
            y[c][r] = !Double.isNaN(tmp) ? getY(raw, c, r) : Float.NaN;
          }
        }
        return new XYIDataMatrix(x, y, z);
      }
    } else {
      int cols = data.getLinesCount();

      float[][] x = new float[cols][], y = new float[cols][];
      double[][] z = new double[cols][];

      for (int c = 0; c < cols; c++) {
        int length = data.getLineLength(c);
        x[c] = new float[length];
        y[c] = new float[length];
        z[c] = new double[length];
        // increment l
        for (int r = 0; r < length; r++) {
          // only if not null: write Intensity
          z[c][r] = getIRaw(c, r);
          // NaN?
          x[c][r] = getXRaw(raw, c, r);
          y[c][r] = getYRaw(raw, c);
        }
      }
      return new XYIDataMatrix(x, y, z);
    }
  }

  /**
   * 
   * @param scale
   * @return x matrix raw or processed. null if there are no x values
   */
  public Object[][] toXMatrix(boolean raw, boolean useSettings) {
    // export with rotation etc
    if (useSettings) {
      int cols = getMaxLinesCount();
      // rows in data sheet = data points here
      int rows = getMaxLineLength();

      Object[][] dataExp = new Object[rows][cols];
      for (int c = 0; c < cols; c++) {
        for (int r = 0; r < rows; r++) {
          // only if not null: write Intensity
          dataExp[r][c] = isDP(c, r) ? getX(raw, c, r) : "";
        }
      }
      return dataExp;
    } else {
      int cols = data.getLinesCount();
      int rows = data.getMaxDP();
      Object[][] dataExp = new Object[rows][cols];
      for (int c = 0; c < cols; c++) {
        int length = data.getLineLength(c);
        for (int r = 0; r < rows; r++) {
          // only if not null: write Intensity
          dataExp[r][c] = r < length ? getXRaw(raw, c, r) : "";
        }
      }
      return dataExp;
    }
  }

  /**
   * always with settings
   * 
   * @param scale
   * @param sep separation chars
   * @return xmatrix raw by a factor as CSV string
   */
  public String toXCSV(boolean raw, String sep, boolean useSettings) {
    // no x data --> null
    if (MDDataset.class.isInstance(data)) {
      if (((MDDataset) data).hasXData()) {
        // has only one x line
        if (DatasetLinesMD.class.isInstance(data) && ((DatasetLinesMD) data).hasOnlyOneXColumn())
          return toXCSV(raw, sep, 1, useSettings);
        else
          return toXCSV(raw, sep, data.getLinesCount(), useSettings);
      } else
        return null;
    } else {
      return toXCSV(raw, sep, data.getLinesCount(), useSettings);
    }
  }

  /**
   * 
   * @param scale
   * @param sep separation chars
   * @return xmatrix raw by a factor as CSV string
   */
  private String toXCSV(boolean raw, String sep, int lines, boolean useSettings) {
    // no x data --> null
    StringBuilder builder = new StringBuilder();
    int cols = lines;
    int rows = data.getMaxDP();

    // if lines>1 --> otherwise it is x csv
    if (useSettings) {
      // rotation
      int rotation = settings.getSettImage().getRotationOfData();
      if (rotation == 90 || rotation == 270 || rotation == -90) {
        if (cols != 1)
          cols = rows;

        rows = data.getLinesCount();
      }
    }
    // increment dp
    for (int r = 0; r < rows; r++) {
      // increment l
      for (int c = 0; c < cols; c++) {
        // only if not null: write Intensity
        if (useSettings) {
          builder.append(isDP(c, r) ? getX(raw, c, r) : "");
        } else
          builder.append(r < data.getLineLength(c) ? getXRaw(raw, c, r) : "");
        if (c < cols - 1)
          builder.append(sep);
      }
      if (r < rows - 1)
        builder.append("\n");
    }
    return builder.toString();
  }

  /**
   * 
   * @param scale
   * @param sep separation chars
   * @return ymatrix raw by a factor as CSV string
   */
  public String toICSV(boolean raw, String sep, boolean useSettings) {
    StringBuilder builder = new StringBuilder();

    int cols = data.getLinesCount();
    int rows = data.getMaxDP();


    // if lines>1 --> otherwise it is x csv
    if (useSettings) {
      // rotation
      int rotation = settings.getSettImage().getRotationOfData();
      if (rotation == 90 || rotation == 270 || rotation == -90) {
        if (cols != 1)
          cols = rows;

        rows = data.getLinesCount();
      }
    }
    for (int r = 0; r < rows; r++) {
      // increment l
      for (int c = 0; c < cols; c++) {
        // only if not null: write Intensity
        if (useSettings) {
          double tmp = getI(raw, useSettings, c, r);
          builder.append(!Double.isNaN(tmp) ? tmp : "");
        } else
          builder.append(r < data.getLineLength(c) ? data.getI(index, c, r) : "");
        if (c < cols - 1)
          builder.append(sep);
      }
      if (r < rows - 1)
        builder.append("\n");
    }
    return builder.toString();
  }


  /**
   * all intensities as one array (no reflection/rotation)
   * 
   * @return float intensity Array
   */
  public double[] toIArray(boolean raw) {
    // calc count of points
    int scanpoints = data.getTotalDPCount();
    double[] z = new double[scanpoints];
    //
    //
    if (raw) {
      int c = 0;
      for (int iy = 0; iy < data.getLinesCount(); iy++) {
        //
        for (int ix = 0; ix < data.getLineLength(iy); ix++) {
          // x = time; NOT distance; so calc
          z[c] = getIRaw(iy, ix);
          c++;
        }
      }
    } else {
      // for lines (that are actually datapoints)
      int maxlines = getMaxLinesCount();
      int maxdp = getMaxLineLength();
      int c = 0;
      for (int l = 0; l < maxlines; l++) {
        for (int dp = 0; dp < maxdp; dp++) {
          // for dp ( that are actually lines)
          double tmp = getI(raw, l, dp);
          if (!Double.isNaN(tmp)) {
            z[c] = tmp;
            c++;
          }
        }
      }
    }
    //
    return z;
  }

  /**
   * all intensities as one array limited to selections/exclusions or not
   * 
   * @return float intensity Array
   */
  @Override
  public double[] toIArray(boolean raw, boolean onlySelected, boolean excluded) {
    if (onlySelected) {
      double[] datasel = new double[getSelectedDPCount(true)];
      int counter = 0;
      int maxlines = getMaxLinesCount();
      int maxdp = getMaxLineLength();
      for (int l = 0; l < maxlines; l++) {
        for (int dp = 0; dp < maxdp; dp++) {
          double tmp;
          if ((!excluded || !isExcludedDP(l, dp)) && isSelectedDP(l, dp)
              && !Double.isNaN(tmp = getI(raw, l, dp))) {
            datasel[counter] = tmp;
            counter++;
          }
        }
      }
      return datasel;
    } else {
      return toIArray(raw);
    }
  }

  /**
   * Returns the intensity matrix (NaN if pixel is empty)
   * 
   * @param raw
   * @return [line][dp]
   */
  public double[][] toIMatrix(boolean raw, boolean useSettings) {
    // time only once?
    if (useSettings) {
      int lines = getMaxLinesCount();
      int dps = getMaxLineLength();

      double[][] dataExp = new double[lines][dps];
      // c for lines
      for (int l = 0; l < lines; l++) {
        // increment l
        for (int dp = 0; dp < dps; dp++) {
          // also allows NaN as no dp
          dataExp[l][dp] = getI(raw, l, dp);
        }
      }
      return dataExp;
    } else {
      int lines = data.getLinesCount();
      int dps = data.getMaxDP();
      double[][] dataExp = new double[dps][lines];
      for (int l = 0; l < lines; l++) {
        int length = data.getLineLength(l);
        // increment l
        for (int dp = 0; dp < dps; dp++) {
          // only if not null: write Intensity
          dataExp[l][dp] = dp < length ? getIRaw(l, dp) : Double.NaN;
        }
      }
      return dataExp;
    }
  }

  /**
   * Returns the intensity matrix. Always uses the rotation settings etc. Double.NaN for empty or
   * non-selected or excluded pixel
   * 
   * @param raw
   * @return [line][dp]
   */
  public double[][] toIMatrixOfSelected(boolean raw) {
    int cols = getMaxLinesCount();
    int rows = getMaxLineLength();

    double[][] dataExp = new double[cols][rows];
    // c for lines
    for (int c = 0; c < cols; c++) {
      // increment l
      for (int r = 0; r < rows; r++) {
        // only if not null: write Intensity
        double tmp = getI(raw, c, r);
        if (!isExcludedDP(c, r) && isSelectedDP(c, r) && !Double.isNaN(tmp)) {
          dataExp[c][r] = tmp;
        } else
          dataExp[c][r] = Double.NaN;
      }
    }
    return dataExp;
  }

  /**
   * Returns the intensity only. with boolean map as alpha map
   * 
   * @param sett
   * @return [line][dp]
   */
  public Object[][] toIMatrix(boolean raw, State[][] map) {
    // time only once?
    int cols = getMaxLinesCount();
    int rows = getMaxLineLength();

    Object[][] dataExp = new Object[cols][rows];
    // c for lines
    for (int c = 0; c < cols; c++) {
      // r for data points
      for (int r = 0; r < rows; r++) {
        // only if not null: write Intensity
        // only if not null: write Intensity
        boolean state = c < map.length && r < map[c].length && map[c][r].isFalse();
        if (state) {
          double tmp = getI(raw, c, r);
          dataExp[c][r] = !Double.isNaN(tmp) ? tmp : "";
        } else
          dataExp[c][r] = "";
      }
    }
    return dataExp;
  }


  // finished processed data
  // ######################################################################
  // get index from processed data (x/y)
  /**
   * returns the index of the line representing y
   * 
   * @param y
   * @return
   */
  public int getYAsIndex(double y, double x) {
    int rotation = settings.getSettImage().getRotationOfData();
    boolean reflectH = settings.getSettImage().isReflectHorizontal();
    return getYAsIndex(y, x, rotation, reflectH);
  }

  private int getYAsIndex(double y, double x, int rotation, boolean reflectH) {
    // XOR
    reflectH = rotation == 180 ^ reflectH;
    //
    if (rotation == 90) {
      // get line
      int line = getYAsIndex(x, y, 0, reflectH);
      // y --> x and invert reflectV
      return getXAsIndex(line, y, 0, !settings.getSettImage().isReflectVertical(),
          settings.getSettImage().getImagingMode());
    } else if (rotation == -90 || rotation == 270) {
      // get line
      int line = getYAsIndex(x, y, 0, !reflectH);
      // y --> x and invert reflectV
      return getXAsIndex(line, y, 0, settings.getSettImage().isReflectVertical(),
          settings.getSettImage().getImagingMode());
    } else if (!reflectH) {
      // standard: 0 or (180 with reflect)
      if (y <= 0)
        return 0;
      int l = (int) (y / settings.getSettImage().getSpotsize());
      return l < data.getLinesCount() ? l : data.getLinesCount() - 1;
    } else {
      // reflect
      if (y < 0)
        return 0;
      int l = (int) (y / settings.getSettImage().getSpotsize());
      l = data.getLinesCount() - l - 1;
      if (l < data.getLinesCount() - 1)
        return l >= 0 ? l : 0;
      else
        return data.getLinesCount() - 1;
    }
  }

  /**
   * returns the index of the data point in the given line
   * 
   * @param line is an integer index
   * @param x is the coordinate (processed)
   * @return
   */
  public int getXAsIndex(int line, double x) {
    double rx = x / settings.getSettImage().getVelocity();
    for (int i = 1; i < data.getLineLength(line); i++) {
      if (data.getX(line, i) >= rx)
        return i - 1;
    }
    return data.getLineLength(line) - 1;
  }

  private int getXAsIndex(double y, double x, int rotation, boolean reflectV, IMAGING_MODE mode) {
    // XOR
    reflectV = rotation == 180 ^ reflectV;
    //
    if (rotation == 90) {
      // get line
      int line = getYAsIndex(x, y, 0, reflectV);
      // y --> x and invert reflectV
      return getXAsIndex(line, y, 0, !settings.getSettImage().isReflectVertical(),
          settings.getSettImage().getImagingMode());
    } else if (rotation == -90 || rotation == 270) {
      // get line
      int line = getYAsIndex(x, y, 0, !reflectV);
      // y --> x and invert reflectV
      return getXAsIndex(line, y, 0, settings.getSettImage().isReflectVertical(),
          settings.getSettImage().getImagingMode());
    } else if (!reflectV) {
      // standard: 0 or (180 with reflect)
      if (y <= 0)
        return 0;
      int l = (int) (y / settings.getSettImage().getSpotsize());
      return l < data.getLinesCount() ? l : data.getLinesCount() - 1;
    } else {
      // reflect
      if (y < 0)
        return 0;
      int l = (int) (y / settings.getSettImage().getSpotsize());
      l = data.getLinesCount() - l - 1;
      if (l < data.getLinesCount() - 1)
        return l >= 0 ? l : 0;
      else
        return data.getLinesCount() - 1;
    }
  }

  private int getXAsIndex(int line, double x, int rotation, boolean reflectV, IMAGING_MODE mode) {
    double rx = x / settings.getSettImage().getVelocity();
    for (int i = 1; i < data.getLineLength(line); i++) {
      if (data.getX(line, i) >= rx)
        return i - 1;
    }
    return data.getLineLength(line) - 1;
  }

  /**
   * returns the index of the data point at x / y
   * 
   * @param y is the coordinate (processed)
   * @param x is the coordinate (processed)
   * @return
   */
  public int getXAsIndex(double y, double x) {
    return getXAsIndex(getYAsIndex(y, x), x);
  }


  public String getTitle() {
    return settings.getSettImage().getTitle();
  }

  public String getShortTitle() {
    String s = settings.getSettImage().getShortTitle();
    return s.length() > 0 ? s : getTitle();
  }

  // #########################################################################################################
  // GETTER AND SETTER
  /**
   * 
   * @param settings any image settings
   */
  @Override
  public void setSettings(Settings settings) {
    if (settings == null)
      return;

    // dataset settings
    if (DatasetSettings.class.isInstance(settings)) {
      getData().setSettings(settings);
    } else
      super.setSettings(settings);

    // fire changes
    if (SettingsImage2D.class.isAssignableFrom(settings.getClass())) {
      ((SettingsImage2D) settings).setCurrentImage(this);
      fireIntensityProcessingChanged();
    } else if (SettingsImage2DOperations.class.isAssignableFrom(settings.getClass())
        || SettingsImage2DQuantifier.class.isAssignableFrom(settings.getClass())) {
      getSettings().replaceSettings(settings);
      fireIntensityProcessingChanged();
    } else if (SettingsImageContinousSplit.class.isAssignableFrom(settings.getClass()))
      if (DatasetContinuousMD.class.isInstance(data))
        ((DatasetContinuousMD) data).setSplitSettings((SettingsImageContinousSplit) settings);
  }

  /**
   * get settings by class
   * 
   * @param classsettings
   * @return
   */
  public Settings getSettingsByClass(Class classsettings) {
    // return dataset settings
    if (DatasetSettings.class.isAssignableFrom(classsettings)) {
      return getData().getSettingsByClass(classsettings);
    } else
      return super.getSettingsByClass(classsettings);
  }

  /**
   * Given image img will be setup like this image
   * 
   * @param img will get all settings from master image
   */
  @Override
  public void applySettingsToOtherImage(Collectable2D img2) {
    if (img2.isImage2D()) {
      Image2D img = (Image2D) img2;

      try {
        // save name and path
        String name = img.getTitle();
        String shortName = img.getShortTitle();

        String path = img.getSettings().getSettImage().getRAWFilepath();
        // copy all TODO
        img.setSettings(BinaryWriterReader.deepCopy(this.settings.getSettImage()));
        // there should be no need for this
        // img.setSettPaintScale((BinaryWriterReader.deepCopy(this.getSettPaintScale())));
        // img.setSettTheme(BinaryWriterReader.deepCopy(this.getSettTheme()));
        // img.setOperations(BinaryWriterReader.deepCopy(this.getOperations()));
        // img.setQuantifier(BinaryWriterReader.deepCopy(this.getQuantifier()));
        // img.setSettZoom(BinaryWriterReader.deepCopy(this.getdSettZoom()));
        // set name and path
        // only reset to old short title if the titles were not the same
        if (!name.equals(img.getTitle()))
          img.getSettings().getSettImage().setShortTitle(shortName);
        // reset to old title
        img.getSettings().getSettImage().setTitle(name);
        img.getSettings().getSettImage().setRAWFilepath(path);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }


  /**
   * The maximum x value of a line (right edge) --> length
   * 
   * @param raw
   * @param line
   * @return
   */
  public float getMaxXRaw(boolean raw, int line) {
    return data.getLastXLine(line) * (xFactor(raw));
  }

  /**
   * The maximum x value (left edge) --> length
   * 
   * @param raw
   * @param line
   * @return
   */
  public float getMaxXRaw(boolean raw) {
    return data.getLastX() * (xFactor(raw));
  }

  /**
   * left edge maximum of y (bottom edge)
   * 
   * @param raw
   * @return
   */
  public float getMaxYRaw(boolean raw) {
    return getYRaw(raw, data.getLinesCount());
  }


  @Override
  public float getX0() {
    if (!isRotated())
      return data.getX0() * xFactor(false);
    else
      return data.getY0() * yFactor(false);
  }

  @Override
  public float getY0() {
    if (!isRotated())
      return data.getY0() * yFactor(false);
    else
      return data.getX0() * xFactor(false);
  }

  /**
   * width of the image
   * 
   * @param raw
   * @return
   */
  @Override
  public float getWidth() {
    return getWidth(false);
  }

  /**
   * width of the image
   * 
   * @param raw
   * @return
   */
  public float getWidth(boolean raw) {
    if (!isRotated())
      return data.getWidthX() * xFactor(raw);
    else
      return getYRaw(raw, data.getLinesCount());
  }

  /**
   * height of the image
   * 
   * @param raw
   * @return
   */
  @Override
  public float getHeight() {
    return getHeight(false);
  }

  /**
   * height of the image
   * 
   * @param raw
   * @return
   */
  public float getHeight(boolean raw) {
    if (isRotated())
      return data.getWidthX() * xFactor(raw);
    else
      return getYRaw(raw, data.getLinesCount());
  }

  /**
   * 1 or velocity
   * 
   * @param raw
   * @return
   */
  private float xFactor(boolean raw) {
    return raw ? 1 : settings.getSettImage().getVelocity();
  }

  /**
   * 1 or velocity
   * 
   * @param raw
   * @return
   */
  private float yFactor(boolean raw) {
    return raw ? 1 : settings.getSettImage().getSpotsize();
  }

  /**
   * according to rotation of data
   * 
   * @return
   */
  public int getWidthAsMaxDP() {
    SettingsGeneralImage sg = settings.getSettImage();
    return (sg.getRotationOfData() == -90 || sg.getRotationOfData() == 90
        || sg.getRotationOfData() == 270) ? data.getLinesCount() : data.getMaxDP();
  }

  /**
   * according to rotation of data
   * 
   * @return
   */
  public int getHeightAsMaxDP() {
    SettingsGeneralImage sg = settings.getSettImage();
    return (sg.getRotationOfData() == -90 || sg.getRotationOfData() == 90
        || sg.getRotationOfData() == 270) ? data.getMaxDP() : data.getLinesCount();
  }

  /**
   * maximum block width for renderer = distance between one and next block
   * 
   * @return
   */
  public float getMaxBlockWidth() {
    return getMaxBlockWidth(getSettings().getSettImage());
  }

  /**
   * maximum block height for renderer = distance between one and next block in lines
   * 
   * @return
   */
  public float getMaxBlockHeight() {
    return getMaxBlockHeight(getSettings().getSettImage());
  }

  public float getMaxBlockWidth(SettingsGeneralImage settImg) {
    int interpolation = settImg.isUseInterpolation() ? settImg.getInterpolation() : 1;
    int reduction = settImg.isUseReduction() ? settImg.getReduction() : 1;
    return getMaxBlockWidth(settImg.getRotationOfData(), interpolation, reduction);
  }

  public float getMaxBlockWidth(int rotation, int interpolation, int reduction) {
    if (rotation != 0 && rotation != 180)
      return getMaxBlockHeight(0, interpolation, reduction);
    else {
      float f = 1.f / interpolation * reduction;
      return data.getMaxDPWidth() * settings.getSettImage().getVelocity() * f;
    }
  }


  public float getMaxBlockHeight(SettingsGeneralImage settImg) {
    int interpolation = settImg.isUseInterpolation() ? settImg.getInterpolation() : 1;
    int reduction = settImg.isUseReduction() ? settImg.getReduction() : 1;
    return getMaxBlockHeight(settImg.getRotationOfData(), interpolation, reduction);
  }

  public float getMaxBlockHeight(int rotation, int interpolation, int redcution) {
    if (rotation != 0 && rotation != 180)
      return getMaxBlockWidth(0, interpolation, redcution);
    else {
      // height is not changed when reducing data points
      return settings.getSettImage().getSpotsize();
    }
  }

  /**
   * maximum block width for renderer = distance between one and next block
   * 
   * @return
   */
  @Override
  public float getAvgBlockWidth() {
    return getAvgBlockWidth(getSettings().getSettImage());
  }

  /**
   * maximum block height for renderer = distance between one and next block in lines
   * 
   * @return
   */
  @Override
  public float getAvgBlockHeight() {
    return getAvgBlockHeight(getSettings().getSettImage());
  }

  public float getAvgBlockWidth(SettingsGeneralImage settImg) {
    int interpolation = settImg.isUseInterpolation() ? settImg.getInterpolation() : 1;
    int reduction = settImg.isUseReduction() ? settImg.getReduction() : 1;
    return getAvgBlockWidth(settImg.getRotationOfData(), interpolation, reduction);
  }

  public float getAvgBlockWidth(int rotation, int interpolation, int reduction) {
    if (rotation != 0 && rotation != 180)
      return getAvgBlockHeight(0, interpolation, reduction);
    else {
      float f = 1.f / interpolation * reduction;
      return data.getAvgDPWidth() * settings.getSettImage().getVelocity() * f;
    }
  }


  public float getAvgBlockHeight(SettingsGeneralImage settImg) {
    int interpolation = settImg.isUseInterpolation() ? settImg.getInterpolation() : 1;
    int reduction = settImg.isUseReduction() ? settImg.getReduction() : 1;
    return getAvgBlockHeight(settImg.getRotationOfData(), interpolation, reduction);
  }

  public float getAvgBlockHeight(int rotation, int interpolation, int redcution) {
    if (rotation != 0 && rotation != 180)
      return getAvgBlockWidth(0, interpolation, redcution);
    else {
      // height is not changed when reducing data points
      return settings.getSettImage().getSpotsize();
    }
  }



  // #####################################################################################
  // Basic funcitons filling images

  // END of basics
  // #####################################################################################

  /**
   * For full range {@link DataCollectable2D#getIRange(boolean)}
   * 
   * @return intensity span
   */
  public double getIntensitySpan(boolean onlySelected) {
    return getMaxIntensity(onlySelected) - getMinIntensity(onlySelected);
  }

  // vars for that:
  private double[] averageIProcessedForLine;

  /**
   * calcs the average I for a line
   * 
   * @param line
   * @return
   */
  public double getAverageIProcessed(int line) {
    // check for update in parent i processing
    checkForUpdateInParentIProcessing();
    //
    if (averageIProcessedForLine == null) {
      averageIProcessedForLine = new double[this.getMaxLinesCount()];
      for (int i = 0; i < this.getMaxLinesCount(); i++) {
        averageIProcessedForLine[i] = 0;
        for (int dp = 0; dp < getLineLength(i); dp++)
          if (i < getLineCount(dp))
            averageIProcessedForLine[i] += getI(false, i, dp);
        averageIProcessedForLine[i] = averageIProcessedForLine[i] / getLineLength(i);
      }
    }
    return averageIProcessedForLine[line];
  }

  /**
   * checks if there is a parent that has changed I processing then it changes the processing here
   */
  @Override
  protected void checkForUpdateInParentIProcessing() {
    if (parent != null && parent.getLastIProcChangeTime() != lastIProcChangeTime) {
      fireIntensityProcessingChanged();
      lastIProcChangeTime = parent.getLastIProcChangeTime();
    }
  }

  public Image2D getParent() {
    return parent;
  }

  /**
   * save lastIProcChangeTime for comparison in all quantifiers. update all quantifiers if it has
   * changed
   */
  @Override
  public void fireIntensityProcessingChanged() {
    super.fireIntensityProcessingChanged();
    // gives a indirect signal to Quantifier and children to change iProc
    // IS
    SettingsImage2DQuantifierIS internalQ = settings.getInternalQuantifierIS();
    if (internalQ != null && internalQ.getImgIS() != null) {
      if (internalQ.getImgIS().getSettings().getOperations() == null)
        internalQ.getImgIS().getSettings().replaceSettings(new SettingsImage2DOperations());
      internalQ.getImgIS().getSettings().getOperations()
          .setBlankQuantifier(settings.getOperations().getBlankQuantifier());
      internalQ.getImgIS().fireIntensityProcessingChanged();
    }
  }


  /**
   * all child will point at settings from parent.
   * 
   * @param parent
   */
  public void setParent(Image2D parent) {
    if (parent == null) {
      // copy all settings
      this.applySettingsToOtherImage(this);
    } else if (parent != this.parent) {
      // point at the settings of parent TODO
      settings.replaceSettings(parent.getSettings().getSettImage());
      settings.replaceSettings(parent.getSettings().getSettPaintScale());
      settings.replaceSettings(parent.getSettings().getSettTheme());
      settings.replaceSettings(parent.getSettings().getOperations());
      settings.replaceSettings(parent.getSettings().getQuantifier());
      fireIntensityProcessingChanged();
    }
    // set parent
    this.parent = parent;
  }

  /**
   * Sums up the total dp count
   * 
   * @return
   */
  public int getTotalDPCount() {
    return data.getTotalDPCount();
  }

  /**
   * Sums up all the selected data with optional exclusion
   * 
   * @param excluded defines whether to exclude or not
   * @return
   */
  public int getSelectedDPCount(boolean excluded) {
    int counter = 0;
    //
    int lines = getMaxLinesCount();
    int maxdp = getMaxLineLength();

    for (int y = 0; y < lines; y++) {
      for (int x = 0; x < maxdp; x++) {
        if ((!excluded || !isExcludedDP(y, x)) && isSelectedDP(y, x))
          counter++;
      }
    }
    return counter;
  }


  /**
   * Returns all selected and not excluded data points to an array
   * 
   * @return
   */
  public List<Double> getSelectedDataAsList(boolean raw, boolean excluded) {
    ArrayList<Double> list = new ArrayList<>();
    int lines = getMaxLinesCount();
    int maxdp = getMaxLineLength();
    for (int l = 0; l < lines; l++) {
      for (int dp = 0; dp < maxdp; dp++) {
        if ((!excluded || !isExcludedDP(l, dp)) && isSelectedDP(l, dp)) {
          list.add(getI(raw, l, dp));
        }
      }
    }
    return list;
  }

  /**
   * checks if a dp is excluded by a rect in excluded list. Or in alpha map
   * 
   * @param l
   * @param dp
   * @return
   */
  public boolean isExcludedDP(int l, int dp) {
    // out of bounds
    if (!isInBounds(l, dp))
      return true;

    SettingsSelections sel = settings.getSettSelections();

    // use alpha map as exclusion?
    if (sel.isAlphaMapExclusionActive()) {
      // check alpha map
      if (getImageGroup() != null) {
        SettingsAlphaMap a = getImageGroup().getSettAlphaMap();
        if (a != null && a.isActive()) {
          // same dimensions?
          if (a.checkDimensions(this)) {
            State b = a.getMapValue(l, dp);
            // is excluded in alphamap:
            if (b.isFalse())
              return true;
          } else
            a.setActive(false);
        }
      }
    }

    // no exclusion rects?
    if (!sel.hasExclusions())
      return false;

    // coordinates
    float x = getX(false, l, dp);
    float y = getY(false, l, dp);

    // check if dp coordinates are in an exclude rect
    return sel.isExcluded(x, y, (float) getMaxBlockWidth(), (float) getMaxBlockHeight());
  }

  /**
   * checks if a dp is selected (if there are no selected rects - it will always return true
   * 
   * @param l line
   * @param dp datapoint
   * @return
   */
  public boolean isSelectedDP(int l, int dp) {
    // out of bounds
    if (!isInBounds(l, dp))
      return false;
    // no selection rects?
    SettingsSelections sel = settings.getSettSelections();
    if (!sel.hasSelections())
      return true;
    else {
      // coordinates
      float x = getX(false, l, dp);
      float y = getY(false, l, dp);

      // check if dp coordinates are in an sel rect
      return sel.isSelected(x, y, (float) getMaxBlockWidth(), (float) getMaxBlockHeight(), false);
    }
  }


  /**
   * Copies the Image2D and sets this to parent settings will be connected
   * 
   * @return
   * @throws Exception
   */
  public Image2D getCopyChild() throws Exception {
    Image2D copy = BinaryWriterReader.deepCopy(this);
    copy.setParent(this);
    return copy;
  }

  /**
   * Copies the Image2D
   * 
   * @return
   * @throws Exception
   */
  public Image2D getCopy() throws Exception {
    return BinaryWriterReader.deepCopy(this);
  }

  public boolean isAllLinesSameLength() {
    for (int i = 1; i < data.getLinesCount(); i++)
      if (data.getLineLength(i - 1) != data.getLineLength(i))
        return false;

    return true;
  }


  // ###########################################################################
  // Info graphics> / icons

  /**
   * returns an easy icon
   * 
   * @param maxw
   * @param maxh
   * @return
   */
  @Override
  public Icon getIcon(int maxw, int maxh) {
    try {
      double min = getValueCutFilter(2.5, false);
      double max = getValueCutFilter(100.0 - 0.2, false);
      PaintScale scale =
          PaintScaleGenerator.generateStepPaintScale(min, max, settings.getSettPaintScale());

      // scale in x
      float sx = 1;
      int w = getMaxLineLength();
      if (w > maxw) {
        sx = w / (float) maxw;
        w = maxw;
      }

      float sy = 1;
      int lines = getMaxLinesCount();
      int h = lines;
      if (h > maxh) {
        sy = h / (float) maxh;
        h = maxh;
      }

      BufferedImage img = new BufferedImage(Math.min(maxw, w), maxh = Math.min(maxh, h),
          BufferedImage.TYPE_INT_ARGB);
      Graphics2D g = img.createGraphics();

      for (int x = 0; x < w; x++) {
        for (int y = 0; y < h; y++) {
          // fill rects
          int dp = getLineLength((int) (y * sy));
          int ix = (int) (x * sx);
          int iy = (int) (y * sy);
          if (iy < lines && ix < dp) {
            double z = getI(false, iy, ix);
            if (!Double.isNaN(z)) {
              Paint c = scale.getPaint(z);
              g.setPaint(c);
              g.fillRect(x, maxh - y, 1, 1);
            }
          }
        }
      }

      return new ImageIcon(img);
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }
  // end of visual extras
  // ###########################################################################


  public ImageDataset getData() {
    return data;
  }

  public void setData(ImageDataset data) {
    boolean changed = !data.equals(this.data);
    this.data = data;
    if (changed)
      fireIntensityProcessingChanged();
  }

  public void shiftIndex(int i) {
    index += i;
  }

  public void setIndex(int i) {
    index = i;
  }

  public int getIndex() {
    return index;
  }

  // ############################################################
  // listener
  /**
   * raw data changes by: direct imaging,
   * 
   * @param listener
   */
  public void addRawDataChangedListener(RawDataChangedListener listener) {
    data.addRawDataChangedListener(listener);
  }

  public void removeRawDataChangedListener(RawDataChangedListener list) {
    data.removeRawDataChangedListener(list);
  }

  public void clearRawDataChangedListeners() {
    data.cleatRawDataChangedListeners();
  }

  /**
   * checks whether these two images have the same data space
   * 
   * @param i
   * @return
   */
  public boolean hasSameData(Image2D i) {
    return data.equals(i.getData()) || (data.getMaxDP() == i.getData().getMaxDP()
        || data.getMaxDP() == i.getData().getLinesCount())
        && data.getTotalDPCount() == i.getData().getTotalDPCount();
  }

  // a name for lists
  public String toListName() {
    return settings.getSettImage().toListName();
  }

  @Override
  public int getTotalDataPoints() {
    return data.getTotalDPCount();
  }

  @Override
  public boolean hasOneDPWidth() {
    return data.hasOneDPWidth();
  }

  @Override
  public boolean hasOneDPHeight() {
    return data.hasOneDPHeight();
  }

}
