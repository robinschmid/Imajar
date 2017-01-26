package net.rs.lamsi.massimager.Settings.image;

import net.rs.lamsi.massimager.MyMZ.MZIon;

public class SettingsMSImage extends SettingsImage {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    //
	
	protected MZIon mzIon = null;
 
	public SettingsMSImage(boolean allFiles, boolean isTriggert, float velocity, float spotsize, double timePerLine, MZIon mzIon) {
		super("/Settings/MSImage/", "setMSI");
		this.velocity = velocity;
		this.spotsize = spotsize;
		this.timePerLine = timePerLine;
		this.mzIon = mzIon;
		this.isTriggert = isTriggert;
		this.allFiles = allFiles;
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
