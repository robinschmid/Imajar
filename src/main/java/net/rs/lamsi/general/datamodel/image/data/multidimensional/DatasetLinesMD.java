package net.rs.lamsi.general.datamodel.image.data.multidimensional;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.ImageGroupMD;
import net.rs.lamsi.general.datamodel.image.data.interf.MDDataset;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.settings.image.sub.SettingsGeneralRotation;

/**
 * basic dataset of multiple scan lines
 * @author Robin Schmid
 *
 */
public class DatasetLinesMD extends MDDataset implements Serializable  {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    
    
	protected float maxXWidth = -1;
	protected int totalDPCount = -1, minDP=-1, maxDP=-1, avgDP=-1;
	protected ScanLineMD[] lines; 
	
	// last x of longest line ( left edge of the datapoint)
	protected float lastX =-1;
	
	// settings of rotation, reflection, imaging mode
	protected SettingsGeneralRotation settRot;
	
	
	public DatasetLinesMD(ScanLineMD[] listLines) { 
		settRot = new SettingsGeneralRotation();
		lines = listLines;
	}
	public DatasetLinesMD(List<ScanLineMD> scanLines) {
		settRot = new SettingsGeneralRotation();
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


	public void appendLines(ScanLineMD[] add) {
		if(add[0].getImageCount()==lines[0].getImageCount()) {
			int size = add.length + lines.length;
			
			ScanLineMD[] nl = new ScanLineMD[size];
			
			for(int i=0; i<lines.length; i++) {
				nl[i] = lines[i];
			}
			for(int i=0; i<add.length; i++)
				nl[lines.length+i] = add[i];

			lines = nl;
			
			reset();
			fireRawDataChangedEvent();
		}
	}
	
	
	public ImageGroupMD createImageGroup() {
		if(lines==null || lines.length==0)
			return null;
		
		ImageGroupMD group = new ImageGroupMD();
		for(int i= 0; i<getImageCount(); i++) {
			Image2D img = new Image2D(this, i);
			group.add(img);
		}
		return group;
	}
	public ImageGroupMD createImageGroup(File dataFile) {
		ImageGroupMD g = createImageGroup();
		// set data path and name
		if(dataFile!=null) {
			g.getSettings().setName((dataFile.getName()));
			g.getSettings().setPathData(dataFile.getAbsolutePath());
		}
		return g;
	}
	public ImageGroupMD createImageGroup(File dataFile, List<String> titles) {
		ImageGroupMD g = createImageGroup();
		// set data path and name
		if(dataFile!=null) {
			g.getSettings().setName((dataFile.getName()));
			g.getSettings().setPathData(dataFile.getAbsolutePath());
		}
		
		// set titles
		int i = 0;
		for(Collectable2D c : g.getImages()) {
			((Image2D) c).getSettings().getSettImage().setTitle(titles.get(i));
			i++;
		}
		return g;
	}

	public ImageGroupMD createImageGroup(String name) {
		ImageGroupMD g = createImageGroup();
		// set data path and name
		if(name!=null)
			g.getSettings().setName(name);
		return g;
	}
	
	private int getImageCount() { 
		return lines==null || lines.length==0? 0 : lines[0].getImageCount();
	}
	//##################################################
	// Multi dimensional
	@Override
	public boolean removeDimension(int i) {
		boolean removed = true;
		for(ScanLineMD l:lines) {
			if(!l.removeDimension(i))
				removed = false;
		}
		return removed;
	}
	@Override
	public int addDimension(List<Double[]> dim) {
		for(int i=0; i<lines.length; i++)
			lines[i].addDimension(dim.get(i));
		return lines[0].getImageCount()-1;
	} 

	@Override
	public boolean addDimension(Image2D img) {
		if(img.getData().hasSameDataDimensionsAs(this)) {
			// add dimension to all lines
			for(int i=0; i<lines.length; i++)
				lines[i].addDimension(img, i);
			
			// replace image data
			img.setData(this);
			img.setIndex(lines[0].getImageCount()-1);
			return true;
		}
		return false;
	}
	
	
	//##################################################
	// general ImageDataset
	@Override
	public int getLinesCount() {
		// TODO Auto-generated method stub
		return lines.length;
	}
	@Override
	public int getLineLength(int i) {
		if(i<0 || i>=getLinesCount())
			return -1;
		return lines[i].getDPCount();
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
	public double getI(int index, int line, int ix) {
		// TODO Auto-generated method stub
		return lines[line].getI(index, ix);
	}

	@Override
	public float getX(int line, int idp) {
		if(hasOnlyOneXColumn())
			return lines[0].getX(idp);
		else return lines[line].getX(idp);
	}
	
	@Override
	public float getRightEdgeX(int l) {
		if(hasOnlyOneXColumn())
			return lines[0].getEndX();
		else return lines[l].getEndX();
	} 
	@Override
	public float getLastXLine(int line) {
		return getX(line, getLineLength(line)-1);
	}
	@Override
	public float getLastX() {
		if(lastX==-1) {
			if(hasOnlyOneXColumn())
				lastX = getLastXLine(0);
			else {
				for(int i=0; i<lines.length; i++)
					if(getLastXLine(i)>lastX)
						lastX = getLastXLine(i);
			}
		}
		return lastX;
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
	public float getMaxXDPWidth() {
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
				// stop if only one x col
				if(hasOnlyOneXColumn())
					break;
			}
		}
		return maxXWidth;
	}
	@Override
	public boolean hasXData() { 
		return lines!=null && lines[0]!=null && lines[0].getX()!=null;
	}
	/**
	 * 
	 * @return true if there is only one x column false if there is no x or more than one
	 */
	public boolean hasOnlyOneXColumn() { 
		if(hasXData()){
			return !(lines.length>=2 && lines[1]!=null && lines[1].hasXData() && !lines[1].getX().equals(lines[0].getX()));
		}
		else return false;
	}
	@Override
	public int size() {
		return lines!=null && lines.length>0? lines[0].getImageCount() : 0;
	}
	
}
