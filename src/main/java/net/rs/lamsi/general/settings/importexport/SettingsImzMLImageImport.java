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
  private double[][] importList;

  private boolean useWindow = false;
  private double window = 0.02;


  public SettingsImzMLImageImport() {
    super("SettingsImzMLImageImport", "/Settings/Import/", "imzml2img");
    resetAll();
  }

  @Override
  public void resetAll() {
    importList = new double[0][0];
    useWindow = false;
    window = 0.02;
  }


  // ##########################################################
  // xml input/output
  @Override
  public void appendSettingsValuesToXML(Element elParent, Document doc) {
    StringBuilder s = new StringBuilder();
    for (int i = 0; i < importList.length; i++) {
      s.append(StringUtils.join(importList[i], ','));
      if (i < importList.length - 1)
        s.append(";");
    }

    toXML(elParent, doc, "importList", s.toString());
    toXML(elParent, doc, "useWindow", useWindow);
    toXML(elParent, doc, "window", window);
  }

  @Override
  public void loadValuesFromXML(Element el, Document doc) {
    NodeList list = el.getChildNodes();
    for (int i = 0; i < list.getLength(); i++) {
      if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
        Element nextElement = (Element) list.item(i);
        String paramName = nextElement.getNodeName();
        if (paramName.equals("useWindow"))
          useWindow = booleanFromXML(nextElement);
        else if (paramName.equals("window"))
          window = doubleFromXML(nextElement);
        else if (paramName.equals("importList")) {
          String s = nextElement.getTextContent();
          String[] values = s.split(";");
          importList = new double[values.length][2];
          for (int j = 0; j < values.length; j++) {
            String[] sep = values[j].split(",");
            importList[j][0] = Double.parseDouble(sep[0]);
            importList[j][1] = Double.parseDouble(sep[1]);
          }
        }
      }
    }
  }

  public double[][] getImportList() {
    return importList;
  }

  public void setImportList(double[][] importList) {
    this.importList = importList;
  }

  public boolean isUseWindow() {
    return useWindow;
  }

  public void setUseWindow(boolean useWindow) {
    this.useWindow = useWindow;
  }

  public double getWindow() {
    return window;
  }

  public void setWindow(double window) {
    this.window = window;
  }

}
