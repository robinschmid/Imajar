package net.rs.lamsi.multiimager.Frames.dialogs.analytics;

import java.awt.BorderLayout;
import java.util.function.Supplier;
import javax.swing.JDialog;
import javax.swing.JFrame;
import org.jfree.data.Range;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.TestImageFactory;
import net.rs.lamsi.utils.math.DoubleArraySupplier;

public class HistogramDialog extends JFrame {

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    try {
      // HistogramDialog dialog =
      // new HistogramDialog(TestImageFactory.createPerfectStandard(4, 4, 20).getFirstImage2D());
      // dialog.setVisible(true);

      Image2D img = TestImageFactory.createGaussianTest(1000, 1000).getFirstImage2D();
      DoubleArraySupplier s = () -> {
        return img.toIArray(false, true);
      };
      Supplier<Range> r = () -> {
        return new Range(img.getMinIntensity(true), img.getMaxIntensity(true));
      };
      HistogramData data = new HistogramData(s, r);
      HistogramDialog dialog2 = new HistogramDialog("Test histo gaussian", data);
      dialog2.setVisible(true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Create the dialog.
   */
  public HistogramDialog(String title, HistogramData data) {
    this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    setTitle(title);
    setBounds(100, 100, 1000, 800);
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(new HistogramPanel(data), BorderLayout.CENTER);
    this.setTitle(title);
  }
}
