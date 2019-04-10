package net.rs.lamsi.multiimager.utils.imageimportexport;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alanmrace.jimzmlparser.imzml.ImzML;
import com.alanmrace.jimzmlparser.mzml.Spectrum;
import com.alanmrace.jimzmlparser.parser.ImzMLHandler;
import net.rs.lamsi.general.datamodel.image.ImageGroupMD;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.DatasetLinesMD;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.ScanLineMD;
import net.rs.lamsi.general.settings.importexport.SettingsImzMLImageImport;
import net.rs.lamsi.utils.threads.ProgressUpdateTask;

public class ImportImzML {
  private static final Logger logger = LoggerFactory.getLogger(ImportImzML.class);

  public static ImageGroupMD parse(File f, SettingsImzMLImageImport sett, ProgressUpdateTask task)
      throws Exception {
    // images
    double[][] mz = sett.getImportList();
    double window = sett.getWindow();
    boolean useWindow = sett.isUseWindow();
    String[] titles = createTitles(mz, window, useWindow);
    int specCount = 0;

    // all files
    try {
      logger.info("Open imzML: " + f.getAbsolutePath());
      ImzML imzml = ImzMLHandler.parseimzML(f.getAbsolutePath(), true);
      logger.info("DONE: open imzML: " + f.getAbsolutePath());
      logger.info("Width={}  Height={}", imzml.getWidth(), imzml.getHeight());

      //
      ScanLineMD[] lines = new ScanLineMD[imzml.getHeight()];
      for (int y = 0; y < imzml.getHeight(); y++) {
        // image, x
        double[][] dimensions = new double[mz.length][imzml.getWidth()];
        for (int x = 0; x < imzml.getWidth(); x++) {
          Spectrum spec = imzml.getSpectrum(x + 1, y + 1);
          // logger.info("x={} y={} , spec={}", x, y, spec);
          double[] intensities;
          if (spec != null) {
            intensities = getIntensities(spec, mz, window, useWindow);
            specCount++;
          } else {
            intensities = new double[mz.length];
            for (int i = 0; i < intensities.length; i++) {
              intensities[i] = Double.NaN;
            }
          }
          //
          for (int i = 0; i < intensities.length; i++) {
            dimensions[i][x] = intensities[i];
          }
        }
        lines[y] = new ScanLineMD(null, dimensions);
      }
      if (task != null)
        task.addProgressStep(1.0);

      logger.info("{} spectra imported", specCount);

      logger.info("Creating images now");
      // Generate Image2D from scanLines
      DatasetLinesMD dataset = new DatasetLinesMD(lines);
      ImageGroupMD group = dataset.createImageGroup(f, titles);
      return group;
    } catch (Exception e) {
      logger.error("Error in imzML import", e);
      return null;
    }
  }


  private static String[] createTitles(double[][] mz, double window, boolean useWindow) {
    String[] titles = new String[mz.length];
    for (int i = 0; i < titles.length; i++) {
      double realWindow = useWindow || mz[i][1] == 0 ? window : mz[i][1];
      titles[i] = MessageFormat.format("mz={0} ({1} width)", mz[i][0], realWindow);
    }
    return titles;
  }


  private static double[] getIntensities(Spectrum s, double[][] list, double window,
      boolean useWindow) {
    double[] intensities = new double[list.length];
    Arrays.fill(intensities, Double.NaN);
    try {
      double[] i = s.getIntensityArray();
      double[] mz = s.getmzArray();
      for (int j = 0; j < list.length; j++) {
        try {
          double currentWindow = useWindow || list[j][1] == 0 ? window : list[j][1];
          intensities[j] = getMaxIntensity(mz, i, list[j][0], currentWindow);
        } catch (Exception e) {
          intensities[j] = Double.NaN;
        }
      }
    } catch (IOException e) {
      logger.error("Cannot load intensities ", e);
    }
    return intensities;
  }

  /**
   * maximum intensity in range centermz+-pm
   * 
   * @param mz
   * @param i
   * @param centermz
   * @param pm
   * @return
   */
  private static double getMaxIntensity(double[] mz, double[] i, double centermz, double pm) {
    double max = 0;
    for (int x = 0; x < mz.length; x++) {
      if (mz[x] >= centermz - pm && mz[x] <= centermz + pm)
        if (i[x] > max)
          max = i[x];
    }
    return max;
  }
}
