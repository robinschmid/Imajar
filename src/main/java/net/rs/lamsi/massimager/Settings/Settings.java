package net.rs.lamsi.massimager.Settings;

import java.io.File;
import java.io.Serializable;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.utils.mywriterreader.BinaryWriterReader;

public abstract class Settings implements Serializable {  
	// do not change the version!
    private static final long serialVersionUID = 1L;
    //
	protected String path = "";
	protected String fileEnding = "";
	
	
	public Settings(String path, String fileEnding) {
		super();
		this.path = path;
		this.fileEnding = fileEnding; 
	}

	public abstract void resetAll();

	protected void saveToFile(BinaryWriterReader writer, File file) { 
		writer.save2file(this, file);
		writer.closeOut();
	} 

	protected Settings loadFromFile(BinaryWriterReader writer, File file) { 
		Settings set = (Settings) writer.readFromFile(file);
		writer.closeIn();
		return set;
	}


	public void applyToImage(Image2D img) throws Exception {
		img.setSettings(this.copy());
	}


	public String getPathSettingsFile() {
		return path;
	} 
	public void setPathSettingsFile(String path) {
		this.path = path;
	}

	public String getFileEnding() {
		return fileEnding;
	}

	public void setFileEnding(String fileEnding) {
		this.fileEnding = fileEnding;
	}

	/**
	 * returns a copy by binary copy
	 * @return
	 * @throws Exception 
	 */
	public Settings copy() throws Exception { 
		return BinaryWriterReader.deepCopy(this);
	}
	
}
