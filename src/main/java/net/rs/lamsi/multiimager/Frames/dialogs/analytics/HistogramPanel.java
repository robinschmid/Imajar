package net.rs.lamsi.multiimager.Frames.dialogs.analytics;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.concurrent.ExecutionException;
import java.util.function.DoubleFunction;
import java.util.stream.DoubleStream;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataset;
import net.rs.lamsi.general.datamodel.image.interf.DataCollectable2D;
import net.rs.lamsi.general.framework.listener.DelayedDocumentListener;
import net.rs.lamsi.general.framework.modules.Module;
import net.rs.lamsi.general.myfreechart.EChartFactory;
import net.rs.lamsi.general.myfreechart.swing.EChartPanel;
import net.rs.lamsi.utils.math.Precision;

public class HistogramPanel extends JPanel {

  private final JPanel contentPanel;
  private DelayedDocumentListener ddlRepaint;
  private EChartPanel pnHisto;
  private JPanel southwest;
  private JTextField txtBinWidth, txtBinShift;
  private JCheckBox cbExcludeSmallerNoise, cbThirdSQRT;
  private JLabel lbStats;
  private JTextField txtRangeX;
  private JTextField txtRangeY;
  private JTextField txtRangeXEnd;
  private JTextField txtRangeYEnd;
  private JTextField txtGaussianLower;
  private JTextField txtGaussianUpper;
  private JTextField txtPrecision;
  private JCheckBox cbGaussianFit;

  private DataCollectable2D img;

  /**
   * Create the dialog.
   */
  public HistogramPanel(DataCollectable2D img) {
    setBounds(100, 100, 903, 952);
    setLayout(new BorderLayout());
    contentPanel = new JPanel();
    add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(new BorderLayout(0, 0));
    {
      JPanel west = new JPanel();
      contentPanel.add(west, BorderLayout.WEST);
      west.setLayout(new BorderLayout(0, 0));
      {
        JPanel panel = new JPanel();
        west.add(panel, BorderLayout.NORTH);
      }
    }
    {
      JPanel center1 = new JPanel();
      contentPanel.add(center1, BorderLayout.CENTER);
      center1.setLayout(new BorderLayout(0, 0));
      {
        JPanel box = new JPanel();
        center1.add(box, BorderLayout.SOUTH);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        {
          JPanel pnstats = new JPanel();
          box.add(pnstats);
          {
            lbStats = new JLabel("last stats:");
            pnstats.add(lbStats);
          }
        }
        {
          JPanel pnHistoSett = new JPanel();
          box.add(pnHistoSett);
          {
            cbExcludeSmallerNoise = new JCheckBox("exclude smallest");
            cbExcludeSmallerNoise.setSelected(true);
            pnHistoSett.add(cbExcludeSmallerNoise);
          }
          {
            cbThirdSQRT = new JCheckBox("cube root(I)");
            cbThirdSQRT.setSelected(false);
            pnHistoSett.add(cbThirdSQRT);
          }
          {
            Component horizontalStrut = Box.createHorizontalStrut(20);
            pnHistoSett.add(horizontalStrut);
          }
          {
            JLabel lblBinWidth = new JLabel("bin width");
            pnHistoSett.add(lblBinWidth);
          }
          {
            txtBinWidth = new JTextField();
            txtBinWidth.setText("1");
            pnHistoSett.add(txtBinWidth);
            txtBinWidth.setColumns(7);
          }
          {
            Component horizontalStrut = Box.createHorizontalStrut(20);
            pnHistoSett.add(horizontalStrut);
          }
          {
            JLabel lblBinWidth = new JLabel("shift bins by");
            pnHistoSett.add(lblBinWidth);
          }
          {
            txtBinShift = new JTextField();
            txtBinShift.setText("0.5");
            pnHistoSett.add(txtBinShift);
            txtBinShift.setColumns(7);
          }
        }
        {
          JPanel secondGaussian = new JPanel();
          box.add(secondGaussian);
          {
            JButton btnToggleLegend = new JButton("Toggle legend");
            btnToggleLegend.addActionListener(e -> toggleLegends());
            btnToggleLegend.setToolTipText("Show/hide legend");
            secondGaussian.add(btnToggleLegend);
          }
          {
            JButton btnUpdateGaussian = new JButton("Update");
            btnUpdateGaussian.addActionListener(e -> updateGaussian());
            btnUpdateGaussian.setToolTipText("Update Gaussian fit");
            secondGaussian.add(btnUpdateGaussian);
          }
          {
            cbGaussianFit = new JCheckBox("Gaussian fit");
            secondGaussian.add(cbGaussianFit);
          }
          {
            JLabel lblFrom = new JLabel("from");
            secondGaussian.add(lblFrom);
          }
          {
            txtGaussianLower = new JTextField();
            txtGaussianLower.setToolTipText("The lower bound (domain axis) for the Gaussian fit");
            txtGaussianLower.setText("0");
            secondGaussian.add(txtGaussianLower);
            txtGaussianLower.setColumns(7);
          }
          {
            JLabel label = new JLabel("-");
            secondGaussian.add(label);
          }
          {
            txtGaussianUpper = new JTextField();
            txtGaussianUpper
                .setToolTipText("The upper bound (domain axis, x) for the Gaussian fit");
            txtGaussianUpper.setText("0");
            txtGaussianUpper.setColumns(7);
            secondGaussian.add(txtGaussianUpper);
          }
          {
            Component horizontalStrut = Box.createHorizontalStrut(20);
            secondGaussian.add(horizontalStrut);
          }
          {
            JLabel lblSignificantFigures = new JLabel("significant figures");
            secondGaussian.add(lblSignificantFigures);
          }
          {
            txtPrecision = new JTextField();
            txtPrecision.setText("4");
            secondGaussian.add(txtPrecision);
            txtPrecision.setColumns(3);
          }
        }
        {
          JPanel third = new JPanel();
          box.add(third);
          {
            JLabel lblRanges = new JLabel("x-range");
            third.add(lblRanges);
          }
          {
            txtRangeX = new JTextField();
            third.add(txtRangeX);
            txtRangeX.setToolTipText("Set the x-range for both histograms");
            txtRangeX.setText("0");
            txtRangeX.setColumns(6);
          }
          {
            JLabel label = new JLabel("-");
            third.add(label);
          }
          {
            txtRangeXEnd = new JTextField();
            txtRangeXEnd.setToolTipText("Set the x-range for both histograms");
            txtRangeXEnd.setText("0");
            txtRangeXEnd.setColumns(6);
            third.add(txtRangeXEnd);
          }
          {
            JButton btnApplyX = new JButton("Apply");
            btnApplyX.addActionListener(e -> applyXRange());
            third.add(btnApplyX);
          }
          {
            JPanel panel = new JPanel();
            box.add(panel);
            {
              JLabel label = new JLabel("y-range");
              panel.add(label);
            }
            {
              txtRangeY = new JTextField();
              panel.add(txtRangeY);
              txtRangeY.setText("0");
              txtRangeY.setToolTipText("Set the y-range for both histograms");
              txtRangeY.setColumns(6);
            }
            {
              JLabel label = new JLabel("-");
              panel.add(label);
            }
            {
              txtRangeYEnd = new JTextField();
              txtRangeYEnd.setToolTipText("Set the y-range for both histograms");
              txtRangeYEnd.setText("0");
              txtRangeYEnd.setColumns(6);
              panel.add(txtRangeYEnd);
            }
            {
              JButton btnApplyY = new JButton("Apply");
              btnApplyY.addActionListener(e -> applyYRange());
              panel.add(btnApplyY);
            }
          }
        }
      }
      {
        southwest = new JPanel();
        center1.add(southwest, BorderLayout.CENTER);
        southwest.setLayout(new BorderLayout(0, 0));
      }
    }

    addListener();

    //
    this.img = img;
    if (img != null) {
      // set bin width
      int bin = (int) Math.sqrt(img.getTotalDataPoints());
      double l = img.getMaxIntensity(true) - img.getMinIntensity(true);
      double bw = l / (double) bin;
      String bws = String.valueOf(bw);
      try {
        bws = Precision.toString(bw, 4);
      } catch (Exception e) {
      }
      txtBinWidth.setText(bws);

      //
      ddlRepaint.stop();
      updateHistograms();

      contentPanel.revalidate();
      contentPanel.repaint();
    }
  }

  /**
   * Toggles visibility of legends
   */
  private void toggleLegends() {
    if (pnHisto != null) {
      LegendTitle legend = pnHisto.getChart().getLegend();
      if (legend != null)
        legend.setVisible(!legend.isVisible());
    }
  }

  private void addListener() {
    ddlRepaint = new DelayedDocumentListener(e -> repaint());

    // ranges
    DelayedDocumentListener ddlx = new DelayedDocumentListener(e -> applyXRange());
    DelayedDocumentListener ddly = new DelayedDocumentListener(e -> applyYRange());

    txtRangeX.getDocument().addDocumentListener(ddlx);
    txtRangeXEnd.getDocument().addDocumentListener(ddlx);
    txtRangeY.getDocument().addDocumentListener(ddly);
    txtRangeYEnd.getDocument().addDocumentListener(ddly);
    cbThirdSQRT.addItemListener(e -> updateHistograms());
    cbExcludeSmallerNoise.addItemListener(e -> updateHistograms());
    txtBinWidth.getDocument()
        .addDocumentListener(new DelayedDocumentListener(e -> updateHistograms()));
    txtBinShift.getDocument()
        .addDocumentListener(new DelayedDocumentListener(e -> updateHistograms()));

    // add gaussian?
    cbGaussianFit.addItemListener(e -> updateGaussian());
  }


  private void applyXRange() {
    try {
      double x = Double.parseDouble(txtRangeX.getText());
      double xe = Double.parseDouble(txtRangeXEnd.getText());
      if (x < xe) {
        if (pnHisto != null)
          pnHisto.getChart().getXYPlot().getDomainAxis().setRange(x, xe);
      }
    } catch (Exception e2) {
      e2.printStackTrace();
    }
  }

  private void applyYRange() {
    try {
      double y = Double.parseDouble(txtRangeY.getText());
      double ye = Double.parseDouble(txtRangeYEnd.getText());
      if (y < ye) {
        if (pnHisto != null)
          pnHisto.getChart().getXYPlot().getRangeAxis().setRange(y, ye);
      }
    } catch (Exception e2) {
      e2.printStackTrace();
    }
  }


  /**
   * Create new histograms
   * 
   * @throws Exception
   */
  private void updateHistograms() {
    if (img != null) {
      double binwidth2 = Double.NaN;
      double binShift2 = Double.NaN;
      try {
        binwidth2 = Double.parseDouble(txtBinWidth.getText());
        binShift2 = Double.parseDouble(txtBinShift.getText());
      } catch (Exception e) {
      }
      if (!Double.isNaN(binShift2)) {
        try {
          final double binwidth = binwidth2;
          final double binShift = Math.abs(binShift2);
          new SwingWorker<JFreeChart, Void>() {
            @Override
            protected JFreeChart doInBackground() throws Exception {
              // create histogram
              double[] data = null;
              if (cbExcludeSmallerNoise.isSelected()) {
                double noise = img.getMinIntensity(true);
                // get processed data from original image
                double[] dlist = img.toIArray(false, true);
                data = DoubleStream.of(dlist).filter(d -> d > noise).toArray();
              } else
                data = img.toIArray(false, true);

              Range r = EChartFactory.getBounds(data);

              DoubleFunction<Double> f =
                  cbThirdSQRT.isSelected() ? val -> Math.cbrt(val) : val -> val;

              JFreeChart chart = EChartFactory.createHistogram(data, "I", binwidth,
                  r.getLowerBound() - binShift, r.getUpperBound(), f);
              // add gaussian?
              if (cbGaussianFit.isSelected()) {
                addGaussianCurve(chart.getXYPlot());
              }
              return chart;
            }

            @Override
            protected void done() {
              JFreeChart histo;
              try {
                Range x = null, y = null;
                if (pnHisto != null) {
                  x = pnHisto.getChart().getXYPlot().getDomainAxis().getRange();
                  y = pnHisto.getChart().getXYPlot().getRangeAxis().getRange();
                }
                histo = get();
                if (x != null)
                  histo.getXYPlot().getDomainAxis().setRange(x);
                if (y != null)
                  histo.getXYPlot().getRangeAxis().setRange(y);
                pnHisto = new EChartPanel(histo, true, true, true, true, true);
                histo.getLegend().setVisible(true);

                southwest.removeAll();
                southwest.add(pnHisto, BorderLayout.CENTER);
                southwest.getParent().revalidate();
                southwest.getParent().repaint();
              } catch (InterruptedException e) {
                e.printStackTrace();
              } catch (ExecutionException e) {
                e.printStackTrace();
              }
            }
          }.execute();
        } catch (Exception e1) {
          e1.printStackTrace();
        }

      }
    }
  }

  protected void updateGaussian() {
    if (cbGaussianFit.isSelected())
      addGaussianCurves();
    else
      hideGaussianCurves();
  }

  protected void addGaussianCurves() {
    if (pnHisto != null)
      addGaussianCurve(pnHisto.getChart().getXYPlot());
  }

  /**
   * Add Gaussian curve to the plot
   * 
   * @param p
   */
  protected void addGaussianCurve(XYPlot p) {
    try {
      double gMin = Module.doubleFromTxt(txtGaussianLower);
      double gMax = Module.doubleFromTxt(txtGaussianUpper);
      int sigDigits = Module.intFromTxt(getTxtPrecision());

      XYDataset data = p.getDataset(0);
      hideGaussianCurve(p);

      EChartFactory.addGaussianFit(p, data, 0, gMin, gMax, sigDigits, true);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  protected void hideGaussianCurves() {
    if (pnHisto != null)
      hideGaussianCurve(pnHisto.getChart().getXYPlot());
  }

  protected void hideGaussianCurve(XYPlot p) {
    if (p.getDatasetCount() > 1) {
      p.setRenderer(p.getDatasetCount() - 1, null);
      p.setDataset(p.getDatasetCount() - 1, null);
    }
  }

  public JPanel getSouthwest() {
    return southwest;
  }

  public JTextField getTxtBinWidth() {
    return txtBinWidth;
  }

  public JCheckBox getCbExcludeSmallerNoise() {
    return cbExcludeSmallerNoise;
  }

  public JLabel getLbStats() {
    return lbStats;
  }

  public JTextField getTxtRangeX() {
    return txtRangeX;
  }

  public JTextField getTxtRangeY() {
    return txtRangeY;
  }

  public JTextField getTxtRangeYEnd() {
    return txtRangeYEnd;
  }

  public JTextField getTxtRangeXEnd() {
    return txtRangeXEnd;
  }

  public JCheckBox getCbGaussianFit() {
    return cbGaussianFit;
  }

  public JTextField getTxtGaussianLower() {
    return txtGaussianLower;
  }

  public JTextField getTxtGaussianUpper() {
    return txtGaussianUpper;
  }

  public JTextField getTxtPrecision() {
    return txtPrecision;
  }
}
