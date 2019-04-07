package net.rs.lamsi.utils.mywriterreader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.Vector;
import org.mozilla.universalchardet.UniversalDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.rs.lamsi.utils.useful.FileDim;

public class TxtWriter {
  private static final Logger logger = LoggerFactory.getLogger(TxtWriter.class);

  // BufferedReader in;
  BufferedWriter out;
  Scanner scanner;
  private String lastencoding = null;

  public TxtWriter() {

  }

  /**
   * automatically detects charset with juniversalchardet package
   * 
   * @param file
   * @return
   */
  public Vector<String> readLines(String file) {
    return readLines(new File(file));
  }

  /**
   * automatically detects charset with juniversalchardet package
   * 
   * @param file
   * @return
   */
  public Vector<String> readLines(File file) {
    try {
      String encoding = UniversalDetector.detectCharset(file);
      return readLines(file, encoding);
    } catch (Exception ex) {
      return readLines(file, null);
    }
  }

  /**
   * read lines with the given encoding (UTF-16 = unicode; UTF-8...)
   * 
   * @param file
   * @param encoding as String (given by UniversalDetector.detectCharset(file); juniversalchardet)
   *        null for standard encoding
   * @return
   */
  public Vector<String> readLines(File file, String encoding) {
    Vector<String> sList = new Vector<String>();
    try {
      if (encoding != null)
        scanner = new Scanner(file, encoding);
      else
        scanner = new Scanner(file);
      while (scanner.hasNextLine()) {
        sList.add(scanner.nextLine());
      }
      // unicode delete starting char
      if (encoding != null && encoding.toLowerCase().startsWith("utf") && sList.size() > 0) {
        String first = sList.remove(0).substring(1);
        sList.add(0, first);
      }
    } catch (Exception e) {
      logger.warn("Exception in read txt lines", e);
    } finally {
      scanner.close();
    }
    return sList;
  }

  /**
   * automatically detects charset with juniversalchardet package
   * 
   * @param file
   * @return
   */
  public Vector<String> readLinesBuffered(String file) throws IOException {
    return readLinesBuffered(new File(file));
  }

  /**
   * automatically detects charset with juniversalchardet package
   * 
   * @param file
   * @return
   */
  public Vector<String> readLinesBuffered(File file) throws IOException {
    try {
      String encoding = UniversalDetector.detectCharset(file);
      try {
        return readLinesBuffered(file, encoding);
      } catch (IOException ex) {
        throw ex;
      }
    } catch (Exception ex) {
      return readLinesBuffered(file, null);
    }
  }

  /**
   * read lines with the given encoding (UTF-16 = unicode; UTF-8...)
   * 
   * @param file
   * @param encoding as String (given by UniversalDetector.detectCharset(file); juniversalchardet)
   *        null for standard encoding
   * @return
   */
  public Vector<String> readLinesBuffered(File file, String encoding) throws IOException {
    Vector<String> sList = new Vector<String>();

    BufferedReader br = null;
    try {
      // BufferedReader br = new BufferedReader(ReaderFactory.createReaderFromFile(file));
      if (encoding != null)
        br = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
      else
        br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
      String line;
      while ((line = br.readLine()) != null) {
        // process the line.
        sList.add(line);
      }

      // unicode delete starting char
      if (encoding != null && encoding.toLowerCase().startsWith("utf") && sList.size() > 0) {
        String first = sList.remove(0).substring(1);
        sList.add(0, first);
      }
      return sList;
    } finally {
      if (br != null)
        br.close();
    }
  }


  /**
   * automatically detects charset with juniversalchardet package close bufferedreader after
   * finishing!
   * 
   * @param file
   * @return
   */
  public BufferedReader getBufferedReader(String file) throws IOException {
    return getBufferedReader(new File(file));
  }

  /**
   * automatically detects charset with juniversalchardet package close bufferedreader after
   * finishing!
   * 
   * @param file
   * @return
   */
  public BufferedReader getBufferedReader(File file) throws IOException {
    lastencoding = UniversalDetector.detectCharset(file);
    BufferedReader br = null;
    // BufferedReader br = new BufferedReader(ReaderFactory.createReaderFromFile(file));
    if (lastencoding != null)
      br = new BufferedReader(new InputStreamReader(new FileInputStream(file), lastencoding));
    else
      br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
    return br;
  }

  /**
   * automatically detects charset with juniversalchardet package close bufferedreader after
   * finishing!
   * 
   * @param file
   * @return
   */
  public BufferedReader getBufferedReader(InputStream stream) throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(stream));
    return br;
  }

  /**
   * old version to detect charset
   * 
   * @param file
   * @return
   */
  public Vector<String> readLinesOLD(File file) {
    Vector<String> sList = new Vector<String>();
    try {
      scanner = new Scanner(file);
      String line;
      // first line to check for UTF8
      // BOM in first line first char
      if (scanner.hasNextLine()) {
        line = scanner.nextLine();
        if (line.startsWith("\uFEFF")) {
          return readLines(file, "UTF-8");
        } else {
          sList.add(line);
          // falsche leertaste die nur bei unicode drin ist:
          char falseSpace = 0;
          // Alle linien in Vector speichern
          while (scanner.hasNextLine()) {
            // ANSI (UTF-16)
            line = scanner.nextLine();
            sList.add(line);
            // Ist der Text in unicode?
            if (line.contains(String.valueOf(falseSpace))) {
              return readLines(file, "UTF-16");
            }
          }
        }
      }
    } catch (Exception e) {
      System.err.println(e.getMessage());
    } finally {
      scanner.close();
    }
    return sList;
  }
  // END OF READ DATA
  // #####################################################################################

  // #####################################################################################
  // WRITE DATA
  /**
   * appends a line with break
   * 
   * @param s
   */
  public void writeLine(String s) {
    try {
      String[] st = s.split("\n");
      for (String ns : st) {
        out.append(ns);
        out.newLine();
      }
      out.flush();
    } catch (IOException ex) {
      logger.error("", ex);
    }
  }

  /**
   * append
   * 
   * @param s
   */
  public void write(String s) {
    try {
      out.append(s);
      out.flush();
    } catch (IOException ex) {
      logger.error("", ex);
    }
  }

  /**
   * opens a new file output
   * 
   * @param file
   */
  public void openNewFileOutput(String file) {
    try {
      File f = new File(file);
      if (!f.exists()) {
        if (f.getParentFile() != null)
          f.getParentFile().mkdirs();
        f.createNewFile();
      }

      closeDatOutput();
      out = new BufferedWriter(new FileWriter(file));
    } catch (IOException e) {
      logger.error("", e);
    }
  }

  /**
   * closes the file stream
   */
  public void closeDatOutput() {
    try {
      if (out != null)
        out.close();
      out = null;
    } catch (IOException ex) {
      logger.error("", ex);
    }
  }

  public boolean isReadyToWrite() {
    return out != null;
  }


  /**
   * write data array to file
   * 
   * @param file
   * @param sep separation
   * @param model[rows][columns]
   */
  public void writeDataArrayToFile(File file, Object[][] model, String sep) {
    writeDataArrayToFile(file.getAbsolutePath(), model, sep);
  }

  /**
   * write data array to file. mode[row][col].
   * 
   * @param file
   * @param model
   * @param sep
   */
  public void writeDataArrayToFile(String file, double[][] model, String sep) {
    try {
      StringBuilder s = new StringBuilder();

      for (int r = 0; r < model.length; r++) {
        for (int c = 0; c < model[r].length; c++) {
          s.append(model[r][c]);
          if (c != model[r].length - 1)
            s.append(sep);
        }
        s.append("\n");
      }
      // StringSelection transferable = new StringSelection(s.toString());

      // open new file
      openNewFileOutput(file);

      // write string
      write(s.toString());

      // close
      closeDatOutput();
    } catch (Exception ex) {
      logger.error("", ex);
    }
  }

  /**
   * write data array to file. mode[row][col].
   * 
   * @param file
   * @param model
   * @param sep
   */
  public void writeDataArrayToFile(String file, Object[][] model, String sep) {
    try {
      StringBuilder s = new StringBuilder();

      for (int r = 0; r < model.length; r++) {
        for (int c = 0; c < model[r].length; c++) {
          s.append(model[r][c]);
          if (c != model[r].length - 1)
            s.append(sep);
        }
        s.append("\n");
      }
      // StringSelection transferable = new StringSelection(s.toString());

      // open new file
      openNewFileOutput(file);

      // write string
      write(s.toString());

      // close
      closeDatOutput();
    } catch (Exception ex) {
      logger.error("", ex);
    }
  }

  /**
   * write data array to file. mode[row][col].
   * 
   * @param file
   * @param map [row][col]
   * @param sep
   */
  public void writeBooleanArrayToFile(String file, boolean[][] map, String sep) {
    try {
      StringBuilder s = new StringBuilder();

      for (int r = 0; r < map.length; r++) {
        for (int c = 0; c < map[r].length; c++) {
          s.append(map[r][c] ? 1 : 0);
          if (c != map[r].length - 1)
            s.append(sep);
        }
        s.append("\n");
      }
      // StringSelection transferable = new StringSelection(s.toString());

      // open new file
      openNewFileOutput(file);

      // write string
      write(s.toString());

      // close
      closeDatOutput();
    } catch (Exception ex) {
      logger.error("", ex);
    }
  }


  /**
   * append data array to current file
   * 
   * @param file
   * @param model
   * @param sep
   */
  public void writeDataArrayToCurrentFile(Object[][] model, String sep) {
    try {
      StringBuilder s = new StringBuilder();

      for (int r = 0; r < model.length; r++) {
        for (int c = 0; c < model[r].length; c++) {
          s.append(model[r][c]);
          if (c != model[r].length - 1)
            s.append(sep);
        }
        s.append("\n");
      }

      // write string
      write(s.toString());
    } catch (Exception ex) {
      logger.error("", ex);
    }
  }



  /**
   * writes a list of data to one column
   * 
   * @param data
   */
  public void writeDataColumnToCurrentFile(Vector<Object> data) {
    for (int r = 0; r < data.size(); r++) {
      writeLine(String.valueOf(data.get(r)));
    }
  }

  /**
   * writes an array of data to one column
   * 
   * @param data
   */
  public void writeDataColumnToCurrentFile(Object[] data) {
    for (int r = 0; r < data.length; r++) {
      writeLine(String.valueOf(data[r]));
    }
  }

  /**
   * writes an array of data to one column
   * 
   * @param data
   */
  public void writeDataColumnToCurrentFile(double[] data) {
    for (int r = 0; r < data.length; r++) {
      writeLine(String.valueOf(data[r]));
    }
  }


  /**
   * compare 2 text files if files are the same returns true
   * 
   * @param file
   * @param file2
   * @return
   * @throws Exception
   */
  public boolean compareFilesAreTheSame(FileDim file, FileDim file2) throws Exception {
    return file.compareTo(file2);
  }

  public FileDim getFileDim(File file) {
    int lines1 = 0;
    int length1 = 0;
    try {
      scanner = new Scanner(file);
      while (scanner.hasNextLine()) {
        lines1++;
        length1 += scanner.nextLine().length();
      }
    } catch (Exception e) {
      System.err.println(e.getMessage());
    } finally {
      scanner.close();
    }
    return new FileDim(file, file.length(), lines1, length1);
  }

  public FileDim[] getFileDim(File[] files) {
    // time:
    long time1 = System.currentTimeMillis();
    FileDim[] dim = new FileDim[files.length];
    for (int i = 0; i < dim.length; i++) {
      dim[i] = getFileDim(files[i]);
    }
    return dim;
  }

  public String getLastencoding() {
    return lastencoding;
  }


}
