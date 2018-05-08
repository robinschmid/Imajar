package net.rs.lamsi.massimager.MyOES;

import java.io.File;
import java.util.Vector;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.rs.lamsi.general.settings.SettingsDataSaver;
import net.rs.lamsi.utils.mywriterreader.TxtWriter;
import net.rs.lamsi.utils.mywriterreader.XSSFExcelWriterReader;

public class OESFileReaderWriter {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  // static Scanner scan=new Scanner(System.in);
  // TxtWriter zum Einlesen und Schreiben von txt Dateien
  private TxtWriter writer = new TxtWriter();

  public OESFile generateElementLineListFromTxtFile(File file) {
    // Textdatei lesen und in der Konsole ausgeben.
    // System.out.println("geben sie den Dateipfad an, sonst gibt es ärger.D:/Programmieren/Ca-Werte
    // ICP-OES2.txt");
    try {
      // Eingelesen wird Zeile für Zeile
      Vector<String> liste = writer.readLines(file);
      // scan.close();
      writer.closeDatOutput();
      // Liste von beliebig vielen Elementlinien die jeweils beliebig viele messungen enthalten
      // Name der Variable
      OESFile elementlinien = new OESFile(file);

      // Kopfzeile finden und lesen
      boolean hatkopfzeile = false;// bisher noch keine kopfzeile gefunden
      boolean hatDaten = false;
      String kopfzeile = "";// leerer string. muss vor for-schleife sein damit es nicht bei neuem
                            // schleifendurchgang gelöscht wird

      for (int i = 1; i < liste.size(); i++) {
        String currentline = liste.get(i);// daten werden als zeichenkette eingelesen
        // Die falschen Lerrzeichen rauslöschen
        char leerzeichen = 0;
        currentline = currentline.replaceAll(String.valueOf(leerzeichen), "");// Leerzeichen in
                                                                              // Schriftart in
                                                                              // dokument ist hier
                                                                              // eine null
        //
        if (currentline.length() > 2) {

          // kopfzeile finden
          if (hatkopfzeile == false) {
            kopfzeile = currentline;// die aktuelle zeile zwischengespeichert als kopfzeile
            hatkopfzeile = true;
          }
          // Daten finden und einlesen, leerzeichen rauslöschen
          else {

            // finden (über: "beginnt die zeile mit Element?" dann nächste zeile sind daten
            if (!currentline.startsWith("Element")) {// wenn currentline nicht (=!) mit element
                                                     // anfängt, dann haben wir daten vorliegen
              //
              currentline = currentline.replaceAll(" ", "");// currentline.replacAll gibt string
                                                            // ohne leerzeichen zurück und speichert
                                                            // es als currentline (=überschreibung
                                                            // "altes" currentline)
              // eckige klammern sind array
              String[] werteTrennung = currentline.split(",");// jede zeile in liste ist jetzt
                                                              // zwischen den werten durch ,
                                                              // getrennt

              // [] array hat hier die größe 5
              if (werteTrennung.length == 5) {// bis hierhin:nur wenn zeile 5 werte lang, dann
                                              // verarbeiten (siehe folgendes)
                // Wert1 gibt elementlinie an >> möglichekeiten: neue linie erstellen oder alte
                // verwenden
                Datenverarbeiten(elementlinien, werteTrennung, kopfzeile);
                hatDaten = true;
              }
            }

          }


        }
        // currentline ist leer (d.h. messung zu ende) >> neue kopfzeile einlesen.
        else if (hatDaten == true) {
          hatkopfzeile = false;
          hatDaten = false;
        }

      }
      // zurück geben
      return elementlinien;
    } catch (Exception ex) {
      logger.error("", ex);
      return null;
      // TODO Fheler anzeigen
    }
  }

  // OES OUTPUT TO EXCEL
  public boolean writeOESFileToXLSXFile(SettingsDataSaver setds, XSSFExcelWriterReader writer,
      OESFile oesf, XSSFWorkbook wb) throws Exception {
    // Elementline or Scan as Sheet?
    if (setds.isUsesElementLineAsSheet()) {
      // ELementLine as Sheet
      // For ElementLine
      for (int i = 0; i < oesf.size(); i++) {
        // Progress:
        //
        OESElementLine el = oesf.get(i);
        XSSFSheet sheet = writer.getSheet(wb, el.getName());
        // write scans
        // es können schon Scans geschrieben sein deswegen hier start column suchen
        int startcolumn = writer.getFirstEmptyColumn(sheet);
        // Scans durchgehen und in column i+startcolumn schreiben
        // collumnidex speichern
        int columnindex = 0;
        for (int s = 0; s < el.getListScan().size(); s++) {
          // Scan schreiben
          OESScan scan = el.getListScan().get(s);
          // Write Time only once?
          boolean writeTime = !(setds.isWriteTimeOnlyOnce() && s != 0);
          //
          writeScanToWBColumn(writer, sheet, scan, writeTime, columnindex);
          //
          columnindex += 1 + (writeTime ? 1 : 0);
        }
      }
      return true;
    } else {
      // TODO Scan as sheet alles hier wie eben auch

      return true;
    }
  }

  private void writeScanToWBColumn(XSSFExcelWriterReader writer, XSSFSheet sheet, OESScan scan,
      boolean writeTime, int icol) {
    // Header schreiben
    int colloff = 0;
    if (writeTime) {
      colloff = 1;
      writer.writeToCell(sheet, icol, 0, "Name");
      writer.writeToCell(sheet, icol, 1, "Date");
      writer.writeToCell(sheet, icol, 2, "Info");

      writer.writeToCell(sheet, icol, 4, "Time");
    }
    writer.writeToCell(sheet, icol + colloff, 0, scan.getName());
    writer.writeToCell(sheet, icol + colloff, 1, scan.getDate());
    writer.writeToCell(sheet, icol + colloff, 2, scan.getInfo());

    writer.writeToCell(sheet, icol + colloff, 4, "Intensity");

    int startrow = 5;
    // Daten schreiben
    for (int r = 0; r < scan.getTime().size(); r++) {
      if (writeTime) {
        // TIME schreiben
        writer.writeToCell(sheet, icol, startrow + r, scan.getTime().get(r));
      }
      // Center schreiben
      writer.writeToCell(sheet, icol + colloff, startrow + r, scan.getCenter().get(r));
    }
  }

  /*
   * // Alle ElementLinien als WorkSHeet und alle Messungen darein public static void
   * outputToExcel(Vector<OESElementLine> elementlinien) { // Einen Neuen ExcelWriter zum erstellen
   * von .xls Dateien. // xlsx funktioniert hiermit leider nicht! ExcelWriter xlsWriter = new
   * ExcelWriter(); try { // Ein neues Workbook erstellen
   * xlsWriter.createNewWorkbook("ErstesBuch.xls");
   * 
   * // Jede ElementLinie durchgehen for (int i=0; i<elementlinien.size(); i++) { OESElementLine el
   * = elementlinien.get(i); // Ein Arbeitsblatt erstellen für jede neue ElementLinie WritableSheet
   * sheet = xlsWriter.getWorksheet(el.Name);
   * 
   * // Alle Messungen durchgehen for(int m = 0; m<el.listMessungen.size(); m++) { OESScan cMessung
   * = el.listMessungen.get(m); // Kopfdaten in Excel schreiben xlsWriter.addLabel(sheet, m*2, 0,
   * cMessung.name); xlsWriter.addLabel(sheet, m*2, 1, cMessung.date); xlsWriter.addLabel(sheet,
   * m*2, 2, cMessung.info); // Schreibe Daten // dafür time und center als kopfzeile
   * xlsWriter.addLabel(sheet, m*2, 3, "t [s]"); xlsWriter.addLabel(sheet, m*2+1, 3, "Intensität");
   * // alle Daten einer Messung mit for schleife durchgehen for(int t=0; t<cMessung.time.size();
   * t++) { int row = 4+t; xlsWriter.addLabel(sheet, m*2, row, cMessung.time.get(t));
   * xlsWriter.addNumber(sheet, m*2+1, row, cMessung.center.get(t), true); } } }
   * 
   * // Alles Schreiben und Schließen xlsWriter.closeDataOutputAndSave(); // Fertig }
   * catch(Exception ex) { logger.error("",ex); }
   * 
   * }
   */
  // neue Methode schreiben (übersichtlicher) zum Daten auswerten
  // diese Sachen übergeben wir ihm (aus der methode oben) um damit zu arbeiten
  public static void Datenverarbeiten(Vector<OESElementLine> elementlinien, String[] werteTrennung,
      String kopfzeile) {

    // elementlinie checken (haben wir schon eine), >> möglichekeiten: neue linie erstellen oder
    // alte verwenden
    // Klasse s.o.
    // Variable vom Typ (der Klasse) ElementLinie
    OESElementLine currentElementLinie = null;// currentElementlinie ist neue variable und sie ist
                                              // leer

    // alle bekannten Elementlinien durchgehen, und mit daten(werteTrennung) vergleichen
    for (int i = 0; i < elementlinien.size(); i++) {
      if (elementlinien.get(i).getName().equalsIgnoreCase(werteTrennung[0])) {
        // wenn elementlinie schon vorhanden, dann soll "er" sie auch nehmen
        currentElementLinie = elementlinien.get(i);
        break;
      }
    }

    // wenn keine elementlinie gefunden, dann neue Elementlinie anlege(in excel später neues
    // worksheet)
    if (currentElementLinie == null) {
      currentElementLinie = new OESElementLine(werteTrennung[0]);
      elementlinien.add(currentElementLinie);
    }
    // Daten umwandeln time und center
    double time = Double.valueOf(werteTrennung[1]);
    double center = Double.valueOf(werteTrennung[3]);

    // Neue Messung wenn Zeit == 0.0
    if (Double.valueOf(time) == 0 || currentElementLinie.listScan.size() == 0) {
      // erste messung der neuen elementlinie erstellen.
      OESScan neueMessung = new OESScan();
      neueMessung.setKopfzeile(kopfzeile);
      currentElementLinie.listScan.add(neueMessung);
      // übergabewert aus methode
    }
    // die letzte Messung aus der Liste messung von currentElementlinie soll Daten bekommen
    OESScan currentMessung = currentElementLinie.listScan.lastElement();

    // Daten einspeichern
    currentMessung.setData(time, center);
  }


}
