package net.rs.lamsi.massimager.Settings;

import net.rs.lamsi.massimager.Settings.image.sub.SettingsGeneralImage;
import net.sf.mzmine.parameters.parametertypes.MZTolerance;
import net.sf.mzmine.parameters.parametertypes.RTTolerance;


public class SettingsChargeCalculator extends SettingsGeneralImage {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    //
	private MZTolerance mzTolerance;
	private RTTolerance rtTolerance;
	private boolean monotonicShape;
	private int maximumCharge; 


	public SettingsChargeCalculator() {
		super("/Settings/Charge Calculator/", "setChargeCalc"); 
		resetAll();
	} 

	@Override
	public void resetAll() { 
		mzTolerance = new MZTolerance(0.005, 5);
		rtTolerance = new RTTolerance(true, 0.25);
		monotonicShape = false;
		maximumCharge = 15;
	} 
	
	
	public MZTolerance getMzTolerance() {
		return mzTolerance;
	}

	public void setMzTolerance(MZTolerance mzTolerance) {
		this.mzTolerance = mzTolerance;
	}

	public RTTolerance getRtTolerance() {
		return rtTolerance;
	}

	public void setRtTolerance(RTTolerance rtTolerance) {
		this.rtTolerance = rtTolerance;
	}

	public boolean isMonotonicShape() {
		return monotonicShape;
	}

	public void setMonotonicShape(boolean monotonicShape) {
		this.monotonicShape = monotonicShape;
	}

	public int getMaximumCharge() {
		return maximumCharge;
	}

	public void setMaximumCharge(int maximumCharge) {
		this.maximumCharge = maximumCharge;
	}

}
