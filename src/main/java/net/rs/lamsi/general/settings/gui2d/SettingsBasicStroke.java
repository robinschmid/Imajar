package net.rs.lamsi.general.settings.gui2d;

import java.awt.BasicStroke;
import java.awt.Graphics2D;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.rs.lamsi.general.heatmap.Heatmap;
import net.rs.lamsi.general.myfreechart.Plot.image2d.annot.ScaleInPlot;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.general.settings.image.selection.SettingsShapeSelection.ROI;

public class SettingsBasicStroke extends Settings {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private float w, miterlimit, dashphase; 
	private int cap, join; 
	private float[] array;
	
	private transient BasicStroke stroke;


	public SettingsBasicStroke() {
		super("SettingsBasicStroke", "/Settings/GUI/", "setStroke"); 
		resetAll();
	} 
	
	public SettingsBasicStroke(float w, int cap, int join, float miterlimit, float[] array, float dashphase) {
		this();
		setAll(w, cap, join, miterlimit, array, dashphase);
		
	}

	public SettingsBasicStroke(float w, int cap, int join, float miter) {
		this();
		setAll(w,cap,join,miter,new float[]{10.f},0);
	}

	public SettingsBasicStroke(float w) {
		this();
		setAll(w,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,2,new float[]{10.f},0);
	}

	public SettingsBasicStroke(BasicStroke s) {
		this();
		setAll(s.getLineWidth(), s.getEndCap(), s.getLineJoin(), s.getMiterLimit(), s.getDashArray(), s.getDashPhase());
		stroke = s;
	}
	

	public void setAll(float w, int cap, int join, float miterlimit, float[] array, float dashphase) {
		setLineWidth(w);
		setMiterlimit(miterlimit);
		setDashArray(array);
		setDashphase(dashphase);
		setCap(cap);
		setJoin(join);
	}

	public void setLineWidth(float w) {
		if(changed(this.w, w)) {
			this.w = w;
			stroke = null;
		}
	}

	public void setMiterlimit(float miterlimit) {
		if(changed(this.miterlimit, miterlimit)) {
			this.miterlimit = miterlimit;
			stroke = null;
		}
	}

	public void setDashphase(float dashphase) {
		if(changed(this.dashphase, dashphase)) {
			this.dashphase = dashphase;
			stroke = null;
		}
	}

	public void setCap(int cap) {
		if(changed(this.cap, cap)) {
			this.cap = cap;
			stroke = null;
		}
	}

	public void setJoin(int join) {
		if(changed(this.join, join)) {
			this.join = join;
			stroke = null;
		}
	}

	public void setDashArray(float[] array) {
		if(changed(this.array, array)) {
			this.array = array;
			stroke = null;
		}
	}

	@Override
	public void resetAll() {
		this.w = 1.5f;
		this.miterlimit = 2.f;
		this.dashphase = 0;
		this.cap = BasicStroke.CAP_ROUND;
		this.join = BasicStroke.JOIN_MITER;
		this.array = new float[]{10.f};
		stroke = null;
	}
	

	//#########################################################################
	// xml import export
	@Override
	public void appendSettingsValuesToXML(Element el, Document doc) {
		toXML(el, doc, "w", w); 
 		toXML(el, doc, "miterlimit",miterlimit); 
		toXML(el, doc, "dashphase", dashphase); 
		toXML(el, doc, "cap", cap); 
		toXML(el, doc, "join", join); 
		toXMLArray(el, doc, "array", array); 
	}

	@Override
	public void loadValuesFromXML(Element el, Document doc) {
		boolean hasNoBG = false;
		NodeList list = el.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element nextElement = (Element) list.item(i);
				String paramName = nextElement.getNodeName();
				if(paramName.equals("w")) w = floatFromXML(nextElement);  
				else if(paramName.equals("miterlimit"))miterlimit = floatFromXML(nextElement);  
				else if(paramName.equals("dashphase"))dashphase = floatFromXML(nextElement);  
				else if(paramName.equals("cap"))cap = intFromXML(nextElement);  
				else if(paramName.equals("join"))join = intFromXML(nextElement);  
				else if(paramName.equals("array"))array = floatArrayFromXML(nextElement);  
			}
		}
	}
	
	public BasicStroke getStroke() {
		if(stroke==null) {
			stroke = new BasicStroke(w, cap, join, miterlimit, array, dashphase);
		}
		return stroke;
	}

	public float[] getDashArray() {
		return array;
	}

	public float getDashPhase() {
		// TODO Auto-generated method stub
		return dashphase;
	}

    /**
     * Returns the line width.  Line width is represented in user space,
     * which is the default-coordinate space used by Java 2D.  See the
     * <code>Graphics2D</code> class comments for more information on
     * the user space coordinate system.
     * @return the line width of this <code>BasicStroke</code>.
     * @see Graphics2D
     */
    public float getLineWidth() {
        return w;
    }

    /**
     * Returns the end cap style.
     * @return the end cap style of this <code>BasicStroke</code> as one
     * of the static <code>int</code> values that define possible end cap
     * styles.
     */
    public int getEndCap() {
        return cap;
    }

    /**
     * Returns the line join style.
     * @return the line join style of the <code>BasicStroke</code> as one
     * of the static <code>int</code> values that define possible line
     * join styles.
     */
    public int getLineJoin() {
        return join;
    }

    /**
     * Returns the limit of miter joins.
     * @return the limit of miter joins of the <code>BasicStroke</code>.
     */
    public float getMiterLimit() {
        return miterlimit;
    }

}
