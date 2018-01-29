package net.rs.lamsi.general.framework.basics;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

public class JColorList extends JList<JColorPickerButton> {

  private Border selected, undecorated;

  public JColorList() {
    setCellRenderer(new CellRenderer());
    addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        int index = locationToIndex(e.getPoint());
        if (index != -1) {
          if (e.getClickCount() == 2) {
            try {
              JColorPickerButton cbtn = (JColorPickerButton) getModel().getElementAt(index);
              cbtn.doClick();
            } catch (Exception ex) {
              ex.printStackTrace();
            }
          }
        }
      }
    });
    setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

    selected = new CompoundBorder(
        BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.WHITE, Color.BLACK),
        BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.WHITE, Color.BLACK));
    undecorated = BorderFactory.createEmptyBorder(0, 0, 0, 0);
  }

  protected class CellRenderer implements ListCellRenderer {
    public Component getListCellRendererComponent(JList list, Object value, int index,
        boolean isSelected, boolean cellHasFocus) {
      JColorPickerButton btn = (JColorPickerButton) value;

      if (isSelected) {
        btn.setBorderPainted(true);
        btn.setBorder(selected);
      } else {
        btn.setBorderPainted(false);
        btn.setBorder(undecorated);
      }
      return btn;
    }
  }

}
