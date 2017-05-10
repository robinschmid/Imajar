package net.rs.lamsi.multiimager.Frames.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import javax.swing.JLabel;
import javax.swing.JTextField;

import java.awt.Dialog.ModalityType;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class CroppingDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField txtCurrent;
	private JTextField txtNewProject;

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
	public CroppingDialog() {
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
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		// wait for this dialog
		setModalityType(ModalityType.APPLICATION_MODAL);
	}

	public JTextField getTxtNewProject() {
		return txtNewProject;
	}
	public JTextField getTxtCurrent() {
		return txtCurrent;
	}
}
