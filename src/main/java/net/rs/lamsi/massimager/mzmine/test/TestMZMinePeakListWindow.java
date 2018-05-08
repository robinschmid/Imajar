package net.rs.lamsi.massimager.mzmine.test;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.rs.lamsi.massimager.mzmine.MZMineLogicsConnector;
import net.sf.mzmine.datamodel.PeakList;
import net.sf.mzmine.datamodel.PeakListRow;

// net.rs.lamsi.massimager.mzmine.test.TestMZMinePeakListWindow
public class TestMZMinePeakListWindow {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  private JFrame frame;
  protected DefaultListModel model;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    // connect to mzmine
    // have to load it before everything else
    MZMineLogicsConnector.connectToMZMine();

    // load normal app
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          TestMZMinePeakListWindow window = new TestMZMinePeakListWindow();
          window.frame.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public TestMZMinePeakListWindow() {

    initialize();
  }

  // load peaklist
  protected void loadPeakListAndShowInList() {
    PeakList[] peakLists = MZMineLogicsConnector.getPeakLists();
    model.removeAllElements();
    for (int i = 0; i < peakLists.length; i++) {
      PeakList pkl = peakLists[i];

      model.addElement("###############################################");
      model.addElement("PEAKLIST: " + pkl.getName());
      PeakListRow[] rows = pkl.getRows();
      for (int j = 0; j < rows.length; j++) {
        PeakListRow row = rows[j];

        String s = "MZ=" + row.getAverageMZ();
        s += "   HEIGHT=" + row.getAverageHeight();
        model.addElement(s);
      }
    }
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frame = new JFrame();
    frame.setBounds(100, 100, 450, 300);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JScrollPane scrollPane = new JScrollPane();
    frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

    JList list = new JList();
    model = new DefaultListModel();
    list.setModel(model);

    scrollPane.setViewportView(list);

    JButton btnLoad = new JButton("load");
    btnLoad.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        loadPeakListAndShowInList();
      }
    });
    frame.getContentPane().add(btnLoad, BorderLayout.NORTH);
  }

}
