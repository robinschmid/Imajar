package net.rs.lamsi.general.datamodel.image;

import java.io.Serializable;

import javax.swing.Icon;

import net.rs.lamsi.general.datamodel.image.data.twodimensional.XYIData2D;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.general.settings.image.SettingsImageOverlay;
import net.rs.lamsi.general.settings.image.sub.SettingsZoom;
import net.rs.lamsi.general.settings.image.visualisation.SettingsPaintScale;
import net.rs.lamsi.general.settings.image.visualisation.SettingsThemes;

public class ImageOverlay  extends Collectable2D<SettingsImageOverlay> implements Serializable {	 
	// do not change the version!
	private static final long serialVersionUID = 1L;

	protected ImageGroupMD group;

	public ImageOverlay(ImageGroupMD group, SettingsImageOverlay settings) throws Exception {
		super(settings); 
		this.group = group;
		// set images to settings to copy current paintscales
		if(!settings.isInitialised())
			settings.init(group);
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
			//			
			//			applyCutFilterMin(2.5);
			//			applyCutFilterMax(0.2);
			//			PaintScale scale = PaintScaleGenerator.generateStepPaintScale(minZFiltered, maxZFiltered, getSettPaintScale()); 
			//
			//			// scale in x
			//			float sx = 1;
			//			int w = data.getMinDP();
			//			if(w>maxw) {
			//				sx = w/(float)maxw;
			//				w = maxw;
			//			}
			//
			//			float sy = 1;
			//			int lines = data.getLinesCount();
			//			int h = lines;
			//			if(h>maxh) { 
			//				sy = h/(float)maxh;
			//				h = maxh;
			//			}	
			//
			//			BufferedImage img = new BufferedImage(Math.min(maxw, w), maxh = Math.min(maxh, h),BufferedImage.TYPE_INT_ARGB);
			//			Graphics2D g = img.createGraphics();
			//
			//			for(int x=0; x<w; x++) {
			//				for(int y=0; y<h; y++) {
			//					// fill rects
			//					int dp = data.getLineLength((int)(y*sy));
			//					int ix=(int)(x*sx);
			//					int iy=(int)(y*sy);
			//					if(iy<lines && ix<dp) {
			//						Paint c = scale.getPaint(getI(false, iy, ix));
			//						g.setPaint(c);
			//						g.fillRect(x, maxh-y, 1, 1); 
			//					}
			//				}
			//			}
			//
			//			return  new ImageIcon(img); 
			return null;
		} catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * Given image img will be setup like this image
	 * @param img will get all settings from master image
	 */
	@Override
	public void applySettingsToOtherImage(Collectable2D img2) {
		if(img2.isImageOverlay()) {
			ImageOverlay img = (ImageOverlay) img2;

			try {
				// save name and path
				String name = img.getTitle();
				// copy all TODO
				img.setSettings(settings.copy());
				// set name and path
				img.getSettings().setTitle(name);
			} catch (Exception e) { 
				e.printStackTrace();
			}
		}
	}

	// ######################################################################################
	// sizing
	/**
	 * maximum width of this overlay
	 * @param raw
	 * @return
	 */
	public float getWidth(boolean raw) {
		float max = 0;
		for(int i=0; i<group.image2dCount(); i++) {
			Image2D img = (Image2D) group.get(i);
			if(max<img.getWidth(raw))
				max = img.getWidth(raw);
		}
		return max;
	}
	/**
	 * maximum height of this overlay
	 * @param raw
	 * @return
	 */
	public float getHeight(boolean raw) {
		float max = 0;
		for(int i=0; i<group.image2dCount(); i++) {
			Image2D img = (Image2D) group.get(i);
			if(max<img.getHeight(raw))
				max = img.getHeight(raw);
		}
		return max;
	}
	
	/**
	 * according to rotation of data
	 * @return
	 */
	public int getWidthAsMaxDP() {
		int max = 0;
		for(int i=0; i<group.image2dCount(); i++) {
			Image2D img = (Image2D) group.get(i);
			if(max<img.getWidthAsMaxDP())
				max = img.getWidthAsMaxDP();
		}
		return max;
	}
	/**
	 * according to rotation of data
	 * @return
	 */
	public int getHeightAsMaxDP() {
		int max = 0;
		for(int i=0; i<group.image2dCount(); i++) {
			Image2D img = (Image2D) group.get(i);
			if(max<img.getHeightAsMaxDP())
				max = img.getHeightAsMaxDP();
		}
		return max;
	}


	public Image2D[] getImages() {
		return group.getImagesOnly();
	} 
	
	public int size()  { 
		return group.image2dCount();
	}
	/**
	 * add image2d 
	 * @throws Exception 
	 */
	public void addImage(Image2D i) throws Exception { 
		settings.addImage(i);
	}
	/**
	 * add image2d 
	 * @throws Exception 
	 */
	public void removeImage(int i) throws Exception { 
		settings.removeImage(i);
	}
	
	@Override
	public void setSettings(Settings sett) {
		try {
			if(sett==null)
				return;
			// TODO --> set all settings in one: 
			if(SettingsPaintScale.class.isAssignableFrom(sett.getClass())) 
				settings.setCurrentSettPaintScale((SettingsPaintScale) sett);
			else if(SettingsImageOverlay.class.isAssignableFrom(sett.getClass())) {
				this.settings = (SettingsImageOverlay)sett;
			}
			else {
				super.setSettings(sett);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	} 

	@Override
	public Settings getSettingsByClass(Class classsettings) {
		// TODO -- add other settings here
		if(SettingsPaintScale.class.isAssignableFrom(classsettings)) 
			return settings.getCurrentSettPaintScale();
		else {
			return super.getSettingsByClass(classsettings);
		}
	}
	public SettingsThemes getSettTheme() {
		return settings.getSettTheme();
	}
	@Override
	public SettingsZoom getSettZoom() {
		return settings.getSettZoom();
	}
	/**
	 * returns all active datasets
	 * @return
	 */
	public XYIData2D[] getDataSets() {
		XYIData2D[] dat = new XYIData2D[countActive()];
		int c = 0;
		for(int i=0; i<group.image2dCount(); i++) {
			if(settings.isActive(i)) {
				dat[c] = ((Image2D)group.get(i)).toXYIArray(false, true); 
				c++;
			}
		}

		return dat;
	}
	
	public int countActive() {
		int c = 0;
		for(int i=0; i<group.image2dCount(); i++) {
			if(settings.isActive(i)) {
				c++;
			}
		}
		return c;
	}
	
	@Override
	public String getTitle() { 
		return settings.getTitle();
	}

	public String getShortTitle() { 
		return getTitle();
	} 
	/**
	 * titles of all images
	 * (unique)
	 * @return
	 */
	public String[] getTitles() {

		String[] dat = new String[countActive()];
		int c = 0;
		for(int i=0; i<group.image2dCount(); i++) {
			if(settings.isActive(i)) {
				dat[c] = group.get(i).getTitle();
				String tt = dat[c];
				int tmp = 2;
				// check if title exists
				for(int x=0; x<c; x++) {
					// exists?
					if(dat[x].equals(dat[c])) {
						// add number
						dat[c] = tt + tmp;
						// check again
						x = -1;
						tmp++;
					}
				}
				// next title
				c++;
			}
		}
		return dat;
	}
	public ImageGroupMD getGroup() {
		return group;
	}
	public void setGroup(ImageGroupMD group) {
		this.group = group;
	}
	public Image2D get(int i) {
		return i<group.image2dCount()? (Image2D)group.get(i) : null;
	}
}
