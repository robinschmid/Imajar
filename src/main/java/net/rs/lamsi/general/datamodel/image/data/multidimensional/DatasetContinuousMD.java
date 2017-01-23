package net.rs.lamsi.general.datamodel.image.data.multidimensional;

import java.util.Vector;

import net.rs.lamsi.general.datamodel.image.interf.ImageDataset;
import net.rs.lamsi.massimager.Settings.SettingsImage;
import net.rs.lamsi.massimager.Settings.SettingsPaintScale;
import net.rs.lamsi.massimager.Settings.SettingsImage.XUNIT;
import net.rs.lamsi.massimager.Settings.image.SettingsImageContinousSplit;

/**
 * basic dataset of multiple scan lines
 * @author Robin Schmid
 *
 */
public class DatasetContinuousMD implements ImageDataset {
	
	protected SettingsImageContinousSplit sett;

	protected ScanLineMD line; 
	protected int[] lineStart;
	
	protected float maxXWidth = -1;
	protected int minDP=-1, maxDP=-1, avgDP=-1;
	
	public DatasetContinuousMD(ScanLineMD line) { 
		this(line, new SettingsImageContinousSplit(aproxLineLength(line)));
	}
	public DatasetContinuousMD(ScanLineMD line, SettingsImageContinousSplit sett) {  
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
			float startTime = line.getX(0);
			for(int i=0; i<line.getDPCount(); i++) {
				// first line
				if(start==0 && line.getX(i)-startTime>=sett.getStartX()) {
					lineStart[start] = i;
					start++;
				}
				// next lines
				else if(line.getX(i)-startTime-sett.getStartX()>=sett.getSplitAfterX()*(start)) {
					lineStart[start] = i;
					start++;
				}
			}
		}
		else {
			lineStart = null;
		}
	}
	public SettingsImageContinousSplit getSplitSettings() {
		return sett;
	}

	/**
	 * approximate line length
	 * @param line
	 * @return
	 */
	private static int aproxLineLength(ScanLineMD line) {
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
		return this.line.getX(getIndex(line, dpi))-this.line.getX(getIndex(line, 0));
	}

	@Override
	public double getI(int index, int line, int dpi) {
		return this.line.getI(index, getIndex(line, dpi));
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
					for(int i=0; i<line.getDPCount()-2; i++) {
						float dp1 = line.getX(i);
						float dp2 = line.getX(i+1);
						float width = Math.abs(dp2 -dp1);
						if(width>maxXWidth)
							maxXWidth = width;
					} 
			}
			return maxXWidth; 
	} 
}
