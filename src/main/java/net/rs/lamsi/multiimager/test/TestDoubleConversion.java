package net.rs.lamsi.multiimager.test;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Formatter;

public class TestDoubleConversion {

	public static void main(String[] args) {
		System.out.println("Starting");
		float value = 2.0005f;
		long time1 = System.currentTimeMillis();
		for(int i=0; i<1000000000; i++) {
			double d =  Math.round(value*1E6)/1E6;
		}
		long time2 = System.currentTimeMillis(); 
		System.out.println(((time2-time1)/1000.0)+"  for Math.round(value)  "+ Math.round(value*1E6)/1E6);
		
		time1 = System.currentTimeMillis();
		for(int i=0; i<1000000000; i++) {
			double d =  Math.round(value*1E6)/1E6;
		}
		time2 = System.currentTimeMillis();
		System.out.println(((time2-time1)/1000.0)+"  for Math.round(value)  "+ Math.round(value*1E6)/1E6);
		
		time1 = System.currentTimeMillis();
		for(int i=0; i<1000000000; i++) {
			double d =  Math.round(value*1E6)/1E6;
		}
		time2 = System.currentTimeMillis();
		System.out.println(((time2-time1)/1000.0)+"  for Math.round(value)  "+ Math.round(value*1E6)/1E6);
		
		time1 = System.currentTimeMillis();
		for(int i=0; i<1000000000; i++) {
			double d =  Math.round(value*1E6)/1E6;
		}
		time2 = System.currentTimeMillis();
		System.out.println(((time2-time1)/1000.0)+"  for Math.round(value)  "+ Math.round(value*1E6)/1E6);
		
		
		
		DecimalFormat df = new DecimalFormat("0.######"); 

		time1 = System.currentTimeMillis();
		for(int i=0; i<1000000; i++) {
			double d =  Double.valueOf(df.format(value));
		}
		time2 = System.currentTimeMillis();
		System.out.println(((time2-time1)/1000.0)+"  for Double.valueOf(df.format(value))  "+ Double.valueOf(df.format(value)));

		value = 123456.789f;
		time1 = System.currentTimeMillis();
		for(int i=0; i<1000000; i++) {
			double d =  Double.valueOf(df.format(value));
		}
		time2 = System.currentTimeMillis();
		System.out.println(((time2-time1)/1000.0)+"  for Double.valueOf(df.format(value))  "+ Double.valueOf(df.format(value)));

		time1 = System.currentTimeMillis();
		for(int i=0; i<1000000; i++) {
			double d =  Double.valueOf(df.format(value));
		}
		time2 = System.currentTimeMillis();
		System.out.println(((time2-time1)/1000.0)+"  for Double.valueOf(df.format(value))  "+ Double.valueOf(df.format(value)));
		

		
		
		


		time1 = System.currentTimeMillis();
		for(int i=0; i<1000000; i++) {
			double d = Double.valueOf(String.valueOf(value));
		}
		time2 = System.currentTimeMillis();
		System.out.println(((time2-time1)/1000.0)+"  for  Double.valueOf(String.valueOf(value))  "+ Double.valueOf(String.valueOf(value)));

		time1 = System.currentTimeMillis();
		for(int i=0; i<1000000; i++) {
			double d = Double.valueOf(String.valueOf(value+1.0035f));
		}
		time2 = System.currentTimeMillis();
		System.out.println(((time2-time1)/1000.0)+"  for  Double.valueOf(String.valueOf(value+1.0035f))  "+ Double.valueOf(String.valueOf(value+1.0035f)));
		

		BigDecimal val2 = new BigDecimal("2.0005123");
		time1 = System.currentTimeMillis();
		for(int i=0; i<1000000; i++) {
			double d = val2.doubleValue();
		}
		time2 = System.currentTimeMillis();
		System.out.println(((time2-time1)/1000.0)+"  for  val2.doubleValue() BigDecimal  "+ val2.doubleValue());
		
 
		
	}
}
