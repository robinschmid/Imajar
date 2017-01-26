package net.rs.lamsi.general.datamodel.image.data.twodimensional;

import java.io.Serializable;
import java.util.Vector;

import net.rs.lamsi.general.datamodel.image.interf.ImageDataset;
import net.rs.lamsi.massimager.Settings.image.SettingsImage;
import net.rs.lamsi.massimager.Settings.image.SettingsImageContinousSplit;
import net.rs.lamsi.massimager.Settings.image.SettingsPaintScale;
import net.rs.lamsi.massimager.Settings.image.SettingsImage.XUNIT;

/**
 * basic dataset of multiple scan lines
 * @author Robin Schmid
 *
 */
public class DatasetContinuous2D extends ImageDataset  implements Serializable  {
	// do not change the version!
    private static final long serialVersionUID = 1L;
	
	protected SettingsImageContinousSplit sett;

	protected ScanLine2D line; 
	protected int[] lineStart;
	
	protected float maxXWidth = -1;
	protected int minDP=-1, maxDP=-1, avgDP=-1;
	
	public DatasetContinuous2D(ScanLine2D line) { 
		this(line, new SettingsImageContinousSplit(aproxLineLength(line)));
	}
	public DatasetContinuous2D(ScanLine2D line, SettingsImageContinousSplit sett) {  
		this.line = line; 
		setSplitSettings(sett);
	}
	
	
	public void setSplitSettings(SettingsImageContinousSplit sett) {
		this.sett = sett;
		reset();
		// split data TODO
		if(sett.getSplitMode()==XUNIT.s) {
			lineStart = new int[getLinesCount()];
			int start = 0;
			float startTime = line.getPoint(0).getX();
			for(int i=0; i<line.getDPCount(); i++) {
				// first line
				if(start==0 && line.getPoint(i).getX()-startTime>=sett.getStartX()) {
					lineStart[start] = i;
					start++;
				}
				// next lines
				else if(line.getPoint(i).getX()-startTime-sett.getStartX()>=sett.getSplitAfterX()*(start)) {
					lineStart[start] = i;
					start++;
				}
			}
		}
		else {
			lineStart = null;
		}
	}

	/**
	 * approximate line length
	 * @param line
	 * @return
	 */
	private static int aproxLineLength(ScanLine2D line) {
		int total = line.getDPCount();
		int best = 0;
		// i lines
		// break if line length<line count
		for(int i=5; i<=150 && total/i<i; i++) {
			// save as best
			if(total%i==0) {
				best = i;
			}
		}
		return best!=0? total/best : (int)Math.sqrt(total);
	}
	/**
	 * reset to start conditions (e.g. after data has changed)
	 */
	public void reset() {
		maxXWidth = -1;
		minDP=-1; 
		maxDP=-1; 
		avgDP=-1;
	}

	@Override
	public int getLinesCount() { 
		if(sett.getSplitMode()==XUNIT.s)
			return (int)Math.floor((line.getXWidth()-sett.getStartX())/sett.getSplitAfterX());
		else return (int)(line.getDPCount()-sett.getStartX())/sett.getSplitAfterDP();
	}

	@Override
	public int getLineLength(int i) {
		if(sett.getSplitMode()==XUNIT.s)
			return i==getLinesCount()-1? getTotalDPCount()-lineStart[i] : lineStart[i+1]-lineStart[i];
		else return i==getLinesCount()-1? getTotalDPCount()-(int)sett.getStartX()-(sett.getSplitAfterDP()*i) : sett.getSplitAfterDP();
	}

	@Override
	public float getX(int line, int dpi) {
		return this.line.getPoint(getIndex(line, dpi)).getX()-this.line.getPoint(getIndex(line, 0)).getX();
	}

	@Override
	public double getI(int index, int line, int dpi) {
		return this.line.getPoint(getIndex(line, dpi)).getI();
	}
	
	/**
	 * calculates the data point in the continuous dimension
	 * @param line
	 * @param dpi
	 * @return
	 */
	public int getIndex(int line, int dpi) {
		if(sett.getSplitMode()==XUNIT.s){
			return dpi+lineStart[line];
		}
		else {
			return line*sett.getSplitAfterDP()+dpi+(int)sett.getStartX();
		}
	}


	@Override
	public int getTotalDPCount() { 
		return line.getDPCount();
	}
	/**
	 * The maximum datapoints of the longest line
	 * @return
	 */
	public int getMaxDP() {
		if(sett.getSplitMode()==XUNIT.s){
			if(maxDP==-1) {
				maxDP = Integer.MIN_VALUE;
				for(int i=0; i<lineStart.length; i++) {
					int l = getLineLength(i);
					if(l>maxDP) maxDP = l;
				}
				}
			return maxDP;
		}
		else return sett.getSplitAfterDP();
	}
	/**
	 * The maximum datapoints of the longest line
	 * does not uses the first and last line! for calculation
	 * @return
	 */
	public int getMinDP() {
		if(sett.getSplitMode()==XUNIT.s){
			if(minDP==-1) {
				minDP = Integer.MAX_VALUE;
				for(int i=0; i<lineStart.length; i++) {
					int l = getLineLength(i);
					if(l<minDP) minDP = l;
				}
				}
				return minDP;
		}
		else return getLineLength(getLinesCount()-1);
	}
	/**
	 * The average datapoints of all lines
	 * does not uses the first and last line! for calculation
	 * @return
	 */
	public int getAvgDP() {
		if(sett.getSplitMode()==XUNIT.s){
			if(avgDP==-1) {
			int avg = 0;
			for(int i=1; i<lineStart.length-1; i++) {
				int l = getLineLength(i);
				avg += l;
			}
			avgDP = Math.round((avg/(getLinesCount()-2)));
			}
			return avgDP;
		}
		else return sett.getSplitAfterDP();
	}
	
	@Override
	public float getMaxXWidth() {
			if(maxXWidth==-1) {
				// calc min x
				maxXWidth = Float.NEGATIVE_INFINITY; 
					for(int i=0; i<line.getData().length-2; i++) {
						DataPoint2D dp1 = line.getPoint(i);
						DataPoint2D dp2 = line.getPoint(i+1);
						float width = Math.abs(dp2.getX() -dp1.getX());
						if(width>maxXWidth)
							maxXWidth = width;
					} 
			}
			return maxXWidth; 
	} 
}
