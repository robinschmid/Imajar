package net.rs.lamsi.multiimager.Frames.dialogs.selectdata;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

public class TableHistoColumnRenderer implements TableCellRenderer {
	
	private JPanel panel;
	
	public TableHistoColumnRenderer() {
		panel = new JPanel(new BorderLayout());
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {  
		panel.removeAll();
		if(value!=null)
			panel.add(((ChartPanel)value), BorderLayout.CENTER);
		return panel;
	}

}
