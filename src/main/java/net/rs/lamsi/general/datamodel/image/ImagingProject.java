package net.rs.lamsi.general.datamodel.image;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import net.rs.lamsi.general.datamodel.image.data.interf.MDDataset;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.datamodel.image.listener.GroupChangedEvent;
import net.rs.lamsi.general.datamodel.image.listener.GroupChangedListener;
import net.rs.lamsi.general.datamodel.image.listener.ProjectChangedEvent;
import net.rs.lamsi.general.datamodel.image.listener.ProjectChangedEvent.Event;
import net.rs.lamsi.general.datamodel.image.listener.ProjectChangedListener;
import net.rs.lamsi.general.framework.modules.ModuleTree;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.general.settings.SettingsContainerSettings;
import net.rs.lamsi.general.settings.image.SettingsImagingProject;

public class ImagingProject implements Serializable, GroupChangedListener {
  // do not change the version!
  private static final long serialVersionUID = 1L;
  // settings
  protected SettingsImagingProject settings;

  // dataset
  protected MDDataset data = null;
  protected ArrayList<ImageGroupMD> groups;
  // treenode in tree view
  protected DefaultMutableTreeNode node = null;

  private List<ProjectChangedListener> listener;


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
    if (groups != null && groups.length > 0) {
      for (ImageGroupMD i : groups)
        add(i);
    }
  }

  // ################################################
  // ArrayList methods

  /**
   * add an image to this group and sets a unique imagegroup parameter to this image object
   * 
   * @param img
   */
  public void add(ImageGroupMD grp) {
    // same name as project
    if (getName().equals(grp.getName())) {
      grp.getSettings().setName(grp.getName() + " (G)");
    }
    // same name as other group?
    int c = 1;
    for (int i = 0; i < groups.size(); i++) {
      ImageGroupMD g = groups.get(i);
      if (grp.getName().equals(g.getName())) {
        c++;
        i = -1;
        String name = grp.getName();
        if (c > 2)
          name = name.substring(0, name.length() - String.valueOf(c).length() - 2);
        name += "(" + c + ")";
        grp.getSettings().setName(name);
      }
    }
    //
    groups.add(grp);
    grp.setProject(this);
    grp.addGroupListener(this);
    // fire event
    fireChangeEvent(grp, null, Event.ADD);
  }

  public boolean remove(ImageGroupMD g) {
    return remove(groups.indexOf(g)) != null;
  }

  public ImageGroupMD remove(int index) {
    if (index >= 0 && index < size()) {
      ImageGroupMD g = groups.remove(index);
      g.setProject(null);
      g.removeGroupListener(this);
      // fire event
      fireChangeEvent(g, null, Event.REMOVED);
      return g;
    } else
      return null;
  }


  /**
   * replace all collectable2d place holders in settings
   * 
   * @param tree
   */
  public void replacePlaceHoldersInSettings(ModuleTree<Collectable2D> tree) {
    getSettings().replacePlaceHoldersInSettings(tree);

    for (ImageGroupMD g : groups)
      g.replacePlaceHoldersInSettings(tree);
  }


  // #######################################################################################
  // GETTERS AND SETTERS
  /**
   * overlays and groups
   * 
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
    if (groups != null && i >= 0 && i < groups.size())
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
   * 
   * @param classsettings
   * @return
   */
  public Settings getSettingsByClass(Class classsettings) {
    if (classsettings.isInstance(this.settings))
      return this.settings;
    else {
      return ((SettingsContainerSettings) this.settings).getSettingsByClass(classsettings);
    }
  }

  public String getName() {
    return settings.getName();
  }

  @Override
  public String toString() {
    return getName();
  }

  /**
   * get group by unique name
   * 
   * @param group
   * @return
   */
  public ImageGroupMD getGroupByName(String group) {
    for (ImageGroupMD g : groups)
      if (g.getName().equals(group))
        return g;
    return null;
  }


  public void fireChangeEvent(ImageGroupMD group, Collectable2D img, Event e) {
    if (listener != null) {
      ProjectChangedEvent event = new ProjectChangedEvent(this, group, img, e);
      listener.stream().forEach(l -> l.projectChanged(event));
    }
  }

  public void addProjectListener(ProjectChangedListener listener) {
    if (this.listener == null)
      this.listener = new ArrayList<ProjectChangedListener>();
    this.listener.add(listener);
  }

  public void removeProjectListener(ProjectChangedListener listener) {
    if (this.listener == null)
      return;
    this.listener.remove(listener);
  }

  @Override
  public void groupChanged(GroupChangedEvent e) {
    // TODO Auto-generated method stub

  }
}
