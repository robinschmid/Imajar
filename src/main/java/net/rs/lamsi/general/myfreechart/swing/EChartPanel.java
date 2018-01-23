/*
 * Copyright 2006-2015 The MZmine 2 Development Team
 * 
 * This file is part of MZmine 2.
 * 
 * MZmine 2 is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * MZmine 2 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with MZmine 2; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301
 * USA
 */

package net.rs.lamsi.general.myfreechart.swing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYTitleAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.data.RangeType;
import org.jfree.data.xy.XYDataset;

import net.rs.lamsi.general.dialogs.GraphicsExportDialog;
import net.rs.lamsi.general.myfreechart.gestures.ChartGestureHandler;
import net.rs.lamsi.general.myfreechart.gestures.ChartGestureMouseAdapter;
import net.rs.lamsi.general.myfreechart.gestures.interf.GestureHandlerFactory;
import net.rs.lamsi.general.myfreechart.listener.AxesRangeChangedListener;
import net.rs.lamsi.general.myfreechart.listener.AxisRangeChangedEvent;
import net.rs.lamsi.general.myfreechart.listener.AxisRangeChangedListener;
import net.rs.lamsi.general.myfreechart.listener.history.ZoomHistory;
import net.rs.lamsi.general.myfreechart.menus.MenuExportToClipboard;
import net.rs.lamsi.general.myfreechart.menus.MenuExportToExcel;
import net.rs.lamsi.general.myfreechart.plots.image2d.annot.ScaleInPlot;
import net.rs.lamsi.utils.ChartExportUtil;
import net.rs.lamsi.utils.mywriterreader.XSSFExcelWriterReader;

/**
 * Enhanced ChartPanel with extra chart gestures (drag mouse over entities (e.g., axis, titles)
 * ZoomHistory, GraphicsExportDialog, axesRangeListener included
 * 
 * @author Robin Schmid (robinschmid@uni-muenster.de)
 */
public class EChartPanel extends ChartPanel {
  private static final long serialVersionUID = 1L;

  protected ZoomHistory zoomHistory;
  protected List<AxesRangeChangedListener> axesRangeListener;
  protected boolean isMouseZoomable = true;
  protected ChartGestureMouseAdapter mouseAdapter;

  /**
   * Enhanced ChartPanel with extra scrolling methods, zoom history, graphics and data export<br>
   * stickyZeroForRangeAxis = false <br>
   * Graphics and data export menu are added
   * 
   * @param chart
   */
  public EChartPanel(JFreeChart chart) {
    this(chart, true, true, true, true, false);
  }

  /**
   * Enhanced ChartPanel with extra scrolling methods, zoom history, graphics and data export<br>
   * stickyZeroForRangeAxis = false <br>
   * Graphics and data export menu are added
   * 
   * @param chart
   */
  public EChartPanel(JFreeChart chart, boolean useBuffer) {
    this(chart, useBuffer, true, true, true, false);
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
  public EChartPanel(JFreeChart chart, boolean graphicsExportMenu, boolean dataExportMenu,
      boolean standardGestures) {
    this(chart, graphicsExportMenu, dataExportMenu, standardGestures, false);
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
  public EChartPanel(JFreeChart chart, boolean useBuffer, boolean graphicsExportMenu,
      boolean dataExportMenu, boolean standardGestures) {
    this(chart, useBuffer, graphicsExportMenu, dataExportMenu, standardGestures, false);
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
  public EChartPanel(JFreeChart chart, boolean useBuffer, boolean graphicsExportMenu,
      boolean dataExportMenu, boolean standardGestures, boolean stickyZeroForRangeAxis) {
    super(chart, useBuffer);
    // super(chart, true, false, true, true, true);
    // setDoubleBuffered(useBuffer);
    // setRefreshBuffer(useBuffer);
    initChartPanel(stickyZeroForRangeAxis);
    // Add Export to Excel and graphics export menu
    if (graphicsExportMenu || dataExportMenu)
      addExportMenu(graphicsExportMenu, dataExportMenu);

    // add gestures
    if (standardGestures)
      addStandardGestures();
    
    // images only
	// try to find in plot scale
    // set this chart panel
	List list = chart.getXYPlot().getAnnotations();
	for(int i=0; i<list.size(); i++) {
		XYAnnotation ann = (XYAnnotation) list.get(i); 
		if(XYTitleAnnotation.class.isInstance(ann)) {
			XYTitleAnnotation annt = (XYTitleAnnotation)ann;
			if(ScaleInPlot.class.isInstance(annt.getTitle())) {
				((ScaleInPlot)annt.getTitle()).setChartPanel(this);
				break;
			}
		}
	}
  }

  /**
   * Adds all standard gestures defined in {@link ChartGestureHandler#getStandardGestures()}
   */
  public void addStandardGestures() {
    // add ChartGestureHandlers
    ChartGestureMouseAdapter m = getGestureAdapter();
    if (m != null) {
      for (GestureHandlerFactory f : ChartGestureHandler.getStandardGestures())
        m.addGestureHandler(f.createHandler());
    }
  }

  /**
   * Init ChartPanel Mouse Listener For MouseDraggedOverAxis event For scrolling X-Axis und zooming
   * Y-Axis0
   */
  private void initChartPanel(boolean stickyZeroForRangeAxis) {
    final EChartPanel chartPanel = this;

    // set sticky zero
    if (stickyZeroForRangeAxis) {
      ValueAxis rangeAxis = chartPanel.getChart().getXYPlot().getRangeAxis();
      if (rangeAxis instanceof NumberAxis) {
        NumberAxis axis = (NumberAxis) rangeAxis;
        axis.setAutoRangeIncludesZero(true);
        axis.setAutoRange(true);
        axis.setAutoRangeStickyZero(true);
        axis.setRangeType(RangeType.POSITIVE);
      }
    }

    // zoom history
    zoomHistory = new ZoomHistory(this, 20);

    // axis range changed listener for zooming and more
    ValueAxis rangeAxis = this.getChart().getXYPlot().getRangeAxis();
    ValueAxis domainAxis = this.getChart().getXYPlot().getDomainAxis();
    if (rangeAxis != null) {
      rangeAxis.addChangeListener(new AxisRangeChangedListener(this, e -> axesRangeChanged(e)));
    }
    if (domainAxis != null) {
      domainAxis.addChangeListener(new AxisRangeChangedListener(this, e -> axesRangeChanged(e)));
    }

    // mouse adapter for scrolling and zooming
    mouseAdapter = new ChartGestureMouseAdapter();
    // mouseAdapter.addDebugHandler();
    this.addMouseListener(mouseAdapter);
    this.addMouseMotionListener(mouseAdapter);
    this.addMouseWheelListener(mouseAdapter);
  }
  
  /**
   * notify listeners of changed range
   * @param e
   */
  private void axesRangeChanged(AxisRangeChangedEvent e) {
      if (axesRangeListener != null)
        for (AxesRangeChangedListener l : axesRangeListener)
          l.axesRangeChanged(e);
  }

  @Override
  public void setMouseZoomable(boolean flag) {
    super.setMouseZoomable(flag);
    isMouseZoomable = flag;
  }

  /**
   * Default tries to extract all series from an XYDataset <br>
   * series 1 | Series 2 <br>
   * x y x y
   * 
   * @return Data array[columns][rows]
   */
  public Object[][] getDataArrayForExport() {
    if (getChart().getXYPlot() != null && getChart().getXYPlot().getDataset() != null) {
      try {
        XYDataset data = getChart().getXYPlot().getDataset();

        int series = data.getSeriesCount();
        Object[][] model = new Object[series * 2][];
        for (int s = 0; s < series; s++) {
          int size = 1 + data.getItemCount(s);
          Object[] x = new Object[size];
          Object[] y = new Object[size];
          // create new Array model[row][col]
          // Write header
          x[0] = getChart().getXYPlot().getDomainAxis().getLabel();
          y[0] = getChart().getXYPlot().getRangeAxis().getLabel();
          // write data
          for (int i = 0; i < data.getItemCount(s); i++) {
            x[i + 1] = data.getX(s, i);
            y[i + 1] = data.getY(s, i);
          }
          model[s * 2] = x;
          model[s * 2 + 1] = y;
        }
        return model;
      } catch (Exception ex) {
        return null;
      }
    }
    return null;
  }

  /*
   * ############################################################### Export Graphics
   */
  /**
   * Adds the GraphicsExportDialog menu and the data export menu
   */
  protected void addExportMenu(boolean graphics, boolean data) {
    this.getPopupMenu().addSeparator();
    if (graphics) {
      // Graphics Export
      ChartExportUtil.addExportDialogToMenu(this, e -> openGraphicsExportDialog());
    }
    if (data) {
      // General data export
      JMenu export = new JMenu("Export data ...");
      // Excel XY
      MenuExportToExcel exportXY =
          new MenuExportToExcel(new XSSFExcelWriterReader(), "to Excel", this);
      export.add(exportXY);
      // clip board
      MenuExportToClipboard exportXYClipboard = new MenuExportToClipboard("to Clipboard", this);
      export.add(exportXYClipboard);
      // add to panel
      addPopupMenu(export);
    }
  }

  protected void openGraphicsExportDialog() {
	  GraphicsExportDialog.openDialog(this.getChart());
  }

public void addPopupMenuItem(JMenuItem item) {
    this.getPopupMenu().add(item);
  }

  public void addPopupMenu(JMenu menu) {
    this.getPopupMenu().add(menu);
  }
  
  /**
   * Opens a file chooser and gives the user an opportunity to save the chart
   * in PNG format.
   *
   * @throws IOException if there is an I/O error.
   */
  public void doSaveAs() throws IOException {
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setCurrentDirectory(this.getDefaultDirectoryForSaveAs());
      FileNameExtensionFilter filter = new FileNameExtensionFilter(
                  localizationResources.getString("PNG_Image_Files"), "png");
      fileChooser.addChoosableFileFilter(filter);
      fileChooser.setFileFilter(filter);

      int option = fileChooser.showSaveDialog(this);
      if (option == JFileChooser.APPROVE_OPTION) {
          String filename = fileChooser.getSelectedFile().getPath();
          if (isEnforceFileExtensions()) {
              if (!filename.endsWith(".png")) {
                  filename = filename + ".png";
              }
          }
          ChartUtils.saveChartAsPNG(new File(filename), getChart(),
                  getWidth(), getHeight(), getChartRenderingInfo());
      }
  }

  public void addAxesRangeChangedListener(AxesRangeChangedListener l) {
    if (axesRangeListener == null)
      axesRangeListener = new ArrayList<AxesRangeChangedListener>(1);
    axesRangeListener.add(l);
  }

  public void removeAxesRangeChangedListener(AxesRangeChangedListener l) {
    if (axesRangeListener != null)
      axesRangeListener.remove(l);
  }

  public void clearAxesRangeChangedListeners() {
    if (axesRangeListener != null)
      axesRangeListener.clear();
  }

  public boolean isMouseZoomable() {
    return isMouseZoomable;
  }

  public ZoomHistory getZoomHistory() {
    return zoomHistory;
  }

  /**
   * Returns the {@link ChartGestureMouseAdapter} alternatively for other ChartPanel classes use:
   * 
   * <pre>
   * for(MouseListener l : getMouseListeners())
   * 	if(ChartGestureMouseAdapter.class.isInstance(l)){
   * 		ChartGestureMouseAdapter m = (ChartGestureMouseAdapter) l;
   * </pre>
   * 
   * @return
   */
  public ChartGestureMouseAdapter getGestureAdapter() {
    return mouseAdapter;
  }

  public void setGestureAdapter(ChartGestureMouseAdapter mouseAdapter) {
    this.mouseAdapter = mouseAdapter;
  }
}
