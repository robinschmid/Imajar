package net.rs.lamsi.multiimager.Frames.multiimageframe;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.settings.image.visualisation.SettingsAlphaMap.State;


public class MultiImgTableRow {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private Image2D img;
  private boolean isShowing = true, useRange = false;
  private double lower = 0, upper = 0, min = 0, max = 0;
  private int index = 0;


  public MultiImgTableRow(int index, Image2D i) {
    this(index, i, true, false, i.getMinIntensity(false), i.getMaxIntensity(false));
  }

  public MultiImgTableRow(int index, Image2D img, boolean isShowing, boolean useRange, double lower,
      double upper) {
    super();
    this.img = img;
    this.isShowing = isShowing;
    this.useRange = useRange;
    this.lower = lower;
    this.min = lower;
    this.upper = upper;
    this.max = upper;
    this.index = index;
  }

  /**
   * range is multipied by 1000
   * 
   * @return
   */
  public Object[] getRowData() {
    return new Object[] {Integer.valueOf(index), img.getTitle(), isShowing, useRange, lower, upper,
        new int[] {(int) lower * 1000, (int) upper * 1000, (int) min * 1000, (int) max * 1000}};
  }

  /**
   * devided by 1000.0
   * 
   * @param value
   */
  public boolean setRange(int[] value) {
    return setLower(value[0] / 1000.0) || setUpper(value[1] / 1000.0);
  }


  /**
   * apply settings to boolean map map init as all true only change to false
   * 
   * @param map
   * @throws Exception
   */
  public void applyToMap(State[][] map) throws Exception {
    // same dimension`?
    if (map.length != img.getMaxLinesCount() || map[0].length != img.getMaxLineLength())
      throw new Exception("Map has a different dimension than image " + img.getTitle());
    // apply
    if (isUseRange() && (max != upper || min != lower)) {
      // lines
      for (int l = 0; l < map.length; l++) {
        for (int d = 0; d < map[l].length; d++) {
          // only if Boolean is defined (if not - there is no dp)
          if (map[l][d] != null) {
            // check if img.intensity out of range
            double tmp = img.getI(false, l, d);
            if (!Double.isNaN(tmp) && !inRange(tmp))
              map[l][d] = map[l][d].toFalse();
          }
        }
      }
    }
  }

  /**
   * applyToBinaryMap apply settings to binary map. i as index to change to 1
   * 
   * @param map
   * @throws Exception
   */
  public void applyToBinaryMap(Integer[][] map, int i) throws Exception {
    // same dimension`?
    if (map.length != img.getMaxLinesCount() || map[0].length != img.getMaxLineLength())
      throw new Exception("Map has a different dimension than image " + img.getTitle());
    // apply
    if (isUseRange() && (max != upper || min != lower)) {
      // lines
      for (int l = 0; l < map.length; l++) {
        for (int d = 0; d < map[l].length; d++) {
          // only if Integer is defined (if not - there is no dp)
          if (map[l][d] != null) {
            // check if img.intensity out of range
            if (l < img.getLineCount(d)) {
              double tmp = img.getI(false, l, d);
              if (!Double.isNaN(tmp) && inRange(tmp))
                map[l][d] += (int) Math.pow(2, i);
            }
          }
        }
      }
    }
  }



  public Image2D getImg() {
    return img;
  }

  public void setImg(Image2D img) {
    this.img = img;
  }

  public boolean isShowing() {
    return isShowing;
  }

  public void setShowing(boolean isShowing) {
    this.isShowing = isShowing;
  }

  public boolean isUseRange() {
    return useRange;
  }

  public void setUseRange(boolean useRange) {
    this.useRange = useRange;
  }

  public double getLower() {
    return lower;
  }

  public boolean setLower(double lower) {
    boolean res = this.lower != lower;
    this.lower = lower;
    return res;
  }

  public double getUpper() {
    return upper;
  }

  public boolean setUpper(double upper) {
    boolean res = this.upper != upper;
    this.upper = upper;
    return res;
  }

  public boolean inRange(double value) {
    return value >= lower && value <= upper;
  }

  @Override
  public String toString() {
    return isUseRange() + " " + index + " " + img.getTitle() + " " + lower + "-" + upper;
  }
}
