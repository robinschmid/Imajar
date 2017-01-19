package net.rs.lamsi.massimager.MyOES;

import java.io.File;
import java.util.Vector;

public class OESFile extends Vector<OESElementLine> {

	private File file;

	public OESFile(File file) {
		super();
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	
}
