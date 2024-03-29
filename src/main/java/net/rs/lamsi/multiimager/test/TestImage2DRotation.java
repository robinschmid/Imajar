package net.rs.lamsi.multiimager.test;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.ImageGroupMD;
import net.rs.lamsi.general.heatmap.Heatmap;
import net.rs.lamsi.general.heatmap.HeatmapFactory;
import net.rs.lamsi.general.settings.importexport.SettingsImageDataImportTxt;
import net.rs.lamsi.general.settings.importexport.SettingsImageDataImportTxt.IMPORT;
import net.rs.lamsi.multiimager.utils.imageimportexport.Image2DImportExportUtil;
import net.rs.lamsi.utils.FileAndPathUtil;

public class TestImage2DRotation extends JFrame {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  private JPanel contentPane;
  private JPanel panel;
  protected Heatmap map;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          TestImage2DRotation frame = new TestImage2DRotation();
          frame.setVisible(true);
        } catch (Exception e) {
        }
      }
    });
  }

  /**
   * Create the frame.
   */
  public TestImage2DRotation() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 545, 821);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    contentPane.setLayout(new BorderLayout(0, 0));
    setContentPane(contentPane);

    panel = new JPanel();
    contentPane.add(panel, BorderLayout.CENTER);
    panel.setLayout(new BorderLayout(0, 0));

    JPanel panel_1 = new JPanel();
    contentPane.add(panel_1, BorderLayout.NORTH);

    JButton btnInvertY = new JButton("invert y");
    btnInvertY.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        map.getPlot().getRangeAxis().setInverted(!map.getPlot().getRangeAxis().isInverted());
      }
    });
    panel_1.add(btnInvertY);

    JButton btnInvertX = new JButton("invert x");
    btnInvertX.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        map.getPlot().getDomainAxis().setInverted(!map.getPlot().getDomainAxis().isInverted());
      }
    });
    panel_1.add(btnInvertX);

    JButton btnOrientation = new JButton("Orientation");
    btnOrientation.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        PlotOrientation or = map.getPlot().getOrientation();
        map.getPlot()
            .setOrientation(or.equals(PlotOrientation.HORIZONTAL) ? PlotOrientation.VERTICAL
                : PlotOrientation.HORIZONTAL);
      }
    });
    panel_1.add(btnOrientation);

    SettingsImageDataImportTxt settingsDataImport =
        new SettingsImageDataImportTxt(IMPORT.MULTIPLE_FILES_LINES_TXT_CSV, true, ",", false);
    // load data ThermoMP17 Image qtofwerk.csv C:\DATA\Agilent ICP\Mstd2
    String s = "C:\\DATA\\Agilent ICP\\Mstd2\\";
    File[] files = {new File(s)};

    for (File f : files) {
      if (f.isDirectory()) {
        // get all files in this folder TODO change csv to settings
        // each file[] element is for one image
        List<File[]> sub = FileAndPathUtil.findFilesInDir(f, settingsDataImport.getFilter(), true,
            settingsDataImport.isFilesInSeparateFolders());

        for (File[] i : sub) {
          // load them as image set
          // add
          try {
            ImageGroupMD[] groups =
                Image2DImportExportUtil.importTextDataToImage(i, settingsDataImport, true, null);
            map = HeatmapFactory.generateHeatmap((Image2D) groups[0].get(0));
            panel.add(map.getChartPanel(), BorderLayout.CENTER);
            panel.revalidate();
          } catch (Exception e) {
            logger.error("", e);
          }
        }
      }
    }


  }

  public JPanel getPanel() {
    return panel;
  }
}
