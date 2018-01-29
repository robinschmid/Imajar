package net.rs.lamsi.general.settings.image.visualisation;

import java.awt.Paint;
import java.io.Serializable;

public class PaintItem implements Serializable {

  private static final long serialVersionUID = 1L;
  private Paint paint;
  private double value;

  public PaintItem(Paint paint, double value) {
    super();
    this.paint = paint;
    this.value = value;
  }

  public Paint getPaint() {
    return paint;
  }

  public void setPaint(Paint paint) {
    this.paint = paint;
  }

  public double getValue() {
    return value;
  }

  public void setValue(double value) {
    this.value = value;
  }
}
