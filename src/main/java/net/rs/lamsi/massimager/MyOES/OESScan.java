package net.rs.lamsi.massimager.MyOES;

import java.util.Vector;

import net.rs.lamsi.massimager.MyMZ.MZChromatogram;

public class OESScan {
	
	protected Vector<Double> time = new Vector<Double> ();
	protected Vector<Double> center = new Vector<Double> ();
	protected String name,date,info;
	
	public void setKopfzeile(String kopfzeile){//static nur wenn man in  main klasse ist
		String [] tmp = kopfzeile.split(",");
		try {
			name=tmp[0];
			date=tmp[1];
			info=tmp[2];
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setData(double time2, double center2) {
		
		time.add(time2);
		
		//abziehen von von vorigem center-wert 
		double summe=0;
		for (int i=0; i<center.size(); i++){
			summe=summe+center.get(i);
		}
		double res = center2-summe;
		center.add(res);  
	}
	

	// returns a MZChrom for this scan with I against t
	public MZChromatogram getMZChrom() {
		MZChromatogram chrom = new MZChromatogram("OES");
		for(int i=0; i<center.size(); i++) {
			chrom.add(time.get(i), center.get(i));
		} 
		return chrom;
	}

	public Vector<Double> getTime() {
		return time;
	}

	public void setTime(Vector<Double> time) {
		this.time = time;
	}

	public Vector<Double> getCenter() {
		return center;
	}

	public void setCenter(Vector<Double> center) {
		this.center = center;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	
	

}
