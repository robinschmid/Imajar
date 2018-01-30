package net.rs.lamsi.general.settings.image.visualisation.paintscales;

import java.awt.Color;
import java.awt.Paint;
import java.io.Serializable;
import org.jfree.chart.HashUtils;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.util.PublicCloneable;

public class GreyPaintScale implements PaintScale, PublicCloneable, Serializable {

  /** The lower bound. */
  private double lowerBound;

  /** The upper bound. */
  private double upperBound;

  private double start, end;

  //
  private Color[] color;

  /**
   * The alpha transparency (0-255).
   *
   * @since 1.0.13
   */
  private int alpha;

  /**
   * Creates a new {@code GrayPaintScale} instance with default values.
   */
  public GreyPaintScale() {
    this(0.0, 1.0);
  }

  /**
   * Creates a new paint scale for values in the specified range.
   *
   * @param lowerBound the lower bound.
   * @param upperBound the upper bound.
   *
   * @throws IllegalArgumentException if {@code lowerBound} is not less than {@code upperBound}.
   */
  public GreyPaintScale(double lowerBound, double upperBound) {
    this(lowerBound, upperBound, 255);
  }

  /**
   * Creates a new paint scale for values in the specified range.
   *
   * @param lowerBound the lower bound.
   * @param upperBound the upper bound.
   * @param start brightness (0 black, 1 white, 0.5 50% grey .. )
   * @param end brightness (0 black, 1 white, 0.5 50% grey .. )
   *
   * @throws IllegalArgumentException if {@code lowerBound} is not less than {@code upperBound}.
   */
  public GreyPaintScale(double lowerBound, double upperBound, double start, double end) {
    this(lowerBound, upperBound, 255, start, end);
  }

  /**
   * Creates a new paint scale for values in the specified range.
   *
   * @param lowerBound the lower bound.
   * @param upperBound the upper bound.
   * @param alpha the alpha transparency (0-255).
   *
   * @throws IllegalArgumentException if {@code lowerBound} is not less than {@code upperBound}, or
   *         {@code alpha} is not in the range 0 to 255.
   *
   * @since 1.0.13
   */
  public GreyPaintScale(double lowerBound, double upperBound, int alpha) {
    this(lowerBound, upperBound, alpha, 1, 0);
  }

  /**
   * Creates a new paint scale for values in the specified range.
   *
   * @param lowerBound the lower bound.
   * @param upperBound the upper bound.
   * @param alpha the alpha transparency (0-255).
   * @param start brightness (0 black, 1 white, 0.5 50% grey .. )
   * @param end brightness (0 black, 1 white, 0.5 50% grey .. )
   *
   * @throws IllegalArgumentException if {@code lowerBound} is not less than {@code upperBound}, or
   *         {@code alpha} is not in the range 0 to 255.
   *
   * @since 1.0.13
   */
  public GreyPaintScale(double lowerBound, double upperBound, int alpha, double start, double end) {
    if (lowerBound >= upperBound) {
      throw new IllegalArgumentException("Requires lowerBound < upperBound.");
    }
    if (alpha < 0 || alpha > 255) {
      throw new IllegalArgumentException("Requires alpha in the range 0 to 255.");

    }
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
    this.alpha = alpha;
    this.start = start;
    this.end = end;
    //
    int steps = (int) (256 * Math.abs(start - end));
    color = new Color[steps];
  }

  /**
   * Returns the lower bound.
   *
   * @return The lower bound.
   *
   * @see #getUpperBound()
   */
  @Override
  public double getLowerBound() {
    return this.lowerBound;
  }

  /**
   * Returns the upper bound.
   *
   * @return The upper bound.
   *
   * @see #getLowerBound()
   */
  @Override
  public double getUpperBound() {
    return this.upperBound;
  }

  public double getStart() {
    return start;
  }

  public double getEnd() {
    return end;
  }

  /**
   * Returns the alpha transparency that was specified in the constructor.
   * 
   * @return The alpha transparency (in the range 0 to 255).
   * 
   * @since 1.0.13
   */
  public int getAlpha() {
    return this.alpha;
  }

  /**
   * Returns a paint for the specified value.
   *
   * @param value the value (must be within the range specified by the lower and upper bounds for
   *        the scale).
   *
   * @return A paint for the specified value.
   */
  @Override
  public Paint getPaint(double value) {
    double v = Math.max(value, this.lowerBound);
    v = Math.min(v, this.upperBound);
    int g = (int) ((v - this.lowerBound) / (this.upperBound - this.lowerBound) * 255.0
        * Math.abs(start - end));
    // lazy initialisation
    if (color[g] == null)
      color[g] = new Color(g, g, g, this.alpha);
    return color[g];
  }

  /**
   * Tests this {@code GrayPaintScale} instance for equality with an arbitrary object. This method
   * returns {@code true} if and only if:
   * <ul>
   * <li>{@code obj} is not {@code null};</li>
   * <li>{@code obj} is an instance of {@code GrayPaintScale};</li>
   * </ul>
   *
   * @param obj the object ({@code null} permitted).
   *
   * @return A boolean.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof GreyPaintScale)) {
      return false;
    }
    GreyPaintScale that = (GreyPaintScale) obj;
    if (this.lowerBound != that.lowerBound) {
      return false;
    }
    if (this.upperBound != that.upperBound) {
      return false;
    }
    if (this.alpha != that.alpha) {
      return false;
    }
    if (this.start != that.start) {
      return false;
    }
    if (this.end != that.end) {
      return false;
    }

    return true;
  }

  /**
   * Returns a hash code for this instance.
   *
   * @return A hash code.
   */
  @Override
  public int hashCode() {
    int hash = 7;
    hash = HashUtils.hashCode(hash, this.lowerBound);
    hash = HashUtils.hashCode(hash, this.upperBound);
    hash = 43 * hash + this.alpha;
    return hash;
  }

  /**
   * Returns a clone of this {@code GrayPaintScale} instance.
   *
   * @return A clone.
   *
   * @throws CloneNotSupportedException if there is a problem cloning this instance.
   */
  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}
