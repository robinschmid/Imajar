package net.rs.lamsi.utils.threads;

public abstract class StoppableRunnable implements Runnable {

	public StoppableRunnable() {
		super();
	}
	
	private boolean stopped = false;
	
	public void stop() {
		stopped = true;
	}
	
	public boolean isStopped() {
		return stopped;
	}
}
