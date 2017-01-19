package net.rs.lamsi.dataextract.frames;

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

import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.dataextract.presets.GroupAndNameProducer;

public class AddImportPresetsDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField txtProd;
	private JTextField txtName;
	private JLabel lbProd;
	
	protected GroupAndNameProducer data;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			AddImportPresetsDialog dialog = new AddImportPresetsDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public AddImportPresetsDialog() {
		this.setModal(true);
		setBounds(100, 100, 225, 158);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel);
			panel.setLayout(new MigLayout("", "[][grow]", "[][]"));
			{
				lbProd = new JLabel("Producer");
				panel.add(lbProd, "cell 0 0");
			}
			{
				txtProd = new JTextField();
				panel.add(txtProd, "cell 1 0,growx");
				txtProd.setColumns(10);
			}
			{
				JLabel lblName = new JLabel("Name");
				panel.add(lblName, "cell 0 1,alignx trailing");
			}
			{
				txtName = new JTextField();
				panel.add(txtName, "cell 1 1,growx");
				txtName.setColumns(10);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String name = getTxtName().getText();
						String group = getTxtProd().isVisible()? getTxtProd().getText() : "NULL";
						
						if(name!=null && name.length()>0 && group != null && group.length()>0) {
							data = new GroupAndNameProducer(group, name);
							setVisible(false);
							dispose();
						}
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						data = null;
						setVisible(false);
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	public GroupAndNameProducer showDialog(boolean useGroup) {
		getTxtProd().setVisible(useGroup);
		getLbProd().setVisible(useGroup);
	    setVisible(true);
	    return data;
	}

	public JTextField getTxtName() {
		return txtName;
	}
	public JTextField getTxtProd() {
		return txtProd;
	}
	public JLabel getLbProd() {
		return lbProd;
	}
}
