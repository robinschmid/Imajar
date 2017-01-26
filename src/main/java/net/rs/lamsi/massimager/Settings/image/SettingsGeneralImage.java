package net.rs.lamsi.massimager.Settings.image;



public class SettingsGeneralImage extends SettingsImage {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    //
	 
 
	public SettingsGeneralImage() {
		super("/Settings/OESImage/", "setGIMG"); 
	} 

	@Override
	public void resetAll() { 
		velocity = 50;
		spotsize = 50;
		allFiles = true;
	} 
}
