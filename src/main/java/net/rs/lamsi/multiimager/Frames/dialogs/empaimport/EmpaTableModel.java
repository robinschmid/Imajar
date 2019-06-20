package net.rs.lamsi.multiimager.Frames.dialogs.empaimport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import net.rs.lamsi.utils.mywriterreader.TxtWriter;

public class EmpaTableModel extends AbstractTableModel {

  private List<EmpaTableRow> rows;

  public EmpaTableModel() {
    rows = new ArrayList<>();
  }

  public List<EmpaTableRow> getRows() {
    return rows;
  }

  @Override
  public int getRowCount() {
    return rows == null ? 0 : rows.size() + 1;
  }

  @Override
  public int getColumnCount() {
    return EmpaColumn.values().length;
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    if (rows == null || rowIndex < 0 || rowIndex > rows.size() - 1)
      return null;

    return rows.get(rowIndex).getValue(columnIndex);
  }

  @Override
  public String getColumnName(int col) {
    return EmpaColumn.values()[col].getTitle();
  }

  @Override
  public Class<?> getColumnClass(int col) {
    return EmpaColumn.values()[col].getClazz();
  }

  @Override
  public boolean isCellEditable(int row, int col) {
    return true;
  }

  @Override
  public void setValueAt(Object value, int row, int col) {
    // add row if value of next row is changed
    if (row == rows.size()) {
      addRow(new EmpaTableRow());
    }
    rows.get(row).setValue(col, value);
    fireTableCellUpdated(row, col);
  }

  private void addRow(EmpaTableRow row) {
    rows.add(row);
    fireTableRowsInserted(rows.size() - 1, rows.size() - 1);
  }

  public void loadTableData(String[] data) {
    for (String s : data) {
      loadTableData(s);
    }
  }

  private void loadTableData(String line) {
    try {
      String[] s = line.split(",");
      if (s.length > 2) {
        EmpaTableRow row = new EmpaTableRow(Boolean.parseBoolean(s[0]), Boolean.parseBoolean(s[1]),
            s[2], Double.parseDouble(s[3]), Double.parseDouble(s[4]), Double.parseDouble(s[5]),
            Double.parseDouble(s[6]));
        addRow(row);
      }
    } catch (Exception e) {
    }
  }

  public void loadTableData(File file) {
    TxtWriter writer = new TxtWriter();
    String[] data = writer.readLines(file).toArray(new String[0]);
    if (data != null) {
      rows.clear();
      loadTableData(data);
    }
  }

  public void saveTableData(File file) {
    TxtWriter writer = new TxtWriter();
    Object[][] data = new Object[rows.size() + 1][EmpaColumn.values().length];
    int i = 0;
    for (EmpaColumn v : EmpaColumn.values()) {
      data[0][i] = v.getTitle();
      i++;
    }

    for (i = 0; i < rows.size(); i++) {
      data[1 + i] = rows.get(i).toObjectArray();
    }

    writer.writeDataArrayToFile(file, data, ",");
  }

}
