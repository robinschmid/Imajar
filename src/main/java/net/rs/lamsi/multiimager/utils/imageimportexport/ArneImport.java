package net.rs.lamsi.multiimager.utils.imageimportexport;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.ImageGroupMD;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.DatasetLinesMD;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.ScanLineMD;
import net.rs.lamsi.general.settings.importexport.SettingsImageDataImportTxt;
import net.rs.lamsi.utils.mywriterreader.TxtWriter;
import net.rs.lamsi.utils.threads.ProgressUpdateTask;

public class ArneImport {

  private static final Logger logger = LoggerFactory.getLogger(ArneImport.class);


  public static ImageGroupMD[] parse(File[] files, SettingsImageDataImportTxt sett,
      ProgressUpdateTask task) throws Exception {
    // images
    ArrayList<ImageGroupMD> groups = new ArrayList<ImageGroupMD>();

    // all files
    for (File f : files) {
      ImageGroupMD group = parseFile(f, sett);


      // newly loaded group size is 1 and dimensions are the same?
      if (!groups.isEmpty() && group.size() == 1
          && group.getData().hasSameDataDimensionsAs(groups.get(0).getData())) {
        // add to data set
        groups.get(0).getData().addDimension((Image2D) group.get(0));
        // add image to first group
        groups.get(0).add(group.get(0));
      }
      // else - add to group list
      else {
        if (group != null)
          groups.add(group);
      }

      if (task != null)
        task.addProgressStep(1.0);
    }

    // return image
    ImageGroupMD imgArray[] = new ImageGroupMD[groups.size()];
    imgArray = groups.toArray(imgArray);
    return imgArray;

  }


  private static ImageGroupMD parseFile(File file, SettingsImageDataImportTxt sett)
      throws Exception {
    TxtWriter txtWriter = new TxtWriter();
    // line by line
    BufferedReader br = txtWriter.getBufferedReader(file);
    String s;

    ArrayList<DataPoint> datapoints = new ArrayList<>();
    int maxx = Integer.MIN_VALUE;
    int maxy = Integer.MIN_VALUE;
    int miny = Integer.MAX_VALUE;
    int minx = Integer.MAX_VALUE;

    while ((s = br.readLine()) != null) {
      // try to separate by separation
      // R00X1714Y257 316
      String[] sep = s.split(" ");
      double z = Double.parseDouble(sep[1]);
      sep = sep[0].split("Y");
      int y = Integer.parseInt(sep[1]);
      sep = sep[0].split("X");
      int x = Integer.parseInt(sep[1]);
      datapoints.add(new DataPoint(x, y, z));

      if (x > maxx)
        maxx = x;
      if (x < minx)
        minx = x;
      if (y > maxy)
        maxy = y;
      if (y < miny)
        miny = y;
    }
    if (datapoints.isEmpty())
      return null;

    // y x
    double[][] data = new double[maxy - miny + 1][maxx - minx + 1];
    for (double[] line : data) {
      Arrays.fill(line, Double.NaN);
    }

    // put all data points
    for (DataPoint dp : datapoints)
      data[dp.y - miny][dp.x - minx] = dp.z;

    ScanLineMD[] lines = new ScanLineMD[data.length];
    for (int j = 0; j < lines.length; j++) {
      lines[j] = new ScanLineMD(data[j]);
    }

    // Generate Image2D from scanLines
    DatasetLinesMD dataset = new DatasetLinesMD(lines);
    return dataset.createImageGroup(file);
  }

  private static class DataPoint {
    int x, y;
    double z;

    public DataPoint(int x, int y, double z) {
      super();
      this.x = x;
      this.y = y;
      this.z = z;
    }

  }
}
