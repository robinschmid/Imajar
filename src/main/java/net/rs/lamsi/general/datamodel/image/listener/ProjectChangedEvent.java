package net.rs.lamsi.general.datamodel.image.listener;

import net.rs.lamsi.general.datamodel.image.ImageGroupMD;
import net.rs.lamsi.general.datamodel.image.ImagingProject;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;

public class ProjectChangedEvent {

  public enum Event {
    ADD, REMOVED;
  }

  private ImagingProject project;
  private ImageGroupMD group;
  private Collectable2D img;
  private Event event;

  public ProjectChangedEvent(ImagingProject project, ImageGroupMD group, Collectable2D img,
      Event event) {
    super();
    this.project = project;
    this.group = group;
    this.img = img;
    this.event = event;
  }

  public ImagingProject getProject() {
    return project;
  }

  public ImageGroupMD getGroup() {
    return group;
  }

  public Collectable2D getImg() {
    return img;
  }

  public Event getEvent() {
    return event;
  }

}
