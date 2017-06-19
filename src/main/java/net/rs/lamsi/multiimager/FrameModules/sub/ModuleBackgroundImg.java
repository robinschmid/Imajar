package net.rs.lamsi.multiimager.FrameModules.sub;


import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;

import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.framework.listener.ColorChangedListener;
import net.rs.lamsi.general.framework.modules.Collectable2DSettingsModule;
import net.rs.lamsi.general.settings.image.visualisation.SettingsBackgroundImg;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageLogicRunner;

public class ModuleBackgroundImg extends Collectable2DSettingsModule<SettingsBackgroundImg, Collectable2D> {
	private JTextField txtPath; 
	private JTextField txtAngle;
	private JTextField txtWidth;
	private JTextField txtX;
	private JTextField txtY;
	private JCheckBox cbActive;
	// action listener for update
	private ActionListener al;
	private JButton btnAddImage;
	//
	
	// AUTOGEN

	/**
	 * Create the panel.
	 */
	public ModuleBackgroundImg() {
		super("Background", false, SettingsBackgroundImg.class, Collectable2D.class);  
		
		JPanel panel = new JPanel();
		getPnContent().add(panel, BorderLayout.CENTER);
		panel.setLayout(new MigLayout("", "[][][grow]", "[][][bottom][][]"));
		
		btnAddImage = new JButton("Add image");
		btnAddImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File f = ImageEditorWindow.getEditor().getLogicRunner().importMicroscopicImageBG();
				getSettings().setPathBGImage(f);
				if(f!=null)
					getTxtPath().setText(f.getAbsolutePath());
				else 
					getTxtPath().setText("No background image selected");
				
				cbActive.setSelected(f!=null);
			}
		});
		panel.add(btnAddImage, "cell 0 0");
		
		txtPath = new JTextField();
		txtPath.setEditable(false);
		txtPath.setText("No background image selected");
		panel.add(txtPath, "cell 1 0 2 1,growx");
		txtPath.setColumns(10);
		
		JLabel lblOffset = new JLabel("Offset");
		panel.add(lblOffset, "cell 0 2,alignx trailing");
		lblOffset.setHorizontalAlignment(SwingConstants.TRAILING);
		
		JPanel panel_2 = new JPanel();
		panel.add(panel_2, "cell 1 2,grow");
		panel_2.setLayout(new MigLayout("", "[][]", "[][]"));
		
		JLabel lblX = new JLabel("x");
		panel_2.add(lblX, "cell 0 0,alignx center");
		
		JLabel lblY = new JLabel("y");
		panel_2.add(lblY, "cell 1 0,alignx center");
		
		txtX = new JTextField();
		txtX.setText("0");
		panel_2.add(txtX, "cell 0 1,alignx left");
		txtX.setColumns(8);
		
		txtY = new JTextField();
		txtY.setText("0");
		txtY.setColumns(8);
		panel_2.add(txtY, "cell 1 1,alignx left");
		
		JLabel lblWidth = new JLabel("Width");
		panel.add(lblWidth, "cell 0 3,alignx trailing");
		
		txtWidth = new JTextField();
		txtWidth.setText("0");
		txtWidth.setColumns(8);
		panel.add(txtWidth, "cell 1 3,alignx left");
		
		JLabel lblAngle = new JLabel("Angle");
		panel.add(lblAngle, "cell 0 4,alignx trailing");
		
		txtAngle = new JTextField();
		txtAngle.setText("0");
		panel.add(txtAngle, "cell 1 4,alignx left");
		txtAngle.setColumns(8);
		
		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
		getPnTitle().add(panel_1, BorderLayout.CENTER);
		
		cbActive = new JCheckBox("Background");
		panel_1.add(cbActive);
		
	}
	
	public JButton getBtnAddImage() {
		return btnAddImage;
	}
	
	//################################################################################################
	// Autoupdate
	@Override
	public void addAutoupdater(ActionListener al, ChangeListener cl, DocumentListener dl, ColorChangedListener ccl, ItemListener il) {
		this.al = al;
	}
	
	@Override
	public void addAutoRepainter(ActionListener al, ChangeListener cl, DocumentListener dl, ColorChangedListener ccl, ItemListener il) {
		getTxtX().getDocument().addDocumentListener(dl);
		getTxtY().getDocument().addDocumentListener(dl);
		getTxtWidth().getDocument().addDocumentListener(dl);
		getTxtAngle().getDocument().addDocumentListener(dl);
		cbActive.addItemListener(il);
	}

	//################################################################################################
	// LOGIC
	// Paintsclae from Image
	@Override
	public void setAllViaExistingSettings(SettingsBackgroundImg s) {  
		ImageLogicRunner.setIS_UPDATING(false);
		// set all to panels
		File f = s.getPathBGImage();
		if(f!=null) 
			getTxtPath().setText(f.getAbsolutePath());
		else 
			getTxtPath().setText("No background image selected");
		
		getCbActive().setSelected(s.isVisible());
		getTxtAngle().setText(String.valueOf(s.getAngle()));
		getTxtX().setText(String.valueOf(s.getOffset().getX()));
		getTxtY().setText(String.valueOf(s.getOffset().getY()));
		getTxtWidth().setText(String.valueOf(s.getBgWidth()));
		
		// finished
		ImageLogicRunner.setIS_UPDATING(true);
		//ImageEditorWindow.getEditor().fireUpdateEvent(true);
	} 

	@Override
	public SettingsBackgroundImg writeAllToSettings(SettingsBackgroundImg s) {
		if(s!=null) {
			try {
				// set all to s
				s.setAngle(doubleFromTxt(getTxtAngle()));
				s.setOffset(doubleFromTxt(getTxtX()), doubleFromTxt(getTxtY()));
				s.setBgWidth((doubleFromTxt(getTxtWidth())));
				s.setVisible(getCbActive().isSelected());
				
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		return s;
	}
	
	//################################################################################################
	// GETTERS AND SETTERS 
	
	public JTextField getTxtPath() {
		return txtPath;
	}
	public JTextField getTxtX() {
		return txtX;
	}
	public JTextField getTxtY() {
		return txtY;
	}
	public JTextField getTxtWidth() {
		return txtWidth;
	}
	public JTextField getTxtAngle() {
		return txtAngle;
	}
	public JCheckBox getCbActive() {
		return cbActive;
	}
}
