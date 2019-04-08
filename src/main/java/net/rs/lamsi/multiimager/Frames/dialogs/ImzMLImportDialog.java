package net.rs.lamsi.multiimager.Frames.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import net.rs.lamsi.general.framework.modules.SettingsModule;
import net.rs.lamsi.general.settings.SettingsHolder;
import net.rs.lamsi.general.settings.importexport.SettingsImzMLImageImport;
import net.rs.lamsi.general.settings.preferences.SettingsGeneralPreferences;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.utils.DialogLoggerUtil;
import net.rs.lamsi.utils.FileAndPathUtil;
import net.rs.lamsi.utils.myfilechooser.FileTypeFilter;
import net.rs.lamsi.utils.mywriterreader.TxtWriter;

public class ImzMLImportDialog extends JDialog {

  private SettingsGeneralPreferences preferences;
  private JFileChooser fc = new JFileChooser();
  private final JPanel contentPanel = new JPanel();
  private JTextField txtMZWindow;
  private JCheckBox cbUseMZWindow;
  private JTextArea txtMZList;
  private JList<String> txtLastFiles;
  private List<File> lastFiles;

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
    preferences = SettingsHolder.getSettings().getSetGeneralPreferences();
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
      JPanel panel_1 = new JPanel();
      contentPanel.add(panel_1, BorderLayout.CENTER);
      panel_1.setLayout(new GridLayout(1, 0, 0, 0));
      {
        JPanel panel = new JPanel();
        panel_1.add(panel);
        panel.setLayout(new BorderLayout(0, 0));
        {
          JScrollPane scrollPane = new JScrollPane();
          panel.add(scrollPane, BorderLayout.CENTER);
          scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
          {
            txtLastFiles = new JList<>();
            txtLastFiles.setModel(new DefaultListModel<String>());
            txtLastFiles.setToolTipText("Open last used files by a double click");
            scrollPane.setViewportView(txtLastFiles);
            txtLastFiles.addMouseListener(new ActionJList(txtLastFiles));
          }
        }
        {
          JLabel lblNewLabel = new JLabel("Last loaded lists (double click)");
          panel.add(lblNewLabel, BorderLayout.NORTH);
          lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
          lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
        }
      }
      {
        JPanel panel = new JPanel();
        panel_1.add(panel);
        panel.setLayout(new BorderLayout(0, 0));
        {
          JScrollPane scrollPane = new JScrollPane();
          panel.add(scrollPane, BorderLayout.CENTER);
          scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
          {
            txtMZList = new JTextArea();
            txtMZList.setText("200,0.02");
            scrollPane.setViewportView(txtMZList);
          }
        }
        {
          JLabel lblMzListcenterwindow = new JLabel("m/z list (center,window)");
          lblMzListcenterwindow.setHorizontalAlignment(SwingConstants.CENTER);
          lblMzListcenterwindow.setFont(new Font("Tahoma", Font.BOLD, 12));
          panel.add(lblMzListcenterwindow, BorderLayout.NORTH);
        }
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
    updateLastLoadedLists();
  }

  private void loadList() {
    if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      File f = fc.getSelectedFile();
      openListFile(f);
      preferences.addMZListForImzMLPath(f, true);
      updateLastLoadedLists();
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
      preferences.addMZListForImzMLPath(new File(file), true);
      updateLastLoadedLists();
    }
  }

  private void updateLastLoadedLists() {
    lastFiles = preferences.getImzmlListHistory();
    for (File f : lastFiles)
      ((DefaultListModel) txtLastFiles.getModel())
          .addElement(MessageFormat.format("\n{0} ({1})", f.getName(), f.getAbsolutePath()));

  }


  private void openListFile(File f) {
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

  public JList<String> getTxtLastFiles() {
    return txtLastFiles;
  }


  class ActionJList extends MouseAdapter {
    protected JList<String> list;

    public ActionJList(JList<String> l) {
      list = l;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
      if (e.getClickCount() == 2) {
        int index = list.locationToIndex(e.getPoint());
        if (index >= 0) {
          list.ensureIndexIsVisible(index);
          File f = lastFiles.get(index);
          openListFile(f);
        }
      }
    }
  }
}
