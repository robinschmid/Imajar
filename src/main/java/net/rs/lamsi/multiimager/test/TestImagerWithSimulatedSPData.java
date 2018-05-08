package net.rs.lamsi.multiimager.test;

import java.awt.EventQueue;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.data.twodimensional.Simple2DDataset;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.dialogs.SimpleImageFrame;
import net.rs.lamsi.utils.useful.DebugStopWatch;

public class TestImagerWithSimulatedSPData {
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
          // generate img
          int lines = 100;
          int dp = 100 * 1000;
          double[][] d = new double[lines][dp];
          for (int i = 0; i < d.length; i++) {
            for (int j = 0; j < d[i].length; j++) {
              d[i][j] = i + j;
            }
          }

          Simple2DDataset data = new Simple2DDataset(0.001f, 1, d);
          Image2D image = new Image2D(data);
          SimpleImageFrame frame = new SimpleImageFrame(image);
          DebugStopWatch timer = new DebugStopWatch();
          frame.setVisible(true);
          timer.stopAndLOG("for showing SimpleImageFrame");

          // window.getLogicRunner().addImageNode(image, null);
          //
          //
          // // import non triggered
          // SettingsImageDataImportTxt settingsDataImport = new SettingsImageDataImportTxt(
          // IMPORT.MULTIPLE_FILES_LINES_TXT_CSV, true, "\t", false);
          // settingsDataImport.setExcludeColumns("1,2");
          // settingsDataImport.setModeImport(IMPORT.CONTINOUS_DATA_TXT_CSV);
          // settingsDataImport.setSplitUnit(XUNIT.DP);
          // settingsDataImport.setSplitAfter(150);
          // settingsDataImport.setUseHardSplit(false);
          // settingsDataImport.setNoXData(true);
          //
          // ImagingProject project2 = new ImagingProject("TOF");
          // window.getLogicRunner().importTextDataToImage(settingsDataImport, new File[] {new File(
          // "D:\\DataC\\DATA\\TOF-Werk_AKK\\LC_image_3 mm x7.5
          // mm_07.26-13h52m39s_AS.h5__BufWriteProfiles_Segment_31P,32S,34S,42Ca,44Ca.txt")},
          // project2);
          //
          // // hard split
          // settingsDataImport.setUseHardSplit(true);
          // window.getLogicRunner().importTextDataToImage(settingsDataImport, new File[] {new File(
          // "D:\\DataC\\DATA\\TOF-Werk_AKK\\LC_image_3 mm x7.5
          // mm_07.26-13h52m39s_AS.h5__BufWriteProfiles_Segment_31P,32S,34S,42Ca,44Ca.txt")},
          // project2);
          //
          //
          // ImageGroupMD img = TestImageFactory.createNonNormalImage(4);
          //
          // project2 = null;
          // window.getLogicRunner().addGroup(img, project2);
          //
          // img = TestImageFactory.createOverlayTest();
          // window.getLogicRunner().addGroup(img, project2);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    });
  }

}
