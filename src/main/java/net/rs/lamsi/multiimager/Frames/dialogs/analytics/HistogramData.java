package net.rs.lamsi.multiimager.Frames.dialogs.analytics;

import java.util.function.Supplier;
import org.jfree.data.Range;
import net.rs.lamsi.utils.math.DoubleArraySupplier;

public class HistogramData {
  private double[] data;
  private Range range;


  public HistogramData(double[] data, Range range) {
    this.data = data;
    this.range = range;
  }

  public HistogramData(double[] data, double min, double max) {
    this.data = data;
    this.range = new Range(min, max);
  }

  public HistogramData(double[] data) {
    this.data = data;
    findRange();
  }

  public HistogramData(DoubleArraySupplier data, Supplier<Range> range) {
    this(data.get(), range.get());
    this.data = data.get();
  }

  public HistogramData(DoubleArraySupplier data) {
    this(data.get());
  }

  public double[] getData() {
    return data;
  }

  public Range getRange() {
    if (range == null)
      findRange();
    if (range == null)
      return new Range(0, 0);
    return range;
  }

  protected void setRange(Range range) {
    this.range = range;
  }

  private void findRange() {
    if (data != null && data.length > 0) {
      double min = data[0];
      double max = data[0];
      for (int i = 1; i < data.length; i++) {
        if (data[i] > max)
          max = data[i];
        if (data[i] < min)
          min = data[i];
      }
      setRange(new Range(min, max));
    }
  }

  public double size() {
    return data != null ? data.length : 0;
  }
}
