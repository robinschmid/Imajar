package net.rs.lamsi.multiimager.Frames.dialogs.selectdata;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.ImageGroupMD;
import net.rs.lamsi.general.datamodel.image.TestImageFactory;
import net.rs.lamsi.general.datamodel.image.interf.DataCollectable2D;
import net.rs.lamsi.general.framework.basics.JColorPickerButton;
import net.rs.lamsi.general.framework.basics.strokechooser.JStrokeChooserPanel;
import net.rs.lamsi.general.heatmap.Heatmap;
import net.rs.lamsi.general.heatmap.HeatmapFactory;
import net.rs.lamsi.general.myfreechart.ChartLogics;
import net.rs.lamsi.general.myfreechart.general.annotations.EXYShapeAnnotation;
import net.rs.lamsi.general.settings.gui2d.SettingsBasicStroke;
import net.rs.lamsi.general.settings.image.selection.SettingsPolygonSelection;
import net.rs.lamsi.general.settings.image.selection.SettingsSelections;
import net.rs.lamsi.general.settings.image.selection.SettingsShapeSelection;
import net.rs.lamsi.general.settings.image.selection.SettingsShapeSelection.ROI;
import net.rs.lamsi.general.settings.image.selection.SettingsShapeSelection.SHAPE;
import net.rs.lamsi.general.settings.image.selection.SettingsShapeSelection.SelectionMode;
import net.rs.lamsi.general.settings.image.visualisation.SettingsAlphaMap;
import net.rs.lamsi.multiimager.Frames.dialogs.DialogDataSaver;
import net.rs.lamsi.multiimager.Frames.dialogs.analytics.DataDialog;
import net.rs.lamsi.multiimager.Frames.dialogs.analytics.HistogramData;
import net.rs.lamsi.multiimager.Frames.dialogs.analytics.HistogramPanel;
import net.rs.lamsi.multiimager.test.TestQuantifier;
import net.rs.lamsi.utils.DialogLoggerUtil;
import net.rs.lamsi.utils.useful.dialogs.DialogLinearRegression;


public class SelectDataAreaDialog extends JFrame implements MouseListener, MouseMotionListener {

  private enum Position {
    HIDE, LEFT, TOP;

    public Position next() {
      switch (this) {
        case HIDE:
          return LEFT;
        case TOP:
          return HIDE;
        case LEFT:
          return TOP;
      }
      return HIDE;
    }

    public String toBorderLayout() {
      switch (this) {
        case HIDE:
          return null;
        case TOP:
          return BorderLayout.NORTH;
        case LEFT:
          return BorderLayout.WEST;
      }
      return null;
    }

    public int toSplitLayout() {
      switch (this) {
        case HIDE:
        case LEFT:
          return JSplitPane.HORIZONTAL_SPLIT;
        case TOP:
          return JSplitPane.VERTICAL_SPLIT;
      }
      return JSplitPane.VERTICAL_SPLIT;
    }
  }
  private enum KEY {
    SHRINK, SHIFT, ENLARGE
  }

  private final Logger logger = LoggerFactory.getLogger(getClass());
  // mystuff
  private Heatmap heat;
  private DataCollectable2D img;
  // save the relation in hashmap
  private HashMap<SettingsShapeSelection, EXYShapeAnnotation> map;
  //
  private SettingsSelections settSel;

  // last click or anything that was registered!
  private MouseEvent lastMouseEvent;

  // active selection (can be deleted, shifted etc)
  private SettingsShapeSelection currentSelect;
  private boolean isPressed = false;
  // coordinates of first and second mouse event (data space)
  private float x0, x1, y0, y1;
  private double lastConcentration = 0;
  // components
  private JPanel contentPane;
  private JPanel pnChartView;
  private JToggleButton btnChoose;
  private JTable table;
  private ShapeSelectionsTableModel tableModel;
  private JComboBox<SelectionMode> comboShape;
  private JComboBox comboSelectionMode;
  private JCheckBox cbPerformance;
  private JCheckBox cbMarkDp;
  private JComboBox<ROI> comboRoi;
  private JButton btnRegression;
  private JColorPickerButton cbtnColor;
  private JStrokeChooserPanel strokeChooserPanel;
  private JCheckBox cbShowAnnotations;
  private JCheckBox cbAlphaMapAsExclusion;
  // histo panel
  private HistogramPanel histoPanel;
  private Position histoPos = Position.HIDE;
  private JPanel pnSplitTop;
  private JSplitPane splitCenter;
  private JButton btnShowData;


  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          SelectDataAreaDialog frame = new SelectDataAreaDialog();
          TestQuantifier.rand = new Random(System.currentTimeMillis());
          ImageGroupMD img = TestImageFactory.createNonNormalImage(1);
          frame.startDialog((Image2D) img.get(0));
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }


  /**
   * Create the frame.
   */
  public SelectDataAreaDialog() {
    final JFrame thisframe = this;
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setBounds(100, 100, 778, 767);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    contentPane.setLayout(new BorderLayout(0, 0));
    setContentPane(contentPane);

    JSplitPane splitPane = new JSplitPane();
    splitPane.setResizeWeight(0.9);
    splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
    contentPane.add(splitPane, BorderLayout.CENTER);

    JPanel panel = new JPanel();
    splitPane.setRightComponent(panel);
    panel.setLayout(new BorderLayout(0, 0));

    JPanel pnSouthMenu = new JPanel();
    panel.add(pnSouthMenu, BorderLayout.SOUTH);
    FlowLayout flowLayout = (FlowLayout) pnSouthMenu.getLayout();
    flowLayout.setAlignment(FlowLayout.RIGHT);

    JButton btnOk = new JButton("Ok");
    btnOk.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // apply
        applySelections();
        dispatchEvent(new WindowEvent(thisframe, WindowEvent.WINDOW_CLOSING));
      }
    });
    pnSouthMenu.add(btnOk);

    JButton btnCancel = new JButton("Cancel");
    btnCancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        dispatchEvent(new WindowEvent(thisframe, WindowEvent.WINDOW_CLOSING));
      }
    });
    pnSouthMenu.add(btnCancel);

    JScrollPane scrollPane = new JScrollPane();
    panel.add(scrollPane, BorderLayout.CENTER);

    table = new JTable();
    scrollPane.setViewportView(table);

    pnSplitTop = new JPanel();
    splitPane.setLeftComponent(pnSplitTop);
    pnSplitTop.setLayout(new BorderLayout(0, 0));

    splitCenter = new JSplitPane();
    pnSplitTop.add(splitCenter, BorderLayout.CENTER);

    pnChartView = new JPanel();
    splitCenter.setRightComponent(pnChartView);
    splitCenter.setDividerLocation(0);
    splitCenter.setLeftComponent(new JPanel(null));
    pnChartView.setLayout(new BorderLayout(0, 0));

    JPanel pnNorthMenuContainer = new JPanel();
    contentPane.add(pnNorthMenuContainer, BorderLayout.NORTH);
    pnNorthMenuContainer.setLayout(new BorderLayout(0, 0));

    JPanel pnNorthMenu = new JPanel();
    pnNorthMenuContainer.add(pnNorthMenu, BorderLayout.NORTH);

    JButton btnToggleHisto = new JButton("toggle histo");
    btnToggleHisto.addActionListener(e -> toggleHisto());

    btnShowData = new JButton("show data");
    btnShowData.addActionListener(e -> showDataDialog());
    pnNorthMenu.add(btnShowData);
    pnNorthMenu.add(btnToggleHisto);


    cbAlphaMapAsExclusion = new JCheckBox("use alpha map exclusion");
    cbAlphaMapAsExclusion.setToolTipText(
        "Alpha map is used to exclude. Create alpha map in multi image explorer. All data points that are out of range are excluded from statistics");
    cbAlphaMapAsExclusion.setSelected(true);
    pnNorthMenu.add(cbAlphaMapAsExclusion);
    cbAlphaMapAsExclusion
        .addItemListener(il -> setAlphaMapExclude(cbAlphaMapAsExclusion.isSelected()));


    cbShowAnnotations = new JCheckBox("show annotations");
    cbShowAnnotations
        .setToolTipText("Show annotations for blank application direction and other descriptors");
    cbShowAnnotations.setSelected(true);
    pnNorthMenu.add(cbShowAnnotations);

    cbPerformance = new JCheckBox("Performance");
    cbPerformance.setToolTipText(
        "Calculates statistics at the end of the creation of a shape. (Saves performance)");
    pnNorthMenu.add(cbPerformance);

    cbMarkDp = new JCheckBox("Mark dp");
    cbMarkDp.addItemListener(e -> showMarkingMap(cbMarkDp.isSelected()));
    pnNorthMenu.add(cbMarkDp);

    JButton btnDeleteAll = new JButton("Delete All");
    btnDeleteAll.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        tableModel.removeAllRows();
        currentSelect = null;
        settSel.removeAllSelections();
        heat.getPlot().clearAnnotations();
        heat.getChart().fireChartChanged();
      }
    });
    pnNorthMenu.add(btnDeleteAll);

    JButton btnExportData = new JButton("Export data");
    btnExportData
        .setToolTipText("Export all selected/not excluded data points as raw or processed data.");
    btnExportData.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        DialogDataSaver.startDialogWith(img, settSel);
      }
    });

    btnRegression = new JButton("Regression");
    btnRegression.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // get data for regression
        double[][] data = settSel.getRegressionData();
        // create dialog
        if (data != null) {
          DialogLinearRegression d = DialogLinearRegression.createInstance(data, true);
          DialogLoggerUtil.centerOnScreen(d, true);
          d.setVisible(true);
        }
      }
    });
    pnNorthMenu.add(btnRegression);
    pnNorthMenu.add(btnExportData);

    JButton btnUpdateStats = new JButton("Update stats");
    btnUpdateStats.addActionListener(e -> updateAllStats());
    btnUpdateStats.setToolTipText("Updates statistics (is usually performed automatically)");
    pnNorthMenu.add(btnUpdateStats);

    JPanel panel_1 = new JPanel();
    pnNorthMenuContainer.add(panel_1, BorderLayout.CENTER);

    cbtnColor = new JColorPickerButton((Component) null);
    panel_1.add(cbtnColor);

    strokeChooserPanel = new JStrokeChooserPanel();
    strokeChooserPanel.getButton().setPreferredSize(new Dimension(50, 20));
    panel_1.add(strokeChooserPanel);

    comboRoi = new JComboBox<ROI>();
    panel_1.add(comboRoi);
    comboRoi.setModel(new DefaultComboBoxModel(ROI.values()));

    comboShape = new JComboBox();
    panel_1.add(comboShape);
    comboShape.setModel(new DefaultComboBoxModel(SHAPE.values()));

    comboSelectionMode = new JComboBox<SelectionMode>();
    panel_1.add(comboSelectionMode);
    comboSelectionMode.setModel(new DefaultComboBoxModel(SelectionMode.values()));



    JButton btnFinish = new JButton("Finish shape");
    panel_1.add(btnFinish);

    btnChoose = new JToggleButton("Choose/Zoom");
    panel_1.add(btnChoose);

    JButton btnDelete = new JButton("Delete");
    panel_1.add(btnDelete);
    btnDelete.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        deleteSelection(currentSelect);
        currentSelect = null;
      }
    });
    btnChoose.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        heat.getChartPanel().setMouseZoomable(((JToggleButton) e.getSource()).isSelected());
      }
    });
    btnFinish.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // concentration insert dialog for QUANTIFIER
        if (currentSelect != null && currentSelect.getRoi().equals(ROI.QUANTIFIER)
            && currentSelect.getConcentration() == 0) {
          // open dialog
          try {
            double concentration =
                Double.valueOf(JOptionPane.showInputDialog("concentration", "0"));
            currentSelect.setConcentration(concentration);
          } catch (Exception ex) {
          }
        }
        // desselect
        setCurrentSelect(null);
      }
    });
    comboSelectionMode.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        contentPane.requestFocusInWindow();
        // change color and stroke
        Color c = SettingsShapeSelection.getColorForSelectionMode(getCurrentSelectionMode());
        getCbtnColor().setColor(c);
      }
    });
    comboRoi.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        contentPane.requestFocusInWindow();
        // change color and stroke
        SettingsBasicStroke stroke = SettingsShapeSelection.getStrokeForROI(getCurrentRoiMode());
        getStrokeChooserPanel().setStroke(stroke);
      }
    });
    comboShape.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        contentPane.requestFocusInWindow();
      }
    });
    //
    addKeys();
    //
    WindowAdapter wl = new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent e) {
        super.windowClosed(e);
        // show selected excluded rects
        applySelections();
      }
    };
    this.addWindowListener(wl);

    contentPane.requestFocusInWindow();
  }

  private void showDataDialog() {
    if (currentSelect != null) {
      SelectionTableRow row = currentSelect.getDefaultTableRow(cbAlphaMapAsExclusion.isSelected());
      List<Double> d = row.getData();
      if (d != null) {
        DataDialog dialog = new DataDialog("Data of ROI " + currentSelect.getOrderNumber(), d);
        dialog.setVisible(true);
      }
    }
  }


  /**
   * Show, change position and hide large histogram
   * 
   * @return
   */
  private void toggleHisto() {
    boolean wasHidden = histoPos == Position.HIDE;
    // next position
    histoPos = histoPos.next();
    // create
    if (histoPanel == null)
      histoPanel = new HistogramPanel();

    if (wasHidden) {
      splitCenter.setLeftComponent(histoPanel);
      updateHistoPanelData();
    } else if (histoPos.equals(Position.HIDE)) {
      splitCenter.setLeftComponent(new JPanel(null));
    }
    splitCenter.setOrientation(histoPos.toSplitLayout());
    splitCenter.resetToPreferredSizes();
  }


  /**
   * Set new data to histogram
   */
  private void updateHistoPanelData() {
    if (!histoPos.equals(Position.HIDE) && currentSelect != null) {
      SelectionTableRow row = currentSelect.getDefaultTableRow(cbAlphaMapAsExclusion.isSelected());
      List<Double> d = row.getData();
      if (d != null) {
        HistogramData data = new HistogramData(
            d.stream().mapToDouble(Double::doubleValue).toArray(), row.getMin(), row.getMax());
        histoPanel.setData(data, false);
      }
    }
  }


  private void setAlphaMapExclude(boolean state) {
    // set to settSel
    settSel.setAlphaMapExclusionActive(state);
    // data has changed in table
    // update table
    tableModel.fireTableDataChanged();
    showMarkingMap(cbMarkDp.isSelected());
  }



  protected void updateAllStats() {
    // update all rects
    settSel.updateStatistics();
    // update table
    tableModel.fireTableDataChanged();
    updateHistoPanelData();
    repaint();
  }


  /**
   * shows all selected data points marked with a map
   * 
   * @param selected
   */
  protected void showMarkingMap(boolean selected) {
    if (img != null) {
      SettingsAlphaMap sett = img.getImageGroup().getSettAlphaMap();
      sett.setDrawMarks(selected);

      // erase markings
      sett.eraseMarkings();
      if (sett != null) {
        sett.setActive(cbAlphaMapAsExclusion.isSelected());
        if (selected) {
          // create map
          settSel.markAlphaMap(sett);
          img.setSettings(sett);
          sett.setAlpha(0.3f);
        }
        //
        heat.getChart().fireChartChanged();
      }
    }
  }


  /**
   * shift x by i screen pixel
   * 
   * @param i
   * @param key hold position and enlarge or shrink
   */
  protected void shiftCurrentRectX(int i, KEY key) {
    if (currentSelect == null)
      return;
    // translate to data space
    ChartPanel cp = heat.getChartPanel();
    float val = (float) ChartLogics.screenValueToPlotValue(cp, i).getX();
    // shift
    if (!((key.equals(KEY.ENLARGE) && i > 0) || (key.equals(KEY.SHRINK) && i < 0)))
      currentSelect.translate(val, 0);
    // enlarge?
    if (key.equals(KEY.ENLARGE))
      currentSelect.grow(Math.abs(val), 0);
    if (key.equals(KEY.SHRINK))
      currentSelect.grow(-Math.abs(val), 0);

    updateSelection();
  }

  protected void shiftCurrentRectY(int i, KEY key) {
    if (currentSelect == null)
      return;
    // translate to data space
    ChartPanel cp = heat.getChartPanel();
    float val = (float) ChartLogics.screenValueToPlotValue(cp, i).getY();
    // shift
    if (!((key.equals(KEY.ENLARGE) && i > 0) || (key.equals(KEY.SHRINK) && i < 0)))
      currentSelect.translate(0, val);
    // enlarge?
    if (key.equals(KEY.ENLARGE))
      currentSelect.grow(0, Math.abs(val));
    if (key.equals(KEY.SHRINK))
      currentSelect.grow(0, -Math.abs(val));

    updateSelection();
  }

  /**
   * Delete roi selection, annotation
   * 
   * @param r
   */
  protected void deleteSelection(SettingsShapeSelection r) {

    // tableModel.removeAllRows();
    // currentSelect = null;
    // settSel.removeAllSelections();
    // heat.getPlot().clearAnnotations();
    // heat.getChart().fireChartChanged();

    if (r != null) {
      // remove annotation
      EXYShapeAnnotation currentAnn = map.get(r);
      if (currentAnn != null)
        heat.getPlot().removeAnnotation(currentAnn, false);
      // remove from map
      map.remove(r);
      boolean state = true;
      // remove from tableModel (and ArrayList)
      // and automatically update statistics if it was an exclusion
      tableModel.removeRow(r, true);
      // repaint
      JFreeChart chart = heat.getChart();
      chart.fireChartChanged();
    }
  }

  /**
   * give all selections to img end of dialog
   */
  protected void applySelections() {
    // TODO
    // directly changing selections or apply here after closing the frame
    // listeners?
  }

  public void startDialog(DataCollectable2D img) {
    if (img != null) {
      try {
        heat = HeatmapFactory.generateHeatmap(img);
        this.img = img;
        // get all existing rects
        map = new HashMap<SettingsShapeSelection, EXYShapeAnnotation>();

        settSel =
            (SettingsSelections) img.getSettings().getSettingsByClass(SettingsSelections.class);
        settSel.setCurrentImage(img, false);

        // set table model
        tableModel = new ShapeSelectionsTableModel(settSel);
        table.setModel(tableModel);
        tableModel.init(table.getColumnModel());
        ListSelectionListener sellist = new ListSelectionListener() {
          @Override
          public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
              int i = table.getSelectedRow();
              if (i != -1)
                setCurrentSelect(getSelections().get(i));
            }
          }
        };
        table.getSelectionModel().addListSelectionListener(sellist);
        // table.getColumnModel().getSelectionModel().addListSelectionListener(sellist);


        // add all
        if (settSel.getSelections() != null)
          for (SettingsShapeSelection r : getSelections())
            updateAnnotation(r);

        // set color
        getCbtnColor()
            .setColor(SettingsShapeSelection.getColorForSelectionMode(getCurrentSelectionMode()));

        // add to screen
        getPnChartView().add(heat.getChartPanel(), BorderLayout.CENTER);
        // add mouse
        heat.getChartPanel().addMouseListener(this);
        heat.getChartPanel().addMouseMotionListener(this);
        heat.getChartPanel().setMouseZoomable(false);

        // init stroke and color
        SettingsBasicStroke stroke = SettingsShapeSelection.getStrokeForROI(getCurrentRoiMode());
        getStrokeChooserPanel().setStroke(stroke);
        Color c = SettingsShapeSelection.getColorForSelectionMode(getCurrentSelectionMode());
        getCbtnColor().setColor(c);

        // show
        setAlphaMapExclude(cbAlphaMapAsExclusion.isSelected());
        setVisible(true);
      } catch (Exception ex) {
        DialogLoggerUtil.showErrorDialog(this, "", ex);
        logger.error("Error while startig dialog", ex);
      }
    }
  }

  private ArrayList<SettingsShapeSelection> getSelections() {
    return settSel.getSelections();
  }

  private void updateAllAnnotations() {
    if (getSelections() == null)
      return;

    for (Iterator iterator = getSelections().iterator(); iterator.hasNext();) {
      SettingsShapeSelection s = (SettingsShapeSelection) iterator.next();
      updateAnnotation(s);
    }
  }

  /**
   * updates a annotation
   * 
   * @param r
   */
  private void updateAnnotation(final SettingsShapeSelection r) {
    // remove old
    EXYShapeAnnotation currentAnn = map.get(r);
    if (currentAnn == null) {
      currentAnn = r.createXYShapeAnnotation();
      heat.getPlot().addAnnotation(currentAnn, false);
      map.put(r, currentAnn);
      // add listener
      currentAnn.addChangeListener(e -> {
        // update statistics on change
        updateSelection(r);
        updateHistoPanelData();
      });
    } else {
      // set shape
      currentAnn.setShape(r.getShape());
    }
  }

  /**
   * adds a selection to the list and an annotation to the plot
   * 
   * @param r
   */
  private void addNewSelection(SettingsShapeSelection r) {
    r.setColor(getCurrentColor());
    r.setStroke(getCurrentStroke());
    setCurrentSelect(r);

    // put data in table
    tableModel.addRow(r, false);
    // update statistics
    updateSelection();
  }


  /**
   * update statistics, add annotation and show all in chart call on size/position/data processing
   * change
   */
  protected void updateSelection() {
    updateSelection(currentSelect);
    updateHistoPanelData();
  }

  /**
   * update statistics, add annotation and show all in chart call on size/position/data processing
   * change
   */
  protected void updateSelection(SettingsShapeSelection currentSelect) {
    if (img != null)
      img.getImageGroup().getSettAlphaMap().setActive(cbAlphaMapAsExclusion.isSelected());
    // Update rects
    if (currentSelect != null && currentSelect.getMode() == SelectionMode.EXCLUDE) {
      // update all rects
      settSel.updateStatistics();
    } else {
      // update this selection
      settSel.updateStatistics(currentSelect);
      tableModel.updateRow(currentSelect);
    }
    // update annotation of current only
    updateAnnotation(currentSelect);

    // update table
    tableModel.fireTableDataChanged();

    // update map
    showMarkingMap(getCbMarkDp().isSelected());

    // update chart
    JFreeChart chart = heat.getChart();
    chart.fireChartChanged();
    this.repaint();
    //
    logger.debug("UPDATE CHART");
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    // no selected?
    if (currentSelect == null)
      isPressed = false;
    // UPDATE THE CURRENT RECT
    if (isPressed) {
      if (!(getBtnChoose().isSelected())) {
        // add points to freehand
        if (getCurrentShape().equals(SHAPE.FREEHAND)
            && SettingsPolygonSelection.class.isInstance(currentSelect)) {
          ChartPanel cp = heat.getChartPanel();
          Point2D pos = ChartLogics.mouseXYToPlotXY(cp, e.getX(), e.getY());

          // only if travel distance was far enough
          if (lastMouseEvent == null || e.getPoint().distance(lastMouseEvent.getPoint()) > 5) {
            x1 = (float) pos.getX();
            y1 = (float) pos.getY();
            ((SettingsPolygonSelection) currentSelect).addPoint(x1, y1);
            lastMouseEvent = e;
            updateSelection();
          }
        } else {
          // end other shapes
          ChartPanel cp = heat.getChartPanel();
          Point2D pos = ChartLogics.mouseXYToPlotXY(cp, e.getX(), e.getY());

          x1 = (float) pos.getX();
          y1 = (float) pos.getY();


          if (currentSelect instanceof SettingsPolygonSelection) {
            float x = Math.min(x0, x1);
            float y = Math.min(y0, y1);
            float w = Math.abs(x0 - x1);
            float h = Math.abs(y0 - y1);
            switch (getCurrentShape()) {
              case RECT:
                ((SettingsPolygonSelection) currentSelect)
                    .setPolygonFromShape(new Rectangle2D.Float(x, y, w, h));
                break;
              case ELIPSE:
                ((SettingsPolygonSelection) currentSelect)
                    .setPolygonFromShape(new Ellipse2D.Float(x, y, w, h));
                break;
              default:
                currentSelect.setFirstAndSecondMouseEvent(x0, y0, x1, y1);
                break;
            }
          }

          lastMouseEvent = e;
          // update selection stats and annotation
          updateSelection();
        }
      }
    }
  }

  @Override
  public void mouseMoved(MouseEvent e) {}

  @Override
  public void mouseClicked(MouseEvent e) {
    if (e.getButton() == MouseEvent.BUTTON1) {
      if (getBtnChoose().isSelected()) {
        ChartPanel cp = heat.getChartPanel();
        Point2D pos = ChartLogics.mouseXYToPlotXY(cp, e.getX(), e.getY());

        setCurrentSelect(null);
        // choose current rect
        for (int i = 0; getSelections() != null && i < getSelections().size(); i++) {
          SettingsShapeSelection s = getSelections().get(i);
          if ((currentSelect == null || !currentSelect.equals(s))
              && s.contains(pos.getX(), pos.getY())) {
            // found rect
            setCurrentSelect(s);
            lastMouseEvent = e;
            return;
          }
        }
      } else if (getCurrentShape().equals(SHAPE.POLYGON)) {
        ChartPanel cp = heat.getChartPanel();
        Point2D pos = ChartLogics.mouseXYToPlotXY(cp, e.getX(), e.getY());
        if (currentSelect == null || !SettingsPolygonSelection.class.isInstance(currentSelect)) {
          // create new
          addNewSelection(new SettingsPolygonSelection(img, getCurrentRoiMode(),
              getCurrentSelectionMode(), x0, y0));
          lastMouseEvent = e;
        } else {
          // add points
          ((SettingsPolygonSelection) currentSelect).addPoint((float) pos.getX(),
              (float) pos.getY());
          lastMouseEvent = e;
        }

        // update selection stats and annotation
        updateSelection();
      }
    }
  }


  private void setCurrentSelect(SettingsShapeSelection s) {
    // deselect old
    if (currentSelect != null) {
      currentSelect.setHighlighted(false);
      updateAnnotation(currentSelect);
    }
    // select new
    currentSelect = s;
    if (s != null) {
      currentSelect.setHighlighted(true);
      updateAnnotation(currentSelect);
      updateHistoPanelData();
    }
    JFreeChart chart = heat.getChart();
    chart.fireChartChanged();
  }


  @Override
  public void mousePressed(MouseEvent e) {
    // stop if key modifiers are pressed
    if (e.isShiftDown() || e.isAltDown() || e.isControlDown())
      return;
    // creation of new selections
    if (e.getButton() == MouseEvent.BUTTON1 && !(getBtnChoose().isSelected())) {
      //
      ChartPanel cp = heat.getChartPanel();
      Point2D pos = ChartLogics.mouseXYToPlotXY(cp, e.getX(), e.getY());
      x0 = (float) pos.getX();
      y0 = (float) pos.getY();

      // inside the chart?
      Range yrange = cp.getChart().getXYPlot().getRangeAxis().getRange();
      Range xrange = cp.getChart().getXYPlot().getDomainAxis().getRange();

      if (xrange.contains(pos.getX()) && yrange.contains(pos.getY())) {
        // create new selection
        SettingsShapeSelection tmpSelect = null;
        switch (getCurrentShape()) {
          case RECT:
            tmpSelect = new SettingsPolygonSelection(img, getCurrentRoiMode(),
                getCurrentSelectionMode(), new Rectangle2D.Float(x0, y0, 1, 1));
            break;
          case ELIPSE:
            tmpSelect = new SettingsPolygonSelection(img, getCurrentRoiMode(),
                getCurrentSelectionMode(), new Ellipse2D.Float(x0, y0, 1, 1));
            break;
          case FREEHAND:
            tmpSelect = new SettingsPolygonSelection(img, getCurrentRoiMode(),
                getCurrentSelectionMode(), x0, y0);
            break;
        }
        if (tmpSelect != null) {
          isPressed = true;
          addNewSelection(tmpSelect);
          lastMouseEvent = e;
        }
      }
    }
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    if (e.getButton() == MouseEvent.BUTTON1 && isPressed) {
      //
      isPressed = false;

      if (currentSelect != null) {
        ChartPanel cp = heat.getChartPanel();
        Point2D pos = ChartLogics.mouseXYToPlotXY(cp, e.getX(), e.getY());
        x1 = (float) pos.getX();
        y1 = (float) pos.getY();

        if (currentSelect instanceof SettingsPolygonSelection) {
          float x = Math.min(x0, x1);
          float y = Math.min(y0, y1);
          float w = Math.abs(x0 - x1);
          float h = Math.abs(y0 - y1);
          switch (getCurrentShape()) {
            case RECT:
              ((SettingsPolygonSelection) currentSelect)
                  .setPolygonFromShape(new Rectangle2D.Float(x, y, w, h));
              break;
            case ELIPSE:
              ((SettingsPolygonSelection) currentSelect)
                  .setPolygonFromShape(new Ellipse2D.Float(x, y, w, h));
              break;
          }
        }
        // update selection stats and annotation
        updateSelection();

        // concentration insert dialog for QUANTIFIER
        if (currentSelect.getRoi().equals(ROI.QUANTIFIER)) {
          // open dialog
          try {
            lastConcentration = Double.valueOf(
                JOptionPane.showInputDialog("concentration", String.valueOf(lastConcentration)));
            currentSelect.setConcentration(lastConcentration);
          } catch (Exception ex) {
          }
        }
      }
    }
  }

  /**
   * keys
   */
  private void addKeys() {
    JPanel pn = (JPanel) getContentPane();
    InputMap im = pn.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "LEFT");
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "RIGHT");
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "UP");
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "DOWN");
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.SHIFT_MASK), "shift LEFT");
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.SHIFT_MASK), "shift RIGHT");
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.SHIFT_MASK), "shift UP");
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.SHIFT_MASK), "shift DOWN");
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.CTRL_MASK), "ctrl LEFT");
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.CTRL_MASK), "ctrl RIGHT");
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_MASK), "ctrl UP");
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_MASK), "ctrl DOWN");

    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.CTRL_MASK + InputEvent.SHIFT_MASK),
        "ctrl shift LEFT");
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.CTRL_MASK + InputEvent.SHIFT_MASK),
        "ctrl shift RIGHT");
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_MASK + InputEvent.SHIFT_MASK),
        "ctrl shift UP");
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_MASK + InputEvent.SHIFT_MASK),
        "ctrl shift DOWN");

    // shift
    pn.getActionMap().put("shift LEFT", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        shiftCurrentRectX(-1, KEY.ENLARGE);
      }
    });
    pn.getActionMap().put("shift RIGHT", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        shiftCurrentRectX(1, KEY.ENLARGE);
      }
    });
    pn.getActionMap().put("shift UP", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        shiftCurrentRectY(1, KEY.ENLARGE);
      }
    });
    pn.getActionMap().put("shift DOWN", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        shiftCurrentRectY(-1, KEY.ENLARGE);
      }
    });
    // ctrl for shrinking
    pn.getActionMap().put("ctrl LEFT", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        shiftCurrentRectX(-1, KEY.SHRINK);
      }
    });
    pn.getActionMap().put("ctrl RIGHT", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        shiftCurrentRectX(1, KEY.SHRINK);
      }
    });
    pn.getActionMap().put("ctrl UP", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        shiftCurrentRectY(1, KEY.SHRINK);
      }
    });
    pn.getActionMap().put("ctrl DOWN", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        shiftCurrentRectY(-1, KEY.SHRINK);
      }
    });
    // arrows
    pn.getActionMap().put("LEFT", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        shiftCurrentRectX(-1, KEY.SHIFT);
      }
    });
    pn.getActionMap().put("RIGHT", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        shiftCurrentRectX(1, KEY.SHIFT);
      }
    });
    pn.getActionMap().put("UP", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        shiftCurrentRectY(1, KEY.SHIFT);
      }
    });
    pn.getActionMap().put("DOWN", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        shiftCurrentRectY(-1, KEY.SHIFT);
      }
    });

    // arrows
    pn.getActionMap().put("ctrl shift LEFT", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        shiftCurrentRectX(-5, KEY.SHIFT);
      }
    });
    pn.getActionMap().put("ctrl shift RIGHT", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        shiftCurrentRectX(5, KEY.SHIFT);
      }
    });
    pn.getActionMap().put("ctrl shift UP", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        shiftCurrentRectY(5, KEY.SHIFT);
      }
    });
    pn.getActionMap().put("ctrl shift DOWN", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        shiftCurrentRectY(-5, KEY.SHIFT);
      }
    });
  }

  @Override
  public void mouseEntered(MouseEvent e) {}

  @Override
  public void mouseExited(MouseEvent e) {}

  public JToggleButton getBtnChoose() {
    return btnChoose;
  }

  public JPanel getPnChartView() {
    return pnChartView;
  }

  public JTable getTable() {
    return table;
  }

  public SHAPE getCurrentShape() {
    return (SHAPE) getComboShape().getSelectedItem();
  }

  private SelectionMode getCurrentSelectionMode() {
    return (SelectionMode) comboSelectionMode.getSelectedItem();
  }

  private Color getCurrentColor() {
    return getCbtnColor().getColor();
  }

  private SettingsBasicStroke getCurrentStroke() {
    return strokeChooserPanel.getStroke();
  }

  private ROI getCurrentRoiMode() {
    return (ROI) comboRoi.getSelectedItem();
  }

  public JComboBox getComboShape() {
    return comboShape;
  }

  public JComboBox getComboSelectionMode() {
    return comboSelectionMode;
  }

  public JCheckBox getCbPerformance() {
    return cbPerformance;
  }

  public JCheckBox getCbMarkDp() {
    return cbMarkDp;
  }

  public JComboBox getComboRoi() {
    return comboRoi;
  }

  public JButton getBtnRegression() {
    return btnRegression;
  }

  public JColorPickerButton getCbtnColor() {
    return cbtnColor;
  }

  public JStrokeChooserPanel getStrokeChooserPanel() {
    return strokeChooserPanel;
  }

  public JCheckBox getCbShowAnnotations() {
    return cbShowAnnotations;
  }

  public JPanel getPnSplitTop() {
    return pnSplitTop;
  }

  public JSplitPane getSplitCenter() {
    return splitCenter;
  }
}
