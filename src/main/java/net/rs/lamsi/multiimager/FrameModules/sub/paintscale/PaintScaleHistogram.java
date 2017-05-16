package net.rs.lamsi.multiimager.FrameModules.sub.paintscale;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.myfreechart.Plot.PlotChartPanel;
import net.rs.lamsi.general.settings.image.visualisation.SettingsPaintScale;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow.LOG;

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
	private PlotChartPanel chart =null;
	
	private boolean isUptodate = false;
	private double lastMin = Double.NaN, lastMax = Double.NaN;
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
		if(this.img!=img)
			isUptodate = false;
		this.img = img;
	}

	public void updateHisto(SettingsPaintScale ps) {
		if(img!=null) {
			try {
				double min = ps.getMinIAbs(img);
				double max = ps.getMaxIAbs(img);
				if(min<max) {
					if(min!=lastMin || max!=lastMax)
						isUptodate = false;
					
					if(!isUptodate) {
						double[] dat1 = img.toIArray(false);
						double[] dat2 = img.getIInIRange(ps);
						if(dat2.length>2) {
							int bins2 = (int) Math.sqrt(dat2.length)+40;
							double binwidth2 = (max-min)/bins2;
							int bins1 = (int) ((img.getMaxIntensity(ps.isUsesMinMaxFromSelection())-img.getMinIntensity(ps.isUsesMinMaxFromSelection()))/binwidth2);
							if(bins1>100000) bins1 = 100000;
							
							ImageEditorWindow.log("bi1:"+bins1+ "   Bins2:"+bins2, LOG.DEBUG);
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
							chart = new PlotChartPanel(c);
							this.removeAll();
							this.add(chart, BorderLayout.CENTER);
							this.validate();
							isUptodate = true;
							lastMin = min;
							lastMax = max;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
