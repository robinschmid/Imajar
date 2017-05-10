package net.rs.lamsi.general.settings.image.selection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.data.twodimensional.XYIData2D;
import net.rs.lamsi.general.datamodel.image.data.twodimensional.XYIDataMatrix;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.general.settings.image.operations.listener.IntensityProcessingChangedListener;
import net.rs.lamsi.general.settings.image.selection.SettingsShapeSelection.ROI;
import net.rs.lamsi.general.settings.image.selection.SettingsShapeSelection.SelectionMode;
import net.rs.lamsi.general.settings.image.visualisation.SettingsAlphaMap;
import net.rs.lamsi.general.settings.importexport.SettingsImage2DDataSelectionsExport;
import net.rs.lamsi.general.settings.interf.Image2DSett;
import net.rs.lamsi.utils.mywriterreader.XSSFExcelWriterReader;
import net.rs.lamsi.utils.useful.dialogs.DialogLinearRegression;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SettingsSelections extends Settings implements Serializable, Image2DSett, IntensityProcessingChangedListener {
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

	protected transient Image2D currentImg;
	// last time processing has changed for currentImg
	protected int lastProcessingChangeTime = -1;
	// version id for regression to track changes 
	// for quantified images to register value processing changes
	protected int regressionVersionID = 1;

	public SettingsSelections() {
		super("SettingsSelection", "/Settings/Selections/", "setSelList"); 
	} 

	@Override
	public void resetAll() {  
		regression = null;
		updateRegression = true;
		lastProcessingChangeTime = -1;
		regressionVersionID = 1;
	}

	//##########################################################
	// List stuff
	public void addSelection(SettingsShapeSelection sel, boolean updateStats) { 
		if(selections==null)
			selections = new ArrayList<SettingsShapeSelection>();
		selections.add(sel); 

		hasSelections = hasSelections || sel.getMode().equals(SelectionMode.SELECT);

		if(sel.getMode().equals(SelectionMode.EXCLUDE)) {
			hasExclusions = true;
			// update all stats
			if(updateStats)
				updateStatistics();
		}
		else if(updateStats) 
			updateStatistics(sel);
	}


	public void removeSelection(int index, boolean updateStats) {
		if(selections!=null && index>=0 && index<selections.size()) {
			SettingsShapeSelection sel = selections.remove(index);
			if(sel!=null) {
				if(sel.getMode().equals(SelectionMode.EXCLUDE)) {
					hasExclusions = false;
					for(int i=0; i<selections.size() && !hasExclusions; i++) {
						if(selections.get(i).getMode().equals(SelectionMode.EXCLUDE))
							hasExclusions = true;
					}
					// update all stats
					if(updateStats)
						updateStatistics();
				}
				else if(sel.getMode().equals(SelectionMode.SELECT)) {
					hasSelections = false;
					for(int i=0; i<selections.size() && !hasSelections; i++) {
						if(selections.get(i).getMode().equals(SelectionMode.SELECT))
							hasSelections = true;
					}
				}
				// quantifier?
				if(sel.getRoi().equals(ROI.QUANTIFIER))
					updateRegression = true;
			}
		}
	}
	
	public void removeSelection(SettingsShapeSelection sel, boolean updateStats) {
		if(selections!=null) {
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
	//##########################################################
	// logic
	public void setCurrentImage(Image2D img) {
		setCurrentImage(img, true);
	}
	public void setCurrentImage(Image2D img, boolean checkUpdate) {
		// remove listener
		if(currentImg!=null)
			currentImg.removeIntensityProcessingChangedListener(this);
		// add listener
		if(img!=null)
			img.addIntensityProcessingChangedListener(this);
		
		// update stats
		boolean update = false;
		if(img!=null && !img.equals(currentImg)) {
			currentImg = img;
			update = true;
		}

		// set current image
		if(selections!=null) {
			for(int i=0; i<selections.size(); i++) {
				SettingsShapeSelection s = selections.get(i);
				s.setCurrentImage(currentImg);
			}
		}
		if(update && checkUpdate)
			updateStatistics();
	}
	public Image2D getCurrentImage() {
		return currentImg;
	}

	/**
	 * updates the statistics for all selections
	 */
	public void updateStatistics() {
		if(currentImg!=null && selections!=null && selections.size()>0) {
			// TODO do statistics for all shape selections
			XYIData2D data = currentImg.toXYIArray(false, true);
			double[] x = data.getX();
			double[] y = data.getY();
			double[] z = data.getI();
			
			float w = (float)currentImg.getMaxBlockWidth();
			float h = (float)currentImg.getMaxBlockHeight();

			// for each data point
			for(int d=0; d<x.length; d++) {
				boolean isExcluded = isExcluded((float)x[d], (float)y[d], w, h); 

				// check dp for all selected rects with exclude information
				// and add to containing shapes
				for(int i=0; i<selections.size(); i++) {
					SettingsShapeSelection s = selections.get(i);

					// check with the information that it is excluded
					s.check(x[d]+w,y[d]+h,z[d], w, h, isExcluded);
				}
			}

			// finalise the process?
			for(int i=0; i<selections.size(); i++) {
				SettingsShapeSelection s = selections.get(i);
				// calculates statistics and frees memory
				s.calculateStatistics();
			}
			// update quantifier
			updateRegression = true;
		}
	}


	/**
	 * updates the statistics for one selection (use general update method if you want to update all)
	 */
	public void updateStatistics(SettingsShapeSelection s) {
		if(currentImg!=null && s!=null) {
			// TODO do statistics for all shape selections
			XYIData2D data = currentImg.toXYIArray(false, true);
			double[] x = data.getX();
			double[] y = data.getY();
			double[] z = data.getI();
			
			float w = (float)currentImg.getMaxBlockWidth();
			float h = (float)currentImg.getMaxBlockHeight();

			// for each data point
			for(int d=0; d<x.length; d++) {
				boolean isExcluded = isExcluded((float)x[d], (float)y[d], w, h); 
				// check for s
				// and add to containing shapes 
				s.check(x[d],y[d],z[d], w, h, isExcluded);
			}

			// finalise the process?
			s.calculateStatistics();

			// quantifier?
			if(s.getRoi().equals(ROI.QUANTIFIER))
				updateRegression = true;
		}
	}

	/**
	 * checks if the point is inside a exclusion shape
	 * @param x coordinate in the given processed data space (e.g. micro meters)
	 * @param y coordinate in the given processed data space (e.g. micro meters)
	 * @return
	 */
	public boolean isExcluded(float x, float y, float w, float h) {
		if(hasExclusions) {
			for(SettingsShapeSelection s : selections) {
				// only exclusions
				if(s.getMode().equals(SelectionMode.EXCLUDE)) {
					if(s.contains(x+w/2.f, y+h/2.f)) {
						return true; 
					}
				}
			}
		}
		return false;
	}
	/**
	 * checks if the point is inside any shape
	 * @param x coordinate in the given processed data space (e.g. micro meters)
	 * @param y coordinate in the given processed data space (e.g. micro meters)
	 * @return
	 */
	public boolean isInsideShape(float x, float y, float w, float h) {
		if(selections!=null && selections.size()>0) {
			for(SettingsShapeSelection s : selections) {
				if(s.contains(x+w/2.f, y+h/2.f)) {
					return true; 
				}
			}
		}
		return false;
	}
	/**
	 * checks if the point is inside a selection shape and optional for exclusion
	 * @param x coordinate in the given processed data space (e.g. micro meters)
	 * @param y coordinate in the given processed data space (e.g. micro meters)
	 * @return
	 */
	public boolean isSelected(float x, float y, float w, float h, boolean checkForExclusion) {
		boolean state = false;
		// is selected?
		if(hasSelections) {
			for(int i=0; i<selections.size() && !state; i++) {
				SettingsShapeSelection s = selections.get(i);
				// only exclusions
				if(s.getMode().equals(SelectionMode.SELECT))
					if(s.contains(x+w/2.f, y+h/2.f))
						state = true; 
			}
		}

		if(state) {
			// check for exclusions?
			if(checkForExclusion && hasExclusions) {
				for(int i=0; i<selections.size(); i++) {
					SettingsShapeSelection s = selections.get(i);
					// only exclusions
					if(s.getMode().equals(SelectionMode.EXCLUDE))
						if(s.contains(x+w/2.f, y+h/2.f))
							return false; 
				}
			}
			// not excluded - return state - that is true
			return true;
		}
		else return false;
	}


	//##########################################################
	// xml input/output
	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
		//toXML(elParent, doc, "xrange.lower", xrange.getLowerBound()); 
		if(selections!=null) {
			for(int i=0; i<selections.size(); i++) {
				SettingsShapeSelection s = selections.get(i);
				s.appendSettingsToXML(elParent, doc);
			}
		}
	}

	@Override
	public void loadValuesFromXML(Element el, Document doc) {
		double xu=0, yu=0;
		double xlower = Double.NaN, ylower = Double.NaN;
		NodeList list = el.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element nextElement = (Element) list.item(i);
				String paramName = nextElement.getNodeName();
				// is a settings node?
				if(isSettingsNode(nextElement, SettingsShapeSelection.class)) {
					if(selections==null)
						selections = new ArrayList<SettingsShapeSelection>();
					// how to load from xml????
					SettingsShapeSelection ns = SettingsShapeSelection.loadSettingsFromXML(nextElement, doc);
					if(ns!=null)
						selections.add(ns);
				}
			}
		}
	}



	/**
	 * are exclusions present
	 * @return
	 */
	public boolean hasExclusions() {
		return hasExclusions;
	}
	/**
	 * are selections present
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
	 * @param select
	 * @return
	 */
	public int count(SelectionMode select) {
		if(selections==null)
			return 0;

		int c = 0;
		for (Iterator iterator = selections.iterator(); iterator.hasNext();) {
			SettingsShapeSelection s = (SettingsShapeSelection) iterator.next();
			if(s.getMode().equals(select))
				c++;
		}
		return c;
	}

	/**
	 * counts the selections of a specific ROI mode
	 * @param roi
	 * @return
	 */
	public int count(ROI roi) {
		if(selections==null)
			return 0;

		int c = 0;
		for (Iterator iterator = selections.iterator(); iterator.hasNext();) {
			SettingsShapeSelection s = (SettingsShapeSelection) iterator.next();
			if(s.getRoi().equals(roi))
				c++;
		}
		return c;
	}
	/**
	 * update on the run
	 * save all to excel
	 * 1 table
	 * 2 selected, excluded data as array
	 * 3 selected, excluded data as matrix
	 * 4 each selection as matrix
	 * @param sett 
	 * @param xwriter
	 * @param wb
	 */
	public void saveToExcel(SettingsImage2DDataSelectionsExport sett, XSSFExcelWriterReader xwriter, XSSFWorkbook wb) {
		if(currentImg!=null && selections!=null && selections.size()>0) {

			// create some sheets
			XSSFSheet shSummary = sett.isSummary()? xwriter.getSheet(wb, "table") : null; 
			XSSFSheet shShapesData = sett.isShapeData()? xwriter.getSheet(wb, "DataShapes") : null; 
			XSSFSheet shDEF = sett.isDefinitions()? xwriter.getSheet(wb, "DEFINITIONS") : null; 
			XSSFSheet shArray = sett.isArrays()? xwriter.getSheet(wb, "SelectExcl") : null; 
			XSSFSheet shX = sett.isX()? xwriter.getSheet(wb, "X") : null; 
			XSSFSheet shY = sett.isY()? xwriter.getSheet(wb, "Y") : null; 
			XSSFSheet shZ = sett.isZ()? xwriter.getSheet(wb, "Z") : null; 
			XSSFSheet shExMat = sett.isImgEx()? xwriter.getSheet(wb, "Excl IMG") : null; 
			XSSFSheet shSelMat = sett.isImgSel()? xwriter.getSheet(wb, "Select IMG") : null; 
			XSSFSheet shSelNonExMat = sett.isImgSelNEx()? xwriter.getSheet(wb, "SelectNExcl IMG") : null; 

			int headerrows = 6;

			// two sheet for each shape
			XSSFSheet[] shapeSel=null, shapeSelNonEx=null;
			int[] cdpShape=null, cdpShapeSelNonEx=null;
			if(sett.isShapes() || sett.isShapesSelNEx()) {
				if(sett.isShapes()) {
					shapeSel = new XSSFSheet[selections.size()];
					cdpShape = new int[selections.size()];
				}
				if(sett.isShapesSelNEx()){
					shapeSelNonEx = new XSSFSheet[selections.size()];
					cdpShapeSelNonEx = new int[selections.size()];
				}
				// create all sheets for shapes: first select, then exclude, then info
				for(int m=0; m<3; m++) {
					int c=1;
					for(int i=0; i<selections.size(); i++) {
						SettingsShapeSelection s = selections.get(i);
						if((m==0 && s.getMode().equals(SelectionMode.SELECT)) ||
								(m==1 && s.getMode().equals(SelectionMode.EXCLUDE)) ||
								(m==2 && s.getMode().equals(SelectionMode.INFO))) {
							// generate title
							String title = s.getMode().getShortTitle();
							// create sheets
							if(sett.isShapes())
								shapeSel[i] = xwriter.getSheet(wb, title+c);

							// only for selected
							if(m==0 && sett.isShapesSelNEx())
								shapeSelNonEx[i] = xwriter.getSheet(wb, title+"NExcl"+c);

							if(sett.isShapeData()) {
								// header for shapes summary
								xwriter.writeToCell(shShapesData, i, 1, ""+i);
								xwriter.writeToCell(shShapesData, i, 2, s.getMode().getShortTitle()+c);
							}

							// increment
							c++;
						}
					}
				}
			}

			// write header
			if(sett.isShapeData())
				xwriter.writeToCell(shShapesData, 0, 0, "All data points that were used to calculate statistics for each shape.");

			// write header
			xwriter.writeToCell(shArray, 0, 0, "Excluded");
			xwriter.writeToCell(shArray, 1, 0, "Selected");
			xwriter.writeToCell(shArray, 2, 0, "Selected (-exclusions)");

			// do statistics for all shape selections
			XYIDataMatrix data = currentImg.toXYIDataMatrix(false, true);
			Float[][] x = data.getX();
			Float[][] y = data.getY();
			Double[][] z = data.getI();

			// write xyz matrix
			if(sett.isX())
				xwriter.writeDataArrayToSheet(shX, x, 0, 0);
			if(sett.isY())
				xwriter.writeDataArrayToSheet(shY, y, 0, 0);
			if(sett.isZ())
				xwriter.writeDataArrayToSheet(shZ, z, 0, 0);
			//
			boolean usey = y[0]==y[1];
			// count excluded and selected
			int csel = 0;
			int cex = 0;
			int cselnonex = 0;

			// width height of data points
			float w = (float)currentImg.getMaxBlockWidth();
			float h = (float)currentImg.getMaxBlockHeight();

			//for each line
			for(int l=0; l<x.length; l++) {
				// for each data point
				for(int d=0; d<x[l].length; d++) {
					if(!Double.isNaN(z[l][d])) {
						// is excluded?
						boolean isExcluded = isExcluded((float)x[l][d], (float)y[l][d], w, h); 
						// write to excluded
						if(isExcluded) {
							cex++;
							// write as matrix
							if(sett.isImgEx())
								xwriter.writeToCell(shExMat, l, d, z[l][d]);
							// write as array
							if(sett.isArrays())
								xwriter.writeToCell(shArray, 0, cex, z[l][d]);
						}


						// check dp for all selected rects with exclude information
						// and add to containing shapes
						boolean isSelected = false;
						for(int i=0; i<selections.size(); i++) {
							SettingsShapeSelection s = selections.get(i);
							// check with the information that it is excluded
							boolean inside = s.check(x[l][d],y[l][d],z[l][d], w, h, isExcluded);

							isSelected = (inside && s.getMode().equals(SelectionMode.SELECT)) || isSelected;
							// write to 
							if(inside) {
								// write to shape without ex
								// write as matrix
								if(sett.isShapes()) {
									xwriter.writeToCell(shapeSel[i], l, d+headerrows, z[l][d]);
									// write in row
									xwriter.writeToCell(shapeSel[i], cdpShape[i], 5, z[l][d]);
								}
								// write to shape data summary
								if(sett.isShapeData() && !s.getMode().equals(SelectionMode.SELECT)) {
									xwriter.writeToCell(shShapesData, i, cdpShape[i]+4, z[l][d]);
								}
								cdpShape[i]++;

								// only for selected
								if(!isExcluded && s.getMode().equals(SelectionMode.SELECT)) {
									if(sett.isShapesSelNEx()) {
										// write to shape with regards to exclusion
										// write as matrix
										xwriter.writeToCell(shapeSelNonEx[i], l, d+headerrows, z[l][d]);

										// write in row
										xwriter.writeToCell(shapeSelNonEx[i], cdpShapeSelNonEx[i], 5, z[l][d]);
									}
									// write to shape data summary
									if(sett.isShapeData()) {
										xwriter.writeToCell(shShapesData, i, cdpShapeSelNonEx[i]+4, z[l][d]);
									}
									cdpShapeSelNonEx[i]++;
								}
							}
						}
						// selected write to array and matrix
						if(isSelected) {
							csel++;
							// array
							// write as matrix
							if(sett.isImgSel())
								xwriter.writeToCell(shSelMat, l, d, z[l][d]);
							// write as array
							if(sett.isArrays())
								xwriter.writeToCell(shArray, 1, csel, z[l][d]);

							if(!isExcluded) {
								cselnonex++;
								// write as matrix
								if(sett.isImgSelNEx())
									xwriter.writeToCell(shSelNonExMat, l, d, z[l][d]);
								// write as array
								if(sett.isArrays())
									xwriter.writeToCell(shArray, 2, cselnonex, z[l][d]);
							}
						}
					}
				}
			}

			//#####################################################
			// finalise the statistics
			for(int i=0; i<selections.size(); i++) {
				SettingsShapeSelection s = selections.get(i);
				// calculates statistics and frees memory
				s.calculateStatistics();
			}

			//#####################################################
			// write summary table
			xwriter.writeToCell(shSummary, 0, 0, "Summary of all rects.");
			// write title line
			xwriter.writeDataArrayToSheet(shSummary, SettingsShapeSelection.getTitleArrayExport(), 0, 1, false);
			// write data rows
			for(int r=0; r<selections.size(); r++) {
				// write all tablerows
				Object[] row = selections.get(r).getRowDataExport();
				xwriter.writeDataArrayToSheet(shSummary, row, 0, 2+r, false);

				// for all shape sheets:
				// write title line
				// write data row to all shape sheets
				if(sett.isShapes()) {
					xwriter.writeDataArrayToSheet(shapeSel[r], SettingsShapeSelection.getTitleArrayExport(), 0, 1, false);
					xwriter.writeDataArrayToSheet(shapeSel[r], row, 0, 2, false);
					// data
					xwriter.writeToCell(shapeSel[r], 0, 4, "All data points");
				}

				// only for selections
				if(sett.isShapesSelNEx() && selections.get(r).getMode().equals(SelectionMode.SELECT)) {
					xwriter.writeDataArrayToSheet(shapeSelNonEx[r], SettingsShapeSelection.getTitleArrayExport(), 0, 1, false);
					xwriter.writeDataArrayToSheet(shapeSelNonEx[r], row, 0, 2, false);
					// data
					xwriter.writeToCell(shapeSelNonEx[r], 0, 4, "All data points");
				}
			}

			// write definitions sheet
			if (sett.isDefinitions()) {
				int i=0;
				xwriter.writeToCell(shDEF, 0, 0+i, "table");
				xwriter.writeToCell(shDEF, 1, 0+i++, "Table with full statistics. I99 is the 99th percentile value comparable to the median as the 50th percentile value");
				xwriter.writeToCell(shDEF, 0, 0+i, "DataShapes");
				xwriter.writeToCell(shDEF, 1, 0+i++, "All data points which were used to calculate statistics of each shape. (for selections only selected, non excluded data points)");
				xwriter.writeToCell(shDEF, 0, 0+i, "DEFINITIONS");
				xwriter.writeToCell(shDEF, 1, 0+i++, "This sheet holds some explanations");
				xwriter.writeToCell(shDEF, 0, 0+i, "SelectExcl");
				xwriter.writeToCell(shDEF, 1, 0+i++, "Arrays of values for all values: 1. Excluded, 2. Selected and 3. Selected (in regards to exclusion). 3. is used to calculate statistics for selections. 2. for info and exclusion shapes");
				xwriter.writeToCell(shDEF, 0, 0+i, "Excl IMG");
				xwriter.writeToCell(shDEF, 1, 0+i++, "Data matrix of all excluded data points");
				xwriter.writeToCell(shDEF, 0, 0+i, "Select IMG");
				xwriter.writeToCell(shDEF, 1, 0+i++, "Data matrix of all selected data points");
				xwriter.writeToCell(shDEF, 0, 0+i, "SelectNExcl IMG");
				xwriter.writeToCell(shDEF, 1, 0+i++, "Data matrix of all selected non excluded data points");
				xwriter.writeToCell(shDEF, 0, 0+i, "Sel / Excl / Info#");
				xwriter.writeToCell(shDEF, 1, 0+i++, "Data matrix cut out of this shape");
				xwriter.writeToCell(shDEF, 0, 0+i, "SelNExcl#");
				xwriter.writeToCell(shDEF, 1, 0+i++, "Data matrix cut out of this selection shape in regards to exclusions (excluded data points are left out)");
			}
		}
	}


	/**
	 * create alpha map
	 */
	public void createAlphaMap(SettingsAlphaMap sett) {
		if(currentImg!=null && selections!=null && selections.size()>0) {
			// do statistics for all shape selections
			XYIDataMatrix data = currentImg.toXYIDataMatrix(false, true);
			Float[][] x = data.getX();
			Float[][] y = data.getY();
			Double[][] z = data.getI();

			Boolean[][] map = new Boolean[z.length][];

			float w = (float)currentImg.getMaxBlockWidth();
			float h = (float)currentImg.getMaxBlockHeight();
			
			//for each line
			for(int l=0; l<x.length; l++) {
				map[l] = new Boolean[z[l].length];
				// for each data point
				for(int d=0; d<x[l].length; d++) {
					if(!Double.isNaN(z[l][d])) {
						// is excluded?
						boolean inside = isInsideShape((float)x[l][d], (float)y[l][d], w, h);  
						map[l][d] = !inside;
					}
					else map[l][d] = null;
				}
			}
			sett.setMap(map);
			sett.setActive(true);
		}
	}

	
	//#################################################################
	// quantifier business
	/**
	 * auto order all quantifier 
	 */
	public void autoOrderQuantifier() {
		// TODO Auto-generated method stub
		
	}


	/**
	 * updates and returns the regression for quantification
	 * @return
	 */
	public SimpleRegression getRegression() {
		// create new regression
		if(updateRegression || regression == null) {
			regression = new SimpleRegression(true);
			regression.addData(getRegressionData());
			// track version of regression for quantified images to register changes
			if(regressionVersionID==Integer.MAX_VALUE)
				regressionVersionID = 1;
			regressionVersionID++;
			// unset
			updateRegression = false;
			// 
			return regression;
		}
		else return regression;
	}
	
	/**
	 * the regression data as [n - data points][2: concentration, avg intensity]
	 * can be used for the {@link DialogLinearRegression}
	 * @return
	 */
	public double[][] getRegressionData() {
		int n = countQuantifier();
		if(n>0) {
			// n data points
			double[][] dat = new double[n][2];
			// concentration and avg
			int c = 0;
			for (SettingsShapeSelection s : selections) {
				if(s.getRoi().equals(ROI.QUANTIFIER)) {
					dat[c][0] = s.getConcentration();
					dat[c][1] = s.getDefaultTableRow().getAvg();
					c++;
				}
			}
			return dat;
		}
		else return null;
	}
	/**
	 * number of quantifiers set as ROI in a SettingsShapeSelection
	 * @return
	 */
	public int countQuantifier() {
		if(selections==null)
			return 0;
		int c=0; 
		for (SettingsShapeSelection s : selections)
			if(s.getRoi().equals(ROI.QUANTIFIER))
				c++;
		return c;
	}

	@Override
	public void fireIntensityProcessingChanged(Image2D img) {
		// update if processing of current image has changed
		// e.g. after internal standard was applied or anything else
		if(lastProcessingChangeTime!=img.getLastIProcChangeTime()) {
			lastProcessingChangeTime = img.getLastIProcChangeTime();
			if(img.equals(currentImg))
				updateStatistics();
			else img.removeIntensityProcessingChangedListener(this);
		}
	}

	public int getRegressionVersionID() {
		return regressionVersionID;
	}
}

















