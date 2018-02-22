package net.rs.lamsi.multiimager.Frames.multiimageframe;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.chart.plot.XYPlot;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.ImageGroupMD;
import net.rs.lamsi.general.datamodel.image.TestImageFactory;
import net.rs.lamsi.general.datamodel.image.data.interf.ImageDataset;
import net.rs.lamsi.general.datamodel.image.listener.RawDataChangedListener;
import net.rs.lamsi.general.framework.basics.RangeSliderColumn;
import net.rs.lamsi.general.heatmap.Heatmap;
import net.rs.lamsi.general.heatmap.HeatmapFactory;
import net.rs.lamsi.general.myfreechart.ChartLogics;
import net.rs.lamsi.general.myfreechart.listener.ChartZoomConnector;
import net.rs.lamsi.general.myfreechart.plot.XYSquaredPlot;
import net.rs.lamsi.general.myfreechart.plot.XYSquaredPlot.Scale;
import net.rs.lamsi.general.myfreechart.plots.image2d.ImageRenderer;
import net.rs.lamsi.general.myfreechart.swing.EChartPanel;
import net.rs.lamsi.general.settings.image.visualisation.SettingsAlphaMap;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow.LOG;
import net.rs.lamsi.utils.DialogLoggerUtil;
import net.rs.lamsi.utils.FileAndPathUtil;
import net.rs.lamsi.utils.myfilechooser.FileTypeFilter;
import net.rs.lamsi.utils.mywriterreader.TxtWriter;
import net.rs.lamsi.utils.mywriterreader.XSSFExcelWriterReader;

public class MultiImageFrame extends JFrame {
  private static final long serialVersionUID = 1L;
  private JFrame thisframe;
  private JPanel contentPane;
  private JSplitPane split;
  private JPanel pnGridImg;
  private int GRID_COL = 4;
  // data table
  private JTable table;
  private MultiImageTableModel tableModel;
  // data
  private ImageGroupMD group;
  private SettingsAlphaMap settings;
  // keep track of updating, dont update every heatmap all the time only shown ones
  private boolean[] uptodate;
  // insert heatmaps in this array
  private Heatmap[] heat;
  // panels in each grid
  private JPanel pn[];

  // same zoom
  private ChartZoomConnector zoomConnect;

  // boolean map for visible pixel according to range limitations of other images
  // map[lines][dp]
  private Boolean[][] map;
  private boolean[] maplinear;
  private JMenuBar menuBar;
  private JMenu mnSettings;
  private JMenuItem mntmColumns;
  private JPanel panel_1;
  private JPanel panel_2;
  private JCheckBox cbShowTitles;
  private JCheckBox cbShowAxes;
  private JCheckBox cbKeepAspectRatio;
  private JSpinner spinnerColumns;
  private JLabel lblCol;
  private JMenu mnExport;
  private JMenuItem mntmExportMap;
  private JMenuItem mntmExportData;
  private JFileChooser chooserMap = new JFileChooser();

  // last width of grid panel
  private int lastWidthPn = 0;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          MultiImageFrame frame = new MultiImageFrame();
          frame.setVisible(true);

          ImageGroupMD group = TestImageFactory.createTestStandard(10);
          frame.init(group);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the frame.
   */
  public MultiImageFrame() {
    thisframe = this;
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setBounds(100, 100, 790, 462);

    menuBar = new JMenuBar();
    setJMenuBar(menuBar);

    mnExport = new JMenu("Export");
    menuBar.add(mnExport);

    mntmExportMap = new JMenuItem("Export map");
    mntmExportMap.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // export boolean map as binary
        if (chooserMap.showSaveDialog(thisframe) == JFileChooser.APPROVE_OPTION) {
          File file = chooserMap.getSelectedFile();
          FileTypeFilter filter = (FileTypeFilter) chooserMap.getFileFilter();
          // excel or csv/txt
          if (filter.getExtension().equalsIgnoreCase("xlsx")) {
            saveMapToExcel(file);
          } else {
            saveMapToTxt(file, filter.getExtension());
          }
        }
      }
    });
    mnExport.add(mntmExportMap);


    JMenuItem mntmExportMap = new JMenuItem("Export binary map");
    mntmExportMap.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // export boolean map as binary
        if (chooserMap.showSaveDialog(thisframe) == JFileChooser.APPROVE_OPTION) {
          File file = chooserMap.getSelectedFile();
          FileTypeFilter filter = (FileTypeFilter) chooserMap.getFileFilter();
          // excel or csv/txt
          if (filter.getExtension().equalsIgnoreCase("xlsx")) {
            saveBinaryMapToExcel(file);
          } else {
            saveBinaryMapToTxt(file, filter.getExtension());
          }
        }
      }
    });
    mnExport.add(mntmExportMap);

    mntmExportData = new JMenuItem("Export data");
    mntmExportData.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // export boolean map as binary
        if (chooserMap.showSaveDialog(thisframe) == JFileChooser.APPROVE_OPTION) {
          File file = chooserMap.getSelectedFile();
          FileTypeFilter filter = (FileTypeFilter) chooserMap.getFileFilter();
          // excel or csv/txt
          if (filter.getExtension().equalsIgnoreCase("xlsx")) {
            saveAllToExcel(file);
          } else {
            saveAllToTxt(file, filter.getExtension());
          }
        }
      }
    });
    mnExport.add(mntmExportData);

    mnSettings = new JMenu("Settings");
    menuBar.add(mnSettings);

    mntmColumns = new JMenuItem("Set columns");
    mntmColumns.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // open dialog get integer
        try {
          int s = Integer.valueOf(JOptionPane.showInputDialog("How many columns?"));
          setColumns(s);
        } catch (Exception ex) {
        }
      }
    });
    mnSettings.add(mntmColumns);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    contentPane.setLayout(new BorderLayout(0, 0));
    setContentPane(contentPane);

    split = new JSplitPane();
    split.setResizeWeight(0.5);
    split.setOrientation(JSplitPane.VERTICAL_SPLIT);
    contentPane.add(split, BorderLayout.CENTER);

    JScrollPane scrollTable = new JScrollPane();
    split.setLeftComponent(scrollTable);

    table = new JTable();
    scrollTable.setViewportView(table);


    panel_1 = new JPanel();
    split.setRightComponent(panel_1);
    panel_1.setLayout(new BorderLayout(0, 0));

    panel_2 = new JPanel();
    FlowLayout flowLayout = (FlowLayout) panel_2.getLayout();
    flowLayout.setAlignment(FlowLayout.LEFT);
    flowLayout.setVgap(0);
    panel_1.add(panel_2, BorderLayout.NORTH);

    cbShowTitles = new JCheckBox("Titles");
    cbShowTitles.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        showTitles(cbShowTitles.isSelected());
      }
    });
    cbShowTitles.setSelected(true);
    panel_2.add(cbShowTitles);

    cbShowAxes = new JCheckBox("Axes");
    cbShowAxes.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        setAxesVisible(cbShowAxes.isSelected());
        updateGridView();
      }
    });
    panel_2.add(cbShowAxes);

    cbKeepAspectRatio = new JCheckBox("Keep aspect ratio");
    cbKeepAspectRatio.setSelected(true);
    panel_2.add(cbKeepAspectRatio);
    cbKeepAspectRatio.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        boolean keepRatio = cbKeepAspectRatio.isSelected();
        for (Heatmap map : heat) {
          XYPlot plot = map.getChart().getXYPlot();
          if (plot instanceof XYSquaredPlot) {
            XYSquaredPlot sp = ((XYSquaredPlot) plot);
            sp.setScaleMode(keepRatio ? Scale.FIXED_WIDTH : Scale.IGNORE);
            sp.setMaximumHeight(600);
          }
        }

        updateGridView();
      }
    });

    spinnerColumns = new JSpinner();

    spinnerColumns.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        setColumns((int) spinnerColumns.getValue());
      }
    });
    spinnerColumns.setModel(new SpinnerNumberModel(GRID_COL, 1, 20, 1));
    panel_2.add(spinnerColumns);

    lblCol = new JLabel("col");
    panel_2.add(lblCol);

    JScrollPane scrollImages = new JScrollPane();
    panel_1.add(scrollImages);
    scrollImages.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

    JPanel panel = new JPanel();
    scrollImages.setViewportView(panel);
    panel.setLayout(new BorderLayout(0, 0));

    pnGridImg = new JPanel();
    panel.add(pnGridImg, BorderLayout.CENTER);
    // gridLayout = new GridLayout(1, 2, 5, 5);
    // pnGridImg.setLayout(gridLayout);
    pnGridImg.setLayout(null);

    pnGridImg.addComponentListener(new ComponentListener() {
      @Override
      public void componentResized(ComponentEvent e) {
        if (lastWidthPn != getPnGridImg().getWidth()) {
          resizeHeatmaps();
          lastWidthPn = getPnGridImg().getWidth();
        }
      }

      @Override
      public void componentMoved(ComponentEvent e) {}

      @Override
      public void componentShown(ComponentEvent e) {}

      @Override
      public void componentHidden(ComponentEvent e) {}
    });
    // frame resize
    this.addComponentListener(new ComponentListener() {
      @Override
      public void componentResized(ComponentEvent e) {
        getPnGridImg()
            .setSize(new Dimension(thisframe.getWidth() - 30, getPnGridImg().getHeight()));
      }

      @Override
      public void componentMoved(ComponentEvent e) {}

      @Override
      public void componentShown(ComponentEvent e) {}

      @Override
      public void componentHidden(ComponentEvent e) {}
    });

    FileTypeFilter filter = new FileTypeFilter("xlsx", "Excel file");
    chooserMap.addChoosableFileFilter(filter);
    chooserMap.setFileFilter(filter);
    chooserMap.addChoosableFileFilter(new FileTypeFilter("txt", "Text file"));
    chooserMap.addChoosableFileFilter(new FileTypeFilter("csv", "Comma separated text file"));
  }

  private void resizeHeatmaps() {
    int width = (getPnGridImg().getWidth() - 22) / GRID_COL;
    int height = getPnGridImg().getHeight();

    int size = pn.length;
    // rows

    if (height < 200) {
      height = 200;
    }

    // save max height to know where to start the next row
    int maxHeight = 0;
    int y = 0;
    // for all pn
    for (int i = 0; i < size; i++) {
      JPanel p = pn[i];
      if (p.getComponents().length > 0) {
        EChartPanel cp = (EChartPanel) p.getComponent(0);

        // calculate height if keep aspect ratio==true
        if (getCbKeepAspectRatio().isSelected()) {
          height = (int) ChartLogics.calcHeightToWidth(cp, width, false);
        }
        if (height > maxHeight)
          maxHeight = height;
        // pos
        int x = width * (i % GRID_COL);
        // next row
        if (i > 0 && (i % GRID_COL) == 0) {
          y += maxHeight;
          maxHeight = height;
        }

        // size
        Dimension dim = new Dimension(width, height);
        p.setBounds(x, y, width, height);
      }
    }
    // set size of parent
    getPnGridImg().setPreferredSize(new Dimension(getPnGridImg().getWidth(), y + maxHeight));
  }

  /**
   * sets them visible
   * 
   * @param selected
   */
  protected void showTitles(boolean show) {
    if (heat != null) {
      for (int i = 0; i < heat.length; i++) {
        heat[i].getShortTitle().setVisible(show);
      }
    }
  }

  /**
   * all charts that are not null are getting their show axes state
   * 
   * @param selected
   */
  protected void setAxesVisible(boolean show) {
    if (heat != null) {
      for (Heatmap h : heat) {
        if (h != null) {
          h.getPlot().getDomainAxis().setVisible(show);
          h.getPlot().getRangeAxis().setVisible(show);
        }
      }
    }
  }

  protected void setColumns(int s) {
    ImageEditorWindow.log("Set col: " + s, LOG.DEBUG);
    GRID_COL = s;
    updateGridView();
  }

  /**
   * initilize this frame
   * 
   * @param img
   * @param folder collection name
   */
  public void init(ImageGroupMD group) {
    this.group = group;
    settings = group.getSettAlphaMap();
    // already has a table model?
    if (settings.getTableModel() != null) {
      tableModel = settings.getTableModel();
      tableModel.setWindow(this);
    } else {
      // new table model
      tableModel = new MultiImageTableModel(this);
      // for all images2d (first in list)
      for (int k = 0; k < group.image2dCount(); k++)
        // put them into the table
        tableModel.addRow(new MultiImgTableRow(k, (Image2D) group.get(k)));
    }
    table.setModel(tableModel);
    settings.setTableModel(tableModel);
    // range slider column
    RangeSliderColumn col = new RangeSliderColumn(table, 6, 0, 100);


    heat = new Heatmap[group.size()];
    uptodate = new boolean[group.size()];
    pn = new JPanel[group.size()];
    // for all images
    for (int k = 0; k < group.size(); k++) {
      // create new pn as place holder
      final int index = k;
      pn[k] = new JPanel(new BorderLayout());
      getPnGridImg().add(pn[k]);
    }

    // add raw data changed listener for direct imaging
    group.getLastImage2D().addRawDataChangedListener(new RawDataChangedListener() {
      @Override
      public void rawDataChangedEvent(ImageDataset data) {
        if (data.getLinesCount() > 0) {
          createNewMap();
          for (int i = 0; i < uptodate.length; i++) {
            heat[i] = null;
            uptodate[i] = false;
          }
          updateGridView();
        }
      }
    });
    // show them in the grid view
    fireProcessingChanged();
    //
    this.setVisible(true);
  }

  /**
   * gets called if data processing changes taget function!
   */
  public void fireProcessingChanged() {
    for (int i = 0; i < uptodate.length; i++)
      uptodate[i] = false;
    // update boolean map for visible pixel
    createNewMap(); // + update
    // update grid and show charts
    updateGridView();
  }

  /**
   * create new map
   */
  protected void createNewMap() {
    // only if different size
    Image2D first = group.getFirstImage2D();

    ImageDataset data = first.getData();

    int maxlines = first.getMaxLinesCount();
    int maxdp = first.getMaxLineLength();

    boolean different = map == null || maxlines != map.length || maxdp != map[0].length;
    if (different) {
      settings.setMap(null);
    }
    updateMap();

    if (map != null)
      ImageEditorWindow.log("new map " + map.length, LOG.DEBUG);
  }

  /**
   * update boolean map for visible points
   * 
   */
  private void updateMap() {
    try {
      map = group.updateMap();

      // save linear one
      maplinear = settings.convertToLinearMap();
    } catch (Exception ex) {
      ex.printStackTrace();
      ImageEditorWindow.log(ex.getMessage(), LOG.ERROR);
    }
  }

  /**
   * takes all rows in account and shows images in grid view
   */
  public void updateGridView() {
    // empty grid
    for (JPanel p : pn)
      p.removeAll();
    // gridindex
    int gi = 0;
    // go through table, update and add heats
    for (int i = 0; i < uptodate.length; i++) {
      MultiImgTableRow row = null;
      if (i < tableModel.getRowCount())
        row = tableModel.getRowList().get(i);
      // if row==null -> image overlay
      if (row == null || row.isShowing()) {
        // update needed?
        updateGrid(gi, i, false);
        // increment
        gi++;
      }
    }
    // set sizes
    resizeHeatmaps();
    // update fram
    getPnGridImg().revalidate();
    getPnGridImg().repaint();
  }

  /**
   * updates a grid
   * 
   * @param gi
   * @param imgIndex
   */
  private void updateGrid(int gi, int imgIndex, boolean repaint) {
    if (!uptodate[imgIndex])
      updateChart(imgIndex);
    // add to grid view
    if (heat[imgIndex] != null) {
      Heatmap h = heat[imgIndex];
      EChartPanel cp = h.getChartPanel();
      cp.setPreferredSize(new Dimension(50, 50));
      pn[gi].add(cp, BorderLayout.CENTER);
    }
    if (repaint) {
      getPnGridImg().revalidate();
      getPnGridImg().repaint();
    }
  }

  /**
   * searches for the grid index
   * 
   * @param imgIndex
   */
  private void updateGrid(int imgIndex) {
    // is overlay?
    if (imgIndex >= tableModel.getRowCount())
      updateGrid(imgIndex, imgIndex, true);
    // gridindex
    int gi = 0;
    // go through table
    for (int i = 0; i < imgIndex; i++)
      if (tableModel.getRowList().get(i).isShowing())
        // increment
        gi++;
    // update grid
    updateGrid(gi, imgIndex, true);
  }

  /**
   * creates new chart for i gets called after change in update grid view
   * 
   * @param i
   */
  private void updateChart(int i) {
    try {
      // generate heat if not already
      if (heat[i] == null) {
        if (zoomConnect == null)
          zoomConnect = new ChartZoomConnector(e -> resizeHeatmaps());
        zoomConnect.clear();

        heat[i] = HeatmapFactory.generateHeatmap(group.get(i));

        // add all charts
        // set all to the same zoom factor
        for (Heatmap h : heat)
          if (h != null)
            zoomConnect.add(h.getChartPanel());

        // show axes?
        heat[i].getPlot().getDomainAxis().setVisible(getCbShowAxes().isSelected());
        heat[i].getPlot().getRangeAxis().setVisible(getCbShowAxes().isSelected());

        // title?
        XYPlot plot = heat[i].getPlot();
        plot.addAnnotation(heat[i].getShortTitle().getAnnotation());
      }

      // set new map
      if (heat[i].getRenderer() instanceof ImageRenderer)
        ((ImageRenderer) heat[i].getRenderer()).setMapLinear(maplinear);
      heat[i].getChart().fireChartChanged();
      // set uptodate
      uptodate[i] = true;
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * creates new chart for i gets called after change in update grid view
   */
  private void updateAllCharts() {
    for (int i = 0; i < group.size(); i++)
      updateChart(i);
  }


  // ###################################################################
  // EXPORT: data, map, all, binary map


  /**
   * update map and save to txt file
   * 
   * @param extension
   */
  protected void saveAllToTxt(File file, String ext) {
    try {
      TxtWriter writer = new TxtWriter();
      //
      String fname = FileAndPathUtil.eraseFormat(file.getAbsolutePath());
      // save table
      writer.writeDataArrayToFile(FileAndPathUtil.getRealFileName(fname + "_table", ext),
          tableModel.toArray(true), ",");

      // save map
      updateMap();
      writer.writeDataArrayToFile(FileAndPathUtil.getRealFileName(fname + "_map", ext), map, ",");

      // save multi map
      if (isBinaryMapAvailable())
        saveBinaryMapToTxt(new File(fname + "_multimap"), ext);

      // save all img
      for (int i = 0; i < group.image2dCount(); i++) {
        updateChart(i);
        // export to new file
        writer.writeDataArrayToFile(
            FileAndPathUtil.getRealFileName(fname + "_" + i + "_" + group.get(i).getTitle(), ext),
            ((Image2D) group.get(i)).toIMatrix(true, map), ",");
      }
      // show dialog
      DialogLoggerUtil.showMessageDialogForTime(thisframe, "SUCCESS", "File written!", 2);
    } catch (Exception ex) {
      ex.printStackTrace();
      // show dialog
      DialogLoggerUtil.showErrorDialog(thisframe, "ERROR while saving", "File not written");
    }
  }

  /**
   * update map and save to xlsx file
   * 
   * @param extension
   */
  protected void saveAllToExcel(File file) {
    try {
      XSSFExcelWriterReader writer = new XSSFExcelWriterReader();
      XSSFWorkbook wb = new XSSFWorkbook();
      // write table
      XSSFSheet sheet = writer.getSheet(wb, "table");
      writer.writeDataArrayToSheet(sheet, tableModel.toArray(true), 0, 0, true);

      // map
      updateMap();
      sheet = writer.getSheet(wb, "map");
      writer.writeDataArrayToSheet(sheet, map, 0, 0, true);

      // multi map
      if (isBinaryMapAvailable()) {
        sheet = writer.getSheet(wb, "multimap");
        Object[][] bmap = group.createBinaryMap();
        writer.writeDataArrayToSheet(sheet, bmap, 0, 0, true);
      }

      // write all images
      for (int i = 0; i < group.image2dCount(); i++) {
        updateChart(i);
        // export to new file
        sheet = writer.getSheet(wb, i + " " + group.get(i).getTitle());
        writer.writeDataArrayToSheet(sheet, ((Image2D) group.get(i)).toIMatrix(true, map), 0, 0,
            true);
      }

      writer.saveWbToFile(new File(FileAndPathUtil.getRealFileName(file, "xlsx")), wb);
      writer.closeAllWorkbooks();
      // show dialog
      DialogLoggerUtil.showMessageDialogForTime(thisframe, "SUCCESS", "File written!", 2);
    } catch (Exception ex) {
      ex.printStackTrace();
      // show dialog
      DialogLoggerUtil.showErrorDialog(thisframe, "ERROR while saving", "File not written");
    }
  }

  /**
   * update map and save to txt file
   * 
   * @param extension
   */
  protected void saveMapToTxt(File file, String ext) {
    try {
      updateMap();
      new TxtWriter().writeDataArrayToFile(FileAndPathUtil.getRealFileName(file, ext), map, ",");
      // show dialog
      DialogLoggerUtil.showMessageDialogForTime(thisframe, "SUCCESS", "File written!", 2);
    } catch (Exception ex) {
      ex.printStackTrace();
      // show dialog
      DialogLoggerUtil.showErrorDialog(thisframe, "ERROR while saving", "File not written");
    }
  }

  /**
   * update map and save to xlsx file
   * 
   * @param extension
   */
  protected void saveMapToExcel(File file) {
    try {
      updateMap();
      XSSFExcelWriterReader writer = new XSSFExcelWriterReader();
      XSSFWorkbook wb = new XSSFWorkbook();
      XSSFSheet sheet = writer.getSheet(wb, "map");
      writer.writeDataArrayToSheet(sheet, map, 0, 0, true);
      writer.saveWbToFile(new File(FileAndPathUtil.getRealFileName(file, "xlsx")), wb);
      writer.closeAllWorkbooks();
      // show dialog
      DialogLoggerUtil.showMessageDialogForTime(thisframe, "SUCCESS", "File written!", 2);
    } catch (Exception ex) {
      ex.printStackTrace();
      // show dialog
      DialogLoggerUtil.showErrorDialog(thisframe, "ERROR while saving", "File not written");
    }
  }

  /**
   * update map and save to txt file binary map:
   * 
   * @param extension
   */
  protected void saveBinaryMapToTxt(File file, String ext) {
    try {
      Object[][] bmap = group.createBinaryMap();
      new TxtWriter().writeDataArrayToFile(FileAndPathUtil.getRealFileName(file, ext), bmap, ",");
      DialogLoggerUtil.showMessageDialogForTime(thisframe, "SUCCESS", "File written!", 2);
    } catch (Exception ex) {
      ex.printStackTrace();
      // show dialog
      DialogLoggerUtil.showErrorDialog(thisframe, "ERROR while saving", "File not written");
    }
  }

  /**
   * update map and save to xlsx file
   * 
   * @param extension
   */
  protected void saveBinaryMapToExcel(File file) {
    try {
      Object[][] bmap = group.createBinaryMap();
      XSSFExcelWriterReader writer = new XSSFExcelWriterReader();
      XSSFWorkbook wb = new XSSFWorkbook();
      XSSFSheet sheet = writer.getSheet(wb, "map");
      writer.writeDataArrayToSheet(sheet, bmap, 0, 0, true);
      writer.saveWbToFile(new File(FileAndPathUtil.getRealFileName(file, "xlsx")), wb);
      writer.closeAllWorkbooks();
      // show dialog
      DialogLoggerUtil.showMessageDialogForTime(thisframe, "SUCCESS", "File written!", 2);
    } catch (Exception ex) {
      ex.printStackTrace();
      // show dialog
      DialogLoggerUtil.showErrorDialog(thisframe, "ERROR while saving", "File not written");
    }
  }



  // ###################################################################
  // getters and setters
  public JSplitPane getSplit() {
    return split;
  }

  public JPanel getPnGridImg() {
    return pnGridImg;
  }

  public JTable getTable() {
    return table;
  }

  public JCheckBox getCbKeepAspectRatio() {
    return cbKeepAspectRatio;
  }

  public JSpinner getSpinner() {
    return spinnerColumns;
  }

  public JCheckBox getCbShowAxes() {
    return cbShowAxes;
  }

  public JCheckBox getCbShowTitles() {
    return cbShowTitles;
  }

  /**
   * more than one range selected?
   * 
   * @return
   */
  public boolean isBinaryMapAvailable() {
    boolean one = false;
    for (int i = 0; i < tableModel.getRowList().size(); i++) {
      MultiImgTableRow row = tableModel.getRowList().get(i);
      if (row.isUseRange() && one)
        return true;
      if (row.isUseRange())
        one = true;
    }
    return false;
  }

}
