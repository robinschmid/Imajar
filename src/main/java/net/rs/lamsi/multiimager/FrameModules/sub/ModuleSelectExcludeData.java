package net.rs.lamsi.multiimager.FrameModules.sub;


import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;

import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.framework.basics.ColorChangedListener;
import net.rs.lamsi.general.framework.modules.Collectable2DSettingsModule;
import net.rs.lamsi.general.heatmap.Heatmap;
import net.rs.lamsi.general.settings.image.selection.SettingsSelections;
import net.rs.lamsi.general.settings.image.selection.SettingsShapeSelection.SelectionMode;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.dialogs.selectdata.Image2DSelectDataAreaDialog;

public class ModuleSelectExcludeData extends Collectable2DSettingsModule<SettingsSelections, Image2D> { 
	//
	private ImageEditorWindow window;
	private JLabel lbExcludedRects;
	private JLabel lbUsedDataPerc;
	private JLabel lbSelectedRects;
	private JCheckBox cbShowSelExcl;
	private JButton btnOpenSelectData;

	/**
	 * Create the panel.
	 */
	public ModuleSelectExcludeData(ImageEditorWindow wnd) {
		super("Data selection", false, SettingsSelections.class, Image2D.class);  
		window = wnd;
		setShowTitleAlways(true);
		
		JPanel panel = new JPanel();
		getPnContent().add(panel, BorderLayout.CENTER);
		panel.setLayout(new MigLayout("", "[][][]", "[][][][][]"));
		
		cbShowSelExcl = new JCheckBox("Show selected and exluded rects");
		cbShowSelExcl.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				// if changed send signal to window.
				if(currentHeat!=null) {
					currentHeat.showSelectedExcludedRects(cbShowSelExcl.isSelected());
				}
			}
		});
		cbShowSelExcl.setToolTipText("Selected rects (black); Excluded rects (red).");
		panel.add(cbShowSelExcl, "cell 0 0 3 1");
		
		JLabel lblSelectedRects = new JLabel("Selected rects:");
		panel.add(lblSelectedRects, "cell 1 1,alignx trailing");
		
		lbSelectedRects = new JLabel("0");
		lbSelectedRects.setFont(new Font("Tahoma", Font.BOLD, 11));
		panel.add(lbSelectedRects, "cell 2 1");
		
		JLabel lblExcludedRects = new JLabel("Excluded rects:");
		panel.add(lblExcludedRects, "cell 1 2,alignx trailing");
		
		lbExcludedRects = new JLabel("0");
		lbExcludedRects.setFont(new Font("Tahoma", Font.BOLD, 11));
		panel.add(lbExcludedRects, "cell 2 2");
		
		JLabel lblUsedData = new JLabel("Used data:");
		lblUsedData.setToolTipText("Percentage of used data as SELECTED-EXCLUDED.");
		panel.add(lblUsedData, "cell 1 3,alignx trailing");
		
		lbUsedDataPerc = new JLabel("100%");
		lbUsedDataPerc.setFont(new Font("Tahoma", Font.BOLD, 11));
		panel.add(lbUsedDataPerc, "cell 2 3");
		
		btnOpenSelectData = new JButton("Select data");
		btnOpenSelectData.addActionListener(new ActionListener() { 
			@Override
			public void actionPerformed(ActionEvent e) {
				// open dialog with image ex
				if(currentImage!=null && currentImage.isImage2D()) {
					final Image2DSelectDataAreaDialog dialog = new Image2DSelectDataAreaDialog();
					dialog.startDialog((Image2D) currentImage);
					WindowAdapter wl = new WindowAdapter() {
						@Override
						public void windowClosed(WindowEvent e) { 
							super.windowClosed(e); 
							// show selected excluded rects 
							if(currentHeat!=null) {
								currentHeat.updateSelectedExcludedRects();
								// show stats
								Image2D img = ((Image2D)currentHeat.getImage());
								SettingsSelections sel = img.getSettings().getSettSelections();
								
								getLbSelectedRects().setText(String.valueOf(sel.count(SelectionMode.SELECT)));
								getLbExcludedRects().setText(String.valueOf(sel.count(SelectionMode.EXCLUDE)));
								double used = Math.round(img.getSelectedDPCount(true)*1000.0/img.getTotalDPCount())/10.0;
								getLbUsedDataPerc().setText(String.valueOf(used));
							}
						}
					};
					dialog.addWindowListener(wl);
				}
			}
		});
		panel.add(btnOpenSelectData, "cell 1 4");
	}
	
	/**
	 * show data selection
	 */
	@Override
	public void setCurrentHeatmap(Heatmap heat) { 
		super.setCurrentHeatmap(heat);
		// show selected excluded rects 
		if(currentHeat!=null)
			currentHeat.showSelectedExcludedRects(cbShowSelExcl.isSelected());
	}

	@Override
	public void setAllViaExistingSettings(SettingsSelections sel) throws Exception {
		getLbSelectedRects().setText(String.valueOf(sel.count(SelectionMode.SELECT)));
		getLbExcludedRects().setText(String.valueOf(sel.count(SelectionMode.EXCLUDE)));
		double used = Math.round(currentImage.getSelectedDPCount(true)*1000.0/currentImage.getTotalDPCount())/10.0;
		getLbUsedDataPerc().setText(String.valueOf(used));
	}

	@Override
	public SettingsSelections writeAllToSettings(SettingsSelections si) {
		
		return si;
	}
	
	public JButton getBtnOpenSelectData() {
		return btnOpenSelectData;
	}
	
	public JLabel getLbExcludedRects() {
		return lbExcludedRects;
	}
	public JLabel getLbUsedDataPerc() {
		return lbUsedDataPerc;
	}
	public JLabel getLbSelectedRects() {
		return lbSelectedRects;
	}

	@Override
	public void addAutoupdater(ActionListener al, ChangeListener cl, DocumentListener dl, ColorChangedListener ccl, ItemListener il) {
	}
	@Override
	public void addAutoRepainter(ActionListener al, ChangeListener cl, DocumentListener dl, ColorChangedListener ccl, ItemListener il) {
	}
}
