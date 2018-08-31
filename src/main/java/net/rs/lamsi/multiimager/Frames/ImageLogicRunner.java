package net.rs.lamsi.multiimager.Frames;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.jfree.chart.ChartPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.ImageGroupMD;
import net.rs.lamsi.general.datamodel.image.ImageMerge;
import net.rs.lamsi.general.datamodel.image.ImageOverlay;
import net.rs.lamsi.general.datamodel.image.ImagingProject;
import net.rs.lamsi.general.datamodel.image.SingleParticleImage;
import net.rs.lamsi.general.datamodel.image.data.interf.ImageDataset;
import net.rs.lamsi.general.datamodel.image.data.interf.MDDataset;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.DatasetContinuousMD;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.DatasetLinesMD;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.ScanLineMD;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.datamodel.image.interf.DataCollectable2D;
import net.rs.lamsi.general.dialogs.HeatmapGraphicsExportDialog;
import net.rs.lamsi.general.framework.modules.ModuleTree;
import net.rs.lamsi.general.framework.modules.tree.IconNode;
import net.rs.lamsi.general.heatmap.Heatmap;
import net.rs.lamsi.general.heatmap.HeatmapFactory;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.general.settings.SettingsHolder;
import net.rs.lamsi.general.settings.image.SettingsImageMerge;
import net.rs.lamsi.general.settings.image.SettingsImageOverlay;
import net.rs.lamsi.general.settings.image.SettingsSPImage;
import net.rs.lamsi.general.settings.image.selection.SettingsSelections;
import net.rs.lamsi.general.settings.image.sub.SettingsImage2DSetup;
import net.rs.lamsi.general.settings.image.visualisation.SettingsPaintScale;
import net.rs.lamsi.general.settings.image.visualisation.SettingsPaintScale.ValueMode;
import net.rs.lamsi.general.settings.importexport.SettingsImageDataImportTxt;
import net.rs.lamsi.general.settings.preferences.SettingsGeneralPreferences;
import net.rs.lamsi.multiimager.Frames.dialogs.DialogChooseProject;
import net.rs.lamsi.multiimager.Frames.dialogs.analytics.HistogramData;
import net.rs.lamsi.multiimager.Frames.dialogs.analytics.HistogramDialog;
import net.rs.lamsi.multiimager.utils.imageimportexport.DataExportUtil;
import net.rs.lamsi.multiimager.utils.imageimportexport.Image2DImportExportUtil;
import net.rs.lamsi.utils.DialogLoggerUtil;
import net.rs.lamsi.utils.FileAndPathUtil;
import net.rs.lamsi.utils.myfilechooser.FileTypeFilter;
import net.rs.lamsi.utils.mywriterreader.BinaryWriterReader;
import net.rs.lamsi.utils.mywriterreader.TxtWriter;
import net.rs.lamsi.utils.threads.EasyTask;
import net.rs.lamsi.utils.threads.ProgressUpdateTask;

public class ImageLogicRunner {

  private final Logger logger = LoggerFactory.getLogger(getClass());
  // ##################################################################################
  // MyStuff
  // statics
  // this one saves whether to update or not the image
  public static boolean IS_UPDATING = true;
  // variables
  private ImageEditorWindow window;
  private DirectImageLogicRunner diaRunner;
  private HeatmapFactory heatFactory;
  private SettingsGeneralPreferences preferences;
  // Readers and Writers
  private BinaryWriterReader binaryWriter;
  private TxtWriter txtWriter;
  // last file for filechooser
  private File lastPath = null;
  // List of Images
  private ModuleTree<Collectable2D> treeImg;
  // get parent IconNode by String keys (parent only)
  private HashMap<String, IconNode> mapNodes;
  // private Vector<Image2D> listImages = new Vector<Image2D>();
  private Collectable2D selectedImage = null;
  private Heatmap currentHeat = null;
  //

  // ##################################################################################
  // LOGIC
  public ImageLogicRunner(ImageEditorWindow wnd) {
    this.window = wnd;
    preferences = SettingsHolder.getSettings().getSetGeneralPreferences();
    diaRunner = new DirectImageLogicRunner(this);
    mapNodes = new HashMap<String, IconNode>();
    treeImg = wnd.getModuleTreeImages();
    treeImg.getTree().addTreeSelectionListener(new TreeSelectionListener() {

      @Override
      public void valueChanged(TreeSelectionEvent e) {
        try {
          // show first selected item
          Object o = treeImg.getSelectedObject();
          if (o != null) {
            if (Collectable2D.class.isInstance(o)) {
              setSelectedImageAndShow((Collectable2D) o);
            }
            // select first image
            else if (ImageGroupMD.class.isInstance(o)) {
              ImageGroupMD g = (ImageGroupMD) o;
              if (g.size() > 0)
                setSelectedImageAndShow(g.get(0));
            }
            // select first image
            else if (ImagingProject.class.isInstance(o)) {
              ImagingProject p = (ImagingProject) o;
              if (p.size() > 0 && p.get(0).size() > 0)
                setSelectedImageAndShow(p.get(0).get(0));
            }
          }
        } catch (Exception ex) {
          logger.error("", ex);
        }
      }
    });
    // init heatmapfactory
    heatFactory = new HeatmapFactory();
    // Init binaryWriter
    binaryWriter = new BinaryWriterReader();
    txtWriter = new TxtWriter();
  }

  // ############################################################################################
  // Add, select, renew, delete images
  /**
   * add to group and to tree and remove image from jList gets automatically removed from
   * vector<Image2D>
   * 
   * @param gid parent node id
   */
  public void addImage(Collectable2D i, String projectName, String gid) {
    // TODO
    ImageGroupMD group = treeImg.getGroup(projectName, gid);

    // group exists?
    if (group != null) {
      group.add(i);
      IconNode node = addImageNode(i, group.getNode());
      // treeImg.getTreeModel().reload();
    } else {
      // cerate and add group
      // create image group
      group = new ImageGroupMD(i);
      group.setGroupName(gid);
      addGroup(group, projectName);
    }
  }

  public void addImage(Collectable2D i, ImageGroupMD group) {
    if (group.getProject() == null)
      logger.error("Cannot add group {} (has no project)", group.getName());
    addImage(i, group.getProject().getName(), group.getName());
  }

  /**
   * adds an image to a parent node
   * 
   * @param i
   * @param parent
   */
  public IconNode addImageNode(Collectable2D i, DefaultMutableTreeNode parent) {
    //
    SettingsGeneralPreferences sett = SettingsHolder.getSettings().getSetGeneralPreferences();
    IconNode node = new IconNode(i, false,
        window.isCreatingImageIcons() ? i.getIcon(sett.getIconWidth(), sett.getIconHeight())
            : null);
    if (parent != null)
      treeImg.add(node, parent);
    else
      treeImg.addNodeToRoot(node);
    return node;
  }

  /**
   * add group to the specified project (project may be null)
   * 
   * @param group
   * @param projectName
   */
  public void addGroup(ImageGroupMD group, String projectName) {
    ImagingProject project = treeImg.getProject(projectName);
    if (projectName != null && project == null) {
      // create new project
      project = new ImagingProject(projectName);
      project.add(group);
      addProject(project, false);
    }
    // add group
    addGroup(group, project);
  }

  /**
   * add group to the specified project (project may be null)
   * 
   * @param group
   * @param project
   */
  public void addGroup(ImageGroupMD group, ImagingProject project) {
    // only one image? do not create subnodes
    if (group == null || group.getImages().isEmpty()) {
      return;
    }
    // project was already added?
    else if (project != null && project.getNode() == null)
      addProject(project, false);


    // add group
    DefaultMutableTreeNode parentPr = project != null ? project.getNode() : treeImg.getRoot();
    // add group to parentProject
    DefaultMutableTreeNode parent = new DefaultMutableTreeNode(group);
    group.setNode(parent);
    getTree().add(parent, parentPr);

    // create img nodes
    for (Collectable2D c2d : group.getImages()) {
      if (Image2D.class.isInstance(c2d)) {
        addImageNode(c2d, parent);
      }
    }
    // add overlays afterwards
    for (Collectable2D c2d : group.getImages()) {
      if (!Image2D.class.isInstance(c2d)) {
        addImageNode(c2d, parent);
      }
    }
    // treeImg.reload();
  }

  /**
   * add the project as node to the root
   * 
   * @param project
   * @return
   */
  public DefaultMutableTreeNode addProject(ImagingProject project, boolean addsGroups) {
    // exists already?
    int c = 1;
    String name = project.getName();
    while (treeImg.getProject(project.getName()) != null) {
      // change name
      c++;
      // add number
      project.getSettings().setName(name + "(" + c + ")");
    }

    // create subnode with image name
    DefaultMutableTreeNode node = new DefaultMutableTreeNode(project);
    project.setNode(node);
    treeImg.addNodeToRoot(node);
    treeImg.reload();

    // add all groups
    if (addsGroups)
      for (int i = 0; i < project.size(); i++)
        addGroup(project.get(i), project);

    return node;
  }


  public void removeAllImage() {
    treeImg.removeAllElements();
  }

  /**
   * gets called by listImages.selectionChanged, sets the selected Image creates and shows a heatmap
   * in chartpanel in centerView
   */
  public void setSelectedImageAndShow(Collectable2D c2d) {
    //
    if (c2d == null) {
      logger.debug("REMOVE VIEWED IMAGE2D");
      selectedImage = c2d;
      // TODO set image2D -> null?
      // remove heatmap von central
      window.getPnCenterImageView().removeAll();
    } else if (this.selectedImage != c2d) {
      logger.debug("UPDATE IMAGE2D");
      //
      selectedImage = c2d;
      // Renew all Modules
      if (Image2D.class.isInstance(c2d)) {
        window.setImage2D((Image2D) c2d);
        logger.debug("for setImage2D");
      } else if (ImageOverlay.class.isInstance(c2d)) {
        window.setImageOverlay((ImageOverlay) c2d);
        logger.debug("for setImageOverlay");
      } else if (ImageMerge.class.isInstance(c2d)) {
        window.setImageMerge((ImageMerge) c2d);
        logger.debug("for setImageOverlay");
      } else if (SingleParticleImage.class.isInstance(c2d)) {
        window.setSPImage((SingleParticleImage) c2d);
        logger.debug("for setSPImage");
      }

      // create new heatmap
      renewImage2DView();
      logger.debug("FOR renewImage2DView");
    }
  }

  /**
   * this one gets called to renew the central chartpanel thats displayed by the ImageEditorWindow
   * class called from logicRunner.setSelectedImageAndShow called from autoupdater in window class
   * called from other events on imagemodules
   * 
   * @return
   */
  public Heatmap renewImage2DView() {
    if (selectedImage != null) {
      try {
        logger.debug("Create Heatmap");
        // show heatmap in Center

        currentHeat = HeatmapFactory.generateHeatmap(selectedImage);
        logger.debug("creating the heatmap took ");

        ChartPanel myChart = currentHeat.getChartPanel();
        myChart.setMouseWheelEnabled(true);
        // remove all and add
        window.addHeatmapToPanel(currentHeat);
        return currentHeat;
      } catch (Exception ex) {
        logger.error("", ex);
        // Dialog
        logger.error("Cannot create image from {}", selectedImage.getTitle(), ex.getMessage());
        JOptionPane.showMessageDialog(window,
            "cannot create image from " + selectedImage.getTitle() + "; " + ex.getMessage(),
            "ERROR", JOptionPane.ERROR_MESSAGE);
        return null;
      }
    }
    return null;
  }

  // ##################################################################################
  // apply settings

  /**
   * opens a dialog. if ok -> change all settings of all images to current settings Copy settings!
   */
  public void applySettingsToAllImagesInList() {
    boolean state =
        DialogLoggerUtil.showDialogYesNo(window, "Change settings of all images in list?",
            "Attention: You are about to replace all settings of all images! Ok?");
    final Collectable2D current = getSelectedImage();
    if (state && current != null) {
      // current settings as image

      // get List of images
      new ProgressUpdateTask(1) {

        @Override
        protected Boolean doInBackground2() throws Exception {
          Settings sett = current.getSettings();
          List<Collectable2D> list = getListImages();
          for (Collectable2D img : list) {
            // for each replace all settings
            if (current.getClass().isInstance(img))
              current.applySettingsToOtherImage((img));

            addProgressStep(1.0 / list.size());
          }
          return true;
        }
      }.execute();

    }
  }


  // ##################################################################################
  // LOAD AND SAVE IMAGE
  //
  /**
   * export heat graphics dialog
   */
  public void openExportHeatGraphicsDialog() {
    if (currentHeat != null)
      HeatmapGraphicsExportDialog.openDialog(currentHeat.getChart(), getSelectedImage());
  }

  // saves selected Image to file
  public void saveProjectToFile() {
    saveImage2DAndProjectToFile(preferences.getFileTFProject());
  }

  public void saveImage2DToFile() {
    saveImage2DAndProjectToFile(preferences.getFileTFImage2D());
  }

  private void saveImage2DAndProjectToFile(FileTypeFilter filter) {
    // save filter type (image2d or project)
    preferences.getFcSave().setFileFilter(filter);
    if (selectedImage != null
        && preferences.getFcSave().showSaveDialog(window) == JFileChooser.APPROVE_OPTION) {
      filter = (FileTypeFilter) preferences.getFcSave().getFileFilter();
      File file = preferences.getFcSave().getSelectedFile();
      file = filter.addExtensionToFileName(file);
      if (filter.getExtension().equals("image2d")) {
        // save group to image2d
        ImageGroupMD group = selectedImage.getImageGroup();

        boolean noGroup = group == null;
        if (noGroup)
          group = new ImageGroupMD(selectedImage);
        try {
          Image2DImportExportUtil.writeToStandardZip(group, file);
          preferences.addImage2DImportExportPath(file);
        } catch (IOException e) {
          logger.error("Error while writing {}", file.getAbsolutePath(), e);
        }

        if (noGroup)
          selectedImage.setImageGroup(null);
      } else {
        // save project to img2dproject
        boolean noGroup = selectedImage.getImageGroup() == null;
        if (noGroup)
          new ImageGroupMD(selectedImage);

        ImagingProject project = selectedImage.getImageGroup().getProject();
        boolean noProject = project == null;
        if (noProject)
          project = new ImagingProject(selectedImage.getImageGroup(),
              selectedImage.getImageGroup().getName());
        try {
          Image2DImportExportUtil.writeProjectToStandardZip(project, file);
          preferences.addImage2DImportExportPath(file);
        } catch (IOException e) {
          logger.error("Error while writing {}", file.getAbsolutePath(), e);
        }

        if (noProject)
          selectedImage.getImageGroup().setProject(null);
        if (noGroup)
          selectedImage.setImageGroup(null);
      }
    }
  }

  /**
   * opens a file chooser and loads own format image2D
   */
  public void loadProjectFromFile() {
    loadImage2DAndProjectFromFile(preferences.getFileTFProject());
  }

  public void loadImage2DFromFile() {
    loadImage2DAndProjectFromFile(preferences.getFileTFImage2D());
  }

  private void loadImage2DAndProjectFromFile(FileTypeFilter filter) {
    // load filter type (image2d or project)
    preferences.getFcOpen().setFileFilter(filter);
    if (preferences.getFcOpen().showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
      final File[] files = preferences.getFcOpen().getSelectedFiles();

      // Up dateTask
      new ProgressUpdateTask(files.length) {
        // Load all Files
        @Override
        protected Boolean doInBackground2() throws Exception {
          //
          // load file
          if (files.length > 0) {
            // has single groups?
            boolean hasGroups = false;
            // import all projects first
            try {
              // All files in fileList
              for (File f : files) {
                if (preferences.getFileTFProject().accept(f)) {
                  loadProjectFromFile(f);
                  // Progress:
                  addProgressStep(1);
                } else
                  hasGroups = true;
              }
            } catch (Exception ex) {
              logger.error("", ex);
            }

            if (hasGroups) {
              // import all groups (image2D)
              // choose project dialog
              ImagingProject project =
                  DialogChooseProject.choose(treeImg.getSelectedProject(), treeImg);

              try {
                // All files in fileList
                for (File f : files) {
                  if (preferences.getFileTFImage2D().accept(f)) {
                    loadImage2DFromFile(f, project);
                    // Progress:
                    addProgressStep(1);
                  }
                }
              } catch (Exception ex) {
                logger.error("", ex);
              } finally {
                // save changes
                preferences.saveChanges();
              }
            }
          }
          //
          return true;
        }
      }.execute();
    }
  }

  /**
   * tries to load project or image2d
   * 
   * @param f
   * @param project
   */
  public void loadImage2DAndProjectFromFile(final File f, final ImagingProject project) {
    loadProjectFromFile(f);
    loadImage2DFromFile(f, project);
  }

  /**
   * loads own format image2D set no project
   * 
   * @param project
   */
  public void loadProjectFromFile(final File f) {
    // image
    if (preferences.getFileTFProject().accept(f)) {
      new ProgressUpdateTask(1) {
        // Load all Files
        @Override
        protected Boolean doInBackground2() throws Exception {
          try {
            // load image group from file
            ImagingProject project = Image2DImportExportUtil.readProjectFromStandardZip(f, this);
            if (project != null) {
              addProject(project, true);
              preferences.addImage2DImportExportPath(f, false);

              // replace place holders in settings
              project.replacePlaceHoldersInSettings(getTree());
            }

          } catch (Exception ex) {
            logger.error("", ex);
            // Dialog
            logger.error("Error while reading {}", f.getAbsolutePath(), ex);
            JOptionPane.showMessageDialog(window,
                "Cannot load image file " + f.getPath() + "; " + ex.getMessage(), "ERROR",
                JOptionPane.ERROR_MESSAGE);
          }
          return true;
        }
      }.execute();;

    }
  }

  /**
   * loads own format image2D set no project
   * 
   * @param project
   */
  public void loadImage2DFromFile(final File f, final ImagingProject project) {
    // image
    if (preferences.getFileTFImage2D().accept(f)) {
      new ProgressUpdateTask(1) {
        // Load all Files
        @Override
        protected Boolean doInBackground2() throws Exception {
          try {
            // load image group from file
            ImageGroupMD img = Image2DImportExportUtil.readFromStandardZip(f, this);
            if (img != null) {
              if (project != null)
                project.add(img);
              addGroup(img, project);
              preferences.addImage2DImportExportPath(f, false);

              // replace place holders in settings
              img.replacePlaceHoldersInSettings(getTree());
            }

          } catch (Exception ex) {
            logger.error("", ex);
            // Dialog
            logger.error("Error while reading {}", f.getAbsolutePath(), ex);
            JOptionPane.showMessageDialog(window,
                "Cannot load image file " + f.getPath() + "; " + ex.getMessage(), "ERROR",
                JOptionPane.ERROR_MESSAGE);
          }
          return true;
        }
      }.execute();
    }
  }

  /**
   * Import from data file txt, csv, xlsx or set up the direct imaging analysis
   * 
   * @param settingsDataImport
   */
  public void importDataToImage(SettingsImageDataImportTxt settingsDataImport) {
    // import TXT or csv
    if (settingsDataImport instanceof SettingsImageDataImportTxt) {
      // txt chooser
      preferences.getFcImport().setFileFilter(preferences.getFileTFtxtcsv());
      // choose files
      if (preferences.getFcImport().showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
        // many folders or files
        File[] files = preferences.getFcImport().getSelectedFiles();

        // set mother directory as NEW Project
        String newProject =
            files.length == 1 ? files[0].getName() : files[0].getParentFile().getName();
        newProject = FileAndPathUtil.eraseFormat(newProject);

        // choose project dialog
        ImagingProject project =
            DialogChooseProject.choose(treeImg.getSelectedProject(), treeImg, newProject);
        if (project == null)
          project = new ImagingProject(newProject);

        if (files.length > 0) {
          // open the files
          SettingsImageDataImportTxt sett = (SettingsImageDataImportTxt) settingsDataImport;

          // import or start direct image analysis?
          if (window.getCurrentView() == ImageEditorWindow.VIEW_IMAGING_ANALYSIS) {
            // import text files
            importTextDataToImage(sett, files, project);
          } else if (window.getCurrentView() == ImageEditorWindow.VIEW_DIRECT_IMAGING_ANALYSIS) {
            // set up direct imaging analysis
            startDirectImagingAnalysis(sett, files, project);
          }
        }
        // save changed path
        preferences.saveChanges();

        // run garbage collection
        System.gc();
      }
    }
    // import xlsx
  }

  // Import data direct with files
  public ProgressUpdateTask importTextDataToImage(
      final SettingsImageDataImportTxt settingsDataImport, final File[] files,
      final ImagingProject project) {
    // load image
    try {
      if (files.length > 0) {
        ProgressUpdateTask task = new ProgressUpdateTask(files.length) {
          // Load all Files
          @Override
          protected Boolean doInBackground2() throws Exception {
            boolean state = true;
            // folder or files?
            if (files[0].isDirectory()) {
              // go into sub folders to find data
              // load each folder as one set of images
              for (File f : files) {
                if (f.isDirectory()) {
                  // get all files in this folder TODO change csv to settings
                  // each file[] element is for one image
                  List<File[]> sub =
                      FileAndPathUtil.findFilesInDir(f, settingsDataImport.getFilter(), true,
                          settingsDataImport.isFilesInSeparateFolders());

                  for (File[] i : sub) {
                    setProgress(0);
                    setProgressSteps(i.length);
                    // load them as image set
                    ImageGroupMD[] imgs = Image2DImportExportUtil.importTextDataToImage(i,
                        settingsDataImport, true, this);

                    // add to project
                    if (project != null) {
                      for (ImageGroupMD g : imgs) {
                        if (settingsDataImport.isOpenImageSetupDialog())
                          setUpImage2D(g);
                        project.add(g);
                      }
                    }

                    logger.debug("Imported image {}", i[0].getName());
                    for (int coll = 0; coll < imgs.length; coll++) {
                      if (imgs[coll].getImages().size() > 0) {
                        // add img to list
                        addGroup(imgs[coll], project);
                      }
                    }
                  }
                }
              }
            } else {
              try {
                // load all files as one image set
                ImageGroupMD[] imgs = Image2DImportExportUtil.importTextDataToImage(files,
                    settingsDataImport, true, this);

                // add to project
                if (project != null) {
                  for (ImageGroupMD g : imgs) {
                    if (settingsDataImport.isOpenImageSetupDialog())
                      setUpImage2D(g);
                    project.add(g);
                  }
                }

                // add all
                for (int coll = 0; coll < imgs.length; coll++) {
                  if (imgs[coll].getImages().size() > 0) {
                    // add img to list
                    addGroup(imgs[coll], project);
                  }
                }
              } catch (Exception e) {
                logger.error("Import text data to image failed", e);
                DialogLoggerUtil.showErrorDialog(window, "Import failed", e);
              }
            }
            return state;
          }
        };
        task.execute();
        return task;
      }

    } catch (Exception e) {
      logger.error("", e);
      DialogLoggerUtil.showErrorDialog(window, "Import failed", e);
    }
    return null;
  }

  private void setUpImage2D(ImageGroupMD group) throws Exception {
    if (group != null && group.size() > 0) {
      window.getImageSetupDialog().setVisible(true);
      SettingsImage2DSetup sett = window.getImageSetupDialog().getSettings();
      // turn reduction on if there are 400x more dp than lines
      Image2D[] imgs = group.getImagesOnly();
      for (Image2D i : imgs)
        sett.applyToImage(i);

      //
      logger.info("Set up performed on image group {} (lines: {} dp: {})", group.getName(),
          imgs[0].getData().getLinesCount(), imgs[0].getData().getMaxDP());
    }
  }

  /**
   * Recalculate all ROI statistics of all images in all projects
   */
  public void recalculateAllStatistics() {
    List<ImagingProject> list = getProjects();
    for (ImagingProject p : list) {
      for (ImageGroupMD g : p.getGroups()) {
        for (int i = 0; i < g.size(); i++) {
          if (g.get(i) instanceof DataCollectable2D) {
            DataCollectable2D img = (DataCollectable2D) g.get(i);
            final SettingsSelections sel = img.getSelections();
            if (sel != null) {
              // in new thread
              new EasyTask(() -> sel.updateStatistics()).execute();
            }
          }
        }
      }
    }
  }


  /**
   * exports a data report on quantification
   */
  public void exportDataReport() {
    // show save dialog
    JFileChooser chooser = new JFileChooser(lastPath);
    // export
    if (chooser.showSaveDialog(window) == JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();
      if (file != null && getSelectedImage() != null) {
        try {
          if (Image2D.class.isInstance(selectedImage)) {
            logger.info("Exporting data report to {}", FileAndPathUtil
                .getRealFilePath(file.getParentFile(), file.getName(), "xlsx").getAbsolutePath());
            DataExportUtil.exportDataReportOnOperations((Image2D) selectedImage,
                file.getParentFile(), file.getName());
            logger.debug("Exporting data report finished");
          }
        } catch (Exception e) {
          logger.error("", e);
        }
      }
    }
  }


  /**
   * opens a histogram frame for a DataCollectable2D
   * 
   * @param c
   */
  public void openHistogram(Collectable2D c) {
    if (c != null && c instanceof DataCollectable2D) {
      DataCollectable2D img = (DataCollectable2D) c;
      HistogramData data = new HistogramData(img.toIArray(false, true), img.getMinIntensity(true),
          img.getMaxIntensity(true));
      // open histogram frame
      HistogramDialog d = new HistogramDialog("Histogram of " + img.getTitle(), data);
      DialogLoggerUtil.centerOnScreen(d, true);
      d.setVisible(true);
    }
  }

  /**
   * combine multiple images to one large image by selection in tree (all lines are combined)
   * 
   * @throws Exception
   */
  public void combineImages() throws Exception {
    TreePath[] main = DialogLoggerUtil.showTreeDialogAndChoose(window, getTree().getTree(),
        TreeSelectionModel.SINGLE_TREE_SELECTION, "Single selection", "Select the main group");

    if (main != null && main.length > 0) {
      DefaultMutableTreeNode test = (DefaultMutableTreeNode) main[0].getLastPathComponent();
      ImageGroupMD group = null;
      if (getTree().isCollectable2DNode(test)) {
        group = getTree().getImageFromPath(test).getImageGroup();
      } else if (getTree().isGroupNode(test)) {
        group = getTree().getImageGroup(test);
      }

      if (group != null) {
        MDDataset data = group.getData();
        // search for the same image
        TreePath[] add = DialogLoggerUtil.showTreeDialogAndChoose(window, getTree().getTree(),
            TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION, "Multi selection",
            "Select more groups to merge");

        for (TreePath a : add) {
          DefaultMutableTreeNode addNode = (DefaultMutableTreeNode) a.getLastPathComponent();
          if (getTree().isGroupNode(addNode)) {
            ImageGroupMD g = getTree().getImageGroup(addNode);
            if (DatasetLinesMD.class.isInstance(g.getData())
                && g.image2dCount() == group.image2dCount()) {
              ScanLineMD[] lines = ((DatasetLinesMD) g.getData()).getLines().clone();
              // same ordering?
              if (!g.hasSameOrdering(group)) {
                // reorder dimensions
                for (int i = 0; i < g.image2dCount(); i++) {
                  // i = current dimension
                  boolean swapped = false;
                  for (int t = 0; t < group.image2dCount(); t++) {
                    // t = target dimension
                    if (g.get(i).getTitle().equals(group.get(t).getTitle())) {
                      swapped = true;
                      // swap dimensions
                      for (ScanLineMD l : lines) {
                        double[] dim = l.getIntensity().remove(i);
                        l.getIntensity().add(t, dim);
                      }
                    }
                  }
                  // not swapped? error
                  if (!swapped)
                    throw new Exception("There was no image " + g.get(i).getTitle() + " in group "
                        + group.getName());
                }
              }
              // add lines
              try {
                data.appendLines(lines);
                logger.debug("Added {} lines to group {}", lines.length, group.getName());
              } catch (Exception e) {
                logger.error("", e);
              }
            }
          }
        }
      }
    }
  }


  /**
   * Create one group of multiple images with the same dimensions
   */
  public void imagesToOneGroup() {
    try {
      // search for the same image size
      TreePath[] add = DialogLoggerUtil.showTreeDialogAndChoose(window, getTree().getTree(),
          TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION, "Multi selection",
          "Select multiple groups to merge");

      if (add.length <= 1) {
        DialogLoggerUtil.showErrorDialog(window, "Error",
            "Please select multiple groups to create one group");
        logger.error("Please select multiple groups to create one group");
        return;
      }

      ImageGroupMD result = new ImageGroupMD();
      ImagingProject project = null;

      for (TreePath a : add) {
        ImageGroupMD group = getTree().getImageGroup(a);
        // error
        if (group == null) {
          DialogLoggerUtil.showErrorDialog(window, "Error",
              "Please select multiple groups to create one group");
          logger.error("Please select multiple groups to create one group");
          return;
        } else {
          if (project == null)
            project = group.getProject();
          // add all images
          // TODO copy
          for (Image2D d : group.getImagesOnly())
            result.add(d);
        }
      }

      result.setGroupName("Combined");
      project.add(result);
      addGroup(result, project);
    } catch (Exception e1) {
      DialogLoggerUtil.showErrorDialog(window,
          "Groups were not compatible (need to have the same data dimensions", e1);
      logger.error("Groups were not compatible (need to have the same data dimensions", e1);
    }
  }

  /**
   * creates an overlay of the currently selected group
   */
  public void createOverlay() {

    if (selectedImage != null) {
      try {
        ImageGroupMD group = selectedImage.getImageGroup();
        if (group != null) {

          // add overlay
          SettingsImageOverlay settings = new SettingsImageOverlay();
          ImageOverlay ov = new ImageOverlay(group, settings);
          group.add(ov);

          addImageNode(ov, selectedImage.getImageGroup().getNode());
          // update tree
          // treeImg.reload();
        }
      } catch (Exception e) {
        logger.error("", e);
      }
    }
  }


  /**
   * creates a group of ImageMerge for this project
   */
  public void createImageMerge() {
    ImagingProject project = treeImg.getSelectedProject();
    if (project != null) {
      // find all titles
      ImageGroupMD g0 = project.get(0);

      // final group of merges
      ImageGroupMD mergeGroup = new ImageGroupMD();
      mergeGroup.setGroupName("merge");
      mergeGroup.setProject(project);
      // for each title
      for (Collectable2D c : g0.getImages()) {
        if (c instanceof DataCollectable2D) {
          SettingsImageMerge sett = new SettingsImageMerge();
          try {
            ImageMerge m = new ImageMerge(mergeGroup, sett, c.getTitle(), true);
            mergeGroup.add(m);
          } catch (Exception e) {
            logger.error("Create image merge failed for img {}", c.getTitle(), e);
          }
        }
      }
      logger.debug("Created {} merge images", mergeGroup.size());

      // add group to tree
      project.add(mergeGroup);
      addGroup(mergeGroup, project);
    }
  }

  /**
   * Create SP image
   * 
   * @return
   */
  public void createSingleParticleImage() {
    if (selectedImage != null && selectedImage.isImage2D()) {
      Image2D img = (Image2D) selectedImage;
      createSingleParticleImage(img);
    }
  }

  /**
   * Create SP image
   * 
   * @return
   */
  public void createSingleParticleImage(Image2D img) {
    if (img != null) {
      try {
        ImageGroupMD group = img.getImageGroup();
        if (group != null) {
          // add overlay
          SettingsSPImage settings = new SettingsSPImage();
          settings.getSettImage().setTitle(img.getTitle() + "sp");
          settings.getSettImage().setShortTitle(img.getShortTitle() + "sp");
          SingleParticleImage spi = new SingleParticleImage(img, settings);
          SettingsPaintScale ps = spi.getPaintScaleSettings();
          if (ps != null) {
            ps.setModeMax(ValueMode.RELATIVE);
            ps.setMax(100);
            ps.setModeMin(ValueMode.ABSOLUTE);
            ps.setMin(0);
          }
          group.add(spi);

          DefaultMutableTreeNode node = group.getNode();
          if (node != null)
            addImageNode(spi, node);
          // update tree
          // treeImg.reload();
        }
      } catch (Exception e) {
        logger.error("Cannot create single particle image", e);
      }
    }
  }


  /**
   * Convert current image group to continuous data to resplit lines
   * 
   * @return
   */
  public ImageGroupMD convertImageGroupToContinuousData() {
    if (getSelectedImage() != null)
      return convertImageGroupToContinuousData(selectedImage.getImageGroup());
    else
      return null;
  }

  /**
   * Convert current image group to continuous data to resplit lines
   * 
   * @return
   */
  public ImageGroupMD convertImageGroupToContinuousData(ImageGroupMD g) {
    if (g == null)
      return null;
    else {
      MDDataset data = g.getData();
      if (data instanceof DatasetContinuousMD)
        return g;
      else {
        ScanLineMD line = new ScanLineMD();
        // create new group
        int dim = data.size();
        List<Float> x = new ArrayList<Float>();
        Image2D[] img = g.getImagesOnly();

        String[] titles = new String[img.length];
        int c = 0;
        for (Image2D i : img) {
          double[] d = i.toIArray(true);
          line.addDimension(d);
          titles[c] = i.getTitle();
          c++;
        }

        // x
        float lastV = 0;
        // width
        float space = data.getLastXLine(0) - data.getX(0, 0);
        space = space / data.getLineLength(0);
        for (int l = 0; l < data.getLinesCount(); l++) {
          // x0 of line
          float x0 = data.getX(l, 0);
          for (int dp = 0; dp < data.getLineLength(l); dp++) {
            float xv = lastV + data.getX(l, dp) - x0;
            x.add(xv);
          }
          lastV = x.get(x.size() - 1) + space;
        }

        line.setX(x);

        DatasetContinuousMD dat2 = new DatasetContinuousMD(line);
        ImageGroupMD g2 = dat2.createImageGroup(
            new File(g.getFirstImage2D().getSettings().getSettImage().getRAWFilepath()), titles);
        g2.setGroupName(g.getName() + "(continuous)");

        g.getProject().add(g2);
        addGroup(g2, g.getProject());
        return g2;
      }
    }
  }

  /**
   * imports a down sampled microscopic image to the selected image group
   */
  public void importMicroscopicImageDownSampled() {
    if (selectedImage != null) {
      try {
        // choose files
        if (preferences.getFcImportPicture()
            .showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
          // many folders or files
          File file = preferences.getFcImportPicture().getSelectedFile();
          // open Image
          BufferedImage image = ImageIO.read(file);
          // down sample to target
          int tw = selectedImage.getWidthAsMaxDP();
          int th = selectedImage.getHeightAsMaxDP();

          Image ti = image.getScaledInstance(tw, th, Image.SCALE_AREA_AVERAGING);
          BufferedImage scaled = new BufferedImage(tw, th, image.getType());

          Graphics2D graphics = scaled.createGraphics();
          graphics.drawImage(ti, 0, 0, null);

          int rot = selectedImage.getImageGroup().getFirstImage2D().getSettings().getSettImage()
              .getRotationOfData();

          // generate dataset
          List<double[]> data = new ArrayList<double[]>();

          if (rot == 0 || rot == 180) {

            for (int l = 0; l < th; l++) {
              data.add(new double[tw]);
              for (int dp = 0; dp < tw; dp++) {
                int rgb = scaled.getRGB(dp, l);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = (rgb & 0xFF);
                double gray = (r + g + b) / 3.0;

                data.get(l)[dp] = 255.0 - gray;
              }
            }
          } else {
            for (int l = 0; l < tw; l++) {
              data.add(new double[th]);
              for (int dp = 0; dp < th; dp++) {
                int rgb = scaled.getRGB(l, dp);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = (rgb & 0xFF);
                double gray = (r + g + b) / 3.0;

                data.get(l)[dp] = 255.0 - gray;
              }
            }
          }

          // create image
          int index = ((MDDataset) selectedImage.getImageGroup().getData()).addDimension(data);
          Image2D result =
              new Image2D((ImageDataset) selectedImage.getImageGroup().getData(), index);

          result.getSettings().getSettImage().setTitle(file.getName());
          result.getSettings().getSettImage().setRAWFilepath(file.getAbsolutePath());
          // add to image group
          selectedImage.getImageGroup().add(result);
          addImageNode(result, selectedImage.getImageGroup().getNode());

          // update tree
          // treeImg.reload();
        }
      } catch (IOException e) {
        logger.error("Import down sampled microscopic image failed", e);
      }
    }
  }


  /**
   * imports a microscopic image to the selected image group background
   */
  public File importMicroscopicImageBG() {
    if (selectedImage != null) {
      try {
        // choose files
        if (preferences.getFcImportPicture()
            .showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
          // many folders or files
          File file = preferences.getFcImportPicture().getSelectedFile();
          // open Image
          BufferedImage image = ImageIO.read(file);

          selectedImage.getImageGroup().setBackgroundImage(image, file);
          return file;
        } else
          return null;
      } catch (IOException e) {
        logger.error("Import microscopic image as background failed", e);
        return null;
      }
    } else
      return null;
  }

  // #####################################################################################
  // Direct imaging analysis

  /**
   * starting direct imaging analysis with one file selected or one folder
   * 
   * @param sett
   * @param files
   * @param project
   */
  public void startDirectImagingAnalysis(SettingsImageDataImportTxt sett, File[] files,
      ImagingProject project) {
    File dir = files[0].isFile() ? files[0].getParentFile() : files[0];
    //
    diaRunner.startDIA(dir, sett);
  }

  // ##################################################################################
  // GETTERS AND SETTERS
  public List<ImagingProject> getProjects() {
    return treeImg.getProjects();
  }

  public List<Collectable2D> getListImages() {
    ArrayList<Collectable2D> list = new ArrayList<Collectable2D>();
    treeImg.toList(list, Collectable2D.class);
    return list;
  }

  public ArrayList<Image2D> getListImage2DOnly() {
    ArrayList<Image2D> list = new ArrayList<Image2D>();
    for (Collectable2D c : getListImages())
      if (c.isImage2D())
        list.add((Image2D) c);
    return list;
  }

  public List<DataCollectable2D> getListDataCollectable2DOnly() {
    ArrayList<DataCollectable2D> list = new ArrayList<DataCollectable2D>();
    for (Collectable2D c : getListImages())
      if (c instanceof DataCollectable2D)
        list.add((DataCollectable2D) c);
    return list;
  }

  public Collectable2D getSelectedImage() {
    return selectedImage;
  }

  public DirectImageLogicRunner getDIARunner() {
    return diaRunner;
  }

  public static boolean IS_UPDATING() {
    return IS_UPDATING;
  }

  public static void setIS_UPDATING(boolean iS_UPDATING) {
    IS_UPDATING = iS_UPDATING;
  }

  public Heatmap getCurrentHeat() {
    return currentHeat;
  }

  public void setCurrentHeat(Heatmap currentHeat) {
    this.currentHeat = currentHeat;
  }

  public ModuleTree<Collectable2D> getTree() {
    return treeImg;
  }


}
