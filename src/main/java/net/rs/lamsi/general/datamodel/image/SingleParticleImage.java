package net.rs.lamsi.general.datamodel.image;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Icon;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;
import net.rs.lamsi.general.datamodel.image.data.interf.ImageDataset;
import net.rs.lamsi.general.datamodel.image.data.interf.MDDataset;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.DatasetContinuousMD;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.DatasetLinesMD;
import net.rs.lamsi.general.datamodel.image.data.twodimensional.XYIDataMatrix;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.datamodel.image.listener.RawDataChangedListener;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.general.settings.image.SettingsSPImage;
import net.rs.lamsi.general.settings.image.operations.listener.IntensityProcessingChangedListener;
import net.rs.lamsi.general.settings.image.selection.SettingsSelections;
import net.rs.lamsi.general.settings.image.sub.SettingsGeneralImage;
import net.rs.lamsi.general.settings.image.sub.SettingsGeneralImage.IMAGING_MODE;
import net.rs.lamsi.general.settings.image.sub.SettingsImageContinousSplit;
import net.rs.lamsi.general.settings.image.visualisation.SettingsPaintScale;
import net.rs.lamsi.general.settings.importexport.SettingsImageDataImportTxt.ModeData;
import net.rs.lamsi.general.settings.interf.DatasetSettings;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow.LOG;
import net.rs.lamsi.utils.mywriterreader.BinaryWriterReader;

// XY raw data!
// have to be multiplied with velocity and spot size
public class SingleParticleImage extends Collectable2D<SettingsSPImage> implements Serializable {
  // do not change the version!
  private static final long serialVersionUID = 1L;

  // changed events TODO
  protected List<IntensityProcessingChangedListener> listenerProcessingChanged =
      new ArrayList<IntensityProcessingChangedListener>();

  // ############################################################
  // data
  protected ImageDataset data;

  // index of image in data set: (multidimensional data set)
  protected int index = 0;

  // are getting calculated only once or after processing changed
  // max and min z (intensity)
  protected double averageIProcessed = -1;
  protected double minZ = Double.NaN, maxZ = Double.NaN;
  protected double minNonZeroZSelected = Double.NaN, minNonZeroZ = Double.NaN;
  protected double minZSelected = Double.NaN, maxZSelected = Double.NaN, avgZSelected = Double.NaN;
  protected double minZFiltered = -1;
  protected double maxZFiltered = -1;
  // store total dp count

  public SingleParticleImage() {
    super((new SettingsSPImage()));
  }

  public SingleParticleImage(SettingsSPImage settings) {
    super(settings);
  }

  public SingleParticleImage(ImageDataset data) {
    this();
    this.data = data;
  }

  public SingleParticleImage(ImageDataset data, int index) {
    this();
    this.data = data;
    this.index = index;
  }

  public SingleParticleImage(ImageDataset data, int index, SettingsSPImage sett) {
    this(sett);
    this.data = data;
    this.index = index;
  }

  public SingleParticleImage(ImageDataset data, SettingsSPImage sett) {
    this(data, 0, sett);
  }

  /**
   * y values in respect to rotation reflection imaging mode
   * 
   * @param raw
   * @param l
   * @param dp
   * @return
   */
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
  public float getYRaw(int l) {
    return l * yFactor();
  }

  /**
   * intensity values in respect to rotation reflection imaging mode
   * 
   * @param raw
   * @param l
   * @param dp
   * @return
   */
  public double getI(int l, int dp) {
    return getI(true, l, dp);
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
  public double getI(boolean useSettings, int l, int dp) {
    // get raw i
    double i = !useSettings ? getIRaw(l, dp)
        : getI(l, dp, settings.getSettImage().getImagingMode(),
            settings.getSettImage().getRotationOfData(),
            settings.getSettImage().isReflectHorizontal(),
            settings.getSettImage().isReflectVertical());

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
  public float getX(int l, int dp) {
    return getX(l, dp, settings.getSettImage().getImagingMode(),
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
  public float getXRaw(int l, int dp) {
    int line = l < data.getLinesCount() ? l : data.getLinesCount() - 1;
    if (dp < data.getLineLength(line))
      return data.getX(line, dp) * (settings.getSettImage().getVelocity());
    // end of data x (right edge of last datapoint)
    else if (dp == data.getLineLength(line))
      return data.getRightEdgeX(l) * (settings.getSettImage().getVelocity());
    else {
      // for the maximum processed line length
      int overMax = (data.getLineLength(line) - dp + 1);
      ImageEditorWindow.log("ask for a dp>then line in getXProcessed", LOG.DEBUG);
      // return (((data.getX(line, data.getLineLength(line)-1) + getLine(line).getWidthDP()*overMax)
      // * settImage.getVelocity()));
      // tmp change
      return (((data.getX(line, data.getLineLength(line) - 1))
          * (settings.getSettImage().getVelocity())));
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
  public float getX(int l, int dp, IMAGING_MODE imgMode, int rotation, boolean reflectH,
      boolean reflectV) {
    // the usual case
    if (rotation == 90 || rotation == 270 || rotation == -90) {
      // return line height of line: dp
      return getY(dp, l, imgMode, 0, reflectH, reflectV);
    } else if (rotation == 180) {
      // invert reflection
      return getX(l, dp, imgMode, 0, !reflectH, !reflectV);
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
        float value = getXRaw(cy, cx);
        // xor ^
        // imagecreation mode: if twoways -> first reflect every 2. line (x values)
        if (reflectV ^ (imgMode == IMAGING_MODE.MODE_IMAGING_TWOWAYS && cy % 2 != 0)) {
          // reflect x
          float width = getMaxXRaw();
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
  public float getY(int l, int dp, IMAGING_MODE imgMode, int rotation, boolean reflectH,
      boolean reflectV) {
    // the usual case
    if (rotation == 0 || rotation == 180 || rotation == 360) {
      return getYRaw(l);
    } else {
      // 90°
      if (rotation == 90) {
        // rotate back to 0
        // dp -> l
        // line length(dp) -1 -l -> dp
        // return getX(raw, dp, data.getMaxDP()-1-l, imgMode, 0, reflectH,reflectV);
        return getX(dp, l, imgMode, 0, reflectH, !reflectV);
      }
      // -90° = 270°
      else {
        // l -> dp
        // data.linescount -1 -dp
        return getX(data.getLinesCount() - 1 - dp, l, imgMode, 0, reflectH, reflectV);
      }
    }
  }


  /**
   * minimum line length in regards to rotation columns of the image
   * 
   * @return
   */
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
  public double[][] toXYIArray(boolean useSettings) {
    if (useSettings) {
      SettingsGeneralImage s = settings.getSettImage();
      int diff = data.getMaxDP() - data.getMinDP();
      // crop?
      if (s.isCropDataToMin() && diff != 0) {
        return toXYIDataMatrix(useSettings).toLinearArray();
      } else {
        // no crop
        return toXYIArray(s.getImagingMode(), s.getRotationOfData(), s.isReflectHorizontal(),
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
  public Object[][] toXYIMatrix(boolean useSettings) {
    double[][] data = toXYIArray(useSettings);

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
  public double[][] toXYIArray(IMAGING_MODE imgMode, int rotation, boolean reflectH,
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
        double tmp = getI(usesRot, iy, ix);
        if (!Double.isNaN(tmp)) {
          z[currentdp] = tmp;
          if (usesRot) {
            y[currentdp] = Double.valueOf(String.valueOf((getY(iy, ix))));
            x[currentdp] = Double.valueOf(String.valueOf(getX(iy, ix)));
          } else {
            y[currentdp] = Double.valueOf(String.valueOf((getYRaw(iy))));
            x[currentdp] = Double.valueOf(String.valueOf(getXRaw(iy, ix)));
          }
          currentdp++;
        }
      }
    }
    //
    return new double[][] {x, y, z};
  }

  /**
   * returns all line lengths according to rotation etc
   * 
   * @return
   */
  private int[] getLineLenghts() {
    int rotation = settings.getSettImage().getRotationOfData();
    int[] length = new int[data.getLinesCount()];
    if ((rotation == 90 || rotation == 270 || rotation == -90))
      // all line length
      for (int i = 0; i < data.getLinesCount(); i++)
        length[i] = getLineCount(i);
    else
      for (int i = 0; i < data.getLinesCount(); i++)
        length[i] = getLineLength(i);
    return length;
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
        x[currentdp] = Double.valueOf(String.valueOf(getXRaw(iy, ix)));
        y[currentdp] = Double.valueOf(String.valueOf((getYRaw(iy))));
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
  public Object[][] toDataArray(ModeData mode, boolean useSettings) {
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
            dataExp[r][c] = isDP(l, r) ? getX(l, r) : "";
          }
        } else {
          // write intensity
          for (int r = 0; r < rows; r++) {
            // only if not null
            double tmp = getI(l, r);
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
            dataExp[r][c] = r < data.getLineLength(l) ? getXRaw(l, r) : "";
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
  public XYIDataMatrix toXYIDataMatrix(boolean useSettings) {
    if (useSettings) {
      SettingsGeneralImage sett = settings.getSettImage();
      int diff = data.getMaxDP() - data.getMinDP();
      // apply crop?
      if (sett.isCropDataToMin() && diff != 0) {
        int rot = sett.getRotationOfData();
        boolean reflectH = sett.isReflectHorizontal();
        // exclude last line? if shorter than 0.8 of avg
        boolean excludeLastLine =
            data.getLineLength(data.getLinesCount() - 1) / data.getAvgDP() < 0.8;

        // start line and last line if exclude
        final int sl =
            excludeLastLine && ((reflectH && rot == 0) || (!reflectH && rot == 180)) ? 1 : 0;
        final int ll =
            excludeLastLine && ((!reflectH && rot == 0) || (reflectH && rot == 180)) ? 1 : 0;
        // start/last dp if exclude last line
        final int sdp = excludeLastLine
            && ((reflectH && rot == 90) || (!reflectH && (rot == -90 || rot == 270))) ? 1 : 0;
        final int ldp = excludeLastLine
            && ((!reflectH && rot == 90) || (reflectH && (rot == -90 || rot == 270))) ? 1 : 0;



        // get full matrix
        final int cols = getMaxLinesCount() - sl - ll;
        final int rows = getMaxLineLength() - sdp - ldp;

        Double[][] z = new Double[cols][rows];
        Float[][] x = new Float[cols][rows], y = new Float[cols][rows];

        // track first dp / first line / last dp/line index
        int firstDP = 0, lastDP = rows;

        // c for lines
        for (int c = 0; c < cols; c++) {
          int l = c + sl;
          // increment l
          for (int r = 0; r < rows; r++) {
            int dp = r + sdp;
            // only if not null: write Intensity
            double tmp = getI(l, dp);
            z[c][r] = tmp;
            // NaN?
            if (!Double.isNaN(tmp)) {
              x[c][r] = getX(l, dp);
              y[c][r] = getY(l, dp);
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

        Double[][] newz = new Double[h][w];
        Float[][] newx = new Float[h][w], newy = new Float[h][w];

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

        Double[][] z = new Double[cols][rows];
        Float[][] x = new Float[cols][rows], y = new Float[cols][rows];

        // c for lines
        for (int c = 0; c < cols; c++) {
          // increment l
          for (int r = 0; r < rows; r++) {
            // only if not null: write Intensity
            double tmp = getI(c, r);
            z[c][r] = tmp;
            // NaN?
            x[c][r] = !Double.isNaN(tmp) ? getX(c, r) : Float.NaN;
            y[c][r] = !Double.isNaN(tmp) ? getY(c, r) : Float.NaN;
          }
        }
        return new XYIDataMatrix(x, y, z);
      }
    } else {
      int cols = data.getLinesCount();

      Float[][] x = new Float[cols][], y = new Float[cols][];
      Double[][] z = new Double[cols][];

      for (int c = 0; c < cols; c++) {
        int length = data.getLineLength(c);
        x[c] = new Float[length];
        y[c] = new Float[length];
        z[c] = new Double[length];
        // increment l
        for (int r = 0; r < length; r++) {
          // only if not null: write Intensity
          z[c][r] = getIRaw(c, r);
          // NaN?
          x[c][r] = getXRaw(c, r);
          y[c][r] = getYRaw(c);
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
          dataExp[r][c] = isDP(c, r) ? getX(c, r) : "";
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
          dataExp[r][c] = r < length ? getXRaw(c, r) : "";
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
          builder.append(isDP(c, r) ? getX(c, r) : "");
        } else
          builder.append(r < data.getLineLength(c) ? getXRaw(c, r) : "");
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
          double tmp = getI(useSettings, c, r);
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
  public double[] toIArray() {
    // calc count of points
    int scanpoints = data.getTotalDPCount();
    double[] z = new double[scanpoints];
    //
    //
    int c = 0;
    for (int iy = 0; iy < data.getLinesCount(); iy++) {
      //
      for (int ix = 0; ix < data.getLineLength(iy); ix++) {
        // x = time; NOT distance; so calc
        z[c] = getIRaw(iy, ix);
        c++;
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
  public double[] toIArray(boolean onlySelected) {
    if (onlySelected) {
      // calc count of points
      ArrayList<Double> z = new ArrayList<Double>();
      // for lines (that are actually datapoints)
      int maxlines = getMaxLinesCount();
      int maxdp = getMaxLineLength();
      int c = 0;
      for (int l = 0; l < maxlines; l++) {
        for (int dp = 0; dp < maxdp; dp++) {
          // for dp ( that are actually lines)
          double tmp;
          if (!isExcludedDP(l, dp) && isSelectedDP(l, dp) && !Double.isNaN(tmp = getI(l, dp))) {
            z.add(tmp);
          }
        }
      }
      // convert
      double[] zz = new double[z.size()];
      for (int i = 0; i < z.size(); i++)
        zz[i] = z.get(i);

      return zz;
    } else {
      return toIArray();
    }
  }

  /**
   * Returns the intensity matrix
   * 
   * @param raw
   * @return [line][dp]
   */
  public Object[][] toIMatrix(boolean useSettings) {
    // time only once?
    if (useSettings) {
      int cols = getMaxLinesCount();
      int rows = getMaxLineLength();

      Object[][] dataExp = new Object[rows][cols];
      // c for lines
      for (int c = 0; c < cols; c++) {
        // increment l
        for (int r = 0; r < rows; r++) {
          // only if not null: write Intensity
          double tmp = getI(c, r);
          dataExp[r][c] = !Double.isNaN(tmp) ? tmp : "";
        }
      }
      return dataExp;
    } else {
      int cols = data.getLinesCount();
      int rows = data.getMaxDP();
      Object[][] dataExp = new Object[rows][cols];
      for (int c = 0; c < cols; c++) {
        int length = data.getLineLength(c);
        // increment l
        for (int r = 0; r < rows; r++) {
          // only if not null: write Intensity
          dataExp[r][c] = r < length ? getIRaw(c, r) : "";
        }
      }
      return dataExp;
    }
  }

  /**
   * Returns the intensity only. with boolean map as alpha map
   * 
   * @param sett
   * @return [line][dp]
   */
  public Object[][] toIMatrix(Boolean[][] map) {
    // time only once?
    int cols = getMaxLinesCount();
    int rows = getMaxLineLength();

    Object[][] dataExp = new Object[rows][cols];
    // c for lines
    for (int c = 0; c < cols; c++) {
      // r for data points
      for (int r = 0; r < rows; r++) {
        // only if not null: write Intensity
        // only if not null: write Intensity
        boolean state = c < map.length && r < map[c].length && map[c][r];
        if (state) {
          double tmp = getI(c, r);
          dataExp[r][c] = !Double.isNaN(tmp) ? tmp : "";
        } else
          dataExp[r][c] = "";
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
    if (SettingsSPImage.class.isAssignableFrom(settings.getClass())) {
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
    if (img2 instanceof SingleParticleImage) {
      SingleParticleImage img = (SingleParticleImage) img2;

      try {
        // save name and path
        String name = img.getTitle();
        String shortName = img.getShortTitle();

        String path = img.getSettings().getSettImage().getRAWFilepath();
        // copy all TODO
        img.setSettings(BinaryWriterReader.deepCopy(this.settings.getSettImage()));
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
   * minimum intensity processed
   * 
   * @return
   */
  public double getMinIntensity(boolean onlySelected) {
    // array with values only (no NaN)
    double[] inten = toIArray(onlySelected);
    if (onlySelected) {
      if (Double.isNaN(minZSelected)) {
        minZSelected = Double.POSITIVE_INFINITY;
        for (double i : inten)
          if (i < minZSelected)
            minZSelected = i;
      }

      if (minZSelected == Double.POSITIVE_INFINITY) {
        minZSelected = Double.NaN;
        return Double.NaN;
      }
      return minZSelected;
    } else {
      if (Double.isNaN(minZ)) {
        minZ = Double.POSITIVE_INFINITY;
        for (double i : inten)
          if (i < minZ)
            minZ = i;
      }

      if (minZ == Double.POSITIVE_INFINITY) {
        minZ = Double.NaN;
        return Double.NaN;
      }
      return minZ;
    }
  }

  /**
   * minimum intensity that is not zero processed
   * 
   * @return
   */
  public double getMinNonZeroIntensity(boolean onlySelected) {
    // array with values only (no NaN)
    double[] inten = toIArray(onlySelected);
    if (onlySelected) {
      if (Double.isNaN(minNonZeroZSelected)) {
        minNonZeroZSelected = Double.POSITIVE_INFINITY;
        for (double i : inten)
          if (i < minNonZeroZSelected && i != 0)
            minNonZeroZSelected = i;
      }

      if (minNonZeroZSelected == Double.POSITIVE_INFINITY) {
        minNonZeroZSelected = Double.NaN;
        return Double.NaN;
      }
      return minNonZeroZSelected;
    } else {
      if (Double.isNaN(minNonZeroZ)) {
        minNonZeroZ = Double.POSITIVE_INFINITY;
        for (double i : inten)
          if (i < minNonZeroZ && i != 0)
            minNonZeroZ = i;
      }

      if (minNonZeroZ == Double.POSITIVE_INFINITY) {
        minNonZeroZ = Double.NaN;
        return Double.NaN;
      }
      return minNonZeroZ;
    }
  }

  /**
   * maximum intensity processed
   * 
   * @return
   */
  public double getMaxIntensity(boolean onlySelected) {
    // array with values only (no NaN)
    double[] inten = toIArray(onlySelected);
    if (onlySelected) {
      if (Double.isNaN(maxZSelected)) {
        maxZSelected = Double.NEGATIVE_INFINITY;
        for (double i : inten)
          if (i > maxZSelected)
            maxZSelected = i;
      }

      if (maxZSelected == Double.NEGATIVE_INFINITY) {
        maxZSelected = Double.NaN;
        return Double.NaN;
      }
      return maxZSelected;
    } else {
      if (Double.isNaN(maxZ)) {
        maxZ = Double.NEGATIVE_INFINITY;
        for (double i : inten)
          if (i > maxZ)
            maxZ = i;
      }

      if (maxZ == Double.NEGATIVE_INFINITY) {
        maxZ = Double.NaN;
        return Double.NaN;
      }
      return maxZ;
    }
  }


  /**
   * Calcs the average I for this img
   * 
   * @return
   */
  public double getAverageIntensity(boolean onlySelected) {
    // array with values only (no NaN)
    double[] inten = toIArray(onlySelected);
    if (onlySelected) {
      if (Double.isNaN(avgZSelected)) {
        avgZSelected = 0;

        for (double i : inten)
          avgZSelected += i;

        avgZSelected = avgZSelected / inten.length;
      }
      return avgZSelected;
    } else {
      //
      if (Double.isNaN(averageIProcessed)) {
        averageIProcessed = 0;

        for (double i : inten)
          averageIProcessed += i;

        averageIProcessed = averageIProcessed / inten.length;
      }
      return averageIProcessed;
    }
  }


  /**
   * intensity range (max-min)
   * 
   * @param onlySelected
   * @return
   */
  public double getIRange(boolean onlySelected) {
    return this.getMaxIntensity(onlySelected) - this.getMinIntensity(onlySelected);
  }

  /**
   * 
   * @return value (set in a paintscale) as a percentage of the maximum value (value==max:
   *         result=100)
   */
  public double getIPercentage(double intensity, boolean onlySelected) {
    return (intensity / getIRange(onlySelected) * 100.0);
  }

  /**
   * 
   * @param value as percentage (0-100%)
   * @param onlySelected
   * @return value /100 * intensityRange
   */
  public double getIAbs(double value, boolean onlySelected) {
    return value / 100.0 * getIRange(onlySelected);
  }

  /**
   * 
   * @param intensity
   * @return the percentile of all intensities (if value is equal to max the result is 100)
   */
  public double getIPercentile(boolean raw, double intensity, boolean onlySelected) {
    // sort all z values
    double[] z = null;
    if (!onlySelected)
      toIArray(raw);
    else
      z = getSelectedDataAsArray(raw, true);
    Arrays.sort(z);

    for (int i = 0; i < z.length; i++) {
      if (z[i] <= intensity) {
        return (i / (z.length - 1));
      }
    }
    return 0;
  }


  /**
   * The maximum x value of a line (right edge) --> length
   * 
   * @param raw
   * @param line
   * @return
   */
  public float getMaxXRaw(, int line) {
    return data.getLastXLine(line) * (xFactor());
  }

  /**
   * The maximum x value (left edge) --> length
   * 
   * @param raw
   * @param line
   * @return
   */
  public float getMaxXRaw() {
    return data.getLastX() * (xFactor(raw));
  }

  /**
   * left edge maximum of y (bottom edge)
   * 
   * @param raw
   * @return
   */
  public float getMaxYRaw() {
    return getYRaw(data.getLinesCount());
  }

  /**
   * width of the image
   * 
   * @param raw
   * @return
   */
  public float getWidth() {
    int rot = settings.getSettImage().getRotationOfData();
    if (rot == 0 || rot == 180)
      return data.getWidthX() * xFactor();
    else
      return getYRaw(data.getLinesCount());
  }

  /**
   * height of the image
   * 
   * @param raw
   * @return
   */
  public float getHeight() {
    int rot = settings.getSettImage().getRotationOfData();
    if (rot == 90 || rot == 270)
      return data.getWidthX() * xFactor();
    else
      return getYRaw(data.getLinesCount());
  }

  /**
   * 1 or velocity
   * 
   * @param raw
   * @return
   */
  private float xFactor() {
    return settings.getSettImage().getVelocity();
  }

  /**
   * 1 or velocity
   * 
   * @param raw
   * @return
   */
  private float yFactor() {
    return settings.getSettImage().getSpotsize();
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
  public double getMaxBlockWidth() {
    return getMaxBlockWidth(getSettings().getSettImage());
  }

  public double getMaxBlockWidth(SettingsGeneralImage settImg) {
    double interpolation = settImg.isUseInterpolation() ? settImg.getInterpolation() : 1;
    return getMaxBlockWidth(settImg.getRotationOfData(), interpolation);
  }

  public double getMaxBlockWidth(int rotation, double interpolation) {
    if (rotation != 0 && rotation != 180)
      return getMaxBlockHeight(0, interpolation);
    else {
      int red = interpolation != 0 ? (int) (1 / interpolation) : 0;
      int inter = (int) interpolation;
      double f = red == 0 ? 1.0 / inter : red;
      return data.getMaxXDPWidth() * settings.getSettImage().getVelocity() * f;
    }
  }

  /**
   * maximum block height for renderer = distance between one and next block in lines
   * 
   * @return
   */
  public double getMaxBlockHeight() {
    return getMaxBlockHeight(getSettings().getSettImage());
  }

  public double getMaxBlockHeight(SettingsGeneralImage settImg) {
    double interpolation = settImg.isUseInterpolation() ? settImg.getInterpolation() : 1;
    return getMaxBlockHeight(settImg.getRotationOfData(), interpolation);
  }

  public double getMaxBlockHeight(int rotation, double interpolation) {
    if (rotation != 0 && rotation != 180)
      return getMaxBlockWidth(0, interpolation);
    else {
      // height is not changed when reducing data points
      int red = interpolation != 0 ? (int) (1 / interpolation) : 0;
      int inter = (int) interpolation;
      double f = red == 0 ? 1.0 / inter : 1;
      return settings.getSettImage().getSpotsize();
    }
  }

  /**
   * 
   * @return intensity span
   */
  public double getIntensitySpan(boolean onlySelected) {
    return getMaxIntensity(onlySelected) - getMinIntensity(onlySelected);
  }

  /**
   * save lastIProcChangeTime for comparison in all quantifiers. update all quantifiers if it has
   * changed
   */
  public void fireIntensityProcessingChanged() {
    // gives a indirect signal to Quantifier and children to change iProc
    averageIProcessed = Double.NaN;
    minZ = Double.NaN;
    maxZ = Double.NaN;
    minZSelected = Double.NaN;
    maxZSelected = Double.NaN;
    avgZSelected = Double.NaN;

    minNonZeroZ = Double.NaN;
    minNonZeroZSelected = Double.NaN;


    // register changes
    // e.g. for regression SettingsSelection
    // for (IntensityProcessingChangedListener l : listenerProcessingChanged)
    // l.fireIntensityProcessingChanged(this);
  }

  public void addIntensityProcessingChangedListener(IntensityProcessingChangedListener li) {
    listenerProcessingChanged.add(li);
  }

  public void removeIntensityProcessingChangedListener(IntensityProcessingChangedListener li) {
    listenerProcessingChanged.remove(li);
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
  public double[] getSelectedDataAsArray(boolean raw, boolean excluded) {
    double[] datasel = new double[getSelectedDPCount(true)];
    int counter = 0;
    for (int l = 0; l < data.getLinesCount(); l++) {
      for (int dp = 0; dp < data.getLineLength(l); dp++) {
        if ((!excluded || !isExcludedDP(l, dp)) && isSelectedDP(l, dp)) {
          datasel[counter] = getI(raw, l, dp);
          counter++;
        }
      }
    }
    return datasel;
  }


  /**
   * are l and dp in bounds (after rotation, reflection, ...)
   * 
   * @param l
   * @param dp
   * @return
   */
  public boolean isInBounds(int l, int dp) {
    return !(l < 0 || l >= getLineCount(dp) || dp < 0 || dp >= getLineLength(l));
  }

  /**
   * checks if a dp is excluded by a rect in excluded list
   * 
   * @param l
   * @param dp
   * @return
   */
  public boolean isExcludedDP(int l, int dp) {
    // out of bounds
    if (!isInBounds(l, dp))
      return true;

    // no exculsion rects?
    SettingsSelections sel = settings.getSettSelections();
    if (!sel.hasExclusions())
      return false;

    // coordinates
    float x = getX(l, dp);
    float y = getY(l, dp);

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
      float x = getX(l, dp);
      float y = getY(l, dp);

      // check if dp coordinates are in an sel rect
      return sel.isSelected(x, y, (float) getMaxBlockWidth(), (float) getMaxBlockHeight(), false);
    }
  }

  /**
   * checks if this is a dp with data (because of rotation and different line length)
   * 
   * @param l
   * @param dp
   * @return
   */
  public boolean isDP(int l, int dp) {
    return !Double.isNaN(getI(true, l, dp));
  }

  /**
   * Copies the Image2D and sets this to parent settings will be connected
   * 
   * @return
   * @throws Exception
   */
  public SingleParticleImage getCopyChild() throws Exception {
    SingleParticleImage copy = BinaryWriterReader.deepCopy(this);
    return copy;
  }

  /**
   * Copies the Image2D
   * 
   * @return
   * @throws Exception
   */
  public SingleParticleImage getCopy() throws Exception {
    return BinaryWriterReader.deepCopy(this);
  }

  public boolean isAllLinesSameLength() {
    for (int i = 1; i < data.getLinesCount(); i++)
      if (data.getLineLength(i - 1) != data.getLineLength(i))
        return false;

    return true;
  }


  // ###########################################################################
  // Info graphics> histogram / icons
  /**
   * creates a histogram
   * 
   * @return
   */
  public ChartPanel createHistogram(double[] data, int bin) {
    if (data != null && data.length > 0) {
      HistogramDataset dataset = new HistogramDataset();
      dataset.addSeries("histo", data, bin);

      JFreeChart chart = ChartFactory.createHistogram("", null, null, dataset,
          PlotOrientation.VERTICAL, true, false, false);

      chart.setBackgroundPaint(new Color(230, 230, 230));
      chart.getLegend().setVisible(false);
      XYPlot xyplot = (XYPlot) chart.getPlot();
      xyplot.setForegroundAlpha(0.7F);
      xyplot.setBackgroundPaint(Color.WHITE);
      xyplot.setDomainGridlinePaint(new Color(150, 150, 150));
      xyplot.setRangeGridlinePaint(new Color(150, 150, 150));
      xyplot.getDomainAxis().setVisible(true);
      xyplot.getRangeAxis().setVisible(false);
      XYBarRenderer xybarrenderer = (XYBarRenderer) xyplot.getRenderer();
      xybarrenderer.setShadowVisible(false);
      xybarrenderer.setBarPainter(new StandardXYBarPainter());
      // xybarrenderer.setDrawBarOutline(false);
      return new ChartPanel(chart);
    } else
      return null;
  }

  public ChartPanel createHistogram(double[] data) {
    int bin = (int) Math.sqrt(data.length);
    return createHistogram(data, bin);
  }

  /**
   * returns an easy icon
   * 
   * @param maxw
   * @param maxh
   * @return
   */
  @Override
  public Icon getIcon(int maxw, int maxh) {
    return null;
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

  public void cleatRawDataChangedListeners() {
    data.cleatRawDataChangedListeners();
  }

  /**
   * checks whether these two images have the same data space
   * 
   * @param i
   * @return
   */
  public boolean hasSameData(SingleParticleImage i) {
    return data.equals(i.getData()) || (data.getMaxDP() == i.getData().getMaxDP()
        || data.getMaxDP() == i.getData().getLinesCount())
        && data.getTotalDPCount() == i.getData().getTotalDPCount();
  }

  // a name for lists
  public String toListName() {
    return settings.getSettImage().toListName();
  }

}
