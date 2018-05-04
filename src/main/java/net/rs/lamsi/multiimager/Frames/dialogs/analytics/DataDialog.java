package net.rs.lamsi.multiimager.Frames.dialogs.analytics;

import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.TestImageFactory;
import net.rs.lamsi.utils.mywriterreader.ClipboardWriter;

public class DataDialog extends JFrame {

  private JTextArea txtArea;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    try {
      // HistogramDialog dialog =
      // new HistogramDialog(TestImageFactory.createPerfectStandard(4, 4, 20).getFirstImage2D());
      // dialog.setVisible(true);

      Image2D img = TestImageFactory.createGaussianTest(1000, 1000).getFirstImage2D();
      DataDialog dialog2 = new DataDialog("Test histo gaussian", img.toIArray(false));
      dialog2.setVisible(true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Create the dialog.
   * 
   * @wbp.parser.constructor
   */
  public DataDialog(String title) {
    this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    setTitle(title);
    setBounds(100, 100, 400, 200);
    getContentPane().setLayout(new BorderLayout());

    JScrollPane scrollPane = new JScrollPane();
    getContentPane().add(scrollPane, BorderLayout.CENTER);
    txtArea = new JTextArea();
    txtArea.setEditable(false);
    scrollPane.setViewportView(txtArea);


    JPanel pn = new JPanel();
    getContentPane().add(pn, BorderLayout.SOUTH);

    JButton copy = new JButton("Copy");
    copy.addActionListener(e -> copy());
    pn.add(copy);

    JButton close = new JButton("Close");
    close.addActionListener(e -> setVisible(false));
    pn.add(close);

    getRootPane().setDefaultButton(copy);
  }

  public DataDialog(String title, double[] data) {
    this(title);
    // set text
    String s = Arrays.stream(data).mapToObj(String::valueOf).collect(Collectors.joining("\n"));
    txtArea.setText(s);
  }

  public DataDialog(String title, Object[][] data) {
    this(title);
    // set text
    String s = ClipboardWriter.dataToTabSepString(data, true);
    txtArea.setText(s);
  }

  public DataDialog(String title, List<? extends Object> data) {
    this(title);
    String s = data.stream().map(String::valueOf).collect(Collectors.joining("\n"));
    txtArea.setText(s);
  }

  private void copy() {
    ClipboardWriter.writeToClipBoard(txtArea.getText());
  }

}
