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
  // count events in number of pixel
  private int numberOfPixel = 1000;

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


  public void setAll(double noiseLevel, int splitPixel, Range window, int numberOfPixel) {
    this.noiseLevel = noiseLevel;
    this.splitPixel = splitPixel;
    this.window = window;
    this.numberOfPixel = numberOfPixel;
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
      }
    }

    if (!Double.isNaN(lower) && !Double.isNaN(upper))
      setWindow(lower, upper);
  }


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
