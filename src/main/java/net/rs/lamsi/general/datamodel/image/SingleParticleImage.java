package net.rs.lamsi.general.datamodel.image;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.swing.Icon;
import org.jfree.data.Range;
import net.rs.lamsi.general.datamodel.image.data.interf.ImageDataset;
import net.rs.lamsi.general.datamodel.image.data.twodimensional.XYIDataMatrix;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
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
public class SingleParticleImage extends Collectable2D<SettingsSPImage> implements Serializable {
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
  protected int lastSelectedDPCount;


  public SingleParticleImage(Image2D img) {
    this(img, new SettingsSPImage());
  }

  public SingleParticleImage(Image2D img, SettingsSPImage sett) {
    super(sett);
    this.img = img;
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

  public double[] getSPDataArraySelected(SingleParticleSettings sett) {
    // the data
    DebugStopWatch timer = new DebugStopWatch();

    // if not yet filtered or setings have changed
    if (selectedFilteredData == null || lastSelected.getSplitPixel() != sett.getSplitPixel()
        || Double.compare(lastSelected.getNoiseLevel(), sett.getNoiseLevel()) != 0
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
    int i = 0;
    double[] arr = new double[lastSelectedDPCount];

    for (int x = 0; x < selectedFilteredData.length; x++) {
      for (int y = 0; y < selectedFilteredData[x].length; y++) {
        if (!Double.isNaN(selectedFilteredData[x][y])) {
          arr[i] = selectedFilteredData[x][y];
          i++;
        }
      }
    }
    timer.stopAndLOG("2D array to 1D");
    return arr;
  }



  /**
   * to xycounts array in regards to the rotation, reflection and imaging mode
   * 
   * @return [lines][x,y,z] with z as number of particles
   */
  public double[][] toXYCountsArray() {
    SingleParticleSettings sett = getSettings().getSettSingleParticle();
    return toXYCountsArray(sett);
  }

  /**
   * to xycounts array in regards to the rotation, reflection and imaging mode
   * 
   * @return [lines][x,y,z] with z as number of particles
   */
  public double[][] toXYCountsArray(SingleParticleSettings sett) {
    XYIDataMatrix matrix = img.toXYIDataMatrix(false, true);
    Float[][] x = matrix.getX();
    Float[][] y = matrix.getY();
    Double[][] z = matrix.getI();
    ImageEditorWindow.log("Start SPI counter: ", LOG.MESSAGE);

    DebugStopWatch timer = new DebugStopWatch();

    filteredData = new double[z.length][];
    for (int i = 0; i < z.length; i++)
      filteredData[i] = new double[z[i].length];

    for (int i = 0; i < z.length; i++) {
      for (int k = 0; k < z[i].length; k++) {
        filteredData[i][k] = z[i][k];
      }
    }

    // filter split pixel eventstimer
    timer.stopAndLOG("XYIDataMatrix from img");
    ImageEditorWindow.log("Start SPI counter: Data filtering", LOG.MESSAGE);
    filteredData = filterOutSplitPixelEvents(sett, filteredData, img.isRotated());
    timer.stopAndLOG("filter split particle events");

    int numberOfPixel = sett.getNumberOfPixel();
    int ipixel = 0;
    Range window = sett.getWindow();

    LinkedList<Float> fx = new LinkedList<Float>();
    LinkedList<Float> fy = new LinkedList<Float>();
    LinkedList<Double> fz = new LinkedList<Double>();

    double counter = 0;

    ImageEditorWindow.log("Start SPI counter: Count particles", LOG.MESSAGE);
    // count particles in numberOfPixel pixel and in window
    // TODO line-wise?
    if (!img.isRotated()) {
      for (int l = 0; l < filteredData.length; l++) {
        float cx = -1;
        float cy = -1;
        for (int dp = 0; dp < filteredData[l].length; dp++) {
          // init with first dp of line
          if (cx == -1) {
            cx = x[l][dp];
            cy = y[l][dp];
          }
          // for all dp
          if (cx != -1) {
            ipixel++;
            // in window?
            if (!Double.isNaN(filteredData[l][dp]) && window.contains(filteredData[l][dp])) {
              counter++;
            }
            // enough pixel?
            if (ipixel >= numberOfPixel) {
              // add
              fx.add(cx);
              fy.add(cy);
              fz.add(counter);
              // reset
              ipixel = 0;
              cx = -1;
              cy = -1;
              counter = 0;
            }
          }
        }
      }
    } else {
      int max = 0;
      for (int l = 0; l < filteredData.length; l++)
        if (filteredData.length > max)
          max = filteredData.length;

      for (int l = 0; l < max; l++) {
        float cx = -1;
        float cy = -1;
        for (int dp = 0; dp < filteredData.length; dp++) {
          if (l < filteredData[dp].length) {
            // init with first dp of line
            if (cx == -1) {
              cx = x[dp][l];
              cy = y[dp][l];
            }
            // for all dp
            if (cx != -1) {
              ipixel++;
              // in window?
              if (!Double.isNaN(filteredData[dp][l]) && window.contains(filteredData[dp][l])) {
                counter++;
              }
              // enough pixel?
              if (ipixel >= numberOfPixel) {
                // add
                fx.add(cx);
                fy.add(cy);
                fz.add(counter);
                // reset
                ipixel = 0;
                cx = -1;
                cy = -1;
                counter = 0;
              }
            }
          }
        }
      }
    }
    //
    timer.stopAndLOG("counted particle events of " + fx.size() + " pixels");

    ImageEditorWindow.log("Start SPI counter: DONE", LOG.MESSAGE);
    ImageEditorWindow.log("Start SPI counter: Tranfer to array", LOG.MESSAGE);
    // transform to XYZ array [lines][x,y,z]
    double[][] arr = new double[3][fx.size()];
    for (int i = 0; i < fx.size(); i++) {
      arr[0][i] = fx.get(i);
      arr[1][i] = fy.get(i);
      arr[2][i] = fz.get(i);
    }
    timer.stopAndLOG("transfer to array finished");
    return arr;
  }

  /**
   * 
   * @param data [lines][dp]
   * @param rotated if true data is used as [dp][lines]
   * @return
   */
  private double[][] filterOutSplitPixelEvents(SingleParticleSettings sett, double[][] data,
      boolean rotated) {
    ImageEditorWindow.log("Filtering data array " + data.length + "x" + data[0].length,
        LOG.MESSAGE);

    int solved = 0;
    lastSelectedDPCount = 0;
    if (data != null && data.length > 0) {
      int maxlength = 0;
      double[][] result = new double[data.length][];
      for (int i = 0; i < data.length; i++) {
        result[i] = new double[data[i].length];
        if (data[i].length > maxlength)
          maxlength = i;
      }

      // number of pixels to accumulate for split pixel events
      int pixel = sett.getSplitPixel();
      double noise = sett.getNoiseLevel();
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

        // safety
        if (noise <= min)
          noise = min2;

        // for lines and dp accumulate pixel and add the sum to result
        for (int l = 0; l < data.length; l++) {
          for (int dp = 0; dp < data[l].length; dp++) {
            double current = data[l][dp];
            // stop accumulation
            if (ilast > 0 && (Double.isNaN(current) || current < noise || ilast >= last.length)) {
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
              if (ilast < last.length) {
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
              if (ilast > 0 && (Double.isNaN(current) || current < noise || ilast >= last.length)) {
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
                if (ilast < last.length) {
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

  /**
   * Returns all selected and not excluded data points to an array
   * 
   * @return
   */
  public double[] getSelectedDataAsArray(boolean raw, boolean excluded) {
    return img.getSelectedDataAsArray(raw, excluded);
  }

  /**
   * Returns all selected and not excluded data points to an array
   * 
   * @return
   */
  public List<Double> getSelectedDataAsList(boolean raw, boolean excluded) {
    return img.getSelectedDataAsList(raw, excluded);
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
    return getMaxBlockWidth(getSettings().getSettImage());
  }

  public double getMaxBlockHeight() {
    return getMaxBlockHeight(getSettings().getSettImage());
  }

  public double getMaxBlockWidth(SettingsGeneralImage settImage) {
    int rot = getSettings().getSettImage().getRotationOfData();
    double f = getSettings().getSettSingleParticle().getNumberOfPixel();
    return img.getMaxBlockWidth(rot, 1) * f;
  }

  public double getMaxBlockHeight(SettingsGeneralImage settImage) {
    int rot = getSettings().getSettImage().getRotationOfData();
    return img.getMaxBlockHeight(rot, 1);
  }

  public float getWidth() {
    return img.getWidth(false);
  }

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

}
