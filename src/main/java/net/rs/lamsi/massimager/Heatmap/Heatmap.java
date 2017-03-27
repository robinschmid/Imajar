package net.rs.lamsi.massimager.Heatmap;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.Vector;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.ImageOverlay;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.massimager.MyFreeChart.Plot.PlotChartPanel;
import net.rs.lamsi.massimager.MyFreeChart.Plot.image2d.ImageOverlayRenderer;
import net.rs.lamsi.massimager.MyFreeChart.Plot.image2d.ImageRenderer;
import net.rs.lamsi.massimager.MyFreeChart.Plot.image2d.PlotImage2DChartPanel;
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
	private Vector<XYBoxAnnotation> selected, excluded;
	
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
		showBlankMinMax(image.getOperations().getBlankQuantifier().isShowInChart());
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
			for(XYBoxAnnotation a : selected)
				plot.removeAnnotation(a, false);
			for(XYBoxAnnotation a : excluded)
				plot.removeAnnotation(a, false);
			selected.removeAllElements();
			excluded.removeAllElements();
			// add them
			if(isShowingSelectedExcludedRects) {
				for(RectSelection r : image.getSelectedData()) {  
					XYBoxAnnotation box = new XYBoxAnnotation(image.getX(false, r.getMinY(), r.getMinX()), image.getY(false, r.getMinY(), r.getMinX()), 
															image.getX(false, r.getMaxY()+1, r.getMaxX()+1), image.getY(false, r.getMaxY()+1,r.getMaxX()+1),  new BasicStroke(1.5f), Color.BLACK);
					selected.add(box);
					plot.addAnnotation(box, false);
				}
				for(RectSelection r : image.getExcludedData()) {
					XYBoxAnnotation box = new XYBoxAnnotation(image.getX(false, r.getMinY(), r.getMinX()), image.getY(false, r.getMinY(), r.getMinX()), 
							image.getX(false, r.getMaxY()+1, r.getMaxX()+1), image.getY(false, r.getMaxY()+1, r.getMaxX()+1),  new BasicStroke(1.5f), Color.RED);
					excluded.add(box);
					plot.addAnnotation(box, false);
				}
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
		if(isImage2D()) { // TODO
				Image2D image = (Image2D) this.image;
			// remove
			if(upperMarker!=null) 
				plot.removeDomainMarker(upperMarker);
			if(lowerMarker!=null) 
				plot.removeDomainMarker(lowerMarker);
			
			if(isShowingBlankMinMax) {
				// add
				double lower = image.getX(false, 0, image.getOperations().getBlankQuantifier().getQSameImage().getLowerBound());
				plot.addDomainMarker(lowerMarker = new ValueMarker(lower,Color.BLUE,new BasicStroke(1.5f)));
				
				if(image.getOperations().getBlankQuantifier().getQSameImage().isUseEnd()) {
					double upper = image.getX(false, 0, image.getOperations().getBlankQuantifier().getQSameImage().getUpperBound());
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
