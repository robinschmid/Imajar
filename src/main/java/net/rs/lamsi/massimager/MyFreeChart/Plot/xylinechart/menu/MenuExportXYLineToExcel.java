package net.rs.lamsi.massimager.MyFreeChart.Plot.xylinechart.menu;

import java.io.File;

import net.rs.lamsi.massimager.MyFreeChart.Plot.ExtraMenu.abstr.MenuExportToExcel;
import net.rs.lamsi.massimager.MyFreeChart.Plot.xylinechart.PlotXYLineChartPanel;
import net.rs.lamsi.utils.mywriterreader.XSSFExcelWriterReader;

public class MenuExportXYLineToExcel extends MenuExportToExcel {
	
	private PlotXYLineChartPanel xyChart; 

	
	public MenuExportXYLineToExcel(XSSFExcelWriterReader excelWriter, String menuTitle, PlotXYLineChartPanel xyChart) {
		super(excelWriter, menuTitle);
		this.xyChart = xyChart;
	}

	@Override
	public void exportDataToExcel(File file) {
		
	}

	@Override
	public void exportDataToExcel(File file, String sheet, int column, int row) {
		
	}

	

}
