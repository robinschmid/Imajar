package net.rs.lamsi.general.heatmap;

import java.awt.Color;
import java.util.List;
import org.jfree.chart.renderer.GrayPaintScale;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.PaintScale;
import net.rs.lamsi.general.settings.image.visualisation.SettingsPaintScale;


public class PaintScaleGenerator {
  private static final Color transparent = new Color(0, 0, 0, 0);



  // old version PaintScales
  public static PaintScale generateStepPaintScale(double min, double max, double promin,
      double promax, Color cmin, Color cmax, int stepCount) {
    // Min ist pormin von max
    double realmin = max * promin;
    double realmax = max * promax;
    //
    LookupPaintScale paintScale = new LookupPaintScale(min, max, Color.lightGray);
    // Bei null den min Wert hinzufügen
    paintScale.add(0, cmin);
    //
    for (int i = 0; i < stepCount; i++) {
      double value = realmin + realmax / (stepCount - 1) * i;
      paintScale.add(value, interpolate(cmin, cmax, i / (stepCount - 1.0f)));
    }
    //
    paintScale.add(max, cmax);
    //
    return paintScale;
  }

  // new version PaintScales
  /**
   * adds the first and last color step
   * 
   * @param min z value in dataset
   * @param max z value in dataset
   * @param settings
   * @return
   */
  public static PaintScale generateStepPaintScale(double min, double max,
      SettingsPaintScale settings) {
    if (max < min)
      min = max - 0.000001;
    // real min and max values as given by minz or min in settings
    double realmin = ((settings.isUsesMinMax() || settings.isUsesMinMaxFromSelection())
        ? settings.getMinIAbs(min, max)
        : min);
    double realmax = ((settings.getMaxIAbs(min, max) != 0
        && (settings.isUsesMinMax() || settings.isUsesMinMaxFromSelection()))
            ? settings.getMaxIAbs(min, max)
            : max);

    // generate list paint scale
    if (settings.isListScale()) {
      return generateColorListPaintScale(min, max, realmin, realmax, settings.getColorList(),
          settings.isInverted(), settings.isUsesMinAsInvisible(), settings.isUsesMaxAsInvisible(),
          settings.getLevels(), false);
    } else if (settings.isGrey()) {
      //
      return generateGreyscale(min, max, settings, false);
    } else {
      boolean isHue = settings.isHueScale();
      boolean isList = settings.isListScale();

      // error when min == max
      if (realmax <= realmin) {
        realmin = realmax - 0.001;
      }
      // with minimum and maximum bounds
      LookupPaintScale paintScale = new LookupPaintScale(min, max, Color.lightGray);
      // Index
      int i = 0;
      // add one point to the minimum value in dataset (Changed from 0-> min because can be <0)
      Color firstc = null;
      Color lastc = null;
      // Invisible or White / Black or min color
      // color as min for one sided monochrome
      if (settings.isMonochrom() && (settings.isUsesWAsMin() ^ settings.isUsesBAsMax())) {
        Color c = settings.getMinColor();
        firstc = settings.isInverted() ? (settings.isUsesBAsMax() ? Color.BLACK : c)
            : (settings.isUsesWAsMin() ? Color.WHITE : c);
        lastc = !settings.isInverted() ? (settings.isUsesBAsMax() ? Color.BLACK : c)
            : (settings.isUsesWAsMin() ? Color.WHITE : c);
      } else {
        // Black or white as start (also in monochroms)
        if (settings.isLODMonochrome() || settings.isMonochrom()
            || (settings.isUsesWAsMin() && !settings.isInverted())
            || (settings.isUsesBAsMax() && settings.isInverted()))
          firstc = settings.isInverted() ? Color.BLACK : Color.WHITE;
        // min/max: inverted color as start
        else if (isHue)
          firstc = interpolate(settings.getMinColor(), settings.getMaxColor(),
              settings.isInverted() ? 1 : 0);
        else if (isList)
          firstc = settings.getListColor(0);

        // last color
        if (settings.isMonochrom() || (settings.isUsesWAsMin() && settings.isInverted())
            || (settings.isUsesBAsMax() && !settings.isInverted()))
          lastc = !settings.isInverted() ? Color.BLACK : Color.WHITE;
        // min/max: inverted color as start
        else if (isHue)
          lastc = interpolate(settings.getMinColor(), settings.getMaxColor(),
              settings.isInverted() ? 0 : 1);
        else if (isList)
          firstc = settings.getListColor(1);
      }

      // 0 and min to Invis
      if (settings.isUsesMinMax() && settings.isUsesMinAsInvisible())
        firstc = transparent;
      if (settings.isUsesMinMax() && settings.isUsesMaxAsInvisible())
        lastc = transparent;

      // add first two color steps
      paintScale.add(min, firstc);
      paintScale.add(realmin, firstc);
      i++;

      // adding color steps in middle
      if (isList) {
        int size = settings.getListColorSize();
        addListColorSteps(realmin, realmax, settings, paintScale, i, size);
      } else
        addColorsteps(realmin, realmax, settings, paintScale, i);

      // add end color step
      if (max > settings.getMaxIAbs(min, max) && settings.isUsesMinMax()) {
        paintScale.add(realmax + Double.MIN_VALUE, lastc);
        paintScale.add(max, lastc);
      }

      //
      return paintScale;
    }
  }


  private static double calcRealMin(double min, double max, SettingsPaintScale settings) {
    return ((settings.isUsesMinMax() || settings.isUsesMinMaxFromSelection())
        ? settings.getMinIAbs(min, max)
        : min);
  }

  private static double calcRealMax(double min, double max, SettingsPaintScale settings) {
    return ((settings.getMaxIAbs(min, max) != 0
        && (settings.isUsesMinMax() || settings.isUsesMinMaxFromSelection()))
            ? settings.getMaxIAbs(min, max)
            : max);
  }

  public static PaintScale generateColorListPaintScale(double min, double max,
      SettingsPaintScale settings, boolean forLegend) {
    if (max < min)
      min = max - 0.000001;
    // real min and max values as given by minz or min in settings
    double realmin = calcRealMin(min, max, settings);
    double realmax = calcRealMax(min, max, settings);

    // generate list paint scale
    return generateColorListPaintScale(min, max, realmin, realmax, settings.getColorList(),
        settings.isInverted(), settings.isUsesMinAsInvisible(), settings.isUsesMaxAsInvisible(),
        settings.getLevels(), forLegend);
  }


  public static PaintScale generateGreyscale(double min, double max, SettingsPaintScale settings,
      boolean forLegend) {
    if (max < min)
      min = max - 0.000001;
    // real min and max values as given by minz or min in settings
    double realmin = calcRealMin(min, max, settings);
    double realmax = calcRealMax(min, max, settings);

    // generate list paint scale
    return generateGreyscale(min, max, realmin, realmax, settings.isInverted(),
        settings.isUsesMinAsInvisible(), settings.isUsesMaxAsInvisible(), settings.getLevels(),
        forLegend);
  }

  /**
   * 
   * @param min the absolute data minimum
   * @param max the absolute data maximum
   * @param realmin the paintscale minimum
   * @param realmax the paintscale maximum
   * @param list colors
   * @param isInverted invert colours
   * @param firstTransparent values < realmin are transparent
   * @param lastTransparent values > realmax are transparent
   * @param steps how many steps
   * @return
   */
  public static PaintScale generateGreyscale(double min, double max, double realmin, double realmax,
      boolean isInverted, boolean firstTransparent, boolean lastTransparent, int steps,
      boolean forLegend) {
    if ((max <= min) || (realmax <= realmin)) {
      // no real data
      return generateEmptyScale();
    } else {
      // with minimum and maximum bounds
      double lower = min;
      double upper = max;
      if (forLegend) {
        lower = realmin;
        upper = realmax;
      }
      LookupPaintScale paintScale = new LookupPaintScale(lower, upper, Color.lightGray);
      Color c = null;
      if (firstTransparent)
        c = transparent;
      else {
        float i = isInverted ? 0 : 1;
        c = new Color(i, i, i);
      }
      paintScale.add(Double.NEGATIVE_INFINITY, c);
      paintScale.add(realmin - Double.MIN_VALUE, c);


      // add list
      for (int i = 1; i < steps; i++) {
        float p = i / (float) (steps - 1.f);
        double v = (float) (realmin + (realmax - realmin) * p);
        if (!isInverted)
          p = 1.f - p;
        c = new Color(p, p, p);
        paintScale.add(v, c);
      }

      // add one point to the minimum value in dataset (Changed from 0-> min because can be <0)
      if (lastTransparent)
        c = transparent;
      else {
        float i = isInverted ? 1 : 0;
        c = new Color(i, i, i);
      }
      paintScale.add(realmax + Double.MIN_VALUE, c);
      paintScale.add(Double.MAX_VALUE, c);

      //
      return paintScale;
    }
  }


  /**
   * 
   * @param min the absolute data minimum
   * @param max the absolute data maximum
   * @param realmin the paintscale minimum
   * @param realmax the paintscale maximum
   * @param list colors
   * @param isInverted invert colours
   * @param firstTransparent values < realmin are transparent
   * @param lastTransparent values > realmax are transparent
   * @param steps how many steps
   * @return
   */
  public static PaintScale generateColorListPaintScale(double min, double max, double realmin,
      double realmax, List<Color> list, boolean isInverted, boolean firstTransparent,
      boolean lastTransparent, int steps, boolean forLegend) {
    if ((max <= min) || (realmax <= realmin)) {
      // no real data
      return generateEmptyScale();
    } else {
      int size = list.size();
      // with minimum and maximum bounds
      double lower = min;
      double upper = max;
      if (forLegend) {
        lower = realmin;
        upper = realmax;
      }
      LookupPaintScale paintScale = new LookupPaintScale(lower, upper, Color.lightGray);
      Color c = null;
      if (firstTransparent)
        c = transparent;
      else {
        // non inverted
        int i = 0;
        if (isInverted)
          i = steps < size ? steps - 1 : size - 1;
        c = list.get(i);
      }
      paintScale.add(Double.NEGATIVE_INFINITY, c);
      paintScale.add(realmin - Double.MIN_VALUE, c);


      // add list
      int rsteps = Math.min(steps, size);
      for (int i = 0; i < rsteps; i++) {
        double v = realmin + (realmax - realmin) * i / (rsteps);
        c = list.get(isInverted ? size - 1 - i : i);
        paintScale.add(v, c);
      }

      // add one point to the minimum value in dataset (Changed from 0-> min because can be <0)
      if (lastTransparent)
        c = transparent;
      paintScale.add(realmax + Double.MIN_VALUE, c);
      paintScale.add(Double.MAX_VALUE, c);

      //
      return paintScale;
    }
  }

  private static PaintScale generateEmptyScale() {
    // TODO Auto-generated method stub
    return null;
  }

  private static void addListColorSteps(double realmin, double realmax, SettingsPaintScale settings,
      LookupPaintScale paintScale, int i, int size) {
    double s = size;
    Color c = settings.getListColor(i / s);
    double value = realmin + (realmax - realmin) * i / s;
    paintScale.add(value, c);
    if (i < size - 1)
      addListColorSteps(realmin, realmax, settings, paintScale, i + 1, size);
  }

  // PaintScales FOR LEGEND
  public static PaintScale generateStepPaintScaleForLegend(double min, double max,
      SettingsPaintScale settings) {
    if (min == max) {
      LookupPaintScale paintScale = new LookupPaintScale(min, max, Color.lightGray);
      paintScale.add(min, new Color(0, 0, 0, 0));
      paintScale.add(max, new Color(0, 0, 0, 0));
      return paintScale;
    } else {
      if (settings.isListScale()) {
        return generateColorListPaintScale(min, max, settings, true);
      } else if (settings.isGrey()) {
        return generateGreyscale(min, max, settings, true);
      } else {
        // set min and max
        double realmin = ((settings.isUsesMinMax() || settings.isUsesMinMaxFromSelection())
            ? settings.getMinIAbs(min, max)
            : min);
        double realmax = ((settings.getMaxIAbs(min, max) != 0
            && (settings.isUsesMinMax() || settings.isUsesMinMaxFromSelection()))
                ? settings.getMaxIAbs(min, max)
                : max);
        //
        LookupPaintScale paintScale = new LookupPaintScale(realmin, realmax, Color.lightGray);
        // set index to 0

        boolean isList = settings.isListScale();
        int size = settings.getListColorSize();
        if (isList)
          addListColorSteps(realmin, realmax, settings, paintScale, 0, size);
        else
          addColorsteps(realmin, realmax, settings, paintScale, 0);
        //
        return paintScale;
      }
    }
  }

  /**
   * adds color steps to the middle
   * 
   * @param realmin
   * @param realmax
   * @param settings
   * @param paintScale
   * @param i
   */
  private static void addColorsteps(double realmin, double realmax, SettingsPaintScale settings,
      LookupPaintScale paintScale, int i) {
    // step width
    double step = (realmax - realmin) / (settings.getLevels() - 1);
    // levels to LOD
    double lod = settings.getLOD();
    if (settings.isLODMonochrome() && lod > realmin && lod < realmax) {
      int LODlevels = (int) ((settings.getLOD() - realmin) / step);
      // monochrome to LOD
      for (; i < LODlevels; i++) {
        double value = realmin + step * i;
        if (i == LODlevels - 1)
          value = lod;
        float p = i / (LODlevels - 1.0f) / 4 * 3;
        // Invert?
        if (settings.isInverted())
          p = (1 - p);
        float brightness = (1 - p);
        paintScale.add(value, Color.getHSBColor(0, 0, brightness));
      }

      // HUE / Color
      for (; i < settings.getLevels(); i++) {
        // current value
        double value = realmin + step * i;
        float p = (i - LODlevels) / (settings.getLevels() - LODlevels - 1.0f);
        // Invert?
        if (settings.isInverted())
          p = (1 - p);
        // only color interpolate without BnW
        paintScale.add(value, interpolateWeighted(settings.getMinColor(), settings.getMaxColor(), p,
            settings.getHue(), settings.getPosition(), settings.isInverted()));
      }
    } else {
      // uses black or white not both and is monochrome
      boolean monochromeOneSided =
          settings.isMonochrom() && ((settings.isUsesBAsMax() ^ settings.isUsesWAsMin()));

      // adding steps to the middle
      for (; i < settings.getLevels(); i++) {
        // current value
        double value = realmin + step * i;
        float p = i / (settings.getLevels() - 1.0f);
        // Invert?
        if (settings.isInverted())
          p = (1.f - p);
        // Monochrome two sided
        if (settings.isMonochrom()) {
          // brightness and saturation
          if (monochromeOneSided)
            paintScale.add(value,
                interpolateMonochromOneSided(settings.getMinColor(), p, settings.isUsesBAsMax()));
          else
            paintScale.add(value,
                interpolateMonochrom(settings.getMinColor(), p, 2.f, settings.isGrey()));
        } else if (settings.isUsesBAsMax() || settings.isUsesWAsMin()) {
          paintScale.add(value,
              interpolateWithBlackAndWhiteWeighted(settings.getMinColor(), settings.getMaxColor(),
                  p, settings.getBrightnessFactor(), settings.isUsesWAsMin(),
                  settings.isUsesBAsMax(), settings.getHue(), settings.getPosition(),
                  settings.isInverted()));
        } else {
          // only color interpolate without BnW
          paintScale.add(value, interpolateWeighted(settings.getMinColor(), settings.getMaxColor(),
              p, settings.getHue(), settings.getPosition(), settings.isInverted()));
        }
      }
    }
  }

  // grey
  public static PaintScale generateGreyPaintScale(double min, double max,
      SettingsPaintScale settings) {
    double realmin = ((settings.isUsesMinMax()) ? settings.getMinIAbs(min, max) : min);
    double realmax = ((settings.getMaxIAbs(min, max) != 0 && settings.isUsesMinMax())
        ? settings.getMaxIAbs(min, max)
        : max);

    PaintScale scale = new GrayPaintScale(realmin, realmax);
    return scale;
  }


  /**
   * interpolate with weights for specified hue values
   * 
   * @param start (real starting color
   * @param end (real ending color)
   * @param p
   * @param hue
   * @param position
   * @return
   */
  public static Color interpolateWeighted(Color start, Color end, float p, float[] hue,
      float[] position, boolean invertedPos) {
    float[] startHSB = Color.RGBtoHSB(start.getRed(), start.getGreen(), start.getBlue(), null);
    float[] endHSB = Color.RGBtoHSB(end.getRed(), end.getGreen(), end.getBlue(), null);

    float brightness = (startHSB[2] + endHSB[2]) / 2;
    float saturation = (startHSB[1] + endHSB[1]) / 2;

    float hueMin = startHSB[0];
    float hueMax = endHSB[0];

    float p0 = 0.f;
    float p1 = 1.f;

    // between which position?
    // start .... hue[0] .. hue[1] .. hue[n] ... end
    if (position != null && position.length > 0) {
      if (invertedPos) {
        int s = position.length;
        if (p < 1.f - position[s - 1]) {
          hueMax = hue[s - 1];
          p1 = 1.f - position[s - 1];
        } else {
          for (int i = 1; i < s; i++) {
            float pos = 1.f - position[s - 1 - i];
            if (p <= pos) {
              hueMin = hue[s - 1 - i];
              hueMax = hue[s - i];
              p0 = pos;
              p1 = 1.f - position[s - i];
              break;
            }
          }
          // end step
          if (p0 == 0.f) {
            p0 = 1.f - position[0];
            hueMin = hue[0];
          }
        }
      } else {
        if (p < position[0]) {
          hueMax = hue[0];
          p1 = position[0];
        } else {
          int max = position.length;
          for (int i = 1; i < max; i++) {
            if (p <= position[i]) {
              i--;
              hueMin = hue[i];
              hueMax = hue[i + 1];
              p0 = position[i];
              p1 = position[i + 1];
              break;
            }
          }
          // end step
          if (p0 == 0.f) {
            p0 = position[position.length - 1];
            hueMin = hue[hue.length - 1];
          }
        }
      }
    }

    float newp = (p - p0) / (p1 - p0);

    float H = ((hueMax - hueMin) * newp) + hueMin;

    // TODO add brightness and saturation modifiers
    // brightness = 1.f - 0.25f/10.f*pb;
    // saturation = 1.f - 0.25f/10.f*pb;

    return Color.getHSBColor(H, saturation, brightness);
  }

  /**
   * interpolate without black and white as min/max
   * 
   * @param start
   * @param end
   * @param p
   * @return
   */
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

    // TODO add brightness and saturation modifiers
    // brightness = 1.f - 0.25f/10.f*pb;
    // saturation = 1.f - 0.25f/10.f*pb;

    return Color.getHSBColor(hue, saturation, brightness);
  }

  /**
   * interpolate with option for black and white at the end
   * 
   * @param start
   * @param end
   * @param p
   * @param pSaturationBrightness
   * @param white
   * @param black
   * @param hue
   * @param position
   * @return
   */
  private static Color interpolateWithBlackAndWhiteWeighted(Color start, Color end, float p,
      float pSaturationBrightness, boolean white, boolean black, float[] hue, float[] position,
      boolean inverted) {
    // pSaturationBrightness as position = inverse
    float posBS = 1.f / pSaturationBrightness;

    // HSB
    float[] startHSB = Color.RGBtoHSB(start.getRed(), start.getGreen(), start.getBlue(), null);
    float[] endHSB = Color.RGBtoHSB(end.getRed(), end.getGreen(), end.getBlue(), null);

    // Saturation rises; Hue between lowHue and highHue; Brightness falls at the end;
    // white?
    if (white && p <= posBS) {
      float B = 1;
      float H = startHSB[0];
      // p between 0-posBS
      float S = p / posBS;
      if (S > 1.f)
        S = 1.f;

      return Color.getHSBColor(H, S, B);
    }

    // black?
    else if (black && p >= 1.f - posBS) {
      float S = 1;
      float H = endHSB[0];
      // p between 0-posBS
      float B = (1.f - p) / posBS;
      if (B > 1.f)
        B = 1.f;

      return Color.getHSBColor(H, S, B);

    }

    // weighted hue scale
    else {
      //
      float realp = white ? p - posBS : p;
      float width = 1.f;
      if (white)
        width -= posBS;
      if (black)
        width -= posBS;

      realp = realp / width;

      return interpolateWeighted(start, end, realp, hue, position, inverted);
    }
  }

  /*
   * Determines what colour a heat map cell should be based upon the cell values. with black and
   * white
   */
  private static Color interpolateWithBlackAndWhite(Color start, Color end, float p,
      float pSaturationBrightness, boolean white, boolean black) {
    // HSB
    float[] startHSB = Color.RGBtoHSB(start.getRed(), start.getGreen(), start.getBlue(), null);
    float[] endHSB = Color.RGBtoHSB(end.getRed(), end.getGreen(), end.getBlue(), null);
    // Saturation schnell hoch; Hue between lowHue and highHue --> auf 0 und 100%; Brightness am
    // ende schnell runter;
    float saturation = 1;
    if (white) {
      saturation = p * pSaturationBrightness;
      if (saturation > 1.f)
        saturation = 1.f;
    }
    // brightness
    float brightness = 1;
    if (black) {
      brightness = (1 - p) * pSaturationBrightness;
      if (brightness >= 1.f)
        brightness = 1.f;
      if (brightness <= 0.f)
        brightness = 0.f;
    }

    /*
     * float hueMax = 0; float hueMin = 0; hueMin = startHSB[0]; hueMax = endHSB[0];
     * 
     * float hue = ((hueMax - hueMin) * p) + hueMin;
     */

    // Test Huerange
    int bw = 0;
    if (white)
      bw++;
    if (black)
      bw++;
    // reduce range by one or two sides ( black and white area)
    float cut = 1.2f / pSaturationBrightness;
    // new range from 0 to max
    float max = 1.f - cut;
    float realp = p - cut / bw;

    if (realp < 0)
      realp = 0;
    if (realp > 1)
      realp = 1;

    realp = (1.f) * realp / max;

    if (realp < 0)
      realp = 0;
    if (realp > 1)
      realp = 1;

    // old style
    float hueMax = 0;
    float hueMin = 0;
    hueMin = startHSB[0];
    hueMax = endHSB[0];

    float hue = ((hueMax - hueMin) * realp) + hueMin;

    // Zweiter Versuch: Color als Array
    // Color color[] = new Color{new Color(255,255,255),new Color(247,255,145),new
    // Color(255,236,0),new Color(255,179,0),new Color(244,122,0),new Color(,,),new Color(,,),new
    // Color(,,),};

    return Color.getHSBColor(hue, saturation, brightness);
  }


  /*
   * Hue and saturation white: hsb = -01 black: hsb = -10 color: hsb = ?11 increasing saturation
   */
  private static Color interpolateMonochrom(Color start, float p, float pSaturationBrightness,
      boolean isGrey) {
    if (isGrey) {
      float brightness = (1 - p);
      if (brightness >= 1.f)
        brightness = 1.f;
      if (brightness <= 0.f)
        brightness = 0.f;
      return Color.getHSBColor(0, 0, brightness);
    } else {
      // HSB
      float[] startHSB = Color.RGBtoHSB(start.getRed(), start.getGreen(), start.getBlue(), null);
      // Saturation schnell hoch; Hue between lowHue and highHue --> auf 0 und 100%; Brightness am
      // ende schnell runter;
      float saturation = p * pSaturationBrightness;
      if (saturation > 1.f)
        saturation = 1.f;
      // brightness
      float brightness = (1 - p) * pSaturationBrightness;
      if (brightness >= 1.f)
        brightness = 1.f;
      if (brightness <= 0.f)
        brightness = 0.f;
      // hue
      float hue = startHSB[0];

      return Color.getHSBColor(hue, saturation, brightness);
    }
  }

  /*
   * Hue and saturation white: hsb = -01 black: hsb = -10 color: hsb = ?11 increasing saturation
   */
  private static Color interpolateMonochromOneSided(Color start, float p, boolean black) {
    // HSB
    float[] startHSB = Color.RGBtoHSB(start.getRed(), start.getGreen(), start.getBlue(), null);
    // Saturation schnell hoch; Hue between lowHue and highHue --> auf 0 und 100%; Brightness am
    // ende schnell runter;
    float saturation = 1;
    // brightness
    float brightness = (1 - p);
    if (brightness >= 1.f)
      brightness = 1.f;
    if (brightness <= 0.f)
      brightness = 0.f;

    // hue
    float hue = startHSB[0];

    if (black)
      return Color.getHSBColor(hue, saturation, brightness);
    else
      return Color.getHSBColor(hue, 1 - brightness, 1);
  }

}
