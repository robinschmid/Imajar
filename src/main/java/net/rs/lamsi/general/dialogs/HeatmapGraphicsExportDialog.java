package net.rs.lamsi.general.dialogs;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ui.Size2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.rs.lamsi.general.datamodel.image.ImageGroupMD;
import net.rs.lamsi.general.datamodel.image.ImagingProject;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.heatmap.Heatmap;
import net.rs.lamsi.general.heatmap.HeatmapFactory;
import net.rs.lamsi.general.myfreechart.swing.EChartPanel;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.general.settings.SettingsHolder;
import net.rs.lamsi.general.settings.importexport.SettingsExportGraphics;
import net.rs.lamsi.general.settings.importexport.SettingsExportGraphics.FIXED_SIZE;
import net.rs.lamsi.general.settings.importexport.SettingsExportGraphics.FORMAT;
import net.rs.lamsi.general.settings.importexport.SettingsImageResolution.DIM_UNIT;
import net.rs.lamsi.massimager.Frames.Dialogs.generalsettings.interfaces.SettingsPanel;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.utils.DialogLoggerUtil;
import net.rs.lamsi.utils.FileAndPathUtil;

public class HeatmapGraphicsExportDialog extends GraphicsExportDialog implements SettingsPanel {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  public enum EXPORT_STRUCTURE {
    IMAGE("Single image"), GROUP("Group"), PROJECT("Project"), ALL("All images");

    private String text;

    EXPORT_STRUCTURE(String text) {
      this.text = text;
    }

    @Override
    public String toString() {
      return text;
    }
  }

  // only one instance!
  private static HeatmapGraphicsExportDialog inst;

  // ###################################################################
  // Vars
  private Heatmap heat;
  private Collectable2D selected;

  private boolean canExport;

  private JCheckBox cbShowAnnotations;
  private JComboBox comboExportStruc;

  // ###################################################################
  // create instance in window and imageeditor
  public static HeatmapGraphicsExportDialog createInstance() {
    if (inst == null) {
      inst = new HeatmapGraphicsExportDialog();
    }
    return inst;
  }

  public static HeatmapGraphicsExportDialog getInst() {
    return inst;
  }

  // ###################################################################
  // get Settings
  /**
   * OPen Dialog with chart
   * 
   * @param chart
   */
  public static void openDialog(JFreeChart chart) {
    createInstance().openDialogI(chart, null);
  }

  public static void openDialog(JFreeChart chart, Collectable2D selected) {
    createInstance().openDialogI(chart, selected);
  }

  protected void openDialogI(JFreeChart chart, Collectable2D selected) {
    createInstance().selected = selected;

    //
    try {
      if (selected != null) {
        heat = HeatmapFactory.generateHeatmap(selected);
        addChartToPanel(heat.getChartPanel(), true);
      } else
        addChartToPanel(new EChartPanel((JFreeChart) chart.clone()), true);
      setVisible(true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void renewPreview() {
    // annotations
    if (heat != null)
      heat.showSelectedExcludedRects(getCbShowAnnotations().isSelected());
    super.renewPreview();
  }

  @Override
  protected void saveGraphicsAs() {
    setAllSettings(SettingsHolder.getSettings());
    //
    if (canExport) {
      final SettingsExportGraphics sett =
          (SettingsExportGraphics) getSettings(SettingsHolder.getSettings());
      final EXPORT_STRUCTURE struc = (EXPORT_STRUCTURE) getComboExportStruc().getSelectedItem();
      try {
        if (selected == null || struc.equals(EXPORT_STRUCTURE.IMAGE)) {
          logger.debug("Writing image to file: {}", sett.getFullFilePath());

          applyMaxPathLengthToSett(sett);
          // renew size
          renewPreview();
          // export size
          final SettingsExportGraphics fsett = (SettingsExportGraphics) sett.copy();
          new GraphicsExportThread(chartPanel.getChart(), fsett).start();

        } else {
          try {
            switch (struc) {
              case ALL:
                File rootPath = (sett.getPath());
                List<Collectable2D> list = ImageEditorWindow.getImages();
                if (list != null)
                  for (Collectable2D c : list) {
                    if (c.getImageGroup() != null) {
                      if (c.getImageGroup().getProject() != null) {
                        // add project and group folder
                        sett.setPath(new File(
                            new File(rootPath,
                                c.getImageGroup().getProject().getName().replace(".", "_")),
                            c.getImageGroup().getName().replace(".", "_")));
                      }
                      // add group folder
                      else
                        sett.setPath(
                            new File(rootPath, c.getImageGroup().getName().replace(".", "_")));
                    }
                    // show, renew, calc size and save
                    saveCollectable2DGraphics(sett, c, c.getTitle());
                  }
                break;
              case PROJECT:
                if (selected != null && selected.getImageGroup() != null
                    && selected.getImageGroup().getProject() != null) {
                  ImagingProject project = selected.getImageGroup().getProject();
                  File mainPath = new File(sett.getPath(), project.getName().replace(".", "_"));
                  for (ImageGroupMD g : project.getGroups()) {
                    sett.setPath(new File(mainPath, g.getName().replace(".", "_")));
                    for (Collectable2D c : g.getImages()) {
                      // show, renew, calc size and save
                      saveCollectable2DGraphics(sett, c, c.getTitle());
                    }
                  }
                }
                break;
              case GROUP:
                if (selected != null && selected.getImageGroup() != null) {
                  sett.setPath(new File(sett.getPath(),
                      selected.getImageGroup().getName().replace(".", "_")));
                  for (Collectable2D c : selected.getImageGroup().getImages()) {
                    // show, renew, calc size and save
                    saveCollectable2DGraphics(sett, c, c.getTitle());
                  }
                }
                break;
            }
            //
            logger.info("File written successfully");
            DialogLoggerUtil.showMessageDialogForTime(inst, "Information",
                "File written successfully ", 1000);
          } catch (Exception e) {
            logger.error("File not written.", e);
            DialogLoggerUtil.showErrorDialog(inst, "File not written. ", e);
          }
        }
      } catch (Exception e) {
        logger.error("File not written.", e);
        DialogLoggerUtil.showErrorDialog(this, "File not written. ", e);
      }
    }
  }

  /**
   * change chartpanel, calc size, repaint, export
   * 
   * @param sett
   * @param img
   * @param title
   */
  private void saveCollectable2DGraphics(SettingsExportGraphics sett, Collectable2D img,
      String title) {
    // change file name
    File path = sett.getPath();
    String fileName = sett.getFileName();
    // import all
    try {
      // create chart
      heat = HeatmapFactory.generateHeatmap(img);
      // TODO maybe you have to put it on the chartpanel and show it?
      addChartToPanel(heat.getChartPanel(), false);
      // set the name and path
      // replace
      title = FileAndPathUtil.replaceInvalidChar(title);
      // title as filename
      sett.setFileName(fileName + title);
      // export
      logger.debug("Writing image to file: {}", sett.getFullFilePath());

      renewPreview();
      // title as filename
      sett.setFileName(fileName + title);
      applyMaxPathLengthToSett(sett);
      // export size
      final SettingsExportGraphics fsett = (SettingsExportGraphics) sett.copy();
      new GraphicsExportThread(chartPanel.getChart(), fsett).start();
    } catch (Exception ex) {
      logger.error("FIle: {} is not saveable", sett.getFileName(), ex);
    }
    // reset
    sett.setFileName(fileName);
  }

  @Override
  public void setAllSettings(SettingsHolder settings) {
    SettingsExportGraphics sett = (SettingsExportGraphics) getSettings(settings);
    // all set right?
    canExport = false;
    //
    try {
      sett.setShowAnnotations(getCbShowAnnotations().isSelected());
      // FilePath and name
      String name = getTxtFileName().getText();
      sett.setFileName(name);
      File f = new File(getTxtPath().getText());
      f = FileAndPathUtil.applyMaxLength(f);
      sett.setPath(f);
      // Format
      FORMAT format = SettingsExportGraphics.FORMAT.PDF;
      if (getRbSVG().isSelected())
        format = SettingsExportGraphics.FORMAT.SVG;
      else if (getRbEPS().isSelected())
        format = SettingsExportGraphics.FORMAT.EPS;
      else if (getRbPNG().isSelected())
        format = SettingsExportGraphics.FORMAT.PNG;
      else if (getRbJPG().isSelected())
        format = SettingsExportGraphics.FORMAT.JPG;
      else if (getRbEmf().isSelected())
        format = SettingsExportGraphics.FORMAT.EMF;
      sett.setFormat(format);

      // Resolution
      if (getRbForPrintRes().isSelected())
        sett.setResolution(300);
      else if (getRbForPresentationRes().isSelected())
        sett.setResolution(72);
      else
        sett.setResolution(Integer.valueOf(getTxtManualRes().getText()));

      // fixed size for chart or plot
      sett.setFixedSize(getComboWidthPlotChart().getSelectedItem().equals("Plot") ? FIXED_SIZE.PLOT
          : FIXED_SIZE.CHART);

      // Size
      float width = Float.valueOf(getTxtWidth().getText());
      float height = Float.valueOf(getTxtHeight().getText());
      DIM_UNIT unit = (DIM_UNIT) getComboSizeUnit().getSelectedItem();
      sett.setSize(width, height, unit);
      sett.setUseOnlyWidth(getCbOnlyUseWidth().isSelected());

      // Background
      if (getRbTransparent().isSelected())
        sett.setColorBackground(new Color(255, 255, 255, 0));
      else if (getRbBlack().isSelected())
        sett.setColorBackground(new Color(0, 0, 0, 255));
      else if (getRbWhite().isSelected())
        sett.setColorBackground(Color.WHITE);
      else
        sett.setColorBackground(getBtnChooseBackgroundColor().getBackground());

      // is everything set right?
      canExport =
          (sett.getPath() != null && sett.getFileName().length() > 0 && width > 0 && height > 0);
    } catch (Exception ex) {
      canExport = false;
    }
  }

  @Override
  public void setAllSettingsOnPanel(SettingsHolder settings) {
    SettingsExportGraphics sett = (SettingsExportGraphics) getSettings(settings);
    // set to panel TODO
    getCbShowAnnotations().setSelected(sett.isShowAnnotations());
    getTxtFileName().setText(sett.getFileName());
    getTxtPath().setText(sett.getPath().getAbsolutePath());
    getTxtManualRes().setText("" + sett.getResolution());
    getComboSizeUnit().setSelectedItem(sett.getUnit());
    DecimalFormat form = new DecimalFormat("#.###");
    Size2D size = sett.getSizeInUnit();
    getTxtWidth().setText("" + form.format(size.getWidth()));
    getTxtHeight().setText("" + form.format(size.getHeight()));

    getComboWidthPlotChart()
        .setSelectedItem(sett.getFixedSize().equals(FIXED_SIZE.PLOT) ? "Plot" : "Chart");

    // not everything set ! TODO cb rb combo
    getCbOnlyUseWidth().setSelected(sett.isUseOnlyWidth());
  }

  @Override
  public Settings getSettings(SettingsHolder settings) {
    return settings.getSetGraphicsExport();
  }
  //
  // ###################################################################

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    try {
      HeatmapGraphicsExportDialog dialog = new HeatmapGraphicsExportDialog();
      dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
      dialog.setVisible(true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Create the dialog.
   */
  public HeatmapGraphicsExportDialog() {
    super();
    final JFrame thisframe = this;
    //
    //
    cbShowAnnotations = new JCheckBox("Show annotations (ROIs, ...)");
    contentPanel.add(cbShowAnnotations, "cell 0 3");
    cbShowAnnotations.setSelected(true);

    comboExportStruc = new JComboBox();
    comboExportStruc.setFont(new Font("Tahoma", Font.BOLD, 12));
    comboExportStruc.setModel(new DefaultComboBoxModel(EXPORT_STRUCTURE.values()));
    contentPanel.add(comboExportStruc, "cell 0 2");
  }

  public JComboBox getComboExportStruc() {
    return comboExportStruc;
  }

  public JCheckBox getCbShowAnnotations() {
    return cbShowAnnotations;
  }
}
