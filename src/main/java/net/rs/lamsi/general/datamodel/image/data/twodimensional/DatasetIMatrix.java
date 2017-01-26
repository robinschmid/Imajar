package net.rs.lamsi.general.datamodel.image.data.twodimensional;

import java.io.Serializable;

import net.rs.lamsi.general.datamodel.image.interf.ImageDataset;

/**
 * basic dataset of multiple scan lines
 * @author Robin Schmid
 *
 */
public class DatasetIMatrix  extends ImageDataset  implements Serializable  {
	// do not change the version!
    private static final long serialVersionUID = 1L;

	// data[lines][dp]
	protected double[][] data;
	protected int totalDPCount = -1, minDP=-1, maxDP=-1, avgDP=-1;
	protected double minI = -1;
	
	public DatasetIMatrix(double[][] data) {
		super();
		this.data = data;
	}
	
	/**
	 * reset to start conditions (e.g. after data has changed)
	 */
	public void reset() {
		totalDPCount = -1;
		minDP=-1; 
		maxDP=-1; 
		avgDP=-1;
		minI = -1;
	}

	@Override
	public int getLinesCount() {
		return data.length;
	}

	@Override
	public int getLineLength(int i) {
		return data[i].length;
	}

	@Override
	public float getX(int line, int dpi) {
		return dpi;
	}

	@Override
	public double getI(int index, int line, int dpi) {
		return data[line].length<dpi? data[line][dpi] : getMinI();
	}


	private double getMinI() { 
		if(minI==-1) {
			minI = Double.MAX_VALUE;
			for(int i=0; i<data.length; i++) {
				for(int x=0; x<data[i].length; x++) {
				if(data[i][x]<minI) minI = data[i][x];
			}
			}
		}
		return minI;
	}

	@Override
	public int getTotalDPCount() {
		if(totalDPCount == -1) {
			// calc
			totalDPCount = 0;
			for(int i=0; i<data.length; i++) {
				totalDPCount += data[i].length;
			}
		}
		return totalDPCount;
	}
	/**
	 * The maximum datapoints of the longest line
	 * @return
	 */
	public int getMaxDP() {
		if(maxDP==-1) {
		maxDP = Integer.MIN_VALUE;
		for(int i=0; i<data.length; i++) {
			if(data[i].length>maxDP) maxDP = data[i].length;
		}
		}
		return maxDP;
	}
	/**
	 * The maximum datapoints of the longest line
	 * does not uses the first and last line! for calculation
	 * @return
	 */
	public int getMinDP() {
		if(minDP==-1) {
		minDP = Integer.MAX_VALUE;
		for(int i=0; i<data.length; i++) {
			if(data[i].length<minDP) minDP = data[i].length;
		}
		}
		return minDP;
	}
	/**
	 * The average datapoints of all lines
	 * does not uses the first and last line! for calculation
	 * @return
	 */
	public int getAvgDP() {
		if(avgDP==-1) {
		int avg = 0;
		for(int i=0; i<data.length; i++) {
			avg += data[i].length;
		}
		avgDP = Math.round((avg/(data.length-2)));
		}
		return avgDP;
	}
	
	@Override
	public float getMaxXWidth() {
		return 1;
	} 
}
