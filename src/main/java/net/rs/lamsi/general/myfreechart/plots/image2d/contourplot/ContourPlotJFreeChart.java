package net.rs.lamsi.general.myfreechart.plots.image2d.contourplot;


import java.util.ArrayList;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.data.twodimensional.XYIDataMatrix;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.myfreechart.plots.image2d.EImage2DChartPanel;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.jfree.chart.JFreeChart;


public class ContourPlotJFreeChart extends EImage2DChartPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public ContourPlotJFreeChart(JFreeChart chart, Collectable2D img) {
		super(chart, img);
	}
	
	

	public static ContourPlotJFreeChart createChart(Image2D img, int interpolationFactor, double isoFactor) {
		XYIDataMatrix matrix = img.toXYIDataMatrix(false, true);
		Double[][] data = matrix.getI();
		

        double minDataValue = findMin(data);
        double maxDataValue = findMax(data);
        
        // interpolate data
        int correction = interpolationFactor - 1;
        double[][] interpolatedData = new double[data.length * interpolationFactor - correction][data[0].length * interpolationFactor - correction];
		BicubicInterpolator bicubicInterpolator = new BicubicInterpolator();
        for (int i = 0; i < data.length * interpolationFactor -correction; i++) {
            double idx = i * (1.0 / interpolationFactor);
            ArrayList<Double> row = new ArrayList<>();
            for (int j = 0; j < data[0].length * interpolationFactor -correction; j++) {
                double jdy = j * (1.0 / interpolationFactor);
                double value = bicubicInterpolator.getValue(data, idx, jdy);

                if (value < minDataValue) {
                    value = minDataValue;
                } else if (value > maxDataValue) {
                    value = maxDataValue;
                }
                
                interpolatedData[i][j] = value;
            }
        }
        
        // create paintscale
        
        
//        // create iso tiles
//        int isoCellsNumberX = interpolatedData[0].length - 1;
//        int isoCellsNumberY = interpolatedData.length - 1;
//
//        double isoCellSizeX = sizeX / isoCellsNumberX;
//        double isoCellSizeY = sizeY / isoCellsNumberY;
//
//        double isoCellPositionX = 0;
//        double isoCellPositionY = 0;
//
//        ArrayList<ArrayList<IsoCell>> matrixOfIsoCells = new ArrayList<>();
//
//        for (int i = 0; i < isoCellsNumberY; i++) {
//
//            ArrayList<IsoCell> oneRowOfIsoCells = new ArrayList<>();
//
//            for (int j = 0; j < isoCellsNumberX; j++) {
//
//                IsoCell isoCell = new IsoCell(isoCellSizeX, isoCellSizeY);
//
//                isoCell.setLayoutX(isoCellPositionX);
//                isoCell.setLayoutY(isoCellPositionY);
//
//                this.getChildren().add(isoCell);
//
//                oneRowOfIsoCells.add(isoCell);
//
//                isoCellPositionX += isoCellSizeX;
//            }
//            matrixOfIsoCells.add(oneRowOfIsoCells);
//
//            isoCellPositionX = 0;
//            isoCellPositionY += isoCellSizeY;
//        }
//
//        /**
//         * Based on: https://en.wikipedia.org/wiki/Marching_squares#Isoband.
//         * For each isoColor draw polygon in the specific isoCell if ternary index is different than 0 and 80.
//         */
//        for (int i = 0; i < colorScale.size(); i++) {
//
//            double startOfRange = arrayListOfIsoValues.get(i);
//            double endOfRange = arrayListOfIsoValues.get(i + 1);
//
//            for (int j = 0; j < matrixOfIsoCells.size(); j++) {
//
//                ArrayList<IsoCell> isoCellsRow = matrixOfIsoCells.get(j);
//
//                for (int k = 0; k < isoCellsRow.size(); k++) {
//
//                    ArrayList<Integer> ternaryNumber = new ArrayList<>();
//
//                    // Bottom left corner of the iso cell.
//                    double average = 0;
//
//                    double decibelThreshold = interpolatedData[j + 1][k];
//                    int ternarySingleValue = checkIfValueIsInRange(startOfRange, endOfRange, decibelThreshold);
//                    ternaryNumber.add(ternarySingleValue);
//                    average += decibelThreshold;
//
//                    // Bottom right corner of the iso cell.
//                    decibelThreshold = interpolatedData[j + 1][k + 1];
//                    ternarySingleValue = checkIfValueIsInRange(startOfRange, endOfRange, decibelThreshold);
//                    ternaryNumber.add(ternarySingleValue);
//                    average += decibelThreshold;
//
//                    // Top right corner of the iso cell.
//                    decibelThreshold = interpolatedData[j][k + 1];
//                    ternarySingleValue = checkIfValueIsInRange(startOfRange, endOfRange, decibelThreshold);
//                    ternaryNumber.add(ternarySingleValue);
//                    average += decibelThreshold;
//
//                    // Top left corner of the iso cell.
//                    decibelThreshold = interpolatedData[j][k];
//                    ternarySingleValue = checkIfValueIsInRange(startOfRange, endOfRange, decibelThreshold);
//                    ternaryNumber.add(ternarySingleValue);
//                    average += decibelThreshold;
//
//                    int ternaryIndex = ternaryToDecimalConverter(ternaryNumber);
//                    if (ternaryIndex != 0 && ternaryIndex != 80) {
//
//                        int[] saddleIndices = {10, 11, 19, 20, 23, 30, 33, 47, 50, 57, 60, 61, 69, 70};
//                        boolean contains = IntStream.of(saddleIndices).anyMatch(x -> x == ternaryIndex);
//                        if (contains) {
//                            average /= 4;
//                            int ternaryIndexOfAverageOfCorners = checkIfValueIsInRange(startOfRange, endOfRange, average);
//                            isoCellsRow.get(k).setTernaryIndexOfAverageOfCorners(ternaryIndexOfAverageOfCorners);
//                            isoCellsRow.get(k).drawIsoBand(ternaryIndex, colorScale.get(i));
//                        } else {
//                            isoCellsRow.get(k).drawIsoBand(ternaryIndex, colorScale.get(i));
//                        }
//                    }
//                }
//            }
//        }
		
        
        return null;
	}
	
	
	
	

    private static double findMin(Double[][] matrix) {
        double min = matrix[0][0];
        for (int row = 0; row < matrix.length; row++) {
            for (int column = 0; column < matrix[row].length; column++) {
                if (min > matrix[row][column]) {
                    min = matrix[row][column];
                }
            }
        }
        return min;
    }

    private static double findMax(Double[][] matrix) {
        double max = matrix[0][0];
        for (int row = 0; row < matrix.length; row++) {
            for (int column = 0; column < matrix[row].length; column++) {
                if (max < matrix[row][column]) {
                    max = matrix[row][column];
                }
            }
        }
        return max;
    }
}
