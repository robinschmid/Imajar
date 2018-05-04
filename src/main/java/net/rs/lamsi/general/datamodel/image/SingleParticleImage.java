package net.rs.lamsi.general.datamodel.image;

import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.DoubleStream;
import javax.swing.Icon;
import org.jfree.data.Range;
import net.rs.lamsi.general.datamodel.image.data.interf.ImageDataset;
import net.rs.lamsi.general.datamodel.image.data.twodimensional.XYIDataMatrix;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.datamodel.image.interf.DataCollectable2D;
import net.rs.lamsi.general.settings.image.SettingsSPImage;
import net.rs.lamsi.general.settings.image.selection.SettingsSelections;
import net.rs.lamsi.general.settings.image.special.SingleParticleSettings;
import net.rs.lamsi.general.settings.image.sub.SettingsGeneralImage;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow.LOG;
import net.rs.lamsi.utils.mywriterreader.BinaryWriterReader;
import net.rs.lamsi.utils.useful.DebugStopWatch;

// XY raw data!
// have to be multiplied with velocity and spot size
public class SingleParticleImage extends DataCollectable2D<SettingsSPImage>
    implements Serializable {
  // do not change the version!
  private static final long serialVersionUID = 1L;

  // ############################################################
  // data
  protected final Image2D img;

  // last settings for calculations
  protected SingleParticleSettings lastSelected, lastFull;
  protected SettingsSelections lastSelections;

  // empty dp == null
  protected double[][] selectedFilteredData;
  protected double[][] filteredData;


  public SingleParticleImage(Image2D img) {
    this(img, new SettingsSPImage());
  }

  public SingleParticleImage(Image2D img, SettingsSPImage sett) {
    super(sett);
    this.img = img;
    sett.setImg(img);
  }

  /**
   * the underlying image for this single particle image
   * 
   * @return
   */
  public Image2D getImage() {
    return img;
  }


  /**
   * Filtered data array from selected data points
   * 
   * @return
   */
  public double[] getSPDataArraySelected() {
    SingleParticleSettings sett = getSettings().getSettSingleParticle();
    return getSPDataArraySelected(sett);
  }

  /**
   * Filtered data array from selected data points Split particle events are filtered out
   * 
   * @param sett
   * @return
   */
  public double[] getSPDataArraySelected(SingleParticleSettings sett) {
    // the data
    DebugStopWatch timer = new DebugStopWatch();

    // if not yet filtered or settings have changed
    if (selectedFilteredData == null || !lastSelected.equals(sett)
        || !img.getSettings().getSettSelections().equals(lastSelections)) {
      // get data matrix of selected DP
      selectedFilteredData = img.toIMatrixOfSelected(false);
      timer.stopAndLOG("image matrix generation");
      // filter out split events
      selectedFilteredData = filterOutSplitPixelEvents(sett, selectedFilteredData, img.isRotated());
      timer.stopAndLOG("split pixel event filter");

      // save settings
      try {
        lastSelected = (SingleParticleSettings) sett.copy();
        lastSelections = (SettingsSelections) img.getSettings().getSettSelections().copy();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    // to array
    int size = 0;
    for (int x = 0; x < selectedFilteredData.length; x++)
      for (int y = 0; y < selectedFilteredData[x].length; y++)
        if (!Double.isNaN(selectedFilteredData[x][y]))
          size++;

    int i = 0;
    double[] arr = new double[size];

    for (int x = 0; x < selectedFilteredData.length; x++) {
      for (int y = 0; y < selectedFilteredData[x].length; y++) {
        if (!Double.isNaN(selectedFilteredData[x][y])) {
          arr[i] = selectedFilteredData[x][y];
          i++;
        }
      }
    }
    timer.stopAndLOG("2D array to 1D of " + size + " selected");
    return arr;
  }



  /**
   * to xycounts array in regards to the rotation, reflection and imaging mode
   * 
   * @return [lines][x,y,z] with z as number of particles
   */
  public double[][] updateFilteredDataCountsArray() {
    SingleParticleSettings sett = getSettings().getSettSingleParticle();
    return toXYCountsArray(sett);
  }

  /**
   * to xycounts array in regards to the rotation, reflection and imaging mode
   * 
   * @return [lines][x,y,z] with z as number of particles
   */
  public double[][] toXYCountsArray(SingleParticleSettings sett) {
    ImageEditorWindow.log("Start SPI counter: ", LOG.MESSAGE);

    DebugStopWatch timer = new DebugStopWatch();
    filteredData = img.toIMatrix(false, true);
    // filter split pixel eventstimer
    timer.stopAndLOG("toIMatrixOfSelected from img");
    ImageEditorWindow.log("Start SPI counter: Data filtering", LOG.MESSAGE);
    filteredData = filterOutSplitPixelEvents(sett, filteredData, img.isRotated());
    timer.stopAndLOG("filter split particle events");

    int particles = 0;
    Range window = sett.getWindow();
    if (window != null) {
      // count events
      ImageEditorWindow.log("Start SPI counter: Count particles in window" + window.toString(),
          LOG.MESSAGE);
      for (int i = 0; i < filteredData.length; i++) {
        for (int j = 0; j < filteredData[i].length; j++) {
          if (!Double.isNaN(filteredData[i][j])) {
            if (window.contains(filteredData[i][j])) {
              filteredData[i][j] = 1;
              particles++;
            } else
              filteredData[i][j] = 0;
          }
        }
      }
    } else
      ImageEditorWindow.log("SPI counter: no window defined", LOG.MESSAGE);

    ImageEditorWindow.log("SPI counter DONE: particles:" + particles, LOG.MESSAGE);
    return filteredData;
  }

  /**
   * Filter out split pixel events. These events are defined as consecutive high intensities>noise
   * level
   * 
   * @param data [lines][dp]
   * @param rotated if true data is used as [dp][lines]
   * @return
   */
  private double[][] filterOutSplitPixelEvents(SingleParticleSettings sett, double[][] data,
      boolean rotated) {

    double noise = sett.getNoiseLevel();
    int pixel = sett.getSplitPixel();
    // short circuit if pixel is 0 -> no filter applied
    if (pixel <= 0) {
      ImageEditorWindow.log("No split pixel filter applied because split pixel was 0", LOG.MESSAGE);
      return data;
    } else {
      ImageEditorWindow.log("Filtering data array " + data.length + "x" + data[0].length
          + " with split pixel=" + pixel + " and noise=" + noise, LOG.MESSAGE);
      int solved = 0;
      int lastSelectedDPCount = 0;
      if (data != null && data.length > 0) {
        int maxlength = 0;
        double[][] result = new double[data.length][];
        for (int i = 0; i < data.length; i++) {
          result[i] = new double[data[i].length];
          if (data[i].length > maxlength)
            maxlength = i;
        }

        // number of pixels to accumulate for split pixel events
        double[] last = new double[pixel];
        int ilast = 0;
        // rotated?
        if (!rotated) {
          // [lines][dp]
          // find minimum value as background
          double min = Double.MAX_VALUE;
          // second smallest
          double min2 = min;
          for (int dp = 0; dp < data[0].length; dp++) {
            double v = data[0][dp];
            if (!Double.isNaN(v)) {
              if (v < min)
                min = v;
              else if (v > min && v < min2)
                min2 = v;
            }
          }
          // safety: filter out min value by setting noise to at least min2
          if (noise <= min)
            noise = min2;

          // for lines and dp accumulate pixel and add the sum to result
          for (int l = 0; l < data.length; l++) {
            for (int dp = 0; dp < data[l].length; dp++) {
              double current = data[l][dp];
              // stop accumulation
              if (ilast > 0 && (Double.isNaN(current) || current < noise || ilast >= pixel)) {
                // finish this split pixel event
                double sum = 0;
                int imax = 0;
                for (int i = 0; i < ilast; i++) {
                  sum += last[i];
                  if (last[imax] < last[i])
                    imax = i;
                }
                // add all data points as sum or min
                for (int i = 0; i < ilast; i++) {
                  if (i == imax)
                    result[l][dp - ilast + i] = sum;
                  else
                    result[l][dp - ilast + i] = min;
                }
                // reset
                ilast = 0;
                solved++;
              }
              // is greater noise?
              if (Double.isNaN(current) || current < noise) {
                // add noisy pixel
                result[l][dp] = current;
              } else {
                // accumulate pixel
                if (ilast < pixel) {
                  last[ilast] = current;
                  ilast++;
                }
              }

              // count non-NaN
              if (!Double.isNaN(current))
                lastSelectedDPCount++;
            }
            // resolve last split event
            if (ilast > 0) {
              // finish this split pixel event
              double sum = 0;
              int imax = 0;
              for (int i = 0; i < ilast; i++) {
                sum += last[i];
                if (last[imax] < last[i])
                  imax = i;
              }
              // add all data points as sum or min
              for (int i = 0; i < ilast; i++) {
                if (i == imax)
                  result[l][result[l].length - ilast + i] = sum;
                else
                  result[l][result[l].length - ilast + i] = min;
              }
              // reset
              ilast = 0;
              solved++;
            }
          }
        } else {
          // non rotated [dp][line]
          // find minimum value as background
          double min = Double.MAX_VALUE;
          for (int dp = 0; dp < data.length; dp++)
            if (!Double.isNaN(data[dp][0]) && data[dp][0] < min)
              min = data[dp][0];

          // for lines and dp accumulate pixel and add the sum to result
          for (int l = 0; l < maxlength; l++) {
            for (int dp = 0; dp < data.length; dp++) {
              if (l < data[dp].length) {
                double current = data[dp][l];
                // stop accumulation
                if (ilast > 0 && (Double.isNaN(current) || current < noise || ilast >= pixel)) {
                  // finish this split pixel event
                  double sum = 0;
                  int imax = 0;
                  for (int i = 0; i < ilast; i++) {
                    sum += last[i];
                    if (last[imax] < last[i])
                      imax = i;
                  }
                  // add all data points as sum or min
                  for (int i = 0; i < ilast; i++) {
                    if (i == imax)
                      result[dp - ilast + i][l] = sum;
                    else
                      result[dp - ilast + i][l] = min;
                  }
                  // reset
                  ilast = 0;
                  solved++;
                }
                // is greater noise?
                if (Double.isNaN(current) || current < noise) {
                  // add noisy pixel
                  result[dp][l] = current;
                } else {
                  // accumulate pixel
                  if (ilast < pixel) {
                    last[ilast] = current;
                    ilast++;
                  }
                }

                // count non-NaN
                if (!Double.isNaN(current))
                  lastSelectedDPCount++;
              }
            }
            // resolve last split event
            if (ilast > 0) {
              // finish this split pixel event
              double sum = 0;
              int imax = 0;
              for (int i = 0; i < ilast; i++) {
                sum += last[i];
                if (last[imax] < last[i])
                  imax = i;
              }
              // add all data points as sum or min
              for (int i = 0; i < ilast; i++) {
                if (i == imax)
                  result[result.length - 1 - ilast + i][l] = sum;
                else
                  result[result.length - 1 - ilast + i][l] = min;
              }
              // reset
              ilast = 0;
              solved++;
            }
          }
        }

        ImageEditorWindow.log(
            "Filtered data array size " + lastSelectedDPCount + " solved events " + solved,
            LOG.MESSAGE);
        // return
        return result;
      } else
        return null;
    }
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
    return img.getIcon(maxw, maxh);
  }

  // end of visual extras
  // ###########################################################################


  public ImageDataset getData() {
    return img.getData();
  }

  public int getIndex() {
    return img.getIndex();
  }

  // a name for lists
  public String toListName() {
    return settings.getSettImage().toListName();
  }


  @Override
  public int getWidthAsMaxDP() {
    return img.getWidthAsMaxDP();
  }

  @Override
  public int getHeightAsMaxDP() {
    return img.getHeightAsMaxDP();
  }

  public double getMaxBlockWidth() {
    return getMaxBlockWidth(img.getSettings().getSettImage());
  }

  public double getMaxBlockHeight() {
    return getMaxBlockHeight(img.getSettings().getSettImage());
  }

  public double getMaxBlockWidth(SettingsGeneralImage settImage) {
    int rot = settImage.getRotationOfData();
    double f = getSettings().getSettSingleParticle().getNumberOfPixel();
    return img.getMaxBlockWidth(rot, 1, 1) * f;
  }

  public double getMaxBlockHeight(SettingsGeneralImage settImage) {
    int rot = settImage.getRotationOfData();
    return img.getMaxBlockHeight(rot, 1, 1);
  }

  public float getAvgBlockWidth() {
    return getAvgBlockWidth(img.getSettings().getSettImage());
  }

  public float getAvgBlockHeight() {
    return getAvgBlockHeight(img.getSettings().getSettImage());
  }

  public float getAvgBlockWidth(SettingsGeneralImage settImage) {
    int rot = settImage.getRotationOfData();
    float f = getSettings().getSettSingleParticle().getNumberOfPixel();
    return img.getAvgBlockWidth(rot, 1, 1) * f;
  }

  public float getAvgBlockHeight(SettingsGeneralImage settImage) {
    int rot = settImage.getRotationOfData();
    return img.getAvgBlockHeight(rot, 1, 1);
  }


  @Override
  public boolean hasOneDPWidth() {
    return img.hasOneDPWidth();
  }

  @Override
  public boolean hasOneDPHeight() {
    return img.hasOneDPHeight();
  }


  @Override
  public float getX0() {
    return img.getX0();
  }

  @Override
  public float getY0() {
    return img.getY0();
  }

  @Override
  public float getWidth() {
    return img.getWidth(false);
  }

  @Override
  public float getHeight() {
    return img.getHeight(false);
  }

  @Override
  public String getTitle() {
    return settings.getSettImage().getTitle();
  }

  @Override
  public String getShortTitle() {
    String s = settings.getSettImage().getShortTitle();
    return s.length() > 0 ? s : getTitle();
  }

  @Override
  public void applySettingsToOtherImage(Collectable2D img2) {
    if (img2.isSPImage()) {
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


  @Override
  public float getX(boolean raw, int l, int dp) {
    return img.getX(false, l, dp);
  }

  @Override
  public float getY(boolean raw, int l, int dp) {
    return img.getY(false, l, dp);
  }

  @Override
  public double getI(boolean raw, int l, int dp) {
    if (raw)
      return img.getI(false, l, dp);
    else {
      if (filteredData == null)
        updateFilteredDataCountsArray();
      return isInBounds(l, dp) ? filteredData[l][dp] : Double.NaN;
    }
  }

  @Override
  public int getMinLineLength() {
    return img.getMinLineLength();
  }

  @Override
  public int getMaxLineLength() {
    return img.getMaxLineLength();
  }

  @Override
  public int getMinLinesCount() {
    return img.getMinLinesCount();
  }

  @Override
  public int getMaxLinesCount() {
    return img.getMaxLinesCount();
  }

  @Override
  public int getLineLength(int l) {
    return img.getLineLength(l);
  }

  @Override
  public int getLineCount(int dp) {
    return img.getLineCount(dp);
  }

  @Override
  public double[] toIArray(boolean raw, boolean onlySelected, boolean excluded) {
    if (raw)
      // return processed data of original image
      return img.toIArray(false, onlySelected, excluded);
    else {
      if (filteredData == null)
        updateFilteredDataCountsArray();
      return Arrays.stream(filteredData).flatMapToDouble(DoubleStream::of).toArray();
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
    XYIDataMatrix d = img.toXYIDataMatrix(false, useSettings, false);
    if (raw) {
      return d;
    } else {
      if (filteredData == null)
        updateFilteredDataCountsArray();
      d.setI(filteredData);
      return d;
    }
  }

  @Override
  public void fireIntensityProcessingChanged() {
    super.fireIntensityProcessingChanged();
    filteredData = null;
  }

  @Override
  public int getTotalDataPoints() {
    return img.getTotalDataPoints();
  }
}
