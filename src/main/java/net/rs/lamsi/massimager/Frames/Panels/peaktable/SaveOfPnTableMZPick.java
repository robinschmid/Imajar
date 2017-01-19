package net.rs.lamsi.massimager.Frames.Panels.peaktable;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class SaveOfPnTableMZPick extends JPanel {
	private JTable tableMZPeak;

	/**
	 * Create the panel.
	 */
	public SaveOfPnTableMZPick() {
		setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER); 
		
		tableMZPeak = new JTable();
		tableMZPeak.setCellSelectionEnabled(true);
		tableMZPeak.setColumnSelectionAllowed(true);
		tableMZPeak.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"File", "MZ", "z (charge)", "Mass", "Monoisotopic mass", "Intensity", "rt(min)", "rt(max)"
			}
		) {
			Class[] columnTypes = new Class[] {
				String.class, Double.class, Integer.class, Double.class, Double.class, Double.class, Double.class, Double.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
			boolean[] columnEditables = new boolean[] {
				true, false, true, false, true, false, false, false
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		scrollPane.setViewportView(tableMZPeak);

	}

	public TableModel getTable() {
		return tableMZPeak.getModel();
	}
}
