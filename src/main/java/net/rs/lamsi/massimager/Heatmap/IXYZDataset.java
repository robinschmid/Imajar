package net.rs.lamsi.massimager.Heatmap;
import org.jfree.data.xy.DefaultXYZDataset;


public class IXYZDataset extends DefaultXYZDataset{
	
	// CollsFirst bedeutet dass erst alle y werte für x=0 und danach für x=1 sind.
	protected boolean collsFirst;
	protected int xcount, ycount;

	
	
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
			ycount = y;
			xcount = this.getItemCount(0)/ycount;
		}
		if(x>y) {
			// Colls first. erstmal xcount xWerte mit x=0
			collsFirst = true;
			xcount = x;
			ycount = this.getItemCount(0)/xcount;
		}
	}

	// BlockWidth as maximum of distance
	public double getBlockWidth() {
		if(collsFirst) {
			double max = Double.NEGATIVE_INFINITY;
			for(int i=0; i<this.getItemCount(0)-xcount; i++) {
				// jump to next line with xcount
				double width = Math.abs(this.getXValue(0, i+xcount) - this.getXValue(0, i));
				if(width>max) {
					max = width;
				}
			}
			return max;
		}
		else {
			// line after line
			double max = Double.NEGATIVE_INFINITY;
			for(int i=0; i<this.getItemCount(0)-1; i++) {
				double width = Math.abs(this.getXValue(0, i+1) - this.getXValue(0, i));
				if(width>max) {
					max = width; 
				}
			}
			return max;
		}
	}
	// Blockheight as maxmum of y-distace
	public double getBlockHeight() {
		if(collsFirst) {
			double max = Double.NEGATIVE_INFINITY;
			for(int i=0; i<xcount-1; i++) {
				double height = Math.abs(this.getYValue(0, i+1) - this.getYValue(0, i));
				if(height>max) {
					max = height;
				}
			}
			return max;
		}
		else {
			double max = Double.MIN_VALUE;
			for(int i=0; i<this.getItemCount(0)-xcount; i++) {
				double height = Math.abs(this.getYValue(0, i+xcount) - this.getYValue(0, i));
				if(height>max) {
					max = height;
				}
			}
			return max;
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
