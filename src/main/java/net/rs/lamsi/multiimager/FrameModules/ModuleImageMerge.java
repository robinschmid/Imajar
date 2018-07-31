package net.rs.lamsi.multiimager.FrameModules;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import net.rs.lamsi.general.datamodel.image.ImageMerge;
import net.rs.lamsi.general.framework.modules.MainSettingsModuleContainer;
import net.rs.lamsi.general.settings.image.SettingsImageMerge;
import net.rs.lamsi.multiimager.FrameModules.sub.ModuleBackgroundImg;
import net.rs.lamsi.multiimager.FrameModules.sub.ModuleZoom;
import net.rs.lamsi.multiimager.FrameModules.sub.merge.ModuleMerge;
import net.rs.lamsi.multiimager.FrameModules.sub.theme.ModuleThemes;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;

public class ModuleImageMerge extends MainSettingsModuleContainer<SettingsImageMerge, ImageMerge> {
  private ImageEditorWindow window;

  private ModuleZoom moduleZoom;
  private ModuleThemes moduleThemes;
  private ModuleBackgroundImg moduleBG;
  private ModuleMerge moduleMerge;

  /**
   * Create the panel.
   */
  public ModuleImageMerge(ImageEditorWindow wnd) {
    super("", false, SettingsImageMerge.class, ImageMerge.class, true);
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

    moduleZoom = new ModuleZoom();
    addModule(moduleZoom);

    moduleMerge = new ModuleMerge();
    addModule(moduleMerge);

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

  public ModuleMerge getModuleMerge() {
    return moduleMerge;
  }
}
