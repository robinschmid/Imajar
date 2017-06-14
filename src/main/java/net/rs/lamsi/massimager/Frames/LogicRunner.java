package net.rs.lamsi.massimager.Frames;


import java.awt.geom.Rectangle2D;
import java.io.File;
import java.text.NumberFormat;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.settings.SettingsDataSaver;
import net.rs.lamsi.general.settings.SettingsHolder;
import net.rs.lamsi.general.settings.image.sub.SettingsGeneralImage;
import net.rs.lamsi.general.settings.image.sub.SettingsImageContinousSplit;
import net.rs.lamsi.general.settings.image.sub.SettingsMSImage;
import net.rs.lamsi.massimager.MyMZ.ChromGenType;
import net.rs.lamsi.massimager.MyMZ.MZChromatogram;
import net.rs.lamsi.massimager.MyMZ.MZDataFactory;
import net.rs.lamsi.massimager.MyMZ.MZIon;
import net.rs.lamsi.massimager.MyMZ.oldmzjavasave.MZXMLReaderList;
import net.rs.lamsi.massimager.MyMZ.preprocessing.filtering.exceptions.FilteringFailedException;
import net.rs.lamsi.massimager.MyMZ.preprocessing.filtering.spectra.MZSpectrumCombineFilter;
import net.rs.lamsi.massimager.mzmine.MZMineLogicsConnector;
import net.rs.lamsi.utils.myfilechooser.exceptions.NoFileSelectedException;
import net.rs.lamsi.utils.mywriterreader.XSSFExcelWriterReader;
import net.rs.lamsi.utils.threads.ProgressUpdateTask;
import net.rs.lamsi.utils.useful.dialogs.ProgressDialog;
import net.sf.mzmine.datamodel.PeakList;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.datamodel.Scan;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.chart.ChartPanel;

// abgeleitet von JFrame -> Oeffnet das Fenster
public class LogicRunner { 
	//###############################################################################
	// static
	public static final int WIDTH = 800, HEIGHT = 600;
	public static final int PN_PARAMETERS_WIDTH = 200, PN_PARAMETERS_HEIGHT = HEIGHT;
	public static final int COMP_WIDTH = PN_PARAMETERS_WIDTH/2-10, COMP_HEIGHT=25, X = 5, Y=5;
	public static final int CHART_WIDTH = WIDTH-COMP_WIDTH-X-10, CHART_HEIGHT = HEIGHT-10;
	public static final int CHART_X = PN_PARAMETERS_WIDTH+X+5, CHART_Y = 5;
	
	public static final int VIEW_COUNT = 5, MODE_COUNT = 2;
	public static final int VIEW_TIC=0, VIEW_MZCHROM = VIEW_TIC+1, VIEW_SPEC = VIEW_MZCHROM+1, VIEW_MSI_DISCON = VIEW_SPEC+1, VIEW_MSI_CON = VIEW_MSI_DISCON+1,
						VIEW_VS_MZCHROM = VIEW_MSI_CON+1, VIEW_VS_SPEC = VIEW_VS_MZCHROM+1;
	
	// Export
	public static final int XLS_COLUMNWIDTH = 50;
	// Window
	Window window;
	//###############################################################################
	// Objekte
	MZXMLReaderList reader;
	// Jedes File in ein neues Spectrum
	Vector<RawDataFile> listSpecFiles = new Vector<RawDataFile>(); 
	// PeakLists 
	Vector<PeakList> listPeakList = new Vector<PeakList>(); 

	// Variablen 
    Vector<MZIon> listMZIon = new Vector<MZIon>();
	//
	String filepath = "data/ASS-sauberes-spect.mzXML"; 

	//###############################################################################
    // Variablen
	private int currentView = 0;

	private double currentRT = 0; 
    // Alle MZCHrom Daten als Liste speichern
    private Vector<MZIon> listSelectedMZIon = new Vector<MZIon>();
    
    // wird gerade angeziegt 
    private MZChromatogram[] currentMZChrom;
	private MZChromatogram currentTIC, currentSpectrum, currentFileTIC;
	private int selectedFileIndex=-1; 
	
	
	// IMAGES
	private Image2D currentImageDiscon, currentImageCon;
	
	

	//###############################################################################
    // Ein neues Fenster erstellen und alle Komponenten darauf plazieren
    LogicRunner(Window window){    
    	this.window = window;
        // Setup fileReader
        setUpFileReader();
    } 

    // prepare File reader
	private void setUpFileReader() {
		// mzXML reader:
		reader = new MZXMLReaderList();
	}
	
	//###############################################################################
    // INPUT AND OUTPUT 
	// NEW loadFIles with UpdateProgressTask
	public void loadFiles() {
		MZMineLogicsConnector.importRawDataDialog();
		/*
    	final File[] files = window.getFilesFromFileChooser(window.getFcOpenMS());
    	if(files != null) {
	    	// Up	dateTask
			ProgressUpdateTask task = ProgressDialog.getInst().startTask(new ProgressUpdateTask(ProgressDialog.getInst(), files.length) {
				// Load all Files
				@Override
				protected Boolean doInBackground() throws Exception {
					boolean state = true;
					//
					// load file  
					if(files.length>0) { 
						try {
							// All files in fileList
							for(File f : files) {
					            // RAW?
								if(FileTypeFilter.getExtensionFromFile(f).equalsIgnoreCase("RAW")) {
					            	loadRawFile(f);
					            }
					            // mzXML ?
					            if(FileTypeFilter.getExtensionFromFile(f).equalsIgnoreCase("mzXML")) {
					            	loadMzXMLFile(f); 
					            } 
								// Progress:
								addProgressStep(1);
							}
							try { 
					    		// show extracted masschromatogram  
					    		setNewFileSelectedAndShowAll(0);
							}catch(Exception ex) {
								ex.printStackTrace();
							}
						} catch(Exception ex) {
						} finally {
							ProgressDialog.getInst().setVisibleDialog(false);
						}
			        }
					//
					return state; 
				} 
			});
    	}
    	*/
	}
	
    /*
    public void loadMzXMLFile(File file) {
		// TODO Auto-generated method stub
		// load mzXML with reader
        if(FileTypeFilter.getExtensionFromFile(file).equalsIgnoreCase("mzXML")) { 
	    	try {
				if(reader.openFile(file)) {
					// List file in fileSpectrumList for Spectrum Information
					listSpecFiles.add(reader.getSpecList());
					// List File in Files List
					((DefaultListModel)window.getListFiles().getModel()).addElement(file); 
				}
			} catch(OutOfMemoryError ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(window.getFrame(), "Cannot load mzXML-file "+file.getName()+" \n Out of memory! "+ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE); 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane.showMessageDialog(window.getFrame(), "Cannot load mzXML-file "+file.getName()+" \n"+e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE); 
			} 
        }
	}

	private void loadRawFile(File file) {
		// TODO Auto-generated method stub
		// Convert via Tool the when Ready load with Reader
		try {
			MZFileConverter.convertRAWtoMzXml(window.getSettings().getSetConvertRAW(), this, file);
		} catch (Exception e) {
            e.printStackTrace(); 
			JOptionPane.showMessageDialog(window.getFrame(), "Cannot convert file from .RAW! "+e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE); 
        }   
	}
	*/

    public void saveImageFile() {
    	// TODO 
    	// Graphics Frame öffnen
    	// dann 
    	File file = window.getFileFromFileChooser(window.getFcSaveImage());
    	// FileTypeFilter.addExtensionToFile
        if(file!=null) { 
        	//TODO SAVE DATA
        }
    }
    
    // Variablen zum Speichern der Excel datei
    private XSSFWorkbook[] listWB;
    private File[] listWBFiles;
 // Saving Stuff: With Settings to path, filename, etc
    public boolean saveDataFile(final SettingsDataSaver settings, final XSSFExcelWriterReader excelWriter) throws NoFileSelectedException {   
    	// wenn nur selected File und keins selektiert dann abbrechen
    	if((settings.isExportsAllFiles() || window.getListFiles().getSelectedIndex()!=-1)) {
	    	// Berechnen wie viele Workbooks erstelt werden sollen
	    	int wbcount = 0;
	    	if(settings.isExportsAllFiles()) {
	        	// Export all files
	    		if(settings.isSavesAllFilesToOneXLS()) {
	    			// alle in eine xlsx
	    			wbcount = 1;
	    		}
	    		else {
	    			// alle in seperate files
	        		wbcount = getListSpecFiles().size();  
	    		}
				if(settings.isExportEIC() && settings.isAllMZInSeperateFiles()) {
					// All MZ in seperate files
					// Wenn kein TIC oder Spectrum dann wbcount auf 0 setzen
					// dann nur für jedes MZ ein  file
					if(!settings.isExportSpectrum() && !settings.isExportTIC())
						wbcount = 0;
					// dann alle MZ zusätzlich ein file
					wbcount += getMZListBySettings(settings.isSelectedMZOnly()).size(); 
				}
	    	}
	    	else {
	    		// export one file
	    		wbcount = 1;
				if(settings.isExportEIC() && settings.isAllMZInSeperateFiles()) {
					// All MZ in seperate files
					// Wenn kein TIC oder Spectrum dann wbcount auf 0 setzen
					// dann nur für jedes MZ ein  file
					if(!settings.isExportSpectrum() && !settings.isExportTIC())
						wbcount = 0;
					// dann alle MZ zusätzlich ein file
					wbcount += getMZListBySettings(settings.isSelectedMZOnly()).size(); 
				}
	    	}
	    	// FERTIG: Anzahl der xlsx files
	    	// Entweder ein File für spectrum und tic dann files für MZ
	    	// ODer ein File für alles und dann Sheets für MZ
	    	// Oder eine xls für jedes file und MZ als sheets oder als files am ende
	    	listWB = new XSSFWorkbook[wbcount];
	    	listWBFiles = new File[wbcount];
	    	// Listen für files und 
	    	ProgressUpdateTask task = ProgressDialog.startTask(new ProgressUpdateTask(listWB.length) {
				// Load all Files
				@Override
				protected Boolean doInBackground() throws Exception {
					boolean retVal = true;
					//
					// jedes WB nacheinander füllen
			    	// Files Aussuchen:  
					for(int i=0; i<listWB.length; i++) {
						// neues WB erstellen
						listWB[i] = new XSSFWorkbook();
						//
						int filei = i;
						if(!settings.isExportsAllFiles()) // nur ein file
							filei = window.getListFiles().getSelectedIndex();
						// All MZ in seperate files
						if(settings.isExportEIC() && settings.isAllMZInSeperateFiles()) {
							// um nur spec und TIC auszugeben exports EIC auf false
							settings.setExportEIC(false);
							// nur ein file oder alle für TIC und Spec?
							if(i==0 && settings.isExportsAllFiles() && settings.isSavesAllFilesToOneXLS()) { 
								// alle zu einem also mit schleife durchgehen in 0
								for(int s=0; s<listSpecFiles.size();s++) {
									// jedes spec und tic in ein file
									retVal = saveDataFileToWB(settings, excelWriter,i, s, s);
									// SetProgress
									addProgressStep(1.0/listSpecFiles.size());
								}
							}
							else if(i==0 && !settings.isExportsAllFiles()) {
								// nur selected file in 0
								retVal = saveDataFileToWB(settings, excelWriter,i, filei, 0);
							}
							else if(i<listSpecFiles.size() && settings.isExportsAllFiles() && !settings.isSavesAllFilesToOneXLS()) {
								// alle in ein eigenes 
								retVal = saveDataFileToWB(settings, excelWriter,i, filei, 0);
							}
							else { 
					    		Vector<MZIon> mzList = getMZListBySettings(settings.isSelectedMZOnly());
								// Nicht alle MZ durchgehen sondern mit dem fileindex i
					    		int mzindex = i;
					    		if(settings.isExportsAllFiles() && !settings.isSavesAllFilesToOneXLS()) mzindex -= listSpecFiles.size();
					    		else mzindex--;
								// Mz nehmen
								MZIon mz = mzList.get(mzindex);
								// nach den TIC xlsx kommt jetzt das richtige mz
								saveDataFileWithSeperatedMZ(settings, excelWriter,i, mzindex, mz); 
							}
							// wieder EIC anmachen 
							settings.setExportEIC(true);
						} 
						// Ansonsten alle MZ in ein File
						else {
							if(settings.isExportsAllFiles() && settings.isSavesAllFilesToOneXLS()) {
								// alle zu einem also mit schleife durchgehen
								for(int s=0; s<listSpecFiles.size();s++) {
									// jedes spec in ein file
									retVal = saveDataFileToWB(settings, excelWriter,i, s, s);
								}
							}
							else {
								// ansonsten jedes file in eigenes WB
								retVal = saveDataFileToWB(settings, excelWriter,i, filei, 0);
							}
						}
						// at end
				    	// write and Close all workbooks Data Output 
						if(retVal==true)
							excelWriter.saveWbToFile(listWBFiles[i], listWB[i]);
						else {
							// TODO fehler anzeigen. ein wb kann nciht exportiert werden.
						}
					}
					//
					return retVal; 
				} 
			});  
	    	
	    	while(!task.isDone()) {
	    		try {
	    			Thread.sleep(33);
	    		} catch (InterruptedException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    			return false;
	    		}
	    	} 
	    	return true;
    	} 
    	else {
    		return false;
    	}
    }
    // TODO ALT rauslöschen
	// Saving Stuff: With Settings to path, filename, etc
    public boolean saveDataFileALT(SettingsDataSaver settings, XSSFExcelWriterReader excelWriter) throws NoFileSelectedException {   
    	// wenn nur selected File und keins selektiert dann abbrechen
    	if((settings.isExportsAllFiles() || window.getListFiles().getSelectedIndex()!=-1)) {
	    	// Berechnen wie viele Workbooks erstelt werden sollen
	    	int wbcount = 0;
	    	if(settings.isExportsAllFiles()) {
	        	// Export all files
	    		if(settings.isSavesAllFilesToOneXLS()) {
	    			// alle in eine xlsx
	    			wbcount = 1;
	    		}
	    		else {
	    			// alle in seperate files
	        		wbcount = getListSpecFiles().size();  
	    		}
				if(settings.isExportEIC() && settings.isAllMZInSeperateFiles()) {
					// All MZ in seperate files
					// Wenn kein TIC oder Spectrum dann wbcount auf 0 setzen
					// dann nur für jedes MZ ein  file
					if(!settings.isExportSpectrum() && !settings.isExportTIC())
						wbcount = 0;
					// dann alle MZ zusätzlich ein file
					wbcount += getMZListBySettings(settings.isSelectedMZOnly()).size(); 
				}
	    	}
	    	else {
	    		// export one file
	    		wbcount = 1;
				if(settings.isExportEIC() && settings.isAllMZInSeperateFiles()) {
					// All MZ in seperate files
					// Wenn kein TIC oder Spectrum dann wbcount auf 0 setzen
					// dann nur für jedes MZ ein  file
					if(!settings.isExportSpectrum() && !settings.isExportTIC())
						wbcount = 0;
					// dann alle MZ zusätzlich ein file
					wbcount += getMZListBySettings(settings.isSelectedMZOnly()).size(); 
				}
	    	}
	    	// FERTIG: Anzahl der xlsx files
	    	// Entweder ein File für spectrum und tic dann files für MZ
	    	// ODer ein File für alles und dann Sheets für MZ
	    	// Oder eine xls für jedes file und MZ als sheets oder als files am ende
	    	listWB = new XSSFWorkbook[wbcount];
	    	listWBFiles = new File[wbcount];
	    	// Listen für files und 
	    	//TODO daten speichern? 
	    	boolean retVal = false; 
	    	// jedes WB nacheinander füllen
	    	// Files Aussuchen:  
			for(int i=0; i<listWB.length; i++) {
				// neues WB erstellen
				listWB[i] = new XSSFWorkbook();
				//
				int filei = i;
				if(!settings.isExportsAllFiles()) // nur ein file
					filei = window.getListFiles().getSelectedIndex();
				// All MZ in seperate files
				if(settings.isExportEIC() && settings.isAllMZInSeperateFiles()) {
					// um nur spec und TIC auszugeben exports EIC auf false
					settings.setExportEIC(false);
					// nur ein file oder alle für TIC und Spec?
					if(i==0 && settings.isExportsAllFiles() && settings.isSavesAllFilesToOneXLS()) { 
						// alle zu einem also mit schleife durchgehen in 0
						for(int s=0; s<listSpecFiles.size();s++) {
							// jedes spec und tic in ein file
							retVal = saveDataFileToWB(settings, excelWriter,i, s, s);
						}
					}
					else if(i==0 && !settings.isExportsAllFiles()) {
						// nur selected file in 0
						retVal = saveDataFileToWB(settings, excelWriter,i, filei, 0);
					}
					else if(i<listSpecFiles.size() && settings.isExportsAllFiles() && !settings.isSavesAllFilesToOneXLS()) {
						// alle in ein eigenes 
						retVal = saveDataFileToWB(settings, excelWriter,i, filei, 0);
					}
					else { 
			    		Vector<MZIon> mzList = getMZListBySettings(settings.isSelectedMZOnly());
						// Nicht alle MZ durchgehen sondern mit dem fileindex i
			    		int mzindex = i;
			    		if(settings.isExportsAllFiles() && !settings.isSavesAllFilesToOneXLS()) mzindex -= listSpecFiles.size();
			    		else mzindex--;
						// Mz nehmen
						MZIon mz = mzList.get(mzindex);
						// nach den TIC xlsx kommt jetzt das richtige mz
						saveDataFileWithSeperatedMZ(settings, excelWriter,i, mzindex, mz); 
					}
					// wieder EIC anmachen 
					settings.setExportEIC(true);
				} 
				// Ansonsten alle MZ in ein File
				else {
					if(settings.isExportsAllFiles() && settings.isSavesAllFilesToOneXLS()) {
						// alle zu einem also mit schleife durchgehen
						for(int s=0; s<listSpecFiles.size();s++) {
							// jedes spec in ein file
							retVal = saveDataFileToWB(settings, excelWriter,i, s, s);
						}
					}
					else {
						// ansonsten jedes file in eigenes WB
						retVal = saveDataFileToWB(settings, excelWriter,i, filei, 0);
					}
				}
				// at end
		    	// write and Close all workbooks Data Output 
				if(retVal==true)
					excelWriter.saveWbToFile(listWBFiles[i], listWB[i]);
				else {
					// TODO fehler anzeigen. ein wb kann nciht exportiert werden.
				}
			}
			 
	    	//
	    	return retVal;
    	}
    	else {
    		// Single file aber nicht selektiert: throw error
    		throw new NoFileSelectedException(); 
    	}
    }
    // Das nächste File durchgehen und daten speichern
    // filesExported zeigt wo das nächste geschrieben werden soll
    private boolean saveDataFileToWB(SettingsDataSaver settings, XSSFExcelWriterReader xlsWriter, int listWBIndex, int specFileIndex, int filesExported) {
    	// in ein file oder mehrere 
    	try{ 
    		// WB
    		XSSFWorkbook wb = listWB[listWBIndex];
    		// Styles 
    		CellStyle borderStyle = wb.createCellStyle();
    		borderStyle.setWrapText(true);
    		borderStyle.setBorderRight(CellStyle.BORDER_MEDIUM); 
	    	// Spectrum from File nehmen
	    	RawDataFile spec = listSpecFiles.get(specFileIndex);
	    	// Get WorkBook: 
	    	File file;
	    	//Save all to one xls?
	    	// save all mz in seperate files?
	    	if((settings.isSavesAllFilesToOneXLS() && filesExported==0)) {
	    		// Nur einmal eine xls erzeugen
	    		file = new File(settings.getPath(), settings.getFilename()+".xlsx");
	    		listWBFiles[listWBIndex] = file;
	    	}
	    	else if (filesExported==0){
	    		// Ansonsten immer ein Neues xls
	    		file = new File(settings.getPath(), spec.getName()+".xlsx");  
	        	// Open new Workbook
	    		listWBFiles[listWBIndex] = file; 
	    	}
	    	// Get Postion
	    	// Set Postion Offset for colls
	    	int normcolloffset = filesExported*2;
	    	int colloffset = normcolloffset;
	    	// Write Time Only Once
	    	if(settings.isWriteTimeOnlyOnce()) {
	    		colloffset = filesExported; 
	    	} 
	    	// Onlyy?
	    	boolean onlyY = !(!settings.isWriteTimeOnlyOnce() || filesExported==0);
    	
	    	// get WorkSheet 
	    	// Write COntent
	    	// Export TIC?
	    	if(settings.isExportTIC()) {
	    		XSSFSheet sheet = xlsWriter.getSheet(wb, "TIC");
	    		// set column width of 1+colloffset
	    		//sheet.setColumnWidth(1+colloffset, XLS_COLUMNWIDTH);
	    		//
	    		if(!onlyY) {
	    			xlsWriter.writeToCell(sheet, 0+colloffset, 0, "Filename");
		    		xlsWriter.writeToCell(sheet, 0+colloffset, 1, "Path");
		    		//xlsWriter.writeToCell(sheet, 0+colloffset, 2, "Header");
		    		// data header 
		    		xlsWriter.writeToCell(sheet, 0+colloffset, 3, "time (s)");
	    		}
	    		// TimeRelated immer
	    		xlsWriter.writeToCell(sheet, 1+colloffset, 0, spec.getName()).setCellStyle(borderStyle);
	    		// xlsWriter.writeToCell(sheet, 1+colloffset, 1, spec.get).setCellStyle(borderStyle);
	    		//xlsWriter.writeToCell(sheet, 1+colloffset, 2, spec.getHeader()).setCellStyle(borderStyle);
	    		// data header
	    		xlsWriter.writeToCell(sheet, 1+colloffset, 3, "intensity (cps)").setCellStyle(borderStyle); 
	    		// TICDATA
	    		writeMZChromToXLSAtPosition(xlsWriter, MZDataFactory.getTIC(spec), wb, sheet, 0+colloffset,4, onlyY);
	    	}
	    	
	    	// Export Spectrum?
	    	if(settings.isExportSpectrum()) {
	    		XSSFSheet sheet = xlsWriter.getSheet(wb, "Spetrum");
	    		// set column width of 1+colloffset
	    		//sheet.setColumnWidth(1+normcolloffset, XLS_COLUMNWIDTH);
	    		//
	    		xlsWriter.writeToCell(sheet, 0+normcolloffset, 0, "Filename");
	    		xlsWriter.writeToCell(sheet, 0+normcolloffset, 1, "Path");
	    		//xlsWriter.writeToCell(sheet, 0+normcolloffset, 2, "Header");
	    		xlsWriter.writeToCell(sheet, 1+normcolloffset, 0, spec.getName()).setCellStyle(borderStyle);
	    		//xlsWriter.writeToCell(sheet, 1+normcolloffset, 1, spec.getFile().getPath()).setCellStyle(borderStyle);
	    		//xlsWriter.writeToCell(sheet, 1+normcolloffset, 2, spec.getHeader()).setCellStyle(borderStyle);
	    		// get RT for spectrum
	    		double rt = 0;
	    		try{
	    			rt = Double.valueOf(window.getTxtRetentionTime().getText());
	    		} catch(Exception ex) { 
	    		}
	    		// write rt
	    		xlsWriter.writeToCell(sheet, 0+normcolloffset, 3, "r. time");
	    		xlsWriter.writeToCell(sheet, 1+normcolloffset, 3, rt).setCellStyle(borderStyle);
	    		
	    		// data header
	    		xlsWriter.writeToCell(sheet, 0+normcolloffset, 4, "m/z");
	    		xlsWriter.writeToCell(sheet, 1+normcolloffset, 4, "intensity (cps)").setCellStyle(borderStyle);
	    		// TICDATA
	    		writeMZChromToXLSAtPosition(xlsWriter, MZDataFactory.getSpectrumAsMZChrom(spec, rt), wb, sheet, 0+normcolloffset,5, false);
	    	}
	    	
	    	// Export EIC?
	    	// Export TIC?
	    	if(settings.isExportEIC()) {
	    		// Alle MZ bekommen
	    		Vector<MZIon> mzList = getMZListBySettings(settings.isSelectedMZOnly());
	    		for(int i=0; i<mzList.size(); i++) {
	    			MZIon mz = mzList.get(i); 
		    		//
		    		XSSFSheet sheet = xlsWriter.getSheet(wb, "mz="+mz.getMz()); 
		    		// set column width of 1+colloffset
		    		//sheet.setColumnWidth(1+colloffset, XLS_COLUMNWIDTH);
		    		if(!onlyY) {
			    		xlsWriter.writeToCell(sheet, 0+colloffset, 0, "Filename");
			    		xlsWriter.writeToCell(sheet, 0+colloffset, 1, "Path");
			    		//xlsWriter.writeToCell(sheet, 0+colloffset, 2, "Header");
			    		// data header 
			    		xlsWriter.writeToCell(sheet, 0+colloffset, 3, "time (s)");
		    		}
		    		// TimeRelated immer
		    		xlsWriter.writeToCell(sheet, 1+colloffset, 0, spec.getName()).setCellStyle(borderStyle);
		    		//xlsWriter.writeToCell(sheet, 1+colloffset, 1, spec.getFile().getPath()).setCellStyle(borderStyle);
		    		//xlsWriter.writeToCell(sheet, 1+colloffset, 2, spec.getHeader()).setCellStyle(borderStyle);
		    		// data header 
		    		xlsWriter.writeToCell(sheet, 1+colloffset, 3, "intensity (cps)").setCellStyle(borderStyle); 
		    		// TICDATA
		    		writeMZChromToXLSAtPosition(xlsWriter, MZDataFactory.getMZChrom(spec, mz, ChromGenType.HIGHEST_PEAK), wb, sheet, 0+colloffset,4, onlyY);
	    		} 
	    	} 

    	}catch(Exception ex) {
    		ex.printStackTrace();
    		return false;
    	}
    	return true;
    }
    
    // SAVE SEPARATED MZ
    // vorher alle mz durchgehen und übergeben
    private boolean saveDataFileWithSeperatedMZ(SettingsDataSaver settings,XSSFExcelWriterReader xlsWriter, int listWBIndex, int mzExported, MZIon mz) {
    	// in ein file oder mehrere 
    	try{ 
	    	// Get WorkBook:  
    		// WB
    		XSSFWorkbook wb = listWB[listWBIndex];
    		// Styles 
    		CellStyle borderStyle = wb.createCellStyle();
    		borderStyle.setWrapText(true);
    		borderStyle.setBorderRight(CellStyle.BORDER_MEDIUM);
    		// Nur einmal eine xls erzeugen
    		File file = new File(settings.getPath(), mz.getMz()+mz.getName()+".xlsx");
    		listWBFiles[listWBIndex] = file;
    		
	    	// Spectrum from File nehmen
    		for(int filesExported=0; filesExported<listSpecFiles.size(); filesExported++) {
		    	RawDataFile spec = listSpecFiles.get(filesExported); 
		    	// Get Postion
		    	// Set Postion Offset for colls
		    	int colloffset = filesExported*2;
		    	// Write Time Only Once
		    	if(settings.isWriteTimeOnlyOnce()) {
		    		colloffset = filesExported;
		    	} 
	    	
		    	// get WorkSheet 
		    	// Write COntent 
	    		// Alle MZ bekommen  
	    		XSSFSheet sheet = xlsWriter.getSheet(wb, "mz="+mz.getMz()); 
	    		// set column width of 1+colloffset
	    		//sheet.setColumnWidth(1+colloffset, XLS_COLUMNWIDTH);
	    		//
	    		boolean onlyY = !(!settings.isWriteTimeOnlyOnce() || filesExported==0);
	    		if(!onlyY) {
		    		xlsWriter.writeToCell(sheet, 0+colloffset, 0, "Filename");
		    		xlsWriter.writeToCell(sheet, 0+colloffset, 1, "Path");
		    		xlsWriter.writeToCell(sheet, 0+colloffset, 2, "Header");
		    		// data header 
		    		xlsWriter.writeToCell(sheet, 0+colloffset, 3, "time (s)");
	    		}
	    		// TimeRelated immer
	    		xlsWriter.writeToCell(sheet, 1+colloffset, 0, spec.getName()).setCellStyle(borderStyle);
	    		//xlsWriter.writeToCell(sheet, 1+colloffset, 1, spec.getFile().getPath()).setCellStyle(borderStyle);
	    		//xlsWriter.writeToCell(sheet, 1+colloffset, 2, spec.headerInfo).setCellStyle(borderStyle);
	    		// data header 
	    		xlsWriter.writeToCell(sheet, 1+colloffset, 3, "intensity (cps)").setCellStyle(borderStyle); 
	    		// TICDATA
	    		writeMZChromToXLSAtPosition(xlsWriter, MZDataFactory.getMZChrom(spec, mz, ChromGenType.HIGHEST_PEAK), wb, sheet, 0+colloffset,4,onlyY); 
    		}

    	}catch(Exception ex) {
    		ex.printStackTrace();
    		return false;
    	}
    	return true;
    }
    
    // MZChrom to xls 
    private void writeMZChromToXLSAtPosition(XSSFExcelWriterReader xlsWriter, MZChromatogram chrom, XSSFWorkbook wb, XSSFSheet sheet, int scol, int srow, boolean onlyY) {
    	// style
    	CellStyle borderStyle = wb.createCellStyle(); 
		borderStyle.setBorderRight(CellStyle.BORDER_MEDIUM);
    	
    	// write mzchrom to position in sheet
    	for(int i=0; i<chrom.getItemCount(); i++) {
    		// x (time or m/z)
    		if(!onlyY) xlsWriter.writeToCell(sheet, scol, srow+i, chrom.getX(i).doubleValue()); 
    		// y (intensity)
    		xlsWriter.writeToCell(sheet, scol+1, srow+i, chrom.getY(i).doubleValue()).setCellStyle(borderStyle); 
    	}
    }

    // INPUT OUTPUT COMPLETE
	//###############################################################################
    
	//###############################################################################
    // LOGIC
    // Wird als CurrentMode gesetzt
	public void setAsCurrentMode(JList listFiles) {
		// Alle Files wieder rein tun
		for(int i=0; i<listSpecFiles.size(); i++) {
			((DefaultListModel)listFiles.getModel()).addElement(listSpecFiles.get(i).getName());
		}
		listFiles.repaint();
	} 
 
    // Erneuert alle Plots
    public void setNewFileSelectedAndShowAll(int i) {
    	selectedFileIndex = i;
    	// TODO set currentMZ PM and RT
    	System.out.println("New File Selected "+i);
    	// 
    	if(i>=0) { 
	    	renewSpecVsImageVsChrom();
    	}
    }
    
    private void renewSpecVsImageVsChrom() {
		window.getTabThresomeTICvsMZvsSpec().renewAll();
	}

	// Close all Streams
	public void closeAll() {
		// TODO close all streams from mzmine?
	}
	
	// returns a list of MZ: onlySelected or all
	public Vector<MZIon> getMZListBySettings(boolean selectedOnly) {
		if(selectedOnly) {
			int[] sel = window.getListMZ().getSelectedIndices();
			Vector<MZIon> mz = new Vector<MZIon>();
			for(int i : sel) {
				mz.add(listMZIon.get(i));
			}
			return mz;
		}
		else return listMZIon;
	}
	

	//###############################################################################
	// Create Spectrums and all things for other Panels
	/*TODO
	 * returns spectrum
	 * 		getSpec(rt)				DONE
	 * 		getSpec(rt0,rt1) 		DONE
	 * 		getSpec(Settings x,y)	DONE
	 * 		getSpec(x,y,x2,y2		DONE
	 * returns chartpanel
	 * 		getChartPanelFromSpec(Spectrum
	 * 		getTIC
	 * 		getEIC(mz,pm,
	 * 		getImage(SettingsImage , mz, pm
	 *     		- discon / con is defined by settingstype --> check class
	 */ 
	public Scan generateSpectrumByRT(double rt) {
		try{
			if(selectedFileIndex!=-1)
				return MZDataFactory.getNearestSpectrumAtRT(listSpecFiles.get(selectedFileIndex), rt);
			else return null;
		}catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	} 
	public MZChromatogram generateSpectrumSUMByRT(double rt0, double rt1) {
		try{ 
			if(selectedFileIndex!=-1)
				return MZDataFactory.getSpectrumSumAsMZChrom(listSpecFiles.get(selectedFileIndex), rt0, rt1);
			else return null;
		}catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	// get spectrum from point in image. with image settings!
	public Scan generateSpectrumByXY(SettingsGeneralImage sett, double x, double y) {
		try{
			// var init
			int filei = -1;
			double rt = 0;
			// triggert / discon
			if(sett.isTriggered()) {
				// Fileindex y gibt aufschluss zur linie und damit zum file 
				filei = (int)Math.floor(y/sett.getSpotsize());
				if(filei<0) filei = 0;
				if(filei>=listSpecFiles.size()) filei = listSpecFiles.size()-1;
				// rt by x
				rt = x/sett.getVelocity(); 
			} else {
				// Continuous
				// x und y gehen in zeit ein
				filei = selectedFileIndex;
				rt = Math.floor(y/sett.getSpotsize())*sett.getTimePerLine() + x/sett.getVelocity();
			}
			// find File for y value
			if(filei!=-1)
				return MZDataFactory.getNearestSpectrumAtRT(listSpecFiles.get(filei), rt);
			else return null;
		}catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	} 
	// get SUM spectrum from area in image. with image settings!
	public MZChromatogram generateSpectrumByXY(SettingsGeneralImage sett, Rectangle2D rect) {
		double x = rect.getX();
		double y = rect.getY();
		double x2 = rect.getMaxX();
		double y2 = rect.getMaxY();
		return generateSpectrumByXY(sett, x, y, x2, y2);
	}
	// get SUM spectrum from area in image. with image settings!
	public MZChromatogram generateSpectrumByXY(SettingsGeneralImage sett, double x, double y, double x2, double y2) {
		try{
			// var init
			int fileiStart = -1;
			int fileiEnd = -1;
			double rt = 0;
			double rt2 = 0;
			// triggert / discon
			if(sett.isTriggered()) {
				// Fileindex y gibt aufschluss zur linie und damit zum file 
				fileiStart = (int)Math.floor(y/sett.getSpotsize());
				if(fileiStart<0) fileiStart = 0;
				if(fileiStart>=listSpecFiles.size()) fileiStart = listSpecFiles.size()-1;
				// End file index 
				fileiEnd = (int)Math.floor(y2/sett.getSpotsize());
				if(fileiEnd<0) fileiEnd = 0;
				if(fileiEnd>=listSpecFiles.size()) fileiEnd = listSpecFiles.size()-1;
				// rt by x
				rt = x/sett.getVelocity(); 
				rt2 = x2/sett.getVelocity(); 
			} else {
				// Continuous
				// x und y gehen in zeit ein
				fileiStart = selectedFileIndex;
				fileiEnd = selectedFileIndex;
				// time per line
				if(sett.getModeTimePerLine()==SettingsGeneralImage.MODE_TIME_PER_LINE) {
					// get Spectrum by time per line
					rt = Math.floor(y/sett.getSpotsize())*sett.getTimePerLine() + x/sett.getVelocity();
					rt2 = Math.floor(y2/sett.getSpotsize())*sett.getTimePerLine() + x2/sett.getVelocity();  
				}
				else {
					// resolution in scans per line
					// get Spectrum by direct spectrum number 
					// TODO wenn der mode umgestellt wird dann muss hier eine andere art zur berechnung des ausgewählten spektrums her
					rt = Math.floor(y/sett.getSpotsize())*sett.getTimePerLine() + x/sett.getVelocity();
					rt2 = Math.floor(y2/sett.getSpotsize())*sett.getTimePerLine() + x2/sett.getVelocity();  
				}
			}
			// for all files sum spectra from rt to rt2
			if(fileiStart!=-1) {
				if(fileiStart>fileiEnd) {
					int tmp = fileiStart;
					fileiStart = fileiEnd;
					fileiEnd = tmp;
				}
				
				if(fileiEnd-fileiStart>=1) {
					// Add alls specs
					Vector<MZChromatogram> specList = new Vector<MZChromatogram>(); 
					for(int i=fileiStart; i<=fileiEnd; i++) {
						specList.add(MZDataFactory.getSpectrumSumAsMZChrom(listSpecFiles.get(i), rt, rt2)); 
					} 
					// Filter combine all specs 
					MZSpectrumCombineFilter filter = new MZSpectrumCombineFilter(specList);
					if(filter.doFiltering()) {
						return (MZChromatogram) filter.getResult();
					} else {
						throw new FilteringFailedException("Cannot create sum spectrum for image"); 
					} 
				}
				else {
					return MZDataFactory.getSpectrumSumAsMZChrom(listSpecFiles.get(fileiStart),rt, rt2);
				} 
			}
			else return null;
		}catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	} 
	
	public ChartPanel generateSpectrumAsChartPanel(Scan spec) {
		return MZDataFactory.getSpectrumAsMZChrom(spec).getChromChartPanel("", "m/z", "intensity");   
	} 
	
	// TIC / EIC 
	public ChartPanel generateTICAsChartPanel() throws Exception { 
		if(selectedFileIndex!=-1 && listSpecFiles!=null)
			return MZDataFactory.getTIC(listSpecFiles.get(selectedFileIndex)).getChromChartPanel("TIC", "time", "intensity");
		else return null;
	}  
	public ChartPanel generateEICAsChartPanel(MZIon mzIon) throws Exception { 
		if(selectedFileIndex!=-1 && listSpecFiles!=null)
			return MZDataFactory.getMZChrom(listSpecFiles.get(selectedFileIndex), mzIon, ChromGenType.HIGHEST_PEAK).getChromChartPanel("MZ = "+mzIon.getMz(), "time", "intensity"); 	
		else return null;
	} 
	
	// Images
	public Image2D generateImageCon(SettingsMSImage setMSICon, SettingsImageContinousSplit settSplit) { 
		// pass options to reader. 
		// TODO nicht immer SUM_PEAKS
		MZChromatogram mzChrom = MZDataFactory.getMZChrom(getSelectedFile(), setMSICon.getMZIon(), ChromGenType.SUM_PEAKS); 
        // Panel as ChartViewer 
		if(mzChrom!=null) {
			// set title 
			NumberFormat form = SettingsHolder.getSettings().getSetGeneralValueFormatting().getMZFormat();
			String title = "m/z = "+form.format(setMSICon.getMZIon().getMz())+"(+- "+form.format(setMSICon.getMZIon().getPm())+")";
			setMSICon.setTitle(title);
			// set Path from first rawfile TODO
			//setMSICon.setRAWFilepath(listSpecFiles.get(selectedFileIndex).getFile().getPath());
			// create image
			Image2D img = Image2D.generateImage2DFromCon(window.getSettings().getSetPaintScale(), setMSICon, settSplit, mzChrom);
			return img;
		} 
		else return null;
	}
	
	// Discontinous Imaging for Triggert Data
	public Image2D generateImageDiscon(SettingsMSImage setMSIDiscon) {
		// TODO mehrere MZ zum auswählen
		// TODO Überlagerungsbilder mit richtigem Bild und mit anderen Ionen
		// Jedes File der List? oder nur ausgewählte? Somit FileListe
		Vector<RawDataFile> specList = null;
		if(setMSIDiscon.isAllFiles()) {
			specList = listSpecFiles;
		} 
		else { 
		}
		// Heatmap erstellen
		if(specList != null) { 
			// Alle Spektren zu MZChrom
			MZChromatogram[] mzChrom = new MZChromatogram[specList.size()];
			for(int i=0; i<specList.size(); i++) {
				mzChrom[i] = MZDataFactory.getMZChrom(specList.get(i), setMSIDiscon.getMZIon(), ChromGenType.SUM_PEAKS); 
			}
			// set title
			String title = setMSIDiscon.getMZIon().getName();
			if(title==null || title.length()<=0) title = "m/z = "+setMSIDiscon.getMZIon().getMz();
			setMSIDiscon.setTitle(title);
			// set Path from first rawfile TODO
			// setMSIDiscon.setRAWFilepath(specList.firstElement().getFile().getPath());
			// 
			Image2D img = Image2D.generateImage2DFrom(window.getSettings().getSetPaintScale(), setMSIDiscon, mzChrom);
			return img;
		} 
		else return null;
	}
	
	// Creation of spectrums and other things finished
	//###############################################################################
	
	// send images to ImageEditor  
	public Image2D getCurrentImage2DDiscon() { 
		return currentImageDiscon;
	} 
	public Image2D getCurrentImage2DCon() { 
		return currentImageCon;
	} 
	
	//###############################################################################
    // GETTERS AND SETTERS 
	public int getCurrentView() {
		return currentView;
	} 
	public void setCurrentView(int currentView) {
		this.currentView = currentView;
	}

	public Vector<MZIon> getListMZIon() {
		return listMZIon;
	}

	public void setListMZIon(Vector<MZIon> listMZIon) {
		this.listMZIon = listMZIon;
	}

	public double getCurrentRT() {
		return currentRT;
	}

	public void setCurrentRT(double currentRT) {
		this.currentRT = currentRT; 
	} 

	public MZChromatogram getCurrentTIC() {
		return currentTIC;
	}

	public void setCurrentTIC(MZChromatogram currentTIC) {
		this.currentTIC = currentTIC;
	}
    public Vector<RawDataFile> getListSpecFiles() {
		return listSpecFiles;
	}

	public void setListSpecFiles(Vector<RawDataFile> listSpecFiles) {
		this.listSpecFiles = listSpecFiles;
	}
    
    public int getSelectedFileIndex() {
		return selectedFileIndex;
	}

	public void setSelectedFileIndex(int selectedFileIndex) {
		this.selectedFileIndex = selectedFileIndex;
	} 
	
	public RawDataFile getSelectedFile() {
		return listSpecFiles.get(selectedFileIndex);
	}

	public Vector<PeakList> getPeakLists() {
		return listPeakList;
	}

	public void setListPeakList(Vector<PeakList> listPeakList) {
		this.listPeakList = listPeakList;
	}

}

