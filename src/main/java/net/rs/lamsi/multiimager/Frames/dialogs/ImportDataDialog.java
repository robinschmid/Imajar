package net.rs.lamsi.multiimager.Frames.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.Module;
import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.massimager.Settings.SettingsHolder;
import net.rs.lamsi.massimager.Settings.image.sub.SettingsGeneralImage.XUNIT;
import net.rs.lamsi.massimager.Settings.importexport.SettingsImageDataImportTxt;
import net.rs.lamsi.massimager.Settings.importexport.SettingsImageDataImportTxt.IMPORT;
import net.rs.lamsi.massimager.Settings.importexport.SettingsImageDataImportTxt.ModeData;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow.LOG;
import net.rs.lamsi.multiimager.Frames.ImageLogicRunner;
import net.rs.lamsi.utils.FileAndPathUtil;
import net.rs.lamsi.utils.useful.FileNameExtFilter;

public class ImportDataDialog extends JDialog {
	//
	protected SettingsImageDataImportTxt settingsDataImport = null;
	protected ImageLogicRunner runner;
	private final ImportDataDialog thisframe;
	// presets
	private Vector<SettingsImageDataImportTxt> presets;
	private JTextField txtOwnSeperation;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JPanel tabTXT;
	private JPanel panEXCEL;
	private JCheckBox chckbxSearchForMeta;
	private JPanel panel;
	private JPanel tab2DIntensity;
	private JLabel lblLoadAnImage;
	private JTextField txt2DOwn;
	private JCheckBox cb2DMetadata;
	private final ButtonGroup buttonGroup_1 = new ButtonGroup();
	private JPanel tabMSPresets;
	private JPanel panel_1;
	private JRadioButton rbMP17Thermo;
	private JCheckBox cbContinousData;
	private JLabel lblStartsWith;
	private JTextField txtStartsWith;
	private JLabel lblEndsWith;
	private JTextField txtEndsWith;
	private JScrollPane scrollPane;
	private JList listPresets;
	private JButton btnLoadPreset;
	private JButton btnSavePreset;
	private JTabbedPane tabbedPane;
	private JCheckBox cbFilesInSeparateFolders;
	private JRadioButton rbNeptune;
	private final ButtonGroup buttonGroup_2 = new ButtonGroup();
	private JTextField txtThermoSeparation;
	private JComboBox combo2DDataMode;
	private JLabel lblData;
	private JComboBox comboSep;
	private JLabel lblSeparation;
	private JComboBox comboSepLines;
	private JPanel panel_2;
	private JPanel panel_3;
	private JLabel lblFilter;
	private JLabel lblScanLines;
	private JLabel lblDataPoints;
	private JTextField txtFirstLine;
	private JTextField txtFirstDP;
	private JLabel lblTo;
	private JLabel lblTo_1;
	private JTextField txtLastLine;
	private JTextField txtLastDP;
	private JLabel lblFirst;
	private JLabel lblLast;
	private JPanel pnContinuousSplit;
	private JCheckBox cbHardSplit;
	private JLabel lblsp1;
	private JTextField txtSplitAfter;
	private JComboBox comboSplitXUnit;
	private JLabel lblStartX;
	private JTextField txtSplitStart;
	private JPanel south;
	private JLabel lblExcludeColumns;
	private JTextField txtExcludeColumns;
	private JCheckBox cbNoXData;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ImportDataDialog dialog = new ImportDataDialog(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public ImportDataDialog(ImageLogicRunner runner2) {
		this.runner = runner2;
		thisframe = this;
		setBounds(100, 100, 573, 637);
		getContentPane().setLayout(new BorderLayout());
		{
			tabbedPane = new JTabbedPane(JTabbedPane.TOP);
			getContentPane().add(tabbedPane, BorderLayout.CENTER);
			{
				tabMSPresets = new JPanel();
				tabbedPane.addTab("MS presets", null, tabMSPresets, null);
				tabMSPresets.setLayout(new BorderLayout(0, 0));
				{
					panel_1 = new JPanel();
					tabMSPresets.add(panel_1, BorderLayout.NORTH);
					panel_1.setLayout(new MigLayout("", "[][]", "[][]"));
					{
						rbMP17Thermo = new JRadioButton("iCAP-Q - Thermo Fischer Scientific");
						rbMP17Thermo.addActionListener(new ActionListener() { 
							@Override
							public void actionPerformed(ActionEvent e) {
								getTxtEndsWith().setText("csv");
							}
						});
						buttonGroup_2.add(rbMP17Thermo);
						rbMP17Thermo.setSelected(true);
						panel_1.add(rbMP17Thermo, "cell 0 0");
					}
					{
						rbNeptune = new JRadioButton("Thermo Neptune sector field");
						rbNeptune.addActionListener(new ActionListener() { 
							@Override
							public void actionPerformed(ActionEvent e) {
								getTxtEndsWith().setText("exp");
							}
						});
						{
							txtThermoSeparation = new JTextField();
							txtThermoSeparation.setToolTipText("Separation for columns (\\t for tab)");
							txtThermoSeparation.setText("\\t");
							panel_1.add(txtThermoSeparation, "cell 1 0,growx");
							txtThermoSeparation.setColumns(5);
						}
						buttonGroup_2.add(rbNeptune);
						panel_1.add(rbNeptune, "cell 0 1");
					}
				}
				{
					scrollPane = new JScrollPane();
					tabMSPresets.add(scrollPane, BorderLayout.CENTER);
					{
						listPresets = new JList(new DefaultListModel<String>());
						scrollPane.setViewportView(listPresets);
						listPresets.addListSelectionListener(new ListSelectionListener() { 
							@Override
							public void valueChanged(ListSelectionEvent e) {
								if(!e.getValueIsAdjusting() && getListPresets().getSelectedIndex()!=-1) {
									setAllViaExistingSettings(getListPresets().getSelectedIndex());
									listPresets.clearSelection();
								}
							}
						}); 
					}
				}
			}
			{
				panel = new JPanel();
				tabbedPane.addTab("2D Intensity", null, panel, null);
				panel.setLayout(new BorderLayout(0, 0));
				{
					tab2DIntensity = new JPanel();
					panel.add(tab2DIntensity);
					tab2DIntensity.setLayout(new MigLayout("", "[][][]", "[][][][][][][]"));
					{
						lblLoadAnImage = new JLabel("Load an image from one 2D intensity matrix file (.txt or .csv)");
						tab2DIntensity.add(lblLoadAnImage, "cell 0 0 3 1");
					}
					{
						lblData = new JLabel("Data:");
						tab2DIntensity.add(lblData, "cell 0 2,alignx trailing");
					}
					{
						combo2DDataMode = new JComboBox();
						combo2DDataMode.setModel(new DefaultComboBoxModel(ModeData.values()));
						combo2DDataMode.setSelectedIndex(0);
						tab2DIntensity.add(combo2DDataMode, "cell 1 2,growx");
					}
					{
						lblSeparation = new JLabel("Separation:");
						tab2DIntensity.add(lblSeparation, "cell 0 3,alignx trailing");
					}
					{
						comboSep = new JComboBox();
						comboSep.setModel(new DefaultComboBoxModel(new String[] {"COMMA", "SPACE", "TAB", "SEMICOLON", "OWN"}));
						comboSep.setSelectedIndex(0);
						tab2DIntensity.add(comboSep, "cell 1 3,growx");
						comboSep.addItemListener(new ItemListener() {
								@Override
							    public void itemStateChanged(ItemEvent event) {
							       if (event.getStateChange() == ItemEvent.SELECTED) {
							    	   getTxt2DOwnSeparation().setEditable(comboSep.getSelectedItem().equals("OWN"));
							       }
							    }
						});
					}
					{
						txt2DOwn = new JTextField();
						txt2DOwn.setToolTipText("Own seperation");
						txt2DOwn.setEditable(false);
						txt2DOwn.setColumns(10);
						tab2DIntensity.add(txt2DOwn, "cell 2 3,growx");
					}
					{
						cb2DMetadata = new JCheckBox("Search for meta data");
						cb2DMetadata.setToolTipText("Tries to search for meta data (non XI-paired data)");
						cb2DMetadata.setSelected(true);
						tab2DIntensity.add(cb2DMetadata, "cell 1 5");
					}
				}
			}
			{
				tabTXT = new JPanel();
				tabbedPane.addTab("Lines", null, tabTXT, null);
				tabTXT.setLayout(new MigLayout("", "[][][]", "[][][][][][][][][][][]"));
				{
					JLabel lblImportFromText = new JLabel("Import from text files. Select all files containing scan lines");
					tabTXT.add(lblImportFromText, "cell 0 0 3 1");
				}
				{
					JLabel lblDataSeperationBy = new JLabel("Separation:");
					tabTXT.add(lblDataSeperationBy, "cell 0 2,alignx trailing");
				}
				{
					comboSepLines = new JComboBox();
					comboSepLines.setModel(new DefaultComboBoxModel(new String[] {"COMMA", "SPACE", "TAB", "SEMICOLON", "OWN"}));
					comboSepLines.setSelectedIndex(0);
					tabTXT.add(comboSepLines, "cell 1 2,growx");
					comboSepLines.addItemListener(new ItemListener() {
						@Override
					    public void itemStateChanged(ItemEvent event) {
					       if (event.getStateChange() == ItemEvent.SELECTED) {
					    	   getTxtOwnSeparation().setEditable(comboSepLines.getSelectedItem().equals("OWN"));
					       }
					    }
				});
				}
				{
					txtOwnSeperation = new JTextField();
					txtOwnSeperation.setEditable(false);
					txtOwnSeperation.setToolTipText("Own seperation");
					tabTXT.add(txtOwnSeperation, "cell 2 2,alignx left");
					txtOwnSeperation.setColumns(10);
				}
				{
					lblExcludeColumns = new JLabel("Exclude columns:");
					tabTXT.add(lblExcludeColumns, "cell 0 4,alignx trailing");
				}
				{
					txtExcludeColumns = new JTextField();
					txtExcludeColumns.setToolTipText("Input: 1,2,3 or 1-3,5... Exclude columns from data import (if x-data is in column 2, exclude column 1)");
					tabTXT.add(txtExcludeColumns, "cell 1 4 2 1,growx");
					txtExcludeColumns.setColumns(10);
				}
				{
					cbNoXData = new JCheckBox("No X-data");
					cbNoXData.setToolTipText("All columns are imported as intensities.");
					tabTXT.add(cbNoXData, "cell 1 5");
				}
				{
					chckbxSearchForMeta = new JCheckBox("Search for meta data");
					chckbxSearchForMeta.setToolTipText("Tries to search for meta data (non XI-paired data)");
					chckbxSearchForMeta.setSelected(true);
					tabTXT.add(chckbxSearchForMeta, "cell 1 7 2 1");
				}
				{
					{
						cbFilesInSeparateFolders = new JCheckBox("Files in separate folders");
						cbFilesInSeparateFolders.setToolTipText("Unchecked: All files are in one folder. Checked: Each file has its own sub folder");
						tabTXT.add(cbFilesInSeparateFolders, "cell 1 8 2 1");
					}
				}
				cbContinousData = new JCheckBox("Continous data (one file=one image)");
				cbContinousData.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent e) {
						getCbFilesInSeparateFolders().setVisible(!((JCheckBox)e.getSource()).isSelected());
						getPnContinuousSplit().setVisible(((JCheckBox)e.getSource()).isSelected());
					}
				});
				tabTXT.add(cbContinousData, "cell 1 9 2 1");
				{
					pnContinuousSplit = new JPanel();
					tabTXT.add(pnContinuousSplit, "cell 1 10 2 1,grow");
					pnContinuousSplit.setLayout(new MigLayout("", "[][][]", "[][][]"));
					{
						cbHardSplit = new JCheckBox("Hard split");
						cbHardSplit.setToolTipText("Performs a hard split with the splitting settings. This is recommended if the correct splitting settings are known.");
						cbHardSplit.setSelected(true);
						pnContinuousSplit.add(cbHardSplit, "cell 0 0 2 1");
					}
					{
						lblsp1 = new JLabel("Split after:");
						pnContinuousSplit.add(lblsp1, "flowx,cell 0 1");
					}
					{
						txtSplitAfter = new JTextField();
						txtSplitAfter.setToolTipText("Split all lines after X data points (DP) or time units. (Leave empty for automatic estimation)");
						txtSplitAfter.setHorizontalAlignment(SwingConstants.RIGHT);
						pnContinuousSplit.add(txtSplitAfter, "cell 1 1");
						txtSplitAfter.setColumns(6);
					}
					{
						comboSplitXUnit = new JComboBox();
						comboSplitXUnit.setModel(new DefaultComboBoxModel(XUNIT.values()));
						comboSplitXUnit.setSelectedIndex(0);
						pnContinuousSplit.add(comboSplitXUnit, "cell 2 1,growx");
					}
					{
						lblStartX = new JLabel("Start x:");
						pnContinuousSplit.add(lblStartX, "cell 0 2,alignx trailing");
					}
					{
						txtSplitStart = new JTextField();
						txtSplitStart.setToolTipText("Start x (time or data points) to be removed from the data. (relative to x0 the start time/DP)");
						txtSplitStart.setHorizontalAlignment(SwingConstants.RIGHT);
						txtSplitStart.setText("0");
						pnContinuousSplit.add(txtSplitStart, "cell 1 2,growx");
						txtSplitStart.setColumns(6);
					}
				}
			}
			{
				panEXCEL = new JPanel();
				tabbedPane.addTab("Excel (.xlsx; .xls)", null, panEXCEL, null);
				panEXCEL.setLayout(new BorderLayout(0, 0));
			}
		}
		{
			south = new JPanel();
			getContentPane().add(south, BorderLayout.SOUTH);
			south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
			{
				panel_2 = new JPanel();
				panel_2.setAlignmentX(Component.RIGHT_ALIGNMENT);
				south.add(panel_2);
				panel_2.setLayout(new MigLayout("", "[][][][]", "[][][]"));
				{
					lblFilter = new JLabel("Filter:");
					lblFilter.setFont(new Font("Tahoma", Font.BOLD, 11));
					panel_2.add(lblFilter, "cell 0 0,alignx left");
				}
				{
					lblFirst = new JLabel("first");
					panel_2.add(lblFirst, "cell 1 0,alignx center");
				}
				{
					lblLast = new JLabel("last");
					panel_2.add(lblLast, "cell 3 0,alignx center");
				}
				{
					lblScanLines = new JLabel("Scan lines:");
					lblScanLines.setHorizontalAlignment(SwingConstants.TRAILING);
					panel_2.add(lblScanLines, "cell 0 1,alignx trailing");
				}
				{
					txtFirstLine = new JTextField();
					txtFirstLine.setHorizontalAlignment(SwingConstants.RIGHT);
					txtFirstLine.setToolTipText("First scan line to import (use 0 or no input for no filtering)");
					txtFirstLine.setText("0");
					panel_2.add(txtFirstLine, "cell 1 1,growx");
					txtFirstLine.setColumns(5);
				}
				{
					lblTo = new JLabel("to");
					panel_2.add(lblTo, "cell 2 1,alignx trailing");
				}
				{
					txtLastLine = new JTextField();
					txtLastLine.setToolTipText("Last scan line to import (use no input for no filtering)");
					txtLastLine.setHorizontalAlignment(SwingConstants.RIGHT);
					txtLastLine.setColumns(5);
					panel_2.add(txtLastLine, "cell 3 1,growx");
				}
				{
					lblDataPoints = new JLabel("Data points:");
					lblDataPoints.setHorizontalAlignment(SwingConstants.TRAILING);
					panel_2.add(lblDataPoints, "cell 0 2,alignx trailing");
				}
				{
					txtFirstDP = new JTextField();
					txtFirstDP.setHorizontalAlignment(SwingConstants.RIGHT);
					txtFirstDP.setToolTipText("First data point in a scan line to import (use 0 or no input for no filtering)");
					txtFirstDP.setText("0");
					panel_2.add(txtFirstDP, "cell 1 2,growx");
					txtFirstDP.setColumns(5);
				}
				{
					lblTo_1 = new JLabel("to");
					panel_2.add(lblTo_1, "cell 2 2,alignx trailing");
				}
				{
					txtLastDP = new JTextField();
					txtLastDP.setHorizontalAlignment(SwingConstants.RIGHT);
					txtLastDP.setToolTipText("Last data point in a scan line to import (use no input for no filtering)");
					txtLastDP.setColumns(5);
					panel_2.add(txtLastDP, "cell 3 2,growx");
				}
			}
			{
				panel_3 = new JPanel();
				south.add(panel_3);
				{
					lblStartsWith = new JLabel("Starts with:");
					panel_3.add(lblStartsWith);
				}
				{
					txtStartsWith = new JTextField();
					panel_3.add(txtStartsWith);
					txtStartsWith.setColumns(10);
				}
				{
					lblEndsWith = new JLabel("Ends with:");
					panel_3.add(lblEndsWith);
				}
				{
					txtEndsWith = new JTextField();
					panel_3.add(txtEndsWith);
					txtEndsWith.setText("csv");
					txtEndsWith.setColumns(10);
				}
			}
			{
				JPanel buttonPane = new JPanel();
				south.add(buttonPane);
				buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
				{
					JButton okButton = new JButton("Open");
					okButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							// create settings
							createSettingsForImport();
							// open data with settings but first open filechoose
							runner.importDataToImage(settingsDataImport);
						}
					});
					{
						btnSavePreset = new JButton("Save Preset");
						btnSavePreset.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) { 
								try {
									SettingsImageDataImportTxt sett = createSettingsForImport();
									File file = SettingsHolder.getSettings().saveSettingsToFile(null, sett);
									if(file!=null)
										addPreset(sett, FileAndPathUtil.eraseFormat(file.getName()));
								} catch (Exception e1) {
									e1.printStackTrace();
									ImageEditorWindow.log("Cannot save preset "+e1.getMessage(), LOG.ERROR);
								}
							}
						});
						buttonPane.add(btnSavePreset);
					}
					{
						btnLoadPreset = new JButton("Load Preset");
						btnLoadPreset.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) { 
								try {
									Settings sett = createSettingsForImport();
									sett = SettingsHolder.getSettings().loadSettingsFromFile(thisframe, sett);
								} catch (Exception e1) { 
									e1.printStackTrace();
									ImageEditorWindow.log("Cannot load preset "+e1.getMessage(), LOG.ERROR);
								}
							}
						});
						buttonPane.add(btnLoadPreset);
					}
					okButton.setActionCommand("OK");
					buttonPane.add(okButton);
					getRootPane().setDefaultButton(okButton);
				}
				{
					JButton cancelButton = new JButton("Cancel");
					cancelButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							// close
							setVisible(false);
						}
					});
					cancelButton.setActionCommand("Cancel");
					buttonPane.add(cancelButton);
				}
			}
		}
		
		// end of init
		// load presets to list
		loadPresets();
	}
	
	protected void setAllViaExistingSettings(int i) {
		// TODO Auto-generated method stub
		SettingsImageDataImportTxt sett = presets.get(i);
		// set active tab
		if(SettingsImageDataImportTxt.class.isInstance(sett)) {
			SettingsImageDataImportTxt s = (SettingsImageDataImportTxt)sett;
			// filename
			getTxtStartsWith().setText(s.getFilter().getStartsWith());
			getTxtEndsWith().setText(s.getFilter().getExt());
			// sep
			String sep = s.getSeparation();
			// txt
			// line dp limits
			getTxtFirstLine().setText(String.valueOf(s.getStartLine()));
			getTxtLastLine().setText(String.valueOf(s.getEndLine()));
			getTxtFirstDP().setText(String.valueOf(s.getStartDP()));
			getTxtLastDP().setText(String.valueOf(s.getEndDP()));
			// mode = tab
			IMPORT mode = s.getModeImport();
			switch(mode) {
			case ONE_FILE_2D_INTENSITY:
				getTabbedPane().setSelectedComponent(getTab2DIntensity());
				//"COMMA", "SPACE", "TAB", "SEMICOLON", "OWN"
				// seperation  
				if(sep.equals(",")) getComboSep().setSelectedIndex(0);
				else if(sep.equals(" ")) getComboSep().setSelectedIndex(1);
				else if(sep.equals("	")) getComboSep().setSelectedIndex(2);
				else if(sep.equals(";")) getComboSep().setSelectedIndex(3);
				else { 
					getComboSep().setSelectedIndex(4);
					getTxt2DOwnSeparation().setText(sep);
				} 

				// check for meta  
				getCb2DMetadata().setSelected(sett.isSearchingForMetaData());
				
				break;
			case CONTINOUS_DATA_TXT_CSV:
			case MULTIPLE_FILES_LINES_TXT_CSV:
				getTabbedPane().setSelectedComponent(getTabTXT());
				
				// seperation   
				if(sep.equals(",")) getComboSepLines().setSelectedIndex(0);
				else if(sep.equals(" ")) getComboSepLines().setSelectedIndex(1);
				else if(sep.equals("	")) getComboSepLines().setSelectedIndex(2);
				else if(sep.equals(";")) getComboSepLines().setSelectedIndex(3);
				else { 
					getComboSepLines().setSelectedIndex(4);
					getTxtOwnSeparation().setText(sep);
				} 
				
				// no x and exclusion
				getTxtExcludeColumns().setText(s.getExcludeColumns());
				getCbNoXData().setSelected(s.isNoXData());
				
				// split
				getTxtSplitAfter().setText(String.valueOf(s.getSplitAfter()));
				getTxtSplitStart().setText(String.valueOf(s.getSplitStart()));
				getComboSplitXUnit().setSelectedItem(s.getSplitUnit());
				getCbHardSplit().setSelected(s.isUseHardSplit());

				// check for meta 
				getChckbxSearchForMeta().setSelected(sett.isSearchingForMetaData());
				getCbContinousData().setSelected(mode==IMPORT.CONTINOUS_DATA_TXT_CSV);
				getCbFilesInSeparateFolders().setSelected(s.isFilesInSeparateFolders());
				break;
			case PRESETS_THERMO_MP17:
				getTabbedPane().setSelectedComponent(getTabMSPresets());
				getTxtThermoSeparation().setText(sep.equals("\t")? "\t" : sep);
				getRbMP17Thermo().setSelected(true);
				break;
			}
		}
		else {
			// xlsx?
		}
	}

	/**
	 * init! 
	 * load presets to list
	 */
	private void loadPresets() { 
		presets = new Vector<SettingsImageDataImportTxt>();
		// load files from directory as presets
		Settings sett = createSettingsForImport();
		
		if(sett!=null) {
			File path = new File(FileAndPathUtil.getPathOfJar(), sett.getPathSettingsFile());
			String type = sett.getFileEnding();
			try {
				if(path.exists()) {
					Vector<File[]> files = FileAndPathUtil.findFilesInDir(path,  new FileNameExtFilter("", type), false, false);
					// load each file as settings and add to menu as preset
					for(File f : files.get(0)) {
						// load
						try {
							SettingsImageDataImportTxt load = (SettingsImageDataImportTxt)SettingsHolder.getSettings().loadSettingsFromFile(f, sett);
							if(load !=null)
								addPreset(load, FileAndPathUtil.eraseFormat(f.getName()));
						} catch(Exception ex) {
							ImageEditorWindow.log("Preset is broken remove from settings directory: \n"+f.getAbsolutePath(), LOG.WARNING);
						}
					}
				}
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	} 
	
	
	
	/**
	 * adds a preset to list
	 * @param settings
	 * @param title
	 * @return
	 */
	public void addPreset(final SettingsImageDataImportTxt settings, String title) { 
		// add to lists
		((DefaultListModel)(getListPresets().getModel())).addElement(title);
		presets.addElement(settings);
	}
	
	

	protected SettingsImageDataImportTxt createSettingsForImport() {
		// fileformatfilter
		FileNameExtFilter filter = new FileNameExtFilter(getTxtStartsWith().getText(), getTxtEndsWith().getText());
		// line/dp  start/end (0 for no filter)
		int startLine = Module.intFromTxt(getTxtFirstLine());
		int endLine = Module.intFromTxt(getTxtLastLine());
		int startDP = Module.intFromTxt(getTxtFirstDP());
		int endDP = Module.intFromTxt(getTxtLastDP());
		
		// text file
		if(getTabbedPane().getSelectedComponent().equals(getTabTXT())) {
			// seperation
			String separation = getSeparationChar(getComboSepLines().getSelectedItem(), false);
			// check for meta
			boolean checkformeta = getChckbxSearchForMeta().isSelected();
			boolean isContinousData = getCbContinousData().isSelected();
			boolean isFilesInSeparateFolders = isContinousData? false : getCbFilesInSeparateFolders().isSelected();
			// continuous split
			float startSplitX = Module.floatFromTxt(getTxtSplitStart());
			float splitAfter = Module.floatFromTxt(getTxtSplitAfter());
			XUNIT unit = (XUNIT) comboSplitXUnit.getSelectedItem();
			boolean useHardSplit = getCbHardSplit().isSelected();
			
			// no X data and exclude columns
			String excludeColumns = getTxtExcludeColumns().getText();
			boolean usesXData = getCbNoXData().isSelected();
			// create settings
			settingsDataImport = new SettingsImageDataImportTxt(isContinousData? IMPORT.CONTINOUS_DATA_TXT_CSV : IMPORT.MULTIPLE_FILES_LINES_TXT_CSV, 
					checkformeta, separation, null, filter, isFilesInSeparateFolders,
					startLine, endLine, startDP, endDP,
					unit, startSplitX, splitAfter, useHardSplit,
					excludeColumns, usesXData);
		}
		else if(getTabbedPane().getSelectedComponent().equals(getTab2DIntensity())) {
			// seperation
			String separation = getSeparationChar(getComboSep().getSelectedItem(), true);
			// mode of data:
			ModeData mode = (ModeData) getCombo2DDataMode().getSelectedItem();
			
			// check for meta
			boolean checkformeta = getCb2DMetadata().isSelected();
			// create settings
			settingsDataImport = new SettingsImageDataImportTxt(IMPORT.ONE_FILE_2D_INTENSITY, 
					checkformeta, separation, mode, filter, false,
					startLine, endLine, startDP, endDP);
		}
		else if(getTabbedPane().getSelectedComponent().equals(getTabMSPresets())) {
			String separation = "	";
			boolean checkformeta = true;
			if(rbMP17Thermo.isSelected()) { 
				if(getTxtThermoSeparation().getText().length()>0)
					separation = getTxtThermoSeparation().getText();
				IMPORT importMode = IMPORT.PRESETS_THERMO_MP17; 
				settingsDataImport = new SettingsImageDataImportTxt(importMode, checkformeta, separation, null, filter, false,
						startLine, endLine, startDP, endDP);
			}
			else if(rbNeptune.isSelected()) {
				IMPORT importMode = IMPORT.PRESETS_THERMO_NEPTUNE; 
				settingsDataImport = new SettingsImageDataImportTxt(importMode, checkformeta, separation, null, filter, false,
						startLine, endLine, startDP, endDP);
			}
		}
		return settingsDataImport;
	}

	/**
	 * 
	 * @param s
	 * @param for2DMatrix
	 * @return separation character
	 */
	private String getSeparationChar(Object s, boolean for2DMatrix) {
		if(s.equals("COMMA")) return ",";
		else if(s.equals("TAB")) return "	";
		else if(s.equals("SPACE")) return " ";
		else if(s.equals("SEMICOLON")) return ";";
		else if(s.equals("OWN")) {
			if(for2DMatrix) return getTxt2DOwnSeparation().getText();
			else return getTxtOwnSeparation().getText();
		} 
		else return ",";
	}
	public JTextField getTxtOwnSeparation() {
		return txtOwnSeperation;
	}
	public JTextField getTxt2DOwnSeparation() {
		return txt2DOwn;
	}
	public JPanel getTabTXT() {
		return tabTXT;
	}
	public JPanel getPanEXCEL() {
		return panEXCEL;
	} 
	public JCheckBox getChckbxSearchForMeta() {
		return chckbxSearchForMeta;
	} 
	public SettingsImageDataImportTxt getSettingsDataImport() {
		return settingsDataImport;
	}  
	public JCheckBox getCb2DMetadata() {
		return cb2DMetadata;
	} 
	public JPanel getTab2DIntensity() {
		return panel;
	}
	public JRadioButton getRbMP17Thermo() {
		return rbMP17Thermo;
	}
	public JPanel getTabMSPresets() {
		return tabMSPresets;
	}
	public JCheckBox getCbContinousData() {
		return cbContinousData;
	}
	public JTextField getTxtEndsWith() {
		return txtEndsWith;
	}
	public JTextField getTxtStartsWith() {
		return txtStartsWith;
	}
	public JList getListPresets() {
		return listPresets;
	}
	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}
	public JCheckBox getCbFilesInSeparateFolders() {
		return cbFilesInSeparateFolders;
	}
	public JTextField getTxtThermoSeparation() {
		return txtThermoSeparation;
	}
	public JComboBox getComboSep() {
		return comboSep;
	}
	public JComboBox getCombo2DDataMode() {
		return combo2DDataMode;
	}
	public JComboBox getComboSepLines() {
		return comboSepLines;
	}
	public JTextField getTxtFirstDP() {
		return txtFirstDP;
	}
	public JTextField getTxtFirstLine() {
		return txtFirstLine;
	}
	public JTextField getTxtLastLine() {
		return txtLastLine;
	}
	public JTextField getTxtLastDP() {
		return txtLastDP;
	}
	public JPanel getPnContinuousSplit() {
		return pnContinuousSplit;
	}
	public JComboBox getComboSplitXUnit() {
		return comboSplitXUnit;
	}
	public JTextField getTxtSplitAfter() {
		return txtSplitAfter;
	}
	public JTextField getTxtSplitStart() {
		return txtSplitStart;
	}
	public JCheckBox getCbHardSplit() {
		return cbHardSplit;
	}
	public JCheckBox getCbNoXData() {
		return cbNoXData;
	}
	public JTextField getTxtExcludeColumns() {
		return txtExcludeColumns;
	}
}
