package net.rs.lamsi.general.datamodel.image;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.tree.DefaultMutableTreeNode;

import net.rs.lamsi.general.datamodel.image.interf.ImageDataset;
import net.rs.lamsi.general.datamodel.image.interf.MDDataset;
import net.rs.lamsi.massimager.Settings.image.SettingsImageGroup;
import net.rs.lamsi.massimager.Settings.image.visualisation.SettingsAlphaMap;
import net.rs.lamsi.multiimager.Frames.multiimageframe.MultiImgTableRow;

public class ImageGroupMD  implements Serializable {  
	// do not change the version!
	private static final long serialVersionUID = 1L;
	// settings
	protected SettingsImageGroup settings;
	
	// dataset
	protected MDDataset data = null;
	protected Vector<Image2D> images;
	// treenode in tree view
	protected DefaultMutableTreeNode node = null;
	// background microscopic image
	protected Image bgImage = null;
	
	public ImageGroupMD() {
		settings = new SettingsImageGroup();
		images = new Vector<Image2D>();
	}
	public ImageGroupMD(Image2D img) {
		settings = new SettingsImageGroup();
		images = new Vector<Image2D>();
		add(img);
		// set background iamge
		img.getSettImage().setBGImagePath(getBGImagePath());
		img.getSettImage().setUseBGImage(getBGImagePath()!=null);
	}
	public ImageGroupMD(Image2D[] img) {
		settings = new SettingsImageGroup();
		if(img!=null && img.length>0) {
			images = new Vector<Image2D>();
			for(Image2D i : img)
				add(i);
			// set background iamge
			img[0].getSettImage().setBGImagePath(getBGImagePath());
			img[0].getSettImage().setUseBGImage(getBGImagePath()!=null);
		}
		else images = new Vector<Image2D>();
	}

	//################################################
	// vector methods
	
	/**
	 * add an image to this group and sets a unique imagegroup parameter to this image object
	 * @param img
	 */
	public void add(Image2D img) {
		if(MDDataset.class.isInstance(img.getData())) {
			if(data==null) data = (MDDataset) img.getData();

			if(data.equals(img.getData())){
				images.addElement(img);
				img.setImageGroup(this); 
			}
		}
	}
	public boolean remove(Image2D img) {
		return remove(images.indexOf(img))!=null;
	}

	public Image2D remove(int index) {
		if(index>=0 && index<size()) {
			data.removeDimension(index);
			for(int i=index+1; i<size(); i++)
				images.get(i).shiftIndex(-1);
			return images.remove(index);
		}
		else return null;
	}
	
	

	public void setBackgroundImage(Image image, File pathBGImage) {
		this.bgImage = image;
		settings.setPathBGImage(pathBGImage);
		for(Image2D img : images) {
			img.getSettImage().setBGImagePath(pathBGImage);
			img.getSettImage().setUseBGImage(pathBGImage!=null);
		}
	}
	/**
	 * 
	 * @return bg image or null
	 */
	public Image getBGImage() {
		if(bgImage==null && getBGImagePath()!=null){
			try {
				bgImage = ImageIO.read(getBGImagePath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bgImage;
	}

	/**
	 * 
	 * @return path or null
	 */
	public File getBGImagePath() {
		if(settings.getPathBGImage()==null) {
			if(images!=null && images.size()>0)
				settings.setPathBGImage(images.get(0).getSettImage().getBGImagePath());
		}
		return settings.getPathBGImage();
	}


	// #######################################################################################
	// ALPHA MAP STUFF
	// alpha map is rotated and reflected ETC
	/**
	 * constructs/updates an alpha map by the alpha map settings
	 * @return alpha map 
	 */
	public Boolean[][] updateMap() throws Exception {
		//new?
		Image2D first = get(0);
		//
		SettingsAlphaMap sttA = getSettAlphaMap();
		Boolean[][] map = sttA.getMap();
		// create new
		if(map == null) {
			map = new Boolean[first.getMaxLineCount()][first.getMaxDP()];
		}
		// init as true
		for(int r = 0; r<map.length; r++)
			for(int d=0; d<map[r].length; d++)
				map[r][d] = true;

		// go through all rows and check if in range
		for(int i=0; i<sttA.getTableModel().getRowList().size(); i++) {
			MultiImgTableRow row = sttA.getTableModel().getRowList().get(i);
			// thorws exeption if not the same dimensions
			row.applyToMap(map);
		}
		
		sttA.setMap(map);
		return map;
	}
	/**
	 * map ony for export: binary map
	 * @return
	 */
	public Object[][] createBinaryMap() throws Exception {
		Image2D first = get(0);
		Integer[][] bmap = new Integer[first.getMaxLineCount()][first.getMaxDP()];

		// init as 0
		for(int r = 0; r<bmap.length; r++)
			for(int d=0; d<bmap[r].length; d++)
				bmap[r][d] = 0;

		// go through all rows and check if in range
		SettingsAlphaMap sttA = getSettAlphaMap();
		int counter = 0;
		for(int i=0; i<sttA.getTableModel().getRowList().size(); i++) {
			MultiImgTableRow row = sttA.getTableModel().getRowList().get(i);
			if(row.isUseRange()) {
				row.applyToBinaryMap(bmap,counter);
				counter++;
			}
		}

		return bmap;
	}
	
	
	// #######################################################################################
	// GETTERS AND SETTERS
	public int size() {
		return images.size();
	}
	
	public Vector<Image2D> getImages() {
		return images;
	}

	public DefaultMutableTreeNode getNode() {
		return node;
	}
	public void setNode(DefaultMutableTreeNode node) {
		this.node = node;
	}
	public MDDataset getData() {
		return data;
	}
	public Image2D get(int i) { 
		if(images!=null && i>=0 && i<images.size())
			return images.get(i);
		return null;
	}
	public SettingsImageGroup getSettings() {
		return settings;
	}
	public void setSettings(SettingsImageGroup settings) {
		this.settings = settings;
	}

	public SettingsAlphaMap getSettAlphaMap() {
		return settings.getSettAlphaMap();
	}
	public void setSettAlphaMap(SettingsAlphaMap settAlphaMap) {
		settings.setSettAlphaMap(settAlphaMap);
	}
	
}
