package net.rs.lamsi.multiimager.FrameModules.paintscale;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;

import net.rs.lamsi.general.datamodel.image.Image2D;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedRangeXYPlot;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;

public class PaintScaleHistogram extends JPanel {

	private Image2D img;
	private ChartPanel chart =null;
	/**
	 * Create the panel.
	 */
	public PaintScaleHistogram() {
		this.setLayout(new BorderLayout());
	} 
	
	public Image2D getImg() {
		return img;
	}
	public void setImg(Image2D img) {
		this.img = img;
		updateHisto();
	}
	
	public void updateHisto() {
		if(img!=null) {
		try {
		double min = img.getSettPaintScale().getMin();
		double max = img.getSettPaintScale().getMax();
		
		double[] dat2 = img.getIInIRange();
		int bin = (int) Math.sqrt(dat2.length);
		
		ChartPanel pn1 = img.createHistogram(img.toIArray(), bin);
		XYPlot plot1 = pn1.getChart().getXYPlot();
		XYPlot plot2 = img.createHistogram(dat2, bin).getChart().getXYPlot();

		Marker minM = new ValueMarker(min);
		minM.setPaint(Color.RED);
		minM.setLabel("min");
		minM.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
		minM.setLabelTextAnchor(TextAnchor.TOP_LEFT);
		plot1.addDomainMarker(minM);
		
		Marker maxM = new ValueMarker(max);
		maxM.setPaint(Color.RED);
		maxM.setLabel("max");
		maxM.setLabelAnchor(RectangleAnchor.TOP_LEFT);
		maxM.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
		plot1.addDomainMarker(maxM);
		
		ValueAxis axis = plot1.getRangeAxis();
		
		CombinedRangeXYPlot combined = new CombinedRangeXYPlot(axis);
		combined.add(plot1);
		combined.add(plot2); 
		
		JFreeChart c = new JFreeChart(combined);
		c.getLegend().setVisible(false);
		chart = new ChartPanel(c);
		this.removeAll();
		this.add(chart, BorderLayout.CENTER);
		this.validate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	}
}
