package net.rs.lamsi.general.settings.image.operations.quantifier;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.rs.lamsi.general.datamodel.image.Collectable2DPlaceHolderLink;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.framework.modules.ModuleTree;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.utils.FileAndPathUtil;

public class SettingsImage2DQuantifierIS extends SettingsImage2DQuantifier {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    
    // image for IS
    protected Image2D imgIS;
    // after import there is only a link
    protected transient Collectable2DPlaceHolderLink link = null;
    
    // double
    protected double concentrationFactor = 1;
	
	
	public SettingsImage2DQuantifierIS() {
		super(MODE.IS, "SettingsImage2DQuantifierIS", "/Settings/operations/IS/", "setISDiv"); 
	} 
	public SettingsImage2DQuantifierIS(Image2D imgIS) {
		super(MODE.IS, "SettingsImage2DQuantifierIS", "/Settings/operations/IS/", "setISDiv"); 
		this.imgIS = imgIS;
	} 
	@Override
	public void resetAll() {
		super.resetAll(); 
		imgIS = null;
	}
	
	/**
	 * 
	 */
	@Override
	public double calcIntensity(Image2D img,  int line, int dp, double intensity) {
		if(!isActive() || !isApplicable())
			return intensity;
		else {
			double is = imgIS.getI(false, line, dp);
			if(Double.isNaN(is))
				return is;
			if(is==0)
				return 0;
			else 
				return intensity/is*concentrationFactor;
		} 
	}
	/**
	 * force blank
	 * @param img
	 * @param line
	 * @param dp
	 * @param intensity
	 * @param blank
	 * @return
	 */
	public double calcIntensity(Image2D img,  int line, int dp, double intensity, boolean blank) {
		if(isApplicable() && line<imgIS.getLineCount(dp)  && dp<imgIS.getLineLength(line)) {
			if(blank) {
				SettingsImage2DBlankSubtraction b = imgIS.getSettings().getOperations().getBlankQuantifier();
				boolean tmp = b.isActive();
				b.setActive(blank);
				double is = imgIS.getI(false, line, dp);
				if(is==0)
					return 0;
				//
				intensity = intensity/is*concentrationFactor;
				b.setActive(tmp);
				return intensity;
			} 
		}
		return intensity; 
	}

	// TODO same data dimensions?
	public boolean isApplicable() {
		return (imgIS!=null && imgIS.getData()!=null);
	}
	
	public Image2D getImgIS() {
		// try to replace?
		if(imgIS==null && link!=null) {
			try {
				ModuleTree<Collectable2D> tree = ImageEditorWindow.getEditor().getLogicRunner().getTree();
				imgIS = (Image2D) tree.getCollectable2DFromPlaceHolder(link);
				if(imgIS!=null)
					link = null;
			} catch (Exception e) {
			}
		}
		return imgIS;
	}
	public void setImgIS(Image2D imgIS) {
		this.imgIS = imgIS;
	}
	public double getConcentrationFactor() {
		return concentrationFactor;
	}
	public void setConcentrationFactor(double concentrationFactor) {
		this.concentrationFactor = concentrationFactor;
	}
	

	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
		super.appendSettingsValuesToXML(elParent, doc);
		toXML(elParent, doc, "factor", concentrationFactor);
		if(imgIS!=null)
			toXML(elParent, doc, "externalSTDImage", imgIS);
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
				else if(paramName.equals("factor")) 
					concentrationFactor = doubleFromXML(nextElement);
			}
		}
	} 
	
	@Override
	public void replacePlaceHoldersInSettings(ModuleTree<Collectable2D> tree) {
		super.replacePlaceHoldersInSettings(tree);
		if(link!=null) {
			try {
				imgIS = (Image2D) tree.getCollectable2DFromPlaceHolder(link);
				if(imgIS!=null)
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
		sett.setImgEx(imgIS);
		sett.setActive(isActive);
		return sett;
	}
}
