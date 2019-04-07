package net.rs.lamsi.multiimager.Frames.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import net.rs.lamsi.general.framework.modules.SettingsModule;
import net.rs.lamsi.general.settings.importexport.SettingsImzMLImageImport;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.utils.DialogLoggerUtil;
import net.rs.lamsi.utils.FileAndPathUtil;
import net.rs.lamsi.utils.myfilechooser.FileTypeFilter;
import net.rs.lamsi.utils.mywriterreader.TxtWriter;

public class ImzMLImportDialog extends JDialog {

  private JFileChooser fc = new JFileChooser();
  private final JPanel contentPanel = new JPanel();
  private JTextField txtMZWindow;
  private JCheckBox cbUseMZWindow;
  private JTextArea txtMZList;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    try {
      ImzMLImportDialog dialog = new ImzMLImportDialog();
      dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
      dialog.setVisible(true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Create the dialog.
   */
  public ImzMLImportDialog() {
    fc.addChoosableFileFilter(new FileTypeFilter("csv", "Comma separated file"));
    setBounds(100, 100, 450, 300);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(new BorderLayout(0, 0));
    {
      JPanel panel = new JPanel();
      contentPanel.add(panel, BorderLayout.NORTH);
      {
        JButton btnLoadData = new JButton("Load data");
        btnLoadData.addActionListener(e -> importImzMLData());
        panel.add(btnLoadData);
      }
      {
        JButton btnLoadList = new JButton("Load list");
        btnLoadList.addActionListener(e -> loadList());
        panel.add(btnLoadList);
      }
      {
        JButton btnSaveList = new JButton("Save list");
        btnSaveList.addActionListener(e -> saveList());
        panel.add(btnSaveList);
      }
      {
        cbUseMZWindow = new JCheckBox("Override mz window with");
        panel.add(cbUseMZWindow);
      }
      {
        txtMZWindow = new JTextField();
        txtMZWindow.setText("0.02");
        panel.add(txtMZWindow);
        txtMZWindow.setColumns(10);
      }
    }
    {
      JScrollPane scrollPane = new JScrollPane();
      scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
      contentPanel.add(scrollPane, BorderLayout.CENTER);
      {
        txtMZList = new JTextArea();
        txtMZList.setText("200,0.02");
        scrollPane.setViewportView(txtMZList);
      }
    }
    {
      JPanel buttonPane = new JPanel();
      buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
      getContentPane().add(buttonPane, BorderLayout.SOUTH);
      {
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            setVisible(false);
          }
        });
        cancelButton.setActionCommand("Cancel");
        buttonPane.add(cancelButton);
      }
    }
  }

  private void loadList() {
    if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      File f = fc.getSelectedFile();
      TxtWriter writer = new TxtWriter();
      Vector<String> lines = writer.readLines(f);

      // append or replace
      Object[] options = {"Append", "Replace"};
      int n = JOptionPane.showOptionDialog(this, "Append or replace?", "Append or replace?",
          JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
      boolean append = n == 0;

      if (!append)
        txtMZList.setText("");
      else
        txtMZList.append("\n");

      for (String s : lines)
        txtMZList.append(s + "\n");
    }
  }

  private void saveList() {
    if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      File f = fc.getSelectedFile();
      String file = FileAndPathUtil.getRealFileName(f, "csv");
      TxtWriter writer = new TxtWriter();
      writer.openNewFileOutput(file);
      writer.write(txtMZList.getText());
      writer.closeDatOutput();
    }
  }

  private void importImzMLData() {
    SettingsImzMLImageImport sett = createSettings();
    if (sett != null) {
      ImageEditorWindow.getEditor().getLogicRunner().importDataToImage(sett);
    }
  }

  public SettingsImzMLImageImport createSettings() {
    SettingsImzMLImageImport sett = new SettingsImzMLImageImport();

    String[] lines = txtMZList.getText().split("\n");
    ArrayList<double[]> values = new ArrayList<>();
    try {
      for (String s : lines) {
        String[] sep = s.replaceAll(" ", "").split(",");
        double[] v = new double[2];
        v[0] = Double.parseDouble(sep[0]);
        v[1] = sep.length > 1 ? Double.parseDouble(sep[1]) : 0;
        values.add(v);
      }
    } catch (Exception e) {
      DialogLoggerUtil.showErrorDialog(this,
          "Enter values as mz center, mz window (mz window is optional)", e);
      return null;
    }
    double[][] data = new double[values.size()][];
    for (int i = 0; i < data.length; i++) {
      data[i] = values.get(i);
    }
    double window = SettingsModule.doubleFromTxt(txtMZWindow);

    sett.setAll(data, cbUseMZWindow.isSelected(), window == 0 ? 0.02d : window);
    return sett;
  }

  public JCheckBox getCbUseMZWindow() {
    return cbUseMZWindow;
  }

  public JTextField getTxtMZWindow() {
    return txtMZWindow;
  }

  public JTextArea getTxtMZList() {
    return txtMZList;
  }
}
