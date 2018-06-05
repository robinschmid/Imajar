package net.rs.lamsi.massimager.mzmine;

import java.util.ArrayList;
import net.sf.mzmine.MyStuff.listener.ProjectChangeListener;
import net.sf.mzmine.datamodel.MZmineProject;
import net.sf.mzmine.datamodel.MZmineProjectListener;
import net.sf.mzmine.datamodel.PeakList;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.datamodel.impl.MZmineProjectListenerAdapter;
import net.sf.mzmine.datamodel.impl.MZmineProjectListenerAdapter.Operation;
import net.sf.mzmine.desktop.impl.MainWindow;
import net.sf.mzmine.main.MZmineCore;

public class MZMineCallBackListener {
  // ProjectChangeListener for loding old projects
  private static ProjectChangeListener projectChangeListener;

  //
  // private static Vector<MZMinePeakListsChangedListener> peakListListeners = new
  // Vector<MZMinePeakListsChangedListener>();
  // private static Vector<MZMineRawDataListsChangedListener> rawDataListListeners = new
  // Vector<MZMineRawDataListsChangedListener>();

  private static ArrayList<MZmineProjectListener> projectListener =
      new ArrayList<MZmineProjectListener>();
  private static MZmineProjectListenerAdapter parentProjectListener;


  public static void initCallbackListeners() {
    setListModelListeners();
    projectChangeListener = new ProjectChangeListener() {
      @Override
      public void projectChanged() {
        setListModelListeners();
        callAllListenersForRawAndPeakLists();
      }
    };
    // add listener to mainwindow
    MainWindow mainWnd = (MainWindow) MZmineCore.getDesktop();
    // if the current project has changed?
    mainWnd.addProjectChangeListener(projectChangeListener);
  }

  private static void setListModelListeners() {
    MainWindow mainWnd = (MainWindow) MZmineCore.getDesktop();

    MZmineProject project = getProject();
    // call all other listeners from this callbacklistener class
    parentProjectListener = new MZmineProjectListenerAdapter() {
      @Override
      public void peakListsChanged(PeakList pkl, Operation op) {
        for (MZmineProjectListener l : projectListener)
          if (op.equals(Operation.ADDED))
            l.peakListAdded(pkl);
          else
            l.peakListRemoved(pkl);
      }

      @Override
      public void dataFilesChanged(RawDataFile raw, Operation op) {
        for (MZmineProjectListener l : projectListener)
          if (op.equals(Operation.ADDED))
            l.dataFileAdded(raw);
          else
            l.dataFileRemoved(raw);
      }
    };
    project.addProjectListener(parentProjectListener);

    // TreeListener for RawImport and PeakLists
    // mainPN.getPeakListTree().getModel().addTreeModelListener(new PeakListChangedListener());
    // mainPN.getRawDataTree().getModel().addTreeModelListener(new RawDataListChangedListener());
  }

  public static MZmineProject getProject() {
    return MZmineCore.getProjectManager().getCurrentProject();
  }

  // Called by Project change to load all
  public static void callAllListenersForRawAndPeakLists() {
    parentProjectListener.dataFilesChanged(null, Operation.ADDED);
    parentProjectListener.peakListsChanged(null, Operation.ADDED);
  }

  // Called by Project change to load all
  public static void callAllListenersForRaw() {
    parentProjectListener.dataFilesChanged(null, Operation.ADDED);
  }

  // add listener to callback list
  public static void addMZmineProjectListener(MZmineProjectListener listener) {
    projectListener.add(listener);
  }

  // // MZMine Trees have been changed
  // private static class PeakListChangedListener implements TreeModelListener {
  // @Override
  // public void treeNodesChanged(TreeModelEvent e) {
  // MZMineCallBackListener.peakListsChanged(MZMineLogicsConnector.getPeakLists());
  // }
  // @Override
  // public void treeNodesInserted(TreeModelEvent e) {
  // MZMineCallBackListener.peakListsChanged(MZMineLogicsConnector.getPeakLists());
  // }
  // @Override
  // public void treeNodesRemoved(TreeModelEvent e) {
  // MZMineCallBackListener.peakListsChanged(MZMineLogicsConnector.getPeakLists());
  // }
  // @Override
  // public void treeStructureChanged(TreeModelEvent e) {
  // MZMineCallBackListener.peakListsChanged(MZMineLogicsConnector.getPeakLists());
  // }
  // }
  // private static class RawDataListChangedListener implements TreeModelListener {
  // @Override
  // public void treeNodesChanged(TreeModelEvent e) {
  // MZMineCallBackListener.rawDataListsChanged(MZMineLogicsConnector.getRawDataLists());
  // }
  // @Override
  // public void treeNodesInserted(TreeModelEvent e) {
  // MZMineCallBackListener.rawDataListsChanged(MZMineLogicsConnector.getRawDataLists());
  // }
  // @Override
  // public void treeNodesRemoved(TreeModelEvent e) {
  // MZMineCallBackListener.rawDataListsChanged(MZMineLogicsConnector.getRawDataLists());
  // }
  // @Override
  // public void treeStructureChanged(TreeModelEvent e) {
  // MZMineCallBackListener.rawDataListsChanged(MZMineLogicsConnector.getRawDataLists());
  // }
  // }

}
