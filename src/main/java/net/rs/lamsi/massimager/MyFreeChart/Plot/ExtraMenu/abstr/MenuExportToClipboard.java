package net.rs.lamsi.massimager.MyFreeChart.Plot.ExtraMenu.abstr;

import javax.swing.JMenuItem;
 

public abstract class MenuExportToClipboard extends JMenuItem implements MenuExport {  

	public MenuExportToClipboard(String menuTitle) {
		super(menuTitle); 
	}
	
	public abstract void exportDataToClipboard(); 
}
