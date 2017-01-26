package net.rs.lamsi.massimager.Settings.image;

import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.massimager.Settings.image.SettingsImage.XUNIT;

public class SettingsImageContinousSplit extends Settings {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    // 
	protected int splitAfterDP = 100;
	protected float splitAfterX = 10, startX=0;
	protected XUNIT splitMode = XUNIT.DP;
	  
	
 
	public SettingsImageContinousSplit() {
		super("Settings/Image/Continous", "setImgCon");  
		resetAll();
	}

	public SettingsImageContinousSplit(int splitAfterDP) {
		this();
		this.splitAfterDP = splitAfterDP;
	}


	public SettingsImageContinousSplit(float splitAfter, float splitStart, XUNIT splitUnit) {
		this();
		this.splitAfterX = splitAfter;
		this.splitAfterDP = Math.round(splitAfter);
		this.startX = splitStart;
		splitMode = splitUnit;
	}

	@Override
	public void resetAll() { 
		splitAfterDP = 100;
		splitAfterX = 10;
		splitMode = XUNIT.DP;
		startX = 0;
	}


	public int getSplitAfterDP() {
		return splitAfterDP;
	}


	public void setSplitAfterDP(int splitAfterDP) {
		this.splitAfterDP = splitAfterDP;
	}


	public float getSplitAfterX() {
		return splitAfterX;
	}


	public void setSplitAfterX(float splitAfterX) {
		this.splitAfterX = splitAfterX;
	}


	public XUNIT getSplitMode() {
		return splitMode;
	}


	public void setSplitMode(XUNIT splitMode) {
		this.splitMode = splitMode;
	} 

	public float getStartX() {
		return startX;
	} 
	public void setStartX(float startX) {
		this.startX = startX;
	}
}
