package net.rs.lamsi.utils.threads;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.SwingWorker;

import net.rs.lamsi.utils.useful.dialogs.ProgressDialog;


public abstract class ProgressUpdateTask extends SwingWorker<Boolean, Void> {
	ProgressDialog progressDialog;
	private double stepwidth=0;
	private double progress = 0;

	public ProgressUpdateTask(int steps) {
		this.setStepWidth(steps);
		addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				//progressDialog.getProgressBar().setValue(getProgress());
				if ("progress".equalsIgnoreCase(evt.getPropertyName())) {
					int progress = (Integer) (evt.getNewValue())*10;
					progressDialog.getProgressBar().setValue(progress);
				}
			} 
		});
	}

	@Override
	protected void done() {
		progressDialog.setVisible(false);
	}

	// Hier alle wichtigen Prozesse wie laden von daten
	protected abstract Boolean doInBackground() throws Exception;
	
    
	@Override
	protected void process(java.util.List<Void> chunks) { 
		super.process(chunks);
	}

	// Steps 
	public void addProgressStep(double a) {
		double p = Math.min(progress+getStepwidth()*a, 100);
		setProgress(p);
	} 

	public void setProgress(double progress) {
		this.progress = progress;
		super.setProgress((int)progress);
		
		progressDialog.getProgressBar().setValue((int)(progress*10));
		progressDialog.validate();
	}
	
	public void setProgressSteps(int steps) {
		setStepWidth(steps);
	}

	protected void setStepWidth(int steps) {
		if(steps>0) 
			this.stepwidth = 100.0/steps;
	} 
	public double getStepwidth() {
		return stepwidth;
	}

	public ProgressDialog getProgressDialog() {
		return progressDialog;
	}

	public void setProgressDialog(ProgressDialog progressDialog) {
		this.progressDialog = progressDialog;
	}
}
