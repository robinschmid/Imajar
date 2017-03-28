package net.rs.lamsi.massimager.Frames.FrameWork.modules;
import java.awt.BorderLayout;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.ImageGroupMD;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.tree.IconNode;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow.LOG;


public class ModuleTree <T> extends Module {

	/**
	 * Create the panel.
	 */
	protected JTree tree;
	protected DefaultMutableTreeNode root;
	public ModuleTree(String stitle, boolean westside) {
		super(stitle, westside);
		JScrollPane scrollPane = new JScrollPane();
		getPnContent().add(scrollPane, BorderLayout.CENTER);

		root = new IconNode("Collections");
		tree = new JTree(root);
		tree.setRootVisible(true);
		scrollPane.setViewportView(tree);
	}

	public void addNodeToRoot(DefaultMutableTreeNode node) { 
		root.add(node); 
		reload();
	}
	/**
	 * removes all elements from the list
	 */
	public void removeAllElements() {
		try{  
			getRoot().removeAllChildren();
			reload();
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	public void removeSelectedObjects() {
		try{
			TreePath[] paths = getTree().getSelectionPaths();

			for(TreePath p : paths) {
				// remove 
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)p.getLastPathComponent();
				// empty parent?
				if(node.getParent().getChildCount()==1) {
					getTreeModel().removeNodeFromParent((DefaultMutableTreeNode)node.getParent());  
				}
				//
				getTreeModel().removeNodeFromParent(node);  
				// image? remove data
				if(isCollectable2D(node)) {
					Collectable2D image = (Collectable2D) node.getUserObject();
					if(image.getImageGroup()!=null) {
						image.getImageGroup().remove(image);
					}
				}
			}  
		}catch(Exception ex){
			ImageEditorWindow.log(ex.getMessage(), LOG.ERROR);
		}
	}

	/**
	 * only if image is selected otherwise returns null
	 * @param path
	 * @return
	 */
	public Collectable2D getImageFromPath(TreePath path) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
		if(isCollectable2D(node)) {
			return (Collectable2D) node.getUserObject();
		}
		else return null;
	}
	
	/**
	 * returns all images from this collection
	 * path can be one image from a collection or the collection itself
	 * @param path
	 * @return
	 */
	public ImageGroupMD getImageCollection(TreePath path) {
		if(path==null) return null;
		
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
		if(isCollectable2D(node)) {
			return ((Collectable2D)node.getUserObject()).getImageGroup();
		}
		else {
			// search all first level child paths
			for(int i=0; i<node.getChildCount(); i++) {
				DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
				if(isCollectable2D(child)) 
					return ((Collectable2D)child.getUserObject()).getImageGroup();
			}
		}
		return null;
	}

	public boolean isCollectable2D(DefaultMutableTreeNode node) {
		return Collectable2D.class.isInstance(node.getUserObject());
	}
	public boolean isCollection(DefaultMutableTreeNode node) {
		return !isCollectable2D(node);
	}
	
	public Vector<T> toList() {
		Vector<T> list = new Vector<T>(); 
		toList(list, root.getFirstLeaf());
		return list;
	} 

	private void toList(Vector<T> list, DefaultMutableTreeNode leaf) { 
		if(leaf!=null) {
			list.addElement((T)leaf.getUserObject());
			toList(list, leaf.getNextLeaf());
		} 
	}
	public DefaultTreeModel getTreeModel() {
		return (DefaultTreeModel) tree.getModel();
	}
	public JTree getTree() {
		return tree;
	}
	public DefaultMutableTreeNode getRoot() {
		return root;
	}
	
	public void reload() {
		getTreeModel().reload();
	}

}
