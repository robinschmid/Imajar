package net.rs.lamsi.general.settings.image;

import java.util.HashMap;
import java.util.Vector;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.DatasetContinuousMD;
import net.rs.lamsi.general.heatmap.Heatmap;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.general.settings.SettingsContainerSettings;
import net.rs.lamsi.general.settings.image.operations.SettingsImage2DOperations;
import net.rs.lamsi.general.settings.image.operations.quantifier.SettingsImage2DQuantifier;
import net.rs.lamsi.general.settings.image.operations.quantifier.SettingsImage2DQuantifierIS;
import net.rs.lamsi.general.settings.image.operations.quantifier.SettingsImage2DQuantifierLinear;
import net.rs.lamsi.general.settings.image.sub.SettingsGeneralImage;
import net.rs.lamsi.general.settings.image.sub.SettingsImageContinousSplit;
import net.rs.lamsi.general.settings.image.sub.SettingsZoom;
import net.rs.lamsi.general.settings.image.visualisation.SettingsBackgroundImg;
import net.rs.lamsi.general.settings.image.visualisation.SettingsPaintScale;
import net.rs.lamsi.general.settings.image.visualisation.themes.SettingsThemesContainer;
import net.rs.lamsi.utils.mywriterreader.BinaryWriterReader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class SettingsContainerCollectable2D extends SettingsContainerSettings {
	
	// do not change the version!
    private static final long serialVersionUID = 1L;
    // 
	public SettingsContainerCollectable2D(String description, String path,
			String fileEnding) {
		super(description, path, fileEnding);
	}
	 
	public SettingsThemesContainer getSettTheme() {
		return (SettingsThemesContainer) list.get(SettingsThemesContainer.class);
	} 
	public SettingsZoom getSettZoom() {
		return (SettingsZoom) getSettingsByClass(SettingsZoom.class);
	}
}
