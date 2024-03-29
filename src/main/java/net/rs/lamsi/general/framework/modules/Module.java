package net.rs.lamsi.general.framework.modules;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import net.rs.lamsi.general.framework.modules.listeners.HideShowChangedListener;
import net.rs.lamsi.general.framework.modules.menu.ModuleMenu;

public class Module extends JPanel {
  protected ModuleMenu menu;
  private JLabel lbTitle;
  private JCheckBox cbTitle;
  private JLabel lbTitleHidden;
  private JCheckBox cbTitleHidden;
  private JPanel pnOpen;
  private JPanel pnHidden;
  private JPanel pnContent;
  private JPanel pnTitle;

  protected boolean showTitleAlways = false;

  private HideShowChangedListener showListener;
  private JPanel panel;
  private Component titleStrut;

  /**
   * Create the panel.
   */
  public Module() {
    this("");
  }

  public Module(String stitle) {
    this(stitle, false);
  }

  public Module(String stitle, boolean westside) {
    this(stitle, westside, null);
  }

  public Module(String stitle, boolean useCheckBox, boolean westside) {
    this(stitle, useCheckBox, westside, null);
  }

  public Module(String stitle, boolean westside, ModuleMenu menu) {
    this(stitle, false, westside, menu);
  }

  public Module(String stitle, boolean useCheckBox, boolean westside, ModuleMenu menu) {
    setLayout(new BorderLayout(0, 0));
    setBorder(new LineBorder(new Color(0, 0, 0)));

    pnHidden = new JPanel();
    pnHidden.setVisible(false);
    add(pnHidden, BorderLayout.WEST);
    pnHidden.setLayout(new BorderLayout(0, 0));

    panel = new JPanel();
    pnHidden.add(panel, BorderLayout.NORTH);
    panel.setLayout(new BorderLayout(0, 0));


    JButton btnShow = new JButton("");
    panel.add(btnShow, BorderLayout.WEST);
    btnShow.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        hideModul(false);
        if (showListener != null)
          showListener.moduleChangedToShown(true);
      }
    });
    btnShow.setIcon(new ImageIcon(Module.class.getResource("/img/btn-plus-20x20.png")));
    btnShow.setPreferredSize(new Dimension(20, 20));
    btnShow.setMaximumSize(new Dimension(20, 20));
    btnShow.setMinimumSize(new Dimension(20, 20));

    titleStrut = Box.createHorizontalStrut(20);
    panel.add(titleStrut, BorderLayout.CENTER);
    titleStrut.setVisible(false);

    pnOpen = new JPanel();
    add(pnOpen, BorderLayout.CENTER);
    pnOpen.setLayout(new BorderLayout(0, 0));

    pnTitle = new JPanel();
    pnOpen.add(pnTitle, BorderLayout.NORTH);
    pnTitle.setLayout(new BorderLayout(0, 0));

    JButton btnHide = new JButton("");
    btnHide.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        hideModul(true);

        // resize parent
        if (getParent().getParent() != null)
          getParent().getParent().revalidate();
        else if (getParent() != null)
          getParent().revalidate();

        if (showListener != null)
          showListener.moduleChangedToShown(false);
      }
    });
    btnHide.setIcon(new ImageIcon(Module.class.getResource("/img/btn-minus-20x20.png")));
    btnHide.setPreferredSize(new Dimension(20, 20));
    btnHide.setMinimumSize(new Dimension(20, 20));
    btnHide.setMaximumSize(new Dimension(20, 20));
    pnTitle.add(btnHide, (westside ? BorderLayout.EAST : BorderLayout.WEST));

    if (useCheckBox) {
      cbTitle = new JCheckBox(stitle);
      cbTitle.addItemListener(e -> {
        if (cbTitle.isSelected() != cbTitleHidden.isSelected())
          cbTitleHidden.setSelected(cbTitle.isSelected());
      });
      cbTitle.setHorizontalAlignment(SwingConstants.CENTER);
      pnTitle.add(cbTitle, BorderLayout.CENTER);
    } else {
      lbTitle = new JLabel(stitle);
      lbTitle.setHorizontalAlignment(SwingConstants.CENTER);
      pnTitle.add(lbTitle, BorderLayout.CENTER);
    }
    pnContent = new JPanel();
    pnOpen.add(pnContent, BorderLayout.CENTER);
    pnContent.setLayout(new BorderLayout(0, 0));

    if (useCheckBox) {
      cbTitleHidden = new JCheckBox(stitle);
      cbTitleHidden.addItemListener(e -> {
        if (cbTitle.isSelected() != cbTitleHidden.isSelected())
          cbTitle.setSelected(cbTitleHidden.isSelected());
      });
      cbTitleHidden.setHorizontalAlignment(SwingConstants.CENTER);
      panel.add(cbTitleHidden, BorderLayout.EAST);
      cbTitleHidden.setVisible(false);
    } else {
      lbTitleHidden = new JLabel(stitle);
      lbTitleHidden.setHorizontalAlignment(SwingConstants.CENTER);
      panel.add(lbTitleHidden, BorderLayout.EAST);
      lbTitleHidden.setVisible(false);
    }

    if (menu != null)
      addPopupMenu(menu);
  }

  // adding a menu
  public void addPopupMenu(ModuleMenu menu) {
    this.menu = menu;
    getPnTitle().add(menu, BorderLayout.EAST);
    getPnTitle().validate();
  }


  public void hideModul(boolean hide) {
    getPnOpen().setVisible(!hide);
    getPnHidden().setVisible(hide);
  }

  public void setTitle(String title) {
    getLbTitle().setText(title);
    getLbTitleHidden().setText(title);
  }

  public JLabel getLbTitle() {
    return lbTitle;
  }

  public JPanel getPnOpen() {
    return pnOpen;
  }

  public JPanel getPnHidden() {
    return pnHidden;
  }

  public JPanel getPnContent() {
    return pnContent;
  }

  public JPanel getPnTitle() {
    return pnTitle;
  }

  public void addHideShowChangedListener(HideShowChangedListener listener) {
    showListener = listener;
  }

  public JCheckBox getCbTitle() {
    return cbTitle;
  }

  public static double doubleFromTxt(JTextField txt) {
    try {
      return Double.parseDouble(txt.getText());
    } catch (Exception ex) {
      return 0;
    }
  }

  public static float floatFromTxt(JTextField txt) {
    try {
      return Float.parseFloat(txt.getText());
    } catch (Exception ex) {
      return 0;
    }
  }

  public static int intFromTxt(JTextField txt) {
    try {
      return Integer.parseInt(txt.getText());
    } catch (Exception ex) {
      return 0;
    }
  }

  public ModuleMenu getPopupMenu() {
    return menu;
  }

  public boolean isShowTitleAlways() {
    return showTitleAlways;
  }

  public void setShowTitleAlways(boolean showTitleAlways) {
    this.showTitleAlways = showTitleAlways;
    if (lbTitleHidden != null)
      getLbTitleHidden().setVisible(showTitleAlways);
    if (cbTitleHidden != null)
      cbTitleHidden.setVisible(showTitleAlways);
    getTitleStrut().setVisible(showTitleAlways);
  }

  /**
   * True if this module does not use a checkbox title or if the chackbox is selected
   * 
   * @return
   */
  public boolean isActive() {
    return cbTitle == null || cbTitle.isSelected();
  }

  public JLabel getLbTitleHidden() {
    return lbTitleHidden;
  }

  public Component getTitleStrut() {
    return titleStrut;
  }
}
