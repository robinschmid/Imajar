package net.rs.lamsi.massimager.MyMZ;

import java.io.Serializable;

public class MZIon implements Serializable {

	private String name;
	private double mz, pm;

	public MZIon(String name, double mz, double pm) {
		super();
		this.name = name;
		this.mz = mz;
		this.pm = pm;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getMz() {
		return mz;
	}
	public void setMz(double mz) {
		this.mz = mz;
	}
	public double getPm() {
		return pm;
	}
	public void setPm(double pm) {
		this.pm = pm;
	}

	public void setUp(String name, double mz, double pm) { 
		this.name = name;
		this.mz = mz;
		this.pm = pm;
	}
}
