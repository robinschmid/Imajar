package net.rs.lamsi.utils.threads;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.SwingWorker;
import com.google.common.util.concurrent.AtomicDouble;
import net.rs.lamsi.utils.useful.dialogs.ProgressDialog;


public abstract class ProgressUpdateTask<T> extends SwingWorker<T, Void> {

  protected String threadName = "UpdateTask";

  ProgressDialog progressDialog;
  private double stepwidth = 0;
  private AtomicDouble progress = new AtomicDouble(0);
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

  public ProgressUpdateTask(String name, int steps) {
    this(name, steps, 1000);
  }

  public ProgressUpdateTask(String name, int steps, long millisToPopUp) {
    this(steps, millisToPopUp);
    threadName = name;
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


  @Override
  protected T doInBackground() throws Exception {
    wasStarted();
    Thread.currentThread().setName(threadName);
    T result = doInBackground2();
    return result;
  }

  // process everything here
  protected abstract T doInBackground2() throws Exception;



  protected void wasStarted() {
    isStarted = true;
    startTime = System.currentTimeMillis();
    // open dialog?
    if (millisDelayToPopUp == 0 && progressDialog == null) {
      progressDialog = ProgressDialog.openTask(this);
    }
  }

  // Steps
  public void addProgressStep(double a) {
    setProgress(progress.get() + getStepwidth() * a);
  }

  public void setProgress(double progress) {
    double p = Math.min(progress, 100);
    p = Math.max(0, p);
    this.progress.getAndSet(progress);
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
    return progress.get();
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
