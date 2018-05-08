package net.rs.lamsi.multiimager.test;

import java.util.Random;

public class TestQuantifier {
	public static Random rand;

//	public static void main(String[] args) { 
//		// start MultiImager application
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					ImageEditorWindow window = new ImageEditorWindow();
//					window.setVisible(true);
//					//
//					rand = new Random(System.currentTimeMillis());
//					// load data ThermoMP17 Image
//					String s = "sample", ss = "sample2", cal = "cal", cal2 = "cal2";
//					// create sample and sample IS
//					Image2D[]  sample = createImage(1, 5, 500);
//					window.getLogicRunner().addImage(sample[0], s);
//					window.getLogicRunner().addImage(sample[1], s);
//					window.getLogicRunner().addImage(sample[2], s);
//					
//
//					// create sample and sample IS   with blank in front
//					Image2D[]  sample2 = createImageWithBlank(1, 5, 500);
//					window.getLogicRunner().addImage(sample2[0], ss);
//					window.getLogicRunner().addImage(sample2[1], ss);
//					window.getLogicRunner().addImage(sample2[2], ss);
//
//					// create kali and kali IS
//					Image2D[]  kali = createStandards(1, 5, 500);
//					window.getLogicRunner().addImage(kali[0], cal);
//					window.getLogicRunner().addImage(kali[1], cal);
//					window.getLogicRunner().addImage(kali[2], cal);
//					
//
//					// create kali and kali IS
//					kali = createStandardsWithBlank(1, 5, 500);
//					window.getLogicRunner().addImage(kali[0],cal2);
//					window.getLogicRunner().addImage(kali[1],cal2);
//					window.getLogicRunner().addImage(kali[2],cal2);
//					
//					// create kali and kali IS
//					kali = createStandardsOne(10, 5, 500);
//					window.getLogicRunner().addImage(kali[0],"c3");
//					window.getLogicRunner().addImage(kali[1],"c3");
//					window.getLogicRunner().addImage(kali[2],"c3");
//				} catch(Exception ex) {
//					logger.error("",ex);
//				}
//			}
//		}); 
//	}
//
//	
//
//	/**
//	 * create images for 
//	 * 0=blank: rising from line to line
//	 * 1=IS: bg+noise+constant signal
//	 * 2=sample: bg+(signal+noise)*factor
//	 * 
//	 * @return
//	 */
//	public static Image2D[] createImage(double factorSample, double factorBG, double signalIS) {
//		
//		ScanLine2D[] lines = new ScanLine2D[100];
//		ScanLine2D[] linesIS = new ScanLine2D[100];
//		ScanLine2D[] linesBlank = new ScanLine2D[100];
//		
//		for(int l=0; l<lines.length; l++) {
//			DataPoint2D[] dp = new DataPoint2D[500];
//			DataPoint2D[] dpIS = new DataPoint2D[500];
//			DataPoint2D[] dpBlank = new DataPoint2D[500];
//			
//			for(int d=0; d<dp.length; d++) {
//				// bg is rising from line 0 to line n 1-100
//				double bg = l*factorBG;
//				// signal 250 + 250 = 500
//				double signal = (50.0-Math.abs(l+1-lines.length/2))*5.0+ 250.0-Math.abs(d+1-dp.length/2); 
//				// noise 60
//				double noise = Math.abs(rand.nextInt(120000)/1000.0);
//				// create dpBG
//				dpBlank[d] = new DataPoint2D(d*0.1, bg);
//				dpIS[d] = new DataPoint2D(d*0.1, bg+ (signalIS+noise));
//				dp[d] = new DataPoint2D(d*0.1, bg+ signal+(noise)*factorSample);
//			}
//			lines[l] = new ScanLine2D(dp);
//			linesIS[l] = new ScanLine2D(dpIS);
//			linesBlank[l] = new ScanLine2D(dpBlank);
//		}
//		// 
//		Image2D img[] = new Image2D[3];
//		img[2] = new Image2D(new Dataset2D(lines));
//		img[2].getSettImage().setTitle("sample");
//		img[1] = new Image2D(new Dataset2D(linesIS));
//		img[1].getSettImage().setTitle("sample IS");
//		img[0] = new Image2D(new Dataset2D(linesBlank));
//		img[0].getSettImage().setTitle("sample blank");
//		return img;
//	}
//
//	/**
//	 * create images for 
//	 * 0=blank: rising from line to line
//	 * 1=IS: bg+noise+constant signal
//	 * 2=sample: bg+(signal+noise)*factor
//	 * 
//	 * @return
//	 */
//	public static Image2D[] createImageWithBlank(double factorSample, double factorBG, double signalIS) {
//		
//		ScanLine2D[] lines = new ScanLine2D[100];
//		ScanLine2D[] linesIS = new ScanLine2D[100];
//		ScanLine2D[] linesBlank = new ScanLine2D[100];
//		
//		for(int l=0; l<lines.length; l++) {
//			DataPoint2D[] dp = new DataPoint2D[700];
//			DataPoint2D[] dpIS = new DataPoint2D[700];
//			DataPoint2D[] dpBlank = new DataPoint2D[700];
//			// start and end data
//			for(int d=0; d<100; d++) {
//				double bg = 100+l*factorBG;
//				// create dpBG
//				dpBlank[d] = new DataPoint2D(d*0.1, bg);
//				dpIS[d] = new DataPoint2D(d*0.1, bg);
//				dp[d] = new DataPoint2D(d*0.1, bg);
//				dpBlank[dp.length-1-d] = new DataPoint2D((dp.length-1-d)*0.1, bg);
//				dpIS[dp.length-1-d] = new DataPoint2D((dp.length-1-d)*0.1, bg);
//				dp[dp.length-1-d] = new DataPoint2D((dp.length-1-d)*0.1, bg);
//			}
//			// middle data
//			for(int d=0; d<dp.length-200; d++) {
//				// bg is rising from line 0 to line n 1-100
//				double bg = 100+l*factorBG;
//				// signal 250 + 250 = 500
//				double signal = (50.0-Math.abs(l+1-lines.length/2))*5.0+ 250.0-Math.abs(d+1-500/2); 
//				// noise 60
//				double noise = Math.abs(rand.nextInt(120000)/1000.0);
//				// create dpBG
//				dpBlank[d+100] = new DataPoint2D((d+100)*0.1, bg);
//				dpIS[d+100] = new DataPoint2D((d+100)*0.1, bg+ (signalIS+noise));
//				dp[d+100] = new DataPoint2D((d+100)*0.1, bg+ signal+(noise)*factorSample);
//			}
//			lines[l] = new ScanLine2D(dp);
//			linesIS[l] = new ScanLine2D(dpIS);
//			linesBlank[l] = new ScanLine2D(dpBlank);
//		}
//		// 
//		Image2D img[] = new Image2D[3];
//		img[2] = new Image2D(new Dataset2D(lines));
//		img[2].getSettImage().setTitle("sample");
//		img[1] = new Image2D(new Dataset2D(linesIS));
//		img[1].getSettImage().setTitle("sample IS");
//		img[0] = new Image2D(new Dataset2D(linesBlank));
//		img[0].getSettImage().setTitle("sample blank");
//		return img;
//	}
//	/**
//	 * creating standards
//	 * 
//	 * 0=blank: rising from line to line
//	 * 1=IS: bg+noise+constant signal
//	 * 2=sample: bg+(signal+noise)*factor
//	 * 0 - 800
//	 */
//	public static Image2D[] createStandards(double factorSample, double factorBG, double signalIS) {
//		ScanLine2D[] lines = new ScanLine2D[24];
//		ScanLine2D[] linesIS = new ScanLine2D[24];
//		ScanLine2D[] linesBlank = new ScanLine2D[24];
//		
//		for(int l=0; l<lines.length; l++) {
//			DataPoint2D[] dp = new DataPoint2D[500];
//			DataPoint2D[] dpIS = new DataPoint2D[500];
//			DataPoint2D[] dpBlank = new DataPoint2D[500];
//			
//			for(int d=0; d<dp.length; d++) {
//				// middle the highest
//				double signal = l==0? 0 : (int)(l/4)*200.0;
//				//  
//				// bg is rising from line 0 to line n 1-100
//				double bg = 100+l*factorBG;
//				// noise 120
//				double noise = Math.abs(rand.nextInt(120000)/1000.0);
//				// create dp
//				dpBlank[d] = new DataPoint2D(d*0.1, bg);
//				dpIS[d] = new DataPoint2D(d*0.1, bg+ (signalIS+noise));
//				dp[d] = new DataPoint2D(d*0.1, bg+ signal+(noise)*factorSample);
//			}
//			lines[l] = new ScanLine2D(dp);
//			linesIS[l] = new ScanLine2D(dpIS);
//			linesBlank[l] = new ScanLine2D(dpBlank);
//		}
//		// 
//		Image2D img[] = new Image2D[3];
//		img[2].getSettImage().setTitle("kali");
//		img[1].getSettImage().setTitle("kali IS");
//		img[0].getSettImage().setTitle("kali blank");
//		img[2] = new Image2D(new Dataset2D(lines));
//		img[1] = new Image2D(new Dataset2D(linesIS));
//		img[0] = new Image2D(new Dataset2D(linesBlank));
//		return img;
//	}
//	
//	/**
//	 * creating standards
//	 * 
//	 * 0=blank: rising from line to line
//	 * 1=IS: bg+noise+constant signal
//	 * 2=sample: bg+(signal+noise)*factor
//	 * 0 - 800
//	 */
//	public static Image2D[] createStandardsWithBlank(double factorSample, double factorBG, double signalIS) {
//		ScanLine2D[] lines = new ScanLine2D[24];
//		ScanLine2D[] linesIS = new ScanLine2D[24];
//		ScanLine2D[] linesBlank = new ScanLine2D[24];
//		
//		for(int l=0; l<lines.length; l++) {
//			DataPoint2D[] dp = new DataPoint2D[700];
//			DataPoint2D[] dpIS = new DataPoint2D[700];
//			DataPoint2D[] dpBlank = new DataPoint2D[700];
//			
//			for(int d=0; d<dp.length; d++) {
//				// middle the highest
//				double signal = l==0? 0 : (int)(l/4)*200.0;
//				//  
//				// bg is rising from line 0 to line n 1-100
//				double bg = 100+l*factorBG;
//				// noise 120
//				double noise = Math.abs(rand.nextInt(120000)/1000.0);
//				// create dp
//				dpBlank[d] = new DataPoint2D(d*0.1, bg);
//				dpIS[d] = new DataPoint2D(d*0.1, d<100 || d>=600? bg+noise : bg+ (signalIS+noise));
//				dp[d] = new DataPoint2D(d*0.1,  d<100 || d>=600? bg+noise*factorSample : bg+ signal+(noise)*factorSample);
//			}
//			lines[l] = new ScanLine2D(dp);
//			linesIS[l] = new ScanLine2D(dpIS);
//			linesBlank[l] = new ScanLine2D(dpBlank);
//		}
//		// 
//		Image2D img[] = new Image2D[3];
//		img[2].getSettImage().setTitle("kali");
//		img[1].getSettImage().setTitle("kali IS");
//		img[0].getSettImage().setTitle("kali blank");
//		img[2] = new Image2D(new Dataset2D(lines));
//		img[1] = new Image2D(new Dataset2D(linesIS));
//		img[0] = new Image2D(new Dataset2D(linesBlank));
//		return img;
//	}
//	
//	/**
//	 * creating standards
//	 * 
//	 * 0=blank: rising from line to line
//	 * 1=IS: bg+noise+constant signal
//	 * 2=sample: bg+(signal+noise)*factor
//	 * 0 - 800
//	 */
//	public static Image2D[] createStandardsOne(double factorSample, double factorBG, double signalIS) {
//		ScanLine2D[] lines = new ScanLine2D[6];
//		ScanLine2D[] linesIS = new ScanLine2D[6];
//		ScanLine2D[] linesBlank = new ScanLine2D[6];
//		
//		for(int l=0; l<lines.length; l++) {
//			DataPoint2D[] dp = new DataPoint2D[500];
//			DataPoint2D[] dpIS = new DataPoint2D[500];
//			DataPoint2D[] dpBlank = new DataPoint2D[500];
//			
//			for(int d=0; d<dp.length; d++) {
//				// middle the highest
//				double signal = signalIS*factorSample;
//				//  
//				// bg is rising from line 0 to line n 1-100
//				double bg = 100+l*factorBG;
//				// noise 120
//				double noise = Math.abs(rand.nextInt(120000)/1000.0);
//				// create dp
//				dpBlank[d] = new DataPoint2D(d*0.1, bg);
//				dpIS[d] = new DataPoint2D(d*0.1, bg+ (signalIS+noise));
//				dp[d] = new DataPoint2D(d*0.1, bg+ signal+(noise)*factorSample);
//			}
//			lines[l] = new ScanLine2D(dp);
//			linesIS[l] = new ScanLine2D(dpIS);
//			linesBlank[l] = new ScanLine2D(dpBlank);
//		}
//		// 
//		Image2D img[] = new Image2D[3];
//		img[2].getSettImage().setTitle("Ex");
//		img[1].getSettImage().setTitle("Ex IS");
//		img[0].getSettImage().setTitle("Ex blank");
//		img[2] = new Image2D(new Dataset2D(lines));
//		img[1] = new Image2D(new Dataset2D(linesIS));
//		img[0] = new Image2D(new Dataset2D(linesBlank));
//		return img;
//	}
}
