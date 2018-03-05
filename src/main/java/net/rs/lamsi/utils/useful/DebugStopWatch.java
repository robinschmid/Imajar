package net.rs.lamsi.utils.useful;

import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow.LOG;

public class DebugStopWatch {

  private long start = 0, lastStop = 0;

  public DebugStopWatch() {
    super();
    setNewStartTime();
  }

  public String stopAndLOG() {
    if (ImageEditorWindow.isDebugging() && start != 0) {
      long time = System.nanoTime();
      String s = toTime(time - start) + " s";
      String d = toTime(time - lastStop) + " s";
      ImageEditorWindow.log(s, LOG.DEBUG);
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
      ImageEditorWindow.log("TIME: " + s + "(+" + d + ") for " + message, LOG.DEBUG);
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
