package net.rs.lamsi.multiimager.utils.imageimportexport;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.ProgressMonitor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.ImageGroupMD;
import net.rs.lamsi.general.datamodel.image.ImagingProject;
import net.rs.lamsi.general.dialogs.HeatmapGraphicsExportDialog.EXPORT_STRUCTURE;
import net.rs.lamsi.general.settings.image.selection.SettingsSelections;
import net.rs.lamsi.general.settings.image.selection.SettingsShapeSelection;
import net.rs.lamsi.general.settings.importexport.SettingsImage2DDataExport;
import net.rs.lamsi.general.settings.importexport.SettingsImage2DDataExport.FileType;
import net.rs.lamsi.general.settings.importexport.SettingsImage2DDataSelectionsExport;
import net.rs.lamsi.general.settings.importexport.SettingsImageDataImportTxt.ModeData;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow.LOG;
import net.rs.lamsi.utils.DialogLoggerUtil;
import net.rs.lamsi.utils.FileAndPathUtil;
import net.rs.lamsi.utils.mywriterreader.ClipboardWriter;
import net.rs.lamsi.utils.mywriterreader.TxtWriter;
import net.rs.lamsi.utils.mywriterreader.XSSFExcelWriterReader;
import net.rs.lamsi.utils.threads.ProgressUpdateTask;
import net.rs.lamsi.utils.threads.ProgressUpdateTaskMonitor;

public class DataExportUtil {
  private static TxtWriter writer = new TxtWriter();
  private static XSSFExcelWriterReader xwriter = new XSSFExcelWriterReader();
  private static XSSFWorkbook lastwb;

  /**
   * export all of them. the settings of "export all files" has to be computed before this.
   * 
   * @param imgList
   * @param setImage2DDataExport
   * @throws IOException
   * @throws InvalidFormatException
   * @throws ExecutionException
   * @throws InterruptedException
   */
  public static void exportDataImage2D(final Component parent, final List<Image2D> imgList,
      final SettingsImage2DDataExport setImage2DDataExport)
      throws InvalidFormatException, IOException, InterruptedException, ExecutionException {
    lastwb = null;
    // progress
    // ProgressUpdateTask task = ProgressDialog.getInst().startTask(new
    // ProgressUpdateTask(ProgressDialog.getInst(), imgList.size()) {
    ProgressMonitor monitor = new ProgressMonitor(parent, "Exporting", "note", 0, 100);
    monitor.setMillisToDecideToPopup(0);
    ProgressUpdateTaskMonitor task = new ProgressUpdateTaskMonitor(monitor, 100) {

      @Override
      protected Boolean doInBackground() throws Exception {
        //
        // all titles are the same?
        try {
          boolean titlesAreTheSame = false;
          for (int i = 0; i < imgList.size() - 1 && !titlesAreTheSame; i++) {
            for (int k = i + 1; k < imgList.size() && !titlesAreTheSame; k++) {
              if (imgList.get(i).getTitle().equalsIgnoreCase(imgList.get(k).getTitle()))
                titlesAreTheSame = true;
            }
          }
          // export data
          for (int i = 0; i < imgList.size(); i++) {
            String qualifier = imgList.get(i).getTitle();
            if (titlesAreTheSame)
              qualifier += " " + (i + 1);
            // TODO name extention for txt
            exportDataImage2D(imgList.get(i), setImage2DDataExport, qualifier, i == 0,
                i == imgList.size() - 1, true);
            // no exception then progress
            setProgress((int) ((i + 1) * 100.0 / imgList.size()));
          }
        } catch (Exception ex) {
          this.setProgress(100);
          ex.printStackTrace();
          return false;
        }
        this.setProgress(100);
        return true;
      }

      @Override
      protected void done() {
        super.done();
        try {
          if (get().booleanValue())
            // no exception...
            DialogLoggerUtil.showMessageDialog(parent, "Succeed", "File(s) saved successfully");
          else
            DialogLoggerUtil.showErrorDialog(parent, "Not saved", "Error");
        } catch (InterruptedException e) {
          e.printStackTrace();
        } catch (ExecutionException e) {
          e.printStackTrace();
        }
      }

    };
    task.execute();
  }

  /**
   * export one image
   * 
   * @param img
   * @param sett
   * @throws IOException
   * @throws InvalidFormatException
   */
  public static void exportDataImage2D(final Component parent, Image2D img,
      SettingsImage2DDataExport s) throws InvalidFormatException, IOException {

    switch (s.getExportStructure()) {
      case IMAGE:
        exportDataImage2D(img, s);
        break;
      case GROUP:
        // no group?
        if (img.getImageGroup() == null) {
          s.setExportStructure(EXPORT_STRUCTURE.IMAGE);
          exportDataImage2D(parent, img, s);
          return;
        } else {
          final SettingsImage2DDataExport ss = s;
          final ImageGroupMD g = img.getImageGroup();
          new ProgressUpdateTask(g.image2dCount()) {
            @Override
            protected Boolean doInBackground2() throws Exception {
              // name
              String name = ss.getFilename();
              // create group folder
              File folder = ss.getPath();
              ss.setPath(new File(folder, g.getName()));
              int size = g.image2dCount();
              for (int d = 0; d < size; d++) {
                Image2D i = (Image2D) g.get(d);
                // name
                // ss.setFilename(name+i.getTitle());

                String qualifier = i.getTitle();
                // TODO name extention for txt
                exportDataImage2D(i, ss, qualifier, d == 0, d == size - 1, true);

                addProgressStep(1);
              }
              return true;
            }
          }.execute();
          break;
        }
      case PROJECT:
        // no group?
        if (img.getImageGroup() == null) {
          s.setExportStructure(EXPORT_STRUCTURE.IMAGE);
          exportDataImage2D(parent, img, s);
          return;
        } else if (img.getImageGroup().getProject() == null) {
          s.setExportStructure(EXPORT_STRUCTURE.GROUP);
          exportDataImage2D(parent, img, s);
          return;
        } else {
          final ImagingProject project = img.getImageGroup().getProject();
          final SettingsImage2DDataExport ss = s;

          new ProgressUpdateTask(project.size()) {
            @Override
            protected Boolean doInBackground2() throws Exception {
              // name
              String name = ss.getFilename();
              // create group folder
              File folder = new File(ss.getPath(), project.getName());

              for (ImageGroupMD g : project.getGroups()) {
                ss.setPath(new File(folder, g.getName()));
                //
                int size = g.image2dCount();
                for (int d = 0; d < size; d++) {
                  Image2D i = (Image2D) g.get(d);
                  // name
                  // ss.setFilename(name+i.getTitle());

                  String qualifier = i.getTitle();
                  // TODO name extention for txt
                  exportDataImage2D(i, ss, qualifier, d == 0, d == size - 1, true);

                  addProgressStep(1.0 / size);
                }
              }
              return true;
            }
          }.execute();
          break;
        }
    }
  }

  /**
   * export one image
   * 
   * @param img
   * @param sett
   * @throws IOException
   * @throws InvalidFormatException
   */
  public static void exportDataImage2D(Image2D img, SettingsImage2DDataExport s)
      throws InvalidFormatException, IOException {
    lastwb = null;

    exportDataImage2D(img, s, "", true, true, false);

    if (s.isWritingToClipboard())
      DialogLoggerUtil.showMessageDialog(null, "Succeed", "File(s) saved successfully");
  }

  /**
   * basic export
   * 
   * @param img
   * @param sett
   * @param qualifier is just added to the file (txt) or to the sheet (XLSX)
   * @return
   * @throws IOException
   * @throws InvalidFormatException
   */
  private static void exportDataImage2D(Image2D img, SettingsImage2DDataExport sett,
      String qualifier, boolean firstFile, boolean lastFile, boolean inFolder)
      throws InvalidFormatException, IOException {
    // compute vtk
    if (sett.getFileFormat() == FileType.VTK && !sett.isWritingToClipboard()) {
      sett.setMode(ModeData.ONLY_Y);
      sett.setSeparation(" ");
    }
    // get data
    Object[][] data = null;
    // only for x matrix
    Object[][] xmatrix = null;
    // get title row
    String[] title = null;
    // export line per line matrix of intensity
    if (sett.getMode().equals(ModeData.XYZ)) {
      // export xyz data
      data = img.toXYIMatrix(sett.isExportRaw(), sett.isUseReflectRotate());
      // get title row
      title = new String[] {"X", "Y", "I"};
    } else if (sett.getMode().equals(ModeData.X_MATRIX_STANDARD)) {
      // intensity matrix
      data = img.toDataArray(ModeData.ONLY_Y, sett.isExportRaw(), sett.isUseReflectRotate());
      if (firstFile)
        xmatrix = img.toXMatrix(sett.isExportRaw(), sett.isUseReflectRotate());
    } else {
      // intensity matrix
      data = img.toDataArray(sett.getMode(), sett.isExportRaw(), sett.isUseReflectRotate());
      // get title row
      if (sett.isWriteTitleRow()) {
        int size = data[0].length;
        title = new String[size];
        int line = 1;
        for (int i = 0; i < size; i++) {
          // write title X
          if (((sett.getMode().equals(ModeData.XYYY) && i == 0)
              || (sett.getMode().equals(ModeData.XYXY_ALTERN) && i % 2 == 0))) {
            title[i] = "X" + line;
          } else {
            // write y and increment
            title[i] = "Y" + line;
            line++;
          }
        }
      }
    }
    // writer it to txt or xlsx

    if (sett.isWritingToClipboard()) {
      // total string
      String total = "";
      // to clipboard
      if (title != null) {
        // title row
        String realtitle = title[0];
        for (int i = 1; i < title.length; i++)
          realtitle += "\t" + title[i];
        // write title line
        total += realtitle + "\n";
      }
      // content
      total += ClipboardWriter.dataToTabSepString(data, true);
      ClipboardWriter.writeToClipBoard(total);

      DialogLoggerUtil.showMessageDialogForTime(null, "Succeed",
          "Data copied to clipboard use paste function to retrieve data", 2000);
    } else if (sett.getFileFormat() == FileType.XLSX) {
      String file = "";
      XSSFWorkbook wb = null;
      if (sett.isSavesAllFilesToOneXLS()) {
        file = new File(sett.getPath(), FileAndPathUtil.eraseFormat((sett.getFilename())) + ".xlsx")
            .getAbsolutePath();
        wb = lastwb;
      } else {
        file = new File(sett.getPath(),
            FileAndPathUtil.eraseFormat((sett.getFilename())) + qualifier + ".xlsx")
                .getAbsolutePath();
      }
      if (wb == null)
        wb = new XSSFWorkbook();
      // create sheet
      XSSFSheet sheet = xwriter.getSheet(wb, qualifier.length() < 1 ? "data" : qualifier);
      // write title
      if (title != null)
        xwriter.writeLine(sheet, title, 0, 0);
      // write data
      xwriter.writeDataArrayToSheet(sheet, data, 0, title == null ? 0 : 1, true);
      // final
      lastwb = wb;
      if (lastFile || !sett.isSavesAllFilesToOneXLS()) {
        xwriter.saveWbToFile(new File(file), wb);
        xwriter.closeAllWorkbooks();
        lastwb = null;
      }
    } else {
      // put into one folder? or create a new folder named after the raw file
      File path = null;
      if (inFolder) {
        // create folder like filename
        path = new File(sett.getPath(), img.getSettings().getSettImage().getRAWFileName());
      } else {
        path = sett.getPath();
      }
      File file = new File(path, FileAndPathUtil.eraseFormat((sett.getFilename())) + qualifier + "."
          + sett.getFileFormat().toString());
      file = FileAndPathUtil.applyMaxLength(file);


      writer.openNewFileOutput(file.getAbsolutePath());

      // vtk? write VTK header
      if (sett.getMode().equals(ModeData.X_MATRIX_STANDARD)) {
        // do nothing... no tilte
      } else if (sett.getFileFormat() == FileType.VTK)
        writeVTKHeader(img, data, sett);
      else {
        // write title
        if (title != null) {
          // title row
          String realtitle = title[0];
          for (int i = 1; i < title.length; i++)
            realtitle += sett.getSeparation() + title[i];
          // write title line
          writer.writeLine(realtitle);
        }
      }
      // content
      writer.writeDataArrayToCurrentFile(data, sett.getSeparation());
      // end file
      writer.closeDatOutput();
      // log
      ImageEditorWindow.log("Written: " + file.getName() + " to " + file.getParent(), LOG.MESSAGE);

      // export x matrix
      if (sett.getMode().equals(ModeData.X_MATRIX_STANDARD) && firstFile && xmatrix != null) {
        file = new File(path, "xmatrix." + sett.getFileFormat().toString());
        writer.openNewFileOutput(file.getAbsolutePath());
        // content xmatrix
        writer.writeDataArrayToCurrentFile(xmatrix, sett.getSeparation());
        // end file
        writer.closeDatOutput();
        // log
        ImageEditorWindow.log("Written: " + file.getName() + " to " + file.getParent(),
            LOG.MESSAGE);
      }
    }
  }

  /**
   * writes a basic vtk header for this file # vtk DataFile Version 2.0 Mn55 ASCII DATASET
   * STRUCTURED_POINTS DIMENSIONS 780 560 1 ORIGIN 0 0 1 SPACING 1 1 1
   * 
   * POINT_DATA 436800 SCALARS values float LOOKUP_TABLE default
   * 
   * @param sett
   */
  private static void writeVTKHeader(Image2D img, Object[][] data, SettingsImage2DDataExport sett) {
    // TODO Auto-generated method stub
    writer.writeLine("# vtk DataFile Version 2.0\n" + img.getTitle() + "\n" + "ASCII");
    if (sett.getMode().equals(ModeData.XYZ)) {
      writer.writeLine(
          "DATASET UNSTRUCTURED_ GRID\n" + "POINTS " + (data.length * data[0].length) + " double");
    } else {
      writer.writeLine("DATASET STRUCTURED_POINTS\n" + "DIMENSIONS " + data[0].length + " "
          + data.length + " 1\n" + "ORIGIN 0 0 1\n" + "SPACING 1 1 1\n\n" + "POINT_DATA "
          + (data.length * data[0].length) + "\n" + "SCALARS values double\n"
          + "LOOKUP_TABLE default");
    }
  }

  /**
   * Exports only the selected rects in one file
   * 
   * @param img
   * @param rects
   * @param rectsExcluded
   * @param tableRows
   * @param rectsInfo
   * @param setImage2DDataExport
   * @throws Exception
   */
  public static void exportDataImage2DInRects(final Image2D img, final SettingsSelections sel,
      final SettingsImage2DDataSelectionsExport sett) throws Exception {

    new ProgressUpdateTask(4) {

      @Override
      protected Boolean doInBackground2() throws Exception {
        SettingsSelections selections = sel;
        // copy selections
        boolean update = false;
        if (!img.equals(sel.getCurrentImage())) {
          selections = (SettingsSelections) sel.copy();
          selections.setCurrentImage(img, false);
          // update later
          update = true;
        }

        addProgressStep(1.0);

        double[] dataSelected = img.getSelectedDataAsArray(sett.isExportRaw(), true);
        // export the data
        // writer it to txt or xlsx
        if (sett.isWritingToClipboard()) {
          // update?
          if (update)
            selections.updateStatistics();

          addProgressStep(1.0);

          // save
          ArrayList<SettingsShapeSelection> tableRows = selections.getSelections();
          // write table rows
          Object[][] erows = new Object[tableRows.size() + 1][];
          // write title line
          erows[0] = SettingsShapeSelection.getTitleArrayExport();
          for (int r = 0; r < tableRows.size(); r++) {
            // write all tablerows
            erows[r + 1] = tableRows.get(r).getRowDataExport();

            addProgressStep(1.0 / tableRows.size());
          }

          String s = ClipboardWriter.dataToTabSepString(erows, true);
          s = s + "\n\nOnly selected, not excluded data points\n"
              + ClipboardWriter.dataToTabSepString(dataSelected);

          ClipboardWriter.writeToClipBoard(s);
          DialogLoggerUtil.showMessageDialogForTime(null, "Succeed",
              "Data copied to clipboard use paste function to retrieve data", 2000);

          addProgressStep(1.0);
        } else if (sett.getFileFormat() == FileType.XLSX) {
          // export to excel
          File file =
              new File(sett.getPath(), FileAndPathUtil.eraseFormat((sett.getFilename())) + ".xlsx");
          XSSFWorkbook wb = new XSSFWorkbook();

          addProgressStep(1.0);
          // save data and selections to excel
          selections.saveToExcel(sett, xwriter, wb);

          addProgressStep(1.0);
          // final
          xwriter.saveWbToFile(file, wb);
          xwriter.closeAllWorkbooks();

          addProgressStep(1.0);
          DialogLoggerUtil.showMessageDialog(null, "Succeed", "File(s) saved successfully");
        } else {
          // only write selected data to txt
          String filename = (FileAndPathUtil.getFormat(sett.getFilename()).length() > 0
              && sett.getFilename().length() > 3) ? sett.getFilename()
                  : FileAndPathUtil.eraseFormat((sett.getFilename())) + "."
                      + sett.getFileFormat().toString();
          File file = new File(sett.getPath(), filename);
          String sfile = FileAndPathUtil.applyMaxLength(file).getAbsolutePath();
          writer.openNewFileOutput(sfile);

          addProgressStep(1.0);
          // content
          writer.writeDataColumnToCurrentFile(dataSelected);

          addProgressStep(1.0);
          // end file
          writer.closeDatOutput();

          addProgressStep(1.0);
        }
        return true;
      }
    }.execute();
  }

  /**
   * xports a report on data manipulation operations
   * 
   * @param img
   * @param path
   * @param fileName
   * @throws InvalidFormatException
   * @throws IOException
   */
  public static void exportDataReportOnOperations(Image2D img, File path, String fileName)
      throws InvalidFormatException, IOException {
    SettingsImage2DDataExport sett = new SettingsImage2DDataExport();
    sett.setIsExportRaw(false);
    sett.getMode().equals(ModeData.ONLY_Y);

    File file = new File(path, FileAndPathUtil.eraseFormat(fileName) + ".xlsx");
    file = FileAndPathUtil.applyMaxLength(file);
    XSSFWorkbook wb = new XSSFWorkbook();
    // write image
    exportDataReportOnOperations(img, "Img", wb, sett);

    // final
    xwriter.saveWbToFile(file, wb);
    xwriter.closeAllWorkbooks();

    DialogLoggerUtil.showMessageDialogForTime(null, "Succeed", "File(s) saved successfully", 1000);
  }

  public static void exportDataReportOnOperations(Image2D img, String classifier, XSSFWorkbook wb,
      SettingsImage2DDataExport sett) throws InvalidFormatException, IOException {
    // stats
    /*
     * TODO XSSFSheet sheetStats = xwriter.getSheet(wb, classifier+"Stats");
     * xwriter.writeToCell(sheetStats, 0, 0, "Title:"); xwriter.writeToCell(sheetStats, 0, 1,
     * "Path:"); xwriter.writeToCell(sheetStats, 0, 2, "Lines:"); xwriter.writeToCell(sheetStats, 0,
     * 3, "Datapoints:"); xwriter.writeToCell(sheetStats, 0, 4, "All lines same length:");
     * xwriter.writeToCell(sheetStats, 0, 5, "DP per line:"); xwriter.writeToCell(sheetStats, 1, 0,
     * (img.getTitle())); xwriter.writeToCell(sheetStats, 1, 1,
     * (img.getSettImage().getRAWFilepath())); xwriter.writeToCell(sheetStats, 1, 2,
     * (img.getMaxLineCount())); xwriter.writeToCell(sheetStats, 1, 3, (img.getTotalDPCount()));
     * xwriter.writeToCell(sheetStats, 1, 4, (img.isAllLinesSameLength()));
     * xwriter.writeToCell(sheetStats, 1, 5, (img.getTotalDPCount()/img.getMaxLineCount())); //write
     * data: RAW XSSFSheet sheet = xwriter.getSheet(wb, classifier+"RAW"); Object[][] data =
     * img.toDataMatrix(sett, false, false, false); xwriter.writeToCell(sheet, 0, 0,
     * "Raw data; Ablated lines in columns"); xwriter.writeToCell(sheet, 0, 1, "Lines:");
     * xwriter.writeToCell(sheet, 0, 2, "Datapoints:"); xwriter.writeToCell(sheet, 0, 3,
     * "All lines same length:"); xwriter.writeToCell(sheet, 1, 1, (img.getLineCount()));
     * xwriter.writeToCell(sheet, 1, 2, (img.getTotalDPCount())); xwriter.writeToCell(sheet, 1, 3,
     * (img.isAllLinesSameLength())); xwriter.writeDataArrayToSheet(sheet, data,1,5); //write data:
     * blind boolean usesBlank = img.getOperations()!=null &&
     * img.getOperations().getBlankQuantifier()!=null &&
     * img.getOperations().getBlankQuantifier().isApplicable() &&
     * img.getOperations().getBlankQuantifier().isActive(); if(usesBlank) {
     * SettingsImage2DBlankSubtraction bl = img.getOperations().getBlankQuantifier(); sheet =
     * xwriter.getSheet(wb, classifier+"BlankRed"); data = img.toDataMatrixProcessed(sett, true,
     * false, false); // av and avperline int modetmp = bl.getMode();
     * bl.setMode(SettingsImage2DBlankSubtraction.MODE_AVERAGE); double av =
     * bl.getAverageIntensity(0, 0); // avperline
     * bl.setMode(SettingsImage2DBlankSubtraction.MODE_AVERAGE_PER_LINE); Object[] avperline = new
     * Object[img.getLineCount()]; for(int i=0; i<avperline.length; i++ ) { avperline[i] =
     * bl.getAverageIntensity(i, 0); } bl.setMode(modetmp); // write xwriter.writeToCell(sheet, 0,
     * 0,
     * "Blank reduced data by "+(bl.getModeData()==SettingsImage2DBlankSubtraction.MODE_DATA_START?
     * (bl.getQSameImage().isUseEnd()?
     * "start and end of image as blank":"start of image as blank"):"blank image"));
     * xwriter.writeToCell(sheet, 0, 1, modetmp==SettingsImage2DBlankSubtraction.MODE_AVERAGE?
     * "I-avg(blank)" : "I-avgInLine(blank)"); xwriter.writeToCell(sheet, 0, 2, "Avg overall:");
     * xwriter.writeToCell(sheet, 1, 2, av); xwriter.writeToCell(sheet, 0, 3, "Avg in line:"); //
     * write avperline xwriter.writeDataArrayToSheet(sheet, avperline, 1, 3, false); // data
     * xwriter.writeDataArrayToSheet(sheet, data,1,5);
     * 
     * // write raw data for this image if(bl.getModeData()==bl.MODE_DATA_IMG) {
     * exportDataReportOnOperations(bl.getImgBlank(), classifier+"Blank", wb, sett); } } //write
     * data: IS boolean usesIS = img.getOperations()!=null &&
     * img.getOperations().getInternalQuantifier()!=null &&
     * img.getOperations().getInternalQuantifier().isApplicable() &&
     * img.getOperations().getInternalQuantifier().isActive(); if(usesIS){
     * SettingsImage2DQuantifierIS is = img.getOperations().getInternalQuantifier(); sheet =
     * xwriter.getSheet(wb, classifier+"ISNorm"); data = img.toDataMatrixProcessed(sett, usesBlank,
     * true, false); xwriter.writeToCell(sheet, 0, 0,
     * "Internal standard: "+is.getImgIS().getTitle()); xwriter.writeToCell(sheet, 0, 1, usesBlank?
     * "(I-blank)/(IS-blank)" : "I/IS"); xwriter.writeDataArrayToSheet(sheet, data,1,5); // write
     * raw and blank data for this image exportDataReportOnOperations(is.getImgIS(),
     * classifier+"IS", wb, sett); } //write data: quantify if(img.getQuantifier()!=null &&
     * img.getQuantifier().isApplicable() && img.getQuantifier().isActive()) {
     * SettingsImage2DQuantifier q = img.getQuantifier(); sheet = xwriter.getSheet(wb,
     * classifier+"Quanty"); data = img.toDataMatrixProcessed(sett, usesBlank, usesIS, true);
     * xwriter.writeToCell(sheet, 0, 0, "Quantify with "+(q.getModeAsString())); switch(q.getMode())
     * { case SettingsImage2DQuantifier.MODE_LINEAR: xwriter.writeToCell(sheet, 0, 1,
     * "c = (I-a)/b"); case SettingsImage2DQuantifier.MODE_ONE_POINT: xwriter.writeToCell(sheet, 0,
     * 1, "c = I/avg(external) * factor"); xwriter.writeToCell(sheet, 0, 2, "factor = ");
     * xwriter.writeToCell(sheet, 1, 2,
     * ((SettingsImage2DQuantifierOnePoint)q).getConcentrationEx()); xwriter.writeToCell(sheet, 0,
     * 3, "avg(external) = "); xwriter.writeToCell(sheet, 1, 3,
     * ((SettingsImage2DQuantifierOnePoint)q).getImgEx().getAverageIntensity()); // write raw and
     * blank data for this image
     * exportDataReportOnOperations(((SettingsImage2DQuantifierOnePoint)q).getImgEx().getImg(),
     * classifier+"EX", wb, sett); case SettingsImage2DQuantifier.MODE_MULTIPLE_POINTS:
     * SettingsImage2DQuantifierMultiPoints multi = ((SettingsImage2DQuantifierMultiPoints)q);
     * multi.updateRegression(); SimpleRegression reg = multi.getRegression();
     * xwriter.writeToCell(sheet, 0, 1, "c = (I-intercept)/slope"); xwriter.writeToCell(sheet, 0, 2,
     * "intercept = "); xwriter.writeToCell(sheet, 1, 2, reg.getIntercept());
     * xwriter.writeToCell(sheet, 0, 3, "slope = "); xwriter.writeToCell(sheet, 1, 3,
     * reg.getSlope()); xwriter.writeToCell(sheet, 0, 4, "R^2 = "); xwriter.writeToCell(sheet, 1, 4,
     * reg.getRSquare()); // create one sheet with regression
     * xwriter.writeRegressionToSheet(xwriter.getSheet(wb, classifier+"EXregression"), reg,
     * multi.getRegressionData(), multi.getQuantifier()); } xwriter.writeDataArrayToSheet(sheet,
     * data,1,5); }
     */
  }



}
