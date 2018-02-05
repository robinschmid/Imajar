package net.rs.lamsi.general.framework.listener;

import java.util.EventListener;
import java.util.function.Consumer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow.LOG;

/**
 * Use a consumer or override documentCHanged method
 *
 */
public class DelayedDocumentListener implements DocumentListener, Runnable, EventListener {

  private long lastAutoUpdateTime = -1;
  private boolean isAutoUpdateStarted = false;
  private long delay = 1500;
  private boolean isActive = true;
  private DocumentEvent lastEvent = null;
  private boolean isStopped = false;
  private Consumer<DocumentEvent> consumer = null;

  public DelayedDocumentListener() {
    this(null);
  }

  public DelayedDocumentListener(Consumer<DocumentEvent> consumer) {
    super();
    this.consumer = consumer;
  }

  public DelayedDocumentListener(long delay) {
    this(delay, null);
  }

  public DelayedDocumentListener(long delay, Consumer<DocumentEvent> consumer) {
    super();
    this.delay = delay;
    this.consumer = consumer;
  }

  /**
   * starts the auto update function
   */
  public void startAutoUpdater(DocumentEvent e) {
    lastAutoUpdateTime = System.currentTimeMillis();
    lastEvent = e;
    isStopped = false;
    if (!isAutoUpdateStarted) {
      ImageEditorWindow.log("Auto update started", LOG.DEBUG);
      isAutoUpdateStarted = true;
      Thread t = new Thread(this);
      t.start();
    } else
      ImageEditorWindow.log("no auto update this time", LOG.DEBUG);
  }

  @Override
  public void run() {
    while (!isStopped) {
      if (lastAutoUpdateTime + delay <= System.currentTimeMillis()) {
        documentChanged(lastEvent);
        lastAutoUpdateTime = -1;
        isAutoUpdateStarted = false;
        break;
      }
      try {
        Thread.currentThread().sleep(80);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    isAutoUpdateStarted = false;
    isStopped = false;
  }

  /**
   * the document was changed
   * 
   * @param e last document event (only)
   */
  public void documentChanged(DocumentEvent e) {
    if (consumer != null)
      consumer.accept(e);
  }

  @Override
  public void removeUpdate(DocumentEvent arg0) {
    if (isActive)
      startAutoUpdater(arg0);
  }

  @Override
  public void insertUpdate(DocumentEvent arg0) {
    if (isActive)
      startAutoUpdater(arg0);
  }

  @Override
  public void changedUpdate(DocumentEvent arg0) {
    if (isActive)
      startAutoUpdater(arg0);
  }

  public long getDalay() {
    return delay;
  }

  public void setDalay(long dalay) {
    this.delay = dalay;
  }

  public boolean isActive() {
    return isActive;
  }

  public void setActive(boolean isActive) {
    this.isActive = isActive;
    if (!isActive)
      stop();
  }

  public void stop() {
    isStopped = true;
  }

}
