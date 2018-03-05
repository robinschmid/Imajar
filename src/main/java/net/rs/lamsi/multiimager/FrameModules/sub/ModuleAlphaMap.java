package net.rs.lamsi.multiimager.FrameModules.sub;


import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;
import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.framework.listener.ColorChangedListener;
import net.rs.lamsi.general.framework.modules.Collectable2DSettingsModule;
import net.rs.lamsi.general.settings.image.visualisation.SettingsAlphaMap;
import net.rs.lamsi.multiimager.Frames.ImageLogicRunner;

public class ModuleAlphaMap extends Collectable2DSettingsModule<SettingsAlphaMap, Collectable2D> {
  private JTextField txtAlpha;
  private JLabel lbPercentage;
  private JLabel lbTotalDP;
  private JLabel lbFalseDP;
  //

  /**
   * Create the panel.
   */
  public ModuleAlphaMap() {
    super("Alpha map", true, false, SettingsAlphaMap.class, Collectable2D.class);

    JPanel panel = new JPanel();
    getPnContent().add(panel, BorderLayout.CENTER);
    panel.setLayout(new MigLayout("", "[][][grow]", "[][][][]"));

    JLabel lblAlpha = new JLabel("alpha");
    panel.add(lblAlpha, "cell 0 0,alignx trailing");

    txtAlpha = new JTextField();
    txtAlpha.setToolTipText("Alpha value 0.0 - 1.0 : transparent - visible ");
    txtAlpha.setText("1");
    panel.add(txtAlpha, "cell 1 0 2 1,alignx left,aligny top");
    txtAlpha.setColumns(10);

    JLabel lblExcluded = new JLabel("excluded data points");
    panel.add(lblExcluded, "cell 0 1 2 1,alignx left");

    lbFalseDP = new JLabel("0");
    panel.add(lbFalseDP, "cell 2 1");

    JLabel lblTotalDataPoints = new JLabel("total data points");
    panel.add(lblTotalDataPoints, "cell 0 2 2 1,alignx right");

    lbTotalDP = new JLabel("0");
    panel.add(lbTotalDP, "cell 2 2");

    JLabel lblPercentage = new JLabel("percentage");
    panel.add(lblPercentage, "cell 0 3 2 1,alignx right");

    lbPercentage = new JLabel("0");
    panel.add(lbPercentage, "cell 2 3");

    setMaxPresets(15);
  }

  // ################################################################################################
  // Autoupdate
  @Override
  public void addAutoupdater(ActionListener al, ChangeListener cl, DocumentListener dl,
      ColorChangedListener ccl, ItemListener il) {}

  @Override
  public void addAutoRepainter(ActionListener al, ChangeListener cl, DocumentListener dl,
      ColorChangedListener ccl, ItemListener il) {
    txtAlpha.getDocument().addDocumentListener(dl);
    getCbActiveAlphaMap().addItemListener(il);
  }

  // ################################################################################################
  // LOGIC
  // Paintsclae from Image
  @Override
  public void setAllViaExistingSettings(SettingsAlphaMap si) {
    ImageLogicRunner.setIS_UPDATING(false);
    // new reseted ps
    if (si == null) {
      si = new SettingsAlphaMap();
      si.resetAll();
    }

    int total = si.getRealsize();
    int f = si.getFalseCount();
    double p = f / (double) total * 100.0;

    getCbActiveAlphaMap().setSelected(si.isActive());
    getLbFalseDP().setText(String.valueOf(f));
    getLbTotalDP().setText(String.valueOf(total));
    getLbPercentage().setText(String.format("0.00", p));
    getTxtAlpha().setText(String.valueOf(si.getAlpha()));

    // finished
    ImageLogicRunner.setIS_UPDATING(true);
  }

  @Override
  public SettingsAlphaMap writeAllToSettings(SettingsAlphaMap si) {
    if (si != null) {
      try {
        // changed?
        boolean changed = false;
        changed = si.setActive(getCbActiveAlphaMap().isSelected()) || changed;
        changed = si.setAlpha(floatFromTxt(txtAlpha)) || changed;

      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    return si;
  }

  public JLabel getLbPercentage() {
    return lbPercentage;
  }

  public JLabel getLbTotalDP() {
    return lbTotalDP;
  }

  public JLabel getLbFalseDP() {
    return lbFalseDP;
  }

  public JTextField getTxtAlpha() {
    return txtAlpha;
  }

  public JCheckBox getCbActiveAlphaMap() {
    return getCbTitle();
  }
}
