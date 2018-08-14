package net.rs.lamsi.general.settings.image;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import net.rs.lamsi.general.datamodel.image.ImageGroupMD;
import net.rs.lamsi.general.datamodel.image.ImageMerge;
import net.rs.lamsi.general.datamodel.image.ImagingProject;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.datamodel.image.interf.DataCollectable2D;
import net.rs.lamsi.general.myfreechart.themes.ChartThemeFactory.THEME;
import net.rs.lamsi.general.settings.image.merge.SettingsSingleMerge;
import net.rs.lamsi.general.settings.image.sub.SettingsZoom;
import net.rs.lamsi.general.settings.image.visualisation.themes.SettingsThemesContainer;

public class SettingsImageMerge extends SettingsContainerCollectable2D {
  // do not change the version!
  private static final long serialVersionUID = 1L;
  // merge title settings
  // title of the image
  protected String title = "";
  protected List<DataCollectable2D> list;
  // group name, settings
  protected TreeMap<String, SettingsSingleMerge> settings;


  public SettingsImageMerge() {
    super("SettingsImageMerge", "/Settings/ImageMerge/", "setImgMerge");
    // standard theme
    resetAll();
    // other
    addSettings(new SettingsThemesContainer(THEME.DARKNESS, false));
    addSettings(new SettingsZoom());
  }

  /**
   * 
   * @param project
   * @throws Exception
   */
  public void init(ImagingProject project) throws Exception {
    init(project, title);
  }

  /**
   * 
   * @param project
   * @param title exact title of images to merge
   * @throws Exception
   */
  public void init(ImagingProject project, String title) throws Exception {
    // Treemap already init?
    if (settings != null && !settings.isEmpty()) {
      list = new ArrayList<>();
      // find images in groups
      for (Map.Entry<String, SettingsSingleMerge> e : settings.entrySet()) {
        ImageGroupMD g = project.getGroupByName(e.getKey());
        Collectable2D img = g.getImageByTitle(title);
        if (img != null && img instanceof DataCollectable2D) {
          list.add((DataCollectable2D) img);
        }
      }
    } else {
      // create new list of images
      // create new settings
      this.title = title;
      list = new ArrayList<>();
      for (ImageGroupMD g : project.getGroups()) {
        Collectable2D img = g.getImageByTitle(title);
        if (img != null && img instanceof DataCollectable2D) {
          list.add((DataCollectable2D) img);
        }
      }
      if (list.isEmpty()) {
        list = null;
        return;
      } else {
        // create settings
        settings = new TreeMap<>();
        for (DataCollectable2D d : list) {
          SettingsSingleMerge s = new SettingsSingleMerge();
          settings.put(d.getImageGroup().getName(), s);
        }
      }
    }
  }

  public boolean isInitialised() {
    return list != null;
  }

  @Override
  public void appendSettingsValuesToXML(Element elParent, Document doc) {
    toXML(elParent, doc, "title", title);

    for (Map.Entry<String, SettingsSingleMerge> e : settings.entrySet()) {
      // add group name
      toXML(elParent, doc, "groupName", e.getKey());
      // settings
      e.getValue().appendSettingsToXML(elParent, doc);
    }
  }

  @Override
  public void loadValuesFromXML(Element el, Document doc) {
    SettingsSingleMerge sm = new SettingsSingleMerge();
    List<String> groupNames = new ArrayList<>();
    List<SettingsSingleMerge> sett = new ArrayList<>();
    NodeList list = el.getChildNodes();
    for (int i = 0; i < list.getLength(); i++) {
      if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
        Element nextElement = (Element) list.item(i);
        String paramName = nextElement.getNodeName();
        if (paramName.equals("title"))
          title = nextElement.getTextContent();
        else if (paramName.equals("groupName"))
          groupNames.add(nextElement.getTextContent());
        else if (isSettingsNode(nextElement, sm.getSuperClass())) {
          SettingsSingleMerge sm2 = new SettingsSingleMerge();
          sm2.loadValuesFromXML(nextElement, doc);
          sett.add(sm2);
        }
      }
    }

    if (settings == null)
      settings = new TreeMap<>();
    else
      settings.clear();
    for (int i = 0; i < sett.size(); i++) {
      settings.put(groupNames.get(i), sett.get(i));
    }
  }

  @Override
  public void applyToImage(Collectable2D img) throws Exception {
    if (img instanceof ImageMerge) {
      SettingsImageMerge that = ((ImageMerge) img).getSettings();
      that.getMergeSettings().clear();

      for (Map.Entry<String, SettingsSingleMerge> e : settings.entrySet())
        that.getMergeSettings().put(e.getKey(), (SettingsSingleMerge) e.getValue().copy());
    }
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public List<DataCollectable2D> getImageList() {
    return list;
  }

  public DataCollectable2D getImage(int i) {
    if (list == null || list.size() <= i)
      return null;
    return list.get(i);
  }

  /**
   * Group name as ID
   * 
   * @return
   */
  public TreeMap<String, SettingsSingleMerge> getMergeSettings() {
    return settings;
  }

  public SettingsSingleMerge getMergeSettings(String groupName) {
    return settings.get(groupName);
  }

  public SettingsSingleMerge getMergeSettingsAt(int i) {
    if (list == null || list.size() <= i)
      return null;
    String gname = list.get(i).getImageGroup().getName();
    return settings.get(gname);
  }
}
