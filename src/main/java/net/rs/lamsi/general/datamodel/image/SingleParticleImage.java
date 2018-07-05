package net.rs.lamsi.general.datamodel.image;

import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.DoubleStream;
import javax.swing.Icon;
import org.jfree.data.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.rs.lamsi.general.datamodel.image.data.interf.ImageDataset;
import net.rs.lamsi.general.datamodel.image.data.twodimensional.XYIDataMatrix;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.datamodel.image.interf.DataCollectable2D;
import net.rs.lamsi.general.settings.image.SettingsSPImage;
import net.rs.lamsi.general.settings.image.selection.SettingsSelections;
import net.rs.lamsi.general.settings.image.special.SingleParticleSettings;
import net.rs.lamsi.general.settings.image.sub.SettingsGeneralImage;
import net.rs.lamsi.general.settings.image.sub.SettingsGeneralImage.Transformation;
import net.rs.lamsi.utils.mywriterreader.BinaryWriterReader;

// XY raw data!
// have to be multiplied with velocity and spot size
public class SingleParticleImage extends DataCollectable2D<SettingsSPImage>
    implements Serializable {
  // do not change the version!
  private static final long serialVersionUID = 1L;

  private final Logger logger = LoggerFactory.getLogger(getClass());

  // ############################################################
  // data
  // store in image in SettingsSPImage

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
    sett.setImg(img);
  }

  /**
   * the underlying image for this single particle image
   * 
   * @return
   */
  public Image2D getImage() {
    return getSettings().getImg();
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
    // if not yet filtered or settings have changed
    if (selectedFilteredData == null || !lastSelected.equals(sett)
        || !getImage().getSettings().getSettSelections().equals(lastSelections)) {
      // get data matrix of selected DP
      selectedFilteredData = getImage().toIMatrixOfSelected(true);
      logger.debug("image matrix generation");
      // filter out split events
      selectedFilteredData = filterOutSplitPixelEventsAndTransform(sett, selectedFilteredData,
          getImage().isRotated(), Transformation.NONE);
      logger.debug("split pixel event filter");

      // save settings
      try {
        lastSelected = (SingleParticleSettings) sett.copy();
        lastSelections = (SettingsSelections) getImage().getSettings().getSettSelections().copy();
      } catch (Exception e) {
        logger.error("", e);
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
    logger.debug("2D array to 1D of selected {}", size);
    return arr;
  }



  /**
   * to xycounts array in regards to the rotation, reflection and imaging mode
   * 
   * @return [lines][z] with z as number of particles
   */
  public double[][] updateFilteredDataCountsArray() {
    SingleParticleSettings sett = getSettings().getSettSingleParticle();
    return toXYCountsArray(sett);
  }

  /**
   * to xycounts array in regards to the rotation, reflection and imaging mode
   * 
   * @return [lines][z] with z as number of particles
   */
  public double[][] toXYCountsArray(SingleParticleSettings sett) {
    logger.debug("Start single particle counter on {} with {}", getImage().getTitle(),
        sett.toString());
    logger.debug("Start SPI counter: ");

    filteredData = getImage().toIMatrix(true, true);
    // filter split pixel eventstimer
    logger.debug("toIMatrixOfSelected from img");
    logger.debug("Start SPI counter: Data filtering and transform to {}", sett.getTransform());
    filteredData = filterOutSplitPixelEventsAndTransform(sett, filteredData, getImage().isRotated(),
        sett.getTransform());
    logger.debug("DONE. filter split particle events ");

    // count particles
    if (sett.isCountPixel())
      countPixelInWindow(sett.getWindow());
    return filteredData;
  }

  /**
   * Converts the transformed intensity data into binary 0/1 states where intensity in window = 1.
   * 
   * @param window
   */
  private void countPixelInWindow(Range window) {
    int particles = 0;
    if (window != null) {
      // count events
      logger.debug("Start to count particles in window:{}", window);
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
      logger.info("SPI counter: no window defined");
    logger.info("Particles in window {};  n={}", window, particles);
  }

  /**
   * Filter out split pixel events. These events are defined as consecutive high intensities>noise
   * level
   * 
   * @param data [lines][dp]
   * @param rotated if true data is used as [dp][lines]
   * @param funct function to change resulting data matrix (e.g. apply cuberoot)
   * @return
   */
  private double[][] filterOutSplitPixelEventsAndTransform(SingleParticleSettings sett,
      double[][] data, boolean rotated, Transformation funct) {

    double noise = sett.getNoiseLevel();
    int pixel = sett.getSplitPixel();
    // short circuit if pixel is 0 -> no filter applied
    if (pixel <= 0) {
      logger.debug("Split pixel filter NOT applied (pixel=0)");
      return data;
    } else {
      int solved = 0;
      int lastSelectedDPCount = 0;
      if (data != null && data.length > 0) {
        logger.debug(
            "Split pixel filter (rotated={}) on {}x{} lines x dp (noise={}, pixel={}, transformation={})",
            rotated, data.length, data[0].length, noise, pixel, funct.toString());

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
                // set the dp with max i to the sum
                // rest of data points is set to min (0)
                for (int i = 0; i < ilast; i++) {
                  if (i == imax)
                    result[l][dp - ilast + i] = funct.apply(sum);
                  else
                    result[l][dp - ilast + i] = funct.apply(min);
                }
                // reset
                ilast = 0;
                solved++;
              }
              // is greater noise?
              if (Double.isNaN(current) || current < noise) {
                // add noisy pixel
                result[l][dp] = funct.apply(current);
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
              // set the dp with max i to the sum
              // rest of data points is set to min (0)
              for (int i = 0; i < ilast; i++) {
                if (i == imax)
                  result[l][result[l].length - ilast + i] = funct.apply(sum);
                else
                  result[l][result[l].length - ilast + i] = funct.apply(min);
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
                      result[dp - ilast + i][l] = funct.apply(sum);
                    else
                      result[dp - ilast + i][l] = funct.apply(min);
                  }
                  // reset
                  ilast = 0;
                  solved++;
                }
                // is greater noise?
                if (Double.isNaN(current) || current < noise) {
                  // add noisy pixel
                  result[dp][l] = funct.apply(current);
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
                  result[result.length - 1 - ilast + i][l] = funct.apply(sum);
                else
                  result[result.length - 1 - ilast + i][l] = funct.apply(min);
              }
              // reset
              ilast = 0;
              solved++;
            }
          }
        }

        logger.info("Split pixel filter: result {} data points: solved events={}",
            lastSelectedDPCount, solved);
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
    if (getImage() == null)
      return null;
    return getImage().getIcon(maxw, maxh);
  }

  // end of visual extras
  // ###########################################################################


  public ImageDataset getData() {
    return getImage().getData();
  }

  public int getIndex() {
    return getImage().getIndex();
  }

  // a name for lists
  public String toListName() {
    return settings.getSettImage().toListName();
  }


  @Override
  public int getWidthAsMaxDP() {
    return getImage().getWidthAsMaxDP();
  }

  @Override
  public int getHeightAsMaxDP() {
    return getImage().getHeightAsMaxDP();
  }

  @Override
  public float getMaxBlockWidth(boolean postProcessing) {
    return getMaxBlockWidth(getImage().getSettings().getSettImage(), postProcessing);
  }

  @Override
  public float getMaxBlockHeight(boolean postProcessing) {
    return getMaxBlockHeight(getImage().getSettings().getSettImage(), postProcessing);
  }

  public float getMaxBlockWidth(SettingsGeneralImage settImage, boolean postProcessing) {
    int rot = settImage.getRotationOfData();
    float f = getSettings().getSettSingleParticle().getNumberOfPixel();
    if (postProcessing)
      return getImage().getMaxBlockWidth(rot, 1, 1) * f;
    else
      return getImage().getMaxBlockWidth(rot, 1, 1);
  }

  public float getMaxBlockHeight(SettingsGeneralImage settImage, boolean postProcessing) {
    int rot = settImage.getRotationOfData();
    return getImage().getMaxBlockHeight(rot, 1, 1);
  }

  public float getAvgBlockWidth(boolean postProcessing) {
    return getAvgBlockWidth(getImage().getSettings().getSettImage(), postProcessing);
  }

  public float getAvgBlockHeight(boolean postProcessing) {
    return getAvgBlockHeight(getImage().getSettings().getSettImage());
  }

  public float getAvgBlockWidth(SettingsGeneralImage settImage, boolean postProcessing) {
    int rot = settImage.getRotationOfData();
    float f = getSettings().getSettSingleParticle().getNumberOfPixel();
    if (postProcessing)
      return getImage().getAvgBlockWidth(rot, 1, 1) * f;
    else
      return getImage().getAvgBlockWidth(rot, 1, 1);
  }

  public float getAvgBlockHeight(SettingsGeneralImage settImage) {
    int rot = settImage.getRotationOfData();
    return getImage().getAvgBlockHeight(rot, 1, 1);
  }


  @Override
  public boolean hasOneDPWidth() {
    return getImage().hasOneDPWidth();
  }

  @Override
  public boolean hasOneDPHeight() {
    return getImage().hasOneDPHeight();
  }


  @Override
  public float getX0() {
    return getImage().getX0();
  }

  @Override
  public float getY0() {
    return getImage().getY0();
  }

  @Override
  public float getWidth() {
    return getImage().getWidth(false);
  }

  @Override
  public float getHeight() {
    return getImage().getHeight(false);
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
        logger.error("", e);
      }
    }
  }


  @Override
  public float getX(boolean raw, int l, int dp) {
    return getImage().getX(false, l, dp);
  }

  @Override
  public float getY(boolean raw, int l, int dp) {
    return getImage().getY(false, l, dp);
  }

  @Override
  public double getI(boolean raw, int l, int dp) {
    if (raw)
      return getImage().getI(false, l, dp);
    else {
      if (filteredData == null)
        updateFilteredDataCountsArray();
      return isInBounds(l, dp) ? filteredData[l][dp] : Double.NaN;
    }
  }

  @Override
  public int getMinLineLength() {
    return getImage().getMinLineLength();
  }

  @Override
  public int getMaxLineLength() {
    return getImage().getMaxLineLength();
  }

  @Override
  public int getMinLinesCount() {
    return getImage().getMinLinesCount();
  }

  @Override
  public int getMaxLinesCount() {
    return getImage().getMaxLinesCount();
  }

  @Override
  public int getLineLength(int l) {
    return getImage().getLineLength(l);
  }

  @Override
  public int getLineCount(int dp) {
    return getImage().getLineCount(dp);
  }

  @Override
  public double[] toIArray(boolean raw, boolean onlySelected, boolean excluded) {
    if (raw)
      // return processed data of original image
      return getImage().toIArray(false, onlySelected, excluded);
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
    XYIDataMatrix d = getImage().toXYIDataMatrix(false, useSettings, false);
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
    return getImage().getTotalDataPoints();
  }

}
