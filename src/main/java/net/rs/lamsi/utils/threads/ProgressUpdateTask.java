package net.rs.lamsi.utils.threads;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.SwingWorker;
import net.rs.lamsi.utils.useful.dialogs.ProgressDialog;


public abstract class ProgressUpdateTask extends SwingWorker<Boolean, Void> {
  ProgressDialog progressDialog;
  private double stepwidth = 0;
  private double progress = 0;
  // pop up after 1 second
  private long millisDelayToPopUp = 1000;
  private long startTime = -1;
  private Runnable doneListener;

  protected boolean isStarted = false;

  public ProgressUpdateTask(int steps) {
    this(steps, 1000);
  }

  public ProgressUpdateTask(int steps, long millisToPopUp) {
    this.millisDelayToPopUp = millisToPopUp;
    this.setStepWidth(steps);
    addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        // progressDialog.getProgressBar().setValue(getProgress());
        if ("progress".equalsIgnoreCase(evt.getPropertyName())) {
          int progress = (Integer) (evt.getNewValue()) * 10;

          if (progressDialog != null)
            progressDialog.getProgressBar().setValue(progress);
        }
      }
    });
  }

  @Override
  protected void done() {
    super.done();
    isStarted = false;
    if (progressDialog != null)
      progressDialog.setVisible(false);
    if (doneListener != null)
      doneListener.run();
  }


  protected Boolean doInBackground() throws Exception {
    wasStarted();
    boolean result = doInBackground2();
    done();
    return result;
  }

  // process everything here
  protected abstract Boolean doInBackground2() throws Exception;



  private void wasStarted() {
    isStarted = true;
    startTime = System.currentTimeMillis();
    // open dialog?
    if (millisDelayToPopUp == 0 && progressDialog == null) {
      progressDialog = ProgressDialog.openTask(this);
    }
  }

  // Steps
  public void addProgressStep(double a) {
    setProgress(progress + getStepwidth() * a);
  }

  public void setProgress(double progress) {
    double p = Math.min(progress, 100);
    p = Math.max(0, p);
    this.progress = p;
    super.setProgress((int) p);

    // open dialog?
    if (progressDialog == null && startTime != -1
        && System.currentTimeMillis() - startTime >= millisDelayToPopUp) {
      progressDialog = ProgressDialog.openTask(this);
    }

    // set progress to dialog
    if (progressDialog != null) {
      progressDialog.getProgressBar().setValue((int) (p * 10));
      progressDialog.validate();
    }
  }

  public double getProgressDouble() {
    return progress;
  }

  public void setProgressSteps(int steps) {
    setStepWidth(steps);
  }

  protected void setStepWidth(int steps) {
    if (steps > 0)
      this.stepwidth = 100.0 / steps;
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

  public long getMillisDelayToPopUp() {
    return millisDelayToPopUp;
  }

  public void setMillisDelayToPopUp(long millisDelayToPopUp) {
    this.millisDelayToPopUp = millisDelayToPopUp;
  }

  public boolean isStarted() {
    return isStarted;
  }

  public void setStarted(boolean isStarted) {
    this.isStarted = isStarted;
  }

  public void addDoneListener(Runnable listener) {
    this.doneListener = listener;
  }
}
