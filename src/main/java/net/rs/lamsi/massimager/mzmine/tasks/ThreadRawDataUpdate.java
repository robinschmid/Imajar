package net.rs.lamsi.massimager.mzmine.tasks;

import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.rs.lamsi.massimager.mzmine.MZMineCallBackListener;

public class ThreadRawDataUpdate extends SwingWorker<Boolean, Object> {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  private static ThreadRawDataUpdate instance;
  private static final long TIME = 1500;
  private boolean isStarted = false;
  private double timeReserve = 0;

  public ThreadRawDataUpdate() {

  }

  @Override
  public Boolean doInBackground() {
    while (isStarted) {
      timeReserve -= 50;
      if (timeReserve <= 0) {
        isStarted = false;
        return true;
      }
      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {
        logger.error("", e);
        isStarted = false;
        return false;
      }
    }
    return false;
  }

  @Override
  protected void done() {
    try {
      System.out.println("DONE Thread");
      if (get()) {
        System.out.println("LOAD RAWS");
        MZMineCallBackListener.callAllListenersForRaw();
        System.out.println("LOADING FINISHED");
      }
    } catch ( /* InterruptedException, ExecutionException */ Exception e) {
      logger.error("", e);
    }
  }


  public static void start() {
    if (instance != null && instance.isStarted() && !instance.isDone()) {
      instance.setTimeReserve(TIME);
    } else if (instance == null || instance.isDone() || !instance.isStarted()) {
      instance = new ThreadRawDataUpdate();
      instance.setStarted(true);
      instance.setTimeReserve(TIME);
      instance.execute();
    }
  }

  public void setTimeReserve(double time) {
    timeReserve = time;
  }

  public static ThreadRawDataUpdate getInstance() {
    return instance;
  }

  public boolean isStarted() {
    return isStarted;
  }

  public void setStarted(boolean isStarted) {
    this.isStarted = isStarted;
  }
}
