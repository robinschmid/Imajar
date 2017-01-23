package net.rs.lamsi.massimager.Settings.image.operations.quantifier;

import net.rs.lamsi.general.datamodel.image.Image2D;

public interface Image2DQuantifyStrategyImpl {
	
	/**
	 * calcs the processed intensity of raw data z
	 * @param img
	 * @param line
	 * @param dp 
	 * @param intensity the intensity of this element
	 * @return
	 */
	public double calcIntensity(Image2D img, int line, int dp, double intensity);
}
