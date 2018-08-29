package net.rs.lamsi.test;

import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import net.rs.lamsi.general.datamodel.image.SingleParticleImage;
import net.rs.lamsi.general.datamodel.image.TestImageFactory;

public class TestSingleParticle {
  private final int intensity = 500;
  private final int lines = 10;
  private final int dp = 200;
  private final int particles = 6;
  private final int noise = 50;
  private SingleParticleImage img, imgSplit4;
  private double[][] data, dataSplit4;
  private double[] datasel, dataselSplit4;


  @Before
  public void setUp() throws Exception {
    // lines, dp per line, nano particles per line, noise, intensity
    img = TestImageFactory.createPerfectSingleParticleImg(lines, dp, particles, noise, intensity, 2,
        false);
    data = img.updateFilteredDataCountsArray();
    datasel = img.getSPDataArraySelected();

    imgSplit4 = TestImageFactory.createPerfectSingleParticleImg(lines, dp, particles, noise,
        intensity, 4, false);
    dataSplit4 = imgSplit4.updateFilteredDataCountsArray();
    dataselSplit4 = imgSplit4.getSPDataArraySelected();
  }

  @Test
  public void testToXYCountsArray() {
    // data is 0 or 1 (1=particle)
    int sum = Arrays.stream(data).flatMapToDouble(a -> Arrays.stream(a))
        .mapToInt(v -> v > 0.9 && v < 1.1 ? 1 : 0).sum();
    assertEquals("Number of particles was not correct", sum, particles * lines);

    // for split pixel 4
    sum = Arrays.stream(dataSplit4).flatMapToDouble(a -> Arrays.stream(a))
        .mapToInt(v -> v > 0.9 && v < 1.1 ? 1 : 0).sum();
    assertEquals("Number of particles for split pixel 4 was not correct", sum, particles * lines);

    for (int i = 0; i < lines; i++) {
      assertEquals("Position of particle in data is wrong. line " + i, 1, (int) data[i][i]);
      assertEquals("Position of last particle in data is wrong. line " + i, 1,
          (int) data[i][dp - 2 - i]);

      assertEquals("Position of particle in data (split pixel 4) is wrong. line " + i, 1,
          (int) data[i][i]);
      assertEquals("Position of last particle in data (split pixel 4) is wrong. line " + i, 1,
          (int) dataSplit4[i][dp - 2 - i]);
    }
  }

  @Test
  public void testSPDataArraySelected() {
    // data is split pixel corrected
    int sum = (int) Arrays.stream(datasel).mapToInt(v -> v > noise + 1 ? 1 : 0).sum();
    assertEquals("Number of particles was not correct", sum, particles * lines);

    // for split pixel 4
    sum = (int) Arrays.stream(dataselSplit4).mapToInt(v -> v > noise + 1 ? 1 : 0).sum();
    assertEquals("Number of particles for split pixel 4 was not correct", sum, particles * lines);
  }

  @After
  public void cleanUp() {
    img = null;
    data = null;
    datasel = null;
    imgSplit4 = null;
    dataSplit4 = null;
    dataselSplit4 = null;
  }
}
