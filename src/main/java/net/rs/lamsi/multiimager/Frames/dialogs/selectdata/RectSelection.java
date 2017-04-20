package net.rs.lamsi.multiimager.Frames.dialogs.selectdata;

import java.io.Serializable;

import net.rs.lamsi.multiimager.Frames.dialogs.selectdata.Image2DSelectDataAreaDialog.SelectionMode;

public class RectSelection implements Serializable { 
	private static final long serialVersionUID = 1L;
	private SelectionMode mode;
	private int x,y,xe,ye;
	
	public RectSelection(SelectionMode mode, int x, int y, int xe, int ye) {
		super();
		this.mode = mode;
		this.x = x;
		this.y = y;
		this.xe = xe;
		this.ye = ye;
		// assure that xe>=x
		assureX();
		assureY();
	}
	private void assureX() {
		if(xe<x) xe = swap(x,x=xe); 
	}
	private void assureY() { 
		if(ye<y) ye = swap(y,y=ye);
	}
	/**
	 * true if inside or on borders
	 * @param px
	 * @param py
	 * @return
	 */
	public boolean contains(int px, int py) {
		return (px<=getMaxX() && px>=getMinX() && py<=getMaxY() && py>=getMinY());
	}
	
	/**
	 * translate / shift rect by distance
	 * @param px
	 * @param py
	 */
	public void translate(int px, int py) {
		x+=px;
		y+=py;
		xe+=px;
		ye+=py;
	}
	/**
	 * grow or shrink(if negative) 
	 * @param px
	 * @param py
	 */
	public void grow(int px, int py) {
		xe+=px;
		ye+=py;
		assureX();
		assureY();
	}
	/**
	 * apply minima and maxima.
	 * if minima is 0 coordinate will be set to this if below
	 * same for maximum=100: x/y<=100
	 * @param x0
	 * @param y0
	 * @param x1
	 * @param y1
	 */
	public void applyMaxima(int x0, int y0, int x1, int y1) {
		// assure that xe>=x
		assureX();
		assureY();
		// test
		if(x<x0) x = x0;
		if(y<y0) y = y0;
		if(ye>y1) ye = y1;
		if(xe>x1) xe = x1;
	}
	
	public int swap(int i, int k) {
		return i;
	}
	
	public int getMinX() {
		return x;
	}
	public int getMaxX() {
		return xe;
	}
	public int getMinY() {
		return y;
	}
	public int getMaxY() {
		return ye;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
		assureX();
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
		assureY();
	} 
	public void setXMax(int x) {
		this.xe = x;
		assureX();
	} 
	public void setYMax(int y) {
		this.ye = y;
		assureY();
	}
	public int getWidth() {
		return xe-x+1;
	}
	public void setWidth(int w) {
		this.xe = x+w-1;
		assureX();
	}
	public int getHeight() {
		return ye-y+1;
	}
	public void setHeight(int h) {
		this.ye = y+h-1;
		assureY();
	}
	public void setMax(int x1, int y1) {
		if(x1<x) setX(x1);
		else setXMax(x1);
		if(y1<y) setY(y1);
		else setYMax(y1);
	}
	public void setBounds(int x, int y, int xe, int ye) { 
		this.x = x;
		this.y = y;
		this.xe = xe;
		this.ye = ye;
		// assure that xe>=x 
		assureX();
		assureY();
	} 
	public SelectionMode getMode() {
		return mode;
	} 
	public void setMode(SelectionMode mode) {
		this.mode = mode;
	}
}
