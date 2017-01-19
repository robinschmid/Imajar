package net.rs.lamsi.massimager.mzmine;

import java.util.Vector;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

import net.rs.lamsi.massimager.mzmine.interfaces.MZMinePeakListsChangedListener;
import net.rs.lamsi.massimager.mzmine.interfaces.MZMineRawDataListsChangedListener;
import net.rs.lamsi.massimager.mzmine.tasks.ThreadRawDataUpdate;
import net.sf.mzmine.MyStuff.listener.ProjectChangeListener;
import net.sf.mzmine.datamodel.PeakList;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.desktop.impl.MainMenu;
import net.sf.mzmine.desktop.impl.MainPanel;
import net.sf.mzmine.desktop.impl.MainWindow;
import net.sf.mzmine.main.WindowMZMine;

public class MZMineCallBackListener {  
	// ProjectChangeListener for loding old projects 
	private static ProjectChangeListener projectChangeListener;
	
	//
	private static Vector<MZMinePeakListsChangedListener> peakListListeners = new Vector<MZMinePeakListsChangedListener>();
	private static Vector<MZMineRawDataListsChangedListener> rawDataListListeners = new Vector<MZMineRawDataListsChangedListener>();

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
		MainWindow mainWnd = (MainWindow) WindowMZMine.getDesktop();
		mainWnd.addProjectChangeListener(projectChangeListener);
	}  
	
	private static void setListModelListeners() {
		MainWindow mainWnd = (MainWindow) WindowMZMine.getDesktop();
		MainMenu menu = mainWnd.getMainMenu();
		MainPanel mainPN = mainWnd.getMainPanel();
		// TreeListener for RawImport and PeakLists
		mainPN.getPeakListTree().getModel().addTreeModelListener(new PeakListChangedListener());
		mainPN.getRawDataTree().getModel().addTreeModelListener(new RawDataListChangedListener());
	}
	
	//------------------------------------------------------------------------
	// Called By Changes in MZMine Trees
	// call changes on all listeners
	public static void peakListsChanged(PeakList[] peakLists) {
		for (int i = 0; i < peakListListeners.size(); i++) {
			MZMinePeakListsChangedListener pll = peakListListeners.get(i); 
			pll.peakListsChanged(peakLists);			
		}
	}  
	// Start a Thread for RawDataUpdate
	public static void rawDataListsChanged(RawDataFile[] rawLists) { 
		ThreadRawDataUpdate.start();
	}
	
	// Called by ThreadRawDataUpdate
	public static void callRawDataListeners() { 
		RawDataFile[] rawLists = MZMineLogicsConnector.getRawDataLists();
		
		for (int i = 0; i < rawDataListListeners.size(); i++) {
			MZMineRawDataListsChangedListener pll = rawDataListListeners.get(i); 
			pll.rawDataListsChanged(rawLists);	
		}
	}
	// Update all peaklists 
	public static void callPeakListListeners() { 
		PeakList[] peakLists = MZMineLogicsConnector.getPeakLists();
		
		for (int i = 0; i < peakListListeners.size(); i++) {
			MZMinePeakListsChangedListener pll = peakListListeners.get(i); 
			pll.peakListsChanged(peakLists);	
		}
	}
	
	// Called by Project change to load all
	public static void callAllListenersForRawAndPeakLists() { 
		callRawDataListeners(); 
		callPeakListListeners();
	}

	// add listener to callback list
	public static void addMZMinePeakListChangedListener(MZMinePeakListsChangedListener listener) {
		peakListListeners.addElement(listener);
	}
	public static void addMZMineRawDataListChangedListener(MZMineRawDataListsChangedListener listener) {
		rawDataListListeners.addElement(listener);
	}

	// MZMine Trees have been changed
	private static class PeakListChangedListener implements TreeModelListener {  
		@Override
		public void treeNodesChanged(TreeModelEvent e) { 
			MZMineCallBackListener.peakListsChanged(MZMineLogicsConnector.getPeakLists());
		} 
		@Override
		public void treeNodesInserted(TreeModelEvent e) { 
			MZMineCallBackListener.peakListsChanged(MZMineLogicsConnector.getPeakLists());
		} 
		@Override
		public void treeNodesRemoved(TreeModelEvent e) { 
			MZMineCallBackListener.peakListsChanged(MZMineLogicsConnector.getPeakLists());
		} 
		@Override
		public void treeStructureChanged(TreeModelEvent e) { 
			MZMineCallBackListener.peakListsChanged(MZMineLogicsConnector.getPeakLists());
		} 
	}
	private static class RawDataListChangedListener implements TreeModelListener {  
		@Override
		public void treeNodesChanged(TreeModelEvent e) { 
			MZMineCallBackListener.rawDataListsChanged(MZMineLogicsConnector.getRawDataLists());
		} 
		@Override
		public void treeNodesInserted(TreeModelEvent e) { 
			MZMineCallBackListener.rawDataListsChanged(MZMineLogicsConnector.getRawDataLists());
		} 
		@Override
		public void treeNodesRemoved(TreeModelEvent e) { 
			MZMineCallBackListener.rawDataListsChanged(MZMineLogicsConnector.getRawDataLists());
		} 
		@Override
		public void treeStructureChanged(TreeModelEvent e) { 
			MZMineCallBackListener.rawDataListsChanged(MZMineLogicsConnector.getRawDataLists());
		} 
	} 
	
}
