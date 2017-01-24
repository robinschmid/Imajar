package net.rs.lamsi.general.datamodel.image.data.multidimensional;

import java.io.Serializable;
import java.util.Vector;

import net.rs.lamsi.general.datamodel.image.data.twodimensional.DataPoint2D;
import net.rs.lamsi.general.datamodel.image.data.twodimensional.ScanLine2D;
import net.rs.lamsi.general.datamodel.image.interf.ImageDataset;

/**
 * basic dataset of multiple scan lines
 * @author Robin Schmid
 *
 */
public class DatasetMD implements ImageDataset, Serializable  {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    
    
	protected float maxXWidth = -1;
	protected int totalDPCount = -1, minDP=-1, maxDP=-1, avgDP=-1;
	protected ScanLineMD[] lines; 
	
	
	public DatasetMD(ScanLineMD[] listLines) { 
		lines = listLines;
	}
	public DatasetMD(Vector<ScanLineMD> scanLines) {
		lines = new ScanLineMD[scanLines.size()];
		for(int i=0; i<lines.length; i++)
			lines[i] = scanLines.get(i); 
	}
	
	/**
	 * reset to start conditions (e.g. after data has changed)
	 */
	public void reset() {
		maxXWidth = -1;
		totalDPCount = -1;
		minDP=-1; 
		maxDP=-1; 
		avgDP=-1;
	}
	
	
	@Override
	public int getLinesCount() {
		// TODO Auto-generated method stub
		return lines.length;
	}
	@Override
	public int getLineLength(int i) {
		return lines[i].getDPCount();
	}
	@Override
	public float getX(int line, int idp) {
		return lines[line].getX(idp);
	}
	@Override
	public double getI(int index, int line, int ix) {
		// TODO Auto-generated method stub
		return lines[line].getI(index, ix);
	}

	public ScanLineMD getLine(int line) {
		return lines[line];
	}
	public ScanLineMD[] getLines() {
		return lines;
	} 
	/**
	 * fires raw data changed event
	 * fires intensity processing changed event
	 * @param lines
	 */
	public void setLines(ScanLineMD[] lines) {
		this.lines = lines;
		reset();
		//fireIntensityProcessingChanged();
		//fireRawDataChangedEvent();
	}
	


	@Override
	public int getTotalDPCount() {
		if(totalDPCount == -1) {
			// calc
			totalDPCount = 0;
			for(ScanLineMD l : lines) {
				totalDPCount += l.getDPCount();
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
		for(ScanLineMD l : lines) {
			if(l.getDPCount()>maxDP) maxDP = l.getDPCount();
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
		for(int i=1; i<lines.length-1; i++) {
			ScanLineMD l = lines[i];
			if(l.getDPCount()<minDP) minDP = l.getDPCount();
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
		for(int i=1; i<lines.length-1; i++) {
			ScanLineMD l = lines[i];
			avg += l.getDPCount();
		}
		avgDP = Math.round((avg/(lines.length-2)));
		}
		return avgDP;
	}
	
	@Override
	public float getMaxXWidth() {
		if(maxXWidth==-1) {
			// calc min x
			maxXWidth = Float.NEGATIVE_INFINITY;
			for(ScanLineMD l : lines) {
				for(int i=0; i<l.getDPCount()-2; i++) {
					float dp1 = l.getX(i);
					float dp2 = l.getX(i+1);
					float width = Math.abs(dp2 -dp1);
					if(width>maxXWidth)
						maxXWidth = width;
				}
			}
		}
		return maxXWidth;
	} 
}
