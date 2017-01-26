package net.rs.lamsi.general.datamodel.image.interf;

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
	public int addDimension(Double[] dim);
	
}
