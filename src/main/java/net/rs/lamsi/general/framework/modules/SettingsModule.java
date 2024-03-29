package net.rs.lamsi.general.framework.modules;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.rs.lamsi.general.framework.listener.ColorChangedListener;
import net.rs.lamsi.general.framework.modules.menu.ModuleMenu;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.general.settings.SettingsHolder;
import net.rs.lamsi.general.settings.listener.SettingsChangedListener;
import net.rs.lamsi.utils.DialogLoggerUtil;
import net.rs.lamsi.utils.FileAndPathUtil;
import net.rs.lamsi.utils.mywriterreader.BinaryWriterReader;
import net.rs.lamsi.utils.useful.FileNameExtFilter;


public abstract class SettingsModule<T extends Settings> extends Module
    implements SettingsChangedListener {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  protected final Class classsettings;

  protected T settings;
  protected int presetindex = 4;
  protected ArrayList<JMenuItem> presets;
  protected int maxPresets = -1;

  public SettingsModule(String title, boolean westside, Class csettings) {
    super(title, westside);
    classsettings = csettings;
    createMenu(csettings);
    setShowTitleAlways(true);
  }

  public SettingsModule(String title, boolean useCheckBox, boolean westside, Class csettings) {
    super(title, useCheckBox, westside);
    classsettings = csettings;
    createMenu(csettings);
    setShowTitleAlways(true);
  }

  private void createMenu(Class csettings) {
    ModuleMenu menu = ModuleMenu.createLoadSaveOptionsMenu(this, csettings, this);
    // sep
    menu.addSeparator();
    // load files from directory as presets
    Settings sett = SettingsHolder.getSettings().getSettingsByClass(csettings);

    if (sett != null) {
      File path = new File(FileAndPathUtil.getPathOfJar(), sett.getPathSettingsFile());
      String type = sett.getFileEnding();
      try {
        if (path.exists()) {
          List<File[]> files =
              FileAndPathUtil.findFilesInDir(path, new FileNameExtFilter("", type), false);

          if (files != null && files.size() > 0) {
            // load each file as settings and add to menu as preset
            for (File f : files.get(0)) {
              // load
              try {
                sett.loadFromXML(f);
                Settings load = sett.copy();
                if (load != null)
                  addPreset(menu, (T) load, FileAndPathUtil.eraseFormat(f.getName()));
              } catch (Exception ex) {
                logger.warn("Preset is broken remove from settings directory: \n{}",
                    f.getAbsolutePath(), ex);
              }
            }
          }
        }
      } catch (Exception ex) {
        logger.error("",ex);
      }
    }

    this.addPopupMenu(menu);
  }

  public void addMenu(ModuleMenu menu) {
    // sep
    menu.addSeparator();
    // load files from directory as presets
    Settings sett = SettingsHolder.getSettings().getSettingsByClass(classsettings);

    if (sett != null) {
      File path = new File(FileAndPathUtil.getPathOfJar(), sett.getPathSettingsFile());
      String type = sett.getFileEnding();
      try {
        if (path.exists()) {
          List<File[]> files =
              FileAndPathUtil.findFilesInDir(path, new FileNameExtFilter("", type), false);
          // load each file as settings and add to menu as preset
          for (File f : files.get(0)) {
            // load
            try {
              sett.loadFromXML(f);
              Settings load = sett.copy();
              if (load != null)
                addPreset(menu, (T) load, FileAndPathUtil.eraseFormat(f.getName()));
            } catch (Exception ex) {
              logger.warn("Preset is broken remove from settings directory: \n{}",
                  f.getAbsolutePath(), ex);
            }
          }
        }
      } catch (Exception ex) {
        logger.error("",ex);
      }
    }

    this.addPopupMenu(menu);
  }

  // ################################################################################################
  // Autoupdate
  /**
   * init with listeners for changes of settings in the modules autoUpdater calls SettingsExtraction
   * from modules for creation of a new heatmap
   * 
   * @param al
   * @param cl
   * @param dl
   * @param ccl
   * @param il
   */
  public abstract void addAutoupdater(ActionListener al, ChangeListener cl, DocumentListener dl,
      ColorChangedListener ccl, ItemListener il);

  /**
   * init with listeners for changes of settings in the modules autoUpdater calls SettingsExtraction
   * from modules for REPAINTING the current heatmap
   * 
   * @param al
   * @param cl
   * @param dl
   * @param ccl
   * @param il
   */
  public abstract void addAutoRepainter(ActionListener al, ChangeListener cl, DocumentListener dl,
      ColorChangedListener ccl, ItemListener il);



  /**
   * adds a preset to the menu
   * 
   * @param menu
   * @param settings
   * @param title
   * @return
   */
  public JMenuItem addPreset(ModuleMenu menu, final T settings, String title) {
    if (presets == null || presets.size() == 0 || !presets.get(0).getText().equals(title)) {
      // menuitem
      JMenuItem item = new JMenuItem(title);
      menu.addMenuItem(item, presetindex);
      item.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          try {
            setSettings((T) BinaryWriterReader.deepCopy(settings), true);
          } catch (Exception e1) {
            logger.error("",e1);
          }
        }
      });
      if (presets == null)
        presets = new ArrayList<JMenuItem>();
      presets.add(item);
      if (presets.size() > maxPresets && maxPresets != -1) {
        JMenuItem i = presets.remove(0);
        menu.removeMenuItem(i);
      }
      return item;
    }
    return null;
  }

  /**
   * adds a preset to the menu
   * 
   * @param menu
   * @param settings
   * @param title
   * @return
   */
  public JMenuItem addPreset(final T settings, String title) {
    return addPreset(menu, settings, title);
  }

  /**
   * remove all preset menu items
   */
  public void removeAllPresets() {
    if (presets != null) {
      while (presets.size() > 0) {
        JMenuItem i = presets.remove(0);
        menu.removeMenuItem(i);
      }
    }
  }

  // settings changed via load --> menu
  @SuppressWarnings("unchecked")
  @Override
  public void settingsChanged(Settings settings) {
    this.setSettings((T) settings, true);
  }


  // ################################################################################################
  // LOGIC
  // Paintsclae from Image
  /**
   * set all settings --> panel
   * 
   * @param si
   * @throws Exception
   */
  public abstract void setAllViaExistingSettings(T si) throws Exception;

  /**
   * set all panel --> settings
   * 
   * @return
   */
  public T writeAllToSettings() {
    return writeAllToSettings(settings);
  }

  public abstract T writeAllToSettings(T si);


  // ################################################################################################
  // GETTERS and SETTERS
  public T getSettings() {
    return settings;
  }

  /**
   * gets called by window? like every other method
   * 
   * @param settings
   */
  public void setSettings(T settings, boolean setAllToPanel) {
    this.settings = settings;
    if (settings != null) {
      try {
        if (setAllToPanel)
          setAllViaExistingSettings(settings);
        // transfer to Settingsholder
        // too slow!
        // SettingsHolder.getSettings().replaceSettings((Settings)settings);
      } catch (Exception e) {
        logger.error("",e);
      }
    }
  }

  /**
   * gets called by menu saves settings to file
   */
  public void saveSettings() {
    try {
      Settings s = (Settings) getSettings();
      if (s != null) {
        File f = s.saveSettingsToFile(this);
        File presetpath = new File(FileAndPathUtil.getPathOfJar(), s.getPathSettingsFile());
        // path in presets?
        if (f != null && f.getParentFile().equals(presetpath)) {
          // add to presets
          addPreset(getPopupMenu(), (T) s, FileAndPathUtil.eraseFormat(f.getName()));
        }
      }
    } catch (Exception e1) {
      logger.error("",e1);
      DialogLoggerUtil.showErrorDialog(this, "Error while saving", e1);
    }
  }

  public int getPresetindex() {
    return presetindex;
  }

  public void setPresetindex(int presetindex) {
    this.presetindex = presetindex;
  }

  public Class getSettingsClass() {
    return classsettings;
  }

  public List<JMenuItem> getPresets() {
    return presets;
  }

  public int getMaxPresets() {
    return maxPresets;
  }

  public void setMaxPresets(int maxPresets) {
    this.maxPresets = maxPresets;
  }
}
