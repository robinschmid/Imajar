package net.rs.lamsi.multiimager.Frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.jfree.chart.plot.XYPlot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.ImageGroupMD;
import net.rs.lamsi.general.datamodel.image.ImageMerge;
import net.rs.lamsi.general.datamodel.image.ImageOverlay;
import net.rs.lamsi.general.datamodel.image.ImagingProject;
import net.rs.lamsi.general.datamodel.image.SingleParticleImage;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.datamodel.image.interf.DataCollectable2D;
import net.rs.lamsi.general.dialogs.GraphicsExportDialog;
import net.rs.lamsi.general.dialogs.HeatmapGraphicsExportDialog;
import net.rs.lamsi.general.framework.listener.ColorChangedListener;
import net.rs.lamsi.general.framework.modules.MainSettingsModuleContainer;
import net.rs.lamsi.general.framework.modules.Module;
import net.rs.lamsi.general.framework.modules.ModuleTreeWithOptions;
import net.rs.lamsi.general.framework.modules.listeners.HideShowChangedListener;
import net.rs.lamsi.general.framework.modules.tree.IconNodeRenderer;
import net.rs.lamsi.general.heatmap.Heatmap;
import net.rs.lamsi.general.myfreechart.ChartLogics;
import net.rs.lamsi.general.myfreechart.listener.AxisRangeChangedListener;
import net.rs.lamsi.general.myfreechart.listener.history.ZoomHistory;
import net.rs.lamsi.general.myfreechart.swing.EChartPanel;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.general.settings.SettingsHolder;
import net.rs.lamsi.general.settings.listener.SettingsChangedListener;
import net.rs.lamsi.general.settings.preferences.SettingsGeneralPreferences;
import net.rs.lamsi.multiimager.FrameModules.ModuleImage2D;
import net.rs.lamsi.multiimager.FrameModules.ModuleImageMerge;
import net.rs.lamsi.multiimager.FrameModules.ModuleImageOverlay;
import net.rs.lamsi.multiimager.FrameModules.ModuleSingleParticleImage;
import net.rs.lamsi.multiimager.FrameModules.sub.ModuleBackgroundImg;
import net.rs.lamsi.multiimager.FrameModules.sub.ModuleZoom;
import net.rs.lamsi.multiimager.FrameModules.sub.dataoperations.ModuleSPImage;
import net.rs.lamsi.multiimager.FrameModules.sub.dataoperations.ModuleSelectExcludeData;
import net.rs.lamsi.multiimager.FrameModules.sub.theme.ModuleThemes;
import net.rs.lamsi.multiimager.Frames.dialogs.CroppingDialog;
import net.rs.lamsi.multiimager.Frames.dialogs.DialogChooseProject;
import net.rs.lamsi.multiimager.Frames.dialogs.DialogDataSaver;
import net.rs.lamsi.multiimager.Frames.dialogs.DialogPreferences;
import net.rs.lamsi.multiimager.Frames.dialogs.ImageSetupDialog;
import net.rs.lamsi.multiimager.Frames.dialogs.ImportDataDialog;
import net.rs.lamsi.multiimager.Frames.multiimageframe.MultiImageFrame;
import net.rs.lamsi.utils.DialogLoggerUtil;
import net.rs.lamsi.utils.FileAndPathUtil;
import net.rs.lamsi.utils.WindowStyleUtil;
import net.rs.lamsi.utils.useful.DebugStopWatch;
import net.rs.lamsi.utils.useful.dialogs.ProgressDialog;
import net.sf.mzmine.desktop.ImportantWindow;

// net.rs.lamsi.multiimager.Frames.ImageEditorWindow
public class ImageEditorWindow extends JFrame implements Runnable, ImportantWindow {
  private static final Logger logger = LoggerFactory.getLogger(ImageEditorWindow.class);

  private static SimpleAttributeSet styleWarning, styleMessage, styleImportant, styleError,
      styleDebug;
  private static boolean isLogging = true;
  //
  public static final int VIEW_IMAGING_ANALYSIS = 0, VIEW_DIRECT_IMAGING_ANALYSIS = 1;
  // My STUFF
  private static ImageEditorWindow thisFrame;
  private ImageLogicRunner logicRunner;
  private ImportDataDialog importDataDialog;
  private DialogPreferences preferencesDialog;
  private ImageSetupDialog imageSetupDialog;

  // save all frames for updating style etc
  private List<Component> listFrames = new ArrayList<Component>();

  // MODULES
  private ModuleImage2D modImage2D;
  private ModuleImageOverlay modImageOverlay;
  private ModuleSingleParticleImage modSPImage;
  private ModuleImageMerge modImageMerge;

  /**
   * the module container that is active (imageoverlay or image2d) first set the image or
   * imageoverlay and this will be set
   */
  private MainSettingsModuleContainer activeModuleContainer;

  // Autoupdate after a given time
  private final long AUTO_UPDATE_TIME = 1500;
  private long lastAutoUpdateTime = -1;
  private boolean isAutoUpdateStarted = false;

  // Autoupdate of all modules
  private ActionListener autoActionL;
  private ChangeListener autoChangeL;
  private DocumentListener autoDocumentL;
  private ColorChangedListener autoColorChangedL;
  private ItemListener autoItemL;

  // auto repainter (no new creation of heatmaps
  private ActionListener autoRepActionL;
  private ChangeListener autoRepChangeL;
  private DocumentListener autoRepDocumentL;
  private ColorChangedListener autoRepColorChangedL;
  private ItemListener autoRepItemL;

  // only repaint and not create new heatmaps
  private boolean autoRepaintOnly = true;

  private int currentView = VIEW_IMAGING_ANALYSIS;

  // Image2d history for import/export
  private JMenuItem[] menuItemHistoryImg2D = null;

  // AUTOGEN
  //
  private ModuleTreeWithOptions<Collectable2D> moduleTreeImages;

  // menu
  private JButton btnSingleParticle;

  private JPanel pnCenterImageView;
  private JPanel east;
  private final ButtonGroup buttonGroup = new ButtonGroup();
  private JRadioButtonMenuItem menuRbImagingAnalysis;
  private JRadioButtonMenuItem menuRbDirectImagingAnalysis;
  private final ButtonGroup buttonGroup_1 = new ButtonGroup();
  private JSplitPane splitPane;
  private JPanel west;
  private JPanel pnSouth;
  private JPanel pnDirectIANorthDisplay;
  private JTextField txtDirectTime;
  private JTextField txtDirectFileFilter;
  private JTextField txtDirectStartsWith;
  private JCheckBox cbSumTasks;
  private JCheckBoxMenuItem cbDebug, cbMenuNorth;
  private JCheckBoxMenuItem cbCreateImageIcons;
  private JTextField txtDirectAutoScale;
  private JCheckBox cbDirectAutoScale;
  private JPanel pnNorth;
  private JPanel pnNorthMenu;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    // In the beginning, set the default locale to English, to avoid
    // problems with conversion of numbers etc. (e.g. decimal separator may
    // be . or , depending on the locale)
    Locale.setDefault(new Locale("en", "US"));


    WindowStyleUtil.changeWindowStyle(null, WindowStyleUtil.STYLE_SYSTEM);
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          ImageEditorWindow window = new ImageEditorWindow();
          window.setVisible(true);
        } catch (Exception e) {
          logger.error("", e);
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public ImageEditorWindow() {
    logger.debug("Creates a new window");
    logger.info("Creates a new window");
    logger.error("Creates a new window");
    setTitle("ImageEditor");
    thisFrame = this;
    initialize();
    // init automatic update function
    initAutoUpdater();
    // plot export dialog
    GraphicsExportDialog grpExportDialog = GraphicsExportDialog.createInstance();
    WindowStyleUtil.changeWindowStyle(grpExportDialog, WindowStyleUtil.STYLE_SYSTEM);
    HeatmapGraphicsExportDialog grpHeatmapExportDialog =
        HeatmapGraphicsExportDialog.createInstance();
    WindowStyleUtil.changeWindowStyle(grpHeatmapExportDialog, WindowStyleUtil.STYLE_SYSTEM);
    grpExportDialog.setVisible(false);
    listFrames.add(grpExportDialog);
    // give window to LogicPanel
    logicRunner = new ImageLogicRunner(this);
    // progress dialog
    ProgressDialog.initDialog(this);
    // create preferences dialog
    preferencesDialog = new DialogPreferences(this);
    WindowStyleUtil.changeWindowStyle(preferencesDialog, WindowStyleUtil.STYLE_SYSTEM);
    preferencesDialog.setVisible(false);
    listFrames.add(preferencesDialog);


    imageSetupDialog = new ImageSetupDialog();
    // init data import
    importDataDialog = new ImportDataDialog(logicRunner);
    WindowStyleUtil.changeWindowStyle(importDataDialog, WindowStyleUtil.STYLE_SYSTEM);
    importDataDialog.setVisible(false);
    listFrames.add(importDataDialog);

    DialogDataSaver exportDataDialog = DialogDataSaver.createInst(SettingsHolder.getSettings());
    WindowStyleUtil.changeWindowStyle(exportDataDialog, WindowStyleUtil.STYLE_SYSTEM);
    exportDataDialog.setVisible(false);
    listFrames.add(exportDataDialog);

    JMenuBar menuBar = new JMenuBar();
    setJMenuBar(menuBar);

    final JMenu mnFile = new JMenu("File");
    menuBar.add(mnFile);

    // open save project
    JMenuItem mntmOpenProject = new JMenuItem("Open project");
    mntmOpenProject.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        // load in logicRunner
        logicRunner.loadProjectFromFile();
      }
    });
    mntmOpenProject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
    mnFile.add(mntmOpenProject);

    JMenuItem mntmSaveProject = new JMenuItem("Save project file");
    mntmSaveProject.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        // save in logicRunner
        logicRunner.saveProjectToFile();
      }
    });
    mntmSaveProject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
    mnFile.add(mntmSaveProject);

    // open save image2d
    JMenuItem mntmOpenImage = new JMenuItem("Open image");
    mntmOpenImage.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        // load in logicRunner
        logicRunner.loadImage2DFromFile();
      }
    });
    mntmOpenImage.setAccelerator(
        KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK + InputEvent.SHIFT_MASK));
    mnFile.add(mntmOpenImage);

    JMenuItem mntmSaveImage = new JMenuItem("Save image file");
    mntmSaveImage.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        // save in logicRunner
        logicRunner.saveImage2DToFile();
      }
    });
    mntmSaveImage.setAccelerator(
        KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK + InputEvent.SHIFT_MASK));
    mnFile.add(mntmSaveImage);

    JSeparator separator = new JSeparator();
    mnFile.add(separator);

    JMenuItem mntmSaveDataAs = new JMenuItem("Export data");
    mntmSaveDataAs.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // open data export dialog
        Collectable2D img = logicRunner.getSelectedImage();
        if (img instanceof DataCollectable2D)
          DialogDataSaver.startDialogWith(logicRunner.getListDataCollectable2DOnly(),
              (DataCollectable2D) img);
        else
          logger.error(
              "Select a standard image for data export (e.g. image overlay currently selected");
      }
    });
    mntmSaveDataAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK));
    mnFile.add(mntmSaveDataAs);

    JMenuItem mntmExportGraphics = new JMenuItem("Export graphics");
    mntmExportGraphics.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // open graph export dialog
        logicRunner.openExportHeatGraphicsDialog();
      }
    });

    JMenuItem mntmExportDataReport = new JMenuItem("Export data report");
    mntmExportDataReport.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        logicRunner.exportDataReport();
      }
    });
    mnFile.add(mntmExportDataReport);
    mntmExportGraphics.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_MASK));
    mnFile.add(mntmExportGraphics);

    JSeparator separator_1 = new JSeparator();
    mnFile.add(separator_1);

    JMenuItem mnImportData = new JMenuItem("Import data");
    mnImportData.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // opens the import data frame
        importDataDialog.setVisible(true);
      }
    });
    mnFile.add(mnImportData);

    // ######################################################################
    // add image2D history TODO
    SettingsGeneralPreferences pref = SettingsHolder.getSettings().getSetGeneralPreferences();
    pref.addChangeListener(new SettingsChangedListener() {
      @Override
      public void settingsChanged(Settings settings) {
        // remove old menu items
        if (menuItemHistoryImg2D != null)
          for (JMenuItem mi : menuItemHistoryImg2D)
            mnFile.remove(mi);
        // create new
        Vector<File> files =
            SettingsHolder.getSettings().getSetGeneralPreferences().getImg2DHistory();
        menuItemHistoryImg2D = new JMenuItem[files.size()];

        // add new
        if (menuItemHistoryImg2D != null) {
          for (int i = 0; i < files.size(); i++) {
            final File f = files.get(i);
            JMenuItem mnImportData = new JMenuItem(FileAndPathUtil.eraseFormat(f.getName()) + "; "
                + f.getParentFile().getAbsolutePath());
            mnImportData.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                // choose project dialog
                ImagingProject project = null;
                if (!SettingsHolder.getSettings().getSetGeneralPreferences().getFileTFProject()
                    .accept(f))
                  project = DialogChooseProject.choose(getModuleTreeImages().getSelectedProject(),
                      getModuleTreeImages());

                // import file as image2d
                logicRunner.loadImage2DAndProjectFromFile(f, project);
              }
            });
            mnImportData.setPreferredSize(
                new Dimension(280, (int) mnImportData.getPreferredSize().getHeight()));
            mnFile.add(mnImportData);
            menuItemHistoryImg2D[i] = mnImportData;
          }
        }
      }
    });
    pref.fireChangeEvent();
    //

    JMenu mnAction = new JMenu("Action");
    menuBar.add(mnAction);

    JMenuItem btnRecalcStats = new JMenuItem("Recalc all ROI statistics");
    btnRecalcStats.addActionListener(e -> logicRunner.recalculateAllStatistics());
    mnAction.add(btnRecalcStats);

    JMenuItem btnCrop = new JMenuItem("Crop images");
    btnCrop.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // Open Dialog
        Collectable2D img = getLogicRunner().getSelectedImage();
        if (img != null && Image2D.class.isInstance(img) && img.getImageGroup() != null) {
          CroppingDialog d = new CroppingDialog();
          d.startDialog(img.getImageGroup(), (Image2D) img);
        }
      }
    });
    mnAction.add(btnCrop);

    JMenuItem mntmCombineImages = new JMenuItem("Combine images");
    mntmCombineImages.setToolTipText(
        "Combine multiple images to one large image. Groups are selected. All images with the same titles are combined.");
    mntmCombineImages.addActionListener(e -> {
      // Open Dialog
      try {
        logicRunner.combineImages();
      } catch (Exception e1) {
        DialogLoggerUtil.showErrorDialog(thisFrame, "Groups were not compatible", e1);
        logger.error("", e1);
      }
    });
    mnAction.add(mntmCombineImages);

    JMenuItem mntmImagesToOneGroup = new JMenuItem("Images to one group");
    mntmImagesToOneGroup.setToolTipText("Combined group of images with the same data dimensions");
    mntmImagesToOneGroup.addActionListener(e -> logicRunner.imagesToOneGroup());
    mnAction.add(mntmImagesToOneGroup);


    JMenuItem btnCreateOverlay = new JMenuItem("Create overlay");
    btnCreateOverlay.addActionListener(e -> logicRunner.createOverlay());
    mnAction.add(btnCreateOverlay);

    JMenuItem btnCreateImageMerge = new JMenuItem("Create image merge");
    btnCreateImageMerge.addActionListener(e -> logicRunner.createImageMerge());
    mnAction.add(btnCreateImageMerge);

    JMenuItem btnCreateSP = new JMenuItem("Create single particle image");
    btnCreateSP.addActionListener(e -> logicRunner.createSingleParticleImage());
    mnAction.add(btnCreateSP);

    JMenuItem menuItem = new JMenuItem("Resplit data lines");
    menuItem.addActionListener(e -> logicRunner.convertImageGroupToContinuousData());
    mnAction.add(menuItem);

    JMenuItem btnImportMicroscopic = new JMenuItem("Add down sampled microscopic image");
    btnImportMicroscopic.addActionListener(e -> logicRunner.importMicroscopicImageDownSampled());
    mnAction.add(btnImportMicroscopic);

    JMenuItem btnImportMicroscopic2 = new JMenuItem("Add microscopic image to background");
    btnImportMicroscopic2.addActionListener(e -> {
      // Open Dialog
      if (activeModuleContainer != null) {
        ModuleBackgroundImg mod =
            (ModuleBackgroundImg) activeModuleContainer.getModuleByClass(ModuleBackgroundImg.class);
        if (mod != null) {
          mod.getBtnAddImage().doClick();
        }
      }
    });
    mnAction.add(btnImportMicroscopic2);

    JMenu mnView = new JMenu("View");
    menuBar.add(mnView);

    menuRbImagingAnalysis = new JRadioButtonMenuItem("Imaging analysis");
    buttonGroup_1.add(menuRbImagingAnalysis);
    menuRbImagingAnalysis.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        JRadioButtonMenuItem rb = (JRadioButtonMenuItem) e.getSource();
        if (rb.isSelected())
          setSelectedView(VIEW_IMAGING_ANALYSIS);
      }
    });
    menuRbImagingAnalysis.setSelected(true);
    mnView.add(menuRbImagingAnalysis);

    menuRbDirectImagingAnalysis = new JRadioButtonMenuItem("Direct imaging analysis (DIA)");
    buttonGroup_1.add(menuRbDirectImagingAnalysis);
    menuRbDirectImagingAnalysis.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        JRadioButtonMenuItem rb = (JRadioButtonMenuItem) e.getSource();
        if (rb.isSelected())
          setSelectedView(VIEW_DIRECT_IMAGING_ANALYSIS);
      }
    });
    mnView.add(menuRbDirectImagingAnalysis);

    JSeparator separator_3 = new JSeparator();
    mnView.add(separator_3);

    JMenuItem mntmMultiImageExplorer = new JMenuItem("Multi image explorer");
    mntmMultiImageExplorer.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // select image list TODO
        TreePath path = DialogLoggerUtil.showTreeDialogAndChoose(thisFrame,
            getModuleTreeImages().getRoot(), TreeSelectionModel.SINGLE_TREE_SELECTION,
            getModuleTreeImages().getTree().getSelectionPaths(), "Single selection",
            "One image or group")[0];
        // show dialog with mutliple image view
        if (path != null) {
          // TODO correct?
          ImageGroupMD img = getModuleTreeImages().getImageGroup(path);
          if (img != null) {
            MultiImageFrame frame = new MultiImageFrame();
            frame.init(img);
          }
        }
      }
    });
    mnView.add(mntmMultiImageExplorer);

    JMenuItem mntmSelectData = new JMenuItem("Select data");
    mntmSelectData.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        openDataSelectionDialog();
      }
    });
    mnView.add(mntmSelectData);

    JMenu mnWindow = new JMenu("Window");
    menuBar.add(mnWindow);

    JMenuItem btnOpenPrefDialog = new JMenuItem("General preferences");
    btnOpenPrefDialog.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        // opens the preferences dialog
        preferencesDialog.setVisible(true);
      }
    });
    mnWindow.add(btnOpenPrefDialog);

    mnWindow.add(new JSeparator());

    JRadioButtonMenuItem rdbtnmntmSystemStyle = new JRadioButtonMenuItem("System style");
    rdbtnmntmSystemStyle.setSelected(true);
    rdbtnmntmSystemStyle.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        JRadioButtonMenuItem rb = (JRadioButtonMenuItem) e.getSource();
        if (rb.isSelected())
          changeWindowStyle(WindowStyleUtil.STYLE_SYSTEM);
      }
    });
    buttonGroup.add(rdbtnmntmSystemStyle);
    mnWindow.add(rdbtnmntmSystemStyle);

    JRadioButtonMenuItem rdbtnmntmJavaStyle = new JRadioButtonMenuItem("Java style");
    rdbtnmntmJavaStyle.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        JRadioButtonMenuItem rb = (JRadioButtonMenuItem) e.getSource();
        if (rb.isSelected())
          changeWindowStyle(WindowStyleUtil.STYLE_JAVA);
      }
    });
    buttonGroup.add(rdbtnmntmJavaStyle);
    mnWindow.add(rdbtnmntmJavaStyle);

    JSeparator separator_2 = new JSeparator();
    mnWindow.add(separator_2);

    cbCreateImageIcons = new JCheckBoxMenuItem("Create image icons");
    cbCreateImageIcons.setToolTipText("Creating image icons for the tree view uses RAM");
    cbCreateImageIcons.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        SettingsHolder.getSettings().getSetGeneralPreferences()
            .setGeneratesIcons(cbCreateImageIcons.isSelected());
      }
    });
    cbCreateImageIcons.setSelected(true);
    mnWindow.add(cbCreateImageIcons);

    cbMenuNorth = new JCheckBoxMenuItem("Show quick menu bar");
    cbMenuNorth.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        getPnNorthMenu().setVisible(cbMenuNorth.isSelected());
      }
    });
    cbMenuNorth.setSelected(true);
    mnWindow.add(cbMenuNorth);

    cbDebug = new JCheckBoxMenuItem("Debug");
    cbDebug.setSelected(true);
    mnWindow.add(cbDebug);

    // #####################################################
    // set visible for correct sizes
    this.setVisible(true);
    // collapse all
  }

  /**
   * opens the data selection dialog if possible
   */
  protected void openDataSelectionDialog() {
    if (activeModuleContainer != null) {
      // TODO correct?
      ModuleSelectExcludeData mod = (ModuleSelectExcludeData) activeModuleContainer
          .getModuleByClass(ModuleSelectExcludeData.class);
      if (mod != null) {
        mod.getBtnOpenSelectData().doClick();
      }
    }
  }

  /**
   * opens the data selection dialog if possible
   */
  protected void openSingleParticleDialog() {
    if (activeModuleContainer != null) {
      // TODO correct?
      ModuleSPImage mod =
          (ModuleSPImage) activeModuleContainer.getModuleByClass(ModuleSPImage.class);
      if (mod != null) {
        mod.openSingleParticleDialog();
      }
    }
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    // init progres sdialog
    ProgressDialog.initDialog(this);
    // init
    this.setBounds(100, 100, 934, 619);

    this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent we) {
        String ObjButtons[] = {"Yes", "No"};
        int PromptResult =
            JOptionPane.showOptionDialog(null, "Let's call it a day. Did you save your work?",
                "Great work mate!", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
                ObjButtons, ObjButtons[1]);
        if (PromptResult == JOptionPane.YES_OPTION) {
          System.exit(0);
        }
      }
    });

    JPanel pnNorthContent = new JPanel();
    pnNorthContent.setLayout(new BorderLayout(0, 0));

    east = new JPanel();
    pnNorthContent.add(east, BorderLayout.EAST);
    east.setLayout(new BorderLayout(0, 0));

    modImage2D = new ModuleImage2D(this);
    modImage2D.setVisible(false);

    modImageOverlay = new ModuleImageOverlay(this);
    modImageOverlay.setVisible(false);

    modImageMerge = new ModuleImageMerge(this);
    modImageMerge.setVisible(false);

    modSPImage = new ModuleSingleParticleImage(this);
    modSPImage.setVisible(false);

    // split pane
    splitPane = new JSplitPane();
    pnNorthContent.add(splitPane, BorderLayout.CENTER);
    splitPane.setOneTouchExpandable(true);
    splitPane.setDividerSize(2);

    west = new JPanel();
    splitPane.setLeftComponent(west);
    west.setLayout(new BorderLayout(0, 0));

    // TODO richtig so?
    moduleTreeImages = new ModuleTreeWithOptions<Collectable2D>("", true);
    moduleTreeImages.getTree().setCellRenderer(new IconNodeRenderer());
    moduleTreeImages.getTree().setRowHeight(0);
    moduleTreeImages.getLbTitle().setText("Tree of images");
    west.add(moduleTreeImages, BorderLayout.CENTER);
    // adding a show/hide changed listener for changing size of jsplitpane
    moduleTreeImages.addHideShowChangedListener(new HideShowChangedListener() {
      @Override
      public void moduleChangedToShown(boolean shown) {
        splitPane.resetToPreferredSizes();
      }
    });

    JButton btnLoad = new JButton("Load");
    btnLoad.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // load in logicRunner
        logicRunner.loadProjectFromFile();
      }
    });
    moduleTreeImages.getPnOptions().add(btnLoad);

    JButton btnSave = new JButton("Save");
    btnSave.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        logicRunner.saveProjectToFile();
      }
    });
    moduleTreeImages.getPnOptions().add(btnSave);

    JButton btnRemove = new JButton("Remove");
    btnRemove.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        moduleTreeImages.removeSelectedObjects();
        logicRunner.setSelectedImageAndShow(null);
      }
    });
    moduleTreeImages.getPnOptions().add(btnRemove);

    JPanel center = new JPanel();
    splitPane.setRightComponent(center);
    center.setLayout(new BorderLayout(0, 0));

    pnCenterImageView = new JPanel();
    center.add(pnCenterImageView, BorderLayout.CENTER);
    pnCenterImageView.setLayout(new BorderLayout(0, 0));

    JPanel panel = new JPanel();
    pnCenterImageView.add(panel, BorderLayout.CENTER);
    GridBagLayout gbl_panel = new GridBagLayout();
    gbl_panel.columnWidths = new int[] {0};
    gbl_panel.rowHeights = new int[] {0};
    gbl_panel.columnWeights = new double[] {Double.MIN_VALUE};
    gbl_panel.rowWeights = new double[] {Double.MIN_VALUE};
    panel.setLayout(gbl_panel);

    pnNorthMenu = new JPanel();
    center.add(pnNorthMenu, BorderLayout.NORTH);
    FlowLayout fl_pnNorthMenu = (FlowLayout) pnNorthMenu.getLayout();
    fl_pnNorthMenu.setVgap(0);
    fl_pnNorthMenu.setHgap(4);
    fl_pnNorthMenu.setAlignment(FlowLayout.LEFT);

    JButton btnROI = new JButton("");
    btnROI.addActionListener(e -> openDataSelectionDialog());
    btnROI.setPreferredSize(new Dimension(31, 31));
    btnROI.setIcon(new ImageIcon(new ImageIcon(Module.class.getResource("/img/btnROI.png"))
        .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
    pnNorthMenu.add(btnROI);


    JButton btnOpenHisto = new JButton("");
    btnOpenHisto.addActionListener(e -> logicRunner.openHistogram(logicRunner.getSelectedImage()));
    btnOpenHisto.setPreferredSize(new Dimension(31, 31));
    btnOpenHisto
        .setIcon(new ImageIcon(new ImageIcon(Module.class.getResource("/img/btn_action_histo.png"))
            .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
    pnNorthMenu.add(btnOpenHisto);


    JButton btnMultiImage = new JButton("");
    btnMultiImage.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // show dialog with mutliple image view
        ImageGroupMD img = getModuleTreeImages().getSelectedGroup();
        if (img != null) {
          MultiImageFrame frame = new MultiImageFrame();
          frame.init(img);
        }
      }
    });

    btnMultiImage.setPreferredSize(new Dimension(31, 31));
    btnMultiImage
        .setIcon(new ImageIcon(new ImageIcon(Module.class.getResource("/img/btn_MultiImage.png"))
            .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
    pnNorthMenu.add(btnMultiImage);


    btnSingleParticle = new JButton("SP");
    btnSingleParticle.addActionListener(e -> openSingleParticleDialog());
    btnSingleParticle.setPreferredSize(new Dimension(31, 31));
    pnNorthMenu.add(btnSingleParticle);


    JPanel north = new JPanel();
    pnNorthContent.add(north, BorderLayout.NORTH);
    north.setLayout(new BorderLayout(0, 0));

    JPanel panel_1 = new JPanel();
    getContentPane().add(panel_1, BorderLayout.CENTER);
    panel_1.setLayout(new BorderLayout(0, 0));
    panel_1.add(pnNorthContent, BorderLayout.CENTER);

    pnNorth = new JPanel();
    getContentPane().add(pnNorth, BorderLayout.NORTH);
    pnNorth.setLayout(new BorderLayout(0, 0));

    pnDirectIANorthDisplay = new JPanel();
    pnNorth.add(pnDirectIANorthDisplay, BorderLayout.SOUTH);
    FlowLayout flowLayout_1 = (FlowLayout) pnDirectIANorthDisplay.getLayout();
    flowLayout_1.setAlignment(FlowLayout.LEFT);

    JToggleButton tglbtnIsrunning = new JToggleButton("isRunning");
    tglbtnIsrunning.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        try {
          long sleep = Long.valueOf(getTxtDirectTime().getText());
          String fileFilter = getTxtDirectFileFilter().getText();
          String startsWith = getTxtDirectStartsWith().getText();
          double scaleFactor = Double.valueOf(getTxtDirectAutoScale().getText());

          logicRunner.getDIARunner().setUp(sleep, fileFilter, startsWith,
              getCbSumTasks().isSelected(), getCbDirectAutoScale().isSelected(), scaleFactor);
          JToggleButton tb = (JToggleButton) e.getSource();
          logicRunner.getDIARunner().setPaused(!tb.isSelected());
        } catch (Exception ex) {
          DialogLoggerUtil.showErrorDialog(thisFrame, "Can't start DIA", ex);
        }
      }
    });

    cbSumTasks = new JCheckBox("sum tasks");
    cbSumTasks.setSelected(true);
    pnDirectIANorthDisplay.add(cbSumTasks);

    cbDirectAutoScale = new JCheckBox("auto scale");
    cbDirectAutoScale.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        try {
          if (logicRunner != null && logicRunner.getDIARunner() != null)
            logicRunner.getDIARunner().setAutoScale(cbDirectAutoScale.isSelected());
        } catch (Exception ex) {
        }
      }
    });
    cbDirectAutoScale.setToolTipText(
        "Enter scale factor to the right. Scaling the maximum intensity of paint scale");
    cbDirectAutoScale.setSelected(true);
    pnDirectIANorthDisplay.add(cbDirectAutoScale);

    txtDirectAutoScale = new JTextField();
    txtDirectAutoScale.getDocument().addDocumentListener(new DocumentListener() {

      @Override
      public void removeUpdate(DocumentEvent e) {

      }

      @Override
      public void insertUpdate(DocumentEvent e) {
        try {
          if (getTxtDirectAutoScale().getText().length() > 0) {
            double scaleFactor = Double.valueOf(getTxtDirectAutoScale().getText());
            logicRunner.getDIARunner().setScaleFactor(scaleFactor);
          }
        } catch (Exception ex) {
        }
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        try {
          if (getTxtDirectAutoScale().getText().length() > 0) {
            double scaleFactor = Double.valueOf(getTxtDirectAutoScale().getText());
            logicRunner.getDIARunner().setScaleFactor(scaleFactor);
          }
        } catch (Exception ex) {
        }
      }
    });
    txtDirectAutoScale
        .setToolTipText("Auto scale factor. (X times maximum intensity of last line)");
    txtDirectAutoScale.setText("2");
    pnDirectIANorthDisplay.add(txtDirectAutoScale);
    txtDirectAutoScale.setColumns(2);
    pnDirectIANorthDisplay.add(tglbtnIsrunning);

    txtDirectTime = new JTextField();
    txtDirectTime.setHorizontalAlignment(SwingConstants.TRAILING);
    txtDirectTime.setText("4");
    pnDirectIANorthDisplay.add(txtDirectTime);
    txtDirectTime.setColumns(3);

    JLabel lblS = new JLabel("s");
    pnDirectIANorthDisplay.add(lblS);

    Component horizontalStrut = Box.createHorizontalStrut(20);
    pnDirectIANorthDisplay.add(horizontalStrut);

    JLabel lblFilter = new JLabel("filter:");
    pnDirectIANorthDisplay.add(lblFilter);

    txtDirectFileFilter = new JTextField();
    txtDirectFileFilter.setToolTipText("filter file format");
    txtDirectFileFilter.setText("csv");
    pnDirectIANorthDisplay.add(txtDirectFileFilter);
    txtDirectFileFilter.setColumns(4);

    Component horizontalStrut_2 = Box.createHorizontalStrut(20);
    pnDirectIANorthDisplay.add(horizontalStrut_2);

    JLabel lblStartsWith = new JLabel("starts with:");
    pnDirectIANorthDisplay.add(lblStartsWith);

    txtDirectStartsWith = new JTextField();
    pnDirectIANorthDisplay.add(txtDirectStartsWith);
    txtDirectStartsWith.setColumns(8);

    Component horizontalStrut_1 = Box.createHorizontalStrut(20);
    pnDirectIANorthDisplay.add(horizontalStrut_1);
    pnDirectIANorthDisplay.setVisible(false);


    // add menu north items

  }
  // END OF CONSTRUCTOR
  // ###############################################################################################

  // ###############################################################################################
  // AUTO UPDATER
  // INIT, start, run
  private void initAutoUpdater() {
    // init auto updater to create new heatmaps
    autoActionL = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        if (isCbAutoUpdatingSelected())
          writeAllSettingsFromModules(false);
      }
    };
    autoColorChangedL = new ColorChangedListener() {
      @Override
      public void colorChanged(Color color) {
        if (isCbAutoUpdatingSelected())
          writeAllSettingsFromModules(false);
      }
    };
    autoItemL = new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        if (isCbAutoUpdatingSelected())
          writeAllSettingsFromModules(false);
      }
    };
    autoChangeL = new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent arg0) {
        if (isCbAutoUpdatingSelected())
          startAutoUpdater(false);
      }
    };
    autoDocumentL = new DocumentListener() {
      @Override
      public void removeUpdate(DocumentEvent arg0) {
        if (isCbAutoUpdatingSelected())
          startAutoUpdater(false);
      }

      @Override
      public void insertUpdate(DocumentEvent arg0) {
        if (isCbAutoUpdatingSelected())
          startAutoUpdater(false);
      }

      @Override
      public void changedUpdate(DocumentEvent arg0) {
        if (isCbAutoUpdatingSelected())
          startAutoUpdater(false);
      }
    };


    // ##################################################################
    // init auto repainter
    autoRepActionL = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        if (isCbAutoUpdatingSelected())
          writeAllSettingsFromModules(true);
      }
    };
    autoRepColorChangedL = new ColorChangedListener() {
      @Override
      public void colorChanged(Color color) {
        if (isCbAutoUpdatingSelected())
          writeAllSettingsFromModules(true);
      }
    };
    autoRepItemL = new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        if (isCbAutoUpdatingSelected())
          writeAllSettingsFromModules(true);
      }
    };
    autoRepChangeL = new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent arg0) {
        if (isCbAutoUpdatingSelected())
          startAutoUpdater(true);
      }
    };
    autoRepDocumentL = new DocumentListener() {
      @Override
      public void removeUpdate(DocumentEvent arg0) {
        if (isCbAutoUpdatingSelected())
          startAutoUpdater(true);
      }

      @Override
      public void insertUpdate(DocumentEvent arg0) {
        if (isCbAutoUpdatingSelected())
          startAutoUpdater(true);
      }

      @Override
      public void changedUpdate(DocumentEvent arg0) {
        if (isCbAutoUpdatingSelected())
          startAutoUpdater(true);
      }
    };
    // add to MODULES TODO
    modImage2D.addAutoupdater(autoActionL, autoChangeL, autoDocumentL, autoColorChangedL,
        autoItemL);
    modImage2D.addAutoRepainter(autoRepActionL, autoRepChangeL, autoRepDocumentL,
        autoRepColorChangedL, autoRepItemL);


    modImageOverlay.addAutoupdater(autoActionL, autoChangeL, autoDocumentL, autoColorChangedL,
        autoItemL);
    modImageOverlay.addAutoRepainter(autoRepActionL, autoRepChangeL, autoRepDocumentL,
        autoRepColorChangedL, autoRepItemL);

    modImageMerge.addAutoupdater(autoActionL, autoChangeL, autoDocumentL, autoColorChangedL,
        autoItemL);
    modImageMerge.addAutoRepainter(autoRepActionL, autoRepChangeL, autoRepDocumentL,
        autoRepColorChangedL, autoRepItemL);

    modSPImage.addAutoupdater(autoActionL, autoChangeL, autoDocumentL, autoColorChangedL,
        autoItemL);
    modSPImage.addAutoRepainter(autoRepActionL, autoRepChangeL, autoRepDocumentL,
        autoRepColorChangedL, autoRepItemL);
  }

  /**
   * starts the auto update function
   */
  public void startAutoUpdater(boolean repaintOnly) {
    lastAutoUpdateTime = System.currentTimeMillis();
    if (ImageLogicRunner.IS_UPDATING()) {
      if (!repaintOnly)
        autoRepaintOnly = false;

      if (!isAutoUpdateStarted) {
        logger.debug("Auto update started");
        isAutoUpdateStarted = true;
        Thread t = new Thread(this);
        t.start();
      }
    }
  }

  @Override
  public void run() {
    while (true) {
      if (lastAutoUpdateTime + AUTO_UPDATE_TIME <= System.currentTimeMillis()) {
        writeAllSettingsFromModules(autoRepaintOnly);
        lastAutoUpdateTime = -1;
        isAutoUpdateStarted = false;
        autoRepaintOnly = true;
        break;
      }
      try {
        Thread.currentThread().sleep(100);
      } catch (InterruptedException e) {
        logger.error("", e);
      }
    }
  }
  // END OF AUTO UPDATING THE SETTINGS
  // ##########################################################################################
  // START OF SETTINGS MANIPULATIONS

  /**
   * general preferences have been changed in the general pref dialog Apply to this window
   */
  public void callPreferencesChanged() {
    SettingsGeneralPreferences pref = SettingsHolder.getSettings().getSetGeneralPreferences();
    setIsCreatingImageIcons(pref.isGeneratesIcons());
  }

  /**
   * reads all settings from all modules renews the heatmap that is shown --> update in modules
   * checks if auto update? if false it will always update (do the task)
   * 
   * @param checks whether to check for autoupdate or just do it (when false)
   */
  public void fireUpdateEvent(boolean checks) {
    if (!checks || isCbAutoUpdatingSelected()) {
      writeAllSettingsFromModules(false);
    }
  }

  /**
   * reads all settings from all modules renews the heatmap that is shown --> update in modules
   */
  public void writeAllSettingsFromModules(boolean repaintOnly) {
    //
    if (activeModuleContainer != null) {
      logger.debug("Write all Settings from all Modules --> create Settings");

      // show Image
      if (ImageLogicRunner.IS_UPDATING()) {
        // TODO Write all to Settings
        activeModuleContainer.writeAllToSettings();

        if (repaintOnly) {
          logger.debug("Write all Settings from all Modules --> REPAINT ONLY");
          Heatmap heat = logicRunner.getCurrentHeat();
          Collectable2D img = logicRunner.getSelectedImage();
          if (heat != null && img != null) {
            img.getSettings().applyToHeatMap(heat);

            heat.getChartPanel().revalidate();
            heat.getChartPanel().repaint();
          }
        } else {
          logger.debug("Write all Settings from all Modules --> CREATE NEW");
          Heatmap heat = logicRunner.renewImage2DView();
        }
      }
    }
  }


  /**
   * sets the image to all imagemodules: gets called first (then addHeatmapToPanel)
   * 
   * @param img
   */
  public void setImage2D(Image2D img) {
    DebugStopWatch debug = new DebugStopWatch();
    boolean isauto = modImage2D.isAutoUpdating();
    modImage2D.setAutoUpdating(false);
    // finished
    ImageLogicRunner.setIS_UPDATING(false);
    // show all modules for images
    if (activeModuleContainer != null && activeModuleContainer != modImage2D) {
      activeModuleContainer.setVisible(false);
    }
    if (activeModuleContainer == null || activeModuleContainer != modImage2D) {
      activeModuleContainer = modImage2D;
      activeModuleContainer.setVisible(true);
      east.add(activeModuleContainer, BorderLayout.CENTER);
      debug.stopAndLOG("for showing all modules of img2d");
    }

    // set
    debug.setNewStartTime();
    modImage2D.setCurrentImage(img, true);

    debug.stopAndLOG("FOR setting the current image for all modules");

    // finished
    ImageLogicRunner.setIS_UPDATING(true);
    modImage2D.setAutoUpdating(isauto);
    updateMenuBar(img);

    this.revalidate();
  }

  /**
   * sets the image to all imagemodules: gets called first (then addHeatmapToPanel)
   * 
   * @param img
   */
  public void setImageOverlay(ImageOverlay img) {
    boolean isauto = modImageOverlay.isAutoUpdating();
    modImageOverlay.setAutoUpdating(false);
    // finished
    ImageLogicRunner.setIS_UPDATING(false);
    // show all modules for ImageOverlays
    if (activeModuleContainer != null && activeModuleContainer != modImageOverlay)
      activeModuleContainer.setVisible(false);

    if (activeModuleContainer == null || activeModuleContainer != modImageOverlay) {
      activeModuleContainer = modImageOverlay;
      activeModuleContainer.setVisible(true);
      east.add(activeModuleContainer, BorderLayout.CENTER);
    }

    // set
    DebugStopWatch debug = new DebugStopWatch();
    modImageOverlay.setCurrentImage(img, true);
    logger.debug("TIME: {} FOR setting the current image for all OVERLAY modules ", debug.stop());

    // finished
    ImageLogicRunner.setIS_UPDATING(true);
    modImageOverlay.setAutoUpdating(isauto);
    updateMenuBar(img);

    this.revalidate();
  }

  /**
   * sets the image to all imagemodules: gets called first (then addHeatmapToPanel)
   * 
   * @param img
   */
  public void setImageMerge(ImageMerge img) {
    boolean isauto = modImageMerge.isAutoUpdating();
    modImageMerge.setAutoUpdating(false);
    // finished
    ImageLogicRunner.setIS_UPDATING(false);
    // show all modules for ImageOverlays
    if (activeModuleContainer != null && activeModuleContainer != modImageMerge)
      activeModuleContainer.setVisible(false);

    if (activeModuleContainer == null || activeModuleContainer != modImageMerge) {
      activeModuleContainer = modImageMerge;
      activeModuleContainer.setVisible(true);
      east.add(activeModuleContainer, BorderLayout.CENTER);
    }

    // set
    DebugStopWatch debug = new DebugStopWatch();
    modImageMerge.setCurrentImage(img, true);
    logger.debug("TIME: {} FOR setting the current image for all OVERLAY modules ", debug.stop());

    // finished
    ImageLogicRunner.setIS_UPDATING(true);
    modImageMerge.setAutoUpdating(isauto);
    updateMenuBar(img);

    this.revalidate();
  }


  /**
   * sets the single particle image
   * 
   * @param img
   */
  public void setSPImage(SingleParticleImage img) {
    boolean isauto = modSPImage.isAutoUpdating();
    modSPImage.setAutoUpdating(false);
    // finished
    ImageLogicRunner.setIS_UPDATING(false);
    // show all modules for ImageOverlays
    if (activeModuleContainer != null && activeModuleContainer != modSPImage)
      activeModuleContainer.setVisible(false);

    if (activeModuleContainer == null || activeModuleContainer != modSPImage) {
      activeModuleContainer = modSPImage;
      activeModuleContainer.setVisible(true);
      east.add(activeModuleContainer, BorderLayout.CENTER);
    }

    // set
    DebugStopWatch debug = new DebugStopWatch();
    modSPImage.setCurrentImage(img, true);
    logger.debug("TIME: {}  FOR setting the current image for all SP modules ", debug.stop());

    // finished
    ImageLogicRunner.setIS_UPDATING(true);
    modSPImage.setAutoUpdating(isauto);
    updateMenuBar(img);

    this.revalidate();
  }

  public void updateMenuBar(Collectable2D c) {
    btnSingleParticle.setVisible(c.isSPImage());
    pnNorthMenu.revalidate();
    pnNorthMenu.repaint();
  }

  /**
   * Visualization: Adds a heatmap (Chartpanel) to the center view Adds a heatmap to all
   * imagemodules called from {@link ImageLogicRunner#renewImage2DView()}.
   * 
   * @param heat
   * @throws Exception
   */
  public void addHeatmapToPanel(final Heatmap heat) throws Exception {

    logger.debug("Add Heatmap to panel");

    getPnCenterImageView().removeAll();
    getPnCenterImageView().add(heat.getChartPanel(), BorderLayout.CENTER);

    // resize listener
    EChartPanel cp = heat.getChartPanel();
    ZoomHistory history = cp.getZoomHistory();
    if (history != null) {
      ModuleZoom moduleZoom = getModuleZoom();
      if (moduleZoom != null) {
        moduleZoom.setCurrentHistory(history);
      }
    }

    // zoom link to settings
    XYPlot plot = cp.getChart().getXYPlot();
    plot.getDomainAxis().addChangeListener(new AxisRangeChangedListener(plot, e -> {
      heat.getImage().getSettZoom().setXrange(e.getNewR());

      ModuleZoom moduleZoom = getModuleZoom();
      if (moduleZoom != null) {
        moduleZoom.setAllViaExistingSettings(heat.getImage().getSettZoom());
      }
    }));
    plot.getRangeAxis().addChangeListener(new AxisRangeChangedListener(plot, e -> {
      heat.getImage().getSettZoom().setYrange(e.getNewR());

      ModuleZoom moduleZoom = getModuleZoom();
      if (moduleZoom != null) {
        moduleZoom.setAllViaExistingSettings(heat.getImage().getSettZoom());
      }
    }));


    getPnCenterImageView().revalidate();
    getPnCenterImageView().repaint();

    ChartLogics.setZoomDomainAxis(heat.getChartPanel(),
        ChartLogics.getZoomDomainAxis(heat.getChartPanel()), false);
    // set heatmap for all modules
    if (activeModuleContainer != null)
      activeModuleContainer.setCurrentHeatmap(heat);
    else {
      throw new Exception(
          "No module container is active. First set the Image or overlay - then the heatmap");
    }
  }

  // ##########################################################################################
  // LOGIC
  // changing style of all frames and dialogs
  protected void changeWindowStyle(int style) {
    WindowStyleUtil.changeWindowStyle(this, style);
    for (Component c : listFrames) {
      WindowStyleUtil.changeWindowStyle(c, style);
    }
  }

  /**
   * Sets the current "view". Changes the working area. Imaging or direct imaging analysis
   * 
   * @param view
   */
  protected void setSelectedView(int view) {
    // set some things
    currentView = view;
    // getModuleListImages().setVisible(view==VIEW_IMAGING_ANALYSIS);
    pnDirectIANorthDisplay.setVisible(view == VIEW_DIRECT_IMAGING_ANALYSIS);
    // switch view TODO
    switch (view) {
      case VIEW_IMAGING_ANALYSIS:
        break;
      case VIEW_DIRECT_IMAGING_ANALYSIS:
        break;
    }
    // resets size of split
    getSplitPane().resetToPreferredSizes();
  }

  // ##########################################################################################
  // LOGGER
  /**
   * if debugging is activated
   * 
   * @return
   */
  public static boolean isDebugging() {
    return (getEditor() != null && getEditor().getCbDebug() != null
        && getEditor().getCbDebug().isSelected());
  }

  public static boolean isLogging() {
    return isLogging;
  }

  public static void setLogging(boolean isLogging) {
    ImageEditorWindow.isLogging = isLogging;
  }

  // ##########################################################################################
  // GETTERS AND SETTERS
  public boolean isCbAutoUpdatingSelected() {
    return activeModuleContainer.isAutoUpdating();
  }

  public ImageLogicRunner getLogicRunner() {
    return logicRunner;
  }

  public ModuleTreeWithOptions getModuleTreeImages() {
    return moduleTreeImages;
  }

  public JPanel getPnCenterImageView() {
    return pnCenterImageView;
  }

  public static ImageEditorWindow getEditor() {
    return thisFrame;
  }

  public JRadioButtonMenuItem getMenuRbImagingAnalysis() {
    return menuRbImagingAnalysis;
  }

  public JRadioButtonMenuItem getMenuRbDirectImagingAnalysis() {
    return menuRbDirectImagingAnalysis;
  }

  public JSplitPane getSplitPane() {
    return splitPane;
  }

  public JPanel getWest() {
    return west;
  }

  public int getCurrentView() {
    // TODO Auto-generated method stub
    return currentView;
  }

  public JPanel getPnSouth() {
    return pnSouth;
  }

  public JPanel getPnDirectIANorthDisplay() {
    return pnDirectIANorthDisplay;
  }

  public JTextField getTxtDirectTime() {
    return txtDirectTime;
  }

  public JTextField getTxtDirectStartsWith() {
    return txtDirectStartsWith;
  }

  public JTextField getTxtDirectFileFilter() {
    return txtDirectFileFilter;
  }

  public ModuleZoom getModuleZoom() {
    return (ModuleZoom) (activeModuleContainer == null ? null
        : activeModuleContainer.getModuleByClass(ModuleZoom.class));
  }

  public ModuleThemes getModuleThemes() {
    return (ModuleThemes) (activeModuleContainer == null ? null
        : activeModuleContainer.getModuleByClass(ModuleThemes.class));
  }

  /**
   * the list of images or null if the editor is not initialized
   * 
   * @return
   */
  public static List<Collectable2D> getImages() {
    if (getEditor() == null)
      return null;
    else {
      return getEditor().getLogicRunner().getListImages();
    }
  }

  public JCheckBox getCbSumTasks() {
    return cbSumTasks;
  }

  public JCheckBoxMenuItem getCbDebug() {
    return cbDebug;
  }

  public JTextField getTxtDirectAutoScale() {
    return txtDirectAutoScale;
  }

  public JCheckBox getCbDirectAutoScale() {
    return cbDirectAutoScale;
  }

  public void setIsCreatingImageIcons(boolean state) {
    cbCreateImageIcons.setSelected(state);
  }

  public boolean isCreatingImageIcons() {
    return cbCreateImageIcons.isSelected();
  }

  public JPanel getPnNorth() {
    return pnNorth;
  }

  public JPanel getPnNorthMenu() {
    return pnNorthMenu;
  }

  public ImageSetupDialog getImageSetupDialog() {
    return imageSetupDialog;
  }
}
