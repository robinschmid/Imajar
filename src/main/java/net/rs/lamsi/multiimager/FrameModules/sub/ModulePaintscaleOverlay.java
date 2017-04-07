package net.rs.lamsi.multiimager.FrameModules.sub;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;

import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.ImageOverlay;
import net.rs.lamsi.massimager.Frames.FrameWork.ColorChangedListener;
import net.rs.lamsi.massimager.Frames.FrameWork.ColorPicker2;
import net.rs.lamsi.massimager.Frames.FrameWork.JColorPickerButton;
import net.rs.lamsi.massimager.Frames.FrameWork.listener.DelayedDocumentListener;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.Collectable2DSettingsModule;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.menu.ModuleMenu;
import net.rs.lamsi.massimager.Heatmap.PaintScaleGenerator;
import net.rs.lamsi.massimager.Settings.image.SettingsImageOverlay;
import net.rs.lamsi.massimager.Settings.image.visualisation.SettingsPaintScale;
import net.rs.lamsi.multiimager.FrameModules.sub.paintscale.PaintscaleIcon;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageLogicRunner;

public class ModulePaintscaleOverlay extends Collectable2DSettingsModule<SettingsImageOverlay, ImageOverlay> {
	//################################################################################################
	// MY STUFF
	protected ColorPicker2 colorPickerDialog; 
	
	private static int ICON_WIDTH = 100;

	//################################################################################################
	// GENERATED
	private JTextField txtLevels;
	private JColorPickerButton btnMinColor;
	private JCheckBox cbWhiteAsMin;
	private JCheckBox cbInvert;
	private JTabbedPane tabbedPaintScales;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private final ButtonGroup buttonGroup_1 = new ButtonGroup();
	private JCheckBox cbBlackAsMax;
	private JPanel panel_1;
	
	private DecimalFormat formatAbs = new DecimalFormat("#.###");
	private DecimalFormat formatAbsSmall = new DecimalFormat("#.######");
	private DecimalFormat formatPerc = new DecimalFormat("#.####");
	
	private final Border errorBorder = BorderFactory.createLineBorder(Color.red, 3);
	private final Border emptyBorder = BorderFactory.createEmptyBorder();
	
	private ModulePaintscaleOverlaySub[] modPSList;
	//
	private ActionListener al; 
	private ChangeListener cl; 
	private DocumentListener dl; 
	private ColorChangedListener ccl; 
	private ItemListener il;
	
	/**
	 * Create the panel.
	 */
	public ModulePaintscaleOverlay() { 
		super("Paintscale", false, SettingsImageOverlay.class, ImageOverlay.class); 
		getPnContent().setLayout(new MigLayout("", "[188px,grow]", "[176px][]"));
		
		formatAbs.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		formatAbsSmall.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		formatPerc.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		
		JPanel panel = new JPanel();
		getPnContent().add(panel, "cell 0 0,grow");
		panel.setLayout(new MigLayout("", "[grow][grow]", "[][][][]"));
		
		JLabel lblLevels = new JLabel("levels");
		panel.add(lblLevels, "cell 0 0,alignx trailing");
		
		txtLevels = new JTextField();
		txtLevels.setText("256");
		panel.add(txtLevels, "cell 1 0,alignx left");
		txtLevels.setColumns(10);
		
		cbWhiteAsMin = new JCheckBox("White");
		cbWhiteAsMin.setToolTipText("Use white in paintscale");
		panel.add(cbWhiteAsMin, "cell 0 2");
		
		cbBlackAsMax = new JCheckBox("Black");
		cbBlackAsMax.setToolTipText("Use black in paintscale");
		panel.add(cbBlackAsMax, "cell 1 2");
		
		cbInvert = new JCheckBox("invert");
		panel.add(cbInvert, "flowx,cell 0 3 2 1");
		
		btnMinColor = new JColorPickerButton(this); 
		btnMinColor.setToolTipText("Minimum color");
		panel.add(btnMinColor, "cell 1 3,growy");
		
		panel_1 = new JPanel();
		getPnContent().add(panel_1, "cell 0 1,grow");
		panel_1.setLayout(new BorderLayout(0, 0));
		
		tabbedPaintScales = new JTabbedPane(JTabbedPane.TOP);
		panel_1.add(tabbedPaintScales, BorderLayout.CENTER);
		
		// init 
		colorPickerDialog = new ColorPicker2(this); 
		
		// add standard paintscales to menu
		// TODO comment out for window build
		addStandardPaintScalesToMenu();
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
		
//		addPreset(menu, SettingsPaintScale.createSettings(SettingsPaintScale.S_GREY), "Grey");
		
		setPresetindex(4);
	}
	
	// TODO distribution
	@Override
	public void setCurrentImage(ImageOverlay img) {
		super.setCurrentImage(img);
		// set all images to all paintscale modules
		// TODO
		Vector<Image2D> psList = img.getImages();
		// create new
		if(modPSList==null || modPSList.length!=psList.size()) {

			tabbedPaintScales.removeAll();
			modPSList = new ModulePaintscaleOverlaySub[psList.size()];
			
			for(int i=0; i<psList.size(); i++) {
				modPSList[i] = new ModulePaintscaleOverlaySub();
				modPSList[i].addAutoupdater(al, cl, dl, ccl, il);
				
				modPSList[i].setCurrentImage(psList.get(i));
				modPSList[i].setSettings(img.getSettings().getSettPaintScale(i));
				
				tabbedPaintScales.add(psList.get(i).getTitle(), modPSList[i]);
			}
		}
		else {
			for(int i=0; i<psList.size(); i++) {
				modPSList[i].setCurrentImage(psList.get(i));
				modPSList[i].setSettings(img.getSettings().getSettPaintScale(i));
				tabbedPaintScales.setTitleAt(i, psList.get(i).getTitle());
			}
		}
	}
	
	@Override
	public JMenuItem addPreset(ModuleMenu menu, final SettingsImageOverlay settings, String title) { 
		// menuitem
		JMenuItem item = super.addPreset(menu, settings, title);
		// icon for paintscale TODO
		//PaintscaleIcon icon = new PaintscaleIcon(PaintScaleGenerator.generateStepPaintScaleForLegend(0, 100, settings), ICON_WIDTH, 15, true);
		//item.setIcon(icon);  
		return item;
	}

	//################################################################################################
	// Autoupdate  TODO
	@Override
	public void addAutoupdater(final ActionListener al, ChangeListener cl, DocumentListener dl, ColorChangedListener ccl, ItemListener il) {
		getTxtLevels().getDocument().addDocumentListener(dl);

		getCbBlackAsMax().addActionListener(al);
		getCbWhiteAsMin().addActionListener(al);
		getCbInvert().addActionListener(al);

		getBtnMinColor().addColorChangedListener(ccl);
		
		this.al = al;
		this.cl = cl; 
		this.dl = dl;
		this.ccl = ccl; 
		this.il = il;
	}

	@Override
	public void addAutoRepainter(ActionListener al, ChangeListener cl, DocumentListener dl, ColorChangedListener ccl, ItemListener il) {
	}
	

	//################################################################################################
	// LOGIC
	// Paintsclae from Image 
	@Override
	public void setAllViaExistingSettings(SettingsImageOverlay sett) { 
		ImageLogicRunner.setIS_UPDATING(false);

		//rb
		SettingsPaintScale ps = sett.getSettPaintScale(0);
		if(ps!=null) {
			this.getCbWhiteAsMin().setSelected(ps.isUsesWAsMin()); 
			this.getCbBlackAsMax().setSelected(ps.isUsesBAsMax()); 
			this.getCbInvert().setSelected(ps.isInverted()); 
	
			// for all paintscales set active, color, name
			this.getBtnMinColor().setBackground(ps.getMinColor());
			this.getBtnMinColor().setForeground(ps.getMinColor());
	
			// 
			this.getTxtLevels().setText(String.valueOf(ps.getLevels()));
			

			for(int i=0; i<modPSList.length; i++) {
				SettingsPaintScale ps2 = sett.getSettPaintScale(i);
				modPSList[i].setAllViaExistingSettings(ps2);
			}
		}
		// finished
		ImageLogicRunner.setIS_UPDATING(true);
		ImageEditorWindow.getEditor().fireUpdateEvent(true);
	} 
	
	@Override
	public SettingsImageOverlay writeAllToSettings(SettingsImageOverlay sett) {
		if(sett!=null) {
			try {
				boolean inverted = getCbInvert().isSelected();
				boolean black = getCbBlackAsMax().isSelected();
				boolean white = getCbWhiteAsMin().isSelected();
				int levels = intFromTxt(getTxtLevels());
				
				for(int i=0; i<modPSList.length; i++) {
					SettingsPaintScale ps = sett.getSettPaintScale(i);
					modPSList[i].writeAllToSettings(ps);
					
					ps.setInverted(inverted);
					ps.setUsesBAsMax(black);
					ps.setUsesWAsMin(white);
					ps.setLevels(levels);
				}
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		return sett;
	}  
	
	
	private String formatAbsNumber(double in) {
		return in>10? formatAbs.format(in) : formatAbsSmall.format(in);
	}

	private String formatPercentNumber(double in) {
		return formatPerc.format(in);
	}
	//################################################################################################
	// GETTERS AND SETTERS 
	public JTextField getTxtLevels() {
		return txtLevels;
	}
	public JColorPickerButton getBtnMinColor() {
		return btnMinColor;
	}
	public JCheckBox getCbWhiteAsMin() {
		return cbWhiteAsMin;
	}
	public JCheckBox getCbInvert() {
		return cbInvert;
	}
	public JCheckBox getCbBlackAsMax() {
		return cbBlackAsMax;
	}
}
