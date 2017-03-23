package net.rs.lamsi.multiimager.Frames.dialogs.selectdata;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
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
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.ImageGroupMD;
import net.rs.lamsi.general.datamodel.image.TestImageFactory;
import net.rs.lamsi.massimager.Heatmap.Heatmap;
import net.rs.lamsi.massimager.Heatmap.HeatmapFactory;
import net.rs.lamsi.massimager.MyFreeChart.ChartLogics;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow.LOG;
import net.rs.lamsi.multiimager.Frames.dialogs.DialogDataSaver;
import net.rs.lamsi.multiimager.test.TestQuantifier;
import net.rs.lamsi.utils.DialogLoggerUtil;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYBoxAnnotation;
import org.jfree.data.Range;
 

public class Image2DSelectDataAreaDialog extends JFrame implements MouseListener, MouseMotionListener {
	// 
	private enum KEY {
		SHRINK, SHIFT, ENLARGE
	}
	// what to draw or do
	public enum MODE {
		MODE_EXCLUDE("EXCLUDE"), MODE_SELECT("SELECT"), MODE_INFO("INFO");
		
		private String title;
		MODE(String title) {
			this.title = title;
		}
		@Override
		public String toString() { 
			return title;
		}
	}
	private MODE mode = MODE.MODE_SELECT;
	// mystuff
	private Heatmap heat;
	private Image2D img;
	// save the relation in hashmap
	private HashMap<RectSelection, XYBoxAnnotation> map;
	//
	private Vector<SelectionTableRow> tableRows;
	private Vector<RectSelection> rects, rectsExclude, rectsInfo; 
	private RectSelection currentRect;
	private int x0,x1,y0,y1;
	private XYBoxAnnotation currentAnn;
	private boolean isPressed = false;
	
	// components
	private JPanel contentPane;
	private JPanel pnChartView;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JToggleButton btnRect;
	private JToggleButton btnChoose;
	private JToggleButton btnExclude;
	private JTable table;
	private JToggleButton btnInfoRect;

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
					frame.startDialog(img.get(0));
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
		setBounds(100, 100, 669, 465);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel pnNorthMenu = new JPanel();
		contentPane.add(pnNorthMenu, BorderLayout.NORTH);
		
		btnChoose = new JToggleButton("Choose");
		btnChoose.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				heat.getChartPanel().setMouseZoomable(((JToggleButton)e.getSource()).isSelected());
			}
		});
		buttonGroup.add(btnChoose);
		pnNorthMenu.add(btnChoose);
		
		btnRect = new JToggleButton("Select");
		btnRect.setToolTipText("Selection rectangles are used for selecting data. All data points outside of the sum of selctions are excluded from processing (for example blank reduction)");
		btnRect.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(((JToggleButton)e.getSource()).isSelected()) mode = MODE.MODE_SELECT;
			}
		});
		
		btnInfoRect = new JToggleButton("InfoRect");
		btnInfoRect.setToolTipText("Info rectangles are used for data analysis.");
		btnInfoRect.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(((JToggleButton)e.getSource()).isSelected()) mode = MODE.MODE_INFO;
			}
		});
		buttonGroup.add(btnInfoRect);
		pnNorthMenu.add(btnInfoRect);
		buttonGroup.add(btnRect);
		btnRect.setSelected(true);
		pnNorthMenu.add(btnRect);
		
		JButton btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteCurrentRect();
			}
		}); 
		
		btnExclude = new JToggleButton("Exclude");
		btnExclude.setToolTipText("Exclude rectangles are excluding data points from calculation.");
		btnExclude.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(((JToggleButton)e.getSource()).isSelected()) mode = MODE.MODE_EXCLUDE;
			}
		});
		buttonGroup.add(btnExclude);
		pnNorthMenu.add(btnExclude);
		pnNorthMenu.add(btnDelete);
		
		JButton btnDeleteAll = new JButton("Delete All");
		btnDeleteAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeAllRows();
				currentRect = null;
				rects.removeAllElements();
				rectsExclude.removeAllElements();
				rectsInfo.removeAllElements();
				heat.getPlot().clearAnnotations();
				updateSelection();
			}
		}); 
		pnNorthMenu.add(btnDeleteAll);
		
		JButton btnExportData = new JButton("Export data");
		btnExportData.setToolTipText("Export all selected/not excluded data points as raw or processed data.");
		btnExportData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DialogDataSaver.startDialogWith(img, rects, rectsExclude, rectsInfo, tableRows);
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
		table.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
					"Type", "x0", "y0", "x1", "y1", "I min", "I max", "I avg", "I median", "I 99%", "I stdev", "Histo"
			}
		) {
			Class[] columnTypes = new Class[] {
				String.class, String.class, String.class, String.class, String.class, Double.class, Double.class, Double.class, Double.class, Double.class, Double.class, ChartPanel.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		
		table.getColumnModel().getColumn(table.getColumnCount()-1).setCellRenderer(new TableHistoColumnRenderer());
		
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
	}

	
	/**
	 * shift x by i
	 * @param i
	 * @param enlarge hold position and enlarge
	 */
	protected void shiftCurrentRectX(int i, KEY key) {
		if(currentRect==null)
			return;
		// shift 
		if(!((key.equals(KEY.ENLARGE) && i>0)||(key.equals(KEY.SHRINK) && i<0)))
			currentRect.translate(i, 0);
		// enlarge?
		if(key.equals(KEY.ENLARGE)) currentRect.grow(1, 0);
		if(key.equals(KEY.SHRINK)) currentRect.grow(-1, 0);
		// check bounds
		currentRect.applyMaxima(0, 0, img.getMaxDP()-1, img.getMaxLineCount()-1);
		
		heat.getPlot().removeAnnotation(currentAnn, false);
		map.remove(currentRect); 
		removeRow(currentRect);
		// remove totally if height == 0
		addSelection(currentRect);
		updateSelection();
	}
	protected void shiftCurrentRectY(int i, KEY key) {
		if(currentRect==null)
			return;
		// shift 
		if(!((key.equals(KEY.ENLARGE) && i>0)||(key.equals(KEY.SHRINK) && i<0)))
			currentRect.translate(0 , i);
		// enlarge?
		if(key.equals(KEY.ENLARGE)) currentRect.grow(0,1);
		if(key.equals(KEY.SHRINK)) currentRect.grow(0,-1);
		// check bounds
		currentRect.applyMaxima(0, 0, img.getMaxDP()-1, img.getMaxLineCount()-1);
		
		// remove annotation
		heat.getPlot().removeAnnotation(currentAnn, false);
		map.remove(currentRect);
		removeRow(currentRect);
		// remove totally if height == 0
		addSelection(currentRect);
		updateSelection();
	}

	protected void deleteCurrentRect() { 
		if(currentRect!=null) {
			rects.remove(currentRect);
			rectsExclude.remove(currentRect);
			rectsInfo.remove(currentRect);
			heat.getPlot().removeAnnotation(currentAnn, false);
			map.remove(currentRect);
			removeRow(currentRect);
			updateSelection();
			currentRect = null;
		}
	}

	/**
	 * give all selections to img
	 * end of dialog
	 */
	protected void applySelections() {
		// TODO Auto-generated method stub
		img.setSelectedData(rects);
		img.setExcludedData(rectsExclude);
		img.setInfoData(rectsInfo);
		// listeners?
	}
	
	public void startDialog(Image2D img) {
		if(img!=null) { 
			try {
				heat = HeatmapFactory.generateHeatmap(img);
				this.img = heat.getImage();
				// get all existing rects
				map = new HashMap<RectSelection, XYBoxAnnotation>();
				rects = img.getSelectedData();  
				rectsExclude = img.getExcludedData();  
				rectsInfo = img.getInfoData();  
				if(tableRows==null)
					tableRows = new Vector<SelectionTableRow>();
				else removeAllRows();
				// add all
				for(RectSelection r : rects) {
					addSelection(r);
				} 
				// add all
				getBtnExclude().setSelected(true);
				for(RectSelection r : rectsExclude) {
					addSelection(r);
				}
				// add all
				getBtnInfoRect().setSelected(true);
				for(RectSelection r : rectsInfo) {
					addSelection(r);
				}
				// reset 
				getBtnRect().setSelected(true);
				// add to screen
				getPnChartView().add(heat.getChartPanel(), BorderLayout.CENTER);
				// add mouse 
				heat.getChartPanel().addMouseListener(this);
				heat.getChartPanel().addMouseMotionListener(this); 
				heat.getChartPanel().setMouseZoomable(false);
				// show
				updateSelection();
				setVisible(true); 
			} catch(Exception ex) {
				DialogLoggerUtil.showErrorDialog(this, "", ex);
				ImageEditorWindow.log(ex.getMessage(), LOG.ERROR);
			}
		}
	}

	//'##################################################################
	// table stuff
	private void removeAllRows() {
		while(tableRows.size()>0){
			removeRow(0);
		}
	}
	private void removeRow(int i) { 
		((DefaultTableModel)table.getModel()).removeRow(i);
		tableRows.remove(i);
	}
	private void removeRow(RectSelection r) { 
		for(int i=0; i<tableRows.size(); i++) {
			if(tableRows.get(i).getRect()==r) {
				((DefaultTableModel)table.getModel()).removeRow(i);
				tableRows.remove(i);
				return;
			}
		}
	}
	private void addRow(RectSelection rect) {
		SelectionTableRow row = new SelectionTableRow(img, rect);
		tableRows.add(row);
		((DefaultTableModel)table.getModel()).addRow(row.getRowData());
	}

	/**
	 * adds a rectangle to the selection
	 * @param r
	 */
	private void addSelection(RectSelection r) {
		// for displaying xe and ye +1
		double x0 = img.getX(false, r.getMinY(), r.getMinX());
		double x1 = img.getX(false, r.getMaxY(), r.getMaxX()+1);
		double y0 = img.getY(false,r.getMinY(), r.getMinX());
		double y1 = img.getY(false,r.getMaxY()+1, r.getMaxX()+1); 
		
		Color c = null;
		switch(r.getMode()) {
		case MODE_EXCLUDE:
			c = Color.RED;
			break;
		case MODE_SELECT:
			c = Color.BLACK;
			break;
		case MODE_INFO:
			c = Color.gray;
			break;
		} 
		currentAnn = new XYBoxAnnotation(x0, y0, x1, y1, new BasicStroke(1.5f), c);
		
		heat.getPlot().addAnnotation(currentAnn, false);
		// save in map
		map.put(r, currentAnn);
		
		// put data in table
		addRow(r);
	}
	
	

	/**
	 * show all in chart
	 */
	protected void updateSelection() {
		// Update rects
		if(currentRect!=null && currentRect.getMode()==MODE.MODE_EXCLUDE) {
			// update all selection rects
			for(RectSelection sel : rects) 
				update(sel);
		}
		
		//
		JFreeChart chart = heat.getChart();
		// remove all annotations 
		chart.fireChartChanged();
		//
		ImageEditorWindow.log("UPDATE CHART", LOG.DEBUG);
	}
	
	
	private void update(RectSelection rec) { 
		// TODO
		//XYBoxAnnotation ann = map.get(rec);
		// remove annotation
		//heat.getPlot().removeAnnotation(ann, false); 
		//map.remove(rec);
		removeRow(rec);
		addRow(rec);
		// remove totally if height == 0
		//ann = currentAnn;
		//addSelection(rec);
		//currentAnn = ann;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		ImageEditorWindow.log("MOVED", LOG.DEBUG);
		// UPDATE THE CURRENT RECT
		if(isPressed) {
			ChartPanel cp = heat.getChartPanel();
			Point2D pos = ChartLogics.mouseXYToPlotXY(cp, e.getX(), e.getY()); 
 
			y1 = img.getYAsIndex(pos.getY(), pos.getX());
			x1 = img.getXAsIndex(y1, pos.getX());
			currentRect.setBounds(x0,y0,x1,y1);
			// update annotation
			//
			ImageEditorWindow.log("MOVED: remove and add", LOG.DEBUG);
			heat.getPlot().removeAnnotation(currentAnn, false);
			map.remove(currentRect);
			removeRow(currentRect);
			addSelection(currentRect);
			updateSelection();
		}
	}
	@Override
	public void mouseMoved(MouseEvent e) {
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		ImageEditorWindow.log("CLICKED IN IMAGE2D SELECTION", LOG.DEBUG);  
		if(e.getButton()==MouseEvent.BUTTON1 && (getBtnChoose().isSelected())) { 
			ChartPanel cp = heat.getChartPanel();
			Point2D pos = ChartLogics.mouseXYToPlotXY(cp, e.getX(), e.getY());
			y1 = img.getYAsIndex(pos.getY(), pos.getX());
			x1 = img.getXAsIndex(y1, pos.getX());
			currentRect = null;
			// choose current rect
			for(int i=0; i<rects.size(); i++) {
				if(rects.get(i).contains(x1, y1)) {
					// found rect
					currentRect = rects.get(i);
					currentAnn = map.get(currentRect);
					mode = MODE.MODE_SELECT;
					return;
				}
			}
			// choose current rect
			for(int i=0; i<rectsExclude.size(); i++) {
				if(rectsExclude.get(i).contains(x1, y1)) {
					// found rect
					currentRect = rectsExclude.get(i);
					currentAnn = map.get(currentRect);
					mode = MODE.MODE_EXCLUDE;
					return;
				}
			}
			// choose current rect
			for(int i=0; i<rectsInfo.size(); i++) {
				if(rectsInfo.get(i).contains(x1, y1)) {
					// found rect
					currentRect = rectsInfo.get(i);
					currentAnn = map.get(currentRect);
					mode = MODE.MODE_INFO;
					return;
				}
			}
		}
	}
	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton()==MouseEvent.BUTTON1 && (getBtnRect().isSelected() || getBtnExclude().isSelected()) || getBtnInfoRect().isSelected()) { 
			//
			ImageEditorWindow.log("PRESSED IN IMAGE2D SELECTION", LOG.DEBUG);  
			ChartPanel cp = heat.getChartPanel();
			Point2D pos = ChartLogics.mouseXYToPlotXY(cp, e.getX(), e.getY());
			// nur speichern wenn innerhalb des charts
			Range yrange = cp.getChart().getXYPlot().getRangeAxis().getRange();
			Range xrange = cp.getChart().getXYPlot().getDomainAxis().getRange();
			
			if(xrange.contains(pos.getX()) && yrange.contains(pos.getY())) {
				isPressed = true;
				// create rect
				y0 = img.getYAsIndex(pos.getY(), pos.getX());
				x0 = img.getXAsIndex(y0, pos.getX());
				currentRect = new RectSelection(mode, x0, y0, x0, y0);
				if(mode == MODE.MODE_EXCLUDE)
					rectsExclude.add(currentRect);
				else if(mode==MODE.MODE_SELECT) rects.add(currentRect);
				else rectsInfo.add(currentRect);
				addSelection(currentRect);
				updateSelection();
			}
		}
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton()==MouseEvent.BUTTON1 && isPressed) { 
			//
			ImageEditorWindow.log("RELEASED IN IMAGE2D SELECTION", LOG.DEBUG); 
			isPressed = false;
			

			ChartPanel cp = heat.getChartPanel();
			Point2D pos = ChartLogics.mouseXYToPlotXY(cp, e.getX(), e.getY()); 

			y1 = img.getYAsIndex(pos.getY(), pos.getX());
			x1 = img.getXAsIndex(y1, pos.getX());
			currentRect.setBounds(x0,y0,x1,y1);
			// remove
			heat.getPlot().removeAnnotation(currentAnn, false);
			map.remove(currentRect);
			removeRow(currentRect);
			// readd 
			addSelection(currentRect); 
			updateSelection();
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
	}
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	@Override
	public void mouseExited(MouseEvent e) {
	}
	public JToggleButton getBtnRect() {
		return btnRect;
	}
	public JToggleButton getBtnChoose() {
		return btnChoose;
	}
	public JToggleButton getBtnExclude() {
		return btnExclude;
	}

	public JPanel getPnChartView() {
		return pnChartView;
	}
	public JTable getTable() {
		return table;
	}
	public JToggleButton getBtnInfoRect() {
		return btnInfoRect;
	}
}
