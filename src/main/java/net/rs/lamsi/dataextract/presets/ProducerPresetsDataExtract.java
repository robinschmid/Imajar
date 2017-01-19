package net.rs.lamsi.dataextract.presets;

import java.util.Vector;

import net.rs.lamsi.massimager.Settings.Settings;

public class ProducerPresetsDataExtract extends Settings {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    
    // name of producer
    protected String name;
    // Settings for data input
    protected Vector<PresetsImportInstrument> instruments = new Vector<PresetsImportInstrument>();
    
    
	
	public Vector<PresetsImportInstrument> getInstruments() {
		return instruments;
	}

	public void setInstruments(Vector<PresetsImportInstrument> instruments) {
		this.instruments = instruments;
	}

	public ProducerPresetsDataExtract(String name) {
		super("/Settings/Presets", "prodExtract"); 
		resetAll(); 
		this.name = name;
	}

	@Override
	public void resetAll() {
		
	}
	
	public void addInstrument(PresetsImportInstrument inst) {
		instruments.addElement(inst);
	}
	public void removeInstrument(PresetsImportInstrument inst) {
		instruments.removeElement(inst);
	}
	public void removeInstrument(int i) {
		instruments.removeElement(i);
	}

	public String getName() { 
		return name;
	}
}
