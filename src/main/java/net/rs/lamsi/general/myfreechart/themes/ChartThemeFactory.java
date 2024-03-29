package net.rs.lamsi.general.myfreechart.themes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.ui.RectangleInsets;

public class ChartThemeFactory {

  public enum THEME {
    // main themes
    BNW_PRINT, KARST, DARKNESS,
    // separate options
    FOR_PRINT, FOR_PRESENTATION;

    public static THEME getTheme(String ident) {
      // integer?
      try {
        int i = Integer.parseInt(ident);
        switch (i) {
          case 0:
            return BNW_PRINT;
          case 1:
            return KARST;
          case 2:
            return DARKNESS;
          case 50:
            return FOR_PRINT;
          case 51:
            return FOR_PRESENTATION;
        }
      } catch (Exception ex) {
      }
      // else value of
      return valueOf(ident);
    }

  }

  // the standard to be applied to all new charts
  protected static THEME standardTheme = THEME.BNW_PRINT;

  public static MyStandardChartTheme createChartTheme(THEME theme) {
    switch (theme) {
      case BNW_PRINT:
        return createBlackNWhiteTheme();
      case DARKNESS:
        return createDarknessTheme();
      case KARST:
        return createKarstTheme();
    }
    return createBlackNWhiteTheme();
  }

  public static MyStandardChartTheme changeChartThemeForPrintOrPresentation(
      MyStandardChartTheme theme, boolean forPrint) {
    if (forPrint) {
      // Fonts
      theme.setExtraLargeFont(new Font("Arial", Font.BOLD, 16));
      theme.setLargeFont(new Font("Arial", Font.BOLD, 11));
      theme.setRegularFont(new Font("Arial", Font.PLAIN, 11));
      theme.setSmallFont(new Font("Arial", Font.PLAIN, 11));
      theme.setFontShortTitle(new Font("Arial", Font.BOLD, 11));
    } else { // for presentation larger fonts
      // Fonts
      theme.setExtraLargeFont(new Font("Arial", Font.BOLD, 30));
      theme.setLargeFont(new Font("Arial", Font.BOLD, 20));
      theme.setRegularFont(new Font("Arial", Font.PLAIN, 16));
      theme.setSmallFont(new Font("Arial", Font.PLAIN, 16));

      theme.setFontShortTitle(new Font("Arial", Font.BOLD, 20));
    }
    return theme;
  }

  public static MyStandardChartTheme createBlackNWhiteTheme() {
    MyStandardChartTheme theme = new MyStandardChartTheme(THEME.BNW_PRINT, "BnW");
    // Fonts
    theme.setExtraLargeFont(new Font("Arial", Font.BOLD, 16));
    theme.setLargeFont(new Font("Arial", Font.BOLD, 11));
    theme.setRegularFont(new Font("Arial", Font.PLAIN, 11));
    theme.setSmallFont(new Font("Arial", Font.PLAIN, 11));

    // Paints
    theme.setTitlePaint(Color.black);
    theme.setSubtitlePaint(Color.black);
    theme.setLegendItemPaint(Color.black);
    theme.setPlotOutlinePaint(Color.black);
    theme.setBaselinePaint(Color.black);
    theme.setCrosshairPaint(Color.black);
    theme.setLabelLinkPaint(Color.black);
    theme.setTickLabelPaint(Color.black);
    theme.setAxisLabelPaint(Color.black);
    theme.setShadowPaint(Color.black);
    theme.setItemLabelPaint(Color.black);

    theme.setLegendBackgroundPaint(Color.white);
    theme.setChartBackgroundPaint(Color.white);
    theme.setPlotBackgroundPaint(Color.white);

    //// theme.setDrawingSupplier(new DefaultDrawingSupplier(
    // new Paint[] {Color.BLACK, Color.decode("0x0036CC"), Color.decode("0xFF0000"),
    // Color.decode("0xFFFF7F"), Color.decode("0x6681CC"), Color.decode("0xFF7F7F"),
    // Color.decode("0xFFFFBF"), Color.decode("0x99A6CC"), Color.decode("0xFFBFBF"),
    // Color.decode("0xA9A938"), Color.decode("0x2D4587")},
    // new Paint[] {Color.decode("0xFFFF00"), Color.decode("0x0036CC")},
    // new Stroke[] {new BasicStroke(2.0f)}, new Stroke[] {new BasicStroke(0.5f)},
    // DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE));
    theme.setErrorIndicatorPaint(Color.black);
    theme.setGridBandPaint(new Color(255, 255, 255, 20));
    theme.setGridBandAlternatePaint(new Color(255, 255, 255, 40));

    // axis
    Color transp = new Color(0, 0, 0, 200);
    theme.setRangeGridlinePaint(transp);
    theme.setDomainGridlinePaint(transp);

    theme.setAxisLinePaint(Color.black);

    // axis offset
    theme.setAxisOffset(new RectangleInsets(0, 0, 0, 0));

    return theme;
  }


  /**
   * Creates and returns a theme called "Darkness". In this theme, the charts have a black
   * background.
   *
   * @return The "Darkness" theme.
   */
  public static MyStandardChartTheme createDarknessTheme() {
    MyStandardChartTheme theme = new MyStandardChartTheme(THEME.DARKNESS, "Darkness");
    // Fonts
    theme.setExtraLargeFont(new Font("Arial", Font.BOLD, 20));
    theme.setLargeFont(new Font("Arial", Font.BOLD, 11));
    theme.setRegularFont(new Font("Arial", Font.PLAIN, 11));
    theme.setSmallFont(new Font("Arial", Font.PLAIN, 11));
    //
    theme.setTitlePaint(Color.white);
    theme.setSubtitlePaint(Color.white);
    theme.setLegendBackgroundPaint(Color.black);
    theme.setLegendItemPaint(Color.white);
    theme.setChartBackgroundPaint(Color.black);
    theme.setPlotBackgroundPaint(Color.black);
    theme.setPlotOutlinePaint(Color.yellow);
    theme.setBaselinePaint(Color.white);
    theme.setCrosshairPaint(Color.red);
    theme.setLabelLinkPaint(Color.lightGray);
    theme.setTickLabelPaint(Color.white);
    theme.setAxisLabelPaint(Color.white);
    theme.setShadowPaint(Color.darkGray);
    theme.setItemLabelPaint(Color.white);
    theme.setDrawingSupplier(new DefaultDrawingSupplier(
        new Paint[] {Color.WHITE, Color.decode("0xFFFF00"), Color.decode("0x0036CC"),
            Color.decode("0xFF0000"), Color.decode("0xFFFF7F"), Color.decode("0x6681CC"),
            Color.decode("0xFF7F7F"), Color.decode("0xFFFFBF"), Color.decode("0x99A6CC"),
            Color.decode("0xFFBFBF"), Color.decode("0xA9A938"), Color.decode("0x2D4587")},
        new Paint[] {Color.decode("0xFFFF00"), Color.decode("0x0036CC")},
        new Stroke[] {new BasicStroke(2.0f)}, new Stroke[] {new BasicStroke(0.5f)},
        DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE));
    theme.setErrorIndicatorPaint(Color.lightGray);
    theme.setGridBandPaint(new Color(255, 255, 255, 20));
    theme.setGridBandAlternatePaint(new Color(255, 255, 255, 40));

    // axis
    Color transp = new Color(255, 255, 255, 200);
    theme.setRangeGridlinePaint(transp);
    theme.setDomainGridlinePaint(transp);

    theme.setAxisLinePaint(Color.white);

    theme.setMasterFontColor(Color.WHITE);
    // axis offset
    theme.setAxisOffset(new RectangleInsets(0, 0, 0, 0));
    return theme;
  }

  /**
   * Creates and returns a theme called "Darkness". In this theme, the charts have a black
   * background.
   *
   * @return The "Darkness" theme.
   */
  public static MyStandardChartTheme createKarstTheme() {
    MyStandardChartTheme theme = new MyStandardChartTheme(THEME.KARST, "Karst");
    // Fonts
    theme.setExtraLargeFont(new Font("Arial", Font.BOLD, 20));
    theme.setLargeFont(new Font("Arial", Font.BOLD, 11));
    theme.setRegularFont(new Font("Arial", Font.PLAIN, 11));
    theme.setSmallFont(new Font("Arial", Font.PLAIN, 11));
    //
    Paint bg = new Color(50, 50, 202);
    //
    theme.setTitlePaint(Color.green);
    theme.setSubtitlePaint(Color.yellow);
    theme.setLegendBackgroundPaint(bg);
    theme.setLegendItemPaint(Color.yellow);
    theme.setChartBackgroundPaint(bg);
    theme.setPlotBackgroundPaint(bg);
    theme.setPlotOutlinePaint(Color.yellow);
    theme.setBaselinePaint(Color.white);
    theme.setCrosshairPaint(Color.red);
    theme.setLabelLinkPaint(Color.lightGray);
    theme.setTickLabelPaint(Color.yellow);
    theme.setAxisLabelPaint(Color.yellow);
    theme.setShadowPaint(Color.darkGray);
    theme.setItemLabelPaint(Color.yellow);
    theme.setDrawingSupplier(new DefaultDrawingSupplier(
        new Paint[] {Color.decode("0xFFFF00"), Color.decode("0x0036CC"), Color.decode("0xFF0000"),
            Color.decode("0xFFFF7F"), Color.decode("0x6681CC"), Color.decode("0xFF7F7F"),
            Color.decode("0xFFFFBF"), Color.decode("0x99A6CC"), Color.decode("0xFFBFBF"),
            Color.decode("0xA9A938"), Color.decode("0x2D4587")},
        new Paint[] {Color.decode("0xFFFF00"), Color.decode("0x0036CC")},
        new Stroke[] {new BasicStroke(2.0f)}, new Stroke[] {new BasicStroke(0.5f)},
        DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE));
    theme.setErrorIndicatorPaint(Color.lightGray);
    theme.setGridBandPaint(new Color(255, 255, 255, 20));
    theme.setGridBandAlternatePaint(new Color(255, 255, 255, 40));

    // axis
    Color transp = new Color(255, 255, 255, 200);
    theme.setRangeGridlinePaint(transp);
    theme.setDomainGridlinePaint(transp);

    theme.setAxisLinePaint(Color.yellow);
    theme.setMasterFontColor(Color.yellow);

    // axis offset
    theme.setAxisOffset(new RectangleInsets(0, 0, 0, 0));
    return theme;
  }


  public static MyStandardChartTheme getStandardTheme() {
    return createChartTheme(standardTheme);
  }

  public static void setStandardTheme(THEME theme) {
    standardTheme = theme;
  }
}
