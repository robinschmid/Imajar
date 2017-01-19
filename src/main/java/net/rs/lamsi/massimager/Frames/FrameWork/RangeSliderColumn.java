package net.rs.lamsi.massimager.Frames.FrameWork;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import net.rs.lamsi.massimager.Frames.FrameWork.rangeslider.RangeSlider;

/**
 *  The ButtonColumn class provides a renderer and an editor that looks like a
 *  JButton. The renderer and editor will then be used for a specified column
 *  in the table. The TableModel will contain the String to be displayed on
 *  the button.
 *
 *  The button can be invoked by a mouse click or by pressing the space bar
 *  when the cell has focus. Optionally a mnemonic can be set to invoke the
 *  button. When the button is invoked the provided Action is invoked. The
 *  source of the Action will be the table. The action command will contain
 *  the model row number of the button that was clicked.
 *
 */
public class RangeSliderColumn extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, MouseListener
{
	private JTable table; 
	private int mnemonic;
	private Border originalBorder;
	private Border focusBorder;

	private RangeSlider renderSlider;
	private RangeSlider editSlider;
	private Object editorValue;
	private boolean isButtonColumnEditor;

	/**
	 *  Create the ButtonColumn to be used as a renderer and editor. The
	 *  renderer and editor will automatically be installed on the TableColumn
	 *  of the specified column.
	 *
	 *  @param table the table containing the button renderer/editor
	 *  @param changeListener the Action to be invoked when the button is invoked
	 *  @param column the column to which the button renderer/editor is added
	 */
	public RangeSliderColumn(JTable table, int column, int min, int max) {
		this.table = table; 

		renderSlider = new RangeSlider(min, max);
		editSlider = new RangeSlider(min, max); 

		originalBorder = editSlider.getBorder();
		setFocusBorder( new LineBorder(Color.BLUE) );

		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(column).setCellRenderer( this );
		columnModel.getColumn(column).setCellEditor( this );
		table.addMouseListener( this );
	} 


	/**
	 *  Get foreground color of the button when the cell has focus
	 *
	 *  @return the foreground color
	 */
	public Border getFocusBorder() {
		return focusBorder;
	}

	/**
	 *  The foreground color of the button when the cell has focus
	 *
	 *  @param focusBorder the foreground color
	 */
	public void setFocusBorder(Border focusBorder)
	{
		this.focusBorder = focusBorder;
		editSlider.setBorder( focusBorder );
	}

	public int getMnemonic()
	{
		return mnemonic;
	}

	/**
	 *  The mnemonic to activate the button when the cell has focus
	 *
	 *  @param mnemonic the mnemonic
	 */
	public void setMnemonic(int mnemonic) {
		this.mnemonic = mnemonic; 
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		this.editorValue = value;
		if (isSelected)
		{
			editSlider.setForeground(table.getSelectionForeground());
			editSlider.setBackground(table.getSelectionBackground());
		}
		else
		{
			editSlider.setForeground(table.getForeground());
			editSlider.setBackground(table.getBackground());
		}

		// TODO right?
		if(value!=null) {
			// set min max
			editSlider.setMaximum(((int[])value)[3]);
			editSlider.setMinimum(((int[])value)[2]);
			// set values
			editSlider.setValue(((int[])value)[0]);
			editSlider.setUpperValue(((int[])value)[1]);
		}
		return editSlider;
	}

	@Override
	public Object getCellEditorValue() {
		return new int[] {editSlider.getValue(), editSlider.getUpperValue(), editSlider.getMinimum(), editSlider.getMaximum()};
	}


	public boolean stopCellEditing() {
		return super.stopCellEditing();
	}

	protected void fireEditingStopped() {
		super.fireEditingStopped();
	}
	//
	//  Implement TableCellRenderer interface
	//
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (isSelected)
		{
			renderSlider.setForeground(table.getSelectionForeground());
			renderSlider.setBackground(table.getSelectionBackground());
		}
		else
		{
			renderSlider.setForeground(table.getForeground());
			renderSlider.setBackground(table.getBackground());
		}

		// TODO right?
		if(value!=null) {
			// set min max
			renderSlider.setMaximum(((int[])value)[3]);
			renderSlider.setMinimum(((int[])value)[2]);
			// set values
			renderSlider.setValue(((int[])value)[0]);
			renderSlider.setUpperValue(((int[])value)[1]);
			renderSlider.updateUI();
		}

		return renderSlider;
	} 

	//
	//  Implement MouseListener interface
	//
	/*
	 *  When the mouse is pressed the editor is invoked. If you then then drag
	 *  the mouse to another cell before releasing it, the editor is still
	 *  active. Make sure editing is stopped when the mouse is released.
	 */
	public void mousePressed(MouseEvent e)
	{
		if (table.isEditing() &&  table.getCellEditor() == this)
			isButtonColumnEditor = true;
	}

	public void mouseReleased(MouseEvent e)
	{
		if (isButtonColumnEditor
				&&  table.isEditing())
			table.getCellEditor().stopCellEditing();

		isButtonColumnEditor = false;
	}

	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {} 
}
