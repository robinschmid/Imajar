package net.rs.lamsi.massimager.mzmine;

import java.io.File;
import java.util.ArrayList;

import net.sf.mzmine.datamodel.MZmineProject;
import net.sf.mzmine.datamodel.PeakList;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.desktop.impl.MainMenu;
import net.sf.mzmine.desktop.impl.MainPanel;
import net.sf.mzmine.desktop.impl.MainWindow;
import net.sf.mzmine.main.WindowMZMine;
import net.sf.mzmine.modules.MZmineRunnableModule;
import net.sf.mzmine.modules.rawdatamethods.rawdataimport.RawDataImportParameters;
import net.sf.mzmine.parameters.Parameter;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.parameters.parametertypes.PeakListsParameter;
import net.sf.mzmine.parameters.parametertypes.RawDataFilesParameter;
import net.sf.mzmine.taskcontrol.Task;
import net.sf.mzmine.taskcontrol.TaskPriority;
import net.sf.mzmine.util.ExitCode;
 


public class MZMineLogicsConnector { 
	public static String MODULE_RAW_IMPORT = "Raw data import";
	public static String MODULE_MASSDETECT_WAVELET = "Wavelet transform", MODULE_MASSDETECT_EXACT = "Exact mass";
	public static String MODULE_CHROMATOGRAMBUILDER = "Chromatogram builder";
	public static String MODULE_JOIN_ALIGNER = "Join aligner";
	
	
	public static void connectToMZMine() {
		String[] args = new String[0];
		WindowMZMine.main(args);

		MainWindow mainWnd = (MainWindow) WindowMZMine.getDesktop();
		MainMenu menu = mainWnd.getMainMenu();
		MainPanel mainPN = mainWnd.getMainPanel();
		//
		MZMineCallBackListener.initCallbackListeners();
	}
	
	//
	public static boolean addTaskToStack(Task task) {
		try{
			WindowMZMine.getTaskController().addTask(task, TaskPriority.NORMAL);
			return true;
		} catch(Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	// returns all peaklists in tree
	public static PeakList[] getPeakLists() {
		MZmineProject project = WindowMZMine.getCurrentProject();  
		PeakList[] peakLists = project.getPeakLists(); 
		return peakLists;
	}
	// returns all rawDataFiles in tree
	public static RawDataFile[] getRawDataLists() {
		MZmineProject project = WindowMZMine.getCurrentProject(); 
		RawDataFile[] rawFiles = project.getDataFiles();   
		return rawFiles;
	}
	
	// simulates a click on a JMenuItem-Module (starts the module code sided)
	public static void activateModule(String moduleName) {
		MainWindow mainWnd = (MainWindow) WindowMZMine.getDesktop();
		MainMenu menu = mainWnd.getMainMenu();
		
		menu.startModuleCodeSided(moduleName);
	}
	
	// used to open an import dialog
	public static void importRawDataDialog() {
		activateModule(MODULE_RAW_IMPORT);
	}
	
	public static void importRawDataDirect(File[] files) {
		MainWindow mainWnd = (MainWindow) WindowMZMine.getDesktop();
		MainMenu menu = mainWnd.getMainMenu(); 
		MZmineRunnableModule module = menu.getModuleByName(MODULE_RAW_IMPORT);
		
		if (module != null) {
			RawDataImportParameters moduleParameters = (RawDataImportParameters) WindowMZMine.getConfiguration().getModuleParameters(module.getClass());

			RawDataFile selectedFiles[] = WindowMZMine.getDesktop().getSelectedDataFiles();
			if (selectedFiles.length > 0) {
				for (Parameter<?> p : moduleParameters.getParameters()) {
					if (p instanceof RawDataFilesParameter) {
						RawDataFilesParameter rdp = (RawDataFilesParameter) p;
						rdp.setValue(selectedFiles);
					}
				}

			}
			PeakList selectedPeakLists[] = WindowMZMine.getDesktop()
					.getSelectedPeakLists();
			if (selectedPeakLists.length > 0) {
				for (Parameter<?> p : moduleParameters.getParameters()) {
					if (p instanceof PeakListsParameter) {
						PeakListsParameter plp = (PeakListsParameter) p;
						plp.setValue(selectedPeakLists);
					}
				}
			}
 
			//ExitCode exitCode = moduleParameters.showSetupDialog(WindowMZMine.getDesktop().getMainWindow(), true);
			System.out.println("SETTING PARAMETERS for IMPORT");
			moduleParameters.getParameter(moduleParameters.fileNames).setValue(files);
			 
			ParameterSet parametersCopy = moduleParameters.cloneParameterSet(); 
			ArrayList<Task> tasks = new ArrayList<Task>();
			module.runModule(parametersCopy, tasks);
			WindowMZMine.getTaskController().addTasks(tasks.toArray(new Task[0])); 
			return;
		}
	}
	
}
