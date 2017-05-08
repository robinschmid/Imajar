package net.rs.lamsi.multiimager.Frames.dialogs.selectdata;

import java.util.ArrayList;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import net.rs.lamsi.general.settings.image.selection.SettingsSelections;
import net.rs.lamsi.general.settings.image.selection.SettingsShapeSelection;
import net.rs.lamsi.general.settings.image.selection.SettingsShapeSelection.ROI;
import net.rs.lamsi.general.settings.image.selection.SettingsShapeSelection.SelectionMode;

import org.jfree.chart.ChartPanel;

public class ShapeSelectionsTableModel extends AbstractTableModel {
	
	
	/**
	 * 
	 */
	private final String[] title = new String[] {"Order", "Type", "ROI", "conc.", "x0", "y0", "x1", "y1", "n", "sum", "I min", "I max", "I avg", "I median", "I 99%", "I stdev", "Histo"}; 
	private final Class[] type = new Class[] {
			Integer.class, SelectionMode.class, ROI.class, Double.class, Float.class, Float.class, Float.class, Float.class, Integer.class, Double.class, Double.class, Double.class, Double.class, Double.class, Double.class, Double.class, ChartPanel.class
	};
	private static final long serialVersionUID = 1L;
	
	private SettingsSelections selections;

	/**
	 * Constructor, assign given dataset to this table
	 */
	public ShapeSelectionsTableModel(SettingsSelections selections) {  
		this.selections = selections;
	}
	
	/**
	 * call after adding the table model to the table
	 * @param colModel
	 * @param listSelectionListener 
	 */
	public void init(TableColumnModel colModel) {
		// set column models
		// Create the combo box editor
	    JComboBox<SelectionMode> comboBox = new JComboBox<SelectionMode>(SelectionMode.values());
	    DefaultCellEditor editor = new DefaultCellEditor(comboBox);
	    colModel.getColumn(1).setCellEditor(editor);
	    
	    JComboBox<ROI> comboBox2 = new JComboBox<ROI>(ROI.values());
	    DefaultCellEditor editor2 = new DefaultCellEditor(comboBox2);
	    colModel.getColumn(2).setCellEditor(editor2);
	    
	    // histo
	    colModel.getColumn(getColumnCount()-1).setCellRenderer(new TableHistoColumnRenderer());
	}

	public void addRow(SettingsShapeSelection row, boolean update) {
		selections.addSelection(row, update);
		if(update)
			fireTableDataChanged();
	}
	public void removeRow(SettingsShapeSelection r, boolean update) { 
		removeRow(selections.getSelections().indexOf(r), update);
	}
	public void removeRow(int i, boolean update) {
		if(i<getRowCount())
			selections.removeSelection(i, update);
		// fire update
		if(update)
			fireTableRowsDeleted(i, i);
	} 
	public void removeRows(int[] selectedRows) { 
		for(int i=selectedRows.length-1; i>=0; i--) { 
			removeRow(selectedRows[i], false);
		}
		// update statistics
		selections.updateStatistics();
		fireTableDataChanged();
	} 
	public void removeAllRows() {
		int size = getRowCount();
		selections.removeAllSelections();
		fireTableRowsDeleted(0, size-1);
	}

	public int getColumnCount() {
		return title.length;
	}

	public int getRowCount() {
		return selections == null || selections.getSelections()==null? 0 : selections.getSelections().size();
	}

	public String getColumnName(int col) {
		return title[col];
	}

	public Class<?> getColumnClass(int col) { 
		return type[col];
	}


	/**
	 * This method returns the value at given coordinates of the dataset or null
	 * if it is a missing value
	 */

	public Object getValueAt(int row, int col) {
			SettingsShapeSelection sel = selections.getSelections().get(row);  
			SelectionTableRow r = sel.getDefaultTableRow();
			//   0      1     2       3     4       5    6      7        8         9           10      11        9
			//  orderNumber "Type", ROI, Concent. "x0", "y0", "x1", "y1",    n   sum   "I min", "I max", "I avg", "I median", "I 99%", "I stdev", "Histo"
			// order number for quantifier order
			switch(col) {
			case 0:
				return sel.getOrderNumber();
			case 1:
				return sel.getMode();
			case 2: 
				return sel.getRoi();
			case 3:
				return sel.getConcentration();
			case 4:
				return sel.getX0();
			case 5:
				return sel.getY0();
			case 6:
				return sel.getX1();
			case 7:
				return sel.getY1();
			case 8:
				return r.getN();
			case 9: 
				return r.getSum();
			case 10:
				return r.getMin();
			case 11:
				return r.getMax();	
			case 12:
				return r.getAvg();	
			case 13:
				return r.getMedian();			
			case 14:
				return r.getP99();			
			case 15:
				return r.getSdev();			
			case 16:
				return r.getHisto();			
			}
			return null;
	}

	public boolean isCellEditable(int row, int col) { 
		return (col==0 || col==1 || col==2 || col==3 || col==16);
	}


	public void setValueAt(Object value, int row, int col) {  
		SettingsShapeSelection sel = selections.getSelections().get(row);
		// maybe set it here
		switch (col) {
		case 0:
			sel.setOrderNumber((int) value);
			break;
		case 1:
			SelectionMode nm = (SelectionMode) value;
			if(!sel.getMode().equals(nm)) {
				// update statistics if exclude was set/unset
				boolean update = sel.getMode().equals(SelectionMode.EXCLUDE) || nm.equals(SelectionMode.EXCLUDE);
				// set mode
				sel.setMode(nm);
				if(update)
					selections.updateStatistics();
			}
			break;
		case 2:
			ROI r = (ROI) value;
			if(!sel.getRoi().equals(r)) {
				sel.setRoi((ROI) value);
			}
			break;
		case 3:
			sel.setConcentration((double) value);
			break;
		}
		fireTableCellUpdated(row, col);
		// update repaint
	} 

	/**
	 * update row and fire table update event
	 * @param row
	 */
	public void updateRow(SettingsShapeSelection row) {
		for(int i=0; i<getRowCount(); i++)
			if(selections.getSelections().get(i).equals(row)) { 
				// TODO - is working?
				fireTableRowsUpdated(i, i);
			}
	}

	public ArrayList<SettingsShapeSelection> getselections() {
		return selections.getSelections();
	}

	public void setRows(SettingsSelections rows) {
		selections = rows;
		fireTableDataChanged();
	}

	/**
	 * array[row][col]  with title
	 * @return
	 */
	public Object[][] toArray(boolean useTitle) {
		if(selections==null || getRowCount()==0) 
			return new Object[0][0];
		try {
			Object[][] data = new Object[getRowCount()+(useTitle?1:0)][];
			//
			//title
			if(useTitle)
				data[0] = title; 
			// data
			for(int i=0; i<getRowCount(); i++)
				data[i+1] = selections.getSelections().get(i).getRowData();

			return data;
		}catch(Exception ex) {
			ex.printStackTrace();
			return new Object[0][0];
		}
	}

}