package net.rs.lamsi.multiimager.FrameModules.sub.dataoperations;


import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.jfree.data.Range;
import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.general.datamodel.image.SingleParticleImage;
import net.rs.lamsi.general.framework.listener.ColorChangedListener;
import net.rs.lamsi.general.framework.listener.DelayedDocumentListener;
import net.rs.lamsi.general.framework.modules.Collectable2DSettingsModule;
import net.rs.lamsi.general.heatmap.Heatmap;
import net.rs.lamsi.general.myfreechart.listener.history.ZoomHistory;
import net.rs.lamsi.general.settings.image.special.SingleParticleSettings;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow.LOG;
import net.rs.lamsi.multiimager.Frames.ImageLogicRunner;
import net.rs.lamsi.multiimager.Frames.dialogs.singleparticle.SingleParticleDialog;

public class ModuleSPImage
    extends Collectable2DSettingsModule<SingleParticleSettings, SingleParticleImage> {
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

  // AUTOGEN

  /**
   * Create the panel.
   */
  public ModuleSPImage() {
    super("Single particle", false, SingleParticleSettings.class, SingleParticleImage.class);
    getLbTitle().setText("Single Particle");

    JPanel panel = new JPanel();
    getPnContent().add(panel, BorderLayout.CENTER);
    panel.setLayout(new MigLayout("", "[][][][][grow]", "[][][][][][][]"));

    btnOpenDialog = new JButton("Open dialog");
    btnOpenDialog.addActionListener(e -> openSingleParticleDialog());
    panel.add(btnOpenDialog, "cell 1 0,growx");

    lblWindow = new JLabel("window");
    panel.add(lblWindow, "cell 0 1,alignx trailing");

    txtLower = new JTextField();
    txtLower.setHorizontalAlignment(SwingConstants.RIGHT);
    panel.add(txtLower, "flowx,cell 1 1,growx,aligny top");
    txtLower.setColumns(12);

    JLabel label = new JLabel("-");
    panel.add(label, "cell 2 1,alignx center");

    txtUpper = new JTextField();
    txtUpper.setHorizontalAlignment(SwingConstants.RIGHT);
    panel.add(txtUpper, "cell 3 1,growx");
    txtUpper.setColumns(12);

    label_1 = new JLabel("window");
    panel.add(label_1, "cell 0 2,alignx trailing");

    txtCenter = new JTextField();
    txtCenter.setToolTipText("Center value");
    txtCenter.setHorizontalAlignment(SwingConstants.RIGHT);
    txtCenter.setColumns(7);
    panel.add(txtCenter, "flowx,cell 1 2,growx");

    JLabel lblu = new JLabel("\u00B1");
    panel.add(lblu, "cell 2 2,alignx trailing");

    txtPM = new JTextField();
    txtPM.setToolTipText("Plus minus range around the center value");
    txtPM.setHorizontalAlignment(SwingConstants.RIGHT);
    txtPM.setColumns(10);
    panel.add(txtPM, "cell 3 2,growx");

    lblCountInPixel = new JLabel("count in pixel");
    panel.add(lblCountInPixel, "cell 0 3,alignx trailing");

    txtNPixel = new JTextField();
    txtNPixel.setText("1000");
    panel.add(txtNPixel, "cell 1 3,growx");
    txtNPixel.setColumns(10);

    lblSplitPixelFilter = new JLabel("Split pixel filter:");
    lblSplitPixelFilter.setFont(new Font("Tahoma", Font.BOLD, 11));
    panel.add(lblSplitPixelFilter, "cell 0 4 2 1");

    lblNoiseLevel = new JLabel("noise level");
    panel.add(lblNoiseLevel, "cell 0 5,alignx right");

    txtNoiseLevel = new JTextField();
    txtNoiseLevel.setText("0");
    txtNoiseLevel.setToolTipText("Noise level intensity as threshold");
    panel.add(txtNoiseLevel, "cell 1 5,growx");
    txtNoiseLevel.setColumns(10);

    lblSplitEventPixel = new JLabel("split event pixel");
    panel.add(lblSplitEventPixel, "cell 0 6,alignx right");

    txtSplitPixel = new JTextField();
    txtSplitPixel.setToolTipText(
        "How many pixels should be recognised as split particle events? (integers)");
    txtSplitPixel.setText("2");
    panel.add(txtSplitPixel, "cell 1 6,growx");
    txtSplitPixel.setColumns(10);


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
          ImageEditorWindow.log(e2.getMessage(), LOG.ERROR);
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
    txtNPixel.getDocument().addDocumentListener(dl);
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

    txtNoiseLevel.setText(String.valueOf(si.getNoiseLevel()));
    txtSplitPixel.setText(String.valueOf(si.getSplitPixel()));
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
        double noiseLevel = doubleFromTxt(txtNoiseLevel);
        int numberOfPixel = intFromTxt(txtNPixel);
        boolean changed = si.setAll(noiseLevel, splitPixel, window, numberOfPixel);
        if (currentImage != null && changed)
          currentImage.fireIntensityProcessingChanged();
      } catch (Exception ex) {
        ex.printStackTrace();
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
}
