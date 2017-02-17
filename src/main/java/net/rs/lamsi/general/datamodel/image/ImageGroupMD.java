package net.rs.lamsi.general.datamodel.image;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.tree.DefaultMutableTreeNode;

import net.rs.lamsi.general.datamodel.image.data.multidimensional.DatasetMD;
import net.rs.lamsi.general.datamodel.image.interf.MDDataset;

public class ImageGroupMD {

	// dataset
	protected MDDataset data = null;
	protected Vector<Image2D> images;
	protected DefaultMutableTreeNode node = null;
	// background microscopic image
	protected Image bgImage = null;
	protected File pathBGImage = null;
	
	public ImageGroupMD() {
		images = new Vector<Image2D>();
	}
	public ImageGroupMD(Image2D img) {
		images = new Vector<Image2D>();
		add(img);
		// set background iamge
		img.getSettImage().setBGImagePath(pathBGImage);
		img.getSettImage().setUseBGImage(pathBGImage!=null);
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
		this.pathBGImage = pathBGImage;
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
		if(pathBGImage==null) {
			if(images!=null && images.size()>0)
				pathBGImage = images.get(0).getSettImage().getBGImagePath();
		}
		return pathBGImage;
	}

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
}
