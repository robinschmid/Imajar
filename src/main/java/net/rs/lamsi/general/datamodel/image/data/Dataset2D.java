package net.rs.lamsi.general.datamodel.image.data;

import java.util.Vector;

import net.rs.lamsi.general.datamodel.image.interf.ImageDataset;
import net.rs.lamsi.massimager.Settings.SettingsImage;
import net.rs.lamsi.massimager.Settings.SettingsPaintScale;

/**
 * basic dataset of multiple scan lines
 * @author Robin Schmid
 *
 */
public class Dataset2D implements ImageDataset {

	protected float maxXWidth = -1;
	protected int totalDPCount = -1;
	protected ScanLine[] lines; 
	
	public Dataset2D(ScanLine[] listLines) { 
		lines = listLines;
	}
	public Dataset2D(Vector<ScanLine> scanLines) {
		lines = new ScanLine[scanLines.size()];
		for(int i=0; i<lines.length; i++)
			lines[i] = scanLines.get(i); 
	}
	
	/**
	 * reset to start conditions (e.g. after data has changed)
	 */
	public void reset() {
		maxXWidth = -1;
		totalDPCount = -1;
	}
	
	
	@Override
	public int getLinesCount() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int getLineLength(int i) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public float getX(int line, int dpi) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public double getI(int line, int dpi) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int getTotalDPCount() {
		if(totalDPCount == -1) {
			// calc
			totalDPCount = 0;
			for(ScanLine l : lines) {
				totalDPCount += l.getDPCount();
			}
		}
		return totalDPCount;
	}
	


	public DataPoint2D getDP(int line, int dp) {
		return lines[line].getPoint(dp);
	}
	public ScanLine getLine(int line) {
		return lines[line];
	}
	public Vector<ScanLine> getLinesAsVector() {
		Vector<ScanLine> vl = new Vector<ScanLine>();
		for(ScanLine l : lines) {
			vl.add(l);
		}
		return vl;
	}
	public ScanLine[] getLines() {
		return lines;
	} 
	/**
	 * fires raw data changed event
	 * fires intensity processing changed event
	 * @param lines
	 */
	public void setLines(ScanLine[] lines) {
		this.lines = lines;
		reset();
		//fireIntensityProcessingChanged();
		//fireRawDataChangedEvent();
	}
	


	/**
	 * The maximum datapoints of the longest line
	 * @return
	 */
	public int getMaxDP() {
		int max = Integer.MIN_VALUE;
		for(ScanLine l : lines) {
			if(l.getDPCount()>max) max = l.getDPCount();
		}
		return max;
	}
	/**
	 * The maximum datapoints of the longest line
	 * does not uses the first and last line! for calculation
	 * @return
	 */
	public int getMinDP() {
		int min = Integer.MAX_VALUE;
		for(int i=1; i<lines.length-1; i++) {
			ScanLine l = lines[i];
			if(l.getDPCount()<min) min = l.getDPCount();
		}
		return min;
	}
	/**
	 * The average datapoints of all lines
	 * does not uses the first and last line! for calculation
	 * @return
	 */
	public int getAvgDP() {
		int avg = 0;
		for(int i=1; i<lines.length-1; i++) {
			ScanLine l = lines[i];
			avg += l.getDPCount();
		}
		return Math.round((avg/(lines.length-2)));
	}
	
	@Override
	public float getMaxXWidth() {
		if(maxXWidth==-1) {
			// calc min x
			maxXWidth = Float.NEGATIVE_INFINITY;
			for(ScanLine l : lines) {
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
