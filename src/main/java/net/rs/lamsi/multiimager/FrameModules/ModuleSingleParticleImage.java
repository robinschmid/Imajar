package net.rs.lamsi.multiimager.FrameModules;


import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import net.rs.lamsi.general.datamodel.image.SingleParticleImage;
import net.rs.lamsi.general.framework.modules.MainSettingsModuleContainer;
import net.rs.lamsi.general.settings.image.SettingsSPImage;
import net.rs.lamsi.multiimager.FrameModules.sub.ModuleBackgroundImg;
import net.rs.lamsi.multiimager.FrameModules.sub.ModuleGeneral;
import net.rs.lamsi.multiimager.FrameModules.sub.ModuleZoom;
import net.rs.lamsi.multiimager.FrameModules.sub.dataoperations.ModuleSPImage;
import net.rs.lamsi.multiimager.FrameModules.sub.dataoperations.ModuleSelectExcludeData;
import net.rs.lamsi.multiimager.FrameModules.sub.theme.ModuleThemes;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;

public class ModuleSingleParticleImage
    extends MainSettingsModuleContainer<SettingsSPImage, SingleParticleImage> {
  private ImageEditorWindow window;

  private ModuleGeneral moduleGeneral;
  private ModuleZoom moduleZoom;
  private ModuleThemes moduleThemes;
  private ModuleBackgroundImg moduleBG;
  private ModuleSelectExcludeData moduleSelect;
  private ModuleSPImage moduleSPImage;
  //

  /**
   * Create the panel.
   */
  public ModuleSingleParticleImage(ImageEditorWindow wnd) {
    super("", false, SettingsSPImage.class, SingleParticleImage.class, true);
    window = wnd;

    JButton btnApplySettingsToAll = new JButton("apply to all");
    btnApplySettingsToAll.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        window.getLogicRunner().applySettingsToAllImagesInList();
      }
    });
    getPnTitleCenter().add(btnApplySettingsToAll);

    JButton btnUpdate = new JButton("update");
    btnUpdate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        window.writeAllSettingsFromModules(false);
      }
    });
    getPnTitleCenter().add(btnUpdate);


    moduleSPImage = new ModuleSPImage();
    addModule(moduleSPImage);

    moduleGeneral = new ModuleGeneral(window);
    moduleGeneral.setAlignmentY(Component.TOP_ALIGNMENT);
    addModule(moduleGeneral);

    moduleZoom = new ModuleZoom();
    addModule(moduleZoom);

    moduleBG = new ModuleBackgroundImg();
    addModule(moduleBG);

    moduleThemes = new ModuleThemes();
    addModule(moduleThemes);

    moduleSelect = new ModuleSelectExcludeData(window);
    addModule(moduleSelect);

    // add all modules for Image settings TODO add all mods
    listSettingsModules.add(moduleGeneral.getModSplitConImg());
  }



  // ################################################################################################
  // GETTERS AND SETTERS
  public ModuleGeneral getModuleGeneral() {
    return moduleGeneral;
  }

  public ModuleZoom getModuleZoom() {
    return moduleZoom;
  }

  public ModuleThemes getModuleThemes() {
    return moduleThemes;
  }

  public ModuleBackgroundImg getModuleBackground() {
    return moduleBG;
  }

  public ModuleSelectExcludeData getModuleSelect() {
    return moduleSelect;
  }

  public ModuleSPImage getModuleSPImage() {
    return moduleSPImage;
  }
}
