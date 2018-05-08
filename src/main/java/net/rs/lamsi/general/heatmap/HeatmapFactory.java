package net.rs.lamsi.general.heatmap;

import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.annotations.XYImageAnnotation;
import org.jfree.chart.annotations.XYTitleAnnotation;
import org.jfree.chart.axis.AxisLabelLocation;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.chart.ui.Layer;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.ImageMerge;
import net.rs.lamsi.general.datamodel.image.ImageOverlay;
import net.rs.lamsi.general.datamodel.image.SingleParticleImage;
import net.rs.lamsi.general.datamodel.image.data.twodimensional.XYIDataMatrix;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.datamodel.image.interf.DataCollectable2D;
import net.rs.lamsi.general.heatmap.dataoperations.FastGaussianBlur;
import net.rs.lamsi.general.myfreechart.gestures.ChartGesture.Button;
import net.rs.lamsi.general.myfreechart.gestures.ChartGesture.Entity;
import net.rs.lamsi.general.myfreechart.gestures.ChartGesture.Key;
import net.rs.lamsi.general.myfreechart.gestures.ChartGestureDragDiffHandler;
import net.rs.lamsi.general.myfreechart.plot.JFreeSquaredChart;
import net.rs.lamsi.general.myfreechart.plot.XYSquaredPlot;
import net.rs.lamsi.general.myfreechart.plots.image2d.EImage2DChartPanel;
import net.rs.lamsi.general.myfreechart.plots.image2d.FullImageRenderer;
import net.rs.lamsi.general.myfreechart.plots.image2d.ImageOverlayRenderer;
import net.rs.lamsi.general.myfreechart.plots.image2d.ImageRenderer;
import net.rs.lamsi.general.myfreechart.plots.image2d.annot.BGImageAnnotation;
import net.rs.lamsi.general.myfreechart.plots.image2d.annot.ImageTitle;
import net.rs.lamsi.general.myfreechart.plots.image2d.annot.ScaleInPlot;
import net.rs.lamsi.general.myfreechart.plots.image2d.datasets.DataCollectable2DDataset;
import net.rs.lamsi.general.myfreechart.plots.image2d.datasets.DataCollectable2DListDataset;
import net.rs.lamsi.general.myfreechart.plots.image2d.merge.MergeDragDiffHandler;
import net.rs.lamsi.general.processing.dataoperations.DataInterpolator;
import net.rs.lamsi.general.settings.image.SettingsImageMerge;
import net.rs.lamsi.general.settings.image.SettingsImageOverlay;
import net.rs.lamsi.general.settings.image.sub.SettingsGeneralCollecable2D;
import net.rs.lamsi.general.settings.image.sub.SettingsGeneralImage;
import net.rs.lamsi.general.settings.image.visualisation.SettingsBackgroundImg;
import net.rs.lamsi.general.settings.image.visualisation.SettingsPaintScale;
import net.rs.lamsi.general.settings.image.visualisation.SettingsPaintScale.ValueMode;
import net.rs.lamsi.general.settings.image.visualisation.paintscales.SingleColorPaintScale;
import net.rs.lamsi.general.settings.image.visualisation.themes.SettingsPaintscaleTheme;
import net.rs.lamsi.general.settings.image.visualisation.themes.SettingsThemesContainer;



public class HeatmapFactory {
  private static final Logger logger = LoggerFactory.getLogger(HeatmapFactory.class);

  /**
   * generate heatmap from data
   * 
   * @param settings
   * @param settImage
   * @param title
   * @param xvalues
   * @param yvalues
   * @param zvalues
   * @return
   * @throws Exception
   */
  public static Heatmap generateHeatmap(Image2D img, String title, double[][] data)
      throws Exception {
    return createChart(img, createDataset(title, data));
  }

  // creates jfreechart plot for heatmap
  private static Heatmap createChart(Image2D img, IXYZDataset dataset) throws Exception {
    return createChart(img, (SettingsPaintScale) img.getSettingsByClass(SettingsPaintScale.class),
        (SettingsGeneralImage) img.getSettingsByClass(SettingsGeneralImage.class), dataset, "x",
        "y");
  }

  // erstellt ein JFreeChart Plot der heatmap
  // bwidth und bheight (BlockWidth) sind die Maximalwerte
  private static Heatmap createChart(final Image2D img, SettingsPaintScale settings,
      SettingsGeneralImage settImage, IXYZDataset dataset, String xTitle, String yTitle)
      throws Exception {
    // this min max values in array
    double zmin = dataset.getZMin();
    double zmax = dataset.getZMax();
    // no data!
    if (zmin == zmax || zmax == 0) {
      throw new Exception("Every data point has the same intensity of " + zmin);
    } else {
      SettingsThemesContainer setTheme = img.getSettTheme();
      SettingsPaintscaleTheme psTheme = setTheme.getSettPaintscaleTheme();
      // XAchse
      NumberAxis xAxis = new NumberAxis(xTitle);
      xAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());
      xAxis.setLowerMargin(0.0);
      xAxis.setUpperMargin(0.0);
      // Y Achse
      NumberAxis yAxis = new NumberAxis(yTitle);
      yAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());
      yAxis.setLowerMargin(0.0);
      yAxis.setUpperMargin(0.0);
      // XYBlockRenderer
      FullImageRenderer renderer = new FullImageRenderer();

      // PaintScale für farbe? TODO mit Settings!
      // TODO upper and lower value setzen!!!!
      // two ways of min or max z value:
      // min max values by filter
      if (settings.getModeMin().equals(ValueMode.PERCENTILE)) {
        // uses filter for min
        img.applyCutFilterMin(settings.getMinFilter());
        settings.setMin(img.getMinZFiltered());
      }
      if (settings.getModeMax().equals(ValueMode.PERCENTILE)) {
        // uses filter for min
        img.applyCutFilterMax(settings.getMaxFilter());
        settings.setMax(img.getMaxZFiltered());
      }
      // creation of scale
      // binary data scale? 1, 10, 11, 100, 101, 111, 1000, 1001
      PaintScale scale = null;
      scale = PaintScaleGenerator.generateStepPaintScale(zmin, zmax, settings);
      renderer.setPaintScale(scale);
      renderer.setAutoPopulateSeriesFillPaint(true);
      renderer.setBlockAnchor(RectangleAnchor.BOTTOM_LEFT);

      // Plot erstellen mit daten
      XYSquaredPlot plot = new XYSquaredPlot(dataset, xAxis, yAxis, renderer);
      plot.setBackgroundPaint(Color.lightGray);
      plot.setDomainGridlinesVisible(false);
      plot.setRangeGridlinePaint(Color.white);

      // set background image
      if (img.getImageGroup() != null) {
        Image bg = img.getImageGroup().getBGImage();
        if (bg != null) {
          // plot.setBackgroundImage(bg);
          XYImageAnnotation ann =
              new BGImageAnnotation(bg, img.getImageGroup().getSettings().getSettBGImg(),
                  img.getWidth(false), img.getHeight(false));

          renderer.addAnnotation(ann, Layer.BACKGROUND);
        }
      }

      // create chart
      JFreeSquaredChart chart = new JFreeSquaredChart("XYBlockChartDemo1", plot);
      // remove lower legend - wie farbskala rein? TODO
      chart.removeLegend();
      chart.setBackgroundPaint(Color.white);

      // Legend Generieren
      PaintScale scaleBar =
          PaintScaleGenerator.generateStepPaintScaleForLegend(zmin, zmax, settings);
      PaintScaleLegend legend = createScaleLegend(img, scaleBar,
          createScaleAxis(settings, setTheme), settings.getLevels());
      // adding legend in plot or outside
      if (psTheme.isPaintScaleInPlot()) { // inplot
        XYTitleAnnotation ta = new XYTitleAnnotation(1, 0.0, legend, RectangleAnchor.BOTTOM_RIGHT);
        ta.setMaxWidth(1);
        plot.addAnnotation(ta);
      } else
        chart.addSubtitle(legend);
      //
      chart.setBorderVisible(true);


      // ChartPanel
      EImage2DChartPanel chartPanel = new EImage2DChartPanel(chart, img);

      // add scale legend
      ScaleInPlot scaleInPlot = addScaleInPlot(setTheme, chartPanel);

      // add short title
      ImageTitle shortTitle = new ImageTitle(img, setTheme.getTheme().getFontShortTitle(),
          setTheme.getTheme().getcShortTitle(), setTheme.getTheme().getcBGShortTitle(),
          settImage.isShowShortTitle(), settImage.getXPosTitle(), settImage.getYPosTitle());
      plot.addAnnotation(shortTitle.getAnnotation());

      // theme
      img.getSettTheme().applyToChart(chart);

      // ChartUtilities.applyCurrentTheme(chart);
      // defaultChartTheme.apply(chart);
      chart.fireChartChanged();

      chart.setBorderVisible(false);

      // Heatmap
      Heatmap heat = new Heatmap(dataset, settings.getLevels(), chartPanel, scale, chart, plot,
          legend, img, renderer, scaleInPlot, shortTitle);

      // return Heatmap
      return heat;
    }
  }



  public static Heatmap generateHeatmap(Image2D img, String title, double[] xvalues,
      double[] yvalues, double[] zvalues) throws Exception {
    return generateHeatmap(img, title, new double[][] {xvalues, yvalues, zvalues});
  }

  /**
   * generates a heatmap from raw data
   * 
   * @param title
   * @param data
   * @return
   * @throws Exception
   */
  public static Heatmap generateHeatmap(String title, double[][] data) throws Exception {
    return createRawChart(createDataset(title, data), "x", "y");
  }

  /**
   * generates a heatmap from raw data
   * 
   * @param title
   * @param data
   * @return
   * @throws Exception
   */
  public static Heatmap generateHeatmap(String title, double[] xvalues, double[] yvalues,
      double[] zvalues) throws Exception {
    return generateHeatmap(title, new double[][] {xvalues, yvalues, zvalues});
  }

  // Image2D to Heatmap Image
  public static Heatmap generateHeatmap(Collectable2D image) throws Exception {
    if (Image2D.class.isInstance(image)) {
      return generateHeatmapFromImage2D((Image2D) image);
    } else if (ImageOverlay.class.isInstance(image))
      return generateHeatmapFromImageOverlay((ImageOverlay) image);
    else if (ImageMerge.class.isInstance(image))
      return generateHeatmapFromImageMerge((ImageMerge) image);
    else if (image.isSPImage())
      return generateHeatmapFromSPImage((SingleParticleImage) image);
    else
      return null;
  }

  private static Heatmap generateHeatmapFromImage2D(Image2D image) throws Exception {
    SettingsPaintScale setPaint = image.getSettings().getSettPaintScale();
    SettingsGeneralImage setImg = image.getSettings().getSettImage();
    // Heatmap erzeugen
    // Heatmap h = createChart(image, setPaint, setImg, createDataset(image));
    Heatmap h = createImage2DChart(image, setPaint, setImg, "x", "y");
    // TODO WHY?

    image.getSettings().applyToHeatMap(h);
    return h;
  }



  private static Heatmap generateHeatmapFromSPImage(SingleParticleImage image) throws Exception {
    // Heatmap erzeugen
    Heatmap h = createSPChart(image);
    // TODO WHY?

    image.getSettings().applyToHeatMap(h);
    return h;
  }

  // TODO test contour plot
  // private static Heatmap createContourChart(final Image2D img, double[][] dataset, String xTitle,
  // String yTitle) throws Exception {
  //
  // }
  // this min max values in array


  /**
   * creates a dataset with or without interpolation/reduction and with an option for blurring
   * 
   * @param image
   * @return
   */
  private static IXYZDataset createDataset(Image2D image) {
    SettingsGeneralImage sett = image.getSettings().getSettImage();
    double[][] dat = null;
    // interpolation
    int f = (int) sett.getInterpolation();
    // reduction
    int red = (int) (1 / sett.getInterpolation());

    // applies cropping filter if needed
    if (sett.isUseInterpolation() && (f > 1 || red > 1)) {
      // get matrices
      XYIDataMatrix data = image.toXYIDataMatrix(false, true);
      // interpolate to array [3][n]
      dat = DataInterpolator.interpolateToArray(data, sett.getInterpolation());
    } else {
      // get rotated and reflected dataset
      dat = image.toXYIArray(false, true);
    }
    // blur?
    if (sett.isUseBlur()) {
      double[] z = dat[2];
      double target[] = new double[z.length];
      int w = image.getMinLineLength();
      if (sett.isUseInterpolation()) {
        if (f > 1)
          w *= f;
        else if (red > 1)
          w /= red;
      }
      int h = z.length / w;
      FastGaussianBlur.applyBlur(z, target, w, h, sett.getBlurRadius());
      // set data
      dat[2] = target;
    }
    // return dataset
    return createDataset(image.getSettings().getSettImage().getTitle(), dat);
  }


  /**
   * creates a dataset with or without interpolation/reduction and with an option for blurring
   * 
   * @param image
   * @return
   */
  private static IXYZDataset createDataset(SingleParticleImage image) {
    double[][] dat = null;
    // get matrices
    dat = image.updateFilteredDataCountsArray();
    // return dataset
    return createDataset(image.getSettings().getSettImage().getTitle(), dat);
  }


  private static Heatmap generateHeatmapFromImageOverlay(ImageOverlay image) throws Exception {
    // get rotated and reflected dataset
    double[][][] dat = image.getDataSets();
    // Heatmap erzeugen
    Heatmap h = createChartOverlay(image, createDatasetOverlay(image.getTitles(), dat), "x", "y");
    // TODO WHY?

    image.getSettings().applyToHeatMap(h);
    return h;
  }

  // erstellt ein JFreeChart Plot der heatmap
  // bwidth und bheight (BlockWidth) sind die Maximalwerte
  private static Heatmap createChartOverlay(final ImageOverlay img, IXYZDataset dataset,
      String xTitle, String yTitle) throws Exception {
    SettingsImageOverlay settings = img.getSettings();
    int seriesCount = dataset.getSeriesCount();
    SettingsThemesContainer setTheme = img.getSettTheme();
    // axes
    NumberAxis xAxis = createAxis(xTitle);
    NumberAxis yAxis = createAxis(yTitle);
    // XYBlockRenderer
    ImageOverlayRenderer renderer = new ImageOverlayRenderer(seriesCount, settings.getBlend());

    List<PaintScale> psList = new ArrayList<PaintScale>();
    // create one paintscale for each active image
    int counter = 0;
    for (int i = 0; i < img.size(); i++) {
      if (settings.isActive(img.get(i))) {
        Image2D cimg = img.get(i);
        SettingsPaintScale ps = settings.getSettPaintScale(i);
        // PaintScale für farbe? TODO mit Settings!
        // TODO upper and lower value setzen!!!!
        double zmin = dataset.getZMin(counter);
        double zmax = dataset.getZMax(counter);
        // two ways of min or max z value:
        // min max values by filter
        if (ps.getModeMin().equals(ValueMode.PERCENTILE)) {
          // uses filter for min
          double nmin = cimg.getValueCutFilter(ps.getMinFilter(), ps.isUsesMinMaxFromSelection());
          ps.setMin(nmin);
        }
        if (ps.getModeMax().equals(ValueMode.PERCENTILE)) {
          // uses filter for min
          double nmax =
              cimg.getValueCutFilter(100.0 - ps.getMaxFilter(), ps.isUsesMinMaxFromSelection());
          ps.setMax(nmax);
        }

        // TODO delete this
        // ps.setUsesMinMax(false);
        ps.setUsesBAsMax(true);
        ps.setInverted(true);
        ps.setUsesWAsMin(false);
        PaintScale scale = PaintScaleGenerator.generateStepPaintScale(zmin, zmax, ps);
        psList.add(scale);

        // TODO nicht feste Blockwidth!
        // erstmal feste BlockWidth
        renderer.setBlockWidth(counter, cimg.getMaxBlockWidth());
        renderer.setBlockHeight(counter, cimg.getMaxBlockHeight());


        renderer.setSeriesPaint(counter, ps.getMinColor());

        counter++;
      }
    }
    // creation of scale
    // binary data scale? 1, 10, 11, 100, 101, 111, 1000, 1001
    PaintScale[] scales = psList.toArray(new PaintScale[psList.size()]);

    // set all paint scales
    renderer.setPaintScales(scales);
    renderer.setBlockAnchor(RectangleAnchor.BOTTOM_LEFT);

    // Plot erstellen mit daten
    XYSquaredPlot plot = new XYSquaredPlot(dataset, xAxis, yAxis, renderer);

    // set background image
    if (img.getImageGroup() != null) {
      Image bg = img.getImageGroup().getBGImage();
      SettingsBackgroundImg settBG = img.getImageGroup().getSettings().getSettBGImg();
      if (bg != null) {
        // plot.setBackgroundImage(bg);
        XYImageAnnotation ann =
            new BGImageAnnotation(bg, settBG, img.getWidth(false), img.getHeight(false));

        renderer.addAnnotation(ann, Layer.BACKGROUND);
      }
    }

    // create chart
    JFreeSquaredChart chart = new JFreeSquaredChart("", plot);
    EImage2DChartPanel chartPanel = new EImage2DChartPanel(chart, img, true, true, false, true);

    // add scale legend
    ScaleInPlot scaleInPlot = addScaleInPlot(setTheme, chartPanel);

    // add short title
    ImageTitle shortTitle = new ImageTitle(img, setTheme.getTheme().getFontShortTitle(),
        setTheme.getTheme().getcShortTitle(), setTheme.getTheme().getcBGShortTitle(), false, 0.9f,
        0.9f);
    plot.addAnnotation(shortTitle.getAnnotation());

    // theme
    img.getSettTheme().applyToChart(chart);
    chart.fireChartChanged();

    // Heatmap
    Heatmap heat = new Heatmap(dataset, chartPanel, scales, chart, plot, img, renderer, scaleInPlot,
        shortTitle);

    // return Heatmap
    return heat;
  }


  private static Heatmap generateHeatmapFromImageMerge(ImageMerge image) throws Exception {
    // create Dataset
    DataCollectable2DListDataset dataset = new DataCollectable2DListDataset(image);
    // Heatmap erzeugen
    Heatmap h = createChartImageMerge(image, dataset, "x", "y");
    // TODO WHY?

    image.getSettings().applyToHeatMap(h);
    return h;
  }

  // erstellt ein JFreeChart Plot der heatmap
  // bwidth und bheight (BlockWidth) sind die Maximalwerte
  private static Heatmap createChartImageMerge(final ImageMerge img,
      DataCollectable2DListDataset dataset, String xTitle, String yTitle) throws Exception {
    SettingsImageMerge settings = img.getSettings();
    int seriesCount = dataset.getSeriesCount();
    SettingsThemesContainer setTheme = img.getSettTheme();
    // axes
    NumberAxis xAxis = createAxis(xTitle);
    NumberAxis yAxis = createAxis(yTitle);
    // XYBlockRenderer
    FullImageRenderer renderer = new FullImageRenderer();

    List<PaintScale> psList = new ArrayList<>();
    // create one paintscale for each active image
    for (int i = 0; i < img.size(); i++) {
      DataCollectable2D d = img.getImages().get(i);
      DataCollectable2DDataset data = dataset.getDataset(i);

      SettingsPaintScale ps = d.getPaintScaleSettings();
      if (ps != null) {
        double zmin = data.getMinIntensity();
        double zmax = data.getMaxIntensity();
        // two ways of min or max z value:
        // min max values by filter
        if (ps.getModeMin().equals(ValueMode.PERCENTILE)) {
          // uses filter for min
          double nmin = d.getValueCutFilter(ps.getMinFilter(), ps.isUsesMinMaxFromSelection());
          ps.setMin(nmin);
        }
        if (ps.getModeMax().equals(ValueMode.PERCENTILE)) {
          // uses filter for min
          double nmax =
              d.getValueCutFilter(100.0 - ps.getMaxFilter(), ps.isUsesMinMaxFromSelection());
          ps.setMax(nmax);
        }

        PaintScale scale = PaintScaleGenerator.generateStepPaintScale(zmin, zmax, ps);
        psList.add(scale);
      }
    }

    // creation of scale
    // binary data scale? 1, 10, 11, 100, 101, 111, 1000, 1001
    PaintScale[] scales = psList.toArray(new PaintScale[psList.size()]);

    // set all paint scales
    renderer.setPaintScale(scales);
    renderer.setBlockAnchor(RectangleAnchor.BOTTOM_LEFT);

    // create plot
    XYSquaredPlot plot = new XYSquaredPlot(dataset, xAxis, yAxis, renderer);

    // set background image
    if (img.getImageGroup() != null) {
      Image bg = img.getImageGroup().getBGImage();
      SettingsBackgroundImg settBG = img.getImageGroup().getSettings().getSettBGImg();
      if (bg != null) {
        // plot.setBackgroundImage(bg);
        XYImageAnnotation ann =
            new BGImageAnnotation(bg, settBG, img.getWidth(false), img.getHeight(false));

        renderer.addAnnotation(ann, Layer.BACKGROUND);
      }
    }

    // create chart
    JFreeSquaredChart chart = new JFreeSquaredChart("", plot);
    EImage2DChartPanel chartPanel = new EImage2DChartPanel(chart, img, true, true, false, true);

    // add scale legend
    ScaleInPlot scaleInPlot = addScaleInPlot(setTheme, chartPanel);

    // add short title
    ImageTitle shortTitle = new ImageTitle(img, setTheme.getTheme().getFontShortTitle(),
        setTheme.getTheme().getcShortTitle(), setTheme.getTheme().getcBGShortTitle(), false, 0.9f,
        0.9f);
    plot.addAnnotation(shortTitle.getAnnotation());

    // theme
    img.getSettTheme().applyToChart(chart);
    chart.fireChartChanged();

    // add gestures to shift single images

    ChartGestureDragDiffHandler handler =
        new MergeDragDiffHandler(img, chartPanel, Entity.XY_ITEM, Button.BUTTON1, Key.ALT);
    chartPanel.getGestureAdapter().addGestureHandler(handler);

    // Heatmap
    Heatmap heat = new Heatmap(dataset, chartPanel, scales, chart, plot, img, renderer, scaleInPlot,
        shortTitle);

    // return Heatmap
    return heat;
  }


  // erstellt ein JFreeChart Plot der heatmap
  // bwidth und bheight (BlockWidth) sind die Maximalwerte
  private static Heatmap createImage2DChart(final Image2D img, SettingsPaintScale settings,
      SettingsGeneralImage settImage, String xTitle, String yTitle) throws Exception {

    DataCollectable2DDataset dataset = new DataCollectable2DDataset(img);
    dataset.applyPostProcessing();
    // absolute min max
    double zmin = dataset.getMinIntensity();
    double zmax = dataset.getMaxIntensity();
    // no data!
    if (zmin >= zmax)
      zmax = zmin + 1;

    logger.warn("Every data point has the same intensity of {}", zmin);
    SettingsThemesContainer setTheme = img.getSettTheme();
    SettingsPaintscaleTheme psTheme = setTheme.getSettPaintscaleTheme();
    // axes
    NumberAxis xAxis = createAxis(xTitle);
    NumberAxis yAxis = createAxis(yTitle);
    // XYBlockRenderer
    FullImageRenderer renderer = new FullImageRenderer();

    // two ways of min or max z value:
    // min max values by filter
    if (settings.getModeMin().equals(ValueMode.PERCENTILE)) {
      // uses filter for min
      img.applyCutFilterMin(settings.getMinFilter());
      settings.setMin(img.getMinZFiltered());
    }
    if (settings.getModeMax().equals(ValueMode.PERCENTILE)) {
      // uses filter for min
      img.applyCutFilterMax(settings.getMaxFilter());
      settings.setMax(img.getMaxZFiltered());
    }
    // creation of scale
    // binary data scale? 1, 10, 11, 100, 101, 111, 1000, 1001
    PaintScale scale = null;
    scale = PaintScaleGenerator.generateStepPaintScale(zmin, zmax, settings);
    renderer.setPaintScale(scale);
    renderer.setBlockAnchor(RectangleAnchor.BOTTOM_LEFT);

    // create squared plot
    XYSquaredPlot plot = new XYSquaredPlot(dataset, xAxis, yAxis, renderer);

    // set background image
    setBGImage(img, renderer);

    // create chart
    JFreeSquaredChart chart = new JFreeSquaredChart("", plot);
    // remove lower legend
    chart.removeLegend();

    // Legend
    PaintScale scaleBar = PaintScaleGenerator.generateStepPaintScaleForLegend(zmin, zmax, settings);
    PaintScaleLegend legend =
        createScaleLegend(img, scaleBar, createScaleAxis(settings, setTheme), settings.getLevels());
    // adding legend in plot or outside
    if (psTheme.isPaintScaleInPlot()) { // inplot
      XYTitleAnnotation ta = new XYTitleAnnotation(1, 0.0, legend, RectangleAnchor.BOTTOM_RIGHT);
      ta.setMaxWidth(1);
      plot.addAnnotation(ta);
    } else
      chart.addSubtitle(legend);
    //
    chart.setBorderVisible(false);


    // ChartPanel
    EImage2DChartPanel chartPanel = new EImage2DChartPanel(chart, img);

    // add scale legend
    ScaleInPlot scaleInPlot = addScaleInPlot(setTheme, chartPanel);

    // add short title
    ImageTitle shortTitle = new ImageTitle(img, setTheme.getTheme().getFontShortTitle(),
        setTheme.getTheme().getcShortTitle(), setTheme.getTheme().getcBGShortTitle(),
        settImage.isShowShortTitle(), settImage.getXPosTitle(), settImage.getYPosTitle());
    plot.addAnnotation(shortTitle.getAnnotation());

    // theme
    img.getSettTheme().applyToChart(chart);
    chart.fireChartChanged();

    // Heatmap
    Heatmap heat = new Heatmap(dataset, settings.getLevels(), chartPanel, scale, chart, plot,
        legend, img, renderer, scaleInPlot, shortTitle);

    // return Heatmap
    return heat;
  }

  private static void setBGImage(DataCollectable2D img, AbstractXYItemRenderer renderer) {
    if (img.getImageGroup() != null) {
      Image bg = img.getImageGroup().getBGImage();
      if (bg != null) {
        // plot.setBackgroundImage(bg);
        XYImageAnnotation ann = new BGImageAnnotation(bg,
            img.getImageGroup().getSettings().getSettBGImg(), img.getWidth(), img.getHeight());

        renderer.addAnnotation(ann, Layer.BACKGROUND);
      }
    }
  }

  /**
   * standard axis
   * 
   * @param title
   * @return
   */
  private static NumberAxis createAxis(String title) {
    NumberAxis a = new NumberAxis(title);
    a.setStandardTickUnits(NumberAxis.createStandardTickUnits());
    a.setLowerMargin(0.0);
    a.setUpperMargin(0.0);
    return a;
  }

  // creates jfreechart plot for heatmap
  private static Heatmap createSPChart(SingleParticleImage img) throws Exception {
    return createSPChart(img, img.getPaintScaleSettings(),
        (SettingsGeneralCollecable2D) img.getSettingsByClass(SettingsGeneralCollecable2D.class),
        "x", "y");
  }

  // erstellt ein JFreeChart Plot der heatmap
  // bwidth und bheight (BlockWidth) sind die Maximalwerte
  private static Heatmap createSPChart(final SingleParticleImage img, SettingsPaintScale settings,
      SettingsGeneralCollecable2D settImage, String xTitle, String yTitle) throws Exception {

    DataCollectable2DDataset dataset = new DataCollectable2DDataset(img);
    dataset.applyPostProcessing();
    // absolute min max
    double zmin = dataset.getMinIntensity();
    double zmax = dataset.getMaxIntensity();
    if (zmax <= zmin)
      zmax = zmin + 1;

    if (settings == null) {
      settings = SettingsPaintScale.createStandardSettings();
      settings.setModeMax(ValueMode.RELATIVE);
      settings.setMax(100);
      settings.setModeMin(ValueMode.ABSOLUTE);
      settings.setMin(0);
    }

    // no data!
    SettingsThemesContainer setTheme = img.getSettTheme();
    SettingsPaintscaleTheme psTheme = setTheme.getSettPaintscaleTheme();
    // axes
    NumberAxis xAxis = createAxis(xTitle);
    NumberAxis yAxis = createAxis(yTitle);
    // XYBlockRenderer
    FullImageRenderer renderer = new FullImageRenderer();

    // creation of scale
    // binary data scale? 1, 10, 11, 100, 101, 111, 1000, 1001
    PaintScale scale = null;
    if (zmin != zmax)
      scale = PaintScaleGenerator.generateStepPaintScale(zmin, zmax, settings);
    else
      scale = new SingleColorPaintScale(zmin, zmin + 1, Color.MAGENTA);
    renderer.setPaintScale(scale);
    renderer.setAutoPopulateSeriesFillPaint(true);
    renderer.setBlockAnchor(RectangleAnchor.BOTTOM_LEFT);

    // Plot erstellen mit daten
    XYSquaredPlot plot = new XYSquaredPlot(dataset, xAxis, yAxis, renderer);
    plot.setBackgroundPaint(Color.lightGray);
    plot.setDomainGridlinesVisible(false);
    plot.setRangeGridlinePaint(Color.white);

    // set background image
    if (img.getImageGroup() != null) {
      Image bg = img.getImageGroup().getBGImage();
      if (bg != null) {
        // plot.setBackgroundImage(bg);
        XYImageAnnotation ann = new BGImageAnnotation(bg,
            img.getImageGroup().getSettings().getSettBGImg(), img.getWidth(), img.getHeight());

        renderer.addAnnotation(ann, Layer.BACKGROUND);
      }
    }

    // create chart
    JFreeSquaredChart chart = new JFreeSquaredChart("XYBlockChartDemo1", plot);
    // remove lower legend - wie farbskala rein? TODO
    chart.removeLegend();
    chart.setBackgroundPaint(Color.white);

    // Legend Generieren
    PaintScale scaleBar = null;
    if (zmin != zmax)
      scaleBar = PaintScaleGenerator.generateStepPaintScaleForLegend(zmin, zmax, settings);
    else
      scaleBar = new SingleColorPaintScale(zmin, zmin + 1, Color.MAGENTA);


    PaintScaleLegend legend = null;

    if (zmin != zmax) {
      legend = createScaleLegend(img, scaleBar, createScaleAxis(settings, setTheme),
          settings.getLevels());
      // adding legend in plot or outside
      if (psTheme.isPaintScaleInPlot()) { // inplot
        XYTitleAnnotation ta = new XYTitleAnnotation(1, 0.0, legend, RectangleAnchor.BOTTOM_RIGHT);
        ta.setMaxWidth(1);
        plot.addAnnotation(ta);
      } else
        chart.addSubtitle(legend);
    }
    //
    chart.setBorderVisible(true);


    // ChartPanel
    EImage2DChartPanel chartPanel = new EImage2DChartPanel(chart, img);

    // add scale legend
    ScaleInPlot scaleInPlot = addScaleInPlot(setTheme, chartPanel);

    // add short title
    ImageTitle shortTitle = new ImageTitle(img, setTheme.getTheme().getFontShortTitle(),
        setTheme.getTheme().getcShortTitle(), setTheme.getTheme().getcBGShortTitle(),
        settImage.isShowShortTitle(), settImage.getXPosTitle(), settImage.getYPosTitle());
    plot.addAnnotation(shortTitle.getAnnotation());

    // theme
    img.getSettTheme().applyToChart(chart);

    // ChartUtilities.applyCurrentTheme(chart);
    // defaultChartTheme.apply(chart);
    chart.fireChartChanged();

    chart.setBorderVisible(false);

    // Heatmap
    Heatmap heat = new Heatmap(dataset, settings.getLevels(), chartPanel, scale, chart, plot,
        legend, img, renderer, scaleInPlot, shortTitle);

    // return Heatmap
    return heat;
  }



  /**
   * add scale to plot 2 ways: fix position or like paintscale
   * 
   * @param img
   * @param setTheme
   * @param plot
   */
  private static ScaleInPlot addScaleInPlot(SettingsThemesContainer setTheme,
      ChartPanel chartPanel) {
    // XYTitleAnnotation ta = new XYTitleAnnotation(1, 0.0, legend,RectangleAnchor.BOTTOM_RIGHT);

    ScaleInPlot title = new ScaleInPlot(chartPanel, setTheme);
    // XYDrawableAnnotation ta2 = new XYDrawableAnnotation(1000, 1000, legend.getWidth(),
    // legend.getHeight(), legend);
    chartPanel.getChart().getXYPlot().addAnnotation(title.getAnnotation());

    title.setVisible(setTheme.getSettScaleInPlot().isShowScale());
    return title;
  }

  // erstellt XYZDataset aus xyz
  private static IXYZDataset createDataset(String title, double[][] dat) {
    IXYZDataset dataset = new IXYZDataset();
    dataset.addSeries(title, dat);
    return dataset;
  }

  /**
   * 
   * @param title
   * @param xvalues
   * @param yvalues
   * @param zvalues
   * @return
   */
  private static IXYZDataset createDatasetOverlay(String[] title, double[][][] dat) {
    IXYZDataset dataset = new IXYZDataset();
    // add one black layer / white layer
    // TODO for overlay

    // add all series
    for (int i = 0; i < dat.length; i++) {
      double[][] d = dat[i];
      dataset.addSeries(title[i], d);
    }
    // might need one dataset for each ?
    return dataset;
  }

  // Eine PaintScaleLegend generieren
  private static PaintScaleLegend createScaleLegend(PaintScale scale, NumberAxis scaleAxis,
      int stepCount) {
    return createScaleLegend(null, scale, scaleAxis, stepCount);
  }

  private static PaintScaleLegend createScaleLegend(Collectable2D img, PaintScale scale,
      NumberAxis scaleAxis, int stepCount) {
    SettingsThemesContainer setTheme = img != null ? img.getSettTheme() : null;
    // create legend
    PaintScaleLegend legend = new PaintScaleLegend(scale, scaleAxis);
    legend.setBackgroundPaint(new Color(0, 0, 0, 0));
    legend.setSubdivisionCount(stepCount);
    legend.setStripOutlineVisible(false);
    legend.setAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
    legend.setAxisOffset(0);
    legend.setStripWidth(10);

    RectangleInsets rec = new RectangleInsets(5, 5, 5, 5);

    if (setTheme != null) {
      SettingsPaintscaleTheme psTheme = setTheme.getSettPaintscaleTheme();
      rec = psTheme.isPaintScaleInPlot() ? RectangleInsets.ZERO_INSETS : psTheme.getPsMargin();

      legend.setStripWidth(psTheme.getPsWidth());
    }

    legend.setMargin(rec);
    // legend.setPadding(rec2);

    legend.setPosition(RectangleEdge.RIGHT);
    return legend;
  }

  // ScaleAxis
  private static NumberAxis createScaleAxis(SettingsPaintScale settings) {
    return createScaleAxis(settings, null);
  }

  private static NumberAxis createScaleAxis(SettingsPaintScale settings,
      SettingsThemesContainer theme) {
    NumberAxis scaleAxis =
        new NumberAxis(theme != null && theme.getSettPaintscaleTheme().isUsePaintScaleTitle()
            ? theme.getSettPaintscaleTheme().getPaintScaleTitle()
            : null);
    if (theme != null)
      scaleAxis
          .setNumberFormatOverride(theme.getSettPaintscaleTheme().getIntensitiesNumberFormat());
    scaleAxis.setLabelLocation(AxisLabelLocation.HIGH_END);
    scaleAxis.setLabelAngle(Math.toRadians(-180));
    return scaleAxis;
  }



  /**
   * creates a heatmap from raw data without any image2d or settings
   * 
   * @param dataset
   * @param xTitle
   * @param yTitle
   * @return
   * @throws Exception
   */
  private static Heatmap createRawChart(IXYZDataset dataset, String xTitle, String yTitle)
      throws Exception {
    SettingsPaintScale settings = new SettingsPaintScale();
    settings.setLODMonochrome(false);

    // this min max values in array
    double zmin = dataset.getZMin();
    double zmax = dataset.getZMax();
    // no data!
    if (zmin == zmax || zmax == 0) {
      throw new Exception("Every data point has the same intensity of " + zmin);
    } else {
      // axes
      NumberAxis xAxis = createAxis(xTitle);
      NumberAxis yAxis = createAxis(yTitle);
      // XYBlockRenderer
      ImageRenderer renderer = new ImageRenderer();

      // creation of scale
      // binary data scale? 1, 10, 11, 100, 101, 111, 1000, 1001
      PaintScale scale = null;
      scale = PaintScaleGenerator.generateStepPaintScale(zmin, zmax, settings);
      renderer.setPaintScale(scale);
      renderer.setAutoPopulateSeriesFillPaint(true);
      renderer.setBlockAnchor(RectangleAnchor.BOTTOM_LEFT);
      // TODO change to dynamic block width
      renderer.setBlockWidth(1);
      renderer.setBlockHeight(1);


      // Plot erstellen mit daten
      XYSquaredPlot plot = new XYSquaredPlot(dataset, xAxis, yAxis, renderer);
      plot.setBackgroundPaint(Color.lightGray);
      plot.setDomainGridlinesVisible(false);
      plot.setRangeGridlinePaint(Color.white);

      // create chart
      JFreeSquaredChart chart = new JFreeSquaredChart("XYBlockChartDemo1", plot);
      // remove lower legend - wie farbskala rein? TODO
      chart.removeLegend();
      chart.setBackgroundPaint(Color.white);

      // Legend Generieren
      PaintScale scaleBar =
          PaintScaleGenerator.generateStepPaintScaleForLegend(zmin, zmax, settings);
      PaintScaleLegend legend =
          createScaleLegend(scaleBar, createScaleAxis(settings), settings.getLevels());
      // adding legend
      chart.addSubtitle(legend);
      //
      chart.setBorderVisible(true);


      // ChartPanel
      EImage2DChartPanel chartPanel = new EImage2DChartPanel(chart, null);


      // ChartUtilities.applyCurrentTheme(chart);
      // defaultChartTheme.apply(chart);
      // chart.fireChartChanged();

      chart.setBorderVisible(false);

      // Heatmap
      Heatmap heat = new Heatmap(dataset, settings.getLevels(), chartPanel, scale, chart, plot,
          legend, null, renderer, null, null);

      // return Heatmap
      return heat;
    }
  }

  /*
   * //############################################################################### // Heatmap
   * EXAMPLES public Heatmap getHeatmapChartPanelExample() { double[] x =
   * {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15}; double[] y = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
   * double[] z = {1,2,3,1,2,3,1,2,3,1,2,3,1,2,3};
   * 
   * return createChart(createDataset("test", x, y, z)); } public Heatmap
   * getHeatmapChartPanelExample2() { double[] x = new double[400]; double[] y = new double[400];
   * double[] z = new double[400];
   * 
   * for(int i=0; i<20; i++) { for(int k=0; k<20; k++) { x[i*20+k] = i; y[i*20+k] = k; z[i*20+k] =
   * i+k; } }
   * 
   * return createChart(createDataset("test", x, y, z)); } public Heatmap
   * getHeatmapChartPanelExample3() { double[] x = new double[400]; double[] y = new double[400];
   * double[] z = new double[400];
   * 
   * for(int i=0; i<20; i++) { for(int k=0; k<20; k++) { x[i*20+k] = i*2.5; y[i*20+k] = k*2.1;
   * z[i*20+k] = i+k; } }
   * 
   * return createChart(createDataset("test", x, y, z)); }
   * 
   * public Heatmap getHeatmapChartPanelExampleDiscon() { double[] x = new double[400]; double[] y =
   * new double[400]; double[] z = new double[400];
   * 
   * Random rand = new Random(System.currentTimeMillis());
   * 
   * for(int i=0; i<20; i++) { for(int k=0; k<20; k++) { // 7-10 mm //neue zeile mit x=0 if(i==0)
   * x[i*20+k] = 0; else x[i*20+k] = x[i*20+k-20] + rand.nextInt(100)*0.03+7.0; // 25 mm y[i*20+k] =
   * k*25; z[i*20+k] = i+k; } }
   * 
   * return createChart(createDataset("test", x, y, z)); }
   */


}
