package net.rs.lamsi.general.settings.image.operations.quantifier;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.rs.lamsi.general.datamodel.image.Collectable2DPlaceHolderLink;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.framework.modules.ModuleTree;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.general.settings.image.selection.SettingsSelections;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageLogicRunner;
import net.rs.lamsi.utils.FileAndPathUtil;

public class SettingsImage2DQuantifierOnePoint extends SettingsImage2DQuantifier {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    
    // image for one point
    protected transient Image2D imgEx;  
    protected transient Collectable2DPlaceHolderLink link = null;
    // regression version to track changes
    protected int regressionVersionID = 0;
	
	public SettingsImage2DQuantifierOnePoint(Image2D ex) {
		super(MODE.ONE_POINT); 
		this.imgEx = ex;
	} 
	@Override
	public void resetAll() {
		super.resetAll(); 
		imgEx = null;
		regressionVersionID = 0;
	}

	@Override
	public Class getSuperClass() {
		return SettingsImage2DQuantifier.class; 
	}
	
	@Override
	public double calcIntensity(Image2D img, int line, int dp, double intensity) {
		SimpleRegression r = getRegression();
		if(r==null)
			return intensity;
		else {
			int nid = imgEx.getSettings().getSettSelections().getRegressionVersionID();
			if(regressionVersionID==nid)
				return (intensity - r.getIntercept())/r.getSlope();
			else {
				// regression has changed
				regressionVersionID = nid;
				img.fireIntensityProcessingChanged();
				return calcIntensity(img, line, dp, intensity);
			}
		}
	}
	
	public boolean isApplicable() {
		return getRegression() != null;
	}
	
	private SimpleRegression getRegression() {
		if(imgEx==null)
			return null;
		SettingsSelections s = imgEx.getSettings().getSettSelections();
		return s.getRegression();
	}
	
	public Image2D getImgEx() {
		// try to replace?
		if(imgEx==null && link!=null) {
			try {
				ModuleTree<Collectable2D> tree = ImageEditorWindow.getEditor().getLogicRunner().getTree();
				imgEx = (Image2D) tree.getCollectable2DFromPlaceHolder(link);
				if(imgEx!=null)
					link = null;
			} catch (Exception e) {
			}
		}
		return imgEx;
	}
	/**
	 * 
	 * @param ex
	 * @return true if external img has changed
	 */
	public boolean setImgEx(Image2D ex) {
		boolean state = ex==null || !ex.equals(this.imgEx);
		this.imgEx = ex;
		return state;
	}
	
	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
		super.appendSettingsValuesToXML(elParent, doc);
		if(imgEx!=null)
			toXML(elParent, doc, "externalSTDImage", imgEx);
	}
	@Override
	public void loadValuesFromXML(Element el, Document doc) {
		super.loadValuesFromXML(el, doc);
		NodeList list = el.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element nextElement = (Element) list.item(i);
				String paramName = nextElement.getNodeName();
				if(paramName.equals("externalSTDImage")) 
					link = c2dFromXML(nextElement); 
			}
		}
	} 
	
	@Override
	public void replacePlaceHoldersInSettings(ModuleTree<Collectable2D> tree) {
		super.replacePlaceHoldersInSettings(tree);
		if(link!=null) {
			try {
				imgEx = (Image2D) tree.getCollectable2DFromPlaceHolder(link);
				if(imgEx!=null)
					link = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public Settings copy() throws Exception {
		// do not copy image2d
		SettingsImage2DQuantifierOnePoint sett = (SettingsImage2DQuantifierOnePoint)super.copy();
		sett.setImgEx(imgEx);
		sett.setActive(isActive);
		return sett;
	}
	
}
