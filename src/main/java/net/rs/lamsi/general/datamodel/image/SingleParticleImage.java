package net.rs.lamsi.general.datamodel.image;

import java.io.Serializable;
import javax.swing.Icon;
import net.rs.lamsi.general.datamodel.image.data.interf.ImageDataset;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.settings.image.SettingsSPImage;
import net.rs.lamsi.general.settings.image.special.SingleParticleSettings;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow.LOG;
import net.rs.lamsi.utils.mywriterreader.BinaryWriterReader;

// XY raw data!
// have to be multiplied with velocity and spot size
public class SingleParticleImage extends Collectable2D<SettingsSPImage> implements Serializable {
  // do not change the version!
  private static final long serialVersionUID = 1L;

  // ############################################################
  // data
  protected final Image2D img;

  // empty dp == null
  protected double[][] selectedFilteredData;
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
  public double[] getSPDataArray() {
    SingleParticleSettings sett = getSettings().getSettSingleParticle();
    return getSPDataArray(sett);
  }

  public double[] getSPDataArray(SingleParticleSettings sett) {
    // the data
    selectedFilteredData = img.toIMatrixOfSelected(false);
    // filter out split events
    selectedFilteredData = filterOutSplitPixelEvents(sett, selectedFilteredData, img.isRotated());

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
    return arr;
  }

  /**
   * 
   * @param data [dp][lines]
   * @param rotated if true data is used as [lines][dp]
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
      if (rotated) {
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
