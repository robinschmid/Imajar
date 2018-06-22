package net.rs.lamsi.massimager.Frames.Panels.peaktable;

import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import net.sf.mzmine.datamodel.PeakList;


public class PeakTableModel extends AbstractTableModel {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private Vector<PeakTableRow> peakRowList = new Vector<PeakTableRow>();


  /**
   * Constructor, assign given dataset to this table
   */
  public PeakTableModel() {}

  public void addRow(PeakTableRow row) {
    peakRowList.add(row);
    fireTableDataChanged();
  }

  public void removeRow(int i) {
    if (i < peakRowList.size())
      peakRowList.removeElementAt(i);
    // fire update
    fireTableRowsDeleted(i, i);
  }

  public void removeRows(int[] selectedRows) {
    for (int i = selectedRows.length - 1; i >= 0; i--) {
      removeRow(selectedRows[i]);
    }
  }

  public void removeAllRows() {
    int size = peakRowList.size();
    peakRowList.removeAllElements();
    fireTableRowsDeleted(0, size - 1);
  }

  public int getColumnCount() {
    return PeakTableColumnType.values().length;
  }

  public int getRowCount() {
    return peakRowList.size();
  }

  public String getColumnName(int col) {
    return getCommonColumn(col).getColumnName();
  }

  public Class<?> getColumnClass(int col) {
    PeakTableColumnType commonColumn = getCommonColumn(col);
    return commonColumn.getColumnClass();
  }


  /**
   * This method returns the value at given coordinates of the dataset or null if it is a missing
   * value
   */

  public Object getValueAt(int row, int col) {
    try {

      PeakTableRow peakRow = peakRowList.get(row);

      PeakTableColumnType commonColumn = getCommonColumn(col);

      switch (commonColumn) {
        case ROWID:
          return new String(peakRow.getID());
        case FILENAME:
          return new String(peakRow.getRawDataName());
        case PEAKLISTNAME:
          return new String(peakRow.getPeakListName());
        case MZ:
          return peakRow.getMz();
        case AREA:
          return new Double(peakRow.getArea());
        case HEIGHT:
          return new Double(peakRow.getHeight());
        case MASS:
          return peakRow.getMass();
        case RT:
          return new Double(peakRow.getRt());
        case RTMAX:
          return new Double(peakRow.getRtMax());
        case RTMIN:
          return new Double(peakRow.getRtMin());
        case CHARGE:
          return new Integer(peakRow.getCharge());
      }


    } catch (Exception e) {
      return null;
    }

    return null;

  }

  public boolean isCellEditable(int row, int col) {
    PeakTableColumnType columnType = getCommonColumn(col);

    return ((columnType == PeakTableColumnType.CHARGE)
        || (columnType == PeakTableColumnType.ROWID));
  }

  public void setValueAt(Object value, int row, int col) {
    PeakTableColumnType columnType = getCommonColumn(col);
    setValueAt(value, row, columnType);
  }

  public void setValueAt(Object value, int row, PeakTableColumnType columnType) {

    PeakTableRow prow = peakRowList.get(row);
    // maybe set it here
    switch (columnType) {
      case ROWID:
        prow.setID(String.valueOf(value));
        break;
      case FILENAME:
        prow.setRawDataName(String.valueOf(value));
        break;
      case PEAKLISTNAME:
        prow.setPeakList((PeakList) value);
        break;
      case AREA:
        prow.setArea((double) value);
        break;
      case HEIGHT:
        prow.setHeight((double) value);
        break;
      case MASS:
        prow.setMass((double) value);
        break;
      case MZ:
        prow.setMz((double) value);
        break;
      case RT:
        prow.setRt((double) value);
        break;
      case RTMAX:
        prow.setRtMax((double) value);
        break;
      case RTMIN:
        prow.setRtMin((double) value);
        break;
      case CHARGE:
        prow.setCharge((int) (value));
        // calc Mass
        double calcmass = prow.getMz() * prow.getCharge();
        setValueAt(calcmass, row, PeakTableColumnType.MASS);
        break;
    }
    fireTableCellUpdated(row, columnType.ordinal());
    // update repaint
  }

  boolean isCommonColumn(int col) {
    return col < PeakTableColumnType.values().length;
  }

  public static PeakTableColumnType getCommonColumn(int col) {

    PeakTableColumnType commonColumns[] = PeakTableColumnType.values();

    if (col < commonColumns.length)
      return commonColumns[col];

    return null;
  }

  public Vector<PeakTableRow> getPeakRowList() {
    return peakRowList;
  }

}
