package net.rs.lamsi.general.settings.image.visualisation.themes;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLabelLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.chart.ui.RectangleInsets;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import net.rs.lamsi.general.heatmap.Heatmap;
import net.rs.lamsi.general.settings.Settings;

public class SettingsPaintscaleTheme extends Settings {
  // do not change the version!
  private static final long serialVersionUID = 1L;
  //

  // paintscale stuff
  // scientific intensities
  protected boolean useScientificIntensities = true;
  protected int significantDigits = 2;
  protected NumberFormat intensitiesNumberFormat;

  // paintscale title
  protected String paintScaleTitle = "I";
  protected boolean usePaintScaleTitle = true;
  // paintscale
  protected boolean isPaintScaleInPlot = false;

  // ps width and margins
  protected RectangleInsets psMargin = RectangleInsets.ZERO_INSETS;
  protected int psWidth = 10;

  protected double psTickUnit = 100;
  protected boolean autoSelectTickUnit = true;
  private boolean isPaintScaleVisible;

  public SettingsPaintscaleTheme() {
    super("SettingsPaintscaleTheme", "/Settings/Visualization/", "setPSStyle");
    resetAll();
  }


  public void setAll(boolean isPaintScaleInPlot, boolean useScientificIntensities,
      int significantDigits, String paintScaleTitle, boolean usePaintScaleTitle,
      RectangleInsets psMargin, int psWidth, double psTickUnit, boolean psAutoSelectTickUnit,
      boolean isPaintScaleVisible) {

    // significant intensities
    this.setPaintScaleInPlot(isPaintScaleInPlot);
    this.useScientificIntensities = useScientificIntensities;
    this.significantDigits = significantDigits;
    intensitiesNumberFormat = new DecimalFormat(useScientificIntensities ? "0.0E0" : "#.0");
    int digits = useScientificIntensities ? significantDigits - 1 : significantDigits;
    intensitiesNumberFormat.setMaximumFractionDigits(digits);
    intensitiesNumberFormat.setMinimumFractionDigits(digits);

    // paintscale title
    this.paintScaleTitle = paintScaleTitle;
    this.usePaintScaleTitle = usePaintScaleTitle;

    this.psMargin = psMargin;
    this.psWidth = psWidth;
    this.psTickUnit = psTickUnit;
    this.autoSelectTickUnit = psAutoSelectTickUnit;
    this.isPaintScaleVisible = isPaintScaleVisible;
  }

  @Override
  public void resetAll() {
    // significant intensities
    useScientificIntensities = true;
    significantDigits = 2;
    intensitiesNumberFormat = new DecimalFormat("0.0E0");
    intensitiesNumberFormat.setMaximumFractionDigits(significantDigits);
    intensitiesNumberFormat.setMinimumFractionDigits(significantDigits);

    // paintscale title
    paintScaleTitle = "I";
    usePaintScaleTitle = true;

    psMargin = new RectangleInsets(10, 5, 10, 5);
    psWidth = 10;
    autoSelectTickUnit = true;
    psTickUnit = 100;
    isPaintScaleVisible = true;
  }

  // ##########################################################
  // xml input/output
  @Override
  public void appendSettingsValuesToXML(Element elParent, Document doc) {
    toXML(elParent, doc, "isPaintScaleInPlot", isPaintScaleInPlot);
    toXML(elParent, doc, "significantDigits", significantDigits);
    toXML(elParent, doc, "useScientificIntensities", useScientificIntensities);

    toXML(elParent, doc, "paintScaleTitle", paintScaleTitle);
    toXML(elParent, doc, "usePaintScaleTitle", usePaintScaleTitle);

    toXML(elParent, doc, "psMargin", psMargin);
    toXML(elParent, doc, "psWidth", psWidth);
    toXML(elParent, doc, "autoSelectTickUnit", autoSelectTickUnit);
    toXML(elParent, doc, "psTickUnit", psTickUnit);
    toXML(elParent, doc, "isPaintScaleVisible", isPaintScaleVisible);
  }

  @Override
  public void loadValuesFromXML(Element el, Document doc) {
    NodeList list = el.getChildNodes();
    for (int i = 0; i < list.getLength(); i++) {
      if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
        Element nextElement = (Element) list.item(i);
        String paramName = nextElement.getNodeName();
        if (paramName.equals("isPaintScaleInPlot"))
          isPaintScaleInPlot = booleanFromXML(nextElement);
        else if (paramName.equals("significantDigits"))
          significantDigits = intFromXML(nextElement);
        else if (paramName.equals("useScientificIntensities"))
          useScientificIntensities = booleanFromXML(nextElement);
        else if (paramName.equals("paintScaleTitle"))
          paintScaleTitle = (nextElement.getTextContent());
        else if (paramName.equals("usePaintScaleTitle"))
          usePaintScaleTitle = booleanFromXML(nextElement);
        else if (paramName.equals("psMargin"))
          psMargin = insetsFromXML(nextElement);
        else if (paramName.equals("psWidth"))
          psWidth = Settings.intFromXML(nextElement);
        else if (paramName.equals("autoSelectTickUnit"))
          autoSelectTickUnit = booleanFromXML(nextElement);
        else if (paramName.equals("isPaintScaleVisible"))
          isPaintScaleVisible = booleanFromXML(nextElement);
        else if (paramName.equals("psTickUnit"))
          psTickUnit = doubleFromXML(nextElement);
      }
    }

    //
    if (paintScaleTitle.equals("null"))
      paintScaleTitle = null;
    // create numberformats
    intensitiesNumberFormat = new DecimalFormat(useScientificIntensities ? "0.0E0" : "#.0");
    int digits = significantDigits - (useScientificIntensities ? 1 : 0);
    intensitiesNumberFormat.setMaximumFractionDigits(digits);
    intensitiesNumberFormat.setMinimumFractionDigits(digits);
  }


  @Override
  public void applyToHeatMap(Heatmap heat) {
    super.applyToHeatMap(heat);
    applyToChart(heat.getChart());


    // set numberformat
    if (heat.getLegend() != null) {
      // paint scale
      PaintScaleLegend ps = heat.getLegend();
      NumberAxis psAxis = ((NumberAxis) ps.getAxis());

      ps.setVisible(isPaintScaleVisible);

      // margin and width
      ps.setMargin(psMargin);
      ps.setStripWidth(psWidth);

      // ticks
      psAxis.setAutoTickUnitSelection(autoSelectTickUnit, false);
      psAxis.setTickUnit(new NumberTickUnit(psTickUnit), false, false);

      // number format and title
      psAxis.setNumberFormatOverride(getIntensitiesNumberFormat());
      psAxis.setLabelLocation(AxisLabelLocation.HIGH_END);
      psAxis.setLabel(isUsePaintScaleTitle() ? getPaintScaleTitle() : null);
    }
  }

  /**
   * applies theme to chart
   * 
   * @param chart
   */
  public void applyToChart(JFreeChart chart) {}

  public boolean isUseScientificIntensities() {
    return useScientificIntensities;
  }

  public int getSignificantDigits() {
    return significantDigits;
  }

  public void setUseScientificIntensities(boolean useScientificIntensities) {
    this.useScientificIntensities = useScientificIntensities;
  }

  public void setSignificantDigits(int significantDigits) {
    this.significantDigits = significantDigits;
  }

  public NumberFormat getIntensitiesNumberFormat() {
    return intensitiesNumberFormat;
  }

  public String getPaintScaleTitle() {
    return paintScaleTitle;
  }

  public boolean isUsePaintScaleTitle() {
    return usePaintScaleTitle;
  }

  public void setPaintScaleTitle(String paintScaleTitle) {
    this.paintScaleTitle = paintScaleTitle;
  }

  public void setUsePaintScaleTitle(boolean usePaintScaleTitle) {
    this.usePaintScaleTitle = usePaintScaleTitle;
  }

  public boolean isPaintScaleInPlot() {
    return isPaintScaleInPlot;
  }

  public void setPaintScaleInPlot(boolean isPaintScaleInPlot) {
    this.isPaintScaleInPlot = isPaintScaleInPlot;
  }


  public RectangleInsets getPsMargin() {
    return psMargin;
  }


  public int getPsWidth() {
    return psWidth;
  }


  public double getPsTickUnit() {
    return psTickUnit;
  }


  public boolean isAutoSelectTickUnit() {
    return autoSelectTickUnit;
  }


  public void setPsMargin(RectangleInsets psMargin) {
    this.psMargin = psMargin;
  }


  public void setPsWidth(int psWidth) {
    this.psWidth = psWidth;
  }


  public void setPsTickUnit(double psTickUnit) {
    this.psTickUnit = psTickUnit;
  }


  public void setAutoSelectTickUnit(boolean autoSelectTickUnit) {
    this.autoSelectTickUnit = autoSelectTickUnit;
  }


  public boolean isPaintScaleVisible() {
    return isPaintScaleVisible;
  }

  public void setPaintScaleVisible(boolean isPaintScaleVisible) {
    this.isPaintScaleVisible = isPaintScaleVisible;
  }
}
