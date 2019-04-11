package net.rs.lamsi.multiimager.Frames.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import net.rs.lamsi.general.framework.modules.SettingsModule;
import net.rs.lamsi.general.myfreechart.swing.EChartPanel;
import net.rs.lamsi.general.settings.SettingsHolder;
import net.rs.lamsi.general.settings.importexport.SettingsImzMLImageImport;
import net.rs.lamsi.general.settings.preferences.SettingsGeneralPreferences;
import net.rs.lamsi.utils.DialogLoggerUtil;
import net.rs.lamsi.utils.myfilechooser.FileTypeFilter;

public class EMPAImportDialog extends JFrame {

  private SettingsGeneralPreferences preferences;
  private JFileChooser fc = new JFileChooser();
  private final JPanel contentPanel = new JPanel();
  private JTextField txtMZWindow;
  private JCheckBox cbUseMZWindow;
  private JTextArea txtMZList;
  private JPanel pnChartView;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    try {
      EMPAImportDialog dialog = new EMPAImportDialog();
      dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
      dialog.setVisible(true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Create the dialog.
   */
  public EMPAImportDialog() {
    preferences = SettingsHolder.getSettings().getSetGeneralPreferences();
    fc.addChoosableFileFilter(new FileTypeFilter("bin", "binary"));
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
        btnLoadData.addActionListener(e -> importEmpaData());
        panel.add(btnLoadData);
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
          pnChartView = new JPanel(new BorderLayout());
          panel.add(pnChartView, BorderLayout.CENTER);
        }
        {
          JLabel lblNewLabel = new JLabel("Spectrum");
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
  }

  private void importEmpaData() {
    String fileName =
        "D:\\Daten2\\empa xray LA tof ms\\Imajar_WWU_davide_empa\\Davide_High_Pt_2_000_000_01.bin";

    double[] data = importData(fileName);
    XYSeries series = new XYSeries("data");
    for (int i = 0; i < data.length; i++) {
      series.add(i, data[i]);
      System.out.println(data[i]);
    }
    XYSeriesCollection dataset = new XYSeriesCollection(series);
    JFreeChart chart = ChartFactory.createXYLineChart("", "dp", "intensity", dataset);
    EChartPanel cp = new EChartPanel(chart);
    pnChartView.removeAll();
    pnChartView.add(cp, BorderLayout.CENTER);
    pnChartView.revalidate();
    pnChartView.repaint();
  }

  /**
   * Imports little endian binary double values as intensities
   * 
   * @param fileName
   * @return
   */
  public double[] importData(String fileName) {
    DoubleArrayList list = new DoubleArrayList();
    DataInputStream din = null;
    try {
      // reverse data
      double factor = -1;

      din = new DataInputStream(new FileInputStream(fileName));
      int last = 0;
      byte[] buffer = new byte[8];
      byte b;
      double data;
      while ((last = din.read(buffer)) == 8) {
        // reverse buffer
        data = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getDouble();
        list.add(data * factor);
      }
      if (last < 8) {
        System.out.println("last" + last);
      }
    } catch (

    EOFException ignore) {
    } catch (Exception ioe) {
      ioe.printStackTrace();
    } finally {
      if (din != null) {
        try {
          din.close();
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }
    }
    return list.toDoubleArray();
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
