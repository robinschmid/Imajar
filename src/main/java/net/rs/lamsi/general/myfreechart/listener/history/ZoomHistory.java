/*
 * Copyright 2006-2015 The MZmine 2 Development Team
 * 
 * This file is part of MZmine 2.
 * 
 * MZmine 2 is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * MZmine 2 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with MZmine 2; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301
 * USA
 */

package net.rs.lamsi.general.myfreechart.listener.history;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.CombinedRangeXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.rs.lamsi.general.myfreechart.listener.AxesRangeChangedListener;
import net.rs.lamsi.general.myfreechart.listener.AxisRangeChangedEvent;

/**
 * The ZoomHistory stores all zoom states which are active for at least 1 second. It allows to jump
 * to previous and next states. To obtain the ZoomHistory object from any ChartPanel use:
 * 
 * <pre>
 * {@code (ZoomHistory) chartPanel.getClientProperty(ZoomHistory.PROPERTY_NAME)}
 * </pre>
 * 
 * @author Robin Schmid (robinschmid@uni-muenster.de)
 */
public class ZoomHistory extends AxesRangeChangedListener implements Runnable {

  public static final String PROPERTY_NAME = "ZOOM_HISTORY";
  private final Logger logger = LoggerFactory.getLogger(getClass());

  // history for Range[domain-, range-axis]
  // newest first
  private LinkedList<Range[]> history;
  private int currentI = 0;
  private int maxSize = 0;

  // latest event
  private Range[] newRange;
  private Thread thread;
  private boolean isRunning = false;

  // last change
  private static final long MIN_TIME_DIFF = 1000;
  private long lastChangeTime = 0;
  // consumer
  private Consumer<AxisRangeChangedEvent> axesRangeChangedHandler;
  private Consumer<ZoomHistoryEvent> zoomHistoryEventHandler;

  /**
   * Creates a ZoomHistory for the given ChartPanel as a ClientProperty. The history collects all
   * zoom states that are active for at least 1 second. It allows to jump to previous and next zoom
   * states. To obtain the ZoomHistory object from any ChartPanel use:
   * 
   * <pre>
   * {@code (ZoomHistory) chartPanel.getClientProperty(ZoomHistory.PROPERTY_NAME)}
   * </pre>
   * 
   * @param cp
   * @param maxSize
   */
  public static ZoomHistory create(ChartPanel cp, int maxSize) {
    return create(cp, maxSize, null);
  }

  /**
   * Creates a ZoomHistory for the given ChartPanel and puts a ClientProperty. The history collects
   * all zoom states that are active for at least 1 second. It allows to jump to previous and next
   * zoom states. To obtain the ZoomHistory object from any ChartPanel use:
   * 
   * <pre>
   * {@code (ZoomHistory) chartPanel.getClientProperty(ZoomHistory.PROPERTY_NAME)}
   * </pre>
   * 
   * @param cp
   * @param maxSize
   * @param zoomHistoryEventHandler A consumer to handle changes in the ZoomHistory.
   */
  public static ZoomHistory create(ChartPanel cp, int maxSize,
      Consumer<ZoomHistoryEvent> zoomHistoryEventHandler) {
    ZoomHistory result = null;
    XYPlot plot = cp.getChart().getXYPlot();
    if (plot != null) {
      if (plot instanceof CombinedDomainXYPlot) {
        List l = ((CombinedDomainXYPlot) plot).getSubplots();
        for (Object o : l) {
          if (o instanceof XYPlot) {
            XYPlot p = (XYPlot) o;
            result = new ZoomHistory(p, maxSize, zoomHistoryEventHandler);
          }
        }
      } else if (plot instanceof CombinedRangeXYPlot) {
        List l = ((CombinedRangeXYPlot) plot).getSubplots();
        for (Object o : l) {
          if (o instanceof XYPlot) {
            XYPlot p = (XYPlot) o;
            result = new ZoomHistory(p, maxSize, zoomHistoryEventHandler);
          }
        }
      } else {
        result = new ZoomHistory(plot, maxSize, zoomHistoryEventHandler);
      }
    }

    // add as client property
    cp.putClientProperty(PROPERTY_NAME, result);
    return result;
  }

  /**
   * Creates a ZoomHistory for the given ChartPanel as a ClientProperty. The history collects all
   * zoom states that are active for at least 1 second. It allows to jump to previous and next zoom
   * states. To obtain the ZoomHistory object from any ChartPanel use:
   * 
   * <pre>
   * {@code (ZoomHistory) chartPanel.getClientProperty(ZoomHistory.PROPERTY_NAME)}
   * </pre>
   * 
   * @param plot
   * @param maxSize
   */
  public ZoomHistory(XYPlot plot, int maxSize) {
    this(plot, maxSize, null);
  }

  /**
   * Creates a ZoomHistory for the given ChartPanel as a ClientProperty. The history collects all
   * zoom states that are active for at least 1 second. It allows to jump to previous and next zoom
   * states. To obtain the ZoomHistory object from any ChartPanel use:
   * 
   * <pre>
   * {@code (ZoomHistory) chartPanel.getClientProperty(ZoomHistory.PROPERTY_NAME)}
   * </pre>
   * 
   * @param plot
   * @param maxSize
   * @param zoomHistoryEventHandler
   */
  public ZoomHistory(XYPlot plot, int maxSize, Consumer<ZoomHistoryEvent> zoomHistoryEventHandler) {
    super(plot, null);
    // set to zoom history plot
    if (plot != null && plot instanceof ZoomHistoryPlot) {
      ((ZoomHistoryPlot) plot).setZoomHistory(this);
    }

    history = new LinkedList<Range[]>();
    // max
    this.maxSize = maxSize;
  }

  /**
   * 
   * @param plot
   * @return
   * @throws Exception
   */
  public static ZoomHistory getZoomHistoryFromXYPlot(XYPlot plot) throws Exception {
    if (plot != null && plot instanceof ZoomHistoryPlot) {
      return ((ZoomHistoryPlot) plot).getZoomHistory();
    } else
      throw new Exception("Not a ZoomHistoryPlot. Update XYPlot to implement ZoomHistoryPlot");
  }

  /**
   * Handle AxisRangeChangedEvents. Gets always called after the ZoomHistory has handled this event
   * 
   * @param handler
   */
  public void setOnAxesRangeChanged(Consumer<AxisRangeChangedEvent> handler) {
    this.axesRangeChangedHandler = handler;
  }

  /**
   * Handle ZoomHistoryEvents
   * 
   * @param handler
   */
  public void setOnZoomHistoryChanged(Consumer<ZoomHistoryEvent> handler) {
    this.zoomHistoryEventHandler = handler;
  }

  private void fireZoomHistoryEvent(ZoomHistoryEvent e) {
    if (zoomHistoryEventHandler != null)
      zoomHistoryEventHandler.accept(e);
  }

  @Override
  public void axesRangeChanged(AxisRangeChangedEvent e) {
    XYPlot plot = e.getPlot();
    // ranges
    Range dom = null;
    Range ran = null;

    if (plot instanceof CombinedDomainXYPlot) {
      CombinedDomainXYPlot domp = (CombinedDomainXYPlot) plot;
      dom = domp.getDomainAxis().getRange();
      if (!e.getAxis().equals(dom))
        ran = e.getAxis().getRange();
    } else if (plot instanceof CombinedRangeXYPlot) {
      CombinedRangeXYPlot ranp = (CombinedRangeXYPlot) plot;
      ran = ranp.getRangeAxis().getRange();
      if (!e.getAxis().equals(ran))
        dom = e.getAxis().getRange();
    } else {
      dom = plot.getDomainAxis().getRange();
      ran = plot.getRangeAxis().getRange();
    }
    newRange = new Range[] {dom, ran};

    // set time
    lastChangeTime = System.nanoTime();

    if (!isRunning) {
      thread = new Thread(this);
      thread.start();
      isRunning = true;
    }
    // call additional handler
    if (axesRangeChangedHandler != null)
      axesRangeChangedHandler.accept(e);
  }

  @Override
  public void run() {
    Thread thisThread = Thread.currentThread();
    while (thisThread == thread) {
      // greater than time limit?
      long ctime = System.nanoTime();
      if ((ctime - lastChangeTime) / 1000000 >= MIN_TIME_DIFF) {
        //
        handleLatestEvent();
        // end
        isRunning = false;
        break;
      }
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        logger.error("", e);
      }
    }
  }

  /**
   * jump to next or previous or add new
   */
  private void handleLatestEvent() {
    int prevI = getCurrentIndex();
    // is already in linked list?
    boolean found = false;

    // is current?
    if (history != null && !history.isEmpty()) {
      Range[] c = history.get(currentI);
      if (newRange[0].equals(c[0]) && newRange[1].equals(c[1])) {
        return;
      }
    }
    // is previous?
    if (currentI + 1 < history.size()) {
      Range[] r = history.get(currentI + 1);
      if (newRange[0].equals(r[0]) && newRange[1].equals(r[1])) {
        found = true;
        currentI++;
        // fire event
        fireZoomHistoryEvent(new ZoomHistoryEvent(this, prevI, currentI));
      }
    }

    // is next
    if (currentI - 1 >= 0) {
      Range[] r = history.get(currentI - 1);
      if (newRange[0].equals(r[0]) && newRange[1].equals(r[1])) {
        found = true;
        currentI--;
        // fire event
        fireZoomHistoryEvent(new ZoomHistoryEvent(this, prevI, currentI));
      }
    }

    if (!found) {
      // remove all history objects 0 to currentI-1
      for (int i = 0; i < currentI; i++)
        history.removeFirst();
      // add new
      history.addFirst(newRange);

      // only keep maxSize steps
      if (history.size() > maxSize)
        history.removeLast();
      currentI = 0;

      // fire event
      fireZoomHistoryEvent(new ZoomHistoryEvent(this, prevI, currentI));
    }
  }
  /**
   * OLD: jump in history or add new
   */
  // private void handleLatestEvent() {
  // int prevI = getCurrentIndex();
  // // is already in linked list?
  // boolean found = false;
  // for (int i = 0; i < history.size() && !found; i++) {
  // Range[] r = history.get(i);
  // if (newRange[0].equals(r[0]) && newRange[1].equals(r[1])) {
  // found = true;
  // // jumpt to the position
  // currentI = i;
  // // fire event
  // fireZoomHistoryEvent(new ZoomHistoryEvent(this, prevI, currentI));
  // }
  // }
  // if (!found) {
  // // remove all history objects 0 to currentI-1
  // for (int i = 0; i < currentI; i++)
  // history.removeFirst();
  // // add new
  // history.addFirst(newRange);
  //
  // // only keep maxSize steps
  // if (history.size() > maxSize)
  // history.removeLast();
  // currentI = 0;
  //
  // // fire event
  // fireZoomHistoryEvent(new ZoomHistoryEvent(this,prevI, currentI));
  // }
  // }

  /**
   * Current zoom range
   * 
   * @return
   */
  public Range[] getCurrentRange() {
    if (history.isEmpty())
      return null;
    return history.get(currentI);
  }

  /**
   * Previous zoom range without changing the active state of the history
   * 
   * @return
   */
  public Range[] getPreviousRange() {
    if (history.isEmpty() || currentI + 1 >= getSize())
      return null;
    return history.get(currentI + 1);
  }

  /**
   * Next zoom range without changing the active state of the history
   * 
   * @return
   */
  public Range[] getNextRange() {
    if (history.isEmpty() || currentI - 1 < 0)
      return null;
    return history.get(currentI - 1);
  }

  /**
   * Jump to previous zoom range (change current state)
   * 
   * @return
   */
  public Range[] setPreviousPoint() {
    int tmp = currentI;
    currentI++;
    if (currentI >= getSize())
      currentI = getSize() - 1;
    // fire event
    if (currentI != tmp)
      fireZoomHistoryEvent(new ZoomHistoryEvent(this, tmp, currentI));
    return getCurrentRange();
  }

  /**
   * Jump to next zoom range (change current state)
   * 
   * @return
   */
  public Range[] setNextPoint() {
    int tmp = currentI;
    currentI--;
    if (currentI < 0)
      currentI = 0;
    // fire event
    if (currentI != tmp)
      fireZoomHistoryEvent(new ZoomHistoryEvent(this, tmp, currentI));
    return getCurrentRange();
  }

  /**
   * Might want to clear the history after completing the creation of a chart
   */
  public void clear() {
    stopRunningUpdates();
    history.clear();
  }

  /**
   * Stops the threads run method that updates the history on axes changed events
   */
  public void stopRunningUpdates() {
    thread = null;
    isRunning = false;
  }

  public LinkedList<Range[]> getHistory() {
    return history;
  }

  public int getSize() {
    return history.size();
  }

  public int getCurrentIndex() {
    return currentI;
  }
}
