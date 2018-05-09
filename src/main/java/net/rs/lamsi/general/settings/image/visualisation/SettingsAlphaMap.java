package net.rs.lamsi.general.settings.image.visualisation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import net.rs.lamsi.general.datamodel.image.interf.DataCollectable2D;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.general.settings.interf.GroupSettings;
import net.rs.lamsi.multiimager.Frames.multiimageframe.MultiImageTableModel;

public class SettingsAlphaMap extends Settings implements GroupSettings {
  // do not change the version!
  private static final long serialVersionUID = 1L;

  //
  public enum State {
    NO_DP, // not a dp
    ALPHA_FALSE, // not visible
    ALPHA_TRUE, // visible
    MARKED_ALPHA_FALSE, // marked
    MARKED_ALPHA_TRUE, // marked
    NO_MAP_DEFINED;

    public boolean isTrue() {
      return this.equals(ALPHA_TRUE) || this.equals(MARKED_ALPHA_TRUE);
    }

    public boolean isFalse() {
      return this.equals(ALPHA_FALSE) || this.equals(MARKED_ALPHA_FALSE);
    }

    public boolean isMarked() {
      return this.equals(MARKED_ALPHA_TRUE) || this.equals(MARKED_ALPHA_FALSE);
    }

    /**
     * use to change from MARKED_TRUE to MARKED_FALSE, and TRUE to FALSE assign result to variable
     * 
     * @return The FALSE state of a TRUE state - or the initial state
     */
    public State toFalse() {
      if (this.equals(ALPHA_TRUE))
        return ALPHA_FALSE;
      if (this.equals(MARKED_ALPHA_TRUE))
        return MARKED_ALPHA_FALSE;

      return this;
    }

    public State toMarked() {
      if (this.equals(ALPHA_TRUE))
        return MARKED_ALPHA_TRUE;
      if (this.equals(ALPHA_FALSE))
        return MARKED_ALPHA_FALSE;

      return this;
    }

    public State toUnMarked() {
      if (this.equals(MARKED_ALPHA_TRUE))
        return ALPHA_TRUE;
      if (this.equals(MARKED_ALPHA_FALSE))
        return ALPHA_FALSE;

      return this;
    }
  }

  private boolean isActive = false;
  private boolean isDrawMarks = false;
  // true: visible, false: invisible, null: no data point
  private State[][] map = null;
  private int realsize = 0, falseCount = 0;
  protected float alpha = 0.5f;
  // settings
  private MultiImageTableModel tableModel;

  public SettingsAlphaMap() {
    super("SettingsAlphaMap", "Settings/Visualization/", "setAlphaMap");
    resetAll();
  }

  public SettingsAlphaMap(State[][] map) {
    this();
    setMap(map);
  }


  @Override
  public void resetAll() {
    isActive = false;
    alpha = 0.5f;
    map = null;
    realsize = 0;
    falseCount = 0;
    isDrawMarks = false;
  }

  // ##########################################################
  // xml input/output
  @Override
  public void appendSettingsValuesToXML(Element elParent, Document doc) {
    toXML(elParent, doc, "isActive", isActive);
    toXMLArray(elParent, doc, "map", map);
    toXML(elParent, doc, "alpha", alpha);
    toXML(elParent, doc, "isDrawMarks", isDrawMarks);
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
        else if (paramName.equals("isDrawMarks"))
          isDrawMarks = booleanFromXML(nextElement);
        else if (paramName.equals("alpha"))
          alpha = floatFromXML(nextElement);
        else if (paramName.equals("map"))
          setMap(mapStateFromXML(nextElement));
      }
    }
  }


  /**
   * 
   * @param img
   * @return
   */
  public boolean checkDimensions(DataCollectable2D img) {
    if (map == null || map.length == 0)
      return false;
    return img.getMaxLinesCount() == map.length && img.getMaxLineLength() == map[0].length;
  }



  public boolean isActive() {
    return isActive;
  }

  public boolean setActive(boolean isActive) {
    boolean result = isActive != this.isActive;
    this.isActive = isActive;
    return result;
  }


  public boolean isDrawMarks() {
    return isDrawMarks;
  }

  public boolean setDrawMarks(boolean isDrawMarks) {
    boolean result = isDrawMarks != this.isDrawMarks;
    this.isDrawMarks = isDrawMarks;
    return result;
  }

  /**
   * 
   * @return map[lines][dps]
   */
  public State[][] getMap() {
    return map;
  }

  /**
   * 
   * @param map [lines][dps]
   */
  public void setMap(State[][] map) {
    this.map = map;

    realsize = 0;

    if (map != null) {
      for (State[] m : map)
        for (State b : m) {
          if (b != null) {
            realsize++;
            if (b.equals(State.ALPHA_FALSE))
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

  public boolean isInBounds(int line, int dp) {
    return !(map == null || line >= map.length || dp >= map[line].length);
  }

  public State getMapValue(int line, int dp) {
    if (map == null)
      return State.NO_MAP_DEFINED;
    if (!isInBounds(line, dp))
      return State.NO_DP;
    else {
      return map[line][dp];
    }
  }

  public void setMapValue(int line, int dp, State state) {
    if (isInBounds(line, dp))
      map[line][dp] = state;
  }

  /**
   * converts the map to one dimension as line, line,line,line
   */
  public State[] convertToLinearMap2() {
    if (map == null)
      return null;

    State[] maplinear = new State[realsize];
    int c = 0;
    for (State[] m : map) {
      for (State b : m) {
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

  /**
   * Set all marked dp back to unmarked
   */
  public void eraseMarkings() {
    if (map != null) {
      for (int i = 0; i < map.length; i++) {
        for (int j = 0; j < map[i].length; j++) {
          map[i][j] = map[i][j].toUnMarked();
        }
      }
    }
  }
}
