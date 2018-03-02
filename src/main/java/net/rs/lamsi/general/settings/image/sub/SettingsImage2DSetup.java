package net.rs.lamsi.general.settings.image.sub;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.heatmap.dataoperations.DPReduction;
import net.rs.lamsi.general.heatmap.dataoperations.DPReduction.Mode;
import net.rs.lamsi.general.settings.Settings;


public class SettingsImage2DSetup extends Settings {
  // do not change the version!
  private static final long serialVersionUID = 1L;
  //
  private float velocity, spotsize;
  private double intensityFactor;
  private int redFactor;
  private DPReduction.Mode redMode;
  private boolean useReduction;


  public SettingsImage2DSetup(String path, String fileEnding) {
    super("SettingsImage2DSetup", path, fileEnding);
  }

  public SettingsImage2DSetup() {
    super("SettingsImage2DSetup", "/Settings/GeneralImage/", "setSetupImg");
  }


  public void setAll(float velocity, float spotsize, double intensityFactor, int redFactor,
      Mode redMode, boolean useReduction) {
    this.velocity = velocity;
    this.spotsize = spotsize;
    this.intensityFactor = intensityFactor;
    this.redFactor = redFactor;
    this.redMode = redMode;
    this.useReduction = useReduction;
  }

  @Override
  public void resetAll() {
    velocity = 1;
    spotsize = 1;
    intensityFactor = 1;
    redFactor = 1;
    redMode = Mode.AVG;
    useReduction = false;
  }

  @Override
  public void applyToImage(Collectable2D img) throws Exception {
    if (img instanceof Image2D) {
      applyToImage2D(((Image2D) img));
    }
  }

  public void applyToImage2D(Image2D img) {
    SettingsGeneralImage s = img.getSettings().getSettImage();
    s.setVelocity(velocity);
    s.setSpotsize(spotsize);
    s.setReduction(redFactor);
    s.setReductionMode(redMode);
    s.setUseReduction(useReduction);
    s.setIntensityFactor(intensityFactor);
  }

  // ##########################################################
  // xml input/output
  @Override
  public void appendSettingsValuesToXML(Element elParent, Document doc) {
    toXML(elParent, doc, "velocity", velocity);
    toXML(elParent, doc, "spotsize", spotsize);
    toXML(elParent, doc, "intensityFactor", intensityFactor);
    toXML(elParent, doc, "redFactor", redFactor);
    toXML(elParent, doc, "redMode", redMode);
    toXML(elParent, doc, "useReduction", useReduction);
  }

  @Override
  public void loadValuesFromXML(Element el, Document doc) {
    NodeList list = el.getChildNodes();
    for (int i = 0; i < list.getLength(); i++) {
      if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
        Element nextElement = (Element) list.item(i);
        String paramName = nextElement.getNodeName();
        if (paramName.equals("useReduction"))
          useReduction = booleanFromXML(nextElement);
        else if (paramName.equals("velocity"))
          velocity = floatFromXML(nextElement);
        else if (paramName.equals("spotsize"))
          spotsize = floatFromXML(nextElement);
        else if (paramName.equals("intensityFactor"))
          intensityFactor = doubleFromXML(nextElement);
        else if (paramName.equals("redFactor"))
          redFactor = intFromXML(nextElement);
        else if (paramName.equals("redMode"))
          redMode = Mode.valueOf(nextElement.getTextContent());
      }
    }
  }
}
