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
public class DatasetContinuous implements ImageDataset {

	protected ScanLine[] lines; 
	
	public DatasetContinuous(ScanLine[] listLines) { 
		lines = listLines;
	}
	public DatasetContinuous(Vector<ScanLine> scanLines) {
		lines = new ScanLine[scanLines.size()];
		for(int i=0; i<lines.length; i++)
			lines[i] = scanLines.get(i); 
	}
}
