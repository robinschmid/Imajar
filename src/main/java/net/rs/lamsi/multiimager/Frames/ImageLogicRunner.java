package net.rs.lamsi.multiimager.Frames;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.ImageGroupMD;
import net.rs.lamsi.general.datamodel.image.ImageOverlay;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.datamodel.image.interf.ImageDataset;
import net.rs.lamsi.general.datamodel.image.interf.MDDataset;
import net.rs.lamsi.massimager.Frames.Dialogs.GraphicsExportDialog;
import net.rs.lamsi.massimager.Frames.Dialogs.ProgressDialog;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.ModuleTree;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.tree.IconNode;
import net.rs.lamsi.massimager.Heatmap.Heatmap;
import net.rs.lamsi.massimager.Heatmap.HeatmapFactory;
import net.rs.lamsi.massimager.MyFileChooser.FileTypeFilter;
import net.rs.lamsi.massimager.Settings.SettingsHolder;
import net.rs.lamsi.massimager.Settings.image.SettingsImageOverlay;
import net.rs.lamsi.massimager.Settings.importexport.SettingsImageDataImportTxt;
import net.rs.lamsi.massimager.Settings.preferences.SettingsGeneralPreferences;
import net.rs.lamsi.massimager.Threads.ProgressUpdateTask;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow.LOG;
import net.rs.lamsi.multiimager.utils.imageimportexport.DataExportUtil;
import net.rs.lamsi.multiimager.utils.imageimportexport.Image2DImportExportUtil;
import net.rs.lamsi.utils.DialogLoggerUtil;
import net.rs.lamsi.utils.FileAndPathUtil;
import net.rs.lamsi.utils.mywriterreader.BinaryWriterReader;
import net.rs.lamsi.utils.mywriterreader.TxtWriter;
import net.rs.lamsi.utils.useful.DebugStopWatch;

import org.jfree.chart.ChartPanel;

public class ImageLogicRunner {
	//##################################################################################
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

	//##################################################################################
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
					TreePath path = treeImg.getTree().getSelectionPath();
					if(path!=null) {
						DefaultMutableTreeNode img = (DefaultMutableTreeNode) path.getLastPathComponent();
						if(Collectable2D.class.isInstance(img.getUserObject())) { 
							setSelectedImageAndShow((Collectable2D)img.getUserObject());
						}
					}
				}catch(Exception ex) { 
					ex.printStackTrace();
				} 
			} 
		});
		// init heatmapfactory
		heatFactory = new HeatmapFactory();
		// Init binaryWriter
		binaryWriter = new BinaryWriterReader();
		txtWriter = new TxtWriter();
	} 

	//############################################################################################
	// Add, select, renew, delete images 
	/**
	 *  add and remove image from jList gets automatically removed from vector<Image2D>
	 * @param id parent node id
	 */
	public IconNode addImage(Image2D i, String id) {  
		// TODO 
		// test for existing parent node
		if(mapNodes.containsKey(id)){
			boolean added = false;
			// add to data set - this generates a new image2D
			IconNode parent = mapNodes.get(id);
			if(parent.getChildCount()>0) {
				Image2D friend = ((Image2D)((IconNode)parent.getFirstChild()).getUserObject());
				if(friend.getImageGroup()!=null) {
					// add to MD data set and set this data set to image i
					added = ((MDDataset)friend.getData()).addDimension(i);
					if(added) {
						// add to group 
						friend.getImageGroup().add(i);
					}
				}
			}
			// add to tree
			if(added) {
				IconNode node = addImage(i, parent); 
				treeImg.getTreeModel().reload(); 
				return node;
			}
			else {
				DialogLoggerUtil.showMessageDialogForTime(window, "Image was not added", "Image was not added due to different data dimensions", 2000);
				return null;
			}
		}
		else {  
			// create image group 
			ImageGroupMD group = new ImageGroupMD(i);
			// add to tree
			IconNode parent = new IconNode(id);
			mapNodes.put(id, parent);
			IconNode node = addImage(i, parent);
			treeImg.addNodeToRoot(parent);
			return node;
		}
	}
	/**
	 * adds an image to a parent node
	 * @param i
	 * @param parent
	 */
	public IconNode addImage(Collectable2D i, DefaultMutableTreeNode parent) {  
		//
		SettingsGeneralPreferences sett = SettingsHolder.getSettings().getSetGeneralPreferences();
		IconNode node = new IconNode(i, false, window.isCreatingImageIcons()? i.getIcon(sett.getIconWidth(), sett.getIconHeight()) : null);
		parent.add(node);
		return node;
	}
	// OLD
//	public IconNode[] addCollection2D(Image2D[] img, IconNode parent) { 
//		// only one image? do not create subnodes
//		if(img==null || img.length==0) {
//			return null;
//		}
//		else if(img.length==1) {
//			return new IconNode[]{addImage(img[0], parent)};
//		}
//		else {
//			SettingsGeneralPreferences pref = SettingsHolder.getSettings().getSetGeneralPreferences();
//			// create subnode with image name
//			DefaultMutableTreeNode sub = null;
//			String last = null;
//			IconNode[] nodes = new IconNode[img.length];
//			int c = 0;
//			// create img nodes
//			for(Image2D i : img) {
//				if(sub==null || (!last.equals(i.getSettImage().getRAWFolder()))) {
//					sub = new IconNode(i.getSettImage().getRAWFolderName()+"; "+i.getSettImage().getRAWFolder());
//					parent.add(sub);
//					last = i.getSettImage().getRAWFolder(); 
//				}
//				IconNode inode = new IconNode(i, false, window.isCreatingImageIcons()? i.getIcon(pref.getIconWidth(), pref.getIconHeight()) : null);
//				nodes[c] = inode;
//				sub.add(inode);
//				c++;
//			} 
//			return nodes;
//		}
//	}


	public IconNode[] addCollection2D(ImageGroupMD img, DefaultMutableTreeNode parent) {
		// only one image? do not create subnodes
		if(img==null || img.getImages().size()==0) {
			return null;
		} 
		else {
			SettingsGeneralPreferences pref = SettingsHolder.getSettings().getSetGeneralPreferences();
			// create subnode with image name
			DefaultMutableTreeNode sub = null;
			String last = null;
			Image2D lastI = null;
			IconNode[] nodes = new IconNode[img.getImages().size()];
			int c = 0;

			// create img nodes
			for(Collectable2D c2d : img.getImages()) {
				if(Image2D.class.isInstance(c2d)) {
					Image2D i = (Image2D) c2d;
					if(sub==null || (last==null && !lastI.hasSameData(i)) || (last!=null && !last.equals(i.getSettings().getSettImage().getRAWFolder()))) { 
						String name = i.getSettings().getSettImage().getRAWFolder()==null? "NODEF" : i.getSettings().getSettImage().getRAWFolderName()+"; "+i.getSettings().getSettImage().getRAWFolder(); 
						sub = new IconNode(name);
						parent.add(sub);
						last = i.getSettings().getSettImage().getRAWFolder(); 
					}
					IconNode inode = new IconNode(i, false, window.isCreatingImageIcons()? i.getIcon(pref.getIconWidth(), pref.getIconHeight()) : null);
					nodes[c] = inode;
					sub.add(inode);
					c++;
					lastI = i;
				}
			} 
			// add overlays afterwards
			for(Collectable2D c2d : img.getImages()) {
				if(ImageOverlay.class.isInstance(c2d)) {
					ImageOverlay i = (ImageOverlay) c2d;
					IconNode inode = new IconNode(i, false, window.isCreatingImageIcons()? i.getIcon(pref.getIconWidth(), pref.getIconHeight()) : null);
					nodes[c] = inode;
					sub.add(inode);
					c++;
				}
			} 
			img.setNode(sub);
			return nodes;
		}
	}

	public void removeAllImage() {
		treeImg.removeAllElements();
	}

	/**
	 * gets called by listImages.selectionChanged, 
	 * sets the selected Image 
	 * creates and shows a heatmap in chartpanel in centerView
	 */
	public void setSelectedImageAndShow(Collectable2D c2d) { 
		// 
		if(c2d == null) {
			ImageEditorWindow.log("REMOVE VIEWED IMAGE2D", LOG.DEBUG);
			selectedImage= c2d;
			// TODO set image2D -> null?
			// remove heatmap von central 
			window.getPnCenterImageView().removeAll(); 
		}
		else if(this.selectedImage!=c2d) {
			ImageEditorWindow.log("UPDATE IMAGE2D", LOG.DEBUG); 
			//
			selectedImage= c2d;
			// Renew all Modules
			DebugStopWatch debug = new DebugStopWatch();
			if(Image2D.class.isInstance(c2d)) {
				window.setImage2D((Image2D)c2d);
				ImageEditorWindow.log("setImage2D took "+debug.stop(), LOG.DEBUG);
			}
			else if(ImageOverlay.class.isInstance(c2d)) {
				window.setImageOverlay((ImageOverlay)c2d);
				ImageEditorWindow.log("setImageOverlay took "+debug.stop(), LOG.DEBUG);
			}
			
			// create new heatmap
			debug.setNewStartTime();
			renewImage2DView();
			ImageEditorWindow.log("renewImage2DView took "+debug.stop(), LOG.DEBUG);
		}
	}
	/**
	 * this one gets called to renew the central chartpanel thats displayed by the ImageEditorWindow class
	 * called from logicRunner.setSelectedImageAndShow
	 * called from autoupdater in window class
	 * called from other events on imagemodules
	 * @return
	 */
	public Heatmap renewImage2DView() { 
		if(selectedImage!=null) {
			try{  
				ImageEditorWindow.log("Create Heatmap", LOG.DEBUG); 
				// show heatmap in Center

				DebugStopWatch debug = new DebugStopWatch();
				currentHeat = heatFactory.generateHeatmap(selectedImage);
				ImageEditorWindow.log("creating the heatmap took "+debug.stop(), LOG.DEBUG);
				
				ChartPanel myChart = currentHeat.getChartPanel(); 
				myChart.setMouseWheelEnabled(true);  
				// remove all and add
				window.addHeatmapToPanel(currentHeat);
				return currentHeat;
			}catch(Exception ex) {
				ex.printStackTrace();
				// Dialog
				ImageEditorWindow.log("Cannot create image from "+selectedImage.getTitle()+"; "+ex.getMessage(),LOG.ERROR);
				JOptionPane.showMessageDialog(window, "cannot create image from "+selectedImage.getTitle()+"; "+ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE); 
				return null;
			}
		}
		return null;
	}

	//##################################################################################
	// apply settings 

	/**
	 * opens a dialog. if ok -> change all settings of all images to current settings
	 * Copy settings!
	 */
	public void applySettingsToAllImagesInList() {
		boolean state = DialogLoggerUtil.showDialogYesNo(window, "Change settings of all images in list?", "Attention: You are about to replace all settings of all images! Ok?");
		if(state && getSelectedImage()!=null) {
			// current settings as image 
			
			// get List of images
			for(Collectable2D img : getListImages()) { 
				// for each replace all settings	
				getSelectedImage().applySettingsToOtherImage((img));
			}
		}
	} 


	//##################################################################################
	// LOAD AND SAVE IMAGE	
	//
	/**
	 * export heat graphics dialog
	 */
	public void openExportHeatGraphicsDialog() {
		if(currentHeat!=null)
			GraphicsExportDialog.openDialog(currentHeat.getChart(), getListImages());
	}

	// saves selected Image to file
	public void saveImage2DToFile() {
		if (selectedImage !=null && preferences.getFcSave().showSaveDialog(window) == JFileChooser.APPROVE_OPTION) {   
			File file = preferences.getFcSave().getSelectedFile();
			file = preferences.getFileTFImage2D().addExtensionToFileName(file);
			// save Image2D to file
			ImageGroupMD group = selectedImage.getImageGroup();
			if(group==null)
				group = new ImageGroupMD(selectedImage);
			try {
				Image2DImportExportUtil.writeToStandardZip(group, file);
				preferences.addImage2DImportExportPath(file);
			} catch (IOException e) {
				e.printStackTrace();
				ImageEditorWindow.log("Error while writing "+file.getAbsolutePath()+"\n"+e.getMessage(), LOG.ERROR);
			}
		}
	}

	/**
	 * opens a file chooser and
	 * loads own format image2D
	 */
	public void loadImage2DFromFile() {
		if (preferences.getFcOpen().showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {   
			final File[] files = preferences.getFcOpen().getSelectedFiles(); 
			
			// Up	dateTask
			if(ProgressDialog.getInst()==null) ProgressDialog.initDialog(window);
			ProgressUpdateTask task = ProgressDialog.getInst().startTask(new ProgressUpdateTask(ProgressDialog.getInst(), files.length) {
				// Load all Files
				@Override
				protected Boolean doInBackground() throws Exception {
					boolean state = true;
					//
					// load file  
					if(files.length>0) { 
						try {
							// All files in fileList
							for(File f : files) {
								loadImage2DFromFile(f);
								// Progress:
								addProgressStep(1); 
							} 
							// save changes
							preferences.saveChanges();
						} catch(Exception ex) {
							ex.printStackTrace();
						} finally {
							ProgressDialog.getInst().setVisibleDialog(false);
						}
					}
					//
					return state; 
				}
			});
		}
	}


	/**
	 * loads own format image2D
	 */
	public void loadImage2DFromFile(File f) { 
		// image
		if(FileTypeFilter.getExtensionFromFile(f).equalsIgnoreCase(preferences.getFileTFImage2D().getExtension())) {
			try {
				// load image group from file 
				IconNode fnode = new IconNode(f.getName()+"; "+f.getAbsolutePath());
				ImageGroupMD img = Image2DImportExportUtil.readFromStandardZip(f);
				if(img!=null) 
					addCollection2D(img, fnode);
				treeImg.addNodeToRoot(fnode);

				preferences.addImage2DImportExportPath(f, false);
			}catch(Exception ex) {
				ex.printStackTrace();
				// Dialog
				ImageEditorWindow.log("Error while reading "+f.getAbsolutePath()+"\n"+ex.getMessage(), LOG.ERROR);
				JOptionPane.showMessageDialog(window, "Cannot load image file "+f.getPath()+"; "+ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE); 
			}
		} 
	} 

	/** 
	 * Import from data file txt, csv, xlsx
	 * or set up the direct imaging analysis
	 * @param settingsDataImport
	 */
	public void importDataToImage(SettingsImageDataImportTxt settingsDataImport) { 
		// import TXT or csv
		if(settingsDataImport instanceof SettingsImageDataImportTxt) { 
			// txt chooser
			preferences.getFcImport().setFileFilter(preferences.getFileTFtxtcsv());
			// choose files
			if (preferences.getFcImport().showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
				// many folders or files
				File[] files = preferences.getFcImport().getSelectedFiles(); 

				if(files.length>0) {
					// open the files
					SettingsImageDataImportTxt sett = (SettingsImageDataImportTxt) settingsDataImport;

					// import or start direct image analysis?
					if(window.getCurrentView()== ImageEditorWindow.VIEW_IMAGING_ANALYSIS) {
						// import text files
						importTextDataToImage(sett, files);
					}
					else if(window.getCurrentView()== ImageEditorWindow.VIEW_DIRECT_IMAGING_ANALYSIS) {
						// set up direct imaging analysis
						startDirectImagingAnalysis(sett, files);
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
	public void importTextDataToImage(SettingsImageDataImportTxt settingsDataImport, File[] files) { 
		// load image
		try { 
			if(files.length>0) {
				// folder or files?
				if(files[0].isDirectory()) {
					// go into sub folders to find data 
					// load each folder as one set of images
					for(File f : files) {
						if(f.isDirectory()) {
							IconNode fnode = new IconNode(f.getName()+"; "+f.getAbsolutePath());
							// get all files in this folder TODO change csv to settings
							// each file[] element is for one image
							Vector<File[]> sub = FileAndPathUtil.findFilesInDir(f, settingsDataImport.getFilter(), true, settingsDataImport.isFilesInSeparateFolders());

							for(File[] i : sub) {
								// load them as image set
								ImageGroupMD[] imgs = Image2DImportExportUtil.importTextDataToImage(i, settingsDataImport, true); 
								ImageEditorWindow.log("Imported image "+i[0].getName(), LOG.DEBUG);
								for(int coll = 0; coll<imgs.length; coll++) {
									if(imgs[coll].getImages().size()>0) {
										// add img to list
										addCollection2D(imgs[coll], fnode);
									}
								}
							}
							// add
							if(fnode.getChildCount()>0)
								treeImg.addNodeToRoot(fnode);
						}
					}
				}
				else {
					// load all files as one image set
					ImageGroupMD[] imgs = Image2DImportExportUtil.importTextDataToImage(files, settingsDataImport, true); 
					
					// add img to list
					IconNode fnode = new IconNode(files[0].getParentFile().getName()+"; "+files[0].getParent());
					// add all
					for(int coll = 0; coll<imgs.length; coll++) {
						if(imgs[coll].getImages().size()>0) {
							// add img to list
							addCollection2D(imgs[coll], fnode);
						}
					} 
					// add
					if(fnode.getChildCount()>0)
						treeImg.addNodeToRoot(fnode);
				}
			}

		} catch (Exception e) { 
			e.printStackTrace();
			DialogLoggerUtil.showErrorDialog(window, "Import failed", e);
		}
	} 


	/** 
	 * exports a data report on quantification
	 */
	public void exportDataReport() {
		// show save dialog
		JFileChooser chooser = new JFileChooser(lastPath); 
		// export
		if(chooser.showSaveDialog(window)==JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			if(file!=null && getSelectedImage()!=null) {
				try {
					if(Image2D.class.isInstance(selectedImage)) {
						ImageEditorWindow.log("Exporting data report to "+FileAndPathUtil.getRealFilePath(file.getParentFile(), file.getName(), "xlsx").getAbsolutePath(), LOG.MESSAGE);
						DataExportUtil.exportDataReportOnOperations((Image2D) selectedImage, file.getParentFile(), file.getName());
						ImageEditorWindow.log("Exporting finished", LOG.MESSAGE);
					}
				} catch (Exception e) { 
					e.printStackTrace(); 
				}
			}
		}
	}

	

	/**
	 * creates an overlay of the currently selected group
	 */
	public void createOverlay() {

		if(selectedImage!=null) {
			try {
				ImageGroupMD group = selectedImage.getImageGroup();
				if(group!=null) {

					// add overlay
						SettingsImageOverlay settings = new SettingsImageOverlay();
						ImageOverlay ov = new ImageOverlay(group, settings);
						group.add(ov);

						addImage(ov, selectedImage.getImageGroup().getNode());
						// update tree
						treeImg.reload();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * imports a down sampled microscopic image to the selected image group
	 */
	public void importMicroscopicImageDownSampled() {
		if(selectedImage!=null) {
			try {
				// choose files
				if (preferences.getFcImportPicture().showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
					// many folders or files
					File file = preferences.getFcImportPicture().getSelectedFile(); 
					// open Image
					BufferedImage image = ImageIO.read(file);
					// down sample to target
					int tw = selectedImage.getWidthAsMaxDP();
					int th = selectedImage.getHeightAsMaxDP();
					
					Image ti = image.getScaledInstance(tw, th, Image.SCALE_AREA_AVERAGING);
					BufferedImage scaled = new BufferedImage(tw,th, image.getType());

					Graphics2D graphics = scaled.createGraphics();
					graphics.drawImage(ti, 0, 0, null);
					
					int rot = selectedImage.getImageGroup().getFirstImage2D().getSettings().getSettImage().getRotationOfData();
					
					// generate dataset
					Vector<Double[]> data = new Vector<Double[]>();
					
					if(rot==0 || rot==180) {
						
						for(int l = 0; l<th; l++) {
							data.add(new Double[tw]);
							for(int dp=0; dp<tw; dp++) {
								int rgb = scaled.getRGB(dp, l);
								int r = (rgb >> 16) & 0xFF;
								int g = (rgb >> 8) & 0xFF;
								int b = (rgb & 0xFF);
								double gray = (r + g + b) / 3.0;
								
								data.get(l)[dp] = 255.0-gray;
							}
						}
						}
					else {
						for(int l = 0; l<tw; l++) {
							data.add(new Double[th]);
							for(int dp=0; dp<th; dp++) {
								int rgb = scaled.getRGB(l, dp);
								int r = (rgb >> 16) & 0xFF;
								int g = (rgb >> 8) & 0xFF;
								int b = (rgb & 0xFF);
								double gray = (r + g + b) / 3.0;
								
								data.get(l)[dp] = 255.0-gray;
							}
						}
					}
					
					// create image
					int index = ((MDDataset)selectedImage.getImageGroup().getData()).addDimension(data);
					Image2D result = new Image2D((ImageDataset)selectedImage.getImageGroup().getData(), index);
					
					result.getSettings().getSettImage().setTitle(file.getName());
					result.getSettings().getSettImage().setRAWFilepath(file.getAbsolutePath());
					// add to image group
					selectedImage.getImageGroup().add(result);
					addImage(result, selectedImage.getImageGroup().getNode());
					
					// update tree
					treeImg.reload();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * imports a microscopic image to the selected image group background
	 */
	public File importMicroscopicImageBG() {
		if(selectedImage!=null) {
			try {
				// choose files
				if (preferences.getFcImportPicture().showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
					// many folders or files
					File file = preferences.getFcImportPicture().getSelectedFile(); 
					// open Image
					BufferedImage image = ImageIO.read(file);

					selectedImage.getImageGroup().setBackgroundImage(image, file);
					return file;
				}
				else return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		else return null;
	}
	
	//#####################################################################################
	// Direct imaging analysis

	/**
	 * starting direct imaging analysis with one file selected or one folder
	 * @param sett
	 * @param files
	 */
	public void startDirectImagingAnalysis(SettingsImageDataImportTxt sett, File[] files) {
		File dir = files[0].isFile()? files[0].getParentFile() : files[0];
		//  
		diaRunner.startDIA(dir, sett);
	}

	//##################################################################################
	//GETTERS AND SETTERS

	public Vector<Collectable2D> getListImages() {
		return treeImg.toList();
	}

	public Vector<Image2D> getListImage2DOnly() {
		Vector<Image2D> list = new Vector<Image2D>();
		for(Collectable2D c : getListImages())
			if(c.isImage2D())
				list.add((Image2D)c);
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
