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

	
	public ImageTitle(Collectable2D img, Font font) {
		super(img.getTitle(), font);
		this.img = img;
		 
		this.setBackgroundPaint(new Color(200, 200, 255, 100));
		this.setFrame(new BlockBorder(Color.white));
		this.setPosition(RectangleEdge.BOTTOM);
		ta = new XYTitleAnnotation(0.98, 0.02, this,RectangleAnchor.BOTTOM_RIGHT); 
		ta.setMaxWidth(0.48);
	} 
	
	@Override
	public Object draw(Graphics2D g2, Rectangle2D area, Object params) {
		setText(img.getTitle());
		return super.draw(g2, area, params);
	} 
	
	
	public XYTitleAnnotation getAnnotation() {
		return ta;
	}
	
}
