package net.rs.lamsi.multiimager.FrameModules;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;

import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.DatasetContinuousMD;
import net.rs.lamsi.massimager.Frames.FrameWork.ColorChangedListener;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.ImageSettingsModule;
import net.rs.lamsi.massimager.Settings.image.SettingsImageContinousSplit;
import net.rs.lamsi.massimager.Settings.image.SettingsImage.XUNIT;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageLogicRunner;

public class ModuleSplitContinousImage extends ImageSettingsModule<SettingsImageContinousSplit> {
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JTextField txtSplitValue;
	private JTextField txtAdd;
	private ImageEditorWindow window;
	private JComboBox comboXunit;
	private JTextField txtStartX;
	
	
	//

	/**
	 * Create the panel.
	 */
	public ModuleSplitContinousImage(ImageEditorWindow wnd) {
		super("Split continous data", false, SettingsImageContinousSplit.class);    
		window = wnd;
		
		JPanel panel = new JPanel();
		getPnContent().add(panel, BorderLayout.CENTER);
		panel.setLayout(new MigLayout("", "[][]", "[][][]"));
		
		JLabel lblSplit = new JLabel("split");
		panel.add(lblSplit, "flowx,cell 0 0,alignx trailing");
		
		JButton btnMinus = new JButton("-");
		btnMinus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if(comboXunit.getSelectedItem().equals(XUNIT.DP)) {
						// int
						int value = intFromTxt(getTxtSplitValue());
						int add = (int) doubleFromTxt(getTxtAdd());
						value -= add;
						if(value<0) value = 0;
						getTxtSplitValue().setText(String.valueOf(value));
					}
					else {
						// double
						double value = doubleFromTxt(getTxtSplitValue());
						double add = doubleFromTxt(getTxtAdd());
						value -= add;
						if(value<0) value = 0;
						getTxtSplitValue().setText(String.valueOf(value));
					}
					// update:
					window.fireUpdateEvent(true);
				}catch(Exception ex) { 
				}
			}
		});
		
		txtSplitValue = new JTextField();
		txtSplitValue.setToolTipText("Split value. data point count or x.");
		txtSplitValue.setText("10");
		panel.add(txtSplitValue, "flowx,cell 1 0,aligny top");
		txtSplitValue.setColumns(10);
		
		JLabel lblStartX = new JLabel("start x");
		panel.add(lblStartX, "cell 0 1,alignx trailing");
		
		txtStartX = new JTextField();
		panel.add(txtStartX, "cell 1 1,alignx left");
		txtStartX.setColumns(10);
		panel.add(btnMinus, "flowx,cell 0 2");
		
		comboXunit = new JComboBox();
		comboXunit.setModel(new DefaultComboBoxModel(XUNIT.values()));
		panel.add(comboXunit, "cell 1 0");
		
		txtAdd = new JTextField();
		txtAdd.setText("5");
		panel.add(txtAdd, "flowx,cell 1 2");
		txtAdd.setColumns(6);
		
		JButton btnPlus = new JButton("+");
		btnPlus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if(comboXunit.getSelectedItem().equals(XUNIT.DP)) {
						// int
						int value = intFromTxt(getTxtSplitValue());
						int add = (int) doubleFromTxt(getTxtAdd());
						value += add;
						if(value<0) value = 0;
						getTxtSplitValue().setText(String.valueOf(value));
					}
					else {
						// double
						double value = doubleFromTxt(getTxtSplitValue());
						double add = doubleFromTxt(getTxtAdd());
						value += add;
						if(value<0) value = 0;
						getTxtSplitValue().setText(String.valueOf(value));
					}
					// update:
					window.fireUpdateEvent(true);
				}catch(Exception ex) { 
				}
			}
		});
		panel.add(btnPlus, "cell 1 2");
	}
	
	// apply for print or presentation before changing settings
	@Override
	public void setSettings(SettingsImageContinousSplit settings) {
		super.setSettings(settings);
	}
	
	//################################################################################################
	// Autoupdate
	@Override
	public void addAutoupdater(ActionListener al, ChangeListener cl, DocumentListener dl, ColorChangedListener ccl, ItemListener il) {
		//TODO
		getTxtSplitValue().getDocument().addDocumentListener(dl);
		comboXunit.addItemListener(il);
		getTxtStartX().getDocument().addDocumentListener(dl);  
	}

	//################################################################################################
	// LOGIC
	@Override
	public void setAllViaExistingSettings(SettingsImageContinousSplit st) {  
		if(st!=null) {
			ImageLogicRunner.setIS_UPDATING(false);
			// new reseted ps
			comboXunit.setSelectedItem(st.getSplitMode());
			
			if(st.getSplitMode()==XUNIT.DP)
				getTxtSplitValue().setText(String.valueOf(st.getSplitAfterDP()));
			else getTxtSplitValue().setText(String.valueOf(st.getSplitAfterX()));
			
			this.getTxtStartX().setText(String.valueOf(st.getStartX())); 
			
			// finished
			ImageLogicRunner.setIS_UPDATING(true);
			ImageEditorWindow.getEditor().fireUpdateEvent(true);
		}
	} 

	@Override
	public SettingsImageContinousSplit writeAllToSettings(SettingsImageContinousSplit sett) {
		if(sett!=null) {
			try {
				// setall
				boolean changed = false;
				if(sett.getSplitMode()!=comboXunit.getSelectedItem()) {
					changed = true;
					sett.setSplitMode((XUNIT)comboXunit.getSelectedItem());
				}  
				
				float startx = floatFromTxt(getTxtStartX());
				if(startx!=sett.getStartX()) {
					sett.setStartX(startx);
					changed = true;
				}

				if(sett.getSplitMode()==XUNIT.DP) {
					int val = intFromTxt(getTxtSplitValue());
					if(val != sett.getSplitAfterDP()){
						sett.setSplitAfterDP(val);
						changed = true;
					}
				}
				else {
					float val = floatFromTxt(getTxtSplitValue());
					if(val != sett.getSplitAfterX()){
						sett.setSplitAfterX(val);
						changed = true;
					}
				}
				
				// update image
				if(changed && currentImage!=null && isContinuousData(currentImage)) {
					((DatasetContinuousMD)currentImage.getData()).setSplitSettings(sett);
				}
				
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		return sett;
	}
	
	@Override
	public void setCurrentImage(Image2D img) { 
		super.setCurrentImage(img);
		this.setVisible(isContinuousData(img));
	}
	
	/**
	 * continuous data set?
	 * @param img
	 * @return
	 */
	private boolean isContinuousData(Image2D img) {
		return DatasetContinuousMD.class.isInstance(img.getData());
	}

	//################################################################################################
	// GETTERS AND SETTERS  
	public JTextField getTxtSplitValue() {
		return txtSplitValue;
	}
	public JTextField getTxtAdd() {
		return txtAdd;
	} 
	public JComboBox getComboXunit() {
		return comboXunit;
	}

	public JTextField getTxtStartX() {
		return txtStartX;
	}
}
