package net.rs.lamsi.massimager.MyFreeChart.Plot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import net.rs.lamsi.massimager.Frames.Dialogs.GraphicsExportDialog;
import net.rs.lamsi.massimager.Image.Image2D;
import net.rs.lamsi.massimager.MyFreeChart.ChartLogics;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.Range;

public class PlotChartPanel extends ChartPanel {
	
	
	protected boolean isMouseZoomable = true;
 
	public PlotChartPanel(JFreeChart chart) {
		super(chart,true,false,true,true,true);  
		initChartPanel();  
        // Add Export to Excel Menu
        addExportMenu();
	} 
	
	/**
	 * Init ChartPanel Mouse Listener
	 * For scrolling X-Axis und zooming Y-Axis
	 */
	private void initChartPanel() {
		final PlotChartPanel chartPanel = this;
		/*
		// set sticky zero
		ValueAxis rangeAxis = chartPanel.getChart().getXYPlot().getRangeAxis();
		if (rangeAxis instanceof NumberAxis) {
			NumberAxis axis = (NumberAxis) rangeAxis;
			axis.setAutoRangeIncludesZero(true);
			axis.setAutoRange(true);
			axis.setAutoRangeStickyZero(true);
			axis.setRangeType(RangeType.POSITIVE);
		}
		*/
		
		// mouse adapter for scrolling and zooming
		MouseAdapter mouseAdapter = new MouseAdapter() { 
			boolean wasMouseZoomable = false;
			boolean scrollsXAxis = false, scrollsYAxis = false;
			Point2D pressed = null;
			Point2D last = null;
			@Override
			public void mouseReleased(MouseEvent e) {   
				if(e.getButton()==MouseEvent.BUTTON1 && pressed!=null) {
					last = null;
					pressed = null;
					scrollsXAxis = false;
					scrollsYAxis = false;
					chartPanel.setMouseZoomable(wasMouseZoomable); 
				}
			} 
			@Override
			public void mousePressed(MouseEvent e) {   
				if(e.getButton()==MouseEvent.BUTTON1) { 
					scrollsXAxis = false;
					pressed = null; 
	
					Point2D pos = ChartLogics.mouseXYToPlotXY(chartPanel, e.getX(), e.getY());
					// nur speichern wenn innerhalb des charts
					Range yrange = chartPanel.getChart().getXYPlot().getRangeAxis().getRange();
					Range xrange = chartPanel.getChart().getXYPlot().getDomainAxis().getRange();
					 
					if(pos.getY()<yrange.getLowerBound()) { 
						// mouse scrolling xaxis
						pressed = pos;
						last = pressed;
						scrollsXAxis = true;
						wasMouseZoomable = isMouseZoomable;
						chartPanel.setMouseZoomable(false); 
					}
					else if(pos.getX()<xrange.getLowerBound()) {  
						// mouse scrolling yaxis
						pressed = pos;
						last = pressed; 
						scrollsYAxis = true;
						wasMouseZoomable = isMouseZoomable;
						chartPanel.setMouseZoomable(false); 
					}
				}
			} 
			@Override
			public void mouseDragged(MouseEvent e) { 
					// get Plot Values  
					Point2D released = ChartLogics.mouseXYToPlotXY(chartPanel, e.getX(), e.getY());
	
					// nur wenn innerhalb der range
					Range yrange = chartPanel.getChart().getXYPlot().getRangeAxis().getRange();
					Range xrange = chartPanel.getChart().getXYPlot().getDomainAxis().getRange(); 
					if(pressed!=null) { 
						if(released.getY()<yrange.getLowerBound() && scrollsXAxis) { 
							// scroll x axis if mouse pressed and moved on axis
							double xoffset = -(released.getX()-last.getX());
							
							ChartLogics.offsetDomainAxisAbsolute(chartPanel, xoffset, true);
							last = ChartLogics.mouseXYToPlotXY(chartPanel, e.getX(), e.getY());
						}
						//
						if(released.getX()<xrange.getLowerBound() && scrollsYAxis) {  
							// zoom in yaxis on dragged over yaxis
							double yzoom = -(released.getY()-last.getY())/yrange.getLength()*4;
							
							ChartLogics.zoomRangeAxis(chartPanel, yzoom, true);
							last = ChartLogics.mouseXYToPlotXY(chartPanel, e.getX(), e.getY());
						}
					} 
			}
		};
		this.addMouseListener(mouseAdapter);
		this.addMouseMotionListener(mouseAdapter); 
	}
	
	
	@Override
	public void setMouseZoomable(boolean flag) { 
		super.setMouseZoomable(flag);
		isMouseZoomable = flag;
	}
	
	
	/*###############################################################
	 * Export Graphics
	 */ 
	protected void addExportMenu() {
		this.getPopupMenu().addSeparator();
		// Graphics Export 
		JMenuItem exportGraphics = new JMenuItem("Export Graphics"); 
		exportGraphics.addActionListener(new ActionListener() { 
			@Override
			public void actionPerformed(ActionEvent e) { 
				Vector<Image2D> list = ImageEditorWindow.getImages();
				// open export Graphics dialog 
				GraphicsExportDialog.openDialog(getChart(), list); 
			}
		}); 
		
		// add to panel
		addPopupMenuItem(exportGraphics);
	}
	
	

	public void addPopupMenuItem(JMenuItem item) { 
        this.getPopupMenu().add(item); 
	}
	public void addPopupMenu(JMenu menu) {
		this.getPopupMenu().add(menu);
	}

}
