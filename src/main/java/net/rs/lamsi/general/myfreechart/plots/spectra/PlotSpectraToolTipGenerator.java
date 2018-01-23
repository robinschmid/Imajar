package net.rs.lamsi.general.myfreechart.plots.spectra;

import java.text.NumberFormat;

import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.data.xy.XYDataset;

import net.rs.lamsi.massimager.Frames.Window;

class PlotSpectraToolTipGenerator implements XYToolTipGenerator { 

	/**
	 * @see org.jfree.chart.labels.XYToolTipGenerator#generateToolTip(org.jfree.data.xy.XYDataset,
	 *      int, int)
	 */
	public String generateToolTip(XYDataset dataset, int series, int item) {
		
		NumberFormat mzFormat = Window.getWindow().getSettings().getSetGeneralValueFormatting().getMZFormat();
		NumberFormat intensityFormat = Window.getWindow().getSettings().getSetGeneralValueFormatting().getIntensityFormat();

		double intValue = dataset.getYValue(series, item);
		double mzValue = dataset.getXValue(series, item);

		String tooltip = "m/z: " + mzFormat.format(mzValue) + "\nIntensity: "
				+ intensityFormat.format(intValue);

		return tooltip;

	}
}