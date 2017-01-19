package net.rs.lamsi.massimager.Settings.visualization;

import net.rs.lamsi.massimager.Settings.Settings; 


public class SettingsPlotSpectraLabelGenerator extends Settings {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    //

	protected double minimumRelativeIntensityOfLabel;
	protected int minimumSpaceBetweenLabels;
	protected boolean showLabels, showCharge;
	

	public SettingsPlotSpectraLabelGenerator() {
		super("/Settings/Visualization/", "setVisLabelSpec"); 
		resetAll();
	} 

	@Override
	public void resetAll() { 
		showCharge = true;
		showLabels = true;
		minimumSpaceBetweenLabels = 100;
		minimumRelativeIntensityOfLabel = 0.05;
	}

	public double getMinimumRelativeIntensityOfLabel() {
		return minimumRelativeIntensityOfLabel;
	}

	public void setMinimumRelativeIntensityOfLabel(
			double minimumRelativeIntensityOfLabel) {
		this.minimumRelativeIntensityOfLabel = minimumRelativeIntensityOfLabel;
	}

	public int getMinimumSpaceBetweenLabels() {
		return minimumSpaceBetweenLabels;
	}

	public void setMinimumSpaceBetweenLabels(int minimumSpaceBetweenLabels) {
		this.minimumSpaceBetweenLabels = minimumSpaceBetweenLabels;
	}

	public boolean isShowLabels() {
		return showLabels;
	}

	public void setShowLabels(boolean showLabels) {
		this.showLabels = showLabels;
	}

	public boolean isShowCharge() {
		return showCharge;
	}

	public void setShowCharge(boolean showCharge) {
		this.showCharge = showCharge;
	} 
	
	
}
