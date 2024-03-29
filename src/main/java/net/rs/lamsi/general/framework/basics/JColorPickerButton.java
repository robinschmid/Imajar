package net.rs.lamsi.general.framework.basics;

// ColorPicker.java
// A quick test of the JColorChooser dialog.
//
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.rs.lamsi.general.framework.listener.ColorChangedListener;

public class JColorPickerButton extends JButton {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  JDialog dialog;
  JColorChooser chooser = new JColorChooser();
  Component parentFrame;
  // changelistener
  protected ColorChangedListener colorChangedListener;

  public JColorPickerButton(Component parentFrame) {
    super();
    this.parentFrame = parentFrame;
    setPreferredSize(new Dimension(25, 25));
    this.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        showDialog();
      }
    });
  }

  public JColorPickerButton(Component parentFrame, Color c) {
    this(parentFrame);
    setColor(c);
  }

  @Override
  public void paint(Graphics g) {
    // super.paint(g);
    g.setColor(getBackground());
    g.fillRect(0, 0, getWidth(), getHeight());

    g.setColor(Color.BLACK);
    g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

    paintBorder(g);
  }



  public void colorChanged(Color color) {
    this.setBackground(color);
    if (colorChangedListener != null)
      colorChangedListener.colorChanged(color);
  }


  class OkListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      Color color = chooser.getColor();
      colorChanged(color);
    }
  }
  class CancelListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {}
  }

  public void showDialog() {
    chooser.setColor(this.getBackground());
    //
    // New Dialog
    try {
      if (dialog == null)
        dialog = JColorChooser.createDialog(parentFrame, // parent comp
            "Pick A Color", // dialog title
            false, // modality
            chooser, new OkListener(), new CancelListener());

      dialog.setVisible(true);
      //
    } catch (Exception ex) {
      logger.error("", ex);
    }
    if (dialog != null) {
      dialog.setVisible(true);
    }
  }

  public void addColorChangedListener(ColorChangedListener colorChangedListener) {
    this.colorChangedListener = colorChangedListener;
  }

  public Color getColor() {
    return getBackground();
  }

  /**
   * same as color changed
   * 
   * @param c
   */
  public void setColor(Color c) {
    colorChanged(c);
  }

  /**
   * same as color changed
   * 
   * @param c
   */
  public void setColor(Paint c) {
    setColor((Color) c);
  }
}
