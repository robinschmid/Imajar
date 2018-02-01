package net.rs.lamsi.general.myfreechart;

import java.awt.Color;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;

public class EChartFactory {

  public static JFreeChart createHistogram(double[] data, int bin) {
    if (data != null && data.length > 0) {
      HistogramDataset dataset = new HistogramDataset();
      dataset.addSeries("histo", data, bin);

      JFreeChart chart = ChartFactory.createHistogram("", null, null, dataset,
          PlotOrientation.VERTICAL, true, false, false);

      chart.setBackgroundPaint(new Color(230, 230, 230));
      chart.getLegend().setVisible(false);
      XYPlot xyplot = chart.getXYPlot();
      xyplot.setForegroundAlpha(0.7F);
      xyplot.setBackgroundPaint(Color.WHITE);
      xyplot.setDomainGridlinePaint(new Color(150, 150, 150));
      xyplot.setRangeGridlinePaint(new Color(150, 150, 150));
      xyplot.getDomainAxis().setVisible(true);
      xyplot.getRangeAxis().setVisible(false);
      XYBarRenderer xybarrenderer = (XYBarRenderer) xyplot.getRenderer();
      xybarrenderer.setShadowVisible(false);
      xybarrenderer.setBarPainter(new StandardXYBarPainter());
      // xybarrenderer.setDrawBarOutline(false);
      return chart;
    } else
      return null;
  }

  public static JFreeChart createHistogram(double[] data) {
    int bin = (int) Math.sqrt(data.length);
    return createHistogram(data, bin);
  }


}
