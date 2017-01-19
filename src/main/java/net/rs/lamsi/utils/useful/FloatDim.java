package net.rs.lamsi.utils.useful;

import java.awt.geom.Dimension2D;

public class FloatDim {
	public float width, height;
	
	public FloatDim(float width, float height) {
		setSize(width, height);
	}
	
	public float getWidth() { 
		return width;
	}
 
	public float getHeight() { 
		return height;
	}
 
	public void setSize(float width, float height) {
		this.width = width;
		this.height = height;
	}

}
