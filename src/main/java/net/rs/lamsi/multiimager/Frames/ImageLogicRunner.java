package net.rs.lamsi.multiimager.Frames;

import java.io.File;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.ImageGroupMD;
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
import net.rs.lamsi.massimager.Settings.image.SettingsImageDataImport;
import net.rs.lamsi.massimager.Settings.image.SettingsImageDataImportTxt;
import net.rs.lamsi.massimager.Settings.preferences.SettingsGeneralPreferences;
import net.rs.lamsi.massimager.Threads.ProgressUpdateTask;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow.LOG;
import net.rs.lamsi.multiimager.utils.imageimportexport.DataExportUtil;
import net.rs.lamsi.multiimager.utils.imageimportexport.Image2DImportExportUtil;
import net.rs.lamsi.utils.DialogLoggerUtil;
import net.rs.lamsi.utils.FileAndPathUtil;
import net.rs.lamsi.utils.mywriterreader.BinaryWriterReader;
import net.rs.lamsi.utils.mywriterreader.TxtWriter;
import net.rs.lamsi.utils.useful.FileNameExtFilter;

import org.apache.commons.httpclient.methods.GetMethod;
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
	// Readers and Writers
	private BinaryWriterReader binaryWriter;
	private TxtWriter txtWriter;
	// Filechooser
	final private JFileChooser fcOpen = new JFileChooser();
	final private JFileChooser fcImport = new JFileChooser();
	final private JFileChooser fcSave = new JFileChooser();
	// last file for filechooser
	private File lastPath = null;
	private FileTypeFilter fileTFImage2D, fileTFtxt, fileTFtxtcsv, fileTFcsv, fileTFxls, fileTFxlsx;
	// List of Images
	private ModuleTree<Image2D> treeImg;
	// get parent IconNode by String keys (parent only)
	private HashMap<String, IconNode> mapNodes;
	// private Vector<Image2D> listImages = new Vector<Image2D>();
	private Image2D selectedImage = null;
	private Heatmap currentHeat = null;
	//

	//##################################################################################
	// LOGIC
	public ImageLogicRunner(ImageEditorWindow wnd) {
		this.window = wnd;
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
							if(Image2D.class.isInstance(img.getUserObject())) { 
								setSelectedImageAndShow((Image2D)img.getUserObject());
							}
						}
					}catch(Exception ex) { 
						ex.printStackTrace();
					} 
			} 
		});
		// init heatmapfactory
		heatFactory = new HeatmapFactory();
		// init filechooser 
		// add Filter 
		fileTFImage2D = new FileTypeFilter("image2d", "Image format from this application");
		fcOpen.addChoosableFileFilter(fileTFImage2D); 
		fcOpen.setMultiSelectionEnabled(true);

		String[] txtcsv = {"txt","csv"};
		fcImport.addChoosableFileFilter(fileTFtxtcsv = new FileTypeFilter(txtcsv, "Import text or csv file")); 
		fcImport.addChoosableFileFilter(fileTFtxt = new FileTypeFilter("txt", "Import text file")); 
		fcImport.addChoosableFileFilter(fileTFcsv = new FileTypeFilter("csv", "Import csv file")); 
		fcImport.addChoosableFileFilter(fileTFxlsx = new FileTypeFilter("xlsx", "Import Excel file")); 
		fcImport.addChoosableFileFilter(fileTFxls = new FileTypeFilter("xls", "Import Excel file")); 
		fcImport.setMultiSelectionEnabled(true);
		fcImport.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		fcSave.addChoosableFileFilter(fileTFImage2D); 
		fcSave.setMultiSelectionEnabled(false);
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
	public IconNode addImage(Image2D i, IconNode parent) {  
		//
		SettingsGeneralPreferences sett = SettingsHolder.getSettings().getSetGeneralPreferences();
		IconNode node = new IconNode(i, false, window.isCreatingImageIcons()? i.getIcon(sett.getIconWidth(), sett.getIconHeight()) : null);
		parent.add(node);
		return node;
	}
	public IconNode[] addCollection2D(Image2D[] img, IconNode parent, SettingsImageDataImportTxt sett) { 
		// only one image? do not create subnodes
		if(img==null || img.length==0) {
			return null;
		}
		else if(img.length==1) {
			return new IconNode[]{addImage(img[0], parent)};
		}
		else {
			SettingsGeneralPreferences pref = SettingsHolder.getSettings().getSetGeneralPreferences();
			// create subnode with image name
			DefaultMutableTreeNode sub = null;
			String last = null;
			IconNode[] nodes = new IconNode[img.length];
			int c = 0;
			// create img nodes
			for(Image2D i : img) {
				if(sub==null || (!last.equals(i.getSettImage().getRAWFolder()))) {
					sub = new IconNode(i.getSettImage().getRAWFolderName()+"; "+i.getSettImage().getRAWFolder());
					parent.add(sub);
					last = i.getSettImage().getRAWFolder(); 
				}
				IconNode inode = new IconNode(i, false, window.isCreatingImageIcons()? i.getIcon(pref.getIconWidth(), pref.getIconHeight()) : null);
				nodes[c] = inode;
				sub.add(inode);
				c++;
			}
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
	public void setSelectedImageAndShow(Image2D image2d) { 
		// 
		if(image2d == null) {
			ImageEditorWindow.log("REMOVE VIEWED IMAGE2D", LOG.DEBUG);
			selectedImage= image2d;
			// TODO set image2D -> null?
			// remove heatmap von central 
			window.getPnCenterImageView().removeAll(); 
		}
		else if(this.selectedImage!=image2d) {
			ImageEditorWindow.log("UPDATE IMAGE2D", LOG.DEBUG); 
			//
			selectedImage= image2d;
			// Renew all Modules
			window.setImage2D(image2d); 
			// create new heatmap
			renewImage2DView();
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
				// show heatmap in Center
				currentHeat = heatFactory.generateHeatmap(selectedImage);
				ChartPanel myChart = currentHeat.getChartPanel(); 
				myChart.setMouseWheelEnabled(true);  
				// remove all and add
				window.addHeatmapToPanel(currentHeat);
				return currentHeat;
			}catch(Exception ex) {
				ex.printStackTrace();
				// Dialog
				ImageEditorWindow.log("Cannot create image from "+selectedImage.getSettImage().toListName()+"; "+ex.getMessage(),LOG.ERROR);
				JOptionPane.showMessageDialog(window, "cannot create image from "+selectedImage.getSettImage().toListName()+"; "+ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE); 
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
	protected void applySettingsToAllImagesInList() {
		boolean state = DialogLoggerUtil.showDialogYesNo(window, "Change settings of all images in list?", "Attention: You are about to replace all settings of all images! Ok?");
		if(state && getSelectedImage()!=null) {
			// current settings as image 
			// get List of images
			for(Image2D img : getListImages()) { 
				// for each replace all settings	
				getSelectedImage().applySettingsToOtherImage(img);
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
		if (selectedImage !=null && fcSave.showSaveDialog(window) == JFileChooser.APPROVE_OPTION) {   
			File file = fcSave.getSelectedFile();
			file = fileTFImage2D.addExtensionToFileName(file);
			// save Image2D to file
			binaryWriter.save2file(selectedImage, file); 
			binaryWriter.closeOut();
		}
	}

	/**
	 * loads own format image2D
	 */
	public void loadImage2DFromFile() {
		if (fcOpen.showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {   
			final File[] files = fcOpen.getSelectedFiles(); 
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
								// image
								if(FileTypeFilter.getExtensionFromFile(f).equalsIgnoreCase(fileTFImage2D.getExtension())) {
									try {
										// load image from file with binary read
										Image2D img = (Image2D) binaryWriter.readFromFile(f);
										binaryWriter.closeIn();
										if(img!=null) addImage(img, f.getAbsolutePath());
									}catch(Exception ex) {
										ex.printStackTrace();
										// Dialog
										JOptionPane.showMessageDialog(window, "Cannot load image file "+f.getPath()+"; "+ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE); 
									}
								} 
								// Progress:
								addProgressStep(1);
							} 
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
	 * Import from data file txt, csv, xlsx
	 * or set up the direct imaging analysis
	 * @param settingsDataImport
	 */
	public void importDataToImage(SettingsImageDataImport settingsDataImport) { 
		// import TXT or csv
		if(settingsDataImport instanceof SettingsImageDataImportTxt) { 
			// txt chooser
			fcImport.setFileFilter(fileTFtxtcsv);
			// choose files
			if (fcImport.showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
				// many folders or files
				File[] files = fcImport.getSelectedFiles(); 
				
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
								Image2D[] imgs = Image2DImportExportUtil.importTextDataToImage(i, settingsDataImport, true); 
								ImageEditorWindow.log("Imported image "+i[0].getName(), LOG.DEBUG);
								if(imgs.length>0) {
									// add img to list
									addCollection2D(imgs, fnode, settingsDataImport);
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
					Image2D[] img = Image2DImportExportUtil.importTextDataToImage(files, settingsDataImport, true); 
					// add img to list
					IconNode parent = new IconNode(files[0].getParentFile().getName()+"; "+files[0].getParent());
					addCollection2D(img, parent, settingsDataImport);
					treeImg.addNodeToRoot(parent);
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
					ImageEditorWindow.log("Exporting data report to "+FileAndPathUtil.getRealFilePath(file.getParentFile(), file.getName(), "xlsx").getAbsolutePath(), LOG.MESSAGE);
					DataExportUtil.exportDataReportOnOperations(getSelectedImage(), file.getParentFile(), file.getName());
					ImageEditorWindow.log("Exporting finished", LOG.MESSAGE);
				} catch (Exception e) { 
					e.printStackTrace(); 
				}
			}
		}
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

	public Vector<Image2D> getListImages() {
		return treeImg.toList();
	}

	public Image2D getSelectedImage() {
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
	
	public ModuleTree<Image2D> getTree() {
		return treeImg;
	}

}
