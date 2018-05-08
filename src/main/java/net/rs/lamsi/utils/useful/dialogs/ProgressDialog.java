package net.rs.lamsi.utils.useful.dialogs;


import java.awt.BorderLayout;
import java.util.ArrayList;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.rs.lamsi.utils.threads.ProgressUpdateTask;

public class ProgressDialog extends JDialog {
  private final static Logger logger = LoggerFactory.getLogger(ProgressDialog.class);

  private final JPanel contentPanel = new JPanel();
  private JProgressBar progressBar;

  private int stepwidth = 0;

  private static JFrame mainframe;
  private static ArrayList<ProgressDialog> dialogs;


  /**
   * Create the dialog.
   */
  public ProgressDialog() {
    super(mainframe, "Progress Dialog", ModalityType.MODELESS);

    setBounds(100, 100, 256, 159);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(new BorderLayout(0, 0));

    progressBar = new JProgressBar(0, 1000);
    progressBar.setValue(200);
    progressBar.setStringPainted(true);

    contentPanel.add(BorderLayout.CENTER, progressBar);
    contentPanel.add(BorderLayout.NORTH, new JLabel("Progress..."));
    this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    this.pack();
    this.setVisible(false);
  }

  public static void initDialog(JFrame frame) {
    mainframe = frame;
    dialogs = new ArrayList<ProgressDialog>(10);
    for (int i = 0; i < 10; i++) {
      dialogs.add(new ProgressDialog());
    }
  }

  // Allways a new Task
  public static ProgressDialog openTask(ProgressUpdateTask task) {
    ProgressDialog d = getNextAvailableDialog();
    task.setProgressDialog(d);
    d.setProgress(((int) task.getProgressDouble() * 10));
    d.setVisibleDialog(true);
    logger.debug("starting task dialog with percent: {}", (int) task.getProgressDouble());
    return d;
  }

  private static ProgressDialog getNextAvailableDialog() {
    for (ProgressDialog d : dialogs) {
      if (!d.isVisible())
        return d;
    }
    // add new
    ProgressDialog d = new ProgressDialog();
    dialogs.add(d);
    return d;
  }

  public void setProgress(int promille) {
    getProgressBar().setValue(promille);
  }

  public int getProgress() {
    return getProgressBar().getValue();
  }

  public void setVisibleDialog(boolean flag) {
    getProgressBar().setValue(0);
    setVisible(flag);
  }

  public JProgressBar getProgressBar() {
    return progressBar;
  }

  public void addProgressStep(double a) {
    setProgress(getProgress() + (int) (getStepwidth() * a));
  }

  public void setProgressBar(JProgressBar progressBar) {
    this.progressBar = progressBar;
  }

  public void setProgressSteps(int steps) {
    setStepWidth(steps);
  }

  protected void setStepWidth(int steps) {
    if (steps > 0)
      this.stepwidth = progressBar.getMaximum() / steps;
  }

  public int getStepwidth() {
    return stepwidth;
  }

}
