package net.rs.lamsi.massimager.Frames.Panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import org.jfree.chart.ChartPanel;
import org.jfree.data.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.framework.modules.Module;
import net.rs.lamsi.general.framework.modules.ModuleListWithOptions;
import net.rs.lamsi.general.heatmap.Heatmap;
import net.rs.lamsi.general.myfreechart.ChartLogics;
import net.rs.lamsi.general.settings.SettingsHolder;
import net.rs.lamsi.general.settings.image.sub.SettingsGeneralImage;
import net.rs.lamsi.general.settings.image.sub.SettingsGeneralImage.XUNIT;
import net.rs.lamsi.general.settings.image.sub.SettingsImageContinousSplit;
import net.rs.lamsi.general.settings.image.sub.SettingsMSImage;
import net.rs.lamsi.massimager.Frames.LogicRunner;
import net.rs.lamsi.massimager.Frames.Window;
import net.rs.lamsi.massimager.Frames.Dialogs.SelectMZDirectDialog;
import net.rs.lamsi.massimager.Frames.Menu.MenuChartActions;
import net.rs.lamsi.massimager.Frames.Menu.MenuTableActions;
import net.rs.lamsi.massimager.Frames.Panels.peaktable.PeakTableRow;
import net.rs.lamsi.massimager.Frames.Panels.peaktable.PnTableMZPick;
import net.rs.lamsi.massimager.MyMZ.MZChromatogram;
import net.rs.lamsi.massimager.MyMZ.MZDataFactory;
import net.rs.lamsi.massimager.MyMZ.MZIon;
import net.rs.lamsi.massimager.MyMZ.preprocessing.filtering.peaklist.chargecalculation.MZChargeCalculatorMZMine;
import net.rs.lamsi.massimager.mzmine.MZMineCallBackListener;
import net.rs.lamsi.massimager.mzmine.MZMineLogicsConnector;
import net.rs.lamsi.utils.threads.ProgressUpdateTask;
import net.sf.mzmine.datamodel.Feature;
import net.sf.mzmine.datamodel.ImagingRawData;
import net.sf.mzmine.datamodel.PeakList;
import net.sf.mzmine.datamodel.PeakListRow;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.datamodel.impl.MZmineProjectListenerAdapter;


/*
 * TODO WICHTIG: das fenster das ausgewählt ist in das kann rein gezoomt werden. wenn in andere
 * Views geklickt wird, dann wird rt/mz für das ausgewählte view selektiert! wenn STRG gehalten
 * wird, dann kann in jedes rein gezoomt werden
 */

public class ImageVsSpecViewPanel extends JPanel implements Runnable {
  // MODES
  // TODO image als 3d EIC
  public static final String VK_LEFT = "VK_LEFT", VK_RIGHT = "VK_RIGHT", VK_UP = "VK_UP",
      VK_DOWN = "VK_DOWN", VK_DELETE = "VK_DELETE", VK_F5 = "VK_F5", VK_1 = "VK_1", VK_2 = "VK_2",
      VK_3 = "VK_3";
  public static final String VK_LEFT_RELEASED = "VK_LEFT_RELEASED",
      VK_RIGHT_RELEASED = "VK_RIGHT_RELEASED", VK_UP_RELEASED = "VK_UP_RELEASED",
      VK_DOWN_RELEASED = "VK_DOWN_RELEASED", VK_DELETE_RELEASED = "VK_DELETE_RELEASED",
      VK_F5_RELEASED = "VK_F5_RELEASED", VK_1_RELEASED = "VK_1_RELEASED",
      VK_2_RELEASED = "VK_2_RELEASED", VK_3_RELEASED = "VK_3_RELEASED";
  public static int KEY_LEFT = KeyEvent.VK_A, KEY_RIGHT = KeyEvent.VK_D, KEY_UP = KeyEvent.VK_W,
      KEY_DOWN = KeyEvent.VK_S;
  public static final int VIEW_BOTTOM_SPECTRUM = 0, VIEW_MIDDLE_IMAGECHROM = 1, VIEW_TOP_CHROM = 2;
  public static final int MODE_TIC = 0, MODE_EIC = 1, MODE_IMAGE_CON = 2, MODE_IMAGE_DISCON = 3,
      MODE_IMAGE = 5, MODE_PEAK_LIST = 4;
  public static final int SPECTRUM_SELECTION_MODE_RT = 0, SPECTRUM_SELECTION_MODE_XY = 1;

  private final Logger logger = LoggerFactory.getLogger(getClass());
  // MySTUFF
  //
  protected Window window = null;
  // Dialog
  protected SelectMZDirectDialog dialogSelectMZDirect;
  //
  protected PeakList selectedPeakList;

  // Spectrum VS MZCHrom als normales MSView:
  protected int selectedView = VIEW_BOTTOM_SPECTRUM;
  private double standardVsPM = 1;

  // Spektrum im unteren screen
  private MZChromatogram selectedSpectrum;
  private int specSelectionMode = SPECTRUM_SELECTION_MODE_RT;
  private double[] selectedVsRetentionTime = new double[2];
  // startx, starty, w, h (real rect)
  private Rectangle2D selectedImageRectForSpec;

  // TIC und EIC im Top screen
  private int selectedModeTop = MODE_TIC;
  private double selectedVsTopMZ = -1, selectedVsTopPM = 1;
  // TOP Table Panel for MZ Peak pick
  private PnTableMZPick tableMzPeak;

  // TIC, EIC, Image im mittleren screen
  protected SettingsMSImage settImage;
  protected SettingsImageContinousSplit settSplitCon;
  private int selectedModeMiddle = -1;
  private double selectedVsMiddleMZ = -1, selectedVsMiddlePM = 1;
  //
  private ChartPanel chartBottomSpec = null, chartTopChrom = null, chartMiddleChrom = null,
      chartMiddleImage = null;

  private Heatmap currentHeat;

  private boolean isImagingRawData = false;

  // task for updates
  private ProgressUpdateTask<ChartPanel> taskTop, taskMiddle, taskBottom;
  private ProgressUpdateTask<MZChromatogram> taskSpectrumGenerator;



  // AUTO GENERATION
  private PnChartWithSettings pnMZImageChrom;
  private PnChartWithSettings pnSpec;
  private final ButtonGroup buttonGroup = new ButtonGroup();
  private JLabel lbRT;
  private JLabel lbPMMiddle;
  private JLabel lbMZMiddle;

  private JLabel lbX;
  private JLabel lbY;
  private Module pnWestImageSettings;
  private JPanel topmidle;
  private JSplitPane splitMZvsSpec;
  private JSplitPane splitCenterThree;
  private PnChartWithSettings pnTopEICTIC;
  private JLabel lbPMTop;
  private JLabel lbMZTop;
  private JPanel menuTop;
  private JToggleButton btnTopTic;
  private JToggleButton btnTopEic;
  private JCheckBox cbHideTopTIC;
  private JPanel pnSettImageCon;
  private JLabel lbDisconCon;
  private JLabel lbVelocity;
  private JLabel lbSpotsize;
  private JPanel pnImgCon;
  private JTextField txtVelocity;
  private JTextField txtSpotsize;
  private JLabel lblTp;
  private JTextField txtTimePerLine;
  private final ButtonGroup buttonGroup_1 = new ButtonGroup();
  private JButton btnSendImage;
  private JPanel pnTopEICTIC1;
  private JLabel lbRT2;
  private JLabel lbX2;
  private JLabel lbY2;


  /**
   * Create the panel.
   */
  public ImageVsSpecViewPanel(Window wnd) {
    setLayout(new BorderLayout(0, 0));
    this.window = wnd;

    initVars();
    // ChangeListener for image settings
    DocumentListener autoDocumentL = new DocumentListener() {
      @Override
      public void removeUpdate(DocumentEvent arg0) {
        startImageUpdater();
      }

      @Override
      public void insertUpdate(DocumentEvent arg0) {
        startImageUpdater();
      }

      @Override
      public void changedUpdate(DocumentEvent arg0) {
        startImageUpdater();
      }
    };

    //

    tableMzPeak = new PnTableMZPick() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        // TODO Auto-generated method stub
        // Table selection has changed
        if (!e.getValueIsAdjusting()) {
          int row = tableMzPeak.getTable().getSelectedRow();
          if (row != -1) {
            // image of selected peak
            PeakTableRow prow = tableMzPeak.getTableModel().getPeakRowList().get(row);
            com.google.common.collect.Range<Double> mz = prow.getMzRange();
            //
            selectedVsMiddleMZ = prow.getMz();
            selectedVsMiddlePM = (mz.upperEndpoint() - mz.lowerEndpoint()) / 2.0;
            renewMiddleImageChrom(selectedVsMiddleMZ, selectedVsMiddlePM);
          }
        }
      }
    };

    splitCenterThree = new JSplitPane();
    splitCenterThree.setOrientation(JSplitPane.VERTICAL_SPLIT);
    add(splitCenterThree, BorderLayout.CENTER);

    splitMZvsSpec = new JSplitPane();
    splitCenterThree.setRightComponent(splitMZvsSpec);
    splitMZvsSpec.setOrientation(JSplitPane.VERTICAL_SPLIT);

    pnMZImageChrom = new PnChartWithSettings();
    pnMZImageChrom.getRbSelectedChartView().addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        JRadioButton rb = (JRadioButton) e.getSource();
        if (rb.isSelected()) {
          setSelectedView(VIEW_MIDDLE_IMAGECHROM);
        }
      }
    });
    splitMZvsSpec.setLeftComponent(pnMZImageChrom);
    GridBagLayout gridBagLayout = (GridBagLayout) pnMZImageChrom.getEastSettings().getLayout();
    gridBagLayout.rowWeights = new double[] {0.0, 0.0, 1.0};
    buttonGroup.add(pnMZImageChrom.getRbSelectedChartView());

    lbPMMiddle = new JLabel("pm=");
    GridBagConstraints gbc_lbPMMiddle = new GridBagConstraints();
    gbc_lbPMMiddle.anchor = GridBagConstraints.NORTHWEST;
    gbc_lbPMMiddle.gridx = 0;
    gbc_lbPMMiddle.gridy = 2;
    pnMZImageChrom.getEastSettings().add(lbPMMiddle, gbc_lbPMMiddle);

    lbMZMiddle = new JLabel("mz=");
    GridBagConstraints gbc_lbMZMiddle = new GridBagConstraints();
    gbc_lbMZMiddle.anchor = GridBagConstraints.NORTHWEST;
    gbc_lbMZMiddle.gridx = 0;
    gbc_lbMZMiddle.gridy = 1;
    pnMZImageChrom.getEastSettings().add(lbMZMiddle, gbc_lbMZMiddle);

    topmidle = new JPanel();
    pnMZImageChrom.add(topmidle, BorderLayout.NORTH);
    topmidle.setLayout(new BorderLayout(0, 0));

    toolBar_1 = new JToolBar();
    toolBar_1.setSize(new Dimension(25, 25));
    toolBar_1.setRollover(true);
    toolBar_1.setMinimumSize(new Dimension(13, 25));
    toolBar_1.setMaximumSize(new Dimension(25, 25));
    toolBar_1.setBorder(null);
    topmidle.add(toolBar_1, BorderLayout.WEST);

    btnMiddleTIC = new JToggleButton("");
    btnMiddleTIC.setToolTipText("Total ion current");
    btnMiddleTIC.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setModeMiddle(MODE_TIC);
      }
    });
    btnGroup_middle.add(btnMiddleTIC);
    btnMiddleTIC.setSelectedIcon(
        new ImageIcon(ImageVsSpecViewPanel.class.getResource("/img/btn_tic_selected.png")));
    btnMiddleTIC.setIcon(new ImageIcon(ImageVsSpecViewPanel.class.getResource("/img/btn_tic.png")));
    btnMiddleTIC.setMinimumSize(new Dimension(25, 25));
    btnMiddleTIC.setMaximumSize(new Dimension(25, 25));
    btnMiddleTIC.setMargin(new Insets(0, 0, 0, 0));
    btnMiddleTIC.setBounds(new Rectangle(0, 0, 24, 24));
    toolBar_1.add(btnMiddleTIC);

    btnMiddleEIC = new JToggleButton("");
    btnMiddleEIC.setToolTipText("Selected mz ion trace");
    btnMiddleEIC.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setModeMiddle(MODE_EIC);
      }
    });
    btnMiddleEIC.setSelected(true);
    btnGroup_middle.add(btnMiddleEIC);
    btnMiddleEIC.setIcon(new ImageIcon(ImageVsSpecViewPanel.class.getResource("/img/btn_mz.png")));
    btnMiddleEIC.setSelectedIcon(
        new ImageIcon(ImageVsSpecViewPanel.class.getResource("/img/btn_mz_selected.png")));
    btnMiddleEIC.setMinimumSize(new Dimension(25, 25));
    btnMiddleEIC.setMaximumSize(new Dimension(25, 25));
    btnMiddleEIC.setMargin(new Insets(0, 0, 0, 0));
    toolBar_1.add(btnMiddleEIC);

    btnMiddleImageDisc = new JToggleButton("");
    btnMiddleImageDisc.setToolTipText("Discontinuous imaging (triggert). One file per scan line");
    btnMiddleImageDisc.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setModeMiddle(MODE_IMAGE_DISCON);
      }
    });
    btnGroup_middle.add(btnMiddleImageDisc);
    btnMiddleImageDisc.setSelectedIcon(
        new ImageIcon(ImageVsSpecViewPanel.class.getResource("/img/btn_img_D_selec.png")));
    btnMiddleImageDisc
        .setIcon(new ImageIcon(ImageVsSpecViewPanel.class.getResource("/img/btn_img_D.png")));
    btnMiddleImageDisc.setMinimumSize(new Dimension(25, 25));
    btnMiddleImageDisc.setMaximumSize(new Dimension(25, 25));
    btnMiddleImageDisc.setMargin(new Insets(0, 0, 0, 0));
    toolBar_1.add(btnMiddleImageDisc);

    btnMiddleImageCon = new JToggleButton("");
    btnMiddleImageCon.setToolTipText("Continuous imaging. One file for complete image");
    btnMiddleImageCon.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setModeMiddle(MODE_IMAGE_CON);
      }
    });
    btnGroup_middle.add(btnMiddleImageCon);
    btnMiddleImageCon
        .setIcon(new ImageIcon(ImageVsSpecViewPanel.class.getResource("/img/btn_img_C.png")));
    btnMiddleImageCon.setSelectedIcon(
        new ImageIcon(ImageVsSpecViewPanel.class.getResource("/img/btn_img_C_selec.png")));
    btnMiddleImageCon.setMinimumSize(new Dimension(25, 25));
    btnMiddleImageCon.setMaximumSize(new Dimension(25, 25));
    btnMiddleImageCon.setMargin(new Insets(0, 0, 0, 0));
    toolBar_1.add(btnMiddleImageCon);

    btnMiddleImage = new JToggleButton("");
    btnMiddleImage.setToolTipText("Imaging");
    btnMiddleImage.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setModeMiddle(MODE_IMAGE);
      }
    });
    btnGroup_middle.add(btnMiddleImage);
    btnMiddleImage
        .setIcon(new ImageIcon(ImageVsSpecViewPanel.class.getResource("/img/btn_img_C.png")));
    btnMiddleImage.setSelectedIcon(
        new ImageIcon(ImageVsSpecViewPanel.class.getResource("/img/btn_img_C_selec.png")));
    btnMiddleImage.setMinimumSize(new Dimension(25, 25));
    btnMiddleImage.setMaximumSize(new Dimension(25, 25));
    btnMiddleImageCon.setMargin(new Insets(0, 0, 0, 0));
    toolBar_1.add(btnMiddleImage);

    menuMiddleChartActions = new MenuChartActions() {
      @Override
      public void selectMZorRT() {
        // TODO
        // select MZ + PM for middle Chart
        dialogSelectMZDirect.open(1);
      }
    };
    menuMiddleChartActions.setMaximumSize(new Dimension(2147483647, 28));
    topmidle.add(menuMiddleChartActions, BorderLayout.CENTER);


    pnSpec = new PnChartWithSettings();
    splitMZvsSpec.setRightComponent(pnSpec);
    GridBagLayout gridBagLayout_1 = (GridBagLayout) pnSpec.getEastSettings().getLayout();
    gridBagLayout_1.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0};
    pnSpec.getRbSelectedChartView().addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        JRadioButton rb = (JRadioButton) e.getSource();
        if (rb.isSelected()) {
          setSelectedView(VIEW_BOTTOM_SPECTRUM);
        }
      }
    });
    buttonGroup.add(pnSpec.getRbSelectedChartView());
    pnSpec.getRbSelectedChartView().setSelected(true);

    lbRT = new JLabel("rt=");
    GridBagConstraints gbc_lbRT = new GridBagConstraints();
    gbc_lbRT.insets = new Insets(0, 0, 5, 0);
    gbc_lbRT.anchor = GridBagConstraints.NORTH;
    gbc_lbRT.gridx = 0;
    gbc_lbRT.gridy = 1;
    pnSpec.getEastSettings().add(lbRT, gbc_lbRT);

    lbRT2 = new JLabel("-");
    GridBagConstraints gbc_lbRT2 = new GridBagConstraints();
    gbc_lbRT2.insets = new Insets(0, 0, 5, 0);
    gbc_lbRT2.gridx = 0;
    gbc_lbRT2.gridy = 2;
    pnSpec.getEastSettings().add(lbRT2, gbc_lbRT2);

    lbX = new JLabel("x=");
    lbX.setToolTipText("in \u00B5m");
    GridBagConstraints gbc_lbX = new GridBagConstraints();
    gbc_lbX.insets = new Insets(0, 0, 5, 0);
    gbc_lbX.gridx = 0;
    gbc_lbX.gridy = 3;
    pnSpec.getEastSettings().add(lbX, gbc_lbX);

    lbX2 = new JLabel("-");
    GridBagConstraints gbc_lbX2 = new GridBagConstraints();
    gbc_lbX2.insets = new Insets(0, 0, 5, 0);
    gbc_lbX2.gridx = 0;
    gbc_lbX2.gridy = 4;
    pnSpec.getEastSettings().add(lbX2, gbc_lbX2);

    lbY = new JLabel("y=");
    lbY.setToolTipText("in \u00B5m");
    GridBagConstraints gbc_lbY = new GridBagConstraints();
    gbc_lbY.insets = new Insets(0, 0, 5, 0);
    gbc_lbY.anchor = GridBagConstraints.NORTH;
    gbc_lbY.gridx = 0;
    gbc_lbY.gridy = 5;
    pnSpec.getEastSettings().add(lbY, gbc_lbY);

    lbY2 = new JLabel("-");
    GridBagConstraints gbc_lbY2 = new GridBagConstraints();
    gbc_lbY2.anchor = GridBagConstraints.NORTH;
    gbc_lbY2.gridx = 0;
    gbc_lbY2.gridy = 6;
    pnSpec.getEastSettings().add(lbY2, gbc_lbY2);

    panel_1 = new JPanel();
    FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
    flowLayout.setVgap(0);
    flowLayout.setHgap(0);
    flowLayout.setAlignment(FlowLayout.LEFT);
    pnSpec.add(panel_1, BorderLayout.NORTH);

    toolBar_2 = new JToolBar();
    panel_1.add(toolBar_2);
    toolBar_2.setFloatable(false);
    toolBar_2.setRollover(true);

    tglbtnOri = new JToggleButton("");
    tglbtnOri.setToolTipText("Change orientation");
    tglbtnOri.setIcon(
        new ImageIcon(ImageVsSpecViewPanel.class.getResource("/img/btn_split_change.png")));
    tglbtnOri.setSelectedIcon(
        new ImageIcon(ImageVsSpecViewPanel.class.getResource("/img/btn_split_change_selec.png")));
    tglbtnOri.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        JToggleButton btn = (JToggleButton) e.getSource();
        getSplitMZvsSpec().setOrientation(
            btn.isSelected() ? JSplitPane.HORIZONTAL_SPLIT : JSplitPane.VERTICAL_SPLIT);
      }
    });
    toolBar_2.add(tglbtnOri);

    menuBottomChartActions = new MenuChartActions() {
      @Override
      public void selectMZorRT() {
        // TODO open dialog for RT selection for spectrum
        dialogSelectMZDirect.open(2);
      }
    };
    panel_1.add(menuBottomChartActions);
    menuBottomChartActions.setMaximumSize(new Dimension(2147483647, 27));

    pnTopEICTIC1 = new JPanel();
    splitCenterThree.setLeftComponent(pnTopEICTIC1);
    pnTopEICTIC1.setLayout(new BorderLayout(0, 0));

    pnTopEICTIC = new PnChartWithSettings();
    pnTopEICTIC.getRbSelectedChartView().addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        JRadioButton rb = (JRadioButton) e.getSource();
        if (rb.isSelected()) {
          setSelectedView(VIEW_TOP_CHROM);
        }
      }
    });

    menuTop = new JPanel();
    pnTopEICTIC1.add(menuTop, BorderLayout.NORTH);
    menuTop.setLayout(new BorderLayout(0, 0));

    cbHideTopTIC = new JCheckBox("");
    cbHideTopTIC.setSelected(true);
    menuTop.add(cbHideTopTIC, BorderLayout.EAST);
    cbHideTopTIC.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        JCheckBox cb = (JCheckBox) e.getSource();
        getPnTopEICTIC().setVisible(cb.isSelected());
      }
    });
    cbHideTopTIC.setToolTipText("Hide");

    toolBar = new JToolBar();
    toolBar.setRollover(true);
    toolBar.setMinimumSize(new Dimension(13, 25));
    toolBar.setMaximumSize(new Dimension(25, 25));
    toolBar.setSize(new Dimension(25, 25));
    toolBar.setBorder(null);
    menuTop.add(toolBar, BorderLayout.WEST);

    btnTopTic = new JToggleButton("");
    btnTopTic.setToolTipText("Total ion current");
    btnTopTic.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        setModeTop(MODE_TIC);
      }
    });

    btnTopSplitchange = new JToggleButton("");
    btnTopSplitchange.setToolTipText("Change orientation");
    btnTopSplitchange.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JToggleButton btn = (JToggleButton) e.getSource();
        getSplitCenterThree().setOrientation(
            btn.isSelected() ? JSplitPane.HORIZONTAL_SPLIT : JSplitPane.VERTICAL_SPLIT);
      }
    });
    btnTopSplitchange.setSelectedIcon(
        new ImageIcon(ImageVsSpecViewPanel.class.getResource("/img/btn_split_change_selec.png")));
    btnTopSplitchange.setIcon(
        new ImageIcon(ImageVsSpecViewPanel.class.getResource("/img/btn_split_change.png")));
    btnTopSplitchange.setMinimumSize(new Dimension(25, 25));
    btnTopSplitchange.setMaximumSize(new Dimension(25, 25));
    btnTopSplitchange.setMargin(new Insets(0, 0, 0, 0));
    btnTopSplitchange.setBounds(new Rectangle(0, 0, 24, 24));
    toolBar.add(btnTopSplitchange);
    btngroup_TOP.add(btnTopTic);
    btnTopTic.setSelected(true);
    btnTopTic.setSelectedIcon(
        new ImageIcon(ImageVsSpecViewPanel.class.getResource("/img/btn_tic_selected.png")));
    btnTopTic.setMargin(new Insets(0, 0, 0, 0));
    btnTopTic.setMinimumSize(new Dimension(25, 25));
    btnTopTic.setMaximumSize(new Dimension(25, 25));
    btnTopTic.setBounds(new Rectangle(0, 0, 24, 24));
    btnTopTic.setIcon(new ImageIcon(ImageVsSpecViewPanel.class.getResource("/img/btn_tic.png")));
    toolBar.add(btnTopTic);

    btnTopEic = new JToggleButton("");
    btnTopEic.setToolTipText("Selected mz ion trace");
    btnTopEic.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setModeTop(MODE_EIC);
      }
    });
    btngroup_TOP.add(btnTopEic);
    btnTopEic.setSelectedIcon(
        new ImageIcon(ImageVsSpecViewPanel.class.getResource("/img/btn_mz_selected.png")));
    btnTopEic.setMargin(new Insets(0, 0, 0, 0));
    btnTopEic.setMinimumSize(new Dimension(25, 25));
    btnTopEic.setMaximumSize(new Dimension(25, 25));
    btnTopEic.setIcon(new ImageIcon(ImageVsSpecViewPanel.class.getResource("/img/btn_mz.png")));
    toolBar.add(btnTopEic);

    btnTopListPeaks = new JToggleButton("");
    btnTopListPeaks.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setModeTop(MODE_PEAK_LIST);
      }
    });
    btngroup_TOP.add(btnTopListPeaks);
    btnTopListPeaks
        .setIcon(new ImageIcon(ImageVsSpecViewPanel.class.getResource("/img/btn_table.png")));
    btnTopListPeaks.setSelectedIcon(
        new ImageIcon(ImageVsSpecViewPanel.class.getResource("/img/btn_table_selec.png")));
    btnTopListPeaks.setToolTipText("List of extracted peaks (by mz)");
    btnTopListPeaks.setMinimumSize(new Dimension(25, 25));
    btnTopListPeaks.setMaximumSize(new Dimension(25, 25));
    btnTopListPeaks.setMargin(new Insets(0, 0, 0, 0));
    toolBar.add(btnTopListPeaks);

    menuTopChartActions = new MenuChartActions() {
      @Override
      public void selectMZorRT() {
        // TODO
        // Select MZ + PM for Top Chart
        dialogSelectMZDirect.open(0);
      }
    };
    menuTopChartActions.setMaximumSize(new Dimension(2147483647, 28));
    menuTop.add(menuTopChartActions, BorderLayout.CENTER);

    menuTopTableActions = new MenuTableActions(getTableMzPeak());
    menuTopTableActions.setMaximumSize(new Dimension(2147483647, 28));

    pnTopEICTIC1.add(pnTopEICTIC, BorderLayout.CENTER);
    buttonGroup.add(pnTopEICTIC.getRbSelectedChartView());
    GridBagLayout gridBagLayout_2 = (GridBagLayout) pnTopEICTIC.getEastSettings().getLayout();
    gridBagLayout_2.rowWeights = new double[] {0.0, 0.0, 1.0};
    gridBagLayout_2.rowHeights = new int[] {21, 0, 0};
    gridBagLayout_2.columnWeights = new double[] {0.0};
    gridBagLayout_2.columnWidths = new int[] {21};

    lbMZTop = new JLabel("mz=");
    GridBagConstraints gbc_lbMZTop = new GridBagConstraints();
    gbc_lbMZTop.anchor = GridBagConstraints.NORTHWEST;
    gbc_lbMZTop.insets = new Insets(0, 0, 5, 0);
    gbc_lbMZTop.gridx = 0;
    gbc_lbMZTop.gridy = 1;
    pnTopEICTIC.getEastSettings().add(lbMZTop, gbc_lbMZTop);

    lbPMTop = new JLabel("pm=");
    GridBagConstraints gbc_lbPMTop = new GridBagConstraints();
    gbc_lbPMTop.anchor = GridBagConstraints.NORTHWEST;
    gbc_lbPMTop.gridx = 0;
    gbc_lbPMTop.gridy = 2;
    pnTopEICTIC.getEastSettings().add(lbPMTop, gbc_lbPMTop);

    pnWestSettings = new JPanel();
    add(pnWestSettings, BorderLayout.WEST);
    pnWestSettings.setLayout(new BorderLayout(0, 0));



    pnWestImageSettings = new Module("Image settings", true);
    pnWestSettings.add(pnWestImageSettings, BorderLayout.EAST);
    pnWestImageSettings.getPnContent().setLayout(new BorderLayout(0, 0));

    pnSettImageCon = new JPanel();
    pnWestImageSettings.getPnContent().add(pnSettImageCon, BorderLayout.CENTER);
    pnSettImageCon
        .setLayout(new MigLayout("", "[grow][grow]", "[][][][][top][][][][][][][][][][][][]"));

    lbDisconCon = new JLabel("Discontinuos");
    pnSettImageCon.add(lbDisconCon, "cell 0 0 2 1");

    lbVelocity = new JLabel("v =");
    pnSettImageCon.add(lbVelocity, "flowx,cell 0 2,alignx trailing");

    txtVelocity = new JTextField();
    txtVelocity.setText("50");
    txtVelocity.setToolTipText("Velocity [\u00B5m/s]");
    pnSettImageCon.add(txtVelocity, "cell 1 2");
    txtVelocity.setColumns(10);
    txtVelocity.getDocument().addDocumentListener(autoDocumentL);

    lbSpotsize = new JLabel("d =");
    pnSettImageCon.add(lbSpotsize, "cell 0 3,alignx trailing");

    txtSpotsize = new JTextField();
    txtSpotsize.setText("50");
    txtSpotsize.setToolTipText("Spot size [\u00B5m]");
    pnSettImageCon.add(txtSpotsize, "cell 1 3,alignx left");
    txtSpotsize.setColumns(10);
    txtSpotsize.getDocument().addDocumentListener(autoDocumentL);

    pnImgCon = new JPanel();
    pnSettImageCon.add(pnImgCon, "cell 0 4 2 1,grow");
    pnImgCon.setLayout(new MigLayout("", "[grow][grow]", "[][][][grow]"));

    lblTp = new JLabel("Split after");
    pnImgCon.add(lblTp, "cell 0 0,alignx trailing");

    txtTimePerLine = new JTextField();
    txtTimePerLine.setText("60");
    txtTimePerLine.setToolTipText("Time [s] or scans per line");
    pnImgCon.add(txtTimePerLine, "cell 1 0,alignx left");
    txtTimePerLine.setColumns(5);

    lblStartX = new JLabel("Start x");
    pnImgCon.add(lblStartX, "cell 0 1,alignx trailing");

    txtSplitStartX = new JTextField();
    txtSplitStartX.setToolTipText("Start X to be removed from the data.");
    txtSplitStartX.setText("0");
    pnImgCon.add(txtSplitStartX, "cell 1 1,growx");
    txtSplitStartX.setColumns(5);
    txtSplitStartX.getDocument().addDocumentListener(autoDocumentL);

    comboSplitUnit = new JComboBox();
    comboSplitUnit.setModel(new DefaultComboBoxModel(XUNIT.values()));
    comboSplitUnit.setSelectedIndex(0);
    pnImgCon.add(comboSplitUnit, "cell 1 2,growx");

    panel = new JPanel();
    pnImgCon.add(panel, "cell 0 3 2 1,grow");

    button = new JButton("-");
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (getComboSplitUnit().getSelectedItem().equals(XUNIT.DP)) {
          int tpl = Module.intFromTxt(getTxtTimePerLine()) - Module.intFromTxt(txtAddSplit);
          getTxtTimePerLine().setText(String.valueOf(tpl));
        } else {
          float tpl = Module.floatFromTxt(getTxtTimePerLine()) - Module.floatFromTxt(txtAddSplit);
          NumberFormat format =
              SettingsHolder.getSettings().getSetGeneralValueFormatting().getRTFormat();
          getTxtTimePerLine().setText(format.format(tpl));
        }
      }
    });
    panel.add(button);

    txtAddSplit = new JTextField();
    panel.add(txtAddSplit);
    txtAddSplit.setToolTipText("The data points or time units to be added to \"split after\".");
    txtAddSplit.setText("10");
    txtAddSplit.setColumns(5);

    button_1 = new JButton("+");
    button_1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (getComboSplitUnit().getSelectedItem().equals(XUNIT.DP)) {
          int tpl = Module.intFromTxt(getTxtTimePerLine()) + Module.intFromTxt(txtAddSplit);
          getTxtTimePerLine().setText(String.valueOf(tpl));
        } else {
          float tpl = Module.floatFromTxt(getTxtTimePerLine()) + Module.floatFromTxt(txtAddSplit);
          NumberFormat format =
              SettingsHolder.getSettings().getSetGeneralValueFormatting().getRTFormat();
          getTxtTimePerLine().setText(format.format(tpl));
        }
      }
    });
    panel.add(button_1);
    txtTimePerLine.getDocument().addDocumentListener(autoDocumentL);

    btnSendImage = new JButton("Send Image");
    btnSendImage.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        // send image to imageeditor // TODO
        if (currentHeat != null && currentHeat.getImage() != null) {
          // TODO txtID.getText()
          window.sendImage2DToImageEditor((Image2D) currentHeat.getImage(),
              getTxtProject().getText(), getTxtImgGroupID().getText());
        }
      }
    });

    lblProject = new JLabel("project");
    pnSettImageCon.add(lblProject, "cell 0 5,alignx trailing");

    txtProject = new JTextField();
    txtProject.setToolTipText("Porject name in image editor");
    txtProject.setText("RAW");
    pnSettImageCon.add(txtProject, "cell 1 5,growx");
    txtProject.setColumns(10);

    lblId = new JLabel("group");
    pnSettImageCon.add(lblId, "flowx,cell 0 6");

    txtImgGroupID = new JTextField();
    txtImgGroupID.setToolTipText("ID for grouping in ImageEditor");
    txtImgGroupID.setText("g");
    pnSettImageCon.add(txtImgGroupID, "cell 1 6,growx");
    txtImgGroupID.setColumns(10);
    btnSendImage.setToolTipText("Sends image to image editor");
    pnSettImageCon.add(btnSendImage, "cell 1 7");

    pnPeakList = new ModuleListWithOptions("PeakLists", true, wnd.getLogicRunner().getPeakLists());
    pnPeakList.getList().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    pnWestSettings.add(pnPeakList, BorderLayout.WEST);

    btnApplyPeakList = new JButton("Apply peaklist to rawdata");
    btnApplyPeakList.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // TODO set used PeakLists
        try {
          PeakList pkl =
              window.getLogicRunner().getPeakLists().get(pnPeakList.getList().getSelectedIndex());
          if (pkl != null) {
            setSelectedPeakList(pkl);
            getLbSelectedPeakList().setText(pkl.getName());
          }
        } catch (Exception ex) {
        }
      }
    });
    pnPeakList.getPnOptions().setLayout(new MigLayout("", "[155px]", "[23px][]"));
    pnPeakList.getPnOptions().add(btnApplyPeakList, "cell 0 0,alignx left,aligny top");

    lbSelectedPeakList = new JLabel("");
    pnPeakList.getPnOptions().add(lbSelectedPeakList, "cell 0 1");
    //
    initMZMineListeners();
    //
    setKeyBindings();
    // init dialogs
    initDialogs();
  }

  private void initDialogs() {
    dialogSelectMZDirect = new SelectMZDirectDialog(this);
  }

  private void setKeyBindings() {
    ActionMap actionMap = getPnBottomSpec().getActionMap();
    int condition = JComponent.WHEN_IN_FOCUSED_WINDOW;
    InputMap inputMap = getPnBottomSpec().getInputMap(condition);

    // pressed events
    addKey(actionMap, inputMap, KEY_LEFT, VK_LEFT, false);
    addKey(actionMap, inputMap, KEY_UP, VK_UP, false);
    addKey(actionMap, inputMap, KEY_DOWN, VK_DOWN, false);
    addKey(actionMap, inputMap, KEY_RIGHT, VK_RIGHT, false);
    addKey(actionMap, inputMap, KeyEvent.VK_1, VK_1, false);
    addKey(actionMap, inputMap, KeyEvent.VK_2, VK_2, false);
    addKey(actionMap, inputMap, KeyEvent.VK_3, VK_3, false);
    addKey(actionMap, inputMap, KeyEvent.VK_DELETE, VK_DELETE, false);
    addKey(actionMap, inputMap, KeyEvent.VK_F5, VK_F5, false);

    // released events
    addKey(actionMap, inputMap, KEY_LEFT, VK_LEFT_RELEASED, true);
    addKey(actionMap, inputMap, KEY_UP, VK_UP_RELEASED, true);
    addKey(actionMap, inputMap, KEY_DOWN, VK_DOWN_RELEASED, true);
    addKey(actionMap, inputMap, KEY_RIGHT, VK_RIGHT_RELEASED, true);
    addKey(actionMap, inputMap, KeyEvent.VK_1, VK_1_RELEASED, true);
    addKey(actionMap, inputMap, KeyEvent.VK_2, VK_2_RELEASED, true);
    addKey(actionMap, inputMap, KeyEvent.VK_3, VK_3_RELEASED, true);
    addKey(actionMap, inputMap, KeyEvent.VK_DELETE, VK_DELETE_RELEASED, true);
    addKey(actionMap, inputMap, KeyEvent.VK_F5, VK_F5_RELEASED, true);

  }

  private void addKey(ActionMap actionMap, InputMap inputMap, int keyEvent, String key,
      boolean released) {
    inputMap.put(KeyStroke.getKeyStroke(keyEvent, 0, released), key);
    actionMap.put(key, new KeyAction(key));
  }

  private void initMZMineListeners() {
    MZMineCallBackListener.addMZmineProjectListener(new MZmineProjectListenerAdapter() {
      @Override
      public void peakListsChanged(PeakList p, Operation op) {
        // remove all pkls
        getPnPeakList().removeAllElements();
        // add all
        PeakList[] list = MZMineLogicsConnector.getPeakLists();
        for (PeakList pkl : list) {
          getPnPeakList().addElement(pkl, pkl.getName());
        }
      }

      @Override
      public void dataFilesChanged(RawDataFile raw, Operation op) {

      }
    });
  }

  protected void setSelectedPeakList(PeakList pkl) {
    selectedPeakList = pkl;
  }

  private void initVars() {
    // setup vars
    selectedVsRetentionTime[0] = 0;
    selectedVsRetentionTime[1] = 0;
    //
    settImage = new SettingsMSImage(true, true, 50, 50, 60, new MZIon("Caffein", 195, 0.3));
    settSplitCon = new SettingsImageContinousSplit(10, 0, XUNIT.s);
  }

  // set mode
  protected void setModeMiddle(int mode) {
    // setMode
    selectedModeMiddle = mode;
    // set things visible
    getLbMZMiddle().setVisible(mode != MODE_TIC);
    getLbPMMiddle().setVisible(mode != MODE_TIC);

    // show or hide imagesettings Panel
    getPnWestImageSettings()
        .setVisible(mode == MODE_IMAGE_CON || mode == MODE_IMAGE_DISCON || mode == MODE_IMAGE);
    getPnImgCon().setVisible(mode == MODE_IMAGE_CON);

    // update top view and chart
    renewMiddleImageChrom(selectedVsMiddleMZ, selectedVsMiddlePM);
  }

  protected void setModeTop(int mode) {
    // setMode
    selectedModeTop = mode;
    // set things visible
    getLbMZTop().setVisible(mode == MODE_EIC);
    getLbPMTop().setVisible(mode == MODE_EIC);

    // update top view and chart
    renewTopChrom(selectedVsTopMZ, selectedVsTopPM);
  }


  // is active on tab is shown
  public void setIsShown() {
    // start up settings:
    if (selectedSpectrum == null) {
      MZChromatogram spec =
          MZDataFactory.getSpectrumAsMZChrom(window.getLogicRunner().generateSpectrumByRT(0));
      if (spec != null)
        renewBottomSpectrum(spec);
    }
    //
    if (selectedModeMiddle == -1) {
      setModeTop(MODE_TIC);
      setModeMiddle(MODE_EIC);
    }
    // renewAll();
    renewAll();
  }

  // #############################################################################
  // Chart Logics
  public void renewAll() {
    LogicRunner runner = window.getLogicRunner();
    Image2D img = runner.getCurrentImage();
    SettingsImageContinousSplit split = settSplitCon;
    SettingsGeneralImage sett = settImage;
    if (img != null) {
      sett = img.getSettings().getSettImage();
      settSplitCon =
          (SettingsImageContinousSplit) img.getSettingsByClass(SettingsImageContinousSplit.class);
    }

    renewTopChrom(selectedVsTopMZ, selectedVsTopPM);
    renewMiddleImageChrom(selectedVsMiddleMZ, selectedVsMiddlePM);

    // get selected File
    if (specSelectionMode == SPECTRUM_SELECTION_MODE_RT) {
      selectedSpectrum = window.getLogicRunner().generateSpectrumSUMByRT(selectedVsRetentionTime[0],
          selectedVsRetentionTime[1]);
    } else {
      Rectangle2D rec = selectedImageRectForSpec;
      selectedSpectrum = runner.generateSpectrumByXY(split, sett, rec);
    }
    // got spec?
    if (selectedSpectrum == null) {
      if (isImagingRawData) {
        setSelectedXY(1, 1, 1, 1);
        Rectangle2D rec = selectedImageRectForSpec;
        selectedSpectrum = window.getLogicRunner().generateSpectrumByXY(split, sett, rec);
      } else {
        setSelectedVsRetentionTime(0, 0);
        selectedSpectrum =
            MZDataFactory.getSpectrumAsMZChrom(window.getLogicRunner().generateSpectrumByRT(0));
      }
    }
    //
    renewBottomSpectrum(selectedSpectrum);
  }

  public void renewBottomSpectrum(MZChromatogram spec) {
    if (spec != null) {
      //
      selectedSpectrum = spec;
      // get ViewPanel
      JPanel view = getPnBottomSpec().getPnChartView();
      view.removeAll();

      //
      if (taskBottom != null)
        taskBottom.cancel(true);
      taskBottom = new ProgressUpdateTask<ChartPanel>(1) {
        @Override
        protected ChartPanel doInBackground2() throws Exception {
          return spec.getChromChartPanel("", "m/z", "intensity");
        }

        @Override
        protected void done() {
          try {
            if (!isCancelled() && get() != null) {
              // set chart
              setChartBottomSpectrum(get());
            } else
              logger.warn("No spectrum created");
          } catch (InterruptedException | ExecutionException e) {
            logger.warn("No spectrum created", e);
          }
        }
      };
      Thread t = new Thread(taskBottom);
      t.start();
    }
  }

  public void renewMiddleImageChrom(double mz, double pm) {
    try {
      // get ViewPanel
      JPanel view = getPnMiddleImageChrom().getPnChartView();
      view.removeAll();
      //
      if (taskMiddle != null)
        taskMiddle.cancel(true);
      taskMiddle = new ProgressUpdateTask<ChartPanel>(1) {
        @Override
        protected ChartPanel doInBackground2() throws Exception {
          ChartPanel chart = null;
          // TIC
          if (selectedModeMiddle == MODE_TIC)
            chart = window.getLogicRunner().generateTICAsChartPanel();
          else if (mz != -1) {
            // IMAGE?
            if (selectedModeMiddle == MODE_IMAGE_CON || selectedModeMiddle == MODE_IMAGE_DISCON
                || selectedModeMiddle == MODE_IMAGE) {
              // First get new Imagesettings
              setupNewImageSettingsFromPanel();
              Image2D image = null;
              MZIon ion = settImage.getMZIon();
              ion.setMz(mz);
              ion.setPm(pm);


              // Image Con
              if (selectedModeMiddle == MODE_IMAGE_CON) {
                image = window.getLogicRunner().generateImageCon(settImage, settSplitCon);
              }
              // Image Discon
              if (selectedModeMiddle == MODE_IMAGE_DISCON) {
                image = window.getLogicRunner().generateImageDiscon(settImage);
              }
              // image generation if raw data is imaging raw data file
              if (selectedModeMiddle == MODE_IMAGE)
                image = window.getLogicRunner().generateImage(settImage,
                    window.getLogicRunner().getSelectedRawDataFile());
              // set chart
              if (image != null) {
                currentHeat = window.getHeatFactory().generateHeatmap(image);
                chart = currentHeat.getChartPanel();
              }
            }
            // EIC
            if (selectedModeMiddle == MODE_EIC)
              chart = window.getLogicRunner()
                  .generateEICAsChartPanel(new MZIon("", selectedVsMiddleMZ, selectedVsMiddlePM));
          }
          return chart;
        }

        @Override
        protected void done() {
          try {
            if (!isCancelled() && get() != null) {
              // set chart
              setChartMiddleImageChrom(get());
              if (mz != -1 && selectedModeTop == MODE_EIC) {
                // set mz
                selectedVsTopMZ = mz;
                selectedVsTopPM = pm;
                // show
                getLbMZMiddle().setText("mz=" + window.round(mz, 4));
                getLbPMMiddle().setText("pm=" + window.round(pm, 4));
              }
            } else
              logger.warn("Middle not updated");
          } catch (InterruptedException | ExecutionException e) {
            logger.warn("Middle not updated", e);
          }
        }
      };
      Thread t = new Thread(taskMiddle);
      t.start();

    } catch (Exception e) {
      logger.error("", e);
    }
  }

  public void renewTopChrom(final double mz, final double pm) {
    try {
      // get ViewPanel
      JPanel view = getPnTopEICTIC().getPnChartView();
      view.removeAll();
      //
      // First Mode = Peak List?!
      if (selectedModeTop == MODE_PEAK_LIST) {
        // Open Peak List
        view.add(tableMzPeak, BorderLayout.CENTER);
        view.validate();

        // Add Menu to
        getMenuTop().remove(getMenuTopChartActions());
        getMenuTop().add(getMenuTopTableActions(), BorderLayout.CENTER);
        getMenuTop().repaint();
      } else {
        // Add Menu to
        getMenuTop().remove(getMenuTopTableActions());
        getMenuTop().add(getMenuTopChartActions(), BorderLayout.CENTER);
        getMenuTop().repaint();

        // Chartpanel if not a List
        // TIC or EIC
        if (taskTop != null)
          taskTop.cancel(true);
        taskTop = new ProgressUpdateTask<ChartPanel>(1) {
          @Override
          protected ChartPanel doInBackground2() throws Exception {
            ChartPanel chart = null;
            if (selectedModeTop == MODE_TIC)
              chart = window.getLogicRunner().generateTICAsChartPanel();
            else if (mz != -1) {
              // EIC
              if (selectedModeTop == MODE_EIC)
                chart = window.getLogicRunner().generateEICAsChartPanel(new MZIon("", mz, pm));
            }
            return chart;
          }

          @Override
          protected void done() {
            try {
              if (!isCancelled()) {
                if (get() != null) {
                  // set chart
                  setChartTopChrom(get());
                  if (mz != -1 && selectedModeTop == MODE_EIC) {
                    // set mz
                    selectedVsTopMZ = mz;
                    selectedVsTopPM = pm;
                    // show
                    getLbMZTop().setText("mz=" + window.round(mz, 4));
                    getLbPMTop().setText("pm=" + window.round(pm, 4));
                  }
                } else
                  logger.warn("No TIC created");
              }
            } catch (InterruptedException | ExecutionException e) {
              logger.warn("No TIC created", e);
            }
          }
        };
        Thread t = new Thread(taskTop);
        t.start();
      }
    } catch (Exception e) {
      logger.error("", e);
    }
  }

  // SET Chartpanel
  // add mouselistener and set it right
  // Set bottom spectrum chartpanel
  public void setChartBottomSpectrum(ChartPanel chartSpec2) {
    if (chartBottomSpec != null) {
      Range lastzoom = ChartLogics.getZoomDomainAxis(chartBottomSpec);
      ChartLogics.setZoomDomainAxis(chartSpec2, lastzoom, true);
    }
    this.chartBottomSpec = chartSpec2;
    chartBottomSpec.setMouseZoomable(selectedView == VIEW_BOTTOM_SPECTRUM);
    // set menu chart actions
    getMenuBottomChartActions().setChartPanel(chartBottomSpec);

    // Add ChartMouseListener to myChart
    this.chartBottomSpec.addMouseListener(new MouseListener() {
      boolean scrollsXAxis = false;
      Point2D pressed = null;

      @Override
      public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
          // get Plot Values
          Point2D released = ChartLogics.mouseXYToPlotXY(chartBottomSpec, e.getX(), e.getY());

          // nur wenn innerhalb der range
          Range yrange = chartBottomSpec.getChart().getXYPlot().getRangeAxis().getRange();
          Range xrange = chartBottomSpec.getChart().getXYPlot().getDomainAxis().getRange();
          if (pressed != null) {
            if (released.getY() >= yrange.getLowerBound()
                && released.getY() <= yrange.getUpperBound()
                && released.getX() >= xrange.getLowerBound()
                && released.getX() <= xrange.getUpperBound()) {
              // dann setzen
              System.out.println("OK released");
              // nur die Aktionen ausführen wenn kein MouseResize
              if (!chartBottomSpec.isDomainZoomable()) {
                if (selectedView == VIEW_MIDDLE_IMAGECHROM) {
                  // Aktionen ausführen: hier: Spektrum wurde angeklickt
                  // MZ ausgewählt mit pm
                  double mz = (released.getX() + pressed.getX()) / 2;
                  double pm = Math.abs((released.getX() - pressed.getX())) / 2;
                  System.out.println(mz + "+MIDDLE-" + pm);
                  //
                  renewMiddleImageChrom(mz, pm);
                } else if (selectedView == VIEW_TOP_CHROM) {
                  // Aktionen ausführen: hier: Spektrum wurde angeklickt
                  // MZ ausgewählt mit pm
                  double mz = (released.getX() + pressed.getX()) / 2;
                  double pm = Math.abs((released.getX() - pressed.getX())) / 2;
                  System.out.println(mz + "+TOP-" + pm);
                  // Peak List selected?
                  if (selectedModeTop == MODE_PEAK_LIST) {
                    // add to table
                    addMZtoTable(mz, pm);
                  }
                  // open EIC
                  else if (selectedModeTop == MODE_EIC) {
                    renewTopChrom(mz, pm);
                  }
                }
              }
            } else if (released.getY() < yrange.getLowerBound() && scrollsXAxis) {
              // scroll x axis if mouse pressed and moved on axis

            }
          }
        }
      }

      @Override
      public void mousePressed(MouseEvent e) {
        scrollsXAxis = false;
        pressed = null;
        System.out.println("Pressed " + e.getX() + "  " + e.getY());

        Point2D pos = ChartLogics.mouseXYToPlotXY(chartBottomSpec, e.getX(), e.getY());
        // nur speichern wenn innerhalb des charts
        Range yrange = chartBottomSpec.getChart().getXYPlot().getRangeAxis().getRange();
        Range xrange = chartBottomSpec.getChart().getXYPlot().getDomainAxis().getRange();
        if (pos.getY() >= yrange.getLowerBound() && pos.getY() <= yrange.getUpperBound()
            && pos.getX() >= xrange.getLowerBound() && pos.getX() <= xrange.getUpperBound()) {
          // dann setzen
          pressed = pos;
          System.out.println("OK");
        } else if (pos.getY() < yrange.getLowerBound()) {
          // mouse scrolling xaxis
          pressed = pos;
          scrollsXAxis = true;
        }
      }

      @Override
      public void mouseExited(MouseEvent e) {}

      @Override
      public void mouseEntered(MouseEvent e) {}

      @Override
      public void mouseClicked(MouseEvent e) {}
    });

    // add
    JPanel view = getPnBottomSpec().getPnChartView();
    view.add(chartSpec2, BorderLayout.CENTER);
    view.validate();
  }

  public void setChartMiddleImageChrom(ChartPanel chartChrom2) {

    if ((selectedModeMiddle == MODE_TIC || selectedModeMiddle == MODE_EIC)) {
      if (chartMiddleChrom != null) {
        Range lastzoom = ChartLogics.getZoomDomainAxis(chartMiddleChrom);
        ChartLogics.setZoomDomainAxis(chartChrom2, lastzoom, true);
      }
      this.chartMiddleChrom = chartChrom2;
    }
    if ((selectedModeMiddle == MODE_IMAGE_CON || selectedModeMiddle == MODE_IMAGE_DISCON)
        || selectedModeMiddle == MODE_IMAGE) {
      if (chartMiddleImage != null) {
        Range lastzoom = ChartLogics.getZoomDomainAxis(chartMiddleImage);
        ChartLogics.setZoomDomainAxis(chartChrom2, lastzoom, true);
      }
      this.chartMiddleImage = chartChrom2;
    }

    chartChrom2.setMouseZoomable(selectedView == VIEW_MIDDLE_IMAGECHROM);
    chartChrom2.setMouseWheelEnabled(true);
    // set menu chart actions
    getMenuMiddleChartActions().setChartPanel(chartChrom2);


    // Add ChartMouseListener to myChart
    chartChrom2.addMouseListener(new MouseListener() {
      Point2D pressed = null;

      @Override
      public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
          ChartPanel chartMiddle = getCurrentChartMiddle();
          //
          Point2D released = ChartLogics.mouseXYToPlotXY(chartMiddle, e.getX(), e.getY());
          // nur wenn innerhalb der range
          Range yrange = chartMiddle.getChart().getXYPlot().getRangeAxis().getRange();
          Range xrange = chartMiddle.getChart().getXYPlot().getDomainAxis().getRange();
          if (released.getY() >= yrange.getLowerBound() && released.getY() <= yrange.getUpperBound()
              && released.getX() >= xrange.getLowerBound()
              && released.getX() <= xrange.getUpperBound()) {
            // dann setzen
            System.out.println("OK released");
            // nur die Aktionen ausführen wenn kein MouseResize
            if (!chartMiddle.isDomainZoomable()) {
              // renew spec
              if (taskSpectrumGenerator != null)
                taskSpectrumGenerator.cancel(true);

              taskSpectrumGenerator = new ProgressUpdateTask<MZChromatogram>(1) {
                protected MZChromatogram doInBackground2() throws Exception {
                  if (selectedModeMiddle == MODE_EIC || selectedModeMiddle == MODE_TIC) {
                    // MZ ausgewählt mit pm
                    double rt = pressed.getX();
                    double rt2 = released.getX();
                    setSelectedVsRetentionTime(rt, rt2);
                    // renew spec
                    MZChromatogram spec = window.getLogicRunner().generateSpectrumSUMByRT(rt, rt2);
                    return spec;
                  } else if (selectedModeMiddle == MODE_IMAGE_CON
                      || selectedModeMiddle == MODE_IMAGE_DISCON
                      || selectedModeMiddle == MODE_IMAGE) {
                    // area on Image selected
                    float x = (float) pressed.getX();
                    float y = (float) pressed.getY();
                    float x2 = (float) released.getX();
                    float y2 = (float) released.getY();
                    setSelectedXY(x, y, x2, y2);
                    Image2D img = window.getLogicRunner().getCurrentImage();
                    // renew spec by img area
                    SettingsImageContinousSplit split = settSplitCon;
                    SettingsGeneralImage sett = settImage;
                    if (img != null) {
                      sett = img.getSettings().getSettImage();
                      settSplitCon = (SettingsImageContinousSplit) img
                          .getSettingsByClass(SettingsImageContinousSplit.class);
                    }
                    MZChromatogram spec =
                        window.getLogicRunner().generateSpectrumByXY(split, sett, x, y, x2, y2);
                    return spec;
                  }
                  return null;
                }

                protected void done() {
                  try {
                    if (!isCancelled() && get() != null) {
                      renewBottomSpectrum(get());
                    }
                  } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                  }
                }
              };
              Thread t = new Thread(taskSpectrumGenerator);
              t.start();
            }
          }
        }
      }

      @Override
      public void mousePressed(MouseEvent e) {
        ChartPanel chart = getCurrentChartMiddle();

        Point2D pos = ChartLogics.mouseXYToPlotXY(chart, e.getX(), e.getY());
        // nur speichern wenn innerhalb des charts
        Range yrange = chart.getChart().getXYPlot().getRangeAxis().getRange();
        Range xrange = chart.getChart().getXYPlot().getDomainAxis().getRange();
        if (pos.getY() >= yrange.getLowerBound() && pos.getY() <= yrange.getUpperBound()
            && pos.getX() >= xrange.getLowerBound() && pos.getX() <= xrange.getUpperBound()) {
          // dann setzen
          pressed = pos;
          System.out.println("OK");
        }
      }

      @Override
      public void mouseExited(MouseEvent e) {}

      @Override
      public void mouseEntered(MouseEvent e) {}

      @Override
      public void mouseClicked(MouseEvent e) {}
    });

    // add
    JPanel view = getPnMiddleImageChrom().getPnChartView();
    view.add(chartChrom2, BorderLayout.CENTER);
    view.validate();
  }

  protected ChartPanel getCurrentChartMiddle() {
    // TODO falls neue dazu kommen ändern
    // also neue im middle
    if (selectedModeMiddle == MODE_EIC || selectedModeMiddle == MODE_TIC)
      return chartMiddleChrom;
    else
      return chartMiddleImage;
  }

  public void setChartTopChrom(ChartPanel chartChrom2) {
    if (chartTopChrom != null) {
      Range lastzoom = ChartLogics.getZoomDomainAxis(chartTopChrom);
      ChartLogics.setZoomDomainAxis(chartChrom2, lastzoom, true);
    }
    this.chartTopChrom = chartChrom2;
    chartTopChrom.setMouseZoomable(selectedView == VIEW_TOP_CHROM);
    // set menu chart actions
    getMenuTopChartActions().setChartPanel(chartTopChrom);

    // Add ChartMouseListener to myChart
    chartTopChrom.addMouseListener(new MouseListener() {
      Point2D pressed = null;

      @Override
      public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
          Point2D released = ChartLogics.mouseXYToPlotXY(chartTopChrom, e.getX(), e.getY());
          // nur wenn innerhalb der range
          Range yrange = chartTopChrom.getChart().getXYPlot().getRangeAxis().getRange();
          Range xrange = chartTopChrom.getChart().getXYPlot().getDomainAxis().getRange();
          if (released.getY() >= yrange.getLowerBound() && released.getY() <= yrange.getUpperBound()
              && released.getX() >= xrange.getLowerBound()
              && released.getX() <= xrange.getUpperBound()) {
            // dann setzen
            System.out.println("OK released");
            // nur die Aktionen ausführen wenn kein MouseResize
            if (!chartTopChrom.isDomainZoomable()) {
              // Aktionen ausführen: hier: Chrom/Image wurde angeklickt
              if (selectedModeTop == MODE_EIC || selectedModeTop == MODE_TIC) {
                // MZ ausgewählt mit pm
                double rt = pressed.getX();
                double rt2 = released.getX();
                // renew spec
                if (taskSpectrumGenerator != null)
                  taskSpectrumGenerator.cancel(true);

                taskSpectrumGenerator = new ProgressUpdateTask<MZChromatogram>(1) {
                  protected MZChromatogram doInBackground2() throws Exception {
                    return window.getLogicRunner().generateSpectrumSUMByRT(rt, rt2);
                  }

                  protected void done() {
                    try {
                      if (!isCancelled() && get() != null) {
                        renewBottomSpectrum(get());
                        setSelectedVsRetentionTime(rt, rt2);
                      }
                    } catch (InterruptedException | ExecutionException e) {
                      e.printStackTrace();
                    }
                  }
                };
                Thread t = new Thread(taskSpectrumGenerator);
                t.start();
              }
            }
          }
        }
      }

      @Override
      public void mousePressed(MouseEvent e) {
        Point2D pos = ChartLogics.mouseXYToPlotXY(chartTopChrom, e.getX(), e.getY());
        // nur speichern wenn innerhalb des charts
        Range yrange = chartTopChrom.getChart().getXYPlot().getRangeAxis().getRange();
        Range xrange = chartTopChrom.getChart().getXYPlot().getDomainAxis().getRange();
        if (pos.getY() >= yrange.getLowerBound() && pos.getY() <= yrange.getUpperBound()
            && pos.getX() >= xrange.getLowerBound() && pos.getX() <= xrange.getUpperBound()) {
          // dann setzen
          pressed = pos;
          System.out.println("OK");
        }
      }

      @Override
      public void mouseExited(MouseEvent e) {}

      @Override
      public void mouseEntered(MouseEvent e) {}

      @Override
      public void mouseClicked(MouseEvent e) {}
    });

    // add
    JPanel view = getPnTopEICTIC().getPnChartView();
    view.add(chartChrom2, BorderLayout.CENTER);
    view.validate();
  }


  // add MZ to Table
  protected void addMZtoTable(double mz, double pm) {
    // TODO PEAK pick
    if (selectedPeakList != null) {
      // rows
      PeakListRow[] rows = selectedPeakList
          .getRowsInsideMZRange(com.google.common.collect.Range.<Double>closed(mz - pm, mz + pm));

      // add only the highest peak?
      if (getMenuTopTableActions().getBtnOnlyHighestPeak().isSelected()) {
        // get highest peak in rt range
        PeakListRow highestRow = getHighestPeak(rows, selectedPeakList, selectedVsRetentionTime[0],
            selectedVsRetentionTime[1]);
        addOneRowToTable(highestRow);
      } else {
        // add all peaks
        for (PeakListRow r : rows) {
          addOneRowToTable(r);
        }
      }
    } else {
      // show error dialog: no peaklist selected
      Window.getWindow().showErrorDialog("No peaklist selected",
          "Select a MZMine2-PeakList. To generate one go to MZMine: \n 1. Mass detection\n 2. Chromatogram builder \n 3. Chromatogram deconvolution");
    }
  }

  /**
   * adds one PeakListRow to the table, only called by addMZtoTable
   * 
   * @param row
   */
  private void addOneRowToTable(PeakListRow row) {
    // calculate charge
    // TODO save parameterset in config
    System.out.println("Calc Charge");
    MZChargeCalculatorMZMine chargeCalculator = new MZChargeCalculatorMZMine(selectedPeakList,
        Window.getWindow().getSettings().getSetChargeCalc(),
        row.getPeak(selectedPeakList.getRawDataFile(0)));

    // calculate charge finally
    chargeCalculator.doFiltering();

    // add peak to table
    getTableMzPeak().addPeak(row, selectedPeakList, selectedVsRetentionTime[0],
        selectedVsRetentionTime[1]);
  }

  /*
   * returns the highest PeakRow in an rt selection and mz range
   */
  public PeakListRow getHighestPeak(PeakListRow[] rows, PeakList peakListName, double rtMin,
      double rtMax) {
    // save highest peak
    PeakListRow highest = null;
    //
    for (int i = 0; i < rows.length; i++) {
      PeakListRow r = rows[i];

      // is this peak in the rt range?
      double maxPeakRT = Double.MIN_VALUE, minPeakRT = Double.MAX_VALUE;
      Feature[] peaks = r.getPeaks();
      for (int j = 0; j < peaks.length; j++) {
        Feature p = peaks[j];
        com.google.common.collect.Range<Double> range = p.getRawDataPointsRTRange();
        if (range.lowerEndpoint() < minPeakRT)
          minPeakRT = range.lowerEndpoint();
        if (range.upperEndpoint() > maxPeakRT)
          maxPeakRT = range.upperEndpoint();
      }

      if (rtMin <= maxPeakRT && minPeakRT <= rtMax) {
        // highest peak?
        if (highest == null || highest.getAverageHeight() < r.getAverageHeight()) {
          highest = r;
        }
      }
    }
    // add highest
    return highest;
  }

  // settings from panel to settImage
  private void setupNewImageSettingsFromPanel() {
    try {
      settImage.setMZIon(new MZIon("mz=" + window.round(selectedVsMiddleMZ, 3), selectedVsMiddleMZ,
          selectedVsMiddlePM));

      settImage.setSpotsize(Float.valueOf(getTxtSpotsize().getText()));
      settImage.setVelocity(Float.valueOf(getTxtVelocity().getText()));

      // trigger? or continuous data
      settImage.setTriggered(selectedModeMiddle == MODE_IMAGE_DISCON);

      settSplitCon.setSplitMode((XUNIT) getComboSplitUnit().getSelectedItem());
      settSplitCon.setStartX(Module.floatFromTxt(getTxtSplitStartX()));
      if (settSplitCon.getSplitMode() == XUNIT.DP)
        settSplitCon.setSplitAfterDP(Module.intFromTxt(getTxtTimePerLine()));
      else
        settSplitCon.setSplitAfterX(Module.floatFromTxt(getTxtTimePerLine()));
    } catch (Exception ex) {
      logger.error("", ex);
      JOptionPane.showMessageDialog(window.getFrame(),
          "Wrong input in text fields for image settings. " + ex.getMessage(), "ERROR",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  // set scrolling enabled for selected one only
  public void setSelectedView(int selectedView) {
    this.selectedView = selectedView;
    if (chartBottomSpec != null)
      chartBottomSpec.setMouseZoomable(selectedView == VIEW_BOTTOM_SPECTRUM);
    if (chartMiddleChrom != null)
      chartMiddleChrom.setMouseZoomable(selectedView == VIEW_MIDDLE_IMAGECHROM);
    if (chartMiddleImage != null)
      chartMiddleImage.setMouseZoomable(selectedView == VIEW_MIDDLE_IMAGECHROM);
    if (chartTopChrom != null)
      chartTopChrom.setMouseZoomable(selectedView == VIEW_TOP_CHROM);
  }

  // #############################################################################
  // Getters and Setters
  public int getSelectedView() {
    return selectedView;
  }

  public double getStandardVsPM() {
    return standardVsPM;
  }


  public void setStandardVsPM(double standardVsPM) {
    this.standardVsPM = standardVsPM;
  }


  public double getSelectedVsMZ() {
    return selectedVsMiddleMZ;
  }


  public void setSelectedVsMZ(double selectedVsMZ) {
    this.selectedVsMiddleMZ = selectedVsMZ;
  }


  public double getSelectedVsPM() {
    return selectedVsMiddlePM;
  }


  public void setSelectedVsPM(double selectedVsPM) {
    this.selectedVsMiddlePM = selectedVsPM;
  }


  public double[] getSelectedVsRetentionTime() {
    return selectedVsRetentionTime;
  }


  public void setSelectedVsRetentionTime(double rt, double rt2) {
    setSpecSelectionMode(SPECTRUM_SELECTION_MODE_RT);
    this.selectedVsRetentionTime = new double[2];
    selectedVsRetentionTime[0] = rt;
    selectedVsRetentionTime[1] = rt2;
    getLbRT().setVisible(true);
    getLbRT().setText("rt=" + window.round(rt, 4));
    getLbRT2().setVisible(rt2 != -1);
    getLbRT2().setText("-" + window.round(rt2, 4));
    //
    getLbX().setVisible(false);
    getLbY().setVisible(false);
    getLbX2().setVisible(false);
    getLbY2().setVisible(false);
  }


  public void setSelectedXY(double x, double y, double x2, double y2) {
    setSpecSelectionMode(SPECTRUM_SELECTION_MODE_XY);
    double w = Math.abs(x - x2);
    double h = Math.abs(y - y2);
    Rectangle2D rect = new Rectangle2D.Double(x, y, w, h);
    selectedImageRectForSpec = rect;

    getLbRT().setVisible(false);
    getLbRT2().setVisible(false);

    getLbX().setVisible(true);
    getLbY().setVisible(true);
    getLbX().setText("x=" + window.round(x, 1));
    getLbY().setText("y=" + window.round(y, 1));

    getLbX2().setVisible(x2 != -1);
    getLbY2().setVisible(y2 != -1);
    getLbX2().setText("-" + window.round(x2, 1));
    getLbY2().setText("-" + window.round(y2, 1));
  }



  // ##########################################################################################
  /*
   * KeyListener: a. DELETE: delete row in peaktable b. ARROWS: move selected ChartPanel c. 1/2/3:
   * select chartView d. F5: update charts (image update ..)
   */
  protected int lastSelectedView = -1;
  protected long lastTimeKeyListening = -1;
  protected long keyTimeDelete = -1, keyTimeF5 = -1, keyTime1 = -1, keyTime2 = -2, keyTime3 = -3;
  protected long keyTimeArrowLeft = -1, keyTimeArrowDown = -1, keyTimeArrowRight = -1,
      keyTimeArrowUp = -1;

  private class KeyAction extends AbstractAction {
    public KeyAction(String actionCommand) {
      putValue(ACTION_COMMAND_KEY, actionCommand);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvt) {
      // TODO TEST Keys
      long currentTime = System.currentTimeMillis();
      if (!actionEvt.getActionCommand().endsWith("RELEASED"))
        System.out.println(actionEvt.getActionCommand() + " KeyPressed");

      switch (actionEvt.getActionCommand()) {
        case VK_DELETE:
          if (selectedModeTop == MODE_PEAK_LIST) {
            getTableMzPeak().removeSelectedRows();
          }
          break;
        case VK_F5:
          if (keyTimeF5 < currentTime - 1500) {
            keyTimeF5 = System.currentTimeMillis();
            renewAll();
          }
          break;

        // move chartPanel
        case VK_LEFT:
          // save time for acceleration in scroll speed
          // move chart left
          ChartPanel pn = getSelectedChartPanel();
          if (pn != null) {
            if (keyTimeArrowLeft != -1) {
              double acceleration = ((currentTime - keyTimeArrowLeft) / 1000.0);
              double timeSinceLastOffset = ((currentTime - lastTimeKeyListening) / 1000.0);
              double offsetpersecond = -0.10 * acceleration * acceleration * timeSinceLastOffset;
              ChartLogics.offsetDomainAxis(pn, offsetpersecond, true);
              System.out.println("OFFSET% " + offsetpersecond + "  Acc=" + acceleration);
            } else {
              keyTimeArrowLeft = currentTime;
              ChartLogics.offsetDomainAxis(pn, -0.05, true);
            }
          }
          break;
        case VK_UP:
          // zoom in range axis
          ChartPanel pnUp = getSelectedChartPanel();
          if (pnUp != null) {
            if (keyTimeArrowUp != -1) {
              double acceleration = ((currentTime - keyTimeArrowUp) / 1000.0);
              double timeSinceLastZoom = ((currentTime - lastTimeKeyListening) / 1000.0);
              double zoompersecond = -0.25 * acceleration * acceleration * timeSinceLastZoom;
              ChartLogics.zoomRangeAxis(pnUp, zoompersecond, true);
              System.out.println("Zoom% " + zoompersecond + "  Acc=" + acceleration);
            } else {
              keyTimeArrowUp = currentTime;
              ChartLogics.zoomRangeAxis(pnUp, -0.05, true);
            }
          }
          break;
        case VK_RIGHT:
          // save time for acceleration in scroll speed
          // move chart right
          ChartPanel pn2 = getSelectedChartPanel();
          if (pn2 != null) {
            if (keyTimeArrowRight != -1) {
              double acceleration = ((currentTime - keyTimeArrowRight) / 1000.0);
              double timeSinceLastOffset = ((currentTime - lastTimeKeyListening) / 1000.0);
              double offsetpersecond = 0.10 * acceleration * acceleration * timeSinceLastOffset;
              ChartLogics.offsetDomainAxis(pn2, offsetpersecond, true);
              System.out.println("OFFSET% " + offsetpersecond + "  Acc=" + acceleration);
            } else {
              keyTimeArrowRight = currentTime;
              ChartLogics.offsetDomainAxis(pn2, 0.05, true);
            }
          }
          break;
        case VK_DOWN:
          // zoom out range axis
          ChartPanel pnDown = getSelectedChartPanel();
          if (pnDown != null) {
            if (keyTimeArrowDown != -1) {
              double acceleration = ((currentTime - keyTimeArrowDown) / 1000.0);
              double timeSinceLastZoom = ((currentTime - lastTimeKeyListening) / 1000.0);
              double zoompersecond = 0.25 * acceleration * acceleration * timeSinceLastZoom;
              ChartLogics.zoomRangeAxis(pnDown, zoompersecond, true);
              System.out.println("Zoom% " + zoompersecond + "  Acc=" + acceleration);
            } else {
              keyTimeArrowDown = currentTime;
              ChartLogics.zoomRangeAxis(pnDown, 0.05, true);
            }
          }
          break;

        // select view #
        case VK_1:
          // TOP View
          if (selectedView != VIEW_TOP_CHROM) {
            lastSelectedView = selectedView;
            // set to top view
            setSelectedViewAndRadioButton(VIEW_TOP_CHROM);
            // save time for keyRelease action
            if (keyTime1 == -1)
              keyTime1 = System.currentTimeMillis();
          }
          break;
        case VK_2:
          // Middle View
          if (selectedView != VIEW_MIDDLE_IMAGECHROM) {
            lastSelectedView = selectedView;
            // set to top view
            getPnMiddleImageChrom().getRbSelectedChartView().setSelected(true);
            // save time for keyRelease action
            if (keyTime2 == -1)
              keyTime2 = System.currentTimeMillis();
          }
          break;
        case VK_3:
          // Bottom View
          if (selectedView != VIEW_BOTTOM_SPECTRUM) {
            lastSelectedView = selectedView;
            // set to top view
            getPnBottomSpec().getRbSelectedChartView().setSelected(true);
            // save time for keyRelease action
            if (keyTime3 == -1)
              keyTime3 = System.currentTimeMillis();
          }
          break;
        default:
          keyReleased(actionEvt);
          break;
      }
      lastTimeKeyListening = currentTime;
    }

    /*
     * reset KeyBindings times
     */
    public void keyReleased(ActionEvent actionEvt) {
      long currentTime = System.currentTimeMillis();
      System.out.println(actionEvt.getActionCommand() + " KeyReleased");

      switch (actionEvt.getActionCommand()) {
        case VK_DELETE_RELEASED:
          break;
        case VK_F5_RELEASED:
          break;

        // move chartPanel
        case VK_LEFT_RELEASED:
          keyTimeArrowLeft = -1;
          break;
        case VK_UP_RELEASED:
          keyTimeArrowUp = -1;
          break;
        case VK_RIGHT_RELEASED:
          keyTimeArrowRight = -1;
          break;
        case VK_DOWN_RELEASED:
          keyTimeArrowDown = -1;
          break;

        // select the last selected view if user has hold a number button
        case VK_1_RELEASED:
          if (keyTime1 < currentTime - 750)
            setSelectedViewAndRadioButton(lastSelectedView);
          keyTime1 = -1;
          break;
        case VK_2_RELEASED:
          if (keyTime2 < currentTime - 750)
            setSelectedViewAndRadioButton(lastSelectedView);
          keyTime2 = -1;
          break;
        case VK_3_RELEASED:
          if (keyTime3 < currentTime - 750)
            setSelectedViewAndRadioButton(lastSelectedView);
          keyTime3 = -1;
          break;
      }
    }
  }



  /**
   * sets the radiobutton of the given view to selected=true (code sided view change)
   */
  public void setSelectedViewAndRadioButton(int view) {
    switch (view) {
      case VIEW_TOP_CHROM:
        getPnTopEICTIC().getRbSelectedChartView().setSelected(true);
        break;
      case VIEW_MIDDLE_IMAGECHROM:
        getPnMiddleImageChrom().getRbSelectedChartView().setSelected(true);
        break;
      case VIEW_BOTTOM_SPECTRUM:
        getPnBottomSpec().getRbSelectedChartView().setSelected(true);
        break;
    }
  }

  /**
   * Returns the chart panel in the selected view or null
   * 
   * @return the chart panel in the selected view
   */
  private ChartPanel getSelectedChartPanel() {
    switch (selectedView) {
      case VIEW_TOP_CHROM:
        if (selectedModeTop == MODE_PEAK_LIST)
          return null;
        else
          return chartTopChrom;
      case VIEW_MIDDLE_IMAGECHROM:
        if (selectedModeMiddle == MODE_IMAGE_CON || selectedModeMiddle == MODE_IMAGE_DISCON
            || selectedModeMiddle == MODE_IMAGE)
          return chartMiddleImage;
        else
          return chartMiddleChrom;
      case VIEW_BOTTOM_SPECTRUM:
        return chartBottomSpec;
    }
    return null;
  }
  // END for KeyListening
  // ##########################################################################################

  // ##########################################################################################
  // Update Runner for Image updating after change in txt fields
  protected long startTime = -1;
  protected Thread threadImageUpdater;
  private JToolBar toolBar;
  private final ButtonGroup btngroup_TOP = new ButtonGroup();
  private JToolBar toolBar_1;
  private JToggleButton btnMiddleTIC;
  private JToggleButton btnMiddleEIC;
  private JToggleButton btnMiddleImageDisc;
  private JToggleButton btnMiddleImageCon;
  private JToggleButton btnMiddleImage;
  private final ButtonGroup btnGroup_middle = new ButtonGroup();
  private JToolBar toolBar_2;
  private JToggleButton tglbtnOri;
  private JToggleButton btnTopSplitchange;
  private JToggleButton btnTopListPeaks;
  private MenuChartActions menuMiddleChartActions;
  private MenuChartActions menuTopChartActions;
  private MenuTableActions menuTopTableActions;
  private MenuChartActions menuBottomChartActions;
  private JPanel panel_1;
  private JPanel pnWestSettings;
  private ModuleListWithOptions pnPeakList;
  private JButton btnApplyPeakList;
  private JLabel lbSelectedPeakList;
  private JTextField txtImgGroupID;
  private JLabel lblId;
  private JComboBox comboSplitUnit;
  private JLabel lblStartX;
  private JTextField txtSplitStartX;
  private JButton button;
  private JTextField txtAddSplit;
  private JButton button_1;
  private JPanel panel;
  private JLabel lblProject;
  private JTextField txtProject;

  public void startImageUpdater() {
    if (startTime == -1) {
      startTime = System.currentTimeMillis();
      threadImageUpdater = new Thread(this);
      threadImageUpdater.start();
    }
  }

  @Override
  public void run() {
    try {
      threadImageUpdater.sleep(1500);
      startTime = -1;
      renewMiddleImageChrom(selectedVsMiddleMZ, selectedVsMiddlePM);
    } catch (Exception ex) {
      logger.error("", ex);
    }
  }


  public void setCurrentRawFile(RawDataFile raw) {
    isImagingRawData = raw instanceof ImagingRawData;

    btnMiddleImageCon.setVisible(!isImagingRawData);
    btnMiddleImageDisc.setVisible(!isImagingRawData);
    btnMiddleImage.setVisible(isImagingRawData);

  }

  // ########################################################################################
  // GETTERS AND SETTERS
  public PnChartWithSettings getPnMiddleImageChrom() {
    return pnMZImageChrom;
  }

  public PnChartWithSettings getPnBottomSpec() {
    return pnSpec;
  }

  public JLabel getLbRT() {
    return lbRT;
  }

  public JLabel getLbPMMiddle() {
    return lbPMMiddle;
  }

  public JLabel getLbMZMiddle() {
    return lbMZMiddle;
  }

  public ChartPanel getChartChrom() {
    return chartMiddleChrom;
  }

  public ChartPanel getChartSpec() {
    return chartBottomSpec;
  }

  public JLabel getLbX() {
    return lbX;
  }

  public JLabel getLbY() {
    return lbY;
  }

  public JLabel getLbDisconCon() {
    return lbDisconCon;
  }

  public JPanel getPnImgCon() {
    return pnImgCon;
  }

  public JTextField getTxtVelocity() {
    return txtVelocity;
  }

  public JTextField getTxtSpotsize() {
    return txtSpotsize;
  }

  public JTextField getTxtTimePerLine() {
    return txtTimePerLine;
  }

  public PnChartWithSettings getPnTopEICTIC() {
    return pnTopEICTIC;
  }

  public Module getPnWestImageSettings() {
    return pnWestImageSettings;
  }

  public JLabel getLbMZTop() {
    return lbMZTop;
  }

  public JLabel getLbPMTop() {
    return lbPMTop;
  }

  public JLabel getLbRT2() {
    return lbRT2;
  }

  public JLabel getLbX2() {
    return lbX2;
  }

  public JLabel getLbY2() {
    return lbY2;
  }

  public JSplitPane getSplitMZvsSpec() {
    return splitMZvsSpec;
  }

  public JSplitPane getSplitCenterThree() {
    return splitCenterThree;
  }

  public PnTableMZPick getTableMzPeak() {
    return tableMzPeak;
  }

  public MenuChartActions getMenuTopChartActions() {
    return menuTopChartActions;
  }

  public MenuTableActions getMenuTopTableActions() {
    return menuTopTableActions;
  }

  public MenuChartActions getMenuMiddleChartActions() {
    return menuMiddleChartActions;
  }

  public MenuChartActions getMenuBottomChartActions() {
    return menuBottomChartActions;
  }

  public int getSpecSelectionMode() {
    return specSelectionMode;
  }

  public void setSpecSelectionMode(int specSelectionMode) {
    this.specSelectionMode = specSelectionMode;
  }

  public ModuleListWithOptions getPnPeakList() {
    return pnPeakList;
  }

  public JLabel getLbSelectedPeakList() {
    return lbSelectedPeakList;
  }

  public JPanel getMenuTop() {
    return menuTop;
  }

  public JTextField getTxtImgGroupID() {
    return txtImgGroupID;
  }

  public JComboBox getComboSplitUnit() {
    return comboSplitUnit;
  }

  public JTextField getTxtSplitStartX() {
    return txtSplitStartX;
  }

  public JTextField getTxtProject() {
    return txtProject;
  }
}
