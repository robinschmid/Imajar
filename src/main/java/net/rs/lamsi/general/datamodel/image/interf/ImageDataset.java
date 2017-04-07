package net.rs.lamsi.general.datamodel.image.interf;

import java.util.Vector;

import net.rs.lamsi.general.datamodel.image.listener.RawDataChangedListener;

/**
 * Basic methods of a imaging data set
 * @author Robin Schmid
 *
 */
public abstract class ImageDataset {
	//############################################################
	// listener
	protected Vector<RawDataChangedListener> rawDataChangedListener;

	/**
	 * counts the lines in a data set
	 * @return
	 */
	public abstract int getLinesCount();

	/**
	 * scan points (length) of line i
	 * @param i
	 * @return
	 */
	public abstract int getLineLength(int i);

	/**
	 * returns the raw x value (time/data point number/...)
	 * @param line index of the line
	 * @param dpi index of the data point
	 * @return
	 */
	public abstract float getX(int line, int dpi);

	/**
	 * returns the raw intensity (I) value of a data point in a line
	 * @param index index of image / dimension
	 * @param line index of the line
	 * @param dpi index of the data point
	 * @return
	 */
	public abstract double getI(int index, int line, int dpi);

	/**
	 * raw distance between two data points (dTime or delta data point number)
	 * usually only interesting for delta Time
	 * @return maximum x distance between two data points in a line
	 */
	public abstract float getMaxXDPWidth();

	/**
	 * 
	 * @return total count of data points
	 */
	public abstract int getTotalDPCount();

	/**
	 * The maximum datapoints of the longest line
	 * @return
	 */
	public abstract int getMaxDP();

	/**
	 * The maximum datapoints of the longest line
	 * @return
	 */
	public abstract int getMinDP();

	/**
	 * The average data points of all lines 
	 * @return
	 */
	public abstract int getAvgDP();
	

	/**
	 * right edge of x data
	 * right edge of last data point (for later width calc
	 * @param l
	 * @return
	 */
	public abstract float getRightEdgeX(int l);
	
	/**
	 * width / right edge of the image
	 * @return
	 */
	public float getRightEdgeX() {
		float max = 0;
		for(int i=0; i<getLinesCount(); i++) {
			float n = getRightEdgeX(i);
			if(max<n)
				max = n;
		}
		return max;
	}
	
	/**
	 * Width of the image
	 * @return
	 */
	public float getWidthX() {
		return getRightEdgeX();
	}
	

	/**
	 * data is the same or has the same dimensions
	 * @param data
	 * @return
	 */
	public boolean hasSameDataDimensionsAs(ImageDataset data){
		if(data.equals(this)) return true;
		if(data.getTotalDPCount()!=this.getTotalDPCount()) return false;
		for(int i=0; i<data.getLinesCount(); i++) {
			if(data.getLineLength(i)!=this.getLineLength(i)) 
				return false;
		}
		
		return true;
	}


	//########################################################################
	// listener
	/**
	 * raw data changes by:
	 * direct imaging, 
	 */
	public void fireRawDataChangedEvent() {
		if(rawDataChangedListener!=null) {
			for(RawDataChangedListener l : rawDataChangedListener)
				l.rawDataChangedEvent(this);
		}
	}
	/**
	 * raw data changes by:
	 * direct imaging,
	 * @param listener
	 */
	public void addRawDataChangedListener(RawDataChangedListener listener) {
		if(rawDataChangedListener==null) rawDataChangedListener = new Vector<RawDataChangedListener>();
		rawDataChangedListener.add(listener);
	}
	public void removeRawDataChangedListener(RawDataChangedListener list) {
		if(rawDataChangedListener!=null)
			rawDataChangedListener.remove(list);
	}
	public void cleatRawDataChangedListeners() {
		if(rawDataChangedListener!=null)
			rawDataChangedListener.clear();
	}

	/**
	 * last x of line (left edge of last data point
	 * lineLength-1
	 * @param line
	 * @return
	 */
	public abstract float getLastXLine(int line);

	/**
	 * last x of the longest line (left edge of last data point
	 * lineLength-1
	 * @param line
	 * @return
	 */
	public abstract float getLastX();

}
