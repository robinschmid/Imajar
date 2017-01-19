package net.rs.lamsi.massimager.Settings;

import java.io.File;



public class SettingsConverterRAW extends Settings {  
	// do not change the version!
    private static final long serialVersionUID = 1L;
    //
	// 64bit oder 32bit daten, zlib compression, peakpicking = cenroid aber profile empfohlen (profile ist standard)
	private final String BIT64 = " --64", BIT32 = " --32", ZLIB_COMPRESS = " --zlib",
						PEAK_PICK = " --filter \"peakPicking true 1-\"", FORMAT = " --mzXML", OUTPUT = " -o ";
	private final String EXE = "msconvert.exe "; 
	// 
	// einstellungen vom Settings fenster
	private boolean is32bit = true, isZlibCompressed = true, isPeakPicking = false;

	public SettingsConverterRAW(String path, String end) {
		super(path, end); 
	}
	
	// returns the cmd line for msconvert.exe
	public String getCMDLine(File inputFile, File outputFile) { 
		String cmd = EXE+ inputFile.getPath()+ OUTPUT +inputFile.getParent() + FORMAT;
		if(is32bit) cmd += BIT32;
		else cmd += BIT64;
		if(isZlibCompressed) cmd += ZLIB_COMPRESS;
		if(isPeakPicking) cmd += PEAK_PICK;
		return cmd;
	}
 

	public void setAll(boolean is32bit, boolean isZlib, boolean isPeakPicking) {
		this.is32bit = is32bit;
		isZlibCompressed = isZlib;
		this.isPeakPicking = isPeakPicking;
	}
	
	public void resetAll() {
		is32bit = true; 
		isZlibCompressed = true; 
		isPeakPicking = false;
	}
	
	
	public boolean isIs32bit() {
		return is32bit;
	}


	public void setIs32bit(boolean is32bit) {
		this.is32bit = is32bit;
	}


	public boolean isZlibCompressed() {
		return isZlibCompressed;
	}


	public void setZlibCompressed(boolean isZlibCompressed) {
		this.isZlibCompressed = isZlibCompressed;
	}


	public boolean isPeakPicking() {
		return isPeakPicking;
	}


	public void setPeakPicking(boolean isPeakPicking) {
		this.isPeakPicking = isPeakPicking;
	}

	
	
}
