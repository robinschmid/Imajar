package net.rs.lamsi.multiimager.test;

import java.awt.EventQueue;
import java.io.File;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.rs.lamsi.general.settings.importexport.SettingsImageDataImportTxt;
import net.rs.lamsi.general.settings.importexport.SettingsImageDataImportTxt.IMPORT;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.utils.FileAndPathUtil;

public class TestDirectIA {
  private final Logger logger = LoggerFactory.getLogger(getClass());


  public static void main(String[] args) {
    try {
      // UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
        | UnsupportedLookAndFeelException e) {
      // TODO Auto-generated catch block
    }
    // start MultiImager application
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          ImageEditorWindow window = new ImageEditorWindow();
          window.setVisible(true);
          // load data ThermoMP17 Image
          String s = FileAndPathUtil.getPathOfJar().getParent() + "/data/direct/";
          File[] files = {new File(s)};

          window.getMenuRbDirectImagingAnalysis().setSelected(true);

          SettingsImageDataImportTxt settingsDataImport =
              new SettingsImageDataImportTxt(IMPORT.MULTIPLE_FILES_LINES_TXT_CSV, true, ",", true);
          window.getLogicRunner().startDirectImagingAnalysis(settingsDataImport, files, null);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    });
  }

}
