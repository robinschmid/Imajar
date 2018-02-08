package net.rs.lamsi.multiimager.FrameModules.sub.paintscale;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class ColorListDialog extends JDialog {

  private static final long serialVersionUID = 1L;
  private ColorListPanel list;
  private List<Color> acceptedList;
  private Consumer<List<Color>> listener;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    try {
      ColorListDialog dialog = new ColorListDialog();
      dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
      dialog.setVisible(true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Create the dialog.
   */
  public ColorListDialog() {
    setBounds(100, 100, 450, 300);
    getContentPane().setLayout(new BorderLayout());

    list = new ColorListPanel();

    getContentPane().add(list, BorderLayout.CENTER);
    {
      JPanel buttonPane = new JPanel();
      buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
      getContentPane().add(buttonPane, BorderLayout.SOUTH);
      {
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
          setColorList(list.getColorList(), true);
          setVisible(false);
        });
        buttonPane.add(okButton);
        getRootPane().setDefaultButton(okButton);
      }
      {
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> setVisible(false));
        buttonPane.add(cancelButton);
      }
    }
    pack();
  }

  @Override
  public void setVisible(boolean b) {
    if (acceptedList != null) {
      list.setColorList(acceptedList);
    }
    super.setVisible(b);
  }

  /**
   * 
   * @return The selected colors or an empty list
   */
  public List<Color> getColorList() {
    if (acceptedList == null)
      acceptedList = list.getColorList();
    return acceptedList;
  }

  public void setColorList(List<Color> colorList, boolean notify) {
    boolean hasChanged = acceptedList == null || !acceptedList.equals(colorList);
    acceptedList = colorList;
    if (notify && hasChanged && listener != null)
      listener.accept(getColorList());
  }

  public void setChangeListener(Consumer<List<Color>> listener) {
    this.listener = listener;
  }

}
