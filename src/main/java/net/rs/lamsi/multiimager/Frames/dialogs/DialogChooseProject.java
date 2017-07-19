package net.rs.lamsi.multiimager.Frames.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.openscience.cdk.renderer.elements.path.Close;

import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.general.datamodel.image.ImagingProject;
import net.rs.lamsi.general.framework.modules.ModuleTree;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.utils.DialogLoggerUtil;

public class DialogChooseProject  extends JDialog {

	private static DialogChooseProject inst = new DialogChooseProject();
	
	private final JPanel contentPanel = new JPanel();
	private JTextField txtCurrent;
	private JTextField txtNewProject;
	
	private ImagingProject project = null;
	private ModuleTree tree;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			CroppingDialog dialog = new CroppingDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public DialogChooseProject() {
		final JDialog thisdialog = this;
		setTitle("Select Project");
		setBounds(100, 100, 256, 175);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[grow][]", "[][][][]"));
		{
			JLabel lblTheCurrentProject = new JLabel("The current project is selected by default");
			contentPanel.add(lblTheCurrentProject, "cell 0 0 2 1");
		}
		{
			txtCurrent = new JTextField();
			txtCurrent.setText("current");
			contentPanel.add(txtCurrent, "cell 0 2,growx");
			txtCurrent.setColumns(12);
		}
		{
			JButton btnChooseExisting = new JButton("Choose existing");
			btnChooseExisting.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ModuleTree tree = ImageEditorWindow.getEditor().getModuleTreeImages();
					//
					TreePath[] paths = DialogLoggerUtil.showTreeDialogAndChoose(thisdialog, tree.getRoot(), 
							TreeSelectionModel.SINGLE_TREE_SELECTION, tree.getTree().getSelectionPaths(), 
							"Single selection", "Select one image");
					if(paths!=null && paths.length>0) {
						project = tree.getProject(paths[0]);
						if(project!=null)
							getTxtCurrent().setText(project.getName());
					}
				}
			});
			contentPanel.add(btnChooseExisting, "cell 1 2");
		}
		{
			txtNewProject = new JTextField();
			contentPanel.add(txtNewProject, "cell 0 3,growx");
			txtNewProject.setColumns(12);
		}
		{
			JButton btnCreateNew = new JButton("Create new");
			btnCreateNew.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					//
					if(createNewProject())
					setVisible(false);
				}
			});
			contentPanel.add(btnCreateNew, "cell 1 3,growx");
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						//
						setVisible(false);
					}
				});
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
				
				JButton createNew = new JButton("Create new");
				createNew.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						//
						if(createNewProject())
						setVisible(false);
					}
				});
				buttonPane.add(createNew);
			}
		}
		// wait for this dialog
		setModalityType(ModalityType.APPLICATION_MODAL);
	}
	
	/**
	 * returns a selected or new project
	 * @param project
	 * @param tree
	 * @return
	 */
	public static ImagingProject choose(ImagingProject project, ModuleTree tree) {
		inst.init(project, tree);
		inst.setVisible(true);
		return inst.project;
	}

	/**
	 * returns a selected or new project
	 * @param project
	 * @param tree
	 * @return
	 */
	public static ImagingProject choose(ImagingProject project, ModuleTree tree, String newProject) {
		inst.init(project, tree, newProject);
		inst.setVisible(true);
		return inst.project;
	}

	private void init(ImagingProject project2, ModuleTree tree2) {
		if(project2!=null)
			project = project2;
		tree = tree2;
		if(project!=null)
			getTxtCurrent().setText(project.getName());
		else getTxtCurrent().setText("");
		
		getTxtNewProject().setText("");
	}
	private void init(ImagingProject project2, ModuleTree tree2, String newP) {
		init(project2, tree2);
		getTxtNewProject().setText(newP);
	}

	protected boolean createNewProject() {
		String pname = getTxtNewProject().getText();
		if(pname.length()>0) {
			if(tree.getProject(pname)==null)
				project = new ImagingProject(pname);
			return true;
		}
		return false;
	}

	public JTextField getTxtNewProject() {
		return txtNewProject;
	}
	public JTextField getTxtCurrent() {
		return txtCurrent;
	}
}
