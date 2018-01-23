package net.rs.lamsi.general.myfreechart.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.jfree.chart.ChartPanel;
import org.jfree.data.Range;

public class ChartZoomConnector {

	private List<ChartPanel> charts;
	private Range lastXRange = null;
	private Range lastYRange = null;
	private Consumer<AxisRangeChangedEvent> c;
	
	
	public ChartZoomConnector(Consumer<AxisRangeChangedEvent> c) {
		this.c = c;
	}

	/**
	 * Add the chart to a list and add AxisRangeChangedListener
	 * @param chart
	 */
	public void add(ChartPanel chart) {
		if(charts==null)
			charts = new ArrayList<ChartPanel>();
		charts.add(chart);

		chart.getChart().getXYPlot().getDomainAxis().addChangeListener(new AxisRangeChangedListener(chart, e -> setXRange(e)));
		chart.getChart().getXYPlot().getRangeAxis().addChangeListener(new AxisRangeChangedListener(chart, e -> setYRange(e)));
	}
	
	public void clear() {
		charts.clear();
	}
	public List<ChartPanel> getCharts() {
		return charts;
	}

	/**
	 * set range to all charts
	 * @param e
	 */
	public void setXRange(AxisRangeChangedEvent e) {
		Range r = e.getNewR();
		if(r !=null && !r.equals(lastXRange)) {
			lastXRange = r;
			charts.stream().forEach(cp -> cp.getChart().getXYPlot().getDomainAxis().setRange(r));
			c.accept(e);
		}
	}

	/**
	 * set range to all charts
	 * @param e
	 */
	public void setYRange(AxisRangeChangedEvent e) {
		Range r = e.getNewR();
		if(r !=null && !r.equals(lastYRange)) {
			lastYRange = r;
			charts.stream().forEach(cp -> cp.getChart().getXYPlot().getRangeAxis().setRange(r));
			c.accept(e);
		}
	}
}
