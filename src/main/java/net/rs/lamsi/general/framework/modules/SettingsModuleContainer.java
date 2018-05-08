package net.rs.lamsi.general.framework.modules;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.framework.listener.ColorChangedListener;
import net.rs.lamsi.general.framework.modules.interf.SettingsModuleObject;
import net.rs.lamsi.general.heatmap.Heatmap;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.general.settings.SettingsContainerSettings;
import net.rs.lamsi.multiimager.Frames.ImageLogicRunner;

/**
 * holds multiple Collectable2DSettingsModules or HeatmapSettingsModules or basic Modules
 * 
 * @author r_schm33
 *
 * @param <T> Settings
 * @param <S>
 */
public abstract class SettingsModuleContainer<T extends SettingsContainerSettings, S extends Collectable2D>
    extends Collectable2DSettingsModule<T, S> implements SettingsModuleObject<S> {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  // panel for settings
  protected JPanel gridsettings;
  private JScrollPane scrollPane;

  // list of all Modules
  protected ArrayList<Module> listSettingsModules = new ArrayList<Module>();

  public SettingsModuleContainer(String title, boolean westside, Class settc, Class objclass) {
    super(title, westside, settc, objclass);

    scrollPane = new JScrollPane();
    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    this.getPnContent().add(scrollPane, BorderLayout.EAST);

    gridsettings = new JPanel();
    gridsettings.setAlignmentY(0.0f);
    gridsettings.setAlignmentX(0.0f);
    scrollPane.setViewportView(gridsettings);
    scrollPane.getVerticalScrollBar().setUnitIncrement(25);
    gridsettings.setLayout(new BoxLayout(gridsettings, BoxLayout.Y_AXIS));
  }

  public void setVScrollBar(boolean state) {
    if (state) {
      this.getPnContent().removeAll();
      this.getPnContent().add(scrollPane, BorderLayout.CENTER);
      scrollPane.setViewportView(gridsettings);
    } else {
      this.getPnContent().removeAll();
      this.getPnContent().add(gridsettings, BorderLayout.CENTER);
    }
    revalidate();
    repaint();
  }

  /**
   * add the module to the current layout of the panel and to the list of modules
   * 
   * @param mod
   */
  public void addModule(Module mod) {
    gridsettings.add(mod);
    listSettingsModules.add(mod);
  }

  /**
   * 
   * @param modclass
   * @return this module or a sub module
   */
  public Module getModuleByClass(Class modclass) {
    // TODO -- add other settings here
    if (this.getClass().isAssignableFrom(modclass))
      return this;
    else {
      for (Module s : this.listSettingsModules)
        if (s != null)
          if (modclass.isInstance(s))
            return s;
    }
    return null;
  }

  // ################################################################################################
  // Autoupdate
  @Override
  public void addAutoupdater(ActionListener al, ChangeListener cl, DocumentListener dl,
      ColorChangedListener ccl, ItemListener il) {
    for (Module m : listSettingsModules) {
      if (SettingsModule.class.isInstance(m))
        ((SettingsModule) m).addAutoupdater(al, cl, dl, ccl, il);
    }
  }

  @Override
  public void addAutoRepainter(ActionListener al, ChangeListener cl, DocumentListener dl,
      ColorChangedListener ccl, ItemListener il) {
    for (Module m : listSettingsModules) {
      if (SettingsModule.class.isInstance(m))
        ((SettingsModule) m).addAutoRepainter(al, cl, dl, ccl, il);
    }
  }

  // ################################################################################################
  // LOGIC
  @Override
  public void setAllViaExistingSettings(T st) throws Exception {
    if (st != null) {
      ImageLogicRunner.setIS_UPDATING(false);
      // new reseted ps
      for (Module m : listSettingsModules) {
        if (SettingsModule.class.isInstance(m)) {
          SettingsModule sm = ((SettingsModule) m);
          // try to find settings in collection2d
          Settings sett = getCurrentImage().getSettingsByClass(sm.getSettingsClass());
          if (sett == null) {
            // try to find in parent settings
            sett = st.getSettingsByClass(sm.getSettingsClass());
          }

          if (sett != null) {
            sm.setAllViaExistingSettings(sett);
            sm.setVisible(true);
          } else {
            sm.setVisible(false);
            logger.warn("No Settings for {}", sm.getSettingsClass());
          }
        }
      }
      // finished
      ImageLogicRunner.setIS_UPDATING(true);
      // ImageEditorWindow.getEditor().fireUpdateEvent(true);
    }
  }

  @Override
  public T writeAllToSettings(T st) {
    if (st != null) {
      try {
        for (Module m : listSettingsModules) {
          if (SettingsModule.class.isInstance(m)) {
            SettingsModule sm = ((SettingsModule) m);
            Settings sett = getCurrentImage().getSettingsByClass(sm.getSettingsClass());
            if (sett == null) {
              // try to find in parent settings
              sett = st.getSettingsByClass(sm.getSettingsClass());
            }

            sm.writeAllToSettings(sett);
          }
        }
      } catch (Exception ex) {
        logger.error("",ex);
      }
    }
    return st;
  }

  @Override
  public void setCurrentHeatmap(Heatmap heat) {
    for (Module m : listSettingsModules) {
      if (HeatmapSettingsModule.class.isInstance(m)) {
        HeatmapSettingsModule sm = ((HeatmapSettingsModule) m);

        sm.setCurrentHeatmap(heat);
      }
    }
    super.setCurrentHeatmap(heat);
  }

  /**
   * setAllToPanel specifies that first: all images are set without showing settings on panel then
   * the current image is set for this container and only then all settings are flushed onto the
   * panels
   */
  @Override
  public void setCurrentImage(S img, boolean setAllToPanel) {
    for (Module m : listSettingsModules) {
      if (SettingsModuleObject.class.isInstance(m)) {
        SettingsModuleObject sm = ((SettingsModuleObject) m);
        sm.setCurrentImage(img, false);
      }
    }
    super.setCurrentImage(img, setAllToPanel);
  }

  @Override
  public void setSettings(T settings, boolean setAllToPanel) {
    super.setSettings(settings, setAllToPanel);
    // set settings to all SettingsModules
    for (Module m : listSettingsModules) {
      if (SettingsModule.class.isInstance(m) && !Collectable2DSettingsModule.class.isInstance(m)) {
        SettingsModule sm = ((SettingsModule) m);
        // try to find settings in collection2d
        Settings sett = settings.getSettingsByClass(sm.getSettingsClass());

        if (sett != null) {
          sm.setSettings(sett, false);
        }
      }
    }
  }

}
