package net.rs.lamsi.multiimager.Frames.dialogs;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.massimager.Settings.SettingsDataSaver;
import net.rs.lamsi.massimager.Settings.SettingsHolder;
import net.rs.lamsi.massimager.Settings.image.SettingsImage2DDataExport;
import net.rs.lamsi.massimager.Settings.image.SettingsImage2DDataExport.FileType;
import net.rs.lamsi.multiimager.Frames.dialogs.selectdata.RectSelection;
import net.rs.lamsi.multiimager.Frames.dialogs.selectdata.SelectionTableRow;
import net.rs.lamsi.utils.DataExportUtil;
import net.rs.lamsi.utils.DialogLoggerUtil;
import net.rs.lamsi.utils.mywriterreader.XSSFExcelWriterReader;

/**
 * is only for exporting data of image2d
 * @author vukmir69
 *
 */
public class DialogDataSaver extends JFrame {
	// MYSTUFF 
	private static enum MODE {
		SELECTED_RECTS, ALL
	}
	private MODE currentMode;
	// ExcelWriter 
	private static DialogDataSaver inst;
	private SettingsHolder settings;
	private XSSFExcelWriterReader excelWriter;
	// 
	final private JFileChooser fcDirectoriesChooser = new JFileChooser();
	// 
	private Image2D img;
	private Vector<Image2D> imgList;
	protected Vector<RectSelection> rects, rectsExcluded, rectsInfo; 
	protected Vector<SelectionTableRow> tableRows;
	// 
	private JPanel contentPane;
	private JTextField txtPath;
	private JTextField txtFileName;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JPanel pnXlsOptions;
	private final ButtonGroup buttonGroup_1 = new ButtonGroup();
	private JLabel lblSucceed;
	private JCheckBox cbTimeOnlyOnce;
	private JCheckBox cbSaveAllFilesInOne;
	private JRadioButton rbAllFiles;
	private JRadioButton rbSelectedFileOnly;
	private JPanel pnMS;
	private final ButtonGroup buttonGroup_2 = new ButtonGroup();
	private final ButtonGroup buttonGroup_3 = new ButtonGroup();
	//
	private JCheckBox cbWriteTitleRow;
	private JCheckBox cbRawData;
	private JCheckBox cbExportAsXYZ;
	private JTextField txtSep;
	private JLabel lbSep;
	private JCheckBox cbWriteNoTime;
	private JCheckBox rbToClipboard;
	private JPanel panel_1;
	private JComboBox comboBox;
	
	// TODO
	// Irgendwann das aufrufen: 
	//	runner.saveDataFile();

	public static DialogDataSaver createInst(SettingsHolder sett) {
		inst = new DialogDataSaver(sett);
		return inst;
	} 
	
	/**
	 * Create the frame.
	 */
	public DialogDataSaver(SettingsHolder sett) {
        fcDirectoriesChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		final DialogDataSaver thisFrame = this;
		this.excelWriter = new XSSFExcelWriterReader();
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		// Settings
		this.settings = sett; 
		// 
		setBounds(100, 100, 422, 463);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[56px][6px][100px][6px][109px,grow][9px]", "[14px][23px][23px][23px][243px,center][33px]"));
		
		txtPath = new JTextField();
		contentPane.add(txtPath, "cell 0 1 5 1,growx,aligny center");
		txtPath.setColumns(10);
		
		JLabel lblPath = new JLabel("Path");
		contentPane.add(lblPath, "cell 0 0,growx,aligny top");
		
		JButton btnSelectPath = new JButton("Select path");
		btnSelectPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// open fileChooser 
				fcDirectoriesChooser.showOpenDialog(thisFrame);
		    	File file = fcDirectoriesChooser.getSelectedFile(); 
		        if(file!=null) { 
		        	getTxtPath().setText(file.getPath());
		        	settings.getSetImage2DDataExport().setPath(file);
		        }
			}
		});
		contentPane.add(btnSelectPath, "cell 5 1,alignx left,aligny top");
		
		JLabel lblFilename = new JLabel("Filename:");
		contentPane.add(lblFilename, "cell 0 2,alignx right,aligny center");
		
		txtFileName = new JTextField();
		txtFileName.setColumns(10);
		contentPane.add(txtFileName, "cell 2 2,growx,aligny center");
		
		comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(FileType.values()));
		comboBox.setSelectedIndex(0);
		contentPane.add(comboBox, "cell 4 2,growx");
		
		rbToClipboard = new JCheckBox("to clipboard");
		rbToClipboard.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				getTxtFileName().setEnabled(!rbToClipboard.isSelected());
				getTxtPath().setEnabled(!rbToClipboard.isSelected());
				comboBox.setEnabled(!rbToClipboard.isSelected()); 
			}
		});
		contentPane.add(rbToClipboard, "cell 5 2");
		
		JPanel Buttonsreihe = new JPanel();
		contentPane.add(Buttonsreihe, "cell 0 5 6 1,growx,aligny top");
		
		
		JButton btnLoadSettings = new JButton("Load settings");
		btnLoadSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					settings.loadSettingsFromFile(thisFrame, settings.getSetImage2DDataExport());
				} catch (Exception e1) { 
					e1.printStackTrace();
					DialogLoggerUtil.showErrorDialog(thisFrame, "Error while loading ", e1);
				}
				setAllSettingsCb();
			}
		});
		Buttonsreihe.add(btnLoadSettings);
		
		JButton btnSaveSettings = new JButton("Save settings");
		Buttonsreihe.add(btnSaveSettings);
		btnSaveSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					settings.saveSettingsToFile(thisFrame, settings.getSetImage2DDataExport());
				} catch (Exception e) { 
					e.printStackTrace();
					DialogLoggerUtil.showErrorDialog(thisFrame, "Error while saving", e);
				}
			}
		});
		
		JButton btnClose = new JButton("Close");
		Buttonsreihe.add(btnClose);
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				thisFrame.setVisible(false);
			}
		});
		
		JButton btnSave = new JButton("Save");
		Buttonsreihe.add(btnSave);
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// renew all settings
				renewAllSettings();
				// Save Data to file at path
				try {
					boolean exported = false; 
					if(currentMode==MODE.ALL) {
						if(imgList==null || (settings.getSetImage2DDataExport().isExportsAllFiles()==false && img!=null))
							DataExportUtil.exportDataImage2D(img, settings.getSetImage2DDataExport());
						else if(imgList!=null) 
							DataExportUtil.exportDataImage2D(thisFrame, imgList, settings.getSetImage2DDataExport());
					}
					else if(currentMode == MODE.SELECTED_RECTS){
						DataExportUtil.exportDataImage2DInRects(img, rects, rectsExcluded, rectsInfo, tableRows, settings.getSetImage2DDataExport());
					}
				} catch(Exception ex) { 
					DialogLoggerUtil.showErrorDialog(thisFrame, "Not saved", ex);
					ex.printStackTrace();
				}
			}
		});
		
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		contentPane.add(panel, "cell 0 4 6 1,growy");
		panel.setLayout(new MigLayout("", "[365px]", "[242px]"));
		
		pnMS = new JPanel();
		panel.add(pnMS, "cell 0 0,grow");
		pnMS.setLayout(new BorderLayout(0, 0));
		
		pnXlsOptions = new JPanel();
		pnMS.add(pnXlsOptions);
		pnXlsOptions.setLayout(new MigLayout("", "[178px][154px]", "[23px][23px][23px][23px][14px]"));
		
		rbAllFiles = new JRadioButton("All images");
		rbAllFiles.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				// TODO manche cb deaktivieren
				JRadioButton rb = (JRadioButton) e.getSource();
				try {
					if(rb.isSelected()) getCbSaveAllFilesInOne().setEnabled(true); 
					else getCbSaveAllFilesInOne().setEnabled(false);
				}catch(Exception ex) { 
				}
			}
		});
		buttonGroup_1.add(rbAllFiles);
		rbAllFiles.setSelected(true);
		rbAllFiles.setToolTipText("Export all loaded files at once");
		pnXlsOptions.add(rbAllFiles, "cell 0 0,growx,aligny top");
		
		cbSaveAllFilesInOne = new JCheckBox("Save all to one .xlsx file");
		cbSaveAllFilesInOne.setSelected(true);
		cbSaveAllFilesInOne.setToolTipText("Save data from all files in one xls-file or in seperate ones");
		pnXlsOptions.add(cbSaveAllFilesInOne, "cell 1 0,growx,aligny top");
		
		rbSelectedFileOnly = new JRadioButton("Selected image only");
		buttonGroup_1.add(rbSelectedFileOnly);
		rbSelectedFileOnly.setToolTipText("Export only the selected file");
		pnXlsOptions.add(rbSelectedFileOnly, "cell 0 1,growx,aligny top");
		
		lblSucceed = new JLabel("Succeed");
		lblSucceed.setVisible(false);
		
		cbWriteNoTime = new JCheckBox("Write no time column");
		cbWriteNoTime.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				getCbTimeOnlyOnce().setEnabled(e.getStateChange() != ItemEvent.SELECTED);
			}
		});
		pnXlsOptions.add(cbWriteNoTime, "flowy,cell 0 3");
		
		cbTimeOnlyOnce = new JCheckBox("Write time only once");
		cbTimeOnlyOnce.setSelected(true);
		cbTimeOnlyOnce.setToolTipText("Writes the time only once followed by inensities for all scans");
		pnXlsOptions.add(cbTimeOnlyOnce, "cell 0 3,growx,aligny top");
		
		cbExportAsXYZ = new JCheckBox("Export as XYZ");
		cbExportAsXYZ.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) { 
				getCbTimeOnlyOnce().setEnabled(e.getStateChange() != ItemEvent.SELECTED && !getCbWriteNoTime().isSelected());
				getCbWriteNoTime().setEnabled(e.getStateChange() != ItemEvent.SELECTED);
			}
		});
		pnXlsOptions.add(cbExportAsXYZ, "cell 1 3");
		
		cbWriteTitleRow = new JCheckBox("Write title row");
		cbWriteTitleRow.setSelected(true);
		pnXlsOptions.add(cbWriteTitleRow, "cell 0 4");
		lblSucceed.setForeground(new Color(0, 128, 0));
		pnXlsOptions.add(lblSucceed, "cell 1 4,alignx right,aligny top");
		
		panel_1 = new JPanel();
		pnMS.add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new MigLayout("", "[]", "[][]"));
		
		cbRawData = new JCheckBox("Raw data");
		panel_1.add(cbRawData, "cell 0 0");
		cbRawData.setSelected(true);
		
		lbSep = new JLabel("Separation");
		panel_1.add(lbSep, "flowx,cell 0 1");
		
		txtSep = new JTextField();
		panel_1.add(txtSep, "cell 0 1");
		txtSep.setToolTipText("Separation for txt. (tab = \\t)");
		txtSep.setHorizontalAlignment(SwingConstants.CENTER);
		txtSep.setText(",");
		txtSep.setColumns(3);
	}
	
	
	/**
	 * txt or xlsx or clipboard
	 * @param currentMode
	 */
	public void setCurrentMode(MODE m) {
		// show right panels:
		getPnMS().setVisible(true);
		getPnXlsOptions().setVisible(m == MODE.ALL);
		currentMode = m;
	}
	
	/**
	 * open the dialog with imagelist
	 * @param imgList
	 */
	public static void startDialogWith(Vector<Image2D> imgList, Image2D img) {
		inst.setCurrentMode(MODE.ALL);
		inst.img=img;
		inst.imgList = imgList;
		inst.setVisible(true); 
	}
	public static void startDialogWith(Image2D img) {
		inst.setCurrentMode(MODE.ALL);
		inst.img=img;
		inst.imgList = null;
		inst.rects = null;
		inst.rectsExcluded = null; 
		inst.rectsInfo =null;
		inst.tableRows = null;
		inst.setVisible(true);
	}
	
	//##########################################################################################
	// exporting only selection rects data
	public static void startDialogWith(Image2D img, Vector<RectSelection> rects, Vector<RectSelection> rectsExcluded, Vector<RectSelection> rectsInfo, Vector<SelectionTableRow> tableRows) {
		startDialogWith(img);
		inst.rects = rects;
		inst.rectsExcluded = rectsExcluded;
		inst.rectsInfo = rectsInfo;
		inst.tableRows = tableRows;
		inst.setCurrentMode(MODE.SELECTED_RECTS);
	}
	
	
	//##########################################################################################
	// 
	private void renewAllSettings() {
		SettingsImage2DDataExport settingsData = this.settings.getSetImage2DDataExport();
		// Set mode 
		settingsData.setPath(getTxtPath().getText());
		
		FileType type = (FileType)comboBox.getSelectedItem();
		settingsData.setFileFormat(type);
		settingsData.setWritingToClipboard(getCbToClipboard().isSelected());
		
		// always one file if to clipboard
		settingsData.setExportsAllFiles(getRbAllFiles().isSelected() && !getCbToClipboard().isSelected());
		settingsData.setFilename(getTxtFileName().getText());
		settingsData.setSavesAllFilesToOneXLS(getCbSaveAllFilesInOne().isSelected());
		 
		settingsData.setWriteTimeOnlyOnce(getCbTimeOnlyOnce().isSelected()); 
		settingsData.setIsExportRaw(getCbRawData().isSelected());
		settingsData.setIsWriteTitleRow(getCbWriteTitleRow().isSelected());
		settingsData.setIsWriteXYZData(getCbExportAsXYZ().isSelected());
		settingsData.setIsWriteNoX(getCbWriteNoTime().isSelected());
		
		settingsData.setSeparation(txtSep.getText());
	}
	// all cb set
	public void setAllSettingsCb() {
		SettingsImage2DDataExport settingsData = this.settings.getSetImage2DDataExport(); 

		getRbAllFiles().setSelected(settingsData.isExportsAllFiles());
		// TODO select combo
		comboBox.setSelectedItem(settingsData.getFileFormat());
		//
		getCbToClipboard().setSelected(settingsData.isWritingToClipboard());
		
		getTxtFileName().setText(settingsData.getFilename());
		getCbSaveAllFilesInOne().setSelected(settingsData.isSavesAllFilesToOneXLS());
		
		getTxtPath().setText(settingsData.getPath().getPath());

		getCbTimeOnlyOnce().setSelected(settingsData.isWriteTimeOnlyOnce()); 
		getCbRawData().setSelected(settingsData.isExportRaw());
		getCbWriteTitleRow().setSelected(settingsData.isWriteTitleRow());
		getCbExportAsXYZ().setSelected(settingsData.isWriteXYZData());
		getCbWriteNoTime().setSelected(settingsData.isWriteNoX());
		
		getTxtSep().setText(settingsData.getSeparation());
	}
	
	
	// GETTERS AND SETTERS
	public JPanel getPnXlsOptions() {
		return pnXlsOptions;
	}
	public JLabel getLblSucceed() {
		return lblSucceed;
	}
	public JCheckBox getCbTimeOnlyOnce() {
		return cbTimeOnlyOnce;
	}
	public JTextField getTxtPath() {
		return txtPath; 
	}
	public JTextField getTxtFileName() {
		return txtFileName;
	}
	public JCheckBox getCbSaveAllFilesInOne() {
		return cbSaveAllFilesInOne;
	}
	public JRadioButton getRbAllFiles() {
		return rbAllFiles;
	}
	public JRadioButton getRbSelectedFileOnly() {
		return rbSelectedFileOnly;
	}
	public JPanel getPnMS() {
		return pnMS;
	}
	public JCheckBox getCbWriteTitleRow() {
		return cbWriteTitleRow;
	}
	public JCheckBox getCbRawData() {
		return cbRawData;
	}
	public JCheckBox getCbExportAsXYZ() {
		return cbExportAsXYZ;
	}
	public JLabel getLbSep() {
		return lbSep;
	}
	public JTextField getTxtSep() {
		return txtSep;
	}
	public JCheckBox getCbWriteNoTime() {
		return cbWriteNoTime;
	}
	public JCheckBox getCbToClipboard() {
		return rbToClipboard;
	}
}
