package net.rs.lamsi.multiimager.Frames.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.general.framework.modules.Module;
import net.rs.lamsi.general.heatmap.dataoperations.DPReduction.Mode;
import net.rs.lamsi.general.settings.image.sub.SettingsImage2DSetup;

public class ImageSetupDialog extends JDialog {

  private SettingsImage2DSetup sett;


  private final JPanel contentPanel = new JPanel();
  private JTextField txtReduce;
  private JTextField txtIntensityFactor;
  private JTextField txtVelocity;
  private JTextField txtSpotSize;
  private JComboBox comboReduce;
  private JCheckBox cbReduce;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    try {
      ImageSetupDialog dialog = new ImageSetupDialog();
      dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
      dialog.setVisible(true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Create the dialog.
   */
  public ImageSetupDialog() {
    setTitle("Image setup");
    setBounds(100, 100, 263, 197);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(new BorderLayout(0, 0));
    {
      JPanel panel = new JPanel();
      contentPanel.add(panel, BorderLayout.CENTER);
      panel.setLayout(new MigLayout("", "[][][]", "[][][][]"));
      {
        JLabel lblLaserSpeed = new JLabel("velocity");
        panel.add(lblLaserSpeed, "cell 0 0,alignx trailing");
      }
      {
        txtVelocity = new JTextField();
        txtVelocity.setText("1");
        txtVelocity.setColumns(10);
        panel.add(txtVelocity, "cell 1 0,alignx left");
      }
      {
        JLabel lblSpotSize = new JLabel("spot size");
        panel.add(lblSpotSize, "cell 0 1,alignx right");
      }
      {
        txtSpotSize = new JTextField();
        txtSpotSize.setText("1");
        txtSpotSize.setColumns(10);
        panel.add(txtSpotSize, "cell 1 1,alignx left");
      }
      {
        JLabel lblIntensityFactor = new JLabel("intensity factor");
        panel.add(lblIntensityFactor, "cell 0 2");
      }
      {
        txtIntensityFactor = new JTextField();
        txtIntensityFactor.setText("1");
        txtIntensityFactor.setColumns(10);
        panel.add(txtIntensityFactor, "cell 1 2,alignx left");
      }
      {
        cbReduce = new JCheckBox("reduce");
        panel.add(cbReduce, "cell 0 3");
      }
      {
        txtReduce = new JTextField();
        txtReduce.setText("1");
        panel.add(txtReduce, "cell 1 3,alignx left");
        txtReduce.setColumns(10);
      }
      {
        comboReduce = new JComboBox();
        comboReduce.setModel(new DefaultComboBoxModel(Mode.values()));
        comboReduce.setSelectedIndex(1);
        panel.add(comboReduce, "cell 2 3,growx");
      }
    }
    {
      JPanel buttonPane = new JPanel();
      buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
      getContentPane().add(buttonPane, BorderLayout.SOUTH);
      {
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> createSettingsAndClose());
        buttonPane.add(okButton);
        getRootPane().setDefaultButton(okButton);
      }
    }

    this.setModalityType(ModalityType.APPLICATION_MODAL);
  }

  private void createSettingsAndClose() {
    sett = new SettingsImage2DSetup();
    try {
      sett.setAll(Module.floatFromTxt(txtVelocity), Module.floatFromTxt(txtSpotSize),
          Module.doubleFromTxt(txtIntensityFactor), Module.intFromTxt(txtReduce),
          (Mode) comboReduce.getSelectedItem(), cbReduce.isSelected());

      setVisible(false);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public SettingsImage2DSetup getSettings() {
    return sett;
  }

}
