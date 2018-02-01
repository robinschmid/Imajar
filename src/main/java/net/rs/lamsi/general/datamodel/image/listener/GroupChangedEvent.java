package net.rs.lamsi.general.datamodel.image.listener;

import net.rs.lamsi.general.datamodel.image.ImageGroupMD;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.datamodel.image.listener.ProjectChangedEvent.Event;

public class GroupChangedEvent {

  private ImageGroupMD group;
  private Collectable2D img;
  private Event event;

  public GroupChangedEvent(ImageGroupMD group, Collectable2D img, Event event) {
    super();
    this.group = group;
    this.img = img;
    this.event = event;
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
