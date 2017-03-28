package net.rs.lamsi.multiimager.FrameModules.sub.quantifiertable;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.massimager.Settings.image.operations.quantifier.Quantifier;
import net.sf.mzmine.datamodel.PeakList;
 

public class QuantifierTableModel extends AbstractTableModel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Vector<QuantifierTableRow> quantiRowList = new Vector<QuantifierTableRow>();
    

    /**
     * Constructor, assign given dataset to this table
     */
    public QuantifierTableModel() {  
    }
    
    public void addRow(QuantifierTableRow row) {
    	quantiRowList.add(row); 
    	fireTableDataChanged();
    }
    public void removeRow(int i) {
    	if(i<quantiRowList.size()) {
    		quantiRowList.removeElementAt(i); 
    	}
    	// fire update
    	fireTableRowsDeleted(i, i);
    } 
	public void removeRows(int[] selectedRows) { 
		for(int i=selectedRows.length-1; i>=0; i--) { 
			removeRow(selectedRows[i]);
		}
	} 
    public void removeAllRows() {
    	int size = quantiRowList.size();
    	if(size>0) {
	    	quantiRowList.removeAllElements(); 
	    	fireTableRowsDeleted(0, size-1);
    	}
    }

    public int getColumnCount() {
        return QuantifierTableColumnType.values().length;
    }

    public int getRowCount() {
        return quantiRowList.size();
    }

    public String getColumnName(int col) {
        return getCommonColumn(col).getColumnName();
    }

    public Class<?> getColumnClass(int col) {  
        return getCommonColumn(col).getColumnClass();  
    }
    

    /**
     * This method returns the value at given coordinates of the dataset or null
     * if it is a missing value
     */

    public Object getValueAt(int row, int col) {
    	try { 
    		QuantifierTableRow peakRow = quantiRowList.get(row); 
            return peakRow.getArray()[col]; 
		} catch (Exception e) {
			return null;
		} 
    }

    public boolean isCellEditable(int row, int col) { 
        QuantifierTableColumnType columnType = getCommonColumn(col);

        return ((columnType == QuantifierTableColumnType.MODE) || (columnType == QuantifierTableColumnType.CONC) || (columnType == QuantifierTableColumnType.PARENT) || (columnType == QuantifierTableColumnType.SELECT));
    }

    public void setValueAt(Object value, int row, int col) { 
        QuantifierTableColumnType columnType = getCommonColumn(col);
        setValueAt(value, row, columnType);
    }
    public void setValueAt(Object value, int row, QuantifierTableColumnType columnType) {  
        
        QuantifierTableRow prow = quantiRowList.get(row); 
        // maybe set it here
        switch (columnType) {
        case NAME: 
        	break;
        case PATH:
        	break;
        case PARENT:
        	break;
        case MODE:
        	prow.setMode(String.valueOf(value));
        	break;
        case CONC:
        	prow.setC((double)(value));
        	break;
        }
        fireTableCellUpdated(row, columnType.ordinal());
        // update repaint
    }  

    public static QuantifierTableColumnType getCommonColumn(int col) {

        QuantifierTableColumnType commonColumns[] = QuantifierTableColumnType.values();

        if (col < commonColumns.length)
            return commonColumns[col];

        return null; 
    }

	public Vector<Quantifier> getQuantifiers() {   
	    Vector<Quantifier> quantifiers = new Vector<Quantifier>();
	    for(QuantifierTableRow q : quantiRowList)
	    	quantifiers.add(q.getQuanti());
		return quantifiers;
	}

	public Vector<QuantifierTableRow> getQuantiRowList() { 
		return quantiRowList;
	}
      
}
