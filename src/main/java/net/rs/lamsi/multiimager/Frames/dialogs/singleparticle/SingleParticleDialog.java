package net.rs.lamsi.multiimager.Frames.dialogs.singleparticle;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;
import org.jfree.chart.JFreeChart;
import net.rs.lamsi.general.datamodel.image.SingleParticleImage;
import net.rs.lamsi.general.framework.listener.DelayedDocumentListener;
import net.rs.lamsi.general.myfreechart.EChartFactory;
import net.rs.lamsi.general.myfreechart.swing.EChartPanel;
import net.rs.lamsi.multiimager.FrameModules.ModuleSingleParticleImage;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;

public class SingleParticleDialog extends JDialog {

  private final JPanel contentPanel = new JPanel();

  private ModuleSingleParticleImage module;
  private SingleParticleImage img;
  private DelayedDocumentListener ddlUpdate, ddlRepaint;
  private EChartPanel pnHisto;
  private JPanel southwest;
  private JPanel southeast;
  private JPanel north;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    try {
      SingleParticleDialog dialog = new SingleParticleDialog();
      dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
      dialog.setVisible(true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Create the dialog.
   */
  public SingleParticleDialog() {
    setBounds(100, 100, 595, 566);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(new BorderLayout(0, 0));
    {
      JSplitPane splitPane = new JSplitPane();
      splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
      splitPane.setResizeWeight(0.5);
      contentPanel.add(splitPane, BorderLayout.CENTER);
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
      JPanel west = new JPanel();
      contentPanel.add(west, BorderLayout.WEST);
      west.setLayout(new BorderLayout(0, 0));
      {
        module = new ModuleSingleParticleImage(ImageEditorWindow.getEditor());
        west.add(module, BorderLayout.CENTER);
        module.setLayout(new BorderLayout(0, 0));
      }
      {
        JPanel panel = new JPanel();
        west.add(panel, BorderLayout.NORTH);
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
  }

  public void setSPImage(SingleParticleImage img) {
    this.img = img;
    if (img != null) {
      boolean auto = module.isAutoUpdating();
      module.setAutoUpdating(false);
      // set to
      module.setCurrentImage(img, true);

      // create histogram
      double[] data = img.getSelectedDataAsArray(true);
      JFreeChart histo = EChartFactory.createHistogram(data);
      pnHisto = new EChartPanel(histo);
      southwest.removeAll();
      southwest.add(pnHisto, BorderLayout.CENTER);
      // after removing split events

      southeast.removeAll();
      southeast.add(pnHisto, BorderLayout.CENTER);

      // add image

      module.setAutoUpdating(auto);
      contentPanel.revalidate();
    }
  }


  public void autoUpdate() {
    if (module.isAutoUpdating())
      update();
  }

  public void update() {

  }

  public void repaint() {

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
}
