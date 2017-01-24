package net.rs.lamsi.general.datamodel.image.data.multidimensional;

import java.io.Serializable;
import java.util.Vector;

/**
 * A scan line of multi dimensional data:
 * x y1 y2 y3 ... (y=intensity)
 * @author r_schm33
 *
 */
public class ScanLineMD  implements Serializable  {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    //
	protected float[] x;
	protected Vector<Double[]> intensity;
	
	public ScanLineMD(Vector<Float> x, Vector<Double[]> intensity) {
		super();
		setX(x);
		this.intensity = intensity;
	}
	public ScanLineMD(float[] x, Vector<Double[]> intensity) {
		super();
		this.x = x;
		this.intensity = intensity;
	}
	public ScanLineMD(float[] x, Double[] i) {
		super();
		this.x = x;
		this.intensity = new Vector<Double[]>();
		intensity.add(i);
	}
	public ScanLineMD() {
		this.intensity = new Vector<Double[]>();
	}
	/**
	 * adds an intensity dimension (image)
	 * @param i
	 */
	public void addDimension(Double[] i) {
		intensity.add(i);
	}
	/**
	 * adds an intensity dimension (image)
	 * @param i
	 */
	public void addDimension(Vector<Double> i) {
		addDimension(i.toArray(new Double[i.size()]));
	}
	
	public float[] getX() {
		return x;
	}
	public void setX(float[] x) {
		this.x = x;
	}
	public void setX(Vector<Float> lx) {
		x = new float[lx.size()];
		for(int i=0; i<x.length; i++)
			x[i] = lx.get(i);
	}
	
	/**
	 * 
	 * @param ix
	 * @return
	 */
	public float getX(int ix) { 
		return x!=null? this.x[ix] : ix;
	}
	
	
	/**
	 * 
	 * @param i
	 * @param ix
	 * @return intensity of image(dimension) i and data point ix
	 */
	public double getI(int i, int ix) {
		return intensity.get(i)[ix];
	}
	
	/**
	 * total data points count
	 * @return
	 */
	public int getDPCount() {
		return intensity.firstElement().length;
	}
	/**
	 * raw width of dp by dividing maxX by elements count
	 * @return
	 */
	public float getWidthDP() { 
		// width is defined by x of last dp devided by all datapoints in front of the last
		return (getXWidth())/(getDPCount()-1);
	} 
	
	@Override
	public String toString() {
		return (x==null? "" : x.toString()) +intensity.toString();
	}
	/**
	 * the width between start and end of X values
	 * @return
	 */
	public float getXWidth() { 
		return getX(getDPCount()-1)-getX(0);
	}
	/**
	 * 
	 * @return the number of dimensions (images)
	 */
	public int getImageCount() {
		return intensity.size();
	}
}
