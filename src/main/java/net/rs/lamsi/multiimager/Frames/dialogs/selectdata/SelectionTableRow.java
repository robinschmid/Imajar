package net.rs.lamsi.multiimager.Frames.dialogs.selectdata;

import java.awt.Shape;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.settings.image.selection.SettingsShapeSelection.SelectionMode;

import org.jfree.chart.ChartPanel;

public class SelectionTableRow implements Serializable{
	// do not change the version!
	private static final long serialVersionUID = 1L;

	private SelectionMode mode;
	private transient ChartPanel histo;

	// only used for stats calculation 
	// use finalise to free this list
	private ArrayList<Double> data = null;
	private Shape shape;

	// statistics
	private double max, min, median, p99, avg, sdev, sum;
	private int n;



	public SelectionTableRow(SelectionMode mode, Shape shape) {
		super();
		this.mode = mode; 
		this.shape = shape;
	}


	// statistics calculation
	public void addValue(double i) {
		// init?
		if(data==null) {
			data = new ArrayList<Double>();
			max = Double.NEGATIVE_INFINITY;
			min = Double.POSITIVE_INFINITY;
			sum = 0;
		}
		data.add(i);
		if(i<min) min = i;
		if(i>max) max = i;
		// first sum
		sum += i;
	}
	/**
	 * final stats calculation after all data points were added via check
	 */
	public void calculateStatistics(Image2D img) {
		if(img==null || data==null || data.size()==0)
			return;
		// create histo
		// copy to double array
		double[] array = new double[data.size()];
		for(int i=0; i<data.size(); i++) {
			array[i] = data.get(i);
		}
		histo = img.createHistogram(array);

		// for percentiles and median
		Collections.sort(data);

		median = data.get(Math.max(0, data.size()/2-1));
		p99 = data.get(Math.max(0, (int)Math.round(data.size()*0.99-1)));

		n = data.size();
		// average and sdev
		avg = sum / (double)n;
		// stdev 
		sdev = 0;

		for(double d : data) {
			sdev += Math.pow(d-avg, 2);
		}
		// calc stdev
		sdev = Math.sqrt(sdev/(double)(data.size()-1));

		
		// erase data
		data = null;
	}


	/**
	 * called for table
	 * @return
	 */
	public Object[] getRowData() {
		float y0 = getY0();
		float x0 =getX0();
		float y1 =getY1();
		float x1 = getX1();

		return new Object[]{mode.toString(), x0,y0,x1,y1,n,sum,min, max, avg, median, p99, sdev, histo};
	}

	/**
	 * called for data export 
	 * @return without histogram
	 */
	public Object[] getRowDataExport() {
		float y0 = getY0();
		float x0 =getX0();
		float y1 =getY1();
		float x1 = getX1();
		
		return new Object[]{mode.toString(), x0,y0,x1,y1,n,sum,min, max, avg, median, p99, sdev};	
	}


	/**
	 * array for title line export
	 * without histo
	 */
	public static Object[] getTitleArrayExport() {
		return new Object[]{"Mode", "x0", "y0", "x1", "y1", "n", "sum", "I min", "I max", "I avg", "I median", "I 99%","Stdev"};
	}

	public ChartPanel getHisto() {
		return histo;
	}

	public void setHisto(ChartPanel histo) {
		this.histo = histo;
	}


	public SelectionMode getMode() {
		return mode;
	}


	public Shape getShape() {
		return shape;
	}


	public double getMax() {
		return max;
	}


	public double getMin() {
		return min;
	}


	public double getMedian() {
		return median;
	}


	public double getP99() {
		return p99;
	}


	public double getAvg() {
		return avg;
	}


	public double getSdev() {
		return sdev;
	}
	public int getN() {
		return n;
	}
	public double getSum() {
		return sum;
	}


	public float getX0() {
		return (float)shape.getBounds2D().getMinX();
	} 
	public float getX1() {
		return (float)shape.getBounds2D().getMaxX();
	} 
	public float getY0() {
		return (float)shape.getBounds2D().getMinY();
	} 
	public float getY1() {
		return (float)shape.getBounds2D().getMaxY();
	}


	public void setMode(SelectionMode mode) {
		this.mode = mode;
	}


	public void setShape(Shape shape) {
		this.shape = shape;
	} 
}
