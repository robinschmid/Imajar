package net.rs.lamsi.general.heatmap.dataoperations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * idea from (by Wojciech Jarosz):
 * http://web.archive.org/web/20030801220134/http://www.acm.uiuc.edu/siggraph/workshops/wjarosz_convolution_2001.pdf
 * 
 * and: http://blog.ivank.net/fastest-gaussian-blur.html
 * 
 * @author r_schm33
 *
 */
public class FastGaussianBlur extends PostProcessingOp {
  private static final Logger logger = LoggerFactory.getLogger(FastGaussianBlur.class);

  private double sigma;

  public FastGaussianBlur(double sigma) {
    this.sigma = sigma;
  }


  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof FastGaussianBlur))
      return false;
    else {
      FastGaussianBlur that = (FastGaussianBlur) obj;
      return this.sigma == that.sigma;
    }
  }

  @Override
  public double[][] processItensity(double[][] z, double[][] target) {
    applyBlur(z, target, sigma);
    return target;
  }

  @Override
  public float[][] processXY(float[][] x, float[][] target) {
    return target = x.clone();
  }

  @Override
  public boolean isProcessingXY() {
    return false;
  }



  public static void main(String[] args) {
    int i = 1;
    int k = 10;
    double f = i / (double) k;
    double d = 1.0 / 10;
    logger.debug("+{}  {}", d, f);
    boxesForGauss(2, 3);
    boxesForGauss(1, 3);
    boxesForGauss(2, 3);
    boxesForGauss(3, 3);
    boxesForGauss(4, 3);
    boxesForGauss(5, 3);
    boxesForGauss(6, 3);
    boxesForGauss(7, 3);
  }


  // tcl is empty at start
  /**
   * This filter applies a box blur 3 times to imitate gaussian blur source dataset and target will
   * be changed!
   * 
   * @param source linear representation of a 2d array
   * @param target linear representation of a 2d array
   * @param w width of line
   * @param h line count
   * @param sigma radius of Gaussian filter
   */
  public static void applyBlur(double[][] source, double[][] target, double sigma) {
    int[] bxs = boxesForGauss(sigma, 3);
    boxBlur(source, target, (bxs[0] - 1) / 2);
    boxBlur(target, source, (bxs[1] - 1) / 2);
    boxBlur(source, target, (bxs[2] - 1) / 2);
  }

  private static void boxBlur(double[][] source, double[][] target, int r) {
    for (int i = 0; i < source.length; i++)
      for (int k = 0; k < source[i].length; k++)
        target[i][k] = source[i][k];
    boxBlurHorizontally(target, source, r);
    boxBlurVertically(source, target, r);
  }

  private static void boxBlurHorizontally(double[][] source, double[][] target, int r) {
    // correction factor in one dimension = 2r+1
    double iarr = 1.0 / (r + r + 1.0);
    // from bottom to top for each line
    for (int i = 0; i < source.length; i++) {
      int w = source[i].length;
      // init value with first value in line
      double val = 0;
      int counter = 0;
      int center = 0;
      for (int dp = 0; dp < w; dp++) {
        if (Double.isNaN(source[i][dp])) {
          // add remaining
          int last = dp;
          while (counter > r + 1) {
            last++;
            center++;
            // subtract value from start
            val -= source[i][last - r * 2 - 1];
            counter--;
            // add center value
            target[i][center] = val * 1.0 / counter;
          }
          // reset
          val = 0;
          counter = 0;
          center = dp + 1;
        } else {
          val += source[i][dp];
          if (counter < r * 2 + 1) {
            counter++;
          } else {
            // subtract value from start
            val -= source[i][dp - r * 2 - 1];
          }

          if (counter > r + 1)
            center++;

          // add center value
          target[i][center] = val * 1.0 / counter;
        }
      }
      // add remaining
      int last = w - 1;
      while (counter > r + 1) {
        last++;
        center++;
        // subtract value from start
        val -= source[i][last - r * 2 - 1];
        counter--;
        // add center value
        target[i][center] = val * 1.0 / counter;
      }
    }
  }

  private static void boxBlurVertically(double[][] source, double[][] target, int r) {
    // correction factor in one dimension = 2r+1
    double iarr = 1.0 / (r + r + 1.0);


    int w = 0;
    for (double[] d : source)
      if (w < d.length)
        w = d.length;
    // from bottom to top for each line
    for (int dp = 0; dp < w; dp++) {
      // init value with first value in line
      double val = 0;
      int counter = 0;
      int center = 0;

      for (int i = 0; i < source.length; i++) {
        if (Double.isNaN(source[i][dp])) {
          // add remaining
          int last = i;
          while (counter > r + 1) {
            last++;
            center++;
            // subtract value from start
            val -= source[last - r * 2 - 1][dp];
            counter--;
            // add center value
            target[center][dp] = val * 1.0 / counter;
          }
          // nothing
          val = 0;
          counter = 0;
          center = i + 1;
        } else {
          val += source[i][dp];
          if (counter < r * 2 + 1) {
            counter++;
          } else {
            // subtract value from start
            val -= source[i - r * 2 - 1][dp];
          }

          if (counter > r + 1)
            center++;

          // add center value
          target[center][dp] = val * 1.0 / counter;
        }
      }
      // add remaining
      int last = source.length - 1;
      while (counter > r + 1) {
        last++;
        center++;
        // subtract value from start
        val -= source[last - r * 2 - 1][dp];
        counter--;
        // add center value
        target[center][dp] = val * 1.0 / counter;
      }
    }
  }

  private static int[] boxesForGauss(double sigma, int n) { // standard deviation, number of boxes
    double wIdeal = Math.sqrt((12 * sigma * sigma / n) + 1); // Ideal averaging filter width
    int wl = (int) Math.floor(wIdeal);
    if (wl % 2 == 0)
      wl--;
    int wu = wl + 2;

    double mIdeal = (12 * sigma * sigma - n * wl * wl - 4 * n * wl - 3 * n) / (-4 * wl - 4);
    int m = (int) (mIdeal);
    // var sigmaActual = Math.sqrt( (m*wl*wl + (n-m)*wu*wu - n)/12 );

    int[] sizes = new int[n];
    for (int i = 0; i < n; i++)
      sizes[i] = i < m ? wl : wu;

    logger.debug("For n=" + n + " and sigma=" + sigma + " we got sizes: ");
    for (int i : sizes)
      logger.debug(i + ", ");
    return sizes;
  }

  /**
   * 1D Array
   */
  /**
   * This filter applies a box blur 3 times to imitate gaussian blur source dataset and target will
   * be changed!
   * 
   * @param source linear representation of a 2d array
   * @param target linear representation of a 2d array
   * @param w width of line
   * @param h line count
   * @param r radius of Gaussian filter
   */
  public static void applyBlur(double[] source, double[] target, int w, int h, double r) {
    int[] bxs = boxesForGauss(r, 3);
    boxBlur(source, target, w, h, (bxs[0] - 1) / 2);
    boxBlur(target, source, w, h, (bxs[1] - 1) / 2);
    boxBlur(source, target, w, h, (bxs[2] - 1) / 2);
  }

  private static void boxBlur(double[] source, double[] target, int w, int h, int r) {
    for (int i = 0; i < source.length; i++)
      target[i] = source[i];
    boxBlurHorizontally(target, source, w, h, r);
    boxBlurVertically(source, target, w, h, r);
  }

  private static void boxBlurHorizontally(double[] source, double[] target, int w, int h, int r) {
    // correction factor in one dimension = 2r+1
    double iarr = 1.0 / (r + r + 1.0);
    // from bottom to top for each line
    for (int i = 0; i < h; i++) {
      // ti = current index set it to start of line
      int ti = i * w;
      // left side of box
      int li = ti;
      // right side of box
      int ri = ti + r;

      // first and last value
      double fv = source[ti];
      double lv = source[ti + w - 1];
      // init value with first value in line
      double val = (r + 1) * fv;

      // init box sum for first values
      for (int j = 0; j < r; j++)
        val += source[ti + j];

      // add first values
      for (int j = 0; j <= r; j++) {
        // add next values - remove firstvalue
        val += source[ri++] - fv;
        target[ti++] = (val * iarr);
      }

      // add middle values
      for (int j = r + 1; j < w - r; j++) {
        val += source[ri++] - source[li++];
        target[ti++] = (val * iarr);
      }

      // add last values
      for (int j = w - r; j < w; j++) {
        // add last value - remove middle values
        val += lv - source[li++];
        target[ti++] = (val * iarr);
      }

    }
  }

  private static void boxBlurVertically(double[] source, double[] target, int w, int h, int r) {
    // correction factor in one dimension = 2r+1
    double iarr = 1.0 / (r + r + 1.0);
    // for all columns
    for (int i = 0; i < w; i++) {
      // ti = current index set it to start of line
      int ti = i;
      // bottom side of box
      int li = ti;
      // top side of box
      int ri = ti + r * w;

      // first value
      double fv = source[ti];
      // last value
      double lv = source[ti + w * (h - 1)];
      // value
      double val = (r + 1) * fv;

      // init first values
      for (int j = 0; j < r; j++)
        val += source[ti + j * w];

      // blur bottom edge
      for (int j = 0; j <= r; j++) {
        val += source[ri] - fv;
        target[ti] = (val * iarr);
        ri += w;
        ti += w;
      }

      // blur middle
      for (int j = r + 1; j < h - r; j++) {
        val += source[ri] - source[li];
        target[ti] = (val * iarr);
        li += w;
        ri += w;
        ti += w;
      }

      // blur top edge
      for (int j = h - r; j < h; j++) {
        val += lv - source[li];
        target[ti] = (val * iarr);
        li += w;
        ti += w;
      }
    }
  }
}
