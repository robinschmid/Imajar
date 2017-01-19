package net.rs.lamsi.massimager.MyMZ.oldmzjavasave;

/*
public class SpectrumList extends Vector<MsnSpectrum> { 
	// header informationen
	private String header;
	protected File file;
	// 
	public SpectrumList(File file, String header) {
		super();
		this.header = header;
		this.file = file;
	}

	public MsnSpectrum getNearestSpectrumAtRT(double rt) {  
		if(this.size()==1) return this.firstElement();
		//
		for(int i=0; i<this.size()-1; i++) {
			MsnSpectrum spec0 = this.get(i); 
			MsnSpectrum spec1 = this.get(i+1); 
			double rt0 = spec0.getRetentionTimes().get(0).getTime();
			double rt1 = spec1.getRetentionTimes().get(0).getTime();
			//
			rt0 = Math.abs(rt-rt0);
			rt1 = Math.abs(rt-rt1);
			//  
			if(rt0<=rt1) return spec0;
		}
		// ansonsten last Element
		return this.lastElement();
	}
	// index
	public int getNearestSpectrumIndexAtRT(double rt) { 
		if(this.size()==1) return 0;
		//
		for(int i=0; i<this.size()-1; i++) {
			MsnSpectrum spec0 = this.get(i); 
			MsnSpectrum spec1 = this.get(i+1); 
			double rt0 = spec0.getRetentionTimes().get(0).getTime();
			double rt1 = spec1.getRetentionTimes().get(0).getTime();
			//
			rt0 = Math.abs(rt-rt0);
			rt1 = Math.abs(rt-rt1);
			// 
			if(rt0<=rt1) return i;
		}
		// ansonsten last Element
		return this.size()-1;
	}
	
	// From to
	public MsnSpectrum getSpectrumSum(double t0, double t1) throws FilteringFailedException {
		// TODO Auto-generated method stub
		MZChromatogram chrom = new MZChromatogram("");
		//  
		int spec0 = getNearestSpectrumIndexAtRT(t0); 
		int spec1 = getNearestSpectrumIndexAtRT(t1);   
		// only one spec?
		if(spec0==spec1) {
			return get(spec0);
		}
		else {
			// use filter for adding
			MZSpectrumCombineFilter filter = new MZSpectrumCombineFilter(this, spec0, spec1+1);
			if(filter.doFiltering()) {
				return (MsnSpectrum) filter.getResult();
			} else {
				throw new FilteringFailedException("Cannot create sum spectrum");
			} 
		}
	} 

	// Get SPECTRUM
	public MZChromatogram getSpectrumAsMZChrom(double rt) {
		// TODO Auto-generated method stub
		MZChromatogram chrom = new MZChromatogram("Spectrum");
		// 
		MsnSpectrum spec = getNearestSpectrumAtRT(rt); 
		// Alle spektren durchgehen nach chromType entscheiden wie die Intensität ist
		for(int i=0; i<spec.size(); i++) {
			// time bekommen
			double mz = spec.getMz(i);
			// intensity bekommen 
			double intensity = spec.getIntensity(i);
			// daten hinzufügen
			chrom.add(mz, intensity);
		}
		//		
		return chrom;
	}

	// Get SPECTRUM
	public static MZChromatogram getSpectrumAsMZChrom(MsnSpectrum spec) {
		// TODO Auto-generated method stub
		MZChromatogram chrom = new MZChromatogram("Spectrum"); 
		// Alle spektren durchgehen nach chromType entscheiden wie die Intensität ist
		for(int i=0; i<spec.size(); i++) {
			// time bekommen
			double mz = spec.getMz(i);
			// intensity bekommen 
			double intensity = spec.getIntensity(i);
			// daten hinzufügen
			chrom.add(mz, intensity);
		}
		//		
		return chrom;
	}
	
	// From to
	public MZChromatogram getSpectrumAsMZChrom(double t0, double t1) {
		// TODO Auto-generated method stub
		MZChromatogram chrom = new MZChromatogram("TIC");
		//  
		int spec0 = getNearestSpectrumIndexAtRT(t0); 
		int spec1 = getNearestSpectrumIndexAtRT(t1); 
		//
		MsnSpectrum resultspec = new MsnSpectrum();
		// Alle spektren durchgehen nach chromType entscheiden wie die Intensität ist
		for(int s=spec0; s<=spec1; s++) {
			MsnSpectrum currentSpec = this.get(s);
			resultspec.addPeaks(currentSpec);
		}
		// Generate chrom 
		for(int i=0; i<resultspec.size(); i++) { 
			// time bekommen
			double mz = resultspec.getMz(i);
			// intensity bekommen 
			double intensity = resultspec.getIntensity(i);
			// daten hinzufügen
			chrom.add(mz, intensity);
		}
		//		
		return chrom;
	} 
	// Get TIC
	// MZ Chrom from all Spectrum
	public MZChromatogram getTIC() {
		// TODO Auto-generated method stub
		MZChromatogram chrom = new MZChromatogram("TIC");
		// Alle spektren durchgehen nach chromType entscheiden wie die Intensität ist
		for(int i=0; i<this.size(); i++) {
			MsnSpectrum spec = this.get(i);
			// time bekommen
			double rt = spec.getRetentionTimes().get(0).getTime();
			// intensity bekommen 
			double intensity = spec.getTotalIonCurrent();
			// daten hinzufügen
			chrom.add(rt, intensity);
		}
		//		
		return chrom;
	}
	// MZ Chrom from all Spectrum
	public MZChromatogram getMZChrom(MZIon mzIon, ChromGenType chromType) {
		// TODO Auto-generated method stub
		MZChromatogram chrom = new MZChromatogram(mzIon.getMz());
		chrom.setDescription(mzIon.getName());
		// Alle spektren durchgehen nach chromType entscheiden wie die Intensität ist
		for(int i=0; i<this.size(); i++) {
			MsnSpectrum spec = this.get(i);
			// time bekommen
			double rt = spec.getRetentionTimes().get(0).getTime();
			// intensity bekommen 
			double intensity = chromType.getIntensity(mzIon.getMz(), mzIon.getPm(), spec);
			// daten hinzufügen
			chrom.add(rt, intensity);
		}
		//		
		return chrom;
	}

	public double getMaxRT() { 
		return this.lastElement().getRetentionTimes().get(0).getTime();
	}

	
	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	 }
	
}
*/

