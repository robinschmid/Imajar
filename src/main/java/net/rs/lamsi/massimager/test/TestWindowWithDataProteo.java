package net.rs.lamsi.massimager.test;

import java.awt.EventQueue;
import java.io.File;
import net.rs.lamsi.massimager.Frames.Window;
import net.rs.lamsi.massimager.mzmine.MZMineLogicsConnector;

public class TestWindowWithDataProteo {


  public static void main(String[] args) {
    // connect to mzmine
    // have to load it before everything else
    MZMineLogicsConnector.connectToMZMine();

    // start MassImager application
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          Window window = new Window();
          window.getFrame().setVisible(true);
          // load data
          // /data/triggerimage/data01.mzXML to 45.mzXML

          File[] files = new File[1];
          files[0] = new File(window.getPathOfJar().getParent() + "/data/proteo.mzXML");
          MZMineLogicsConnector.importRawDataDirect(files);

        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

}
