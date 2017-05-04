package net.rs.lamsi.massimager.Settings.image.sub;

import net.rs.lamsi.massimager.MyMZ.MZIon;
import net.rs.lamsi.massimager.Settings.image.selection.SettingsShapeSelection;

public class SettingsMSImage extends SettingsGeneralImage {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    //
	
    // my pm and name
	protected MZIon mzIon = null;
 
	public SettingsMSImage(boolean allFiles, boolean isTriggert, float velocity, float spotsize, double timePerLine, MZIon mzIon) {
		super("/Settings/MSImage/", "setMSI");
		this.velocity = velocity;
		this.spotsize = spotsize;
		this.timePerLine = timePerLine;
		this.mzIon = mzIon;
		this.isTriggered = isTriggert;
		this.allFiles = allFiles;
	}

	@Override
	public Class getSuperClass() {
		return SettingsGeneralImage.class; 
	}

	public MZIon getMZIon() {
		return mzIon;
	}

	public void setMZIon(MZIon mzIon) {
		this.mzIon = mzIon;
	}

	public boolean isAllFiles() {
		return allFiles;
	}

	public void setAllFiles(boolean allFiles) {
		this.allFiles = allFiles;
	}

	@Override
	public void resetAll() { 
	}
}
