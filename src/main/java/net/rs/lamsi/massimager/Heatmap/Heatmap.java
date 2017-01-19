package net.rs.lamsi.massimager.Heatmap;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.Vector;

import net.rs.lamsi.massimager.Image.Image2D;
import net.rs.lamsi.massimager.MyFreeChart.Plot.image2d.ImageRenderer;
import net.rs.lamsi.multiimager.FrameModules.ModuleSelectExcludeData;
import net.rs.lamsi.multiimager.Frames.dialogs.selectdata.RectSelection;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYBoxAnnotation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.title.PaintScaleLegend;


public class Heatmap {
	// Variablen
	protected IXYZDataset dataset;
	// Title
	 
	// just a bunch of visual stuff
	private ChartPanel chartPanel;
	private PaintScale paintScale;
	private JFreeChart chart;
	private ImageRenderer renderer;
	private XYPlot plot;
	private PaintScaleLegend legend;
	// the raw data and settings
	private Image2D image;
	// stats
	private boolean isShowingSelectedExcludedRects = false, isShowingBlankMinMax = false;
	// list of annotations for later removing 
	private Vector<XYBoxAnnotation> selected, excluded;
	
	// blank domain marker for lower and upper bound
	private ValueMarker lowerMarker, upperMarker;
	
	// Construct
	public Heatmap(IXYZDataset dataset, int colorSteps, ChartPanel chartPanel,
			PaintScale paintScale, JFreeChart chart, XYPlot plot,
			PaintScaleLegend legend, Image2D image, ImageRenderer renderer) {
		super();
		this.dataset = dataset; 
		this.chartPanel = chartPanel;
		this.paintScale = paintScale;
		this.chart = chart;
		this.plot = plot;
		this.legend = legend;
		this.setImage(image);
		this.renderer = renderer;
		// 
		showBlankMinMax(image.getOperations().getBlankQuantifier().isShowInChart());
	}

	
	
	public IXYZDataset getDataset() {
		return dataset;
	}

	public void setDataset(IXYZDataset dataset) {
		this.dataset = dataset;
	} 

	public ChartPanel getChartPanel() {
		return chartPanel;
	}

	public void setChartPanel(ChartPanel chartPanel) {
		this.chartPanel = chartPanel;
	}

	public PaintScale getPaintScale() {
		return paintScale;
	}

	public void setPaintScale(PaintScale paintScale) {
		this.paintScale = paintScale;
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



	public void setImage(Image2D image) {
		this.image = image;
	} 
	public Image2D getImage() {
		return image;
	} 

	public ImageRenderer getRenderer() {
		return renderer;
	} 

	/**
	 * update the rects in the chartpanel
	 */
	public void updateSelectedExcludedRects() {
		// remove all annotations
		for(XYBoxAnnotation a : selected)
			plot.removeAnnotation(a, false);
		for(XYBoxAnnotation a : excluded)
			plot.removeAnnotation(a, false);
		selected.removeAllElements();
		excluded.removeAllElements();
		// add them
		if(isShowingSelectedExcludedRects) {
			for(RectSelection r : image.getSelectedData()) {  
				XYBoxAnnotation box = new XYBoxAnnotation(image.getXProcessed(r.getMinY(), r.getMinX()), image.getYProcessed(r.getMinY()), 
														image.getXProcessed(r.getMaxY()+1, r.getMaxX()+1), image.getYProcessed(r.getMaxY()+1),  new BasicStroke(1.5f), Color.BLACK);
				selected.add(box);
				plot.addAnnotation(box, false);
			}
			for(RectSelection r : image.getExcludedData()) {
				XYBoxAnnotation box = new XYBoxAnnotation(image.getXProcessed(r.getMinY(), r.getMinX()), image.getYProcessed(r.getMinY()), 
						image.getXProcessed(r.getMaxY()+1, r.getMaxX()+1), image.getYProcessed(r.getMaxY()+1),  new BasicStroke(1.5f), Color.RED);
				excluded.add(box);
				plot.addAnnotation(box, false);
			}
		}
		// fire change event
		chart.fireChartChanged();
	}
	/**
	 * Show the rects in the heatmap / chartpanel
	 * gets called by Action in {@link ModuleSelectExcludeData}
	 * @param show
	 */
	public void showSelectedExcludedRects(boolean show) {
		isShowingSelectedExcludedRects = show;
		// init
		if(selected == null) {
			selected = new Vector<XYBoxAnnotation>();
			excluded = new Vector<XYBoxAnnotation>();
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
		// remove
		if(upperMarker!=null) 
			plot.removeDomainMarker(upperMarker);
		if(lowerMarker!=null) 
			plot.removeDomainMarker(lowerMarker);
		
		if(isShowingBlankMinMax) {
			// add
			double lower = image.getXProcessed(0, image.getOperations().getBlankQuantifier().getQSameImage().getLowerBound());
			plot.addDomainMarker(lowerMarker = new ValueMarker(lower,Color.BLUE,new BasicStroke(1.5f)));
			
			if(image.getOperations().getBlankQuantifier().getQSameImage().isUseEnd()) {
				double upper = image.getXProcessed(0, image.getOperations().getBlankQuantifier().getQSameImage().getUpperBound());
				plot.addDomainMarker(upperMarker = new ValueMarker(upper,Color.BLUE,new BasicStroke(1.5f)));
			}
		}

		// fire change event
		chart.fireChartChanged();
	} 
}
