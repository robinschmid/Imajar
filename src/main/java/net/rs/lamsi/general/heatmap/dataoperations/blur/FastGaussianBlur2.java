package net.rs.lamsi.general.heatmap.dataoperations.blur;


/**
 * idea from (by Wojciech Jarosz):
 * http://web.archive.org/web/20030801220134/http://www.acm.uiuc.edu/siggraph/workshops/wjarosz_convolution_2001.pdf
 * 
 * and: http://blog.ivank.net/fastest-gaussian-blur.html
 * 
 * @author r_schm33
 *
 */
public class FastGaussianBlur2 {

  public static void main(String[] args) {
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

    System.out.print("For n=" + n + " and sigma=" + sigma + " we got sizes: ");
    for (int i : sizes)
      System.out.print(i + ", ");
    System.out.println();
    return sizes;
  }
}
