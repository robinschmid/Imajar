package net.rs.lamsi.multiimager.FrameModules.sub.quantifiertable;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.massimager.Settings.image.operations.quantifier.Quantifier;
import net.rs.lamsi.massimager.Settings.image.operations.quantifier.SettingsImage2DQuantifier;

public class QuantifierTableRow {
	public static final String MODE_AVERAGE = "Average", MODE_SELECTION = "Selection";
	
	private Quantifier quanti;
	// isAverage = false --> isAverageBoxes = true
	private String mode = MODE_SELECTION; 
	
	public QuantifierTableRow(Image2D img, int c) {
		super();
		this.quanti = new Quantifier(); 
		quanti.setImg(img);
		quanti.setConcentration(c);
		quanti.setMode(Quantifier.MODE_AVERAGE_BOXES);
	} 
	public QuantifierTableRow(Quantifier q) {
		super();
		this.quanti = q;
	} 

	public Object[] toArray() {
		return getArray();
	}
	
	public Object[] getArray() {
		Object[] array = {quanti.getConcentration(), quanti.getImg().getTitle(), quanti.getImg().getSettings().getSettImage().getRAWFilepath(), quanti.getImg().getParent()==null? "-" : quanti.getImg().getParent().getTitle(), mode, "Select"};
		return array;
	}

	public Image2D getImg() {
		return quanti.getImg();
	}

	public void setImg(Image2D img) {
		this.quanti.setImg(img);
	}
	
	
	

	public Quantifier getQuanti() {
		return quanti;
	}

	public void setQuanti(Quantifier quanti) {
		this.quanti = quanti;
	}

	public double getC() {
		return quanti.getConcentration();
	}

	public void setC(double c) {
		quanti.setConcentration(c);
	}

	public void setMode(String mode) {
		this.mode = mode;
		quanti.setMode(mode.equalsIgnoreCase(MODE_SELECTION)? Quantifier.MODE_AVERAGE_BOXES : Quantifier.MODE_AVERAGE);
	}
	public boolean isAverage() {
		return mode.equalsIgnoreCase(MODE_AVERAGE);
	} 
	public void setAverage(boolean isAverage) {
		this.mode = isAverage? MODE_AVERAGE : MODE_SELECTION;
	}
}
