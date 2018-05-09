package net.rs.lamsi.general.settings.image.special;

import org.jfree.data.Range;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import net.rs.lamsi.general.heatmap.Heatmap;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.general.settings.image.sub.SettingsGeneralImage.Transformation;

public class SingleParticleSettings extends Settings {
  // do not change the version!
  private static final long serialVersionUID = 1L;
  //
  // noise level for split events
  private double noiseLevel;
  // how many pixel for split events where one NP is split in two/n windows
  private int splitPixel;
  //
  private Range window;
  // count events in number of pixel
  private int numberOfPixel = 1000;
  private Transformation transform;
  private boolean isCountPixel;

  public SingleParticleSettings(String path, String fileEnding) {
    super("SPSettings", path, fileEnding);
  }

  public SingleParticleSettings() {
    super("SPSettings", "/Settings/SingleParticle/", "setSP");
  }

  @Override
  public void resetAll() {
    window = null;
    noiseLevel = 0;
    splitPixel = 2;
    transform = Transformation.NONE;
    isCountPixel = false;
  }


  public boolean setAll(double noiseLevel, int splitPixel, Range window, int numberOfPixel,
      boolean isCountPixel) {
    boolean diff =
        Double.compare(this.noiseLevel, noiseLevel) != 0 || (this.window == null && window != null)
            || (this.window != null && !this.window.equals(window))
            || this.numberOfPixel != numberOfPixel || this.splitPixel != splitPixel
            || this.isCountPixel != isCountPixel;
    this.noiseLevel = noiseLevel;
    this.splitPixel = splitPixel;
    this.window = window;
    this.numberOfPixel = numberOfPixel;
    this.isCountPixel = isCountPixel;
    return diff;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof SingleParticleSettings))
      return false;
    else {
      SingleParticleSettings that = (SingleParticleSettings) o;
      return that.getSplitPixel() == this.getSplitPixel()
          && Double.compare(that.getNoiseLevel(), this.getNoiseLevel()) == 0
          && that.getWindow().equals(this.getWindow());
    }
  }

  @Override
  public void applyToHeatMap(Heatmap heat) {}

  // ##########################################################
  // xml input/output
  @Override
  public void appendSettingsValuesToXML(Element elParent, Document doc) {
    if (window != null) {
      toXML(elParent, doc, "window.lower", window.getLowerBound());
      toXML(elParent, doc, "window.upper", window.getUpperBound());
    }
    toXML(elParent, doc, "noiseLevel", noiseLevel);
    toXML(elParent, doc, "splitPixel", splitPixel);
    toXML(elParent, doc, "numberOfPixel", numberOfPixel);
    toXML(elParent, doc, "transform", transform);
  }

  @Override
  public void loadValuesFromXML(Element el, Document doc) {
    double xu = 0, yu = 0;
    double lower = Double.NaN, upper = Double.NaN;
    NodeList list = el.getChildNodes();
    for (int i = 0; i < list.getLength(); i++) {
      if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
        Element nextElement = (Element) list.item(i);
        String paramName = nextElement.getNodeName();
        if (paramName.equals("noiseLevel"))
          noiseLevel = doubleFromXML(nextElement);
        else if (paramName.equals("splitPixel"))
          splitPixel = intFromXML(nextElement);
        else if (paramName.equals("window.lower"))
          lower = doubleFromXML(nextElement);
        else if (paramName.equals("window.upper"))
          upper = doubleFromXML(nextElement);
        else if (paramName.equals("numberOfPixel"))
          numberOfPixel = intFromXML(nextElement);
        else if (paramName.equals("transform"))
          transform = Transformation.valueOf(nextElement.getTextContent());
      }
    }

    if (!Double.isNaN(lower) && !Double.isNaN(upper))
      setWindow(lower, upper);
  }

  /**
   * Numbe rof pixel to count particles in
   * 
   * @return
   */
  public int getNumberOfPixel() {
    return numberOfPixel;
  }

  public void setNumberOfPixel(int numberOfPixel) {
    this.numberOfPixel = numberOfPixel;
  }

  public boolean setWindow(double l, double u) {
    return setWindow(new Range(l, u));
  }

  public boolean setWindow(Range w) {
    boolean changed = window == null ? true : !window.equals(w);
    this.window = w;
    return changed;
  }

  /**
   * Find particle intensities >noise
   * 
   * @return
   */
  public double getNoiseLevel() {
    return noiseLevel;
  }

  public void setNoiseLevel(double noiseLevel) {
    this.noiseLevel = noiseLevel;
  }

  /**
   * Number of split pixels (intensity of one particle is split between 1 up to splitPixel data
   * points)
   * 
   * @return
   */
  public int getSplitPixel() {
    return splitPixel;
  }

  public void setSplitPixel(int splitPixel) {
    this.splitPixel = splitPixel;
  }

  /**
   * The intensity window (after applying the mathematical transformation) to count particles
   * 
   * @return
   */
  public Range getWindow() {
    return window;
  }

  /**
   * The mathematical transformation which is applied prior to counting
   * 
   */
  public Transformation getTransform() {
    return transform;
  }

  /**
   * The mathematical transformation which is applied prior to counting
   * 
   * @param transform
   */
  public boolean setTransform(Transformation transform) {
    boolean changed = !transform.equals(this.transform);
    this.transform = transform;
    return changed;
  }

  public boolean isCountPixel() {
    return isCountPixel;
  }

  public void setCountPixel(boolean isCountPixel) {
    this.isCountPixel = isCountPixel;
  }
}
