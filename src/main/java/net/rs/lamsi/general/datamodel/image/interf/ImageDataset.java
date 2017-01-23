package net.rs.lamsi.general.datamodel.image.interf;

import net.rs.lamsi.general.datamodel.image.data.ScanLine;

/**
 * Basic methods of a imaging data set
 * @author Robin Schmid
 *
 */
public interface ImageDataset {
	
	/**
	 * counts the lines in a data set
	 * @return
	 */
	public int getLinesCount();
	
	/**
	 * scan points (length) of line i
	 * @param i
	 * @return
	 */
	public int getLineLength(int i);
	
	/**
	 * returns the raw x value (time/data point number/...)
	 * @param line index of the line
	 * @param dpi index of the data point
	 * @return
	 */
	public float getX(int line, int dpi);
	
	/**
	 * returns the raw intensity (I) value of a data point in a line
	 * @param line index of the line
	 * @param dpi index of the data point
	 * @return
	 */
	public double getI(int line, int dpi);

	/**
	 * raw distance between two data points (dTime or delta data point number)
	 * usually only interesting for delta Time
	 * @return maximum x distance between two data points in a line
	 */
	public float getMaxXWidth();
	
	/**
	 * 
	 * @return total count of data points
	 */
	public int getTotalDPCount();
	
	/**
	 * The maximum datapoints of the longest line
	 * @return
	 */
	public int getMaxDP();

	/**
	 * The maximum datapoints of the longest line
	 * @return
	 */
	public int getMinDP();

	/**
	 * The average data points of all lines 
	 * @return
	 */
	public int getAvgDP();
	
}
