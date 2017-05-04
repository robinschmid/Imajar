package net.rs.lamsi.multiimager.FrameModules.sub;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.util.ResourceBundle;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;

import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.massimager.Frames.FrameWork.ColorChangedListener;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.Collectable2DSettingsModule;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.Module;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.menu.ModuleMenu;
import net.rs.lamsi.massimager.MyFreeChart.themes.ChartThemeFactory;
import net.rs.lamsi.massimager.MyFreeChart.themes.ChartThemeFactory.THEME;
import net.rs.lamsi.massimager.Settings.image.visualisation.SettingsThemes;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageLogicRunner;

import org.jfree.chart.editor.ChartEditor;
import org.jfree.chart.editor.ChartEditorManager;
import org.jfree.chart.util.ResourceBundleWrapper;

public class ModuleThemes extends Collectable2DSettingsModule<SettingsThemes, Collectable2D> {
	// mystuff
	protected boolean isForPrint = true;
	
	
	// auto
	private JCheckBox cbAntiAlias; 
	private JCheckBox cbNoBackground;
	private JCheckBox cbShowTitle;
	protected static ResourceBundle localizationResources = ResourceBundleWrapper.getBundle("org.jfree.chart.LocalizationBundle");
	private JCheckBox cbShowXAxis;
	private JCheckBox cbShowYAxis;
	private JCheckBox cbShowScale;
	private JTextField txtScaleUnit;
	private JTextField txtScaleValue;
	private JTextField txtScaleFactor;
	private JCheckBox cbPaintscaleInPlot;
	private JSlider sliderScaleXPos;
	private JSlider sliderScaleYPos;
	private JTextField txtCSignificantDigits;
	private JCheckBox cbScientificIntensities;
	private JTextField txtPaintScaleTitle;
	private JCheckBox cbUsePaintscaleTitle;
	//

	/**
	 * Create the panel.
	 */
	public ModuleThemes() {
		super("Themes", false, SettingsThemes.class, Collectable2D.class);    
		
		JPanel panel = new JPanel();
		getPnContent().add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		
		Module general = new Module("General");
		panel.add(general, BorderLayout.NORTH);  
		
		JPanel panel_1 = new JPanel();
		general.getPnContent().add(panel_1, BorderLayout.NORTH);
		panel_1.setLayout(new MigLayout("", "[grow][][][grow]", "[][][][]"));
		
		JButton btnPlotSettings = new JButton("Plot settings");
		btnPlotSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// open settings dialog from jfreechart
				if(currentHeat!=null) {
					currentHeat.getChartPanel().getPopupMenu();
					ChartEditor editor = ChartEditorManager.getChartEditor(currentHeat.getChart());
			        int result = JOptionPane.showConfirmDialog(currentHeat.getChartPanel(), editor,
			        		localizationResources.getString("Chart_Properties"),
			                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			        if (result == JOptionPane.OK_OPTION) {
			            editor.updateChart(currentHeat.getChart());
			        	updateFromEditorDialog();
			        }
				}
			}
		});
		panel_1.add(btnPlotSettings, "cell 1 0 2 1,alignx center");
		
		cbAntiAlias = new JCheckBox("anti aliasing");
		panel_1.add(cbAntiAlias, "flowy,cell 1 1");
		
		cbNoBackground = new JCheckBox("no background");
		panel_1.add(cbNoBackground, "cell 2 1");
		
		cbShowTitle = new JCheckBox("show title");
		panel_1.add(cbShowTitle, "cell 1 2");
		
		Module pnFonts = new Module("Fonts and text");
		panel.add(pnFonts, BorderLayout.SOUTH); 
		
		JPanel panel_2 = new JPanel();
		pnFonts.getPnContent().add(panel_2, BorderLayout.NORTH);
		panel_2.setLayout(new MigLayout("", "[grow][][][grow]", "[][]")); 
		
		JButton btnForPrint = new JButton("For print");
		btnForPrint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				applyChangeThemeForPrint(true);
			}
		});
		panel_2.add(btnForPrint, "cell 1 1");
		
		JButton btnForPresentation = new JButton("For presentation");
		btnForPresentation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				applyChangeThemeForPrint(false); 
			}
		});
		panel_2.add(btnForPresentation, "flowy,cell 2 1");
		
		Module modAxes = new Module("Axes and scale");
		panel.add(modAxes, BorderLayout.CENTER); 
		
		JPanel panel_3 = new JPanel();
		modAxes.getPnContent().add(panel_3, BorderLayout.CENTER);
		panel_3.setLayout(new MigLayout("", "[][][grow]", "[][][][][][][][]"));
		
		JLabel lblXAxis = new JLabel("x axis");
		lblXAxis.setFont(new Font("Tahoma", Font.BOLD, 11));
		panel_3.add(lblXAxis, "cell 0 0,alignx trailing");
		
		cbShowXAxis = new JCheckBox("show x axis");
		cbShowXAxis.setSelected(true);
		panel_3.add(cbShowXAxis, "cell 1 0");
		
		JLabel lblYAxis = new JLabel("y axis");
		lblYAxis.setFont(new Font("Tahoma", Font.BOLD, 11));
		panel_3.add(lblYAxis, "cell 0 1,alignx trailing");
		
		cbShowYAxis = new JCheckBox("show y axis");
		cbShowYAxis.setSelected(true);
		panel_3.add(cbShowYAxis, "cell 1 1");
		
		JLabel lblPaintScale = new JLabel("paintscale");
		lblPaintScale.setFont(new Font("Tahoma", Font.BOLD, 11));
		panel_3.add(lblPaintScale, "cell 0 2,alignx right");
		
		cbPaintscaleInPlot = new JCheckBox("in plot");
		panel_3.add(cbPaintscaleInPlot, "cell 1 2");
		
		cbScientificIntensities = new JCheckBox("scientific intensities");
		cbScientificIntensities.setSelected(true);
		panel_3.add(cbScientificIntensities, "cell 1 3");
		
		txtCSignificantDigits = new JTextField();
		txtCSignificantDigits.setText("2");
		txtCSignificantDigits.setToolTipText("Number of significant digits (1.0E >> 2 significant)");
		txtCSignificantDigits.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_3.add(txtCSignificantDigits, "flowx,cell 2 3,alignx left");
		txtCSignificantDigits.setColumns(4);
		
		cbUsePaintscaleTitle = new JCheckBox("use paintscale title");
		cbUsePaintscaleTitle.setSelected(true);
		panel_3.add(cbUsePaintscaleTitle, "cell 1 4");
		
		txtPaintScaleTitle = new JTextField();
		txtPaintScaleTitle.setText("I");
		panel_3.add(txtPaintScaleTitle, "cell 2 4,alignx left");
		txtPaintScaleTitle.setColumns(10);
		
		JLabel lblScale = new JLabel("scale");
		lblScale.setFont(new Font("Tahoma", Font.BOLD, 11));
		panel_3.add(lblScale, "cell 0 6,alignx trailing");
		
		cbShowScale = new JCheckBox("show scale");
		panel_3.add(cbShowScale, "cell 1 6");
		
		JPanel panel_4 = new JPanel();
		panel_3.add(panel_4, "cell 1 7 2 1");
		panel_4.setLayout(new MigLayout("", "[][85.00][][grow]", "[][][][][]"));
		
		JLabel lblUnit = new JLabel("value");
		panel_4.add(lblUnit, "cell 0 0,alignx trailing");
		
		txtScaleValue = new JTextField();
		txtScaleValue.setToolTipText("Value for scale width");
		txtScaleValue.setText("1");
		txtScaleValue.setHorizontalAlignment(SwingConstants.TRAILING);
		panel_4.add(txtScaleValue, "flowx,cell 1 0,growx");
		txtScaleValue.setColumns(10);
		
		txtScaleUnit = new JTextField();
		txtScaleUnit.setToolTipText("Unit for scale width");
		txtScaleUnit.setPreferredSize(new Dimension(5, 20));
		panel_4.add(txtScaleUnit, "flowx,cell 2 0");
		txtScaleUnit.setColumns(10);
		
		JLabel lblFactor = new JLabel("factor");
		panel_4.add(lblFactor, "cell 0 1,alignx trailing");
		
		txtScaleFactor = new JTextField();
		txtScaleFactor.setHorizontalAlignment(SwingConstants.TRAILING);
		txtScaleFactor.setToolTipText("Factor used for scale width calculation");
		txtScaleFactor.setText("1");
		panel_4.add(txtScaleFactor, "cell 1 1,growx");
		txtScaleFactor.setColumns(10);
		
		Component verticalStrut = Box.createVerticalStrut(20);
		panel_4.add(verticalStrut, "cell 0 2");
		
		JLabel lblXPos = new JLabel("x pos");
		panel_4.add(lblXPos, "cell 0 3,alignx trailing");
		
		sliderScaleXPos = new JSlider();
		sliderScaleXPos.setPreferredSize(new Dimension(100, 23));
		sliderScaleXPos.setMinimumSize(new Dimension(100, 23));
		sliderScaleXPos.setValue(90);
		panel_4.add(sliderScaleXPos, "cell 1 3,growx");
		
		JLabel lblYPos = new JLabel("y pos");
		panel_4.add(lblYPos, "cell 0 4,alignx trailing");
		
		JLabel lblUnit_1 = new JLabel("unit");
		panel_4.add(lblUnit_1, "cell 2 0");
		
		sliderScaleYPos = new JSlider();
		sliderScaleYPos.setValue(10);
		sliderScaleYPos.setPreferredSize(new Dimension(100, 23));
		sliderScaleYPos.setMinimumSize(new Dimension(100, 23));
		panel_4.add(sliderScaleYPos, "cell 1 4");
		
		JLabel lblSignificantDifits = new JLabel("significant difits");
		panel_3.add(lblSignificantDifits, "cell 2 3");
		// add standard themes to menu
		addStandardThemesToMenu();
	}
	
	/**
	 * applies changes to font sizes via ChartThemeFactory
	 * @param b
	 */
	protected void applyChangeThemeForPrint(boolean state) {
		if(currentImage!=null) {
			isForPrint = state;
			SettingsThemes theme = currentImage.getSettTheme();
			setSettings(theme);
		} 
	}

	/**
	 * update after settings dialog was closed
	 * @param editor
	 */
	protected void updateFromEditorDialog() {
		// TODO
		// update theme settings and then all components in this panel 
		// update via chart not via editor
	}

	private void addStandardThemesToMenu() { 
		ModuleMenu menu = getPopupMenu();
		menu.addSeparator();
		addThemeToMenu(menu, (THEME.FOR_PRINT), "Print");
		addThemeToMenu(menu, (THEME.FOR_PRESENTATION), "Presentation");
		menu.addSeparator();
		addThemeToMenu(menu, (THEME.BNW_PRINT), "Black n White");
		addThemeToMenu(menu, (THEME.DARKNESS), "Darkness");
		addThemeToMenu(menu, (THEME.KARST), "Karst");
	}
	private void addThemeToMenu(ModuleMenu menu, final THEME themeid, String title) { 
		JMenuItem item = new JMenuItem(title);
		menu.addMenuItem(item);
		item.addActionListener(new ActionListener() {  
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					SettingsThemes theme = null;
					if(themeid==THEME.FOR_PRINT || themeid==THEME.FOR_PRESENTATION) {
						applyChangeThemeForPrint(themeid==THEME.FOR_PRINT); 
					}
					else {
						theme = new SettingsThemes(themeid);
						setSettings(theme); 
					} 
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}); 
	}
	
	// apply for print or presentation before changing settings
	@Override
	public void setSettings(SettingsThemes settings) {
		settings.setTheme(ChartThemeFactory.changeChartThemeForPrintOrPresentation(settings.getTheme(), isForPrint)); 
		ChartThemeFactory.setStandardTheme(settings.getID());
		super.setSettings(settings);
	}
	
	//################################################################################################
	// Autoupdate
	@Override
	public void addAutoupdater(ActionListener al, ChangeListener cl, DocumentListener dl, ColorChangedListener ccl, ItemListener il) {
		//TODO
		getCbPaintscaleInPlot().addItemListener(il);
	}
	
	@Override
	public void addAutoRepainter(ActionListener al, ChangeListener cl, DocumentListener dl, ColorChangedListener ccl, ItemListener il) {
		// axes and scale
		getCbShowScale().addItemListener(il);
		getCbShowXAxis().addItemListener(il);
		getCbShowYAxis().addItemListener(il);
		// general
		getCbAntiAlias().addItemListener(il);
		getCbShowTitle().addItemListener(il);
		getCbNoBackground().addItemListener(il); 

		getTxtScaleFactor().getDocument().addDocumentListener(dl);
		getTxtScaleValue().getDocument().addDocumentListener(dl);
		getTxtScaleUnit().getDocument().addDocumentListener(dl);

		getSliderScaleXPos().addChangeListener(cl);
		getSliderScaleYPos().addChangeListener(cl);
		
		getCbScientificIntensities().addItemListener(il);
		getTxtCSignificantDigits().getDocument().addDocumentListener(dl);
		getCbUsePaintscaleTitle().addItemListener(il);
		getTxtPaintScaleTitle().getDocument().addDocumentListener(dl);
	}
	
	//################################################################################################
	// LOGIC
	@Override
	public void setAllViaExistingSettings(SettingsThemes st) {  
		ImageLogicRunner.setIS_UPDATING(false);
		// new reseted ps
		if(st == null) {
			st = new SettingsThemes();
			st.resetAll();
		} 
		// set all to txt 
		getCbAntiAlias().setSelected(st.isAntiAliased());
		getCbShowTitle().setSelected(st.isShowTitle());
		getCbNoBackground().setSelected(st.isNoBackground()); 
		 
		getCbShowScale().setSelected(st.getTheme().isShowScale());
		getCbShowXAxis().setSelected(st.getTheme().isShowXAxis());
		getCbShowYAxis().setSelected(st.getTheme().isShowYAxis());
		
		getCbPaintscaleInPlot().setSelected(st.getTheme().isPaintScaleInPlot());
		
		// set all txt scale
		getTxtScaleFactor().setText(String.valueOf(st.getTheme().getScaleFactor()));
		getTxtScaleValue().setText(String.valueOf(st.getTheme().getScaleValue()));
		getTxtScaleUnit().setText(String.valueOf(st.getTheme().getScaleUnit()));  
		// scale slider 
		getSliderScaleXPos().setValue((int)(st.getTheme().getScaleXPos()*100));
		getSliderScaleYPos().setValue((int)(st.getTheme().getScaleYPos()*100)); 
		
		// significant scientific notion
		getCbScientificIntensities().setSelected(st.isUseScientificIntensities());
		getTxtCSignificantDigits().setText(String.valueOf(st.getSignificantDigits()));
		
		// paintscale title
		getCbUsePaintscaleTitle().setSelected(st.isUsePaintScaleTitle());
		getTxtPaintScaleTitle().setText(st.getPaintScaleTitle());
		
		// finished
		ImageLogicRunner.setIS_UPDATING(true);
		ImageEditorWindow.getEditor().fireUpdateEvent(true);
	} 

	@Override
	public SettingsThemes writeAllToSettings(SettingsThemes st) {
		if(st!=null) {
			try {
				// setall
				st.setAll(getCbAntiAlias().isSelected(), getCbShowTitle().isSelected(), getCbNoBackground().isSelected(),
						false,false,
						getCbShowXAxis().isSelected(), getCbShowYAxis().isSelected(), 
						getCbShowScale().isSelected(), getTxtScaleUnit().getText(), floatFromTxt(getTxtScaleFactor()), floatFromTxt(getTxtScaleValue()),
						getCbPaintscaleInPlot().isSelected(), getSliderScaleXPos().getValue()/100.f, getSliderScaleYPos().getValue()/100.f,
						cbScientificIntensities.isSelected(), intFromTxt(txtCSignificantDigits), txtPaintScaleTitle.getText(), cbUsePaintscaleTitle.isSelected());
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		return st;
	}
	
	
	//################################################################################################
	// GETTERS AND SETTERS 
	public JCheckBox getCbAntiAlias() {
		return cbAntiAlias;
	}
	public JCheckBox getCbNoBackground() {
		return cbNoBackground;
	}
	public JCheckBox getCbShowTitle() {
		return cbShowTitle;
	} 
	public JCheckBox getCbShowXAxis() {
		return cbShowXAxis;
	}
	public JCheckBox getCbShowYAxis() {
		return cbShowYAxis;
	}
	public JCheckBox getCbShowScale() {
		return cbShowScale;
	}
	public JTextField getTxtScaleValue() {
		return txtScaleValue;
	}
	public JTextField getTxtScaleUnit() {
		return txtScaleUnit;
	}
	public JTextField getTxtScaleFactor() {
		return txtScaleFactor;
	}
	public JCheckBox getCbPaintscaleInPlot() {
		return cbPaintscaleInPlot;
	}
	public JSlider getSliderScaleXPos() {
		return sliderScaleXPos;
	}
	public JSlider getSliderScaleYPos() {
		return sliderScaleYPos;
	}
	public JCheckBox getCbScientificIntensities() {
		return cbScientificIntensities;
	}
	public JTextField getTxtCSignificantDigits() {
		return txtCSignificantDigits;
	}
	public JCheckBox getCbUsePaintscaleTitle() {
		return cbUsePaintscaleTitle;
	}
	public JTextField getTxtPaintScaleTitle() {
		return txtPaintScaleTitle;
	}
}
