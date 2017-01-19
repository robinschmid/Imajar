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
	private JRadioButton rbAutomatic;
	private JRadioButton rbSeperationTab;
	private JRadioButton rbSeperationSpace;
	private JRadioButton rbSeperationComma;
	private JRadioButton rdbtnUseOwnSeperation;
	private JCheckBox chckbxSearchForMeta;
	private JPanel panel;
	private JPanel tab2DIntensity;
	private JLabel lblLoadAnImage;
	private JRadioButton rb2DAuto;
	private JLabel label;
	private JRadioButton rb2DTab;
	private JRadioButton rb2DSpace;
	private JRadioButton rb2DComma;
	private JRadioButton rb2DOwn;
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
					tab2DIntensity.setLayout(new MigLayout("", "[][][]", "[][][][][][][][][]"));
					{
						lblLoadAnImage = new JLabel("Load an image from one 2D intensity matrix file (.txt or .csv)");
						tab2DIntensity.add(lblLoadAnImage, "cell 0 0 3 1");
					}
					{
						label = new JLabel("Data seperation by:");
						tab2DIntensity.add(label, "cell 0 2");
					}
					{
						rb2DAuto = new JRadioButton("automatic");
						buttonGroup_1.add(rb2DAuto);
						rb2DAuto.setToolTipText("Not always right!");
						tab2DIntensity.add(rb2DAuto, "cell 1 2");
					}
					{
						rb2DTab = new JRadioButton("tab \"intensity    intensity\"");
						buttonGroup_1.add(rb2DTab);
						rb2DTab.setSelected(true);
						tab2DIntensity.add(rb2DTab, "cell 1 3");
					}
					{
						rb2DSpace = new JRadioButton("space \"intensity intensity\"");
						buttonGroup_1.add(rb2DSpace);
						tab2DIntensity.add(rb2DSpace, "cell 1 4");
					}
					{
						rb2DComma = new JRadioButton("comma \"intensity,intensity\"");
						buttonGroup_1.add(rb2DComma);
						tab2DIntensity.add(rb2DComma, "cell 1 5");
					}
					{
						rb2DOwn = new JRadioButton("use own seperation:");
						rb2DOwn.addChangeListener(new ChangeListener() {
							public void stateChanged(ChangeEvent e) {
								JRadioButton rb = (JRadioButton)e.getSource();
				        		// monochrom panel off/on
								getTxt2DOwn().setEditable(rb.isSelected()); 
							}
						});
						buttonGroup_1.add(rb2DOwn);
						rb2DOwn.setToolTipText("Define own seperation text. (one or more characters)");
						tab2DIntensity.add(rb2DOwn, "cell 1 6");
					}
					{
						txt2DOwn = new JTextField();
						txt2DOwn.setToolTipText("Own seperation");
						txt2DOwn.setEditable(false);
						txt2DOwn.setColumns(10);
						tab2DIntensity.add(txt2DOwn, "cell 2 6,growx");
					}
					{
						cb2DMetadata = new JCheckBox("Search for meta data");
						cb2DMetadata.setToolTipText("Tries to search for meta data (non XI-paired data)");
						cb2DMetadata.setSelected(true);
						tab2DIntensity.add(cb2DMetadata, "cell 1 8");
					}
				}
			}
			{
				tabTXT = new JPanel();
				tabbedPane.addTab("Lines", null, tabTXT, null);
				tabTXT.setLayout(new MigLayout("", "[][grow][grow]", "[][][][][][][][][][][]"));
				{
					JLabel lblImportFromText = new JLabel("Import from text files. Select all files containing scan lines");
					tabTXT.add(lblImportFromText, "cell 0 0 3 1");
				}
				{
					JLabel lblDataSeperationBy = new JLabel("Data seperation by:");
					tabTXT.add(lblDataSeperationBy, "cell 0 2");
				}
				{
					rbAutomatic = new JRadioButton("automatic");
					buttonGroup.add(rbAutomatic);
					rbAutomatic.setToolTipText("Not always right!");
					tabTXT.add(rbAutomatic, "cell 1 2");
				}
				{
					rbSeperationTab = new JRadioButton("tab \"time    intensity\"");
					rbSeperationTab.setSelected(true);
					buttonGroup.add(rbSeperationTab);
					tabTXT.add(rbSeperationTab, "cell 1 3");
				}
				{
					rbSeperationSpace = new JRadioButton("space \"time intensity\"");
					buttonGroup.add(rbSeperationSpace);
					tabTXT.add(rbSeperationSpace, "cell 1 4");
				}
				{
					rbSeperationComma = new JRadioButton("comma \"time,intensity\"");
					buttonGroup.add(rbSeperationComma);
					tabTXT.add(rbSeperationComma, "cell 1 5");
				}
				{
					rdbtnUseOwnSeperation = new JRadioButton("use own seperation:");
					rdbtnUseOwnSeperation.addChangeListener(new ChangeListener() {
						public void stateChanged(ChangeEvent e) {
							JRadioButton rb = (JRadioButton)e.getSource();
			        		// monochrom panel off/on
							getTxtOwnSeparation().setEditable(rb.isSelected()); 
						}
					});
					buttonGroup.add(rdbtnUseOwnSeperation);
					rdbtnUseOwnSeperation.setToolTipText("Define own seperation text. (one or more characters)");
					tabTXT.add(rdbtnUseOwnSeperation, "cell 1 6");
				}
				{
					txtOwnSeperation = new JTextField();
					txtOwnSeperation.setEditable(false);
					txtOwnSeperation.setToolTipText("Own seperation");
					tabTXT.add(txtOwnSeperation, "cell 2 6,alignx left");
					txtOwnSeperation.setColumns(10);
				}
				{
					chckbxSearchForMeta = new JCheckBox("Search for meta data");
					chckbxSearchForMeta.setToolTipText("Tries to search for meta data (non XI-paired data)");
					chckbxSearchForMeta.setSelected(true);
					tabTXT.add(chckbxSearchForMeta, "cell 1 8");
				}
				{
					cbContinousData = new JCheckBox("Continous data (one file=one image)");
					cbContinousData.addItemListener(new ItemListener() {
						public void itemStateChanged(ItemEvent e) {
							getCbFilesInSeparateFolders().setVisible(!((JCheckBox)e.getSource()).isSelected());
						}
					});
					tabTXT.add(cbContinousData, "cell 1 9 2 1");
				}
				{
					cbFilesInSeparateFolders = new JCheckBox("Files in separate folders");
					cbFilesInSeparateFolders.setToolTipText("Unchecked: All files are in one folder. Checked: Each file has its own sub folder");
					tabTXT.add(cbFilesInSeparateFolders, "cell 1 10");
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

				// seperation  
				if(sep.equals("AUTO")) getRb2DAuto().setSelected(true);
				else if(sep.equals(",")) getRb2DComma().setSelected(true);
				else if(sep.equals(" ")) getRb2DSpace().setSelected(true);
				else if(sep.equals("\t")) getRb2DTab().setSelected(true);
				else { 
					getRb2DOwn().setSelected(true);
					getTxt2DOwn().setText(sep);
				} 

				// check for meta  
				getCb2DMetadata().setSelected(sett.isSearchingForMetaData());
				
				break;
			case CONTINOUS_DATA_TXT_CSV:
			case MULTIPLE_FILES_LINES_TXT_CSV:
				getTabbedPane().setSelectedComponent(getTabTXT());
				
				// seperation   
				if(sep.equals("AUTO")) getRbAutomatic().setSelected(true);
				else if(sep.equals(",")) getRbSeperationComma().setSelected(true);
				else if(sep.equals(" ")) getRbSeperationSpace().setSelected(true);
				else if(sep.equals("\t")) getRbSeperationTab().setSelected(true);
				else { 
					getRdbtnUseOwnSeperation().setSelected(true);
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
			String separation = "";
			if(getRbAutomatic().isSelected()) separation = "AUTO";
			else if(getRbSeperationComma().isSelected()) separation = ",";
			else if(getRbSeperationSpace().isSelected()) separation = " ";
			else if(getRbSeperationTab().isSelected()) separation = "	";
			else if(getRdbtnUseOwnSeperation().isSelected()) separation = getTxtOwnSeparation().getText();
			// check for meta
			boolean checkformeta = getChckbxSearchForMeta().isSelected();
			boolean isContinousData = getCbContinousData().isSelected();
			boolean isFilesInSeparateFolders = isContinousData? false : getCbFilesInSeparateFolders().isSelected();
			// create settings
			settingsDataImport = new SettingsImageDataImportTxt(isContinousData? IMPORT.CONTINOUS_DATA_TXT_CSV : IMPORT.MULTIPLE_FILES_LINES_TXT_CSV, checkformeta, separation, filter, isFilesInSeparateFolders);
		}
		else if(getTabbedPane().getSelectedComponent().equals(getTab2DIntensity())) {
			// seperation
			String seperation = "";
			if(getRb2DAuto().isSelected()) seperation = "AUTO";
			else if(getRb2DComma().isSelected()) seperation = ",";
			else if(getRb2DSpace().isSelected()) seperation = " ";
			else if(getRb2DTab().isSelected()) seperation = "	";
			else if(getRb2DOwn().isSelected()) seperation = getTxt2DOwn().getText();
			// check for meta
			boolean checkformeta = getCb2DMetadata().isSelected();
			// create settings
			settingsDataImport = new SettingsImageDataImportTxt(IMPORT.ONE_FILE_2D_INTENSITY, checkformeta, seperation, filter, false);
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

	public JTextField getTxtOwnSeparation() {
		return txtOwnSeperation;
	}
	public JPanel getTabTXT() {
		return tabTXT;
	}
	public JPanel getPanEXCEL() {
		return panEXCEL;
	}
	public JRadioButton getRbAutomatic() {
		return rbAutomatic;
	}
	public JRadioButton getRbSeperationTab() {
		return rbSeperationTab;
	}
	public JRadioButton getRbSeperationSpace() {
		return rbSeperationSpace;
	}
	public JRadioButton getRbSeperationComma() {
		return rbSeperationComma;
	}
	public JRadioButton getRdbtnUseOwnSeperation() {
		return rdbtnUseOwnSeperation;
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
	public JRadioButton getRb2DOwn() {
		return rb2DOwn;
	}
	public JRadioButton getRb2DComma() {
		return rb2DComma;
	}
	public JTextField getTxt2DOwn() {
		return txt2DOwn;
	}
	public JRadioButton getRb2DSpace() {
		return rb2DSpace;
	}
	public JRadioButton getRb2DTab() {
		return rb2DTab;
	}
	public JRadioButton getRb2DAuto() {
		return rb2DAuto;
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
}
