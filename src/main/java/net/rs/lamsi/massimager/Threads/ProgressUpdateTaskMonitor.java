package net.rs.lamsi.massimager.Threads;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.ExecutionException;

import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import net.rs.lamsi.massimager.Frames.Dialogs.ProgressDialog;


public abstract class ProgressUpdateTaskMonitor extends SwingWorker<Boolean, Void> {
	private ProgressMonitor progressDialog;
	private int stepwidth=0;

	public ProgressUpdateTaskMonitor(ProgressMonitor progressDialog2, int steps) {
		this.setStepWidth(steps);
		this.progressDialog = progressDialog2;
		this.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				//progressDialog.getProgressBar().setValue(getProgress());
				if ("progress".equalsIgnoreCase(evt.getPropertyName())) {
					int progress = (int) (evt.getNewValue());
					progressDialog.setProgress(progress);
					System.out.println(String.format("Completed %d%% of task.\n", progress));
				}
			} 
		});
	}

	@Override
	protected void done() {
		progressDialog.close();
	}

	// Hier alle wichtigen Prozesse wie laden von daten
	protected abstract Boolean doInBackground() throws Exception;
	
    
	@Override
	protected void process(java.util.List<Void> chunks) { 
		super.process(chunks);
		
	}

	// Steps 
	public void addProgressStep(double a) {
		setProgress(getProgress()+(int)(getStepwidth()*a));
	} 

	public void setProgressSteps(int steps) {
		setStepWidth(steps);
	}

	protected void setStepWidth(int steps) {
		if(steps>0) 
			this.stepwidth = 100/steps;
	} 
	public int getStepwidth() {
		return stepwidth;
	}
}
