package net.rs.lamsi.general.settings.image;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import net.rs.lamsi.general.datamodel.image.ImageGroupMD;
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
  protected String title = "";
  protected List<DataCollectable2D> list;
  protected List<SettingsSingleMerge> settings;


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
   * @param title exact title of images to merge
   * @throws Exception
   */
  public void init(ImagingProject project, String title) throws Exception {
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
      settings = new ArrayList<>();
      for (DataCollectable2D d : list) {
        SettingsSingleMerge s = new SettingsSingleMerge();
        settings.add(s);
      }
    }
  }

  public boolean isInitialised() {
    return list != null;
  }

  @Override
  public void appendSettingsValuesToXML(Element elParent, Document doc) {
    toXML(elParent, doc, "title", title);
  }

  @Override
  public void loadValuesFromXML(Element el, Document doc) {
    NodeList list = el.getChildNodes();
    for (int i = 0; i < list.getLength(); i++) {
      if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
        Element nextElement = (Element) list.item(i);
        String paramName = nextElement.getNodeName();
        if (paramName.equals("title"))
          title = nextElement.getTextContent();
      }
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

  public List<SettingsSingleMerge> getMergeSettings() {
    return settings;
  }

  public SettingsSingleMerge getMergeSettings(int i) {
    if (settings == null || settings.size() <= i)
      return null;
    return settings.get(i);
  }

}
