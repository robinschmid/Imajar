package net.rs.lamsi.multiimager.Frames.dialogs;

import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.heatmap.Heatmap;
import net.rs.lamsi.general.heatmap.HeatmapFactory;
import net.rs.lamsi.utils.useful.DebugStopWatch;

public class SimpleImageFrame extends JFrame {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  private JPanel contentPane;

  /**
   * Create the frame.
   */
  public SimpleImageFrame(Image2D img) {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 450, 300);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    contentPane.setLayout(new BorderLayout(0, 0));
    setContentPane(contentPane);

    JButton btn = new JButton("repaint");
    btn.addActionListener(e -> contentPane.repaint());
    contentPane.add(btn, BorderLayout.SOUTH);

    try {
      DebugStopWatch timer = new DebugStopWatch();
      Heatmap map = HeatmapFactory.generateHeatmap(img);
      contentPane.add(map.getChartPanel(), BorderLayout.CENTER);
      timer.stopAndLOG("for creating the heatmap for SimpleImageFrame");
    } catch (Exception e) {
      logger.error("", e);
    }
  }

}
