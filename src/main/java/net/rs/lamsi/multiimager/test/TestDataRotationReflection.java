package net.rs.lamsi.multiimager.test;

import java.io.File;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.ImageGroupMD;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.DatasetLinesMD;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.ScanLineMD;
import net.rs.lamsi.general.settings.image.sub.SettingsGeneralImage.IMAGING_MODE;
import net.rs.lamsi.general.settings.importexport.SettingsImage2DDataExport;
import net.rs.lamsi.general.settings.importexport.SettingsImageDataImportTxt.ModeData;

public class TestDataRotationReflection {


  public static void main(String[] args) {

    ScanLineMD[] lines = new ScanLineMD[10];

    int dpc = 5;

    for (int i = 0; i < 10; i++) {
      // for 5 dp
      float[] x = new float[dpc];
      double[] val = new double[dpc];
      for (int d = 0; d < dpc; d++) {
        // only intensity
        val[d] = (double) d * (i + 1);
        x[d] = d < 3 ? 10 * d : (float) (d * 10) - d;
      }

      lines[i] = new ScanLineMD(x, val);
    }

    DatasetLinesMD data = new DatasetLinesMD(lines);

    ImageGroupMD group = data.createImageGroup("rot");
    Image2D img = (Image2D) group.get(0);

    File f = new File("C:\\DATA\\test\\imgtest.csv");

    SettingsImage2DDataExport sett = new SettingsImage2DDataExport();
    sett.setUseReflectRotate(true);

    // rotate
    img.getSettings().getSettImage().setRotationOfData(0);
    img.getSettings().getSettImage().setImagingMode(IMAGING_MODE.MODE_IMAGING_TWOWAYS);
    img.getSettings().getSettImage().setReflectHorizontal(true);

    Object[][] norotat = img.toDataArray(ModeData.ONLY_Y, true, false);
    Object[][] dataar = img.toDataArray(ModeData.ONLY_Y, true, true);
    Object[][] noxarr = img.toDataArray(ModeData.XYYY, true, false);
    Object[][] xarr = img.toDataArray(ModeData.XYYY, true, true);

    System.out.println("NOOOrot arr::: 90\n");
    printAr(norotat);
    System.out.println("rot arr::: 90\n");
    printAr(dataar);
    System.out.println("NOOrot XXXXarr::: 90\n");
    printAr(noxarr);
    System.out.println("rot XXXXarr::: 90\n");
    printAr(xarr);

    String noproc = img.toICSV(true, "\t", false);
    String proc = img.toICSV(true, "\t", true);

    System.out.println("NOOOproc CSVI::: 90\n" + noproc);
    System.out.println("proc     CSVI::: 90\n" + proc);

    System.out.println("NOOOrot arr::: 90\n");
    printAr(img.toIMatrix(true, false));
    System.out.println("rot arr::: 90\n");
    printAr(img.toIMatrix(true, true));


    System.out.println("NOOO XXX proc::: 90\n" + img.toXCSV(true, "\t", false));
    System.out.println("XXX proc::: 90\n" + img.toXCSV(true, "\t", true));

    System.out.println("NOOOrot XXX arr::: 90\n");
    printAr(img.toXMatrix(true, false));
    System.out.println("rot XXX arr::: 90\n");
    printAr(img.toXMatrix(true, true));

    System.out.println("NOOOrot XYI arr::: 90\n");
    printAr(img.toXYIMatrix(true, false));
    System.out.println("rot XYI arr:::\n");
    printAr(img.toXYIMatrix(true, true));


    //
  }

  private static void printAr(Object[][] a) {
    for (Object[] b : a) {
      for (Object c : b) {
        System.out.print(c + "\t");
      }
      System.out.println("");
    }
    System.out.println("");
  }

  private static void printAr(double[][] a) {
    for (double[] b : a) {
      for (double c : b) {
        System.out.print(c + "\t");
      }
      System.out.println("");
    }
    System.out.println("");
  }
}
