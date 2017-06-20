package net.rs.lamsi.general.framework.modules;

import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;

import net.rs.lamsi.general.framework.listener.ColorChangedListener;
import net.rs.lamsi.general.settings.Settings;

public class ModuleSelectGroupItems extends SettingsModule<Settings> {

	public ModuleSelectGroupItems(String title, boolean westside, Class csettings) {
		super(title, westside, csettings);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addAutoupdater(ActionListener al, ChangeListener cl, DocumentListener dl, ColorChangedListener ccl, ItemListener il) {
	}

	@Override
	public void addAutoRepainter(ActionListener al, ChangeListener cl, DocumentListener dl, ColorChangedListener ccl, ItemListener il) {
	}

	@Override
	public void setAllViaExistingSettings(Settings si) throws Exception {
	}

	@Override
	public Settings writeAllToSettings(Settings si) {
		return null;
	}
}
