package net.rs.lamsi.general.datamodel.image.data;

/*
 * Class for HeatmapFactory (JFreeChart)
 */
public class XYIData2D {
	
	protected double[] x,y,i;

	public XYIData2D(double[] x, double[] y, double[] i) {
		super();
		this.x = x;
		this.y = y;
		this.i = i;
	}


	@Override
	public String toString() {
		return "["+String.valueOf(x)+"; "+String.valueOf(y)+"; "+String.valueOf(i)+"]";
	}
	public double[] getX() {
		return x;
	}

	public void setX(double[] x) {
		this.x = x;
	}

	public double[] getY() {
		return y;
	}

	public void setY(double[] y) {
		this.y = y;
	}

	public double[] getI() {
		return i;
	}

	public void setI(double[] i) {
		this.i = i;
	}

}
