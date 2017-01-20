package net.rs.lamsi.massimager.Frames;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.rs.lamsi.massimager.Frames.Dialogs.ChargeCalculatorSettingsDialog;
import net.rs.lamsi.massimager.Frames.Dialogs.ColorPickerDialog;
import net.rs.lamsi.massimager.Frames.Dialogs.DataSaverFrame;
import net.rs.lamsi.massimager.Frames.Dialogs.GraphicsExportDialog;
import net.rs.lamsi.massimager.Frames.Dialogs.ProgressDialog;
import net.rs.lamsi.massimager.Frames.Dialogs.generalsettings.GeneralSettingsFrame;
import net.rs.lamsi.massimager.Frames.Panels.ImageVsSpecViewPanel;
import net.rs.lamsi.massimager.Heatmap.HeatmapFactory;
import net.rs.lamsi.massimager.Image.Image2D;
import net.rs.lamsi.massimager.MyException.NoFileSelectedException;
import net.rs.lamsi.massimager.MyFileChooser.FileTypeFilter;
import net.rs.lamsi.massimager.MyMZ.MZIon;
import net.rs.lamsi.massimager.Settings.SettingsDataSaver;
import net.rs.lamsi.massimager.Settings.SettingsHolder;
import net.rs.lamsi.massimager.mzmine.MZMineCallBackListener;
import net.rs.lamsi.massimager.mzmine.MZMineLogicsConnector;
import net.rs.lamsi.massimager.mzmine.interfaces.MZMinePeakListsChangedListener;
import net.rs.lamsi.massimager.mzmine.interfaces.MZMineRawDataListsChangedListener;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.utils.DialogLoggerUtil;
import net.rs.lamsi.utils.mywriterreader.XSSFExcelWriterReader;
import net.sf.mzmine.datamodel.PeakList;
import net.sf.mzmine.datamodel.RawDataFile;


public class Window {

	public static final int MODE_MS = 0, MODE_OES = 1; 
	// My Stuff 
	//###############################################################################
    // Komponenten    
	// Image Manipulator Window
	private ImageEditorWindow imageEditorWnd;
	// DAs Programm
	private static Window window; 
	private LogicRunner runner;
	private OESPanel runnerOES; 
	// other frames
	private DataSaverFrame frameDataSaver;
	private GeneralSettingsFrame frameGeneralSettings;
	private ColorPickerDialog frameColorPicker; 
	protected ChargeCalculatorSettingsDialog dialogChargeCalculatorSettings; 
	// settings schreiben mit BinaryWriter 
	private XSSFExcelWriterReader excelWriter;
	// 
	private HeatmapFactory heatFactory = new HeatmapFactory();
    
	// FileChooser fcDirectoriesChooser
	final private JFileChooser fcDirectoriesChooser = new JFileChooser();  
	final private JFileChooser fcOpenMS = new JFileChooser();  
	final private JFileChooser fcOpenOES = new JFileChooser();  
	final private JFileChooser fcSaveImage = new JFileChooser();  
	final private JFileChooser fcSaveData  = new JFileChooser();
	// Menu 
	JMenuBar menuBar;
	JMenu menu, submenu;
	JMenuItem menuItem; 
	private JMenuItem mntmLoadGraphicsSettings;
	private JMenuItem mntmSaveGraphicsSettings;
	private JMenuItem mntmGraphicsSettings;
	JRadioButtonMenuItem rbMenuItem;
	// Alle Radiobuttons für views in array
	JRadioButtonMenuItem[] rbMenuView = new JRadioButtonMenuItem[LogicRunner.VIEW_COUNT];
	JRadioButtonMenuItem[] rbMenuMode = new JRadioButtonMenuItem[LogicRunner.MODE_COUNT];
	// Für jedes View ein Panel
    Vector<JPanel> listPanelViews = new Vector<JPanel>();
    
    //Variablen
    private int currentMode=0;
    
	//###############################################################################

	private JFrame frame;
	private JTextField txtMZ;
	private JTextField txtMZplusminus;
	private JList listMZ;
	private JTextField txtNameMZ;
	private JTextField txtRetentionTime;
	private JPanel pnChartViewMZChrom;
	private JTabbedPane tabpnMS;
	private JPanel pnChartViewSpec;
	private JSlider sliderRT;
	private JList listFiles;
	private JCheckBox cbShowFiles;
	private JCheckBox cbHideFiles;
	private JPanel pnFiles;
	private JPanel pnFilesHidden;
	private JTextField txtVelocity;
	private JTextField txtSpotSize;
	private JTextField txtTimePerRow;
	private JTextField txtTimeToAdd;
	private JButton btnPlusTime;
	private JButton btnMinusTime;
	private JPanel pnChartViewMSICon;
	private JTextField txtMSIMZ;
	private JTextField txtMSIPM;
	private JTextField txtMSIDisconVelocity;
	private JTextField txtMSIDisconSpotsize;
	private JTextField txtMSIDisconMZ;
	private JTextField txtMSIDisconPM;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JRadioButton rbMSIAllFiles;
	private JPanel pnChartViewMSIDiscon;
	private JPanel pnOESCenterContent;
	private JPanel pnMSCenterContent;
	private JPanel CenterContent;
	private ImageVsSpecViewPanel tabThresomeTICvsMZvsSpec;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		// connect to mzmine 
		// have to load it before everything else
		MZMineLogicsConnector.connectToMZMine();
		
		// start MassImager application
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Window window = new Window();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Window() {
		window = this;
		// create settings
		excelWriter = new XSSFExcelWriterReader();
		
		// Programm Logic erstellen
		runner = new LogicRunner(this); 
		
		initialize(); 
        
        // init and place components
        initComponents();
        
        // Create Menu
        createMenu();
        
        // SetUp FileChooser
        setUpFileChooser();
        
        // register MZMine listeners for RawDataFiles and PeaksLists
        registerMZMineListeners();
        
        // createOther Frames and Dialogs
        createFramesAndDialogs();
        
        // create ImageEditorWindow
        createImageEditorWnd(); 
	}

	// listen for new PeakLists or RawDataFiles
	private void registerMZMineListeners() {
		MZMineRawDataListsChangedListener rawListener = new MZMineRawDataListsChangedListener() { 
			@Override
			public void rawDataListsChanged(RawDataFile[] rawDataLists) {
				DefaultListModel model = (DefaultListModel) getListFiles().getModel();
				model.removeAllElements();
				// get list of rawdata files 
				RawDataFile[] rawList = MZMineLogicsConnector.getRawDataLists();
				Vector<RawDataFile> runnerRawList = runner.getListSpecFiles();
				runnerRawList.removeAllElements();
				// insert sort
				for (int i = 0; i < rawList.length; i++) {
					RawDataFile raw = rawList[i];
					boolean added = false;
					for(int x=0; x < runnerRawList.size() && !added; x++) {
						if(runnerRawList.get(x).getName().compareTo(raw.getName()) >= 0) {
							// insert in list
							runnerRawList.insertElementAt(raw, x);
							model.insertElementAt(raw.getName(), x);
							added = true;
						}
					}
					// if not added then add to lists
					if(!added) {
						runnerRawList.add(raw);
						model.addElement(raw.getName());
					}
				}
			}
		};
		
		MZMinePeakListsChangedListener peakListener = new MZMinePeakListsChangedListener() { 
			@Override
			public void peakListsChanged(PeakList[] peakLists) {
				// TODO Auto-generated method stub
				
			}
		};
		
		MZMineCallBackListener.addMZMinePeakListChangedListener(peakListener);
		MZMineCallBackListener.addMZMineRawDataListChangedListener(rawListener);
	}

	// second App: ImageEditorWindow for final image processing
	private void createImageEditorWnd() {
		// Do not close wnd on X
		imageEditorWnd = new ImageEditorWindow();
		imageEditorWnd.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		imageEditorWnd.setVisible(false); 
	}

	// 
	private void createFramesAndDialogs() {
		frameDataSaver = new DataSaverFrame(this, getSettings());
		frameDataSaver.setVisible(false);
		// Settings frames
		frameGeneralSettings = new GeneralSettingsFrame(this);
		frameGeneralSettings.setVisible(false);
		
		frameColorPicker = new ColorPickerDialog(this);
		frameColorPicker.setVisible(false);
		// Progress Dialog
		ProgressDialog.initDialog(getFrame());
		// charge calc settings dialog 
		dialogChargeCalculatorSettings = new ChargeCalculatorSettingsDialog();
		// graphics export 
		GraphicsExportDialog.createInstance();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 931, 626);
		//frame.setDefaultCloseOperation(MyFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            	try { 
                	runner.closeAll(); 
            	}catch(Exception ex) { 
            	}
            	if(getImageEditorWnd().isVisible()) {
            		// Window offen lassen und durch image editor schließen
            		getImageEditorWnd().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            	}
            	else System.exit(0);
            }
         });

		DefaultListModel listModel = new DefaultListModel();
		DefaultListModel listModelFiles = new DefaultListModel();
		
		// TODO tmp adding ASS ion
		double currentMZ = 121.029, currentPM = 0.3;
		MZIon mzIon = new MZIon("ASS", currentMZ, currentPM);
		runner.getListMZIon().add(0, mzIon);


		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel pnFirst = new JPanel();
		frame.getContentPane().add(pnFirst, BorderLayout.CENTER);
        pnFirst.setLayout(new BorderLayout(0, 0));
        
        JPanel leftFilesBoth = new JPanel();
        leftFilesBoth.setMaximumSize(new Dimension(300, 32767));
        pnFirst.add(leftFilesBoth, BorderLayout.WEST);
        leftFilesBoth.setLayout(new BorderLayout(0, 0));
        
        pnFiles = new JPanel();
        pnFiles.setMaximumSize(new Dimension(300, 32767));
        leftFilesBoth.add(pnFiles, BorderLayout.WEST);
        pnFiles.setLayout(new BorderLayout(0, 0));
        
        JPanel top = new JPanel();
        top.setMaximumSize(new Dimension(300, 32767));
        pnFiles.add(top, BorderLayout.NORTH);
        top.setLayout(new BorderLayout(0, 0));
        
        JPanel panel = new JPanel();
        top.add(panel);
        
        JButton btnLoadNewFile = new JButton("Load new file");
        btnLoadNewFile.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) { 
				loadFilesForMode();
        	}
        });
        panel.add(btnLoadNewFile);
        
        JButton btnRemoveFile = new JButton("Remove");
        btnRemoveFile.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		try{
        			int[] sel = getListFiles().getSelectedIndices();
        			if(sel.length>0) {
        				for(int i=0; i<sel.length; i++) {
        					((DefaultListModel)getListFiles().getModel()).remove(sel[i]-i);
        					// Remove file from List 
                			// FilesList from Selected Mode 
        					if(getCurrentMode()==MODE_MS) {
        						runner.getListSpecFiles().remove(sel[i]-i);
        					}
        					else if(getCurrentMode()==MODE_OES) {
        						runnerOES.getListOESFiles().remove(sel[i]-i);
        					}
        				}
        			}
        			//  set index to 0 and show
        			getListFiles().setSelectedIndex(0);
        			runner.setSelectedFileIndex(getListFiles().getSelectedIndex());
        		}catch(Exception ex) {
        			ex.printStackTrace();
        		}
        	}
        });
        panel.add(btnRemoveFile);
        
        cbHideFiles = new JCheckBox("Hide");
        cbHideFiles.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		showFilesPanel(false);
        	}
        });
        top.add(cbHideFiles, BorderLayout.EAST);
        
        JScrollPane scrollPane_2 = new JScrollPane();
        pnFiles.add(scrollPane_2, BorderLayout.CENTER);
        scrollPane_2.setMaximumSize(new Dimension(300, 32767));
        
        listFiles = new JList((ListModel) listModelFiles);
        listFiles.setMaximumSize(new Dimension(300, 0));
        listFiles.addListSelectionListener(new ListSelectionListener() {
        	public void valueChanged(ListSelectionEvent e) {
        		JList lsm = (JList)e.getSource();
        		// update all
        		if(e.getValueIsAdjusting() == false){ 
        			setNewFileSelectedInMode(lsm.getSelectedIndex()); 
        		}
        	}
        });
        scrollPane_2.setViewportView(listFiles);
        
        
        Component horizontalStrut = Box.createHorizontalStrut(5);
        leftFilesBoth.add(horizontalStrut, BorderLayout.EAST);
        
        pnFilesHidden = new JPanel();
        leftFilesBoth.add(pnFilesHidden, BorderLayout.CENTER);
        pnFilesHidden.setLayout(new BorderLayout(0, 0));
        pnFilesHidden.setVisible(false);
        
        cbShowFiles = new JCheckBox("");
        pnFilesHidden.add(cbShowFiles, BorderLayout.NORTH);
        cbShowFiles.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		showFilesPanel(true);
        	}
        });
        
        JPanel pnFilesHidden2 = new JPanel();
        pnFilesHidden.add(pnFilesHidden2, BorderLayout.SOUTH);
        pnFilesHidden2.setLayout(null);
        pnFilesHidden2.setVisible(false);
        
        Component horizontalStrut_1 = Box.createHorizontalStrut(20);
        pnFilesHidden.add(horizontalStrut_1, BorderLayout.WEST);
        
        CenterContent = new JPanel();
        pnFirst.add(CenterContent, BorderLayout.CENTER);
        CenterContent.setLayout(new BorderLayout(0, 0));
        
        pnMSCenterContent = new JPanel();
        CenterContent.add(pnMSCenterContent, BorderLayout.CENTER);
        pnMSCenterContent.setLayout(new BorderLayout(0, 0));
        
        tabpnMS = new JTabbedPane(JTabbedPane.TOP);
        pnMSCenterContent.add(tabpnMS);
        
        tabThresomeTICvsMZvsSpec = new ImageVsSpecViewPanel(this);
        tabThresomeTICvsMZvsSpec.addComponentListener(new ComponentAdapter() {
        	@Override
        	public void componentShown(ComponentEvent e) {
        		getTabThresomeTICvsMZvsSpec().setIsShown();
        	}
        });
        tabpnMS.addTab("ImageVsSpec", null, tabThresomeTICvsMZvsSpec, null);
        
        pnOESCenterContent = new JPanel();
        CenterContent.add(pnOESCenterContent, BorderLayout.WEST);
        pnOESCenterContent.setLayout(new BorderLayout(0, 0));
        pnOESCenterContent.setVisible(false);
        
        runnerOES = new OESPanel(this);
        pnOESCenterContent.add(runnerOES, BorderLayout.CENTER); 
        runnerOES.setVisible(true);
	}
	
	protected void setNewFileSelectedInMode(int selectedIndex) {
		// TODO Auto-generated method stub
		if(currentMode == MODE_MS)  runner.setNewFileSelectedAndShowAll(selectedIndex);
		if(currentMode == MODE_OES)  runnerOES.setSelectedOESFile(selectedIndex);
	}

	// shows and hides the files panel at west
	protected void showFilesPanel(boolean show) {
		getCbHideFiles().setSelected(true);
		getCbShowFiles().setSelected(false);
		// how pn or hide
		getPnFiles().setVisible(show);
		getPnFilesHidden().setVisible(!show);
		//
		if(show) System.out.println("SHOW");
		else System.out.println("HIDE");
	}

	public JList getListMZ() {
		return listMZ;
	}
	public JTextField getTxtRetentionTime() {
		return txtRetentionTime;
	}
	public JTextField getTxtNameMZ() {
		return txtNameMZ;
	}
	public JTextField getTxtMZ() {
		return txtMZ;
	}
	public JTextField getTxtMZplusminus() {
		return txtMZplusminus;
	}
	
	
	
	//####################################################################################################
	// Mein Stuff


	private void initComponents() { 
	}

	// Create MenuBar on Top
    private void createMenu() {
    	menuBar = new JMenuBar();
    	// First Menu File
    	menu = new JMenu("File");
    	menu.setMnemonic(KeyEvent.VK_F);
    	menu.getAccessibleContext().setAccessibleDescription("File menu");
    	menu.setToolTipText(menu.getAccessibleContext().getAccessibleDescription());
    	
    	//a group of JMenuItems
    	menuItem = new JMenuItem("Load file");
    	menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
    	menuItem.getAccessibleContext().setAccessibleDescription("Import mzXML files");
    	menuItem.setToolTipText(menuItem.getAccessibleContext().getAccessibleDescription());
    	menuItem.addActionListener(new ActionListener() { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
				loadFilesForMode();
			}
		});
    	menu.add(menuItem);
    	//Save Data
    	menuItem = new JMenuItem("Save data as");
    	menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
    	menuItem.getAccessibleContext().setAccessibleDescription("Save as Excel table or text data");
    	menuItem.setToolTipText(menuItem.getAccessibleContext().getAccessibleDescription());
    	menuItem.addActionListener(new ActionListener() { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// open DataSaverFrame
				frameDataSaver.setCurrentMode(getCurrentMode());
				frameDataSaver.setVisible(true);
			}
		});
    	menu.add(menuItem);  
    	//Save Image
    	menuItem = new JMenuItem("Save image as");
    	menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_MASK));
    	menuItem.getAccessibleContext().setAccessibleDescription("Save as image (PNG, EPS, PDF)");
    	menuItem.setToolTipText(menuItem.getAccessibleContext().getAccessibleDescription());
    	menuItem.addActionListener(new ActionListener() { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
				runner.saveImageFile();
			}
		});
    	menu.add(menuItem);  
    	menu.addSeparator();
    	
    	//Save parameters
    	menuItem = new JMenuItem("Save parameters");
    	menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
    	menuItem.getAccessibleContext().setAccessibleDescription("Saves all data related parameters to a file");
    	menuItem.setToolTipText(menuItem.getAccessibleContext().getAccessibleDescription());
    	menuItem.addActionListener(new ActionListener() { 
			@Override
			public void actionPerformed(ActionEvent arg0) { 
				// Save Settings Holder
				try {
					getSettings().saveSettingsToFile(window.getFrame(), getSettings());
				} catch (Exception e) {
					DialogLoggerUtil.showErrorDialog(getFrame(), "Error while saving", e);
					e.printStackTrace();
				}
			}
		});
    	menu.add(menuItem);  
    	//load parameters
    	menuItem = new JMenuItem("Load parameters");
    	menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.ALT_MASK));
    	menuItem.getAccessibleContext().setAccessibleDescription("Loades all data related parameters from a file");
    	menuItem.setToolTipText(menuItem.getAccessibleContext().getAccessibleDescription());
    	menuItem.addActionListener(new ActionListener() { 
			@Override
			public void actionPerformed(ActionEvent arg0) { 
				// Load settings Holder for all settings
				try {
					getSettings().loadSettingsFromFile(getFrame(), getSettings());
				} catch (Exception e) { 
					e.printStackTrace();
					DialogLoggerUtil.showErrorDialog(getFrame(), "Error while loading ", e);
				}
				// TODO
				// all refresh
				frameGeneralSettings.setAllSettingsOnPanel();
				frameDataSaver.setAllSettingsCb();
			}
		});
    	menu.add(menuItem);  
    	// add
    	menuBar.add(menu);
    	
    	// Second Menu: GRAPHICS
    	menu = new JMenu("Graphics");
    	menu.setMnemonic(KeyEvent.VK_G);
    	menu.getAccessibleContext().setAccessibleDescription("Graphics menu");
    	menu.setToolTipText(menu.getAccessibleContext().getAccessibleDescription());
    	
    	//a group of JMenuItems
    	mntmGraphicsSettings = new JMenuItem("Color gradient settings");
    	mntmGraphicsSettings.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.ALT_MASK));
    	mntmGraphicsSettings.getAccessibleContext().setAccessibleDescription("Opens the gradient settings");
    	mntmGraphicsSettings.setToolTipText(mntmGraphicsSettings.getAccessibleContext().getAccessibleDescription());
    	mntmGraphicsSettings.addActionListener(new ActionListener() { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO wenn er nicht offen dann den Dialog öffnen.
				frameColorPicker.setVisible(true);
			}
		});
    	menu.add(mntmGraphicsSettings);
    	// PaintScale Settings  
    	mntmGraphicsSettings = new JMenuItem("Graphics settings");
    	mntmGraphicsSettings.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.ALT_MASK));
    	mntmGraphicsSettings.getAccessibleContext().setAccessibleDescription("Opens the Plot-setup frame");
    	mntmGraphicsSettings.setToolTipText(mntmGraphicsSettings.getAccessibleContext().getAccessibleDescription());
    	mntmGraphicsSettings.addActionListener(new ActionListener() { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO
			}
		});
    	menu.add(mntmGraphicsSettings);
    	//Save
    	menuItem = new JMenuItem("Export graphics");
    	menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, ActionEvent.ALT_MASK));
    	menuItem.getAccessibleContext().setAccessibleDescription("Save as image");
    	menuItem.setToolTipText(menuItem.getAccessibleContext().getAccessibleDescription());
    	menuItem.addActionListener(new ActionListener() { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO
				ProgressDialog.getInst().setVisibleDialog(true);
				ProgressDialog.setProgress(500);
			}
		});
    	menu.add(menuItem);  
    	//Save Graphics Parameters
    	mntmSaveGraphicsSettings = new JMenuItem("Save graphics settings");
    	mntmSaveGraphicsSettings.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_5, ActionEvent.ALT_MASK));
    	mntmSaveGraphicsSettings.getAccessibleContext().setAccessibleDescription("Save all graphics related parameters in a file");
    	mntmSaveGraphicsSettings.setToolTipText(mntmSaveGraphicsSettings.getAccessibleContext().getAccessibleDescription());
    	mntmSaveGraphicsSettings.addActionListener(new ActionListener() { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO
			}
		});
    	menu.add(mntmSaveGraphicsSettings);  
    	// add
    	menuBar.add(menu);
    	//load Graphics Parameters
    	mntmLoadGraphicsSettings = new JMenuItem("Load graphics settings");
    	mntmLoadGraphicsSettings.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_6, ActionEvent.ALT_MASK));
    	mntmLoadGraphicsSettings.getAccessibleContext().setAccessibleDescription("Loads all graphics related parameters from a file");
    	mntmLoadGraphicsSettings.setToolTipText(mntmLoadGraphicsSettings.getAccessibleContext().getAccessibleDescription());
    	mntmLoadGraphicsSettings.addActionListener(new ActionListener() { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO
			}
		});
    	menu.add(mntmLoadGraphicsSettings);  
    	// add
    	menuBar.add(menu);
    	
    	// Listerner erstellen für rbChange
    	ChangeListener listenerModeViewChanged = new ChangeListener() { 
			@Override
			public void stateChanged(ChangeEvent e) {
					switchModeState();
			}
		};
    	//
    	// NEW MENU: MODE 
    	menu = new JMenu("MODE"); 
    	menu.setMnemonic(KeyEvent.VK_M);
    	menu.getAccessibleContext().setAccessibleDescription("Select the mode");
    	menu.setToolTipText(menu.getAccessibleContext().getAccessibleDescription());
    	menuBar.add(menu);
    	
    	//a group of radio button menu items
    	ButtonGroup group = new ButtonGroup();
    	rbMenuItem = new JRadioButtonMenuItem("Mass spectrometrie");
    	rbMenuItem.setToolTipText("Shows MS-options");
    	rbMenuItem.getAccessibleContext().setAccessibleDescription("Shows MS-options");
    	rbMenuItem.addChangeListener(listenerModeViewChanged);
    	rbMenuItem.setSelected(true); 
    	group.add(rbMenuItem);
    	menu.add(rbMenuItem);

    	rbMenuItem = new JRadioButtonMenuItem("ICP - OES"); 
    	rbMenuItem.setToolTipText("Shows OES-options");
    	rbMenuItem.getAccessibleContext().setAccessibleDescription("Shows OES-options");
    	rbMenuItem.addChangeListener(listenerModeViewChanged);
    	group.add(rbMenuItem);
    	menu.add(rbMenuItem);
    	// add
    	menuBar.add(menu);

    	// group to array
    	Enumeration<AbstractButton> btns = group.getElements();
    	for(int i=0; i<group.getButtonCount(); i++) { 
    		if(btns.hasMoreElements())
    			rbMenuMode[i] = (JRadioButtonMenuItem) btns.nextElement();
    	}
    	
    	//a group of radio button menu items
    	group = new ButtonGroup();
    	// group to array
    	btns = group.getElements();
    	for(int i=0; i<group.getButtonCount(); i++) { 
    		if(btns.hasMoreElements())
    			rbMenuView[i] = (JRadioButtonMenuItem) btns.nextElement();
    	}
    	

    	// NEW MENU: Toolset 
    	menu = new JMenu("Toolset"); 
    	menu.setMnemonic(KeyEvent.VK_V);
    	menu.getAccessibleContext().setAccessibleDescription("Start other apps from toolset");
    	menu.setToolTipText(menu.getAccessibleContext().getAccessibleDescription());
    	menuBar.add(menu);
    	
    	//a group of radio button menu items
    	//Save Graphics Parameters
    	menuItem = new JMenuItem("Start ImageEditor");
    	menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.ALT_MASK));
    	menuItem.getAccessibleContext().setAccessibleDescription("Starts the ImageEditor");
    	menuItem.setToolTipText(menuItem.getAccessibleContext().getAccessibleDescription());
    	menuItem.addActionListener(new ActionListener() { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Open settings Frame
				getImageEditorWnd().setVisible(true);
			}
		});
    	menu.add(menuItem); 
    	
    	// NEW MENU: Help 
    	menu = new JMenu("Help"); 
    	menu.setMnemonic(KeyEvent.VK_V);
    	menu.getAccessibleContext().setAccessibleDescription("Settings and other stuff");
    	menu.setToolTipText(menu.getAccessibleContext().getAccessibleDescription());
    	menuBar.add(menu);
    	
    	//a group of radio button menu items
    	//Save Graphics Parameters
    	menuItem = new JMenuItem("General settings");
    	menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.ALT_MASK));
    	menuItem.getAccessibleContext().setAccessibleDescription("General settings for converters and other stuff");
    	menuItem.setToolTipText(menuItem.getAccessibleContext().getAccessibleDescription());
    	menuItem.addActionListener(new ActionListener() { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Open settings Frame
				frameGeneralSettings.setVisible(true);
			}
		});
    	menu.add(menuItem); 
    	
    	// Set Menu
    	frame.setJMenuBar(menuBar);
	} 
    
    // Load files in right Mode
    protected void loadFilesForMode() { 
    	if(currentMode == MODE_MS) {
    		runner.loadFiles();
    	}
    	else {
    		runnerOES.loadFiles();
    	}
	}

	// Load and save FC FileChooser
    private void setUpFileChooser() {
		// add Filter 
        fcOpenMS.addChoosableFileFilter(new FileTypeFilter("mzXML", "Opens ms-data"));
        fcOpenMS.addChoosableFileFilter(new FileTypeFilter("RAW", "Opens ms-data"));
        fcOpenMS.setMultiSelectionEnabled(true);
        // OES File Chooser Open
        fcOpenOES.addChoosableFileFilter(new FileTypeFilter("txt", "Opens OES-data from text file")); 
        fcOpenOES.setMultiSelectionEnabled(true);
        // Directory chooser
        fcDirectoriesChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        // Save filter  nur directories auswählen
        fcSaveData.addChoosableFileFilter(new FileTypeFilter("xlsx", "Save to excel table"));
        fcSaveData.addChoosableFileFilter(new FileTypeFilter("txt", "Save to text file"));
        // Images
        fcSaveImage.addChoosableFileFilter(new FileTypeFilter("PNG", "Save image as "));
        fcSaveImage.addChoosableFileFilter(new FileTypeFilter("EPS", "Save image as "));
        fcSaveImage.addChoosableFileFilter(new FileTypeFilter("SVG", "Save image as "));
        fcSaveImage.addChoosableFileFilter(new FileTypeFilter("PDF", "Save image as "));
	}
    
    // Init Complete
	//###############################################################################

	//###############################################################################
    // OBERFLÄCHEN LOGIC
    public void switchModeState() {
    	try {
    		if(rbMenuMode!=null && rbMenuMode[0]!=null) {
	    		// TODO
	    		boolean isMS = rbMenuMode[0].isSelected();
	    		// Panel 
	    		JPanel center = getCenterContent(); 
	            if(isMS && currentMode!=MODE_MS) {
	            	center.add(getPnMSCenterContent(), BorderLayout.CENTER); 
		    		// Beide regeln selbst was alles gemacht werden muss. 
		    		// Files aus Liste raus  
	            	((DefaultListModel)listFiles.getModel()).removeAllElements(); 
	            	// Files wieder rein tun
			    	runner.setAsCurrentMode(listFiles);
	            }
	            else if(!isMS && currentMode!=MODE_OES) {
	            	center.add(getPnOESCenterContent(), BorderLayout.CENTER); 
		    		// Beide regeln selbst was alles gemacht werden muss. 
		    		// Files aus Liste raus  
	            	((DefaultListModel)listFiles.getModel()).removeAllElements(); 
	            	// Files wieder rein tun 
			    	runnerOES.setAsCurrentMode(listFiles);
	            }
	    		// Panel an und aus
	    		getPnOESCenterContent().setVisible(!isMS);
	    		getPnMSCenterContent().setVisible(isMS); 
		    	// welcher views sollen an_
	    		currentMode = (isMS? MODE_MS : MODE_OES);
    		}
    	}catch(Exception ex) {
    		ex.printStackTrace();
    	}
    }
    
    public File getFileFromFileChooser(JFileChooser fc) {   
        if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile();  
        }
        else return null;
    }
    public File[] getFilesFromFileChooser(JFileChooser fc) {   
        if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFiles();  
        }
        else return null;
    }
    
    public boolean createDirectory(File theDir) {
    	// if the directory does not exist, create it
    	  if (!theDir.exists()) { 
    		boolean result = false; 
    	    try{
    	        theDir.mkdirs();
    	        result = true;
    	     } catch(SecurityException se){
    	        //handle it
    	     }        
    	     if(result) {    
    	       System.out.println("DIR created");  
    	     } 
    	     return result;
    	  }
    	  else return true;
    }
     
    //###############################################################################
    // SAVE AND LOAD FILES 
	public boolean saveDataFile(SettingsDataSaver setDataSaver, XSSFExcelWriterReader excelWriter, int currentMode) throws Exception, NoFileSelectedException {
		// daten des aktuellen modes speichern mit den einstellungen 
		if(currentMode == MODE_MS) 
			return getLogicRunner().saveDataFile(setDataSaver, excelWriter);
		else  
			return runnerOES.saveDataFile(setDataSaver, excelWriter);
	}
    
	// Send image2d to Imageeditor for further work
	// will be inserted in ImageList
	public void sendImage2DToImageEditor(Image2D image, String id) {
		imageEditorWnd.getLogicRunner().addImage(image, id);
	}
    // OBERFLÄCHEN LOGIC ENDE
	//###############################################################################  
    
    public static File getPathOfJar() {
    	/*
    	File f = new File(System.getProperty("java.class.path"));
    	File dir = f.getAbsoluteFile().getParentFile(); 
    	return dir; 
    	 */
    	try {
    	File jar = new File(Window.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
    	return jar.getParentFile();
    	}catch(Exception ex) {
    		return new File("");
    	}
    }
    
    public double round(double x, double comma) {
    	return ((int)(x*Math.pow(10, comma)))/Math.pow(10, comma);
    }
    public double round(float x, double comma) {
    	return ((int)(x*Math.pow(10, comma)))/Math.pow(10, comma);
    }
    
	public JPanel getPnChartViewMZChrom() {
		return pnChartViewMZChrom;
	}
	public JTabbedPane getTabpnMS() {
		return tabpnMS;
	} 

	public JFileChooser getFcOpenMS() {
		return fcOpenMS;
	}

	public JFileChooser getFcSaveImage() {
		return fcSaveImage;
	}

	public JFileChooser getFcSaveData() {
		return fcSaveData;
	}
	public JList getListFiles() {
		return listFiles;
	}
	public JPanel getPnChartViewSpec() {
		return pnChartViewSpec;
	}
	public JSlider getSliderRT() {
		return sliderRT;
	} 
	public JFileChooser getFcDirectoriesChooser() {
		return fcDirectoriesChooser;
	}
	public LogicRunner getLogicRunner() {
		return runner;
	}

	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	} 

	
	public SettingsHolder getSettings() {
		return SettingsHolder.getSettings();
	} 
	public JCheckBox getCbShowFiles() {
		return cbShowFiles;
	}
	public JCheckBox getCbHideFiles() {
		return cbHideFiles;
	}
	public JPanel getPnFiles() {
		return pnFiles;
	}
	public JPanel getPnFilesHidden() {
		return pnFilesHidden;
	}  
	public JTextField getTxtTimeToAdd() {
		return txtTimeToAdd;
	}
	public JTextField getTxtTimePerRow() {
		return txtTimePerRow;
	}
	public JTextField getTxtVelocity() {
		return txtVelocity;
	}
	public JTextField getTxtSpotSize() {
		return txtSpotSize;
	}
	public JButton getBtnNewButton() {
		return btnPlusTime;
	}
	public JButton getButton() {
		return btnMinusTime;
	}
	public JPanel getnChartViewMSICon() {
		return pnChartViewMSICon;
	}
	public JTextField getTxtMSIMZ() {
		return txtMSIMZ;
	}
	public JTextField getTxtMSIPM() {
		return txtMSIPM;
	}

	public HeatmapFactory getHeatFactory() {
		return heatFactory;
	}

	public void setHeatFactory(HeatmapFactory heatFactory) {
		this.heatFactory = heatFactory;
	}
	public JTextField getTxtMSIDisconVelocity() {
		return txtMSIDisconVelocity;
	}
	public JTextField getTxtMSIDisconSpotsize() {
		return txtMSIDisconSpotsize;
	}
	public JTextField getTxtMSIDisconMZ() {
		return txtMSIDisconMZ;
	}
	public JTextField getTxtMSIDisconPM() {
		return txtMSIDisconPM;
	}
	public JRadioButton getRbMSIAllFiles() {
		return rbMSIAllFiles;
	}
	public JPanel getPnChartViewMSIDiscon() {
		return pnChartViewMSIDiscon;
	}
	public JPanel getPnOESCenterContent() {
		return pnOESCenterContent;
	}
	public JPanel getPnMSCenterContent() {
		return pnMSCenterContent;
	}
	public JPanel getCenterContent() {
		return CenterContent;
	}

	public JFileChooser getFcOpenOES() {
		return fcOpenOES;
	}

	public int getCurrentMode() {
		return currentMode;
	}

	public void setCurrentMode(int currentMode) {
		this.currentMode = currentMode;
	} 

	public ImageEditorWindow getImageEditorWnd() {
		return imageEditorWnd;
	}

	public void setImageEditorWnd(ImageEditorWindow imageEditorWnd) {
		this.imageEditorWnd = imageEditorWnd;
	}
	public ImageVsSpecViewPanel getTabThresomeTICvsMZvsSpec() {
		return tabThresomeTICvsMZvsSpec;
	}

	public XSSFExcelWriterReader getExcelWriter() {
		return excelWriter;
	}

	public void setExcelWriter(XSSFExcelWriterReader excelWriter) {
		this.excelWriter = excelWriter;
	}

	public static Window getWindow() {
		return window;
	}
	/*
	 * Dialogs
	 */
	public static void showErrorDialog(String message, Exception e) {
		JOptionPane.showMessageDialog(getWindow().getFrame(), message+" \n"+e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE); 
	}
	public static void showErrorDialog(String title, String message) {
		JOptionPane.showMessageDialog(getWindow().getFrame(), message, title, JOptionPane.ERROR_MESSAGE); 
	}
	
	public static boolean showDialogYesNo(String title, String text) {
		Object[] options = {"Yes", "No"};
        int n = JOptionPane.showOptionDialog(getWindow().getFrame(),
			    text,
			    title,
			    JOptionPane.YES_NO_OPTION,
			    JOptionPane.QUESTION_MESSAGE,
			    null,
			    options,
			    options[0]); 
        return n==0;
	}

	public ChargeCalculatorSettingsDialog getDialogChargeCalculatorSettings() {
		return dialogChargeCalculatorSettings;
	}  
}
