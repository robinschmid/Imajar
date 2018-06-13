package net.rs.lamsi.multiimager.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Random;
import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Year;
import org.jfree.data.xy.XYDataset;

/** @see http://stackoverflow.com/questions/5522575 */
public class ChartPanelDemo {

  private static final String title = "Return On Investment";
  private ChartPanel chartPanel = createChart();

  public ChartPanelDemo() {
    JFrame f = new JFrame(title);
    f.setTitle(title);
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setLayout(new BorderLayout(0, 5));
    f.add(chartPanel, BorderLayout.CENTER);
    chartPanel.setMouseWheelEnabled(true);
    chartPanel.setHorizontalAxisTrace(true);
    chartPanel.setVerticalAxisTrace(true);

    JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panel.add(createTrace());
    panel.add(createDate());
    panel.add(createZoom());

    JButton btn = new JButton("Change series paints");
    btn.addActionListener(e -> changeSeriesPaints());
    panel.add(btn);

    f.add(panel, BorderLayout.SOUTH);
    f.pack();
    f.setLocationRelativeTo(null);
    f.setVisible(true);
  }

  private void changeSeriesPaints() {
    Random r = new Random(System.nanoTime());
    for (int i = 0; i < 2; i++) {
      Color c = new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255));
      chartPanel.getChart().getXYPlot().getRenderer().setSeriesPaint(i, c);
    }
    // chartPanel.getChart().fireChartChanged();
  }

  private JComboBox createTrace() {
    final JComboBox trace = new JComboBox();
    final String[] traceCmds = {"Enable Trace", "Disable Trace"};
    trace.setModel(new DefaultComboBoxModel(traceCmds));
    trace.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        if (traceCmds[0].equals(trace.getSelectedItem())) {
          chartPanel.setHorizontalAxisTrace(true);
          chartPanel.setVerticalAxisTrace(true);
          chartPanel.repaint();
        } else {
          chartPanel.setHorizontalAxisTrace(false);
          chartPanel.setVerticalAxisTrace(false);
          chartPanel.repaint();
        }
      }
    });
    return trace;
  }

  private JComboBox createDate() {
    final JComboBox date = new JComboBox();
    final String[] dateCmds = {"Horizontal Dates", "Vertical Dates"};
    date.setModel(new DefaultComboBoxModel(dateCmds));
    date.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        JFreeChart chart = chartPanel.getChart();
        XYPlot plot = (XYPlot) chart.getPlot();
        DateAxis domain = (DateAxis) plot.getDomainAxis();
        if (dateCmds[0].equals(date.getSelectedItem())) {
          domain.setVerticalTickLabels(false);
        } else {
          domain.setVerticalTickLabels(true);
        }
      }
    });
    return date;
  }

  private JButton createZoom() {
    final JButton auto = new JButton(new AbstractAction("Auto Zoom") {

      @Override
      public void actionPerformed(ActionEvent e) {
        chartPanel.restoreAutoBounds();
      }
    });
    return auto;
  }

  private ChartPanel createChart() {
    XYDataset roiData = createDataset();
    JFreeChart chart =
        ChartFactory.createTimeSeriesChart(title, "Date", "Value", roiData, true, true, false);
    XYPlot plot = chart.getXYPlot();
    XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
    renderer.setDefaultShapesVisible(true);
    NumberFormat currency = NumberFormat.getCurrencyInstance();
    currency.setMaximumFractionDigits(0);
    NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
    rangeAxis.setNumberFormatOverride(currency);
    return new ChartPanel(chart);
  }

  private XYDataset createDataset() {
    TimeSeriesCollection tsc = new TimeSeriesCollection();
    tsc.addSeries(createSeries("Projected", 200));
    tsc.addSeries(createSeries("Actual", 100));
    return tsc;
  }

  private TimeSeries createSeries(String name, double scale) {
    TimeSeries series = new TimeSeries(name);
    for (int i = 0; i < 6; i++) {
      series.add(new Year(2005 + i), Math.pow(2, i) * scale);
    }
    return series;
  }

  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {

      @Override
      public void run() {
        ChartPanelDemo cpd = new ChartPanelDemo();
      }
    });
  }
}
