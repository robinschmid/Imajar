package net.rs.lamsi.multiimager.FrameModules.sub.paintscale;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedRangeXYPlot;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.TextAnchor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.rs.lamsi.general.datamodel.image.interf.DataCollectable2D;
import net.rs.lamsi.general.myfreechart.EChartFactory;
import net.rs.lamsi.general.myfreechart.swing.EChartPanel;
import net.rs.lamsi.general.settings.image.visualisation.SettingsPaintScale;
import net.rs.lamsi.utils.threads.DelayedProgressUpdateTask;

public class PaintScaleHistogram extends JPanel {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  private DataCollectable2D lastImg;
  private SettingsPaintScale lastPS = null;
  private EChartPanel chart = null;

  private boolean isUptodate = false;
  private double lastMin = Double.NaN, lastMax = Double.NaN;

  // update in a delayed task to save computation time
  private DelayedProgressUpdateTask task = null;

  /**
   * Create the panel.
   */
  public PaintScaleHistogram() {
    this.setLayout(new BorderLayout());
  }

  public DataCollectable2D getImg() {
    return lastImg;
  }

  /**
   * you might watn to call updateHisto after setImg
   * 
   * @param img
   */
  public void setImg(DataCollectable2D img) {
    if (this.lastImg != img) {
      isUptodate = false;
      // stop old task
      if (task != null)
        task.stop();
    }
    this.lastImg = img;
  }

  public void updateHisto(SettingsPaintScale ps) {
    // stop old task if different
    if (ps != lastPS) {
      isUptodate = false;
      if (task != null)
        task.stop();
    }

    if (lastImg != null) {
      if (task == null || ps != lastPS) {
        lastPS = ps;
        startUpdateTask();
      }
    }
  }

  public void startUpdateTask() {
    logger.debug("HISTOGRAM task was started");
    // task gets started after 1 second delay (if not stopped before)
    task = new DelayedProgressUpdateTask(1, 1000) {
      @Override
      protected Boolean doInBackground2() throws Exception {
        DataCollectable2D img = lastImg;
        if (img != null) {
          try {
            logger.debug("HISTOGRAM is updating now");
            SettingsPaintScale ps = lastPS;
            double min = ps.getMinIAbs(img);
            double max = ps.getMaxIAbs(img);
            if (min < max) {
              if (min != lastMin || max != lastMax)
                isUptodate = false;

              if (!isUptodate) {
                double[] dat1 = img.toIArray(false, false);
                double[] dat2 = img.getIInIRange(ps, false);
                if (dat2.length > 2) {
                  int bins2 = (int) Math.sqrt(dat2.length) + 40;
                  double binwidth2 = (max - min) / bins2;
                  int bins1 = (int) ((img.getMaxIntensity(ps.isUsesMinMaxFromSelection())
                      - img.getMinIntensity(ps.isUsesMinMaxFromSelection())) / binwidth2);
                  if (bins1 > 100000)
                    bins1 = 100000;

                  logger.debug("bi1: {}  bins2: {}", bins1, bins2);
                  JFreeChart chart1 = EChartFactory.createHistogram(dat1, bins1);
                  XYPlot plot1 = chart1.getXYPlot();
                  JFreeChart chart2 = EChartFactory.createHistogram(dat2, bins2);
                  XYPlot plot2 = chart2.getXYPlot();

                  Marker minM = new ValueMarker(min);
                  minM.setPaint(Color.RED);
                  minM.setLabel("min");
                  minM.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
                  minM.setLabelTextAnchor(TextAnchor.TOP_LEFT);
                  plot1.addDomainMarker(minM);

                  Marker maxM = new ValueMarker(max);
                  maxM.setPaint(Color.RED);
                  maxM.setLabel("max");
                  maxM.setLabelAnchor(RectangleAnchor.TOP_LEFT);
                  maxM.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
                  plot1.addDomainMarker(maxM);

                  ValueAxis axis = plot1.getRangeAxis();

                  CombinedRangeXYPlot combined = new CombinedRangeXYPlot(axis);
                  combined.add(plot1);
                  combined.add(plot2);

                  JFreeChart c = new JFreeChart(combined);
                  c.getLegend().setVisible(false);
                  chart = new EChartPanel(c);

                  // add to panel
                  removeAll();
                  add(chart, BorderLayout.CENTER);
                  revalidate();

                  isUptodate = true;
                  lastMin = min;
                  lastMax = max;
                }
              }
            }
          } catch (Exception e) {
            logger.error("",e);
            return false;
          }
          return true;
        }
        return false;
      }
    };

    task.startDelayed();
  }
}
