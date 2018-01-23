package net.rs.lamsi.general.myfreechart.plots.image2d.annot;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import net.rs.lamsi.general.settings.image.visualisation.SettingsBackgroundImg;

import org.jfree.chart.annotations.XYImageAnnotation;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;

public class BGImageAnnotation extends XYImageAnnotation {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private SettingsBackgroundImg settBG;
	private float widthImg2d, heightImg2d;
	

	public BGImageAnnotation(Image image, SettingsBackgroundImg settBG, float widthImg2d, float heightImg2D) {
		super(0, 0, image, RectangleAnchor.BOTTOM_LEFT);
		this.heightImg2d = heightImg2D;
		this.widthImg2d = widthImg2d;
		this.settBG = settBG;
	}

	@Override
	public void draw(Graphics2D g2, XYPlot plot,
			Rectangle2D dataArea, ValueAxis domainAxis,
			ValueAxis rangeAxis, int rendererIndex,
			PlotRenderingInfo info) {
		if(settBG.isVisible()) {
			// given offset in micro meters (x and y scale)
			Point2D offset = settBG.getOffset();
	
	        PlotOrientation orientation = plot.getOrientation();
	        AxisLocation domainAxisLocation = plot.getDomainAxisLocation();
	        AxisLocation rangeAxisLocation = plot.getRangeAxisLocation();
	        RectangleEdge domainEdge
	            = Plot.resolveDomainAxisLocation(domainAxisLocation, orientation);
	        RectangleEdge rangeEdge
	            = Plot.resolveRangeAxisLocation(rangeAxisLocation, orientation);
	        float j2DX
	            = (float) domainAxis.valueToJava2D(this.getX()+offset.getX(), dataArea, domainEdge);
	        float j2DY
	            = (float) rangeAxis.valueToJava2D(this.getY()+offset.getY(), dataArea, rangeEdge);
	        float xx = 0.0f;
	        float yy = 0.0f;
	        if (orientation == PlotOrientation.HORIZONTAL) {
	            xx = j2DY;
	            yy = j2DX;
	        }
	        else if (orientation == PlotOrientation.VERTICAL) {
	            xx = j2DX;
	            yy = j2DY;
	        }
	        // real image width and height
	        double w = this.getImage().getWidth(null);
	        double h = this.getImage().getHeight(null);
	        
	        // resized by width statement in imagegroup
	        // realw is given in micro meters
	        double realw = settBG.getBgWidth();
	        if(realw==0) {
	        	// no width defined - scale to dataArea
	        	w = dataArea.getWidth();
	        	h = dataArea.getHeight();
	        }
	        else {
	        	// calculate size in respect to micro meter width of data image2d
	        	double f = realw/widthImg2d;
	        	w = dataArea.getWidth()*f;
	        	h = dataArea.getHeight()*f;
	        }
	
	        // zoom factor
	        double fw = domainAxis.getRange().getLength()/widthImg2d;
	        double fh = rangeAxis.getRange().getLength()/heightImg2d;
	        
	        w = (int) (w/fw);
	        h = (int) (h/fh);
	        
	        Rectangle2D imageRect = new Rectangle2D.Double(0, 0, w, h);
	        Point2D anchorPoint = RectangleAnchor.coordinates(imageRect, getImageAnchor());
	        xx = xx - (float) anchorPoint.getX();
	        yy = yy - (float) anchorPoint.getY();
	
	        // way faster
	        double angle = settBG.getAngleRad();
	        g2.rotate(angle, xx+w/2, yy+h/2);
	        g2.drawImage(this.getImage(), (int) xx, (int) yy, (int)w, (int)h, null);
	        g2.rotate(-angle, xx+w/2, yy+h/2);
	        // too slow!
	//        g2.drawImage(this.getImage().getScaledInstance((int)w, (int)h, Image.SCALE_SMOOTH), (int) xx, (int) yy, null);
	        
	        String toolTip = getToolTipText();
	        String url = getURL();
	        if (toolTip != null || url != null) {
	            addEntity(info, new Rectangle2D.Float(xx, yy, (float)w, (float)h), rendererIndex,
	                    toolTip, url);
	        }
		}
	}
}
