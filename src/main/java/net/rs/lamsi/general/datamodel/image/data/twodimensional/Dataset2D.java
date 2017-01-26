package net.rs.lamsi.general.datamodel.image.data.twodimensional;

import java.io.Serializable;
import java.util.Vector;

import net.rs.lamsi.general.datamodel.image.interf.ImageDataset;
import net.rs.lamsi.massimager.Settings.image.SettingsImage;
import net.rs.lamsi.massimager.Settings.image.SettingsPaintScale;

/**
 * basic dataset of multiple scan lines
 * @author Robin Schmid
 *
 */
public class Dataset2D extends ImageDataset  implements Serializable  {
	// do not change the version!
    private static final long serialVersionUID = 1L;

	protected float maxXWidth = -1;
	protected int totalDPCount = -1, minDP=-1, maxDP=-1, avgDP=-1;
	protected ScanLine2D[] lines; 
	
	
	public Dataset2D(ScanLine2D[] listLines) { 
		lines = listLines;
	}
	public Dataset2D(Vector<ScanLine2D> scanLines) {
		lines = new ScanLine2D[scanLines.size()];
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
		return lines[line].getPoint(idp).getX();
	}
	@Override
	public double getI(int index, int line, int idp) {
		// TODO Auto-generated method stub
		return lines[line].getPoint(idp).getI();
	}

	public DataPoint2D getDP(int line, int dp) {
		return lines[line].getPoint(dp);
	}
	public ScanLine2D getLine(int line) {
		return lines[line];
	}
	public Vector<ScanLine2D> getLinesAsVector() {
		Vector<ScanLine2D> vl = new Vector<ScanLine2D>();
		for(ScanLine2D l : lines) {
			vl.add(l);
		}
		return vl;
	}
	public ScanLine2D[] getLines() {
		return lines;
	} 
	/**
	 * fires raw data changed event
	 * fires intensity processing changed event
	 * @param lines
	 */
	public void setLines(ScanLine2D[] lines) {
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
			for(ScanLine2D l : lines) {
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
		for(ScanLine2D l : lines) {
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
			ScanLine2D l = lines[i];
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
			ScanLine2D l = lines[i];
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
			for(ScanLine2D l : lines) {
				for(int i=0; i<l.getData().length-2; i++) {
					DataPoint2D dp1 = l.getPoint(i);
					DataPoint2D dp2 = l.getPoint(i+1);
					float width = Math.abs(dp2.getX() -dp1.getX());
					if(width>maxXWidth)
						maxXWidth = width;
				}
			}
		}
		return maxXWidth;
	} 
}
