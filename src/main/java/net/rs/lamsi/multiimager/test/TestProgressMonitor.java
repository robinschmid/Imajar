package net.rs.lamsi.multiimager.test;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ProgressMonitor;
import javax.swing.border.EmptyBorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.rs.lamsi.utils.mywriterreader.TxtWriter;
import net.rs.lamsi.utils.threads.ProgressUpdateTaskMonitor;

public class TestProgressMonitor extends JFrame {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  private JPanel contentPane;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          TestProgressMonitor frame = new TestProgressMonitor();
          frame.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the frame.
   */
  public TestProgressMonitor() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 450, 300);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    contentPane.setLayout(new BorderLayout(0, 0));
    setContentPane(contentPane);

    JButton btnTest = new JButton("Test");
    btnTest.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // open new task
        // Thread.sleep(random.nextInt(1000));
        ProgressMonitor monitor = new ProgressMonitor(null, "Exporting", "note", 0, 200);
        monitor.setMillisToDecideToPopup(0);
        ProgressUpdateTaskMonitor task = new ProgressUpdateTaskMonitor(monitor, 100) {

          @Override
          protected Boolean doInBackground() throws Exception {
            TxtWriter writer = new TxtWriter();
            for (int i = 0; i < 50; i++) {
              writer.openNewFileOutput("../file1.txt");
              for (int a = 0; a < 10000; a++) {
                writer.writeLine(a + "hallo leute das dauert hoffentlich etwas zeit");
              }
              writer.closeDatOutput();
              setProgress(100 / 50 * (i + 1));
              // addProgressStep(1);
            }
            return false;
          }

        };
        task.execute();
      }
    });
    contentPane.add(btnTest, BorderLayout.NORTH);
  }

}
