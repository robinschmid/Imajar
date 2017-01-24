package net.rs.lamsi.multiimager.Frames.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.massimager.Settings.SettingsHolder;
import net.rs.lamsi.massimager.Settings.image.SettingsImageDataImport;
import net.rs.lamsi.massimager.Settings.image.SettingsImageDataImportTxt;
import net.rs.lamsi.massimager.Settings.image.SettingsImageDataImportTxt.IMPORT;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow.LOG;
import net.rs.lamsi.multiimager.Frames.ImageLogicRunner;
import net.rs.lamsi.utils.FileAndPathUtil;
import net.rs.lamsi.utils.useful.FileNameExtFilter;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

import net.rs.lamsi.massimager.Settings.image.SettingsImageDataImportTxt.ModeData;

public class ImportDataDialog extends JDialog {
	//
	protected SettingsImageDataImport settingsDataImport = null;
	protected ImageLogicRunner runner;
	private final ImportDataDialog thisframe;
	// presets
	private Vector<SettingsImageDataImport> presets;
	//
	private final JPanel contentPanel = new JPanel();
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
	private JPanel pnCenterPermSettings;
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
		setBounds(100, 100, 470, 441);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			tabbedPane = new JTabbedPane(JTabbedPane.TOP);
			getContentPane().add(tabbedPane, BorderLayout.NORTH);
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
				tabTXT.setLayout(new MigLayout("", "[][][grow]", "[][][][][][][]"));
				{
					JLabel lblImportFromText = new JLabel("Import from text files. Select all files containing scan lines");
					tabTXT.add(lblImportFromText, "cell 0 0 3 1");
				}
				{
					JLabel lblDataSeperationBy = new JLabel("Separation:");
					tabTXT.add(lblDataSeperationBy, "cell 0 2");
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
					chckbxSearchForMeta = new JCheckBox("Search for meta data");
					chckbxSearchForMeta.setToolTipText("Tries to search for meta data (non XI-paired data)");
					chckbxSearchForMeta.setSelected(true);
					tabTXT.add(chckbxSearchForMeta, "cell 1 4");
				}
				{
					cbContinousData = new JCheckBox("Continous data (one file=one image)");
					cbContinousData.addItemListener(new ItemListener() {
						public void itemStateChanged(ItemEvent e) {
							getCbFilesInSeparateFolders().setVisible(!((JCheckBox)e.getSource()).isSelected());
						}
					});
					tabTXT.add(cbContinousData, "cell 1 5 2 1");
				}
				{
					cbFilesInSeparateFolders = new JCheckBox("Files in separate folders");
					cbFilesInSeparateFolders.setToolTipText("Unchecked: All files are in one folder. Checked: Each file has its own sub folder");
					tabTXT.add(cbFilesInSeparateFolders, "cell 1 6");
				}
			}
			{
				panEXCEL = new JPanel();
				tabbedPane.addTab("Excel (.xlsx; .xls)", null, panEXCEL, null);
				panEXCEL.setLayout(new BorderLayout(0, 0));
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
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
								SettingsImageDataImport sett = createSettingsForImport();
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
		{
			pnCenterPermSettings = new JPanel();
			getContentPane().add(pnCenterPermSettings, BorderLayout.CENTER);
			{
				lblStartsWith = new JLabel("Starts with:");
				pnCenterPermSettings.add(lblStartsWith);
			}
			{
				txtStartsWith = new JTextField();
				pnCenterPermSettings.add(txtStartsWith);
				txtStartsWith.setColumns(10);
			}
			{
				lblEndsWith = new JLabel("Ends with:");
				pnCenterPermSettings.add(lblEndsWith);
			}
			{
				txtEndsWith = new JTextField();
				txtEndsWith.setText("csv");
				pnCenterPermSettings.add(txtEndsWith);
				txtEndsWith.setColumns(10);
			}
		}
		
		// end of init
		// load presets to list
		loadPresets();
	}
	
	protected void setAllViaExistingSettings(int i) {
		// TODO Auto-generated method stub
		SettingsImageDataImport sett = presets.get(i);
		// set active tab
		if(SettingsImageDataImportTxt.class.isInstance(sett)) {
			SettingsImageDataImportTxt s = (SettingsImageDataImportTxt)sett;
			// filename
			getTxtStartsWith().setText(s.getFilter().getStartsWith());
			getTxtEndsWith().setText(s.getFilter().getExt());
			// sep
			String sep = s.getSeparation();
			// txt
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
		presets = new Vector<SettingsImageDataImport>();
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
							SettingsImageDataImport load = (SettingsImageDataImport)SettingsHolder.getSettings().loadSettingsFromFile(f, sett);
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
	public void addPreset(final SettingsImageDataImport settings, String title) { 
		// add to lists
		((DefaultListModel)(getListPresets().getModel())).addElement(title);
		presets.addElement(settings);
	}
	
	

	protected SettingsImageDataImport createSettingsForImport() {
		// fileformatfilter
		FileNameExtFilter filter = new FileNameExtFilter(getTxtStartsWith().getText(), getTxtEndsWith().getText());
		// text file
		if(getTabbedPane().getSelectedComponent().equals(getTabTXT())) {
			// seperation
			String separation = getSeparationChar(getComboSepLines().getSelectedItem(), false);
			// check for meta
			boolean checkformeta = getChckbxSearchForMeta().isSelected();
			boolean isContinousData = getCbContinousData().isSelected();
			boolean isFilesInSeparateFolders = isContinousData? false : getCbFilesInSeparateFolders().isSelected();
			// create settings
			settingsDataImport = new SettingsImageDataImportTxt(isContinousData? IMPORT.CONTINOUS_DATA_TXT_CSV : IMPORT.MULTIPLE_FILES_LINES_TXT_CSV, checkformeta, separation, filter, isFilesInSeparateFolders);
		}
		else if(getTabbedPane().getSelectedComponent().equals(getTab2DIntensity())) {
			// seperation
			String separation = getSeparationChar(getComboSep().getSelectedItem(), true);
			// mode of data:
			ModeData mode = (ModeData) getCombo2DDataMode().getSelectedItem();
			
			// check for meta
			boolean checkformeta = getCb2DMetadata().isSelected();
			// create settings
			settingsDataImport = new SettingsImageDataImportTxt(IMPORT.ONE_FILE_2D_INTENSITY, checkformeta, separation, mode, filter, false);
		}
		else if(getTabbedPane().getSelectedComponent().equals(getTabMSPresets())) {
			String separation = "	";
			boolean checkformeta = true;
			if(rbMP17Thermo.isSelected()) { 
				if(getTxtThermoSeparation().getText().length()>0)
					separation = getTxtThermoSeparation().getText();
				IMPORT importMode = IMPORT.PRESETS_THERMO_MP17; 
				settingsDataImport = new SettingsImageDataImportTxt(importMode, checkformeta, separation, filter, false);
			}
			else if(rbNeptune.isSelected()) {
				IMPORT importMode = IMPORT.PRESETS_THERMO_NEPTUNE; 
				settingsDataImport = new SettingsImageDataImportTxt(importMode, checkformeta, separation, filter, false);
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
	public SettingsImageDataImport getSettingsDataImport() {
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
}
