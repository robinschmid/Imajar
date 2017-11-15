package net.rs.lamsi.massimager.MyMZ.preprocessing.filtering.peaklist.chargecalculation;

import java.awt.geom.Point2D;
import java.util.Vector;
import java.util.logging.Logger;

import net.rs.lamsi.general.settings.SettingsChargeCalculator;
import net.sf.mzmine.parameters.parametertypes.tolerances.MZTolerance;
import net.sf.mzmine.parameters.parametertypes.tolerances.RTTolerance;

public class MZChargeCalculatorDouble {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * The isotopeDistance constant defines expected distance between isotopes.
	 * Actual weight of 1 neutron is 1.008665 Da, but part of this mass is
	 * consumed as binding energy to other protons/neutrons. Actual mass
	 * increase of isotopes depends on chemical formula of the molecule. Since
	 * we don't know the formula, we can assume the distance to be ~1.0033 Da,
	 * with user-defined tolerance.
	 */
	private static final double isotopeDistance = 1.0033;

	private Vector<Point2D> peakList;
	private Point2D aPeak;

	// parameter values 
	private MZTolerance mzTolerance;
	private RTTolerance rtTolerance;
	private boolean monotonicShape;
	private int maximumCharge; 

	/**
	 * @param rawDataFile
	 * @param parameters
	 */
	public MZChargeCalculatorDouble(Vector<Point2D> peakList, SettingsChargeCalculator parameters, Point2D peakMZ) {

		this.peakList = peakList; 
		this.aPeak = peakMZ;

		// Get parameter values for easier use 
		mzTolerance = new MZTolerance(parameters.getMzTolerance().getMzTolerance()*3, parameters.getMzTolerance().getPpmTolerance()*2);
		rtTolerance = parameters.getRtTolerance();
		monotonicShape = parameters.isMonotonicShape();
		maximumCharge = parameters.getMaximumCharge(); 
	}

	public int doFiltering() {
		System.out.println("Do Charge Calc"); 

		// Collect all selected charge states
		int charges[] = new int[maximumCharge];
		for (int i = 0; i < maximumCharge; i++)
			charges[i] = i + 1; 

		// Check which charge state fits best around this peak
		int bestFitCharge = 0;
		int bestFitScore = -1;
		Vector<Point2D> bestFitPeaks = null;
		for (int charge : charges) {

			Vector<Point2D> fittedPeaks = new Vector<Point2D>();
			fittedPeaks.add(aPeak);
			fitPattern(fittedPeaks, aPeak, charge, peakList);

			int score = fittedPeaks.size();
			if ((score > bestFitScore)
					|| ((score == bestFitScore) && (bestFitCharge > charge))) {
				bestFitScore = score;
				bestFitCharge = charge;
				bestFitPeaks = fittedPeaks;
			}

		} 
		
		int saveCharge = bestFitCharge;
		if(bestFitScore<3) 
			bestFitCharge = 0;
		// setting the charge 
		System.out.println("Charge = "+bestFitCharge+ "    bestFItScore = "+bestFitScore+ "   #  saveCharge = "+saveCharge);
		return bestFitCharge;
}

/**
 * Fits isotope pattern around one peak.
 * 
 * @param p
 *            Pattern is fitted around this peak
 * @param charge
 *            Charge state of the fitted pattern
 */
private void fitPattern(Vector<Point2D> fittedPeaks, Point2D p, int charge, Vector<Point2D> sortedPeaks) {

	if (charge == 0) {
		return;
	}

	// Search for peaks before the start peak
	if (!monotonicShape) {
		fitHalfPattern(p, charge, -1, fittedPeaks, sortedPeaks);
	}

	// Search for peaks after the start peak
	fitHalfPattern(p, charge, 1, fittedPeaks, sortedPeaks);

}

/**
 * Helper method for fitPattern. Fits only one half of the pattern.
 * 
 * @param p
 *            Pattern is fitted around this peak
 * @param charge
 *            Charge state of the fitted pattern
 * @param direction
 *            Defines which half to fit: -1=fit to peaks before start M/Z,
 *            +1=fit to peaks after start M/Z
 * @param fittedPeaks
 *            All matching peaks will be added to this set
 */
private void fitHalfPattern(Point2D p, int charge, int direction,
		Vector<Point2D> fittedPeaks, Vector<Point2D> sortedPeaks) {

	// Use M/Z and RT of the strongest peak of the pattern (peak 'p')
	double mainMZ = p.getX();

	// Variable n is the number of peak we are currently searching. 1=first
	// peak before/after start peak, 2=peak before/after previous, 3=...
	boolean followingPeakFound;
	int n = 1;
	do {

		// Assume we don't find match for n:th peak in the pattern (which
		// will end the loop)
		followingPeakFound = false;

		// Loop through all peaks, and collect candidates for the n:th peak
		// in the pattern
		Vector<Point2D> goodCandidates = new Vector<Point2D>();
		for (int ind = 0; ind < sortedPeaks.size(); ind++) {

			double candidatePeakMZ = sortedPeaks.get(ind).getX(); 

			if (candidatePeakMZ == -1 || candidatePeakMZ == 0)
				continue; 

			// Does this peak fill all requirements of a candidate?
			// - within tolerances from the expected location (M/Z and RT)
			// - not already a fitted peak (only necessary to avoid
			// conflicts when parameters are set too wide)
			double isotopeMZ = candidatePeakMZ - isotopeDistance
					* direction * n / (double) charge;

			if (mzTolerance.checkWithinTolerance(isotopeMZ, mainMZ) && (!fittedPeaks.contains(candidatePeakMZ))) {
				goodCandidates.add(sortedPeaks.get(ind)); 
			}

		}

		// Add all good candidates to the isotope pattern (note: in MZmine
		// 2.3 and older, only the highest candidate was added)
		if (!goodCandidates.isEmpty()) {

			fittedPeaks.addAll(goodCandidates);

			// n:th peak was found, so let's move on to n+1
			n++;
			followingPeakFound = true;
		}

	} while (followingPeakFound);

}

}