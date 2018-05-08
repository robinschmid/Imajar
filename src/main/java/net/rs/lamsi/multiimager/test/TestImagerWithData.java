package net.rs.lamsi.multiimager.test;

import java.awt.EventQueue;
import java.io.File;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.rs.lamsi.general.datamodel.image.ImageGroupMD;
import net.rs.lamsi.general.datamodel.image.ImagingProject;
import net.rs.lamsi.general.datamodel.image.TestImageFactory;
import net.rs.lamsi.general.settings.image.sub.SettingsGeneralImage.XUNIT;
import net.rs.lamsi.general.settings.importexport.SettingsImageDataImportTxt;
import net.rs.lamsi.general.settings.importexport.SettingsImageDataImportTxt.IMPORT;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;

public class TestImagerWithData {
  private final Logger logger = LoggerFactory.getLogger(getClass());


  public static void main(String[] args) {
    try {
      // UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
        | UnsupportedLookAndFeelException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // start MultiImager application
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          ImageEditorWindow window = new ImageEditorWindow();
          window.setVisible(true);
          // load data ThermoMP17 Image qtofwerk.csv
          String s = "C:\\DATA\\Agilent ICP\\Mstd2\\";
          File[] files = {new File(s)};

          SettingsImageDataImportTxt settingsDataImport =
              new SettingsImageDataImportTxt(IMPORT.MULTIPLE_FILES_LINES_TXT_CSV, true, ",", false);
          ImagingProject project = new ImagingProject("Agilent");
          window.getLogicRunner().importTextDataToImage(settingsDataImport, files, project);
          window.getLogicRunner().importTextDataToImage(settingsDataImport, files, project);

          // import non triggered
          settingsDataImport = new SettingsImageDataImportTxt(IMPORT.MULTIPLE_FILES_LINES_TXT_CSV,
              true, "\t", false);
          settingsDataImport.setExcludeColumns("1,2");
          settingsDataImport.setModeImport(IMPORT.CONTINOUS_DATA_TXT_CSV);
          settingsDataImport.setSplitUnit(XUNIT.DP);
          settingsDataImport.setSplitAfter(150);
          settingsDataImport.setUseHardSplit(false);
          settingsDataImport.setNoXData(true);

          project = new ImagingProject("TOF");
          window.getLogicRunner().importTextDataToImage(settingsDataImport, new File[] {new File(
              "C:\\DATA\\TOF-Werk_AKK\\LC_image_3 mm x7.5 mm_07.26-13h52m39s_AS.h5__BufWriteProfiles_Segment_31P,32S,34S,42Ca,44Ca.txt")},
              project);

          // hard split
          settingsDataImport.setUseHardSplit(true);
          window.getLogicRunner().importTextDataToImage(settingsDataImport, new File[] {new File(
              "C:\\DATA\\TOF-Werk_AKK\\LC_image_3 mm x7.5 mm_07.26-13h52m39s_AS.h5__BufWriteProfiles_Segment_31P,32S,34S,42Ca,44Ca.txt")},
              project);


          ImageGroupMD img = TestImageFactory.createNonNormalImage(4);

          project = null;
          window.getLogicRunner().addGroup(img, project);

          img = TestImageFactory.createOverlayTest();
          window.getLogicRunner().addGroup(img, project);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    });
  }

}
