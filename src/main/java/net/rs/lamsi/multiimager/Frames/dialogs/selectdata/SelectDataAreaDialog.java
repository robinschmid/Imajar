package net.rs.lamsi.multiimager.Frames.dialogs.selectdata;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Image;
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
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
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
import net.rs.lamsi.general.framework.modules.Module;
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
import weka.gui.WrapLayout;


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
  // copy paste
  // list of selections
  private List<SettingsShapeSelection> listSelected = new ArrayList<SettingsShapeSelection>();
  private List<SettingsShapeSelection> listCopied = new ArrayList<SettingsShapeSelection>();

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
  private JCheckBox cbAlphaMapAsExclusion;
  // histo panel
  private HistogramPanel histoPanel;
  private Position histoPos = Position.HIDE;
  private JPanel pnSplitTop;
  private JSplitPane splitCenter;
  private JButton btnShowData;
  private JPanel panel_2;
  private JLabel lblTransform;
  private JCheckBox cbAll;
  private JButton btnRotateCcw;
  private JButton btnRotateCw;
  private JButton btnHReflect;
  private JButton btnVReflect;
  private JTextField txtAngle;
  private JButton btnRotateBy;
  private JTextField txtShiftX;
  private JButton btnLeft;
  private JButton btnRight;
  private JPanel panel_3;
  private JPanel panel_4;
  private JPanel panel_5;
  private JPanel panel_6;
  private JButton btnDown;
  private JTextField txtShiftY;
  private JButton btnUp;
  private JPanel panel_7;
  private JLabel lblXywh;
  private JTextField txtX;
  private JTextField txtY;
  private JTextField txtW;
  private JTextField txtH;
  private JButton btnApplyCoordinates;


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
    setBounds(100, 100, 945, 791);
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

    JPanel pnNorthMenu = new JPanel(new WrapLayout());
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
        listSelected.clear();
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

    JPanel panel_1 = new JPanel(new WrapLayout());
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
    btnFinish.addActionListener(e -> finishShape(getCurrentSelect(), false));

    btnChoose = new JToggleButton("Choose/Zoom");
    panel_1.add(btnChoose);

    JButton btnDelete = new JButton("Delete");
    panel_1.add(btnDelete);

    panel_2 = new JPanel(new WrapLayout());
    WrapLayout flowLayout_5 = (WrapLayout) panel_2.getLayout();
    flowLayout_5.setHgap(10);
    pnNorthMenuContainer.add(panel_2, BorderLayout.SOUTH);

    lblTransform = new JLabel("Transform");
    panel_2.add(lblTransform);

    cbAll = new JCheckBox("all");
    panel_2.add(cbAll);

    panel_5 = new JPanel();
    FlowLayout flowLayout_3 = (FlowLayout) panel_5.getLayout();
    flowLayout_3.setHgap(0);
    flowLayout_3.setVgap(0);
    panel_2.add(panel_5);


    JButton btnCopy = new JButton("");
    panel_5.add(btnCopy);
    btnCopy.setToolTipText("Copy");
    btnCopy.addActionListener(e -> copy());
    btnCopy.setPreferredSize(new Dimension(31, 31));
    btnCopy
        .setIcon(new ImageIcon(new ImageIcon(Module.class.getResource("/img/btn_action_copy.png"))
            .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));

    JButton btnPaste = new JButton("");
    panel_5.add(btnPaste);
    btnPaste.setToolTipText("Copy");
    btnPaste.addActionListener(e -> paste());
    btnPaste.setPreferredSize(new Dimension(31, 31));
    btnPaste
        .setIcon(new ImageIcon(new ImageIcon(Module.class.getResource("/img/btn_action_paste.png"))
            .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));

    btnRotateCcw = new JButton("");
    panel_5.add(btnRotateCcw);
    btnRotateCcw.setToolTipText("Rotate");
    btnRotateCcw.addActionListener(e -> rotateCCW());
    btnRotateCcw.setPreferredSize(new Dimension(31, 31));
    btnRotateCcw.setIcon(
        new ImageIcon(new ImageIcon(Module.class.getResource("/img/btn_action_rotate_ccw.png"))
            .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));

    btnRotateCw = new JButton("");
    panel_5.add(btnRotateCw);
    btnRotateCw.setToolTipText("Rotate");
    btnRotateCw.addActionListener(e -> rotateCW());
    btnRotateCw.setPreferredSize(new Dimension(31, 31));
    btnRotateCw.setIcon(
        new ImageIcon(new ImageIcon(Module.class.getResource("/img/btn_action_rotate_cw.png"))
            .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));

    btnHReflect = new JButton("");
    panel_5.add(btnHReflect);
    btnHReflect.setToolTipText("Reflect");
    btnHReflect.addActionListener(e -> reflectHorizontally());
    btnHReflect.setPreferredSize(new Dimension(31, 31));
    btnHReflect.setIcon(new ImageIcon(
        new ImageIcon(Module.class.getResource("/img/btn_imaging_reflect_horizontal.png"))
            .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));

    btnVReflect = new JButton("");
    panel_5.add(btnVReflect);
    btnVReflect.setToolTipText("Reflect");
    btnVReflect.addActionListener(e -> reflectVertically());
    btnVReflect.setPreferredSize(new Dimension(31, 31));
    btnVReflect.setIcon(new ImageIcon(
        new ImageIcon(Module.class.getResource("/img/btn_imaging_reflect_vertical.png")).getImage()
            .getScaledInstance(30, 30, Image.SCALE_SMOOTH)));

    panel_4 = new JPanel();
    FlowLayout flowLayout_2 = (FlowLayout) panel_4.getLayout();
    flowLayout_2.setVgap(0);
    flowLayout_2.setHgap(0);
    panel_2.add(panel_4);

    txtAngle = new JTextField();
    panel_4.add(txtAngle);
    txtAngle.setHorizontalAlignment(SwingConstants.RIGHT);
    txtAngle.setText("15");
    txtAngle.setColumns(3);

    btnRotateBy = new JButton("");
    panel_4.add(btnRotateBy);
    btnRotateBy.addActionListener(e -> {
      try {
        double angle = -Module.doubleFromTxt(txtAngle);
        rotate(angle);
      } catch (Exception ex) {
      }
    });
    btnRotateBy.setToolTipText("Rotate");
    btnRotateBy.setPreferredSize(new Dimension(31, 31));
    btnRotateBy.setIcon(
        new ImageIcon(new ImageIcon(Module.class.getResource("/img/btn_action_rotate_cw.png"))
            .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));

    panel_3 = new JPanel();
    FlowLayout flowLayout_1 = (FlowLayout) panel_3.getLayout();
    flowLayout_1.setVgap(0);
    flowLayout_1.setHgap(0);
    panel_2.add(panel_3);

    btnLeft = new JButton("");
    panel_3.add(btnLeft);
    btnLeft.setToolTipText("Rotate");
    btnLeft.setPreferredSize(new Dimension(31, 31));
    btnLeft.setIcon(
        new ImageIcon(new ImageIcon(Module.class.getResource("/img/btn_action_arrow_left.png"))
            .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));

    txtShiftX = new JTextField();
    txtShiftX.setToolTipText("Translate x");
    panel_3.add(txtShiftX);
    txtShiftX.setText("15");
    txtShiftX.setHorizontalAlignment(SwingConstants.CENTER);
    txtShiftX.setColumns(4);

    btnRight = new JButton("");
    panel_3.add(btnRight);
    btnRight.setToolTipText("Rotate");
    btnRight.setPreferredSize(new Dimension(31, 31));
    btnRight.setIcon(
        new ImageIcon(new ImageIcon(Module.class.getResource("/img/btn_action_arrow_right.png"))
            .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));

    panel_6 = new JPanel();
    FlowLayout flowLayout_4 = (FlowLayout) panel_6.getLayout();
    flowLayout_4.setVgap(0);
    flowLayout_4.setHgap(0);
    panel_2.add(panel_6);

    btnDown = new JButton("");
    btnDown.setToolTipText("Rotate");
    btnDown.setPreferredSize(new Dimension(31, 31));
    panel_6.add(btnDown);
    btnDown.setIcon(
        new ImageIcon(new ImageIcon(Module.class.getResource("/img/btn_action_arrow_down.png"))
            .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));

    txtShiftY = new JTextField();
    txtShiftY.setToolTipText("Translate y");
    txtShiftY.setText("15");
    txtShiftY.setHorizontalAlignment(SwingConstants.CENTER);
    txtShiftY.setColumns(4);
    panel_6.add(txtShiftY);

    btnUp = new JButton("");
    btnUp.setToolTipText("Rotate");
    btnUp.setPreferredSize(new Dimension(31, 31));
    btnUp.setIcon(
        new ImageIcon(new ImageIcon(Module.class.getResource("/img/btn_action_arrow_up.png"))
            .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
    panel_6.add(btnUp);

    panel_7 = new JPanel();
    FlowLayout flowLayout_6 = (FlowLayout) panel_7.getLayout();
    flowLayout_6.setVgap(0);
    flowLayout_6.setHgap(0);
    panel_2.add(panel_7);

    lblXywh = new JLabel("x:y:w:h");
    panel_7.add(lblXywh);

    txtX = new JTextField();
    txtX.setToolTipText("X coordinate");
    txtX.setText("0");
    txtX.setHorizontalAlignment(SwingConstants.CENTER);
    txtX.setColumns(4);
    panel_7.add(txtX);
    txtX.addActionListener(e -> {
      try {
        float x = Module.floatFromTxt(txtX);
        applyXCoordinate(x);
        requestFocusOnChart();
      } catch (Exception ex) {
      }
    });

    txtY = new JTextField();
    txtY.setToolTipText("Y coordinate");
    txtY.setText("0");
    txtY.setHorizontalAlignment(SwingConstants.CENTER);
    txtY.setColumns(4);
    panel_7.add(txtY);
    txtY.addActionListener(e -> {
      try {
        float y = Module.floatFromTxt(txtY);
        applyYCoordinate(y);
        requestFocusOnChart();
      } catch (Exception ex) {
      }
    });

    btnApplyCoordinates = new JButton("");
    panel_7.add(btnApplyCoordinates);
    btnApplyCoordinates.setToolTipText("Apply coordinates");
    btnApplyCoordinates.setPreferredSize(new Dimension(31, 31));
    btnApplyCoordinates
        .setIcon(new ImageIcon(new ImageIcon(Module.class.getResource("/img/btn_action_check.png"))
            .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));

    btnApplyCoordinates.addActionListener(e -> {
      try {
        float x = Module.floatFromTxt(txtX);
        float y = Module.floatFromTxt(txtY);
        applyCoordinates(x, y);
      } catch (Exception ex) {
      }
    });

    txtW = new JTextField();
    txtW.setToolTipText("Width");
    txtW.setText("0");
    txtW.setHorizontalAlignment(SwingConstants.CENTER);
    txtW.setColumns(4);
    panel_7.add(txtW);
    txtW.addActionListener(e -> {
      try {
        float w = Module.floatFromTxt(txtW);
        applyWidth(w);
        requestFocusOnChart();
      } catch (Exception ex) {
      }
    });

    txtH = new JTextField();
    txtH.setToolTipText("Height");
    txtH.setText("0");
    txtH.setHorizontalAlignment(SwingConstants.CENTER);
    txtH.setColumns(4);
    panel_7.add(txtH);
    txtH.addActionListener(e -> {
      try {
        float h = Module.floatFromTxt(txtH);
        applyHeight(h);
        requestFocusOnChart();
      } catch (Exception ex) {
      }
    });

    JButton btnApplySize = new JButton("");
    panel_7.add(btnApplySize);
    btnApplySize.setToolTipText("Apply size");
    btnApplySize.setPreferredSize(new Dimension(31, 31));
    btnApplySize
        .setIcon(new ImageIcon(new ImageIcon(Module.class.getResource("/img/btn_action_check.png"))
            .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));

    btnApplySize.addActionListener(e -> {
      try {
        float w = Module.floatFromTxt(txtW);
        float h = Module.floatFromTxt(txtH);
        applySize(w, h);
      } catch (Exception ex) {
      }
    });


    btnLeft.addActionListener(e -> {
      try {
        float d = Module.floatFromTxt(txtShiftX);
        translate(-d, 0);
      } catch (Exception ex) {
      }
    });
    btnRight.addActionListener(e -> {
      try {
        float d = Module.floatFromTxt(txtShiftX);
        translate(d, 0);
      } catch (Exception ex) {
      }
    });

    btnUp.addActionListener(e -> {
      try {
        float d = Module.floatFromTxt(txtShiftY);
        translate(0, d);
      } catch (Exception ex) {
      }
    });
    btnDown.addActionListener(e -> {
      try {
        float d = Module.floatFromTxt(txtShiftY);
        translate(0, -d);
      } catch (Exception ex) {
      }
    });


    btnDelete.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        listSelected.stream().forEach(s -> {
          deleteSelection(s);
        });
        listSelected.clear();
        repaintChart();
      }
    });
    btnChoose.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        heat.getChartPanel().setMouseZoomable(((JToggleButton) e.getSource()).isSelected());
      }
    });
    comboSelectionMode.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        requestFocusOnChart();
        // change color and stroke
        Color c = SettingsShapeSelection.getColorForSelectionMode(getCurrentSelectionMode());
        getCbtnColor().setColor(c);
      }
    });
    comboRoi.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        requestFocusOnChart();
        // change color and stroke
        SettingsBasicStroke stroke = SettingsShapeSelection.getStrokeForROI(getCurrentRoiMode());
        getStrokeChooserPanel().setStroke(stroke);
      }
    });
    comboShape.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        requestFocusOnChart();
      }
    });
    //
    addKeys();
    //
    WindowAdapter wl = new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent e) {
        super.windowClosed(e);
      }
    };
    this.addWindowListener(wl);

    requestFocusOnChart();
  }


  private void paste() {
    try {
      deselectAll();
      // paste
      if (!listCopied.isEmpty())
        for (SettingsShapeSelection s : listCopied)
          addNewSelection((SettingsShapeSelection) s.copy(), true);
    } catch (Exception ex) {
      logger.error("Cannot copy ROI settings", ex);
    }
  }


  private void copy() {
    try {
      listCopied.clear();
      if (!listSelected.isEmpty())
        for (SettingsShapeSelection s : listSelected)
          listCopied.add((SettingsShapeSelection) s.copy());
    } catch (Exception ex) {
      logger.error("Cannot copy ROI settings", ex);
    }
  }


  /**
   * 
   * @return Current selection (first in list) or null
   */
  private SettingsShapeSelection getCurrentSelect() {
    if (listSelected == null || listSelected.isEmpty())
      return null;
    return listSelected.get(0);
  }


  private void requestFocusOnChart() {
    getContentPane().requestFocusInWindow();
  }


  /**
   * Show ordered data in a dialog
   */
  private void showDataDialog() {
    if (getCurrentSelect() != null) {
      SelectionTableRow row =
          getCurrentSelect().getDefaultTableRow(cbAlphaMapAsExclusion.isSelected());
      List<Double> d = row.getData();
      if (d != null) {
        DataDialog dialog = new DataDialog("Data of ROI " + getCurrentSelect().getOrderNumber(), d);
        dialog.setVisible(true);
      }
    }
  }

  /**
   * Set coordinates of all or one ROI Float.NaN
   * 
   * @param x Float.NaN if this value should not be changed
   * @param y Float.NaN if this value should not be changed
   */
  private void applyCoordinates(float x, float y) {
    if (cbAll.isSelected()) {
      if (settSel != null) {
        settSel.getSelections().stream().forEach(s -> s.setPosition(x, y));
        updateAndRepaintAll();
      }
    } else {
      // only selected
      listSelected.stream().forEach(s -> {
        s.setPosition(x, y);
        updateSelection(s, true);
      });
    }
  }

  /**
   * Set coordinates of all or one ROI
   * 
   * @param x Float.NaN if this value should not be changed
   */
  private void applyXCoordinate(float x) {
    applyCoordinates(x, Float.NaN);
  }

  /**
   * Set coordinates of all or one ROI
   * 
   * @param y Float.NaN if this value should not be changed
   */
  private void applyYCoordinate(float y) {
    applyCoordinates(Float.NaN, y);
  }


  /**
   * Set size of all or one ROI
   * 
   * @param w Float.NaN if this value should not be changed
   * @param h Float.NaN if this value should not be changed
   */
  private void applySize(float w, float h) {
    if (cbAll.isSelected()) {
      if (settSel != null) {
        for (SettingsShapeSelection sel : settSel.getSelections()) {
          sel.setSize(w, h);
        }
        updateAndRepaintAll();
      }
    } else {
      // only selected
      listSelected.stream().forEach(s -> {
        s.setSize(w, h);
        updateSelection(s, true);
      });
    }
  }

  /**
   * Set size of all or one ROI
   * 
   * @param w
   */
  private void applyWidth(float w) {
    applySize(w, Float.NaN);
  }

  /**
   * Set size of all or one ROI
   * 
   * @param h
   */
  private void applyHeight(float h) {
    applySize(Float.NaN, h);
  }



  private void translate(float dx, float dy) {
    if (cbAll.isSelected()) {
      if (settSel != null) {
        for (SettingsShapeSelection sel : settSel.getSelections()) {
          sel.translate(dx, dy);
        }
        updateAndRepaintAll();
      }
    } else {
      // only selected
      listSelected.stream().forEach(s -> {
        s.translate(dx, dy);
        updateSelection(s, true);
      });
    }
  }


  private void reflectHorizontally() {
    if (cbAll.isSelected()) {
      if (settSel != null) {
        for (SettingsShapeSelection sel : settSel.getSelections()) {
          sel.reflectH();
        }
        updateAndRepaintAll();
      }
    } else {
      // only selected
      listSelected.stream().forEach(s -> {
        s.reflectH();
        updateSelection(s, true);
      });
    }
  }


  private void reflectVertically() {
    if (cbAll.isSelected()) {
      if (settSel != null) {
        for (SettingsShapeSelection sel : settSel.getSelections()) {
          sel.reflectV();
        }
        updateAndRepaintAll();
      }
    } else {
      // only selected
      listSelected.stream().forEach(s -> {
        s.reflectV();
        updateSelection(s, true);
      });
    }
  }


  private void rotateCCW() {
    rotate(45);
  }

  private void rotateCW() {
    rotate(-45);
  }

  private void rotate(double degAngle) {
    if (cbAll.isSelected()) {
      if (settSel != null) {
        for (SettingsShapeSelection sel : settSel.getSelections()) {
          sel.rotate(degAngle);
        }
        updateAndRepaintAll();
      }
    } else
      // only selected
      listSelected.stream().forEach(s -> {
        s.rotate(degAngle);
        updateSelection(s, true);
      });
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
    SettingsShapeSelection currentSelect = getCurrentSelect();
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


  /**
   * delete shape if too small. update stats. Set quantifier value
   * 
   * @param currentSelect2
   */
  private void finishShape(SettingsShapeSelection s, boolean deselect) {
    if (s == null)
      return;
    // is viable?
    if (s.getWidth() != 0 && s.getHeight() != 0) {
      // update selection stats and annotation
      updateSelection(true);

      // concentration insert dialog for QUANTIFIER
      if (s.getRoi().equals(ROI.QUANTIFIER)) {
        // open dialog
        try {
          lastConcentration = Double.valueOf(
              JOptionPane.showInputDialog("concentration", String.valueOf(lastConcentration)));
          logger.info("Concentration input for shape selection was {}", lastConcentration);
          s.setConcentration(lastConcentration);
        } catch (Exception ex) {
          logger.warn(
              "Invalid concentration input for shape selection! Input any floating point number or integer.");
        }
      }
      s.setFinished(true);
      if (deselect)
        setCurrentSelect(null, false);
    } else {
      logger.info("Shape width and height were 0. Therefore, the shape was discarded.");
      // delete roi
      deleteSelection(s);
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
    // all selected
    listSelected.stream().forEach(s -> {
      // translate to data space
      ChartPanel cp = heat.getChartPanel();
      float val = (float) ChartLogics.screenValueToPlotValue(cp, i).getX();
      // shift
      if (!((key.equals(KEY.ENLARGE) && i > 0) || (key.equals(KEY.SHRINK) && i < 0))) {
        try {
          val = i * Module.floatFromTxt(txtShiftX);
        } catch (Exception e) {
        }
        s.translate(val, 0);
      }
      // enlarge?
      if (key.equals(KEY.ENLARGE))
        s.grow(Math.abs(val), 0);
      if (key.equals(KEY.SHRINK))
        s.grow(-Math.abs(val), 0);

      updateSelection(s, true);
    });
  }

  protected void shiftCurrentRectY(int i, KEY key) {
    // all selected
    listSelected.stream().forEach(s -> {
      // translate to data space
      ChartPanel cp = heat.getChartPanel();
      float val = (float) ChartLogics.screenValueToPlotValue(cp, i).getY();
      // shift
      if (!((key.equals(KEY.ENLARGE) && i > 0) || (key.equals(KEY.SHRINK) && i < 0))) {
        try {
          val = i * Module.floatFromTxt(txtShiftY);
        } catch (Exception e) {
        }
        s.translate(0, val);
      }
      // enlarge?
      if (key.equals(KEY.ENLARGE))
        s.grow(0, Math.abs(val));
      if (key.equals(KEY.SHRINK))
        s.grow(0, -Math.abs(val));

      updateSelection(s, true);
    });
  }

  /**
   * Delete roi selection, annotation
   * 
   * @param r
   */
  protected void deleteSelection(SettingsShapeSelection r) {
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
              int[] i = table.getSelectedRows();
              if (i.length > 0) {
                SettingsShapeSelection cs = getSelections().get(i[0]);
                if (i.length == 1)
                  if (!cs.equals(getCurrentSelect()))
                    setCurrentSelect(cs, false);
                  else if (i.length > 1) {
                    // select first
                    if (!cs.equals(getCurrentSelect()))
                      setCurrentSelect(cs, false);
                    // clear and add all selected rows
                    listSelected.clear();
                    for (int j = 0; j < i.length; j++) {
                      listSelected.add(getSelections().get(i[j]));
                    }
                  }
              }
            }
          }
        };
        table.getSelectionModel().addListSelectionListener(sellist);


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


  /**
   * Update stats, annotations and repaint chart
   */
  private void updateAndRepaintAll() {
    updateAllStats();
    updateAllAnnotations();
    repaintChart();
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
        updateSelection(r, true);
      });
    } else {
      // set shape
      currentAnn.setShape(r.getShape());
      currentAnn.setStroke(r.createStroke());
      currentAnn.setOutlinePaint(r.getColor());
    }
  }

  /**
   * adds a selection to the list and an annotation to the plot
   * 
   * @param r
   */
  private void addNewSelection(SettingsShapeSelection r, boolean multiSelect) {
    r.setColor(getCurrentColor());
    r.setStroke(getCurrentStroke());
    setCurrentSelect(r, multiSelect);

    // put data in table
    tableModel.addRow(r, false);
    // update statistics
    updateSelection(false);
    // update table
    tableModel.fireTableDataChanged();
  }

  /**
   * adds a selection to the list and an annotation to the plot
   * 
   * @param r
   */
  private void addNewSelection(SettingsShapeSelection r) {
    addNewSelection(r, false);
  }


  /**
   * update statistics, add annotation and show all in chart call on size/position/data processing
   * changecbPer
   * 
   */
  protected void updateSelection(boolean updateStats) {
    updateSelection(getCurrentSelect(), updateStats);
  }

  /**
   * update statistics, add annotation and show all in chart call on size/position/data processing
   * change
   */
  protected void updateSelection(SettingsShapeSelection s, boolean updateStats) {
    if (s == null)
      return;
    if (img != null)
      img.getImageGroup().getSettAlphaMap().setActive(cbAlphaMapAsExclusion.isSelected());
    // Update rects
    if (updateStats) {
      if (s.getMode() == SelectionMode.EXCLUDE) {
        // update all rects
        settSel.updateStatistics();
      } else {
        // update this selection
        settSel.updateStatistics(s);
        tableModel.updateRow(s);
      }
      // update table
      tableModel.fireTableDataChanged();
      // update histo
      if (s.equals(getCurrentSelect()))
        updateHistoPanelData();
    }
    // update annotation of current only
    updateAnnotation(s);

    repaintChart();
    //
    logger.debug("UPDATE CHART");
  }

  private void repaintChart() {
    // update map
    showMarkingMap(getCbMarkDp().isSelected());
    // update chart
    JFreeChart chart = heat.getChart();
    chart.fireChartChanged();
    this.repaint();
  }


  @Override
  public void mouseDragged(MouseEvent e) {
    SettingsShapeSelection currentSelect = getCurrentSelect();
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
            updateSelection(!cbPerformance.isSelected());
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
          updateSelection(!cbPerformance.isSelected());
        }
      }
    }
  }

  @Override
  public void mouseMoved(MouseEvent e) {}

  @Override
  public void mouseClicked(MouseEvent e) {
    SettingsShapeSelection currentSelect = getCurrentSelect();
    if (e.getButton() == MouseEvent.BUTTON1) {
      if (getBtnChoose().isSelected()) {
        // select or zoom
        ChartPanel cp = heat.getChartPanel();
        Point2D pos = ChartLogics.mouseXYToPlotXY(cp, e.getX(), e.getY());

        boolean found = false;
        // choose current rect
        for (int i = 0; getSelections() != null && i < getSelections().size(); i++) {
          SettingsShapeSelection s = getSelections().get(i);
          if ((currentSelect == null || !currentSelect.equals(s))
              && s.contains(pos.getX(), pos.getY())) {
            // set selected / add as multiselect if shift down
            setCurrentSelect(s, e.isShiftDown());
            lastMouseEvent = e;
            found = true;
            return;
          }
        }
        if (!found)
          setCurrentSelect(null, false);
      } else if (getCurrentShape().equals(SHAPE.POLYGON)) {
        // Add point? or create new
        ChartPanel cp = heat.getChartPanel();
        Point2D pos = ChartLogics.mouseXYToPlotXY(cp, e.getX(), e.getY());
        if (currentSelect == null || !SettingsPolygonSelection.class.isInstance(currentSelect)
            || currentSelect.isFinished()) {
          // create new
          addNewSelection(new SettingsPolygonSelection(img, getCurrentRoiMode(),
              getCurrentSelectionMode(), x0, y0));
          lastMouseEvent = e;
        } else {
          // only add point if not already finished
          if (!currentSelect.isFinished()) {
            // add points
            ((SettingsPolygonSelection) currentSelect).addPoint((float) pos.getX(),
                (float) pos.getY());
            lastMouseEvent = e;
          }
        }

        // update selection stats and annotation
        updateSelection(!cbPerformance.isSelected());
      }
    }
  }


  /**
   * First selection is always the newest
   * 
   * @param s
   * @param multiSelect
   */
  private void setCurrentSelect(SettingsShapeSelection s, boolean multiSelect) {
    SettingsShapeSelection currentSelect = getCurrentSelect();
    // finish last selection
    if (currentSelect != null && !currentSelect.isFinished())
      finishShape(currentSelect, false);

    // deselect all
    // deselect old
    if (s == null || !multiSelect) {
      // highlighting
      for (SettingsShapeSelection cs : listSelected) {
        cs.setHighlighted(false);
        updateAnnotation(cs);
      }

      listSelected.clear();

      txtX.setText("");
      txtY.setText("");
      txtW.setText("");
      txtH.setText("");
    }
    if (s != null) {
      // add to list selection
      listSelected.add(0, s);
      s.setHighlighted(true);
      updateAnnotation(s);
      updateHistoPanelData();

      txtX.setText(String.valueOf(s.getX0()));
      txtY.setText(String.valueOf(s.getY0()));
      txtW.setText(String.valueOf(s.getWidth()));
      txtH.setText(String.valueOf(s.getHeight()));
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
    SettingsShapeSelection currentSelect = getCurrentSelect();
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
        // finalise
        finishShape(currentSelect, false);
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


    // copy paste
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK), "ctrl C");
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK), "ctrl V");


    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.CTRL_MASK + InputEvent.SHIFT_MASK),
        "ctrl shift LEFT");
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.CTRL_MASK + InputEvent.SHIFT_MASK),
        "ctrl shift RIGHT");
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_MASK + InputEvent.SHIFT_MASK),
        "ctrl shift UP");
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_MASK + InputEvent.SHIFT_MASK),
        "ctrl shift DOWN");

    // remove focus
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "ENTER");
    pn.getActionMap().put("ENTER", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        requestFocusOnChart();
      }
    });

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


    pn.getActionMap().put("ctrl C", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        copy();
      }
    });
    pn.getActionMap().put("ctrl V", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        paste();
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

  protected void deselectAll() {
    listSelected.forEach(s -> {
      s.setHighlighted(false);
      updateAnnotation(s);
    });
    listSelected.clear();
  }


  @Override
  public void mouseEntered(MouseEvent e) {}

  @Override
  public void mouseExited(MouseEvent e) {}


  private ArrayList<SettingsShapeSelection> getSelections() {
    return settSel.getSelections();
  }

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

  public JPanel getPnSplitTop() {
    return pnSplitTop;
  }

  public JSplitPane getSplitCenter() {
    return splitCenter;
  }

  public JCheckBox getCbAll() {
    return cbAll;
  }

  public JTextField getTxtAngle() {
    return txtAngle;
  }

  public JTextField getTxtShiftY() {
    return txtShiftY;
  }

  public JTextField getTxtShiftX() {
    return txtShiftX;
  }

  public JTextField getTxtX() {
    return txtX;
  }

  public JTextField getTxtY() {
    return txtY;
  }

  public JTextField getTxtW() {
    return txtW;
  }

  public JTextField getTxtH() {
    return txtH;
  }
}
