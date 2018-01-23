package net.rs.lamsi.general.myfreechart.plots.image2d.listener;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.data.Range;

import net.rs.lamsi.general.myfreechart.plots.PlotChartPanel;

public interface AxesRangeChangedListener {

	/**
	 * only if axis range has changed
	 * @param axis
	 * @param lastR
	 * @param newR
	 */
	public abstract void axesRangeChanged(PlotChartPanel chart, ValueAxis axis, boolean isDomainAxis, Range lastR, Range newR);
}
