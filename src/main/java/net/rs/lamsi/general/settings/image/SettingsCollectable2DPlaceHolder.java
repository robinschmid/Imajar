package net.rs.lamsi.general.settings.image;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;

public class SettingsCollectable2DPlaceHolder extends SettingsContainerCollectable2D {
  // do not change the version!
  private static final long serialVersionUID = 1L;

  private String title, group, project;

  public SettingsCollectable2DPlaceHolder(String title, String group, String project) {
    super("SettingsCollectable2DPlaceHolder", "/Settings/Image2dLink/", "setImg2dLink");
    this.title = title;
    this.group = group;
    this.project = project;
  }

  public SettingsCollectable2DPlaceHolder(Collectable2D img) {
    this(img.getTitle(), img.getImageGroup().getName(), img.getImageGroup().getProject().getName());
  }



  // ###########################################################
  // XML
  @Override
  public void appendSettingsValuesToXML(Element elParent, Document doc) {}

  @Override
  public void loadValuesFromXML(Element el, Document doc) {}



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
}
