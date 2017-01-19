package net.rs.lamsi.massimager.Frames.Dialogs;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.rs.lamsi.massimager.Frames.Window;
import net.rs.lamsi.massimager.Settings.SettingsDataSaver;
import net.rs.lamsi.massimager.Settings.SettingsHolder;
import net.rs.lamsi.utils.DialogLoggerUtil;
import net.rs.lamsi.utils.mywriterreader.XSSFExcelWriterReader;


public class DataSaverFrame extends JFrame {
	// MYSTUFF 
	// ExcelWriter 
	private DataSaverFrame thisFrame;
	private SettingsHolder settings;
	private XSSFExcelWriterReader excelWriter;
	private Window window;
	private int currentMode;
	// 
	private JPanel contentPane;
	private JTextField txtPath;
	private JTextField txtFileName;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JPanel pnXlsOptions;
	private final ButtonGroup buttonGroup_1 = new ButtonGroup();
	private JLabel lblSucceed;
	private JCheckBox cbTimeOnlyOnce;
	private JRadioButton rbXLS;
	private JRadioButton rbTXT;
	private JCheckBox cbSaveAllFilesInOne;
	private JRadioButton rbAllFiles;
	private JRadioButton rbSelectedFileOnly;
	private JCheckBox cbExTIC;
	private JCheckBox cbExSpectrum;
	private JCheckBox cbExEIC;
	private JCheckBox cbAllMZInSeperateFiles;
	private JCheckBox cbSelectedMZOnly;
	private JPanel pnTxtOptions;
	private JPanel pnXLSOpOES;
	private JPanel pnTXTOpOES;
	private JPanel pnMS;
	private JPanel pnOES;
	private final ButtonGroup buttonGroup_2 = new ButtonGroup();
	private final ButtonGroup buttonGroup_3 = new ButtonGroup();
	private JRadioButton rbOESAllFiles;
	private JRadioButton rbOESOnlySelectedFiles;
	private JCheckBox cbOESSaveAllInOneXls;
	private JRadioButton rbOESElementlineAsSheet;
	private JRadioButton rbOESScanAsSheet;
	private JCheckBox cbOESWriteTimeOnlyOnce;
	
	// TODO
	// Irgendwann das aufrufen: 
	//	runner.saveDataFile();

	/**
	 * Create the frame.
	 */
	public DataSaverFrame(Window wnd, SettingsHolder sett) {
		thisFrame = this;
		this.window = wnd;
		this.excelWriter = wnd.getExcelWriter();
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		// Settings
		this.settings = sett; 
		// 
		setBounds(100, 100, 404, 440);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		txtPath = new JTextField();
		txtPath.setBounds(10, 27, 266, 20);
		contentPane.add(txtPath);
		txtPath.setColumns(10);
		
		JLabel lblPath = new JLabel("Path");
		lblPath.setBounds(10, 9, 46, 14);
		contentPane.add(lblPath);
		
		JButton btnSelectPath = new JButton("Select path");
		btnSelectPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// open fileChooser 
				window.getFcDirectoriesChooser().showOpenDialog(thisFrame);
		    	File file = window.getFcDirectoriesChooser().getSelectedFile(); 
		        if(file!=null) { 
		        	getTxtPath().setText(file.getPath());
		        	settings.getSetDataSaver().setPath(file);
		        }
			}
		});
		btnSelectPath.setBounds(286, 26, 89, 23);
		contentPane.add(btnSelectPath);
		
		JLabel lblFilename = new JLabel("Filename:");
		lblFilename.setBounds(10, 61, 46, 14);
		contentPane.add(lblFilename);
		
		txtFileName = new JTextField();
		txtFileName.setColumns(10);
		txtFileName.setBounds(62, 58, 100, 20);
		contentPane.add(txtFileName);
		
		rbXLS = new JRadioButton(".xlsx");
		buttonGroup.add(rbXLS);
		rbXLS.setSelected(true);
		rbXLS.setBounds(168, 57, 109, 23);
		contentPane.add(rbXLS);
		
		rbTXT = new JRadioButton(".txt");
		buttonGroup.add(rbTXT);
		rbTXT.setBounds(168, 82, 109, 23);
		contentPane.add(rbTXT);
		
		JPanel Buttonsreihe = new JPanel();
		Buttonsreihe.setBounds(0, 358, 385, 33);
		contentPane.add(Buttonsreihe);
		
		
		JButton btnLoadSettings = new JButton("Load settings");
		btnLoadSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					settings.loadSettingsFromFile(thisFrame, settings.getSetDataSaver());
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
					settings.saveSettingsToFile(thisFrame, settings.getSetDataSaver());
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
					// Setup Progressbar
					ProgressDialog.getInst().setVisibleDialog(true);
					ProgressDialog.setProgress(0); 
					// erfolgreich oder nicht gespeichert
					if(window.saveDataFile(settings.getSetDataSaver(), excelWriter, currentMode))
						JOptionPane.showMessageDialog(window.getFrame(), "Save of xlsx succeed", "SUCCEED!", JOptionPane.CANCEL_OPTION);
					else 
						JOptionPane.showMessageDialog(window.getFrame(), "File (.xlsx) not saved", "ERROR", JOptionPane.ERROR_MESSAGE); 
				} catch (Exception e1) { 
					e1.printStackTrace();
					// TODO open Message popup
					JOptionPane.showMessageDialog(window.getFrame(), e1.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
				} finally {
					ProgressDialog.getInst().setVisibleDialog(false);
				}
			}
		});
		
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setBounds(0, 106, 385, 243);
		contentPane.add(panel);
		panel.setLayout(null);
		
		pnOES = new JPanel();
		pnOES.setBounds(10, 11, 365, 242);
		panel.add(pnOES);
		pnOES.setLayout(null);
		
		pnXLSOpOES = new JPanel();
		pnXLSOpOES.setBounds(0, 0, 365, 231);
		pnOES.add(pnXLSOpOES);
		pnXLSOpOES.setLayout(null);
		
		rbOESAllFiles = new JRadioButton("All loaded files");
		rbOESAllFiles.setSelected(true);
		buttonGroup_2.add(rbOESAllFiles);
		rbOESAllFiles.setBounds(6, 7, 109, 23);
		pnXLSOpOES.add(rbOESAllFiles);
		
		rbOESOnlySelectedFiles = new JRadioButton("Selected files only");
		buttonGroup_2.add(rbOESOnlySelectedFiles);
		rbOESOnlySelectedFiles.setBounds(6, 33, 111, 23);
		pnXLSOpOES.add(rbOESOnlySelectedFiles);
		
		cbOESSaveAllInOneXls = new JCheckBox("Save all to one .xls file");
		cbOESSaveAllInOneXls.setSelected(true);
		cbOESSaveAllInOneXls.setBounds(190, 7, 169, 23);
		pnXLSOpOES.add(cbOESSaveAllInOneXls);
		
		rbOESElementlineAsSheet = new JRadioButton("Elementline defines sheet");
		rbOESElementlineAsSheet.setSelected(true);
		buttonGroup_3.add(rbOESElementlineAsSheet);
		rbOESElementlineAsSheet.setToolTipText("One sheet per elementline with every scan.");
		rbOESElementlineAsSheet.setBounds(6, 90, 176, 23);
		pnXLSOpOES.add(rbOESElementlineAsSheet);
		
		rbOESScanAsSheet = new JRadioButton("Scan defines sheet");
		buttonGroup_3.add(rbOESScanAsSheet);
		rbOESScanAsSheet.setToolTipText("One sheet per scan with all elementlines.");
		rbOESScanAsSheet.setBounds(6, 116, 176, 23);
		pnXLSOpOES.add(rbOESScanAsSheet);
		
		cbOESWriteTimeOnlyOnce = new JCheckBox("Write time only once");
		cbOESWriteTimeOnlyOnce.setBounds(190, 90, 169, 23);
		pnXLSOpOES.add(cbOESWriteTimeOnlyOnce);
		
		pnTXTOpOES = new JPanel();
		pnTXTOpOES.setLayout(null);
		pnTXTOpOES.setBounds(0, 0, 365, 231);
		pnOES.add(pnTXTOpOES);
		
		pnMS = new JPanel();
		pnMS.setBounds(10, 11, 365, 242);
		panel.add(pnMS);
		pnMS.setLayout(null);
		
		pnXlsOptions = new JPanel();
		pnXlsOptions.setBounds(0, 0, 365, 242);
		pnMS.add(pnXlsOptions);
		pnXlsOptions.setLayout(null);
		
		cbTimeOnlyOnce = new JCheckBox("Write time only once");
		cbTimeOnlyOnce.setToolTipText("Writes the time only once followed by inensities for all scans");
		cbTimeOnlyOnce.setBounds(201, 169, 154, 23);
		pnXlsOptions.add(cbTimeOnlyOnce);
		
		cbAllMZInSeperateFiles = new JCheckBox("All m/z in seperate .xls");
		cbAllMZInSeperateFiles.setToolTipText("Generates one xls-file per m/z");
		cbAllMZInSeperateFiles.setBounds(201, 116, 154, 23);
		pnXlsOptions.add(cbAllMZInSeperateFiles);
		
		cbExTIC = new JCheckBox("Export TIC");
		cbExTIC.setSelected(true);
		cbExTIC.setToolTipText("Export TIC data");
		cbExTIC.setBounds(6, 64, 178, 23);
		pnXlsOptions.add(cbExTIC);
		
		cbExEIC = new JCheckBox("Export EIC");
		cbExEIC.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) { 
				JCheckBox cb = (JCheckBox) e.getSource();
				if(cb.isSelected()) {
					getCbAllMZInSeperateFiles().setEnabled(true); 
					getCbTimeOnlyOnce().setEnabled(true);
				}
				else {
					getCbAllMZInSeperateFiles().setEnabled(false);
					getCbTimeOnlyOnce().setEnabled(false);
				}
			}
		});
		cbExEIC.setSelected(true);
		cbExEIC.setToolTipText("Export extracted ion current data");
		cbExEIC.setBounds(6, 116, 178, 23);
		pnXlsOptions.add(cbExEIC);
		
		cbExSpectrum = new JCheckBox("Export spectrum at RT");
		cbExSpectrum.setSelected(true);
		cbExSpectrum.setToolTipText("Export spectrum at selected time");
		cbExSpectrum.setBounds(6, 90, 178, 23);
		pnXlsOptions.add(cbExSpectrum);
		
		rbAllFiles = new JRadioButton("All loaded files");
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
		rbAllFiles.setBounds(6, 0, 178, 23);
		pnXlsOptions.add(rbAllFiles);
		
		cbSaveAllFilesInOne = new JCheckBox("Save all to one .xls file");
		cbSaveAllFilesInOne.setSelected(true);
		cbSaveAllFilesInOne.setToolTipText("Save data from all files in one xls-file or in seperate ones");
		cbSaveAllFilesInOne.setBounds(201, 0, 154, 23);
		pnXlsOptions.add(cbSaveAllFilesInOne);
		
		rbSelectedFileOnly = new JRadioButton("Selected file only");
		buttonGroup_1.add(rbSelectedFileOnly);
		rbSelectedFileOnly.setToolTipText("Export only the selected file");
		rbSelectedFileOnly.setBounds(6, 26, 178, 23);
		pnXlsOptions.add(rbSelectedFileOnly);
		
		lblSucceed = new JLabel("Succeed");
		lblSucceed.setVisible(false);
		lblSucceed.setForeground(new Color(0, 128, 0));
		lblSucceed.setBounds(309, 228, 46, 14);
		pnXlsOptions.add(lblSucceed);
		
		cbSelectedMZOnly = new JCheckBox("Only selected m/z");
		cbSelectedMZOnly.setBounds(201, 143, 142, 23);
		pnXlsOptions.add(cbSelectedMZOnly);
		
		pnTxtOptions = new JPanel();
		pnTxtOptions.setVisible(false);
		pnTxtOptions.setBounds(0, 0, 365, 242);
		pnMS.add(pnTxtOptions);
		pnTxtOptions.setLayout(null);
	}
	
	
	// set Mode to OES or MS to specify datasaver
	public void setCurrentMode(int currentMode) {
		this.currentMode = currentMode;
		// show right panels:
		boolean isMS = (currentMode == Window.MODE_MS);
		getPnMS().setVisible(isMS);
		getPnOES().setVisible(!isMS);
	}
	
	
	// 
	private void renewAllSettings() {
		SettingsDataSaver settingsData = this.settings.getSetDataSaver();
		// Set mode
		settingsData.setCurrentMode(currentMode);
		//
		settingsData.setAllMZInSeperateFiles(getCbAllMZInSeperateFiles().isSelected());
		settingsData.setExportEIC(getCbExEIC().isSelected());
		settingsData.setExportTIC(getCbExTIC().isSelected());
		settingsData.setExportSpectrum(getCbExSpectrum().isSelected());
		
		settingsData.setExportsAllFiles(getRbAllFiles().isSelected());
		settingsData.setFileFormat((getRbXLS().isSelected() ? SettingsDataSaver.FORMAT_XLS : SettingsDataSaver.FORMAT_TXT));
		settingsData.setFilename(getTxtFileName().getText());
		settingsData.setSavesAllFilesToOneXLS(getCbSaveAllFilesInOne().isSelected());
		
		settingsData.setSelectedMZOnly(getCbSelectedMZOnly().isSelected()); 
		settingsData.setWriteTimeOnlyOnce(getCbTimeOnlyOnce().isSelected()); 
		// OES
		if(currentMode == Window.MODE_OES) {
			settingsData.setExportsAllFiles(getRbOESAllFiles().isSelected());
			settingsData.setSavesAllFilesToOneXLS(getCbOESSaveAllInOneXls().isSelected());
			settingsData.setWriteTimeOnlyOnce(getCbOESWriteTimeOnlyOnce().isSelected()); 
			// OES spezifisch:
			settingsData.setUsesElementLineAsSheet(getRbOESElementlineAsSheet().isSelected());
		}
	}
	// all cb set
	public void setAllSettingsCb() {
		SettingsDataSaver settingsData = this.settings.getSetDataSaver();
		// set current mode
		setCurrentMode(settingsData.getCurrentMode());
		//
		getCbAllMZInSeperateFiles().setSelected(settingsData.isAllMZInSeperateFiles());
		getCbExEIC().setSelected(settingsData.isExportEIC());
		getCbExTIC().setSelected(settingsData.isExportTIC());
		getCbExSpectrum().setSelected(settingsData.isExportSpectrum());

		getRbAllFiles().setSelected(settingsData.isExportsAllFiles());
		getRbXLS().setSelected(settingsData.getFileFormat()==SettingsDataSaver.FORMAT_XLS);
		getRbTXT().setSelected(settingsData.getFileFormat()==SettingsDataSaver.FORMAT_TXT);
		getTxtFileName().setText(settingsData.getFilename());
		getCbSaveAllFilesInOne().setSelected(settingsData.isSavesAllFilesToOneXLS());
		
		getTxtPath().setText(settingsData.getPath().getPath());

		getCbSelectedMZOnly().setSelected(settingsData.isSelectedMZOnly());
		getCbTimeOnlyOnce().setSelected(settingsData.isWriteTimeOnlyOnce()); 
		// OES
		if(currentMode == Window.MODE_OES) {
			getRbOESAllFiles().setSelected(settingsData.isExportsAllFiles());
			getCbOESSaveAllInOneXls().setSelected(settingsData.isSavesAllFilesToOneXLS());
			getCbOESWriteTimeOnlyOnce().setSelected(settingsData.isWriteTimeOnlyOnce());  
			// OES spezifisch:
			getRbOESElementlineAsSheet().setSelected(settingsData.isUsesElementLineAsSheet()); 
			getRbOESScanAsSheet().setSelected(!settingsData.isUsesElementLineAsSheet());
		}
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
	public JRadioButton getRbXLS() {
		return rbXLS;
	}
	public JRadioButton getRbTXT() {
		return rbTXT;
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
	public JCheckBox getCbExTIC() {
		return cbExTIC;
	}
	public JCheckBox getCbExSpectrum() {
		return cbExSpectrum;
	}
	public JCheckBox getCbExEIC() {
		return cbExEIC;
	}
	public JCheckBox getCbAllMZInSeperateFiles() {
		return cbAllMZInSeperateFiles;
	}
	public JCheckBox getCbSelectedMZOnly() {
		return cbSelectedMZOnly;
	} 
	public JPanel getPnMS() {
		return pnMS;
	}
	public JPanel getPnOES() {
		return pnOES;
	}
	public JRadioButton getRbOESAllFiles() {
		return rbOESAllFiles;
	}
	public JRadioButton getRbOESOnlySelectedFiles() {
		return rbOESOnlySelectedFiles;
	}
	public JCheckBox getCbOESSaveAllInOneXls() {
		return cbOESSaveAllInOneXls;
	}
	public JRadioButton getRbOESElementlineAsSheet() {
		return rbOESElementlineAsSheet;
	}
	public JRadioButton getRbOESScanAsSheet() {
		return rbOESScanAsSheet;
	}
	public JCheckBox getCbOESWriteTimeOnlyOnce() {
		return cbOESWriteTimeOnlyOnce;
	}
}
