package net.rs.lamsi.multiimager.test;

import java.awt.EventQueue;
import java.io.File;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.rs.lamsi.general.datamodel.image.ImageGroupMD;
import net.rs.lamsi.general.datamodel.image.ImagingProject;
import net.rs.lamsi.general.datamodel.image.SingleParticleImage;
import net.rs.lamsi.general.datamodel.image.TestImageFactory;
import net.rs.lamsi.general.settings.image.sub.SettingsGeneralImage.XUNIT;
import net.rs.lamsi.general.settings.importexport.SettingsImageDataImportTxt;
import net.rs.lamsi.general.settings.importexport.SettingsImageDataImportTxt.IMPORT;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;

public class TestImagerWithSingleParticleData {
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
          String s = "D:\\Daten2\\cali.img2dproject";
          window.getLogicRunner().loadProjectFromFile(new File(s));

          // import non triggered
          SettingsImageDataImportTxt settingsDataImport = new SettingsImageDataImportTxt(
              IMPORT.MULTIPLE_FILES_LINES_TXT_CSV, true, "\t", false);
          settingsDataImport.setExcludeColumns("1,2");
          settingsDataImport.setModeImport(IMPORT.CONTINOUS_DATA_TXT_CSV);
          settingsDataImport.setSplitUnit(XUNIT.DP);
          settingsDataImport.setSplitAfter(150);
          settingsDataImport.setUseHardSplit(false);
          settingsDataImport.setNoXData(true);

          ImagingProject project2 = new ImagingProject("TOF");
          window.getLogicRunner().importTextDataToImage(settingsDataImport, new File[] {new File(
              "D:\\DataC\\DATA\\TOF-Werk_AKK\\LC_image_3 mm x7.5 mm_07.26-13h52m39s_AS.h5__BufWriteProfiles_Segment_31P,32S,34S,42Ca,44Ca.txt")},
              project2);

          // hard split
          settingsDataImport.setUseHardSplit(true);
          window.getLogicRunner().importTextDataToImage(settingsDataImport, new File[] {new File(
              "D:\\DataC\\DATA\\TOF-Werk_AKK\\LC_image_3 mm x7.5 mm_07.26-13h52m39s_AS.h5__BufWriteProfiles_Segment_31P,32S,34S,42Ca,44Ca.txt")},
              project2);


          ImageGroupMD img = TestImageFactory.createNonNormalImage(4);

          project2 = null;
          window.getLogicRunner().addGroup(img, project2);

          img = TestImageFactory.createOverlayTest();
          window.getLogicRunner().addGroup(img, project2);

          // simulated
          SingleParticleImage spimg =
              TestImageFactory.createPerfectSingleParticleImg(10, 1000, 10, 1000, 50000, 4);
          ImageGroupMD g = spimg.getImageGroup();
          ImagingProject spp = new ImagingProject("Simulated sp image");
          spp.add(g);
          window.getLogicRunner().addProject(spp, true);

        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    });
  }

}
