package net.rs.lamsi.dataextract.presets;

import net.rs.lamsi.massimager.Settings.Settings;

public class PresetsExportInstrument extends Settings { 
	// do not change the version!
    private static final long serialVersionUID = 1L;
    
    // Settings for data input
    
    
    
	
	public PresetsExportInstrument() {
		super("/Settings/Presets/Export", "exportExtract"); 
		resetAll(); 
	}

	@Override
	public void resetAll() {
		
	}
}
