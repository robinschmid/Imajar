package net.rs.lamsi.multiimager.Frames.dialogs.selectdata;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.general.settings.image.selection.SettingsPolygonSelection;
import net.rs.lamsi.general.settings.image.selection.SettingsShapeSelection;
import net.rs.lamsi.general.settings.image.selection.SettingsShapeSelection.ROI;
import net.rs.lamsi.general.settings.image.selection.SettingsShapeSelection.SelectionMode;

public class CreateMultiRoiDialog extends JDialog {

  private final JPanel contentPanel = new JPanel();
  private JTextField txtNewInX;
  private JTextField txtHeight;
  private JTextField txtWidth;
  private JTextField txtXSpace;
  private JTextField txtNewInY;
  private JTextField txtYSpace;
  private JTextField txtY;
  private JTextField txtX;
  private Consumer<List<SettingsShapeSelection>> okConsumer;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    try {
      CreateMultiRoiDialog dialog = new CreateMultiRoiDialog();
      dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
      dialog.setVisible(true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Create the dialog.
   */
  public CreateMultiRoiDialog() {
    setBounds(100, 100, 211, 398);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(new BorderLayout(0, 0));
    {
      JPanel panel = new JPanel();
      contentPanel.add(panel, BorderLayout.NORTH);
      panel.setLayout(new MigLayout("", "[][]", "[][][][][][][][][][][][]"));
      {
        JLabel lblStartX = new JLabel("start x");
        panel.add(lblStartX, "cell 0 0,alignx trailing");
      }
      {
        txtX = new JTextField();
        txtX.setText("0");
        panel.add(txtX, "cell 1 0,growx");
        txtX.setColumns(10);
      }
      {
        JLabel lblStartY = new JLabel("start y");
        panel.add(lblStartY, "cell 0 1,alignx trailing");
      }
      {
        txtY = new JTextField();
        txtY.setText("0");
        panel.add(txtY, "cell 1 1,growx");
        txtY.setColumns(10);
      }
      {
        JLabel lblWidth = new JLabel("width");
        lblWidth.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lblWidth, "cell 0 2,alignx trailing");
      }
      {
        txtWidth = new JTextField();
        txtWidth.setText("100");
        panel.add(txtWidth, "cell 1 2,growx");
        txtWidth.setColumns(10);
      }
      {
        JLabel lblHeight = new JLabel("height");
        panel.add(lblHeight, "cell 0 3,alignx trailing");
      }
      {
        txtHeight = new JTextField();
        txtHeight.setText("100");
        panel.add(txtHeight, "cell 1 3,growx");
        txtHeight.setColumns(10);
      }
      {
        JLabel lblInLine = new JLabel("In line");
        lblInLine.setFont(new Font("Tahoma", Font.BOLD, 11));
        panel.add(lblInLine, "cell 0 5");
      }
      {
        JLabel lblNewRois = new JLabel("new ROIs");
        panel.add(lblNewRois, "cell 0 6,alignx trailing");
      }
      {
        txtNewInX = new JTextField();
        txtNewInX.setText("1");
        panel.add(txtNewInX, "cell 1 6,growx");
        txtNewInX.setColumns(10);
      }
      {
        JLabel lblXSpacing = new JLabel("x spacing");
        panel.add(lblXSpacing, "cell 0 7,alignx trailing");
      }
      {
        txtXSpace = new JTextField();
        txtXSpace.setText("100");
        panel.add(txtXSpace, "cell 1 7,growx");
        txtXSpace.setColumns(10);
      }
      {
        JLabel lblInColumn = new JLabel("In column");
        lblInColumn.setFont(new Font("Tahoma", Font.BOLD, 11));
        panel.add(lblInColumn, "cell 0 9");
      }
      {
        JLabel label = new JLabel("new ROIs");
        panel.add(label, "cell 0 10,alignx trailing");
      }
      {
        txtNewInY = new JTextField();
        txtNewInY.setText("1");
        txtNewInY.setColumns(10);
        panel.add(txtNewInY, "cell 1 10,growx");
      }
      {
        JLabel lblYSpacing = new JLabel("y spacing");
        panel.add(lblYSpacing, "cell 0 11,alignx trailing");
      }
      {
        txtYSpace = new JTextField();
        txtYSpace.setText("100");
        txtYSpace.setColumns(10);
        panel.add(txtYSpace, "cell 1 11,growx");
      }
    }
    {
      JPanel buttonPane = new JPanel();
      buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
      getContentPane().add(buttonPane, BorderLayout.SOUTH);
      {
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> apply());
        okButton.setActionCommand("OK");
        buttonPane.add(okButton);
        getRootPane().setDefaultButton(okButton);
      }
      {
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> setVisible(false));
        cancelButton.setActionCommand("Cancel");
        buttonPane.add(cancelButton);
      }
    }
  }

  public void start(Consumer<List<SettingsShapeSelection>> okConsumer) {
    this.okConsumer = okConsumer;
    setVisible(true);
  }

  /**
   * 
   */
  private void apply() {
    try {
      int inrow = Integer.parseInt(txtNewInX.getText());
      int incol = Integer.parseInt(txtNewInY.getText());

      float sx = Float.parseFloat(txtX.getText());
      float sy = Float.parseFloat(txtY.getText());
      float w = Float.parseFloat(txtWidth.getText());
      float h = Float.parseFloat(txtHeight.getText());
      float dx = Float.parseFloat(txtXSpace.getText());
      float dy = Float.parseFloat(txtYSpace.getText());
      if (okConsumer != null) {
        // get values:


        // create list
        List<SettingsShapeSelection> list = new ArrayList<>();

        // in row:
        for (int r = 0; r < incol; r++) {
          for (int c = 0; c < inrow; c++) {
            float x = sx + c * dx;
            float y = sy + r * dy;
            Rectangle2D rect = new Rectangle2D.Float(x, y, w, h);
            SettingsPolygonSelection sett =
                new SettingsPolygonSelection(null, ROI.SAMPLE, SelectionMode.SELECT, rect);
            list.add(sett);
          }
        }

        // consume
        okConsumer.accept(list);
      }
    } catch (Exception e) {
    }
    setVisible(false);
  }

  public JTextField getTxtX() {
    return txtX;
  }

  public JTextField getTxtY() {
    return txtY;
  }

  public JTextField getTxtWidth() {
    return txtWidth;
  }

  public JTextField getTxtHeight() {
    return txtHeight;
  }

  public JTextField getTxtNewInX() {
    return txtNewInX;
  }

  public JTextField getTxtXSpace() {
    return txtXSpace;
  }

  public JTextField getTxtNewInY() {
    return txtNewInY;
  }

  public JTextField getTxtYSpace() {
    return txtYSpace;
  }
}
