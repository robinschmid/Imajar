package net.rs.lamsi.massimager.Settings.preferences;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import net.rs.lamsi.massimager.Settings.Settings;

public class SettingsGeneralValueFormatting extends Settings {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    //
	
	private int decimalsMZ, decimalsIntensity, decimalsRT;
	private boolean isShowingExponentIntensity;
	

	public SettingsGeneralValueFormatting() {
		super("/Settings/General/", "valform");  
		resetAll();
	}


	@Override
	public void resetAll() { 
		decimalsMZ = 4;
		decimalsIntensity = 1;
		decimalsRT = 2;
		isShowingExponentIntensity = true;
	}


	public int getDecimalsMZ() {
		return decimalsMZ;
	}


	public void setDecimalsMZ(int decimalsMZ) {
		this.decimalsMZ = decimalsMZ;
	}


	public int getDecimalsIntensity() {
		return decimalsIntensity;
	}


	public void setDecimalsIntensity(int decimalsIntensity) {
		this.decimalsIntensity = decimalsIntensity;
	}


	public int getDecimalsRT() {
		return decimalsRT;
	}


	public void setDecimalsRT(int decimalsRT) {
		this.decimalsRT = decimalsRT;
	}


	public boolean isShowingExponentIntensity() {
		return isShowingExponentIntensity;
	}


	public void setShowingExponentIntensity(boolean isShowingExponentIntensity) {
		this.isShowingExponentIntensity = isShowingExponentIntensity;
	}


	public NumberFormat getMZFormat() {   
		return getNumberFormat(decimalsMZ, false);
	}

	public NumberFormat getIntensityFormat() { 
		return getNumberFormat(decimalsIntensity, isShowingExponentIntensity());
	}

	public NumberFormat getRTFormat() {   
		return getNumberFormat(decimalsRT, false);
	}
	
	public static NumberFormat getNumberFormat(int decimals, boolean exponent) { 
		if(exponent) {
			DecimalFormat exponentFormat = new DecimalFormat("0.##E00;(0.##E00)");
			exponentFormat.setMinimumFractionDigits(decimals);
			exponentFormat.setMaximumFractionDigits(decimals);
			return exponentFormat;
		}
		else {
			DecimalFormat format = new DecimalFormat("#,###.######;(#,###.######)");
			format.setMinimumFractionDigits(decimals);
			format.setMaximumFractionDigits(decimals);
			return format;
		}
	}
	
}
