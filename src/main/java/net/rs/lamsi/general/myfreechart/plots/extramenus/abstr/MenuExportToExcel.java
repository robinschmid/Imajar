package net.rs.lamsi.general.myfreechart.plots.extramenus.abstr;

import java.io.File;

import javax.swing.JMenuItem;

import net.rs.lamsi.utils.mywriterreader.XSSFExcelWriterReader;
 

public abstract class MenuExportToExcel extends JMenuItem implements MenuExport { 
	private XSSFExcelWriterReader excelWriter; 

	public MenuExportToExcel(XSSFExcelWriterReader excelWriter, String menuTitle) {
		super(menuTitle);
		this.excelWriter = excelWriter;
	}
	public abstract void exportDataToExcel(File file);
	public abstract void exportDataToExcel(File file, String sheet, int column, int row);
	 

}
