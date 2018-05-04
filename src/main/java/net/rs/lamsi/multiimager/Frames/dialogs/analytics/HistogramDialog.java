package net.rs.lamsi.multiimager.Frames.dialogs.analytics;

import java.awt.BorderLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import net.rs.lamsi.general.datamodel.image.TestImageFactory;
import net.rs.lamsi.general.datamodel.image.interf.DataCollectable2D;

public class HistogramDialog extends JFrame {

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    try {
      // HistogramDialog dialog =
      // new HistogramDialog(TestImageFactory.createPerfectStandard(4, 4, 20).getFirstImage2D());
      // dialog.setVisible(true);

      HistogramDialog dialog2 =
          new HistogramDialog(TestImageFactory.createGaussianTest(1000, 1000).getFirstImage2D());
      dialog2.setVisible(true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Create the dialog.
   */
  public HistogramDialog(DataCollectable2D img) {
    this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    setBounds(100, 100, 1000, 800);
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(new HistogramPanel(img), BorderLayout.CENTER);
    if (img != null) {
      this.setTitle("Histogram for " + img.getTitle());
    }
  }
}
