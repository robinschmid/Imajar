package net.rs.lamsi.utils.useful;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;

public class DebugStopWatch {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  private long start = 0, lastStop = 0;

  public DebugStopWatch() {
    super();
    setNewStartTime();
  }

  public String stopAndLOG() {
    if (ImageEditorWindow.isDebugging() && start != 0) {
      long time = System.nanoTime();
      String s = toTime(time - start) + " s";
      logger.debug(s);
      lastStop = time;
      return s;
    }
    return "";
  }

  public String stopAndLOG(String message) {
    if (ImageEditorWindow.isDebugging() && start != 0) {
      long time = System.nanoTime();
      String s = toTime(time - start) + " s";
      String d = toTime(time - lastStop) + " s";

      logger.debug("TIME: {} (+{}) for {}", s, d, message);
      lastStop = time;
      return s;
    }
    return "";
  }

  public String stop() {
    if (ImageEditorWindow.isDebugging() && start != 0) {
      long time = System.nanoTime();
      String s = toTime(time - start) + " s";
      lastStop = time;
      return s;
    }
    return "";
  }

  public void setNewStartTime() {
    if (ImageEditorWindow.isDebugging()) {
      this.start = System.nanoTime();
      this.lastStop = start;
    }
  }

  private double toTime(long diff) {
    return (diff) / 1000000 / 1000.0;
  }
}
