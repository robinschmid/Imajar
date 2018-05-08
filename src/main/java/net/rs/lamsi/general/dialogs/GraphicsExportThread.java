package net.rs.lamsi.general.dialogs;

import javax.swing.SwingUtilities;
import org.jfree.chart.JFreeChart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.rs.lamsi.general.settings.importexport.SettingsExportGraphics;
import net.rs.lamsi.utils.ChartExportUtil;
import net.rs.lamsi.utils.DialogLoggerUtil;

public class GraphicsExportThread extends Thread {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  private SettingsExportGraphics fsett;
  private JFreeChart chart;

  public GraphicsExportThread(JFreeChart chart, SettingsExportGraphics fsett) {
    super();
    this.fsett = fsett;
    this.chart = chart;
  }

  @Override
  public void run() {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        try {
          logger.info("Writing image to file: {}", fsett.getFullFilePath());
          ChartExportUtil.writeChartToImage(chart, fsett);
          logger.info("Succeed to export {} at {}", fsett.getFullFilePath().getName(),
              fsett.getFullFilePath());
        } catch (Exception e) {
          DialogLoggerUtil.showMessageDialogForTime(null, "FAILED", "Failed to export "
              + fsett.getFullFilePath().getName() + " at " + fsett.getFullFilePath(), 1500);
          logger.error("Failed to export {} at {}", fsett.getFullFilePath().getName(),
              fsett.getFullFilePath(), e);
        }
      }
    });
  }
}
