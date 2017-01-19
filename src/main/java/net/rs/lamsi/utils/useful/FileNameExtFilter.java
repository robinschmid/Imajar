package net.rs.lamsi.utils.useful;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;

import net.rs.lamsi.utils.FileAndPathUtil;

public class FileNameExtFilter implements FilenameFilter, Serializable { 
	private static final long serialVersionUID = 1L;
	private String startsWith, ext;
	
	public FileNameExtFilter(String startsWith, String ext) {
		this.startsWith = startsWith.toLowerCase();
		this.ext = ext.toLowerCase();
	}
	
	@Override
	public boolean accept(File f, String name) {
		return ((new File(f,name)).isFile() && (ext.equals("") || name.toLowerCase().endsWith(ext)) && (startsWith.equals("") || name.toLowerCase().startsWith(startsWith)));
	}

	public String getStartsWith() {
		return startsWith;
	}

	public void setStartsWith(String startsWith) {
		this.startsWith = startsWith;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

}
