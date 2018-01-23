package net.rs.lamsi.massimager.MyMZ;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import net.rs.lamsi.general.myfreechart.swing.EChartPanel;

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
	public EChartPanel getChromChartPanel(String title, String xtitle, String ytitle) {
		EChartPanel myChart = new EChartPanel(getChromChart(title, xtitle, ytitle)); 
        myChart.setMouseWheelEnabled(true); 
        return myChart;
	} 

	// create a chart image
	public static EChartPanel getChromChartPanelFromVector(String title, String xtitle, String ytitle, MZChromatogram[] listMZChrom) { 
		EChartPanel myChart = new EChartPanel(getChromChartFromVector(title, xtitle, ytitle, listMZChrom)); 
        myChart.setMouseWheelEnabled(true); 
        return myChart;
	}
}
