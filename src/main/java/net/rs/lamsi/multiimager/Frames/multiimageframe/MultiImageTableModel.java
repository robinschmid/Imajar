package net.rs.lamsi.multiimager.Frames.multiimageframe;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import net.rs.lamsi.massimager.Frames.Panels.peaktable.PeakTableColumnType;
import net.rs.lamsi.massimager.Frames.Panels.peaktable.PeakTableRow;
import net.sf.mzmine.datamodel.PeakList;

public class MultiImageTableModel extends AbstractTableModel {

	private MultiImageFrame window;
    /**
     * 
     */
	private final String[] title = new String[] {"#", "Title", "Show", "Use range", "Lower", "Upper", "Range"}; 
		private final Class[] type = new Class[] {
			Integer.class, String.class, Boolean.class, Boolean.class, Double.class, Double.class, Double.class
		};
    private static final long serialVersionUID = 1L;
    private Vector<MultiImgTableRow> rowList = new Vector<MultiImgTableRow>();
    

    /**
     * Constructor, assign given dataset to this table
     */
    public MultiImageTableModel(MultiImageFrame window) {  
    	this.window = window;
    }
    
		public void fireGridChanged() {
			window.updateGridView();
		} 
		public void fireDataProcessingChanged() {
			window.fireProcessingChanged();
		}
	
    
    public void addRow(MultiImgTableRow row) {
    	rowList.add(row);
    	fireTableDataChanged();
    }
    public void removeRow(int i) {
    	if(i<rowList.size())
    		rowList.removeElementAt(i);
    	// fire update
    	fireTableRowsDeleted(i, i);
    } 
	public void removeRows(int[] selectedRows) { 
		for(int i=selectedRows.length-1; i>=0; i--) { 
			removeRow(selectedRows[i]);
		}
	} 
    public void removeAllRows() {
    	int size = rowList.size();
    	rowList.removeAllElements();
    	fireTableRowsDeleted(0, size-1);
    }

    public int getColumnCount() {
        return title.length;
    }

    public int getRowCount() {
        return rowList.size();
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
    	try { 
    		MultiImgTableRow peakRow = rowList.get(row);  
    		return peakRow.getRowData()[col]; 
		} catch (Exception e) {
			return null;
		}  
    }

    public boolean isCellEditable(int row, int col) { 
        return !(col==1 || col==0);
    }
    

    public void setValueAt(Object value, int row, int col) {  
        
        MultiImgTableRow prow = rowList.get(row);
        // maybe set it here
        switch (col) {
        case 2: 
        	prow.setShowing((boolean) value);
        	fireGridChanged();
        	break;
        case 3: 
        	prow.setUseRange((boolean) value);
            fireDataProcessingChanged();
        	break;
        case 4:
        	if(prow.setLower((double) value)) {
        		fireTableCellUpdated(row, 6);
	            fireDataProcessingChanged();
        	}
        	break;
        case 5:
        	if(prow.setUpper((double) value)) {
        		fireTableCellUpdated(row, 6);
	            fireDataProcessingChanged();
        	}
        	break;
        case 6:
        	if(prow.setRange((int[])value)) {
	            fireTableCellUpdated(row, 4);
	            fireTableCellUpdated(row, 5);
	            fireDataProcessingChanged();
        	}
        	break;
        }
        fireTableCellUpdated(row, col);
        // update repaint
    } 

	/**
     * update row and fire table update event
     * @param row
     */
    public void updateRow(MultiImgTableRow row) {
    	for(int i=0; i<rowList.size(); i++)
    		if(rowList.get(i).equals(row)) { 
    			// TODO - is working?
    			fireTableRowsUpdated(i, i);
    		}
    }

	public Vector<MultiImgTableRow> getRowList() {
		return rowList;
	}

	public void setRows(Vector<MultiImgTableRow> rows) {
		rowList = rows;
		fireTableDataChanged();
	}

	/**
	 * array[row][col]  with title
	 * @return
	 */
	public Object[][] toArray(boolean useTitle) {
		if(rowList==null || rowList.size()==0) 
			return new Object[0][0];
		try {
			Object[][] data = new Object[rowList.size()+(useTitle?1:0)][];
			//
			//title
			if(useTitle)
				data[0] = title; 
			// data
	    	for(int i=0; i<rowList.size(); i++)
	    		data[i+1] = rowList.get(i).getRowData();
			
			return data;
		}catch(Exception ex) {
			ex.printStackTrace();
			return new Object[0][0];
		}
	}

	public MultiImageFrame getWindow() {
		return window;
	}

	public void setWindow(MultiImageFrame window) {
		this.window = window;
	}

}