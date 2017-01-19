package net.rs.lamsi.massimager.Frames.Menu;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import net.rs.lamsi.massimager.Frames.Window;
import net.rs.lamsi.massimager.Frames.Panels.peaktable.PnTableMZPick;
import javax.swing.JToggleButton;

public class MenuTableActions extends JPanel { 
	private PnTableMZPick pnTable;
	private JToggleButton btnOnlyHighestPeak;
	/**
	 * Create the panel.
	 */
	public MenuTableActions(PnTableMZPick pnTable2) {
		//
		this.pnTable = pnTable2;
		//
		setMaximumSize(new Dimension(32767, 25));
		setLayout(new BorderLayout(0, 0));
		
		JToolBar toolBar = new JToolBar();
		toolBar.setRollover(true);
		toolBar.setFloatable(false);
		add(toolBar);
		
		JButton btnDeleteRow = new JButton("");
		btnDeleteRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pnTable.removeSelectedRows();
			}
		});
		btnDeleteRow.setIcon(new ImageIcon(MenuTableActions.class.getResource("/img/btn_table_deleterow.png")));
		btnDeleteRow.setSelectedIcon(new ImageIcon(MenuTableActions.class.getResource("/img/btn_table_deleterow.png")));
		btnDeleteRow.setToolTipText("Delete selected row");
		btnDeleteRow.setMinimumSize(new Dimension(25, 25));
		btnDeleteRow.setMaximumSize(new Dimension(25, 25));
		btnDeleteRow.setMargin(new Insets(0, 0, 0, 0));
		btnDeleteRow.setBounds(new Rectangle(0, 0, 25, 25));
		toolBar.add(btnDeleteRow);
		
		JButton btnDeleteAllRows = new JButton("");
		btnDeleteAllRows.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {  
		        boolean yes = Window.showDialogYesNo("Remove all rows?", "Rows will be lost!");
				
		        if(yes) {
		        	pnTable.getTableModel().removeAllRows();
		        }
			}
		});
		btnDeleteAllRows.setIcon(new ImageIcon(MenuTableActions.class.getResource("/img/btn_table_deleteall.png")));
		btnDeleteAllRows.setSelectedIcon(new ImageIcon(MenuTableActions.class.getResource("/img/btn_table_deleteall.png")));
		btnDeleteAllRows.setToolTipText("Delete all rows in table");
		btnDeleteAllRows.setMinimumSize(new Dimension(25, 25));
		btnDeleteAllRows.setMaximumSize(new Dimension(25, 25));
		btnDeleteAllRows.setMargin(new Insets(0, 0, 0, 0));
		btnDeleteAllRows.setBounds(new Rectangle(0, 0, 25, 25));
		toolBar.add(btnDeleteAllRows);
		
		JButton button = new JButton("");
		button.setSelectedIcon(new ImageIcon(MenuTableActions.class.getResource("/img/btn_table_column_editor-01.png")));
		button.setIcon(new ImageIcon(MenuTableActions.class.getResource("/img/btn_table_column_editor-01.png")));
		button.setToolTipText("Delete all rows in table");
		button.setMinimumSize(new Dimension(25, 25));
		button.setMaximumSize(new Dimension(25, 25));
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setBounds(new Rectangle(0, 0, 25, 25));
		toolBar.add(button);
		
		JButton btnChargeCalcSettings = new JButton("");
		btnChargeCalcSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// opens the charge calc settings dialog
				Window.getWindow().getDialogChargeCalculatorSettings().setVisible(true);
			}
		});
		btnChargeCalcSettings.setIcon(new ImageIcon(MenuTableActions.class.getResource("/img/btn_table_chargecalc.png")));
		btnChargeCalcSettings.setSelectedIcon(new ImageIcon(MenuTableActions.class.getResource("/img/btn_table_chargecalc_selected.png")));
		btnChargeCalcSettings.setToolTipText("Opens the charge calculation settings dialog");
		btnChargeCalcSettings.setMinimumSize(new Dimension(25, 25));
		btnChargeCalcSettings.setMaximumSize(new Dimension(25, 25));
		btnChargeCalcSettings.setMargin(new Insets(0, 0, 0, 0));
		btnChargeCalcSettings.setBounds(new Rectangle(0, 0, 25, 25));
		toolBar.add(btnChargeCalcSettings);
		
		btnOnlyHighestPeak = new JToggleButton("");
		btnOnlyHighestPeak.setSelected(true);
		btnOnlyHighestPeak.setSelectedIcon(new ImageIcon(MenuTableActions.class.getResource("/img/btn_table_only_highest_peak_selected-01.png")));
		btnOnlyHighestPeak.setIcon(new ImageIcon(MenuTableActions.class.getResource("/img/btn_table_only_highest_peak-01.png")));
		btnOnlyHighestPeak.setToolTipText("Extracts only the highest peak in range to this table");
		btnOnlyHighestPeak.setMinimumSize(new Dimension(25, 25));
		btnOnlyHighestPeak.setMaximumSize(new Dimension(25, 25));
		btnOnlyHighestPeak.setMargin(new Insets(0, 0, 0, 0));
		btnOnlyHighestPeak.setBounds(new Rectangle(0, 0, 25, 25));
		toolBar.add(btnOnlyHighestPeak);
		
	}
	
	
	 
	public JToggleButton getBtnOnlyHighestPeak() {
		return btnOnlyHighestPeak;
	}
}
