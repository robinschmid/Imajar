package net.rs.lamsi.general.settings.image.special;

import org.jfree.data.Range;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import net.rs.lamsi.general.heatmap.Heatmap;
import net.rs.lamsi.general.settings.Settings;

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
  }


  public void setAll(double noiseLevel, int splitPixel, Range window) {
    this.noiseLevel = noiseLevel;
    this.splitPixel = splitPixel;
    this.window = window;
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
      }
    }

    if (!Double.isNaN(lower) && !Double.isNaN(upper))
      setWindow(lower, upper);
  }


  public boolean setWindow(double l, double u) {
    return setWindow(new Range(l, u));
  }

  public boolean setWindow(Range w) {
    boolean changed = window == null ? true : !window.equals(w);
    this.window = w;
    return changed;
  }

  public double getNoiseLevel() {
    return noiseLevel;
  }

  public void setNoiseLevel(double noiseLevel) {
    this.noiseLevel = noiseLevel;
  }

  public int getSplitPixel() {
    return splitPixel;
  }

  public void setSplitPixel(int splitPixel) {
    this.splitPixel = splitPixel;
  }

  public Range getWindow() {
    return window;
  }

}
