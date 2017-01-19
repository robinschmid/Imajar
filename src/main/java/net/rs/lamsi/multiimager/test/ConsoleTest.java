package net.rs.lamsi.multiimager.test;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import net.rs.lamsi.utils.mywriterreader.TxtWriter;

import org.mozilla.universalchardet.UniversalDetector;
 

public class ConsoleTest {
 
	public static void main(String[] args) {
		// TODO Auto-generated method stub UTF8thermo.csv
		
		System.out.println("TEST FILE:::");
		TxtWriter writer = new TxtWriter();
		//Vector<String> s = writer.readLines("data/UTF8.txt");
		//Vector<String> s = writer.readLinesUTF8(new File("data/UTF8thermo.csv")); 


		File file = new File("data/UTF8.txt");
		File file3 = new File("data/ANSI2.txt");
		File filelarge = new File("data/UTF8thermo.csv");
		File filelarge2 = new File("data/ANSIthermo.csv");
		File file4 = new File("data/UNICODE.txt");
		File file5 = new File("data/UNICODEBIG.txt");
		
 
		try {  
			long timemilli = System.currentTimeMillis();
			
			for(int i=0; i<10000; i++) {
				writer.readLinesBuffered(file);
				writer.readLinesBuffered(file3);
			}
 
			long timemilli2 = System.currentTimeMillis();
 
			System.out.println((timemilli2-timemilli)/1000.0);
			
 
			timemilli = System.currentTimeMillis();
			
			for(int i=0; i<10000; i++) {
				writer.readLines(file);
				writer.readLines(file3);
			}
 
			timemilli2 = System.currentTimeMillis();
 
			System.out.println((timemilli2-timemilli)/1000.0);
			

			// test
			timemilli = System.currentTimeMillis(); 
			for(int i=0; i<10000; i++) {
				writer.readLinesBuffered(file);
				writer.readLinesBuffered(file3);
			} 
			timemilli2 = System.currentTimeMillis(); 
			System.out.println((timemilli2-timemilli)/1000.0);

			// test
			timemilli = System.currentTimeMillis(); 
			for(int i=0; i<10000; i++) {
				writer.readLines(file);
				writer.readLines(file3);
			} 
			timemilli2 = System.currentTimeMillis(); 
			System.out.println((timemilli2-timemilli)/1000.0);
			

			// TEST LARGE FILES
			// test
			timemilli = System.currentTimeMillis(); 
			for(int i=0; i<20; i++) {
				writer.readLinesBuffered(filelarge);
				writer.readLinesBuffered(filelarge2);
			} 
			timemilli2 = System.currentTimeMillis(); 
			System.out.println((timemilli2-timemilli)/1000.0);

			// test
			timemilli = System.currentTimeMillis(); 
			for(int i=0; i<20; i++) {
				writer.readLines(filelarge);
				writer.readLines(filelarge2);
			} 
			timemilli2 = System.currentTimeMillis(); 
			System.out.println((timemilli2-timemilli)/1000.0);
			

			//Vector<String> s = writer.readLinesBuffered(file);
			//for(String w : s)
			//	System.out.println(w);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	} 

}
