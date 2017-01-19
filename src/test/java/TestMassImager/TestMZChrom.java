package TestMassImager;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;

import net.rs.lamsi.massimager.MyFreeChart.ChartLogics;
import net.rs.lamsi.massimager.MyMZ.MZChromatogram;
import net.sf.mzmine.util.Range;

import org.jfree.chart.ChartPanel;

public class TestMZChrom {

	private JFrame frame;
	private JPanel pnChartView;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TestMZChrom window = new TestMZChrom();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public TestMZChrom() {
		initialize();
		// 
		createMZChrom();
	}

	private void createMZChrom() {
		MZChromatogram chrom = new MZChromatogram("");
		for(int i=0; i<100; i++) {
			double x = i;
			double y = x*x-100;
			
			chrom.add(x, y);
		}
		//
		ChartPanel chart = chrom.getChromChartPanel("Test", "x", "y");
		getPnChartView().add(chart, BorderLayout.CENTER); 
		chart.validate();
		
		ChartLogics.setZoomDomainAxis(chart, new Range(10, 30), true); 
		
		chart.getPopupMenu();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		pnChartView = new JPanel();
		frame.getContentPane().add(pnChartView, BorderLayout.CENTER);
		pnChartView.setLayout(new BorderLayout(0, 0));
	}

	public JPanel getPnChartView() {
		return pnChartView;
	}
}
