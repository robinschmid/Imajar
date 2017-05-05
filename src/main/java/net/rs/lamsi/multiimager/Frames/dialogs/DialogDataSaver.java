package net.rs.lamsi.multiimager.Frames.dialogs;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
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
import net.rs.lamsi.general.settings.SettingsHolder;
import net.rs.lamsi.general.settings.image.selection.SettingsSelections;
import net.rs.lamsi.general.settings.image.selection.SettingsShapeSelection;
import net.rs.lamsi.general.settings.importexport.SettingsImage2DDataExport;
import net.rs.lamsi.general.settings.importexport.SettingsImage2DDataSelectionsExport;
import net.rs.lamsi.general.settings.importexport.SettingsImage2DDataExport.FileType;
import net.rs.lamsi.general.settings.importexport.SettingsImageDataImportTxt.ModeData;
import net.rs.lamsi.multiimager.Frames.dialogs.selectdata.SelectionTableRow;
import net.rs.lamsi.multiimager.utils.imageimportexport.DataExportUtil;
import net.rs.lamsi.utils.DialogLoggerUtil;
import net.rs.lamsi.utils.mywriterreader.XSSFExcelWriterReader;

import java.awt.GridLayout;

import javax.swing.BoxLayout;

import com.itextpdf.text.pdf.qrcode.Mode;

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
	private SettingsSelections selections;
	// 
	private JPanel contentPane;
	private JTextField txtPath;
	private JTextField txtFileName;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JPanel pnXlsOptions;
	private final ButtonGroup buttonGroup_1 = new ButtonGroup();
	private JLabel lblSucceed;
	private JCheckBox cbSaveAllFilesInOne;
	private JRadioButton rbAllFiles;
	private JRadioButton rbSelectedFileOnly;
	private JPanel pnMS;
	private final ButtonGroup buttonGroup_2 = new ButtonGroup();
	private final ButtonGroup buttonGroup_3 = new ButtonGroup();
	//
	private JCheckBox cbWriteTitleRow;
	private JCheckBox cbRawData;
	private JTextField txtSep;
	private JLabel lbSep;
	private JCheckBox rbToClipboard;
	private JPanel panel_1;
	private JComboBox comboBox;
	private JComboBox comboDataFormat;
	private JPanel pnExportSelections;
	private JCheckBox cbSummary;
	private JCheckBox cbDef;
	private JCheckBox cbArray;
	private JCheckBox cbImgSelNEx;
	private JCheckBox cbImgSel;
	private JCheckBox cbImgEx;
	private JCheckBox cbShapesSelNEx;
	private JCheckBox cbShapes, cbShapesData, cbX, cbY, cbZ;
	private JPanel pnCenter;
	
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
		setBounds(100, 100, 437, 499);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[56px,grow][6px][100px][6px][109px,grow][9px]", "[14px][23px][23px][23px][243px,center][33px]"));
		
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
						else if(imgList!=null && imgList.size()>0) 
							DataExportUtil.exportDataImage2D(thisFrame, imgList, settings.getSetImage2DDataExport());
					}
					else if(currentMode == MODE.SELECTED_RECTS){
						DataExportUtil.exportDataImage2DInRects(img, selections, settings.getSetImage2DDataSelectionsExport());
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
		panel.setLayout(new MigLayout("", "[365px,grow]", "[grow]"));
		
		pnCenter = new JPanel();
		panel.add(pnCenter, "cell 0 0,grow");
		pnCenter.setLayout(new BorderLayout(0, 0));
		
		pnMS = new JPanel();
		pnCenter.add(pnMS, BorderLayout.CENTER);
		pnMS.setLayout(new BorderLayout(0, 0));
		
		pnXlsOptions = new JPanel();
		pnMS.add(pnXlsOptions);
		pnXlsOptions.setLayout(new MigLayout("", "[178px,grow][154px]", "[23px][23px][23px][23px][14px]"));
		
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
		
		cbWriteTitleRow = new JCheckBox("Write title row");
		cbWriteTitleRow.setSelected(true);
		pnXlsOptions.add(cbWriteTitleRow, "cell 0 3");
		
		comboDataFormat = new JComboBox();
		comboDataFormat.setToolTipText("Select the data export pattern: X-matrix is the standard and exports one xmatrix.txt file to keep information about data point width. XYYY is for equally spaced data points. XYXY (altern.) is for data points not spaced equally. Only-Y has no information about spacing.");
		comboDataFormat.setModel(new DefaultComboBoxModel(ModeData.values()));
		comboDataFormat.setSelectedIndex(0);
		pnXlsOptions.add(comboDataFormat, "cell 0 4,growx");
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

		// export selections panel
		pnExportSelections = new JPanel();
		pnExportSelections.setLayout(new MigLayout("", "[]", "[][][][][][][][][][][][]"));
		
		cbSummary = new JCheckBox("Summary table");
		pnExportSelections.add(cbSummary, "cell 0 0");
		cbSummary.setSelected(true);
		
		cbDef = new JCheckBox("Definitions");
		pnExportSelections.add(cbDef, "cell 0 1");
		cbDef.setSelected(true);
		
		cbArray = new JCheckBox("Select. Excl. as data column");
		cbArray.setToolTipText("Save all data points that are excluded, selected or selected and not excluded to data columns.");
		pnExportSelections.add(cbArray, "cell 0 2");
		cbArray.setSelected(true);
		
		cbImgSelNEx = new JCheckBox("Img select. non excl.");
		cbImgSelNEx.setToolTipText("Data matrix of all selected non excluded data points");
		pnExportSelections.add(cbImgSelNEx, "cell 0 3");
		cbImgSelNEx.setSelected(true);
		
		cbImgSel = new JCheckBox("Img select.");
		cbImgSel.setToolTipText("Data matrix of all selected data points");
		pnExportSelections.add(cbImgSel, "cell 0 4");
		cbImgSel.setSelected(true);
		
		cbImgEx = new JCheckBox("Img excl.");
		cbImgEx.setToolTipText("Data matrix of all excluded data points");
		pnExportSelections.add(cbImgEx, "cell 0 5");
		cbImgEx.setSelected(true);
		
		cbShapesSelNEx = new JCheckBox("Shapes: Select. non Excl.");
		cbShapesSelNEx.setToolTipText("Export all selection shapes in regards to exclusions (cut out data matrix)");
		pnExportSelections.add(cbShapesSelNEx, "cell 0 6");
		cbShapesSelNEx.setSelected(true);
		
		cbShapes = new JCheckBox("Shapes: No regards to exclusions");
		cbShapes.setToolTipText("Export all shapes with no regards to exclusions. All data points inside the shape are exported.");
		pnExportSelections.add(cbShapes, "cell 0 7");
		cbShapes.setSelected(true);

		cbShapesData = new JCheckBox("Shape data summary");
		cbShapesData.setToolTipText("Writes all data points that were used for statistics for each shape");
		cbShapesData.setSelected(true);
		pnExportSelections.add(cbShapesData, "cell 0 8");

		cbX = new JCheckBox("Image (x matrix)");
		cbX.setToolTipText("X coordinates");
		cbX.setSelected(true);
		pnExportSelections.add(cbX, "cell 0 9");

		cbY= new JCheckBox("Image (y matrix)");
		cbY.setToolTipText("y coordinates");
		cbY.setSelected(true);
		pnExportSelections.add(cbY, "cell 0 10");

		cbZ = new JCheckBox("Image (intensity matrix)");
		cbZ.setToolTipText("");
		cbZ.setSelected(true);
		pnExportSelections.add(cbZ, "cell 0 11");
	}
	
	
	/**
	 * txt or xlsx or clipboard
	 * @param currentMode
	 */
	public void setCurrentMode(MODE m) {
		currentMode = m;
		// show right panels:
		getPnCenter().removeAll();
		getPnCenter().add(m.equals(MODE.SELECTED_RECTS)? getPnExportSelections() : getPnMS());
		
		getPnXlsOptions().setVisible(m.equals(MODE.ALL));

		getPnCenter().revalidate();
	}
	
	/**
	 * open the dialog with imagelist
	 * @param imgList
	 */
	public static void startDialogWith(Vector<Image2D> imgList, Image2D img) {
		inst.setCurrentMode(MODE.ALL);
		inst.img=img;
		inst.imgList = imgList;
		inst.selections = null;
		inst.renewAllSettings();
		inst.setVisible(true); 
	}
	public static void startDialogWith(Image2D img) {
		inst.setCurrentMode(MODE.ALL);
		inst.img=img;
		inst.imgList = null;
		inst.selections = null;
		inst.renewAllSettings();
		inst.setVisible(true);
	}
	
	//##########################################################################################
	// exporting only selection rects data
	public static void startDialogWith(Image2D img, SettingsSelections selections) {
		inst.img=img;
		inst.imgList = null;
		inst.selections = selections;
		inst.setCurrentMode(MODE.SELECTED_RECTS);
		inst.renewAllSettings();
		inst.setVisible(true);
	}
	
	//##########################################################################################
	// 
	private void renewAllSettings() {
		if(currentMode.equals(MODE.ALL)) {
			SettingsImage2DDataExport settingsData = this.settings.getSetImage2DDataExport(); 
		// Set mode 
		settingsData.setPath(getTxtPath().getText());
		
		FileType type = (FileType)comboBox.getSelectedItem();
		settingsData.setFileFormat(type);
		settingsData.setWritingToClipboard(getCbToClipboard().isSelected());
		settingsData.setFilename(getTxtFileName().getText());
		
		// always one file if to clipboard
		settingsData.setExportsAllFiles(getRbAllFiles().isSelected() && !getCbToClipboard().isSelected());
		settingsData.setSavesAllFilesToOneXLS(getCbSaveAllFilesInOne().isSelected());
		 
		settingsData.setIsExportRaw(getCbRawData().isSelected());
		settingsData.setIsWriteTitleRow(getCbWriteTitleRow().isSelected());
		
		settingsData.setMode((ModeData) getComboDataFormat().getSelectedItem());
		
		settingsData.setSeparation(txtSep.getText());
		}
		else {
			SettingsImage2DDataSelectionsExport s = this.settings.getSetImage2DDataSelectionsExport();
			s.setPath(getTxtPath().getText());
			
			FileType type = (FileType)comboBox.getSelectedItem();
			s.setFileFormat(type);
			s.setWritingToClipboard(getCbToClipboard().isSelected());
			s.setFilename(getTxtFileName().getText());
			s.setSeparation(txtSep.getText());
			
			s.setAll(cbSummary.isSelected(), cbDef.isSelected(), cbArray.isSelected(), cbImgEx.isSelected(), cbImgSel.isSelected(), 
					cbImgSelNEx.isSelected(), cbShapes.isSelected(), cbShapesSelNEx.isSelected(), cbShapesData.isSelected(), 
					cbX.isSelected(), cbY.isSelected(), cbZ.isSelected());
		}
	}
	// all cb set
	public void setAllSettingsCb() {
		if(selections==null) {
			SettingsImage2DDataExport settingsData = this.settings.getSetImage2DDataExport(); 
	
			getRbAllFiles().setSelected(settingsData.isExportsAllFiles());
			// TODO select combo
			comboBox.setSelectedItem(settingsData.getFileFormat());
			//
			getCbToClipboard().setSelected(settingsData.isWritingToClipboard());
			
			getTxtFileName().setText(settingsData.getFilename());
			getCbSaveAllFilesInOne().setSelected(settingsData.isSavesAllFilesToOneXLS());
			
			getTxtPath().setText(settingsData.getPath().getPath());
	
			getComboDataFormat().setSelectedItem(settingsData.getMode());
	
			getCbWriteTitleRow().setSelected(settingsData.isWriteTitleRow());
			getCbRawData().setSelected(settingsData.isExportRaw());
			getTxtSep().setText(settingsData.getSeparation());
		}
		else {
			SettingsImage2DDataSelectionsExport s = this.settings.getSetImage2DDataSelectionsExport();
	
			getRbAllFiles().setSelected(s.isExportsAllFiles());
			// TODO select combo
			comboBox.setSelectedItem(s.getFileFormat());
			//
			getCbToClipboard().setSelected(s.isWritingToClipboard());
			getTxtFileName().setText(s.getFilename());
			getTxtPath().setText(s.getPath().getPath());
	
			getTxtSep().setText(s.getSeparation());
			
			// 
			getCbArray().setSelected(s.isArrays());
			getCbSummary().setSelected(s.isSummary());
			getCbDef().setSelected(s.isDefinitions());
			getCbImgEx().setSelected(s.isImgEx());
			getCbImgSel().setSelected(s.isImgSel());
			getCbImgSelNEx().setSelected(s.isImgSelNEx());
			getCbShapes().setSelected(s.isShapes());
			getCbShapesSelNEx().setSelected(s.isShapesSelNEx());
			cbShapesData.setSelected(s.isShapeData());
			cbX.setSelected(s.isX());
			cbY.setSelected(s.isY());
			cbZ.setSelected(s.isZ());
		}
	}
	
	
	// GETTERS AND SETTERS
	public JPanel getPnXlsOptions() {
		return pnXlsOptions;
	}
	public JLabel getLblSucceed() {
		return lblSucceed;
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
	public JLabel getLbSep() {
		return lbSep;
	}
	public JTextField getTxtSep() {
		return txtSep;
	}
	public JCheckBox getCbToClipboard() {
		return rbToClipboard;
	}
	public JComboBox getComboDataFormat() {
		return comboDataFormat;
	}
	public JPanel getPnExportSelections() {
		return pnExportSelections;
	}
	public JCheckBox getCbSummary() {
		return cbSummary;
	}
	public JCheckBox getCbDef() {
		return cbDef;
	}
	public JCheckBox getCbArray() {
		return cbArray;
	}
	public JCheckBox getCbImgSelNEx() {
		return cbImgSelNEx;
	}
	public JCheckBox getCbImgSel() {
		return cbImgSel;
	}
	public JCheckBox getCbImgEx() {
		return cbImgEx;
	}
	public JCheckBox getCbShapesSelNEx() {
		return cbShapesSelNEx;
	}
	public JCheckBox getCbShapes() {
		return cbShapes;
	}
	public JPanel getPnCenter() {
		return pnCenter;
	}
}
