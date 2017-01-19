package net.rs.lamsi.dataextract.presets;

import net.rs.lamsi.massimager.Settings.Settings;

public class PresetsCombinedInNOut extends Settings { 
	// do not change the version!
    private static final long serialVersionUID = 1L;
    
    // Settings for data input and output
    protected PresetsImportInstrument settImport;
    protected PresetsExportInstrument settExport;
    
    
	
	public PresetsCombinedInNOut(PresetsImportInstrument settImport, PresetsExportInstrument settExport) {
		super("/Settings/Presets/Combined", "combinedExtract"); 
		resetAll(); 
		this.settImport=settImport;
		this.settExport=settExport;
	}

	@Override
	public void resetAll() {
		
	}
}
