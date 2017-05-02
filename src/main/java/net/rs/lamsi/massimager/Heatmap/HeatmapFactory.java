package net.rs.lamsi.massimager.Heatmap;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.ImageOverlay;
import net.rs.lamsi.general.datamodel.image.data.twodimensional.XYIData2D;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.massimager.MyFreeChart.Plot.PlotChartPanel;
import net.rs.lamsi.massimager.MyFreeChart.Plot.image2d.ImageOverlayRenderer;
import net.rs.lamsi.massimager.MyFreeChart.Plot.image2d.ImageRenderer;
import net.rs.lamsi.massimager.MyFreeChart.Plot.image2d.PlotImage2DChartPanel;
import net.rs.lamsi.massimager.MyFreeChart.Plot.image2d.annot.BGImageAnnotation;
import net.rs.lamsi.massimager.Settings.image.SettingsImageOverlay;
import net.rs.lamsi.massimager.Settings.image.sub.SettingsGeneralImage;
import net.rs.lamsi.massimager.Settings.image.visualisation.SettingsBackgroundImg;
import net.rs.lamsi.massimager.Settings.image.visualisation.SettingsPaintScale;
import net.rs.lamsi.massimager.Settings.image.visualisation.SettingsPaintScale.ValueMode;
import net.rs.lamsi.massimager.Settings.image.visualisation.SettingsThemes;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYImageAnnotation;
import org.jfree.chart.annotations.XYTitleAnnotation;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;



public class HeatmapFactory {
	// Variablen 
	

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
		XYIData2D dat = image.toXYIArray(false, true); 
		// Heatmap erzeugen
		Heatmap h = createChart(image, setPaint, setImg, createDataset(image.getSettings().getSettImage().getTitle(), dat.getX(), dat.getY(), dat.getI()));
		// TODO WHY?
		
		image.getSettings().applyToHeatMap(h);
		return h;
	}
	
	private static Heatmap generateHeatmapFromImageOverlay(ImageOverlay image)  throws Exception { 
		// get rotated and reflected dataset
		XYIData2D[] dat = image.getDataSets();
		// Heatmap erzeugen
		Heatmap h = createChartOverlay(image, createDatasetOverlay(image.getTitles(), dat), "x", "y");
		// TODO WHY?
		
		image.getSettings().applyToHeatMap(h);
		return h;
	}
	/*
	// For Triggered Scan Data
	// A File = A Line
	private Heatmap generateHeatmapDiscontinous(SettingsPaintScale settings, String title, MZChromatogram[] mzchrom, SettingsImage setDisconImage) {
		// TODO mehrere MZ machen
		// daten
		double spotsize = setDisconImage.getSpotsize();
		double xvelocity = setDisconImage.getVelocity();
		// Alle Spec
		// Erst Messpunkteanzahl ausrechnen 
		int scanpoints = 0;
		for(int i=0; i<mzchrom.length; i++) {
			scanpoints += mzchrom[i].getItemCount();
		}
		// Datenerstellen
		double[] x = new double[scanpoints];
		double[] y = new double[scanpoints];
		double[] z = new double[scanpoints];
		
		// jede linie für sich durchgehen und abarbeiten
		MZChromatogram chrom;
		int overallindex = 0;
		for(int c=0; c<mzchrom.length; c++) { 
			chrom = mzchrom[c];
			// Alle Messpunkte vom chrom durchgehen
			for(int i=0; i<chrom.getItemCount(); i++) { 
				// Daten eintragen
				x[i+overallindex] = chrom.getX(i).doubleValue()*xvelocity;
				y[i+overallindex] = c*spotsize;
				z[i+overallindex] = chrom.getY(i).doubleValue();
			}
			overallindex += chrom.getItemCount();
		} 
		// Heatmap erzeugen
		return createChart(settings, setDisconImage, createDataset(title, x, y, z));
	}
	
	
	// Generate Heatmap with Continous Data WIDHTOUT Triggerin every Line
	private Heatmap generateHeatmapContinous(SettingsPaintScale settings, MZChromatogram mzchrom, SettingsMSImage setMSICon) {  
		//
		double timePerLine = setMSICon.getTimePerLine(); 
		double spotsize = setMSICon.getSpotsize();
		double xvelocity = setMSICon.getVelocity();
		// Größe des Images aus Zeiten ableiten
		// deltaTime aus Daten lesen = Zeit zwischen Messungen 
		double overallTime = (mzchrom.getMaxX()-mzchrom.getMinX());
		double deltaTime = overallTime/mzchrom.getItemCount();
		// ist nur abgeschätzt
		// Breite und Höhe fest definieren rundet bisher ab 
		// XYZ anzahl ist definiert durch messwerte im MZChrom 
		int scanpoints = mzchrom.getItemCount();
		double[] x = new double[scanpoints];
		double[] y = new double[scanpoints];
		double[] z = new double[scanpoints];
		// zeigt an wo man sich in der listData befindet
		int currenty = 0; 
		double lastTime = mzchrom.getMinX(); 
		double deltatime;
		// Alle MZChrom punkte durchgehen und in xyz eintragen
		// wenn Zeit größer als timePerLine dann y um eins vergrößern
		for(int i=0; i<mzchrom.getItemCount(); i++) {
			deltatime = mzchrom.getX(i).doubleValue()-lastTime;
			// nächste Zeile?
			if(deltatime>=timePerLine) {
				currenty++;
				// lastTime = mzchrom.getX(i).doubleValue(); 
				lastTime += timePerLine;
			}
			// Daten eintragen
			x[i] = mzchrom.getX(i).doubleValue()*xvelocity -lastTime*xvelocity;
			y[i] = currenty*spotsize;
			z[i] = mzchrom.getY(i).doubleValue();
		}
		
		// Vielleicht muss x und y umgedreht werden also dass erst jede Collumn durchgegangen wird
		// bisher wird jede Row durchgegangen

		return createChart(settings, setMSICon, createDataset("MSI", x, y, z));
	}
	*/

	
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
			
			// theme
			img.getSettTheme().applyToChart(chart);
			chart.fireChartChanged();
			 
			// Heatmap
			Heatmap heat = new Heatmap(dataset, chartPanel, scales, chart, plot, img, renderer, scaleInPlot);
			
			// return Heatmap
	        return heat;
    } 
	
	
	
	// Diese wird aufgerufen um Heatmap zu generieren.
	// test heatmap bei ColorPicker Dialog
	public static Heatmap generateHeatmap(SettingsPaintScale settings, SettingsGeneralImage settImage,  String title, double[] xvalues, double[] yvalues, double[] zvalues)  throws Exception  {
		// chartpanel der Heatmap hinzufügen 
		return createChart(null, settings, settImage, createDataset(title, xvalues, yvalues, zvalues));
	}
	

	// erstellt ein JFreeChart Plot der heatmap
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
			PaintScaleLegend legend = createScaleLegend(img, scaleBar, createScaleAxis(settings, dataset), settings.getLevels());   
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
			
			// theme
			img.getSettTheme().applyToChart(chart);
			
	 		//ChartUtilities.applyCurrentTheme(chart);
			//defaultChartTheme.apply(chart);
			chart.fireChartChanged();
			 
			// Heatmap
			Heatmap heat = new Heatmap(dataset, settings.getLevels(), chartPanel, scale, chart, plot, legend, img, renderer, scaleInPlot);
			
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
	private static IXYZDataset createDataset(String title, double[] xvalues, double[] yvalues, double[] zvalues) {  
        IXYZDataset dataset = new IXYZDataset();
        dataset.addSeries(title, new double[][] { xvalues, yvalues, zvalues });
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
	private static IXYZDataset createDatasetOverlay(String[] title, XYIData2D[] dat) {  
        IXYZDataset dataset = new IXYZDataset();
        // add one black layer / white layer
        // TODO for overlay
        
        // add all series
        for(int i=0; i<dat.length; i++) {
        	XYIData2D d  = dat[i];
        	dataset.addSeries(title[i], new double[][] { d.getX(), d.getY(), d.getI() });
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
		legend.setAxisLocation(AxisLocation.TOP_OR_RIGHT);
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
	private static NumberAxis createScaleAxis(SettingsPaintScale settings, IXYZDataset dataset) {
		NumberAxis scaleAxis = new NumberAxis(null); 
//		scaleAxis.setLabel(null);
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
