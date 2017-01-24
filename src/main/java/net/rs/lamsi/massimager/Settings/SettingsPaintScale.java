package net.rs.lamsi.massimager.Settings;

import java.awt.Color;

public class SettingsPaintScale extends Settings {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    //
	// for creation of standards
	public static final int S_RAINBOW = 0, S_BLACK_RED_YE_W = 1, S_BLACK_BLUE_GR_W = 2, S_RED = 3, S_GREEN = 4, S_BLUE=5, S_YELLOW=6, S_PURPLE=7, S_GREY=8, S_RAINBOW_BRIGHT=9, S_RAINBOW_INVERSE = 10, S_KARST_RAINBOW_INVERSE = 11;
	
	
	private int levels = 256;
	private boolean isMonochrom = false, isGrey = false;
	private boolean isInverted, usesBAsMax, usesWAsMin;
	private boolean usesMinMax, usesMinAsInvisible;
	
	// min max values or filter (cut off filter)
	private boolean usesMinValues, usesMaxValues, usesMinMaxFromSelection;
	private double min, max;
	// in percentage
	private float minFilter, maxFilter;
	private boolean usesMinFilter, usesMaxFilter;
	
	// LOD monochrome
	private boolean isLODMonochrome = false;
	private double LOD = 0;
	
	// brightnessFactor for black and white as min / max
	// defines how fast brightness and saturation will be increased /decreased
	private float brightnessFactor;
	
	private Color minColor, maxColor;
	  

	public SettingsPaintScale() {
		super("/Settings/PaintScales/", "setPaintScale"); 
		resetAll(); 
	} 
	
	public void setAll(int levels, boolean isMonochrom, boolean isInverted, boolean usesBAsMax, boolean usesWAsMin,
			boolean usesMinMax, boolean usesMinAsInvisible, boolean usesMinValues, boolean usesMaxValues, double min, double max, Color minColor,
			Color maxColor, float brightnessFactor, float minFilter, float maxFilter, boolean isGrey, boolean usesMinMaxFromSelection, boolean isLODMonochrome, double LOD) { 
		this.levels = levels;
		this.isMonochrom = isMonochrom;
		this.isInverted = isInverted;
		this.usesWAsMin = usesWAsMin;
		this.usesBAsMax = usesBAsMax;
		this.usesMinMax = usesMinMax;
		this.usesMinAsInvisible = usesMinAsInvisible;
		this.min = min;
		this.max = max;
		this.minColor = minColor;
		this.maxColor = maxColor;
		this.brightnessFactor = brightnessFactor;
		this.minFilter = minFilter;
		this.maxFilter = maxFilter;
		this.usesMaxValues = usesMaxValues;
		this.usesMinValues = usesMinValues;
		this.isGrey = isGrey;
		this.usesMinMaxFromSelection = usesMinMaxFromSelection;
		this.isLODMonochrome = isLODMonochrome;
		this.LOD = LOD; 
	}




	@Override
	public void resetAll() {
		levels = 256;
		this.isMonochrom = false;
		this.isInverted = false;
		this.usesWAsMin = true;
		this.usesBAsMax = true;
		this.usesMinMax = true;
		this.usesMinAsInvisible = false;
		this.min = 0;
		this.max = 0;
		this.minColor = Color.CYAN;
		this.maxColor = Color.RED;
		brightnessFactor = 4;
		this.minFilter = 2.5f;
		this.maxFilter = 0.2f; 
		this.usesMaxValues = false;
		this.usesMinValues = false;
		usesMinMaxFromSelection = false;
	}

	//##############################################################################################
	// create standards 
	public static SettingsPaintScale createSettings(int style) {
		SettingsPaintScale scale = new SettingsPaintScale(); 
		scale.setInverted(true);
		scale.setUsesBWAsMinMax(true, true);
		switch(style) {
		case S_RAINBOW:
			scale.setMonochrom(false);
			scale.setBrightnessFactor(5);
			scale.setMinColor(Color.BLUE);
			scale.setMaxColor(Color.RED);
			break;
		case S_KARST_RAINBOW_INVERSE:
			scale.setMonochrom(false);
			scale.setLODMonochrome(true);
			scale.setBrightnessFactor(6.5f);
			scale.setMinColor(Color.RED);
			scale.setMaxColor(Color.BLUE); 
			scale.setUsesBWAsMinMax(false, true);
			break;
		case S_RAINBOW_INVERSE:
			scale.setMonochrom(false);
			scale.setBrightnessFactor(6.5f);
			scale.setMinColor(Color.RED);
			scale.setMaxColor(Color.BLUE); 
			break;
		case S_RAINBOW_BRIGHT:
			scale.setMonochrom(false);
			scale.setBrightnessFactor(5);
			scale.setMinColor(Color.CYAN);
			scale.setMaxColor(Color.RED);
			break;
		case S_BLACK_BLUE_GR_W:
			scale.setMonochrom(false);
			scale.setBrightnessFactor(5);
			scale.setMinColor(Color.GREEN);
			scale.setMaxColor(new Color(115,0,255));
			break;
		case S_BLACK_RED_YE_W:
			scale.setMonochrom(false);
			scale.setBrightnessFactor(5);
			scale.setMinColor(Color.YELLOW);
			scale.setMaxColor(Color.RED);
			break;
		case S_BLUE:
			scale.setMonochrom(true);
			scale.setBrightnessFactor(5);
			scale.setMinColor(Color.BLUE);
			scale.setMaxColor(Color.BLUE);
			break;
		case S_GREEN:
			scale.setMonochrom(true);
			scale.setBrightnessFactor(5);
			scale.setMinColor(Color.GREEN);
			scale.setMaxColor(Color.GREEN);
			break;
		case S_PURPLE:
			scale.setMonochrom(true);
			scale.setBrightnessFactor(5);
			scale.setMinColor(new Color(186, 0, 255));
			scale.setMaxColor(Color.RED);
			break; 
		case S_RED:
			scale.setMonochrom(true);
			scale.setBrightnessFactor(5);
			scale.setMinColor(Color.RED);
			scale.setMaxColor(Color.RED);
			break;
		case S_YELLOW:
			scale.setMonochrom(true);
			scale.setBrightnessFactor(5);
			scale.setMinColor(Color.YELLOW);
			scale.setMaxColor(Color.YELLOW);
			break;
		case S_GREY:
			scale.setMonochrom(true);
			scale.setBrightnessFactor(5);
			scale.setGrey(true);
			break; 
		}
		return scale;
	}
	// done with standards
	//##############################################################################################
	
	//##############################################################################################
	// GETTER AND SETTER
	public int getLevels() {
		return levels;
	}

	public void setLevels(int levels) {
		this.levels = levels;
	}

	public boolean isMonochrom() {
		return isMonochrom;
	}
	public void setMonochrom(boolean isMonochrom) {
		this.isMonochrom = isMonochrom;
	}
	public boolean isInverted() {
		return isInverted;
	}
	public void setInverted(boolean isInverted) {
		this.isInverted = isInverted;
	}
	public boolean isUsesBAsMax() {
		return usesBAsMax;
	}
	public void setUsesBAsMax(boolean usesBAsMax) {
		this.usesBAsMax = usesBAsMax;
	}
	public boolean isUsesWAsMin() {
		return usesWAsMin;
	}
	public void setUsesWAsMin(boolean usesWAsMin) {
		this.usesWAsMin = usesWAsMin;
	}

	public void setUsesBWAsMinMax(boolean usesWAsMin, boolean usesBAsMax) {
		this.usesWAsMin = usesWAsMin;
		this.usesBAsMax = usesBAsMax;
	}

	public boolean isUsesMinMax() {
		return usesMinMax;
	}

	public void setUsesMinMax(boolean usesMinMax) {
		this.usesMinMax = usesMinMax;
	}

	/**
	 * absolute
	 * @return
	 */
	public double getMin() {
		return min;
	}

	/**
	 * absolute
	 * @param min
	 */
	public void setMin(double min) {
		this.min = min;
	}

	/**
	 * absolute
	 * @return
	 */
	public double getMax() {
		return max;
	}

	/**
	 * absolute
	 * @param max
	 */
	public void setMax(double max) {
		this.max = max;
	}

	public Color getMinColor() {
		return minColor;
	}

	public void setMinColor(Color minColor) {
		this.minColor = minColor;
	}

	public Color getMaxColor() {
		return maxColor;
	}

	public void setMaxColor(Color maxColor) {
		this.maxColor = maxColor;
	}

	public boolean isUsesMinAsInvisible() {
		return usesMinAsInvisible;
	}

	public void setUsesMinAsInvisible(boolean usesMinAsInvisible) {
		this.usesMinAsInvisible = usesMinAsInvisible;
	}

	public float getBrightnessFactor() { 
		return brightnessFactor;
	}
	public void setBrightnessFactor(float bf) {
		brightnessFactor = bf;
	}

	public boolean isUsesMinValues() {
		return usesMinValues;
	}

	public void setUsesMinValues(boolean usesMinValues) {
		this.usesMinValues = usesMinValues;
	}

	public boolean isUsesMaxValues() {
		return usesMaxValues;
	}

	public void setUsesMaxValues(boolean usesMaxValues) {
		this.usesMaxValues = usesMaxValues;
	}

	public float getMinFilter() {
		return minFilter;
	}

	public void setMinFilter(float minFilter) {
		this.minFilter = minFilter;
	}

	public float getMaxFilter() {
		return maxFilter;
	}

	public void setMaxFilter(float maxFilter) {
		this.maxFilter = maxFilter;
	}

	public boolean isGrey() {
		return isGrey;
	}

	public void setGrey(boolean isGrey) {
		this.isGrey = isGrey;
	}

	public boolean isUsesMinMaxFromSelection() {
		return usesMinMaxFromSelection;
	}

	public void setUsesMinMaxFromSelection(boolean usesMinMaxFromSelection) {
		this.usesMinMaxFromSelection = usesMinMaxFromSelection;
	}

	public boolean isLODMonochrome() {
		return isLODMonochrome;
	}

	public void setLODMonochrome(boolean isLODMonochrome) {
		this.isLODMonochrome = isLODMonochrome;
	}

	public double getLOD() {
		return LOD;
	}

	public void setLOD(double lOD) {
		LOD = lOD;
	}

	public boolean isUsesMinFilter() {
		return usesMinFilter;
	}

	public void setUsesMinFilter(boolean usesMinFilter) {
		this.usesMinFilter = usesMinFilter;
	}

	public boolean isUsesMaxFilter() {
		return usesMaxFilter;
	}

	public void setUsesMaxFilter(boolean usesMaxFilter) {
		this.usesMaxFilter = usesMaxFilter;
	}
}
