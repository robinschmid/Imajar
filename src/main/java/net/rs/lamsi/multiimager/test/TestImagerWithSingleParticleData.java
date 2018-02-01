package net.rs.lamsi.multiimager.test;

import java.awt.EventQueue;
import java.io.File;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.ImageGroupMD;
import net.rs.lamsi.general.datamodel.image.ImagingProject;
import net.rs.lamsi.general.datamodel.image.SingleParticleImage;
import net.rs.lamsi.general.datamodel.image.TestImageFactory;
import net.rs.lamsi.general.datamodel.image.listener.ProjectChangedEvent;
import net.rs.lamsi.general.datamodel.image.listener.ProjectChangedListener;
import net.rs.lamsi.general.settings.image.SettingsSPImage;
import net.rs.lamsi.general.settings.image.sub.SettingsGeneralImage;
import net.rs.lamsi.general.settings.image.sub.SettingsGeneralImage.XUNIT;
import net.rs.lamsi.general.settings.importexport.SettingsImageDataImportTxt;
import net.rs.lamsi.general.settings.importexport.SettingsImageDataImportTxt.IMPORT;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.dialogs.singleparticle.SingleParticleDialog;

public class TestImagerWithSingleParticleData {


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
          String s = "D:\\Data\\sp Au\\2018_01_23 LA-spICP-MS_S2_Test_4um_4ums_100%_image.csv";
          String s2 = "D:\\Data\\sp Au\\full.csv";

          SettingsImageDataImportTxt settingsDataImport =
              new SettingsImageDataImportTxt(IMPORT.PRESETS_THERMO_iCAPQ, true, ",", false);
          final ImagingProject project = new ImagingProject("Single Particle Full");
          window.getLogicRunner().importTextDataToImage(settingsDataImport,
              new File[] {new File(s2)}, project);

          final ProjectChangedListener listener = new ProjectChangedListener() {
            @Override
            public void projectChanged(ProjectChangedEvent e) {
              ImagingProject project = e.getProject();
              Image2D img = (Image2D) project.get(0).get(0);
              SettingsGeneralImage simg =
                  (SettingsGeneralImage) img.getSettingsByClass(SettingsGeneralImage.class);
              simg.setInterpolation(0.001);
              simg.setUseInterpolation(true);
              simg.setVelocity(0.05f);

              SettingsSPImage settings = new SettingsSPImage(img.getSettings().getSettImage());
              settings.getSettSingleParticle().setNoiseLevel(3500);
              SingleParticleImage spi = new SingleParticleImage(img, settings);
              SingleParticleDialog d = new SingleParticleDialog();
              d.setSPImage(spi);
              d.setVisible(true);
            }
          };

          project.addProjectListener(listener);

          final ImagingProject project3 = new ImagingProject("Single Particle");
          window.getLogicRunner().importTextDataToImage(
              (SettingsImageDataImportTxt) settingsDataImport.copy(), new File[] {new File(s)},
              project3);

          project3.addProjectListener(e -> {
            SettingsGeneralImage simg = (SettingsGeneralImage) project3.get(0).get(0)
                .getSettingsByClass(SettingsGeneralImage.class);
            simg.setInterpolation(0.001);
            simg.setUseInterpolation(true);
            simg.setVelocity(0.05f);
          });


          // import non triggered
          settingsDataImport = new SettingsImageDataImportTxt(IMPORT.MULTIPLE_FILES_LINES_TXT_CSV,
              true, "\t", false);
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
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    });
  }

}
