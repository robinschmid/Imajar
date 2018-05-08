package net.rs.lamsi.general.myfreechart.experimental;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Rectangle2D;
import java.util.function.Predicate;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.fx.ChartViewer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Returns true if the new size was accepted and the next iteration was sent to the EDT
 *
 */
public class ChartSizeChangedListener implements Predicate<Rectangle2D>, ComponentListener {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  private Component p;
  private ChartPanel cp;
  private ChartViewer cv;
  private boolean resized = false;

  public ChartSizeChangedListener(ChartPanel cp) {
    this.cp = cp;
  }

  public ChartSizeChangedListener(ChartViewer cv) {
    this.cv = cv;
  }


  @Override
  public boolean test(Rectangle2D t) {
    if (cp != null) {
      Component currentparent = cp.getParent();
      if (p == null || currentparent != p) {
        // unset/set component resize listener
        if (p != null)
          p.removeComponentListener(this);
        p = currentparent;
        p.addComponentListener(this);
      }
      Dimension d = toDim(t);

      int pw = p.getSize().width;
      int ph = p.getSize().height;
      int cw = d.width;
      int ch = d.height;

      logger.debug("{}+{}  {}+{}  cp:{}+{}", pw, ph, cw, ch, cp.getWidth(), cp.getHeight());

      // never: both dimensions different size of parent <> cp?
      if (resized || (Math.abs(pw - cw) > 3 && Math.abs(ph - ch) > 3)) {
        resized = false;
        // resize total and restart iterations counter
        // cp.setPreferredSize(d);
        cp.setPreferredSize(p.getSize());
        cp.setMaximumSize(p.getSize());
        cp.getChart().fireChartChanged();
        cp.getParent().revalidate();
        p.getParent().repaint();
        return false;
      } else {
        // resize and next iteration
        // cp.setPreferredSize(d);
        cp.setPreferredSize(d);
        cp.setMaximumSize(d);
        cp.getChart().fireChartChanged();
        cp.getParent().revalidate();
        p.getParent().repaint();
        return true;
      }
    } else if (cv != null) {
      cv.setPrefSize(t.getWidth(), t.getHeight());
      cv.getCanvas().draw();
      return true;
    } else
      return false; // will never happen
  }

  private Dimension toDim(Rectangle2D t) {
    return new Dimension((int) t.getWidth(), (int) t.getHeight());
  }


  @Override
  public void componentResized(ComponentEvent e) {
    resized = true;
    logger.debug("RES {}  {}", e.getComponent().getSize().toString(), cp.getSize().toString());
    // one size is the same?
    cp.setPreferredSize(p.getSize());
    cp.setSize(p.getSize());
    cp.setMaximumSize(p.getSize());
    cp.getParent().revalidate();
    logger.debug("RES {}  {}", e.getComponent().getSize().toString(), cp.getSize().toString());

  }

  @Override
  public void componentHidden(ComponentEvent e) {}

  @Override
  public void componentMoved(ComponentEvent e) {}

  @Override
  public void componentShown(ComponentEvent e) {}
}
