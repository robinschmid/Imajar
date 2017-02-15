package net.rs.lamsi.massimager.Heatmap;
import java.awt.Color;
import java.awt.Paint;

import net.rs.lamsi.massimager.Settings.image.visualisation.SettingsPaintScale;

import org.jfree.chart.renderer.GrayPaintScale;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.PaintScale;


public class PaintScaleGenerator {




	// Unterschiedlicher PaintScales
	public static PaintScale generateStepPaintScale(double min, double max, double promin, double promax, Color cmin, Color cmax, int stepCount) {  
		// Min ist pormin von max
		double realmin = max*promin;
		double realmax = max*promax;
		// 
		LookupPaintScale paintScale = new LookupPaintScale(min,max,Color.lightGray); 
		// Bei null den min Wert hinzufügen
		paintScale.add(0, cmin);
		// 
		for(int i=0; i<stepCount; i++) {
			double value = realmin+realmax/(stepCount-1)*i;
			paintScale.add(value, interpolate(cmin, cmax, i/(stepCount-1.0f)));
		} 
		// 
		paintScale.add(max, cmax);
		//
		return paintScale; 
	}

	// Unterschiedlicher PaintScales
	/**
	 * 
	 * @param min z value in dataset
	 * @param max z value in dataset
	 * @param settings
	 * @return
	 */
	public static PaintScale generateStepPaintScale(double min, double max, SettingsPaintScale settings) {
			if(max<min)
				min=max-0.000001;
			// real min and max values as given by minz or min in settings
			double realmin = ((settings.isUsesMinMax() || settings.isUsesMinMaxFromSelection()) ? settings.getMinIAbs(min,max) : min);
			double realmax = ((settings.getMaxIAbs(min,max)!=0 && (settings.isUsesMinMax() || settings.isUsesMinMaxFromSelection())) ? settings.getMaxIAbs(min,max) : max); 
			

			// error when min == max
			if(realmin==realmax || realmax<realmin) {
				realmin = realmax-0.001;
			} 
			// 
			LookupPaintScale paintScale = new LookupPaintScale(min,max,Color.lightGray); 
			// Index schon festlegen
			int i=0; 
			// add one point to the minimum value in dataset (Changed from 0-> min because can be <0)
			if(settings.isUsesMinMax() && settings.isUsesMinAsInvisible()) {
				// Invisible or  White / Black
				// 0 and min to Invis
				paintScale.add(min, new Color(0, 0, 0, 0)); 
				paintScale.add(realmin, new Color(0, 0, 0, 0));
				i++;
			} // black or white asminimum if monochrome or if set
			else if(settings.isLODMonochrome() || (settings.isUsesWAsMin() && !settings.isInverted()) || (settings.isUsesBAsMax() && settings.isInverted()) || settings.isMonochrom()) { 
				// Black or white as start (also in monochroms)
				Color color = (settings.isInverted()? Color.BLACK : Color.WHITE);
				paintScale.add(min, color); 
				paintScale.add(realmin, color);  
				i++;
			}
			else {
				// normale Farbe als start
				float p = settings.isInverted()? 1:0;
				paintScale.add(min, interpolate(settings.getMinColor(), settings.getMaxColor(), p)); 
				paintScale.add(realmin, interpolate(settings.getMinColor(), settings.getMaxColor(), p));
				i++;
			}
			// adding color steps in middle 
			addColorsteps(realmin, realmax, settings, paintScale, i);
			// end
			if(max>settings.getMaxIAbs(min,max) && settings.isUsesMinMax() && settings.getMinIAbs(min,max)>0) {
				if((settings.isUsesWAsMin() && settings.isInverted()) || (settings.isUsesBAsMax() && !settings.isInverted()) || settings.isMonochrom()) { 
					// end to black or white
					Color color = (settings.isInverted()? Color.WHITE : Color.BLACK); 
					paintScale.add(max, color);  
					i++;
				}
				else // end color to real end color without BnW
					paintScale.add(max, settings.getMaxColor()); 
			}
			//
			return paintScale;  
	}

	// Unterschiedlicher PaintScales FOR LEGEND
	public static PaintScale generateStepPaintScaleForLegend(double min, double max, SettingsPaintScale settings) {   
		if(min==max) {
			LookupPaintScale paintScale = new LookupPaintScale(min,max,Color.lightGray); 
			paintScale.add(min, new Color(0, 0, 0, 0)); 
			paintScale.add(max, new Color(0, 0, 0, 0)); 
			return paintScale;
		}
		else {
			// Min und Max das festgelegt ist
			double realmin = ((settings.isUsesMinMax() || settings.isUsesMinMaxFromSelection()) ? settings.getMinIAbs(min,max) : min);
			double realmax = ((settings.getMaxIAbs(min,max)!=0 && (settings.isUsesMinMax() || settings.isUsesMinMaxFromSelection())) ? settings.getMaxIAbs(min,max) : max); 
			// 
			LookupPaintScale paintScale = new LookupPaintScale(realmin,realmax,Color.lightGray); 
			// Index schon festlegen
			addColorsteps(realmin, realmax, settings, paintScale, 0);
			//
			return paintScale; 
		}
	}
	// adding color steps to the middle
	private static void addColorsteps(double realmin, double realmax,  SettingsPaintScale settings, LookupPaintScale paintScale, int i) {
		// step width
		double step = (realmax-realmin)/(settings.getLevels()-1);
		// levels to LOD 
		double lod = settings.getLOD();
		if(settings.isLODMonochrome() && lod>realmin && lod<realmax) {
			int LODlevels = (int)((settings.getLOD()-realmin)/step);
			// monochrome to LOD
			for(; i<LODlevels; i++) { 
				double value = realmin+step*i;
				if(i==LODlevels-1) value = lod;
				float p = i/(LODlevels-1.0f)/2;
				// Invert? 
				if(settings.isInverted()) p = (1-p);
				float brightness = (1-p);
				paintScale.add(value, Color.getHSBColor(0, 0, brightness));
			}
			// HUE / Color
			for(; i<settings.getLevels(); i++) { 
				// current value
				double value = realmin+step*i;
				float p = (i-LODlevels)/(settings.getLevels()-LODlevels-1.0f);
				// Invert?
				if(settings.isInverted()) p = (1-p);
				// only color interpolate without BnW 
				paintScale.add(value, interpolate(settings.getMinColor(), settings.getMaxColor(), p));
			}
		}
		else {
			// adding steps to the middle
			for(; i<settings.getLevels(); i++) {
				// current value
				double value = realmin+step*i;
				float p = i/(settings.getLevels()-1.0f);
				// Invert?
				if(settings.isInverted()) p = (1-p);
				// 
				if(settings.isMonochrom()) { 
					// brightness and saturation 
					paintScale.add(value, interpolateMonochrom(settings.getMinColor(), p, 2.f, settings.isGrey()));
				} 
				else if(settings.isUsesBAsMax() || settings.isUsesWAsMin()) { 
					paintScale.add(value, interpolateWithBlackAndWhite(settings.getMinColor(), settings.getMaxColor(), p, settings.getBrightnessFactor(),settings.isUsesWAsMin(), settings.isUsesBAsMax())); 
				}
				else {
					// only color interpolate without BnW 
					paintScale.add(value, interpolate(settings.getMinColor(), settings.getMaxColor(), p));
				}
			} 
		}
	} 

	// grey
	public static PaintScale generateGreyPaintScale(double min, double max, SettingsPaintScale settings) { 
		double realmin = ((settings.isUsesMinMax()) ? settings.getMinIAbs(min,max) : min);
		double realmax = ((settings.getMaxIAbs(min,max)!=0 && settings.isUsesMinMax()) ? settings.getMaxIAbs(min,max) : max); 
		
		PaintScale scale = new GrayPaintScale(realmin, realmax); 
		return scale;
	}

	// without black and white!
	public static Color interpolate(Color start, Color end, float p) {
		float[] startHSB = Color.RGBtoHSB(start.getRed(), start.getGreen(), start.getBlue(), null);
		float[] endHSB = Color.RGBtoHSB(end.getRed(), end.getGreen(), end.getBlue(), null);

		float brightness = (startHSB[2] + endHSB[2]) / 2;
		float saturation = (startHSB[1] + endHSB[1]) / 2;

		float hueMax = 0;
		float hueMin = 0; 

		hueMin = startHSB[0];
		hueMax = endHSB[0];

		float hue = ((hueMax - hueMin) * p) + hueMin;

		return Color.getHSBColor(hue, saturation, brightness);
	}

	/*
	 * Determines what colour a heat map cell should be based upon the cell 
	 * values.
	 * with black and white
	 */
	private static Color interpolateWithBlackAndWhite(Color start, Color end, float p, float pSaturationBrightness, boolean white, boolean black) {		 
		// HSB
		float[] startHSB = Color.RGBtoHSB(start.getRed(), start.getGreen(), start.getBlue(), null);
		float[] endHSB = Color.RGBtoHSB(end.getRed(), end.getGreen(), end.getBlue(), null);
		// Saturation schnell hoch; Hue between lowHue and highHue --> auf 0 und 100%; Brightness am ende schnell runter;
		float saturation = 1;
		if(white) {
			saturation = p*pSaturationBrightness;
			if(saturation>1.f) saturation = 1.f;
		}
		// brightness
		float brightness = 1;
		if(black) {
			brightness = (1-p)*pSaturationBrightness;
			if(brightness>=1.f) 
				brightness=1.f;
			if(brightness<=0.f) 
				brightness=0.f;
		}
		
/*
		float hueMax = 0;
		float hueMin = 0; 
		hueMin = startHSB[0];
		hueMax = endHSB[0];

		float hue = ((hueMax - hueMin) * p) + hueMin;
		*/
		
		// hue range
		float hueRange = endHSB[0]-startHSB[0]; 
		
		// Test Huerange 
		int bw = 0;
		if(white) bw++;
		if(black) bw++;
		// reduce range by one or two sides ( black and white area)
		float cut = 1.2f/pSaturationBrightness;
		// new range from 0 to max
		float max = 1.f-cut;
		float realp = p-cut/bw;
		
		realp = realp<0? 0 : realp;
		realp = realp>1? 1 : realp;
		
		realp = (1.f)*realp/max;
		
		realp = realp<0? 0 : realp;
		realp = realp>1? 1 : realp;

		float hueMax = 0;
		float hueMin = 0; 
		hueMin = startHSB[0];
		hueMax = endHSB[0];

		float hue = ((hueMax - hueMin) * realp) + hueMin;
	
		// Zweiter Versuch: Color als Array
		//Color color[] = new Color{new Color(255,255,255),new Color(247,255,145),new Color(255,236,0),new Color(255,179,0),new Color(244,122,0),new Color(,,),new Color(,,),new Color(,,),};

		return Color.getHSBColor(hue, saturation, brightness);
	}


	/*
	 * Hue and saturation 
	 * white: hsb = -01
	 * black: hsb = -10
	 * color: hsb = ?11
	 * increasing saturation
	 */
	private static Color interpolateMonochrom(Color start, float p, float pSaturationBrightness, boolean isGrey) {		    
		if(isGrey) { 
			float brightness = (1-p);
			if(brightness>=1.f) brightness=1.f;
			if(brightness<=0.f) brightness=0.f; 
			return Color.getHSBColor(0, 0, brightness);
		}
		else { 
			// HSB
			float[] startHSB = Color.RGBtoHSB(start.getRed(), start.getGreen(), start.getBlue(), null);  
			// Saturation schnell hoch; Hue between lowHue and highHue --> auf 0 und 100%; Brightness am ende schnell runter;
			float saturation = p*pSaturationBrightness;
			if(saturation>1.f) saturation = 1.f;
			// brightness
			float brightness = (1-p)*pSaturationBrightness;
			if(brightness>=1.f) 
				brightness=1.f;
			if(brightness<=0.f) 
				brightness=0.f; 
			// hue
			float hue = startHSB[0];  
			
			return Color.getHSBColor(hue, saturation, brightness);
		}
	}

}
