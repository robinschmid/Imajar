package net.rs.lamsi.general.framework.listener;

import java.awt.Color;
import java.util.EventListener;


@FunctionalInterface
public interface ColorChangedListener extends EventListener {

  public void colorChanged(Color color);

}
