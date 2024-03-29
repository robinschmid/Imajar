package net.rs.lamsi.general.settings.image.selection;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.data.twodimensional.XYIDataMatrix;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.datamodel.image.interf.DataCollectable2D;
import net.rs.lamsi.general.datamodel.image.interf.DataCollectable2DSett;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.general.settings.gui2d.SettingsBasicStroke;
import net.rs.lamsi.general.settings.image.operations.listener.IntensityProcessingChangedListener;
import net.rs.lamsi.general.settings.image.operations.quantifier.Image2DQuantifyStrategyImpl;
import net.rs.lamsi.general.settings.image.selection.SettingsShapeSelection.ROI;
import net.rs.lamsi.general.settings.image.selection.SettingsShapeSelection.SelectionMode;
import net.rs.lamsi.general.settings.image.visualisation.SettingsAlphaMap;
import net.rs.lamsi.general.settings.image.visualisation.SettingsAlphaMap.State;
import net.rs.lamsi.general.settings.importexport.SettingsImage2DDataSelectionsExport;
import net.rs.lamsi.utils.mywriterreader.XSSFExcelWriterReader;
import net.rs.lamsi.utils.useful.dialogs.DialogLinearRegression;

public class SettingsSelections extends Settings implements Serializable,
    Image2DQuantifyStrategyImpl, DataCollectable2DSett, IntensityProcessingChangedListener {
  // do not change the version!
  private static final long serialVersionUID = 1L;

  // list of selections, exclusions and info
  protected ArrayList<SettingsShapeSelection> selections;

  // Regression for quantification
  protected SimpleRegression regression = null;
  // mark regression for update
  protected boolean updateRegression = true;

  //
  private boolean hasExclusions = false, hasSelections = false;

  protected transient DataCollectable2D currentImg;
  // last time processing has changed for currentImg
  protected int lastProcessingChangeTime = -1;
  // version id for regression to track changes
  // for quantified images to register value processing changes
  protected int regressionVersionID = 1;

  // use alpha map for exclusions
  protected boolean alphaMapExclusionActive = false;

  // use blank reduction?
  protected boolean blankActive = false;

  public SettingsSelections() {
    super("SettingsSelection", "/Settings/Selections/", "setSelList");
  }

  @Override
  public void resetAll() {
    regression = null;
    updateRegression = true;
    lastProcessingChangeTime = -1;
    regressionVersionID = 1;
    alphaMapExclusionActive = false;
    blankActive = false;
  }

  // ##########################################################
  // List stuff
  public void addSelection(SettingsShapeSelection sel, boolean updateStats) {
    if (selections == null)
      selections = new ArrayList<SettingsShapeSelection>();
    selections.add(sel);

    hasSelections = hasSelections || sel.getMode().equals(SelectionMode.SELECT);

    if (sel.getMode().equals(SelectionMode.EXCLUDE)) {
      hasExclusions = true;
      // update all stats
      if (updateStats)
        updateStatistics();
    } else if (updateStats)
      updateStatistics(sel);
  }


  public void removeSelection(int index, boolean updateStats) {
    if (selections != null && index >= 0 && index < selections.size()) {
      SettingsShapeSelection sel = selections.remove(index);
      if (sel != null) {
        if (sel.getMode().equals(SelectionMode.EXCLUDE)) {
          hasExclusions = false;
          for (int i = 0; i < selections.size() && !hasExclusions; i++) {
            if (selections.get(i).getMode().equals(SelectionMode.EXCLUDE))
              hasExclusions = true;
          }
          // update all stats
          if (updateStats)
            updateStatistics();
        } else if (sel.getMode().equals(SelectionMode.SELECT)) {
          hasSelections = false;
          for (int i = 0; i < selections.size() && !hasSelections; i++) {
            if (selections.get(i).getMode().equals(SelectionMode.SELECT))
              hasSelections = true;
          }
        }
        // quantifier?
        if (sel.getRoi().equals(ROI.QUANTIFIER))
          updateRegression = true;
      }
    }
  }

  public void removeSelection(SettingsShapeSelection sel, boolean updateStats) {
    if (selections != null) {
      removeSelection(selections.indexOf(sel), updateStats);
    }
  }

  /**
   * removes all selections
   */
  public void removeAllSelections() {
    selections.clear();
    hasExclusions = false;
    hasSelections = false;
  }

  // ##########################################################
  // logic
  public void setCurrentImage(DataCollectable2D img) {
    setCurrentImage(img, true);
  }

  public void setCurrentImage(DataCollectable2D img, boolean checkUpdate) {
    // remove listener
    if (currentImg != null)
      currentImg.removeIntensityProcessingChangedListener(this);
    // add listener
    if (img != null)
      img.addIntensityProcessingChangedListener(this);

    // update stats
    boolean update = false;
    if (img != null && !img.equals(currentImg)) {
      currentImg = img;
      update = true;
    }

    // set current image
    if (selections != null) {
      for (int i = 0; i < selections.size(); i++) {
        SettingsShapeSelection s = selections.get(i);
        s.setCurrentImage(currentImg);
      }
    }
    if (update && checkUpdate)
      updateStatistics();
  }

  public DataCollectable2D getCurrentImage() {
    return currentImg;
  }

  /**
   * updates the statistics for all selections
   */
  public void updateStatistics() {
    if (currentImg != null && selections != null && selections.size() > 0) {
      clearStatistics();
      // TODO do statistics for all shape selections
      XYIDataMatrix data = currentImg.toXYIDataMatrix(false, true);
      float[][] x = data.getX();
      float[][] y = data.getY();
      double[][] z = data.getI();

      // alpha map settings
      SettingsAlphaMap alpha = currentImg.getImageGroup().getSettAlphaMap();

      float w = (float) currentImg.getAvgBlockWidth(false);
      float h = (float) currentImg.getAvgBlockHeight(false);

      // for each data point
      for (int l = 0; l < z.length; l++) {
        for (int dp = 0; dp < z[l].length; dp++) {
          // is value?
          double zv = z[l][dp];
          if (!Double.isNaN(zv)) {
            boolean isExcluded = isExcluded((float) x[l][dp], (float) y[l][dp], w, h);
            State dpstate = alpha.getMapValue(l, dp);

            // check dp for all selected rects with exclude information
            // and add to containing shapes
            for (int i = 0; i < selections.size(); i++) {
              SettingsShapeSelection s = selections.get(i);

              // check with the information whether it is excluded
              s.check(x[l][dp], y[l][dp], zv, w, h, isExcluded, dpstate);
            }
          }
        }
      }

      // finalise the process?
      for (int i = 0; i < selections.size(); i++) {
        SettingsShapeSelection s = selections.get(i);
        // calculates statistics and frees memory
        s.calculateStatistics();
      }

      hasExclusions = false;
      hasSelections = false;
      for (int i = 0; i < selections.size() && !hasExclusions; i++) {
        if (selections.get(i).getMode().equals(SelectionMode.EXCLUDE))
          hasExclusions = true;
        if (selections.get(i).getMode().equals(SelectionMode.SELECT))
          hasSelections = true;
      }

      // update quantifier
      updateRegression = true;
    }
  }

  /**
   * clear data of all selections
   */
  private void clearStatistics() {
    if (selections != null) {
      selections.stream().forEach(s -> s.clearData());
    }
  }

  /**
   * updates the statistics for one selection (use general update method if you want to update all)
   */
  public void updateStatistics(SettingsShapeSelection s) {
    if (currentImg != null && s != null) {
      s.clearData();
      // TODO do statistics for all shape selections
      XYIDataMatrix data = currentImg.toXYIDataMatrix(false, true);
      float[][] x = data.getX();
      float[][] y = data.getY();
      double[][] z = data.getI();

      // alpha map settings
      SettingsAlphaMap alpha = currentImg.getImageGroup().getSettAlphaMap();

      float w = (float) currentImg.getAvgBlockWidth(false);
      float h = (float) currentImg.getAvgBlockHeight(false);

      // for each data point
      for (int l = 0; l < z.length; l++) {
        for (int dp = 0; dp < z[l].length; dp++) {
          // is value?
          double zv = z[l][dp];
          if (!Double.isNaN(zv)) {
            boolean isExcluded = isExcluded((float) x[l][dp], (float) y[l][dp], w, h);
            State dpstate = alpha.getMapValue(l, dp);
            // check for s
            // and add to containing shapes
            // check with the information whether it is excluded
            s.check(x[l][dp], y[l][dp], zv, w, h, isExcluded, dpstate);
          }
        }
      }

      // finalise the process?
      s.calculateStatistics();

      // quantifier?
      if (s.getRoi().equals(ROI.QUANTIFIER))
        updateRegression = true;
    }
  }

  /**
   * checks if the point is inside a exclusion shape
   * 
   * @param x coordinate in the given processed data space (e.g. micro meters)
   * @param y coordinate in the given processed data space (e.g. micro meters)
   * @return
   */
  public boolean isExcluded(float x, float y, float w, float h) {
    if (hasExclusions) {
      for (SettingsShapeSelection s : selections) {
        // only exclusions
        if (s.getMode().equals(SelectionMode.EXCLUDE)) {
          if (s.contains(x + w / 2.f, y + h / 2.f)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * checks if the point is inside any shape
   * 
   * @param x coordinate in the given processed data space (e.g. micro meters)
   * @param y coordinate in the given processed data space (e.g. micro meters)
   * @return
   */
  public boolean isInsideShape(float x, float y, float w, float h) {
    if (selections != null && selections.size() > 0) {
      for (SettingsShapeSelection s : selections) {
        if (s.contains(x + w / 2.f, y + h / 2.f)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * checks if the point is inside a selection shape and optional for exclusion
   * 
   * @param x coordinate in the given processed data space (e.g. micro meters)
   * @param y coordinate in the given processed data space (e.g. micro meters)
   * @return always true if nothing is selected or excluded
   */
  public boolean isSelected(float x, float y, float w, float h, boolean checkForExclusion) {
    // special case if no sleections and exclusions
    if (!hasSelections && (!checkForExclusion || !hasExclusions))
      return true;

    boolean state = false;
    // is selected?
    if (hasSelections) {
      for (int i = 0; i < selections.size() && !state; i++) {
        SettingsShapeSelection s = selections.get(i);
        // only exclusions
        if (s.getMode().equals(SelectionMode.SELECT))
          if (s.contains(x + w / 2.f, y + h / 2.f))
            state = true;
      }
    }

    if (state) {
      // check for exclusions?
      if (checkForExclusion && hasExclusions) {
        for (int i = 0; i < selections.size(); i++) {
          SettingsShapeSelection s = selections.get(i);
          // only exclusions
          if (s.getMode().equals(SelectionMode.EXCLUDE))
            if (s.contains(x + w / 2.f, y + h / 2.f))
              return false;
        }
      }
      // not excluded - return state - that is true
      return true;
    } else
      return false;
  }


  // ##########################################################
  // xml input/output
  @Override
  public void appendSettingsValuesToXML(Element elParent, Document doc) {
    toXML(elParent, doc, "blankActive", blankActive);
    if (selections != null) {
      for (int i = 0; i < selections.size(); i++) {
        SettingsShapeSelection s = selections.get(i);
        s.appendSettingsToXML(elParent, doc);
      }
      // standard colors
      Map<SelectionMode, Color> map = SettingsShapeSelection.getColors();
      for (Map.Entry<SelectionMode, Color> e : map.entrySet()) {
        toXML(elParent, doc, "stdcolor" + e.getKey(), e.getValue(), "selMode", e.getKey());
      }

      // strokes
      Map<ROI, SettingsBasicStroke> strokes = SettingsShapeSelection.getStrokes();
      for (Map.Entry<ROI, SettingsBasicStroke> e : strokes.entrySet()) {
        toXML(elParent, doc, "stdstroke" + e.getKey(), e.getValue(), "ROI", e.getKey());
      }
    }
  }

  @Override
  public void loadValuesFromXML(Element el, Document doc) {
    // standard colors
    Map<SelectionMode, Color> map = SettingsShapeSelection.getColors();
    Map<ROI, SettingsBasicStroke> strokes = SettingsShapeSelection.getStrokes();

    double xu = 0, yu = 0;
    double xlower = Double.NaN, ylower = Double.NaN;
    NodeList list = el.getChildNodes();
    for (int i = 0; i < list.getLength(); i++) {
      if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
        Element nextElement = (Element) list.item(i);
        String paramName = nextElement.getNodeName();
        if (paramName.startsWith("stdcolor")) {
          // load standard colors
          SelectionMode mode = SelectionMode.valueOf(nextElement.getAttribute("selMode"));
          Color c = colorFromXML(nextElement);
          map.put(mode, c);
        } else if (paramName.startsWith("stdstroke")) {
          // load standard stroke
          ROI roi = ROI.valueOf(nextElement.getAttribute("ROI"));
          SettingsBasicStroke s = strokeFromXML(nextElement);
          strokes.put(roi, s);
        } else if (paramName.equals("blankActive"))
          blankActive = booleanFromXML(nextElement);
        // is a settings node?
        else if (isSettingsNode(nextElement, SettingsShapeSelection.class)) {
          if (selections == null)
            selections = new ArrayList<SettingsShapeSelection>();
          // how to load from xml????
          SettingsShapeSelection ns = SettingsShapeSelection.loadSettingsFromXML(nextElement, doc);
          if (ns != null)
            selections.add(ns);
        }
      }
    }
  }



  /**
   * are exclusions present
   * 
   * @return
   */
  public boolean hasExclusions() {
    return hasExclusions;
  }

  /**
   * are selections present
   * 
   * @return
   */
  public boolean hasSelections() {
    return hasSelections;
  }

  public ArrayList<SettingsShapeSelection> getSelections() {
    return selections;
  }

  /**
   * counts the selections of a specific selection mode
   * 
   * @param select
   * @return
   */
  public int count(SelectionMode select) {
    if (selections == null)
      return 0;

    int c = 0;
    for (Iterator iterator = selections.iterator(); iterator.hasNext();) {
      SettingsShapeSelection s = (SettingsShapeSelection) iterator.next();
      if (s.getMode().equals(select))
        c++;
    }
    return c;
  }

  /**
   * counts the selections of a specific ROI mode
   * 
   * @param roi
   * @return
   */
  public int count(ROI roi) {
    if (selections == null)
      return 0;

    int c = 0;
    for (Iterator iterator = selections.iterator(); iterator.hasNext();) {
      SettingsShapeSelection s = (SettingsShapeSelection) iterator.next();
      if (s.getRoi().equals(roi))
        c++;
    }
    return c;
  }

  /**
   * update on the run save all to excel 1 table 2 selected, excluded data as array 3 selected,
   * excluded data as matrix 4 each selection as matrix
   * 
   * @param sett
   * @param xwriter
   * @param wb
   */
  public void saveToExcel(SettingsImage2DDataSelectionsExport sett, XSSFExcelWriterReader xwriter,
      XSSFWorkbook wb) {
    if (currentImg != null && selections != null && selections.size() > 0) {

      // create some sheets
      XSSFSheet shSummary = sett.isSummary() ? xwriter.getSheet(wb, "table") : null;
      XSSFSheet shShapesData = sett.isShapeData() ? xwriter.getSheet(wb, "DataShapes") : null;
      XSSFSheet shDEF = sett.isDefinitions() ? xwriter.getSheet(wb, "DEFINITIONS") : null;
      XSSFSheet shArray = sett.isArrays() ? xwriter.getSheet(wb, "SelectExcl") : null;
      XSSFSheet shX = sett.isX() ? xwriter.getSheet(wb, "X") : null;
      XSSFSheet shY = sett.isY() ? xwriter.getSheet(wb, "Y") : null;
      XSSFSheet shZ = sett.isZ() ? xwriter.getSheet(wb, "Z") : null;
      XSSFSheet shExMat = sett.isImgEx() ? xwriter.getSheet(wb, "Excl IMG") : null;
      XSSFSheet shSelMat = sett.isImgSel() ? xwriter.getSheet(wb, "Select IMG") : null;
      XSSFSheet shSelNonExMat = sett.isImgSelNEx() ? xwriter.getSheet(wb, "SelectNExcl IMG") : null;

      int headerrows = 6;

      // two sheet for each shape
      XSSFSheet[] shapeSel = null, shapeSelNonEx = null;
      int[] cdpShape = null, cdpShapeSelNonEx = null;
      if (sett.isShapes() || sett.isShapesSelNEx()) {
        if (sett.isShapes()) {
          shapeSel = new XSSFSheet[selections.size()];
          cdpShape = new int[selections.size()];
        }
        if (sett.isShapesSelNEx()) {
          shapeSelNonEx = new XSSFSheet[selections.size()];
          cdpShapeSelNonEx = new int[selections.size()];
        }
        // create all sheets for shapes: first select, then exclude, then info
        for (int m = 0; m < 3; m++) {
          int c = 1;
          for (int i = 0; i < selections.size(); i++) {
            SettingsShapeSelection s = selections.get(i);
            if ((m == 0 && s.getMode().equals(SelectionMode.SELECT))
                || (m == 1 && s.getMode().equals(SelectionMode.EXCLUDE))
                || (m == 2 && s.getMode().equals(SelectionMode.INFO))) {
              // generate title
              String title = s.getMode().getShortTitle();
              // create sheets
              if (sett.isShapes())
                shapeSel[i] = xwriter.getSheet(wb, title + c);

              // only for selected
              if (m == 0 && sett.isShapesSelNEx())
                shapeSelNonEx[i] = xwriter.getSheet(wb, title + "NExcl" + c);

              if (sett.isShapeData()) {
                // header for shapes summary
                xwriter.writeToCell(shShapesData, i, 1, "" + i);
                xwriter.writeToCell(shShapesData, i, 2, s.getMode().getShortTitle() + c);
              }

              // increment
              c++;
            }
          }
        }
      }

      // write header
      if (sett.isShapeData())
        xwriter.writeToCell(shShapesData, 0, 0,
            "All data points that were used to calculate statistics for each shape.");

      // write header
      xwriter.writeToCell(shArray, 0, 0, "Excluded");
      xwriter.writeToCell(shArray, 1, 0, "Selected");
      xwriter.writeToCell(shArray, 2, 0, "Selected (-exclusions)");

      // do statistics for all shape selections
      XYIDataMatrix data = currentImg.toXYIDataMatrix(false, true);
      float[][] x = data.getX();
      float[][] y = data.getY();
      double[][] z = data.getI();

      // write xyz matrix
      if (sett.isX())
        xwriter.writeDataArrayToSheet(shX, x, 0, 0, true);
      if (sett.isY())
        xwriter.writeDataArrayToSheet(shY, y, 0, 0, true);
      if (sett.isZ())
        xwriter.writeDataArrayToSheet(shZ, z, 0, 0, true);
      //
      boolean usey = y[0] == y[1];
      // count excluded and selected
      int csel = 0;
      int cex = 0;
      int cselnonex = 0;

      // width height of data points
      float w = (float) currentImg.getAvgBlockWidth(false);
      float h = (float) currentImg.getAvgBlockHeight(false);

      // alpha map settings
      SettingsAlphaMap alpha = currentImg.getImageGroup().getSettAlphaMap();

      // for each line
      for (int l = 0; l < x.length; l++) {
        // for each data point
        for (int d = 0; d < x[l].length; d++) {
          if (!Double.isNaN(z[l][d])) {
            // is excluded?
            boolean isExcluded = isExcluded((float) x[l][d], (float) y[l][d], w, h);
            // write to excluded
            if (isExcluded) {
              cex++;
              // write as matrix
              if (sett.isImgEx())
                xwriter.writeToCell(shExMat, l, d, z[l][d]);
              // write as array
              if (sett.isArrays())
                xwriter.writeToCell(shArray, 0, cex, z[l][d]);
            }


            // check dp for all selected rects with exclude information
            // and add to containing shapes
            boolean isSelected = false;
            for (int i = 0; i < selections.size(); i++) {
              SettingsShapeSelection s = selections.get(i);
              // check with the information that it is excluded
              boolean inside =
                  s.check(x[l][d], y[l][d], z[l][d], w, h, isExcluded, alpha.getMapValue(l, d));

              isSelected = (inside && s.getMode().equals(SelectionMode.SELECT)) || isSelected;
              // write to
              if (inside) {
                // write to shape without ex
                // write as matrix
                if (sett.isShapes()) {
                  xwriter.writeToCell(shapeSel[i], l, d + headerrows, z[l][d]);
                  // write in row
                  xwriter.writeToCell(shapeSel[i], cdpShape[i], 5, z[l][d]);
                }
                // write to shape data summary
                if (sett.isShapeData() && !s.getMode().equals(SelectionMode.SELECT)) {
                  xwriter.writeToCell(shShapesData, i, cdpShape[i] + 4, z[l][d]);
                }
                cdpShape[i]++;

                // only for selected
                if (!isExcluded && s.getMode().equals(SelectionMode.SELECT)) {
                  if (sett.isShapesSelNEx()) {
                    // write to shape with regards to exclusion
                    // write as matrix
                    xwriter.writeToCell(shapeSelNonEx[i], l, d + headerrows, z[l][d]);

                    // write in row
                    xwriter.writeToCell(shapeSelNonEx[i], cdpShapeSelNonEx[i], 5, z[l][d]);
                  }
                  // write to shape data summary
                  if (sett.isShapeData()) {
                    xwriter.writeToCell(shShapesData, i, cdpShapeSelNonEx[i] + 4, z[l][d]);
                  }
                  cdpShapeSelNonEx[i]++;
                }
              }
            }
            // selected write to array and matrix
            if (isSelected) {
              csel++;
              // array
              // write as matrix
              if (sett.isImgSel())
                xwriter.writeToCell(shSelMat, l, d, z[l][d]);
              // write as array
              if (sett.isArrays())
                xwriter.writeToCell(shArray, 1, csel, z[l][d]);

              if (!isExcluded) {
                cselnonex++;
                // write as matrix
                if (sett.isImgSelNEx())
                  xwriter.writeToCell(shSelNonExMat, l, d, z[l][d]);
                // write as array
                if (sett.isArrays())
                  xwriter.writeToCell(shArray, 2, cselnonex, z[l][d]);
              }
            }
          }
        }
      }

      // #####################################################
      // finalise the statistics
      for (int i = 0; i < selections.size(); i++) {
        SettingsShapeSelection s = selections.get(i);
        // calculates statistics and frees memory
        s.calculateStatistics();
      }

      // #####################################################
      // write summary table
      xwriter.writeToCell(shSummary, 0, 0, "Summary of all rects.");
      // write title line
      xwriter.writeDataArrayToSheet(shSummary, SettingsShapeSelection.getTitleArrayExport(), 0, 1,
          false);
      // write data rows
      for (int r = 0; r < selections.size(); r++) {
        // write all tablerows
        Object[] row = selections.get(r).getRowDataExport(isAlphaMapExclusionActive());
        xwriter.writeDataArrayToSheet(shSummary, row, 0, 2 + r, false);

        // for all shape sheets:
        // write title line
        // write data row to all shape sheets
        if (sett.isShapes()) {
          xwriter.writeDataArrayToSheet(shapeSel[r], SettingsShapeSelection.getTitleArrayExport(),
              0, 1, false);
          xwriter.writeDataArrayToSheet(shapeSel[r], row, 0, 2, false);
          // data
          xwriter.writeToCell(shapeSel[r], 0, 4, "All data points");
        }

        // only for selections
        if (sett.isShapesSelNEx() && selections.get(r).getMode().equals(SelectionMode.SELECT)) {
          xwriter.writeDataArrayToSheet(shapeSelNonEx[r],
              SettingsShapeSelection.getTitleArrayExport(), 0, 1, false);
          xwriter.writeDataArrayToSheet(shapeSelNonEx[r], row, 0, 2, false);
          // data
          xwriter.writeToCell(shapeSelNonEx[r], 0, 4, "All data points");
        }
      }

      // write definitions sheet
      if (sett.isDefinitions()) {
        int i = 0;
        xwriter.writeToCell(shDEF, 0, 0 + i, "table");
        xwriter.writeToCell(shDEF, 1, 0 + i++,
            "Table with full statistics. I99 is the 99th percentile value comparable to the median as the 50th percentile value");
        xwriter.writeToCell(shDEF, 0, 0 + i, "DataShapes");
        xwriter.writeToCell(shDEF, 1, 0 + i++,
            "All data points which were used to calculate statistics of each shape. (for selections only selected, non excluded data points)");
        xwriter.writeToCell(shDEF, 0, 0 + i, "DEFINITIONS");
        xwriter.writeToCell(shDEF, 1, 0 + i++, "This sheet holds some explanations");
        xwriter.writeToCell(shDEF, 0, 0 + i, "SelectExcl");
        xwriter.writeToCell(shDEF, 1, 0 + i++,
            "Arrays of values for all values: 1. Excluded, 2. Selected and 3. Selected (in regards to exclusion). 3. is used to calculate statistics for selections. 2. for info and exclusion shapes");
        xwriter.writeToCell(shDEF, 0, 0 + i, "Excl IMG");
        xwriter.writeToCell(shDEF, 1, 0 + i++, "Data matrix of all excluded data points");
        xwriter.writeToCell(shDEF, 0, 0 + i, "Select IMG");
        xwriter.writeToCell(shDEF, 1, 0 + i++, "Data matrix of all selected data points");
        xwriter.writeToCell(shDEF, 0, 0 + i, "SelectNExcl IMG");
        xwriter.writeToCell(shDEF, 1, 0 + i++,
            "Data matrix of all selected non excluded data points");
        xwriter.writeToCell(shDEF, 0, 0 + i, "Sel / Excl / Info#");
        xwriter.writeToCell(shDEF, 1, 0 + i++, "Data matrix cut out of this shape");
        xwriter.writeToCell(shDEF, 0, 0 + i, "SelNExcl#");
        xwriter.writeToCell(shDEF, 1, 0 + i++,
            "Data matrix cut out of this selection shape in regards to exclusions (excluded data points are left out)");
      }
    }
  }

  /**
   * create alpha map
   */
  public void markAlphaMap(SettingsAlphaMap sett) {
    if (currentImg != null) {
      if (selections != null && selections.size() > 0) {
        // do statistics for all shape selections
        XYIDataMatrix data = currentImg.toXYIDataMatrix(false, true);
        float[][] x = data.getX();
        float[][] y = data.getY();
        double[][] z = data.getI();

        State[][] map = sett.getMap();

        boolean isnew = map == null;
        if (isnew)
          map = new State[z.length][];

        float w = (float) currentImg.getAvgBlockWidth(false);
        float h = (float) currentImg.getAvgBlockHeight(false);

        // for each line
        for (int l = 0; l < x.length; l++) {
          if (isnew)
            map[l] = new State[z[l].length];
          // for each data point
          for (int d = 0; d < x[l].length; d++) {
            if (!Double.isNaN(z[l][d])) {
              // is excluded?
              boolean inside = isInsideShape((float) x[l][d], (float) y[l][d], w, h);
              // mark dp
              if (inside)
                map[l][d] = isnew ? State.MARKED_ALPHA_TRUE : map[l][d].toMarked();
              else {
                if (isnew)
                  map[l][d] = State.ALPHA_TRUE;
              }
            } else {
              if (isnew)
                map[l][d] = State.NO_DP;
            }
          }
        }
        sett.setMap(map);
      }
    }
  }


  // #################################################################
  // quantifier business
  /**
   * auto order all quantifier
   */
  public void autoOrderQuantifier() {
    // TODO Auto-generated method stub

  }


  /**
   * updates and returns the regression for quantification
   * 
   * @return
   */
  public SimpleRegression getRegression() {
    // create new regression
    if (updateRegression || regression == null) {
      regression = new SimpleRegression(true);
      regression.addData(getRegressionData());
      // track version of regression for quantified images to register changes
      if (regressionVersionID == Integer.MAX_VALUE)
        regressionVersionID = 1;
      regressionVersionID++;
      // unset
      updateRegression = false;
      //
      return regression;
    } else
      return regression;
  }

  /**
   * the regression data as [n - data points][2: concentration, avg intensity] can be used for the
   * {@link DialogLinearRegression}
   * 
   * @return
   */
  public double[][] getRegressionData() {
    int n = countQuantifier();
    if (n > 0) {
      // n data points
      double[][] dat = new double[n][2];
      // concentration and avg
      int c = 0;
      for (SettingsShapeSelection s : selections) {
        if (s.getRoi().equals(ROI.QUANTIFIER)) {
          dat[c][0] = s.getConcentration();
          dat[c][1] = s.getDefaultTableRow(isAlphaMapExclusionActive()).getAvg();
          c++;
        }
      }
      return dat;
    } else
      return null;
  }

  /**
   * number of quantifiers set as ROI in a SettingsShapeSelection
   * 
   * @return
   */
  public int countQuantifier() {
    if (selections == null)
      return 0;
    int c = 0;
    for (SettingsShapeSelection s : selections)
      if (s.getRoi().equals(ROI.QUANTIFIER))
        c++;
    return c;
  }

  @Override
  public void fireIntensityProcessingChanged(DataCollectable2D img) {
    // update if processing of current image has changed
    // e.g. after internal standard was applied or anything else
    if (lastProcessingChangeTime != img.getLastIProcChangeTime()) {
      lastProcessingChangeTime = img.getLastIProcChangeTime();
      if (img.equals(currentImg))
        updateStatistics();
      else
        img.removeIntensityProcessingChangedListener(this);
    }
  }

  public int getRegressionVersionID() {
    return regressionVersionID;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof SettingsSelections))
      return false;
    else {
      SettingsSelections that = (SettingsSelections) o;
      if (this.getSelections() == that.getSelections())
        return true;
      if (this.getSelections() == null || that.getSelections() == null)
        return false;
      if (this.getSelections().size() != that.getSelections().size())
        return false;
      if (this.isAlphaMapExclusionActive() != that.isAlphaMapExclusionActive())
        return false;

      for (int i = 0; i < selections.size(); i++)
        selections.get(i).equals(that.getSelections().get(i));
      return true;
    }
  }


  // ###############################################################################
  // blank
  public void setBlankActive(boolean selected) {
    blankActive = selected;
  }

  public boolean isBlankActive() {
    return blankActive;
  }

  /**
   * 
   * @return
   */
  public boolean hasBlankROI() {
    if (selections == null || selections.isEmpty())
      return false;

    for (SettingsShapeSelection s : selections) {
      if (s.getRoi().isBlank())
        return true;
    }
    return false;
  }

  @Override
  public double calcIntensity(Image2D img, int line, int dp, double intensity) {
    if (isBlankActive() && hasBlankROI()) {
      // get direction: columns or rows?
      // get average per col or row
      double average = 0;
      int c = 0;
      for (SettingsShapeSelection s : selections) {
        if (s.getRoi().isBlank()) {
          average += s.getStatsRegardingExclusions().getAvg();
          c++;
        }
      }
      if (c != 0)
        average = average / c;
      // subtract
      return intensity - average;
    } else
      return intensity;
  }

  @Override
  public void applyToImage(Collectable2D c) throws Exception {
    super.applyToImage(c);
    SettingsSelections sel = (SettingsSelections) c.getSettingsByClass(SettingsSelections.class);
    if (sel != null && c instanceof Image2D) {
      sel.setCurrentImage((Image2D) c);
      sel.updateStatistics();
    }
  }

  public boolean isAlphaMapExclusionActive() {
    return alphaMapExclusionActive;
  }

  public void setAlphaMapExclusionActive(boolean alphaMapExclusionActive) {
    this.alphaMapExclusionActive = alphaMapExclusionActive;
  }

}


