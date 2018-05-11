package net.rs.lamsi.multiimager.Frames.dialogs.singleparticle;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
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
import org.jfree.chart.ChartPanel;
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
import net.rs.lamsi.multiimager.FrameModules.ModuleSingleParticleImage;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;

public class SingleParticleDialog extends JFrame {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final JPanel contentPanel = new JPanel();

  private ModuleSingleParticleImage module;
  private SingleParticleImage img;
  private DelayedDocumentListener ddlUpdate, ddlRepaint;
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
      JPanel west = new JPanel();
      contentPanel.add(west, BorderLayout.WEST);
      west.setLayout(new BorderLayout(0, 0));
      {
        module = new ModuleSingleParticleImage(ImageEditorWindow.getEditor(), false, e -> update());
        west.add(module, BorderLayout.CENTER);
      }
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
        JButton okButton = new JButton("OK");
        buttonPane.add(okButton);
        getRootPane().setDefaultButton(okButton);
      }
      {
        JButton cancelButton = new JButton("Cancel");
        buttonPane.add(cancelButton);
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
    ddlUpdate = new DelayedDocumentListener(e -> autoUpdate());
    ddlRepaint = new DelayedDocumentListener(e -> repaint());
    //
    module.addAutoupdater(al -> autoUpdate(), cl -> autoUpdate(), ddlUpdate, e -> autoUpdate(),
        il -> autoUpdate());
    module.addAutoRepainter(al -> repaint(), cl -> repaint(), ddlRepaint, e -> repaint(),
        il -> repaint());

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
      boolean auto = module.isAutoUpdating();
      module.setAutoUpdating(false);
      // set to
      module.setCurrentImage(img, true);
      ddlUpdate.stop();
      ddlRepaint.stop();
      updateHistograms();
      // add image
      updateHeatmap();

      module.setAutoUpdating(auto);
      contentPanel.revalidate();
      contentPanel.repaint();
    }
  }

  /**
   * Create new histograms
   */
  private void updateHeatmap() {
    if (img != null) {
      try {

        new SwingWorker<ChartPanel, Void>() {
          @Override
          protected ChartPanel doInBackground() throws Exception {
            return null;
            // return pnHeat = HeatmapFactory.generateHeatmap(img).getChartPanel();
          }

          @Override
          protected void done() {
            north.removeAll();
            if (pnHeat != null)
              north.add(pnHeat, BorderLayout.CENTER);
            north.revalidate();
            north.repaint();
          }
        }.execute();
      } catch (Exception e) {
        logger.error("", e);
      }
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
      if (!Double.isNaN(binwidth2)) {
        SingleParticleSettings sett;
        try {
          sett = (SingleParticleSettings) getSettings().copy();
          final double binwidth = binwidth2;
          final double binShift = Math.abs(binShift2);
          new SwingWorker<JFreeChart, Void>() {
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
                updateAnnotations();
                southwest.getParent().revalidate();
                southwest.getParent().repaint();
              } catch (InterruptedException e) {
                logger.error("", e);
              } catch (ExecutionException e) {
                logger.error("", e);
              }
            }
          }.execute();

          new SwingWorker<JFreeChart, Void>() {
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
          }.execute();
        } catch (Exception e1) {
          logger.error("", e1);
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

  public void autoUpdate() {
    if (module.isAutoUpdating())
      update();
  }

  public void update() {
    if (img != null) {
      module.writeAllToSettings(img.getSettings());
      updateHistograms();
      updateHeatmap();
    }
  }

  public void repaint() {
    if (img != null)
      module.writeAllToSettings(img.getSettings());
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
}
