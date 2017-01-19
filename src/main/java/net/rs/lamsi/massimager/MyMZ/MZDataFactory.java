package net.rs.lamsi.massimager.MyMZ;

import java.io.File;
import java.util.Vector;

import net.rs.lamsi.massimager.MyMZ.preprocessing.filtering.exceptions.FilteringFailedException;
import net.rs.lamsi.massimager.MyMZ.preprocessing.filtering.spectra.MZSpectrumCombineFilter;
import net.sf.mzmine.datamodel.DataPoint;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.datamodel.Scan;
import net.sf.mzmine.datamodel.Spectrum;
 

public class MZDataFactory {  

	// get Scan next to retentiontime rt
	public static Scan getNearestSpectrumAtRT(RawDataFile raw, double rt) {   
		//
		for(int i=1; i<=raw.getNumOfScans()-1; i++) {
			Scan spec0 = raw.getScan(i);
			Scan spec1 = raw.getScan(i+1);
			double rt0 = spec0.getRetentionTime();
			double rt1 = spec1.getRetentionTime(); 
			//
			rt0 = Math.abs(rt-rt0);
			rt1 = Math.abs(rt-rt1);
			//  
			if(rt0<=rt1) return spec0;
		}
		// ansonsten last Element
		return raw.getScan(raw.getNumOfScans());
	}
	// get Scan index next to retentiontime rt
	public static int getNearestSpectrumIndexAtRT(RawDataFile raw, double rt) { 
		if(raw==null || raw.getNumOfScans()<=0) return 0;
		//
		for(int i=1; i<=raw.getNumOfScans()-1; i++) {
			Scan spec0 = raw.getScan(i);
			Scan spec1 = raw.getScan(i+1);
			double rt0 = spec0.getRetentionTime();
			double rt1 = spec1.getRetentionTime();
			//
			rt0 = Math.abs(rt-rt0);
			rt1 = Math.abs(rt-rt1);
			//  
			if(rt0<=rt1) return i;
		}
		// ansonsten last Element
		return (raw.getNumOfScans());
	}
	
	// From to
	public static MZChromatogram getSpectrumSumAsMZChrom(RawDataFile raw, double t0, double t1) throws FilteringFailedException {
		// TODO Auto-generated method stub
		MZChromatogram chrom = new MZChromatogram("");
		//  
		int spec0 = getNearestSpectrumIndexAtRT(raw, t0); 
		int spec1 = getNearestSpectrumIndexAtRT(raw, t1);   
		// only one spec?
		
		if(spec0==spec1) { 
			return getSpectrumAsMZChrom(raw.getScan(spec0));
		}
		else {
			// use filter for adding
			MZSpectrumCombineFilter filter = new MZSpectrumCombineFilter(raw, spec0, spec1+1);
			if(filter.doFiltering()) {
				return (MZChromatogram) filter.getResult();
			} else {
				throw new FilteringFailedException("Cannot create sum spectrum");
			} 
		}
	} 

	// Get SPECTRUM
	public static MZChromatogram getSpectrumAsMZChrom(RawDataFile raw, double rt) {
		// TODO Auto-generated method stub
		MZChromatogram chrom = new MZChromatogram("Spectrum");
		// 
		Scan spec = getNearestSpectrumAtRT(raw, rt); 
		DataPoint[] dpList = spec.getDataPoints();
		// 
		for(int i=0; i<dpList.length; i++) {
			// mz
			double mz = dpList[i].getMZ();
			// intensity 
			double intensity = dpList[i].getIntensity();
			// add datapoint
			chrom.add(mz, intensity);
		}
		//		
		return chrom;
	}


	// Get SPECTRUM
	public static MZChromatogram getSpectrumAsMZChrom(Scan spec) {
		if(spec==null) return null;
		// TODO Auto-generated method stub
		MZChromatogram chrom = new MZChromatogram("Spectrum"); 
		DataPoint[] dpList = spec.getDataPoints();
		// 
		for(int i=0; i<dpList.length; i++) {
			// mz
			double mz = dpList[i].getMZ();
			// intensity  
			double intensity = dpList[i].getIntensity();
			// add datapoint
			chrom.add(mz, intensity);
		}
		//		
		return chrom;
	}
	
	// Get TIC
	// MZ Chrom from all Spectrum
	public static MZChromatogram getTIC(RawDataFile raw) {
		// TODO Auto-generated method stub
		MZChromatogram chrom = new MZChromatogram("TIC");
		// Alle spektren durchgehen nach chromType entscheiden wie die Intensität ist
		for(int i=1; i<=raw.getNumOfScans(); i++) {
			Scan spec = raw.getScan(i);
			// time bekommen
			double rt = spec.getRetentionTime();
			// intensity bekommen 
			double intensity = spec.getTIC();
			// daten hinzufügen
			chrom.add(rt, intensity);
		}
		//		
		return chrom;
	}
	// MZ Chrom from all Spectrum
	public static MZChromatogram getMZChrom(RawDataFile raw, MZIon mzIon, ChromGenType chromType) {
		// TODO Auto-generated method stub
		MZChromatogram chrom = new MZChromatogram(mzIon.getMz());
		chrom.setDescription(mzIon.getName());
		// Alle spektren durchgehen nach chromType entscheiden wie die Intensität ist
		for(int i=1; i<=raw.getNumOfScans(); i++) {
			Scan spec = raw.getScan(i);
			// time bekommen
			double rt = spec.getRetentionTime();
			// intensity bekommen 
			double intensity = chromType.getIntensity(mzIon.getMz(), mzIon.getPm(), spec);
			// daten hinzufügen
			chrom.add(rt, intensity);
		}
		//		
		return chrom;
	}

	
}
