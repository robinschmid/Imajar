package net.rs.lamsi.massimager.MyMZ.preprocessing.filtering.spectra;

import java.util.Vector;

import net.rs.lamsi.massimager.MyMZ.MZChromatogram;
import net.rs.lamsi.massimager.MyMZ.preprocessing.filtering.AbstractDataFilter;
import net.sf.mzmine.datamodel.DataPoint;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.datamodel.Scan;

public class MZSpectrumCombineFilter extends AbstractDataFilter  {
	
	private MZChromatogram result;
	private RawDataFile raw = null;
	private int start = -1, end = -1;
	private Vector<Scan> specList = null;
	Vector<MZChromatogram> chromList = null;
	// mzwindow for smoothening filter
	private double mzwindow = -1;
	
	// SetUp in constructor 
	public MZSpectrumCombineFilter(RawDataFile raw) {
		super();
		this.raw = raw;
		start = 0;
		end = getNumOfSpectra();
	} 
	
	/*
	 * exclusive end
	 */
	public MZSpectrumCombineFilter(RawDataFile rawold, int start, int end) {
		this(rawold);
		this.start = start;
		this.end = end;
	}

	// doFiltering
	public <T> MZSpectrumCombineFilter(Vector<T> specList) {
		if(specList.size()>0) {
			if(specList.get(0) instanceof Scan) this.specList  = ((Vector<Scan>) specList);
			if(specList.get(0) instanceof MZChromatogram) this.chromList  = (Vector<MZChromatogram>) specList;
		}
		start=0;
		end = getNumOfSpectra(); 
	} 

	public boolean doFiltering() {
		try {
			if(getNumOfSpectra()>0) {
				if(specList!=null || raw != null) combineSpectra();
				if(chromList!=null) combineMZChroms();
				
				// Smoothening filter by MZSpectrumDuplicateMassFilter
				// detect same mz in spectrum add sum intensity
				MZSpectrumDuplicateMassFilter filter = new MZSpectrumDuplicateMassFilter(result, mzwindow);
				boolean state = filter.doFiltering();
				result = (MZChromatogram) filter.getResult();  
				
				return state;
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return false;
	}
	
	private void combineSpectra() {
		result = new MZChromatogram(""); 
		// add all points
		for (int i = start; i < end; i++) {  
			Scan scan = getSpectrumAt(i);
			DataPoint[] dpList = scan.getDataPoints();
			 
			// calc mzwindow for smoothening
			if(mzwindow==-1) {
				// one third of the distance between 2 datapoints (TODO zeros can be deleted - this will cause an wrong output)
				// mzwindow could differ on higher mass TODO   
				mzwindow = (dpList[dpList.length/2].getMZ()-dpList[dpList.length/2 -1].getMZ())*1.0/3.0;
			}
			// add all points to mzchrom as spectrum
			for (int j = 0; j < dpList.length; j++) {
				DataPoint dp = dpList[j]; 
				// add all peaks (they will be inserted sorted)
				result.add(dp.getMZ(), dp.getIntensity());
			}
		} 
	}
	
	private void combineMZChroms() {
		result = new MZChromatogram(""); 
		for (int i = 0; i < chromList.size(); i++) {
			MZChromatogram chrom = chromList.get(i);

			// calc mzwindow for smoothening
			if(mzwindow==-1) {
				// one third of the distance between 2 datapoints (TODO zeros can be deleted - this will cause an wrong output)
				// mzwindow could differ on higher mass TODO   
				mzwindow = (chrom.getX(chrom.getItemCount()/2).doubleValue()-chrom.getX(chrom.getItemCount()/2-1).doubleValue())*1.0/3.0;
			}
			// add all points			
			for (int j = 0; j < chrom.getItemCount(); j++) {
				result.add(chrom.getDataItem(j));
			} 
		}
	}
	
	// to get the right scan 0 based!
	public Scan getSpectrumAt(int i) { 
		if(raw!=null) return raw.getScan(i+1);
		else if(specList!=null) return (specList.get(i)); 
		else return null;
	}
	public int getNumOfSpectra()  {
		if(raw!=null) return raw.getNumOfScans();
		else if(chromList!=null) return chromList.size();
		else return (specList.size());
	}

	@Override
	public Object getResult() { 
		return result;
	}

}
