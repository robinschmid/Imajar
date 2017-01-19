package net.rs.lamsi.multiimager.utils.imageimportexport;

public class TextAnalyzer {

	
	public static boolean isNumberValues(String s[]) { 
		for(String i : s) {
			if(!isNumberValue(i)) return false;
		}
		return true;
	}
	public static boolean isNumberValue(String s) {
		try {
			double d = Double.valueOf(s); 
			return true;
		} catch(Exception ex) {
			return false;
		}
	}

}
