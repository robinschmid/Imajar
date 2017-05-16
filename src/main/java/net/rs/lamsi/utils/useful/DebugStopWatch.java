package net.rs.lamsi.utils.useful;

import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow.LOG;

public class DebugStopWatch {

	private long start = 0;

	public DebugStopWatch() {
		super();
		if(ImageEditorWindow.isDebugging())
			this.start = System.nanoTime();
	}
	
	public String stopAndLOG() {
		if(ImageEditorWindow.isDebugging() && start!=0) {
			String s = (System.nanoTime()-start)/1000000/1000.0+" s";
			ImageEditorWindow.log(s, LOG.DEBUG);
			return s;
		}
		return "";
	}
	public String stopAndLOG(String message) {
		if(ImageEditorWindow.isDebugging() && start!=0) {
			String s = (System.nanoTime()-start)/1000000/1000.0+" s";
			ImageEditorWindow.log("TIME: "+s+" for "+message, LOG.DEBUG);
			return s;
		}
		return "";
	}
	public String stop() {
		if(ImageEditorWindow.isDebugging() && start!=0) {
			String s = (System.nanoTime()-start)/1000000/1000.0+" s";
			return s;
		}
		return "";
	}
	
	public void setNewStartTime() {
		if(ImageEditorWindow.isDebugging())
			this.start = System.nanoTime();
	}
}
