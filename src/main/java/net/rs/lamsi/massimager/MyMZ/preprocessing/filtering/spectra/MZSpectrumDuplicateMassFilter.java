package net.rs.lamsi.massimager.MyMZ.preprocessing.filtering.spectra;

import net.rs.lamsi.massimager.MyMZ.MZChromatogram;
import net.rs.lamsi.massimager.MyMZ.preprocessing.filtering.AbstractDataFilter;
 

/*
 * This Filter deletes duplicate masses within a mz range
 * New Peaks will ADD intensity and COMBINE mz 
 */
public class MZSpectrumDuplicateMassFilter extends AbstractDataFilter {
	
	private MZChromatogram spec, result;
	private double mzwindow;

	public MZSpectrumDuplicateMassFilter(MZChromatogram spec, double mzwindow) {
		super();
		this.spec = spec;
		this.mzwindow = mzwindow;
		System.out.println("Filtering with Window: "+mzwindow);
	}

	@Override
	public boolean doFiltering() {
		try{  
			result = new MZChromatogram("");
			//  
			double lastintensity = spec.getY(0).doubleValue(), lastmz = spec.getX(0).doubleValue();
			// go through all peaks and check for peaks in distance
			for(int i=1; i<spec.getItemCount(); i++) {
				// mza is always the last added result peak, exept result is empty   
				double mzb = spec.getX(i).doubleValue();
				double diff = (Math.abs(lastmz-mzb));
				
				if(diff<=mzwindow) {
					// calc one peak from these two: lastmz and mzb
					double intb = spec.getY(i).doubleValue();
					double intensitySum = lastintensity+intb;
					if(intensitySum==0) { 
						// intensity=0 --> use mzb
						lastmz = mzb;
						lastintensity = intb;
					}
					else {
						// use weightedMZ for next check
						double weightedMZ = lastmz + diff* (intb/intensitySum);  
						lastmz = weightedMZ;
						lastintensity = intensitySum;
					} 
				}
				else {
					// add last point and check mzb next
					result.add(lastmz, lastintensity); 
					// set last 
					lastmz = mzb;
					lastintensity = spec.getY(i).doubleValue();
				}
			}
			// 
		} catch(Exception ex) {
			return false;
		} 
		return true;
	} 


	@Override
	public Object getResult() { 
		return result;
	}
	
}
