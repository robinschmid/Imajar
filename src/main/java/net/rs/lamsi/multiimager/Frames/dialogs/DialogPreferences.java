package net.rs.lamsi.multiimager.Frames.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.Module;
import net.rs.lamsi.massimager.Settings.SettingsHolder;
import net.rs.lamsi.massimager.Settings.preferences.SettingsGeneralPreferences;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;

import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.JCheckBox;
import javax.swing.JTextField;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class DialogPreferences extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JCheckBox cbCreateIcons;
	private JTextField txtIconWidth;
	private JTextField txtIconHeight;
	private ImageEditorWindow window;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			DialogPreferences dialog = new DialogPreferences(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public DialogPreferences(ImageEditorWindow window) {
		setTitle("Preferences");
		this.window = window;
		setBounds(100, 100, 690, 462);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.CENTER);
			panel.setLayout(new BorderLayout(0, 0));
			{
				JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
				panel.add(tabbedPane, BorderLayout.CENTER);
				{
					JPanel pnGeneral = new JPanel();
					tabbedPane.addTab("General", null, pnGeneral, null);
					pnGeneral.setLayout(new BorderLayout(0, 0));
					{
						JPanel panel_2 = new JPanel();
						pnGeneral.add(panel_2, BorderLayout.CENTER);
						panel_2.setLayout(new MigLayout("", "[]", "[]"));
					}
				}
				{
					JPanel panel_1 = new JPanel();
					tabbedPane.addTab("Visual", null, panel_1, null);
					panel_1.setLayout(new BorderLayout(0, 0));
					{
						JPanel panel_2 = new JPanel();
						panel_1.add(panel_2);
						panel_2.setLayout(new MigLayout("", "[][]", "[][][][]"));
						{
							JLabel lblIcons = new JLabel("Icons");
							lblIcons.setFont(new Font("Tahoma", Font.BOLD, 12));
							panel_2.add(lblIcons, "cell 0 0");
						}
						{
							cbCreateIcons = new JCheckBox("Create icons");
							cbCreateIcons.setToolTipText("Icons are stored in the RAM (uncheck if you are encountering problems with memory overflow)");
							panel_2.add(cbCreateIcons, "cell 0 1 2 1");
						}
						{
							JLabel lblIconWidth = new JLabel("Icon width");
							panel_2.add(lblIconWidth, "cell 0 2,alignx trailing");
						}
						{
							txtIconWidth = new JTextField();
							txtIconWidth.setText("60");
							panel_2.add(txtIconWidth, "cell 1 2,growx");
							txtIconWidth.setColumns(4);
						}
						{
							JLabel lblIconHeight = new JLabel("Icon height");
							panel_2.add(lblIconHeight, "cell 0 3,alignx trailing");
						}
						{
							txtIconHeight = new JTextField();
							txtIconHeight.setText("16");
							panel_2.add(txtIconHeight, "cell 1 3,growx");
							txtIconHeight.setColumns(4);
						}
					}
				}
				tabbedPane.setSelectedIndex(1);
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
						applyAllOptions();
						setVisible(false);
					}
				});
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
	}

	@Override
	public void setVisible(boolean b) { 
		if(b==true)
			setAllBySettings(SettingsHolder.getSettings().getSetGeneralPreferences());
		super.setVisible(b);
	}
	
	/**
	 * set up all fields by existing settings
	 * @param pref
	 */
	private void setAllBySettings(SettingsGeneralPreferences pref) {
		cbCreateIcons.setSelected(pref.isGeneratesIcons());
		txtIconWidth.setText(String.valueOf(pref.getIconWidth()));
		txtIconHeight.setText(String.valueOf(pref.getIconHeight()));
	}

	/**
	 * save all settings from panels to settings object
	 */
	protected void applyAllOptions() {
		SettingsGeneralPreferences pref = SettingsHolder.getSettings().getSetGeneralPreferences();
		pref.setGeneratesIcons(getCbCreateIcons().isSelected());
		int iconW = Module.intFromTxt(txtIconWidth);
		if(iconW==0) iconW = 60;
		int iconH = Module.intFromTxt(txtIconHeight);
		if(iconH==0) iconH = 16;
		pref.setIconWidth(iconW);
		pref.setIconHeight(iconH);
		
		// call window for changes
		if(window!=null)
			window.callPreferencesChanged();
	}

	public JCheckBox getCbCreateIcons() {
		return cbCreateIcons;
	}
	public JTextField getTxtIconWidth() {
		return txtIconWidth;
	}
	public JTextField getTxtIconHeight() {
		return txtIconHeight;
	}
}































