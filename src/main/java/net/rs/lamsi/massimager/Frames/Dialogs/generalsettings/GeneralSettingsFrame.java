package net.rs.lamsi.massimager.Frames.Dialogs.generalsettings;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import net.rs.lamsi.massimager.Frames.Window;
import net.rs.lamsi.massimager.Frames.Dialogs.generalsettings.interfaces.SettingsPanel;
import net.rs.lamsi.massimager.Frames.Dialogs.generalsettings.panels.PnGeneralValueFormatting;
import net.rs.lamsi.massimager.Frames.Dialogs.generalsettings.panels.PnVisualSpectraLabelGeneratorSettings;
import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.massimager.Settings.SettingsConverterRAW;
import net.rs.lamsi.massimager.Settings.SettingsHolder;
import net.rs.lamsi.utils.DialogLoggerUtil;

import java.awt.Dimension;

public class GeneralSettingsFrame extends JFrame {
	// MENUS
	private static final String CONVERTER_RAW = "Thermo RAW", GENERAL_VALUE_FORMATTING = "Value formatting";
	
	private static final String VISUAL = "Visualization";
	private static final String VISUAL_SPECTRA = "Spectra";
	private static final String VISUAL_SPECTRA_LABEL_GENERATOR = "Label generator"; 
	
	
	// My Stuff
	private Window window;
	private SettingsHolder settings;
	
	// Panel for Settings
	private Map<String, SettingsPanel> mapSettingsPanel = new HashMap<String, SettingsPanel>();
	
	//	other
	private JPanel contentPane;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private final ButtonGroup buttonGroup_1 = new ButtonGroup();
	private JButton btnResetStandard;
	private JButton btnApply;
	private JTree treeSettings; 
	private Vector<JPanel> listPnSettings = new Vector<JPanel>();
	private JPanel pnSouthApply;
	private JPanel panel_2;

	private Settings currentSettings;
	private JPanel pnSettings;
	private JPanel pnSettingsContent; 

	/**
	 * Create the frame.
	 */
	public GeneralSettingsFrame(Window window) {
		this.window = window;
		settings = window.getSettings();
		currentSettings = settings.getSetConvertRAW();
		//
		setTitle("General settings"); 
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 704, 429);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(250, 2));
		scrollPane.setMinimumSize(new Dimension(250, 23));
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 5, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane, 5, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, 377, SpringLayout.NORTH, contentPane);
		contentPane.add(scrollPane);
		
		pnSettings = new JPanel();
		sl_contentPane.putConstraint(SpringLayout.NORTH, pnSettings, 5, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, pnSettings, -5, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane, -6, SpringLayout.WEST, pnSettings);
		sl_contentPane.putConstraint(SpringLayout.WEST, pnSettings, 152, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, pnSettings, -5, SpringLayout.EAST, contentPane);
		
		// Tree Model
		DefaultTreeModel model = new DefaultTreeModel(
				new DefaultMutableTreeNode("General") {
					{
						DefaultMutableTreeNode node_1;
						node_1 = new DefaultMutableTreeNode("General");
							node_1.add(new DefaultMutableTreeNode(GENERAL_VALUE_FORMATTING)); 
						add(node_1);

						DefaultMutableTreeNode nodeVisual;
						nodeVisual = new DefaultMutableTreeNode(VISUAL);
							DefaultMutableTreeNode nodeSpectra = new DefaultMutableTreeNode(VISUAL_SPECTRA);
							nodeVisual.add(nodeSpectra); 
								nodeSpectra.add(new DefaultMutableTreeNode(VISUAL_SPECTRA_LABEL_GENERATOR));
						add(nodeVisual);
					}
				});
		// Tree
		treeSettings = new JTree(model);
		treeSettings.setMaximumSize(new Dimension(250, 64));
		treeSettings.setPreferredSize(new Dimension(250, 64));
		treeSettings.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				showMenuWhenTreeSelect(e.getNewLeadSelectionPath());
			}
		});
		treeSettings.setToolTipText("General settings");
		treeSettings.setShowsRootHandles(true);
		treeSettings.setRootVisible(false); 
		scrollPane.setViewportView(treeSettings);
		contentPane.add(pnSettings);
		pnSettings.setLayout(new BorderLayout(0, 0));
		
		pnSouthApply = new JPanel();
		pnSettings.add(pnSouthApply, BorderLayout.SOUTH);
		pnSouthApply.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_1 = new JPanel();
		pnSouthApply.add(panel_1, BorderLayout.SOUTH);
		
		btnApply = new JButton("Apply");
		btnApply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Settings von allen seiten übernehmen oder nur von der gerade
				setAllSettingsOfAllPages();
			}
		});
		btnApply.setToolTipText("Apply and save current settings");
		panel_1.add(btnApply);
		
		btnResetStandard = new JButton("Reset standard");
		btnResetStandard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				resetAllStandards();
			}
		});
		btnResetStandard.setToolTipText("Standard settings");
		panel_1.add(btnResetStandard);
		
		panel_2 = new JPanel();
		pnSouthApply.add(panel_2, BorderLayout.NORTH);
		
		JButton btnSaveToFile = new JButton("Save to file");
		btnSaveToFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveSettingsToFile();
			}
		});
		panel_2.add(btnSaveToFile);
		
		JButton btnLoadFromFile = new JButton("Load from file");
		btnLoadFromFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				loadSettingsFromFile();
			}
		});
		panel_2.add(btnLoadFromFile);
		
		pnSettingsContent = new JPanel();
		pnSettings.add(pnSettingsContent, BorderLayout.CENTER);
		pnSettingsContent.setLayout(new BorderLayout(0, 0)); 
		
		// generate settingspanel
		generateSettingsPanel();
	}
	
	/*
	 * Generate all and put in Map
	 */
	private void generateSettingsPanel() { 
		// TODO add all
		// ValueFormatting
		PnGeneralValueFormatting pnGeneralValueFormatting = new PnGeneralValueFormatting(); 
		mapSettingsPanel.put(GENERAL_VALUE_FORMATTING, pnGeneralValueFormatting);
		
		// Visual 
		PnVisualSpectraLabelGeneratorSettings pnVisualSpectraLabelGen = new PnVisualSpectraLabelGeneratorSettings(); 
		mapSettingsPanel.put(VISUAL_SPECTRA_LABEL_GENERATOR, pnVisualSpectraLabelGen);
		
	}

	// MY STUFF
	/*
	 * Sets a Menu active
	 */
	public void showMenuCodeSided(String menu) {
		treeSettings.setSelectionPath(findPath(menu));
	}
	
	private TreePath findPath(String menu) {
		return this.compareChildren((TreeNode)treeSettings.getModel().getRoot(), menu);
	}
	// searches for the path
	private TreePath compareChildren(TreeNode node, String menu) { 
		for (int i = 0; i < node.getChildCount(); i++) {
			TreeNode child = node.getChildAt(i); 
			if (menu.equals(child.toString())) { 
				// Exit
				return new TreePath(((DefaultMutableTreeNode) child).getPath()); 
			}
			TreePath path = compareChildren(child, menu);
			if(path != null) {
				// Exit Childpath
				return path;
			}
		}
		return null;
	}
	
	// LOGIC
	public void showMenuWhenTreeSelect(TreePath path) {
		Object ob = path.getLastPathComponent();  
		// richtiges Panel anzeigen wenn path ok ist
		for(String key : mapSettingsPanel.keySet()) {
			if(key.equalsIgnoreCase(ob.toString())) { 
				// Show Settings panel
				SettingsPanel panel = mapSettingsPanel.get(key);
				currentSettings = panel.getSettings(settings);
				getPnSettingsContent().removeAll();
				pnSettingsContent.add(((Component) panel), BorderLayout.CENTER);  
				this.validate();
				this.repaint();
			}
		}  
	} 
	// SAVE and LOAD from file  
	protected void saveSettingsToFile() {
		if(currentSettings!=null) {
			try {
				settings.saveSettingsToFile(this, currentSettings);
			} catch (Exception e) { 
				e.printStackTrace();
				DialogLoggerUtil.showErrorDialog(this, "Error while saving", e);
			}
		}
	}
	protected void loadSettingsFromFile() {
		if(currentSettings!=null) {
			try {
				settings.loadSettingsFromFile(this, currentSettings);
			} catch (Exception e) { 
				e.printStackTrace();
				DialogLoggerUtil.showErrorDialog(this, "Error while loading", e);
			}
			setAllSettingsOnPanel();
		}
	}

	// Übernimmt immer alle Settings
	private void setAllSettingsOfAllPages() { 
		//
		for(SettingsPanel pn : mapSettingsPanel.values()) {
			pn.setAllSettings(settings);
		}
	}
	
	// Resetting all 
	protected void resetAllStandards() {
		// TODO reset all! cbs and radios from settings
		settings.resetAll();
		setAllSettingsOnPanel();
	}
	// alle Settings werden angezeigt
	public void setAllSettingsOnPanel() { 
		// 
		for(SettingsPanel pn : mapSettingsPanel.values()) {
			pn.setAllSettingsOnPanel(settings);
		}
	}
	
	/**
	 * Overridden for renewing All Panels
	 */ 
	@Override
	public void setVisible(boolean b) {
		setAllSettingsOnPanel();
		super.setVisible(b);
	}
	
	// get Set 
	public JButton getBtnResetStandard() {
		return btnResetStandard;
	}

	public JButton getBtnApply() {
		return btnApply;
	} 
	public JTree getTreeSettings() {
		return treeSettings;
	}  
	public JPanel getPnSettingsContent() {
		return pnSettingsContent;
	}
}
