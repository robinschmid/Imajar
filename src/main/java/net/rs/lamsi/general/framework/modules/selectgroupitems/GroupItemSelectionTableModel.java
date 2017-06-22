package net.rs.lamsi.general.framework.modules.selectgroupitems;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import net.rs.lamsi.general.settings.image.needy.SettingsCollectable2DLink;
 

public class GroupItemSelectionTableModel extends AbstractTableModel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private LinkedHashMap<SettingsCollectable2DLink, Boolean> map; 

    private static final String []TITLES = {"","title","group","project"};
    /**
     * Constructor, assign given dataset to this table
     */
    public GroupItemSelectionTableModel() {  
    }
    
    public void setData(LinkedHashMap<SettingsCollectable2DLink, Boolean> map) {
    	this.map= map;
    	fireTableDataChanged();
    }
    
    public int getColumnCount() {
        return 4;
    }

    public int getRowCount() {
        return map!=null? map.size() : 0;
    }

    public String getColumnName(int col) {
        return TITLES[col];
    }

    public Class<?> getColumnClass(int col) {  
        return col==0? Boolean.class : String.class;
    }
    

    /**
     * This method returns the value at given coordinates of the dataset or null
     * if it is a missing value
     */
    public Object getValueAt(int row, int col) {
    	if(map!=null && row<map.size()) {
    		int r = 0;
    		for(Map.Entry<SettingsCollectable2DLink, Boolean> e : map.entrySet()) {
    			if(r==row) {
    				switch(col) {
    	    		case 0:
    	    			return e.getValue();
    	    		case 1: 
    	    			return e.getKey().getTitle();
    	    		case 2: 
    	    			return e.getKey().getGroup();
    	    		case 3: 
    	    			return e.getKey().getProject();
    	    		}
    			}
    			r++;
    		}
    	}
    	return null;
    }

    public boolean isCellEditable(int row, int col) { 
    	return col==0;
    }

    public void setValueAt(Object value, int row, int col) { 
    	if(map!=null && row<map.size()) {
    		int r = 0;
    		for(Map.Entry<SettingsCollectable2DLink, Boolean> e : map.entrySet()) {
    			if(r==row) {
    				e.setValue((Boolean) value);
    					fireTableCellUpdated(row, col);
    					return;
    			}
    			r++;
    		}
    	}
    }
      
}
