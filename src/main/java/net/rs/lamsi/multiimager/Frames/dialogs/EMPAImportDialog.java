package net.rs.lamsi.multiimager.Frames.dialogs;

import java.awt.BorderLayout;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.ImageGroupMD;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.DatasetLinesMD;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.ScanLineMD;
import net.rs.lamsi.general.framework.modules.SettingsModule;
import net.rs.lamsi.general.myfreechart.gestures.ChartGesture;
import net.rs.lamsi.general.myfreechart.gestures.ChartGesture.Button;
import net.rs.lamsi.general.myfreechart.gestures.ChartGesture.Entity;
import net.rs.lamsi.general.myfreechart.gestures.ChartGesture.Event;
import net.rs.lamsi.general.myfreechart.gestures.ChartGestureEvent;
import net.rs.lamsi.general.myfreechart.gestures.ChartGestureHandler;
import net.rs.lamsi.general.myfreechart.swing.EChartPanel;
import net.rs.lamsi.general.settings.SettingsHolder;
import net.rs.lamsi.general.settings.importexport.SettingsImzMLImageImport;
import net.rs.lamsi.general.settings.preferences.SettingsGeneralPreferences;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.utils.DialogLoggerUtil;
import net.rs.lamsi.utils.FileAndPathUtil;
import net.rs.lamsi.utils.myfilechooser.FileTypeFilter;
import net.rs.lamsi.utils.threads.ProgressUpdateTask;
import net.rs.lamsi.utils.useful.dialogs.ProgressDialog;
import weka.gui.WrapLayout;

public class EMPAImportDialog extends JFrame {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  private enum ExtractMode {
    MAX, SUM;
  }

  public static final String[] ELEMENTS =
      new String[] {"Li,5188,80", "Na,9397,80", "Si,10370,80", "S,11100,80", "Ca,13040,80",
          "Cu,15580,80", "Zn,15960,80", "Se,17550,80", "Mo,19140,80", "Sn,21510,80"};

  private SettingsGeneralPreferences preferences;
  private JFileChooser fc = new JFileChooser();
  private final JPanel contentPanel = new JPanel();
  private JTextField txtMZWindow;
  private JCheckBox cbUseMZWindow;
  private JTextArea txtMZList;
  private JPanel pnChartView;
  private File currentFile;
  private int[] currentXYZ;
  private int[] maxXYZ;

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

        JButton btnCreateImagesFromList = new JButton("create images from list");
        btnCreateImagesFromList.addActionListener(e -> createImagesFromList());
        panel.add(btnCreateImagesFromList);

        panel.add(new JLabel("Extract intensities as"));
        intensityMode = new JComboBox<>(ExtractMode.values());
        intensityMode.setSelectedItem(ExtractMode.MAX);
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
        txtMZWindow.setText("50");
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
            txtMZList = new JTextArea();
            txtMZList.setText(Arrays.stream(ELEMENTS).collect(Collectors.joining("\n")));
            scrollPane.setViewportView(txtMZList);
          }
        }
        {
          JLabel lblMzListcenterwindow = new JLabel("m/z list (title,center,window)");
          lblMzListcenterwindow.setHorizontalAlignment(SwingConstants.CENTER);
          lblMzListcenterwindow.setFont(new Font("Tahoma", Font.BOLD, 12));
          panel.add(lblMzListcenterwindow, BorderLayout.NORTH);
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

  private void createImagesFromList() {
    String[] lines = txtMZList.getText().split("\n");
    ArrayList<String> titles = new ArrayList<>();
    ArrayList<double[]> values = new ArrayList<>();
    try {
      for (String s : lines) {
        String[] sep = s.replaceAll(" ", "").split(",");
        titles.add(sep[0]);

        double[] v = new double[2];
        v[0] = Double.parseDouble(sep[1]);
        v[1] = sep.length > 2 ? Double.parseDouble(sep[2]) : 0;
        values.add(v);
      }
    } catch (Exception e) {
      DialogLoggerUtil.showErrorDialog(this,
          "Enter values as mz center, mz window (mz window is optional)", e);
      return;
    }
    double window = SettingsModule.doubleFromTxt(txtMZWindow);

    // create images
    int i = 0;
    for (double[] centerPM : values) {
      double width = centerPM[1] > 0 ? centerPM[1] : window;
      extractImages(titles.get(i), centerPM[0], width);
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
          double[] data = importData(currentFile);
          createChart(data);
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
    XYSeries series = new XYSeries("data");
    for (int i = 0; i < data.length; i++) {
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

  private void extractImages(String title, double center, double width) {
    if (currentFile != null) {
      extractImagesXY(title, center, width);
    }
  }

  private void extractImagesXY(String title, double center, double width) {
    logger.info("Extract xy images");
    boolean validTitle = title != null && !title.isEmpty();
    ExtractMode mode = (ExtractMode) intensityMode.getSelectedItem();

    ScanLineMD[] lines;
    DecimalFormat f = new DecimalFormat("0");
    String titles[] = new String[maxXYZ[2] + 1];
    for (int i = 0; i < titles.length; i++) {
      titles[i] = (validTitle ? title + " " : "") + "z=" + i + " of " + f.format(center)
          + " (xwidth=" + f.format(width) + ")";
    }
    // create lines
    lines = new ScanLineMD[maxXYZ[1] + 1];
    for (int y = 0; y <= maxXYZ[1]; y++) {
      logger.info("Line {}", y);
      // z as image dimensions
      double[][] intensities = new double[maxXYZ[2] + 1][maxXYZ[0] + 1];
      for (double[] v : intensities)
        Arrays.fill(v, Double.NaN);

      for (int x = 0; x <= maxXYZ[0]; x++) {
        for (int z = 0; z <= maxXYZ[2]; z++) {
          intensities[z][x] = extractIntensity(mode, center, width, x, y, z);
        }
      }
      lines[y] = new ScanLineMD(null, intensities);
    }
    // Generate Image2D from scanLines
    DatasetLinesMD dataset = new DatasetLinesMD(lines);
    ImageGroupMD tmpgroup = dataset.createImageGroup(currentFile, titles);
    String gtitle = (validTitle ? title + " " : "") + "z-stack of " + f.format(center) + " (xwidth="
        + f.format(width) + ")";
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
   * All different images of the same z pane in one group
   * 
   * @param tmpgroup
   */
  private void addAllImagesToZGroups(ImageGroupMD tmpgroup) {
    int z = 0;
    for (Image2D img : tmpgroup.getImagesOnly()) {
      boolean addGroupToProject = false;
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

  public SettingsImzMLImageImport createSettings() {
    SettingsImzMLImageImport sett = new SettingsImzMLImageImport();

    String[] lines = txtMZList.getText().split("\n");
    ArrayList<double[]> values = new ArrayList<>();
    try {
      for (String s : lines) {
        String[] sep = s.replaceAll(" ", "").split(",");
        double[] v = new double[2];
        v[0] = Double.parseDouble(sep[0]);
        v[1] = sep.length > 1 ? Double.parseDouble(sep[1]) : 0;
        values.add(v);
      }
    } catch (Exception e) {
      DialogLoggerUtil.showErrorDialog(this,
          "Enter values as mz center, mz window (mz window is optional)", e);
      return null;
    }
    double[][] data = new double[values.size()][];
    for (int i = 0; i < data.length; i++) {
      data[i] = values.get(i);
    }
    double window = SettingsModule.doubleFromTxt(txtMZWindow);

    sett.setAll(data, cbUseMZWindow.isSelected(), window == 0 ? 0.02d : window);
    return sett;
  }

  public JCheckBox getCbUseMZWindow() {
    return cbUseMZWindow;
  }

  public JTextField getTxtMZWindow() {
    return txtMZWindow;
  }

  public JTextArea getTxtMZList() {
    return txtMZList;
  }

}
