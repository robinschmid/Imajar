package net.rs.lamsi.massimager.MyFreeChart.Plot.image2d.listener;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.event.AxisChangeListener;
import org.jfree.data.Range;

public abstract class AxisRangeChangedListener implements AxisChangeListener {

	// last lower / upper range
	private Range lastRange = null;
	
	@Override
	public void axisChanged(AxisChangeEvent e) {
		ValueAxis a = (ValueAxis) e.getAxis();
		Range r = a.getRange();

		if(r!=null && (lastRange==null || !r.equals(lastRange))) {
			// range has changed
			axisRangeChanged(a, lastRange, r);
		}
		lastRange = r;
	}

	/**
	 * only if axis range has changed
	 * @param axis
	 * @param lastR
	 * @param newR
	 */
	public abstract void axisRangeChanged(ValueAxis axis, Range lastR, Range newR);
}
