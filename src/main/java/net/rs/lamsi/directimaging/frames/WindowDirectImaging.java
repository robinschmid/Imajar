package net.rs.lamsi.directimaging.frames;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.heatmap.Heatmap;
import net.rs.lamsi.general.heatmap.HeatmapFactory;

public class WindowDirectImaging extends JFrame {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  private JPanel contentPane;
  private JLabel lbLabel;
  ImageGeneratorRunner runner;
  Image2D images[];
  private JPanel pnChartView;
  HeatmapFactory fact = new HeatmapFactory();
  private JCheckBox cbAgilentFolder;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          WindowDirectImaging frame = new WindowDirectImaging();
          frame.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the frame.
   */
  public WindowDirectImaging() {
    runner = new ImageGeneratorRunner(this);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 450, 300);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    contentPane.setLayout(new BorderLayout(0, 0));
    setContentPane(contentPane);

    JPanel panel = new JPanel();
    FlowLayout flowLayout = (FlowLayout) panel.getLayout();
    flowLayout.setAlignment(FlowLayout.LEFT);
    contentPane.add(panel, BorderLayout.NORTH);

    JButton btnStart = new JButton("start");
    btnStart.setHorizontalAlignment(SwingConstants.LEFT);
    btnStart.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        images = runner.startImage(getCbAgilentFolder().isSelected());
        showImage(0);
      }
    });
    panel.add(btnStart);

    lbLabel = new JLabel("a");
    panel.add(lbLabel);

    cbAgilentFolder = new JCheckBox("Agilent folder system");
    panel.add(cbAgilentFolder);

    pnChartView = new JPanel();
    contentPane.add(pnChartView, BorderLayout.CENTER);
    pnChartView.setLayout(new BorderLayout(0, 0));
  }

  protected void showImage(int i) {
    try {
      Heatmap heat;
      heat = fact.generateHeatmap(images[i]);
      getPnChartView().add(heat.getChartPanel(), BorderLayout.CENTER);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      logger.error("", e);
    }
  }

  public JLabel getLbLabel() {
    return lbLabel;
  }

  public JPanel getPnChartView() {
    return pnChartView;
  }

  public JCheckBox getCbAgilentFolder() {
    return cbAgilentFolder;
  }
}
