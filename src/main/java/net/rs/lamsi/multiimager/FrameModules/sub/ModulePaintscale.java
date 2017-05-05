package net.rs.lamsi.multiimager.FrameModules.sub;


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.framework.basics.ColorChangedListener;
import net.rs.lamsi.general.framework.basics.JColorPickerButton;
import net.rs.lamsi.general.framework.listener.DelayedDocumentListener;
import net.rs.lamsi.general.framework.modules.Collectable2DSettingsModule;
import net.rs.lamsi.general.framework.modules.menu.ModuleMenu;
import net.rs.lamsi.general.heatmap.PaintScaleGenerator;
import net.rs.lamsi.general.settings.image.visualisation.SettingsPaintScale;
import net.rs.lamsi.general.settings.image.visualisation.SettingsPaintScale.ValueMode;
import net.rs.lamsi.multiimager.FrameModules.sub.paintscale.PaintScaleHistogram;
import net.rs.lamsi.multiimager.FrameModules.sub.paintscale.PaintscaleIcon;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow.LOG;
import net.rs.lamsi.multiimager.Frames.ImageLogicRunner;

public class ModulePaintscale extends Collectable2DSettingsModule<SettingsPaintScale, Image2D> {
	//################################################################################################
	// MY STUFF

	private static int ICON_WIDTH = 100;

	//################################################################################################
	// GENERATED
	private JTextField txtLevels;
	private JPanel pnMinMax;
	private JTextField txtMinimum;
	private JTextField txtMaximum;
	private JColorPickerButton btnMaxColor;
	private JColorPickerButton btnMinColor;
	private JCheckBox cbMonochrom;
	private JCheckBox cbWhiteAsMin;
	private JCheckBox cbInvert;
	private JCheckBox cbUseMinMax;
	private JCheckBox cbMinimumTransparent;
	private JLabel lblEndColor;
	private JLabel lblBrightness;
	private JSlider sliderBrightness;
	private JButton btnReset;
	private Component verticalStrut;
	private Component verticalStrut_1;
	private JSlider sliderMinimum;
	private Component verticalStrut_2;
	private JSlider sliderMaximum;
	private JSeparator separator;
	private JTextField txtMinFilter;
	private JLabel label;
	private JTextField txtMaxFilter;
	private JLabel label_1;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private final ButtonGroup buttonGroup_1 = new ButtonGroup();
	private JLabel lblAbs;
	private JButton btnApplyMinFilter;
	private JButton btnApplyMaxFilter;
	private Component verticalStrut_3;
	private Component verticalStrut_4;
	private JTextField txtBrightness;
	private JCheckBox cbGreyScale;
	private JButton btnSwitchColors;
	private JCheckBox cbOnlyUseSelectedMinMax;
	private JCheckBox cbBlackAsMax;
	private JPanel panel_1;
	private JTextField txtLOD;
	private JLabel lblLod;
	private JLabel lblAbs_2;
	private JCheckBox cbLODMonochrome;
	private PaintScaleHistogram pnHistogram;
	private JComboBox comboMinValType;
	private JComboBox comboMaxValType;
	private JTextField txtMinPerc;
	private JTextField txtMaxPerc;
	private JLabel lblAbs_3;
	private JLabel label_2;
	private JLabel label_3;

	private DecimalFormat formatAbs = new DecimalFormat("#.###");
	private DecimalFormat formatAbsSmall = new DecimalFormat("#.######");
	private DecimalFormat formatPerc = new DecimalFormat("#.####");


	private final Border errorBorder = BorderFactory.createLineBorder(Color.red, 3);
	private final Border emptyBorder = BorderFactory.createEmptyBorder();

	//
	private DelayedDocumentListener[] delayedDListener;
	private DelayedDocumentListener listenerMinAbs, listenerMinPerc, listenerMaxPerc, listenerMaxAbs, listenerMaxFilter, listenerMinFilter;

	/**
	 * Create the panel.
	 */
	public ModulePaintscale() { 
		super("Paintscale", false, SettingsPaintScale.class, Image2D.class); 
		getPnContent().setLayout(new MigLayout("", "[188px,grow]", "[176px][][30.00px]"));

		formatAbs.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		formatAbsSmall.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		formatPerc.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));

		JPanel panel = new JPanel();
		getPnContent().add(panel, "cell 0 0,grow");
		panel.setLayout(new MigLayout("", "[grow][grow]", "[][][][][25.00][][][][][][][][]"));

		JLabel lblLevels = new JLabel("levels");
		panel.add(lblLevels, "cell 0 0,alignx trailing");

		txtLevels = new JTextField();
		txtLevels.setText("256");
		panel.add(txtLevels, "cell 1 0,alignx left");
		txtLevels.setColumns(10);

		cbMonochrom = new JCheckBox("monochrome");
		panel.add(cbMonochrom, "flowx,cell 0 1 2 1,alignx left");
		cbMonochrom.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JCheckBox cb = (JCheckBox)e.getSource();
				// monochrom panel off/on
				getBtnMaxColor().setVisible(!cb.isSelected());
				getLblEndColor().setVisible(!cb.isSelected());
				getCbGreyScale().setEnabled(cb.isSelected());
			}
		});

		cbWhiteAsMin = new JCheckBox("White");
		cbWhiteAsMin.setToolTipText("Use white in paintscale");
		panel.add(cbWhiteAsMin, "cell 0 2");

		cbBlackAsMax = new JCheckBox("Black");
		cbBlackAsMax.setToolTipText("Use black in paintscale");
		panel.add(cbBlackAsMax, "cell 1 2");

		JLabel lblStartColor = new JLabel("min color");
		panel.add(lblStartColor, "cell 0 4,alignx trailing");

		btnMinColor = new JColorPickerButton(this); 
		btnMinColor.setToolTipText("Minimum color");
		panel.add(btnMinColor, "flowx,cell 1 4,growy");

		btnMaxColor = new JColorPickerButton(this); 
		btnMaxColor.setToolTipText("Maximum color");
		panel.add(btnMaxColor, "cell 1 4,growy");

		lblEndColor = new JLabel("max color");
		panel.add(lblEndColor, "cell 1 4,alignx trailing");

		cbInvert = new JCheckBox("invert");
		panel.add(cbInvert, "flowx,cell 0 3 2 1");

		verticalStrut = Box.createVerticalStrut(20);
		verticalStrut.setPreferredSize(new Dimension(0, 5));
		verticalStrut.setMinimumSize(new Dimension(0, 5));
		panel.add(verticalStrut, "cell 0 5");

		lblBrightness = new JLabel("brightness");
		panel.add(lblBrightness, "cell 0 7");

		btnReset = new JButton("reset");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getSliderBrightness().setValue(400);
			}
		});
		panel.add(btnReset, "flowx,cell 0 8");

		sliderBrightness = new JSlider();
		sliderBrightness.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource(); 
				if (!source.getValueIsAdjusting() && getTxtBrightness()!=null) { 
					getTxtBrightness().setText(""+source.getValue()/10/10.f);
				}
			}
		});
		sliderBrightness.setValue(400);
		sliderBrightness.setMaximum(1800);
		sliderBrightness.setMinimum(200);
		panel.add(sliderBrightness, "cell 1 8,growx");

		verticalStrut_1 = Box.createVerticalStrut(20);
		verticalStrut_1.setPreferredSize(new Dimension(0, 5));
		verticalStrut_1.setMinimumSize(new Dimension(0, 5));
		panel.add(verticalStrut_1, "cell 0 9");

		cbOnlyUseSelectedMinMax = new JCheckBox("only use selected values (min/max)");
		cbOnlyUseSelectedMinMax.setToolTipText("Uses minimum and maximum value from selected minus excluded rects ");
		cbOnlyUseSelectedMinMax.setSelected(true);
		panel.add(cbOnlyUseSelectedMinMax, "cell 0 11 2 1");

		cbUseMinMax = new JCheckBox("use min & max values");
		cbUseMinMax.setToolTipText("Set a minimum (limit of detection) and a maximum value. All values beneath or above will be set to minimum or maximum color, respectively.");
		panel.add(cbUseMinMax, "cell 0 12 2 1");

		txtBrightness = new JTextField();
		txtBrightness.setHorizontalAlignment(SwingConstants.CENTER);
		txtBrightness.setPreferredSize(new Dimension(24, 20));
		txtBrightness.setMinimumSize(new Dimension(24, 20));
		txtBrightness.setText("2.0");
		panel.add(txtBrightness, "cell 0 8");
		txtBrightness.setColumns(10);

		cbGreyScale = new JCheckBox("grey scale");
		panel.add(cbGreyScale, "cell 1 1");

		btnSwitchColors = new JButton("<>");
		btnSwitchColors.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Color minsave = getBtnMinColor().getColor();
				getBtnMinColor().setColor(getBtnMaxColor().getColor());
				getBtnMaxColor().setColor(minsave);
			}
		});
		btnSwitchColors.setToolTipText("Switch colors");
		panel.add(btnSwitchColors, "cell 1 3,alignx center");
		cbUseMinMax.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JCheckBox cb = (JCheckBox)e.getSource();  
				getPnMinMax().setVisible(cb.isSelected()); 
			}
		});

		panel_1 = new JPanel();
		getPnContent().add(panel_1, "cell 0 2,grow");
		panel_1.setLayout(new MigLayout("", "[][grow]", "[][]"));

		cbLODMonochrome = new JCheckBox("LOD monochrome");
		panel_1.add(cbLODMonochrome, "cell 0 0 2 1");

		lblLod = new JLabel("LOD");
		panel_1.add(lblLod, "cell 0 1,alignx trailing");

		txtLOD = new JTextField();
		txtLOD.setHorizontalAlignment(SwingConstants.RIGHT);
		txtLOD.setText("0");
		panel_1.add(txtLOD, "flowx,cell 1 1,growx");
		txtLOD.setColumns(10);

		lblAbs_2 = new JLabel("abs");
		panel_1.add(lblAbs_2, "cell 1 1");

		pnMinMax = new JPanel();
		getPnContent().add(pnMinMax, "cell 0 1,growx,aligny top");
		pnMinMax.setLayout(new MigLayout("", "[][][][][][grow][grow]", "[][grow][][][][][][][][][][][][]"));

		cbMinimumTransparent = new JCheckBox("min transparent");
		cbMinimumTransparent.setToolTipText("Displays the all values <=minimum as transparent");
		pnMinMax.add(cbMinimumTransparent, "cell 0 0 3 1,alignx left");

		CbMaximumTransparent = new JCheckBox("max transparent");
		CbMaximumTransparent.setToolTipText("Displays all values >= maximum as transparent");
		pnMinMax.add(CbMaximumTransparent, "cell 3 0");

		pnHistogram = new PaintScaleHistogram();
		pnHistogram.setPreferredSize(new Dimension(10, 120));
		pnMinMax.add(pnHistogram, "cell 0 1 7 1,grow");

		JLabel lblMinimum = new JLabel("minimum");
		lblMinimum.setFont(new Font("Tahoma", Font.BOLD, 11));
		pnMinMax.add(lblMinimum, "cell 0 2 3 1,alignx left");

		comboMinValType = new JComboBox();
		comboMinValType.setModel(new DefaultComboBoxModel(ValueMode.values()));
		pnMinMax.add(comboMinValType, "cell 3 2,alignx left");
		comboMinValType.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					ValueMode mode = (ValueMode)comboMinValType.getSelectedItem();
					switch(mode) {
					case ABSOLUTE: 
						applyNewMinimum();
						break;
					case PERCENTILE:
						break;
					case RELATIVE: 
						applyNewMinimum();
						break;
					}
					// apply
					applyMinMode(mode);
				}
				if(ImageLogicRunner.IS_UPDATING)
					writeAllToSettings();
			}
		});

		sliderMinimum = new JSlider();
		sliderMinimum.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) { 
				JSlider source = (JSlider)e.getSource(); 
				if (!source.getValueIsAdjusting() && source.isEnabled()) { 
					setMinimumValuePercentage(source.getValue()/1000.f, false);
				}
			}
		});

		txtMinimum = new JTextField();
		pnMinMax.add(txtMinimum, "cell 2 3,alignx left");
		txtMinimum.setColumns(20);

		txtMinPerc = new JTextField();
		pnMinMax.add(txtMinPerc, "cell 2 4,growx");
		txtMinPerc.setColumns(18);

		label_2 = new JLabel("%");
		pnMinMax.add(label_2, "flowx,cell 3 4");
		sliderMinimum.setMaximum(100000);
		sliderMinimum.setValue(0);
		pnMinMax.add(sliderMinimum, "cell 3 4,growx");

		verticalStrut_3 = Box.createVerticalStrut(20);
		verticalStrut_3.setPreferredSize(new Dimension(0, 5));
		verticalStrut_3.setMinimumSize(new Dimension(0, 5));
		pnMinMax.add(verticalStrut_3, "cell 2 5");

		txtMinFilter = new JTextField();
		txtMinFilter.setToolTipText("Do not use the first X% of values");
		txtMinFilter.setHorizontalAlignment(SwingConstants.TRAILING);
		txtMinFilter.setText("1");
		pnMinMax.add(txtMinFilter, "cell 2 6,alignx left");
		txtMinFilter.setColumns(18);

		label = new JLabel("%");
		pnMinMax.add(label, "flowx,cell 3 6");

		verticalStrut_2 = Box.createVerticalStrut(20);
		verticalStrut_2.setPreferredSize(new Dimension(0, 10));
		verticalStrut_2.setMinimumSize(new Dimension(0, 10));
		pnMinMax.add(verticalStrut_2, "cell 2 7");

		separator = new JSeparator();
		pnMinMax.add(separator, "flowx,cell 2 8");

		JLabel lblMaximum = new JLabel("maximum");
		lblMaximum.setFont(new Font("Tahoma", Font.BOLD, 11));
		pnMinMax.add(lblMaximum, "flowx,cell 0 9 3 1,alignx left");

		comboMaxValType = new JComboBox();
		comboMaxValType.setModel(new DefaultComboBoxModel(ValueMode.values()));
		pnMinMax.add(comboMaxValType, "cell 3 9,alignx left");
		comboMaxValType.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					ValueMode mode = (ValueMode)comboMaxValType.getSelectedItem();
					switch(mode) {
					case ABSOLUTE: 
						applyNewMaximum(); 
						break;
					case PERCENTILE: 
						break;
					case RELATIVE: 
						applyNewMaximum(); 
						break;
					}
					// apply
					applyMaxMode(mode);
				}
				if(ImageLogicRunner.IS_UPDATING)
					writeAllToSettings();
			}
		});

		sliderMaximum = new JSlider();
		sliderMaximum.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) { 
				JSlider source = (JSlider)e.getSource();
				if (!source.getValueIsAdjusting() && source.isEnabled()) {
					setMaximumValuePercentage(source.getValue()/1000.f, false);
				}
			}
		});

		txtMaximum = new JTextField();
		pnMinMax.add(txtMaximum, "flowx,cell 2 10,alignx left");
		txtMaximum.setColumns(20);

		txtMaxPerc = new JTextField();
		pnMinMax.add(txtMaxPerc, "cell 2 11,growx");
		txtMaxPerc.setColumns(18);

		label_3 = new JLabel("%");
		pnMinMax.add(label_3, "flowx,cell 3 11");
		sliderMaximum.setValue(0);
		sliderMaximum.setMaximum(100000);
		pnMinMax.add(sliderMaximum, "cell 3 11,growx");

		verticalStrut_4 = Box.createVerticalStrut(20);
		verticalStrut_4.setPreferredSize(new Dimension(0, 5));
		verticalStrut_4.setMinimumSize(new Dimension(0, 5));
		pnMinMax.add(verticalStrut_4, "cell 2 12");

		txtMaxFilter = new JTextField();
		txtMaxFilter.setToolTipText("Do not use the last X% values");
		txtMaxFilter.setText("1");
		txtMaxFilter.setHorizontalAlignment(SwingConstants.TRAILING);
		txtMaxFilter.setColumns(18);
		pnMinMax.add(txtMaxFilter, "cell 2 13,growx");

		label_1 = new JLabel("%");
		pnMinMax.add(label_1, "flowx,cell 3 13");

		lblAbs = new JLabel("abs");
		pnMinMax.add(lblAbs, "cell 3 3");

		btnApplyMinFilter = new JButton("Apply");
		btnApplyMinFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(currentImage!=null) {
					applyMinFilter();
				}
			}
		});
		pnMinMax.add(btnApplyMinFilter, "cell 3 6,alignx left");

		btnApplyMaxFilter = new JButton("Apply");
		btnApplyMaxFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(currentImage!=null) {
					applyMaxFilter();
				}
			}
		});
		pnMinMax.add(btnApplyMaxFilter, "cell 3 13,alignx left");

		lblAbs_3 = new JLabel("abs");
		pnMinMax.add(lblAbs_3, "cell 3 10");

		// add standard paintscales to menu
		// TODO comment out for window build
		addStandardPaintScalesToMenu();
	}

	/**
	 * enables text fields and listeners 
	 * @param mode
	 */
	private void applyMinMode(ValueMode mode) {
		getTxtMinimum().setEditable(mode.equals(ValueMode.ABSOLUTE));
		getTxtMinFilter().setEditable(mode.equals(ValueMode.PERCENTILE));
		getTxtMinPerc().setEditable(mode.equals(ValueMode.RELATIVE));
		getSliderMinimum().setEnabled(mode.equals(ValueMode.RELATIVE));
		listenerMinAbs.setActive(mode.equals(ValueMode.ABSOLUTE));
		listenerMinPerc.setActive(mode.equals(ValueMode.RELATIVE));
		listenerMinFilter.setActive(mode.equals(ValueMode.PERCENTILE));
	}

	/**
	 * enables text fields and listeners 
	 * @param mode
	 */
	private void applyMaxMode(ValueMode mode) {
		getTxtMaximum().setEditable(mode.equals(ValueMode.ABSOLUTE));
		getTxtMaxFilter().setEditable(mode.equals(ValueMode.PERCENTILE));
		getTxtMaxPerc().setEditable(mode.equals(ValueMode.RELATIVE));
		getSliderMaximum().setEnabled(mode.equals(ValueMode.RELATIVE));
		listenerMaxAbs.setActive(mode.equals(ValueMode.ABSOLUTE));
		listenerMaxPerc.setActive(mode.equals(ValueMode.RELATIVE));
		listenerMaxFilter.setActive(mode.equals(ValueMode.PERCENTILE));
	}


	/*
	1.	Grey
	2.	R , G, B
	3.	Black-blue-red
	4.	Black-blue-red-white
	5.	Black-blue-green-white
	6.	Black-red-yellow-white
	 */
	private void addStandardPaintScalesToMenu() {
		ModuleMenu menu = getPopupMenu();
		menu.addSeparator();
		setPresetindex(5);
		addPreset(menu, SettingsPaintScale.createSettings(SettingsPaintScale.S_GREY), "Grey");
		addPreset(menu, SettingsPaintScale.createSettings(SettingsPaintScale.S_PURPLE), "Purple");
		addPreset(menu, SettingsPaintScale.createSettings(SettingsPaintScale.S_YELLOW), "YELLOW");
		addPreset(menu, SettingsPaintScale.createSettings(SettingsPaintScale.S_BLUE), "Blue");
		addPreset(menu, SettingsPaintScale.createSettings(SettingsPaintScale.S_RED), "Red");
		addPreset(menu, SettingsPaintScale.createSettings(SettingsPaintScale.S_GREEN), "Green");
		addPreset(menu, SettingsPaintScale.createSettings(SettingsPaintScale.S_BLACK_BLUE_GR_W), "Cyan-Green");
		addPreset(menu, SettingsPaintScale.createSettings(SettingsPaintScale.S_BLACK_RED_YE_W), "Red-Yellow");
		addPreset(menu, SettingsPaintScale.createSettings(SettingsPaintScale.S_RAINBOW_BRIGHT), "Rainbow br+");
		addPreset(menu, SettingsPaintScale.createSettings(SettingsPaintScale.S_RAINBOW), "Rainbow");
		addPreset(menu, SettingsPaintScale.createSettings(SettingsPaintScale.S_RAINBOW_INVERSE), "Rainbow inverse");
		addPreset(menu, SettingsPaintScale.createSettings(SettingsPaintScale.S_KARST_RAINBOW_INVERSE), "KARST:Rainbow inverse");
		setPresetindex(4);
	}

	@Override
	public void setCurrentImage(Image2D img) {
		super.setCurrentImage(img);
		getPnHistogram().setImg(img);
	}

	@Override
	public JMenuItem addPreset(ModuleMenu menu, final SettingsPaintScale settings, String title) { 
		// menuitem
		JMenuItem item = super.addPreset(menu, settings, title);
		// icon for paintscale
		PaintscaleIcon icon = new PaintscaleIcon(PaintScaleGenerator.generateStepPaintScaleForLegend(0, 100, settings), ICON_WIDTH, 15, true);
		item.setIcon(icon);  
		return item;
	}

	//##########################################################################################
	// MINIMUM AND MAXIMUM INTENSITY
	// last values
	private double lastMaxPercentage = 0, lastMinPercentage = 0, lastMin = 0, lastMax = 0;
	private JCheckBox CbMaximumTransparent;
	protected void setMinimumValuePercentage(double f, boolean force) {
		if(((!(lastMinPercentage+0.001>f && lastMinPercentage-0.001<f)) || force)  && currentImage!=null) {
			ImageEditorWindow.log("Setting Min % "+f, LOG.DEBUG);
			lastMinPercentage = f;
			// apply to all perc. components 
			getSliderMinimum().setValue((int)(f*1000));
			getTxtMinPerc().setText(formatPercentNumber(f));
			// absolute
			double absMin = currentImage.getIAbs(f, getCbOnlyUseSelectedMinMax().isSelected());
			setMinimumValue(absMin, false);
		}		
	}
	protected void setMinimumValue(double abs, boolean force) {
		if((lastMin!=abs || force) && currentImage!=null) { 
			lastMin = abs;
			// apply to all abs components
			getTxtMinimum().setText(formatAbsNumber(abs));
			// percentage
			setMinimumValuePercentage(currentImage.getIPercentage(abs, getCbOnlyUseSelectedMinMax().isSelected()), false);
		}
	}
	protected void setMaximumValuePercentage(double f, boolean force) {
		if((force || ((!(lastMaxPercentage+0.001>f && lastMaxPercentage-0.001<f) || getTxtMaxPerc().getText().length()==0)))  && currentImage!=null) {
			ImageEditorWindow.log("Setting max % "+f, LOG.DEBUG);
			lastMaxPercentage = f;
			// apply to all perc. components
			getSliderMaximum().setValue((int)(f*1000));
			getTxtMaxPerc().setText(formatPercentNumber(f));
			// absolute
			double absMax = currentImage.getIAbs(f, getCbOnlyUseSelectedMinMax().isSelected());
			setMaximumValue(absMax, false);
		}		
	}
	protected void setMaximumValue(double abs, boolean force) {
		if((force || lastMax!=abs) && currentImage!=null) {
			ImageEditorWindow.log("Setting max abs "+abs, LOG.DEBUG);
			lastMax = abs;
			// apply to all abs components
			getTxtMaximum().setText(formatAbsNumber(abs));
			// percentage
			setMaximumValuePercentage(currentImage.getIPercentage(abs, getCbOnlyUseSelectedMinMax().isSelected()), false);
		}
	}


	protected void applyMinFilter() {
		try {
			double f = doubleFromTxt(getTxtMinFilter());
			currentImage.applyCutFilterMin(f);
			double minZ = currentImage.getMinZFiltered();
			setMinimumValue(minZ, true);
			getTxtMinFilter().setBorder(emptyBorder);
		} catch (Exception ex) {
			ex.printStackTrace();
			getTxtMinFilter().setBorder(errorBorder);
		}
	}
	protected void applyMaxFilter() {
		try {
			double f = doubleFromTxt(getTxtMaxFilter());
			currentImage.applyCutFilterMax(f);
			double maxZ = currentImage.getMaxZFiltered();
			setMaximumValue(maxZ, true);
			getTxtMaxFilter().setBorder(emptyBorder);
		} catch (Exception ex) {
			ex.printStackTrace();
			getTxtMaxFilter().setBorder(errorBorder);
		}
	}

	//################################################################################################
	// Autoupdate  TODO
	@Override
	public void addAutoupdater(final ActionListener al, ChangeListener cl, DocumentListener dl, ColorChangedListener ccl, ItemListener il) {
		getTxtLevels().getDocument().addDocumentListener(dl);

		getTxtLOD().getDocument().addDocumentListener(dl);

		getCbBlackAsMax().addActionListener(al);
		getCbWhiteAsMin().addActionListener(al);
		getCbInvert().addActionListener(al);
		getCbMinimumTransparent().addActionListener(al);
		getCbMaximumTransparent().addActionListener(al);
		getCbMonochrom().addActionListener(al);
		getCbGreyScale().addActionListener(al);
		getCbUseMinMax().addActionListener(al);
		getCbOnlyUseSelectedMinMax().addActionListener(al);


		getBtnApplyMinFilter().addActionListener(al);
		getBtnApplyMaxFilter().addActionListener(al);

		getSliderBrightness().addChangeListener(cl);

		getBtnMinColor().addColorChangedListener(ccl);
		getBtnMaxColor().addColorChangedListener(ccl);
		// LOD 
		getCbLODMonochrome().addActionListener(al);

		// min max
		//getComboMaxValType().addItemListener(il);
		//getComboMinValType().addItemListener(il);

		listenerMinAbs = new DelayedDocumentListener() {
			@Override
			public void documentChanged(DocumentEvent e) {
				applyNewMinimum();
				al.actionPerformed(null);
			}
		};
		listenerMaxAbs = new DelayedDocumentListener() {
			@Override
			public void documentChanged(DocumentEvent e) {
				applyNewMaximum();
				al.actionPerformed(null);
			}
		};
		listenerMaxPerc = new DelayedDocumentListener() {
			@Override
			public void documentChanged(DocumentEvent e) {
				applyNewMaximum();
				al.actionPerformed(null);
			}
		};
		listenerMinPerc = new DelayedDocumentListener() {
			@Override 
			public void documentChanged(DocumentEvent e) {
				applyNewMinimum();
				al.actionPerformed(null);
			}
		};
		listenerMaxFilter = new DelayedDocumentListener() {
			@Override 
			public void documentChanged(DocumentEvent e) {
				applyMaxFilter();
				al.actionPerformed(null);
			}
		};
		listenerMinFilter = new DelayedDocumentListener() {
			@Override 
			public void documentChanged(DocumentEvent e) {
				applyMinFilter();
				al.actionPerformed(null);
			}
		};

		getTxtMinimum().getDocument().addDocumentListener(listenerMinAbs);
		getTxtMaximum().getDocument().addDocumentListener(listenerMaxAbs);

		getTxtMaxPerc().getDocument().addDocumentListener(listenerMaxPerc);
		getTxtMinPerc().getDocument().addDocumentListener(listenerMinPerc);

		// filter
		getTxtMaxFilter().getDocument().addDocumentListener(listenerMaxFilter);
		getTxtMinFilter().getDocument().addDocumentListener(listenerMinFilter);

		// TODO add to list
		delayedDListener = new DelayedDocumentListener[] {listenerMinAbs, listenerMaxAbs, listenerMaxFilter, listenerMaxPerc, listenerMinFilter, listenerMinPerc};
	}

	@Override
	public void addAutoRepainter(ActionListener al, ChangeListener cl, DocumentListener dl, ColorChangedListener ccl, ItemListener il) {
	}

	/**
	 * calculates new minimum percentage/abs
	 */
	protected void applyNewMinimum() {
		ValueMode mode = (ValueMode) getComboMinValType().getSelectedItem();
		try {
			switch(mode){
			case ABSOLUTE:
				double value = Double.parseDouble(getTxtMinimum().getText());
				setMinimumValue(value, false);
				getTxtMinPerc().setBorder(emptyBorder);
				getTxtMinimum().setBorder(emptyBorder);
				break;
			case RELATIVE:
				double value2 = Double.parseDouble(getTxtMinPerc().getText());
				setMinimumValuePercentage(value2, false);
				getTxtMinPerc().setBorder(emptyBorder);
				getTxtMinimum().setBorder(emptyBorder);
				break;
			}
		}catch(Exception ex) {
			ex.printStackTrace();
			switch(mode){
			case ABSOLUTE:
				getTxtMinimum().setBorder(errorBorder);
				break;
			case RELATIVE:
				getTxtMinPerc().setBorder(errorBorder);
				break;
			}
		}
	}
	protected void applyNewMaximum() {
		ValueMode mode = (ValueMode) getComboMaxValType().getSelectedItem();
		try {
			switch(mode){
			case ABSOLUTE:
				double value = Double.parseDouble(getTxtMaximum().getText());
				setMaximumValue(value, false);
				getTxtMaxPerc().setBorder(emptyBorder);
				getTxtMaximum().setBorder(emptyBorder);
				break;
			case RELATIVE:
				double value2 = Double.parseDouble(getTxtMaxPerc().getText());
				setMaximumValuePercentage(value2, false);
				getTxtMaxPerc().setBorder(emptyBorder);
				getTxtMaximum().setBorder(emptyBorder);
				break;
			}
		}catch(Exception ex) {
			ex.printStackTrace();
			switch(mode){
			case ABSOLUTE:
				getTxtMaximum().setBorder(errorBorder);
				break;
			case RELATIVE:
				getTxtMaxPerc().setBorder(errorBorder);
				break;
			}
		}
	}

	/**
	 * stops all delayed documentlistener
	 */
	public void stopDelayedListener() {
		for(DelayedDocumentListener dl : delayedDListener)
			dl.stop();
	}
	/**
	 * sets the active state for all delayed listeners
	 * @param state
	 */
	public void setDelayedListenerActive(boolean state) {
		for(DelayedDocumentListener dl : delayedDListener)
			dl.setActive(state);
	}

	//################################################################################################
	// LOGIC
	// Paintsclae from Image 
	@Override
	public void setAllViaExistingSettings(SettingsPaintScale ps) { 
		ImageLogicRunner.setIS_UPDATING(false);

		// new reseted ps
		if(ps == null) {
			ps = new SettingsPaintScale();
			ps.resetAll();
		}
		// stop auto updating
		stopDelayedListener();
		setDelayedListenerActive(false);

		//rb

		this.getCbMonochrom().setSelected(ps.isMonochrom());
		this.getCbGreyScale().setSelected(ps.isGrey());
		this.getCbWhiteAsMin().setSelected(ps.isUsesWAsMin()); 
		this.getCbBlackAsMax().setSelected(ps.isUsesBAsMax()); 
		this.getCbInvert().setSelected(ps.isInverted()); 
		this.getCbUseMinMax().setSelected(ps.isUsesMinMax()); 
		this.getCbMinimumTransparent().setSelected(ps.isUsesMinAsInvisible()); 
		this.getCbMaximumTransparent().setSelected(ps.isUsesMaxAsInvisible()); 
		this.getCbOnlyUseSelectedMinMax().setSelected(ps.isUsesMinMaxFromSelection());

		this.getBtnMinColor().setBackground(ps.getMinColor());
		this.getBtnMaxColor().setBackground(ps.getMaxColor());
		this.getBtnMinColor().setForeground(ps.getMinColor());
		this.getBtnMaxColor().setForeground(ps.getMaxColor());

		// visibility
		this.getPnMinMax().setVisible(ps.isUsesMinMax());
		this.getBtnMaxColor().setVisible(!ps.isMonochrom());
		this.getLblEndColor().setVisible(!ps.isMonochrom()); 

		// 
		this.getSliderBrightness().setValue((int) (ps.getBrightnessFactor()*100));

		this.getCbLODMonochrome().setSelected(ps.isLODMonochrome());
		this.getTxtLOD().setText(formatAbsNumber(ps.getLOD()));

		this.getTxtLevels().setText(String.valueOf(ps.getLevels()));

		//max
		double minFilter = ps.getMinFilter();
		double maxFilter = ps.getMaxFilter();
		this.getTxtMinFilter().setText(formatPercentNumber(minFilter));
		this.getTxtMaxFilter().setText(formatPercentNumber(maxFilter));

		// apply if min/max is not set
		if(ps.getMaxIAbs(currentImage)==0 && ps.getModeMax().equals(ValueMode.PERCENTILE))
			applyMaxFilter();
		if(ps.getMinIAbs(currentImage)==0 && ps.getModeMin().equals(ValueMode.PERCENTILE))
			applyMinFilter();

		// set values and force!
		if(ps.getModeMin().equals(ValueMode.ABSOLUTE)) {
			// absolute at last
			setMinimumValuePercentage(ps.getMinIRel(currentImage), true);
			setMinimumValue(ps.getMinIAbs(currentImage), true);
		}
		else {
			// percentage last
			setMinimumValue(ps.getMinIAbs(currentImage), true);
			setMinimumValuePercentage(ps.getMinIRel(currentImage), true);
		}

		if(ps.getModeMax().equals(ValueMode.ABSOLUTE)) {
			// absolute at last
			setMaximumValuePercentage(ps.getMaxIRel(currentImage), true);
			setMaximumValue(ps.getMaxIAbs(currentImage), true);
		}
		else {
			// percentage last
			setMaximumValue(ps.getMaxIAbs(currentImage), true);
			setMaximumValuePercentage(ps.getMaxIRel(currentImage), true);
		}

		// comboboxes
		getComboMinValType().setSelectedItem(ps.getModeMin());
		getComboMaxValType().setSelectedItem(ps.getModeMax());
		applyMinMode(ps.getModeMin());
		applyMaxMode(ps.getModeMax());

		/// renew histo
		getPnHistogram().updateHisto(ps);

		// just in case 
		stopDelayedListener();
		// start autoupdating
		setDelayedListenerActive(true);

		// finished
		ImageLogicRunner.setIS_UPDATING(true);
		ImageEditorWindow.getEditor().fireUpdateEvent(true);
	} 

	@Override
	public SettingsPaintScale writeAllToSettings(SettingsPaintScale ps) {
		if(ps!=null) {
			try {
				// for abs. and percentile the min max values are absolute
				double min = ps.getModeMin().equals(ValueMode.RELATIVE)? doubleFromTxt(getTxtMinPerc()) : doubleFromTxt(getTxtMinimum());
				double max = ps.getModeMax().equals(ValueMode.RELATIVE)? doubleFromTxt(getTxtMaxPerc()) : doubleFromTxt(getTxtMaximum());
				ps.setAll(intFromTxt(getTxtLevels()), 
						getCbMonochrom().isSelected(), getCbInvert().isSelected(), 
						getCbBlackAsMax().isSelected(), getCbWhiteAsMin().isSelected(), getCbUseMinMax().isSelected(), 
						getCbMinimumTransparent().isSelected(),getCbMaximumTransparent().isSelected(),
						(ValueMode)getComboMinValType().getSelectedItem(), (ValueMode)getComboMaxValType().getSelectedItem(), 
						min, max, 
						getBtnMinColor().getBackground(), getBtnMaxColor().getBackground(),
						getSliderBrightness().getValue()/100.f,
						floatFromTxt(getTxtMinFilter()),
						floatFromTxt(getTxtMaxFilter()), 
						getCbGreyScale().isSelected(),
						getCbOnlyUseSelectedMinMax().isSelected(), 
						getCbLODMonochrome().isSelected(), doubleFromTxt(getTxtLOD()));
				/// renew histo
				getPnHistogram().updateHisto(ps);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		return ps;
	}  


	private String formatAbsNumber(double in) {
		return in>10? formatAbs.format(in) : formatAbsSmall.format(in);
	}

	private String formatPercentNumber(double in) {
		return formatPerc.format(in);
	}
	//################################################################################################
	// GETTERS AND SETTERS 
	public JPanel getPnMinMax() {
		return pnMinMax;
	}
	public JTextField getTxtLevels() {
		return txtLevels;
	}
	public JColorPickerButton getBtnMaxColor() {
		return btnMaxColor;
	}
	public JColorPickerButton getBtnMinColor() {
		return btnMinColor;
	}
	public JCheckBox getCbMonochrom() {
		return cbMonochrom;
	}
	public JCheckBox getCbWhiteAsMin() {
		return cbWhiteAsMin;
	}
	public JCheckBox getCbInvert() {
		return cbInvert;
	}
	public JCheckBox getCbUseMinMax() {
		return cbUseMinMax;
	}
	public JCheckBox getCbMinimumTransparent() {
		return cbMinimumTransparent;
	}
	public JLabel getLblEndColor() {
		return lblEndColor;
	}
	public JTextField getTxtMinimum() {
		return txtMinimum;
	}
	public JTextField getTxtMaximum() {
		return txtMaximum;
	} 
	public JSlider getSliderBrightness() {
		return sliderBrightness;
	} 
	public JSlider getSliderMaximum() {
		return sliderMaximum;
	}
	public JSlider getSliderMinimum() {
		return sliderMinimum;
	} 
	public JTextField getTxtMaxFilter() {
		return txtMaxFilter;
	}
	public JTextField getTxtMinFilter() {
		return txtMinFilter;
	}
	public JButton getBtnApplyMinFilter() {
		return btnApplyMinFilter;
	}
	public JButton getBtnApplyMaxFilter() {
		return btnApplyMaxFilter;
	}
	public JTextField getTxtBrightness() {
		return txtBrightness;
	}
	public JCheckBox getCbGreyScale() {
		return cbGreyScale;
	}
	public JCheckBox getCbOnlyUseSelectedMinMax() {
		return cbOnlyUseSelectedMinMax;
	}
	public JCheckBox getCbBlackAsMax() {
		return cbBlackAsMax;
	}
	public JTextField getTxtLOD() {
		return txtLOD;
	}
	public JCheckBox getCbLODMonochrome() {
		return cbLODMonochrome;
	}
	public JTextField getTxtMinPerc() {
		return txtMinPerc;
	}
	public JTextField getTxtMaxPerc() {
		return txtMaxPerc;
	}
	public JComboBox getComboMaxValType() {
		return comboMaxValType;
	}
	public JComboBox getComboMinValType() {
		return comboMinValType;
	}
	public PaintScaleHistogram getPnHistogram() {
		return pnHistogram;
	}
	public JCheckBox getCbMaximumTransparent() {
		return CbMaximumTransparent;
	}
}
