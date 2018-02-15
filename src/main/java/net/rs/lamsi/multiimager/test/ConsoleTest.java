package net.rs.lamsi.multiimager.test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import net.rs.lamsi.utils.math.Precision;

public class ConsoleTest {
  private static DecimalFormat format = new DecimalFormat("0.0E00");

  public static void main(String[] args) {
    // TODO Auto-generated method stub UTF8thermo.csv

    double d = 1.55555;
    System.out.println("v " + getSignificant(d, 3));
    System.out.println("v " + getSignificant(d / 10.0, 3));
    System.out.println("v " + getSignificant(d / 100.0, 3));
    System.out.println("v " + getSignificant(d / 1000.0, 3));
    System.out.println("v " + getSignificant(d / 10000.0, 3));
    System.out.println("v " + getSignificant(d / 100000.0, 3));
    System.out.println("v " + getSignificant(d * 10.0, 3));
    System.out.println("v " + getSignificant(d * 100.0, 3));
    System.out.println("v " + getSignificant(d * 1000.0, 3));
    System.out.println("v " + getSignificant(d * 10000.0, 3));
    System.out.println("v " + getSignificant(d * 100000.0, 3));
    System.out.println("\n");
    System.out.println("v " + getSignificant2(d, 3));
    System.out.println("v " + getSignificant2(d / 10.0, 3));
    System.out.println("v " + getSignificant2(d / 100.0, 3));
    System.out.println("v " + getSignificant2(d / 1000.0, 3));
    System.out.println("v " + getSignificant2(d / 10000.0, 3));
    System.out.println("v " + getSignificant2(d / 100000.0, 3));
    System.out.println("v " + getSignificant2(d * 10.0, 3));
    System.out.println("v " + getSignificant2(d * 100.0, 3));
    System.out.println("v " + getSignificant2(d * 1000.0, 3));
    System.out.println("v " + getSignificant2(d * 10000.0, 3));
    System.out.println("v " + getSignificant2(d * 100000.0, 3));
    System.out.println("v " + getSignificant2(d * 1000000000000.0, 3));
  }

  public static String getSignificant(double value, int sigFigs) {
    MathContext mc = new MathContext(sigFigs, RoundingMode.HALF_UP);
    BigDecimal bigDecimal = new BigDecimal(value, mc);
    return bigDecimal.toPlainString();
  }

  // does not work
  public static String getSignificant2(double value, int sig) {
    return Precision.toString(value, sig, 5);
  }
}
