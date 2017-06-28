package net.rs.lamsi.general.framework.modules;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.framework.modules.interf.SettingsModuleObject;
import net.rs.lamsi.general.settings.SettingsContainerSettings;
import net.rs.lamsi.multiimager.FrameModules.ModuleProjectGroup;

/**
 * the main settings container for each collectable2d subclass (image2d, imageoverlay)
 * holds multiple Collectable2DSettingsModules or HeatmapSettingsModules or basic Modules
 * replaces the title with buttons and auto updating checkbox
 * 
 * @author r_schm33
 *
 * @param <T> Settings
 * @param <S>
 */
public abstract class MainSettingsModuleContainer<T extends SettingsContainerSettings, S extends Collectable2D> 
extends SettingsModuleContainer<T, S> implements SettingsModuleObject<S> {


	// settings for project and group name
	// set current collectable 2d
	private ModuleProjectGroup modProjectGroup;
	
	//
	private JPanel pnTitleSettings;
	private JCheckBox cbAuto;

	public MainSettingsModuleContainer(String title, boolean westside, Class settc, Class objclass) { 
		super(title, westside, settc, objclass);
		
		// projec and group
		modProjectGroup = new ModuleProjectGroup();
		addModule(modProjectGroup);
		
		// add buttons to this module
		pnTitleSettings = new JPanel();
		FlowLayout flowLayout = (FlowLayout) pnTitleSettings.getLayout();
		flowLayout.setHgap(4);
		flowLayout.setVgap(0);
		this.getPnTitle().add(pnTitleSettings, BorderLayout.CENTER);
	}

	public MainSettingsModuleContainer(String title, boolean westside, Class settc, Class objclass, boolean addAutoUpdate) { 
		this(title, westside, settc, objclass);
		cbAuto = new JCheckBox("auto");
		cbAuto.setSelected(true);
		pnTitleSettings.add(cbAuto);
	}	
	
	public JCheckBox getcbAutoUpdating() {
		return cbAuto;
	}
	public boolean isAutoUpdating() {
		return cbAuto.isSelected();
	}
	public void setAutoUpdating(boolean state) {
		cbAuto.setSelected(state);
	}

	public JPanel getPnTitleCenter() {
		return pnTitleSettings;
	}
}
