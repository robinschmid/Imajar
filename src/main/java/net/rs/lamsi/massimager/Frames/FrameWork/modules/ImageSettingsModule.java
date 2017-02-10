package net.rs.lamsi.massimager.Frames.FrameWork.modules;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;

import loci.formats.FilePattern;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.massimager.Frames.FrameWork.ColorChangedListener;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.menu.ModuleMenu;
import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.massimager.Settings.SettingsHolder;
import net.rs.lamsi.massimager.Settings.listener.SettingsChangedListener;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow.LOG;
import net.rs.lamsi.utils.DialogLoggerUtil;
import net.rs.lamsi.utils.FileAndPathUtil;
import net.rs.lamsi.utils.mywriterreader.BinaryWriterReader;
import net.rs.lamsi.utils.useful.FileNameExtFilter;


public abstract class ImageSettingsModule<T> extends ImageModule implements SettingsChangedListener {
 
	protected Class classsettings;
	protected T settings; 
	protected int presetindex = 4;
	
	public ImageSettingsModule(String title, boolean westside, Class csettings) { 
		super(title, westside);
		classsettings = csettings;
		createMenu(csettings);
		setShowTitleAlways(true);
	}

	private void createMenu(Class csettings) {
		ModuleMenu menu = ModuleMenu.createLoadSaveOptionsMenu(this, csettings, this);
		// sep
		menu.addSeparator();
		// load files from directory as presets
		Settings sett = SettingsHolder.getSettings().getSetByClass(csettings);
		
		if(sett!=null) {
			File path = new File(FileAndPathUtil.getPathOfJar(), sett.getPathSettingsFile());
			String type = sett.getFileEnding();
			try {
				if(path.exists()) {
					Vector<File[]> files = FileAndPathUtil.findFilesInDir(path,  new FileNameExtFilter("", type), false);
					// load each file as settings and add to menu as preset
					for(File f : files.get(0)) {
						// load
						try {
							Settings load = SettingsHolder.getSettings().loadSettingsFromFile(f, sett);
							if(load !=null)
								addPreset(menu, (T)load, FileAndPathUtil.eraseFormat(f.getName()));
						} catch(Exception ex) {
							ImageEditorWindow.log("Preset is broken remove from settings directory: \n"+f.getAbsolutePath(), LOG.WARNING);
						}
					}
				}
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		
		this.addPopupMenu(menu);
	} 
	
	
	
	/**
	 * adds a preset to the menu
	 * @param menu
	 * @param settings
	 * @param title
	 * @return
	 */
	public JMenuItem addPreset(ModuleMenu menu, final T settings, String title) { 
		// menuitem
		JMenuItem item = new JMenuItem(title); 
		menu.addMenuItem(item, presetindex);
		item.addActionListener(new ActionListener() {  
			@Override
			public void actionPerformed(ActionEvent e) { 
				try {
					setSettings((T) BinaryWriterReader.deepCopy(settings));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}); 
		return item;
	}
	
	

	// settings changed via load --> menu
	@Override
	public void settingsChanged(Settings settings) {
		this.setSettings((T)settings);
	}
	
	// from ImageModule
	@Override
	public void setCurrentImage(Image2D img) {
		super.setCurrentImage(img);
		setSettings((T) img.getSettingsByClass(classsettings));
	}
	
		//################################################################################################
		// Autoupdate
		public abstract void addAutoupdater(ActionListener al, ChangeListener cl, DocumentListener dl, ColorChangedListener ccl, ItemListener il);

		//################################################################################################
		// LOGIC
		// Paintsclae from Image
		/**
		 * set all settings --> panel
		 * @param si
		 */
		public abstract void setAllViaExistingSettings(T si);
		
		/**
		 * set all panel --> settings
		 * @return
		 */
		public T writeAllToSettings() {
			return writeAllToSettings(settings);
		}

		public abstract T writeAllToSettings(T si);
		

		//################################################################################################
		// GETTERS and SETTERS
		public T getSettings() {
			return settings;
		}
		/**
		 * gets called by window? like every other method
		 * @param settings
		 */
		public void setSettings(T settings) {
			this.settings = settings;
			if(currentImage!=null) currentImage.setSettings((Settings)settings); 
			if(settings!=null) {
				setAllViaExistingSettings(settings);
				// transfer to Settingsholder
				SettingsHolder.getSettings().setSetByClass((Settings)settings);
			}
		}

		/**
		 * gets called by menu 
		 * saves settings to file
		 */
		public void saveSettings() {
			try {
				SettingsHolder settings = SettingsHolder.getSettings(); 
				Settings s = (Settings) getSettings();
				if(s!=null) {
					File f = settings.saveSettingsToFile(this, s);
					File presetpath = new File(FileAndPathUtil.getPathOfJar(), s.getPathSettingsFile());
					// path in presets?
					if(f!=null && f.getParentFile().equals(presetpath)) {
						// add to presets
						addPreset(getPopupMenu(), (T)s, FileAndPathUtil.eraseFormat(f.getName()));
					}
				}
			} catch (Exception e1) { 
				e1.printStackTrace();
				DialogLoggerUtil.showErrorDialog(this, "Error while saving", e1);
			}
		}

		public int getPresetindex() {
			return presetindex;
		}

		public void setPresetindex(int presetindex) {
			this.presetindex = presetindex;
		}
		
}
