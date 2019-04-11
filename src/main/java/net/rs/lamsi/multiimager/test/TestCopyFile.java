package net.rs.lamsi.multiimager.test;

import java.io.File;

public class TestCopyFile {


  public static void main(String[] args) {

    Thread t = new Thread(new Runnable() {
      @Override
      public void run() {
        File f = new File("D:\\tmp\\fc\\test.csv");
        int i = 1;
        while (true) {
          boolean renamed = false;
          while (f.exists() && f.canWrite() && !renamed) {
            renamed = f.renameTo(new File("D:\\tmp\\fc\\data" + i + ".csv"));
            if (renamed)
              i++;
          }
          try {
            Thread.sleep(33);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    });
    t.start();
  }
}
