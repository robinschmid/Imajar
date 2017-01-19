package net.rs.lamsi.massimager.MyOES;

import java.util.Vector;

public class OESElementLine {

	String name;
	Vector<OESScan> listScan= new Vector<OESScan>();
	
	public OESElementLine(String name) {
		super();
		this.name = name; 
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Vector<OESScan> getListScan() {
		return listScan;
	}
	public void setListScan(Vector<OESScan> listScan) {
		this.listScan = listScan;
	}
	public String getNameForList() { 
		return name+" ("+listScan.size()+" scans)";
	}
	
	
}
