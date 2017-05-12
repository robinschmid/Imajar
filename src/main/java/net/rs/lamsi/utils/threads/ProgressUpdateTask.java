package net.rs.lamsi.utils.threads;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.SwingWorker;

import net.rs.lamsi.utils.useful.dialogs.ProgressDialog;


public abstract class ProgressUpdateTask extends SwingWorker<Boolean, Void> {
	ProgressDialog progressDialog;
	private int stepwidth=0;

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
		int progress = Math.min(getProgress()+(int)(getStepwidth()*a), 100);
		setProgress(progress);

		progress = progress*10;
		progressDialog.getProgressBar().setValue(progress);
		progressDialog.validate();
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

	public ProgressDialog getProgressDialog() {
		return progressDialog;
	}

	public void setProgressDialog(ProgressDialog progressDialog) {
		this.progressDialog = progressDialog;
	}
}
