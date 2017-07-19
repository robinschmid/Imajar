package net.rs.lamsi.multiimager.FrameModules.sub.theme;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;

import org.jfree.data.Range;

import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.framework.basics.JFontSpecs;
import net.rs.lamsi.general.framework.listener.ColorChangedListener;
import net.rs.lamsi.general.framework.modules.Collectable2DSettingsModule;
import net.rs.lamsi.general.heatmap.Heatmap;
import net.rs.lamsi.general.settings.image.sub.SettingsZoom;
import net.rs.lamsi.general.settings.image.visualisation.themes.SettingsScaleInPlot;
import net.rs.lamsi.multiimager.Frames.ImageLogicRunner;

public class ModuleScaleInPlot extends Collectable2DSettingsModule<SettingsScaleInPlot, Collectable2D>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JCheckBox cbShowScale;
	private JTextField txtScaleUnit;
	private JTextField txtScaleValue;
	private JTextField txtScaleFactor;
	private JSlider sliderScaleXPos;
	private JSlider sliderScaleYPos;
	private JFontSpecs fontScale;
	
	public ModuleScaleInPlot() {
		super("Scale in plot", false, SettingsScaleInPlot.class, Collectable2D.class);  
		
		JPanel panel = new JPanel();
		getPnContent().add(panel, BorderLayout.CENTER);
		panel.setLayout(new MigLayout("", "[grow][][grow]", "[][]"));
		

		cbShowScale = new JCheckBox("show scale");
		panel.add(cbShowScale, "cell 1 0");
		
		JPanel panel_4 = new JPanel();
		panel.add(panel_4, "cell 1 1");
		panel_4.setLayout(new MigLayout("", "[][85.00][]", "[][][][][][grow][grow]"));
		
		JLabel lblUnit = new JLabel("value");
		panel_4.add(lblUnit, "cell 0 0,alignx trailing");
		
		txtScaleValue = new JTextField();
		txtScaleValue.setToolTipText("Value for scale width");
		txtScaleValue.setText("1");
		txtScaleValue.setHorizontalAlignment(SwingConstants.TRAILING);
		panel_4.add(txtScaleValue, "flowx,cell 1 0,alignx left");
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
		panel_4.add(txtScaleFactor, "cell 1 1,alignx left");
		txtScaleFactor.setColumns(10);
		
		JLabel lblXPos = new JLabel("x pos");
		panel_4.add(lblXPos, "cell 0 3,alignx trailing");
		
		sliderScaleXPos = new JSlider();
		sliderScaleXPos.setPreferredSize(new Dimension(100, 23));
		sliderScaleXPos.setMinimumSize(new Dimension(100, 23));
		sliderScaleXPos.setValue(90);
		panel_4.add(sliderScaleXPos, "cell 1 3 2 1,growx");
		
		JLabel lblYPos = new JLabel("y pos");
		panel_4.add(lblYPos, "cell 0 4,alignx trailing");
		
		sliderScaleYPos = new JSlider();
		sliderScaleYPos.setValue(10);
		sliderScaleYPos.setPreferredSize(new Dimension(100, 23));
		sliderScaleYPos.setMinimumSize(new Dimension(100, 23));
		panel_4.add(sliderScaleYPos, "cell 1 4 2 1,growx");
		
		fontScale = new JFontSpecs();
		panel_4.add(fontScale, "cell 0 5 3 1,alignx left,growy");
		
		JLabel lblUnit_1 = new JLabel("unit");
		panel_4.add(lblUnit_1, "cell 2 0");
		
		setMaxPresets(15);
	}
	
	
	//################################################################################################
	// Autoupdate
	@Override
	public void addAutoupdater(ActionListener al, ChangeListener cl, DocumentListener dl, ColorChangedListener ccl, ItemListener il) {
	}
	
	@Override
	public void addAutoRepainter(ActionListener al, ChangeListener cl, DocumentListener dl, ColorChangedListener ccl, ItemListener il) {
		getCbShowScale().addItemListener(il);
		getTxtScaleFactor().getDocument().addDocumentListener(dl);
		getTxtScaleValue().getDocument().addDocumentListener(dl);
		getTxtScaleUnit().getDocument().addDocumentListener(dl);
		getSliderScaleXPos().addChangeListener(cl);
		getSliderScaleYPos().addChangeListener(cl);
		getFontScale().addListener(ccl, il, dl);
	}

	//################################################################################################
	// LOGIC
	// Paintsclae from Image
	@Override
	public void setAllViaExistingSettings(SettingsScaleInPlot s) {  
		ImageLogicRunner.setIS_UPDATING(false);

		getCbShowScale().setSelected(s.isShowScale());
		// set all txt scale
		getTxtScaleFactor().setText(String.valueOf(s.getScaleFactor()));
		getTxtScaleValue().setText(String.valueOf(s.getScaleValue()));
		getTxtScaleUnit().setText(String.valueOf(s.getScaleUnit()));  
		// scale slider 
		getSliderScaleXPos().setValue((int)(s.getScaleXPos()*100));
		getSliderScaleYPos().setValue((int)(s.getScaleYPos()*100)); 
		getFontScale().setSelectedFont(s.getFontScaleInPlot());
		getFontScale().setColor(s.getScaleFontColor());
		
		// finished
		ImageLogicRunner.setIS_UPDATING(true);
		//ImageEditorWindow.getEditor().fireUpdateEvent(true);
	} 

	@Override
	public SettingsScaleInPlot writeAllToSettings(SettingsScaleInPlot s) {
		if(s!=null) {
			try {
				s.setAll(getCbShowScale().isSelected(), getTxtScaleUnit().getText(), floatFromTxt(getTxtScaleFactor()), 
						floatFromTxt(getTxtScaleValue()), 
						getSliderScaleXPos().getValue()/100.f, getSliderScaleYPos().getValue()/100.f, 
						getFontScale().getSelectedFont(), getFontScale().getColor());
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		return s;
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
	public JSlider getSliderScaleXPos() {
		return sliderScaleXPos;
	}
	public JSlider getSliderScaleYPos() {
		return sliderScaleYPos;
	}
	public JFontSpecs getFontScale() {
		return fontScale;
	}
}
