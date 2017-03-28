package net.rs.lamsi.massimager.Frames.FrameWork.modules.menu;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.SettingsModule;
import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.massimager.Settings.SettingsHolder;
import net.rs.lamsi.massimager.Settings.listener.SettingsChangedListener;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.utils.DialogLoggerUtil;

public class ModuleMenu extends JButton {
	private JPopupMenu popupMenu;
	public ModuleMenu() {
		setBounds(new Rectangle(0, 0, 20, 20));
		setMaximumSize(new Dimension(20, 20));
		setIcon(new ImageIcon(ModuleMenu.class.getResource("/img/btn_module_menu.png")));
		setPreferredSize(new Dimension(20, 20));
		setMinimumSize(new Dimension(20, 20));

		popupMenu = new JPopupMenu();
		addPopup(this, popupMenu);
		this.setComponentPopupMenu(popupMenu);
	}

	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				showMenu(e);
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				showMenu(e);
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				showMenu(e);
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}


	// logic stuff for adding options to this menu
	public void addMenuItem(JMenuItem item) {
		popupMenu.add(item);
	}
	public void addMenuItem(JMenuItem item, int pos) {
		popupMenu.add(item, pos);
	}

	public void addSeparator() {
		popupMenu.addSeparator();
	}



	/*
	 * creation of specific menus
	 */
	public static ModuleMenu createLoadSaveOptionsMenu(final SettingsModule mod, final Class settingsClass, final SettingsChangedListener settingsChangedListener) {
		ModuleMenu menu = new ModuleMenu();

		JMenuItem load = new JMenuItem("Load options");
		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					SettingsHolder settings = SettingsHolder.getSettings(); 
					if(settingsChangedListener!=null)
						settingsChangedListener.settingsChanged(settings.loadSettingsFromFile(mod, settingsClass));	
				} catch (Exception e1) { 
					e1.printStackTrace();
					DialogLoggerUtil.showErrorDialog(mod, "Error while loading", e1);
				}
			}
		}); 
		menu.addMenuItem(load); 

		JMenuItem save = new JMenuItem("Save options");
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				mod.saveSettings();
			}
		}); 
		menu.addMenuItem(save); 



		JMenuItem applyToImg = new JMenuItem("Apply options to other images");
		applyToImg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try { 
					// get list of images
					//Object[] list = ImageEditorWindow.getEditor().getLogicRunner().getListImages().toArray(); 
					//int[] ind = DialogLoggerUtil.showListDialogAndChoose(ImageEditorWindow.getEditor(), list, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
					

					TreePath[] paths = DialogLoggerUtil.showTreeDialogAndChoose(ImageEditorWindow.getEditor(), ImageEditorWindow.getEditor().getModuleTreeImages().getRoot(), TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION, ImageEditorWindow.getEditor().getModuleTreeImages().getTree().getSelectionPaths());
					if(paths==null) return;
					// apply settings to all images
					// save parent collection for avoiding double processing
					Vector<DefaultMutableTreeNode> parent = new Vector<DefaultMutableTreeNode>();
					//
					for(TreePath p : paths) { 
						DefaultMutableTreeNode node = (DefaultMutableTreeNode)p.getLastPathComponent();
						// check if already done in parents!
						boolean added = false;
						for(DefaultMutableTreeNode pnode : parent) {
							if(pnode.isNodeChild(node)) {
								added = true;
								break;
							}
						}
						// otherwise 
						if(added == false) {
							if(Image2D.class.isInstance(node.getUserObject())) {
								((Settings)mod.getSettings()).applyToImage((Image2D)node.getUserObject());
							}
							else {
								// is a parent
								parent.addElement(node);
								// add all children
								for(int i=0; i<node.getChildCount(); i++) {
									Object obj = ((DefaultMutableTreeNode)node.getChildAt(i)).getUserObject();
									if(Image2D.class.isInstance(obj)) {
										((Settings)mod.getSettings()).applyToImage((Image2D)obj);
									}
								}
							}
						}
						
					}
				} catch (Exception e1) { 
					e1.printStackTrace();
					DialogLoggerUtil.showErrorDialog(mod, "Error while applying settings to other images", e1);
				}
			}
		}); 
		menu.addMenuItem(applyToImg); 
		// 
		return menu;
	}




	public JPopupMenu getPopupMenu() {
		return popupMenu;
	}
}
