package net.rs.lamsi.massimager.Frames.Dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Vector;

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
import net.rs.lamsi.massimager.Frames.Dialogs.generalsettings.interfaces.SettingsPanel;
import net.rs.lamsi.massimager.Frames.FrameWork.JColorPickerButton;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.Module;
import net.rs.lamsi.massimager.Heatmap.Heatmap;
import net.rs.lamsi.massimager.Heatmap.HeatmapFactory;
import net.rs.lamsi.massimager.Image.Image2D;
import net.rs.lamsi.massimager.MyFileChooser.FileTypeFilter;
import net.rs.lamsi.massimager.MyFreeChart.Plot.PlotChartPanel;
import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.massimager.Settings.SettingsHolder;
import net.rs.lamsi.massimager.Settings.image.SettingsExportGraphics;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow.LOG;
import net.rs.lamsi.utils.ChartExportUtil;
import net.rs.lamsi.utils.DialogLoggerUtil;
import net.rs.lamsi.utils.FileAndPathUtil;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

public class GraphicsExportDialog extends JFrame implements SettingsPanel {

	// only one instance!
	private static GraphicsExportDialog inst;
	//
	private final JPanel contentPanel = new JPanel();
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
	private ChartPanel chartPanel;
	private JFreeChart chart; 
	private Vector<Image2D> list;
	private boolean canExport;
	private final JFileChooser chooser = new JFileChooser();
	private JPanel pnChartPreview;
	private JRadioButton rbSVG;
	private JRadioButton rbEPS;
	private JRadioButton rbJPG;
	private JCheckBox cbExportAll;
	
	
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
	public static void openDialog2(JFreeChart chart) {
		inst.openDialogI(chart, null); 
	}
	public static void openDialog(JFreeChart chart, Vector<Image2D> list) {
		inst.openDialogI(chart, list); 
	}
	protected void openDialogI(JFreeChart chart, Vector<Image2D> list) {
		inst.chart = chart; 
		inst.list = list;
		setVisible(true);
		//
		addChartToPanel(new PlotChartPanel(chart));
	}
	
	protected void addChartToPanel(ChartPanel chart) { 
		//
		chartPanel = chart;
		getPnChartPreview().removeAll();
		getPnChartPreview().add(chartPanel, BorderLayout.CENTER);
		getPnChartPreview().validate();
		getPnChartPreview().repaint();
	}
	

	/**
	 * choose a path by file chooser
	 */
	protected void choosePath() {
		// open filechooser  
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();  
    		// only a folder? or also a file name > then split
            if(FileAndPathUtil.isOnlyAFolder(file)) {
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
            }
        } 
	}
	
	
	private void saveGraphicsAs() {
		setAllSettings(SettingsHolder.getSettings());
		//
		if(canExport) {
			SettingsExportGraphics sett = (SettingsExportGraphics) getSettings(SettingsHolder.getSettings());
			try {
				if(getCbExportAll().isSelected() && list!=null) {
					File path = sett.getPath();
					String fileName = sett.getFileName(); 
					// import all
					for(Image2D img : list) { 
						try {
							// create chart
							Heatmap heat = HeatmapFactory.generateHeatmap(img);
							// TODO maybe you have to put it on the chartpanel and show it? 
							addChartToPanel(heat.getChartPanel());
							// set the name and path 
							String sub = FileAndPathUtil.eraseFormat(img.getSettImage().getRAWFileName());
							sett.setPath(new File(path,sub));
							// title as filename
							sett.setFileName(fileName+img.getTitle());
							// export
							ChartExportUtil.writeChartToImage(heat.getChartPanel(), sett);
						} catch(Exception ex) {
							ImageEditorWindow.log("FIle: "+sett.getFileName()+" is not saveable. \n"+ex.getMessage(), LOG.ERROR);
						}
					}
				}
				else ChartExportUtil.writeChartToImage(chartPanel, sett);
				// 
				ImageEditorWindow.log("File written successfully", LOG.MESSAGE);
				DialogLoggerUtil.showMessageDialogForTime(this, "Information", "File written successfully ", 1000);
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
			int format = SettingsExportGraphics.FORMAT_PDF;
			if(getRbSVG().isSelected()) format = SettingsExportGraphics.FORMAT_SVG;
			else if(getRbEPS().isSelected()) format = SettingsExportGraphics.FORMAT_EPS;
			else if(getRbPNG().isSelected()) format = SettingsExportGraphics.FORMAT_PNG;
			else if(getRbJPG().isSelected()) format = SettingsExportGraphics.FORMAT_JPG;
			sett.setFormat(format);

			// Resolution
			if(getRbForPrintRes().isSelected()) 
				sett.setResolution(300); 
			else if(getRbForPresentationRes().isSelected()) 
				sett.setResolution(72);
			else 
				sett.setResolution(Integer.valueOf(getTxtManualRes().getText()));
			
			// Size
			float width = Float.valueOf(getTxtWidth().getText());
			float height = Float.valueOf(getTxtHeight().getText());
			int unit = getComboSizeUnit().getSelectedIndex();
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
		getTxtWidth().setText(""+sett.getSize().width);
		getTxtHeight().setText(""+sett.getSize().height); 
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
		// create fileCooser 
		chooser.addChoosableFileFilter(new FileTypeFilter("png", "Save to png")); 
		chooser.addChoosableFileFilter(new FileTypeFilter("pdf", "Save to pdf")); 
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		//
		setBounds(100, 100, 800, 627);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[grow][][][grow]", "[][][][grow]"));
		{
			txtPath = new JTextField();
			txtPath.setToolTipText("Path without filename");
			contentPanel.add(txtPath, "cell 0 0 2 1,growx");
			txtPath.setColumns(10);
		}
		{
			btnPath = new JButton("Path");
			btnPath.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					choosePath();
				}
			});
			contentPanel.add(btnPath, "cell 2 0");
		}
		{
			txtFileName = new JTextField();
			contentPanel.add(txtFileName, "cell 0 1 2 1,growx");
			txtFileName.setColumns(10);
		}
		{
			JLabel lblFilename = new JLabel("filename");
			contentPanel.add(lblFilename, "cell 2 1");
		}
		{
			cbExportAll = new JCheckBox("Export all in list");
			cbExportAll.setToolTipText("Exports all in the list.");
			contentPanel.add(cbExportAll, "cell 0 2");
		}
		{
			JPanel pnSettingsLeft = new JPanel();
			pnSettingsLeft.setMinimumSize(new Dimension(260, 260));
			contentPanel.add(pnSettingsLeft, "cell 0 3 3 1,grow");
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
							panel_2.setLayout(new MigLayout("", "[][][]", "[][]"));
							{
								rbPDF = new JRadioButton("PDF");
								buttonGroup.add(rbPDF);
								rbPDF.setSelected(true);
								panel_2.add(rbPDF, "cell 0 0");
							}
							{
								rbSVG = new JRadioButton("SVG");
								buttonGroup.add(rbSVG);
								panel_2.add(rbSVG, "cell 1 0");
							}
							{
								rbEPS = new JRadioButton("EPS");
								buttonGroup.add(rbEPS);
								panel_2.add(rbEPS, "cell 2 0");
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
							panel_1.setLayout(new MigLayout("", "[][][grow]", "[][][][][][][]"));
							{
								JLabel lblWidth = new JLabel("Width");
								panel_1.add(lblWidth, "flowx,cell 0 0");
							}
							{
								comboSizeUnit = new JComboBox();
								comboSizeUnit.setModel(new DefaultComboBoxModel(new String[] {"cm", "mm", "pt", "inch", "px"}));
								panel_1.add(comboSizeUnit, "cell 1 0,growx");
							}
							{
								JLabel lblHeight = new JLabel("Height");
								panel_1.add(lblHeight, "flowx,cell 0 1,alignx left");
							}
							{
								cbOnlyUseWidth = new JCheckBox("Only use width");
								cbOnlyUseWidth.addItemListener(new ItemListener() {
									public void itemStateChanged(ItemEvent e) {
										JCheckBox cb = (JCheckBox) e.getSource();
										if(getTxtHeight()!=null) 
											getTxtHeight().setEnabled(!cb.isSelected());
									}
								});
								cbOnlyUseWidth.setSelected(true);
								panel_1.add(cbOnlyUseWidth, "cell 0 2");
							}
							{
								rbManual = new JRadioButton("manual");
								buttonGroup_1.add(rbManual);
								rbManual.setSelected(true);
								panel_1.add(rbManual, "flowx,cell 0 4");
							}
							{
								rbForPrintRes = new JRadioButton("for print (300 dpi)");
								buttonGroup_1.add(rbForPrintRes);
								panel_1.add(rbForPrintRes, "cell 0 5");
							}
							{
								rbForPresentationRes = new JRadioButton("for presentation (72 dpi)");
								buttonGroup_1.add(rbForPresentationRes);
								panel_1.add(rbForPresentationRes, "cell 0 6");
							}
							{
								txtWidth = new JTextField();
								txtWidth.setText("15");
								panel_1.add(txtWidth, "cell 0 0,alignx trailing");
								txtWidth.setColumns(10);
							}
							{
								txtManualRes = new JTextField();
								txtManualRes.setToolTipText("Resolution in dpi");
								txtManualRes.setText("300");
								panel_1.add(txtManualRes, "cell 0 4");
								txtManualRes.setColumns(10);
							}
							{
								txtHeight = new JTextField();
								txtHeight.setEnabled(false);
								txtHeight.setText("8");
								panel_1.add(txtHeight, "cell 0 1");
								txtHeight.setColumns(10);
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
							}
						}
					}
				}
			}
		}
		{
			pnChartPreview = new JPanel();
			contentPanel.add(pnChartPreview, "cell 3 3,grow");
			pnChartPreview.setLayout(new BorderLayout(0, 0));
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
						repaint();
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
	public JCheckBox getCbExportAll() {
		return cbExportAll;
	}
}
