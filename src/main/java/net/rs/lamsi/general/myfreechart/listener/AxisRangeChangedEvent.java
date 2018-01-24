package net.rs.lamsi.general.myfreechart.listener;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;

public class AxisRangeChangedEvent {

	
	private XYPlot plot;
	private ValueAxis axis;
	private Range lastR, newR;
	
	public AxisRangeChangedEvent(XYPlot plot, ValueAxis axis, Range lastR, Range newR) {
		super();
		this.plot = plot;
		this.axis = axis;
		this.lastR = lastR;
		this.newR = newR;
	}
	public XYPlot getPlot() {
		return plot;
	}
	public void setPlot(ChartPanel chart) {
		this.plot = plot;
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
