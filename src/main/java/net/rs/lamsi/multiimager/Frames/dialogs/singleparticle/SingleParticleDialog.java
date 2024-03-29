package net.rs.lamsi.multiimager.Frames.dialogs.singleparticle;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.function.DoubleFunction;
import java.util.stream.DoubleStream;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.rs.lamsi.general.datamodel.image.SingleParticleImage;
import net.rs.lamsi.general.framework.listener.DelayedDocumentListener;
import net.rs.lamsi.general.framework.modules.Module;
import net.rs.lamsi.general.myfreechart.EChartFactory;
import net.rs.lamsi.general.myfreechart.swing.EChartPanel;
import net.rs.lamsi.general.settings.image.special.SingleParticleSettings;

public class SingleParticleDialog extends JFrame {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  private SwingWorker<JFreeChart, Void> worker[];
  private final JPanel contentPanel = new JPanel();
  private SingleParticleImage img;
  private DelayedDocumentListener ddlUpdate;
  private EChartPanel pnHisto, pnHistoFiltered, pnHeat;
  private JPanel southwest;
  private JPanel southeast;
  private JPanel north;
  private JTextField txtBinWidth, txtBinShift;
  private JCheckBox cbExcludeSmallerNoise, cbThirdSQRT, cbAnnotations;
  private JLabel lbStats;
  private JTextField txtRangeX;
  private JTextField txtRangeY;
  private JTextField txtRangeXEnd;
  private JTextField txtRangeYEnd;
  private JTextField txtGaussianLower;
  private JTextField txtGaussianUpper;
  private JTextField txtPrecision;
  private JCheckBox cbGaussianFit;
  private JTextField txtNoiseLevel;
  private JTextField txtSplitPixel;

  private static final DecimalFormat form = new DecimalFormat("0.0");
  private JTextField txtMaxDPDecluster;
  private JCheckBox cbDecluster;
  private JLabel lblUpdating;
  private JCheckBox cbIncludeZeroBins;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    try {
      SingleParticleDialog dialog = new SingleParticleDialog();
      dialog.setVisible(true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Create the dialog.
   */
  public SingleParticleDialog() {
    this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    setBounds(100, 100, 903, 952);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(new BorderLayout(0, 0));
    {
      JPanel center1 = new JPanel();
      contentPanel.add(center1, BorderLayout.CENTER);
      center1.setLayout(new BorderLayout(0, 0));
      {
        JSplitPane splitPane = new JSplitPane();
        splitPane.setVisible(false);
        center1.add(splitPane, BorderLayout.NORTH);
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        {
          north = new JPanel();
          splitPane.setLeftComponent(north);
          north.setLayout(new BorderLayout(0, 0));
        }
      }
      {
        JPanel box = new JPanel();
        center1.add(box, BorderLayout.SOUTH);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        {
          JPanel updating = new JPanel();
          box.add(updating);
          {
            lblUpdating = new JLabel("UPDATING");
            lblUpdating.setForeground(Color.RED);
            lblUpdating.setFont(new Font("Tahoma", Font.BOLD, 14));
            updating.add(lblUpdating);
          }
        }
        {
          JPanel pnSplitFilter = new JPanel();
          box.add(pnSplitFilter);
          {
            JLabel lblSplitPixelSettings = new JLabel("Split pixel settings:");
            lblSplitPixelSettings.setFont(new Font("Tahoma", Font.BOLD, 11));
            pnSplitFilter.add(lblSplitPixelSettings);
          }
          {
            JLabel lblNoiseLevel = new JLabel("noise level:");
            pnSplitFilter.add(lblNoiseLevel);
          }
          {
            txtNoiseLevel = new JTextField();
            txtNoiseLevel.setToolTipText(
                "Noise level for split pixel filter. (Only data points>noise level are used)");
            txtNoiseLevel.setText("0");
            pnSplitFilter.add(txtNoiseLevel);
            txtNoiseLevel.setColumns(10);
          }
          {
            Component horizontalStrut = Box.createHorizontalStrut(20);
            pnSplitFilter.add(horizontalStrut);
          }
          {
            JLabel lblSplitPixel = new JLabel("split event pixel:");
            pnSplitFilter.add(lblSplitPixel);
          }
          {
            txtSplitPixel = new JTextField();
            txtSplitPixel.setToolTipText("Maximum number of data points in a split particle event");
            txtSplitPixel.setText("2");
            pnSplitFilter.add(txtSplitPixel);
            txtSplitPixel.setColumns(5);
          }
          {
            JButton btnUpdate = new JButton("update");
            btnUpdate.addActionListener(e -> update());
            pnSplitFilter.add(btnUpdate);
          }
        }
        {
          JPanel panel = new JPanel();
          box.add(panel);
          {
            JLabel lblDeclusterSettings = new JLabel("Decluster settings:");
            lblDeclusterSettings.setFont(new Font("Tahoma", Font.BOLD, 11));
            panel.add(lblDeclusterSettings);
          }
          {
            JLabel lblMax = new JLabel("max dp");
            panel.add(lblMax);
          }
          {
            txtMaxDPDecluster = new JTextField();
            txtMaxDPDecluster.setToolTipText(
                "MAximum consecutive data points > noise level. All data points are set to the data minimum if more data points are clustered together.");
            txtMaxDPDecluster.setText("0");
            txtMaxDPDecluster.setColumns(10);
            panel.add(txtMaxDPDecluster);
          }
          {
            Component horizontalStrut = Box.createHorizontalStrut(20);
            panel.add(horizontalStrut);
          }
          {
            cbDecluster = new JCheckBox("apply declustering");
            panel.add(cbDecluster);
          }
        }
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
            cbIncludeZeroBins = new JCheckBox("include zero-bins");
            cbIncludeZeroBins.setToolTipText(
                "BIns with zero intensity are usually excluded. Check to include all bins in the dataset.");
            pnHistoSett.add(cbIncludeZeroBins);
          }
          {
            cbExcludeSmallerNoise = new JCheckBox("exclude <noise level");
            cbExcludeSmallerNoise.setSelected(true);
            pnHistoSett.add(cbExcludeSmallerNoise);
          }
          {
            cbThirdSQRT = new JCheckBox("cube root(I)");
            cbThirdSQRT.setSelected(true);
            pnHistoSett.add(cbThirdSQRT);
          }
          {
            cbAnnotations = new JCheckBox("annotations");
            cbAnnotations.setSelected(true);
            pnHistoSett.add(cbAnnotations);
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
            txtBinWidth.setText("2030");
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
            txtBinShift.setText("500");
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
        JPanel south = new JPanel();
        center1.add(south, BorderLayout.CENTER);
        south.setLayout(new GridLayout(0, 2, 0, 0));
        {
          southwest = new JPanel();
          south.add(southwest);
          southwest.setLayout(new BorderLayout(0, 0));
        }
        {
          southeast = new JPanel();
          south.add(southeast);
          southeast.setLayout(new BorderLayout(0, 0));
        }
      }
    }
    {
      JPanel buttonPane = new JPanel();
      buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
      getContentPane().add(buttonPane, BorderLayout.SOUTH);
      {
      }
      {
      }
    }

    addListener();
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
    if (pnHistoFiltered != null) {
      LegendTitle legend = pnHistoFiltered.getChart().getLegend();
      if (legend != null)
        legend.setVisible(!legend.isVisible());
    }
  }

  private void addListener() {
    ddlUpdate = new DelayedDocumentListener(e -> update());

    // ranges
    DelayedDocumentListener ddlx = new DelayedDocumentListener(e -> applyXRange());
    DelayedDocumentListener ddly = new DelayedDocumentListener(e -> applyYRange());

    txtRangeX.getDocument().addDocumentListener(ddlx);
    txtRangeXEnd.getDocument().addDocumentListener(ddlx);
    txtRangeY.getDocument().addDocumentListener(ddly);
    txtRangeYEnd.getDocument().addDocumentListener(ddly);

    // histogram settings
    cbAnnotations.addItemListener(e -> updateAnnotations());
    cbThirdSQRT.addItemListener(e -> updateHistograms());
    cbExcludeSmallerNoise.addItemListener(e -> updateHistograms());
    txtBinWidth.getDocument()
        .addDocumentListener(new DelayedDocumentListener(e -> updateHistograms()));
    txtBinShift.getDocument()
        .addDocumentListener(new DelayedDocumentListener(e -> updateHistograms()));


    txtSplitPixel.getDocument().addDocumentListener(ddlUpdate);
    txtNoiseLevel.getDocument().addDocumentListener(ddlUpdate);
    txtMaxDPDecluster.getDocument().addDocumentListener(ddlUpdate);

    cbDecluster.addItemListener(e -> update());

    // add gaussian?
    cbGaussianFit.addItemListener(e -> updateGaussian());

    cbIncludeZeroBins.addItemListener(e -> updateHistograms());
  }


  private void applyXRange() {
    try {
      double x = Double.parseDouble(txtRangeX.getText());
      double xe = Double.parseDouble(txtRangeXEnd.getText());
      if (x < xe) {
        if (pnHisto != null)
          pnHisto.getChart().getXYPlot().getDomainAxis().setRange(x, xe);
        if (pnHistoFiltered != null)
          pnHistoFiltered.getChart().getXYPlot().getDomainAxis().setRange(x, xe);
      }
    } catch (Exception e2) {
      logger.error("", e2);
    }
  }

  private void applyYRange() {
    try {
      double y = Double.parseDouble(txtRangeY.getText());
      double ye = Double.parseDouble(txtRangeYEnd.getText());
      if (y < ye) {
        if (pnHisto != null)
          pnHisto.getChart().getXYPlot().getRangeAxis().setRange(y, ye);
        if (pnHistoFiltered != null)
          pnHistoFiltered.getChart().getXYPlot().getRangeAxis().setRange(y, ye);
      }
    } catch (Exception e2) {
      logger.error("", e2);
    }
  }


  /**
   * initialise dialog and module
   * 
   * @param img
   */
  public void setSPImage(SingleParticleImage img) {
    this.img = img;
    if (img != null) {
      // set to
      settingsToPanel(img.getSettings().getSettSingleParticle());

      ddlUpdate.stop();
      updateHistograms();

      contentPanel.revalidate();
      contentPanel.repaint();
    }
  }

  /**
   * Create new histograms
   * 
   * @throws Exception
   */
  private void updateHistograms() {
    if (img != null) {
      logger.debug("Updating histograms in single particle dialog");
      double binwidth2 = Double.NaN;
      double binShift2 = Double.NaN;
      try {
        binwidth2 = Double.parseDouble(txtBinWidth.getText());
        binShift2 = Double.parseDouble(txtBinShift.getText());
      } catch (Exception e) {
      }
      if (!Double.isNaN(binwidth2)) {
        SingleParticleSettings sett;
        try {
          // init worker
          if (worker == null)
            worker = new SwingWorker[2];

          for (SwingWorker<JFreeChart, Void> w : worker) {
            if (w != null && !w.isDone())
              w.cancel(true);
          }
          // set updating
          lblUpdating.setText("UPDATING");
          lblUpdating.setForeground(Color.RED);

          sett = (SingleParticleSettings) getSettings().copy();
          final double binwidth = binwidth2;
          final double binShift = Math.abs(binShift2);
          worker[0] = new SwingWorker<JFreeChart, Void>() {
            @Override
            protected JFreeChart doInBackground() throws Exception {
              double noise = sett.getNoiseLevel();
              // create histogram
              double[] data = null;
              if (cbExcludeSmallerNoise.isSelected()) {
                // get processed data from original image
                double[] dlist = img.toIArray(true, true);
                logger.debug("filter sp data by noise");
                data = DoubleStream.of(dlist).filter(d -> d >= noise).toArray();
              } else
                data = img.toIArray(true, true);

              Range r = EChartFactory.getBounds(data);

              DoubleFunction<Double> f =
                  cbThirdSQRT.isSelected() ? val -> Math.cbrt(val) : val -> val;

              JFreeChart chart =
                  EChartFactory.createHistogram(data, "I", binwidth, r.getLowerBound() - binShift,
                      r.getUpperBound(), f, cbIncludeZeroBins.isSelected());
              // add gaussian?
              if (cbGaussianFit.isSelected()) {
                addGaussianCurve(chart.getXYPlot());
              }
              return chart;
            }

            @Override
            protected void done() {
              if (isCancelled())
                return;

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

                checkIsUpdating();
                southwest.removeAll();
                southwest.add(pnHisto, BorderLayout.CENTER);
                updateAnnotations();
                southwest.getParent().revalidate();
                southwest.getParent().repaint();
              } catch (InterruptedException e) {
                logger.error("", e);
              } catch (ExecutionException e) {
                logger.error("", e);
              }
            }
          };
          worker[0].execute();

          worker[1] = new SwingWorker<JFreeChart, Void>() {
            @Override
            protected JFreeChart doInBackground() throws Exception {
              double noise = sett.getNoiseLevel();
              Range window = sett.getWindow();

              double[] filtered = img.getSPDataArraySelected();
              // do not show noise
              if (cbExcludeSmallerNoise.isSelected())
                filtered = Arrays.stream(filtered).filter(d -> d >= noise).toArray();

              Range r = EChartFactory.getBounds(filtered);

              DoubleFunction<Double> f =
                  cbThirdSQRT.isSelected() ? val -> Math.cbrt(val) : val -> val;

              JFreeChart chart = EChartFactory.createHistogram(filtered, "I", binwidth,
                  r.getLowerBound() - binShift, r.getUpperBound(), f,
                  cbIncludeZeroBins.isSelected());

              // add gaussian?
              if (cbGaussianFit.isSelected()) {
                addGaussianCurve(chart.getXYPlot());
              }
              return chart;
            }

            @Override
            protected void done() {
              if (isCancelled())
                return;

              // set stats
              double perc =
                  (img.getSelectedDPFiltered() / (double) img.getTotalDataPoints()) * 100.0;
              // stats
              String stats = "Stats: ";
              if (sett.isApplyDeclustering())
                stats += "Filtered clusters=" + img.getDeletedClustersSelected() + ";  ";
              stats += "Solved events=" + img.getSolvedEventsSelected() + " in "
                  + img.getSelectedDPFiltered() + " selected of " + img.getTotalDataPoints()
                  + " total data points (" + form.format(perc) + "%)";
              lbStats.setText(stats);
              // add histo
              JFreeChart histo;
              try {
                Range x = null, y = null;
                if (pnHistoFiltered != null) {
                  x = pnHistoFiltered.getChart().getXYPlot().getDomainAxis().getRange();
                  y = pnHistoFiltered.getChart().getXYPlot().getRangeAxis().getRange();
                }
                histo = get();
                if (x != null)
                  histo.getXYPlot().getDomainAxis().setRange(x);
                if (y != null)
                  histo.getXYPlot().getRangeAxis().setRange(y);
                pnHistoFiltered = new EChartPanel(histo, true, true, true, true, true);
                histo.getLegend().setVisible(true);

                checkIsUpdating();
                southeast.removeAll();
                southeast.add(pnHistoFiltered, BorderLayout.CENTER);
                updateAnnotations();
                southeast.getParent().revalidate();
                southeast.getParent().repaint();
              } catch (InterruptedException e) {
                logger.error("", e);
              } catch (ExecutionException e) {
                logger.error("", e);
              }
            }
          };
          worker[1].execute();
        } catch (Exception e1) {
          logger.error("", e1);
        }

      }
    }
  }

  protected void checkIsUpdating() {
    boolean isDone = true;
    for (SwingWorker<JFreeChart, Void> w : worker) {
      if (!w.isDone())
        isDone = false;
    }
    if (isDone) {
      lblUpdating.setText("DONE");
      lblUpdating.setForeground(Color.green);
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
    if (pnHistoFiltered != null)
      addGaussianCurve(pnHistoFiltered.getChart().getXYPlot());
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

      EChartFactory.addGaussianFit(p, data, 0, gMin, gMax, sigDigits, cbAnnotations.isSelected());
    } catch (Exception ex) {
      logger.error("", ex);
    }
  }

  protected void hideGaussianCurves() {
    if (pnHisto != null)
      hideGaussianCurve(pnHisto.getChart().getXYPlot());
    if (pnHistoFiltered != null)
      hideGaussianCurve(pnHistoFiltered.getChart().getXYPlot());
  }

  protected void hideGaussianCurve(XYPlot p) {
    if (p.getDatasetCount() > 1) {
      p.setRenderer(p.getDatasetCount() - 1, null);
      p.setDataset(p.getDatasetCount() - 1, null);
    }
  }

  /**
   * Clear and add annotations if cbAnnotations is selected
   */
  private void updateAnnotations() {
    if (img != null && pnHisto != null && pnHistoFiltered != null) {
      SingleParticleSettings sett = getSettings();
      double noise = sett.getNoiseLevel();
      Range window = sett.getWindow();

      if (window != null) {
        XYPlot[] plots =
            new XYPlot[] {pnHisto.getChart().getXYPlot(), pnHistoFiltered.getChart().getXYPlot()};

        for (XYPlot p : plots) {
          // remove old
          p.clearDomainMarkers();

          if (cbAnnotations.isSelected()) {
            // add
            p.addDomainMarker(new ValueMarker(window.getLowerBound(), p.getDomainCrosshairPaint(),
                p.getDomainCrosshairStroke()));
            p.addDomainMarker(new ValueMarker(window.getUpperBound(), p.getDomainCrosshairPaint(),
                p.getDomainCrosshairStroke()));
          }
        }
      }
    }
  }

  /**
   * Settings of current sp image
   * 
   * @return
   */
  public SingleParticleSettings getSettings() {
    if (img == null)
      return null;
    return img.getSettings().getSettSingleParticle();
  }

  public void update() {
    if (img != null) {
      writeAllToSettings(img.getSettings().getSettSingleParticle());
      updateHistograms();
    }
  }

  private void writeAllToSettings(SingleParticleSettings sett) {
    try {
      sett.setNoiseLevel(Module.doubleFromTxt(txtNoiseLevel));
      sett.setSplitPixel(Module.intFromTxt(txtSplitPixel));
      sett.setMaxAllowedDP(Module.intFromTxt(txtMaxDPDecluster));
      sett.setApplyDeclustering(cbDecluster.isSelected());
    } catch (Exception e) {
    }
  }

  private void settingsToPanel(SingleParticleSettings sett) {
    txtSplitPixel.setText(String.valueOf(sett.getSplitPixel()));
    txtNoiseLevel.setText(String.valueOf(sett.getNoiseLevel()));
    txtMaxDPDecluster.setText(String.valueOf(sett.getMaxAllowedDP()));
    cbDecluster.setSelected(sett.isApplyDeclustering());
  }


  public JPanel getSouthwest() {
    return southwest;
  }

  public JPanel getSoutheast() {
    return southeast;
  }

  public JPanel getNorth() {
    return north;
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

  public JTextField getTxtSplitPixel() {
    return txtSplitPixel;
  }

  public JTextField getTxtNoiseLevel() {
    return txtNoiseLevel;
  }

  public JTextField getTxtMaxDPDecluster() {
    return txtMaxDPDecluster;
  }

  public JCheckBox getCbDecluster() {
    return cbDecluster;
  }

  public JLabel getLblUpdating() {
    return lblUpdating;
  }

  public JCheckBox getCbIncludeZeroBins() {
    return cbIncludeZeroBins;
  }
}
