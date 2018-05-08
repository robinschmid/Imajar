package net.rs.lamsi.general.framework.modules;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.rs.lamsi.general.datamodel.image.Collectable2DPlaceHolderLink;
import net.rs.lamsi.general.datamodel.image.ImageGroupMD;
import net.rs.lamsi.general.datamodel.image.ImagingProject;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.framework.modules.tree.IconNode;


public class ModuleTree<T> extends Module {
  private final Logger logger = LoggerFactory.getLogger(getClass());

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
    try {
      getRoot().removeAllChildren();
      reload();
    } catch (Exception ex) {
      logger.error("",ex);
    }
  }

  public void removeSelectedObjects() {
    try {
      TreePath[] paths = getTree().getSelectionPaths();

      for (TreePath p : paths) {
        // remove
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) p.getLastPathComponent();
        // empty parent?
        if (node.getParent().getChildCount() == 1) {
          getTreeModel().removeNodeFromParent((DefaultMutableTreeNode) node.getParent());
        }
        //
        getTreeModel().removeNodeFromParent(node);
        // image? remove data
        if (isCollectable2DNode(node)) {
          Collectable2D image = (Collectable2D) node.getUserObject();
          if (image.getImageGroup() != null) {
            ImageGroupMD g = image.getImageGroup();
            g.remove(image);
            if (g.size() == 0)
              if (g.getProject() != null)
                g.getProject().remove(g);
          }
        } else if (isGroupNode(node)) {
          ImageGroupMD g = (ImageGroupMD) node.getUserObject();
          if (g.getProject() != null) {
            g.getProject().remove(g);
          }
        }
      }
    } catch (Exception ex) {
      logger.error("Error while removing selected object from tree", ex);
    }
  }

  /**
   * object of this path
   * 
   * @param path
   * @return
   */
  public Object getObjectFromPath(TreePath path) {
    if (path == null)
      return null;
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
    return node.getUserObject();
  }

  /**
   * only if image is selected otherwise returns null
   * 
   * @param path
   * @return
   */
  public Collectable2D getImageFromPath(TreePath path) {
    if (path == null)
      return null;
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
    return getImageFromPath(node);
  }

  /**
   * only if image is selected otherwise returns null
   * 
   * @param node
   * @return
   */
  public Collectable2D getImageFromPath(DefaultMutableTreeNode node) {
    if (isCollectable2DNode(node)) {
      return (Collectable2D) node.getUserObject();
    } else
      return null;
  }

  /**
   * returns all images from this collection path can be one image from a collection or the
   * collection itself
   * 
   * @param path
   * @return
   */
  public ImageGroupMD getImageGroup(TreePath path) {
    if (path == null)
      return null;
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
    return getImageGroup(node);
  }

  /**
   * returns all images from this collection node can be one image from a collection or the
   * collection itself
   * 
   * @param node
   * @return
   */
  public ImageGroupMD getImageGroup(DefaultMutableTreeNode node) {
    if (isGroupNode(node)) {
      return ((ImageGroupMD) node.getUserObject());
    } else if (isCollectable2DNode(node)) {
      return ((Collectable2D) node.getUserObject()).getImageGroup();
    } else if (isGroupNode(node)) {
      return ((ImageGroupMD) node.getUserObject());
    }
    return null;
  }

  /**
   * returns an imaging project
   * 
   * @param path
   * @return
   */
  public ImagingProject getProject(TreePath path) {
    if (path == null)
      return null;
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
    return getProject(node);
  }

  /**
   * returns an imaging project
   * 
   * @param node
   * @return
   */
  public ImagingProject getProject(DefaultMutableTreeNode node) {
    if (isProjectNode(node)) {
      return ((ImagingProject) node.getUserObject());
    } else if (isCollectable2DNode(node)) {
      ImageGroupMD g = ((Collectable2D) node.getUserObject()).getImageGroup();
      if (g != null)
        return g.getProject();
      else
        return null;
    } else if (isGroupNode(node)) {
      ImageGroupMD g = ((ImageGroupMD) node.getUserObject());
      if (g != null)
        return g.getProject();
      else
        return null;
    }
    return null;
  }

  public boolean isCollectable2DNode(DefaultMutableTreeNode node) {
    return Collectable2D.class.isInstance(node.getUserObject());
  }

  public boolean isGroupNode(DefaultMutableTreeNode node) {
    return ImageGroupMD.class.isInstance(node.getUserObject());
  }

  public boolean isProjectNode(DefaultMutableTreeNode node) {
    return ImagingProject.class.isInstance(node.getUserObject());
  }

  public void toList(List list, Class c) {
    toList(list, c, root.getFirstLeaf());
  }

  private void toList(List list, Class c, DefaultMutableTreeNode leaf) {
    if (leaf != null) {
      if (c.isInstance(leaf.getUserObject()))
        list.add(leaf.getUserObject());
      toList(list, c, leaf.getNextLeaf());
    }
  }

  public ArrayList<T> toList() {
    ArrayList<T> list = new ArrayList<T>();
    toList(list, root.getFirstLeaf());
    return list;
  }

  private void toList(ArrayList<T> list, DefaultMutableTreeNode leaf) {
    if (leaf != null) {
      list.add((T) leaf.getUserObject());
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

  /**
   * the selected object (project, imagegroupmd, collectable2d
   * 
   * @return
   */
  public Object getSelectedObject() {
    if (getTree().getSelectionPath() == null)
      return null;
    DefaultMutableTreeNode node =
        (DefaultMutableTreeNode) getTree().getSelectionPath().getLastPathComponent();
    return node.getUserObject();
  }

  /**
   * the selected project of the selected object
   * 
   * @return
   */
  public ImagingProject getSelectedProject() {
    if (getTree().getSelectionPath() == null)
      return null;
    DefaultMutableTreeNode node =
        (DefaultMutableTreeNode) getTree().getSelectionPath().getLastPathComponent();
    return getProject(node);
  }

  /**
   * the selected project of the selected object
   * 
   * @return
   */
  public ImageGroupMD getSelectedGroup() {
    if (getTree().getSelectionPath() == null)
      return null;
    DefaultMutableTreeNode node =
        (DefaultMutableTreeNode) getTree().getSelectionPath().getLastPathComponent();
    return getImageGroup(node);
  }

  /**
   * searches the first level for projects
   * 
   * @param projectName
   * @return
   */
  public ImagingProject getProject(String projectName) {
    if (projectName == null || projectName.length() == 0)
      return null;

    for (int i = 0; i < root.getChildCount(); i++) {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) root.getChildAt(i);
      // in project?
      if (isProjectNode(node)) {
        ImagingProject project = getProject(node);
        if (project.getName().equals(projectName)) {
          return project;
        }
      }
    }
    return null;
  }

  /**
   * searches the first level for a project (if not null) and then the correct group if projectName
   * is null the first level is searched for groups directly
   * 
   * @param projectName
   * @param gid
   * @return
   */
  public ImageGroupMD getGroup(String projectName, String gid) {
    // in project?
    ImagingProject project = getProject(projectName);
    if (project != null) {
      return getGroup(project, gid);
    } else {
      return getGroup(root, gid);
    }
  }

  public ImageGroupMD getGroup(ImagingProject project, String gid) {
    if (project == null)
      return getGroup("", gid);
    return getGroup(project.getNode(), gid);
  }

  public ImageGroupMD getGroup(DefaultMutableTreeNode parent, String gid) {
    for (int i = 0; i < parent.getChildCount(); i++) {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getChildAt(i);
      // in project?
      if (isGroupNode(node)) {
        ImageGroupMD group = getImageGroup(node);
        if (group.getName().equals(gid)) {
          return group;
        }
      }
    }
    return null;
  }

  /**
   * finds the suiting collectable2d for the placeholder (or null if the project, group or
   * collectable2d title was not found)
   * 
   * @param pl
   * @return
   */
  public Collectable2D getCollectable2DFromPlaceHolder(Collectable2DPlaceHolderLink pl) {
    ImageGroupMD g = null;
    String pst = pl.getSettings().getProject();
    if (pst.isEmpty()) {
      g = getGroup(root, pl.getSettings().getGroup());
    } else {
      ImagingProject p = getProject(pst);
      if (p != null || pst.length() == 0)
        g = p.getGroupByName(pl.getSettings().getGroup());
    }

    if (g != null)
      return g.getImageByTitle(pl.getSettings().getTitle());
    else
      return null;
  }

}
