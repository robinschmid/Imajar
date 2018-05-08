package net.rs.lamsi.multiimager.test.gaussianblur;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import org.jfree.chart.ChartPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.general.heatmap.HeatmapFactory;
import net.rs.lamsi.general.heatmap.dataoperations.FastGaussianBlur;

public class TestGaussianBlur extends JFrame {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  private JPanel contentPane;
  private JTextField txtRadius;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          TestGaussianBlur frame = new TestGaussianBlur();
          frame.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  int w = 100, h = 100;
  double[] x = new double[w * h];
  double[] y = new double[w * h];
  double[] z = new double[w * h];
  private JPanel pnCenter;

  /**
   * Create the frame.
   */
  public TestGaussianBlur() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 645, 597);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    contentPane.setLayout(new BorderLayout(0, 0));
    setContentPane(contentPane);

    JPanel panel = new JPanel();
    contentPane.add(panel, BorderLayout.NORTH);
    panel.setLayout(new MigLayout("", "[][][]", "[]"));

    JLabel lblSigma = new JLabel("radius");
    panel.add(lblSigma, "cell 0 0,alignx trailing");

    txtRadius = new JTextField();
    txtRadius.setText("1");
    panel.add(txtRadius, "cell 1 0,growx");
    txtRadius.setColumns(10);

    JButton btnNewButton = new JButton("Blur");
    btnNewButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          double r = Double.valueOf(txtRadius.getText());
          blur(r);
        } catch (Exception e2) {
          logger.error("",e2);
        }
      }
    });
    panel.add(btnNewButton, "cell 2 0,grow");

    pnCenter = new JPanel();
    contentPane.add(pnCenter, BorderLayout.CENTER);
    pnCenter.setLayout(new BorderLayout(0, 0));


    for (int yy = 0; yy < h; yy++) {
      for (int xx = 0; xx < w; xx++) {
        x[yy * h + xx] = xx;
        y[yy * h + xx] = yy;
        z[yy * h + xx] = (xx >= 20 && xx < w - 20 && yy >= 20 && yy < h - 20) ? 1 : 0;
      }
    }

    blur(0);
  }


  public void blur(double r) {
    double[] source = z.clone();
    double[] target = new double[source.length];

    if (r > 0)
      FastGaussianBlur.applyBlur(source, target, w, h, r);
    else
      target = source;

    try {
      ChartPanel cp = HeatmapFactory.generateHeatmap("test", x, y, target).getChartPanel();
      getPnCenter().removeAll();
      getPnCenter().add(cp, BorderLayout.CENTER);
      getPnCenter().revalidate();
      getPnCenter().repaint();
    } catch (Exception e) {
      logger.error("", e);
    }
  }

  public JTextField getTxtRadius() {
    return txtRadius;
  }

  public JPanel getPnCenter() {
    return pnCenter;
  }
}
