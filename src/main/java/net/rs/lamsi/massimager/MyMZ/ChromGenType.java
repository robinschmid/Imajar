package net.rs.lamsi.massimager.MyMZ;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.Range;
import net.sf.mzmine.datamodel.DataPoint;
import net.sf.mzmine.datamodel.Scan;

public abstract class ChromGenType {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  // Highest Peak
  public static ChromGenType HIGHEST_PEAK = new ChromGenType() {
    @Override
    public double getIntensity(double mz, double pm, Scan spec) {
      try {
        DataPoint[] dps = spec.getDataPointsByMass(Range.<Double>closed(mz - pm, mz + pm));
        DataPoint mostIntense = null;
        for (int i = 0; i < dps.length; i++) {
          DataPoint d = dps[i];
          if (mostIntense == null || mostIntense.getIntensity() < d.getIntensity())
            mostIntense = d;
        }
        return mostIntense.getIntensity();
      } catch (Exception ex) {
        return 0;
      }
    }
  };

  // Summe
  public static ChromGenType SUM_PEAKS = new ChromGenType() {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public double getIntensity(double mz, double pm, Scan spec) {
      try {
        DataPoint[] dps = spec.getDataPointsByMass(Range.<Double>closed(mz - pm, mz + pm));
        double sum = 0;
        for (int i = 0; i < dps.length; i++) {
          DataPoint d = dps[i];
          sum += d.getIntensity();
        }
        return sum;
      } catch (Exception ex) {
        logger.error("", ex);
      }
      return 0;
    }
  };


  // Inhalt
  public abstract double getIntensity(double mz, double pm, Scan spec);
}
