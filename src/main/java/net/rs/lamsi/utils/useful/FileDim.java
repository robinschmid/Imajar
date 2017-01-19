package net.rs.lamsi.utils.useful;

import java.io.File;

public class FileDim {
	
	public FileDim(File file, long filesize, int lines, int length) {
		this.filesize = filesize;
		this.lines = lines;
		this.length = length;
		this.file = file;
	}

	private int lines, length;
	private long filesize;
	private File file;
	
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public int getLines() {
		return lines;
	}
	public void setLines(int lines) {
		this.lines = lines;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public long getFilesize() {
		return filesize;
	}
	public void setFilesize(long filesize) {
		this.filesize = filesize;
	}
	
	public boolean compareTo(FileDim f) { 
		return this.getFilesize()==f.getFilesize() && this.length==f.getLength() && this.lines==f.getLines();
	}
	
	
	
}
