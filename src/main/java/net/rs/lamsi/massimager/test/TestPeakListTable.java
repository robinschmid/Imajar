package net.rs.lamsi.massimager.test;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import net.rs.lamsi.massimager.Frames.Panels.peaktable.PnTableMZPick;
import net.sf.mzmine.datamodel.PeakListRow;
import net.sf.mzmine.datamodel.impl.SimplePeakListRow;

public class TestPeakListTable extends JFrame {

  private JPanel contentPane;
  PnTableMZPick pnTable;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          TestPeakListTable frame = new TestPeakListTable();
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
  public TestPeakListTable() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 450, 300);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    contentPane.setLayout(new BorderLayout(0, 0));
    setContentPane(contentPane);

    JPanel panel = new JPanel();
    contentPane.add(panel, BorderLayout.NORTH);

    JButton btnAdd = new JButton("Add");
    btnAdd.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        PeakListRow row = new SimplePeakListRow(0);
        // row.addPeak(null, null);
        pnTable.addPeak(row, null, 0, 1);
      }
    });
    panel.add(btnAdd);

    JButton btnRemove = new JButton("Remove");
    panel.add(btnRemove);

    pnTable = new PnTableMZPick() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        // TODO Auto-generated method stub

      }
    };
    contentPane.add(pnTable, BorderLayout.CENTER);
  }

}
