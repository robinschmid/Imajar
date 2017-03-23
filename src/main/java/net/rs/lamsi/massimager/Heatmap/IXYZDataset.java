package net.rs.lamsi.massimager.Heatmap;
import org.jfree.data.xy.DefaultXYZDataset;


public class IXYZDataset extends DefaultXYZDataset{
	
	// CollsFirst bedeutet dass erst alle y werte für x=0 und danach für x=1 sind.
	protected boolean collsFirst;

	
	
	@Override
	public void addSeries(Comparable seriesKey, double[][] data) {
		super.addSeries(seriesKey, data); 
		// xcount in line and ycount = lines
		int x = 0; 
		for(int i=0; i<this.getItemCount(0); i++) {
			if(this.getXValue(0, i)==this.getXValue(0,0)) x++;
			else break;
		}
		int y = 0;
		for(int i=0; i<this.getItemCount(0); i++) {
			if(this.getYValue(0, i)==this.getYValue(0,0)) y++;
			else break;
		}
		// the greater value comes first (y>x => line after line)
		if(x<y) {
			// line after line
			collsFirst = false;
		}
		if(x>y) {
			// Colls first. erstmal xcount xWerte mit x=0
			collsFirst = true;
		}
	}

	public double getZMin() {
		double min = Double.POSITIVE_INFINITY;
		for(int i=0; i<this.getItemCount(0); i++) {
			if(this.getZValue(0, i)<min) min = this.getZValue(0, i);
		}
		return min;
	}
	public double getZMax() {
		double max = Double.NEGATIVE_INFINITY;
		for(int i=0; i<this.getItemCount(0); i++) {
			if(this.getZValue(0, i)>max) max = this.getZValue(0, i);
		}
		return max;
	}

}
