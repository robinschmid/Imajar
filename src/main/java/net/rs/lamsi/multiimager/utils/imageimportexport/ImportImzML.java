package net.rs.lamsi.multiimager.utils.imageimportexport;

import java.io.File;
import java.util.ArrayList;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.ImageGroupMD;
import net.rs.lamsi.general.settings.importexport.SettingsImzMLImageImport;
import net.rs.lamsi.utils.threads.ProgressUpdateTask;

public class ImportImzML {

  public static ImageGroupMD[] parse(File[] files, SettingsImzMLImageImport sett,
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
}
