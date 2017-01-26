package net.rs.lamsi.general.datamodel.image;

import java.io.File;
import java.io.Serializable;
import java.util.Vector;

import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;

public class Collection2D extends Vector<Image2D> implements Serializable { 
	private static final long serialVersionUID = 1L;
	
	private File path;

	public Collection2D(File path) {
		super();
		this.path = path;
	}
	
	@Override
	public synchronized String toString() { 
		return path.getAbsolutePath();
	} 
	public File getPath() {
		return path;
	} 
	public void setPath(File path) {
		this.path = path;
	} 
}
