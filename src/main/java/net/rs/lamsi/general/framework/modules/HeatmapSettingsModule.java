package net.rs.lamsi.general.framework.modules;

import net.rs.lamsi.general.heatmap.Heatmap;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;

/**
 * 
 * @author r_schm33
 *
 * @param <T> Settings
 */
public abstract class HeatmapSettingsModule<T extends Settings> extends SettingsModule<T> {

  protected Heatmap currentHeat;

  public HeatmapSettingsModule(String title, boolean westside, Class settc) {
    super(title, westside, settc);
    setShowTitleAlways(true);
  }

  public HeatmapSettingsModule(String title, boolean useCheckBox, boolean westside, Class settc) {
    super(title, useCheckBox, westside, settc);
    setShowTitleAlways(true);
  }

  // ################################################################################################
  // GETTERS and SETTERS

  /**
   * gets called by {@link ImageEditorWindow#addHeatmapToPanel(Heatmap)}
   * 
   * @param heat
   */
  public void setCurrentHeatmap(Heatmap heat) {
    currentHeat = heat;
  }

  public Heatmap getCurrentHeat() {
    return currentHeat;
  }
}
