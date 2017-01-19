package net.rs.lamsi.utils.useful.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

import net.rs.lamsi.massimager.MyFreeChart.themes.ChartThemeFactory;
import net.rs.lamsi.massimager.MyFreeChart.themes.MyStandardChartTheme;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.xy.XYErrorRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class DialogLinearRegression extends JFrame {

	private JPanel contentPane;
	private JPanel pnChartView;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SimpleRegression regression = new SimpleRegression(true);
					Random rand = new Random(System.currentTimeMillis());
					double[][] data = new double[30][2];
					for(int i=0; i<30; i+=3) {
						data[i][0] = i+1; 
						data[i][1] = i*10+rand.nextInt(10000)/1000.0;
						data[i+1][0] = i+1; 
						data[i+1][1] = i*10+rand.nextInt(10000)/1000.0;
						data[i+2][0] = i+1; 
						data[i+2][1] = i*10+rand.nextInt(10000)/1000.0;
					}
					regression.addData(data);
					DialogLinearRegression frame = new DialogLinearRegression(regression, data);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public DialogLinearRegression(SimpleRegression r, double[][] data) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 603, 470);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		pnChartView = new JPanel();
		contentPane.add(pnChartView, BorderLayout.CENTER);
		pnChartView.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.SOUTH);
		
		JTextPane txtpnStats = new JTextPane();
		txtpnStats.setText("Stats:\n"+ 
						"intercept = "+r.getIntercept()+ " +- "+r.getInterceptStdErr() +"\n"
								+ "slope = "+r.getSlope()+" +- "+ r.getSlopeStdErr() +"  conf: "+r.getSlopeConfidenceInterval()+"\n"
										+ "R2 = "+ r.getRSquare());
		scrollPane.setViewportView(txtpnStats);
		
		// create chart

		// add data points
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series = new XYSeries(""); 
		for(int i=0; i<data.length; i++) {
			series.add(data[i][0], data[i][1]);
		}  
		dataset.addSeries(series);
		// add regression line
		series = new XYSeries("linear"); 
		double x0 = (0-r.getIntercept())/r.getSlope();
		if(x0>0) 
			series.add(0, r.predict(0));
		else series.add(x0, 0);
		series.add(data[data.length-1][0], r.predict(data[data.length-1][0]));
		dataset.addSeries(series);
		
		JFreeChart chart = ChartFactory.createXYLineChart("", "c", "I", dataset);
        chart.getLegend().setVisible(false);
        MyStandardChartTheme theme = ChartThemeFactory.createBlackNWhiteTheme(); 
        theme.apply(chart); 
        
		XYErrorRenderer renderer = new XYErrorRenderer(); 
		chart.getXYPlot().setRenderer(renderer);

        renderer.setSeriesPaint(0, Color.BLACK);
        renderer.setSeriesPaint(1, Color.BLACK);
		renderer.setSeriesShapesVisible(0, true);
		renderer.setSeriesLinesVisible(1, true);
		renderer.setSeriesShapesVisible(1, false);
		renderer.setDrawYError(true);
		renderer.setDrawXError(false);
		
		ChartPanel pn = new ChartPanel(chart, true);
		getPnChartView().add(pn, BorderLayout.CENTER);
	}

	public JPanel getPnChartView() {
		return pnChartView;
	}
}
