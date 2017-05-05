package net.rs.lamsi.general.framework.basics;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

public class JCheckBoxList extends JList {

  public JCheckBoxList() {
    setCellRenderer(new CellRenderer());
    addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        int index = locationToIndex(e.getPoint());
        if (index != -1) {
          JCheckBox checkbox = (JCheckBox) getModel().getElementAt(
              index);
          checkbox.setSelected(!checkbox.isSelected());
          repaint();
        }
      }
    });
    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
  }

  protected class CellRenderer implements ListCellRenderer {
    public Component getListCellRendererComponent(JList list, Object value,
        int index, boolean isSelected, boolean cellHasFocus) {
      JCheckBox checkbox = (JCheckBox) value;

      if (isSelected) {
        // checkbox.setBorderPainted(true);
        // checkbox.setForeground(UIManager.getColor("List.selectionForeground"));
        // checkbox.setBackground(UIManager.getColor("List.selectionBackground"));
      } else {
        // checkbox.setBorderPainted(false);
        // checkbox.setForeground(UIManager.getColor("List.foreground"));
        checkbox.setBackground(UIManager.getColor("List.background"));
      }
      return checkbox;
    }
  }

  public void selectAll() {
    int size = this.getModel().getSize();
    for (int i = 0; i < size; i++) {
      JCheckBox checkbox = (JCheckBox) this.getModel().getElementAt(i);
      checkbox.setSelected(true);
    }
    this.repaint();
  }

  public void deselectAll() {
    int size = this.getModel().getSize();
    for (int i = 0; i < size; i++) {
      JCheckBox checkbox = (JCheckBox) this.getModel().getElementAt(i);
      checkbox.setSelected(false);
    }
    this.repaint();
  }
}
