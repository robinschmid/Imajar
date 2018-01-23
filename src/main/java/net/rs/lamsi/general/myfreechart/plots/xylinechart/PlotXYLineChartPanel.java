package net.rs.lamsi.general.myfreechart.plots.xylinechart;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;

import net.rs.lamsi.general.myfreechart.plots.PlotChartPanel;
import net.rs.lamsi.general.myfreechart.plots.xylinechart.menu.MenuExportXYLineToClipboard;
import net.rs.lamsi.general.myfreechart.plots.xylinechart.menu.MenuExportXYLineToExcel;
import net.rs.lamsi.utils.mywriterreader.XSSFExcelWriterReader;

import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;

public class PlotXYLineChartPanel extends PlotChartPanel  {  
	 

	public PlotXYLineChartPanel(JFreeChart chart) {
		super(chart); 
        this.setMouseWheelEnabled(true); 
        // for labels set upper margin
        this.getChart().getXYPlot().getRangeAxis().setUpperMargin(0.1);  
	}
	
	@Override
	protected void addExportMenu() { 
		super.addExportMenu();
		// Allgemeiner Export
		JMenu export = new JMenu("Export data ..."); 
		// Excel XY
		MenuExportXYLineToExcel exportXY = new MenuExportXYLineToExcel(new XSSFExcelWriterReader(), "to Excel", this);
		exportXY.addActionListener(new ActionListener() { 
			@Override
			public void actionPerformed(ActionEvent e) { 
				// TODO Export to Excel frame
			}
		});
		export.add(exportXY);
		// Clipboard
		MenuExportXYLineToClipboard exportXYClipboard = new MenuExportXYLineToClipboard("to Clipboard", this); 
		export.add(exportXYClipboard);
		
		// add to panel
		addPopupMenu(export);
	}
 

	public Object[][] getDataArrayForExport() { 
		try {
			XYDataset data = getChart().getXYPlot().getDataset();
			int size = 1+ data.getItemCount(0);
			// create new Array model[row][col]
			Object[][] model = new Object[size][2];
			// Write header
			model[0][0] = getChart().getXYPlot().getDomainAxis().getLabel();
			model[0][1] = getChart().getXYPlot().getRangeAxis().getLabel();
			//write data
			for(int i=0; i<data.getItemCount(0); i++) {
				model[i+1][0] = data.getX(0, i);
				model[i+1][1] = data.getY(0, i);
			}
			return model;
		}catch(Exception ex) {
			return null;
		} 
	}

}
