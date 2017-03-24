package net.rs.lamsi.general.datamodel.image;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import net.rs.lamsi.general.datamodel.image.data.twodimensional.XYIData2D;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.massimager.Heatmap.PaintScaleGenerator;
import net.rs.lamsi.massimager.Settings.image.SettingsImageOverlay;
import net.rs.lamsi.massimager.Settings.image.visualisation.SettingsThemes;

import org.jfree.chart.renderer.PaintScale;

public class ImageOverlay  extends Collectable2D implements Serializable {	 
	// do not change the version!
	private static final long serialVersionUID = 1L;
	
	// images for the overlay
	protected Vector<Image2D> images;
	protected Vector<Boolean> active;
	
	protected SettingsImageOverlay settings;
	
	
	public ImageOverlay(Vector<Image2D> images, SettingsImageOverlay settings) {
		super();
		this.images = images;
		this.settings = settings;
		active = new Vector<Boolean>(images.size());
		for(int i=0; i<images.size(); i++)
			active.add(new Boolean(true));
	}
	/**
	 * returns an easy icon
	 * @param maxw
	 * @param maxh
	 * @return
	 */
	@Override
	public Icon getIcon(int maxw, int maxh) {
		try { //TODO
			applyCutFilterMin(2.5);
			applyCutFilterMax(0.2);
			PaintScale scale = PaintScaleGenerator.generateStepPaintScale(minZFiltered, maxZFiltered, getSettPaintScale()); 

			// scale in x
			float sx = 1;
			int w = data.getMinDP();
			if(w>maxw) {
				sx = w/(float)maxw;
				w = maxw;
			}

			float sy = 1;
			int lines = data.getLinesCount();
			int h = lines;
			if(h>maxh) { 
				sy = h/(float)maxh;
				h = maxh;
			}	

			BufferedImage img = new BufferedImage(Math.min(maxw, w), maxh = Math.min(maxh, h),BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = img.createGraphics();

			for(int x=0; x<w; x++) {
				for(int y=0; y<h; y++) {
					// fill rects
					int dp = data.getLineLength((int)(y*sy));
					int ix=(int)(x*sx);
					int iy=(int)(y*sy);
					if(iy<lines && ix<dp) {
						Paint c = scale.getPaint(getI(false, iy, ix));
						g.setPaint(c);
						g.fillRect(x, maxh-y, 1, 1); 
					}
				}
			}

			return  new ImageIcon(img); 
		} catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	
	




	public Vector<Boolean> getActive() {
		return active;
	}
	public void setActive(Vector<Boolean> active) {
		this.active = active;
	}
	public Vector<Image2D> getImages() {
		return images;
	}
	public void setImages(Vector<Image2D> images) {
		this.images = images;
	}
	public SettingsImageOverlay getSettings() {
		return settings;
	}
	public void setSettings(SettingsImageOverlay settings) {
		this.settings = settings;
	}
	public SettingsThemes getSettTheme() {
		return settings.getSettTheme();
	}
	/**
	 * returns all active datasets
	 * @return
	 */
	public XYIData2D[] getDataSets() {
		int c = 0;
		for(int i=0; i<images.size(); i++) {
			if(active.get(i)) {
				c++;
			}
		}
		
		XYIData2D[] dat = new XYIData2D[c];
		c = 0;
		for(int i=0; i<images.size(); i++) {
			if(active.get(i)) {
				dat[c] = images.get(i).toXYIArray(false, true); 
			}
		}
		
		return dat;
	}
}
