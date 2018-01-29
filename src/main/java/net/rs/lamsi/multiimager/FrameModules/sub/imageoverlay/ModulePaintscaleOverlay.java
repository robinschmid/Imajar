package net.rs.lamsi.multiimager.FrameModules.sub.imageoverlay;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;
import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.general.datamodel.image.ImageOverlay;
import net.rs.lamsi.general.framework.listener.ColorChangedListener;
import net.rs.lamsi.general.framework.modules.Collectable2DSettingsModule;
import net.rs.lamsi.general.framework.modules.menu.ModuleMenu;
import net.rs.lamsi.general.settings.image.SettingsImageOverlay;
import net.rs.lamsi.general.settings.image.needy.SettingsGroupItemSelections;
import net.rs.lamsi.general.settings.image.visualisation.SettingsPaintScale;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageLogicRunner;
import net.rs.lamsi.utils.useful.graphics2d.blending.BlendComposite;
import net.rs.lamsi.utils.useful.graphics2d.blending.BlendComposite.BlendingMode;

public class ModulePaintscaleOverlay
    extends Collectable2DSettingsModule<SettingsImageOverlay, ImageOverlay> {
  // ################################################################################################
  // MY STUFF

  private static int ICON_WIDTH = 100;
  private JTabbedPane tabbedPaintScales;
  private final ButtonGroup buttonGroup = new ButtonGroup();
  private final ButtonGroup buttonGroup_1 = new ButtonGroup();
  private JPanel panel_1;

  private DecimalFormat formatAbs = new DecimalFormat("#.###");
  private DecimalFormat formatAbsSmall = new DecimalFormat("#.######");
  private DecimalFormat formatPerc = new DecimalFormat("#.####");

  private final Border errorBorder = BorderFactory.createLineBorder(Color.red, 3);
  private final Border emptyBorder = BorderFactory.createEmptyBorder();

  private ModulePaintscaleOverlaySub[] modPSList;
  //
  private ActionListener al;
  private ChangeListener cl;
  private DocumentListener dl;
  private ColorChangedListener ccl;
  private ItemListener il;
  private JComboBox comboBox;
  private JTextField txtAlphaBlending;
  private JLabel lblAlpha;

  /**
   * Create the panel.
   */
  public ModulePaintscaleOverlay() {
    super("Paintscale", false, SettingsImageOverlay.class, ImageOverlay.class);
    getPnContent().setLayout(new MigLayout("", "[grow][188px,grow][grow]", "[][]"));

    formatAbs.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    formatAbsSmall.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    formatPerc.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));

    JPanel panel = new JPanel();
    getPnContent().add(panel, "cell 1 0,grow");
    panel.setLayout(new MigLayout("", "[][]", "[]"));

    comboBox = new JComboBox();
    comboBox.setModel(
        new DefaultComboBoxModel(new BlendingMode[] {BlendingMode.ADD, BlendingMode.NORMAL}));
    comboBox.setToolTipText("Blending mode");
    panel.add(comboBox, "cell 0 0,growx");

    txtAlphaBlending = new JTextField();
    txtAlphaBlending.setToolTipText("Alpha (transparency) for the chosen blending mode.");
    txtAlphaBlending.setHorizontalAlignment(SwingConstants.RIGHT);
    txtAlphaBlending.setText("1");
    panel.add(txtAlphaBlending, "flowx,cell 1 0,alignx left");
    txtAlphaBlending.setColumns(5);

    lblAlpha = new JLabel("alpha (0-1)");
    panel.add(lblAlpha, "cell 1 0");

    panel_1 = new JPanel();
    getPnContent().add(panel_1, "cell 1 1,grow");
    panel_1.setLayout(new BorderLayout(0, 0));

    tabbedPaintScales = new JTabbedPane(JTabbedPane.TOP);
    panel_1.add(tabbedPaintScales, BorderLayout.CENTER);


    // add standard paintscales to menu
    // TODO comment out for window build
    addStandardPaintScalesToMenu();
  }



  /*
   * 1. Grey 2. R , G, B 3. Black-blue-red 4. Black-blue-red-white 5. Black-blue-green-white 6.
   * Black-red-yellow-white
   */
  private void addStandardPaintScalesToMenu() {
    ModuleMenu menu = getPopupMenu();
    menu.addSeparator();
    setPresetindex(5);

    // addPreset(menu, SettingsPaintScale.createSettings(SettingsPaintScale.S_GREY), "Grey");

    setPresetindex(4);
  }

  // TODO distribution
  @Override
  public void setCurrentImage(ImageOverlay img, boolean setAllToPanel) {
    super.setCurrentImage(img, setAllToPanel);
    // set all images to all paintscale modules
    // TODO
    // create new
    SettingsGroupItemSelections sett = img.getSettings().getSettGroupItemSelections();
    if (modPSList == null || modPSList.length != img.size()) {

      tabbedPaintScales.removeAll();
      modPSList = new ModulePaintscaleOverlaySub[img.size()];

      for (int i = 0; i < img.size(); i++) {
        modPSList[i] = new ModulePaintscaleOverlaySub();
        modPSList[i].addAutoupdater(al, cl, dl, ccl, il);

        modPSList[i].setCurrentImage(img.get(i), setAllToPanel);
        modPSList[i].setSettings(img.getSettings().getSettPaintScale(i), setAllToPanel);

        if (sett.isActive(img.get(i))) {
          modPSList[i].setVisible(true);
          tabbedPaintScales.add(img.get(i).getTitle(), modPSList[i]);
        } else
          modPSList[i].setVisible(false);
      }
    } else {
      for (int i = 0; i < img.size(); i++) {
        modPSList[i].setCurrentImage(img.get(i), setAllToPanel);
        modPSList[i].setSettings(img.getSettings().getSettPaintScale(i), setAllToPanel);
      }
    }
  }

  @Override
  public JMenuItem addPreset(ModuleMenu menu, final SettingsImageOverlay settings, String title) {
    // menuitem
    JMenuItem item = super.addPreset(menu, settings, title);
    // icon for paintscale TODO
    // PaintscaleIcon icon = new
    // PaintscaleIcon(PaintScaleGenerator.generateStepPaintScaleForLegend(0, 100, settings),
    // ICON_WIDTH, 15, true);
    // item.setIcon(icon);
    return item;
  }

  // ################################################################################################
  // Autoupdate TODO
  @Override
  public void addAutoupdater(final ActionListener al, ChangeListener cl, DocumentListener dl,
      ColorChangedListener ccl, ItemListener il) {
    getComboBlend().addItemListener(il);
    getTxtAlphaBlending().getDocument().addDocumentListener(dl);

    this.al = al;
    this.cl = cl;
    this.dl = dl;
    this.ccl = ccl;
    this.il = il;
  }

  @Override
  public void addAutoRepainter(ActionListener al, ChangeListener cl, DocumentListener dl,
      ColorChangedListener ccl, ItemListener il) {}


  // ################################################################################################
  // LOGIC
  // Paintsclae from Image
  @Override
  public void setAllViaExistingSettings(SettingsImageOverlay sett) {
    ImageLogicRunner.setIS_UPDATING(false);

    // blend
    BlendComposite blend = sett.getBlend();
    getComboBlend().setSelectedItem(blend.getMode());
    getTxtAlphaBlending().setText(formatPercentNumber(blend.getAlpha()));

    int lastSelection = tabbedPaintScales.getSelectedIndex();
    tabbedPaintScales.removeAll();
    // rb
    for (int i = 0; i < modPSList.length; i++) {
      SettingsPaintScale ps2 = sett.getSettPaintScale(i);
      modPSList[i].setAllViaExistingSettings(ps2);

      if (sett.isActive(modPSList[i].getCurrentImage())) {
        modPSList[i].setVisible(true);
        tabbedPaintScales.add(modPSList[i].getCurrentImage().getTitle(), modPSList[i]);
      } else
        modPSList[i].setVisible(false);
    }
    if (tabbedPaintScales.getTabCount() - 1 < lastSelection)
      lastSelection = tabbedPaintScales.getTabCount() - 1;
    tabbedPaintScales.setSelectedIndex(lastSelection);
    tabbedPaintScales.revalidate();
    tabbedPaintScales.repaint();
    // finished
    ImageLogicRunner.setIS_UPDATING(true);
    ImageEditorWindow.getEditor().fireUpdateEvent(true);
  }

  @Override
  public SettingsImageOverlay writeAllToSettings(SettingsImageOverlay sett) {
    if (sett != null) {
      try {
        // selections changed?
        boolean changed = false;

        for (int i = 0; i < modPSList.length && !changed; i++) {
          if (!sett.isActive(modPSList[i].getCurrentImage()).equals(modPSList[i].isVisible())) {
            changed = true;
          }
        }

        if (changed) {
          tabbedPaintScales.removeAll();
          // rb
          for (int i = 0; i < modPSList.length; i++) {
            if (sett.isActive(modPSList[i].getCurrentImage())) {
              modPSList[i].setVisible(true);
              tabbedPaintScales.add(modPSList[i].getCurrentImage().getTitle(), modPSList[i]);
            } else
              modPSList[i].setVisible(false);
          }
        }
        // tabbedPaintScales.revalidate();
        // tabbedPaintScales.repaint();
        // }
        // blend
        BlendComposite blend = BlendComposite.getInstance(
            (BlendingMode) getComboBlend().getSelectedItem(), floatFromTxt(getTxtAlphaBlending()));
        sett.setBlend(blend);

        boolean inverted = true;
        boolean black = true;
        boolean white = false;
        int levels = 255;

        // paint scales
        for (int i = 0; i < modPSList.length; i++) {
          SettingsPaintScale ps = sett.getSettPaintScale(i);
          modPSList[i].writeAllToSettings(ps);

          ps.setInverted(inverted);
          ps.setUsesBAsMax(black);
          ps.setUsesWAsMin(white);
          ps.setLevels(levels);
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    return sett;
  }


  private String formatAbsNumber(double in) {
    return in > 10 ? formatAbs.format(in) : formatAbsSmall.format(in);
  }

  private String formatPercentNumber(double in) {
    return formatPerc.format(in);
  }

  private String formatPercentNumber(float in) {
    return formatPerc.format(in);
  }

  // ################################################################################################
  // GETTERS AND SETTERS
  public JTextField getTxtAlphaBlending() {
    return txtAlphaBlending;
  }

  public JComboBox getComboBlend() {
    return comboBox;
  }
}
