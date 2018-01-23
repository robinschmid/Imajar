package net.rs.lamsi.multiimager.FrameModules.sub.theme;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.util.ResourceBundle;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;

import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.util.ResourceBundleWrapper;

import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.framework.listener.ColorChangedListener;
import net.rs.lamsi.general.framework.modules.Collectable2DSettingsModule;
import net.rs.lamsi.general.settings.image.visualisation.themes.SettingsPaintscaleTheme;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow.LOG;
import net.rs.lamsi.multiimager.Frames.ImageLogicRunner;

public class ModulePaintscaleTheme extends Collectable2DSettingsModule<SettingsPaintscaleTheme, Collectable2D>  {
	// mystuff
	protected boolean isForPrint = true;

	protected static ResourceBundle localizationResources = ResourceBundleWrapper.getBundle("org.jfree.chart.LocalizationBundle");
	private JCheckBox cbPaintscaleInPlot;

	private JTextField txtCSignificantDigits;
	private JCheckBox cbScientificIntensities;
	private JTextField txtPaintScaleTitle;
	private JCheckBox cbUsePaintscaleTitle;
	private JTextField txtPSMarginTop;
	private JTextField txtPSMarginLeft;
	private JTextField txtPSMarginBottom;
	private JTextField txtPSMarginRight;
	private JTextField txtPSWidth;
	private JTextField txtPSTickUnit;
	private JCheckBox cbAutoSelectTickPS;


	public ModulePaintscaleTheme() {
		super("Paint scale appearance", false, SettingsPaintscaleTheme.class, Collectable2D.class);

		JPanel panel_4 = new JPanel();
		getPnContent().add(panel_4, BorderLayout.CENTER);
		panel_4.setLayout(new MigLayout("", "[grow][grow][][grow]", "[][][][5px:n][][][][]"));

		cbPaintscaleInPlot = new JCheckBox("in plot");
		panel_4.add(cbPaintscaleInPlot, "cell 1 0");

		cbScientificIntensities = new JCheckBox("scientific intensities");
		panel_4.add(cbScientificIntensities, "flowy,cell 1 1");
		cbScientificIntensities.setSelected(true);

		txtCSignificantDigits = new JTextField();
		panel_4.add(txtCSignificantDigits, "flowx,cell 2 1");
		txtCSignificantDigits.setText("2");
		txtCSignificantDigits.setToolTipText("Number of significant digits (1.0E >> 2 significant)");
		txtCSignificantDigits.setHorizontalAlignment(SwingConstants.RIGHT);
		txtCSignificantDigits.setColumns(4);

		JLabel lblSignificantDifits = new JLabel("significant difits");
		panel_4.add(lblSignificantDifits, "cell 2 1");

		cbUsePaintscaleTitle = new JCheckBox("use paintscale title");
		panel_4.add(cbUsePaintscaleTitle, "cell 1 2");
		cbUsePaintscaleTitle.setSelected(true);

		txtPaintScaleTitle = new JTextField();
		panel_4.add(txtPaintScaleTitle, "cell 2 2,growx");
		txtPaintScaleTitle.setText("I");
		txtPaintScaleTitle.setColumns(10);

		JLabel lblPsMargin = new JLabel("ps margin");
		panel_4.add(lblPsMargin, "cell 0 4,alignx right");

		JPanel panel = new JPanel();
		panel_4.add(panel, "cell 1 4 2 1");
		panel.setLayout(new MigLayout("", "[][][][]", "[][]"));

		JLabel lblNewLabel = new JLabel("top");
		panel.add(lblNewLabel, "cell 0 0,alignx center");

		JLabel lblLeft = new JLabel("left");
		panel.add(lblLeft, "cell 1 0,alignx center");

		JLabel lblBottom = new JLabel("bottom");
		panel.add(lblBottom, "cell 2 0,alignx center");

		JLabel lblRight = new JLabel("right");
		panel.add(lblRight, "cell 3 0,alignx center");

		txtPSMarginTop = new JTextField();
		txtPSMarginTop.setHorizontalAlignment(SwingConstants.CENTER);
		txtPSMarginTop.setText("0");
		txtPSMarginTop.setToolTipText("Top margin");
		panel.add(txtPSMarginTop, "flowx,cell 0 1,growx");
		txtPSMarginTop.setColumns(4);

		txtPSMarginLeft = new JTextField();
		txtPSMarginLeft.setHorizontalAlignment(SwingConstants.CENTER);
		txtPSMarginLeft.setText("0");
		txtPSMarginLeft.setToolTipText("Left margin");
		panel.add(txtPSMarginLeft, "cell 1 1");
		txtPSMarginLeft.setColumns(4);

		txtPSMarginBottom = new JTextField();
		txtPSMarginBottom.setHorizontalAlignment(SwingConstants.CENTER);
		txtPSMarginBottom.setText("0");
		txtPSMarginBottom.setToolTipText("Bottom margin");
		panel.add(txtPSMarginBottom, "cell 2 1,growx");
		txtPSMarginBottom.setColumns(4);

		txtPSMarginRight = new JTextField();
		txtPSMarginRight.setHorizontalAlignment(SwingConstants.CENTER);
		txtPSMarginRight.setToolTipText("Right margin");
		txtPSMarginRight.setText("0");
		txtPSMarginRight.setColumns(4);
		panel.add(txtPSMarginRight, "cell 3 1,growx");

		JLabel lblWidth = new JLabel("width");
		panel_4.add(lblWidth, "cell 0 5,alignx trailing");

		txtPSWidth = new JTextField();
		txtPSWidth.setToolTipText("Paint scale legend width");
		txtPSWidth.setText("20");
		panel_4.add(txtPSWidth, "flowy,cell 1 5 2 1,alignx left");
		txtPSWidth.setColumns(4);

		cbAutoSelectTickPS = new JCheckBox("auto select tick unit");
		cbAutoSelectTickPS.setSelected(true);
		panel_4.add(cbAutoSelectTickPS, "cell 1 6 2 1");

		JLabel lblTickUnit = new JLabel("tick unit");
		panel_4.add(lblTickUnit, "cell 0 7,alignx trailing");

		txtPSTickUnit = new JTextField();
		txtPSTickUnit.setToolTipText("Tick unit (manual; uncheck auto selection)");
		panel_4.add(txtPSTickUnit, "cell 1 7 2 1,alignx left");
		txtPSTickUnit.setColumns(12);
	}

	//################################################################################################
	// Autoupdate
	@Override
	public void addAutoupdater(ActionListener al, ChangeListener cl, DocumentListener dl, ColorChangedListener ccl, ItemListener il) {
		getCbPaintscaleInPlot().addItemListener(il);
	}

	@Override
	public void addAutoRepainter(final ActionListener al, ChangeListener cl, DocumentListener dl, ColorChangedListener ccl, ItemListener il) {
		getCbScientificIntensities().addItemListener(il);
		getTxtCSignificantDigits().getDocument().addDocumentListener(dl);
		getCbUsePaintscaleTitle().addItemListener(il);
		getTxtPaintScaleTitle().getDocument().addDocumentListener(dl);

		// ps margin
		getTxtPSMarginBottom().getDocument().addDocumentListener(dl);
		getTxtPSMarginLeft().getDocument().addDocumentListener(dl);
		getTxtPSMarginRight().getDocument().addDocumentListener(dl);
		getTxtPSMarginTop().getDocument().addDocumentListener(dl);
		getTxtPSWidth().getDocument().addDocumentListener(dl);

		// tick unit
		getCbAutoSelectTickPS().addItemListener(il);
		getTxtPSTickUnit().getDocument().addDocumentListener(dl);

	}

	//################################################################################################
	// LOGIC
	@Override
	public void setAllViaExistingSettings(SettingsPaintscaleTheme t) throws Exception {  
		ImageLogicRunner.setIS_UPDATING(false);

		if(t!= null) {
			getCbPaintscaleInPlot().setSelected(t.isPaintScaleInPlot());
			// significant scientific notion
			getCbScientificIntensities().setSelected(t.isUseScientificIntensities());
			getTxtCSignificantDigits().setText(String.valueOf(t.getSignificantDigits()));

			// ps margin and width
			RectangleInsets r = t.getPsMargin();
			getTxtPSMarginBottom().setText(String.valueOf(r.getBottom()));
			getTxtPSMarginLeft().setText(String.valueOf(r.getLeft()));
			getTxtPSMarginTop().setText(String.valueOf(r.getTop()));
			getTxtPSMarginRight().setText(String.valueOf(r.getRight()));

			getTxtPSWidth().setText(String.valueOf(t.getPsWidth()));
			getTxtPSTickUnit().setText(String.valueOf(t.getPsTickUnit()));

			getCbAutoSelectTickPS().setSelected(t.isAutoSelectTickUnit());

			// paintscale title
			getCbUsePaintscaleTitle().setSelected(t.isUsePaintScaleTitle());
			getTxtPaintScaleTitle().setText(t.getPaintScaleTitle());
		}
		else {
			ImageEditorWindow.log("null settings for SettingsPaintscaleTheme", LOG.DEBUG);
		}
		// finished
		ImageLogicRunner.setIS_UPDATING(true);
		//		ImageEditorWindow.getEditor().fireUpdateEvent(true);
	} 

	@Override
	public SettingsPaintscaleTheme writeAllToSettings(SettingsPaintscaleTheme t) {
		if(t!=null) {
			try {
				RectangleInsets r = new RectangleInsets(doubleFromTxt(getTxtPSMarginTop()), 
						doubleFromTxt(getTxtPSMarginLeft()), doubleFromTxt(getTxtPSMarginBottom()), doubleFromTxt(getTxtPSMarginRight()));
				// setall
				t.setAll(getCbPaintscaleInPlot().isSelected(), cbScientificIntensities.isSelected(), 
						intFromTxt(txtCSignificantDigits), txtPaintScaleTitle.getText(), cbUsePaintscaleTitle.isSelected(),
						r, intFromTxt(getTxtPSWidth()), doubleFromTxt(getTxtPSTickUnit()), getCbAutoSelectTickPS().isSelected());
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		return t;
	}


	//################################################################################################
	// GETTERS AND SETTERS 
	public JCheckBox getCbPaintscaleInPlot() {
		return cbPaintscaleInPlot;
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
	public JTextField getTxtPSMarginTop() {
		return txtPSMarginTop;
	}
	public JTextField getTxtPSMarginLeft() {
		return txtPSMarginLeft;
	}
	public JTextField getTxtPSMarginBottom() {
		return txtPSMarginBottom;
	}
	public JTextField getTxtPSMarginRight() {
		return txtPSMarginRight;
	}
	public JTextField getTxtPSWidth() {
		return txtPSWidth;
	}
	public JTextField getTxtPSTickUnit() {
		return txtPSTickUnit;
	}
	public JCheckBox getCbAutoSelectTickPS() {
		return cbAutoSelectTickPS;
	}
}
