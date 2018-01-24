package net.rs.lamsi.general.myfreechart.listener.history;

public class ZoomHistoryEvent {
	
	public enum Event {
		SET_INDEX, DELETED, ADDED;
	}
	private ZoomHistory history;
	private int previousI, currentI;
	
	public ZoomHistoryEvent(ZoomHistory history, int previousI, int currentI) {
		super();
		this.history = history;
		this.previousI = previousI;
		this.currentI = currentI;
	}

	public ZoomHistory getHistory() {
		return history;
	}

	public void setHistory(ZoomHistory history) {
		this.history = history;
	}

	public int getPreviousI() {
		return previousI;
	}

	public void setPreviousI(int previousI) {
		this.previousI = previousI;
	}

	public int getCurrentI() {
		return currentI;
	}

	public void setCurrentI(int currentI) {
		this.currentI = currentI;
	}
	
	
	
}
