package net.rs.lamsi.multiimager.Frames.multiimageframe;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.interf.ImageDataset;
import net.rs.lamsi.general.datamodel.image.listener.RawDataChangedListener;
import net.rs.lamsi.massimager.Frames.FrameWork.RangeSliderColumn;
import net.rs.lamsi.massimager.Heatmap.Heatmap;
import net.rs.lamsi.massimager.Heatmap.HeatmapFactory;
import net.rs.lamsi.massimager.MyFileChooser.FileTypeFilter;
import net.rs.lamsi.massimager.MyFreeChart.Plot.image2d.annot.ImageTitle;
import net.rs.lamsi.massimager.MyFreeChart.Plot.image2d.listener.AspectRatioListener;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow.LOG;
import net.rs.lamsi.utils.DialogLoggerUtil;
import net.rs.lamsi.utils.FileAndPathUtil;
import net.rs.lamsi.utils.mywriterreader.TxtWriter;
import net.rs.lamsi.utils.mywriterreader.XSSFExcelWriterReader;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.chart.plot.XYPlot;

public class MultiImageFrame extends JFrame {

	private JFrame thisframe;
	private Font font = new Font("Arial", Font.BOLD, 14);
	private JPanel contentPane;
	private JSplitPane split;
	private JPanel pnGridImg;
	private GridLayout gridLayout;
	private int GRID_COL = 2;
	// data table
	private JTable table;
	private MultiImageTableModel tableModel;
	// data 
	private Image2D[] img; 
	// keep track of updating, dont update every heatmap all the time only shown ones
	private boolean[] uptodate;
	// insert heatmaps in this array
	private Heatmap[] heat;
	// title
	private ImageTitle[] titles;
	// panels in each grid
	private JPanel pn[];

	// boolean map for visible pixel according to range limitations of other images
	// map[lines][dp]
	private boolean[][] map;
	private boolean[] maplinear;
	private JMenuBar menuBar;
	private JMenu mnSettings;
	private JMenuItem mntmColumns;
	private JPanel panel_1;
	private JPanel panel_2;
	private JCheckBox cbShowTitles;
	private JCheckBox cbShowAxes;
	private JCheckBox cbKeepAspectRatio;
	private JSpinner spinnerColumns;
	private JLabel lblCol;
	private JMenu mnExport;
	private JMenuItem mntmExportMap;
	private JMenuItem mntmExportData;
	private JFileChooser chooserMap = new JFileChooser();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MultiImageFrame frame = new MultiImageFrame();
					frame.setVisible(true);

					Image2D[] images = new Image2D[10];
					for(int i=0; i<10; i++) {
						images[i] = Image2D.createTestStandard();
						images[i].getSettImage().setTitle("ABC"+i);
					}
					frame.init(images);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MultiImageFrame() {
		thisframe = this;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 790, 462);

		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		mnExport = new JMenu("Export");
		menuBar.add(mnExport);

		mntmExportMap = new JMenuItem("Export map");
		mntmExportMap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// export boolean map as binary
				if(chooserMap.showSaveDialog(thisframe)==JFileChooser.APPROVE_OPTION) {
					File file = chooserMap.getSelectedFile();
					FileTypeFilter filter = (FileTypeFilter)chooserMap.getFileFilter();
					// excel or csv/txt
					if(filter.getExtension().equalsIgnoreCase("xlsx")) {
						saveMapToExcel(file);
					}
					else {
						saveMapToTxt(file, filter.getExtension());
					}
				} 
			}
		});
		mnExport.add(mntmExportMap);


		JMenuItem mntmExportMap = new JMenuItem("Export binary map");
		mntmExportMap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// export boolean map as binary
				if(chooserMap.showSaveDialog(thisframe)==JFileChooser.APPROVE_OPTION) {
					File file = chooserMap.getSelectedFile();
					FileTypeFilter filter = (FileTypeFilter)chooserMap.getFileFilter();
					// excel or csv/txt
					if(filter.getExtension().equalsIgnoreCase("xlsx")) {
						saveBinaryMapToExcel(file);
					}
					else {
						saveBinaryMapToTxt(file, filter.getExtension());
					}
				} 
			}
		});
		mnExport.add(mntmExportMap);

		mntmExportData = new JMenuItem("Export data");
		mntmExportData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// export boolean map as binary
				if(chooserMap.showSaveDialog(thisframe)==JFileChooser.APPROVE_OPTION) {
					File file = chooserMap.getSelectedFile();
					FileTypeFilter filter = (FileTypeFilter)chooserMap.getFileFilter();
					// excel or csv/txt
					if(filter.getExtension().equalsIgnoreCase("xlsx")) {
						saveAllToExcel(file);
					}
					else {
						saveAllToTxt(file, filter.getExtension());
					}
				} 
			}
		});
		mnExport.add(mntmExportData);

		mnSettings = new JMenu("Settings");
		menuBar.add(mnSettings);

		mntmColumns = new JMenuItem("Set columns");
		mntmColumns.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// open dialog get integer
				try {
					int s = Integer.valueOf(JOptionPane.showInputDialog( "How many columns?" ));
					setColumns(s);
				} catch(Exception ex) { 
				}
			}
		});
		mnSettings.add(mntmColumns);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		split = new JSplitPane();
		split.setResizeWeight(0.5);
		split.setOrientation(JSplitPane.VERTICAL_SPLIT);
		contentPane.add(split, BorderLayout.CENTER);

		JScrollPane scrollTable = new JScrollPane();
		split.setLeftComponent(scrollTable);

		tableModel = new MultiImageTableModel() { 
			@Override
			public void fireGridChanged() {
				updateGridView();
			} 
			@Override
			public void fireDataProcessingChanged() {
				fireProcessingChanged();
			}
		};

		table = new JTable();
		scrollTable.setViewportView(table);
		table.setModel(tableModel);

		// range slider column
		RangeSliderColumn col = new RangeSliderColumn(table, 6, 0, 100);

		panel_1 = new JPanel();
		split.setRightComponent(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));

		panel_2 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_2.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		flowLayout.setVgap(0);
		panel_1.add(panel_2, BorderLayout.NORTH);

		cbShowTitles = new JCheckBox("Titles");
		cbShowTitles.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				showTitles(cbShowTitles.isSelected());
			}
		});
		cbShowTitles.setSelected(true);
		panel_2.add(cbShowTitles);

		cbShowAxes = new JCheckBox("Axes");
		cbShowAxes.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				setAxesVisible(cbShowAxes.isSelected());
				updateGridView();
			}
		});
		panel_2.add(cbShowAxes);

		cbKeepAspectRatio = new JCheckBox("Keep aspect ratio");
		cbKeepAspectRatio.setSelected(true);
		panel_2.add(cbKeepAspectRatio);

		spinnerColumns = new JSpinner();
		spinnerColumns.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				setColumns((int) spinnerColumns.getValue());
			}
		});
		spinnerColumns.setModel(new SpinnerNumberModel(3, 1, 10, 1));
		panel_2.add(spinnerColumns);

		lblCol = new JLabel("col");
		panel_2.add(lblCol);

		JScrollPane scrollImages = new JScrollPane();
		panel_1.add(scrollImages);
		scrollImages.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		JPanel panel = new JPanel();
		scrollImages.setViewportView(panel);
		panel.setLayout(new BorderLayout(0, 0));

		pnGridImg = new JPanel();
		panel.add(pnGridImg, BorderLayout.CENTER);
		gridLayout = new GridLayout(1, 2, 5, 5);
		pnGridImg.setLayout(gridLayout);

		FileTypeFilter filter = new FileTypeFilter("xlsx", "Excel file");
		chooserMap.addChoosableFileFilter(filter);
		chooserMap.setFileFilter(filter);
		chooserMap.addChoosableFileFilter(new FileTypeFilter("txt", "Text file"));
		chooserMap.addChoosableFileFilter(new FileTypeFilter("csv", "Comma separated text file"));
	}

	/**
	 * sets them visible
	 * @param selected
	 */
	protected void showTitles(boolean show) { 
		if(titles!=null) {
			for(int i=0; i<titles.length; i++) {
				ImageTitle h = titles[i];
				if(h!=null) {
					if(show) heat[i].getPlot().addAnnotation(h.getAnnotation());
					else heat[i].getPlot().removeAnnotation(h.getAnnotation());
				}
			}
		}
	}

	/**
	 * all charts that are not null are getting their show axes state
	 * @param selected
	 */
	protected void setAxesVisible(boolean show) {
		if(heat!=null) {
			for(Heatmap h : heat) {
				if(h!=null) {
					h.getPlot().getDomainAxis().setVisible(show);
					h.getPlot().getRangeAxis().setVisible(show);
				}
			}
		}
	}

	protected void setColumns(int s) {
		ImageEditorWindow.log("Set col: "+s, LOG.DEBUG);
		GRID_COL = s;
		gridLayout.setColumns(GRID_COL);
		updateGridView();
	}

	/**
	 * initilize this frame
	 * @param img
	 * @param folder collection name
	 */
	public void init(Image2D[] img) {
		this.img = img; 
		heat = new Heatmap[img.length];
		uptodate = new boolean[img.length];
		titles = new ImageTitle[img.length];
		pn = new JPanel[img.length];
		// for all images
		for(int k =0; k<img.length; k++) {
			// create new pn as place holder
			final int index = k;
			pn[k] = new JPanel(new BorderLayout());
			pn[k].addComponentListener(new AspectRatioListener() { 
				@Override
				public void componentResized(ComponentEvent e) {
					if(getCbKeepAspectRatio().isSelected() && heat[index] !=null) {
						resize(heat[index].getChartPanel(), pn[index], RATIO.LIMIT_TO_PN_WIDTH);
					}
				}
			});
			getPnGridImg().add(pn[k]);
			// put them into the table
			tableModel.addRow(new MultiImgTableRow(k, img[k]));
		}

		// add raw data changed listener for direct imaging 
		img[img.length-1].addRawDataChangedListener(new RawDataChangedListener() { 
			@Override
			public void rawDataChangedEvent(ImageDataset data) {
				if(data.getLinesCount()>0) { 
					createNewMap(); 
					for(int i=0; i<uptodate.length; i++) {
						heat[i] = null;
						uptodate[i] = false;
					}
					updateGridView();
				}
			}
		});
		// show them in the grid view
		fireProcessingChanged();
		// 
		this.setVisible(true);
	}

	/**
	 * gets called if data processing changes
	 * taget function!
	 */
	public void fireProcessingChanged() { 
		for(int i=0; i<uptodate.length; i++)
			uptodate[i] = false;
		// update boolean map for visible pixel
		updateMap();
		// update grid and show charts
		updateGridView();
	}

	/**
	 * create new map
	 */
	protected void createNewMap() {
		// only if different size
		Image2D first = img[0];

		boolean different = first.getLineCount()!=map.length;
		if(!different) {
			for(int r = 0; r<map.length && !different; r++)
				if(map[r].length!=first.getLineLength(r))
					different = true;
		}
		if(different) {
			map = new boolean[first.getLineCount()][];
			for(int r = 0; r<first.getLineCount(); r++)
				map = new boolean[r][first.getLineLength(r)];  
		}
		updateMap();

		ImageEditorWindow.log("new map "+map.length, LOG.DEBUG);
	}
	/**
	 * update boolean map for visible points
	 * 
	 */
	private void updateMap() {
		try { 
			//new?
			Image2D first = img[0];
			if(map == null) {
				map = new boolean[first.getLineCount()][];
				for(int r = 0; r<first.getLineCount(); r++)
					map = new boolean[r][first.getLineLength(r)]; 
			}
			// init as true
			for(int r = 0; r<map.length; r++)
				for(int d=0; d<map[r].length; d++)
					map[r][d] = true;

			// go through all rows and check if in range
			for(int i=0; i<tableModel.getRowList().size(); i++) {
				MultiImgTableRow row = tableModel.getRowList().get(i);
				row.applyToMap(map);
			}

			// save linear one
			maplinear = convertMap(map);
		} catch(Exception ex) {
			ex.printStackTrace();
			ImageEditorWindow.log(ex.getMessage(), LOG.ERROR);
		}
	}

	/**
	 * takes all rows in account and shows images in grid view
	 */
	private void updateGridView() {
		// empty grid
		for(JPanel p : pn) 
			p.removeAll();
		// count images to show = grid rows 
		int grows = 0;
		for(int i=0; i<uptodate.length; i++) 
			if(tableModel.getRowList().get(i).isShowing()) 
				grows ++;
		grows = (int)(grows/GRID_COL)+(grows%GRID_COL==0?0:1);
		gridLayout.setRows(grows);
		// gridindex
		int gi = 0;
		// go through table, update and add heats
		for(int i=0; i<uptodate.length; i++) {
			MultiImgTableRow row = tableModel.getRowList().get(i);
			if(row.isShowing()) {
				// update needed?
				updateGrid(gi, i, false);
				// increment
				gi++;
			}
		}
		// update fram
		getPnGridImg().revalidate();
		getPnGridImg().repaint();
	}

	/**
	 * updates a grid
	 * @param gi
	 * @param imgIndex
	 */
	private void updateGrid(int gi, int imgIndex, boolean repaint) {
		if(!uptodate[imgIndex])
			updateChart(imgIndex);
		// add to grid view
		if(heat[imgIndex]!=null) { 
			final Heatmap h = heat[imgIndex];
			h.getChartPanel().setPreferredSize(new Dimension(50,50)); 
			pn[gi].add(heat[imgIndex].getChartPanel(), BorderLayout.CENTER);
		}
		if(repaint) {
			getPnGridImg().revalidate();
			getPnGridImg().repaint();
		}
	}

	/**
	 * searches for the grid index
	 * @param imgIndex
	 */
	private void updateGrid(int imgIndex) {
		// gridindex
		int gi = 0;
		// go through table
		for(int i=0; i<imgIndex; i++)
			if(tableModel.getRowList().get(i).isShowing())
				// increment
				gi++;
		// update grid 
		updateGrid(gi, imgIndex, true);
	}

	/**
	 * creates new chart for i
	 * gets called after change in update grid view
	 * @param i
	 */
	private void updateChart(int i) {
		try {
			if(img[i].getLineCount()>0) {
				// generate heat if not already
				if(heat[i]==null) {
					heat[i] = HeatmapFactory.generateHeatmap(img[i]); 
					// show axes?
					heat[i].getPlot().getDomainAxis().setVisible(getCbShowAxes().isSelected());
					heat[i].getPlot().getRangeAxis().setVisible(getCbShowAxes().isSelected());
					// title?
					XYPlot plot = heat[i].getPlot();
					ImageTitle lt = new ImageTitle(img[i], font); 
					lt.setVisible(getCbShowTitles().isSelected());
					plot.addAnnotation(lt.getAnnotation()); 
					// 
					titles[i] = lt;
				} 

				// set new map
				heat[i].getRenderer().setMap(maplinear);
				heat[i].getChart().fireChartChanged();
				// set uptodate
				uptodate[i] = true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * creates new chart for i
	 * gets called after change in update grid view 
	 */
	private void updateAllCharts() {
		for(int i=0; i<img.length; i++)
			updateChart(i);
	}

	/**
	 * converts the map to one dimension as line, line,line,line
	 */
	private boolean[] convertMap(boolean[][] map) {
		int size = 0;
		for(boolean[] m : map)
			size+=m.length;

		boolean[] maplinear = new boolean[size];
		int c = 0;
		for(boolean[] m : map) {
			for(boolean b : m) {
				maplinear[c] = b;
				c++;
			}
		}
		return maplinear;
	}


	//###################################################################
	// EXPORT: data, map, all, binary map


	/** 
	 * update map and save to txt file
	 * @param extension
	 */
	protected void saveAllToTxt(File file, String ext) {
		try {
			TxtWriter writer = new TxtWriter();
			//
			String fname = FileAndPathUtil.eraseFormat(file.getAbsolutePath());
			// save table 
			writer.writeDataArrayToFile(FileAndPathUtil.getRealFileName(fname+"_table", ext), tableModel.toArray(true), ",");
			
			//save map 
			updateMap();
			writer.writeBooleanArrayToFile(FileAndPathUtil.getRealFileName(fname+"_map", ext), map, ",");

			// save multi map
			if(isBinaryMapAvailable())
				saveBinaryMapToTxt(new File(fname+"_multimap"), ext); 

			// save all img
			for(int i=0; i<img.length; i++) {
				updateChart(i);
				// export to new file
				writer.writeDataArrayToFile(FileAndPathUtil.getRealFileName(fname+"_"+i+"_"+img[i].getTitle(), ext), img[i].toIMatrix(true, map), ",");
			}
			// show dialog
			DialogLoggerUtil.showMessageDialogForTime(thisframe, "SUCCESS", "File written!", 2);
		}catch(Exception ex) {
			ex.printStackTrace();
			// show dialog
			DialogLoggerUtil.showErrorDialog(thisframe, "ERROR while saving", "File not written");
		}
	}

	/** 
	 * update map and save to xlsx file
	 * @param extension
	 */
	protected void saveAllToExcel(File file) { 
		try {
			XSSFExcelWriterReader writer = new XSSFExcelWriterReader();
			XSSFWorkbook wb = new XSSFWorkbook();
			// write table 
			XSSFSheet sheet = writer.getSheet(wb, "table"); 
			writer.writeDataArrayToSheet(sheet, tableModel.toArray(true), 0, 0);
			
			// map
			updateMap();
			sheet = writer.getSheet(wb, "map");
			writer.writeBooleanArrayToSheet(sheet, map, 0, 0);

			// multi map 
			if(isBinaryMapAvailable()) {
				sheet = writer.getSheet(wb, "multimap");
				Object[][] bmap = createBinaryMap(); 
				writer.writeDataArrayToSheet(sheet, bmap, 0, 0); 
			}

			// write all images 
			for(int i=0; i<img.length; i++) {
				updateChart(i);
				// export to new file
				sheet = writer.getSheet(wb, i+" "+img[i].getTitle());
				writer.writeDataArrayToSheet(sheet, img[i].toIMatrix(true,map), 0, 0); 
			}

			writer.saveWbToFile(new File(FileAndPathUtil.getRealFileName(file, "xlsx")), wb);
			writer.closeAllWorkbooks();
			// show dialog
			DialogLoggerUtil.showMessageDialogForTime(thisframe, "SUCCESS", "File written!", 2);
		}catch(Exception ex) {
			ex.printStackTrace();
			// show dialog
			DialogLoggerUtil.showErrorDialog(thisframe, "ERROR while saving", "File not written");
		}
	}

	/** 
	 * update map and save to txt file
	 * @param extension
	 */
	protected void saveMapToTxt(File file, String ext) {
		try {
			updateMap();
			new TxtWriter().writeBooleanArrayToFile(FileAndPathUtil.getRealFileName(file, ext), map, ",");
			// show dialog
			DialogLoggerUtil.showMessageDialogForTime(thisframe, "SUCCESS", "File written!", 2);
		}catch(Exception ex) {
			ex.printStackTrace();
			// show dialog
			DialogLoggerUtil.showErrorDialog(thisframe, "ERROR while saving", "File not written");
		}
	}

	/** 
	 * update map and save to xlsx file
	 * @param extension
	 */
	protected void saveMapToExcel(File file) { 
		try {
			updateMap();
			XSSFExcelWriterReader writer = new XSSFExcelWriterReader();
			XSSFWorkbook wb = new XSSFWorkbook();
			XSSFSheet sheet = writer.getSheet(wb, "map");
			writer.writeBooleanArrayToSheet(sheet, map, 0, 0);
			writer.saveWbToFile(new File(FileAndPathUtil.getRealFileName(file, "xlsx")), wb);
			writer.closeAllWorkbooks();
			// show dialog
			DialogLoggerUtil.showMessageDialogForTime(thisframe, "SUCCESS", "File written!", 2);
		}catch(Exception ex) {
			ex.printStackTrace();
			// show dialog
			DialogLoggerUtil.showErrorDialog(thisframe, "ERROR while saving", "File not written");
		}
	}

	/** 
	 * update map and save to txt file
	 * binary map:
	 * 
	 * @param extension
	 */
	protected void saveBinaryMapToTxt(File file, String ext) {
		try {
			Object[][] bmap = createBinaryMap();
			new TxtWriter().writeDataArrayToFile(FileAndPathUtil.getRealFileName(file, ext), bmap, ",");
			DialogLoggerUtil.showMessageDialogForTime(thisframe, "SUCCESS", "File written!", 2);
		}catch(Exception ex) {
			ex.printStackTrace();
			// show dialog
			DialogLoggerUtil.showErrorDialog(thisframe, "ERROR while saving", "File not written");
		}
	}

	/** 
	 * update map and save to xlsx file
	 * @param extension
	 */
	protected void saveBinaryMapToExcel(File file) { 
		try {
			Object[][] bmap = createBinaryMap();
			XSSFExcelWriterReader writer = new XSSFExcelWriterReader();
			XSSFWorkbook wb = new XSSFWorkbook();
			XSSFSheet sheet = writer.getSheet(wb, "map");
			writer.writeDataArrayToSheet(sheet, bmap, 0, 0);
			writer.saveWbToFile(new File(FileAndPathUtil.getRealFileName(file, "xlsx")), wb);
			writer.closeAllWorkbooks();
			// show dialog
			DialogLoggerUtil.showMessageDialogForTime(thisframe, "SUCCESS", "File written!", 2);
		}catch(Exception ex) {
			ex.printStackTrace();
			// show dialog
			DialogLoggerUtil.showErrorDialog(thisframe, "ERROR while saving", "File not written");
		}
	}


	/**
	 * map ony for export: binary map
	 * @return
	 */
	private Object[][] createBinaryMap() throws Exception {
		Image2D first = img[0];
		Integer[][] bmap = new Integer[first.getLineCount()][];
		for(int r = 0; r<first.getLineCount(); r++)
			bmap = new Integer[r][first.getLineLength(r)];  

		// init as 0
		for(int r = 0; r<bmap.length; r++)
			for(int d=0; d<bmap[r].length; d++)
				bmap[r][d] = 0;

		// go through all rows and check if in range
		int counter = 0;
		for(int i=0; i<tableModel.getRowList().size(); i++) {
			MultiImgTableRow row = tableModel.getRowList().get(i);
			if(row.isUseRange()) {
				row.applyToBinaryMap(bmap,counter);
				counter++;
			}
		}

		return bmap;
	}

	//###################################################################
	// getters and setters
	public JSplitPane getSplit() {
		return split;
	}
	public JPanel getPnGridImg() {
		return pnGridImg;
	}
	public JTable getTable() {
		return table;
	}
	public JCheckBox getCbKeepAspectRatio() {
		return cbKeepAspectRatio;
	}
	public JSpinner getSpinner() {
		return spinnerColumns;
	}
	public JCheckBox getCbShowAxes() {
		return cbShowAxes;
	}
	public JCheckBox getCbShowTitles() {
		return cbShowTitles;
	}
	/**
	 * more than one range selected?
	 * @return
	 */
	public boolean isBinaryMapAvailable() {
		boolean one = false;
		for(int i=0; i<tableModel.getRowList().size(); i++) {
			MultiImgTableRow row = tableModel.getRowList().get(i);
			if(row.isUseRange() && one)
				return true;
			if(row.isUseRange())
				one = true;
		}
		return false;
	}
}
