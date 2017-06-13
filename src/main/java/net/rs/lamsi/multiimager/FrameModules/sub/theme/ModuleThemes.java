package net.rs.lamsi.multiimager.FrameModules.sub.theme;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ResourceBundle;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jfree.chart.editor.ChartEditor;
import org.jfree.chart.editor.ChartEditorManager;
import org.jfree.chart.util.ResourceBundleWrapper;

import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.framework.basics.ColorChangedListener;
import net.rs.lamsi.general.framework.basics.JColorPickerButton;
import net.rs.lamsi.general.framework.basics.JFontSpecs;
import net.rs.lamsi.general.framework.listener.DelayedDocumentListener;
import net.rs.lamsi.general.framework.modules.Module;
import net.rs.lamsi.general.framework.modules.SettingsModule;
import net.rs.lamsi.general.framework.modules.SettingsModuleContainer;
import net.rs.lamsi.general.framework.modules.menu.ModuleMenu;
import net.rs.lamsi.general.myfreechart.themes.ChartThemeFactory;
import net.rs.lamsi.general.myfreechart.themes.ChartThemeFactory.THEME;
import net.rs.lamsi.general.myfreechart.themes.MyStandardChartTheme;
import net.rs.lamsi.general.settings.image.visualisation.themes.SettingsScaleInPlot;
import net.rs.lamsi.general.settings.image.visualisation.themes.SettingsThemesContainer;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageLogicRunner;

public class ModuleThemes extends SettingsModuleContainer<SettingsThemesContainer, Collectable2D> {
	/**
	 * Create the panel.
	 */
	public ModuleThemes() {
		super("Themes", false, SettingsThemesContainer.class, Collectable2D.class);    
		getLbTitle().setText("Themes");
		
		setVScrollBar(false);
		
		ModuleGeneralTheme modTheme = new ModuleGeneralTheme();
		addModule(modTheme);
		
		ModuleScaleInPlot modScaleInPlot = new ModuleScaleInPlot();
		addModule(modScaleInPlot);
		
	}
	
	/**
	 * update after settings dialog was closed
	 * @param editor
	 */
	protected void updateFromEditorDialog() {
		// TODO
		// update theme settings and then all components in this panel 
		// update via chart not via editor
	}

	// apply for print or presentation before changing settings
	@Override
	public void setSettings(SettingsThemesContainer settings) {
		ChartThemeFactory.setStandardTheme(settings.getID());
		super.setSettings(settings);
	}
	//################################################################################################
	// GETTERS AND SETTERS 
}
