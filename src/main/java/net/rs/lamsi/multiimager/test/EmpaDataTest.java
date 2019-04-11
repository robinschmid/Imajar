package net.rs.lamsi.multiimager.test;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmpaDataTest {

  private final static Logger logger = LoggerFactory.getLogger(ConsoleTest.class);

  public static void main(String[] args) {
    // importData();
    for (int i = 0; i < 1; i++) {
      System.out.println("Start of import " + i);
      // importData();
      importDataByte(4);
    }

  }

  public static void importData2() {
    String fileName =
        "D:\\Daten2\\empa xray LA tof ms\\Imajar_WWU_davide_empa\\Davide_High_Pt_2_001_001_01.bin";
    RandomAccessFile in;
    try {
      in = new RandomAccessFile(fileName, "r");
      in.skipBytes(32);
      for (int i = 0; i < 20; i++)
        System.out.println(in.readDouble());
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void importData() {
    String fileName =
        "D:\\Daten2\\empa xray LA tof ms\\Imajar_WWU_davide_empa\\Davide_High_Pt_2_000_000_01.bin";
    DataInputStream din = null;
    try {
      din = new DataInputStream(new FileInputStream(fileName));

      din.skipBytes(7000 * 4);
      double data;
      for (int i = 0; i < 5000; i++) {
        data = din.readDouble();
        if (data > 0.5 && data < 100)
          System.out.println(">>>>>>>>>>>>>>>>>>" + data);
        else
          System.out.println(data);
      }
    } catch (

    EOFException ignore) {
    } catch (Exception ioe) {
      ioe.printStackTrace();
    } finally {
      if (din != null) {
        try {
          din.close();
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }
    }
  }

  public static void importDataByte(int skip) {
    String fileName =
        "D:\\Daten2\\empa xray LA tof ms\\Imajar_WWU_davide_empa\\Davide_High_Pt_2_000_000_01.bin";
    DataInputStream din = null;
    try {
      din = new DataInputStream(new FileInputStream(fileName));
      int last = 0;
      byte[] buffer = new byte[8];
      byte b;
      double data;
      for (int i = 0; i < 20; i++) {
        if ((last = din.read(buffer)) < 8) {
          System.out.println("last" + last);
          break;
        }
        // reverse buffer
        data = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getDouble();
        if (data > 0.5 && data < 10000)
          System.out.println(">>>>>>>>>>>>>>>>>>" + data);
        else
          System.out.println(data);
      }
    } catch (

    EOFException ignore) {
    } catch (Exception ioe) {
      ioe.printStackTrace();
    } finally {
      if (din != null) {
        try {
          din.close();
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }
    }
  }

}


