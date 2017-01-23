package net.rs.lamsi.general.datamodel.image.data.twodimensional;

import java.io.Serializable;

public class DataPoint2D  implements Serializable {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    //

	protected float x;
	protected double i;

	public DataPoint2D(float x, double i) {
		super();
		this.x = x;
		this.i = i;
	}

	public DataPoint2D(double x, double i) {
		this((float)x, (double)i);
	}

	@Override
	public String toString() {
		return "["+String.valueOf(x)+"; "+String.valueOf(i)+"]";
	}
	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public double getI() {
		return i;
	}
	public void setI(double i) {
		this.i = i;
	}

}
