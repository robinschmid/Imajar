package net.rs.lamsi.general.settings;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import net.sf.mzmine.parameters.parametertypes.tolerances.MZTolerance;
import net.sf.mzmine.parameters.parametertypes.tolerances.RTTolerance;


public class SettingsChargeCalculator extends Settings {
  // do not change the version!
  private static final long serialVersionUID = 1L;
  //

  private MZTolerance mzTolerance;
  private RTTolerance rtTolerance;
  private boolean monotonicShape;
  private int maximumCharge;


  public SettingsChargeCalculator() {
    super("SettingsChargeCalculator", "/Settings/Charge Calculator/", "setChargeCalc");
    resetAll();
  }

  @Override
  public void resetAll() {
    mzTolerance = new MZTolerance(0.005, 5);
    rtTolerance = new RTTolerance(true, 0.25);
    monotonicShape = false;
    maximumCharge = 15;
  }


  public MZTolerance getMzTolerance() {
    return mzTolerance;
  }

  public void setMzTolerance(MZTolerance mzTolerance) {
    this.mzTolerance = mzTolerance;
  }

  public RTTolerance getRtTolerance() {
    return rtTolerance;
  }

  public void setRtTolerance(RTTolerance rtTolerance) {
    this.rtTolerance = rtTolerance;
  }

  public boolean isMonotonicShape() {
    return monotonicShape;
  }

  public void setMonotonicShape(boolean monotonicShape) {
    this.monotonicShape = monotonicShape;
  }

  public int getMaximumCharge() {
    return maximumCharge;
  }

  public void setMaximumCharge(int maximumCharge) {
    this.maximumCharge = maximumCharge;
  }

  // ##########################################################
  // xml input/output
  @Override
  public void appendSettingsValuesToXML(Element elParent, Document doc) {
    toXML(elParent, doc, "monotonicShape", monotonicShape);
    toXML(elParent, doc, "maximumCharge", maximumCharge);
    toXML(elParent, doc, "mzTolerance", "", new String[] {"ppm", "abs"},
        new Double[] {mzTolerance.getPpmTolerance(), mzTolerance.getMzTolerance()});
    toXML(elParent, doc, "rtTolerance", rtTolerance.getTolerance(), "isAbsolute",
        rtTolerance.isAbsolute());
  }

  @Override
  public void loadValuesFromXML(Element el, Document doc) {
    float ax = 0.5f;
    float ay = 0.5f;
    NodeList list = el.getChildNodes();
    for (int i = 0; i < list.getLength(); i++) {
      if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
        Element nextElement = (Element) list.item(i);
        String paramName = nextElement.getNodeName();
        if (paramName.equals("monotonicShape"))
          monotonicShape = booleanFromXML(nextElement);
        else if (paramName.equals("maximumCharge"))
          maximumCharge = intFromXML(nextElement);
        else if (paramName.equals("rtTolerance")) {
          boolean isAbs = Boolean.parseBoolean(nextElement.getAttribute("isAbsolute"));
          double rt = doubleFromXML(nextElement);
          rtTolerance = new RTTolerance(isAbs, rt);
        } else if (paramName.equals("mzTolerance")) {
          double ppm = Double.parseDouble(nextElement.getAttribute("ppm"));
          double abs = Double.parseDouble(nextElement.getAttribute("abs"));
          mzTolerance = new MZTolerance(abs, ppm);
        }
      }
    }
  }

}
