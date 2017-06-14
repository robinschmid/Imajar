package net.rs.lamsi.utils.myfilechooser.exceptions;

public class NoFileSelectedException extends Exception {  
	public NoFileSelectedException() {
		super("No file selected while trying to export");
	}
}
