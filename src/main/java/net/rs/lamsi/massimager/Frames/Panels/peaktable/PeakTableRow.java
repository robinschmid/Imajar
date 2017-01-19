package net.rs.lamsi.massimager.Frames.Panels.peaktable;

import net.sf.mzmine.datamodel.PeakList;
import net.sf.mzmine.datamodel.PeakListRow;

public class PeakTableRow {
	// "File", "PeakList", "MZ", "z (charge)", "Mass", "- Mass (rule)", "Monoisotopic mass", "Height", "Area", "rt(min)", "rt", "rt(max)"
	private String rawDataName, ID;
	private PeakList peakList;
	private PeakListRow peakRow;
	private double mz, mass, monoisotopicMass, height, area, rtMin, rt, rtMax;
	private double minusMassRule = 0;
	private int charge;
	
	public PeakTableRow(String ID, String rawDataName, PeakList peakList, double mz,
			double mass, double monoisotopicMass, double height, double area,
			double rtMin, double rt, double rtMax, int charge, double minusMassRule) {
		super();
		this.ID = ID;
		this.rawDataName = rawDataName;
		this.peakList = peakList;
		this.mz = mz;
		this.mass = mass;
		this.monoisotopicMass = monoisotopicMass;
		this.height = height;
		this.rtMin = rtMin;
		this.rt = rt;
		this.rtMax = rtMax;
		this.charge = charge;
		this.area = area;
		this.minusMassRule = minusMassRule;
	}
	
	public PeakTableRow(String ID, String rawDataName, PeakList peakList, PeakListRow r, double rtMin, double rtMax, double minusMassRule) {
		this.ID = ID;
		this.rawDataName = rawDataName;
		this.peakList = peakList;
		this.peakRow = r;
		this.mz = r.getAverageMZ();
		this.height = r.getAverageHeight();
		this.area = r.getAverageArea();
		this.rt = r.getAverageRT(); 
		this.rtMin = rtMin;
		this.rtMax = rtMax; 
		if(r.getBestPeak()!=null) this.charge = r.getBestPeak().getCharge();
		else this.charge = 0;
		this.minusMassRule = minusMassRule;
		// 
		this.mass = mz*charge;
		this.monoisotopicMass = mass-minusMassRule; 
	}

	public Object[] toArray() {
		return getArray();
	}
	
	public Object[] getArray() {
		Object[] array = {rawDataName, peakList, mz, charge, mass, minusMassRule, monoisotopicMass, height, area, rtMin, rt, rtMax};
		return array;
	}
	
	
	public String getRawDataName() {
		return rawDataName;
	}

	public void setRawDataName(String rawDataName) {
		this.rawDataName = rawDataName;
	}

	public String getPeakListName() {
		if(peakList==null) return "";
		return peakList.getName();
	}

	public void setPeakList(PeakList peakList) {
		this.peakList = peakList;
	}
	public PeakList getPeakList() {
		return peakList;
	}

	public double getMz() {
		return mz;
	}

	public void setMz(double mz) {
		this.mz = mz;
	}

	public double getMass() {
		return mass;
	}

	public void setMass(double mass) {
		this.mass = mass;
	}

	public double getMonoisotopicMass() {
		return monoisotopicMass;
	}

	public void setMonoisotopicMass(double monoisotopicMass) {
		this.monoisotopicMass = monoisotopicMass;
	} 

	public double getRtMin() {
		return rtMin;
	}

	public void setRtMin(double rtMin) {
		this.rtMin = rtMin;
	}

	public double getRtMax() {
		return rtMax;
	}

	public void setRtMax(double rtMax) {
		this.rtMax = rtMax;
	}

	public int getCharge() {
		return charge;
	}

	public void setCharge(int charge) {
		this.charge = charge;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double getArea() {
		return area;
	}

	public void setArea(double area) {
		this.area = area;
	}

	public double getRt() {
		return rt;
	}

	public void setRt(double rt) {
		this.rt = rt;
	}

	public double getMinusMassRule() {
		return minusMassRule;
	}

	public void setMinusMassRule(double minusMassRule) {
		this.minusMassRule = minusMassRule;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public PeakListRow getPeakRow() {
		return peakRow;
	} 
}
