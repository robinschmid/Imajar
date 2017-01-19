package net.rs.lamsi.multiimager.FrameModules.paintscale;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.Icon;

import org.jfree.chart.renderer.PaintScale;

public class PaintscaleIcon implements Icon {
	
	private PaintScale scale;
	private boolean horizontal;
	private int w,h;

	/**
	 * Create the panel.
	 */
	public PaintscaleIcon(PaintScale scale, int w, int h, boolean horizontal) {
		this.scale = scale;
		this.w = w;
		this.h = h; 
		this.horizontal = horizontal;
	} 

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) { 
		if(scale!=null) {
			double dist = scale.getUpperBound()-scale.getLowerBound();
			if(horizontal) {
				for(int i=0; i<w; i++) {
					double perc =  i/(w-1.0);
					
					g.setColor((Color)scale.getPaint(scale.getLowerBound()+dist*perc));
					g.fillRect(x+i, y, 1, h);
				}
			}
			else {
				for(int i=0; i<h; i++) {
					double perc =  i/(h-1.0);
					
					g.setColor((Color)scale.getPaint(scale.getLowerBound()+dist*perc));
					g.fillRect(x, y+i, w, 1);
				}
			}
			g.setColor(Color.BLACK);
			g.drawRect(x-1, y-1, w+1, h+1);
		}
	}

	@Override
	public int getIconWidth() { 
		return w;
	}

	@Override
	public int getIconHeight() { 
		return h;
	}

}
