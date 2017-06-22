package net.rs.lamsi.general.settings.image.needy;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.rs.lamsi.general.datamodel.image.Collectable2DPlaceHolderLink;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.framework.modules.ModuleTree;
import net.rs.lamsi.general.settings.Settings;

public class SettingsCollectable2DLink extends Settings {
	// do not change the version!
	private static final long serialVersionUID = 1L;
	//
	private String title, group, project;
	
	public SettingsCollectable2DLink() {
		super("SettingsCollectable2DLink", "/Settings/Image2dLink/", "setImg2dLink"); 
		resetAll();
	} 

	public SettingsCollectable2DLink(String title, String group, String project) {  
		super("SettingsCollectable2DLink", "/Settings/Image2dLink/", "setImg2dLink"); 
		this.title = title;
		this.group = group; 
		this.project = project;
	} 

	public SettingsCollectable2DLink(String title) {  
		super("SettingsCollectable2DLink", "/Settings/Image2dLink/", "setImg2dLink"); 
		this.title = title;
		this.group = null; 
		this.project = null;
	}

	public SettingsCollectable2DLink(String title, String group) {  
		super("SettingsCollectable2DLink", "/Settings/Image2dLink/", "setImg2dLink"); 
		this.title = title;
		this.group = group; 
		this.project = null;
	}
	
	public String getTitle() {
		return title;
	}
	public String getGroup() {
		return group;
	}
	public String getProject() {
		return project;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public void setProject(String project) {
		this.project = project;
	}

	public void setAll(String title, String group, String project) {
		this.title = title;
		this.group = group; 
		this.project = project;
	}
	@Override
	public void resetAll() { 
		this.title = null;
		this.group = null; 
		this.project = null;
	}

	//##########################################################
	// xml input/output
	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
		toXML(elParent, doc, "title", title); 
		toXML(elParent, doc, "group", group); 
		toXML(elParent, doc, "project", project); 
	}

	@Override
	public void loadValuesFromXML(Element el, Document doc) {
		NodeList list = el.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element nextElement = (Element) list.item(i);
				String paramName = nextElement.getNodeName();
				if(paramName.equals("title")) title = nextElement.getTextContent();
				else if(paramName.equals("group")) group = nextElement.getTextContent();
				else if(paramName.equals("project")) project = nextElement.getTextContent();
			}
		}
		if(group!=null && group.isEmpty()) group = null;
		if(project != null && project.isEmpty()) project = null;
	}

	/**
	 * [att names, att values]
	 * @return
	 */
	public String[][] toXMLAttributes() {
		if(project==null) {
			if(group==null) return new String[][]{{"title"},{title}};
			else return new String[][]{{"group", "title"},{group, title}};
		}
		else return new String[][]{{"project", "group", "title"},{project, group, title}};
	}
	
	/**
	 * if objects of the same group/project are compared: group/project == null
	 * 
	 * @param c
	 * @return
	 */
	public boolean equals(Collectable2D c) {
		if(title==null)
			return false;
		else {
			if(c.getTitle().equals(title))
				if(group==null || (c.getImageGroup()!=null && group.equals(c.getImageGroup().getName())))
					if(project==null || 
					(c.getImageGroup().getProject()!=null && 
					project.equals(c.getImageGroup().getProject().getName())))
						return true;
			
			// not equal
			return false;
		}
	}
	
	/**
	 * if objects of the same group/project are compared: group/project == null
	 * compare if source and target are in the same group if null
	 * @param c
	 * @return
	 */
	public boolean equals(Collectable2D source, Collectable2D target) {
		if(title==null)
			return false;
		else {
			if(target.getTitle().equals(title))
				// link == null but the same || correct link
				if((group==null && target.getImageGroup()!=null && source.getImageGroup()!=null && 
				target.getImageGroup().getName().equals(source.getImageGroup().getName())) || 
						(group!=null && target.getImageGroup()!=null && group.equals(target.getImageGroup().getName())))
					// both no project or same or correct link
					if((project==null && ((source.getImageGroup().getProject()==null && target.getImageGroup().getProject()==null) || 
							((source.getImageGroup().getProject()!=null && target.getImageGroup().getProject()!=null) && 
									target.getImageGroup().getProject().getName().equals(source.getImageGroup().getProject().getName())))) ||
							(project!=null && target.getImageGroup().getProject()!=null && project.equals(target.getImageGroup().getProject().getName())))
						return true;
			
			// not equal
			return false;
		}
	}
}
