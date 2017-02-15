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
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
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
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.massimager.Frames.Dialogs.GraphicsExportDialog;
import net.rs.lamsi.massimager.Frames.Dialogs.ProgressDialog;
import net.rs.lamsi.massimager.Frames.FrameWork.ColorChangedListener;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.ImageModule;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.ImageSettingsModule;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.Module;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.ModuleTreeWithOptions;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.listeners.HideShowChangedListener;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.tree.IconNodeRenderer;
import net.rs.lamsi.massimager.Heatmap.Heatmap;
import net.rs.lamsi.massimager.MyFreeChart.ChartLogics;
import net.rs.lamsi.massimager.MyFreeChart.Plot.image2d.listener.AspectRatioListener;
import net.rs.lamsi.massimager.Settings.SettingsHolder;
import net.rs.lamsi.massimager.Settings.preferences.SettingsGeneralPreferences;
import net.rs.lamsi.multiimager.FrameModules.ModuleGeneral;
import net.rs.lamsi.multiimager.FrameModules.ModuleImage2D;
import net.rs.lamsi.multiimager.FrameModules.ModuleOperations;
import net.rs.lamsi.multiimager.FrameModules.ModulePaintscale;
import net.rs.lamsi.multiimager.FrameModules.ModuleThemes;
import net.rs.lamsi.multiimager.Frames.dialogs.DialogDataSaver;
import net.rs.lamsi.multiimager.Frames.dialogs.DialogPreferences;
import net.rs.lamsi.multiimager.Frames.dialogs.ImportDataDialog;
import net.rs.lamsi.multiimager.Frames.multiimageframe.MultiImageFrame;
import net.rs.lamsi.utils.DialogLoggerUtil;
import net.rs.lamsi.utils.WindowStyleUtil;

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
	// list of all Modules
	private Vector<ImageModule> listImageSettingsModules = new Vector<ImageModule>();
	

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

	private int currentView = VIEW_IMAGING_ANALYSIS;

	// AUTOGEN 
	//
	private JCheckBox cbAuto;
	private ModuleImage2D modImage2D;
	private ModuleGeneral moduleGeneral;
	private ModulePaintscale modulePaintscale;
	private ModuleThemes moduleThemes;
	private JPanel pnCenterImageView;	
	private ModuleTreeWithOptions<Image2D> moduleTreeImages;
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
	private ModuleOperations moduleOperations;
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

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmOpenImage = new JMenuItem("Open image");
		mntmOpenImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// load in logicRunner
				logicRunner.loadImage2DFromFile();
			}
		});
		mntmOpenImage.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		mnFile.add(mntmOpenImage);

		JMenuItem mntmSaveImage = new JMenuItem("Save image file");
		mntmSaveImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// save in logicRunner  
				logicRunner.saveImage2DToFile();
			}
		});
		mntmSaveImage.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		mnFile.add(mntmSaveImage);

		JSeparator separator = new JSeparator();
		mnFile.add(separator);

		JMenuItem mntmSaveDataAs = new JMenuItem("Export data");
		mntmSaveDataAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// open data export dialog
				DialogDataSaver.startDialogWith(logicRunner.getListImages(), logicRunner.getSelectedImage());
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

		JMenu mnImportData = new JMenu("Import data");
		mnImportData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// opens the import data frame 
				importDataDialog.setVisible(true);
			}
		});
		mnFile.add(mnImportData);


		JMenuItem mntmOpenDialog = new JMenuItem("Open dialog");
		mntmOpenDialog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// opens the import data frame 
				importDataDialog.setVisible(true);
			}
		});
		mnImportData.add(mntmOpenDialog);
		
		JMenu mnAction = new JMenu("Action");
		menuBar.add(mnAction);
		
		JMenuItem btnCrop = new JMenuItem("Crop images");
		btnCrop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Open Dialog
				
			}
		});
		mnAction.add(btnCrop);
		
		JMenuItem mntmCombineImages = new JMenuItem("Combine images");
		mnAction.add(mntmCombineImages);
		
		JMenuItem mntmSplitImages = new JMenuItem("Split images");
		mnAction.add(mntmSplitImages);


		JMenuItem btnImportMicroscopic = new JMenuItem("Add down sampled microscopic image");
		btnImportMicroscopic.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Open Dialog
				logicRunner.importMicroscopicImageDownSampled();
			}
		});
		mnAction.add(btnImportMicroscopic);
		
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
					Image2D[] img = getModuleTreeImages().getImageCollection(path);
					if(img!=null) {
						MultiImageFrame frame = new MultiImageFrame();
						frame.init(img); 
					}
				}
			}
		});
		mnView.add(mntmMultiImageExplorer);

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

		JPanel east = new JPanel();
		pnNorthContent.add(east, BorderLayout.EAST);
		east.setLayout(new BorderLayout(0, 0));

		modImage2D = new ModuleImage2D(this);
		east.add(modImage2D, BorderLayout.CENTER);

		JPanel pnTitleSettings = new JPanel();
		FlowLayout flowLayout = (FlowLayout) pnTitleSettings.getLayout();
		flowLayout.setHgap(4);
		flowLayout.setVgap(0);
		modImage2D.getPnTitle().add(pnTitleSettings, BorderLayout.CENTER);

		JButton btnApplySettingsToAll = new JButton("apply to all");
		btnApplySettingsToAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logicRunner.applySettingsToAllImagesInList();
			}
		});
		pnTitleSettings.add(btnApplySettingsToAll);

		JButton btnUpdate = new JButton("update");
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				writeAllSettingsFromModules();
			}
		});
		pnTitleSettings.add(btnUpdate);

		cbAuto = new JCheckBox("auto");
		cbAuto.setSelected(true);
		pnTitleSettings.add(cbAuto);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		modImage2D.getPnContent().add(scrollPane, BorderLayout.EAST);

		JPanel gridsettings = new JPanel();
		gridsettings.setAlignmentY(0.0f);
		gridsettings.setAlignmentX(0.0f);
		scrollPane.setViewportView(gridsettings);
		gridsettings.setLayout(new BoxLayout(gridsettings, BoxLayout.Y_AXIS));

		moduleGeneral = new ModuleGeneral(this);
		moduleGeneral.setAlignmentY(Component.TOP_ALIGNMENT);
		gridsettings.add(moduleGeneral);

		modulePaintscale = new ModulePaintscale();
		gridsettings.add(modulePaintscale);

		moduleThemes = new ModuleThemes();
		gridsettings.add(moduleThemes);

		moduleOperations = new ModuleOperations(this);
		gridsettings.add(moduleOperations);

		// add all modules for Image settings TODO add all mods
		listImageSettingsModules.addElement(modImage2D);
		listImageSettingsModules.addElement(moduleGeneral);
		listImageSettingsModules.addElement(moduleGeneral.getModSplitConImg());
		listImageSettingsModules.addElement(modulePaintscale);
		listImageSettingsModules.addElement(moduleThemes);
		listImageSettingsModules.addElement(moduleOperations);
		listImageSettingsModules.addElement(moduleOperations.getModQuantifier());
		listImageSettingsModules.addElement(moduleOperations.getModSelectExcludeData());

		splitPane = new JSplitPane(); 
		pnNorthContent.add(splitPane, BorderLayout.CENTER);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerSize(2);

		west = new JPanel();
		splitPane.setLeftComponent(west);
		west.setLayout(new BorderLayout(0, 0));

		// TODO richtig so?  
		moduleTreeImages = new ModuleTreeWithOptions<Image2D>("", true);
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
		pnCenterImageView.addComponentListener(new AspectRatioListener() { 
			@Override
			public void componentResized(ComponentEvent e) {
				// resize chart
				if(getCbKeepAspectRatio().isSelected() && pnChartAspectRatio !=null && (logicRunner).getCurrentHeat()!=null) { 
					resize(getLogicRunner().getCurrentHeat().getChartPanel(), getPnCenterImageView(), RATIO.LIMIT_TO_PARENT_SIZE);
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
		autoActionL = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(getCbAuto().isSelected()) writeAllSettingsFromModules();
			}
		};
		autoChangeL = new ChangeListener() { 
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if(getCbAuto().isSelected()) startAutoUpdater(); 
			}
		};
		autoDocumentL = new DocumentListener() { 
			@Override
			public void removeUpdate(DocumentEvent arg0) {  
				if(getCbAuto().isSelected()) startAutoUpdater(); 
			} 
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				if(getCbAuto().isSelected()) startAutoUpdater(); 
			} 
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				if(getCbAuto().isSelected()) startAutoUpdater(); 
			}
		};
		autoColorChangedL = new ColorChangedListener() { 
			@Override
			public void colorChanged(Color color) {
				if(getCbAuto().isSelected()) writeAllSettingsFromModules(); 
			}
		};
		autoItemL = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(getCbAuto().isSelected()) writeAllSettingsFromModules();
			}
		};
		// add to MODULES TODO 
		for(ImageModule m : listImageSettingsModules) {
			m.addAutoupdater(autoActionL, autoChangeL, autoDocumentL, autoColorChangedL, autoItemL);
		}  
	} 
	
	/**
	 * starts the auto update function
	 */
	public void startAutoUpdater() {
		lastAutoUpdateTime = System.currentTimeMillis();
		if(!isAutoUpdateStarted &&  ImageLogicRunner.IS_UPDATING()) { 
			ImageEditorWindow.log("Auto update started", LOG.DEBUG);
			isAutoUpdateStarted = true;
			Thread t = new Thread(this);
			t.start();
		}
		else  
			ImageEditorWindow.log("no auto update this time", LOG.DEBUG);
	}
	@Override
	public void run() {
		while(true) {
			if(lastAutoUpdateTime+AUTO_UPDATE_TIME<=System.currentTimeMillis()) {
				writeAllSettingsFromModules();
				lastAutoUpdateTime=-1;
				isAutoUpdateStarted = false;
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
		if(!checks || getCbAuto().isSelected()) {
			writeAllSettingsFromModules();
		}
	}
	/**
	 * reads all settings from all modules
	 * renews the heatmap that is shown --> update in modules
	 */ 
	private void writeAllSettingsFromModules() {
		// 
		ImageEditorWindow.log("Write all Settings from all Modules", LOG.DEBUG);
		// TODO Write all to Settings 
		for(ImageModule m : listImageSettingsModules) {
			if(m instanceof ImageSettingsModule)
				((ImageSettingsModule)m).writeAllToSettings();
		}  
		// show Image 
		if(ImageLogicRunner.IS_UPDATING()) {
			Heatmap heat = logicRunner.renewImage2DView();
		}
	} 
	
	/**
	 * sets the image to all imagemodules: gets called first (then addHeatmapToPanel)
	 * @param img
	 */
	public void setImage2D(Image2D img) { 
		boolean isauto = cbAuto.isSelected();
		cbAuto.setSelected(false); 
		// finished
		ImageLogicRunner.setIS_UPDATING(false);
		// set
		for(ImageModule m : listImageSettingsModules) {
			m.setCurrentImage(img); 
		} 
		// finished
		ImageLogicRunner.setIS_UPDATING(true);
		cbAuto.setSelected(isauto);
	} 
	
	/**
	 * Visualization:
	 * Adds a heatmap (Chartpanel) to the center view
	 * Adds a heatmap to all imagemodules
	 * called from {@link ImageLogicRunner#renewImage2DView()}.
	 * @param heat 
	 */
	public void addHeatmapToPanel(Heatmap heat) { 
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
		for(ImageModule m : listImageSettingsModules) {
			m.setCurrentHeatmap(heat);
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
	 * prints a message to the log 
	 * @param s
	 * @param mode LOG is in this class
	 */
	public static void log(String s, LOG mode) {
		if(txtLog!=null && isLogging() && getEditor()!=null && !(mode==LOG.DEBUG && !getEditor().getCbDebug().isSelected())) {
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
	public JCheckBox getCbAuto() {
		return cbAuto;
	}
	public ModuleGeneral getModuleGeneral() {
		return moduleGeneral;
	}
	public ModulePaintscale getModulePaintscale() {
		return modulePaintscale;
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
	public ModuleOperations getModuleOperations() {
		return moduleOperations;
	}

	/**
	 * the list of images or null if the editor is not initialized
	 * @return
	 */
	public static Vector<Image2D> getImages() { 
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
}
