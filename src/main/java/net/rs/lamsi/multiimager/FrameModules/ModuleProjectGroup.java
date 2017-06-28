package net.rs.lamsi.multiimager.FrameModules;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

import de.schlichtherle.truezip.util.Link;
import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.general.datamodel.image.ImageGroupMD;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.framework.listener.DelayedDocumentListener;
import net.rs.lamsi.general.framework.modules.Module;
import net.rs.lamsi.general.framework.modules.interf.SettingsModuleObject;
import net.rs.lamsi.general.settings.SettingsContainerSettings;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;

/** part of every {@link MainSettingsModuleContainer}
 * 
 */
public class ModuleProjectGroup extends Module implements SettingsModuleObject<Collectable2D> {
	private JTextField txtProject;
	private JTextField txtGroup;
	
	private Collectable2D img = null;
	
	public ModuleProjectGroup() {
		getLbTitle().setText("Project and group");
		
		JPanel panel = new JPanel();
		getPnContent().add(panel, BorderLayout.CENTER);
		panel.setLayout(new MigLayout("", "[][grow]", "[][]"));
		
		JLabel lblProject = new JLabel("project");
		panel.add(lblProject, "cell 0 0");
		
		txtProject = new JTextField();
		panel.add(txtProject, "cell 1 0,growx");
		txtProject.setColumns(20);
		
		JLabel lblGroup = new JLabel("group");
		panel.add(lblGroup, "cell 0 1,alignx trailing");
		
		txtGroup = new JTextField();
		txtGroup.setColumns(20);
		panel.add(txtGroup, "cell 1 1,growx");
		
		txtProject.getDocument().addDocumentListener(new DelayedDocumentListener() {
			@Override
			public void documentChanged(DocumentEvent e) {
				if(img!=null) {
					this.setActive(false);
					// try to change
					String result = img.setProjectName(txtProject.getText());
					// if not change back
					if(!result.equals(txtProject.getText()))
						txtProject.setText(result);
					else ImageEditorWindow.getEditor().getModuleTreeImages().repaint();
					this.setActive(true);
				}
			}
		});
		

		txtGroup.getDocument().addDocumentListener(new DelayedDocumentListener() {
			@Override
			public void documentChanged(DocumentEvent e) {
				if(img!=null) {
					this.setActive(false);
					// try to change
					String result = img.setGroupName(txtGroup.getText());
					// if not change back
					if(!result.equals(txtGroup.getText()))
						txtGroup.setText(result);
					else ImageEditorWindow.getEditor().getModuleTreeImages().repaint();
					this.setActive(true);
				}
			}
		});
	}

	
	
	public JTextField getTxtProject() {
		return txtProject;
	}
	public JTextField getTxtGroup() {
		return txtGroup;
	}

	@Override
	public void setCurrentImage(Collectable2D img, boolean setAllToPanel) {
		this.img = img;
		ImageGroupMD g = img.getImageGroup();
		if(g!=null) {
			getTxtGroup().setText(g.getName());
			if(g.getProject()!=null)
				getTxtProject().setText(g.getProject().getName());
		}
	}

	@Override
	public Collectable2D getCurrentImage() {
		return img;
	}
}
