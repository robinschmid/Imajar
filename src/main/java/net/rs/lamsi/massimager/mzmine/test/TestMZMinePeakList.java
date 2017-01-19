package net.rs.lamsi.massimager.mzmine.test;

import java.io.File;

import net.rs.lamsi.massimager.Frames.Window;
import net.rs.lamsi.massimager.mzmine.MZMineLogicsConnector;

public class TestMZMinePeakList {

	public static void main(String[] args) { 
		//Window window = new Window();
		//window.getFrame().setVisible(true); 
		
		// connect to mzmine
		MZMineLogicsConnector.connectToMZMine();
		MZMineLogicsConnector.getPeakLists();
	}
}
