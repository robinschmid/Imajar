package net.rs.lamsi.general.datamodel.image.interf;

import java.util.Vector;

import net.rs.lamsi.general.datamodel.image.Image2D;

/**
 * multidimensional data set
 * @author r_schm33
 *
 */
public interface MDDataset {

	/**
	 * removes the dimension i from the data set
	 * @param i
	 * @return
	 */
	public boolean removeDimension(int i);
	
	/**
	 * Adds a dimension to the data set 
	 * @param dim
	 * @return the index of the added dimension
	 */
	public int addDimension(Vector<Double[]> dim);

	/**
	 * adds the image img as a new dimension
	 * sets this data set as the new data set of img
	 * the old is discarded
	 * @param img
	 * @return 
	 */
	public boolean addDimension(Image2D img);


	/**
	 * true if x data is present
	 * (data point indices are often ignored as x data
	 * @return
	 */
	public abstract boolean hasXData();
	
	public abstract int size();
}
