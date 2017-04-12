package net.rs.lamsi.massimager.Frames.FrameWork.listener;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageLogicRunner;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow.LOG;

public abstract class DelayedDocumentListener implements DocumentListener, Runnable {

	private long lastAutoUpdateTime = -1;
	private boolean isAutoUpdateStarted = false;
	private long dalay = 1500;
	private boolean isActive = true;
	private DocumentEvent lastEvent = null;
	private boolean isStopped = false;

	public DelayedDocumentListener() {
		super();
	}
	
	public DelayedDocumentListener(long dalay) {
		super();
		this.dalay = dalay;
	}
	/**
	 * starts the auto update function
	 */
	public void startAutoUpdater(DocumentEvent e) {
		lastAutoUpdateTime = System.currentTimeMillis();
		lastEvent = e;
		isStopped = false;
		if(!isAutoUpdateStarted) { 
			ImageEditorWindow.log("Auto update started", LOG.DEBUG);
			isAutoUpdateStarted = true;
			Thread t = new Thread(this);
			t.start();
		}
		else  
			ImageEditorWindow.log("no auto update this time", LOG.DEBUG);
	}
	@Override
	public void run() {
		while(!isStopped) {
			if(lastAutoUpdateTime+dalay<=System.currentTimeMillis()) {
				documentChanged(lastEvent);
				lastAutoUpdateTime=-1;
				isAutoUpdateStarted = false;
				break;
			}
			try {
				Thread.currentThread().sleep(80);
			} catch (InterruptedException e) { 
				e.printStackTrace();
			}
		} 
		isStopped = false;
	}

	/**
	 * the document was changed
	 * @param e last document event (only)
	 */
	public abstract void documentChanged(DocumentEvent e);

	@Override
	public void removeUpdate(DocumentEvent arg0) {  
		if(isActive) startAutoUpdater(arg0); 
	} 
	@Override
	public void insertUpdate(DocumentEvent arg0) {
		if(isActive) startAutoUpdater(arg0); 
	} 
	@Override
	public void changedUpdate(DocumentEvent arg0) {
		if(isActive) startAutoUpdater(arg0); 
	}

	public long getDalay() {
		return dalay;
	}

	public void setDalay(long dalay) {
		this.dalay = dalay;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public void stop() {
		isStopped = true;
	}
	
}
