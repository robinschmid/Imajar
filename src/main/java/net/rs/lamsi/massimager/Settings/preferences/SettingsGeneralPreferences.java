package net.rs.lamsi.massimager.Settings.preferences;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import net.rs.lamsi.massimager.Settings.Settings;

public class SettingsGeneralPreferences extends Settings {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    //
    // Icon settings
    private int iconWidth, iconHeight;
    private boolean generatesIcons = true;
    
	

	public SettingsGeneralPreferences() {
		super("/Settings/General/", "settPrefer");  
		resetAll();
	}


	@Override
	public void resetAll() { 
		iconWidth = 60;
		iconHeight = 16;
		generatesIcons = true;
	}


	public int getIconWidth() {
		return iconWidth;
	}
	public void setIconWidth(int iconWidth) {
		this.iconWidth = iconWidth;
	}
	public int getIconHeight() {
		return iconHeight;
	}
	public void setIconHeight(int iconHeight) {
		this.iconHeight = iconHeight;
	}
	public boolean isGeneratesIcons() {
		return generatesIcons;
	}
	public void setGeneratesIcons(boolean generatesIcons) {
		this.generatesIcons = generatesIcons;
	}
}
