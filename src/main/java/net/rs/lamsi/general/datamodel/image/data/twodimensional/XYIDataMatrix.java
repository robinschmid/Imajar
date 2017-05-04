package net.rs.lamsi.general.datamodel.image.data.twodimensional;

public class XYIDataMatrix {

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
}