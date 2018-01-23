package net.rs.lamsi.multiimager.FrameModules.sub.paintscale;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.myfreechart.plots.PlotChartPanel;
import net.rs.lamsi.general.settings.image.visualisation.SettingsPaintScale;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow.LOG;
import net.rs.lamsi.utils.threads.DelayedProgressUpdateTask;

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

	private Image2D lastImg;
	private SettingsPaintScale lastPS = null;
	private PlotChartPanel chart =null;
	
	private boolean isUptodate = false;
	private double lastMin = Double.NaN, lastMax = Double.NaN;
	
	// update in a delayed task to save computation time
	private DelayedProgressUpdateTask task = null;
	
	/**
	 * Create the panel.
	 */
	public PaintScaleHistogram() {
		this.setLayout(new BorderLayout());
	} 

	public Image2D getImg() {
		return lastImg;
	}
	/**
	 * you might watn to call updateHisto after setImg
	 * @param img
	 */
	public void setImg(Image2D img) {
		if(this.lastImg!=img) {
			isUptodate = false;
			// stop old task
			if(task!=null)
				task.stop();
		}
		this.lastImg = img;
	}

	public void updateHisto(SettingsPaintScale ps) {
		// stop old task if different
		if(ps!=lastPS) {
			isUptodate = false;
			if(task!=null)
				task.stop();
		}
		
		if(lastImg!=null) {
			if(task==null || ps!=lastPS) {
				lastPS = ps;
				startUpdateTask();
			}
		}
	}
	
	public void startUpdateTask() {
		ImageEditorWindow.log("HISTOGRAM task was started", LOG.DEBUG);
		// task gets started after 1 second delay (if not stopped before)
				task = new DelayedProgressUpdateTask(1, 1000) {
					@Override
					protected Boolean doInBackground2() throws Exception {
						Image2D img = lastImg;
						if(img!=null) {
							try {
								ImageEditorWindow.log("HISTOGRAM is updating now", LOG.DEBUG);
								SettingsPaintScale ps = lastPS;
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
											
											// add to panel
											removeAll();
											add(chart, BorderLayout.CENTER);
											revalidate();
											
											isUptodate = true;
											lastMin = min;
											lastMax = max;
										}
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
								return false;
							}
							return true;
						}
						return false;
					}
				};

				task.startDelayed();
	}
}
