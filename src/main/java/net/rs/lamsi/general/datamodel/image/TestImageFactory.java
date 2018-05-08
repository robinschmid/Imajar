package net.rs.lamsi.general.datamodel.image;

import java.util.Arrays;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.DatasetLinesMD;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.ScanLineMD;
import net.rs.lamsi.general.settings.image.SettingsImageOverlay;

public class TestImageFactory {
  private static final Logger logger = LoggerFactory.getLogger(TestImageFactory.class);


  public static ImageGroupMD createNonNormalImage(int c) {
    Random rand = new Random(System.currentTimeMillis());
    ScanLineMD[] lines = new ScanLineMD[24];
    for (int f = 0; f < c; f++) {
      for (int l = 0; l < lines.length; l++) {
        Double[] i = new Double[240 - l * 2];
        for (int d = 0; d < i.length; d++) {
          // middle the highest
          double in = (int) (l / 4) * 200.0;
          in += Math.abs(rand.nextInt(6000) / 100.0);
          // create dp
          i[d] = in;
        }
        if (lines[l] == null)
          lines[l] = new ScanLineMD(i);
        else
          lines[l].addDimension(i);
      }
    }
    DatasetLinesMD data = new DatasetLinesMD(lines);
    return data.createImageGroup("Non Normal");
  }


  /**
   * test images
   * 
   * @return
   */
  public static ImageGroupMD createTestStandard(int c) {
    Random rand = new Random(System.currentTimeMillis());
    ScanLineMD[] lines = new ScanLineMD[24];
    for (int f = 0; f < c; f++) {
      for (int l = 0; l < lines.length; l++) {
        Double[] i = new Double[240];
        for (int d = 0; d < i.length; d++) {
          // middle the highest
          double in = (int) (l / 4) * 200.0;
          in += Math.abs(rand.nextInt(6000) / 100.0);
          // create dp
          i[d] = in;
        }
        if (lines[l] == null)
          lines[l] = new ScanLineMD(i);
        else
          lines[l].addDimension(i);
      }
    }
    DatasetLinesMD data = new DatasetLinesMD(lines);
    return data.createImageGroup("Test Standards");
  }


  /**
   * test images
   * 
   * @return
   */
  public static ImageGroupMD createOverlayTest() {
    Random rand = new Random(System.currentTimeMillis());
    ScanLineMD[] lines = new ScanLineMD[50];
    // 3 images
    int c = 3;
    for (int f = 0; f < c; f++) {
      for (int l = 0; l < lines.length; l++) {
        Double[] i = new Double[100];
        for (int d = 0; d < i.length; d++) {
          // middle the highest
          double in = 0;
          switch (f) {
            case 0:
              in = Math.sin(d) + Math.sin(l) + rand.nextInt(1000) / 1000.0;
              break;
            case 1:
              in = Math.cos(d) + Math.cos(l) + rand.nextInt(1000) / 1000.0;
              break;
            case 2:
              in = l + d;
              break;
          }
          // create dp
          i[d] = in;
        }
        if (lines[l] == null)
          lines[l] = new ScanLineMD(i);
        else
          lines[l].addDimension(i);
      }
    }
    DatasetLinesMD data = new DatasetLinesMD(lines);
    ImageGroupMD img = data.createImageGroup("Overlay Test");

    // add overlay
    try {
      SettingsImageOverlay settings = new SettingsImageOverlay();
      ImageOverlay ov = new ImageOverlay(img, settings);
      img.add(ov);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      logger.error("", e);
    }

    return img;
  }


  /**
   * Creates a perfect standard
   * 
   * @param n number of standards
   * @param rowsPerStandard rows per standard
   * @param dp data points per row
   * @return
   */
  public static ImageGroupMD createPerfectStandard(int n, int rowsPerStandard, int dp) {
    ScanLineMD[] lines = new ScanLineMD[n * rowsPerStandard];
    for (int i = 0; i < n; i++) {
      for (int l = 0; l < rowsPerStandard; l++) {
        Double[] data = new Double[dp];
        Arrays.fill(data, (double) i);
        lines[i * rowsPerStandard + l] = new ScanLineMD(data);
      }
    }
    DatasetLinesMD data = new DatasetLinesMD(lines);
    ImageGroupMD img = data.createImageGroup("Perfect standard test");
    return img;
  }


  /**
   * Creates a gaussian (mean 1, sd 1) distribution
   * 
   * @param rows
   * @param dp
   * @return
   */
  public static ImageGroupMD createGaussianTest(int rows, int dp) {
    Random rand = new Random(System.currentTimeMillis());
    ScanLineMD[] lines = new ScanLineMD[rows];
    for (int i = 0; i < rows; i++) {
      Double[] data = new Double[dp];
      for (int l = 0; l < dp; l++) {
        data[l] = rand.nextGaussian() + 1;
      }
      lines[i] = new ScanLineMD(data);
    }
    DatasetLinesMD data = new DatasetLinesMD(lines);
    ImageGroupMD img = data.createImageGroup("Gaussian");
    return img;
  }
}
