package net.rs.lamsi.massimager.Frames.Panels.peaktable;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.table.DefaultTableCellRenderer;

public class PeakTableCellRenderer  extends DefaultTableCellRenderer {

	boolean showingExponentIntensity = false;
	DecimalFormat numberFormat = new DecimalFormat("#,###.######;(#,###.######)");
	DecimalFormat exponentFormat = new DecimalFormat("0.##E00;(0.##E00)");
    
    
    public void setValue(Object value) {
    	if(showingExponentIntensity) 
    		super.setValue(exponentFormat.format(value));
    	else 
    		super.setValue(numberFormat.format(value));
    } 
    
    public DecimalFormat getNumberFormat() {
    	return numberFormat;
    }

	public void setFormat(int decimals, boolean showingExponentIntensity) {
		numberFormat.setMaximumFractionDigits(decimals);
		numberFormat.setMinimumFractionDigits(decimals); 
		// TODO Exponent
		this.showingExponentIntensity = showingExponentIntensity;
	}
}
