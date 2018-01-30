package net.rs.lamsi.general.settings.image.visualisation.paintscales;

import java.awt.Paint;
import java.io.Serializable;
import org.jfree.chart.renderer.PaintScale;

public class SingleColorPaintScale implements PaintScale, Serializable {

  private static final long serialVersionUID = 1L;

  private double lowerBound = -1, upperBound = -1;
  private Paint color;

  public SingleColorPaintScale(double lowerBound, double upperBound, Paint color) {
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
    this.color = color;
  }

  @Override
  public double getLowerBound() {
    return lowerBound;
  }

  @Override
  public double getUpperBound() {
    return upperBound;
  }

  @Override
  public Paint getPaint(double value) {
    return color;
  }

}
