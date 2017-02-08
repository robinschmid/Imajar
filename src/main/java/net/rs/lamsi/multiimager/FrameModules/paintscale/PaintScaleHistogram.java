package net.rs.lamsi.multiimager.FrameModules.paintscale;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.massimager.Settings.image.visualisation.SettingsPaintScale;

public class PaintScaleHistogram extends JPanel {

	private Image2D img;
	private SettingsPaintScale ps;
	/**
	 * Create the panel.
	 */
	public PaintScaleHistogram() {
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());

		// TODO
	}
	
	public void setPaintScale(SettingsPaintScale ps) {
		this.ps = ps;
	}
	public Image2D getImg() {
		return img;
	}
	public void setImg(Image2D img) {
		this.img = img;
	}
}
