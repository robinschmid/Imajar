package net.rs.lamsi.massimager.mzmine;

import java.io.File;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.sf.mzmine.datamodel.MZmineProject;
import net.sf.mzmine.datamodel.PeakList;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.desktop.impl.MainMenu;
import net.sf.mzmine.desktop.impl.MainPanel;
import net.sf.mzmine.desktop.impl.MainWindow;
import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.modules.MZmineRunnableModule;
import net.sf.mzmine.modules.rawdatamethods.rawdataimport.RawDataImportParameters;
import net.sf.mzmine.parameters.Parameter;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.parameters.parametertypes.selectors.PeakListsParameter;
import net.sf.mzmine.parameters.parametertypes.selectors.PeakListsSelection;
import net.sf.mzmine.parameters.parametertypes.selectors.PeakListsSelectionType;
import net.sf.mzmine.parameters.parametertypes.selectors.RawDataFilesParameter;
import net.sf.mzmine.parameters.parametertypes.selectors.RawDataFilesSelection;
import net.sf.mzmine.parameters.parametertypes.selectors.RawDataFilesSelectionType;
import net.sf.mzmine.taskcontrol.Task;
import net.sf.mzmine.taskcontrol.TaskPriority;



public class MZMineLogicsConnector {
  public static String MODULE_PROJECT_IMPORT = "Open project";
  public static String MODULE_RAW_IMPORT = "Raw data import";
  public static String MODULE_MASSDETECT_WAVELET = "Wavelet transform",
      MODULE_MASSDETECT_EXACT = "Exact mass";
  public static String MODULE_CHROMATOGRAMBUILDER = "Chromatogram builder";
  public static String MODULE_JOIN_ALIGNER = "Join aligner";
  private static final Logger logger = LoggerFactory.getLogger(MZMineLogicsConnector.class);


  public static void connectToMZMine() {
    String[] args = new String[0];
    MZmineCore.main(args);

    MainWindow mainWnd = (MainWindow) MZmineCore.getDesktop();
    MainMenu menu = mainWnd.getMainMenu();
    MainPanel mainPN = mainWnd.getMainPanel();
    //
    MZMineCallBackListener.initCallbackListeners();
  }

  //
  public static boolean addTaskToStack(Task task) {
    try {
      MZmineCore.getTaskController().addTask(task, TaskPriority.NORMAL);
      return true;
    } catch (Exception ex) {
      logger.error("", ex);
      return false;
    }
  }

  /**
   * current project
   * 
   * @return
   */
  public static MZmineProject getProject() {
    return MZmineCore.getProjectManager().getCurrentProject();
  }

  /**
   * peak lists
   * 
   * @return
   */
  public static PeakList[] getPeakLists() {
    return getProject().getPeakLists();
  }

  /**
   * raw data files
   * 
   * @return
   */
  public static RawDataFile[] getRawDataLists() {
    return getProject().getDataFiles();
  }

  /**
   * simulates a click on a JMenuItem-Module (starts the module code sided)
   * 
   * @param moduleName
   */
  public static void activateModule(String moduleName) {
    MainWindow mainWnd = (MainWindow) MZmineCore.getDesktop();
    MainMenu menu = mainWnd.getMainMenu();

    menu.startModuleCodeSided(moduleName);
  }

  /**
   * used to open an import dialog
   */
  public static void importRawDataDialog() {
    activateModule(MODULE_RAW_IMPORT);
  }

  /**
   * import files or use importRawDataDialog()
   * 
   * @param files
   */
  public static void importRawDataDirect(File[] files) {
    MainWindow mainWnd = getMZmineMainWindow();
    MainMenu menu = mainWnd.getMainMenu();
    MZmineRunnableModule module = menu.getModuleByName(MODULE_RAW_IMPORT);

    if (module != null) {
      RawDataImportParameters moduleParameters = (RawDataImportParameters) MZmineCore
          .getConfiguration().getModuleParameters(module.getClass());

      RawDataFile selectedFiles[] = mainWnd.getSelectedDataFiles();
      RawDataFilesSelection sel =
          new RawDataFilesSelection(RawDataFilesSelectionType.SPECIFIC_FILES);
      sel.setSpecificFiles(selectedFiles);
      if (selectedFiles.length > 0) {
        for (Parameter<?> p : moduleParameters.getParameters()) {
          if (p instanceof RawDataFilesParameter) {
            RawDataFilesParameter rdp = (RawDataFilesParameter) p;
            rdp.setValue(sel);
          }
        }

      }
      PeakList selectedPeakLists[] = getMZmineMainWindow().getSelectedPeakLists();
      PeakListsSelection selP = new PeakListsSelection();
      selP.setSelectionType(PeakListsSelectionType.SPECIFIC_PEAKLISTS);
      selP.setSpecificPeakLists(selectedPeakLists);
      if (selectedPeakLists.length > 0) {
        for (Parameter<?> p : moduleParameters.getParameters()) {
          if (p instanceof PeakListsParameter) {
            PeakListsParameter plp = (PeakListsParameter) p;
            plp.setValue(selP);
          }
        }
      }

      // ExitCode exitCode =
      // moduleParameters.showSetupDialog(WindowMZMine.getDesktop().getMainWindow(), true);
      System.out.println("SETTING PARAMETERS for IMPORT");
      moduleParameters.getParameter(moduleParameters.fileNames).setValue(files);

      ParameterSet parametersCopy = moduleParameters.cloneParameterSet();
      ArrayList<Task> tasks = new ArrayList<Task>();
      module.runModule(getProject(), parametersCopy, tasks);
      MZmineCore.getTaskController().addTasks(tasks.toArray(new Task[0]));
      return;
    }
  }

  public static MainWindow getMZmineMainWindow() {
    return (MainWindow) MZmineCore.getDesktop();
  }
}
