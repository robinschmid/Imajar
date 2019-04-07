package net.rs.lamsi.general.settings.importexport;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import net.rs.lamsi.general.settings.Settings;

public class SettingsImzMLImageImport extends Settings {
  // do not change the version!
  private static final long serialVersionUID = 1L;
  //
  // [entry][center , window]
  private double[][] importListImzML;
  private boolean useImzMLWindow = false;
  private double imzMLWindow = 0.02;


  public SettingsImzMLImageImport() {
    super("SettingsImzMLImageImport", "/Settings/Import/", "imzml2img");
    resetAll();
  }

  @Override
  public void resetAll() {
    importListImzML = new double[0][0];
    useImzMLWindow = false;
    imzMLWindow = 0.02;
  }

  public void setAll(double[][] importListImzML, boolean useImzMLWindow, double imzMLWindow) {
    this.importListImzML = importListImzML;
    this.useImzMLWindow = useImzMLWindow;
    this.imzMLWindow = imzMLWindow;
  }

  // ##########################################################
  // xml input/output
  @Override
  public void appendSettingsValuesToXML(Element elParent, Document doc) {
    StringBuilder s = new StringBuilder();
    for (int i = 0; i < importListImzML.length; i++) {
      s.append(StringUtils.join(importListImzML[i], ','));
      if (i < importListImzML.length - 1)
        s.append(";");
    }

    toXML(elParent, doc, "importList", s.toString());
    toXML(elParent, doc, "useWindow", useImzMLWindow);
    toXML(elParent, doc, "window", imzMLWindow);
  }

  @Override
  public void loadValuesFromXML(Element el, Document doc) {
    NodeList list = el.getChildNodes();
    for (int i = 0; i < list.getLength(); i++) {
      if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
        Element nextElement = (Element) list.item(i);
        String paramName = nextElement.getNodeName();
        if (paramName.equals("useWindow"))
          useImzMLWindow = booleanFromXML(nextElement);
        else if (paramName.equals("window"))
          imzMLWindow = doubleFromXML(nextElement);
        else if (paramName.equals("importList")) {
          String s = nextElement.getTextContent();
          String[] values = s.split(";");
          importListImzML = new double[values.length][2];
          for (int j = 0; j < values.length; j++) {
            String[] sep = values[j].split(",");
            importListImzML[j][0] = Double.parseDouble(sep[0]);
            importListImzML[j][1] = Double.parseDouble(sep[1]);
          }
        }
      }
    }
  }

  public double[][] getImportList() {
    return importListImzML;
  }

  public void setImportList(double[][] importList) {
    this.importListImzML = importList;
  }

  public boolean isUseWindow() {
    return useImzMLWindow;
  }

  public void setUseWindow(boolean useWindow) {
    this.useImzMLWindow = useWindow;
  }

  public double getWindow() {
    return imzMLWindow;
  }

  public void setWindow(double window) {
    this.imzMLWindow = window;
  }

}
