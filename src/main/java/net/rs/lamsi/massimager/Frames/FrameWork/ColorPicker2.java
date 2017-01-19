package net.rs.lamsi.massimager.Frames.FrameWork;

// ColorPicker.java
// A quick test of the JColorChooser dialog.
//
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;

public class ColorPicker2 { 
	JDialog   dialog;
	JColorChooser chooser = new JColorChooser();
	JButton target;
	Component parent; 
	// changelistener
	protected ColorChangedListener colorChangedListener;

	public ColorPicker2(Component parent) {   
		this.parent = parent; 
		/* geht nicht
  	// nur HSB
      AbstractColorChooserPanel[] panels = chooser.getChooserPanels();
      for (AbstractColorChooserPanel accp : panels) {
          if (!accp.getDisplayName().equals("HSB")) {
          	chooser.removeChooserPanel(accp);
              //c = JOptionPane.showMessageDialog(null, accp);
          }
      }  
		 */
	}
 


	class OkListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Color color = chooser.getColor();
			if(target!=null) {
				target.setBackground(color); 
				if(colorChangedListener!=null) colorChangedListener.colorChanged(color);
			}
		}
	}
	class CancelListener implements ActionListener {
		public void actionPerformed(ActionEvent e) { 
		}
	}
	public void showDialog(JButton btn) {
		this.target = btn; 
		chooser.setColor(btn.getBackground());
		//
		// New Dialog
		try {
			if(dialog == null)
				dialog   = JColorChooser.createDialog(
						parent, // parent comp
						"Pick A Color",  // dialog title
						false,        // modality
						chooser,    
						new OkListener(), 
						new CancelListener());

			dialog.setVisible(true);
			//  
		} catch(Exception ex) {
			ex.printStackTrace(); 
		}
		if(dialog!=null) dialog.setVisible(true);
	} 
	
	public void addColorChangedListener(ColorChangedListener colorChangedListener) {
		this.colorChangedListener = colorChangedListener;
	}
}
