package net.rs.lamsi.multiimager.directimaging;

import java.util.Vector;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.ImageGroupMD;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.DatasetMD;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.ScanLineMD;
import net.rs.lamsi.general.framework.modules.tree.IconNode;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow.LOG;
import net.rs.lamsi.utils.useful.FileDim;

public class DIATask {

	private int newFileIndex = 0;
	private FileDim[] lastFiles; 
	// save all lines in here. old lines have to be deleted and new lines are to be added
	private DatasetMD data;
	
	private ImageGroupMD img;
	private IconNode[] nodes;
	
	private int index = 0;
	

	public DIATask(ImageGroupMD img, FileDim[] dim, IconNode[] nodes, int index) {
		this.img = img;
		this.lastFiles = dim;
		this.index = index;
		//
		this.nodes = nodes;
		// add lines to this data set
		data = (DatasetMD) img.getData();
	}


	/**
	 * raw file path of images
	 * @return
	 */
	public String getIdentifier() { 
		if(img!=null && img.size()>0)
			return ((Image2D)img.getImages().get(0)).getSettings().getSettImage().getRAWFilepath();
		else return "";
	}

	/**
	 * compares new and old files to reset the new files index
	 * @param dim
	 * @return
	 */
	public int compareNewAndOldFiles(FileDim[] dim) { 
		if(lastFiles!=null) {
			// new version to find new files
			for(int nf=newFileIndex; nf<dim.length; nf++) {
				for(int lf=newFileIndex; lf<lastFiles.length; lf++) {
					if(dim[nf].compareTo(lastFiles[lf])) {
						newFileIndex++;
						ImageEditorWindow.log("####NEW FILE####", LOG.MESSAGE);
					}
				}
			}
		}
		lastFiles = dim;
		return newFileIndex;
	}
	

	/**
	 * 
	 * @param newLines
	 */
	public void addNewLines(Vector<ScanLineMD> newLines, boolean autoScale, double scaleFactor) throws Exception {
		// TODO commented out this is the actual version
		// TODO only commented out because of error
		// same size?
//		if(newLines!=null && img.length==newLines.firstElement().getImageCount()) {
//			// for all images / scanline vectors
//			for(int i=0; i<img.length; i++) {
//				// delete new lines that are in lines vector (>newFileIndex)
//				for(int n=newFileIndex; n<lines[i].size(); n++) 
//					lines[i].remove(newFileIndex);
//				
//				// add new lines
//				lines[i].addAll(newLines[i]);
//				
//				// keep old image2d objects but change the lines vectors
//				img[i].setLines(lines[i].toArray(new ScanLine2D[lines[i].size()]));
//				
//				// change max of paintscale to fit last full line
//				if(autoScale) {
//					int l = lines[i].size()-2;
//					if(l<0) l=0;
//					double min = img[i].getMinIntensity(false);
//					double max = img[i].getMaxIntensity(l, false);
//					
//					if(min<max) {
//						max = max + (max-min)*scaleFactor;
//						// max higher than max of image?
//						double maxall = img[i].getMaxIntensity(false);
//						if(max>maxall) max = maxall;
//						// set wider range
//						img[i].getSettPaintScale().setUsesMinMax(true);
//						img[i].getSettPaintScale().setUsesMaxValues(true);
//						img[i].getSettPaintScale().setMax(max);
//					}
//				}
//				
//				// change icon for node 
//				nodes[i].setIcon(img[i].getIcon(60)); 
//			} 
//		}
//		else {
//			// error - reset and load images new
//			ImageEditorWindow.log("ERROR while loading new lines", LOG.ERROR);
//			// reset task for new check
//			reset();
//			throw(new Exception("Data is not readable"));
//		}
	}

	//##########################################################
	// getters and setters
	public int getNewFileIndex() {
		return newFileIndex;
	} 
	public void setNewFileIndex(int newFileIndex) {
		this.newFileIndex = newFileIndex;
	} 
	public ImageGroupMD getImg() {
		return img;
	} 
	public void setImg(ImageGroupMD img) {
		this.img = img;
	}

	/**
	 * reset on error to check all lines again
	 */
	public void reset() {
		lastFiles = null;
		newFileIndex = 0;
	}


	public int getIndex() {
		return index;
	}


	public void setIndex(int index) {
		this.index = index;
	}

}
