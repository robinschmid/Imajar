package net.rs.lamsi.general.myfreechart.listener;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.data.Range;

public class AxisRangeChangedEvent {

	
	private ChartPanel chart;
	private ValueAxis axis;
	private Range lastR, newR;
	
	public AxisRangeChangedEvent(ChartPanel chart, ValueAxis axis, Range lastR, Range newR) {
		super();
		this.chart = chart;
		this.axis = axis;
		this.lastR = lastR;
		this.newR = newR;
	}
	public ChartPanel getChart() {
		return chart;
	}
	public void setChart(ChartPanel chart) {
		this.chart = chart;
	}
	public ValueAxis getAxis() {
		return axis;
	}
	public void setAxis(ValueAxis axis) {
		this.axis = axis;
	}
	public Range getLastR() {
		return lastR;
	}
	public void setLastR(Range lastR) {
		this.lastR = lastR;
	}
	public Range getNewR() {
		return newR;
	}
	public void setNewR(Range newR) {
		this.newR = newR;
	}
	
}
