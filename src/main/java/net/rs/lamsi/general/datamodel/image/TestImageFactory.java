package net.rs.lamsi.general.datamodel.image;

import java.util.Arrays;
import java.util.Random;
import org.jfree.data.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.DatasetLinesMD;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.ScanLineMD;
import net.rs.lamsi.general.heatmap.dataoperations.DPReduction.Mode;
import net.rs.lamsi.general.settings.image.SettingsImageOverlay;
import net.rs.lamsi.general.settings.image.special.SingleParticleSettings;

public class TestImageFactory {
  private static final Logger logger = LoggerFactory.getLogger(TestImageFactory.class);


  public static ImageGroupMD createNonNormalImage(int c) {
    Random rand = new Random(System.currentTimeMillis());
    ScanLineMD[] lines = new ScanLineMD[24];
    for (int f = 0; f < c; f++) {
      for (int l = 0; l < lines.length; l++) {
        double[] i = new double[240 - l * 2];
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
        double[] i = new double[240];
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
        double[] i = new double[100];
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
        double[] data = new double[dp];
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
      double[] data = new double[dp];
      for (int l = 0; l < dp; l++) {
        data[l] = rand.nextGaussian() + 1;
      }
      lines[i] = new ScanLineMD(data);
    }
    DatasetLinesMD data = new DatasetLinesMD(lines);
    ImageGroupMD img = data.createImageGroup("Gaussian");
    return img;
  }

  /**
   * Creates a perfect single particle image
   * 
   * @param rows lines
   * @param dp dp per line
   * @param particlesPerLine minimum 2
   * @param noise
   * @param intensity has to be at least >4 times the noise
   * @return
   */
  public static SingleParticleImage createPerfectSingleParticleImg(int rows, int dp,
      int particlesPerLine, int noise, int intensity, int splitPixel, boolean addCluster) {

    Random rand = new Random(System.currentTimeMillis());
    double f = 0.6;
    ScanLineMD[] lines = new ScanLineMD[rows];
    // fill with noise
    // add particle to start and end of lines
    for (int i = 0; i < rows; i++) {
      double[] data = new double[dp];
      for (int l = 0; l < dp; l++) {
        // first line first dp
        if ((i == l) || (l == dp - 2 - i)) {
          // first data point
          // or second last dp
          data[l] = intensity * f;
        } else if ((i == l - 1) || (l == dp - 1 - i)) {
          // second data point
          // or last dp
          data[l] = intensity * (1 - f);
        } else
          data[l] = (double) noise;
      }
      lines[i] = new ScanLineMD(data);
    }
    // place cluster
    if (addCluster) {
      // first is always a cluster:
      for (ScanLineMD l : lines) {
        double[] data = l.getIntensity().get(0);
        int clusterPixel = splitPixel * 8;
        // place at middle
        int i = (dp - clusterPixel) / 2;
        // add
        for (int x = 0; x < clusterPixel; x++)
          data[i + x] = intensity;
      }
    }
    // first is always a cluster:
    // add more particles
    for (ScanLineMD l : lines) {
      double[] data = l.getIntensity().get(0);
      int placed = 2;
      while (placed < particlesPerLine) {
        // place at random
        int i = rand.nextInt(dp - (splitPixel - 1));
        // only place if no particle is near
        boolean free = true;
        for (int d = Math.max(0, i - 1); free && d < Math.min(dp, i + splitPixel); d++)
          if (data[d] > noise + 1)
            free = false;

        // add
        if (free) {
          data[i] = intensity * f;
          for (int x = 1; x < splitPixel; x++)
            data[i + x] = intensity * (1.0 - f) / (double) (splitPixel - 1);
          placed++;
        }
      }
    }
    // the sum of all split pixel is always = intensity

    // create img
    DatasetLinesMD data = new DatasetLinesMD(lines);
    ImageGroupMD img = data.createImageGroup("SP Test");
    // create spimg
    SingleParticleImage spimg = new SingleParticleImage(img.getFirstImage2D());
    SingleParticleSettings sett = spimg.getSettings().getSettSingleParticle();
    sett.setAll(noise + 1, splitPixel, splitPixel * 2, true,
        new Range(intensity - 2, intensity + 2), 1, true, Mode.MAX);
    // add to group
    img.add(spimg);
    return spimg;
  }
}
