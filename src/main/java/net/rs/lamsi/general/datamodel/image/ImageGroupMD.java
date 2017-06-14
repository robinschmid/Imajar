package net.rs.lamsi.general.datamodel.image;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.tree.DefaultMutableTreeNode;

import net.rs.lamsi.general.datamodel.image.data.interf.MDDataset;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.general.settings.SettingsContainerSettings;
import net.rs.lamsi.general.settings.image.SettingsImageGroup;
import net.rs.lamsi.general.settings.image.visualisation.SettingsAlphaMap;
import net.rs.lamsi.multiimager.Frames.multiimageframe.MultiImgTableRow;

public class ImageGroupMD implements Serializable {	 
	// do not change the version!
	private static final long serialVersionUID = 1L;
	// settings
	protected SettingsImageGroup settings;

	// project
	protected ImagingProject project = null;
	
	// dataset
	protected MDDataset data = null;
	protected ArrayList<Collectable2D> images;
	// treenode in tree view
	protected DefaultMutableTreeNode node = null;
	// background microscopic image
	protected Image bgImage = null;


	public ImageGroupMD() {
		settings = new SettingsImageGroup();
		images = new ArrayList<Collectable2D>();
	}
	public ImageGroupMD(Collectable2D img) {
		settings = new SettingsImageGroup();
		images = new ArrayList<Collectable2D>();
		add(img);
		if(Image2D.class.isInstance(img)) {
			Image2D i = ((Image2D)img);
			settings.setName(i.getSettings().getSettImage().getRAWFolder()!=null? i.getSettings().getSettImage().getRAWFolderName():
				i.getSettings().getSettImage().getRAWFileName()); 
		}
		else settings.setName(img.getTitle());
	}
	public ImageGroupMD(Collectable2D[] img) {
		settings = new SettingsImageGroup();
		if(img!=null && img.length>0) {
			images = new ArrayList<Collectable2D>();
			for(Collectable2D i : img)
				add(i);
		}
		else images = new ArrayList<Collectable2D>();
	}

	//################################################
	// vector methods

	/**
	 * add an image to this group and sets a unique imagegroup parameter to this image object
	 * @param img
	 */
	public void add(Collectable2D c2d) {
		if(Image2D.class.isInstance(c2d)){
			Image2D img = (Image2D)c2d;
			if(MDDataset.class.isInstance(img.getData())) {
				if(data==null) data = (MDDataset) img.getData();
				if(data.equals(img.getData())){
					images.add(image2dCount(),img);
					img.setImageGroup(this);
					// add to all overlays
					for(int i=image2dCount(); i<size(); i++)
						if(ImageOverlay.class.isInstance(get(i)))
							try {
								((ImageOverlay)get(i)).addImage(img);
							} catch (Exception e) {
								e.printStackTrace();
							}
				}
			}
		}
		else {
			images.add(c2d);
			c2d.setImageGroup(this); 
		}
	}
	public boolean remove(Collectable2D img) {
		return remove(images.indexOf(img))!=null;
	}

	public Collectable2D remove(int index) {
		if(index>=0 && index<size()) {
			if(data.removeDimension(index)) {
				for(int i=index+1; i<size(); i++)
					if(Image2D.class.isInstance(images.get(i))) {
						((Image2D)images.get(i)).shiftIndex(-1);
					}

				// remove from all overlays
				for(int f=image2dCount(); f<size(); f++)
					if(ImageOverlay.class.isInstance(get(f)))
						try {
							((ImageOverlay)get(f)).removeImage(index);
						} catch (Exception e) {
							e.printStackTrace();
						}
			} 
			return images.remove(index);
		}
		else return null;
	}



	public void setBackgroundImage(Image image, File pathBGImage) {
		this.bgImage = image;
		settings.getSettBGImg().setPathBGImage(pathBGImage);
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
		return settings.getSettBGImg().getPathBGImage();
	}

	// ######################################################################################
	// sizing
	/**
	 * according to rotation of data
	 * @return
	 */
	public int getWidthAsMaxDP() {
		return getFirstImage2D().getWidthAsMaxDP();
	}
	/**
	 * according to rotation of data
	 * @return
	 */
	public int getHeightAsMaxDP() {
		return getFirstImage2D().getHeightAsMaxDP();
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
		Image2D first = getFirstImage2D();
		//
		SettingsAlphaMap sttA = getSettAlphaMap();
		Boolean[][] map = sttA.getMap();
		// create new
		if(map == null) {
			map = new Boolean[first.getMaxLinesCount()][first.getMaxLineLength()];
		}
		// init as true
		for(int r = 0; r<map.length; r++) {
			for(int d=0; d<map[r].length; d++) {
				if(first.isDP(r,d))
					map[r][d] = true;
				else map[r][d] = null;
			}
		}

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
		Image2D first = getFirstImage2D();
		Integer[][] bmap = new Integer[first.getMaxLinesCount()][first.getMaxLineLength()];

		// init as 0
		for(int r = 0; r<bmap.length; r++)
			for(int d=0; d<bmap[r].length; d++)
				if(first.isDP(r,d))
					bmap[r][d] = 0;
				else bmap[r][d] = null;

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

	public Image2D getFirstImage2D() {
		for(Collectable2D c2d : images)
			if(Image2D.class.isInstance(c2d))
				return (Image2D) c2d;
		return null;
	}

	public Image2D getLastImage2D() {
		for(int i=images.size()-1; i>=0; i--)
			if(Image2D.class.isInstance(images.get(i)))
				return (Image2D) images.get(i);
		return null;
	}


	// #######################################################################################
	// GETTERS AND SETTERS
	/**
	 * overlays and images
	 * @return
	 */
	public int size() {
		return images.size();
	}
	/**
	 * iamges only
	 * @return
	 */
	public int image2dCount() {
		return Math.min(getData().size(), images.size());
	}

	public ArrayList<Collectable2D> getImages() {
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
	public void setData(MDDataset data) {
		this.data = data;
		for(Collectable2D c : images)
			if(Image2D.class.isInstance(c))
				((Image2D)c).setData(data);
	}
	public Collectable2D get(int i) { 
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
	/**
	 * get settings by class
	 * @param classsettings
	 * @return
	 */
	public Settings getSettingsByClass(Class classsettings) {
		if(classsettings.isInstance(this.settings))
			return this.settings;
		else {
			return ((SettingsContainerSettings)this.settings).getSettingsByClass(classsettings);
		}
	}

	public SettingsAlphaMap getSettAlphaMap() {
		return settings.getSettAlphaMap();
	}
	public void setSettAlphaMap(SettingsAlphaMap settAlphaMap) {
		settings.replaceSettings(settAlphaMap);
	}
	/**
	 * a vector of all iamges
	 * @return
	 */
	public Image2D[] getImagesOnly() {
		Image2D[] img = new Image2D[image2dCount()];
		for(int i=0; i<image2dCount(); i++)
			if(Image2D.class.isInstance(images.get(i)))
				img[i] = ((Image2D) images.get(i));
		return img;
	}
	/**
	 * 
	 * @return a list of non image2d entries (like overlays)
	 */
	public Collectable2D[] getOtherThanImagesOnly() {
		int size = images.size()-image2dCount();
		Collectable2D[] img = new Collectable2D[size];
		for(int i=0; i<size; i++)
			img[i] = (images.get(i+image2dCount()));
		return img;
	}
	public ImagingProject getProject() {
		return project;
	}
	public void setProject(ImagingProject project) {
		this.project = project;
	}
	public String getName() {
		return settings.getName();
	}
	
	@Override
	public String toString() {
		return getName()+"; "+settings.getPathData();
	}
	/**
	 * 
	 * @param index
	 * @param class1
	 * @return the object[index] of an sub array of class1
	 */
	public Collectable2D get(int index, Class class1) {
		int c = 0;
		for(int i=0; i<size(); i++)
			if(class1.isInstance(images.get(i))) {
				if(c==index)
					return images.get(i);
				c++;
			}
		return null;
	}
}
