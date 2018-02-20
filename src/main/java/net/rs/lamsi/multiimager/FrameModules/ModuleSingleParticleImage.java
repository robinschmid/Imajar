package net.rs.lamsi.multiimager.FrameModules;


import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;
import javax.swing.JButton;
import net.rs.lamsi.general.datamodel.image.SingleParticleImage;
import net.rs.lamsi.general.framework.modules.MainSettingsModuleContainer;
import net.rs.lamsi.general.settings.image.SettingsSPImage;
import net.rs.lamsi.multiimager.FrameModules.sub.ModuleBackgroundImg;
import net.rs.lamsi.multiimager.FrameModules.sub.ModuleGeneralCollectable2D;
import net.rs.lamsi.multiimager.FrameModules.sub.ModuleZoom;
import net.rs.lamsi.multiimager.FrameModules.sub.dataoperations.ModuleSPImage;
import net.rs.lamsi.multiimager.FrameModules.sub.theme.ModuleThemes;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;

public class ModuleSingleParticleImage
    extends MainSettingsModuleContainer<SettingsSPImage, SingleParticleImage> {
  private ImageEditorWindow window;

  private ModuleGeneralCollectable2D moduleGeneral;
  private ModuleZoom moduleZoom;
  private ModuleThemes moduleThemes;
  private ModuleBackgroundImg moduleBG;
  private ModuleSPImage moduleSPImage;
  //

  /**
   * Create the panel.
   */
  public ModuleSingleParticleImage(ImageEditorWindow wnd) {
    this(wnd, true, null);
  }

  public ModuleSingleParticleImage(ImageEditorWindow wnd, boolean applyToAll,
      Consumer<ActionEvent> updateConsumer) {
    super("", false, SettingsSPImage.class, SingleParticleImage.class, true);
    window = wnd;

    if (applyToAll) {
      JButton btnApplySettingsToAll = new JButton("apply to all");
      btnApplySettingsToAll.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          window.getLogicRunner().applySettingsToAllImagesInList();
        }
      });
      getPnTitleCenter().add(btnApplySettingsToAll);
    }

    JButton btnUpdate = new JButton("update");
    btnUpdate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (updateConsumer != null)
          updateConsumer.accept(e);
        else
          window.writeAllSettingsFromModules(false);
      }
    });
    getPnTitleCenter().add(btnUpdate);

    moduleGeneral = new ModuleGeneralCollectable2D();
    moduleGeneral.setAlignmentY(Component.TOP_ALIGNMENT);
    addModule(moduleGeneral);

    moduleSPImage = new ModuleSPImage();
    addModule(moduleSPImage);

    moduleZoom = new ModuleZoom();
    addModule(moduleZoom);

    moduleBG = new ModuleBackgroundImg();
    addModule(moduleBG);

    moduleThemes = new ModuleThemes();
    addModule(moduleThemes);
  }



  // ################################################################################################
  // GETTERS AND SETTERS

  public ModuleZoom getModuleZoom() {
    return moduleZoom;
  }

  public ModuleThemes getModuleThemes() {
    return moduleThemes;
  }

  public ModuleBackgroundImg getModuleBackground() {
    return moduleBG;
  }

  public ModuleSPImage getModuleSPImage() {
    return moduleSPImage;
  }

  public ModuleGeneralCollectable2D getModuleGeneral() {
    return moduleGeneral;
  }
}
