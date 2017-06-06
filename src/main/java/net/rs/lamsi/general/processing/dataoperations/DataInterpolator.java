package net.rs.lamsi.general.processing.dataoperations;

import java.util.ArrayList;

import net.rs.lamsi.general.datamodel.image.data.twodimensional.XYIDataMatrix;
import net.rs.lamsi.general.heatmap.interpolation.BicubicInterpolator;

public class DataInterpolator {

	/** 
	 * interpolates (f>1) / or reduces (f<0) the data by the factor f 
	 * reduction is only performed for data point sin a line
	 * interpolation is performed in both dimensions
	 * @param data
	 * @param f
	 * @return
	 */
	public static double[][] interpolateToArray(XYIDataMatrix data, double f) {

		Float[][] x = data.getX();
		Float[][] y = data.getY();
		Double[][] z = data.getI();
		// get data and interpolate/reduce
		ArrayList<Double> zz = new ArrayList<Double>();
		ArrayList<Double> xx = new ArrayList<Double>();
		ArrayList<Double> yy = new ArrayList<Double>();

		if(f<1) {
			// invert
			int red = (int)(1/f);
			double vz = 0;
			// reduce
			for(int l=0; l<z.length; l++) {
				int counter = 0;
				for(int d=0; d<z[l].length; d++) {
					if(!Double.isNaN(z[l][d])) {
						// 
						vz += z[l][d];
						if(counter%red==red-1) {
							// add
							zz.add(vz/red);
							xx.add((double)x[l][d-red+1]);
							yy.add((double)y[l][d]);

							vz = 0;
						}
						counter++;
					}
				}
				// reset
				vz = 0;
			}
		}
		else {
			// interpolate
			int interpolationFactor = (int)f;

			BicubicInterpolator bicubicInterpolator = new BicubicInterpolator();

			double minDataValue = data.getMinI();
			double maxDataValue = data.getMaxI();

			int correction = interpolationFactor - 1;

			for (int i = 0; i < z.length * interpolationFactor -correction; i++) {
				double idx = i * (1.0 / interpolationFactor);
				for (int j = 0; j < data.lineLength((int)idx) * interpolationFactor -correction; j++) {
					double jdy = j * (1.0 / interpolationFactor);
					
					double value = bicubicInterpolator.getValue(z, idx, jdy);
					
					if(!Double.isNaN(value)) {
						
						if (value < minDataValue) {
							value = minDataValue;
						} else if (value > maxDataValue) {
							value = maxDataValue;
						}

						zz.add(value);
						// use previous dp width for the last dp
						double xw = jdy+1<data.lineLength((int)idx)? 
								x[(int)idx][(int)jdy+1]-x[(int)idx][(int)jdy] : 
								x[(int)idx][(int)jdy]-x[(int)idx][(int)jdy-1];
								

						// use previous line height for the last line	
						double yh = idx+1<z.length? 
										y[(int)idx+1][(int)jdy]-y[(int)idx][(int)jdy] : 
										y[(int)idx][(int)jdy]-y[(int)idx-1][(int)jdy];
						xw /= interpolationFactor;
						yh /= interpolationFactor;
						xx.add(x[(int)idx][(int)jdy] + xw*(j%interpolationFactor));
						yy.add(y[(int)idx][(int)jdy] + yh*(i%interpolationFactor));
					}
				}
			}
		}
		// convert
		double[][] dat = new double[3][xx.size()];
		for(int i=0; i<xx.size(); i++) {
			dat[0][i] = xx.get(i);
			dat[1][i] = yy.get(i);
			dat[2][i] = zz.get(i);
		}
		return dat;
	}

}
