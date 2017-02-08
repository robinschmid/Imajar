package net.rs.lamsi.massimager.Settings.image.operations.quantifier;

import java.util.Vector;








import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.utils.DialogLoggerUtil;
import net.rs.lamsi.utils.useful.dialogs.DialogLinearRegression;

public class SettingsImage2DQuantifierMultiPoints extends SettingsImage2DQuantifier {
	// do not change the version!
	private static final long serialVersionUID = 1L;

	// check for i processing changed
	private long lastIProcChangeTime = 0;
	// 
	private SimpleRegression regression = null;
	private Vector<Quantifier> quantifier = null;
	private double factor=1;


	public SettingsImage2DQuantifierMultiPoints() {
		super(MODE_MULTIPLE_POINTS); 
	} 
	@Override
	public void resetAll() {
		super.resetAll(); 
	}


	@Override
	public double calcIntensity(Image2D img,  int line, int dp, double intensity) {
		if(regression==null)
			updateRegression();
		if(!isApplicable())
			return 0;
		// check for changes
		if(lastIProcChangeTime!=img.getLastIProcChangeTime()) {
			lastIProcChangeTime = img.getLastIProcChangeTime();
			updateRegression();
		}

		// quantify by linear regression TODO
		return (intensity-regression.getIntercept())/regression.getSlope()*factor;
	}


	public boolean isApplicable() {
		return !(quantifier==null || quantifier.size()==0);
	}

	public void updateRegression() {
		// add all points TODO
		regression = new SimpleRegression(true);
		for(Quantifier q : quantifier) {
			q.fireIntensityProcessingChanged();
			regression.addData(q.getConcentration(), q.getAverageIntensity());
		}
	}

	public Vector<Quantifier> getQuantifier() {
		return quantifier;
	}
	public void setQuantifier(Vector<Quantifier> quantifier) {
		this.quantifier = quantifier;
		if(quantifier==null)
			regression = null;
		else {
			updateRegression();
		}
	}
	public void setFactor(double c) {
		factor = c;
	}
	public double getFactor() {
		return factor;
	}
	public SimpleRegression getRegression() {
		return regression;
	}
	public double[][] getRegressionData() {
		double[][] data = new double[quantifier.size()][2];
		for(int i=0; i<data.length; i++) {
			Quantifier q = quantifier.get(i);
			data[i][0] = q.getConcentration(); 
			data[i][1] = q.getAverageIntensity();
		}
		return data;
	}

	public void openRegressionDialog() {
		DialogLinearRegression dialog = new DialogLinearRegression(getRegression(), getRegressionData());
		DialogLoggerUtil.centerOnScreen(dialog, true);
		dialog.setVisible(true);
	}
	
	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
	}
	@Override
	public void loadValuesFromXML(Element el, Document doc) {
	} 
	
}
