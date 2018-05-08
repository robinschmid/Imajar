package net.rs.lamsi.general.framework.modules.menu;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.ImageGroupMD;
import net.rs.lamsi.general.datamodel.image.ImagingProject;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.framework.modules.SettingsModule;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.general.settings.listener.SettingsChangedListener;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.utils.DialogLoggerUtil;
import net.rs.lamsi.utils.threads.ProgressUpdateTask;

public class ModuleMenu extends JButton {
  private static final Logger logger = LoggerFactory.getLogger(ModuleMenu.class);

  private ArrayList<ModuleMenuApplyToImage> applyToListener;

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

  public void removeMenuItem(JMenuItem item) {
    popupMenu.remove(item);
  }

  public void addSeparator() {
    popupMenu.addSeparator();
  }

  /*
   * creation of specific menus
   */
  public static ModuleMenu createLoadSaveOptionsMenu(final SettingsModule mod,
      final Class settingsClass, final SettingsChangedListener settingsChangedListener) {
    final ModuleMenu menu = new ModuleMenu();

    JMenuItem load = new JMenuItem("Load options");
    load.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          Settings sett = mod.getSettings();
          if (settingsChangedListener != null)
            settingsChangedListener.settingsChanged(sett.loadSettingsFromFile(mod));
        } catch (Exception e1) {
          logger.error("", e1);
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
          // Object[] list =
          // ImageEditorWindow.getEditor().getLogicRunner().getListImages().toArray();
          // int[] ind = DialogLoggerUtil.showListDialogAndChoose(ImageEditorWindow.getEditor(),
          // list, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);


          final TreePath[] paths =
              DialogLoggerUtil.showTreeDialogAndChoose(ImageEditorWindow.getEditor(),
                  ImageEditorWindow.getEditor().getModuleTreeImages().getRoot(),
                  TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION,
                  ImageEditorWindow.getEditor().getModuleTreeImages().getTree().getSelectionPaths(),
                  "Multi select", "Select images, groups or projects");
          if (paths == null)
            return;
          new ProgressUpdateTask(paths.length) {
            @Override
            protected Boolean doInBackground2() throws Exception {
              // apply settings to all images
              for (TreePath p : paths) {
                try {
                  // null, project, group or image2d (collectable2d
                  Object o = ((DefaultMutableTreeNode) p.getLastPathComponent()).getUserObject();
                  if (o != null) {
                    // project?
                    if (ImagingProject.class.isInstance(o)) {
                      applyToImagingProject(mod.getSettings(), (ImagingProject) o, this);
                    } else if (ImageGroupMD.class.isInstance(o)) {
                      applyToImageGroup(mod.getSettings(), (ImageGroupMD) o, this);
                    } else if (Image2D.class.isInstance(o)) {
                      applyToImage(mod.getSettings(), (Image2D) o, this);
                    }
                  }
                } catch (Exception e1) {
                  logger.error("", e1);
                  DialogLoggerUtil.showErrorDialog(mod,
                      "Error while applying settings to other images", e1);
                }
              }
              return true;
            }

            private void applyToImagingProject(Settings sett, ImagingProject img,
                ProgressUpdateTask task) throws Exception {
              for (ImageGroupMD g : img.getGroups()) {
                applyToImageGroup(sett, g, task);
                addProgressStep(-1.0 + 1.0 / img.getGroups().size());
              }
            }

            private void applyToImageGroup(Settings sett, ImageGroupMD img, ProgressUpdateTask task)
                throws Exception {
              for (Collectable2D c : img.getImages()) {
                if (Image2D.class.isInstance(c))
                  applyToImage(sett, (Image2D) c, task);

                addProgressStep(-1.0 + 1.0 / img.getImages().size());
              }
            }

            private void applyToImage(Settings sett, Image2D img, ProgressUpdateTask task)
                throws Exception {
              sett.applyToImage(img);
              if (menu.applyToListener != null)
                for (ModuleMenuApplyToImage l : menu.applyToListener)
                  l.applyToImage(sett, img);

              addProgressStep(1.0);
            }
          }.execute();

        } catch (Exception e1) {
          logger.error("", e1);
          DialogLoggerUtil.showErrorDialog(mod, "Error while applying settings to other images",
              e1);
        }
      }
    });

    menu.addMenuItem(applyToImg);
    //
    return menu;
  }

  public void applyToImagingProject(Settings sett, ImagingProject img) throws Exception {
    for (ImageGroupMD g : img.getGroups())
      applyToImageGroup(sett, g);
  }

  public void applyToImageGroup(Settings sett, ImageGroupMD img) throws Exception {
    for (Collectable2D c : img.getImages())
      if (Image2D.class.isInstance(c))
        applyToImage(sett, (Image2D) c);
  }

  public void applyToImage(Settings sett, Image2D img) throws Exception {
    sett.applyToImage(img);
    if (applyToListener != null)
      for (ModuleMenuApplyToImage l : applyToListener)
        l.applyToImage(sett, img);
  }

  /**
   * gets called on apply to image events
   * 
   * @param listener
   */
  public void addApplyToImageListener(ModuleMenuApplyToImage listener) {
    if (applyToListener == null)
      applyToListener = new ArrayList<ModuleMenuApplyToImage>();
    applyToListener.add(listener);
  }



  public JPopupMenu getPopupMenu() {
    return popupMenu;
  }
}
