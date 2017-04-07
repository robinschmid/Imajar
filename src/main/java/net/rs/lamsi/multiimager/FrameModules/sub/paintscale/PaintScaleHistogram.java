package net.rs.lamsi.multiimager.FrameModules.sub.paintscale;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.massimager.Settings.image.visualisation.SettingsPaintScale;

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
	}
	
	public void updateHisto(SettingsPaintScale ps) {
		if(img!=null) {
		try {
		double min = ps.getMinIAbs(img);
		double max = ps.getMaxIAbs(img);
		
		double[] dat1 = img.toIArray(false);
		double[] dat2 = img.getIInIRange();
		int bins2 = (int) Math.sqrt(dat2.length)+40;
		double binwidth2 = (max-min)/bins2;
		int bins1 = (int) ((img.getMaxIntensity(ps.isUsesMinMaxFromSelection())-img.getMinIntensity(ps.isUsesMinMaxFromSelection()))/binwidth2);
		System.out.println("bi1:"+bins1+ "   Bins2:"+bins2);
		ChartPanel pn1 = img.createHistogram(dat1, bins1);
		XYPlot plot1 = pn1.getChart().getXYPlot();
		XYPlot plot2 = img.createHistogram(dat2, bins2).getChart().getXYPlot();

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
