package net.rs.lamsi.general.settings.listener;

import net.rs.lamsi.general.framework.modules.menu.ModuleMenu;
import net.rs.lamsi.general.settings.Settings;

/**
 * Used in {@link SettingsModule} for loading settings in the {@link ModuleMenu} and setting them
 * @author vukmir69
 *
 */
public interface SettingsChangedListener {
	public void settingsChanged(Settings settings);
}
