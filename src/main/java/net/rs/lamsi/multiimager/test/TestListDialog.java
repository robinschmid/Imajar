package net.rs.lamsi.multiimager.test;

import java.awt.EventQueue;
import java.util.Vector;
import javax.swing.ListSelectionModel;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.utils.DialogLoggerUtil;

public class TestListDialog {


  public static void main(String[] args) {
    // start MultiImager application
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          ImageEditorWindow window = new ImageEditorWindow();
          window.setVisible(true);
          Vector<Object> list = new Vector<Object>();
          list.addElement("a1");
          list.addElement("a2");
          list.addElement("a3");
          list.addElement("a4");
          list.addElement("a5");
          list.addElement("a6");
          list.addElement("a7");
          int[] ind = DialogLoggerUtil.showListDialogAndChoose(window, list,
              ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
          for (int i : ind) {
            System.out.println(list.get(i));
          }
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    });
  }

}
