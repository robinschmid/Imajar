package net.rs.lamsi.massimager.Settings.image.selection;

import java.util.ArrayList;
import java.util.Iterator;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.data.twodimensional.XYIData2D;
import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.massimager.Settings.image.selection.SettingsShapeSelection.SelectionMode;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SettingsSelections extends Settings {
	// do not change the version!
	private static final long serialVersionUID = 1L;

	// list of selections, exclusions and info
	protected ArrayList<SettingsShapeSelection> selections; 

	// 
	private boolean hasExclusions = false, hasSelections = false;

	protected Image2D currentImg;

	public SettingsSelections() {
		super("SettingsSelection", "/Settings/Selections/", "setSelList"); 
	} 

	@Override
	public void resetAll() {  
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
		if(update)
			updateStatistics();
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

			// for each data point
			for(int d=0; d<x.length; d++) {
				boolean isExcluded = isExcluded((float)x[d], (float)y[d]); 

				// check dp for all selected rects with exclude information
				// and add to containing shapes
				for(int i=0; i<selections.size(); i++) {
					SettingsShapeSelection s = selections.get(i);

					// check with the information that it is excluded
					s.check(x[d],y[d],z[d], isExcluded);
				}
			}

			// finalise the process?
			for(int i=0; i<selections.size(); i++) {
				SettingsShapeSelection s = selections.get(i);
				// calculates statistics and frees memory
				s.calculateStatistics();
			}
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

			// for each data point
			for(int d=0; d<x.length; d++) {
				boolean isExcluded = isExcluded((float)x[d], (float)y[d]); 
				// check for s
				// and add to containing shapes 
				s.check(x[d],y[d],z[d], isExcluded);
			}

			// finalise the process?
			s.calculateStatistics();
		}
	}


	/**
	 * checks if the point is inside a exclusion shape
	 * @param x coordinate in the given processed data space (e.g. micro meters)
	 * @param y coordinate in the given processed data space (e.g. micro meters)
	 * @return
	 */
	public boolean isExcluded(float x, float y) {
		if(hasExclusions) {
			for(SettingsShapeSelection s : selections) {
				// only exclusions
				if(s.getMode().equals(SelectionMode.EXCLUDE)) {
					if(s.contains(x, y)) {
						return true; 
					}
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
	public boolean isSelected(float x, float y, boolean checkForExclusion) {
		boolean state = false;
		// is selected?
		if(hasSelections) {
			for(int i=0; i<selections.size() && !state; i++) {
				SettingsShapeSelection s = selections.get(i);
				// only exclusions
				if(s.getMode().equals(SelectionMode.SELECT))
					if(s.contains(x, y))
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
						if(s.contains(x, y))
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
				if(paramName.equals("SettingsShapeSelection")) {
					if(selections==null)
						selections = new ArrayList<SettingsShapeSelection>();
					// how to load from xml????
					selections.add(SettingsShapeSelection.loadSettingsFromXML(nextElement, doc));
				}
				//				else if(paramName.equals("xrange.upper"))xu = doubleFromXML(nextElement);
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
}
