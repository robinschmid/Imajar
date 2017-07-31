package net.rs.lamsi.general.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.general.datamodel.image.ImageGroupMD;
import net.rs.lamsi.general.datamodel.image.ImagingProject;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.framework.basics.JColorPickerButton;
import net.rs.lamsi.general.framework.modules.Module;
import net.rs.lamsi.general.heatmap.Heatmap;
import net.rs.lamsi.general.heatmap.HeatmapFactory;
import net.rs.lamsi.general.myfreechart.ChartLogics;
import net.rs.lamsi.general.myfreechart.Plot.PlotChartPanel;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.general.settings.SettingsHolder;
import net.rs.lamsi.general.settings.importexport.SettingsExportGraphics;
import net.rs.lamsi.general.settings.importexport.SettingsExportGraphics.FIXED_SIZE;
import net.rs.lamsi.general.settings.importexport.SettingsExportGraphics.FORMAT;
import net.rs.lamsi.general.settings.importexport.SettingsImageResolution;
import net.rs.lamsi.general.settings.importexport.SettingsImageResolution.DIM_UNIT;
import net.rs.lamsi.massimager.Frames.Dialogs.generalsettings.interfaces.SettingsPanel;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow.LOG;
import net.rs.lamsi.utils.ChartExportUtil;
import net.rs.lamsi.utils.DialogLoggerUtil;
import net.rs.lamsi.utils.FileAndPathUtil;
import net.rs.lamsi.utils.threads.ProgressUpdateTask;
import net.rs.lamsi.utils.useful.dialogs.ProgressDialog;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.FloatDimension;

public class GraphicsExportDialog extends JFrame implements SettingsPanel {

	// only one instance!
	private static GraphicsExportDialog inst;
	//
	protected final JPanel contentPanel = new JPanel();
	private JTextField txtPath;
	private JTextField txtFileName;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private final ButtonGroup buttonGroup_1 = new ButtonGroup();
	private JTextField txtWidth;
	private JTextField txtHeight;
	private JTextField txtManualRes;
	private final ButtonGroup buttonGroup_2 = new ButtonGroup();
	private JRadioButton rbPDF;
	private JRadioButton rbPNG;
	private JCheckBox cbOnlyUseWidth;
	private JComboBox comboSizeUnit;
	private JRadioButton rbManual;
	private JRadioButton rbForPrintRes;
	private JRadioButton rbForPresentationRes;
	private JRadioButton rbTransparent;
	private JRadioButton rbWhite;
	private JRadioButton rbBlack;
	private JRadioButton rbColor;
	private JColorPickerButton btnChooseBackgroundColor;
	private JButton btnPath;
	private JButton btnRenewPreview;



	//###################################################################
	// Vars
	protected ChartPanel chartPanel;

	private boolean canExport;
	private JPanel pnChartPreview;
	private JRadioButton rbSVG;
	private JRadioButton rbEPS;
	private JRadioButton rbJPG;
	private JPanel panel;
	private JRadioButton rbEmf;
	private JPanel panel_3;
	private JLabel lblSizeFor;
	private JComboBox comboWidthPlotChart;


	//###################################################################
	// create instance in window and imageeditor 
	public static GraphicsExportDialog createInstance() {
		if(inst==null) {
			inst = new GraphicsExportDialog();
		}
		return inst;
	}
	public static GraphicsExportDialog getInst() {
		return inst;
	}

	//###################################################################
	// get Settings
	/**
	 * OPen Dialog with chart
	 * @param chart
	 */
	public static void openDialog(JFreeChart chart) {
		inst.openDialogI(chart); 
	}
	protected void openDialogI(JFreeChart chart) {
		try {
			addChartToPanel(new PlotChartPanel((JFreeChart) chart.clone()), true);
			setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void addChartToPanel(ChartPanel chart, boolean renew) { 
		//
		chartPanel = chart;
		getPnChartPreview().removeAll();
		getPnChartPreview().add(chartPanel);
		if(renew)
			renewPreview();
	}


	protected void renewPreview() {
		// set dimensions to chartpanel
		// set height
		try {
			setAllSettings(SettingsHolder.getSettings());
			SettingsExportGraphics sett = (SettingsExportGraphics) getSettings(SettingsHolder.getSettings());

			DecimalFormat form = new DecimalFormat("#.###");
			if(sett.isUseOnlyWidth()) {
				double height = (ChartLogics.calcHeightToWidth(chartPanel, sett.getSize().getWidth(), false));
				
				sett.setSize((Float.valueOf(getTxtWidth().getText())), SettingsImageResolution.changeUnit((float)height, DIM_UNIT.PX, sett.getUnit()), sett.getUnit());
				getTxtHeight().setText(""+form.format(sett.getSizeInUnit().getHeight())); 
				
				chartPanel.setSize(sett.getSize());
				getPnChartPreview().repaint();
			}
			else {
				chartPanel.setSize((int)sett.getSize().getWidth(), (int)sett.getSize().getHeight());
				chartPanel.repaint();
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * choose a path by file chooser
	 */
	protected void choosePath() {
		// open filechooser  
		JFileChooser chooser = SettingsHolder.getSettings().getSetGeneralPreferences().getFcExportPicture();
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();  
			// only a folder? or also a file name > then split
			if(file.isDirectory()) {
				// only a folder
				getTxtPath().setText(file.getAbsolutePath()); 
			}
			else {
				// data file selected
				// get folder
				getTxtPath().setText(FileAndPathUtil.getFileAsFolder(file).getAbsolutePath());
				// get filename
				getTxtFileName().setText(FileAndPathUtil.getFileNameFromPath(file));            	
				// get format without .
				String format = FileAndPathUtil.getFormat(file);
				if(format.equalsIgnoreCase("pdf")) {
					getRbPDF().setSelected(true);
				}
				else if(format.equalsIgnoreCase("png")) {
					getRbPNG().setSelected(true);
				} 
				else if(format.equalsIgnoreCase("jpg")) {
					getRbJPG().setSelected(true);
				}
				else if(format.equalsIgnoreCase("eps")) {
					getRbEPS().setSelected(true);
				}
				else if(format.equalsIgnoreCase("svg")) {
					getRbSVG().setSelected(true);
				}
				else if(format.equalsIgnoreCase("emf")) {
					getRbEmf().setSelected(true);
				}
			}
		} 
	}

	protected void saveGraphicsAs() {
		setAllSettings(SettingsHolder.getSettings());
		//
		if(canExport) {
			final SettingsExportGraphics sett = (SettingsExportGraphics) getSettings(SettingsHolder.getSettings());
			try {
					ImageEditorWindow.log("Writing image to file: "+sett.getFullFilePath(), LOG.MESSAGE);
					ChartExportUtil.writeChartToImage(chartPanel, sett);
			} catch (Exception e) {
				e.printStackTrace();
				ImageEditorWindow.log("File not written.", LOG.ERROR);
				DialogLoggerUtil.showErrorDialog(this, "File not written. ", e);
			}
		}
	}  


	@Override
	public void setAllSettings(SettingsHolder settings) { 
		SettingsExportGraphics sett = (SettingsExportGraphics) getSettings(settings);
		// all set right?
		canExport = false;
		//
		try {
			// FilePath and name
			sett.setFileName(getTxtFileName().getText());
			sett.setPath(new File(getTxtPath().getText()));
			// Format
			FORMAT format = SettingsExportGraphics.FORMAT.PDF;
			if(getRbSVG().isSelected()) format = SettingsExportGraphics.FORMAT.SVG;
			else if(getRbEPS().isSelected()) format = SettingsExportGraphics.FORMAT.EPS;
			else if(getRbPNG().isSelected()) format = SettingsExportGraphics.FORMAT.PNG;
			else if(getRbJPG().isSelected()) format = SettingsExportGraphics.FORMAT.JPG;
			else if(getRbEmf().isSelected()) format = SettingsExportGraphics.FORMAT.EMF;
			sett.setFormat(format);

			// Resolution
			if(getRbForPrintRes().isSelected()) 
				sett.setResolution(300); 
			else if(getRbForPresentationRes().isSelected()) 
				sett.setResolution(72);
			else 
				sett.setResolution(Integer.valueOf(getTxtManualRes().getText()));
			
			// fixed size for chart or plot
			sett.setFixedSize(getComboWidthPlotChart().getSelectedItem().equals("Plot")? FIXED_SIZE.PLOT : FIXED_SIZE.CHART);

			// Size
			float width = Float.valueOf(getTxtWidth().getText());
			float height = Float.valueOf(getTxtHeight().getText());
			DIM_UNIT unit = (DIM_UNIT)getComboSizeUnit().getSelectedItem();
			sett.setSize(width, height, unit); 
			sett.setUseOnlyWidth(getCbOnlyUseWidth().isSelected());

			// Background
			if(getRbTransparent().isSelected())
				sett.setColorBackground(new Color(255, 255, 255, 0));
			else if(getRbBlack().isSelected())
				sett.setColorBackground(new Color(0,0,0,255));
			else if(getRbWhite().isSelected())
				sett.setColorBackground(Color.WHITE);
			else 
				sett.setColorBackground(getBtnChooseBackgroundColor().getBackground());

			// is everything set right?
			canExport = (sett.getPath()!=null && sett.getFileName().length()>0 && width>0 && height>0);
		} catch(Exception ex) {
			canExport = false;
		}
	}

	@Override
	public void setAllSettingsOnPanel(SettingsHolder settings) { 
		SettingsExportGraphics sett = (SettingsExportGraphics) getSettings(settings);
		// set to panel TODO
		getTxtFileName().setText(sett.getFileName());
		getTxtPath().setText(sett.getPath().getAbsolutePath());
		getTxtManualRes().setText(""+sett.getResolution());
		getComboSizeUnit().setSelectedItem(sett.getUnit());
		DecimalFormat form = new DecimalFormat("#.###");
		FloatDimension size = sett.getSizeInUnit();
		getTxtWidth().setText(""+form.format(size.getWidth()));
		getTxtHeight().setText(""+form.format(size.getHeight())); 
		
		getComboWidthPlotChart().setSelectedItem(sett.getFixedSize().equals(FIXED_SIZE.PLOT)? "Plot" : "Chart");
		
		// not everything set ! TODO cb rb combo
		getCbOnlyUseWidth().setSelected(sett.isUseOnlyWidth());
	}

	@Override
	public Settings getSettings(SettingsHolder settings) { 
		return settings.getSetGraphicsExport();
	} 
	// 
	//###################################################################

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			GraphicsExportDialog dialog = new GraphicsExportDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public GraphicsExportDialog() {
		final JFrame thisframe = this;
		//
		//
		setBounds(100, 100, 808, 795);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[][][grow]", "[][][][][grow]"));
		{
			txtPath = new JTextField();
			txtPath.setToolTipText("Path without filename");
			contentPanel.add(txtPath, "flowx,cell 0 0,growx");
			txtPath.setColumns(10);
		}
		{
			btnPath = new JButton("Path");
			btnPath.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					choosePath();
				}
			});
			contentPanel.add(btnPath, "cell 1 0");
		}
		{
			txtFileName = new JTextField();
			contentPanel.add(txtFileName, "cell 0 1,growx");
			txtFileName.setColumns(10);
		}
		{
			JLabel lblFilename = new JLabel("filename");
			contentPanel.add(lblFilename, "cell 1 1");
		}
		{
			JPanel pnSettingsLeft = new JPanel();
			pnSettingsLeft.setMinimumSize(new Dimension(260, 260));
			contentPanel.add(pnSettingsLeft, "cell 0 4,grow");
			pnSettingsLeft.setLayout(new BorderLayout(0, 0));
			{
				JScrollPane scrollPane = new JScrollPane();
				pnSettingsLeft.add(scrollPane, BorderLayout.CENTER);
				{
					JPanel grid = new JPanel();
					scrollPane.setViewportView(grid);
					grid.setLayout(new BoxLayout(grid, BoxLayout.Y_AXIS));
					{
						Module panel_1 = new Module("Format");
						grid.add(panel_1);
						{
							JPanel panel_2 = new JPanel();
							panel_1.getPnContent().add(panel_2, BorderLayout.CENTER);
							panel_2.setLayout(new MigLayout("", "[][][][]", "[][]"));
							{
								rbPDF = new JRadioButton("PDF");
								buttonGroup.add(rbPDF);
								rbPDF.setSelected(true);
								panel_2.add(rbPDF, "cell 0 0");
							}
							{
								rbEmf = new JRadioButton("EMF");
								buttonGroup.add(rbEmf);
								rbEmf.setToolTipText("Enhanced metafile (for Microsoft Office)");
								panel_2.add(rbEmf, "cell 1 0");
							}
							{
								rbEPS = new JRadioButton("EPS");
								buttonGroup.add(rbEPS);
								panel_2.add(rbEPS, "cell 2 0");
							}
							{
								rbSVG = new JRadioButton("SVG");
								buttonGroup.add(rbSVG);
								panel_2.add(rbSVG, "cell 3 0");
							}
							{
								rbPNG = new JRadioButton("PNG");
								buttonGroup.add(rbPNG);
								panel_2.add(rbPNG, "cell 0 1");
							}
							{
								rbJPG = new JRadioButton("JPG");
								buttonGroup.add(rbJPG);
								panel_2.add(rbJPG, "cell 1 1");
							}
						}
					}
					{
						Module module = new Module("Resolution");
						grid.add(module);
						{
							JPanel panel_1 = new JPanel();
							module.getPnContent().add(panel_1, BorderLayout.CENTER);
							panel_1.setLayout(new MigLayout("", "[][][][grow]", "[][][][][][][][]"));
							{
								lblSizeFor = new JLabel("Fixed size for");
								lblSizeFor.setFont(new Font("Tahoma", Font.BOLD, 12));
								panel_1.add(lblSizeFor, "flowx,cell 0 0");
							}
							{
								comboWidthPlotChart = new JComboBox();
								comboWidthPlotChart.setToolTipText("A fixed size can either be set for the plot (image region) or the whole chart.");
								comboWidthPlotChart.setModel(new DefaultComboBoxModel(new String[] {"Plot", "Chart"}));
								panel_1.add(comboWidthPlotChart, "cell 1 0");
							}
							{
								JLabel lblWidth = new JLabel("Width");
								panel_1.add(lblWidth, "cell 0 1,alignx right");
							}
							{
								{
									JLabel lblHeight = new JLabel("Height");
									panel_1.add(lblHeight, "cell 0 2,alignx right");
								}
							}
							cbOnlyUseWidth = new JCheckBox("Only use width");
							cbOnlyUseWidth.addItemListener(new ItemListener() {
								public void itemStateChanged(ItemEvent e) {
									JCheckBox cb = (JCheckBox) e.getSource();
									if(getTxtHeight()!=null) 
										getTxtHeight().setEnabled(!cb.isSelected());
								}
							});
							cbOnlyUseWidth.setSelected(true);
							panel_1.add(cbOnlyUseWidth, "cell 1 3");
							{
								rbManual = new JRadioButton("manual");
								buttonGroup_1.add(rbManual);
								rbManual.setSelected(true);
								panel_1.add(rbManual, "cell 0 5");
							}
							{
								rbForPrintRes = new JRadioButton("for print (300 dpi)");
								buttonGroup_1.add(rbForPrintRes);
								panel_1.add(rbForPrintRes, "cell 0 6 2 1");
							}
							{
								txtWidth = new JTextField();
								txtWidth.setText("15");
								panel_1.add(txtWidth, "flowx,cell 1 1,alignx left");
								txtWidth.setColumns(10);
							}
							{
								txtManualRes = new JTextField();
								txtManualRes.setToolTipText("Resolution in dpi");
								txtManualRes.setText("300");
								panel_1.add(txtManualRes, "cell 1 5");
								txtManualRes.setColumns(10);
							}
							{
								txtHeight = new JTextField();
								txtHeight.setEnabled(false);
								txtHeight.setText("8");
								panel_1.add(txtHeight, "cell 1 2");
								txtHeight.setColumns(10);
							}
							{
								comboSizeUnit = new JComboBox();
								comboSizeUnit.setModel(new DefaultComboBoxModel(DIM_UNIT.values()));
								panel_1.add(comboSizeUnit, "cell 1 1,growx");
							}
							{
								rbForPresentationRes = new JRadioButton("for presentation (72 dpi)");
								buttonGroup_1.add(rbForPresentationRes);
								panel_1.add(rbForPresentationRes, "cell 0 7 2 1");
							}
						}
					}
					{
						Module module = new Module("Background");
						grid.add(module);
						{
							JPanel panel_1 = new JPanel();
							module.getPnContent().add(panel_1, BorderLayout.SOUTH);
							panel_1.setLayout(new MigLayout("", "[]", "[][][][]"));
							{
								rbTransparent = new JRadioButton("Transparent");
								buttonGroup_2.add(rbTransparent);
								rbTransparent.setSelected(true);
								panel_1.add(rbTransparent, "cell 0 0");
							}
							{
								rbWhite = new JRadioButton("White");
								buttonGroup_2.add(rbWhite);
								panel_1.add(rbWhite, "cell 0 1");
							}
							{
								rbBlack = new JRadioButton("Black");
								buttonGroup_2.add(rbBlack);
								panel_1.add(rbBlack, "cell 0 2");
							}
							{
								rbColor = new JRadioButton("Color");
								buttonGroup_2.add(rbColor);
								panel_1.add(rbColor, "flowx,cell 0 3");
							}
							{
								btnChooseBackgroundColor = new JColorPickerButton(thisframe);  
								btnChooseBackgroundColor.setPreferredSize(new Dimension(25, 25));
								panel_1.add(btnChooseBackgroundColor, "cell 0 3");
								btnChooseBackgroundColor.setColor(Color.BLUE);
							}
						}
					}
					{
						panel_3 = new JPanel();
						grid.add(panel_3);
						panel_3.setLayout(new BorderLayout(0, 0));
						
						JPanel panel_1 = new JPanel();
						panel_3.add(panel_1, BorderLayout.CENTER);
						panel_1.setLayout(new MigLayout("", "[]", "[]"));
					}
				}
			}
		}
		{
			{
				pnChartPreview = new JPanel();
				pnChartPreview.setLayout(null);
				contentPanel.add(pnChartPreview, "cell 1 4 2 1,grow");
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Save");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// Save as
						saveGraphicsAs();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				btnRenewPreview = new JButton("Renew Preview");
				btnRenewPreview.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						renewPreview();
					}
				});
				buttonPane.add(btnRenewPreview);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	public JRadioButton getRbPDF() {
		return rbPDF;
	}
	public JRadioButton getRbPNG() {
		return rbPNG;
	}
	public JCheckBox getCbOnlyUseWidth() {
		return cbOnlyUseWidth;
	}
	public JTextField getTxtWidth() {
		return txtWidth;
	}
	public JTextField getTxtHeight() {
		return txtHeight;
	}
	public JComboBox getComboSizeUnit() {
		return comboSizeUnit;
	}
	public JRadioButton getRbManual() {
		return rbManual;
	}
	public JTextField getTxtManualRes() {
		return txtManualRes;
	}
	public JRadioButton getRbForPrintRes() {
		return rbForPrintRes;
	}
	public JRadioButton getRbForPresentationRes() {
		return rbForPresentationRes;
	}
	public JRadioButton getRbTransparent() {
		return rbTransparent;
	}
	public JRadioButton getRbWhite() {
		return rbWhite;
	}
	public JRadioButton getRbBlack() {
		return rbBlack;
	}
	public JRadioButton getRbColor() {
		return rbColor;
	}
	public JButton getBtnChooseBackgroundColor() {
		return btnChooseBackgroundColor;
	}
	public JTextField getTxtPath() {
		return txtPath;
	}
	public JTextField getTxtFileName() {
		return txtFileName;
	}
	public JButton getBtnPath() {
		return btnPath;
	}
	public JPanel getPnChartPreview() {
		return pnChartPreview;
	}
	public JRadioButton getRbSVG() {
		return rbSVG;
	}
	public JRadioButton getRbJPG() {
		return rbJPG;
	}
	public JRadioButton getRbEPS() {
		return rbEPS;
	}
	public JRadioButton getRbEmf() {
		return rbEmf;
	}
	public JComboBox getComboWidthPlotChart() {
		return comboWidthPlotChart;
	}
}
