package net.rs.lamsi.massimager.Settings.listener;

import net.rs.lamsi.massimager.Frames.FrameWork.modules.menu.ModuleMenu;
import net.rs.lamsi.massimager.Settings.Settings;

/**
 * Used in {@link SettingsModule} for loading settings in the {@link ModuleMenu} and setting them
 * @author vukmir69
 *
 */
public interface SettingsChangedListener {
	public void settingsChanged(Settings settings);
}
