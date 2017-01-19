package net.rs.lamsi.multiimager.Frames.dialogs.selectdata;

public class DataMinMaxAvg {
	
	private double min, max, avg, stdev;

	public DataMinMaxAvg(double min, double max, double avg, double stdev) {
		super();
		this.min = min;
		this.max = max;
		this.avg = avg;
		this.stdev = stdev;
	}

	public double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public double getAvg() {
		return avg;
	}

	public void setAvg(double avg) {
		this.avg = avg;
	}

	public double getStdev() {
		return stdev;
	}

	public void setStdev(double stdev) {
		this.stdev = stdev;
	}
	
	
}
