package net.rs.lamsi.massimager.test;

import java.awt.EventQueue;
import java.io.File;
import net.rs.lamsi.massimager.Frames.Window;
import net.rs.lamsi.massimager.mzmine.MZMineLogicsConnector;

public class TestWindowWithData {


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
          File[] files = new File[13];
          for (int i = 8; i < 21; i++) {
            String s = "C:\\DATA\\RAW\\Neuralgin_2017_06_26\\0";
            if (i < 10)
              s += "0";
            s += i + ".mzML";
            files[i - 8] = new File(s);
          }
          MZMineLogicsConnector.importRawDataDirect(files);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

}
