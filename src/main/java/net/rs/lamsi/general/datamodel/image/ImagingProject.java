package net.rs.lamsi.general.datamodel.image;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.tree.DefaultMutableTreeNode;

import net.rs.lamsi.general.datamodel.image.data.interf.MDDataset;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.general.settings.SettingsContainerSettings;
import net.rs.lamsi.general.settings.image.SettingsImagingProject;
import net.rs.lamsi.general.settings.image.sub.SettingsGeneralImage;
import net.rs.lamsi.general.settings.image.visualisation.SettingsAlphaMap;
import net.rs.lamsi.general.settings.image.visualisation.SettingsThemes;
import net.rs.lamsi.multiimager.Frames.multiimageframe.MultiImgTableRow;

public class ImagingProject  implements Serializable {  
	// do not change the version!
	private static final long serialVersionUID = 1L;
	// settings
	protected SettingsImagingProject settings;

	// dataset
	protected MDDataset data = null;
	protected ArrayList<ImageGroupMD> groups;
	// treenode in tree view
	protected DefaultMutableTreeNode node = null;

	
	public ImagingProject() {
		settings = new SettingsImagingProject();
		groups = new ArrayList<ImageGroupMD>();
	}
	public ImagingProject(String projectName) {
		this(); 
		settings.setName(projectName);
	}
	public ImagingProject(ImageGroupMD group, String name) {
		this(name);
		add(group);
	}
	public ImagingProject(ImageGroupMD[] groups, String name) {
		this(name);
		if(groups!=null && groups.length>0) {
			for(ImageGroupMD i : groups)
				add(i);
		}
	}

	//################################################
	// ArrayList methods

	/**
	 * add an image to this group and sets a unique imagegroup parameter to this image object
	 * @param img
	 */
	public void add(ImageGroupMD grp) {
		// same name as project
		if(getName().equals(grp.getName())) {
			grp.getSettings().setName(grp.getName()+" (G)");
		}
		// same name as other group?
		int c = 1;
		for(int i=0; i<groups.size(); i++) {
			ImageGroupMD g = groups.get(i);
			if(grp.getName().equals(g.getName())) {
				c++;
				i=-1;
				String name = grp.getName();
				if(c>2) name = name.substring(0, name.length()-String.valueOf(c).length()-2);
				name += "("+c+")";
				grp.getSettings().setName(name);
			}
		}
		//
		groups.add(grp);
		grp.setProject(this); 
	}
	public boolean remove(ImageGroupMD g) {
		g.setProject(null);
		return remove(groups.indexOf(g))!=null;
	}

	public ImageGroupMD remove(int index) {
		if(index>=0 && index<size()) {
			ImageGroupMD g = groups.remove(index);
			g.setProject(null);
			return g;
		}
		else return null;
	}

	// #######################################################################################
	// GETTERS AND SETTERS
	/**
	 * overlays and groups
	 * @return
	 */
	public int size() {
		return groups.size();
	}

	public ArrayList<ImageGroupMD> getGroups() {
		return groups;
	}

	public DefaultMutableTreeNode getNode() {
		return node;
	}
	public void setNode(DefaultMutableTreeNode node) {
		this.node = node;
	}
	public ImageGroupMD get(int i) { 
		if(groups!=null && i>=0 && i<groups.size())
			return groups.get(i);
		return null;
	}
	
	
	public SettingsImagingProject getSettings() {
		return settings;
	}
	public void setSettings(SettingsImagingProject settings) {
		this.settings = settings;
	}
	/**
	 * get settings by class
	 * @param classsettings
	 * @return
	 */
	public Settings getSettingsByClass(Class classsettings) {
		if(classsettings.isInstance(this.settings))
			return this.settings;
		else {
			return ((SettingsContainerSettings)this.settings).getSettingsByClass(classsettings);
		}
	}

	public String getName() {
		return settings.getName();
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
