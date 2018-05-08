package net.rs.lamsi.multiimager.Frames;

import java.io.File;
import java.util.Vector;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.framework.modules.tree.IconNode;
import net.rs.lamsi.general.settings.image.visualisation.SettingsPaintScale.ValueMode;
import net.rs.lamsi.general.settings.importexport.SettingsImageDataImportTxt;
import net.rs.lamsi.multiimager.directimaging.DIATask;
import net.rs.lamsi.utils.FileAndPathUtil;
import net.rs.lamsi.utils.mywriterreader.TxtWriter;
import net.rs.lamsi.utils.useful.FileNameExtFilter;

/**
 * this one is for direct imaging analysis it runs in a loop loading the files every T min and
 * updating the images
 * 
 * @author vukmir69
 *
 */
public class DirectImageLogicRunner implements Runnable {
  private ImageLogicRunner runner;
  //
  private TxtWriter writer = new TxtWriter();
  private Thread thread;
  private SettingsImageDataImportTxt settings;
  // parameters
  private boolean isPaused = true, sumTasks = true;
  private File dir = null;
  private long sleepSec = 4;
  private String fileExt = "", startsWith = "";
  private FileNameExtFilter fileFilter;
  // save stuff that was loaded here
  // task stores all images, completed lines, fileDim, current line
  // add all files that are completed
  private Vector<DIATask> tasks;
  private Vector<Image2D> sumimages = null;
  //
  private boolean autoScale = true;
  private double scaleFactor = 2.0;



  /*
   * old stuff //private int newFileIndex = 0; private FileDim[] lastFiles; // save all lines in
   * here. old lines have to be deleted and new lines are to be added private Vector<ScanLine>[]
   * lines;
   */

  // counts the loops
  private boolean isFirstRun = false;


  /**
   * 
   * @param runner
   */
  public DirectImageLogicRunner(ImageLogicRunner runner) {
    this.runner = runner;
  }

  // run in a loop
  /*
   * set up new tasks - add images to tree - all finished lines and filedim to diatask next run:
   * search for new files - compare with old dim - new task found? - - new task = more File[] in sub
   * vector
   */
  @Override
  public void run() {
    // TODO commented out for test. this is the actual version
    // SettingsImageDataImportTxt sett = (SettingsImageDataImportTxt) settings;
    // IconNode parent = null, sumNode = null;
    // while(!isPaused()) {
    // long time1 = System.currentTimeMillis();
    // // files will be stored in files in each task
    // Vector<File[]> sub = FileAndPathUtil.findFilesInDir(dir, sett.getFilter(), true,
    // sett.isFilesInSeparateFolders());
    //
    // if(sub!=null && sub.size()>0) {
    // // first run
    // if(isFirstRun) {
    // // clear tree
    // runner.getTree().removeAllElements();
    // // create Images totally new
    // tasks = new Vector<DIATask>();
    // try {
    // // create parent node for tree
    // parent = new IconNode(dir.getName()+"; "+dir.getAbsolutePath());
    // // each i[] is a task with one data origin and multiple images
    // for(int i = 0; i< sub.size(); i++) {
    // DIATask task = createTask(sub.get(i), parent, i);
    // if(task!=null)
    // tasks.add(task);
    // }
    // // add parent to tree
    // runner.getTree().addNodeToRoot(parent);
    // isFirstRun=false;
    // } catch (Exception e) {
    // logger.error("",e);
    // }
    // }
    // else {
    // // just add new lines
    // // read data:
    // try {
    // // first: new task or still same amount
    // if(sub.size()>tasks.size()) {
    // // new tasks! which one?
    // int newi;
    // for(int i=0; i<sub.size(); i++) {
    // boolean isNew = true;
    // for(int t=0; t<tasks.size(); t++) {
    // // same?
    // String idT = tasks.get(t).getIdentifier();
    // File p = sub.get(i)[0].getParentFile();
    // if(sett.isFilesInSeparateFolders() && !p.getParent().equals(dir.getAbsolutePath()))
    // p = p.getParentFile();
    // String idS = p.getAbsolutePath();
    //
    // ImageEditorWindow.log("Compare "+idT + " # "+idS, LOG.DEBUG);
    // // compare
    // if(idT.equals(idS)) {
    // isNew = false;
    // tasks.get(t).setIndex(i);
    // break;
    // }
    // }
    // // spot found?
    // if(isNew) {
    // ImageEditorWindow.log("Task added to "+i+" position", LOG.MESSAGE);
    // // add new task at i
    // DIATask task = createTask(sub.get(i), parent, i);
    // if(task!=null) {
    // tasks.add(task);
    // runner.getTree().getTreeModel().nodeStructureChanged(parent);
    // }
    // }
    // }
    //
    // }
    //
    // // Update all tasks
    // for(int t = 0; t<tasks.size(); t++) {
    // DIATask tsk = tasks.get(t);
    // File[] files = sub.get(tsk.getIndex());
    // // get all filedimensions like lines/length... for later comparison
    // FileDim[] dim = writer.getFileDim(files);
    // // compare to calc newFileIndex
    // tsk.compareNewAndOldFiles(dim);
    // // where to start importing?
    // int newFileIndex = tsk.getNewFileIndex();
    //
    // // create new files array
    // File[] newFiles = new File[files.length-newFileIndex];
    // for(int nf=newFileIndex; nf<files.length; nf++) {
    // newFiles[nf-newFileIndex] = files[nf];
    // }
    // // only load data if there are new lines
    // if(newFiles.length>0) {
    // // create new Vector<ScanLine> [] (kind of Image2D without settings)
    // Vector<ScanLineMD> newLines = Image2DImportExportUtil.importTextFilesToScanLines(newFiles,
    // settings, ((SettingsImageDataImportTxt)settings).getSeparation(), false);
    //
    // // try and add new lines to tasks images
    // try{
    // tsk.addNewLines(newLines, autoScale, scaleFactor);
    // } catch(Exception ex) {
    // logger.error("",ex);
    // ImageEditorWindow.log("restarting dia", LOG.WARNING);
    // // restart dia!
    // startDIA(dir, sett);
    // }
    // }
    // }
    // //
    // } catch (Exception e) {
    // logger.error("",e);
    // }
    // // send update to
    // runner.renewImage2DView();
    // // on error the loop will skip this section
    // try {
    // thread.sleep(sleepSec*1000);
    // } catch(Exception ex) {
    // logger.error("",ex);
    // }
    // }
    //
    // // sum all tasks to one
    // if(sumTasks) {
    // // more than one task?
    // if(tasks.size()>1) {
    // // create new TODO
    // if(sumimages==null) {
    // // new
    // sumimages = new Vector<Image2D>();
    // // add images to tree later
    // sumNode = new IconNode("Sum task");
    // parent.add(sumNode);
    // runner.getTree().getTreeModel().nodeStructureChanged(parent);
    // }
    // // go through all images of all tasks
    // Vector<ScanLine2D[]> list = new Vector<ScanLine2D[]>();
    // // init
    // for(int i=0; i<sumimages.size(); i++) {
    // list.add(new ScanLine2D[0]);
    // }
    //
    // // sum all tasks
    // for(DIATask tsk : tasks) {
    // for(Image2D img : tsk.getImg()) {
    // boolean added = false;
    // // check if image is already in list
    // for(int i=0; i<sumimages.size(); i++) {
    // ScanLine2D[] old = list.get(i);
    // if(sumimages.get(i).getTitle().equals(img.getTitle())) {
    // added = true;
    // // add lines on top
    // int aLen = old.length;
    // int bLen = img.getLineCount();
    // ScanLine2D[] lines = new ScanLine2D[aLen+bLen];
    // System.arraycopy(old, 0, lines, 0, aLen);
    // System.arraycopy(img.getLines(), 0, lines, aLen, bLen);
    //
    // list.add(i, lines);
    // list.remove(i+1);
    // }
    // }
    // // not added? create new image copy
    // if(!added) {
    // try {
    // Image2D copy = img.getCopy();
    // sumimages.add(copy);
    // list.add(copy.getLines());
    // runner.addImage(copy, sumNode);
    // runner.getTree().getTreeModel().nodeStructureChanged(sumNode);
    // } catch (Exception e) {
    // logger.error("",e);
    // }
    // }
    // }
    // }
    //
    // // setting the lines
    // for(int i=0; i<sumimages.size() && i<list.size(); i++) {
    // sumimages.get(i).setLines(list.get(i));
    // }
    //
    // // settings Max for better range in paintscale
    // if(autoScale) {
    // for(Image2D old : sumimages) {
    // // change max of paintscale to fit last full line
    // int l = old.getLines().length-2;
    // if(l<0) l=0;
    // double min = old.getMinIntensity(false);
    // double max = old.getMaxIntensity(l, false);
    //
    // if(min<max) {
    // max = max + (max-min)*scaleFactor;
    // // max higher than max of image?
    // double maxall = old.getMaxIntensity(false);
    // if(max>maxall) max = maxall;
    // // set wider range
    // old.getSettPaintScale().setUsesMinMax(true);
    // old.getSettPaintScale().setUsesMaxValues(true);
    // old.getSettPaintScale().setMax(max);
    // }
    // }
    // }
    // }
    // }
    //
    // }
    // }
  }


  /*
   * old one // run in a loop
   * 
   * @Override public void run() { SettingsImageDataImportTxt sett = (SettingsImageDataImportTxt)
   * settings; while(!isPaused()) { long time1 = System.currentTimeMillis(); // files will be stored
   * in files File[] files = findFilesInDir(dir);
   * 
   * Vector<File[]> sub = FileAndPathUtil.findFilesInDir(dir, sett.getFilter(), true,
   * sett.isFilesInSeparateFolders());
   * 
   * for(File[] i : sub) { // load them as image set Image2D[] imgs =
   * Image2DImportExportUtil.importTextDataToImage(i, sett, true);
   * ImageEditorWindow.log("Imported image "+i[0].getName(), LOG.DEBUG); if(imgs.length>0) { // add
   * img to list addCollection2D(imgs, fnode, sett); } }
   * 
   * // get all filedimensions like lines/length... for later comparison FileDim[] dim =
   * writer.getFileDim(files);
   * 
   * if(files!=null && files.length>0) { // first run if(lastFiles==null) { // create Images totally
   * new Image2D[] imgList; try { imgList = Image2DImportExportUtil.importTextDataToImage(files,
   * sett, false); // save lines here in lines vector lines = new Vector[imgList.length]; //
   * IconNode parent = new IconNode(dir.getName()+"; "+dir.getAbsolutePath());
   * runner.addCollection2D(imgList, parent, sett); runner.getTree().addNodeToRoot(parent); // add
   * each image and add all line of images to lines vector for(int img=0; img<imgList.length; img++)
   * { // add all lines lines[img] = imgList[img].getLinesAsVector(); } if(imgList.length>0)
   * lastFiles = dim; } catch (Exception e) { logger.error("",e); } } else { try { // new version
   * to find new files for(int nf=newFileIndex; nf<files.length; nf++) { for(int lf=newFileIndex;
   * lf<lastFiles.length; lf++) { if(dim[nf].compareTo(lastFiles[lf])) { newFileIndex++;
   * ImageEditorWindow.log("####NEW FILE####", LOG.MESSAGE); } } } // create new files array File[]
   * newFiles = new File[files.length-newFileIndex]; for(int nf=newFileIndex; nf<files.length; nf++)
   * { newFiles[nf-newFileIndex] = files[nf]; } if(newFiles.length>0) { // get current Image2D
   * Vector Vector<Image2D> imgList = runner.getListImages(); // create new Vector<ScanLine> []
   * (kind of Image2D without settings) Vector<ScanLine>[] newLines =
   * Image2DImportExportUtil.importTextFilesToScanLines(newFiles, settings,
   * ((SettingsImageDataImportTxt)settings).getSeparation(), false);
   * 
   * // same size? if(newLines!=null && imgList.size()==newLines.length) { // for all images /
   * scanline vectors for(int i=0; i<imgList.size(); i++) { // delete new lines that are in lines
   * vector (>newFileIndex) for(int n=newFileIndex; n<lines[i].size(); n++)
   * lines[i].remove(newFileIndex);
   * 
   * // add new lines lines[i].addAll(newLines[i]);
   * 
   * // keep old image2d objects but change the lines vectors
   * imgList.get(i).setLines(lines[i].toArray(new ScanLine[lines[i].size()])); } // send update to
   * runner.renewImage2DView(); } else { // error - reset and load images new
   * ImageEditorWindow.log("ERROR while loading new lines", LOG.ERROR); lastFiles = null; continue;
   * // next while loop to load images new }
   * 
   * //check all 10 times if there was an error and create images new if needed // new version to
   * find new files
   * 
   * } } catch (Exception e) { // TODO Auto-generated catch block logger.error("",e); } // on error
   * the loop will skip this section // keep the last files lastFiles = dim; loops ++; try {
   * thread.sleep(sleepSec*1000); } catch(Exception ex) { logger.error("",ex); } } } } }
   */

  /**
   * adds a task to the given tasks vector
   * 
   * @param i
   * @param parent
   * @param tasks2
   * @throws Exception
   */
  private DIATask createTask(File[] i, IconNode parent, int index) throws Exception {
    // // load them as image set
    // ImageGroupMD[] imgs = Image2DImportExportUtil.importTextDataToImage(i,settings, true);
    // ImageEditorWindow.log("Imported image "+i[0].getName(), LOG.DEBUG);
    // for(ImageGroupMD g : imgs)
    // if(g.getImages().size()>0) {
    // // add img to list
    // IconNode nodes[] = runner.addGroup(g, parent);
    // // get all filedimensions like lines/length... for later comparison
    // FileDim[] dim = writer.getFileDim(i);
    // // create task
    // return new DIATask(g, dim, nodes, index);
    // }
    return null;
  }

  /**
   * in sorted order
   * 
   * @param dir2
   * @return
   */
  private File[] findFilesInDir(File dir) {
    File[] subDir = FileAndPathUtil.getSubDirectories(dir);

    if (subDir.length <= 0) {
      // no subdir end directly
      // sort all files and return them
      return FileAndPathUtil.sortFilesByNumber(dir.listFiles(fileFilter));
    } else {
      // sort dirs
      subDir = FileAndPathUtil.sortFilesByNumber(subDir);
      // go in all sub and subsub... folders to find files
      Vector<File> list = new Vector<File>();
      findFilesInDir(subDir, list);
      // return as array (unsorted because they are sorted folder wise)
      return list.toArray(new File[list.size()]);
    }
  }

  /**
   * go into all subfolders and find all files and go in further subfolders
   * 
   * @param dir musst be sorted!
   * @param list
   * @return
   */
  private void findFilesInDir(File[] dirs, Vector<File> list) {
    // go into folder and find files
    // each file in one folder
    for (int i = 0; i < dirs.length; i++) {
      // find all suiting files
      File[] subFiles = dirs[i].listFiles(fileFilter);
      // put them into the list
      for (int f = 0; f < subFiles.length; f++) {
        list.addElement(subFiles[f]);
      }
      // find all subfolders, sort them and do the same iterative
      File[] subDir = FileAndPathUtil.sortFilesByNumber(FileAndPathUtil.getSubDirectories(dirs[i]));
      // call this method
      findFilesInDir(subDir, list);
    }
  }

  public void startDIA(File dir, SettingsImageDataImportTxt settings) {
    this.dir = dir;
    this.settings = settings;
    isFirstRun = true;
    sumimages = null;
  }


  // #########################################################################
  // getters and setters
  public boolean isPaused() {
    return isPaused;
  }

  /**
   * this actually starts DIA
   * 
   * @param isPaused
   */
  public void setPaused(boolean isPaused) {
    this.isPaused = isPaused;
    if (isPaused == false) {
      thread = new Thread(this);
      thread.start();
    }
  }

  /**
   * call this before setPaused(false) (before starting DIA)
   * 
   * @param sleep
   * @param fileFilter
   * @param startsWith
   * @param sumTasks
   */
  public void setUp(long sleep, String fileFilterExt, String startsWith, boolean sumTasks,
      boolean autoScale, double scaleFactor) {
    this.sleepSec = sleep;
    this.startsWith = startsWith;
    this.fileExt = fileFilterExt;
    this.fileFilter = new FileNameExtFilter(startsWith, fileExt);
    if (settings != null)
      settings.setFilter(fileFilter);
    this.sumTasks = sumTasks;
    this.autoScale = autoScale;
    this.scaleFactor = scaleFactor;
  }

  public void setPaused() {
    isPaused = true;
  }

  public void resume() {
    isPaused = false;
    thread = new Thread(this);
    thread.start();
  }

  public long getSleepSec() {
    return sleepSec;
  }

  public void setSleepSec(long sleepSec) {
    this.sleepSec = sleepSec;
  }

  public boolean isAutoScale() {
    return autoScale;
  }

  public void setAutoScale(boolean autoScale) {
    this.autoScale = autoScale;
    if (!autoScale) {
      // reset to img max (reverse effect of autoscale)
      for (DIATask task : tasks) {
        for (Image2D img : task.getImg().getImagesOnly()) {
          img.getSettings().getSettPaintScale().setUsesMinMax(true);
          img.getSettings().getSettPaintScale().setModeMin(ValueMode.PERCENTILE);
          img.getSettings().getSettPaintScale().setModeMax(ValueMode.PERCENTILE);
          img.getSettings().getSettPaintScale().setMax(img.getMaxIntensity(false));
        }
      }
      for (Image2D img : sumimages) {
        img.getSettings().getSettPaintScale().setUsesMinMax(true);
        img.getSettings().getSettPaintScale().setModeMin(ValueMode.PERCENTILE);
        img.getSettings().getSettPaintScale().setModeMax(ValueMode.PERCENTILE);
        img.getSettings().getSettPaintScale().setMax(img.getMaxIntensity(false));
      }
    }
  }

  public double getScaleFactor() {
    return scaleFactor;
  }

  public void setScaleFactor(double scaleFactor) {
    this.scaleFactor = scaleFactor;
  }

}
