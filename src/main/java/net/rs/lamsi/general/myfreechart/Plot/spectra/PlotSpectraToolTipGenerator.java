package net.rs.lamsi.general.myfreechart.Plot.spectra;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import net.rs.lamsi.massimager.Frames.Window;
import net.sf.mzmine.datamodel.Feature;
import net.sf.mzmine.datamodel.IsotopePattern;
import net.sf.mzmine.datamodel.PeakList;
import net.sf.mzmine.datamodel.PeakListRow;
import net.sf.mzmine.main.WindowMZMine;
import net.sf.mzmine.modules.visualization.spectra.datasets.IsotopesDataSet;
import net.sf.mzmine.modules.visualization.spectra.datasets.PeakListDataSet;

import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.data.xy.XYDataset;

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