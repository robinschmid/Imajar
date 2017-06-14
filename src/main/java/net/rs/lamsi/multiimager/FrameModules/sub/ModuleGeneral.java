package net.rs.lamsi.multiimager.FrameModules.sub;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;

import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.framework.basics.ColorChangedListener;
import net.rs.lamsi.general.framework.basics.JColorPickerButton;
import net.rs.lamsi.general.framework.basics.JFontSpecs;
import net.rs.lamsi.general.framework.modules.Collectable2DSettingsModule;
import net.rs.lamsi.general.framework.modules.Module;
import net.rs.lamsi.general.framework.modules.menu.ModuleMenuApplyToImage;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.general.settings.image.sub.SettingsGeneralImage;
import net.rs.lamsi.general.settings.image.sub.SettingsGeneralImage.IMAGING_MODE;
import net.rs.lamsi.general.settings.image.visualisation.themes.SettingsThemesContainer;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageLogicRunner;

import org.jfree.chart.plot.XYPlot;

public class ModuleGeneral extends Collectable2DSettingsModule<SettingsGeneralImage, Image2D> { 
	//
	private ImageEditorWindow window;
	
	// AUTOGEN
	private JTextField txtTitle;
	private JTextField txtVelocity;
	private JTextField txtSpotsize;
	private JToggleButton btnImagingOneWay;
	private JToggleButton btnImagingTwoWays;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private final ButtonGroup buttonGroup_1 = new ButtonGroup();
	private JRadioButton rbRotation0;
	private JRadioButton rbRotation90;
	private JRadioButton rbRotation180;
	private JRadioButton rbRotation270;
	private JToggleButton btnReflectHorizontal;
	private JToggleButton btnReflectVertical;
	private ModuleSplitContinousImage modSplitConImg;
	private JCheckBox cbBiaryData;
	private JTextField txtShortTitle;
	private JCheckBox cbShortTitle;
	private JTextField txtXPosTitle;
	private JTextField txtYPosTitle;
	private JFontSpecs fontShortTitle;
	private JColorPickerButton colorBGShortTitle;
	private JTextField txtInterpolate;
	private JCheckBox cbInterpolate;
	private JCheckBox cbBlurRadius;
	private JTextField txtBlurRadius;
	private JCheckBox cbCropDataToMin;

	/**
	 * Create the panel.
	 */
	public ModuleGeneral(ImageEditorWindow wnd) {
		super("General", false, SettingsGeneralImage.class, Image2D.class);  
		window = wnd;
		
		JPanel pnNorth = new JPanel();
		getPnContent().add(pnNorth, BorderLayout.NORTH);
		pnNorth.setLayout(new BorderLayout(0, 0));
		
		JPanel pnTitleANdLaser = new JPanel();
		pnNorth.add(pnTitleANdLaser, BorderLayout.NORTH);
		pnTitleANdLaser.setLayout(new MigLayout("", "[][grow]", "[][][][][][][][][][]"));
		
		JLabel lblTitle = new JLabel("title");
		pnTitleANdLaser.add(lblTitle, "cell 0 0,alignx trailing");
		lblTitle.setAlignmentY(Component.TOP_ALIGNMENT);
		lblTitle.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		txtTitle = new JTextField();
		pnTitleANdLaser.add(txtTitle, "cell 1 0");
		txtTitle.setAlignmentY(Component.TOP_ALIGNMENT);
		txtTitle.setColumns(10);
		
		cbShortTitle = new JCheckBox("short title");
		cbShortTitle.setSelected(true);
		cbShortTitle.setHorizontalAlignment(SwingConstants.TRAILING);
		pnTitleANdLaser.add(cbShortTitle, "cell 0 1,alignx right");
		
		txtShortTitle = new JTextField();
		txtShortTitle.setHorizontalAlignment(SwingConstants.LEFT);
		pnTitleANdLaser.add(txtShortTitle, "flowx,cell 1 1,alignx left");
		txtShortTitle.setColumns(10);
		
		JLabel lblVelocityx = new JLabel("velocity (x | \u00B5m/s)");
		pnTitleANdLaser.add(lblVelocityx, "cell 0 3,alignx trailing");
		lblVelocityx.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		txtVelocity = new JTextField();
		pnTitleANdLaser.add(txtVelocity, "cell 1 3");
		txtVelocity.setAlignmentY(Component.TOP_ALIGNMENT);
		txtVelocity.setColumns(10);
		
		JLabel lblSpotSize = new JLabel("spot size (y | \u00B5m)");
		pnTitleANdLaser.add(lblSpotSize, "cell 0 4,alignx trailing");
		lblSpotSize.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		txtSpotsize = new JTextField();
		pnTitleANdLaser.add(txtSpotsize, "cell 1 4");
		txtSpotsize.setAlignmentY(Component.TOP_ALIGNMENT);
		txtSpotsize.setColumns(10);
		
		cbCropDataToMin = new JCheckBox("crop data to minimum");
		cbCropDataToMin.setToolTipText("Crops all lines to the length of the shortest line.");
		pnTitleANdLaser.add(cbCropDataToMin, "cell 0 5 2 1");
		
		cbInterpolate = new JCheckBox("interpolate");
		cbInterpolate.setToolTipText("Use bicubic interpolation for values >1 or reduce data by factors <1.");
		pnTitleANdLaser.add(cbInterpolate, "cell 0 6");
		
		txtInterpolate = new JTextField();
		txtInterpolate.setToolTipText("Use bicubic interpolation for values >1 or reduce data by factors <1.");
		txtInterpolate.setText("1");
		pnTitleANdLaser.add(txtInterpolate, "cell 1 6,alignx left");
		txtInterpolate.setColumns(10);
		
		cbBlurRadius = new JCheckBox("use blur radius");
		cbBlurRadius.setToolTipText("Approximation of the Gaussian blur by applying a box blur three times. Always performs \"crop data to minimum\".");
		pnTitleANdLaser.add(cbBlurRadius, "cell 0 7");
		
		txtBlurRadius = new JTextField();
		txtBlurRadius.setToolTipText("Use bicubic interpolation for values >1 or reduce data by factors <1.");
		txtBlurRadius.setText("1");
		txtBlurRadius.setColumns(10);
		pnTitleANdLaser.add(txtBlurRadius, "cell 1 7,alignx left");
		
		JButton btnCommentary = new JButton("Commentary");
		pnTitleANdLaser.add(btnCommentary, "flowy,cell 0 8");
		btnCommentary.setToolTipText("Commentary with dates");
		
		JButton btnMetadata = new JButton("Metadata");
		pnTitleANdLaser.add(btnMetadata, "cell 1 8");
		btnMetadata.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		btnMetadata.setAlignmentX(Component.RIGHT_ALIGNMENT);
		btnMetadata.setToolTipText("Metadata such as used instruments and methods");
		
		cbBiaryData = new JCheckBox("binary data");
		cbBiaryData.setSelected(true);
		cbBiaryData.setToolTipText("Is data binary? Like binary map export from multi view window.");
		pnTitleANdLaser.add(cbBiaryData, "cell 0 9 2 1");
		
		txtXPosTitle = new JTextField();
		txtXPosTitle.setToolTipText("X position in percent");
		txtXPosTitle.setText("0.9");
		pnTitleANdLaser.add(txtXPosTitle, "cell 1 1");
		txtXPosTitle.setColumns(5);
		
		txtYPosTitle = new JTextField();
		txtYPosTitle.setToolTipText("Y position in percent");
		txtYPosTitle.setText("0.9");
		txtYPosTitle.setColumns(5);
		pnTitleANdLaser.add(txtYPosTitle, "cell 1 1");
		
		
		// all family names of fonts
		fontShortTitle = new JFontSpecs();
		pnTitleANdLaser.add(fontShortTitle, "flowx,cell 0 2 2 1,alignx left");

		colorBGShortTitle = new JColorPickerButton(this);
		pnTitleANdLaser.add(colorBGShortTitle, "cell 0 2 2 1");
		colorBGShortTitle.setColor(Color.BLACK);
		
		//########################################################
		// add MODULESplitContinous Image TODO
		modSplitConImg = new ModuleSplitContinousImage(window);
		modSplitConImg.setVisible(false);
		pnNorth.add(modSplitConImg, BorderLayout.CENTER);
		
		JPanel pnOrientation = new JPanel();
		getPnContent().add(pnOrientation, BorderLayout.CENTER);
		pnOrientation.setLayout(new BorderLayout(0, 0));
		
		Module modImagingMode = new Module("Imaging mode");
		modImagingMode.setShowTitleAlways(true);
		pnOrientation.add(modImagingMode, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		modImagingMode.getPnContent().add(panel, BorderLayout.CENTER);
		panel.setLayout(new MigLayout("", "[grow,center][][fill][][grow,fill]", "[]"));
		
		int size = 100;
		btnImagingOneWay = new JToggleButton();
		btnImagingOneWay.setToolTipText("One way: Laser ist starting at one side and does return to this location for the next line");
		buttonGroup.add(btnImagingOneWay);
		btnImagingOneWay.setMaximumSize(new Dimension(size, size));
		btnImagingOneWay.setMargin(new Insets(0, 0, 0, 0));
		btnImagingOneWay.setIcon(new ImageIcon(new ImageIcon(ModuleGeneral.class.getResource("/img/btn_imaging_mode_oneway.png")).getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH)));
		btnImagingOneWay.setMinimumSize(new Dimension(size, size));
		btnImagingOneWay.setPreferredSize(new Dimension(size, size));
		btnImagingOneWay.setSelectedIcon(new ImageIcon(new ImageIcon(ModuleGeneral.class.getResource("/img/btn_imaging_mode_oneway_selected.png")).getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH)));
		btnImagingOneWay.setSelected(true);
		panel.add(btnImagingOneWay, "cell 1 0,alignx center");
		
		btnImagingTwoWays = new JToggleButton();
		btnImagingTwoWays.setToolTipText("Two ways: Laser is not returning to the starting side. Starts lines alternating from left and right sample side.");
		buttonGroup.add(btnImagingTwoWays);
		btnImagingTwoWays.setSelectedIcon(new ImageIcon(new ImageIcon(ModuleGeneral.class.getResource("/img/btn_imaging_mode_twoways_selected.png")).getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH)));
		btnImagingTwoWays.setIcon(new ImageIcon(new ImageIcon(ModuleGeneral.class.getResource("/img/btn_imaging_mode_twoways.png")).getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH)));
		btnImagingTwoWays.setPreferredSize(new Dimension(size,size));
		btnImagingTwoWays.setMinimumSize(new Dimension(size, size));
		panel.add(btnImagingTwoWays, "cell 3 0");
		
		Module modCropRotate = new Module("Rotation and reflection");
		modCropRotate.setShowTitleAlways(true);
		pnOrientation.add(modCropRotate, BorderLayout.CENTER);
		
		JPanel panel_1 = new JPanel();
		modCropRotate.getPnContent().add(panel_1, BorderLayout.NORTH);
		panel_1.setLayout(new MigLayout("", "[grow][][][][][][grow]", "[][][][]"));
		
		size = 50;
		
		btnReflectHorizontal = new JToggleButton();
		btnReflectHorizontal.setToolTipText("Horizontal reflection");
		btnReflectHorizontal.setSelectedIcon(new ImageIcon(new ImageIcon(ModuleGeneral.class.getResource("/img/btn_imaging_reflect_vertical_selected.png")).getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH)));
		btnReflectHorizontal.setIcon(new ImageIcon(new ImageIcon(ModuleGeneral.class.getResource("/img/btn_imaging_reflect_vertical.png")).getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH)));
		btnReflectHorizontal.setPreferredSize(new Dimension(size, size));
		btnReflectHorizontal.setMinimumSize(new Dimension(size, size));
		btnReflectHorizontal.setMaximumSize(new Dimension(size, size));
		btnReflectHorizontal.setMargin(new Insets(0, 0, 0, 0));
		panel_1.add(btnReflectHorizontal, "cell 2 0");
		
		btnReflectVertical = new JToggleButton();
		btnReflectVertical.setToolTipText("Vertical reflection");
		btnReflectVertical.setSelectedIcon(new ImageIcon(new ImageIcon(ModuleGeneral.class.getResource("/img/btn_imaging_reflect_horizontal_selected.png")).getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH)));
		btnReflectVertical.setIcon(new ImageIcon(new ImageIcon(ModuleGeneral.class.getResource("/img/btn_imaging_reflect_horizontal.png")).getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH)));
		btnReflectVertical.setPreferredSize(new Dimension(size, size));
		btnReflectVertical.setMinimumSize(new Dimension(size, size));
		btnReflectVertical.setMaximumSize(new Dimension(size, size));
		btnReflectVertical.setMargin(new Insets(0, 0, 0, 0));
		panel_1.add(btnReflectVertical, "cell 4 0");
		
		JLabel lblHorizontal = new JLabel("horizontal");
		panel_1.add(lblHorizontal, "cell 2 1,alignx center");
		
		JLabel lblVertical = new JLabel("vertical");
		panel_1.add(lblVertical, "cell 4 1,alignx center");
		
		JLabel lblRotation = new JLabel("rotation");
		panel_1.add(lblRotation, "cell 1 3");
		
		rbRotation270 = new JRadioButton("-90\u00B0");
		buttonGroup_1.add(rbRotation270);
		rbRotation270.setToolTipText("Rotation");
		panel_1.add(rbRotation270, "flowx,cell 2 3,alignx right");
		
		rbRotation0 = new JRadioButton("0\u00B0");
		buttonGroup_1.add(rbRotation0);
		rbRotation0.setToolTipText("Rotation");
		rbRotation0.setSelected(true);
		panel_1.add(rbRotation0, "cell 3 3,alignx center");
		
		rbRotation90 = new JRadioButton("90\u00B0");
		buttonGroup_1.add(rbRotation90);
		rbRotation90.setToolTipText("Rotation");
		panel_1.add(rbRotation90, "cell 4 3,alignx center");
		
		rbRotation180 = new JRadioButton("180\u00B0");
		buttonGroup_1.add(rbRotation180);
		rbRotation180.setToolTipText("Rotation");
		panel_1.add(rbRotation180, "cell 5 3");
		
		JPanel panel_2 = new JPanel();
		modCropRotate.getPnContent().add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new MigLayout("", "[grow][][][grow]", "[][]"));
		
		JLabel lblCropMarks = new JLabel("Crop marks");
		panel_2.add(lblCropMarks, "cell 1 0 2 1,alignx center");
		
		JButton btnApplyCropMarks = new JButton("Apply crop marks");
		btnApplyCropMarks.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// get crop marks from displayed plot (heatmap)
				// current zoom bounds
				XYPlot plot = getCurrentHeat().getChart().getXYPlot();
				double x0 = plot.getDomainAxis().getLowerBound();
				double x1 = plot.getDomainAxis().getUpperBound();
				double y0 = plot.getRangeAxis().getLowerBound();
				double y1 = plot.getRangeAxis().getUpperBound();
				// apply crop marks and save to settings of image 
				getSettings().applyCropMarks(x0,x1,y0,y1);
			}
		});
		panel_2.add(btnApplyCropMarks, "cell 1 1");
		
		JButton btnDeleteCropMarks = new JButton("Delete crop marks");
		btnDeleteCropMarks.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// only delete crop marks in settings
				getSettings().deleteCropMarks();
			}
		});
		panel_2.add(btnDeleteCropMarks, "cell 2 1");
		 
		// apply to Listener
		getPopupMenu().addApplyToImageListener(new ModuleMenuApplyToImage() {
			@Override
			public void applyToImage(Settings sett, Image2D img) {
				// also set short title to theme
				img.getSettTheme().getTheme().setShortTitle(fontShortTitle.getColor(), getColorBGShortTitle().getColor(), fontShortTitle.getSelectedFont());
			}
		});
	}
	
	//################################################################################################
	// Autoupdate
	@Override
	public void addAutoupdater(ActionListener al, ChangeListener cl, DocumentListener dl, ColorChangedListener ccl, ItemListener il) {
		modSplitConImg.addAutoupdater(al, cl, dl, ccl, il);
		getTxtSpotsize().getDocument().addDocumentListener(dl);
		getTxtTitle().getDocument().addDocumentListener(dl);
		getTxtVelocity().getDocument().addDocumentListener(dl); 
		getCbBiaryData().addItemListener(il);
		
		// reflection and rotation
		getRbRotation0().addItemListener(il);
		getRbRotation90().addItemListener(il);
		getRbRotation180().addItemListener(il);
		getRbRotation270().addItemListener(il);
		
		getBtnReflectHorizontal().addActionListener(al);
		getBtnReflectVertical().addActionListener(al);
		
		// imaging mode
		getBtnImagingOneWay().addActionListener(al);
		getBtnImagingTwoWays().addActionListener(al);
		
		getCbInterpolate().addItemListener(il);
		getTxtInterpolate().getDocument().addDocumentListener(dl);
		
		getCbBlurRadius().addItemListener(il);
		getTxtBlurRadius().getDocument().addDocumentListener(dl);
		
		cbCropDataToMin.addItemListener(il);
	}

	@Override
	public void addAutoRepainter(ActionListener al, ChangeListener cl, DocumentListener dl, ColorChangedListener ccl, ItemListener il) {
		modSplitConImg.addAutoRepainter(al, cl, dl, ccl, il);
		
		cbShortTitle.addItemListener(il);
		txtShortTitle.getDocument().addDocumentListener(dl);
		txtXPosTitle.getDocument().addDocumentListener(dl);
		txtYPosTitle.getDocument().addDocumentListener(dl);
		
		colorBGShortTitle.addColorChangedListener(ccl);
		fontShortTitle.addListener(ccl, il, dl);
	}
	//################################################################################################
	// LOGIC
	// Paintsclae from Image
	@Override
	public void setAllViaExistingSettings(SettingsGeneralImage si) {  
		ImageLogicRunner.setIS_UPDATING(false);
		// new reseted ps
		if(si == null) {
			si = new SettingsGeneralImage();
			si.resetAll();
		} 
		// crop to min
		cbCropDataToMin.setSelected(si.isCropDataToMinGetRealValue());
		// interpolation
		getTxtInterpolate().setText(String.valueOf(si.getInterpolation()));
		getCbInterpolate().setSelected(si.isUseInterpolation());
		// blur
		getTxtInterpolate().setText(String.valueOf(si.getBlurRadius()));
		getCbInterpolate().setSelected(si.isUseBlur());
		
		//
		this.getTxtTitle().setText(si.getTitle());
		this.getTxtSpotsize().setText(String.valueOf(si.getSpotsize()));
		this.getTxtVelocity().setText(String.valueOf(si.getVelocity())); 
		
		this.getTxtShortTitle().setText(si.getShortTitle());
		getCbShortTitle().setSelected(si.isShowShortTitle());
		getTxtXPosTitle().setText(String.valueOf(si.getXPosTitle()));
		getTxtYPosTitle().setText(String.valueOf(si.getYPosTitle()));

		SettingsThemesContainer s = currentImage.getSettTheme();
		// font 
		fontShortTitle.setSelectedFont(s.getTheme().getFontShortTitle());
		// bg color
		fontShortTitle.setColor(s.getTheme().getcShortTitle());
		colorBGShortTitle.setColor(s.getTheme().getcBGShortTitle());
		
		this.getCbBiaryData().setSelected(si.isBinaryData());
		// 
		getBtnImagingOneWay().setSelected(si.getImagingMode()==IMAGING_MODE.MODE_IMAGING_ONEWAY);
		getBtnImagingTwoWays().setSelected(si.getImagingMode()!=IMAGING_MODE.MODE_IMAGING_ONEWAY);
		// reflection
		getBtnReflectHorizontal().setSelected(si.isReflectHorizontal());
		getBtnReflectVertical().setSelected(si.isReflectVertical());
		// rotation
		switch(si.getRotationOfData()) {
		case 0: getRbRotation0().setSelected(true); break;
		case 90: getRbRotation90().setSelected(true); break;
		case 180: getRbRotation180().setSelected(true); break;
		case 270: getRbRotation270().setSelected(true); break;
		} 
		// finished
		ImageLogicRunner.setIS_UPDATING(true);
		ImageEditorWindow.getEditor().fireUpdateEvent(true);
	} 

	@Override
	public SettingsGeneralImage writeAllToSettings(SettingsGeneralImage settImage) {
		if(settImage!=null) {
			try {
				IMAGING_MODE imagingMode = getBtnImagingOneWay().isSelected()? IMAGING_MODE.MODE_IMAGING_ONEWAY : IMAGING_MODE.MODE_IMAGING_TWOWAYS;
				int rotation = 270;
				if(getRbRotation0().isSelected()) rotation = 0;
				if(getRbRotation90().isSelected()) rotation = 90;
				if(getRbRotation180().isSelected()) rotation = 180;
				
				
				
				settings.setAll(getTxtTitle().getText(), getTxtShortTitle().getText(), cbShortTitle.isSelected(),
						floatFromTxt(txtXPosTitle), floatFromTxt(txtYPosTitle), floatFromTxt(getTxtVelocity()), floatFromTxt(getTxtSpotsize()), 
						imagingMode, getBtnReflectHorizontal().isSelected(), getBtnReflectVertical().isSelected(), rotation, getCbBiaryData().isSelected(),
						getCbInterpolate().isSelected(), doubleFromTxt(getTxtInterpolate()), getCbBlurRadius().isSelected(), doubleFromTxt(getTxtBlurRadius()),
						getCbCropDataToMin().isSelected());
			
				SettingsThemesContainer s = currentImage.getSettTheme();
				s.getTheme().setcShortTitle(fontShortTitle.getColor());
				s.getTheme().setcBGShortTitle(colorBGShortTitle.getColor());
				s.getTheme().setFontShortTitle(fontShortTitle.getSelectedFont());
				
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		return settImage;
	}
	
	
	//################################################################################################
	// GETTERS AND SETTERS 
	public JTextField getTxtTitle() {
		return txtTitle;
	}
	public JTextField getTxtSpotsize() {
		return txtSpotsize;
	}
	public JTextField getTxtVelocity() {
		return txtVelocity;
	}  
	public JToggleButton getBtnImagingOneWay() {
		return btnImagingOneWay;
	}
	public JToggleButton getBtnImagingTwoWays() {
		return btnImagingTwoWays;
	}
	public JRadioButton getRbRotation0() {
		return rbRotation0;
	}
	public JRadioButton getRbRotation90() {
		return rbRotation90;
	}
	public JRadioButton getRbRotation180() {
		return rbRotation180;
	}
	public JRadioButton getRbRotation270() {
		return rbRotation270;
	}
	public JToggleButton getBtnReflectHorizontal() {
		return btnReflectHorizontal;
	}
	public JToggleButton getBtnReflectVertical() {
		return btnReflectVertical;
	}
	public ModuleSplitContinousImage getModSplitConImg() {
		return modSplitConImg;
	}
	public JCheckBox getCbBiaryData() {
		return cbBiaryData;
	}
	public JTextField getTxtShortTitle() {
		return txtShortTitle;
	}
	public JCheckBox getCbShortTitle() {
		return cbShortTitle;
	}
	public JTextField getTxtXPosTitle() {
		return txtXPosTitle;
	}
	public JTextField getTxtYPosTitle() {
		return txtYPosTitle;
	}

	public JColorPickerButton getColorBGShortTitle() {
		return colorBGShortTitle;
	}


	public void setColorBGShortTitle(JColorPickerButton colorBGShortTitle) {
		this.colorBGShortTitle = colorBGShortTitle;
	}
	public JTextField getTxtInterpolate() {
		return txtInterpolate;
	}
	public JCheckBox getCbInterpolate() {
		return cbInterpolate;
	}
	public JCheckBox getCbBlurRadius() {
		return cbBlurRadius;
	}
	public JTextField getTxtBlurRadius() {
		return txtBlurRadius;
	}
	public JCheckBox getCbCropDataToMin() {
		return cbCropDataToMin;
	}
}
