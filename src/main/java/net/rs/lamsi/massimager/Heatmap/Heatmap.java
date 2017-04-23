package net.rs.lamsi.massimager.Heatmap;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.ImageOverlay;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.massimager.MyFreeChart.Plot.PlotChartPanel;
import net.rs.lamsi.massimager.MyFreeChart.Plot.image2d.ImageRenderer;
import net.rs.lamsi.massimager.MyFreeChart.Plot.image2d.PlotImage2DChartPanel;
import net.rs.lamsi.massimager.Settings.image.selection.SettingsShapeSelection;
import net.rs.lamsi.multiimager.FrameModules.sub.ModuleSelectExcludeData;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYShapeAnnotation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.title.PaintScaleLegend;


public class Heatmap {
	// Variablen
	protected IXYZDataset dataset;
	// Title
	 
	// just a bunch of visual stuff
	private PlotChartPanel chartPanel;
	private PaintScale[] paintScale;
	private JFreeChart chart;
	private ImageRenderer renderer;
	private XYPlot plot;
	private PaintScaleLegend legend;
	// the raw data and settings
	private Collectable2D image;
	private ScaleInPlot scaleInPlot;
	// stats
	private boolean isShowingSelectedExcludedRects = false, isShowingBlankMinMax = false;
	// list of annotations for later removing 
	private ArrayList<XYShapeAnnotation> annSelections;
	
	// blank domain marker for lower and upper bound
	private ValueMarker lowerMarker, upperMarker;
	
	// Construct
	public Heatmap(IXYZDataset dataset, int colorSteps, PlotImage2DChartPanel chartPanel,
			PaintScale paintScale, JFreeChart chart, XYPlot plot,
			PaintScaleLegend legend, Image2D image, ImageRenderer renderer, ScaleInPlot scaleInPlot) {
		super();
		this.dataset = dataset; 
		this.chartPanel = chartPanel;
		this.paintScale = new PaintScale[]{paintScale};
		this.chart = chart;
		this.plot = plot;
		this.legend = legend;
		this.setImage(image);
		this.renderer = renderer;
		this.scaleInPlot = scaleInPlot;
		// 
		showBlankMinMax(image.getSettings().getOperations().getBlankQuantifier().isShowInChart());
	}
	
	public Heatmap(IXYZDataset dataset, PlotChartPanel chartPanel,
			PaintScale[] paintScale, JFreeChart chart, XYPlot plot,
			//PaintScaleLegend legend, 
			ImageOverlay image, ImageRenderer renderer, ScaleInPlot scaleInPlot) {
		super();
		this.dataset = dataset; 
		this.chartPanel = chartPanel;
		this.paintScale = paintScale;
		this.chart = chart;
		this.plot = plot;
		// this.legend = legend;
		this.setImage(image);
		this.renderer = renderer;
		this.scaleInPlot = scaleInPlot;
	}

	public IXYZDataset getDataset() {
		return dataset;
	}

	public void setDataset(IXYZDataset dataset) {
		this.dataset = dataset;
	} 

	public PlotChartPanel getChartPanel() {
		return chartPanel;
	}

	public void setChartPanel(PlotImage2DChartPanel chartPanel) {
		this.chartPanel = chartPanel;
	}

	public PaintScale getPaintScale(int i) {
		return paintScale[i];
	}
	public PaintScale[] getPaintScales() {
		return paintScale;
	}

	public void setPaintScales(PaintScale[] paintScale) {
		this.paintScale = paintScale;
	}
	public void setPaintScales(PaintScale paintScale, int i) {
		this.paintScale[i] = paintScale;
	}

	public JFreeChart getChart() {
		return chart;
	}

	public void setChart(JFreeChart chart) {
		this.chart = chart;
	}

	public XYPlot getPlot() {
		return plot;
	}

	public void setPlot(XYPlot plot) {
		this.plot = plot;
	}

	public PaintScaleLegend getLegend() {
		return legend;
	}

	public void setLegend(PaintScaleLegend legend) {
		this.legend = legend;
	}



	public void setImage(Collectable2D image) {
		this.image = image;
	} 
	public Collectable2D getImage() {
		return image;
	} 
	public boolean isImage2D() {
		return Image2D.class.isInstance(image);
	}

	public ImageRenderer getRenderer() {
		return renderer;
	} 

	/**
	 * update the rects in the chartpanel
	 */
	public void updateSelectedExcludedRects() {
		if(isImage2D()) { // TODO
			Image2D image = (Image2D) this.image;
			// remove all annotations
			for(XYShapeAnnotation a : annSelections)
				plot.removeAnnotation(a, false);
			// add them
			if(isShowingSelectedExcludedRects) {
				annSelections.clear();
				
				ArrayList<SettingsShapeSelection> selections = image.getSettings().getSettSelections().getSelections();
				if(selections!=null) {
					for (Iterator iterator = selections.iterator(); iterator.hasNext();) {
						SettingsShapeSelection sel = (SettingsShapeSelection) iterator.next();
						XYShapeAnnotation ann = sel.createXYShapeAnnotation();
						annSelections.add(ann);
						plot.addAnnotation(ann, false);
					}
				}
			}
			else {
				annSelections = null;
			}
			// fire change event
			chart.fireChartChanged();
		}
	}
	/**
	 * Show the rects in the heatmap / chartpanel
	 * gets called by Action in {@link ModuleSelectExcludeData}
	 * @param show
	 */
	public void showSelectedExcludedRects(boolean show) {
		isShowingSelectedExcludedRects = show;
		// init
		if(annSelections == null) {
			annSelections = new ArrayList<XYShapeAnnotation>();
		}
		// update
		updateSelectedExcludedRects();
	}


	public void showBlankMinMax(boolean state) {
		isShowingBlankMinMax = state; 
		// update
		updateShowBlankMinMax();
	}
	
	public void updateShowBlankMinMax() {
		if(isImage2D()) { // TODO
				Image2D image = (Image2D) this.image;
			// remove
			if(upperMarker!=null) 
				plot.removeDomainMarker(upperMarker);
			if(lowerMarker!=null) 
				plot.removeDomainMarker(lowerMarker);
			
			if(isShowingBlankMinMax) {
				// add
				double lower = image.getX(false, 0, image.getSettings().getOperations().getBlankQuantifier().getQSameImage().getLowerBound());
				plot.addDomainMarker(lowerMarker = new ValueMarker(lower,Color.BLUE,new BasicStroke(1.5f)));
				
				if(image.getSettings().getOperations().getBlankQuantifier().getQSameImage().isUseEnd()) {
					double upper = image.getX(false, 0, image.getSettings().getOperations().getBlankQuantifier().getQSameImage().getUpperBound());
					plot.addDomainMarker(upperMarker = new ValueMarker(upper,Color.BLUE,new BasicStroke(1.5f)));
				}
			}
	
			// fire change event
			chart.fireChartChanged();
		}
	}



	public ScaleInPlot getScaleInPlot() {
		return scaleInPlot;
	}



	public void setScaleInPlot(ScaleInPlot scaleInPlot) {
		setScaleInPlot(scaleInPlot);
	} 
}
