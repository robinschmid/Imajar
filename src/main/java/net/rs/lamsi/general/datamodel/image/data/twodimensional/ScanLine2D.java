package net.rs.lamsi.general.datamodel.image.data.twodimensional;

import java.io.Serializable;
import java.util.Vector;

public class ScanLine2D  implements Serializable  {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    //

	protected DataPoint2D[] data; 
	
	public ScanLine2D(DataPoint2D[] data) {
		super();
		this.data = data;
	}
	public ScanLine2D(Vector<DataPoint2D> dpList) {
		data = new DataPoint2D[dpList.size()];
		for(int i = 0; i<dpList.size(); i++) {
			data[i] = dpList.get(i);
		}
	}

	public DataPoint2D[] getData() {
		return data;
	} 
	public DataPoint2D getPoint(int i) {
		return data[i];
	}

	public void setData(DataPoint2D[] data) {
		this.data = data;
	}
	public int getDPCount() {
		return data.length;
	}
	/**
	 * raw width of dp by dividing maxX by elements count
	 * @return
	 */
	public float getWidthDP() { 
		// width is defined by x of last dp devided by all datapoints in front of the last
		return getXWidth()/(data.length-1);
	} 
	
	@Override
	public String toString() {
		return data.toString();
	}
	/**
	 * the width between start and end of X values
	 * @return
	 */
	public float getXWidth() { 
		return data[data.length-1].getX()-data[0].getX();
	}
}
