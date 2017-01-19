package net.rs.lamsi.massimager.MyMZ;

import net.rs.lamsi.massimager.MyFreeChart.Plot.xylinechart.PlotXYLineChartPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class MZChromatogram extends XYSeries {
	 

	public MZChromatogram(Comparable key) {
		super(key,true, true); 
	}  
	
	
	// create a chart image
	public JFreeChart getChromChart(String title, String xtitle, String ytitle) {
		XYSeriesCollection my_data_series= new XYSeriesCollection();
		my_data_series.addSeries(this);
		return ChartFactory.createXYLineChart(title, xtitle, ytitle, my_data_series,PlotOrientation.VERTICAL,false,true,false);
	} 

	// create a chart image
	public static JFreeChart getChromChartFromVector(String title, String xtitle, String ytitle, MZChromatogram[] listMZChrom) {
		XYSeriesCollection my_data_series= new XYSeriesCollection();
		for(MZChromatogram chrom : listMZChrom)
			my_data_series.addSeries(chrom);
		return ChartFactory.createXYLineChart(title, xtitle, ytitle, my_data_series,PlotOrientation.VERTICAL,false,true,false);
	}
	

	
	// create a chart image
	public PlotXYLineChartPanel getChromChartPanel(String title, String xtitle, String ytitle) {
		PlotXYLineChartPanel myChart = new PlotXYLineChartPanel(getChromChart(title, xtitle, ytitle)); 
        myChart.setMouseWheelEnabled(true); 
        return myChart;
	} 

	// create a chart image
	public static PlotXYLineChartPanel getChromChartPanelFromVector(String title, String xtitle, String ytitle, MZChromatogram[] listMZChrom) { 
		PlotXYLineChartPanel myChart = new PlotXYLineChartPanel(getChromChartFromVector(title, xtitle, ytitle, listMZChrom)); 
        myChart.setMouseWheelEnabled(true); 
        return myChart;
	}
}
