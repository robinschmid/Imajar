package net.rs.lamsi.multiimager.FrameModules.sub.quantifiertable;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.massimager.Frames.FrameWork.ButtonColumn;
import net.rs.lamsi.massimager.Settings.image.operations.quantifier.Quantifier;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.dialogs.selectdata.Image2DSelectDataAreaDialog;
import net.rs.lamsi.utils.DialogLoggerUtil;

public abstract class PnTableQuantifier extends JPanel implements ListSelectionListener {
	
	protected String[] columnToolTips = {"Concentration", "Name of image", "Path of raw file", "Name of parent. Parent has master settings.", "Average calculation with all data points", "Average calculation with selection boxes", "Select data"};
	
	private JTable tableQuantifier;
	private QuantifierTableModel model;
	private ImageEditorWindow window;

	/**
	 * Create the panel.
	 */
	public PnTableQuantifier(ImageEditorWindow wnd) {
		setLayout(new BorderLayout(0, 0));
		this.window = wnd;

		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER); 

		tableQuantifier = new JTable() { 
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
		        return super.getCellRenderer(row, column);
		    }
		};
		
		// 
		tableQuantifier.setAutoCreateRowSorter(true); 
		tableQuantifier.setRowSelectionAllowed(true);
		tableQuantifier.setCellSelectionEnabled(true);
		tableQuantifier.setColumnSelectionAllowed(true);
		tableQuantifier.getSelectionModel().addListSelectionListener(this);
		model = new QuantifierTableModel();
		tableQuantifier.setModel(model);
		scrollPane.setViewportView(tableQuantifier);
		
		// add buttons for select dialog(rects)
		Action openSelectBoxDialog = new AbstractAction() {
		    public void actionPerformed(ActionEvent e) {
		        // TODO open color Dialog
		    	final JTable table = (JTable)e.getSource();
		        final int modelRow = Integer.valueOf( e.getActionCommand() );
		        QuantifierTableRow row = getTableModel().getQuantiRowList().get(modelRow);
		        // open TODO
		        Image2DSelectDataAreaDialog dialog = new Image2DSelectDataAreaDialog();
				dialog.startDialog(row.getImg());
				WindowAdapter wl = new WindowAdapter() {
					@Override
					public void windowClosed(WindowEvent e) { 
						super.windowClosed(e); 
						// show selected excluded rects 
						getTableModel().fireTableCellUpdated(modelRow, QuantifierTableColumnType.SELECT.getIndex());
					}
				};
				dialog.addWindowListener(wl);
		    }
		};
		 
		ButtonColumn buttonColumn = new ButtonColumn(tableQuantifier, openSelectBoxDialog, QuantifierTableColumnType.SELECT.getIndex());
		buttonColumn.setMnemonic(KeyEvent.VK_D);
		

		// add buttons for parent select
		Action openSelectParentDialog = new AbstractAction() {
		    public void actionPerformed(ActionEvent e) {
		        // TODO open color Dialog
		    	final JTable table = (JTable)e.getSource();
		        final int modelRow = Integer.valueOf( e.getActionCommand() );
		        QuantifierTableRow row = getTableModel().getQuantiRowList().get(modelRow);
		        // open TODO
		        Object[] list = window.getLogicRunner().getListImages().toArray(); 
				try {
					int parenti = row.getImg().getParent()==null? 0 : window.getLogicRunner().getListImages().indexOf(row.getImg().getParent()); 
					if(parenti==-1) parenti = 0;
					int i = DialogLoggerUtil.showListDialogAndChoose(window, list, ListSelectionModel.SINGLE_SELECTION, parenti)[0];
					// set parent and update
					row.getImg().setParent(i>=0 && i<list.length? (Image2D) list[i] : null); 

					getTableModel().fireTableCellUpdated(modelRow, QuantifierTableColumnType.PARENT.getIndex());
				} catch(Exception ex) { 
				}
		    }
		};
		 
		buttonColumn = new ButtonColumn(tableQuantifier, openSelectParentDialog, QuantifierTableColumnType.PARENT.getIndex());
		buttonColumn.setMnemonic(KeyEvent.VK_E);
		
		// set column mode to combobox
		TableColumn colMode = getTable().getColumnModel().getColumn(QuantifierTableColumnType.MODE.getIndex());
		JComboBox comboBox = new JComboBox();
		comboBox.addItem(QuantifierTableRow.MODE_AVERAGE);
		comboBox.addItem(QuantifierTableRow.MODE_SELECTION);
		colMode.setCellEditor(new DefaultCellEditor(comboBox));
	} 

	public QuantifierTableModel getTableModel() {
		return (QuantifierTableModel) tableQuantifier.getModel();
	}
	public JTable getTable() {
		return tableQuantifier;
	}

	public void addQuantifier(Image2D img) {
		QuantifierTableRow row = new QuantifierTableRow(img, model.getRowCount());
		model.addRow(row); 
	} 
	public void addQuantifier(Quantifier q) {
		QuantifierTableRow row = new QuantifierTableRow(q);
		model.addRow(row); 
	}

	public void removeSelectedRows() {
		getTableModel().removeRows(getTable().getSelectedRows());
	}
	
	public void duplicateSelectedRows() {
		int[] ind = getTable().getSelectedRows();
		for(int i:ind) {
			try {
				addQuantifier(getTableModel().getQuantiRowList().get(i).getImg().getCopyChild());
			} catch (Exception e) { 
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @return list of quantifiers
	 */
	public Vector<Quantifier> getQuantifiers() {
		return getTableModel().getQuantifiers();
	}

}
