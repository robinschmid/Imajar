package net.rs.lamsi.general.myfreechart.plots.image2d;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jfree.chart.JFreeChart;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.dialogs.HeatmapGraphicsExportDialog;
import net.rs.lamsi.general.myfreechart.ChartLogics;
import net.rs.lamsi.general.myfreechart.gestures.ChartGesture.Button;
import net.rs.lamsi.general.myfreechart.gestures.ChartGesture.Entity;
import net.rs.lamsi.general.myfreechart.gestures.ChartGesture.Event;
import net.rs.lamsi.general.myfreechart.gestures.ChartGesture.Key;
import net.rs.lamsi.general.myfreechart.gestures.ChartGestureDragDiffHandler.Orientation;
import net.rs.lamsi.general.myfreechart.gestures.ChartGestureHandler;
import net.rs.lamsi.general.myfreechart.gestures.ChartGestureHandler.DragHandler;
import net.rs.lamsi.general.myfreechart.gestures.ChartGestureHandler.Handler;
import net.rs.lamsi.general.myfreechart.gestures.ChartGestureMouseAdapter;
import net.rs.lamsi.general.myfreechart.gestures.interf.GestureHandlerFactory;
import net.rs.lamsi.general.myfreechart.swing.EChartPanel;
import net.rs.lamsi.general.settings.importexport.SettingsImage2DDataExport;
import net.rs.lamsi.general.settings.importexport.SettingsImageDataImportTxt.ModeData;
import net.rs.lamsi.multiimager.Frames.dialogs.DialogDataSaver;
import net.rs.lamsi.multiimager.utils.imageimportexport.DataExportUtil;
import net.rs.lamsi.utils.useful.DebugStopWatch;

public class EImage2DChartPanel extends EChartPanel {
  private Collectable2D img;

  public EImage2DChartPanel(JFreeChart chart, Collectable2D img) {
    this(chart, img, true, true, true);
  }

  /**
   * Enhanced ChartPanel with extra scrolling methods, zoom history, graphics and data export<br>
   * stickyZeroForRangeAxis = false
   * 
   * @param chart
   * @param graphicsExportMenu adds graphics export menu
   * @param standardGestures adds the standard ChartGestureHandlers
   * @param dataExportMenu adds data export menu
   */
  public EImage2DChartPanel(JFreeChart chart, Collectable2D img, boolean graphicsExportMenu,
      boolean dataExportMenu, boolean standardGestures) {
    this(chart, img, true, graphicsExportMenu, dataExportMenu, standardGestures);
  }

  /**
   * Enhanced ChartPanel with extra scrolling methods, zoom history, graphics and data export<br>
   * stickyZeroForRangeAxis = false
   * 
   * @param chart
   * @param graphicsExportMenu adds graphics export menu
   * @param standardGestures adds the standard ChartGestureHandlers
   * @param dataExportMenu adds data export menu
   */
  public EImage2DChartPanel(JFreeChart chart, Collectable2D img, boolean useBuffer,
      boolean graphicsExportMenu, boolean dataExportMenu, boolean standardGestures) {
    this(chart, img, useBuffer, graphicsExportMenu, dataExportMenu, standardGestures, false);
  }

  /**
   * Enhanced ChartPanel with extra scrolling methods, zoom history, graphics and data export
   * 
   * @param chart
   * @param graphicsExportMenu adds graphics export menu
   * @param dataExportMenu adds data export menu
   * @param standardGestures adds the standard ChartGestureHandlers
   * @param stickyZeroForRangeAxis
   */
  public EImage2DChartPanel(JFreeChart chart, Collectable2D img, boolean useBuffer,
      boolean graphicsExportMenu, boolean dataExportMenu, boolean standardGestures,
      boolean stickyZeroForRangeAxis) {
    super(chart, useBuffer, graphicsExportMenu, dataExportMenu, standardGestures,
        stickyZeroForRangeAxis);
    this.img = img;
    addExtraExportMenu(graphicsExportMenu, dataExportMenu);
    ChartLogics.makeChartResizable(this);
  }

  @Override
  public void paintComponent(Graphics g) {
    DebugStopWatch timer = new DebugStopWatch();
    super.paintComponent(g);
    timer.stopAndLOG("to paintComponent on " + img.getTitle());
  }


  @Override
  public void addStandardGestures() {
    // super.addStandardGestures();

    // add ChartGestureHandlers
    ChartGestureMouseAdapter g = getGestureAdapter();
    if (g != null) {
      for (GestureHandlerFactory f : ChartGestureHandler.initStandardGestures(true, true, false,
          true, true, true, true, true))
        g.addGestureHandler(f.createHandler());
    }

    if (g != null) {
      // XYItems cover the whole plot
      g.addGestureHandler(Handler.PREVIOUS_ZOOM_HISTORY, Entity.XY_ITEM,
          new Event[] {Event.DOUBLE_CLICK}, Button.BUTTON1, Key.NONE, null);
      g.addGestureHandler(Handler.NEXT_ZOOM_HISTORY, Entity.XY_ITEM,
          new Event[] {Event.DOUBLE_CLICK}, Button.BUTTON1, Key.CTRL, null);

      // drag zoomstandardGestures
      g.addDragGestureHandler(new DragHandler[] {DragHandler.SCROLL_AXIS}, new Key[] {Key.CTRL},
          Entity.XY_ITEM, Button.BUTTON1, Orientation.HORIZONTAL, null);
      g.addDragGestureHandler(new DragHandler[] {DragHandler.SCROLL_AXIS}, new Key[] {Key.CTRL},
          Entity.PLOT, Button.BUTTON1, Orientation.HORIZONTAL, null);
    }
  }

  @Override
  protected void openGraphicsExportDialog() {
    HeatmapGraphicsExportDialog.openDialog(getChart(), img);
  }

  protected void addExtraExportMenu(boolean graphics, boolean data) {
    final EImage2DChartPanel thispanel = this;
    if (data) {
      if (img != null && img.isImage2D()) {
        this.getPopupMenu().addSeparator();
        // Data Export
        JMenuItem exportData = new JMenuItem("Export data to..");
        exportData.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // open export Graphics dialog
            if (img.isImage2D())
              DialogDataSaver.startDialogWith((Image2D) img);
          }
        });
        // add to panel
        addPopupMenuItem(exportData);

        // TODO ADD MORE OPTIONS FOR CLIPBOARD BY DIFFERENT MENUBUTTONS
        this.getPopupMenu().addSeparator();

        // Data Export
        JMenu exportDataMenu = new JMenu("Export data to clipboard..");
        addPopupMenu(exportDataMenu);
        // Data Export
        JMenuItem exportDataCB = new JMenuItem("..raw data");
        exportDataCB.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            //
            SettingsImage2DDataExport sett = new SettingsImage2DDataExport();
            sett.setUpForDataOnly(true, true);
            try {
              if (img.isImage2D())
                DataExportUtil.exportDataImage2D((Image2D) img, sett);
            } catch (InvalidFormatException | IOException e1) {
              // TODO Auto-generated catch block
              e1.printStackTrace();
            }
          }
        });
        // add to panel
        exportDataMenu.add(exportDataCB);
        //
        JMenuItem exportDataCB2 = new JMenuItem("..processed data");
        exportDataCB2.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            //
            SettingsImage2DDataExport sett = new SettingsImage2DDataExport();
            sett.setUpForDataOnly(true, false);
            try {
              if (img.isImage2D())
                DataExportUtil.exportDataImage2D((Image2D) img, sett);
            } catch (InvalidFormatException | IOException e1) {
              // TODO Auto-generated catch block
              e1.printStackTrace();
            }
          }
        });
        // add to panel
        exportDataMenu.add(exportDataCB2);
        //
        exportDataCB2 = new JMenuItem("..raw data XYZ");
        exportDataCB2.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            //
            SettingsImage2DDataExport sett = new SettingsImage2DDataExport();
            sett.setUpForDataOnly(true, true);
            sett.setMode(ModeData.XYZ);
            try {
              if (img.isImage2D())
                DataExportUtil.exportDataImage2D((Image2D) img, sett);
            } catch (InvalidFormatException | IOException e1) {
              // TODO Auto-generated catch block
              e1.printStackTrace();
            }
          }
        });
        // add to panel
        exportDataMenu.add(exportDataCB2);
        //
        exportDataCB2 = new JMenuItem("..processed data XYZ");
        exportDataCB2.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            //
            SettingsImage2DDataExport sett = new SettingsImage2DDataExport();
            sett.setUpForDataOnly(true, false);
            sett.setMode(ModeData.XYZ);
            try {
              if (img.isImage2D())
                DataExportUtil.exportDataImage2D((Image2D) img, sett);
            } catch (InvalidFormatException | IOException e1) {
              // TODO Auto-generated catch block
              e1.printStackTrace();
            }
          }
        });
        // add to panel
        exportDataMenu.add(exportDataCB2);
      }
    }
  }
}
