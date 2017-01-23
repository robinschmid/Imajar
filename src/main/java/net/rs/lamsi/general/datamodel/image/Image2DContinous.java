package net.rs.lamsi.general.datamodel.image;

import java.util.Vector;

import net.rs.lamsi.general.datamodel.image.data.DataPoint2D;
import net.rs.lamsi.general.datamodel.image.data.ScanLine;
import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.massimager.Settings.SettingsImage;
import net.rs.lamsi.massimager.Settings.SettingsImage.XUNIT;
import net.rs.lamsi.massimager.Settings.SettingsPaintScale;
import net.rs.lamsi.massimager.Settings.image.SettingsImageContinousSplit;
import net.rs.lamsi.utils.mywriterreader.BinaryWriterReader;

/**
 * represents an image with only one data line
 * @author vukmir69
 *
 */
public class Image2DContinous extends Image2D {

	private static final long serialVersionUID = 1L;
	
	// raw data is in one line
	protected ScanLine dataLine;
	// settings for splitting this line
	protected SettingsImageContinousSplit settContinousSplit;
	
 
	public Image2DContinous(ScanLine line) {
		super(); 
		settContinousSplit = new SettingsImageContinousSplit();
		dataLine = line;
		settContinousSplit.setSplitAfterDP(line.getDPCount());
		resplitData();
	}

	public Image2DContinous(ScanLine line, SettingsPaintScale settPaintScale, SettingsImage setImage) {
		super(settPaintScale, setImage);
		settContinousSplit = new SettingsImageContinousSplit();
		dataLine = line;
		resplitData();
	}
	
	
	/**
	 * uses the settings for resplitting the one line data to multiple lines
	 * discards the last line if it is incomplete in length ???TODO
	 */
	public void resplitData() {
			// calculate line count
			int linecount = 0;
			// start is index or time (DP or second)
			float start = settContinousSplit.getStartX();
			if(settContinousSplit.getSplitMode()==XUNIT.DP && dataLine.getDPCount()>start) { 
				int dpcount = dataLine.getDPCount()-(int)start;
				linecount = (int)Math.ceil(1.0*dpcount/settContinousSplit.getSplitAfterDP()); 
				// create lines
				ScanLine[] lines = new ScanLine[linecount];
				
				// counter for next dp
				int counter = (int)start;
				for(int i=0; i<lines.length; i++) {
					// dp array 
						// start of line in x for setting it to 0
						float startx = dataLine.getPoint(counter).getX();
						// list of datapoints (full or half line?)
						DataPoint2D[] dp = (counter+settContinousSplit.getSplitAfterDP()-1<dataLine.getDPCount())? new DataPoint2D[settContinousSplit.getSplitAfterDP()]
																					: new DataPoint2D[dataLine.getDPCount()-counter];
						for(int d=0; d<dp.length && counter<dataLine.getDPCount(); d++) {
							dp[d] = new DataPoint2D(dataLine.getPoint(counter).getX()-startx, dataLine.getPoint(counter).getI());
							counter++;
						} 
						// add line
						lines[i] = new ScanLine(dp); 
				} 
				// set
				setLines(lines);
			}
			else {
				// split after time (s)
				float splitx = settContinousSplit.getSplitAfterX();
				Vector<ScanLine> lines = new Vector<ScanLine>();
				// get start index?
				int starti = 0;
				if(start!=0) {
					for(int i=0; i<dataLine.getDPCount(); i++) {
						if(dataLine.getPoint(i).getX()>=start) {
							starti = (dataLine.getPoint(i).getX()-start)<(start-dataLine.getPoint(i-1).getX())? i : i-1;
							break;
						}
					}
				}
				// in loop
				float linex = dataLine.getPoint(starti).getX();
				float STARTX = linex;
				Vector<DataPoint2D> dp = new Vector<DataPoint2D>();
				for(int d=starti; d<dataLine.getDPCount(); d++) {
					if(dataLine.getPoint(d).getX()-STARTX>=splitx*(lines.size()+1)) {
						// split, add line, new dp
						lines.add(new ScanLine(dp));
						dp.removeAllElements();
						linex = dataLine.getPoint(d).getX();						
					}
					dp.add(new DataPoint2D(dataLine.getPoint(d).getX()-linex, dataLine.getPoint(d).getI())); 
				} 
	
				// set
				setLines(lines.toArray(new ScanLine[lines.size()]));
			} 
	}
	
	/**
	 * 
	 * @param settings any image settings
	 */
	@Override
	public void setSettings(Settings settings) {
		try {
			// TODO --> set all settings in one: 
			// TODO --> complete!!!
			if(SettingsImageContinousSplit.class.isAssignableFrom(settings.getClass())) {
				setSettContinousSplit((SettingsImageContinousSplit) settings);
				resplitData();
			}
			else super.setSettings(settings);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	} 
	@Override
	public Settings getSettingsByClass(Class classsettings) {
		// TODO -- add other settings here
		if(SettingsImageContinousSplit.class.isAssignableFrom(classsettings)) 
			return getSettContinousSplit();
		else return super.getSettingsByClass(classsettings);
	}
	
	/**
	 * Given image img will be setup like this image
	 * @param img will get all settings from master image
	 */
	@Override
	public void applySettingsToOtherImage(Image2D img) {
		try {
			if(Image2DContinous.class.isInstance(img)) {
				((Image2DContinous)img).setSettContinousSplit(BinaryWriterReader.deepCopy(this.getSettContinousSplit()));
			}
			super.applySettingsToOtherImage(img); 
		} catch (Exception e) { 
			e.printStackTrace();
		}
	}
	
	/**
	 * all child will point at settings from parent. 
	 * @param parent 
	 */
	public void setParent(Image2D parent) { 
		if(parent!=null && parent!=this.parent && Image2DContinous.class.isInstance(parent)) {
			((Image2DContinous)parent).setSettContinousSplit(this.getSettContinousSplit());
		}
		super.setParent(parent);
	}
	
	//############################################################################
	// GETTERS AND SETTERS
	public ScanLine getDataLine() {
		return dataLine;
	}
	public void setDataLine(ScanLine dataLine) {
		this.dataLine = dataLine;
	}
	public SettingsImageContinousSplit getSettContinousSplit() {
		return settContinousSplit;
	}
	public void setSettContinousSplit(SettingsImageContinousSplit settContinousSplit) {
		this.settContinousSplit = settContinousSplit;
	}
}
