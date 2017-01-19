package net.rs.lamsi.massimager.Frames.Panels.peaktable;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import net.rs.lamsi.massimager.Frames.Window;
import net.rs.lamsi.massimager.Settings.SettingsGeneralValueFormatting;
import net.sf.mzmine.datamodel.PeakList;
import net.sf.mzmine.datamodel.PeakListRow;

public abstract class PnTableMZPick extends JPanel implements ListSelectionListener {
	
	protected String[] columnToolTips = {"ID",
        "Rawdata file",
        "Used PeakList",
        "m/z (mass per charge)",
        "Calculated charge (can be modified)",
        "Mass calculated by mz*charge",
        "A rule to calculate the monoisotopic mass (= mass - rule)",
        "Monoisotopic mass",
        "Average height of this peak",
        "Average area of this peak",
        "Minimum of the selected retention time (rt) range",
        "Average retention time (rt) of this peak",
        "Maximum of the selected retention time (rt) range"};
	
	private JTable tableMZPeak;
	private PeakTableModel model;
	private PeakTableCellRenderer renderer;

	/**
	 * Create the panel.
	 */
	public PnTableMZPick() {
		setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER); 

		tableMZPeak = new JTable() { 
			//Implement table cell tool tips.
		    public String getToolTipText(MouseEvent e) {
		        String tip = null;
		        java.awt.Point p = e.getPoint();
		        int rowIndex = rowAtPoint(p);
		        int colIndex = columnAtPoint(p);
		        int realColumnIndex = convertColumnIndexToModel(colIndex);

		        
		        return tip;
		    }

		    //Implement table header tool tips.
		    protected JTableHeader createDefaultTableHeader() {
		        return new JTableHeader(columnModel) {
		            public String getToolTipText(MouseEvent e) {
		                String tip = null;
		                java.awt.Point p = e.getPoint();
		                int index = columnModel.getColumnIndexAtX(p.x);
		                int realIndex = columnModel.getColumn(index).getModelIndex();
		                return columnToolTips[realIndex];
		            }
		        };
		    }
		    
		    
		    // CellRenderer for mz
		    public TableCellRenderer getCellRenderer(int row, int column) { 
		    	SettingsGeneralValueFormatting settings = Window.getWindow().getSettings().getSetGeneralValueFormatting();
	            PeakTableColumnType commonColumn = PeakTableModel.getCommonColumn(column);

	            switch (commonColumn) { 
	            case MZ: 
	            case MASS: 
	            case MONOISOTOPICMASS: 
	            	renderer.setFormat(settings.getDecimalsMZ(), false);
	            	return renderer; 
	            case RT: 
	            case RTMAX: 
	            case RTMIN: 
	            	renderer.setFormat(settings.getDecimalsRT(), false);
	            	return renderer; 
	            case AREA:
	            case HEIGHT:
	            	renderer.setFormat(settings.getDecimalsIntensity(), settings.isShowingExponentIntensity());
	            	return renderer; 
	            }
		        // else...
		        return super.getCellRenderer(row, column);
		    }
		};
		
		// 
		tableMZPeak.setAutoCreateRowSorter(true); 
		tableMZPeak.setRowSelectionAllowed(true);
		tableMZPeak.setCellSelectionEnabled(true);
		tableMZPeak.setColumnSelectionAllowed(false);
		tableMZPeak.getSelectionModel().addListSelectionListener(this);
		model = new PeakTableModel();
		tableMZPeak.setModel(model);
		scrollPane.setViewportView(tableMZPeak); 

		// init renderer
		renderer = new PeakTableCellRenderer();
	} 
	

	public PeakTableModel getTableModel() {
		return (PeakTableModel) tableMZPeak.getModel();
	}
	public JTable getTable() {
		return tableMZPeak;
	}

	public void addPeak(PeakListRow peak, PeakList peakListName, double rtMin, double rtMax) {
		// 
		String rawName = "";
		if(peakListName!=null && peakListName.getRawDataFiles().length==1) rawName = peakListName.getRawDataFile(0).getName();
		PeakTableRow row = new PeakTableRow("#"+model.getRowCount(), rawName, peakListName, peak, rtMin, rtMax, 0);
		model.addRow(row); 
		//
		System.out.println("Peak added"+" #"+model.getRowCount()); 
	}


	public void removeSelectedRows() {
		getTableModel().removeRows(getTable().getSelectedRows());
	}

 
	
	
}
