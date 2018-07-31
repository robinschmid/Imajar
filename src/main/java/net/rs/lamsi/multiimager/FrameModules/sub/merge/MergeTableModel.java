package net.rs.lamsi.multiimager.FrameModules.sub.merge;

import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.rs.lamsi.general.settings.image.SettingsImageMerge;
import net.rs.lamsi.general.settings.image.merge.SettingsSingleMerge;

public class MergeTableModel extends AbstractTableModel {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  private static final String[] columnNames = {"Group", "dx", "dy", "angle", "ax", "ay"};
  private SettingsImageMerge settMerge;


  public int getColumnCount() {
    return columnNames.length;
  }

  public int getRowCount() {
    return settMerge == null ? 0 : settMerge.getMergeSettings().size();
  }

  public String getColumnName(int col) {
    return columnNames[col];
  }

  public Object getValueAt(int row, int col) {
    try {
      if (col == 0)
        return settMerge.getImageList().get(row).getImageGroup().getName();

      //
      SettingsSingleMerge s = settMerge.getMergeSettings(row);
      switch (col) {
        case 1:
          return s.getDX();
        case 2:
          return s.getDY();
        case 3:
          return s.getAngle();
        case 4:
          return s.getAnchor().getX();
        case 5:
          return s.getAnchor().getY();
      }
    } catch (Exception e) {
      logger.warn("No value in merge table", e);
    }
    return -1;
  }

  /*
   * JTable uses this method to determine the default renderer/ editor for each cell. If we didn't
   * implement this method, then the last column would contain text ("true"/"false"), rather than a
   * check box.
   */
  public Class getColumnClass(int c) {
    return getValueAt(0, c).getClass();
  }

  public boolean isCellEditable(int row, int col) {
    return col != 0;
  }

  /*
   * Don't need to implement this method unless your table's data can change.
   */
  public void setValueAt(Object value, int row, int col) {
    logger.debug("Setting merge table value at row {} col {} to {} of type {}", row, col, value,
        value.getClass());


    SettingsSingleMerge s = settMerge.getMergeSettings(row);
    switch (col) {
      case 1:
        s.setDX((float) value);
        break;
      case 2:
        s.setDY((float) value);
        break;
      case 3:
        s.setAngle((float) value);
        break;
      case 4:
        s.setAnchor((float) value, s.getAnchor().y);
        break;
      case 5:
        s.setAnchor(s.getAnchor().x, (float) value);
        break;
    }

    fireTableCellUpdated(row, col);
  }

  public void setMergeSettings(SettingsImageMerge si) {
    settMerge = si;
    fireTableDataChanged();
  }

}
