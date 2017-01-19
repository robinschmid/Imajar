package net.rs.lamsi.massimager.MyFreeChart.Plot.xylinechart.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.rs.lamsi.massimager.MyFreeChart.Plot.ExtraMenu.abstr.MenuExportToClipboard;
import net.rs.lamsi.massimager.MyFreeChart.Plot.xylinechart.PlotXYLineChartPanel;
import net.rs.lamsi.utils.mywriterreader.ClipboardWriter;
 

public class MenuExportXYLineToClipboard extends MenuExportToClipboard { 
	
	private PlotXYLineChartPanel xyChart; 
	
	public MenuExportXYLineToClipboard(String menuTitle, PlotXYLineChartPanel xyChart) {
		super(menuTitle);
		this.xyChart = xyChart;
		this.addActionListener(new ActionListener() { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
				exportDataToClipboard();
			}
		});
	} 
	
	@Override
	public void exportDataToClipboard() {
		Object[][] model = xyChart.getDataArrayForExport();
		if(model!=null)
			ClipboardWriter.writeToClipBoard(model);
	}
	
}
