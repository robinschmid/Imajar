package net.rs.lamsi.multiimager.Frames.dialogs.selectdata;

import java.awt.Color;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.multiimager.Frames.dialogs.selectdata.Image2DSelectDataAreaDialog.MODE;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;

public class SelectionTableRow {
	
	private ChartPanel histo;
	private Image2D img;
	private RectSelection rect; 
	
	public SelectionTableRow(Image2D img, RectSelection rect) {
		super();
		this.img = img;
		this.rect = rect; 
		//
		double[] data = img.getIProcessedRect(rect);
		histo = img.createHistogram(data);
	}

	/**
	 * called for table
	 * @return
	 */
	public Object[] getRowData() {
		String y0 = String.valueOf(rect.getY())+"; "+String.valueOf(img.getYProcessed(rect.getY()));
		String x0 = String.valueOf(rect.getX())+"; "+String.valueOf(img.getXProcessed(rect.getY(), rect.getX()));
		String y1 =String.valueOf(rect.getMaxY())+"; "+String.valueOf(img.getYProcessed(rect.getMaxY()));
		String x1 = String.valueOf(rect.getMaxX())+"; "+String.valueOf(img.getXProcessed(rect.getMaxY(), rect.getMaxX()));
		
		DataMinMaxAvg data = img.analyzeDataInRect(rect);
		double max99 = img.analyzePercentile(rect, 0.99);
		double median = img.analyzePercentile(rect, 0.5);
		
		return new Object[]{rect.getMode().toString(), x0,y0,x1,y1,data.getMin(),data.getMax(), data.getAvg(), median, max99, data.getStdev(), histo};
	}
	
	/**
	 * called for data export
	 * @return
	 */
	public Object[] getRowDataExport() {
		String y0 = String.valueOf(rect.getY())+"; "+String.valueOf(img.getYProcessed(rect.getY()));
		String x0 = String.valueOf(rect.getX())+"; "+String.valueOf(img.getXProcessed(rect.getY(), rect.getX()));
		String y1 =String.valueOf(rect.getMaxY())+"; "+String.valueOf(img.getYProcessed(rect.getMaxY()));
		String x1 = String.valueOf(rect.getMaxX())+"; "+String.valueOf(img.getXProcessed(rect.getMaxY(), rect.getMaxX()));
		
		DataMinMaxAvg data = img.analyzeDataInRect(rect);
		double max99 = img.analyzePercentile(rect, 0.99);
		double median = img.analyzePercentile(rect, 0.5);
		
		return new Object[]{rect.getMode().toString(), x0,y0,x1,y1,data.getMin(),data.getMax(), data.getAvg(), median, max99, data.getStdev()};
	}

	
	/**
	 * array for title line export
	 * without histo
	 */
	public static Object[] getTitleArrayExport() {
		return new Object[]{"Mode", "x0", "y0", "x1", "y1", "I min", "I max", "I avg", "I median", "I 99%","Stdev"};
	}
	
	public Image2D getImg() {
		return img;
	}
	public void setImg(Image2D img) {
		this.img = img;
	}
	public RectSelection getRect() {
		return rect;
	}
	public void setRect(RectSelection rect) {
		this.rect = rect;
	}

	public ChartPanel getHisto() {
		return histo;
	}

	public void setHisto(ChartPanel histo) {
		this.histo = histo;
	}
	
}
