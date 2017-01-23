package net.rs.lamsi.massimager.Frames;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.massimager.Heatmap.Heatmap;
import net.rs.lamsi.massimager.MyFileChooser.FileTypeFilter;
import net.rs.lamsi.massimager.MyMZ.MZChromatogram;
import net.rs.lamsi.massimager.MyOES.OESElementLine;
import net.rs.lamsi.massimager.MyOES.OESFile;
import net.rs.lamsi.massimager.MyOES.OESFileReaderWriter;
import net.rs.lamsi.massimager.MyOES.OESScan;
import net.rs.lamsi.massimager.Settings.SettingsDataSaver;
import net.rs.lamsi.massimager.Settings.SettingsGeneralImage;
import net.rs.lamsi.utils.mywriterreader.XSSFExcelWriterReader;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.chart.ChartPanel;

public class OESPanel extends JPanel {
	// Mein Stuff
	private Window window;
	private OESFileReaderWriter readerOES;
	// Objekte
	private Vector<OESFile> listOESFiles;
	private int selectedOESFile, selectedElementLine;
	
	private Image2D currentImage2D = null;
	
	
	// Generiert
	private JPanel pnChartViewImage;
	private JTextField txtVelocity;
	private JTextField txtSpotsize;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JRadioButton rbAllFiles;
	private JList listScans;
	private JPanel pnLines;
	private JPanel pnLinesHidden;
	private JPanel pnScans;
	private JPanel pnScansHidden;
	private JList listElementLines;
	private JPanel tabLine;
	private JPanel tabImage;
	private JPanel pnChartViewLine;
	private JButton btnSendImageToImageEditor;

	/**
	 * Create the panel.
	 */
	public OESPanel(Window wnd) {
		this.window = wnd;
		setLayout(new BorderLayout(0, 0));
		
		JPanel Images = new JPanel();
		add(Images);
		Images.setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedOES = new JTabbedPane(JTabbedPane.TOP);
		Images.add(tabbedOES, BorderLayout.CENTER); 
		
		tabLine = new JPanel();
		tabbedOES.addTab("Line", null, tabLine, null);
		tabLine.setLayout(new BorderLayout(0, 0));
		
		JPanel pnSettingsLine = new JPanel();
		tabLine.add(pnSettingsLine, BorderLayout.WEST);
		GroupLayout gl_pnSettingsLine = new GroupLayout(pnSettingsLine);
		gl_pnSettingsLine.setHorizontalGroup(
			gl_pnSettingsLine.createParallelGroup(Alignment.LEADING)
				.addGap(0, 0, Short.MAX_VALUE)
		);
		gl_pnSettingsLine.setVerticalGroup(
			gl_pnSettingsLine.createParallelGroup(Alignment.LEADING)
				.addGap(0, 309, Short.MAX_VALUE)
		);
		pnSettingsLine.setLayout(gl_pnSettingsLine);
		
		pnChartViewLine = new JPanel();
		tabLine.add(pnChartViewLine, BorderLayout.CENTER);
		pnChartViewLine.setLayout(new BorderLayout(0, 0));
		
		tabImage = new JPanel();
		tabbedOES.addTab("Image", null, tabImage, null);
		tabImage.setLayout(new BorderLayout(0, 0));
		
		JPanel pnSettingsImage = new JPanel();
		tabImage.add(pnSettingsImage, BorderLayout.WEST);
		
		JLabel lblNewLabel = new JLabel("v = ");
		
		txtVelocity = new JTextField();
		txtVelocity.setToolTipText("x-velocity [\u00B5m/s]");
		txtVelocity.setText("50");
		txtVelocity.setColumns(10);
		
		JLabel lblD = new JLabel("d = ");
		
		txtSpotsize = new JTextField();
		txtSpotsize.setToolTipText("spot size [\u00B5m]");
		txtSpotsize.setText("50");
		txtSpotsize.setColumns(10);
		
		rbAllFiles = new JRadioButton("all files");
		rbAllFiles.setToolTipText("all files in one image");
		rbAllFiles.setSelected(true);
		buttonGroup.add(rbAllFiles);
		
		JRadioButton rdbtnSelectedFile = new JRadioButton("selected files");
		buttonGroup.add(rdbtnSelectedFile);
		
		JButton btnApply = new JButton("Apply");
		btnApply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// apply new Settings
				applyOESImageSettings();
			}
		});
		btnApply.setToolTipText("apply settings and renew image");
		
		btnSendImageToImageEditor = new JButton("Send image");
		btnSendImageToImageEditor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//TODO
				if(currentImage2D!=null && selectedElementLine!=-1) {
					window.sendImage2DToImageEditor(currentImage2D, "OES");
				}
			}
		});
		btnSendImageToImageEditor.setToolTipText("Send image to ImageEditor");
		GroupLayout gl_pnSettingsImage = new GroupLayout(pnSettingsImage);
		gl_pnSettingsImage.setHorizontalGroup(
			gl_pnSettingsImage.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnSettingsImage.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_pnSettingsImage.createParallelGroup(Alignment.LEADING)
						.addComponent(btnApply, GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE)
						.addGroup(gl_pnSettingsImage.createParallelGroup(Alignment.LEADING, false)
							.addComponent(rdbtnSelectedFile, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addGroup(gl_pnSettingsImage.createSequentialGroup()
								.addComponent(lblNewLabel)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(txtVelocity, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE))
							.addGroup(gl_pnSettingsImage.createSequentialGroup()
								.addComponent(lblD, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
								.addGap(4)
								.addComponent(txtSpotsize, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE))
							.addComponent(rbAllFiles, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addComponent(btnSendImageToImageEditor))
					.addContainerGap())
		);
		gl_pnSettingsImage.setVerticalGroup(
			gl_pnSettingsImage.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnSettingsImage.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_pnSettingsImage.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel)
						.addComponent(txtVelocity, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_pnSettingsImage.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_pnSettingsImage.createSequentialGroup()
							.addGap(3)
							.addComponent(lblD))
						.addComponent(txtSpotsize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(rbAllFiles)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(rdbtnSelectedFile)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnApply)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnSendImageToImageEditor)
					.addContainerGap(269, Short.MAX_VALUE))
		);
		pnSettingsImage.setLayout(gl_pnSettingsImage);
		
		pnChartViewImage = new JPanel();
		tabImage.add(pnChartViewImage, BorderLayout.CENTER);
		pnChartViewImage.setLayout(new BorderLayout(0, 0));
		
		JPanel eastScansAndLines = new JPanel();
		add(eastScansAndLines, BorderLayout.EAST);
		eastScansAndLines.setLayout(new BorderLayout(0, 0));
		
		Component horizontalStrut = Box.createHorizontalStrut(5);
		eastScansAndLines.add(horizontalStrut, BorderLayout.CENTER);
		
		JPanel Lines1 = new JPanel();
		eastScansAndLines.add(Lines1, BorderLayout.WEST);
		Lines1.setLayout(new BorderLayout(0, 0));
		
		pnLines = new JPanel();
		Lines1.add(pnLines, BorderLayout.CENTER);
		pnLines.setLayout(new BorderLayout(0, 0));
		
		JPanel pnHeaderLines = new JPanel();
		pnLines.add(pnHeaderLines, BorderLayout.NORTH);
		pnHeaderLines.setLayout(new BorderLayout(0, 0));
		
		JCheckBox cbHideListLines = new JCheckBox("");
		cbHideListLines.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showListLines(false);
			}
		});
		cbHideListLines.setSelected(true);
		pnHeaderLines.add(cbHideListLines, BorderLayout.WEST);
		
		JLabel lblNewLabel_1 = new JLabel("Element lines");
		pnHeaderLines.add(lblNewLabel_1);
		
		JPanel centerLines = new JPanel();
		pnLines.add(centerLines, BorderLayout.CENTER);
		centerLines.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane_1 = new JScrollPane();
		centerLines.add(scrollPane_1, BorderLayout.CENTER);

		DefaultListModel modelLines = new DefaultListModel();
		listElementLines = new JList(modelLines);
		listElementLines.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) { 
        		JList lsm = (JList)e.getSource();
        		// update all
        		if(e.getValueIsAdjusting() == false){ 
        			setSelectedElementLine(lsm.getSelectedIndex()); 
        			renewCurrentView();
        		}
			}
		});
		scrollPane_1.setViewportView(listElementLines);
		
		pnLinesHidden = new JPanel();
		pnLinesHidden.setVisible(false);
		Lines1.add(pnLinesHidden, BorderLayout.WEST);
		pnLinesHidden.setLayout(new BorderLayout(0, 0));
		
		JCheckBox cbShowListLines = new JCheckBox("");
		cbShowListLines.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showListLines(true);
			}
		});
		pnLinesHidden.add(cbShowListLines, BorderLayout.NORTH);
		
		JPanel Scans1 = new JPanel();
		eastScansAndLines.add(Scans1, BorderLayout.EAST);
		Scans1.setLayout(new BorderLayout(0, 0));
		
		pnScans = new JPanel();
		Scans1.add(pnScans, BorderLayout.CENTER);
		pnScans.setLayout(new BorderLayout(0, 0));
		
		JPanel HeaderScans = new JPanel();
		pnScans.add(HeaderScans, BorderLayout.NORTH);
		
		JCheckBox cbHideListScans = new JCheckBox("");
		cbHideListScans.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showListScans(false);
			}
		});
		cbHideListScans.setSelected(true);
		
		JLabel lblScans = new JLabel("Scans");
		HeaderScans.setLayout(new BorderLayout(0, 0));
		HeaderScans.add(cbHideListScans, BorderLayout.WEST);
		HeaderScans.add(lblScans);
		
		JPanel centerScans = new JPanel();
		pnScans.add(centerScans, BorderLayout.CENTER);
		centerScans.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		centerScans.add(panel, BorderLayout.NORTH);
		
		JButton btnSelectAllScans = new JButton("Select all");
		panel.add(btnSelectAllScans);
		
		JButton btnDeselectAllScans = new JButton("Deselect all");
		panel.add(btnDeselectAllScans);
		
		JScrollPane scrollPane = new JScrollPane();
		centerScans.add(scrollPane, BorderLayout.CENTER);

		DefaultListModel modelScans = new DefaultListModel();
		listScans = new JList(modelScans);
		listScans.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
        		JList lsm = (JList)e.getSource();
        		// update all
        		if(e.getValueIsAdjusting() == false){ 
        			if(getTabLine().isShowing()){ 
	        			renewCurrentView();
        			}
        		}
			}
		});
		scrollPane.setViewportView(listScans);
		
		pnScansHidden = new JPanel();
		pnScansHidden.setVisible(false);
		Scans1.add(pnScansHidden, BorderLayout.WEST);
		pnScansHidden.setLayout(new BorderLayout(0, 0));
		
		JCheckBox cbShowListScans = new JCheckBox("");
		cbShowListScans.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				showListScans(true);
			}
		});
		pnScansHidden.add(cbShowListScans, BorderLayout.NORTH);
		
		
		initOESPanel();
	}

	




	//'##################################################################################
	// MY STUFF
	// Init at Start
	private void initOESPanel() {
		// TODO Init all at start
		readerOES = new OESFileReaderWriter();
		listOESFiles = new Vector<OESFile>();
		
	}
	// Aktiviert diesen Mode
	// Alle Files der File Liste hinzufügen die vorher schon leer gemacht wurde
	public void setAsCurrentMode(JList listFiles) { 
		// Alle Files wieder rein tun
		for(int i=0; i<listOESFiles.size(); i++) {
			((DefaultListModel)listFiles.getModel()).addElement(listOESFiles.get(i).getFile());
		}
		listFiles.repaint();
	}
	// Show or hide list at East
	protected void showListScans(boolean b) {
		getPnScans().setVisible(b);
		getPnScansHidden().setVisible(!b);
	}
	protected void showListLines(boolean b) {
		getPnLines().setVisible(b);
		getPnLinesHidden().setVisible(!b);
	}
	
	
	// Set Selected Index of:
	// Selected File
	public void setSelectedOESFile(int i) {
		selectedOESFile = i;
		selectedElementLine = -1;
		// ListModels 
		DefaultListModel dlmodelLines = ((DefaultListModel)getListElementLines().getModel()); 
		// Clear ElementList first
		dlmodelLines.clear();
		// OES File at Index i
		if(i>=0 && i<listOESFiles.size()) {
			OESFile file = listOESFiles.get(i);
			// List All ElementLines
			if(file!=null) {
				for(int el = 0; el<file.size(); el++) {
					dlmodelLines.addElement(file.get(el).getNameForList()); 
				}
				// List all Scans of Selected ElementLine
			}
		}
	}
	
	public void setSelectedElementLine(int i) {
		selectedElementLine = i;
		// Models for lists
		DefaultListModel dlmodelLines = ((DefaultListModel)getListElementLines().getModel()); 
		DefaultListModel dlmodelScans = ((DefaultListModel)getListScans().getModel()); 
		// Clear ScanList first
		dlmodelScans.clear();
		// List all Scans of selected ElementLine
		if(i>=0 && i<dlmodelLines.getSize()) {
			OESElementLine eline = listOESFiles.get(selectedOESFile).get(i);
			// List All ElementLines
			if(eline!=null) {
				for(int s = 0; s<eline.getListScan().size(); s++) {
					OESScan scan = eline.getListScan().get(s);
					dlmodelScans.addElement(scan.getName()+" ("+scan.getDate()+")"); 
				}
				// List all Scans of Selected ElementLine
			}
		} 
	}

	

	//###############################################################################
    // INPUT AND OUTPUT 
	// opens a fcOpen
    public void loadFiles() { 
    	// load file  
    	try {
	    	File[] files = window.getFilesFromFileChooser(window.getFcOpenOES());
			if(files.length>0) { 
				// All files in fileList
				for(File f : files) {
		            // Load txt File
					loadOESTxtFile(f);
				} 
	        }
    	}catch(Exception ex) {
    		ex.printStackTrace();
    	}
    }
    
    // Load Single File and add to listOESFiles
    public void loadOESTxtFile(File file) {
    	try {
			// TODO Auto-generated method stub
			// load mzXML with reader
	        if(FileTypeFilter.getExtensionFromFile(file).equalsIgnoreCase("txt")) {
		    	OESFile oesFile = readerOES.generateElementLineListFromTxtFile(file);
		    	if(oesFile != null) {
		    		// List file in fileSpectrumList for Spectrum Information
		    		listOESFiles.add(oesFile);
		    		// List File in Files List
					((DefaultListModel)window.getListFiles().getModel()).addElement(file); 
		    	} 
	        }
    	}catch(Exception ex) {
    		ex.printStackTrace();
    	}
	}
    
    //##################
    // DATA OUTPUT - Save Data 
	public boolean saveDataFile(SettingsDataSaver setds, XSSFExcelWriterReader excelWriter) throws Exception {
		// xls or txt
		if(setds.getFileFormat()==SettingsDataSaver.FORMAT_XLS) {
			// XLSX speichern:
			return writeDataToXLSXFile(setds, excelWriter);
		}
		else {
			// txt speichern
			
		}
		return false;
	}
	
	// Writes xlsx Data to files
	private boolean writeDataToXLSXFile(SettingsDataSaver setds, XSSFExcelWriterReader excelWriter) throws Exception { 
		// All FIles? or only selected?
		Vector<OESFile> filesToWrite;
		if(setds.isExportsAllFiles()) 
			filesToWrite = listOESFiles;
		else {
			// nur ausgewählte 
			int[] selectedi = window.getListFiles().getSelectedIndices();
			filesToWrite = new Vector<OESFile>();
			for(int i=0; i<selectedi.length; i++) {
				int currentIndex = selectedi[i];
				filesToWrite.add(listOESFiles.get(currentIndex));
			}
		}
		// WB to work with: 
	    // All to One:
		if(setds.isSavesAllFilesToOneXLS()) { 
			// neues WB und file
			XSSFWorkbook cwb = new XSSFWorkbook();
			File cfile = new File(setds.getPath(), setds.getFilename()+".xlsx");
			boolean retVal = false;
			// Alle  files die geschrieben werden soll durchgehen:
			for(int i=0; i<filesToWrite.size(); i++) {
				OESFile oesf = filesToWrite.get(i); 
				// Write Data to cwb on file cfile
				retVal = readerOES.writeOESFileToXLSXFile(setds, excelWriter, oesf, cwb);
				
				// Save all wbs 
				if(retVal==true) // TODO save wb to boolean
					retVal = excelWriter.saveWbToFile(cfile, cwb);
			} 
			return retVal;
		}
		else {
			// alle FIles in eigene xlsx 
			// Neue WBS und Files
		    XSSFWorkbook[] listWB = new XSSFWorkbook[filesToWrite.size()];
		    File[] listWBFiles = new File[filesToWrite.size()];  
		    boolean[] retVal = new boolean[filesToWrite.size()]; 
		    // Alle  files die geschrieben werden soll durchgehen:
			for(int i=0; i<filesToWrite.size(); i++) {
				OESFile oesf = filesToWrite.get(i);
				// NEues WB und File falls jedes in eigenes  
				listWB[i] = new XSSFWorkbook();
				listWBFiles[i] = new File(setds.getPath(), setds.getFilename()+"-"+oesf.getFile().getName()+".xlsx");
				retVal[i] = false; 
				// Write Data to cwb on file cfile
				retVal[i] = readerOES.writeOESFileToXLSXFile(setds, excelWriter, oesf, listWB[i]); 
				
				// Save all wbs 
				if(retVal[i]==true)
					retVal[i] = excelWriter.saveWbToFile(listWBFiles[i], listWB[i]);
			} 
			// nur true wenn ale retVal true
			for(boolean b : retVal) 
				if(!b) return false;
			// Ansonsten ist alles abgespeichert
			return true;
		} 
	}

	//##################################################################################
	// PLOT THINGS to Screen

	protected void renewCurrentView() {
		// TODO Auto-generated method stub
		if(getTabImage().isShowing()) {
			applyOESImageSettings();
		}
		else if(getTabLine().isShowing()) {
			renewOESScanLine();
		}
	}

    // OES Image With Option
	protected void renewOESScanLine() {
		if(selectedOESFile!=-1 && selectedElementLine!=-1 && listScans.getSelectedIndex()!=-1) {
			// Get selected scan
			int scanindex = listScans.getSelectedIndex();
			OESScan scan = listOESFiles.get(selectedOESFile).get(selectedElementLine).getListScan().get(scanindex);
			// MZChrom erstellen
			MZChromatogram chrom = new MZChromatogram(scan.getName());
			
			for(int i=0; i<scan.getTime().size(); i++) {
				chrom.add(scan.getTime().get(i), scan.getCenter().get(i));
			}
			// pass options to reader 
			// add chart
			ChartPanel myChart = new ChartPanel(chrom.getChromChart(scan.getName(), "time", "intensity")); 
			myChart.setMouseWheelEnabled(true); 
			// remove all
			JPanel pnChartView = getPnChartViewLine();
			pnChartView.removeAll();
			// Add Panel
			pnChartView.add(myChart,BorderLayout.CENTER);
			pnChartView.validate();  
		}
	}

	// Type settings in SettingsOESImage
	protected void applyOESImageSettings() {  
		try {
			float velocity = Float.valueOf(getTxtVelocity().getText());
			float spotsize = Float.valueOf(getTxtSpotsize().getText());
			boolean allFiles = getRbAllFiles().isSelected();
			SettingsGeneralImage sett = getWindow().getSettings().getSetGeneralImage();
			sett.setAllFiles(allFiles);
			sett.setSpotsize(spotsize);
			sett.setVelocity(velocity);
			if(selectedOESFile!=-1 && selectedElementLine!=-1) {
				sett.setTitle(listOESFiles.get(selectedOESFile).get(selectedElementLine).getName());
				sett.setRAWFilepath(listOESFiles.get(selectedOESFile).getFile().getPath());
			}
			// RENEW Image
			renewOESImageView(sett);
		} catch (NumberFormatException e) { 
			//
			e.printStackTrace();
			// Dialog
			JOptionPane.showMessageDialog(window.getFrame(), "Wrong input (no valid value?) "+e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE); 
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
    // OES Image With Option
	protected void renewOESImageView(SettingsGeneralImage oessettings) {
		if(selectedOESFile!=-1 && selectedElementLine!=-1) {
			// TODO bisher immer all Scans aber noch allscans raus machen
			try {
				Image2D img = Image2D.generateImage2DFromOES(window.getSettings().getSetPaintScale(), oessettings, listOESFiles.get(selectedOESFile).get(selectedElementLine));
				/*
				// Alles Scans zu einem mzChrom Array:
				Vector<OESScan> scans = listOESFiles.get(selectedOESFile).get(selectedElementLine).getListScan();
				int countScans = scans.size();
				
				MZChromatogram[] mzChrom = new MZChromatogram[countScans];
				for(int i=0; i<countScans; i++) {
					mzChrom[i] = scans.get(i).getMZChrom(); 
				}
				// add chart
				Heatmap heat = window.getHeatFactory().generateHeatmapDiscontinous(window.getSettings().getSetPaintScale(), "OESI", mzChrom, oessettings);
		        */
				Heatmap heat = window.getHeatFactory().generateHeatmap(img);
		        ChartPanel myChart = heat.getChartPanel(); 
		        myChart.setMouseWheelEnabled(true); 
		        
				// remove all
		        JPanel pnChartView = getPnChartViewImage();
				pnChartView.removeAll();
				// Add Panel
		        pnChartView.add(myChart,BorderLayout.CENTER);
		        pnChartView.validate(); 
		        //
		        currentImage2D = img;
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	//##################################################################################
	// GETTER SETTER
	public JPanel getPnChartViewImage() {
		return pnChartViewImage;
	}
	public JTextField getTxtVelocity() {
		return txtVelocity;
	}
	public JTextField getTxtSpotsize() {
		return txtSpotsize;
	}
	public JRadioButton getRbAllFiles() {
		return rbAllFiles;
	}
	public JList getListScans() {
		return listScans;
	}
	public JPanel getPnLines() {
		return pnLines;
	}
	public JPanel getPnLinesHidden() {
		return pnLinesHidden;
	}
	public JPanel getPnScans() {
		return pnScans;
	}
	public JPanel getPnScansHidden() {
		return pnScansHidden;
	} 
	public JList getListElementLines() {
		return listElementLines;
	} 
	public Window getWindow() {
		return window;
	} 
	public void setWindow(Window window) {
		this.window = window;
	} 
	public JPanel getTabLine() {
		return tabLine;
	}
	public JPanel getTabImage() {
		return tabImage;
	}
	public JPanel getPnChartViewLine() {
		return pnChartViewLine;
	} 
	public Vector<OESFile> getListOESFiles() {
		return listOESFiles;
	} 
	public void setListOESFiles(Vector<OESFile> listOESFiles) {
		this.listOESFiles = listOESFiles;
	}
	public JButton getBtnSendImageToImageEditor() {
		return btnSendImageToImageEditor;
	}
}
