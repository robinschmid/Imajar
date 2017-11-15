package net.rs.lamsi.massimager.mzmine.test;

import net.rs.lamsi.massimager.mzmine.MZMineLogicsConnector;

public class TestRawImport {


	public static void main(String[] args) { 
		//Window window = new Window();
		//window.getFrame().setVisible(true); 
		
		// connect to mzmine
		MZMineLogicsConnector.connectToMZMine();

		MZMineLogicsConnector.activateModule(MZMineLogicsConnector.MODULE_RAW_IMPORT);
	}
	

}
