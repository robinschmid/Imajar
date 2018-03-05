package net.rs.lamsi.general.settings.image.visualisation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import net.rs.lamsi.general.heatmap.Heatmap;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.general.settings.interf.GroupSettings;
import net.rs.lamsi.multiimager.Frames.multiimageframe.MultiImageTableModel;

public class SettingsAlphaMap extends Settings implements GroupSettings {
  // do not change the version!
  private static final long serialVersionUID = 1L;
  //

  private boolean isActive = false;
  // true: visible, false: invisible, null: no data point
  private Boolean[][] map = null;
  private int realsize = 0, falseCount = 0;
  protected float alpha = 1;
  // settings
  private MultiImageTableModel tableModel;

  public SettingsAlphaMap() {
    super("SettingsAlphaMap", "Settings/Visualization/", "setAlphaMap");
    resetAll();
  }

  public SettingsAlphaMap(Boolean[][] map) {
    this();
    setMap(map);
  }


  @Override
  public void resetAll() {
    isActive = false;
    alpha = 1;
    map = null;
    realsize = 0;
    falseCount = 0;
  }

  @Override
  public void applyToHeatMap(Heatmap heat) {
    super.applyToHeatMap(heat);
    heat.applyAlphaMapSettings(this);
  }

  // ##########################################################
  // xml input/output
  @Override
  public void appendSettingsValuesToXML(Element elParent, Document doc) {
    toXML(elParent, doc, "isActive", isActive);
    toXMLArray(elParent, doc, "map", map);
    toXML(elParent, doc, "alpha", alpha);
  }

  @Override
  public void loadValuesFromXML(Element el, Document doc) {
    NodeList list = el.getChildNodes();
    for (int i = 0; i < list.getLength(); i++) {
      if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
        Element nextElement = (Element) list.item(i);
        String paramName = nextElement.getNodeName();
        if (paramName.equals("isActive"))
          isActive = booleanFromXML(nextElement);
        if (paramName.equals("alpha"))
          alpha = floatFromXML(nextElement);
        else if (paramName.equals("map"))
          setMap(mapFromXML(nextElement));
      }
    }
  }

  public boolean isActive() {
    return isActive;
  }

  public boolean setActive(boolean isActive) {
    boolean result = isActive != this.isActive;
    this.isActive = isActive;
    return result;
  }

  /**
   * 
   * @return map[lines][dps]
   */
  public Boolean[][] getMap() {
    return map;
  }

  /**
   * 
   * @param map [lines][dps]
   */
  public void setMap(Boolean[][] map) {
    this.map = map;

    realsize = 0;

    if (map != null) {
      for (Boolean[] m : map)
        for (Boolean b : m) {
          if (b != null) {
            realsize++;
            if (!b)
              falseCount++;
          }
        }
    }
  }

  public int getFalseCount() {
    return falseCount;
  }

  public int getRealsize() {
    return realsize;
  }

  public Boolean getMapValue(int line, int dp) {
    if (map == null || line >= map.length || dp >= map[line].length)
      return null;
    else {
      return map[line][dp];
    }
  }

  /**
   * converts the map to one dimension as line, line,line,line
   */
  public boolean[] convertToLinearMap2() {
    if (map == null)
      return null;

    boolean[] maplinear = new boolean[realsize];
    int c = 0;
    for (Boolean[] m : map) {
      for (Boolean b : m) {
        if (b != null) {
          maplinear[c] = b;
          c++;
        }
      }
    }
    return maplinear;
  }

  public MultiImageTableModel getTableModel() {
    return tableModel;
  }

  public void setTableModel(MultiImageTableModel tableModel) {
    this.tableModel = tableModel;
  }

  public float getAlpha() {
    return alpha;
  }

  public boolean setAlpha(float a) {
    boolean result = a != alpha;
    alpha = a;
    return result;
  }
}
