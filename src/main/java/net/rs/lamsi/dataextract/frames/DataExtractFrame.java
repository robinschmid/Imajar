package net.rs.lamsi.dataextract.frames;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.dataextract.presets.GroupAndNameProducer;
import net.rs.lamsi.dataextract.presets.PresetsImportInstrument;
import net.rs.lamsi.dataextract.presets.ProducerPresetsDataExtract;
import net.rs.lamsi.massimager.Settings.image.SettingsImageDataImport;
import net.rs.lamsi.massimager.Settings.image.SettingsImageDataImportTxt;
import net.rs.lamsi.massimager.Settings.image.SettingsImageDataImportTxt.IMPORT;

public class DataExtractFrame extends JFrame {
	// 
	public static final int CENTER_PRESETS = 0, CENTER_IMPORT = 1, CENTER_OUTPUT = 2, CENTER_EXTRACT = 3;
	// list of producers for import
	protected Vector<ProducerPresetsDataExtract> vecProducer;
	//
	protected PresetsImportInstrument settingsDataImport = null;
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
	private JPanel panel_2;
	private JScrollPane scrollPane;
	private JList listProducer;
	private JScrollPane scrollPane_1;
	private JList listInstruments;
	private JPanel panel_3;
	private JToggleButton btnPresets;
	private JToggleButton btnInput;
	private JToggleButton btnOutput;
	private JPanel panel_4;
	private JButton btnInRemove;
	private final ButtonGroup buttonGroup_2 = new ButtonGroup();
	private JPanel pnCenter;
	private JTabbedPane tabbedInput;
	private JPanel pnPresets;
	private JToggleButton btnConvert;
	private JButton btnTopRemove;
	private JScrollPane scrollPane_2;
	private JList listTopPresets;
	private JPanel pnOutputLoadnConvert;
	private JTabbedPane tabbedOutput;
	private JPanel tabOutPresets;
	private JPanel tabOutTxt;
	private JPanel tabOutXlsx;
	private JPanel panel_7;
	private JButton btnTopContinue;
	private JPanel pnLoadNConvert;
	private JButton btnAddToPresets;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			DataExtractFrame dialog = new DataExtractFrame();
			dialog.initFunctionsAndLoadPresets();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public DataExtractFrame() { 
		setBounds(100, 100, 897, 438);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			panel_3 = new JPanel();
			contentPanel.add(panel_3, BorderLayout.NORTH);
			{
				btnPresets = new JToggleButton("Presets");
				btnPresets.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						showRightCenterContent();
					}
				});
				buttonGroup_2.add(btnPresets);
				btnPresets.setSelected(true);
				panel_3.add(btnPresets);
			}
			{
				btnInput = new JToggleButton("Input");
				btnInput.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						showRightCenterContent();
					}
				});
				buttonGroup_2.add(btnInput);
				panel_3.add(btnInput);
			}
			{
				btnOutput = new JToggleButton("Output");
				btnOutput.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						showRightCenterContent();
					}
				});
				buttonGroup_2.add(btnOutput);
				panel_3.add(btnOutput);
			}
			{
				btnConvert = new JToggleButton("Load 'n' Convert");
				btnConvert.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						showRightCenterContent();
					}
				});
				buttonGroup_2.add(btnConvert);
				panel_3.add(btnConvert);
			}
		}
		{
			pnCenter = new JPanel();
			contentPanel.add(pnCenter, BorderLayout.CENTER);
			pnCenter.setLayout(new BorderLayout(0, 0));
			{
				tabbedInput = new JTabbedPane(JTabbedPane.TOP);
				pnCenter.add(tabbedInput, BorderLayout.EAST);
				{
					tabMSPresets = new JPanel();
					tabbedInput.addTab("MS presets", null, tabMSPresets, null);
					tabMSPresets.setLayout(new BorderLayout(0, 0));
					{
						panel_1 = new JPanel();
						tabMSPresets.add(panel_1);
						panel_1.setLayout(new MigLayout("", "[grow]", "[][grow]"));
						{
							panel_4 = new JPanel();
							panel_1.add(panel_4, "cell 0 0,grow");
							{
								btnInRemove = new JButton("remove");
								panel_4.add(btnInRemove);
							}
						}
						{
							panel_2 = new JPanel();
							panel_1.add(panel_2, "cell 0 1,grow");
							panel_2.setLayout(new BorderLayout(0, 0));
							{
								scrollPane = new JScrollPane();
								panel_2.add(scrollPane, BorderLayout.WEST);
								{
									listProducer = new JList(new DefaultListModel());
									listProducer.addListSelectionListener(new ListSelectionListener() {
										public void valueChanged(ListSelectionEvent e) {
											if(getListProducer().getSelectedIndex()>=0) {
												// show all presets from producer in import settings
												showPresetsFromSelectedProducer(getListProducer().getSelectedIndex());
											}
										}
									});
									listProducer.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
									scrollPane.setViewportView(listProducer);
								}
							}
							{
								scrollPane_1 = new JScrollPane();
								panel_2.add(scrollPane_1, BorderLayout.CENTER);
								{
									listInstruments = new JList(new DefaultListModel());
									listInstruments.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
									scrollPane_1.setViewportView(listInstruments);
								}
							}
						}
					}
				}
				{
					panel = new JPanel();
					tabbedInput.addTab("2D Intensity", null, panel, null);
					panel.setLayout(new BorderLayout(0, 0));
					{
						tab2DIntensity = new JPanel();
						panel.add(tab2DIntensity);
						tab2DIntensity.setLayout(new MigLayout("", "[][][grow]", "[][][][][][][][][]"));
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
					tabbedInput.addTab("Text (.txt)", null, tabTXT, null);
					tabTXT.setLayout(new MigLayout("", "[][][grow]", "[][][][][][][][][]"));
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
				}
				{
					panEXCEL = new JPanel();
					tabbedInput.addTab("Excel (.xlsx; .xls)", null, panEXCEL, null);
					panEXCEL.setLayout(new BorderLayout(0, 0));
				}
			}
			{
				pnPresets = new JPanel();
				pnCenter.add(pnPresets, BorderLayout.WEST);
				pnPresets.setLayout(new BorderLayout(0, 0));
				{
					scrollPane_2 = new JScrollPane();
					pnPresets.add(scrollPane_2, BorderLayout.CENTER);
					{
						listTopPresets = new JList(new DefaultListModel());
						listTopPresets.addListSelectionListener(new ListSelectionListener() {
							public void valueChanged(ListSelectionEvent e) {
								// TODO set all fields
								
								// double click jump to final load section?
							}
						});
						listTopPresets.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
						scrollPane_2.setViewportView(listTopPresets);
					}
				}
				{
					panel_7 = new JPanel();
					pnPresets.add(panel_7, BorderLayout.SOUTH);
					{
						btnTopRemove = new JButton("Remove");
						panel_7.add(btnTopRemove);
					}
					{
						btnTopContinue = new JButton("Continue");
						panel_7.add(btnTopContinue);
					}
				}
			}
			{
				pnOutputLoadnConvert = new JPanel();
				pnCenter.add(pnOutputLoadnConvert, BorderLayout.CENTER);
				pnOutputLoadnConvert.setLayout(new BorderLayout(0, 0));
				{
					tabbedOutput = new JTabbedPane(JTabbedPane.TOP);
					pnOutputLoadnConvert.add(tabbedOutput, BorderLayout.CENTER);
					{
						tabOutPresets = new JPanel();
						tabbedOutput.addTab("Presets", null, tabOutPresets, null);
					}
					{
						tabOutTxt = new JPanel();
						tabbedOutput.addTab(".txt/.csv", null, tabOutTxt, null);
					}
					{
						tabOutXlsx = new JPanel();
						tabbedOutput.addTab(".xlsx", null, tabOutXlsx, null);
					}
				}
				{
					pnLoadNConvert = new JPanel();
					pnOutputLoadnConvert.add(pnLoadNConvert, BorderLayout.EAST);
				}
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
						createSettingsForImport("");
						// open data with settings but first open filechoose
					}
				});
				{
					btnAddToPresets = new JButton("Add to presets");
					btnAddToPresets.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							addCurrentToPresets();
						}
					});
					buttonPane.add(btnAddToPresets);
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
	

	protected void addCurrentToPresets() {
		// get current center content (import/output/all)
		int c = getCurrentCenterContent();
		// get name and group by dialog
		AddImportPresetsDialog dialog = new AddImportPresetsDialog();
		GroupAndNameProducer name = dialog.showDialog(c==CENTER_IMPORT);
		if(name!=null) {
			// put them in the list
			DefaultListModel model;
			switch(c) {
			case CENTER_PRESETS: 
				model = (DefaultListModel) getListTopPresets().getModel();
				break;
			case CENTER_IMPORT:
				// create import preset
				PresetsImportInstrument imp = createSettingsForImport(name.name); 
				// added?
				int added = -1;
				// search for producer
				for(int i=0; i<vecProducer.size(); i++) {
					if(name.group.equalsIgnoreCase(vecProducer.get(i).getName())) {
						// add to existing group
						vecProducer.get(i).addInstrument(imp);
						added = 1;
					}
				}
				if(added==-1) {
					// add new producer
					for(int i=0; i<vecProducer.size(); i++) {
						if(name.group.compareToIgnoreCase(vecProducer.get(i).getName())<=0) {
							added = i;
							vecProducer.add(i, new ProducerPresetsDataExtract(name.group));
						}
					}
					if(added==-1) {
						vecProducer.add(new ProducerPresetsDataExtract(name.group));
						added = vecProducer.size()-1;
					}
					// add instrument to new prod
					vecProducer.get(added).addInstrument(imp);
				}
				// save them to a file
				
				// show them in the lists
				updateImportPresetsInLists();
				break; // END OF IMPORT
			}
		}
		else {
			System.out.println("NO NAME INPUT");
		}
		// save them to a file
		
		// update
	}
	 

	private void updateImportPresetsInLists() {
		int i= getListProducer().getSelectedIndex();
		i = i>=0? i : 0;
		// remove all
		DefaultListModel modelProd = (DefaultListModel) getListProducer().getModel();
		modelProd.removeAllElements();
		DefaultListModel model = (DefaultListModel) getListInstruments().getModel();
		model.removeAllElements();
		// add producer
		for(ProducerPresetsDataExtract p : vecProducer) {
			modelProd.addElement(p.getName());
		}
		// select first
		getListProducer().setSelectedIndex(i);
	}

	public int getCurrentCenterContent() {
		if(getBtnPresets().isSelected()) 
			return CENTER_PRESETS;
		else if(getBtnInput().isSelected()) 
			return CENTER_IMPORT;
		else if(getBtnOutput().isSelected()) 
			return CENTER_OUTPUT;
		else if(getBtnConvert().isSelected())  
			return CENTER_EXTRACT;
		else return 0;
	}
	 

	protected void showPresetsFromSelectedProducer(int selectedIndex) {
		// remove all from list
		DefaultListModel model = (DefaultListModel) getListInstruments().getModel();
		model.removeAllElements();
		// add all new Instruments from producer 
		ProducerPresetsDataExtract p = vecProducer.get(selectedIndex);
		for(PresetsImportInstrument imp : p.getInstruments()) {
			model.addElement(imp.getName());
		}
	}

	/**
	 * Has to be run once, will load all presets and reset all fields
	 */
	public void initFunctionsAndLoadPresets() {
		// show center content
		showRightCenterContent();
		// load presets for import TODO
		vecProducer = new Vector<ProducerPresetsDataExtract>();
		// load presets for output
		
		// load presets for general
		
		// 
		
	}
	
	public void showRightCenterContent() {
		//
		getPnCenter().removeAll(); 
		
		if(getBtnPresets().isSelected()) {
			getPnCenter().add(getPnPresets(), BorderLayout.CENTER);
		}
		else if(getBtnInput().isSelected()) {
			getPnCenter().add(getTabbedInput(), BorderLayout.CENTER);
		}
		else if(getBtnOutput().isSelected()) { 
			getPnCenter().add(getTabbedOutput(), BorderLayout.CENTER);
		}
		else if(getBtnConvert().isSelected()) {  
			getPnCenter().add(getPnLoadNConvert(), BorderLayout.CENTER);
		}
		this.revalidate();
		this.repaint();
	}

	protected PresetsImportInstrument createSettingsForImport(String name) {
		// text file
		if(getTabTXT().isShowing()) {
			// seperation
			String separation = "";
			if(getRbAutomatic().isSelected()) separation = "AUTO";
			else if(getRbSeperationComma().isSelected()) separation = ",";
			else if(getRbSeperationSpace().isSelected()) separation = " ";
			else if(getRbSeperationTab().isSelected()) separation = "	";
			else if(getRdbtnUseOwnSeperation().isSelected()) separation = getTxtOwnSeparation().getText();
			// check for meta
			boolean checkformeta = getChckbxSearchForMeta().isSelected();
			// create settings
			settingsDataImport = new PresetsImportInstrument(name, IMPORT.MULTIPLE_FILES_LINES_TXT_CSV, checkformeta, separation);
			return settingsDataImport;
		}
		else if(getTab2DIntensity().isShowing()) {
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
			settingsDataImport = new PresetsImportInstrument(name, IMPORT.ONE_FILE_2D_INTENSITY, checkformeta, seperation);
			return settingsDataImport;
		}
		else if(getTabMSPresets().isShowing()) {
			String separation = "	";
			boolean checkformeta = true;
			IMPORT importMode = IMPORT.PRESETS_THERMO_MP17;
			//if(getRbMP17Thermo().isSelected()) importMode = SettingsImageDataImportTxt.IMPORT_PRESETS_THERMO_MP17;
			settingsDataImport = new PresetsImportInstrument(name, importMode, checkformeta, separation);
			return settingsDataImport;
		}
		return null;
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
		return tab2DIntensity;
	}
	public JPanel getTabMSPresets() {
		return tabMSPresets;
	}
	public JList getListInstruments() {
		return listInstruments;
	}
	public JList getListProducer() {
		return listProducer;
	}
	public JToggleButton getBtnPresets() {
		return btnPresets;
	}
	public JToggleButton getBtnInput() {
		return btnInput;
	}
	public JToggleButton getBtnOutput() {
		return btnOutput;
	}
	public JTabbedPane getTabbedInput() {
		return tabbedInput;
	}
	public JToggleButton getBtnConvert() {
		return btnConvert;
	}
	public JButton getBtnTopRemove() {
		return btnTopRemove;
	}
	public JList getListTopPresets() {
		return listTopPresets;
	}
	public JPanel getPnPresets() {
		return pnPresets;
	}
	public JTabbedPane getTabbedOutput() {
		return tabbedOutput;
	}
	public JPanel getPnLoadNConvert() {
		return pnLoadNConvert;
	}
	public JPanel getPnOutputLoadnConvert() {
		return pnOutputLoadnConvert;
	}
	public JPanel getPnCenter() {
		return pnCenter;
	}
}
