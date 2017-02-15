package net.rs.lamsi.general.datamodel.image;

import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import net.rs.lamsi.general.datamodel.image.interf.MDDataset;

public class ImageGroupMD {

	// dataset
	protected MDDataset data = null;
	protected Vector<Image2D> images;
	protected DefaultMutableTreeNode node = null;
	public ImageGroupMD() {
		images = new Vector<Image2D>();
	}
	public ImageGroupMD(Image2D img) {
		images = new Vector<Image2D>();
		add(img);
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
}
