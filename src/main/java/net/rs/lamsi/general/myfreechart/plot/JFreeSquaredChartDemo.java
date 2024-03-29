package net.rs.lamsi.general.myfreechart.plot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import net.rs.lamsi.general.myfreechart.ChartLogics;
import net.rs.lamsi.general.myfreechart.plot.XYSquaredPlot.Scale;
import net.rs.lamsi.general.myfreechart.swing.EChartPanel;

public class JFreeSquaredChartDemo {
	/** Creates a new instance of SquaredXYPlotDemo */
    public JFreeSquaredChartDemo() {
        XYDataset dataset = createDataset();
        JPanel chartPanel = createChartPanel(dataset);
        JFrame frame = new JFrame("Squared XY Plot Demo");
        frame.setSize(1000, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel border = new JPanel(new BorderLayout());
        FlowLayout l = new FlowLayout();
        l.setHgap(0);
        l.setVgap(0);
        
        
        JPanel flow = new JPanel(l);
        

        BoxLayout b = new BoxLayout(flow, BoxLayout.Y_AXIS);
        
        
        border.add(chartPanel, BorderLayout.CENTER);
        
        frame.getContentPane().add(border);
        //Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //frame.setLocation((screenSize.width - frame.getWidth()) / 2,
        //        (screenSize.height - frame.getHeight()) / 2);
        frame.setVisible(true);
    }
    
    private XYDataset createDataset(){
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series = new XYSeries("Series 1");
        for (int i = 0; i < 25; i++) {
            series.add(i, i/5);
        }
        dataset.addSeries(series);
        return dataset;
    }
    
    private JPanel createChartPanel(XYDataset dataset){
        //uncomment to use logarithmicAxis
        //LogarithmicAxis rangeAx = new LogarithmicAxis("Crater Density per Million KM");
        //LogarithmicAxis domainAx = new LogarithmicAxis("Crater Diameter in KM");
        
        //regular number axis
        NumberAxis rangeAx = new NumberAxis("Title on Range");
        NumberAxis domainAx = new NumberAxis("Title on Domain");
        
        
        //set range of axes, the domainAx range will be overwritten later
        rangeAx.setRange(new Range(0.0, 25.0));
        domainAx.setRange(new Range(0.0, 25.0));
        
        //create an ErrorbarRenderer
        StandardXYItemRenderer r = new StandardXYItemRenderer();
        //don't connect the dots
        r.setPlotLines(false);
        r.setPlotImages(true);
        //show the points
        r.setBaseShapesVisible(true);
        
        //create a SquaredXYPlot with above data
        XYSquaredPlot squarePlot = new XYSquaredPlot(dataset, domainAx, rangeAx, r, Scale.DYNAMIC);
        squarePlot.setOrientation(PlotOrientation.VERTICAL);
        
        //define x-axis, and square y-axis to it

        //connect plot and renderer
        r.setPlot(squarePlot);
        r.addChangeListener(squarePlot);
        
        //create the actual chart
        JFreeSquaredChart chart = new JFreeSquaredChart("Squared XY Plot Demo", squarePlot);
        
        // set the background color for the chart...
        chart.setBackgroundPaint(Color.white);
        
        //create a chartPanel to hold the chart
        EChartPanel chartPanel = new EChartPanel(chart);
        ChartLogics.makeChartResizable(chartPanel);
        chartPanel.setMouseZoomable(true, false);
        return chartPanel;
    }
    
    public static void main(String[] args) {
        new JFreeSquaredChartDemo();
    }
}
