package net.rs.lamsi.massimager.Frames.Dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.general.settings.SettingsChargeCalculator;
import net.rs.lamsi.massimager.Frames.Window;
import net.sf.mzmine.parameters.parametertypes.tolerances.MZTolerance;
import net.sf.mzmine.parameters.parametertypes.tolerances.RTTolerance;

public class ChargeCalculatorSettingsDialog extends JDialog {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final JPanel contentPanel = new JPanel();
  private JTextField txtMZTolerance;
  private JTextField txtMZTolerancePPM;
  private JTextField txtRTTolerance;
  private JTextField txtMaxCharge;
  private JComboBox comboAbsRT;
  private JCheckBox cbMonotonicShape;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    try {
      ChargeCalculatorSettingsDialog dialog = new ChargeCalculatorSettingsDialog();
      dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
      dialog.setVisible(true);
    } catch (Exception e) {
    }
  }

  /**
   * Create the dialog.
   */
  public ChargeCalculatorSettingsDialog() {
    setBounds(100, 100, 450, 300);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(new MigLayout("", "[][][33.00][][][]", "[][][][]"));
    {
      JLabel lblMzTolerance = new JLabel("mz tolerance");
      contentPanel.add(lblMzTolerance, "cell 0 0,alignx trailing");
    }
    {
      txtMZTolerance = new JTextField();
      txtMZTolerance.setText("0.005");
      contentPanel.add(txtMZTolerance, "cell 1 0,alignx left");
      txtMZTolerance.setColumns(10);
    }
    {
      JLabel lblMz = new JLabel("m/z");
      contentPanel.add(lblMz, "cell 2 0,alignx leading");
    }
    {
      JLabel lblOr = new JLabel("or");
      contentPanel.add(lblOr, "cell 3 0,alignx trailing");
    }
    {
      txtMZTolerancePPM = new JTextField();
      txtMZTolerancePPM.setText("0.005");
      txtMZTolerancePPM.setColumns(10);
      contentPanel.add(txtMZTolerancePPM, "cell 4 0,alignx left");
    }
    {
      JLabel lblPpm = new JLabel("ppm");
      contentPanel.add(lblPpm, "cell 5 0");
    }
    {
      JLabel lblRetentionTimeTolerance = new JLabel("retention time tolerance");
      contentPanel.add(lblRetentionTimeTolerance, "cell 0 1,alignx trailing");
    }
    {
      txtRTTolerance = new JTextField();
      txtRTTolerance.setText("0.1");
      contentPanel.add(txtRTTolerance, "cell 1 1,alignx left");
      txtRTTolerance.setColumns(10);
    }
    {
      comboAbsRT = new JComboBox();
      comboAbsRT
          .setModel(new DefaultComboBoxModel(new String[] {"absolute (min)", "relative (%)"}));
      contentPanel.add(comboAbsRT, "cell 2 1 3 1");
    }
    {
      cbMonotonicShape = new JCheckBox("Monotonic shape");
      contentPanel.add(cbMonotonicShape, "cell 0 2");
    }
    {
      JLabel lblMaximumCharge = new JLabel("Maximum charge");
      contentPanel.add(lblMaximumCharge, "cell 0 3,alignx trailing");
    }
    {
      txtMaxCharge = new JTextField();
      txtMaxCharge.setText("15");
      contentPanel.add(txtMaxCharge, "cell 1 3,alignx left");
      txtMaxCharge.setColumns(10);
    }
    {
      JPanel buttonPane = new JPanel();
      buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
      getContentPane().add(buttonPane, BorderLayout.SOUTH);
      {
        JButton okButton = new JButton("Apply");
        okButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            //
            saveToChargeCalcSettings();
          }
        });
        okButton.setActionCommand("OK");
        buttonPane.add(okButton);
        getRootPane().setDefaultButton(okButton);
      }
      {
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setVisible(false);
          }
        });
        cancelButton.setActionCommand("Cancel");
        buttonPane.add(cancelButton);
      }
    }
  }

  protected void saveToChargeCalcSettings() {
    SettingsChargeCalculator sett = Window.getWindow().getSettings().getSetChargeCalc();
    try {
      sett.setMzTolerance(new MZTolerance(Double.valueOf(getTxtMZTolerance().getText()),
          Double.valueOf(getTxtMZTolerancePPM().getText())));
      boolean absolute = (getComboAbsRT().getSelectedIndex() == 0 ? true : false);
      sett.setRtTolerance(new RTTolerance(absolute, Double.valueOf(getTxtRTTolerance().getText())));
      sett.setMaximumCharge(Integer.valueOf(getTxtMaxCharge().getText()));
      sett.setMonotonicShape(getCbMonotonicShape().isSelected());
    } catch (Exception e) {
    }
  }

  public void showSettings(SettingsChargeCalculator sett) {
    getTxtMZTolerance().setText("" + sett.getMzTolerance().getMzTolerance());
    getTxtMZTolerancePPM().setText("" + sett.getMzTolerance().getPpmTolerance());
    getTxtRTTolerance().setText("" + sett.getRtTolerance().getTolerance());
    getTxtMaxCharge().setText("" + sett.getMaximumCharge());

    getComboAbsRT().setSelectedIndex(sett.getRtTolerance().isAbsolute() ? 0 : 1);
    getCbMonotonicShape().setSelected(sett.isMonotonicShape());
  }

  public JTextField getTxtMZTolerance() {
    return txtMZTolerance;
  }

  public JTextField getTxtMZTolerancePPM() {
    return txtMZTolerancePPM;
  }

  public JComboBox getComboAbsRT() {
    return comboAbsRT;
  }

  public JTextField getTxtRTTolerance() {
    return txtRTTolerance;
  }

  public JCheckBox getCbMonotonicShape() {
    return cbMonotonicShape;
  }

  public JTextField getTxtMaxCharge() {
    return txtMaxCharge;
  }
}
