package net.rs.lamsi.multiimager.Frames.dialogs.empaimport;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.ImageGroupMD;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.DatasetLinesMD;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.ScanLineMD;
import net.rs.lamsi.general.framework.layout.WrapLayout;
import net.rs.lamsi.general.framework.modules.SettingsModule;
import net.rs.lamsi.general.myfreechart.gestures.ChartGesture;
import net.rs.lamsi.general.myfreechart.gestures.ChartGesture.Button;
import net.rs.lamsi.general.myfreechart.gestures.ChartGesture.Entity;
import net.rs.lamsi.general.myfreechart.gestures.ChartGesture.Event;
import net.rs.lamsi.general.myfreechart.gestures.ChartGestureEvent;
import net.rs.lamsi.general.myfreechart.gestures.ChartGestureHandler;
import net.rs.lamsi.general.myfreechart.swing.EChartPanel;
import net.rs.lamsi.general.myfreechart.themes.ChartThemeFactory;
import net.rs.lamsi.general.myfreechart.themes.MyStandardChartTheme;
import net.rs.lamsi.general.settings.SettingsHolder;
import net.rs.lamsi.general.settings.preferences.SettingsGeneralPreferences;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.utils.FileAndPathUtil;
import net.rs.lamsi.utils.myfilechooser.FileTypeFilter;
import net.rs.lamsi.utils.threads.ProgressUpdateTask;
import net.rs.lamsi.utils.useful.dialogs.ProgressDialog;

public class EMPAImportDialog extends JFrame {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  private enum ExtractMode {
    MAX, SUM;
  }

  public static final String[] STANDARD_CAL = new String[] {
      "﻿m/z cal,create img,isotope,dp,mz,width,relative", "true,true,7Li,5185,7.0155,0,1.00",
      "true,true,23Na,9397,22.9892,0,1.00", "true,true,28Si,10376,27.9764,0,1.00",
      "true,true,32S,11094,31.9715,0,1.00", "true,true,40Ca,12550,39.962,0,1.00",
      "true,true,63Cu,15580,62.929,0,1.00", "true,true,65Cu,15847,64.9272,0,0.45",
      "true,true,64Zn,15725,63.9286,0,1.00", "true,true,66Zn,15966,65.9255,0,0.57",
      "true,true,68Zn,16207,67.9243,0,0.39", "true,false,76Se,17128,75.9187,0,0.19",
      "true,false,77Se,17241,76.9194,0,0.15", "true,true,78Se,17353,77.9168,0,0.48",
      "true,true,80Se,17573,79.916,0,1.00", "true,false,82Se,17793,81.9162,0,0.18",
      "true,true,92Mo,18842,91.9063,0,0.62", "true,true,94Mo,19045,93.9045,0,0.38",
      "true,true,95Mo,19145,94.9053,0,0.66", "true,true,96Mo,19246,95.9041,0,0.69",
      "true,true,97Mo,19347,96.9055,0,0.40", "true,true,98Mo,19446,97.9049,0,1.00",
      "true,true,100Mo,19643,99.9069,0,0.40", "true,true,116Sn,21152,115.9012,0,0.45",
      "true,false,117Sn,21245,116.9024,0,0.24", "true,true,118Sn,21337,117.9011,0,0.74",
      "true,false,119Sn,21425,118.9028,0,0.26", "true,true,120Sn,21515,119.9016,0,1.00",
      "true,false,122Sn,21695,121.9029,0,0.14", "true,false,124Sn,21868,123.9047,0,0.18"};

  private SettingsGeneralPreferences preferences;
  private JFileChooser fc = new JFileChooser();
  private JFileChooser fcCsv = new JFileChooser();
  private final JPanel contentPanel = new JPanel();
  private JTextField txtMZWindow;
  private JCheckBox cbUseMZWindow;
  private JTable txtMZList;
  private JPanel pnChartView;
  private File currentFile;
  private int[] currentXYZ;
  private int[] maxXYZ;
  private double[] lastData;

  // only if load to memory
  // data x y z dp
  private double[][][][] data = null;

  private DecimalFormat[] format;
  private String currentName;

  private File currentPath;

  private JCheckBox cbInMemory;

  private JTextField txtX;

  private JTextField txtZ;

  private JTextField txtY;

  private ProgressUpdateTask task;
  private boolean allFilesImported;

  private JComboBox<ExtractMode> intensityMode;

  private EmpaTableModel tableModel;

  private double[] fits;
  private double[] fitsMZtoDP;

  private JCheckBox cbX;

  private JCheckBox cbY;

  private JCheckBox cbZ;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    try {
      EMPAImportDialog dialog = new EMPAImportDialog();
      ProgressDialog.initDialog(dialog);
      dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
      dialog.setVisible(true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Create the dialog.
   */
  public EMPAImportDialog() {
    preferences = SettingsHolder.getSettings().getSetGeneralPreferences();
    fc.addChoosableFileFilter(new FileTypeFilter("bin", "binary"));
    fc.setMultiSelectionEnabled(false);
    fcCsv.addChoosableFileFilter(new FileTypeFilter("csv", "comma separated values"));
    fcCsv.setMultiSelectionEnabled(false);
    setBounds(100, 100, 1024, 600);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(new BorderLayout(0, 0));
    {
      JPanel panel = new JPanel(new WrapLayout(WrapLayout.CENTER));
      contentPanel.add(panel, BorderLayout.NORTH);
      {
        cbInMemory = new JCheckBox("In memory");
        cbInMemory.setSelected(true);
        panel.add(cbInMemory);

        JButton btnLoadData = new JButton("Load data");
        btnLoadData.addActionListener(e -> importEmpaData());
        panel.add(btnLoadData);

        txtX = new JTextField();
        txtX.setText("0");
        panel.add(txtX);
        txtX.setColumns(3);

        txtY = new JTextField();
        txtY.setText("0");
        panel.add(txtY);
        txtY.setColumns(3);

        txtZ = new JTextField();
        txtZ.setText("0");
        panel.add(txtZ);
        txtZ.setColumns(3);


        JButton btnSetXYZ = new JButton("load XYZ");
        btnSetXYZ.addActionListener(e -> {
          try {
            int x = Integer.parseInt(txtX.getText());
            int y = Integer.parseInt(txtY.getText());
            int z = Integer.parseInt(txtZ.getText());
            showSpectrumAt(x, y, z);
          } catch (Exception e2) {
          }
        });
        panel.add(btnSetXYZ);

        cbX = new JCheckBox("x");
        cbX.setSelected(true);
        panel.add(cbX);

        cbY = new JCheckBox("y");
        cbY.setSelected(true);
        panel.add(cbY);

        cbZ = new JCheckBox("z");
        cbZ.setSelected(true);
        panel.add(cbZ);

        JButton btnCreateImagesFromList = new JButton("create images from list");
        btnCreateImagesFromList.addActionListener(e -> createImagesFromList());
        panel.add(btnCreateImagesFromList);

        panel.add(new JLabel("Extract intensities as"));
        intensityMode = new JComboBox<>(ExtractMode.values());
        intensityMode.setSelectedItem(ExtractMode.SUM);
        intensityMode.setPreferredSize(new Dimension(60, intensityMode.getPreferredSize().height));
        panel.add(intensityMode);

        // JButton btnPre = new JButton("Previous");
        // btnPre.addActionListener(e -> previousFile());
        // panel.add(btnPre);
        // JButton btnNext = new JButton("Next");
        // btnNext.addActionListener(e -> nextFile());
        // panel.add(btnNext);
      }
      {
        cbUseMZWindow = new JCheckBox("Override mz window with");
        panel.add(cbUseMZWindow);
      }
      {
        txtMZWindow = new JTextField();
        txtMZWindow.setText("0.8");
        panel.add(txtMZWindow);
        txtMZWindow.setColumns(4);
      }
    }
    {
      {
        JPanel panel = new JPanel();
        contentPanel.add(panel, BorderLayout.CENTER);
        panel.setLayout(new BorderLayout(0, 0));
        {
          pnChartView = new JPanel(new BorderLayout());
          panel.add(pnChartView, BorderLayout.CENTER);
        }
        {
          JLabel lblNewLabel = new JLabel("Spectrum");
          panel.add(lblNewLabel, BorderLayout.NORTH);
          lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
          lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
        }
      }
      {
        JPanel panel = new JPanel();
        contentPanel.add(panel, BorderLayout.EAST);
        panel.setLayout(new BorderLayout(0, 0));
        {
          JScrollPane scrollPane = new JScrollPane();
          panel.add(scrollPane, BorderLayout.CENTER);
          scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
          {
            txtMZList = new JTable();
            tableModel = new EmpaTableModel();
            txtMZList.setModel(tableModel);
            tableModel.loadTableData(STANDARD_CAL);
            updateMZCalibration();
            scrollPane.setViewportView(txtMZList);
          }
        }
        {
          JPanel pnTableMenu = new JPanel(new WrapLayout(WrapLayout.CENTER));
          panel.add(pnTableMenu, BorderLayout.NORTH);
          {
            JButton btnToggleCal = new JButton("Toggle cal");
            btnToggleCal.addActionListener(e -> toggleCalibration());
            pnTableMenu.add(btnToggleCal);
            JButton btnToggleImg = new JButton("Toggle img");
            btnToggleImg.addActionListener(e -> toggleImg());
            pnTableMenu.add(btnToggleImg);

            JButton btnUpdateMzCal = new JButton("Update m/z cal");
            btnUpdateMzCal.addActionListener(e -> updateMZCalibration());
            pnTableMenu.add(btnUpdateMzCal);

            JButton btnShowCali = new JButton("Show m/z cal");
            btnShowCali.addActionListener(e -> showMZCalibration());
            pnTableMenu.add(btnShowCali);
          }
          {
            JButton btnLoad = new JButton("Load");
            btnLoad.addActionListener(e -> loadTableData());
            pnTableMenu.add(btnLoad);
          }
          {
            JButton btnSave = new JButton("Save");
            btnSave.addActionListener(e -> saveTableData());
            pnTableMenu.add(btnSave);
          }
        }
      }
    }
    {
      JPanel buttonPane = new JPanel();
      buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
      getContentPane().add(buttonPane, BorderLayout.SOUTH);
      {
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            setVisible(false);
          }
        });
        cancelButton.setActionCommand("Cancel");
        buttonPane.add(cancelButton);
      }
    }
  }

  private void toggleImg() {
    List<EmpaTableRow> rows = tableModel.getRows();
    boolean allselected = rows.stream().allMatch(EmpaTableRow::isCreateImage);
    rows.stream().forEach(r -> r.setCreateImage(!allselected));
    tableModel.fireTableDataChanged();
  }

  private void toggleCalibration() {
    List<EmpaTableRow> rows = tableModel.getRows();
    boolean allselected = rows.stream().allMatch(EmpaTableRow::isUseAsMZCalibration);
    rows.stream().forEach(r -> r.setUseAsMZCalibration(!allselected));
    tableModel.fireTableDataChanged();
  }

  private void showMZCalibration() {
    updateMZCalibration();
    JFrame window = new JFrame("Calibration");
    XYSeries s2 = new XYSeries("cal");
    XYSeries s1 = new XYSeries("fit");
    List<EmpaTableRow> rows = tableModel.getRows();
    int minDP = 0;
    double maxDP = 0;
    for (EmpaTableRow r : rows) {
      if (r.isUseAsMZCalibration() && r.getDp() > 0 && r.getMz() > 0) {
        s1.add(r.getDp(), r.getMz());
        if (r.getDp() > maxDP)
          maxDP = r.getDp();
      }
    }

    for (int i = minDP; i < maxDP; i++) {
      double mz = dpToMZ(i);
      s2.add(i, mz);
    }
    XYSeriesCollection data = new XYSeriesCollection();
    data.addSeries(s1);
    data.addSeries(s2);

    EChartPanel chart = new EChartPanel(ChartFactory.createXYLineChart("", "dp", "m/z", data));
    MyStandardChartTheme theme = ChartThemeFactory.createBlackNWhiteTheme();
    theme.apply(chart.getChart());

    XYLineAndShapeRenderer renderer =
        (XYLineAndShapeRenderer) chart.getChart().getXYPlot().getRenderer();

    renderer.setSeriesPaint(0, Color.BLACK);
    renderer.setSeriesPaint(1, Color.BLACK);
    renderer.setSeriesShapesVisible(1, false);
    renderer.setSeriesLinesVisible(1, true);
    renderer.setSeriesShapesVisible(0, true);
    renderer.setSeriesLinesVisible(0, false);

    window.getContentPane().add(chart, BorderLayout.CENTER);
    window.pack();
    window.setVisible(true);
  }

  private void updateMZCalibration() {
    List<EmpaTableRow> rows = tableModel.getRows();
    final WeightedObservedPoints obs = new WeightedObservedPoints();
    final WeightedObservedPoints obsReversed = new WeightedObservedPoints();

    for (EmpaTableRow r : rows) {
      if (r.isUseAsMZCalibration() && r.getDp() > 0 && r.getMz() > 0) {
        obs.add(r.getDp(), r.getMz());
        obsReversed.add(r.getMz(), r.getDp());
      }
    }

    // Instantiate a quadratic polynomial fitter.
    final PolynomialCurveFitter fitter = PolynomialCurveFitter.create(2);
    fits = fitter.fit(obs.toList());
    fitsMZtoDP = fitter.fit(obsReversed.toList());

    System.out.println("a=" + fits[0] + "   b=" + fits[1] + "   c=" + fits[2]);
    System.out.println("m/z = " + fits[0] + " + x * " + fits[1] + " + x^2 * " + fits[2]);
    logger.info("a=" + fits[0] + "   b=" + fits[1] + "   c=" + fits[2]);
    logger.info("m/z = " + fits[0] + " + x * " + fits[1] + " + x^2 * " + fits[2]);
    if (lastData != null)
      createChart(lastData);
  }

  private void loadTableData() {
    if (fcCsv.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      tableModel.loadTableData(fcCsv.getSelectedFile());
    }
  }

  private void saveTableData() {
    if (fcCsv.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      File f = fcCsv.getSelectedFile();
      f = FileAndPathUtil.getRealFilePath(f, "csv");
      tableModel.saveTableData(f);
    }
  }

  private void createImagesFromList() {
    double window = SettingsModule.doubleFromTxt(txtMZWindow);
    List<EmpaTableRow> rows = tableModel.getRows();
    // create images
    int i = 0;
    for (EmpaTableRow row : rows) {
      double width = row.getWidth() > 0 ? row.getWidth() : window;
      extractImages(row.getIsotope(), row.getMz(), width);
      i++;
    }
  }

  private void showSpectrumAt(int x, int y, int z) {
    try {
      if (data != null) {
        try {
          createChart(data[x][y][z]);
        } catch (Exception e) {
          logger.error("", e);
        }
      } else {
        File f = getFileName(x, y, z);
        if (f != null && f.exists()) {
          lastData = importData(currentFile);
          createChart(lastData);
        }
      }
    } catch (Exception e) {
      logger.warn("Cannot show spectrum for xyz {}, {}, {}", x, y, z);
      logger.warn("Cannot show spectrum for xyz", e);
    }
  }

  private void nextFile() {
    if (currentFile != null) {
    }
  }

  private void previousFile() {
    if (currentFile != null) {

    }
  }

  private void importEmpaData() {
    // String fileName = "D:\\Daten\\empa\\sub\\Davide_High_Pt_2_000_000_01.bin";
    // currentFile = new File(fileName);
    // importEmpaData(currentFile);

    if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      currentFile = fc.getSelectedFile();
      importEmpaData(currentFile);
    }
  }

  private void importEmpaData(File file) {
    data = null;
    currentFile = file;
    parseCurrentFile();
    // show spectrum
    double[] spec = importData(currentFile);
    createChart(spec);
    if (cbInMemory.isSelected()) {
      // load all to memory in background
      loadAllData(currentFile);
    }
  }



  private void loadAllData(File file) {
    data = null;
    currentFile = file;
    parseCurrentFile();

    if (task != null && task.isStarted())
      task.cancel(false);

    final int totalFiles = (maxXYZ[0] + 1) * (maxXYZ[1] + 1) * (maxXYZ[2] + 1);
    task = new ProgressUpdateTask<double[][][][]>(totalFiles) {

      @Override
      protected double[][][][] doInBackground2() throws Exception {
        double[][][][] data = new double[maxXYZ[0] + 1][maxXYZ[1] + 1][maxXYZ[2] + 1][];

        IntStream.range(0, maxXYZ[0] + 1).parallel().forEach(x -> {
          if (!isCancelled()) {
            IntStream.range(0, maxXYZ[1] + 1).parallel().forEach(y -> {
              if (!isCancelled()) {
                IntStream.range(0, maxXYZ[2] + 1).forEach(z -> {
                  if (!isCancelled()) {
                    // set data
                    data[x][y][z] = importData(getFileName(x, y, z));
                    addProgressStep(1.0);
                  }
                });
              }
            });
          }
        });

        logger.info("Import finished");
        setAllFilesImported(true);
        if (isCancelled())
          return null;
        else
          return data;
      }
    };
    task.execute();
    task.addDoneListener(() -> {
      try {
        Object result = task.get();
        if (result != null) {
          data = (double[][][][]) result;
          logger.info("Import finished and data set to memory");
        }
      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
      }
    });

  }

  public void setAllFilesImported(boolean allFilesImported) {
    this.allFilesImported = allFilesImported;
  }

  private void createChart(double[] data) {
    if (data == null)
      return;

    XYSeries series = new XYSeries("data");
    for (int i = 0; i < data.length; i++) {
      if (fits != null) {
        double mz = dpToMZ(i);
        series.add(mz, data[i]);
      } else
        series.add(i, data[i]);
    }
    XYSeriesCollection dataset = new XYSeriesCollection(series);
    JFreeChart chart = ChartFactory.createXYLineChart("", "dp", "intensity", dataset);
    EChartPanel cp = new EChartPanel(chart);
    // add logic
    cp.getGestureAdapter().addGestureHandler(new ChartGestureHandler(
        new ChartGesture(Entity.PLOT, Event.CLICK, Button.BUTTON1), e -> handleChartClick(e)));

    pnChartView.removeAll();
    pnChartView.add(cp, BorderLayout.CENTER);
    pnChartView.revalidate();
    pnChartView.repaint();
  }

  private void handleChartClick(ChartGestureEvent e) {
    try {
      Point2D dim = e.getCoordinates(e.getChartPanel(), e.getX(), e.getY());
      double center = dim.getX();
      double width = Double.parseDouble(txtMZWindow.getText());
      logger.info("handle click at  center, width {} {}", center, width);
      extractImages(null, center, width);
    } catch (Exception e2) {
      logger.error("Error in chart click", e2);
    }
  }

  private void extractImages(String title, double centerMZ, double widthMZ) {
    if (currentFile != null) {
      if (cbX.isSelected())
        extractImagesYZ(title, centerMZ, widthMZ);
      if (cbY.isSelected())
        extractImagesXZ(title, centerMZ, widthMZ);
      if (cbZ.isSelected())
        extractImagesXY(title, centerMZ, widthMZ);
    }
  }

  /**
   * Extract images
   * 
   * @param title
   * @param centerMZ
   * @param widthMZ
   */
  private void extractImagesXY(String title, double centerMZ, double widthMZ) {
    logger.info("Extract xy images");
    boolean validTitle = title != null && !title.isEmpty();
    ExtractMode mode = (ExtractMode) intensityMode.getSelectedItem();

    ScanLineMD[] lines;
    DecimalFormat f = new DecimalFormat("0.000");
    String titles[] = new String[maxXYZ[2] + 1];
    for (int i = 0; i < titles.length; i++) {
      titles[i] = (validTitle ? title + " " : "") + "z=" + i + " of " + f.format(centerMZ)
          + " (mzwidth=" + f.format(widthMZ) + ")";
    }
    // create lines
    double centerDP = mzToDP(centerMZ);
    double widthDP = mzToDP(widthMZ);

    lines = new ScanLineMD[maxXYZ[1] + 1];
    for (int y = 0; y <= maxXYZ[1]; y++) {
      // z as image dimensions
      double[][] intensities = new double[maxXYZ[2] + 1][maxXYZ[0] + 1];
      for (double[] v : intensities)
        Arrays.fill(v, Double.NaN);

      for (int x = 0; x <= maxXYZ[0]; x++) {
        for (int z = 0; z <= maxXYZ[2]; z++) {
          intensities[z][x] = extractIntensity(mode, centerDP, widthDP, x, y, z);
        }
      }
      lines[y] = new ScanLineMD(null, intensities);
    }
    // Generate Image2D from scanLines
    DatasetLinesMD dataset = new DatasetLinesMD(lines);
    ImageGroupMD tmpgroup = dataset.createImageGroup(currentFile, titles);
    String gtitle = (validTitle ? title + " " : "") + "z-stack of " + f.format(centerMZ)
        + " (mzwidth=" + f.format(widthMZ) + ")";
    tmpgroup.setGroupName(gtitle);
    int z = 0;
    for (Image2D img : tmpgroup.getImagesOnly()) {
      String st = (validTitle ? title + " " : "") + "z=" + z;
      img.getSettings().getSettImage().setShortTitle(st);
      z++;
    }

    // add all images to separate z-pane groups
    addAllImagesToZGroups(tmpgroup);

    // add the z-stack group
    ImageEditorWindow.getEditor().getLogicRunner().addGroup(tmpgroup, currentName, true);
    logger.info("Creating images now");
  }

  /**
   * Extract images YZ
   * 
   * @param title
   * @param centerMZ
   * @param widthMZ
   */
  private void extractImagesYZ(String title, double centerMZ, double widthMZ) {
    logger.info("Extract yz images");
    boolean validTitle = title != null && !title.isEmpty();
    ExtractMode mode = (ExtractMode) intensityMode.getSelectedItem();

    ScanLineMD[] lines;
    DecimalFormat f = new DecimalFormat("0.000");
    String titles[] = new String[maxXYZ[2] + 1];
    for (int i = 0; i < titles.length; i++) {
      titles[i] = (validTitle ? title + " " : "") + "x=" + i + " of " + f.format(centerMZ)
          + " (mzwidth=" + f.format(widthMZ) + ")";
    }
    // create lines
    double centerDP = mzToDP(centerMZ);
    double widthDP = mzToDP(widthMZ);

    lines = new ScanLineMD[maxXYZ[2] + 1];
    for (int z = 0; z <= maxXYZ[2]; z++) {
      // z as image dimensions
      double[][] intensities = new double[maxXYZ[0] + 1][maxXYZ[1] + 1];
      for (double[] v : intensities)
        Arrays.fill(v, Double.NaN);

      for (int y = 0; y <= maxXYZ[1]; y++) {
        for (int x = 0; x <= maxXYZ[0]; x++) {
          intensities[x][y] = extractIntensity(mode, centerDP, widthDP, x, y, z);
        }
      }
      lines[z] = new ScanLineMD(null, intensities);
    }
    // Generate Image2D from scanLines
    DatasetLinesMD dataset = new DatasetLinesMD(lines);
    ImageGroupMD tmpgroup = dataset.createImageGroup(currentFile, titles);
    String gtitle = (validTitle ? title + " " : "") + "x-stack of " + f.format(centerMZ)
        + " (mzwidth=" + f.format(widthMZ) + ")";
    tmpgroup.setGroupName(gtitle);
    int z = 0;
    for (Image2D img : tmpgroup.getImagesOnly()) {
      String st = (validTitle ? title + " " : "") + "x=" + z;
      img.getSettings().getSettImage().setShortTitle(st);
      z++;
    }

    // add all images to separate z-pane groups
    addAllImagesToXGroups(tmpgroup);

    // add the z-stack group
    ImageEditorWindow.getEditor().getLogicRunner().addGroup(tmpgroup, currentName, true);
    logger.info("Creating images now");
  }

  /**
   * Extract images XZ
   * 
   * @param title
   * @param centerMZ
   * @param widthMZ
   */
  private void extractImagesXZ(String title, double centerMZ, double widthMZ) {
    logger.info("Extract xz images");
    boolean validTitle = title != null && !title.isEmpty();
    ExtractMode mode = (ExtractMode) intensityMode.getSelectedItem();

    ScanLineMD[] lines;
    DecimalFormat f = new DecimalFormat("0.000");
    String titles[] = new String[maxXYZ[2] + 1];
    for (int i = 0; i < titles.length; i++) {
      titles[i] = (validTitle ? title + " " : "") + "y=" + i + " of " + f.format(centerMZ)
          + " (mzwidth=" + f.format(widthMZ) + ")";
    }
    // create lines
    double centerDP = mzToDP(centerMZ);
    double widthDP = mzToDP(widthMZ);

    lines = new ScanLineMD[maxXYZ[2] + 1];
    for (int z = 0; z <= maxXYZ[2]; z++) {
      // z as image dimensions
      double[][] intensities = new double[maxXYZ[1] + 1][maxXYZ[0] + 1];
      for (double[] v : intensities)
        Arrays.fill(v, Double.NaN);

      for (int x = 0; x <= maxXYZ[0]; x++) {
        for (int y = 0; y <= maxXYZ[1]; y++) {
          intensities[y][x] = extractIntensity(mode, centerDP, widthDP, x, y, z);
        }
      }
      lines[z] = new ScanLineMD(null, intensities);
    }
    // Generate Image2D from scanLines
    DatasetLinesMD dataset = new DatasetLinesMD(lines);
    ImageGroupMD tmpgroup = dataset.createImageGroup(currentFile, titles);
    String gtitle = (validTitle ? title + " " : "") + "y-stack of " + f.format(centerMZ)
        + " (mzwidth=" + f.format(widthMZ) + ")";
    tmpgroup.setGroupName(gtitle);
    int z = 0;
    for (Image2D img : tmpgroup.getImagesOnly()) {
      String st = (validTitle ? title + " " : "") + "y=" + z;
      img.getSettings().getSettImage().setShortTitle(st);
      z++;
    }

    // add all images to separate z-pane groups
    addAllImagesToYGroups(tmpgroup);

    // add the z-stack group
    ImageEditorWindow.getEditor().getLogicRunner().addGroup(tmpgroup, currentName, true);
    logger.info("Creating images now");
  }

  private double dpToMZ(double dp) {
    if (fits == null)
      return dp;
    return fits[0] + dp * fits[1] + dp * dp * fits[2];
  }

  private double mzToDP(double mz) {
    if (fitsMZtoDP == null)
      return mz;
    return fitsMZtoDP[0] + mz * fitsMZtoDP[1] + mz * mz * fitsMZtoDP[2];
  }

  /**
   * All different images of the same z pane in one group
   * 
   * @param tmpgroup
   */
  private void addAllImagesToZGroups(ImageGroupMD tmpgroup) {
    int z = 0;
    for (Image2D img : tmpgroup.getImagesOnly()) {
      String groupname = "z=" + z;
      try {
        // add clone to a group
        Image2D clone = img.getCopy();
        ImageEditorWindow.getEditor().getLogicRunner().addImage(clone, currentName, groupname);
      } catch (Exception e) {
        logger.error("Cannot copy image {}", img.getTitle(), e);
      }
      z++;
    }
  }

  /**
   * All different images of the same x pane in one group
   * 
   * @param tmpgroup
   */
  private void addAllImagesToXGroups(ImageGroupMD tmpgroup) {
    int z = 0;
    for (Image2D img : tmpgroup.getImagesOnly()) {
      String groupname = "x=" + z;
      try {
        // add clone to a group
        Image2D clone = img.getCopy();
        ImageEditorWindow.getEditor().getLogicRunner().addImage(clone, currentName, groupname);
      } catch (Exception e) {
        logger.error("Cannot copy image {}", img.getTitle(), e);
      }
      z++;
    }
  }

  /**
   * All different images of the same y pane in one group
   * 
   * @param tmpgroup
   */
  private void addAllImagesToYGroups(ImageGroupMD tmpgroup) {
    int z = 0;
    for (Image2D img : tmpgroup.getImagesOnly()) {
      String groupname = "y=" + z;
      try {
        // add clone to a group
        Image2D clone = img.getCopy();
        ImageEditorWindow.getEditor().getLogicRunner().addImage(clone, currentName, groupname);
      } catch (Exception e) {
        logger.error("Cannot copy image {}", img.getTitle(), e);
      }
      z++;
    }
  }

  /**
   * 
   * @param mode
   * @param center in datapoints
   * @param width in datapoints
   * @param x
   * @param y
   * @param z
   * @return
   */
  private double extractIntensity(ExtractMode mode, double center, double width, int x, int y,
      int z) {
    // for test only as x is integers so far
    int lower = (int) (center - width / 2);
    int upper = (int) (lower + width);

    final double spec[];
    if (data != null) {
      spec = data[x][y][z];
    } else {
      File file = getFileName(x, y, z);
      if (file != null && file.exists()) {
        spec = importData(file);
      } else
        return Double.NaN;
    }

    if (spec.length < lower) {
      return Double.NaN;
    } else {
      switch (mode) {
        case SUM:
          double sum = 0;
          for (int i = lower; i <= upper && i < spec.length; i++)
            sum += spec[i];
          return sum;
        case MAX:
        default:
          double max = Double.NEGATIVE_INFINITY;
          for (int i = lower; i <= upper && i < spec.length; i++)
            if (max < spec[i])
              max = spec[i];
          return max;
      }
    }
  }

  private void parseCurrentFile() {
    currentXYZ = new int[3];
    maxXYZ = new int[3];
    Arrays.fill(currentXYZ, 0);
    Arrays.fill(maxXYZ, -1);
    if (currentFile != null) {
      try {
        String name = currentFile.getName();
        // sub .bin
        name = name.substring(0, name.length() - 4);
        String[] split = name.split("_");
        int s = split.length;
        currentXYZ[2] = Integer.parseInt(split[s - 1]);
        currentXYZ[1] = Integer.parseInt(split[s - 2]);
        currentXYZ[0] = Integer.parseInt(split[s - 3]);

        currentPath = currentFile.getParentFile();
        currentName = "";
        for (int i = 0; i < split.length - 3; i++) {
          currentName += split[i] + "_";
        }
        currentName = currentName.substring(0, currentName.length() - 1);

        // length of int string e.g. 001
        format = new DecimalFormat[3];
        for (int i = 0; i < format.length; i++) {
          format[i] = new DecimalFormat("0");
          format[i].setMinimumIntegerDigits(split[s - (2 - i) - 1].length());
        }
        // check x
        File checkFile = null;
        for (int d = 0; d < 3; d++) {
          while (true) {
            // all dimensions 0 but one counting up
            int[] cdims = new int[3];
            Arrays.fill(cdims, 0);
            cdims[d] = maxXYZ[d] + 1;

            // file exists?
            checkFile = getFileName(cdims);
            System.out.println("Checking file " + checkFile.getAbsolutePath());
            logger.info("Checking file " + checkFile.getAbsolutePath());
            if (checkFile != null && checkFile.exists())
              maxXYZ[d]++;
            else
              break;
          }
        }
      } catch (Exception e) {
        logger.warn("Cannot parse xyz coordinates from file: " + currentFile.getAbsolutePath(), e);
      }
    }
    data = null;
    System.out.println("max " + Arrays.toString(maxXYZ));
    logger.info("max " + Arrays.toString(maxXYZ));
  }


  private String compileName(int[] xyz) {
    String compiledName = currentName;
    for (int i = 0; i < xyz.length; i++) {
      compiledName += "_" + format[i].format(xyz[i]);
    }
    return compiledName;
  }

  private String compileName(int x, int y, int z) {
    return compileName(new int[] {x, y, z});
  }

  private File getFileName(int[] xyz) {
    return FileAndPathUtil.getRealFilePath(currentPath, compileName(xyz), "bin");
  }

  private File getFileName(int x, int y, int z) {
    return getFileName(new int[] {x, y, z});
  }

  /**
   * Imports little endian binary double values as intensities
   * 
   * @param fileName
   * @return
   */
  public double[] importData(File fileName) {
    return importData(fileName.getAbsolutePath());
  }

  public double[] importData(String fileName) {
    DoubleArrayList list = new DoubleArrayList();
    DataInputStream din = null;
    try {
      // reverse data
      double factor = -1;

      din = new DataInputStream(new FileInputStream(fileName));
      int last = 0;
      byte[] buffer = new byte[8];
      byte b;
      double data;
      while ((last = din.read(buffer)) == 8) {
        // reverse buffer
        data = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getDouble();
        list.add(data * factor);
      }
    } catch (

    EOFException ignore) {
    } catch (Exception ioe) {
      logger.warn("Execption in data import ", ioe);
    } finally {
      if (din != null) {
        try {
          din.close();
        } catch (IOException e1) {
          logger.warn("Execption in data import ", e1);
        }
      }
    }
    return list.toDoubleArray();
  }

  public JCheckBox getCbUseMZWindow() {
    return cbUseMZWindow;
  }

  public JTextField getTxtMZWindow() {
    return txtMZWindow;
  }

  public JTable getTxtMZList() {
    return txtMZList;
  }

}
