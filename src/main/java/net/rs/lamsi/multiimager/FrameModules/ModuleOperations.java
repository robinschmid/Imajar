package net.rs.lamsi.multiimager.FrameModules;


import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;

import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.massimager.Frames.FrameWork.ColorChangedListener;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.ImageSettingsModule;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.Module;
import net.rs.lamsi.massimager.Settings.image.operations.SettingsImage2DOperations;
import net.rs.lamsi.massimager.Settings.image.operations.quantifier.SettingsImage2DBlankSubtraction;
import net.rs.lamsi.massimager.Settings.image.operations.quantifier.SettingsImage2DQuantifier;
import net.rs.lamsi.massimager.Settings.image.operations.quantifier.SettingsImage2DQuantifierIS;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageLogicRunner;
import net.rs.lamsi.utils.DialogLoggerUtil;

import java.awt.event.ActionEvent;

import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JTabbedPane;
import javax.swing.JSpinner;
import javax.swing.JSlider;

import java.awt.Dimension;
import java.awt.event.ItemEvent;

import javax.swing.event.ChangeEvent;

public class ModuleOperations extends ImageSettingsModule<SettingsImage2DOperations> { 
	//data mode: img or start of this image
	private int lastModeBlank = 0;
	protected Image2D imgBlank, imgIS;
	//
	private ImageEditorWindow window; 
	private ModuleQuantifyStrategy modQuantifier;
	private ModuleSelectExcludeData modSelectExcludeData;
	private JTextField txtBlankTitle, txtISTitle;
	private JTextField txtBlankPath, txtISPath;
	private JCheckBox cbUseBlank, cbUseIS;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JRadioButton rbBlankActualDataPoint;
	private JRadioButton rbBlankAverage;
	private JLabel lblF;
	private JTextField txtISFactor;
	private JRadioButton rbBlankAveragePerLine;
	private JPanel pn;
	private JTabbedPane tabbedBlank;
	private JPanel tabBlankImage;
	private JPanel tabBlankBothSides;
	private JCheckBox cbBlankBothSides;
	private JLabel lblLowerBound;
	private JSlider sliderBlankLowerB;
	private JLabel lblUpperBound;
	private JSlider sliderBlankUpperB;
	private JCheckBox chckbxShowInChart;
	private JTextField txtBlankStart;
	private JTextField txtBlankUpperB;
	private JPanel north; 

	/**
	 * Create the panel.
	 */
	public ModuleOperations(ImageEditorWindow wnd) {
		super("Operations", false, SettingsImage2DOperations.class);

		window = wnd;
		modQuantifier = new ModuleQuantifyStrategy(wnd);
		modQuantifier.setShowTitleAlways(true);
		getPnContent().add(modQuantifier, BorderLayout.SOUTH);
		
		// same with IS
		Module modIS = new Module();
		modIS.setShowTitleAlways(true);
		modIS.setTitle("IS");
		getPnContent().add(modIS, BorderLayout.CENTER);
		
		JPanel pnISContent = new JPanel();
		modIS.getPnContent().add(pnISContent, BorderLayout.NORTH);
		pnISContent.setLayout(new MigLayout("", "[][grow][grow]", "[][][][][][][]"));
		
		cbUseIS = new JCheckBox("Use IS");
		pnISContent.add(cbUseIS, "cell 0 0 2 1"); 
		
		lblF = new JLabel("f = ");
		pnISContent.add(lblF, "cell 0 1,alignx trailing");
		
		txtISFactor = new JTextField();
		txtISFactor.setText("1");
		pnISContent.add(txtISFactor, "cell 1 1,growx");
		txtISFactor.setColumns(10);
		
		JLabel lblTitle = new JLabel("Title");
		pnISContent.add(lblTitle, "cell 0 2,alignx trailing");
		
		txtISTitle = new JTextField();
		pnISContent.add(txtISTitle, "cell 1 2 2 1,growx");
		txtISTitle.setColumns(10);
		
		JLabel lblPath = new JLabel("Path");
		pnISContent.add(lblPath, "cell 0 3,alignx trailing");
		
		txtISPath = new JTextField();
		pnISContent.add(txtISPath, "cell 1 3 2 1,growx");
		txtISPath.setColumns(10);
		
		JButton btnChooseFromList = new JButton("Choose from current list");
		btnChooseFromList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				Object[] list = window.getLogicRunner().getListImages().toArray(); 
				try {
					int i = DialogLoggerUtil.showListDialogAndChoose(window, list, ListSelectionModel.SINGLE_SELECTION)[0];
					setIS(((Image2D)list[i]));
				} catch(Exception ex) { 
					setIS(null);
				}
			}
		});
		pnISContent.add(btnChooseFromList, "cell 1 4,growx");
		
		JButton btnChooseFromRaw = new JButton("Choose from raw data");
		pnISContent.add(btnChooseFromRaw, "cell 1 5,growx");
		
		JButton btnChooseFromImaged = new JButton("Choose from image2d");
		pnISContent.add(btnChooseFromImaged, "cell 1 6,growx");
		
		north = new JPanel();
		getPnContent().add(north, BorderLayout.NORTH);
		north.setLayout(new BorderLayout(0, 0));
		
		Module modBlank = new Module("Blank");
		north.add(modBlank, BorderLayout.CENTER); 
		modBlank.setShowTitleAlways(true);
		
		JPanel pnBlankContent = new JPanel();
		modBlank.getPnContent().add(pnBlankContent, BorderLayout.NORTH);
		pnBlankContent.setLayout(new MigLayout("", "[grow]", "[][][][][grow][]"));
		
		cbUseBlank = new JCheckBox("Use blank");
		cbUseBlank.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(currentHeat!=null) {
					currentHeat.showBlankMinMax((cbUseBlank.isSelected()));
				}
			}
		});
		pnBlankContent.add(cbUseBlank, "cell 0 0");
		
		rbBlankAverage = new JRadioButton("Average");
		buttonGroup.add(rbBlankAverage);
		pnBlankContent.add(rbBlankAverage, "cell 0 1");
		
		rbBlankAveragePerLine = new JRadioButton("Average per line");
		buttonGroup.add(rbBlankAveragePerLine);
		pnBlankContent.add(rbBlankAveragePerLine, "cell 0 2");
		
		rbBlankActualDataPoint = new JRadioButton("Actual data point");
		buttonGroup.add(rbBlankActualDataPoint);
		rbBlankActualDataPoint.setSelected(true);
		pnBlankContent.add(rbBlankActualDataPoint, "cell 0 3");
		
		pn = new JPanel();
		pnBlankContent.add(pn, "cell 0 4,grow");
		pn.setLayout(new BorderLayout(0, 0));
		
		tabbedBlank = new JTabbedPane(JTabbedPane.TOP); 
		pn.add(tabbedBlank, BorderLayout.CENTER);
		
		tabBlankImage = new JPanel();
		tabbedBlank.addTab("Blank img", null, tabBlankImage, null);
		tabBlankImage.setLayout(new MigLayout("", "[][][grow]", "[][][][][]"));
		
		lblTitle = new JLabel("Title");
		tabBlankImage.add(lblTitle, "cell 0 0");
		
		txtBlankTitle = new JTextField();
		tabBlankImage.add(txtBlankTitle, "cell 1 0 2 1,growx");
		txtBlankTitle.setColumns(10);
		
		lblPath = new JLabel("Path");
		tabBlankImage.add(lblPath, "cell 0 1");
		
		txtBlankPath = new JTextField();
		tabBlankImage.add(txtBlankPath, "cell 1 1 2 1,growx");
		txtBlankPath.setColumns(10);
		
		btnChooseFromList = new JButton("Choose from current list");
		tabBlankImage.add(btnChooseFromList, "cell 1 2");
		
		btnChooseFromRaw = new JButton("Choose from raw data");
		tabBlankImage.add(btnChooseFromRaw, "cell 1 3,growx");
		
		btnChooseFromImaged = new JButton("Choose from image2d");
		tabBlankImage.add(btnChooseFromImaged, "cell 1 4,growx");
		btnChooseFromList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				Object[] list = window.getLogicRunner().getListImages().toArray(); 
				try {
					int i = DialogLoggerUtil.showListDialogAndChoose(window, list, ListSelectionModel.SINGLE_SELECTION)[0];
					setBlank(((Image2D)list[i]));
				} catch(Exception ex) { 
					setBlank(null);
				}
			}
		});
		
		tabBlankBothSides = new JPanel();
		tabbedBlank.addTab("Starting with blank", null, tabBlankBothSides, null);
		tabBlankBothSides.setLayout(new MigLayout("", "[][][][]", "[][][][][]"));
		
		cbBlankBothSides = new JCheckBox("Use both sides");
		cbBlankBothSides.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				getSliderBlankUpperB().setEnabled(((JCheckBox)e.getSource()).isSelected());
			}
		});
		cbBlankBothSides.setToolTipText("Uses the data at the start and at the end to do blank reduction");
		tabBlankBothSides.add(cbBlankBothSides, "flowx,cell 0 0 3 1");
		
		chckbxShowInChart = new JCheckBox("Show in chart");
		tabBlankBothSides.add(chckbxShowInChart, "cell 3 0");
		
		lblLowerBound = new JLabel("Lower bound");
		tabBlankBothSides.add(lblLowerBound, "cell 1 1,alignx trailing");
		
		sliderBlankLowerB = new JSlider();
		sliderBlankLowerB.setValue(0);
		sliderBlankLowerB.setMinimumSize(new Dimension(200, 23));
		tabBlankBothSides.add(sliderBlankLowerB, "cell 2 1 2 1,growx");
		sliderBlankLowerB.addChangeListener(new ChangeListener() { 
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider slider = (JSlider) e.getSource();
				getTxtBlankLowerB().setText(String.valueOf(slider.getValue()));
			}
		});
		
		txtBlankStart = new JTextField("0");
		tabBlankBothSides.add(txtBlankStart, "cell 2 2,alignx left");
		txtBlankStart.setColumns(10);
		
		lblUpperBound = new JLabel("Upper bound");
		tabBlankBothSides.add(lblUpperBound, "cell 1 3,alignx trailing");
		
		sliderBlankUpperB = new JSlider();
		sliderBlankUpperB.setMinimumSize(new Dimension(200, 23));
		sliderBlankUpperB.setValue(100);
		tabBlankBothSides.add(sliderBlankUpperB, "cell 2 3 2 1,growx");
		sliderBlankUpperB.addChangeListener(new ChangeListener() { 
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider slider = (JSlider) e.getSource();
				getTxtBlankUpperB().setText(String.valueOf(slider.getValue()));
			}
		});
		
		txtBlankUpperB = new JTextField("0");
		txtBlankUpperB.setColumns(10);
		tabBlankBothSides.add(txtBlankUpperB, "cell 2 4,growx");
		
		modSelectExcludeData = new ModuleSelectExcludeData(wnd);
		north.add(modSelectExcludeData, BorderLayout.NORTH);
		
	}
	
	/**
	 * sets the IS and checks for blank in both images
	 * same blank for IS and sample
	 * @param img
	 */
	protected void setIS(Image2D img) {
		if(img==null) {
			getTxtISTitle().setText("");
			getTxtISPath().setText("");
		}
		else { 
			getTxtISTitle().setText(img.getTitle());
			getTxtISPath().setText(img.getSettImage().getRAWFilepath()); 
			// check IS and sample (currentImage) for blank
			if(currentImage!=null && img.getOperations().getBlankQuantifier().isActive()) {
				// image gets recreated so makes no sense
				// currentImage.getOperations().setBlankQuantifier(img.getOperations().getBlankQuantifier());
				setAllViaExistingSettingsBlank(img.getOperations().getBlankQuantifier());
			}
			else if(currentImage!=null && currentImage.getOperations().getBlankQuantifier().isActive()) {
				img.getOperations().setBlankQuantifier(currentImage.getOperations().getBlankQuantifier());
			}
		}
		// update settings
		imgIS = img; 
	}

	protected void setBlank(Image2D img) { 
		if(img==null) {
			getTxtBlankTitle().setText("");
			getTxtBlankPath().setText("");
		}
		else { 
			getTxtBlankTitle().setText(img.getTitle());
			getTxtBlankPath().setText(img.getSettImage().getRAWFilepath());
			
			if(getRbBlankActualDataPoint().isSelected() && (img.getLineCount(0)!=currentImage.getLineCount(0) || img.getData().getAvgDP()!=currentImage.getData().getAvgDP()))
				getRbBlankAveragePerLine().setSelected(true);
		}
		// update settings
		imgBlank = img; 
	} 

	//################################################################################################
	// Autoupdate
	@Override
	public void addAutoupdater(ActionListener al, ChangeListener cl, DocumentListener dl, ColorChangedListener ccl, ItemListener il) {
		getTxtBlankPath().getDocument().addDocumentListener(dl);
		getTxtBlankTitle().getDocument().addDocumentListener(dl);
		getTxtBlankLowerB().getDocument().addDocumentListener(dl);
		getTxtBlankUpperB().getDocument().addDocumentListener(dl);

		// blanks
		getCbUseBlank().addItemListener(il);
		getRbBlankActualDataPoint().addItemListener(il);
		getRbBlankAverage().addItemListener(il);
		getRbBlankAveragePerLine().addItemListener(il);
		// in image
		getCbBlankBothSides().addItemListener(il);
		getChckbxShowInChart().addItemListener(il);
		// they just change the txt field
		//getSliderBlankLowerB().addChangeListener(cl);
		//getSliderBlankUpperB().addChangeListener(cl);

		getTxtISPath().getDocument().addDocumentListener(dl);
		getTxtISTitle().getDocument().addDocumentListener(dl);
		getCbUseIS().addItemListener(il); 
		
	}

	@Override
	public void addAutoRepainter(ActionListener al, ChangeListener cl, DocumentListener dl, ColorChangedListener ccl, ItemListener il) {
	}
	//################################################################################################
	// LOGIC
	// Paintsclae from Image
	@Override
	public void setAllViaExistingSettings(SettingsImage2DOperations sett) { 
		ImageLogicRunner.setIS_UPDATING(false);
		// Blank
		setAllViaExistingSettingsBlank(sett.getBlankQuantifier());
		// IS
		setAllViaExistingSettingsIS(sett.getInternalQuantifier());
		// finished
		ImageLogicRunner.setIS_UPDATING(true);
		ImageEditorWindow.getEditor().fireUpdateEvent(true);
	} 
	/**
	 * call on changes to current image
	 * @param q
	 */
	private void setAllViaExistingSettingsBlank(SettingsImage2DBlankSubtraction q) { 
		ImageLogicRunner.setIS_UPDATING(false);
		imgBlank = q.getImgBlank();
		getCbUseBlank().setSelected(q.isActive());
		getRbBlankActualDataPoint().setSelected(q.getMode()==SettingsImage2DBlankSubtraction.MODE_ACTUAL_DP);
		getRbBlankAverage().setSelected(q.getMode()==SettingsImage2DBlankSubtraction.MODE_AVERAGE);
		getRbBlankAveragePerLine().setSelected(q.getMode()==SettingsImage2DBlankSubtraction.MODE_AVERAGE_PER_LINE);
		// set tab
		getTabbedBlank().setSelectedIndex(q.getModeData()==SettingsImage2DBlankSubtraction.MODE_DATA_IMG? 0:1); 
		// set tab img
		if(imgBlank==null) {
			getTxtBlankPath().setText("");
			getTxtBlankTitle().setText("");
		}
		else { 
			getTxtBlankPath().setText(q.getImgBlank().getSettImage().getRAWFilepath());
			getTxtBlankTitle().setText(q.getImgBlank().getTitle());
		}
		// set tab starting data same image
		getCbBlankBothSides().setSelected(q.isUseBothDataStartEnd());
		getChckbxShowInChart().setSelected(q.isShowInChart());
		try {
			// apply to sliders
			if(imgBlank!=null) {
				int max = imgBlank.getData().getMaxDP();
				getSliderBlankLowerB().setMaximum(max);
				getSliderBlankUpperB().setMaximum(max); 
			}
			getSliderBlankLowerB().setValue(q.getStart()); 
			getSliderBlankUpperB().setValue(q.getEnd()!=-1? q.getEnd() : 0);
			getTxtBlankLowerB().setText(String.valueOf(q.getStart())); 
			getTxtBlankUpperB().setText(String.valueOf(q.getEnd()!=-1? q.getEnd() : 0));
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		// finished
		ImageLogicRunner.setIS_UPDATING(true);
	}

	private void setAllViaExistingSettingsIS(SettingsImage2DQuantifierIS IS) { 
		ImageLogicRunner.setIS_UPDATING(false);
		// IS 
		if(IS==null || IS.getImgIS()==null) {
			getTxtISPath().setText("");
			getTxtISTitle().setText("");
			
			getCbUseIS().setSelected(false);
			imgIS=null;
		}
		else {
			getTxtISPath().setText(IS.getImgIS().getSettImage().getRAWFilepath());
			getTxtISTitle().setText(IS.getImgIS().getTitle());
			getCbUseIS().setSelected(IS.isActive()); 
			
			imgIS = IS.getImgIS();
		}
		// finished
		ImageLogicRunner.setIS_UPDATING(true);
	}

	@Override
	public SettingsImage2DOperations writeAllToSettings(SettingsImage2DOperations sett) {
		if(sett!=null) {
			try {
				// blank
				sett.getBlankQuantifier().setActive(getCbUseBlank().isSelected());
				sett.getBlankQuantifier().setImgBlank(imgBlank);
				int mode = getRbBlankActualDataPoint().isSelected()? SettingsImage2DBlankSubtraction.MODE_ACTUAL_DP : SettingsImage2DBlankSubtraction.MODE_AVERAGE;
				if(getRbBlankAveragePerLine().isSelected()) mode = SettingsImage2DBlankSubtraction.MODE_AVERAGE_PER_LINE;
				sett.getBlankQuantifier().setMode(mode);
				// same image starting: tab
				sett.getBlankQuantifier().setModeData(tabbedBlank.getSelectedIndex()==0? SettingsImage2DBlankSubtraction.MODE_DATA_IMG:SettingsImage2DBlankSubtraction.MODE_DATA_START);
				sett.getBlankQuantifier().setUseBothDataStartEnd(getCbBlankBothSides().isSelected());
				sett.getBlankQuantifier().setShowInChart(getChckbxShowInChart().isSelected());
				sett.getBlankQuantifier().setStart(intFromTxt(getTxtBlankLowerB()));
				sett.getBlankQuantifier().setEnd(intFromTxt(getTxtBlankUpperB()));
				// set sliders
				getSliderBlankLowerB().setValue(intFromTxt(getTxtBlankLowerB()));
				getSliderBlankUpperB().setValue((intFromTxt(getTxtBlankUpperB())));
				// IS
				if(sett.getInternalQuantifier()==null)
					sett.setInternalQuantifier(new SettingsImage2DQuantifierIS(imgIS));
				sett.getInternalQuantifier().setActive(getCbUseIS().isSelected());
				sett.getInternalQuantifier().setImgIS(imgIS);
				sett.getInternalQuantifier().setConcentrationFactor(doubleFromTxt(getTxtISFactor()));
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			finally { 
				// important
				if(currentImage!=null)
					currentImage.fireIntensityProcessingChanged();
				// show? 
				if(currentHeat!=null) {
					currentHeat.updateShowBlankMinMax();
				}
			}
		}
		return sett;
	}
	

	@Override
	public void setSettings(SettingsImage2DOperations settings) {
		if(settings==null)
			settings = new SettingsImage2DOperations();
		super.setSettings(settings);
	}
	
	@Override
	public void setCurrentImage(Image2D img) { 
		super.setCurrentImage(img);
		// apply to sliders
		int max = img.getData().getMaxDP();
		getSliderBlankLowerB().setMaximum(max);
		getSliderBlankUpperB().setMaximum(max); 
	}
	
	//################################################################################################
	// GETTERS AND SETTERS  
	public ModuleQuantifyStrategy getModQuantifier() {
		return modQuantifier;
	}
	public JTextField getTxtBlankPath() {
		return txtBlankPath;
	}
	public JTextField getTxtBlankTitle() {
		return txtBlankTitle;
	}
	public JCheckBox getCbUseBlank() {
		return cbUseBlank;
	}
	public JRadioButton getRbBlankActualDataPoint() {
		return rbBlankActualDataPoint;
	}
	public JRadioButton getRbBlankAverage() {
		return rbBlankAverage;
	}

	public JTextField getTxtISTitle() {
		return txtISTitle;
	}

	public JTextField getTxtISPath() {
		return txtISPath;
	}

	public JCheckBox getCbUseIS() {
		return cbUseIS;
	}
	public JTextField getTxtISFactor() {
		return txtISFactor;
	}
	public JRadioButton getRbBlankAveragePerLine() {
		return rbBlankAveragePerLine;
	}
	public JSlider getSliderBlankUpperB() {
		return sliderBlankUpperB;
	}
	public JCheckBox getCbBlankBothSides() {
		return cbBlankBothSides;
	}
	public JSlider getSliderBlankLowerB() {
		return sliderBlankLowerB;
	}
	public JCheckBox getChckbxShowInChart() {
		return chckbxShowInChart;
	}
	public JTabbedPane getTabbedBlank() {
		return tabbedBlank;
	}
	public JTextField getTxtBlankUpperB() {
		return txtBlankUpperB;
	}
	public JTextField getTxtBlankLowerB() {
		return txtBlankStart;
	}

	public ModuleSelectExcludeData getModSelectExcludeData() {
		return modSelectExcludeData;
	}
}
