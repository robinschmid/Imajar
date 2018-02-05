package net.rs.lamsi.multiimager.Frames.dialogs.singleparticle;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.List;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import org.jfree.chart.JFreeChart;
import net.rs.lamsi.general.datamodel.image.SingleParticleImage;
import net.rs.lamsi.general.framework.listener.DelayedDocumentListener;
import net.rs.lamsi.general.myfreechart.EChartFactory;
import net.rs.lamsi.general.myfreechart.swing.EChartPanel;
import net.rs.lamsi.multiimager.FrameModules.ModuleSingleParticleImage;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;

public class SingleParticleDialog extends JFrame {

  private final JPanel contentPanel = new JPanel();

  private ModuleSingleParticleImage module;
  private SingleParticleImage img;
  private DelayedDocumentListener ddlUpdate, ddlRepaint;
  private EChartPanel pnHisto, pnHistoFiltered;
  private JPanel southwest;
  private JPanel southeast;
  private JPanel north;
  private JTextField txtBinWidth;
  private JCheckBox cbExcludeSmallerNoise;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    try {
      SingleParticleDialog dialog = new SingleParticleDialog();
      dialog.setVisible(true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Create the dialog.
   */
  public SingleParticleDialog() {
    this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    setBounds(100, 100, 872, 707);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(new BorderLayout(0, 0));
    {
      JPanel west = new JPanel();
      contentPanel.add(west, BorderLayout.WEST);
      west.setLayout(new BorderLayout(0, 0));
      {
        module = new ModuleSingleParticleImage(ImageEditorWindow.getEditor(), false, e -> update());
        west.add(module, BorderLayout.CENTER);
      }
      {
        JPanel panel = new JPanel();
        west.add(panel, BorderLayout.NORTH);
      }
    }
    {
      JPanel center1 = new JPanel();
      contentPanel.add(center1, BorderLayout.CENTER);
      center1.setLayout(new BorderLayout(0, 0));
      {
        JSplitPane splitPane = new JSplitPane();
        center1.add(splitPane, BorderLayout.CENTER);
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        {
          north = new JPanel();
          splitPane.setLeftComponent(north);
          north.setLayout(new BorderLayout(0, 0));
        }
        {
          JPanel south = new JPanel();
          splitPane.setRightComponent(south);
          south.setLayout(new GridLayout(0, 2, 0, 0));
          {
            southwest = new JPanel();
            south.add(southwest);
            southwest.setLayout(new BorderLayout(0, 0));
          }
          {
            southeast = new JPanel();
            south.add(southeast);
            southeast.setLayout(new BorderLayout(0, 0));
          }
        }
      }
      {
        JPanel pnHistoSett = new JPanel();
        center1.add(pnHistoSett, BorderLayout.SOUTH);
        {
          cbExcludeSmallerNoise = new JCheckBox("exclude <noise level");
          cbExcludeSmallerNoise.setSelected(true);
          pnHistoSett.add(cbExcludeSmallerNoise);
        }
        {
          Component horizontalStrut = Box.createHorizontalStrut(20);
          pnHistoSett.add(horizontalStrut);
        }
        {
          JLabel lblBinWidth = new JLabel("bin width");
          pnHistoSett.add(lblBinWidth);
        }
        {
          txtBinWidth = new JTextField();
          txtBinWidth.setText("2030");
          pnHistoSett.add(txtBinWidth);
          txtBinWidth.setColumns(7);
        }
      }
    }
    {
      JPanel buttonPane = new JPanel();
      buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
      getContentPane().add(buttonPane, BorderLayout.SOUTH);
      {
        JButton okButton = new JButton("OK");
        buttonPane.add(okButton);
        getRootPane().setDefaultButton(okButton);
      }
      {
        JButton cancelButton = new JButton("Cancel");
        buttonPane.add(cancelButton);
      }
    }

    ddlUpdate = new DelayedDocumentListener(e -> autoUpdate());
    ddlRepaint = new DelayedDocumentListener(e -> repaint());
    //
    module.addAutoupdater(al -> autoUpdate(), cl -> autoUpdate(), ddlUpdate, e -> autoUpdate(),
        il -> autoUpdate());
    module.addAutoRepainter(al -> repaint(), cl -> repaint(), ddlRepaint, e -> repaint(),
        il -> repaint());

    // histogram settings
    cbExcludeSmallerNoise.addItemListener(e -> updateHistograms());
    txtBinWidth.getDocument()
        .addDocumentListener(new DelayedDocumentListener(e -> updateHistograms()));
  }

  public void setSPImage(SingleParticleImage img) {
    this.img = img;
    if (img != null) {
      boolean auto = module.isAutoUpdating();
      module.setAutoUpdating(false);
      // set to
      module.setCurrentImage(img, true);
      updateHistograms();

      // add image

      module.setAutoUpdating(auto);
      contentPanel.revalidate();
    }
  }


  private void updateHistograms() {
    if (img != null) {
      double binwidth = Double.NaN;
      try {
        binwidth = Double.parseDouble(txtBinWidth.getText());
      } catch (Exception e) {
      }
      if (!Double.isNaN(binwidth)) {
        double noise = img.getSettings().getSettSingleParticle().getNoiseLevel();
        // create histogram
        double[] data = null;
        if (cbExcludeSmallerNoise.isSelected()) {
          List<Double> dlist = img.getSelectedDataAsList(true, true);
          data = dlist.stream().mapToDouble(d -> d).filter(d -> d >= noise).toArray();
        } else
          data = img.getSelectedDataAsArray(true, true);

        JFreeChart histo = EChartFactory.createHistogram(data, "I", binwidth);
        pnHisto = new EChartPanel(histo);
        southwest.removeAll();
        southwest.add(pnHisto, BorderLayout.CENTER);
        // after removing split events

        double[] filtered = img.getSPDataArraySelected();

        // do not show noise
        if (cbExcludeSmallerNoise.isSelected())
          filtered = Arrays.stream(filtered).filter(d -> d >= noise).toArray();

        histo = EChartFactory.createHistogram(filtered, "I", binwidth);
        pnHistoFiltered = new EChartPanel(histo);
        southeast.removeAll();
        southeast.add(pnHistoFiltered, BorderLayout.CENTER);

        southeast.getParent().revalidate();
        southeast.getParent().repaint();
      }
    }
  }

  public void autoUpdate() {
    if (module.isAutoUpdating())
      update();
  }

  public void update() {
    if (img != null) {
      module.writeAllToSettings(img.getSettings());
      updateHistograms();
    }
  }

  public void repaint() {
    if (img != null)
      module.writeAllToSettings(img.getSettings());
  }

  public JPanel getSouthwest() {
    return southwest;
  }

  public JPanel getSoutheast() {
    return southeast;
  }

  public JPanel getNorth() {
    return north;
  }

  public JTextField getTxtBinWidth() {
    return txtBinWidth;
  }

  public JCheckBox getCbExcludeSmallerNoise() {
    return cbExcludeSmallerNoise;
  }
}
