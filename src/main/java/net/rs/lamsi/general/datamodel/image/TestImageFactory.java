package net.rs.lamsi.general.datamodel.image;

import java.util.Random;

import net.rs.lamsi.general.datamodel.image.data.multidimensional.DatasetMD;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.ScanLineMD;

public class TestImageFactory {

	
	public static ImageGroupMD createNonNormalImage(int c) {
		Random rand = new Random(System.currentTimeMillis());
		ScanLineMD[] lines = new ScanLineMD[24];
		for(int f=0; f<c; f++) {
		for(int l=0; l<lines.length; l++) { 
			Double[] i = new Double[240-l*2];
			for(int d=0; d<i.length; d++) {
				// middle the highest
				double in = (int)(l/4)*200.0;
				in += Math.abs(rand.nextInt(6000)/100.0);
				// create dp
				i[d] = in;
			}
			if(lines[l]==null)
				lines[l] = new ScanLineMD(i);
			else lines[l].addDimension(i);
		}
	}
		DatasetMD data = new DatasetMD(lines);
		return data.createImageGroup();
	}
	

	/**
	 * test images
	 * @return
	 */
	public static ImageGroupMD createTestStandard(int c) {
		Random rand = new Random(System.currentTimeMillis());
		ScanLineMD[] lines = new ScanLineMD[24];
		for(int f=0; f<c; f++) {
			for(int l=0; l<lines.length; l++) { 
				Double[] i = new Double[240];
				for(int d=0; d<i.length; d++) {
					// middle the highest
					double in = (int)(l/4)*200.0;
					in += Math.abs(rand.nextInt(6000)/100.0);
					// create dp
					i[d] = in;
				}
				if(lines[l]==null)
					lines[l] = new ScanLineMD(i);
				else lines[l].addDimension(i);
			}
		}
		DatasetMD data = new DatasetMD(lines);
		return data.createImageGroup();
	}
}
