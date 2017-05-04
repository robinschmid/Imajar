package net.rs.lamsi.massimager.MyFreeChart.Plot.image2d.annot;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;

import org.jfree.chart.annotations.XYTitleAnnotation;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.title.TextTitle;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;

public class ImageTitle extends TextTitle {
	
	private static final long serialVersionUID = 1L;
	// keep track of title changes
	private Collectable2D img;
	private XYTitleAnnotation ta;

	
	public ImageTitle(Collectable2D img, Font font, Color color, Color bg, boolean visible, float x, float y) {
		super(img.getTitle(), font);
		setPaint(color);
		setBackgroundPaint(bg);

		this.img = img;
		 
		this.setFrame(new BlockBorder(Color.white));
		this.setPosition(RectangleEdge.BOTTOM);
		ta = new XYTitleAnnotation(x, y, this,RectangleAnchor.BOTTOM_RIGHT); 
		ta.setMaxWidth(0.48);
		setVisible(visible);
	} 
	
	@Override
	public Object draw(Graphics2D g2, Rectangle2D area, Object params) {
		if(isVisible()) {
			setText(img.getShortTitle());
			return super.draw(g2, area, params);
		}
		else return null;
	} 
	
	
	public XYTitleAnnotation getAnnotation() {
		return ta;
	}
	
	public XYTitleAnnotation setPosition(float x, float y) {
		ta = new XYTitleAnnotation(x, y, this,RectangleAnchor.BOTTOM_RIGHT); 
		ta.setMaxWidth(0.48);
		return ta;
	}
}
