package net.rs.lamsi.multiimager.Frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.data.Range;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.ImageGroupMD;
import net.rs.lamsi.general.datamodel.image.ImageOverlay;
import net.rs.lamsi.general.datamodel.image.ImagingProject;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.dialogs.GraphicsExportDialog;
import net.rs.lamsi.general.framework.basics.ColorChangedListener;
import net.rs.lamsi.general.framework.modules.MainSettingsModuleContainer;
import net.rs.lamsi.general.framework.modules.ModuleTreeWithOptions;
import net.rs.lamsi.general.framework.modules.listeners.HideShowChangedListener;
import net.rs.lamsi.general.framework.modules.tree.IconNodeRenderer;
import net.rs.lamsi.general.heatmap.Heatmap;
import net.rs.lamsi.general.myfreechart.ChartLogics;
import net.rs.lamsi.general.myfreechart.Plot.PlotChartPanel;
import net.rs.lamsi.general.myfreechart.Plot.image2d.listener.AspectRatioListener;
import net.rs.lamsi.general.myfreechart.Plot.image2d.listener.AspectRatioListener.RATIO;
import net.rs.lamsi.general.myfreechart.Plot.image2d.listener.AxesRangeChangedListener;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.general.settings.SettingsHolder;
import net.rs.lamsi.general.settings.listener.SettingsChangedListener;
import net.rs.lamsi.general.settings.preferences.SettingsGeneralPreferences;
import net.rs.lamsi.multiimager.FrameModules.ModuleImage2D;
import net.rs.lamsi.multiimager.FrameModules.ModuleImageOverlay;
import net.rs.lamsi.multiimager.FrameModules.sub.ModuleBackgroundImg;
import net.rs.lamsi.multiimager.FrameModules.sub.ModuleSelectExcludeData;
import net.rs.lamsi.multiimager.FrameModules.sub.ModuleZoom;
import net.rs.lamsi.multiimager.FrameModules.sub.theme.ModuleThemes;
import net.rs.lamsi.multiimager.Frames.dialogs.CroppingDialog;
import net.rs.lamsi.multiimager.Frames.dialogs.DialogChooseProject;
import net.rs.lamsi.multiimager.Frames.dialogs.DialogDataSaver;
import net.rs.lamsi.multiimager.Frames.dialogs.DialogPreferences;
import net.rs.lamsi.multiimager.Frames.dialogs.ImportDataDialog;
import net.rs.lamsi.multiimager.Frames.multiimageframe.MultiImageFrame;
import net.rs.lamsi.utils.DialogLoggerUtil;
import net.rs.lamsi.utils.FileAndPathUtil;
import net.rs.lamsi.utils.WindowStyleUtil;
import net.rs.lamsi.utils.useful.DebugStopWatch;
import net.rs.lamsi.utils.useful.dialogs.ProgressDialog;

// net.rs.lamsi.multiimager.Frames.ImageEditorWindow
public class ImageEditorWindow extends JFrame implements Runnable {
	// LOG
	public static enum LOG {
		MESSAGE, ERROR, WARNING, IMPORTANT, DEBUG
	}
	private static SimpleAttributeSet styleWarning, styleMessage, styleImportant, styleError, styleDebug;
	private static boolean isLogging = true;
	//
	public static final int VIEW_IMAGING_ANALYSIS = 0, VIEW_DIRECT_IMAGING_ANALYSIS = 1;
	// My STUFF
	private static ImageEditorWindow thisFrame;
	private ImageLogicRunner logicRunner;
	private ImportDataDialog importDataDialog; 
	private DialogPreferences preferencesDialog;

	// save all frames for updating style etc
	private Vector<Component> listFrames = new Vector<Component>();

	// MODULES
	private ModuleImage2D modImage2D;
	private ModuleImageOverlay modImageOverlay;
	/**
	 * the module container that is active (imageoverlay or image2d)
	 * first set the image or imageoverlay and this will be set
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
	
	private AspectRatioListener aspectRatioListener;
	
	// AUTOGEN 
	//
	private ModuleTreeWithOptions<Collectable2D> moduleTreeImages;
	
	
	private JPanel pnCenterImageView;	
	private JPanel east;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JRadioButtonMenuItem menuRbImagingAnalysis;
	private JRadioButtonMenuItem menuRbDirectImagingAnalysis;
	private final ButtonGroup buttonGroup_1 = new ButtonGroup();
	private JSplitPane splitPane;
	private JPanel west;
	private JSplitPane splitNorthSouth;
	private static JTextPane txtLog;
	private JPanel pnSouth;
	private JPanel pnDirectIANorthDisplay;
	private JTextField txtDirectTime;
	private JTextField txtDirectFileFilter;
	private JTextField txtDirectStartsWith;
	private JCheckBox cbSumTasks;
	private JCheckBoxMenuItem cbDebug;
	private JCheckBoxMenuItem cbKeepAspectRatio;
	private JCheckBoxMenuItem cbCreateImageIcons;
	private JPanel pnChartAspectRatio;
	private JTextField txtDirectAutoScale;
	private JCheckBox cbDirectAutoScale;

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
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ImageEditorWindow() {
		setTitle("ImageEditor");
		thisFrame = this;
		initialize();
		// init automatic update function
		initAutoUpdater();
		// plot export dialog 
		GraphicsExportDialog grpExportDialog = GraphicsExportDialog.createInstance();
		WindowStyleUtil.changeWindowStyle(grpExportDialog, WindowStyleUtil.STYLE_SYSTEM);
		grpExportDialog.setVisible(false);
		listFrames.addElement(grpExportDialog);
		// give window to LogicPanel
		logicRunner = new ImageLogicRunner(this); 
		// progress dialog 
		ProgressDialog.initDialog(this);
		// create preferences dialog
		preferencesDialog = new DialogPreferences(this);
		WindowStyleUtil.changeWindowStyle(preferencesDialog, WindowStyleUtil.STYLE_SYSTEM);
		preferencesDialog.setVisible(false);
		listFrames.addElement(preferencesDialog);
		
		// init data import
		importDataDialog = new ImportDataDialog(logicRunner);
		WindowStyleUtil.changeWindowStyle(importDataDialog, WindowStyleUtil.STYLE_SYSTEM);
		importDataDialog.setVisible(false);
		listFrames.addElement(importDataDialog);

		DialogDataSaver exportDataDialog = DialogDataSaver.createInst(SettingsHolder.getSettings());
		WindowStyleUtil.changeWindowStyle(exportDataDialog, WindowStyleUtil.STYLE_SYSTEM);
		exportDataDialog.setVisible(false);
		listFrames.addElement(exportDataDialog);

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
		mntmOpenImage.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK+ InputEvent.SHIFT_MASK));
		mnFile.add(mntmOpenImage);

		JMenuItem mntmSaveImage = new JMenuItem("Save image file");
		mntmSaveImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// save in logicRunner  
				logicRunner.saveImage2DToFile();
			}
		});
		mntmSaveImage.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK+InputEvent.SHIFT_MASK));
		mnFile.add(mntmSaveImage);

		JSeparator separator = new JSeparator();
		mnFile.add(separator);

		JMenuItem mntmSaveDataAs = new JMenuItem("Export data");
		mntmSaveDataAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// open data export dialog
				Collectable2D img = logicRunner.getSelectedImage();
				if(img.isImage2D())
					DialogDataSaver.startDialogWith(logicRunner.getListImage2DOnly(),(Image2D)img);
				else log("Select a standard image for data export (e.g. image overlay currently selected)", LOG.ERROR);
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
				if(menuItemHistoryImg2D!=null)
					for(JMenuItem mi : menuItemHistoryImg2D)
						mnFile.remove(mi);
				// create new
				Vector<File> files = SettingsHolder.getSettings().getSetGeneralPreferences().getImg2DHistory();
				menuItemHistoryImg2D = new JMenuItem[files.size()];
				
				// add new
				if(menuItemHistoryImg2D!=null) {
					for(int i=0; i<files.size(); i++){
						final File f = files.get(i);
						JMenuItem mnImportData = new JMenuItem(FileAndPathUtil.eraseFormat(f.getName())+"; "+f.getParentFile().getAbsolutePath());
						mnImportData.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								// choose project dialog
								ImagingProject project = DialogChooseProject.choose(getModuleTreeImages().getSelectedProject(), getModuleTreeImages());

								// import file as image2d
								logicRunner.loadImage2DAndProjectFromFile(f, project);
							}
						});
						mnImportData.setPreferredSize(new Dimension(280, (int)mnImportData.getPreferredSize().getHeight()));
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
		
		JMenuItem btnCrop = new JMenuItem("Crop images");
		btnCrop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Open Dialog
				Collectable2D img = getLogicRunner().getSelectedImage();
				if(img!=null && Image2D.class.isInstance(img) && img.getImageGroup()!=null) {
					CroppingDialog d = new CroppingDialog();
					d.startDialog(img.getImageGroup(), (Image2D)img);
				}
			}
		});
		mnAction.add(btnCrop);
		
		JMenuItem mntmCombineImages = new JMenuItem("Combine images");
		mnAction.add(mntmCombineImages);
		
		JMenuItem mntmSplitImages = new JMenuItem("Split images");
		mnAction.add(mntmSplitImages);


		JMenuItem btnCreateOverlay = new JMenuItem("Create overlay");
		btnCreateOverlay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Open Dialog
				logicRunner.createOverlay();
			}
		});
		mnAction.add(btnCreateOverlay);
		
		JMenuItem btnImportMicroscopic = new JMenuItem("Add down sampled microscopic image");
		btnImportMicroscopic.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Open Dialog
				logicRunner.importMicroscopicImageDownSampled();
			}
		});
		mnAction.add(btnImportMicroscopic);

		JMenuItem btnImportMicroscopic2 = new JMenuItem("Add microscopic image to background");
		btnImportMicroscopic2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Open Dialog
				if(activeModuleContainer!=null) {
					ModuleBackgroundImg mod = (ModuleBackgroundImg)activeModuleContainer.getModuleByClass(ModuleBackgroundImg.class);
					if(mod!=null) {
						mod.getBtnAddImage().doClick();
					}
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
				if(rb.isSelected()) setSelectedView(VIEW_IMAGING_ANALYSIS);
			}
		});
		menuRbImagingAnalysis.setSelected(true);
		mnView.add(menuRbImagingAnalysis);

		menuRbDirectImagingAnalysis = new JRadioButtonMenuItem("Direct imaging analysis (DIA)");
		buttonGroup_1.add(menuRbDirectImagingAnalysis);
		menuRbDirectImagingAnalysis.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				JRadioButtonMenuItem rb = (JRadioButtonMenuItem) e.getSource();
				if(rb.isSelected()) setSelectedView(VIEW_DIRECT_IMAGING_ANALYSIS);
			}
		});
		mnView.add(menuRbDirectImagingAnalysis);
		
		JSeparator separator_3 = new JSeparator();
		mnView.add(separator_3);
		
		JMenuItem mntmMultiImageExplorer = new JMenuItem("Multi image explorer");
		mntmMultiImageExplorer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// select image list TODO
				TreePath path = DialogLoggerUtil.showTreeDialogAndChoose(thisFrame, getModuleTreeImages().getRoot(), TreeSelectionModel.SINGLE_TREE_SELECTION, getModuleTreeImages().getTree().getSelectionPaths())[0];
				// show dialog with mutliple image view
				if(path!=null) { 
					// TODO correct?
					ImageGroupMD img = getModuleTreeImages().getImageGroup(path);
					if(img!=null) {
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
				if(activeModuleContainer!=null) { 
					// TODO correct?
					ModuleSelectExcludeData mod = (ModuleSelectExcludeData)activeModuleContainer.getModuleByClass(ModuleSelectExcludeData.class);
					if(mod!=null) {
						mod.getBtnOpenSelectData().doClick();
					}
				}
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

		mnWindow.add( new JSeparator());

		JRadioButtonMenuItem rdbtnmntmSystemStyle = new JRadioButtonMenuItem("System style");
		rdbtnmntmSystemStyle.setSelected(true);
		rdbtnmntmSystemStyle.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				JRadioButtonMenuItem rb = (JRadioButtonMenuItem) e.getSource();
				if(rb.isSelected()) changeWindowStyle(WindowStyleUtil.STYLE_SYSTEM);
			}
		});
		buttonGroup.add(rdbtnmntmSystemStyle);
		mnWindow.add(rdbtnmntmSystemStyle);

		JRadioButtonMenuItem rdbtnmntmJavaStyle = new JRadioButtonMenuItem("Java style");
		rdbtnmntmJavaStyle.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				JRadioButtonMenuItem rb = (JRadioButtonMenuItem) e.getSource();
				if(rb.isSelected()) changeWindowStyle(WindowStyleUtil.STYLE_JAVA);
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
				SettingsHolder.getSettings().getSetGeneralPreferences().setGeneratesIcons(cbCreateImageIcons.isSelected());
			}
		});
		cbCreateImageIcons.setSelected(true);
		mnWindow.add(cbCreateImageIcons);
		
		cbKeepAspectRatio = new JCheckBoxMenuItem("Keep aspect ratio");
		cbKeepAspectRatio.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) { 
				logicRunner.renewImage2DView();
				aspectRatioListener.setKeepRatio(cbKeepAspectRatio.isSelected());
			}
		});
		cbKeepAspectRatio.setSelected(true);
		mnWindow.add(cbKeepAspectRatio);
		
		cbDebug = new JCheckBoxMenuItem("Debug");
		cbDebug.setSelected(true);
		mnWindow.add(cbDebug);
		 
		// #####################################################
		// set visible for correct sizes
		this.setVisible(true); 
		// collapse all
	} 

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {  
		// init progres sdialog
		ProgressDialog.initDialog(this);
		// init
		this.setBounds(100, 100, 934, 619);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		splitNorthSouth = new JSplitPane();
		splitNorthSouth.setResizeWeight(0.9);
		splitNorthSouth.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitNorthSouth.setMinimumSize(new Dimension(247, 20));
		splitNorthSouth.setPreferredSize(new Dimension(200, 500));
		getContentPane().add(splitNorthSouth, BorderLayout.CENTER);

		JPanel pnNorthContent = new JPanel(); 
		pnNorthContent.setLayout(new BorderLayout(0, 0));

		east = new JPanel();
		pnNorthContent.add(east, BorderLayout.EAST);
		east.setLayout(new BorderLayout(0, 0));

		modImage2D = new ModuleImage2D(this); 
		modImage2D.setVisible(false);
		
		modImageOverlay = new ModuleImageOverlay(this);
		modImageOverlay.setVisible(false);
		
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
				logicRunner.loadImage2DFromFile();
			}
		});
		moduleTreeImages.getPnOptions().add(btnLoad);

		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logicRunner.saveImage2DToFile();
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
		center.setLayout(new BorderLayout(5, 5));

		pnCenterImageView = new JPanel();
		pnCenterImageView.addComponentListener(aspectRatioListener = new AspectRatioListener(pnCenterImageView, true,  RATIO.LIMIT_TO_PARENT_SIZE) { 
			@Override
			public void componentResized(ComponentEvent e) {
				// resize chart
				if(pnChartAspectRatio !=null && (logicRunner).getCurrentHeat()!=null) { 
					resize(getLogicRunner().getCurrentHeat().getChartPanel());
				}
			}
		});
		center.add(pnCenterImageView, BorderLayout.CENTER); 
		pnCenterImageView.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		pnCenterImageView.add(panel, BorderLayout.CENTER);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0};
		gbl_panel.rowHeights = new int[]{0};
		gbl_panel.columnWeights = new double[]{Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{Double.MIN_VALUE};
		panel.setLayout(gbl_panel);

		JPanel north = new JPanel();
		pnNorthContent.add(north, BorderLayout.NORTH);
		north.setLayout(new BorderLayout(0, 0));

		JPanel panel_1 = new JPanel();
		splitNorthSouth.setLeftComponent(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));
		panel_1.add(pnNorthContent, BorderLayout.CENTER);

		pnSouth = new JPanel();
		splitNorthSouth.setRightComponent(pnSouth);
		pnSouth.setLayout(new BorderLayout(0, 0));

		JPanel pnLog = new JPanel();
		pnSouth.add(pnLog, BorderLayout.CENTER);
		pnLog.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane_1 = new JScrollPane();
		pnLog.add(scrollPane_1, BorderLayout.CENTER);

		txtLog = new JTextPane();
		scrollPane_1.setViewportView(txtLog);
		DefaultCaret caret = (DefaultCaret) txtLog.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		JLabel lblLog = new JLabel("Log");
		pnLog.add(lblLog, BorderLayout.NORTH);

		pnDirectIANorthDisplay = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) pnDirectIANorthDisplay.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		getContentPane().add(pnDirectIANorthDisplay, BorderLayout.NORTH);

		JToggleButton tglbtnIsrunning = new JToggleButton("isRunning");
		tglbtnIsrunning.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				try {
					long sleep = Long.valueOf(getTxtDirectTime().getText());
					String fileFilter = getTxtDirectFileFilter().getText();
					String startsWith = getTxtDirectStartsWith().getText();
					double scaleFactor = Double.valueOf(getTxtDirectAutoScale().getText());
					
					logicRunner.getDIARunner().setUp(sleep, fileFilter, startsWith, getCbSumTasks().isSelected(), getCbDirectAutoScale().isSelected(), scaleFactor);
					JToggleButton tb = (JToggleButton) e.getSource();
					logicRunner.getDIARunner().setPaused(!tb.isSelected());
				} catch(Exception ex) {
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
					if(logicRunner!=null && logicRunner.getDIARunner() !=null)
						logicRunner.getDIARunner().setAutoScale(cbDirectAutoScale.isSelected());
				} catch(Exception ex) { 
				}
			}
		});
		cbDirectAutoScale.setToolTipText("Enter scale factor to the right. Scaling the maximum intensity of paint scale");
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
					if(getTxtDirectAutoScale().getText().length()>0) {
						double scaleFactor = Double.valueOf(getTxtDirectAutoScale().getText());
						logicRunner.getDIARunner().setScaleFactor(scaleFactor);
					}
				} catch(Exception ex) { 
				}
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				try { 
					if(getTxtDirectAutoScale().getText().length()>0) {
						double scaleFactor = Double.valueOf(getTxtDirectAutoScale().getText());
						logicRunner.getDIARunner().setScaleFactor(scaleFactor);
					}
				} catch(Exception ex) { 
				}
			}
		});
		txtDirectAutoScale.setToolTipText("Auto scale factor. (X times maximum intensity of last line)");
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
		
		
		
	}
	// END OF CONSTRUCTOR
	//###############################################################################################

	//###############################################################################################
	// AUTO UPDATER
	// INIT, start, run
	private void initAutoUpdater() {
		// init auto updater to create new heatmaps
		autoActionL = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(isCbAutoUpdatingSelected()) writeAllSettingsFromModules(false);
			}
		};
		autoColorChangedL = new ColorChangedListener() { 
			@Override
			public void colorChanged(Color color) {
				if(isCbAutoUpdatingSelected()) writeAllSettingsFromModules(false); 
			}
		};
		autoItemL = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(isCbAutoUpdatingSelected()) writeAllSettingsFromModules(false);
			}
		};
		autoChangeL = new ChangeListener() { 
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if(isCbAutoUpdatingSelected()) startAutoUpdater(false); 
			}
		};
		autoDocumentL = new DocumentListener() { 
			@Override
			public void removeUpdate(DocumentEvent arg0) {  
				if(isCbAutoUpdatingSelected()) startAutoUpdater(false); 
			} 
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				if(isCbAutoUpdatingSelected()) startAutoUpdater(false); 
			} 
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				if(isCbAutoUpdatingSelected()) startAutoUpdater(false); 
			}
		};
		
		
		// ##################################################################
		// init auto repainter
		autoRepActionL = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(isCbAutoUpdatingSelected()) writeAllSettingsFromModules(true);
			}
		};
		autoRepColorChangedL = new ColorChangedListener() { 
			@Override
			public void colorChanged(Color color) {
				if(isCbAutoUpdatingSelected()) writeAllSettingsFromModules(true); 
			}
		};
		autoRepItemL = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(isCbAutoUpdatingSelected()) writeAllSettingsFromModules(true);
			}
		};
		autoRepChangeL = new ChangeListener() { 
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if(isCbAutoUpdatingSelected()) startAutoUpdater(true); 
			}
		};
		autoRepDocumentL = new DocumentListener() { 
			@Override
			public void removeUpdate(DocumentEvent arg0) {  
				if(isCbAutoUpdatingSelected()) startAutoUpdater(true); 
			} 
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				if(isCbAutoUpdatingSelected()) startAutoUpdater(true); 
			} 
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				if(isCbAutoUpdatingSelected()) startAutoUpdater(true); 
			}
		};
		// add to MODULES TODO 
		modImage2D.addAutoupdater(autoActionL, autoChangeL, autoDocumentL, autoColorChangedL, autoItemL);
		modImage2D.addAutoRepainter(autoRepActionL, autoRepChangeL, autoRepDocumentL, autoRepColorChangedL, autoRepItemL);	
		

		modImageOverlay.addAutoupdater(autoActionL, autoChangeL, autoDocumentL, autoColorChangedL, autoItemL);
		modImageOverlay.addAutoRepainter(autoRepActionL, autoRepChangeL, autoRepDocumentL, autoRepColorChangedL, autoRepItemL);
	} 
	
	/**
	 * starts the auto update function
	 */
	public void startAutoUpdater(boolean repaintOnly) {
		lastAutoUpdateTime = System.currentTimeMillis();
		if(ImageLogicRunner.IS_UPDATING()) {
			if(!repaintOnly) 
				autoRepaintOnly = false;
			
			if(!isAutoUpdateStarted) { 
				ImageEditorWindow.log("Auto update started", LOG.DEBUG);
				isAutoUpdateStarted = true;
				Thread t = new Thread(this);
				t.start();
			}
		}
	}

	@Override
	public void run() {
		while(true) {
			if(lastAutoUpdateTime+AUTO_UPDATE_TIME<=System.currentTimeMillis()) {
				writeAllSettingsFromModules(autoRepaintOnly);
				lastAutoUpdateTime=-1;
				isAutoUpdateStarted = false;
				autoRepaintOnly = true;
				break;
			}
			try {
				Thread.currentThread().sleep(100);
			} catch (InterruptedException e) { 
				e.printStackTrace();
			}
		} 
	}
	// END OF AUTO UPDATING THE SETTINGS
	//##########################################################################################
	// START OF SETTINGS MANIPULATIONS

	/**
	 * general preferences have been changed in the general pref dialog
	 * Apply to this window
	 */
	public void callPreferencesChanged() {
		SettingsGeneralPreferences pref = SettingsHolder.getSettings().getSetGeneralPreferences();
		setIsCreatingImageIcons(pref.isGeneratesIcons());
	}
	/**
	 * reads all settings from all modules
	 * renews the heatmap that is shown --> update in modules
	 * checks if auto update? if false it will always update (do the task)
	 * @param checks whether to check for autoupdate or just do it (when false) 
	 */ 
	public void fireUpdateEvent(boolean checks) {
		if(!checks || isCbAutoUpdatingSelected()) {
			writeAllSettingsFromModules(false);
		}
	}
	/**
	 * reads all settings from all modules
	 * renews the heatmap that is shown --> update in modules
	 */ 
	public void writeAllSettingsFromModules(boolean repaintOnly) {
		// 
		if(activeModuleContainer!=null) {
			ImageEditorWindow.log("Write all Settings from all Modules --> create Settings", LOG.DEBUG);
			
			// show Image 
			if(ImageLogicRunner.IS_UPDATING()) {
				// TODO Write all to Settings 
				activeModuleContainer.writeAllToSettings();
				
				if(repaintOnly) {
					ImageEditorWindow.log("Write all Settings from all Modules --> REPAINT ONLY", LOG.DEBUG);
					Heatmap heat = logicRunner.getCurrentHeat();
					Collectable2D img = logicRunner.getSelectedImage();
					if(heat!=null && img!=null) {
						img.getSettings().applyToHeatMap(heat);
	
						heat.getChartPanel().revalidate();
						heat.getChartPanel().repaint();
					}
				}
				else {
					ImageEditorWindow.log("Write all Settings from all Modules --> CREATE NEW", LOG.DEBUG);
					Heatmap heat = logicRunner.renewImage2DView();
				}
			}
		}
	} 
	
	
	/**
	 * sets the image to all imagemodules: gets called first (then addHeatmapToPanel)
	 * @param img
	 */
	public void setImage2D(Image2D img) { 
		boolean isauto = modImage2D.isAutoUpdating();
		modImage2D.setAutoUpdating(false); 
		// finished
		ImageLogicRunner.setIS_UPDATING(false);
		// show all modules for images
		if(activeModuleContainer!=null)
			activeModuleContainer.setVisible(false);
		
		activeModuleContainer = modImage2D;
		activeModuleContainer.setVisible(true);
		east.add(activeModuleContainer, BorderLayout.CENTER);
		
		// set
		DebugStopWatch debug = new DebugStopWatch();
		modImage2D.setCurrentImage(img, true); 
		ImageEditorWindow.log("TIME: " +debug.stop()+"   FOR setting the current image for all modules ", LOG.DEBUG);
		
		// finished
		ImageLogicRunner.setIS_UPDATING(true);
		modImage2D.setAutoUpdating(isauto);
		
		this.revalidate();
	} 
	/**
	 * sets the image to all imagemodules: gets called first (then addHeatmapToPanel)
	 * @param img
	 */
	public void setImageOverlay(ImageOverlay img) { 
		boolean isauto = modImageOverlay.isAutoUpdating();
		modImageOverlay.setAutoUpdating(false); 
		// finished
		ImageLogicRunner.setIS_UPDATING(false);
		// show all modules for ImageOverlays
		if(activeModuleContainer!=null)
			activeModuleContainer.setVisible(false);
		
		activeModuleContainer = modImageOverlay;
		activeModuleContainer.setVisible(true);
		east.add(activeModuleContainer, BorderLayout.CENTER);
		
		// set
		DebugStopWatch debug = new DebugStopWatch();
		modImageOverlay.setCurrentImage(img, true); 
		ImageEditorWindow.log("TIME: " +debug.stop()+"   FOR setting the current image for all OVERLAY modules "+debug.stop(), LOG.DEBUG);
		
		// finished
		ImageLogicRunner.setIS_UPDATING(true);
		modImageOverlay.setAutoUpdating(isauto);
		
		this.revalidate();
	} 
	
	
	
	/**
	 * Visualization:
	 * Adds a heatmap (Chartpanel) to the center view
	 * Adds a heatmap to all imagemodules
	 * called from {@link ImageLogicRunner#renewImage2DView()}.
	 * @param heat 
	 * @throws Exception 
	 */
	public void addHeatmapToPanel(final Heatmap heat) throws Exception { 

		ImageEditorWindow.log("Add Heatmap to panel", LOG.DEBUG); 
		
		getPnCenterImageView().removeAll(); 
		if(getCbKeepAspectRatio().isSelected()) {
			if(pnChartAspectRatio==null) {
				pnChartAspectRatio = new JPanel(); 
				pnChartAspectRatio.setLayout(new GridBagLayout());
			}
			// 
			getPnCenterImageView().add(pnChartAspectRatio,BorderLayout.CENTER);  
			pnChartAspectRatio.removeAll();
			pnChartAspectRatio.add(heat.getChartPanel());
			// add aspect ratio listener
			heat.getChartPanel().setAspectRatioListener(aspectRatioListener);
			// resize listener
			heat.getChartPanel().addAxesRangeChangedListener(new AxesRangeChangedListener() {
				@Override
				public void axesRangeChanged(PlotChartPanel chart, ValueAxis axis,
						boolean isDomainAxis, Range lastR, Range newR) {
					// save to zoom settings
					logicRunner.setIS_UPDATING(false);
					if(isDomainAxis)
						heat.getImage().getSettZoom().setXrange(newR);
					else 
						heat.getImage().getSettZoom().setYrange(newR);
					
					// set to module
					ModuleZoom moduleZoom = getModuleZoom();
					if(moduleZoom!=null) {
						moduleZoom.setAllViaExistingSettings(heat.getImage().getSettZoom());
					}
					logicRunner.setIS_UPDATING(true);
				}
			});
			
			// set width and height
			Dimension dim = ChartLogics.calcMaxSize(heat.getChartPanel(), getPnCenterImageView().getWidth(), getPnCenterImageView().getHeight());
			heat.getChartPanel().setPreferredSize(dim);
			logicRunner.getCurrentHeat().getChartPanel().setSize(dim);
			//pnChartAspectRatio.setSize(dim);
		}
		else {
			getPnCenterImageView().add(heat.getChartPanel(),BorderLayout.CENTER); 
		} 
		//
		getPnCenterImageView().revalidate();
		getPnCenterImageView().repaint();
		
		ChartLogics.setZoomDomainAxis(heat.getChartPanel(), ChartLogics.getZoomDomainAxis(heat.getChartPanel()), false); 
		// set heatmap for all modules 
		if(activeModuleContainer!=null)
			activeModuleContainer.setCurrentHeatmap(heat);
		else  {
			throw new Exception("No module container is active. First set the Image or overlay - then the heatmap");
		}
	}

	//##########################################################################################
	// LOGIC 
	// changing style of all frames and dialogs
	protected void changeWindowStyle(int style) {
		WindowStyleUtil.changeWindowStyle(this, style);
		for(Component c : listFrames) {
			WindowStyleUtil.changeWindowStyle(c, style);
		}
	}

	/**
	 * Sets the current "view". Changes the working area.
	 * Imaging or direct imaging analysis
	 * @param view
	 */
	protected void setSelectedView(int view) {
		// set some things 
		currentView = view;
		// getModuleListImages().setVisible(view==VIEW_IMAGING_ANALYSIS); 
		pnDirectIANorthDisplay.setVisible(view == VIEW_DIRECT_IMAGING_ANALYSIS);
		// switch view TODO
		switch(view) {
		case VIEW_IMAGING_ANALYSIS:
			break;
		case VIEW_DIRECT_IMAGING_ANALYSIS:
			break;
		}
		// resets size of split
		getSplitPane().resetToPreferredSizes();
		getSplitNorthSouth().resetToPreferredSizes();
	}
	
	//##########################################################################################
	// LOGGER
/**
 * if debugging is activated
 * @return
 */
	public static boolean isDebugging() { 
		return (getEditor()!=null && getEditor().getCbDebug()!=null && getEditor().getCbDebug().isSelected());
	}
	/**
	 * prints a message to the log 
	 * @param s
	 * @param mode LOG is in this class
	 */
	public static void log(String s, LOG mode) {
		if(txtLog!=null && isLogging() && getEditor()!=null && !(mode==LOG.DEBUG && !isDebugging())) {
			try {
				StyledDocument doc = txtLog.getStyledDocument();
				if(styleWarning==null) {
					// init styles 
					styleWarning = new SimpleAttributeSet();
					StyleConstants.setForeground(styleWarning, Color.ORANGE); 
					StyleConstants.setBold(styleWarning, false);
					styleError = new SimpleAttributeSet();
					StyleConstants.setForeground(styleError, Color.RED); 
					StyleConstants.setBold(styleError, true);
					styleMessage = new SimpleAttributeSet();
					StyleConstants.setForeground(styleMessage, Color.BLACK); 
					StyleConstants.setBold(styleMessage, false);
					styleImportant = new SimpleAttributeSet();
					StyleConstants.setForeground(styleImportant, Color.CYAN); 
					StyleConstants.setBold(styleImportant, true);
					styleDebug = new SimpleAttributeSet();
					StyleConstants.setForeground(styleDebug, Color.YELLOW); 
					StyleConstants.setBackground(styleDebug, Color.BLACK); 
					StyleConstants.setBold(styleDebug, false);
				}
				SimpleAttributeSet style = null;
				switch(mode){
				case ERROR:
					s = "Error: "+s;
					style = styleError;
					break;
				case WARNING:
					s = "Warning: "+s;
					style = styleWarning;
					break;
				case IMPORTANT:
					style = styleImportant;
					break;
				case MESSAGE:
					style = styleMessage;
					break;
				case DEBUG:
					s = "Debug: "+s;
					style = styleDebug;
					break;
				}
				// append
				doc.insertString(doc.getLength(), s+"\n", style);
			} catch(BadLocationException exc) {
				exc.printStackTrace();
			}	
		}
	}

	public static boolean isLogging() {
		return isLogging;
	}

	public static void setLogging(boolean isLogging) {
		ImageEditorWindow.isLogging = isLogging;
	}

	//##########################################################################################
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
	public JSplitPane getSplitNorthSouth() {
		return splitNorthSouth;
	}
	public JTextPane getTxtLog() {
		return txtLog;
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
		return (ModuleZoom) (activeModuleContainer==null? null : activeModuleContainer.getModuleByClass(ModuleZoom.class));
	}
	
	public ModuleThemes getModuleThemes() {
		return (ModuleThemes) (activeModuleContainer==null? null : activeModuleContainer.getModuleByClass(ModuleThemes.class));
	}

	/**
	 * the list of images or null if the editor is not initialized
	 * @return
	 */
	public static List<Collectable2D> getImages() { 
		if(getEditor()==null)
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
	public JCheckBoxMenuItem getCbKeepAspectRatio() {
		return cbKeepAspectRatio;
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

	public AspectRatioListener getAspectRatioListener() {
		return aspectRatioListener;
	}

}
