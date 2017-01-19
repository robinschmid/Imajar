package net.rs.lamsi.massimager.Settings.image;

import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.massimager.Settings.SettingsImage.XUNIT;

public class SettingsImageContinousSplit extends Settings {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    // 
	protected int splitAfterDP = 10;
	protected float splitAfterX = 1, startX=0;
	protected XUNIT splitMode = XUNIT.DP;
	  
	
 
	public SettingsImageContinousSplit() {
		super("Settings/Image/Continous", "setImgCon");  
		resetAll();
	}


	@Override
	public void resetAll() { 
		splitAfterDP = 10;
		splitAfterX = 1;
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
