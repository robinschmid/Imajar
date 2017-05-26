package net.rs.lamsi.general.heatmap;
import java.awt.Color;
import java.awt.Image;
import java.util.Vector;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.ImageOverlay;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.myfreechart.Plot.PlotChartPanel;
import net.rs.lamsi.general.myfreechart.Plot.image2d.ImageOverlayRenderer;
import net.rs.lamsi.general.myfreechart.Plot.image2d.ImageRenderer;
import net.rs.lamsi.general.myfreechart.Plot.image2d.PlotImage2DChartPanel;
import net.rs.lamsi.general.myfreechart.Plot.image2d.annot.BGImageAnnotation;
import net.rs.lamsi.general.myfreechart.Plot.image2d.annot.ImageTitle;
import net.rs.lamsi.general.myfreechart.Plot.image2d.annot.ScaleInPlot;
import net.rs.lamsi.general.settings.image.SettingsImageOverlay;
import net.rs.lamsi.general.settings.image.sub.SettingsGeneralImage;
import net.rs.lamsi.general.settings.image.visualisation.SettingsBackgroundImg;
import net.rs.lamsi.general.settings.image.visualisation.SettingsPaintScale;
import net.rs.lamsi.general.settings.image.visualisation.SettingsPaintScale.ValueMode;
import net.rs.lamsi.general.settings.image.visualisation.SettingsThemes;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYImageAnnotation;
import org.jfree.chart.annotations.XYTitleAnnotation;
import org.jfree.chart.axis.AxisLabelLocation;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;



public class HeatmapFactory {
	/**
	 * generate heatmap from data
	 * @param settings
	 * @param settImage
	 * @param title
	 * @param xvalues
	 * @param yvalues
	 * @param zvalues
	 * @return
	 * @throws Exception
	 */
	public static Heatmap generateHeatmap(Image2D img,  String title, double[][] data)  throws Exception  {
		return createChart(img, createDataset(title, data));
	}
	public static Heatmap generateHeatmap(Image2D img,  String title, double[] xvalues, double[] yvalues, double[] zvalues)  throws Exception  {
		return generateHeatmap(img, title, new double[][]{xvalues, yvalues, zvalues});
	}

	// Image2D to Heatmap Image
	public static Heatmap generateHeatmap(Collectable2D image)  throws Exception { 
		if(Image2D.class.isInstance(image)) {
			return generateHeatmapFromImage2D((Image2D)image); 
		}
		else if(ImageOverlay.class.isInstance(image))
			return generateHeatmapFromImageOverlay((ImageOverlay)image);
		else
			return null;
	}
	
	private static Heatmap generateHeatmapFromImage2D(Image2D image)  throws Exception { 
		SettingsPaintScale setPaint = image.getSettings().getSettPaintScale();
		SettingsGeneralImage setImg = image.getSettings().getSettImage();
		// get rotated and reflected dataset
		double[][] dat = image.toXYIArray(false, true); 
		// Heatmap erzeugen
		Heatmap h = createChart(image, setPaint, setImg, createDataset(image.getSettings().getSettImage().getTitle(), dat));
		// TODO WHY?
		
		image.getSettings().applyToHeatMap(h);
		return h;
	}
	
	private static Heatmap generateHeatmapFromImageOverlay(ImageOverlay image)  throws Exception { 
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
	private static Heatmap createChartOverlay(final ImageOverlay img, IXYZDataset dataset, String xTitle, String yTitle) throws Exception {
        SettingsImageOverlay settings = img.getSettings();
		int seriesCount = dataset.getSeriesCount();
	    	SettingsThemes setTheme = img.getSettTheme();
	    	
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
	        ImageOverlayRenderer renderer = new ImageOverlayRenderer(seriesCount, settings.getBlend());
	        
	        Vector<PaintScale> psList = new Vector<PaintScale>();
	        // create one paintscale for each active image
	        int counter = 0;
	        for(int i=0; i<img.size(); i++) {
	        	if(settings.isActive(i)) {
	        		Image2D cimg = img.get(i);
	        		SettingsPaintScale ps = settings.getSettPaintScale(i);
			        // PaintScale für farbe? TODO mit Settings!
			        // TODO upper and lower value setzen!!!!
	                double zmin = dataset.getZMin(counter);
	                double zmax = dataset.getZMax(counter);
			        //two ways of min or max z value: 
			        // min max values by filter
			        if(ps.getModeMin().equals(ValueMode.PERCENTILE)) {
			        	// uses filter for min
			        	double nmin = cimg.getValueCutFilter(ps.getMinFilter(), ps.isUsesMinMaxFromSelection());
			        	ps.setMin(nmin);
			        }
			        if(ps.getModeMax().equals(ValueMode.PERCENTILE)) {
			        	// uses filter for min
			        	double nmax = cimg.getValueCutFilter(100.0-ps.getMaxFilter(), ps.isUsesMinMaxFromSelection());
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
			        renderer.setBlockWidth(counter, cimg.getMaxBlockWidth(cimg.getSettings().getSettImage().getRotationOfData())); 
			        renderer.setBlockHeight(counter, cimg.getMaxBlockHeight(cimg.getSettings().getSettImage().getRotationOfData())); 
			        

			        renderer.setSeriesPaint(counter, ps.getMinColor());

			        counter++;
	        	}
	        }
	        // creation of scale
	        // binary data scale? 1, 10, 11, 100, 101, 111, 1000, 1001
	        PaintScale[] scales = psList.toArray(new PaintScale[psList.size()]);
	        
	        // set all paint scales
	        renderer.setPaintScales(scales);
	        renderer.setAutoPopulateSeriesFillPaint(true);
	        renderer.setBlockAnchor(RectangleAnchor.BOTTOM_LEFT);
	        
	        // Plot erstellen mit daten
	        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
	        plot.setBackgroundPaint(Color.BLACK);
	        plot.setDomainGridlinesVisible(false);
	        plot.setRangeGridlinePaint(Color.white);
	        
	        // set background image
	        if(img.getImageGroup()!=null) {
	        	Image bg = img.getImageGroup().getBGImage();
	        	SettingsBackgroundImg settBG = img.getImageGroup().getSettings().getSettBGImg();
	        	if(bg!=null) {
	        		//plot.setBackgroundImage(bg);
	        		XYImageAnnotation ann = new BGImageAnnotation(bg, settBG, img.getWidth(false), img.getHeight(false));
	        		
	        		renderer.addAnnotation(ann, Layer.BACKGROUND);
	        	}
	        }
	        
	        // create chart
	        JFreeChart chart = new JFreeChart("XYBlockChartDemo1", plot);
	        // remove lower legend - wie farbskala rein? TODO
//	        chart.removeLegend();
	        chart.setBackgroundPaint(Color.white);
	        
			//
			chart.setBorderVisible(true); 
			
			// ChartPanel
			PlotChartPanel chartPanel = new PlotChartPanel(chart); 
			
			// add scale legend
			ScaleInPlot	scaleInPlot = addScaleInPlot(setTheme, chartPanel);

			// add short title
			ImageTitle shortTitle = new ImageTitle(img, setTheme.getTheme().getFontShortTitle(), setTheme.getTheme().getcShortTitle(), setTheme.getTheme().getcBGShortTitle(), false, 0.9f, 0.9f);
			plot.addAnnotation(shortTitle.getAnnotation());
			
			// theme
			img.getSettTheme().applyToChart(chart);
			chart.fireChartChanged();
			
			chart.setBorderVisible(false);
			 
			// Heatmap
			Heatmap heat = new Heatmap(dataset, chartPanel, scales, chart, plot, img, renderer, scaleInPlot, shortTitle);
			
			// return Heatmap
	        return heat;
    } 

	// creates jfreechart plot for heatmap
	private static Heatmap createChart(Image2D img, IXYZDataset dataset)  throws Exception  {
    	return createChart(img, (SettingsPaintScale)img.getSettingsByClass(SettingsPaintScale.class),
    			(SettingsGeneralImage)img.getSettingsByClass(SettingsGeneralImage.class), dataset, "x", "y");
    }
	private static Heatmap createChart(Image2D img, SettingsPaintScale settings, SettingsGeneralImage settImage, IXYZDataset dataset)  throws Exception  {
    	return createChart(img, settings, settImage, dataset, "x", "y");
    }

	// erstellt ein JFreeChart Plot der heatmap
	// bwidth und bheight (BlockWidth) sind die Maximalwerte
	private static Heatmap createChart(final Image2D img, SettingsPaintScale settings, SettingsGeneralImage settImage, IXYZDataset dataset, String xTitle, String yTitle) throws Exception {
        // this min max values in array
        double zmin = dataset.getZMin();
        double zmax = dataset.getZMax();
        // no data!
        if(zmin == zmax || zmax == 0) {
        	throw new Exception("Every data point has the same intensity of "+zmin);
        }
        else { 
	    	SettingsThemes setTheme = img.getSettTheme();
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
	        ImageRenderer renderer = new ImageRenderer();
	        
	        // PaintScale für farbe? TODO mit Settings!
	        // TODO upper and lower value setzen!!!!
	        //two ways of min or max z value: 
	        // min max values by filter
	        if(settings.getModeMin().equals(ValueMode.PERCENTILE)) {
	        	// uses filter for min
	        	img.applyCutFilterMin(settings.getMinFilter());
	        	settings.setMin(img.getMinZFiltered());
	        }
	        if(settings.getModeMax().equals(ValueMode.PERCENTILE)) {
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
	        // TODO nicht feste Blockwidth!
	        // erstmal feste BlockWidth 
	        renderer.setBlockWidth(img.getMaxBlockWidth(settImage.getRotationOfData())); 
	        renderer.setBlockHeight(img.getMaxBlockHeight(settImage.getRotationOfData())); 
	        // Plot erstellen mit daten
	        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
	        plot.setBackgroundPaint(Color.lightGray);
	        plot.setDomainGridlinesVisible(false);
	        plot.setRangeGridlinePaint(Color.white);
	        
	        // set background image
	        if(img.getImageGroup()!=null) {
	        	Image bg = img.getImageGroup().getBGImage();
	        	if(bg!=null) {
	        		//plot.setBackgroundImage(bg);
	        		XYImageAnnotation ann = new BGImageAnnotation(bg, img.getImageGroup().getSettings().getSettBGImg(), img.getWidth(false), img.getHeight(false));
	        		
	        		renderer.addAnnotation(ann, Layer.BACKGROUND);
	        	}
	        }
	        
	        // create chart
	        JFreeChart chart = new JFreeChart("XYBlockChartDemo1", plot);
	        // remove lower legend - wie farbskala rein? TODO
	        chart.removeLegend();
	        chart.setBackgroundPaint(Color.white);
	        
	        // Legend Generieren
	        PaintScale scaleBar = PaintScaleGenerator.generateStepPaintScaleForLegend(zmin, zmax, settings); 
			PaintScaleLegend legend = createScaleLegend(img, scaleBar, createScaleAxis(settings, setTheme, dataset), settings.getLevels());   
			// adding legend in plot or outside
			if(setTheme.getTheme().isPaintScaleInPlot()) { // inplot
				XYTitleAnnotation ta = new XYTitleAnnotation(1, 0.0, legend,RectangleAnchor.BOTTOM_RIGHT);  
				ta.setMaxWidth(1);
				plot.addAnnotation(ta);
			}
			else chart.addSubtitle(legend);
			//
			chart.setBorderVisible(true); 
	
			
			// ChartPanel
			PlotImage2DChartPanel chartPanel = new PlotImage2DChartPanel(chart, img); 
			
			// add scale legend
			ScaleInPlot	scaleInPlot = addScaleInPlot(setTheme, chartPanel);
			
			// add short title
			ImageTitle shortTitle = new ImageTitle(img, setTheme.getTheme().getFontShortTitle(), setTheme.getTheme().getcShortTitle(), setTheme.getTheme().getcBGShortTitle(), settImage.isShowShortTitle(), settImage.getXPosTitle(), settImage.getYPosTitle());
			plot.addAnnotation(shortTitle.getAnnotation());
			
			// theme
			img.getSettTheme().applyToChart(chart);
			
	 		//ChartUtilities.applyCurrentTheme(chart);
			//defaultChartTheme.apply(chart);
			chart.fireChartChanged();
			
			chart.setBorderVisible(false);
			 
			// Heatmap
			Heatmap heat = new Heatmap(dataset, settings.getLevels(), chartPanel, scale, chart, plot, legend, img, renderer, scaleInPlot, shortTitle);
			
			// return Heatmap
	        return heat;
        }
    } 
	
	/**
	 * add scale to plot
	 * 2 ways: fix position or like paintscale
	 * @param img
	 * @param setTheme
	 * @param plot
	 */
	private static ScaleInPlot addScaleInPlot(SettingsThemes setTheme,  ChartPanel chartPanel) {
		// XYTitleAnnotation ta = new XYTitleAnnotation(1, 0.0, legend,RectangleAnchor.BOTTOM_RIGHT);  
		
		ScaleInPlot title = new ScaleInPlot(chartPanel, setTheme);
		//XYDrawableAnnotation ta2 = new XYDrawableAnnotation(1000, 1000, legend.getWidth(), legend.getHeight(), legend);
		chartPanel.getChart().getXYPlot().addAnnotation(title.getAnnotation());
		title.setVisible(setTheme.getTheme().isShowScale());
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
        for(int i=0; i<dat.length; i++) {
        	double[][] d  = dat[i];
        	dataset.addSeries(title[i], d);
        }
        	// might need one dataset for each ?
        return dataset;
    } 
	 
	// Eine PaintScaleLegend generieren
	private static PaintScaleLegend createScaleLegend(Image2D img, PaintScale scale, NumberAxis scaleAxis, int stepCount) { 
		SettingsThemes setTheme = img.getSettTheme();
		// create legend
		PaintScaleLegend legend = new PaintScaleLegend(scale, scaleAxis);
		legend.setBackgroundPaint(new Color(0,0,0,0));
		legend.setSubdivisionCount(stepCount);
		legend.setStripOutlineVisible(false);
		legend.setAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
		legend.setAxisOffset(0);
		RectangleInsets rec = setTheme.getTheme().isPaintScaleInPlot()? RectangleInsets.ZERO_INSETS : new RectangleInsets(5, 0, 10, 5);
		legend.setMargin(rec);
		RectangleInsets rec2 = setTheme.getTheme().isPaintScaleInPlot()? RectangleInsets.ZERO_INSETS : new RectangleInsets(4, 0, 22, 2);
		legend.setPadding(rec2);
		legend.setStripWidth(10);
		legend.setPosition(RectangleEdge.RIGHT); 
		return legend;
	}	
	
	// ScaleAxis
	private static NumberAxis createScaleAxis(SettingsPaintScale settings, SettingsThemes theme, IXYZDataset dataset) {
		NumberAxis scaleAxis = new NumberAxis(theme.getTheme().isUsePaintScaleTitle()? theme.getTheme().getPaintScaleTitle() : null);
		scaleAxis.setNumberFormatOverride(theme.getTheme().getIntensitiesNumberFormat());
		scaleAxis.setLabelLocation(AxisLabelLocation.HIGH_END);
		scaleAxis.setLabelAngle(Math.toRadians(-180));
		return scaleAxis;
	}
	
/*
	//###############################################################################
	// Heatmap EXAMPLES
	public Heatmap getHeatmapChartPanelExample() {
		double[] x = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
		double[] y = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
		double[] z = {1,2,3,1,2,3,1,2,3,1,2,3,1,2,3};
		
		return createChart(createDataset("test", x, y, z));
	}
	public Heatmap getHeatmapChartPanelExample2() {
		double[] x = new double[400];
		double[] y = new double[400];
		double[] z = new double[400];
		
		for(int i=0; i<20; i++) {
			for(int k=0; k<20; k++) {
				x[i*20+k] = i;
				y[i*20+k] = k;
				z[i*20+k] =  i+k; 
			}
		}
		
		return createChart(createDataset("test", x, y, z));
	}
	public Heatmap getHeatmapChartPanelExample3() {
		double[] x = new double[400];
		double[] y = new double[400];
		double[] z = new double[400];
		
		for(int i=0; i<20; i++) {
			for(int k=0; k<20; k++) {
				x[i*20+k] = i*2.5;
				y[i*20+k] = k*2.1;
				z[i*20+k] =  i+k; 
			}
		}
		
		return createChart(createDataset("test", x, y, z));
	}

	public Heatmap getHeatmapChartPanelExampleDiscon() {
		double[] x = new double[400];
		double[] y = new double[400];
		double[] z = new double[400];
		
		Random rand = new Random(System.currentTimeMillis());
		
		for(int i=0; i<20; i++) {
			for(int k=0; k<20; k++) {
				// 7-10 mm  
				//neue zeile mit x=0
				if(i==0) x[i*20+k] = 0;
				else x[i*20+k] = x[i*20+k-20] + rand.nextInt(100)*0.03+7.0;
				// 25 mm
				y[i*20+k] = k*25;
				z[i*20+k] =  i+k; 
			}
		}
		
		return createChart(createDataset("test", x, y, z));
	}
*/


}
