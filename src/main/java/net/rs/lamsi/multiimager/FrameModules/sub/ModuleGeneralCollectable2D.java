package net.rs.lamsi.multiimager.FrameModules.sub;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;
import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.framework.basics.JColorPickerButton;
import net.rs.lamsi.general.framework.basics.JFontSpecs;
import net.rs.lamsi.general.framework.listener.ColorChangedListener;
import net.rs.lamsi.general.framework.modules.Collectable2DSettingsModule;
import net.rs.lamsi.general.framework.modules.menu.ModuleMenuApplyToImage;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.general.settings.image.sub.SettingsGeneralCollecable2D;
import net.rs.lamsi.general.settings.image.visualisation.themes.SettingsThemesContainer;
import net.rs.lamsi.multiimager.Frames.ImageLogicRunner;

public class ModuleGeneralCollectable2D
    extends Collectable2DSettingsModule<SettingsGeneralCollecable2D, Collectable2D> {

  // AUTOGEN
  private JTextField txtTitle;
  private final ButtonGroup buttonGroup = new ButtonGroup();
  private final ButtonGroup buttonGroup_1 = new ButtonGroup();
  private JTextField txtShortTitle;
  private JCheckBox cbShortTitle;
  private JTextField txtXPosTitle;
  private JTextField txtYPosTitle;
  private JFontSpecs fontShortTitle;
  private JColorPickerButton colorBGShortTitle;
  private JCheckBox cbKeepAspectRatio;

  /**
   * Create the panel.
   */
  public ModuleGeneralCollectable2D() {
    super("General", false, SettingsGeneralCollecable2D.class, Collectable2D.class);

    JPanel pnNorth = new JPanel();
    getPnContent().add(pnNorth, BorderLayout.NORTH);
    pnNorth.setLayout(new BorderLayout(0, 0));

    JPanel pnTitleANdLaser = new JPanel();
    pnNorth.add(pnTitleANdLaser, BorderLayout.NORTH);
    pnTitleANdLaser.setLayout(new MigLayout("", "[][]", "[][][][]"));

    JLabel lblTitle = new JLabel("title");
    pnTitleANdLaser.add(lblTitle, "cell 0 0,alignx trailing");
    lblTitle.setAlignmentY(Component.TOP_ALIGNMENT);
    lblTitle.setAlignmentX(Component.RIGHT_ALIGNMENT);

    txtTitle = new JTextField();
    pnTitleANdLaser.add(txtTitle, "cell 1 0");
    txtTitle.setAlignmentY(Component.TOP_ALIGNMENT);
    txtTitle.setColumns(10);

    cbShortTitle = new JCheckBox("short title");
    cbShortTitle.setSelected(true);
    cbShortTitle.setHorizontalAlignment(SwingConstants.TRAILING);
    pnTitleANdLaser.add(cbShortTitle, "cell 0 1,alignx right");

    txtShortTitle = new JTextField();
    txtShortTitle.setHorizontalAlignment(SwingConstants.LEFT);
    pnTitleANdLaser.add(txtShortTitle, "flowx,cell 1 1,alignx left");
    txtShortTitle.setColumns(10);

    txtXPosTitle = new JTextField();
    txtXPosTitle.setToolTipText("X position in percent");
    txtXPosTitle.setText("0.9");
    pnTitleANdLaser.add(txtXPosTitle, "cell 1 1");
    txtXPosTitle.setColumns(5);

    txtYPosTitle = new JTextField();
    txtYPosTitle.setToolTipText("Y position in percent");
    txtYPosTitle.setText("0.9");
    txtYPosTitle.setColumns(5);
    pnTitleANdLaser.add(txtYPosTitle, "cell 1 1");


    // all family names of fonts
    fontShortTitle = new JFontSpecs();
    pnTitleANdLaser.add(fontShortTitle, "flowx,cell 0 2 2 1,alignx left");

    colorBGShortTitle = new JColorPickerButton(this);
    pnTitleANdLaser.add(colorBGShortTitle, "cell 0 2 2 1");
    colorBGShortTitle.setColor(Color.BLACK);

    cbKeepAspectRatio = new JCheckBox("keep aspect ratio");
    cbKeepAspectRatio.setToolTipText(
        "Check if X and Y have the same dimension units. Uncheck to get maximum scaling.");
    pnTitleANdLaser.add(cbKeepAspectRatio, "cell 0 3 2 1");

    int size = 100;

    size = 50;

    // apply to Listener
    getPopupMenu().addApplyToImageListener(new ModuleMenuApplyToImage() {
      @Override
      public void applyToImage(Settings sett, Collectable2D img) {
        // also set short title to theme
        img.getSettTheme().getTheme().setShortTitle(fontShortTitle.getColor(),
            getColorBGShortTitle().getColor(), fontShortTitle.getSelectedFont());
      }
    });
  }

  // ################################################################################################
  // Autoupdate
  @Override
  public void addAutoupdater(ActionListener al, ChangeListener cl, DocumentListener dl,
      ColorChangedListener ccl, ItemListener il) {
    getTxtTitle().getDocument().addDocumentListener(dl);
  }

  @Override
  public void addAutoRepainter(ActionListener al, ChangeListener cl, DocumentListener dl,
      ColorChangedListener ccl, ItemListener il) {
    cbShortTitle.addItemListener(il);
    cbKeepAspectRatio.addItemListener(il);
    txtShortTitle.getDocument().addDocumentListener(dl);
    txtXPosTitle.getDocument().addDocumentListener(dl);
    txtYPosTitle.getDocument().addDocumentListener(dl);

    colorBGShortTitle.addColorChangedListener(ccl);
    fontShortTitle.addListener(ccl, il, dl);
  }

  // ################################################################################################
  // LOGIC
  // Paintsclae from Image
  @Override
  public void setAllViaExistingSettings(SettingsGeneralCollecable2D si) {
    ImageLogicRunner.setIS_UPDATING(false);
    // new reseted ps
    if (si != null) {
      // crop to min
      getCbKeepAspectRatio().setSelected(si.isKeepAspectRatio());


      //
      this.getTxtTitle().setText(si.getTitle());
      this.getTxtShortTitle().setText(si.getShortTitle());
      getCbShortTitle().setSelected(si.isShowShortTitle());
      getTxtXPosTitle().setText(String.valueOf(si.getXPosTitle()));
      getTxtYPosTitle().setText(String.valueOf(si.getYPosTitle()));

      SettingsThemesContainer s = currentImage.getSettTheme();
      // font
      fontShortTitle.setSelectedFont(s.getTheme().getFontShortTitle());
      // bg color
      fontShortTitle.setColor(s.getTheme().getcShortTitle());
      colorBGShortTitle.setColor(s.getTheme().getcBGShortTitle());
    }
    // finished
    ImageLogicRunner.setIS_UPDATING(true);
    // ImageEditorWindow.getEditor().fireUpdateEvent(true);
  }

  @Override
  public SettingsGeneralCollecable2D writeAllToSettings(SettingsGeneralCollecable2D settImage) {
    if (settImage != null) {
      try {
        settings.setAll(getTxtTitle().getText(), getTxtShortTitle().getText(),
            cbShortTitle.isSelected(), floatFromTxt(txtXPosTitle), floatFromTxt(txtYPosTitle),
            getCbKeepAspectRatio().isSelected());

        SettingsThemesContainer s = currentImage.getSettTheme();
        s.getTheme().setcShortTitle(fontShortTitle.getColor());
        s.getTheme().setcBGShortTitle(colorBGShortTitle.getColor());
        s.getTheme().setFontShortTitle(fontShortTitle.getSelectedFont());
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    return settImage;
  }


  // ################################################################################################
  // GETTERS AND SETTERS
  public JTextField getTxtTitle() {
    return txtTitle;
  }

  public JTextField getTxtShortTitle() {
    return txtShortTitle;
  }

  public JCheckBox getCbShortTitle() {
    return cbShortTitle;
  }

  public JTextField getTxtXPosTitle() {
    return txtXPosTitle;
  }

  public JTextField getTxtYPosTitle() {
    return txtYPosTitle;
  }

  public JColorPickerButton getColorBGShortTitle() {
    return colorBGShortTitle;
  }


  public void setColorBGShortTitle(JColorPickerButton colorBGShortTitle) {
    this.colorBGShortTitle = colorBGShortTitle;
  }

  public JCheckBox getCbKeepAspectRatio() {
    return cbKeepAspectRatio;
  }

}
