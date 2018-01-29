package net.rs.lamsi.multiimager.FrameModules.sub.paintscale;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.general.framework.basics.JColorList;
import net.rs.lamsi.general.framework.basics.JColorPickerButton;

public class ColorListPanel extends JPanel {
  private JColorList listColor;
  private DefaultListModel<JColorPickerButton> listModel;
  private JColorChooser chooser;
  private JDialog dialog;
  private JTextField txtR;
  private JTextField txtG;
  private JTextField txtB;
  private JTextField txtH;
  private JTextField txtS;
  private JTextField txtBrightness;

  public ColorListPanel() {
    setLayout(new MigLayout("", "[grow][]", "[][grow]"));

    JLabel lblColorList = new JLabel("color list (maximum on top)");
    add(lblColorList, "cell 0 0");

    JScrollPane scrollPane = new JScrollPane();
    add(scrollPane, "cell 0 1,grow");

    listColor = new JColorList();
    scrollPane.setViewportView(listColor);
    listModel = new DefaultListModel<JColorPickerButton>();
    listColor.setModel((DefaultListModel) listModel);

    JPanel panel_1 = new JPanel();
    add(panel_1, "cell 1 1,grow");
    panel_1.setLayout(new MigLayout("", "[][grow]", "[][][][][][][][][]"));

    JButton btnAdd = new JButton("Add");
    btnAdd.addActionListener(e -> {
      if (dialog == null)
        dialog = JColorChooser.createDialog(this, // parent comp
            "Pick a color", // dialog title
            false, // modality
            chooser, ok -> addColor(chooser.getColor()), null);

      dialog.setVisible(true);
    });
    panel_1.add(btnAdd, "cell 0 0,growx");

    JButton btnRemove = new JButton("Remove");
    btnRemove.addActionListener(e -> removeColor());

    JButton btnAddRgb = new JButton("Add RGB");
    btnAddRgb.addActionListener(e -> {
      try {
        int r = Integer.valueOf(txtR.getText());
        int g = Integer.valueOf(txtG.getText());
        int b = Integer.valueOf(txtB.getText());
        addRGBColor(r, g, b);
      } catch (Exception e2) {
        e2.printStackTrace();
      }
    });
    panel_1.add(btnAddRgb, "cell 1 0,growx");
    panel_1.add(btnRemove, "cell 0 1,growx");

    JButton btnMoveUp = new JButton("Move up");
    btnMoveUp.addActionListener(e -> shiftSelected(-1));

    JLabel lblR = new JLabel("R");
    panel_1.add(lblR, "flowx,cell 1 1");

    txtR = new JTextField();
    txtR.setHorizontalAlignment(SwingConstants.CENTER);
    txtR.setText("255");
    panel_1.add(txtR, "cell 1 1,growx");
    txtR.setColumns(10);
    panel_1.add(btnMoveUp, "cell 0 2,growx");

    JButton btnMoveDown = new JButton("Move down");
    btnMoveDown.addActionListener(e -> shiftSelected(+1));

    JLabel lblG = new JLabel("G");
    panel_1.add(lblG, "flowx,cell 1 2");

    txtG = new JTextField();
    txtG.setHorizontalAlignment(SwingConstants.CENTER);
    txtG.setText("255");
    panel_1.add(txtG, "cell 1 2,growx");
    txtG.setColumns(10);
    panel_1.add(btnMoveDown, "cell 0 3,growx");

    JLabel lblB = new JLabel("B");
    panel_1.add(lblB, "flowx,cell 1 3");

    txtB = new JTextField();
    txtB.setHorizontalAlignment(SwingConstants.CENTER);
    txtB.setText("255");
    panel_1.add(txtB, "cell 1 3,growx");
    txtB.setColumns(10);

    JButton btnAddHsb = new JButton("Add HSB");
    btnAddHsb.addActionListener(e -> {
      try {
        float h = Float.valueOf(txtH.getText()) / 100.f;
        float s = Float.valueOf(txtS.getText()) / 100.f;
        float b = Float.valueOf(txtBrightness.getText()) / 100.f;
        addHSBColor(h, s, b);
      } catch (Exception e2) {
        e2.printStackTrace();
      }
    });
    panel_1.add(btnAddHsb, "cell 1 5,growx");

    JLabel lblH = new JLabel("H");
    panel_1.add(lblH, "flowx,cell 1 6");

    txtH = new JTextField();
    txtH.setText("100");
    txtH.setHorizontalAlignment(SwingConstants.CENTER);
    txtH.setColumns(10);
    panel_1.add(txtH, "cell 1 6,growx");

    JLabel lblS = new JLabel("S");
    panel_1.add(lblS, "flowx,cell 1 7");

    txtS = new JTextField();
    txtS.setText("100");
    txtS.setHorizontalAlignment(SwingConstants.CENTER);
    txtS.setColumns(10);
    panel_1.add(txtS, "cell 1 7,growx");

    JLabel lblB_1 = new JLabel("B");
    panel_1.add(lblB_1, "flowx,cell 1 8");

    txtBrightness = new JTextField();
    txtBrightness.setText("100");
    txtBrightness.setHorizontalAlignment(SwingConstants.CENTER);
    txtBrightness.setColumns(10);
    panel_1.add(txtBrightness, "cell 1 8,growx");

    chooser = new JColorChooser();
  }

  public void addRGBColor(int r, int g, int b) {
    Color c = new Color(r, g, b);
    addColor(c);
  }

  public void addHSBColor(float h, float s, float b) {
    Color c = Color.getHSBColor(h, s, b);
    addColor(c);
  }

  private void shiftSelected(int shift) {
    int[] index = listColor.getSelectedIndices();
    ArrayList<Integer> nindex = new ArrayList<Integer>();
    if (index.length > 0) {
      if (shift > 0) {
        for (int i = index.length - 1; i >= 0; i--) {
          int si = index[i];
          if (si + 1 < listModel.size()) {
            swapElements(si, si + 1);
            nindex.add(si + 1);
          }
        }
      } else if (shift < 0) {
        for (int i = 0; i < index.length; i++) {
          int si = index[i];
          if (si - 1 > 0) {
            swapElements(si, si - 1);
            nindex.add(si - 1);
          }
        }
      }
      int[] sel = nindex.stream().mapToInt(x -> x).toArray();
      listColor.setSelectedIndices(sel);
      listColor.updateUI();
    }
  }

  private void swapElements(int pos1, int pos2) {
    JColorPickerButton tmp = (JColorPickerButton) listModel.get(pos1);
    listModel.set(pos1, listModel.get(pos2));
    listModel.set(pos2, tmp);
  }

  public void removeColor() {
    int[] index = listColor.getSelectedIndices();
    if (index.length > 0) {
      for (int i = index.length - 1; i >= 0; i--) {
        listModel.remove(index[i]);
      }
    }
  }

  public Color addColor(Color color) {
    int index = listColor.getSelectedIndex();
    if (index == -1)
      index = listModel.getSize();
    listModel.add(index, new JColorPickerButton(this, color));
    return color;
  }

  /**
   * 
   * @return The selected colors or an empty list
   */
  public List<Color> getColorList() {
    ArrayList<Color> list = new ArrayList<Color>(listModel.size());
    for (int i = 0; i < listModel.size(); i++) {
      list.add(listModel.get(i).getColor());
    }
    return list;
  }

  public JTextField getTxtR() {
    return txtR;
  }

  public JTextField getTxtH() {
    return txtH;
  }

  public JTextField getTxtS() {
    return txtS;
  }

  public JTextField getTxtBrightness() {
    return txtBrightness;
  }

  public JTextField getTxtB() {
    return txtB;
  }

  public JTextField getTxtG() {
    return txtG;
  }

  public void setColorList(List<Color> clist) {
    listModel.removeAllElements();
    if (clist != null)
      for (Color c : clist)
        listModel.addElement(new JColorPickerButton(this, c));
    listColor.updateUI();
  }
}
