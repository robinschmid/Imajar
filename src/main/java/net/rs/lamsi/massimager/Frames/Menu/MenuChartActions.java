package net.rs.lamsi.massimager.Frames.Menu;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import net.rs.lamsi.massimager.Frames.Dialogs.ZoomXYAreaDialog;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.event.AxisChangeListener;

public abstract class MenuChartActions extends JPanel {
	private ChartPanel chartPanel;
	private ZoomXYAreaDialog zoomDialog;
	private JToggleButton btnAutoAdjust;
	/**
	 * Create the panel.
	 */
	public MenuChartActions() {
		setMaximumSize(new Dimension(32767, 25));
		setLayout(new BorderLayout(0, 0));
		
		JToolBar toolBar = new JToolBar();
		toolBar.setRollover(true);
		toolBar.setFloatable(false);
		add(toolBar);
		
		btnAutoAdjust = new JToggleButton("");
		btnAutoAdjust.setIcon(new ImageIcon(MenuChartActions.class.getResource("/img/btn_action_auto_adjust-01.png")));
		btnAutoAdjust.setSelectedIcon(new ImageIcon(MenuChartActions.class.getResource("/img/btn_action_auto_adjust_sel-01.png")));
		btnAutoAdjust.setSelected(true);
		btnAutoAdjust.setToolTipText("Auto adjust Y-axis");
		btnAutoAdjust.setMinimumSize(new Dimension(25, 25));
		btnAutoAdjust.setMaximumSize(new Dimension(25, 25));
		btnAutoAdjust.setMargin(new Insets(0, 0, 0, 0));
		btnAutoAdjust.setBounds(new Rectangle(0, 0, 25, 25));
		toolBar.add(btnAutoAdjust);
		
		JButton btnZoomToXWindow = new JButton("");
		btnZoomToXWindow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(zoomDialog==null) zoomDialog = new ZoomXYAreaDialog();
				zoomDialog.setVisible(true);
			}
		});
		btnZoomToXWindow.setIcon(new ImageIcon(MenuChartActions.class.getResource("/img/btn_action_zoom_x.png")));
		btnZoomToXWindow.setSelectedIcon(new ImageIcon(MenuChartActions.class.getResource("/img/btn_action_zoom_x_sel.png")));
		btnZoomToXWindow.setToolTipText("Zoom to X-axis-window");
		btnZoomToXWindow.setMinimumSize(new Dimension(25, 25));
		btnZoomToXWindow.setMaximumSize(new Dimension(25, 25));
		btnZoomToXWindow.setMargin(new Insets(0, 0, 0, 0));
		btnZoomToXWindow.setBounds(new Rectangle(0, 0, 25, 25));
		toolBar.add(btnZoomToXWindow);
		
		JButton btnSelectMZorRT = new JButton("");
		btnSelectMZorRT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectMZorRT();
			}
		});
		btnSelectMZorRT.setIcon(new ImageIcon(MenuChartActions.class.getResource("/img/btn_action_select_mz_rt.png")));
		btnSelectMZorRT.setSelectedIcon(new ImageIcon(MenuChartActions.class.getResource("/img/btn_action_select_mz_rt_selected.png")));
		btnSelectMZorRT.setToolTipText("Select mz or retention time (rt) for this chart");
		btnSelectMZorRT.setMinimumSize(new Dimension(25, 25));
		btnSelectMZorRT.setMaximumSize(new Dimension(25, 25));
		btnSelectMZorRT.setMargin(new Insets(0, 0, 0, 0));
		btnSelectMZorRT.setBounds(new Rectangle(0, 0, 25, 25));
		toolBar.add(btnSelectMZorRT);
		
	}
	
	// select MZ or RT 
	public abstract void selectMZorRT();	
	
	
	
	public ChartPanel getChartPanel() {
		return chartPanel;
	}
	public void setChartPanel(ChartPanel chartPanel2) {
		this.chartPanel = chartPanel2; 
		NumberAxis axis = (NumberAxis) chartPanel.getChart().getXYPlot().getRangeAxis();
		axis.setAutoRangeIncludesZero(true);
		// Zoom dialog
		if(zoomDialog==null) zoomDialog = new ZoomXYAreaDialog();
		this.zoomDialog.setPlotChartPanel(chartPanel);
		// add zoom change listener
		chartPanel.getChart().getXYPlot().getDomainAxis().addChangeListener(new AxisChangeListener() {
			
			@Override
			public void axisChanged(AxisChangeEvent e) {  
				if(getBtnAutoAdjust().isSelected()) {
					chartPanel.restoreAutoRangeBounds();  
					
				}
			}
		}); 
	} 

	public JToggleButton getBtnAutoAdjust() {
		return btnAutoAdjust;
	}
}
