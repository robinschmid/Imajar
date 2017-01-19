package net.rs.lamsi.massimager.test;

import java.awt.EventQueue;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class TestNumberFormat {

	

	public static void main(String[] args) { 
		DecimalFormat numberFormat = new DecimalFormat("#,###.######;(#,###.######)");
		DecimalFormat exponentFormat = new DecimalFormat("0.##E00;(0.##E00)");
		DecimalFormat exponentFormat2 = new DecimalFormat("0.##E00;(0.##E00)"); 
		DecimalFormatSymbols newSymbols = new DecimalFormatSymbols();
	    newSymbols.setExponentSeparator(" E");
	    newSymbols.setMinusSign('\u2212');
	    exponentFormat2.setDecimalFormatSymbols(newSymbols);
		
	    double number = 1234567890;

		System.out.println(numberFormat.format(number));
		System.out.println(exponentFormat.format(number));
		System.out.println(exponentFormat2.format(number));
	}
}
