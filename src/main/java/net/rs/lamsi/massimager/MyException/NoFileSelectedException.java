package net.rs.lamsi.massimager.MyException;

public class NoFileSelectedException extends Exception {  
	public NoFileSelectedException() {
		super("No file selected while trying to export");
	}
}
