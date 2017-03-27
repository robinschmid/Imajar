package net.rs.lamsi.massimager.Heatmap;
import org.jfree.data.xy.DefaultXYZDataset;


public class IXYZDataset extends DefaultXYZDataset{
	
	@Override
	public void addSeries(Comparable seriesKey, double[][] data) {
		super.addSeries(seriesKey, data); 
	}

	public double getZMin() {
		return getZMin(0);
	}
	public double getZMax() {
		return getZMax(0);
	}

	
	public double getZMin(int series) {
		double min = Double.POSITIVE_INFINITY;
		int size = this.getItemCount(series);
		for(int i=0; i<size; i++) {
			if(this.getZValue(series, i)<min) min = this.getZValue(series, i);
		}
		return min;
	}

	public double getZMax(int series) {
		double max = Double.NEGATIVE_INFINITY;
		int size = this.getItemCount(series);
		for(int i=0; i<size; i++) {
			if(this.getZValue(series, i)>max) max = this.getZValue(series, i);
		}
		return max;
	}
		
}
