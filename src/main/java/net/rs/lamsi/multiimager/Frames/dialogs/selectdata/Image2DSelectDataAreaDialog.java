package net.rs.lamsi.multiimager.Frames.dialogs.selectdata;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.ImageGroupMD;
import net.rs.lamsi.general.datamodel.image.TestImageFactory;
import net.rs.lamsi.general.heatmap.Heatmap;
import net.rs.lamsi.general.heatmap.HeatmapFactory;
import net.rs.lamsi.general.myfreechart.ChartLogics;
import net.rs.lamsi.general.settings.image.selection.SettingsElipseSelection;
import net.rs.lamsi.general.settings.image.selection.SettingsPolygonSelection;
import net.rs.lamsi.general.settings.image.selection.SettingsRectSelection;
import net.rs.lamsi.general.settings.image.selection.SettingsSelections;
import net.rs.lamsi.general.settings.image.selection.SettingsShapeSelection;
import net.rs.lamsi.general.settings.image.selection.SettingsShapeSelection.SHAPE;
import net.rs.lamsi.general.settings.image.selection.SettingsShapeSelection.SelectionMode;
import net.rs.lamsi.general.settings.image.visualisation.SettingsAlphaMap;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow.LOG;
import net.rs.lamsi.multiimager.Frames.dialogs.DialogDataSaver;
import net.rs.lamsi.multiimager.test.TestQuantifier;
import net.rs.lamsi.utils.DialogLoggerUtil;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYShapeAnnotation;
import org.jfree.data.Range;

import javax.swing.JCheckBox;
 

public class Image2DSelectDataAreaDialog extends JFrame implements MouseListener, MouseMotionListener {
	
	private enum KEY {
		SHRINK, SHIFT, ENLARGE
	}
	
	private SelectionMode mode = SelectionMode.SELECT;
	
	// mystuff
	private Heatmap heat;
	private Image2D img;
	// save the relation in hashmap
	private HashMap<SettingsShapeSelection, XYShapeAnnotation> map;
	//
	private SettingsSelections settSel;
	
	// last click or anything that was registered!
	private MouseEvent lastMouseEvent;
	
	// active selection (can be deleted, shifted etc)
	private SettingsShapeSelection currentSelect;
	private boolean isPressed = false;
	// coordinates of first and second mouse event (data space)
	private float x0,x1,y0,y1;
	// components
	private JPanel contentPane;
	private JPanel pnChartView;
	private JToggleButton btnChoose;
	private JTable table;
	private ShapeSelectionsTableModel tableModel;
	private JComboBox comboShape;
	private JComboBox comboSelectionMode;
	private JCheckBox cbPerformance;
	private JCheckBox cbMarkDp;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Image2DSelectDataAreaDialog frame = new Image2DSelectDataAreaDialog(); 
					TestQuantifier.rand = new Random(System.currentTimeMillis());
					ImageGroupMD img = TestImageFactory.createNonNormalImage(1);
					frame.startDialog((Image2D)img.get(0));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
	/**
	 * Create the frame.
	 */
	public Image2DSelectDataAreaDialog() {
		final JFrame thisframe = this;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 778, 767);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel pnNorthMenu = new JPanel();
		contentPane.add(pnNorthMenu, BorderLayout.NORTH);
		
		JButton btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteSelection(currentSelect);
				currentSelect = null;
			}
		});
		
		comboShape = new JComboBox();
		comboShape.setModel(new DefaultComboBoxModel(SHAPE.values()));
		comboShape.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				contentPane.requestFocusInWindow();
			}
		});
		
		cbPerformance = new JCheckBox("Performance");
		cbPerformance.setToolTipText("Calculates statistics at the end of the creation of a shape. (Saves performance)");
		pnNorthMenu.add(cbPerformance);
		
		cbMarkDp = new JCheckBox("Mark dp");
		cbMarkDp.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				showMarkingMap(cbMarkDp.isSelected());
			}
		});
		pnNorthMenu.add(cbMarkDp);
		pnNorthMenu.add(comboShape);
		
		comboSelectionMode = new JComboBox();
		comboSelectionMode.setModel(new DefaultComboBoxModel(SelectionMode.values()));
		comboSelectionMode.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				contentPane.requestFocusInWindow();
			}
		});
		pnNorthMenu.add(comboSelectionMode);
		

		JButton btnFinish = new JButton("Finish shape");
		btnFinish.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentSelect = null;
			}
		});
		pnNorthMenu.add(btnFinish);
		
		btnChoose = new JToggleButton("Choose");
		btnChoose.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				heat.getChartPanel().setMouseZoomable(((JToggleButton)e.getSource()).isSelected());
			}
		});
		pnNorthMenu.add(btnChoose);
		pnNorthMenu.add(btnDelete);
		
		JButton btnDeleteAll = new JButton("Delete All");
		btnDeleteAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tableModel.removeAllRows();
				currentSelect = null;
				settSel.removeAllSelections();
				heat.getPlot().clearAnnotations();
				getPnChartView().repaint();
			}
		}); 
		pnNorthMenu.add(btnDeleteAll);
		
		JButton btnExportData = new JButton("Export data");
		btnExportData.setToolTipText("Export all selected/not excluded data points as raw or processed data.");
		btnExportData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DialogDataSaver.startDialogWith(img, settSel);
			}
		});
		pnNorthMenu.add(btnExportData);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.9);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		contentPane.add(splitPane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		splitPane.setRightComponent(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		JPanel pnSouthMenu = new JPanel();
		panel.add(pnSouthMenu, BorderLayout.SOUTH);
		FlowLayout flowLayout = (FlowLayout) pnSouthMenu.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		
		JButton btnOk = new JButton("Ok");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// apply
				applySelections();
				dispatchEvent(new WindowEvent(thisframe, WindowEvent.WINDOW_CLOSING));
			}
		});
		pnSouthMenu.add(btnOk);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispatchEvent(new WindowEvent(thisframe, WindowEvent.WINDOW_CLOSING));
			}
		});
		pnSouthMenu.add(btnCancel);
		
		JScrollPane scrollPane = new JScrollPane();
		panel.add(scrollPane, BorderLayout.CENTER);
		
		table = new JTable();
		scrollPane.setViewportView(table);
		
		pnChartView = new JPanel();
		splitPane.setLeftComponent(pnChartView);
		pnChartView.setLayout(new BorderLayout(0, 0));
		//
		addKeys();
		// 
		WindowAdapter wl = new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) { 
				super.windowClosed(e); 
				// show selected excluded rects 
				applySelections();
			}
		};
		this.addWindowListener(wl);
		
		contentPane.requestFocusInWindow();
	}

	/**
	 * shows all selected data points marked with a map
	 * @param selected
	 */
	protected void showMarkingMap(boolean selected) {
		if(img!=null) {
			SettingsAlphaMap sett = (SettingsAlphaMap)img.getSettingsByClass(SettingsAlphaMap.class);

			sett.setActive(selected);
			
			if(selected) {
				// create map 
				settSel.createAlphaMap(sett);
				img.setSettings(sett);
				sett.setAlpha(0.25f);
				sett.applyToHeatMap(heat);
			}
		}
	}


	/**
	 * shift x by i screen pixel
	 * @param i
	 * @param key hold position and enlarge or shrink
	 */
	protected void shiftCurrentRectX(int i, KEY key) {
		if(currentSelect==null)
			return;
		// translate to data space
		ChartPanel cp = heat.getChartPanel();
		float val = (float)ChartLogics.screenValueToPlotValue(cp, i).getX();
		// shift 
		if(!((key.equals(KEY.ENLARGE) && i>0)||(key.equals(KEY.SHRINK) && i<0)))
			currentSelect.translate(val, 0);
		// enlarge?
		if(key.equals(KEY.ENLARGE)) currentSelect.grow(Math.abs(val), 0);
		if(key.equals(KEY.SHRINK)) currentSelect.grow(-Math.abs(val), 0);
		
		updateSelection();
	}
	protected void shiftCurrentRectY(int i, KEY key) {
		if(currentSelect==null)
			return;
		// translate to data space
		ChartPanel cp = heat.getChartPanel();
		float val = (float)ChartLogics.screenValueToPlotValue(cp, i).getY();
		// shift 
		if(!((key.equals(KEY.ENLARGE) && i>0)||(key.equals(KEY.SHRINK) && i<0)))
			currentSelect.translate(0 , val);
		// enlarge?
		if(key.equals(KEY.ENLARGE)) currentSelect.grow(0,Math.abs(val));
		if(key.equals(KEY.SHRINK)) currentSelect.grow(0,-Math.abs(val));

		updateSelection();
	}

	protected void deleteSelection(SettingsShapeSelection r) { 
		if(r!=null) {
			// remove annotation
			XYShapeAnnotation currentAnn = map.get(r);
			if(currentAnn!=null)
				heat.getPlot().removeAnnotation(currentAnn, false); 
			// remove from map
			map.remove(r);
			// remove from tableModel (and ArrayList)
			// and automatically update statistics if it was an exclusion
			tableModel.removeRow(r, true);
			// repaint
			JFreeChart chart = heat.getChart();
			chart.fireChartChanged();
		}
	}

	/**
	 * give all selections to img
	 * end of dialog
	 */
	protected void applySelections() {
		// TODO
		// directly changing selections or apply here after closing the frame
		// listeners?
	}
	
	public void startDialog(Image2D img) {
		if(img!=null) { 
			try {
				heat = HeatmapFactory.generateHeatmap(img);
				this.img = (Image2D) heat.getImage();
				// get all existing rects
				map = new HashMap<SettingsShapeSelection, XYShapeAnnotation>();
				
				settSel = img.getSettings().getSettSelections();
				
				// set table model
				tableModel = new ShapeSelectionsTableModel(settSel);
				table.setModel(tableModel);
				table.getColumnModel().getColumn(table.getColumnCount()-1).setCellRenderer(new TableHistoColumnRenderer());
				
				// add all
				if(settSel.getSelections()!=null)
					for(SettingsShapeSelection r : getSelections())
						updateAnnotation(r);
				
				// add to screen
				getPnChartView().add(heat.getChartPanel(), BorderLayout.CENTER);
				// add mouse 
				heat.getChartPanel().addMouseListener(this);
				heat.getChartPanel().addMouseMotionListener(this); 
				heat.getChartPanel().setMouseZoomable(false);
				// show
				setVisible(true); 
			} catch(Exception ex) {
				DialogLoggerUtil.showErrorDialog(this, "", ex);
				ImageEditorWindow.log(ex.getMessage(), LOG.ERROR);
				ex.printStackTrace();
			}
		}
	}
	
	private ArrayList<SettingsShapeSelection> getSelections() {
		return settSel.getSelections();
	}

	private void updateAllAnnotations() {
		if(getSelections()== null)
			return;
		
		for (Iterator iterator = getSelections().iterator(); iterator.hasNext();) {
			SettingsShapeSelection s = (SettingsShapeSelection) iterator.next();
			updateAnnotation(s);
		}
	}
	/**
	 * updates a annotation
	 * @param r
	 */
	private void updateAnnotation(SettingsShapeSelection r) { 
		// remove old
		XYShapeAnnotation currentAnn = map.get(r);
		if(currentAnn!=null)
			heat.getPlot().removeAnnotation(currentAnn, false); 
		
		// add new
		currentAnn = r.createXYShapeAnnotation();
		heat.getPlot().addAnnotation(currentAnn, false);
		map.put(r, currentAnn);
	}
	
	/**
	 * adds a selection to the list and an annotation to the plot
	 * @param r
	 */
	private void addNewSelection(SettingsShapeSelection r) {
		// put data in table
		tableModel.addRow(r, false);
		// update statistics
		updateSelection(r);
	}
	

	/**
	 * update statistics, add annotation and
	 * show all in chart
	 * call on size/position/data processing change
	 */
	protected void updateSelection() {
		updateSelection(currentSelect);
	}
	
	/**
	 * update statistics, add annotation and
	 * show all in chart
	 * call on size/position/data processing change
	 */
	protected void updateSelection(SettingsShapeSelection currentSelect) {
		// Update rects
		if(currentSelect!=null && currentSelect.getMode()==SelectionMode.EXCLUDE) {
			// update all rects
			settSel.updateStatistics();
		}
		else {
			// update this selection 
			settSel.updateStatistics(currentSelect);
			tableModel.updateRow(currentSelect);
		}
		// update annotation of current only 
		updateAnnotation(currentSelect);

		// update table
		tableModel.fireTableDataChanged();
		
		// update map
		if(getCbMarkDp().isSelected())
			showMarkingMap(true);
		
		// update chart
		JFreeChart chart = heat.getChart();
		chart.fireChartChanged();
		this.repaint();
		//
		ImageEditorWindow.log("UPDATE CHART", LOG.DEBUG);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		ImageEditorWindow.log("MOVED", LOG.DEBUG);
		// UPDATE THE CURRENT RECT
		if(isPressed) {
			if(!(getBtnChoose().isSelected())) {
				// add points to freehand
				if(getCurrentShape().equals(SHAPE.FREEHAND) && SettingsPolygonSelection.class.isInstance(currentSelect)) {
					ChartPanel cp = heat.getChartPanel();
					Point2D pos = ChartLogics.mouseXYToPlotXY(cp, e.getX(), e.getY()); 
					
					// only if travel distance was far enough
					if(lastMouseEvent==null || e.getPoint().distance(lastMouseEvent.getPoint())>5) {
						x1 = (float)pos.getX();
						y1 = (float)pos.getY();
						((SettingsPolygonSelection)currentSelect).addPoint(x1,y1);
						lastMouseEvent = e;
						updateSelection();
					}
				}
				else {
					// end other shapes
					ChartPanel cp = heat.getChartPanel();
					Point2D pos = ChartLogics.mouseXYToPlotXY(cp, e.getX(), e.getY()); 
					
					x1 = (float)pos.getX();
					y1 = (float)pos.getY();
					
					currentSelect.setFirstAndSecondMouseEvent(x0, y0, x1, y1);
					lastMouseEvent = e;
					// update selection stats and annotation
					updateSelection();
				}
			}
		}
	}
	@Override
	public void mouseMoved(MouseEvent e) {
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		ImageEditorWindow.log("CLICKED IN IMAGE2D SELECTION", LOG.DEBUG);  
		if(e.getButton()==MouseEvent.BUTTON1) {
			if(getBtnChoose().isSelected()) { 
				ChartPanel cp = heat.getChartPanel();
				Point2D pos = ChartLogics.mouseXYToPlotXY(cp, e.getX(), e.getY());
				
				currentSelect = null;
				// choose current rect
				for(int i=0; getSelections()!=null && i<getSelections().size(); i++) {
					SettingsShapeSelection s = getSelections().get(i);
					if((currentSelect==null || !currentSelect.equals(s)) && s.contains(pos.getX(), pos.getY())) {
						// found rect
						currentSelect = s;
						// TODO WHY? MODE?
						mode = s.getMode();
						lastMouseEvent = e;
						return;
					}
				}
			}
			else if(getCurrentShape().equals(SHAPE.POLYGON)) {
				ChartPanel cp = heat.getChartPanel();
				Point2D pos = ChartLogics.mouseXYToPlotXY(cp, e.getX(), e.getY());
				if(currentSelect==null || !SettingsPolygonSelection.class.isInstance(currentSelect)) {
					// create new
					currentSelect = new SettingsPolygonSelection(img, getCurrentSelectionMode(), x0, y0);
					addNewSelection(currentSelect);
					lastMouseEvent = e;
				}
				else {
					// add points
					((SettingsPolygonSelection)currentSelect).addPoint((float)pos.getX(), (float)pos.getY());
					lastMouseEvent = e;
				}
				
				// update selection stats and annotation
				updateSelection();		
			}
		}
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// creation of new selections
		if(e.getButton()==MouseEvent.BUTTON1 && !(getBtnChoose().isSelected())) { 
			// 
			ImageEditorWindow.log("PRESSED IN IMAGE2D SELECTION", LOG.DEBUG);  
			ChartPanel cp = heat.getChartPanel();
			Point2D pos = ChartLogics.mouseXYToPlotXY(cp, e.getX(), e.getY());
			x0 = (float)pos.getX();
			y0 = (float)pos.getY();
			
			// inside the chart?
			Range yrange = cp.getChart().getXYPlot().getRangeAxis().getRange();
			Range xrange = cp.getChart().getXYPlot().getDomainAxis().getRange();
			
			if(xrange.contains(pos.getX()) && yrange.contains(pos.getY())) {
				// create new selection
				SettingsShapeSelection tmpSelect = null;
				switch(getCurrentShape()) {
				case RECT:
					tmpSelect = new SettingsRectSelection(img, getCurrentSelectionMode(), x0, y0, 1, 1);
					break;
				case ELIPSE:
					tmpSelect = new SettingsElipseSelection(img, getCurrentSelectionMode(), x0, y0, 1, 1);
					break;
				case FREEHAND:
					tmpSelect = new SettingsPolygonSelection(img, getCurrentSelectionMode(), x0, y0);
					break;
				}
				if(tmpSelect!=null) {
					isPressed = true;
					addNewSelection(tmpSelect);
					currentSelect = tmpSelect;
					lastMouseEvent = e;
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton()==MouseEvent.BUTTON1 && isPressed) { 
			//
			ImageEditorWindow.log("RELEASED IN IMAGE2D SELECTION", LOG.DEBUG); 
			isPressed = false;
			
			if(currentSelect!=null) {
				ChartPanel cp = heat.getChartPanel();
				Point2D pos = ChartLogics.mouseXYToPlotXY(cp, e.getX(), e.getY()); 
				x1 = (float)pos.getX();
				y1 = (float)pos.getY();
				
				currentSelect.setFirstAndSecondMouseEvent(x0, y0, x1, y1);
				// update selection stats and annotation
				updateSelection();
			}
		}
	}
	/**
	 * keys
	 */
	private void addKeys() {   
		JPanel pn = (JPanel) getContentPane(); 
		InputMap im = pn.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "LEFT"); 
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "RIGHT");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "UP");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "DOWN");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.SHIFT_MASK), "shift LEFT"); 
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.SHIFT_MASK), "shift RIGHT");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.SHIFT_MASK), "shift UP");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.SHIFT_MASK), "shift DOWN");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.CTRL_MASK), "ctrl LEFT"); 
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.CTRL_MASK), "ctrl RIGHT");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_MASK), "ctrl UP");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_MASK), "ctrl DOWN");
		
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.CTRL_MASK+InputEvent.SHIFT_MASK), "ctrl shift LEFT"); 
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.CTRL_MASK+InputEvent.SHIFT_MASK), "ctrl shift RIGHT");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_MASK+InputEvent.SHIFT_MASK), "ctrl shift UP");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_MASK+InputEvent.SHIFT_MASK), "ctrl shift DOWN");

		// shift
		pn.getActionMap().put("shift LEFT", new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) {  
					shiftCurrentRectX(-1,KEY.ENLARGE);  
		}});
		pn.getActionMap().put("shift RIGHT", new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) { 
					shiftCurrentRectX(1,KEY.ENLARGE);  
		}});
		pn.getActionMap().put("shift UP", new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) { 
					shiftCurrentRectY(1,KEY.ENLARGE);  
		}});
		pn.getActionMap().put("shift DOWN", new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) { 
					shiftCurrentRectY(-1,KEY.ENLARGE);  
		}});
		// ctrl for shrinking
		pn.getActionMap().put("ctrl LEFT", new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) {  
					shiftCurrentRectX(-1,KEY.SHRINK);  
		}});
		pn.getActionMap().put("ctrl RIGHT", new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) { 
					shiftCurrentRectX(1,KEY.SHRINK);  
		}});
		pn.getActionMap().put("ctrl UP", new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) { 
					shiftCurrentRectY(1,KEY.SHRINK);  
		}});
		pn.getActionMap().put("ctrl DOWN", new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) { 
					shiftCurrentRectY(-1,KEY.SHRINK);  
		}});
		// arrows
		pn.getActionMap().put("LEFT", new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) {  
					shiftCurrentRectX(-1,KEY.SHIFT);  
		}});
		pn.getActionMap().put("RIGHT", new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) { 
					shiftCurrentRectX(1,KEY.SHIFT);  
		}});
		pn.getActionMap().put("UP", new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) { 
					shiftCurrentRectY(1,KEY.SHIFT);  
		}});
		pn.getActionMap().put("DOWN", new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) { 
					shiftCurrentRectY(-1,KEY.SHIFT);  
		}});

		// arrows
		pn.getActionMap().put("ctrl shift LEFT", new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) {  
					shiftCurrentRectX(-5,KEY.SHIFT);  
		}});
		pn.getActionMap().put("ctrl shift RIGHT", new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) { 
					shiftCurrentRectX(5,KEY.SHIFT);  
		}});
		pn.getActionMap().put("ctrl shift UP", new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) { 
					shiftCurrentRectY(5,KEY.SHIFT);  
		}});
		pn.getActionMap().put("ctrl shift DOWN", new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) { 
					shiftCurrentRectY(-5,KEY.SHIFT);  
		}});
	}
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	@Override
	public void mouseExited(MouseEvent e) {
	}
	public JToggleButton getBtnChoose() {
		return btnChoose;
	}

	public JPanel getPnChartView() {
		return pnChartView;
	}
	public JTable getTable() {
		return table;
	}
	public SHAPE getCurrentShape() {
		return (SHAPE) getComboShape().getSelectedItem(); 
	}
	private SelectionMode getCurrentSelectionMode() {
		return (SelectionMode) comboSelectionMode.getSelectedItem();
	}
	public JComboBox getComboShape() {
		return comboShape;
	}
	public JComboBox getComboSelectionMode() {
		return comboSelectionMode;
	}
	public JCheckBox getCbPerformance() {
		return cbPerformance;
	}
	public JCheckBox getCbMarkDp() {
		return cbMarkDp;
	}
}
