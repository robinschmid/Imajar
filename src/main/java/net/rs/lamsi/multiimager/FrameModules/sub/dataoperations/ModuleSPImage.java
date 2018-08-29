package net.rs.lamsi.multiimager.FrameModules.sub.dataoperations;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.jfree.data.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.general.datamodel.image.SingleParticleImage;
import net.rs.lamsi.general.framework.listener.ColorChangedListener;
import net.rs.lamsi.general.framework.listener.DelayedDocumentListener;
import net.rs.lamsi.general.framework.modules.Collectable2DSettingsModule;
import net.rs.lamsi.general.heatmap.Heatmap;
import net.rs.lamsi.general.heatmap.dataoperations.DPReduction.Mode;
import net.rs.lamsi.general.myfreechart.listener.history.ZoomHistory;
import net.rs.lamsi.general.settings.image.special.SingleParticleSettings;
import net.rs.lamsi.general.settings.image.sub.SettingsGeneralImage.Transformation;
import net.rs.lamsi.multiimager.Frames.ImageLogicRunner;
import net.rs.lamsi.multiimager.Frames.dialogs.singleparticle.SingleParticleDialog;

public class ModuleSPImage
    extends Collectable2DSettingsModule<SingleParticleSettings, SingleParticleImage> {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  //
  private DelayedDocumentListener ddlCenterPM;
  private JTextField txtLower;
  private JTextField txtUpper;

  private ZoomHistory history;
  private JTextField txtCenter;
  private JTextField txtPM;
  private JTextField txtNoiseLevel;
  private JTextField txtSplitPixel;
  private JLabel lblNoiseLevel;
  private JLabel lblSplitEventPixel;
  private JLabel lblWindow;
  private JLabel label_1;
  private JButton btnOpenDialog;
  private JTextField txtNPixel;
  private JLabel lblCountInPixel;
  private JLabel lblSplitPixelFilter;
  private JCheckBox cbCountParticles;
  private JComboBox<Transformation> comboTransform;
  private JLabel lblTransform;
  private JLabel lblIntensityWindowafter;
  private Component verticalStrut;
  private Component verticalStrut_1;
  private JComboBox comboReduction;
  private JTextField txtMaxDP;
  private JLabel lblMaxDpnoisedecluster;
  private JLabel lblDeclusteringbeforeTransform;
  private JCheckBox cbDecluster;

  // AUTOGEN

  /**
   * Create the panel.
   */
  public ModuleSPImage() {
    super("Single particle", false, SingleParticleSettings.class, SingleParticleImage.class);
    getLbTitle().setText("Single Particle");

    JPanel panel = new JPanel();
    getPnContent().add(panel, BorderLayout.CENTER);
    panel.setLayout(
        new MigLayout("", "[][][][][grow]", "[][][][][][][][][][][][][][][][][][][][][10.00]"));

    btnOpenDialog = new JButton("Open dialog");
    btnOpenDialog.addActionListener(e -> openSingleParticleDialog());
    panel.add(btnOpenDialog, "cell 1 0,growx");

    lblSplitPixelFilter = new JLabel("Split pixel filter (before tranform):");
    lblSplitPixelFilter.setFont(new Font("Tahoma", Font.BOLD, 11));
    panel.add(lblSplitPixelFilter, "cell 0 1 4 1");

    lblNoiseLevel = new JLabel("noise level");
    panel.add(lblNoiseLevel, "cell 0 2,alignx right");

    txtNoiseLevel = new JTextField();
    txtNoiseLevel.setText("0");
    txtNoiseLevel.setToolTipText("Noise level intensity as threshold");
    panel.add(txtNoiseLevel, "cell 1 2,growx");
    txtNoiseLevel.setColumns(10);

    lblSplitEventPixel = new JLabel("split event pixel");
    panel.add(lblSplitEventPixel, "cell 0 3,alignx right");

    txtSplitPixel = new JTextField();
    txtSplitPixel.setToolTipText(
        "How many pixels should be recognised as split particle events? (integers)");
    txtSplitPixel.setText("2");
    panel.add(txtSplitPixel, "flowy,cell 1 3,growx");
    txtSplitPixel.setColumns(10);

    lblDeclusteringbeforeTransform = new JLabel("Declustering (before transform):");
    lblDeclusteringbeforeTransform.setFont(new Font("Tahoma", Font.BOLD, 11));
    panel.add(lblDeclusteringbeforeTransform, "cell 0 4 2 1");

    lblMaxDpnoisedecluster = new JLabel("max dp (decluster)");
    lblMaxDpnoisedecluster.setToolTipText(
        "Declustering: Maximum number of consecutive data points>noise level. Data is removed if more data points are above the noise level. ");
    panel.add(lblMaxDpnoisedecluster, "cell 0 6,alignx trailing");

    txtMaxDP = new JTextField();
    txtMaxDP.setText("4");
    panel.add(txtMaxDP, "cell 1 6,growx,aligny top");
    txtMaxDP.setColumns(10);

    cbDecluster = new JCheckBox("apply declustering");
    cbDecluster.setSelected(true);
    panel.add(cbDecluster, "cell 0 7 2 1");

    verticalStrut_1 = Box.createVerticalStrut(10);
    panel.add(verticalStrut_1, "cell 0 8");

    lblTransform = new JLabel("Transform");
    lblTransform.setFont(new Font("Tahoma", Font.BOLD, 11));
    panel.add(lblTransform, "cell 0 9,alignx trailing");

    comboTransform = new JComboBox<>();
    comboTransform.setModel(new DefaultComboBoxModel(Transformation.values()));
    panel.add(comboTransform, "cell 1 9,alignx left");

    verticalStrut = Box.createVerticalStrut(10);
    panel.add(verticalStrut, "cell 0 10");

    lblIntensityWindowafter = new JLabel("Intensity window (after transform):");
    lblIntensityWindowafter.setFont(new Font("Tahoma", Font.BOLD, 11));
    panel.add(lblIntensityWindowafter, "cell 0 11 4 1");

    lblWindow = new JLabel("window");
    panel.add(lblWindow, "cell 0 12,alignx trailing");

    txtLower = new JTextField();
    txtLower.setHorizontalAlignment(SwingConstants.RIGHT);
    panel.add(txtLower, "flowx,cell 1 12,growx,aligny top");
    txtLower.setColumns(12);

    JLabel label = new JLabel("-");
    panel.add(label, "cell 2 12,alignx center");

    txtUpper = new JTextField();
    txtUpper.setHorizontalAlignment(SwingConstants.RIGHT);
    panel.add(txtUpper, "cell 3 12,growx");
    txtUpper.setColumns(12);

    label_1 = new JLabel("window");
    panel.add(label_1, "cell 0 13,alignx trailing");

    txtCenter = new JTextField();
    txtCenter.setToolTipText("Center value");
    txtCenter.setHorizontalAlignment(SwingConstants.RIGHT);
    txtCenter.setColumns(7);
    panel.add(txtCenter, "flowx,cell 1 13,growx");

    JLabel lblu = new JLabel("\u00B1");
    panel.add(lblu, "cell 2 13,alignx trailing");

    txtPM = new JTextField();
    txtPM.setToolTipText("Plus minus range around the center value");
    txtPM.setHorizontalAlignment(SwingConstants.RIGHT);
    txtPM.setColumns(10);
    panel.add(txtPM, "cell 3 13,growx");

    lblCountInPixel = new JLabel("count in pixel");
    panel.add(lblCountInPixel, "cell 0 14,alignx trailing");

    txtNPixel = new JTextField();
    txtNPixel.setText("1000");
    panel.add(txtNPixel, "cell 1 14,growx");
    txtNPixel.setColumns(10);

    comboReduction = new JComboBox();
    comboReduction.setModel(new DefaultComboBoxModel<Mode>(Mode.values()));
    comboReduction
        .setToolTipText("This reduction is replaced by sum if ocunt particles is selected");
    panel.add(comboReduction, "cell 3 14,alignx left");

    cbCountParticles = new JCheckBox("count particles in window");
    panel.add(cbCountParticles, "cell 0 15 2 1");


    ddlCenterPM = new DelayedDocumentListener() {
      @Override
      public void documentChanged(DocumentEvent e) {
        try {
          double c = doubleFromTxt(txtCenter);
          double pm = doubleFromTxt(txtPM);
          Range r = new Range(c - pm, c + pm);
          txtLower.setText(String.valueOf(r.getLowerBound()));
          txtUpper.setText(String.valueOf(r.getUpperBound()));
        } catch (Exception e2) {
          logger.error("Wrong input for center or plus minus range (single particle module", e2);
        }
      }
    };
    ddlCenterPM.setActive(true);
    txtCenter.getDocument().addDocumentListener(ddlCenterPM);
    txtPM.getDocument().addDocumentListener(ddlCenterPM);

    setMaxPresets(15);
  }

  /**
   * Open a new single particle dialog
   */
  public void openSingleParticleDialog() {
    if (getCurrentImage() != null) {
      SingleParticleDialog d = new SingleParticleDialog();
      d.setSPImage(getCurrentImage());
      d.setVisible(true);
    }
  }

  @Override
  public void setCurrentHeatmap(Heatmap heat) {
    super.setCurrentHeatmap(heat);
    // extract
    if (heat != null && getSettings() != null) {
      // getSettings().setXrange(heat.getPlot().getDomainAxis().getRange());
      // getSettings().setYrange(heat.getPlot().getRangeAxis().getRange());

      setAllViaExistingSettings(getSettings());
    }
  }


  // ################################################################################################
  // Autoupdate
  @Override
  public void addAutoupdater(ActionListener al, ChangeListener cl, DocumentListener dl,
      ColorChangedListener ccl, ItemListener il) {
    txtLower.getDocument().addDocumentListener(dl);

    txtUpper.getDocument().addDocumentListener(dl);
    txtNoiseLevel.getDocument().addDocumentListener(dl);
    txtSplitPixel.getDocument().addDocumentListener(dl);
    txtMaxDP.getDocument().addDocumentListener(dl);
    txtNPixel.getDocument().addDocumentListener(dl);
    comboTransform.addItemListener(il);
    cbCountParticles.addItemListener(il);
    comboReduction.addItemListener(il);
    cbDecluster.addItemListener(il);
  }

  @Override
  public void addAutoRepainter(ActionListener al, ChangeListener cl, DocumentListener dl,
      ColorChangedListener ccl, ItemListener il) {

  }


  // ################################################################################################
  // LOGIC
  // Paintsclae from Image
  @Override
  public void setAllViaExistingSettings(SingleParticleSettings si) {
    ImageLogicRunner.setIS_UPDATING(false);
    // new reseted ps
    if (si == null) {
      si = new SingleParticleSettings();
      si.resetAll();
    }

    if (si.getWindow() != null) {
      txtLower.setText(String.valueOf(si.getWindow().getLowerBound()));
      txtUpper.setText(String.valueOf(si.getWindow().getUpperBound()));
      txtCenter.setText(String.valueOf(si.getWindow().getCentralValue()));
      txtPM.setText(String.valueOf(si.getWindow().getLength() / 2.0));
    } else {
      txtLower.setText("0");
      txtUpper.setText("0");
      txtCenter.setText("0");
      txtPM.setText("0");
    }

    comboReduction.setSelectedItem(si.getReductionMode());

    cbDecluster.setSelected(si.isApplyDeclustering());
    cbCountParticles.setSelected(si.isCountPixel());
    comboTransform.setSelectedItem(si.getTransform());
    txtNoiseLevel.setText(String.valueOf(si.getNoiseLevel()));
    txtSplitPixel.setText(String.valueOf(si.getSplitPixel()));
    txtMaxDP.setText(String.valueOf(si.getMaxAllowedDP()));
    txtNPixel.setText(String.valueOf(si.getNumberOfPixel()));
    // stop internal document listener
    ddlCenterPM.stop();
    // finished
    ImageLogicRunner.setIS_UPDATING(true);
    // ImageEditorWindow.getEditor().fireUpdateEvent(true);
  }

  @Override
  public SingleParticleSettings writeAllToSettings(SingleParticleSettings si) {
    if (si != null) {
      try {
        Range window = new Range(doubleFromTxt(txtLower), doubleFromTxt(txtUpper));
        int splitPixel = intFromTxt(txtSplitPixel);
        int maxDP = intFromTxt(txtMaxDP);
        double noiseLevel = doubleFromTxt(txtNoiseLevel);
        int numberOfPixel = intFromTxt(txtNPixel);
        boolean changed = si.setAll(noiseLevel, splitPixel, maxDP, cbDecluster.isSelected(), window,
            numberOfPixel, cbCountParticles.isSelected(), (Mode) comboReduction.getSelectedItem());

        changed = changed || si.setTransform((Transformation) comboTransform.getSelectedItem());
        if (currentImage != null && changed)
          currentImage.fireIntensityProcessingChanged();
      } catch (Exception ex) {
        logger.error("", ex);
      }
    }
    return si;
  }

  public JButton getBtnOpenDialog() {
    return btnOpenDialog;
  }

  public JTextField getTxtNPixel() {
    return txtNPixel;
  }

  public JCheckBox getCbCountParticles() {
    return cbCountParticles;
  }

  public JComboBox getComboTransform() {
    return comboTransform;
  }

  public JComboBox getComboReduction() {
    return comboReduction;
  }

  public JTextField getTxtMaxDP() {
    return txtMaxDP;
  }

  public JCheckBox getCbDecluster() {
    return cbDecluster;
  }
}
