package net.rs.lamsi.general.datamodel.image.interf;

import java.io.Serializable;
import java.util.List;

import net.rs.lamsi.general.datamodel.image.Image2D;

/**
 * multidimensional data set
 * @author r_schm33
 *
 */
public abstract class MDDataset extends ImageDataset  implements Serializable {	 
	// do not change the version!
	private static final long serialVersionUID = 1L;

	/**
	 * removes the dimension i from the data set
	 * @param i
	 * @return
	 */
	public abstract boolean removeDimension(int i);
	
	/**
	 * Adds a dimension to the data set 
	 * @param dim
	 * @return the index of the added dimension
	 */
	public abstract int addDimension(List<Double[]> dim);

	/**
	 * adds the image img as a new dimension
	 * sets this data set as the new data set of img
	 * the old is discarded
	 * @param img
	 * @return 
	 */
	public abstract boolean addDimension(Image2D img);


	/**
	 * true if x data is present
	 * (data point indices are often ignored as x data
	 * @return
	 */
	public abstract boolean hasXData();
	
	public abstract int size();
}
