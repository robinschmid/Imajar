package net.rs.lamsi.general.datamodel.image.data.twodimensional;

public class XYIDataMatrix {

	// NaN  at line end
	protected Double[][] i;
	protected Float[][] x,y;

	public XYIDataMatrix(Float[][] x, Float[][] y, Double[][] i) {
		super();
		this.x = x;
		this.y = y;
		this.i = i;
	}


	@Override
	public String toString() {
		return "["+String.valueOf(x)+"; "+String.valueOf(y)+"; "+String.valueOf(i)+"]";
	}
	public Double[][] getI() {
		return i;
	}
	public Float[][] getX() {
		return x;
	}
	public Float[][] getY() {
		return y;
	}
	public void setI(Double[][] i) {
		this.i = i;
	}
	public void setX(Float[][] x) {
		this.x = x;
	}
	public void setY(Float[][] y) {
		this.y = y;
	}
	
	public int getMinimumLineLength() {
		int min = Integer.MAX_VALUE;
		for(Double[] d : i) {
			int length = lineLength(d);
			if(length<min) min = length;
		}
		return min;
	}
	public int getMaximumLineLength() {
		int max = 0;
		for(Double[] d : i) {
			int length = lineLength(d);
			if(length>max) max = length;
		}
		return max;
	}
	public int getAverageLineLength() {
		int max = 0;
		for(Double[] d : i) {
			int length = lineLength(d);
			max += length;
		}
		max = max / i.length;
		return max;
	}
	public int lineLength(int line) {
		if(line>=i.length)
			return 0;
		return lineLength(i[line]);
	}
	private int lineLength(Double[] l) {
		for(int i=l.length-1; i>=0; i--) {
			if(!Double.isNaN(l[i]))
				return i+1;
		}
		return 0;
	}


	public double getMinI() {
		double min = Double.MAX_VALUE;
		for(Double[] d : i) {
		for(int f=0; f<d.length; f++) {
			if(!Double.isNaN(d[f]) && d[f]<min)
				min = d[f];
		}
		}
		
		return min;
	}

	public double getMaxI() {
		double max = Double.NEGATIVE_INFINITY;
		for(Double[] d : i) {
		for(int f=0; f<d.length; f++) {
			if(!Double.isNaN(d[f]) && d[f]>max)
				max = d[f];
		}
		}
		
		return max;
	}
}