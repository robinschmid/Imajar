package net.rs.lamsi.multiimager.utils.imageimportexport;

import java.io.BufferedReader;
import java.io.File;
import java.util.Arrays;
import java.util.Vector;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.DatasetContinuousMD;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.DatasetMD;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.ScanLineMD;
import net.rs.lamsi.general.datamodel.image.data.twodimensional.DataPoint2D;
import net.rs.lamsi.general.datamodel.image.data.twodimensional.ScanLine2D;
import net.rs.lamsi.massimager.Settings.SettingsGeneralImage;
import net.rs.lamsi.massimager.Settings.SettingsPaintScale;
import net.rs.lamsi.massimager.Settings.image.SettingsImageDataImport;
import net.rs.lamsi.massimager.Settings.image.SettingsImageDataImportTxt;
import net.rs.lamsi.massimager.Settings.image.SettingsImageDataImportTxt.IMPORT;
import net.rs.lamsi.massimager.Settings.image.SettingsImageDataImportTxt.ModeData;
import net.rs.lamsi.utils.FileAndPathUtil;
import net.rs.lamsi.utils.mywriterreader.TxtWriter;

public class Image2DImportExportUtil {
	private static final TxtWriter txtWriter = new TxtWriter();


	/**
	 * switches the available modes for import
	 * @param files
	 * @param sett
	 * @return
	 * @throws Exception
	 */
	public static Image2D[] importTextDataToImage(File[] files, SettingsImageDataImportTxt sett, boolean sortFiles) throws Exception { 
		// get separation strng
		String separation = "";
		if(sett.getSeparation().equalsIgnoreCase("AUTO")) {
			// automatic separation TODO
			// separation = TextAnalyzer.getSeparationString(files, sett, separation);
		} 
		else {
			// normal separation
			separation = sett.getSeparation();
		} 
		// switch import mode
		switch(sett.getModeImport()){
		case MULTIPLE_FILES_LINES_TXT_CSV:
		case PRESETS_THERMO_NEPTUNE:
			return importTextFilesToImage(files, sett, separation, sortFiles);
		case CONTINOUS_DATA_TXT_CSV:
			// do the import for one file after each other because one image=one file
			Vector<Image2D> list = new Vector<Image2D>(); 
			for(File f : files) {
				Image2D[] imgList = importTextFilesToImage(new File[]{f}, sett, separation, sortFiles);
				// add all
				for(Image2D i : imgList)
					list.addElement(i);
			}
			return list.toArray(new Image2D[list.size()]);
		case ONE_FILE_2D_INTENSITY:  
			return import2DIntensityToImage(files, sett, separation);
		case PRESETS_THERMO_MP17: 
			return importFromThermoMP17FileToImage(files, sett); 
		}

		return null;
	}

	// one file per image
	// importing a 2D intensity matrix
	// txt /csv / Excel
	// I0	i1	i2	i3	i4
	// i5	i6	i7	i8	i9 ...
	public static Image2D[] import2DIntensityToImage(File[] files, SettingsImageDataImportTxt sett, String separation) throws Exception { 

		Image2D[] img = new Image2D[files.length];
		// store data in Vector
		// x[line].get(dp)
		Vector<ScanLineMD> scanLines = new Vector<ScanLineMD>();  
		// read x only once
		Vector<Float>[] x = null; 
		int xcol = -1;
		
		// one file is one dimension (image) of scanLines
		for(int f=0; f<files.length; f++) {
			File file = files[f];
			Vector<Double>[] y = null;  
			ModeData mode = sett.getModeData();
			// for metadata collection if selected in settings
			String metadata = "";
			String title = "";
	
			// read text file 
			// line by line
			BufferedReader br = txtWriter.getBufferedReader(file);
			String s;
			int k = 0;
			while ((s = br.readLine()) != null) {
				// try to separate by separation
				String[] sep = s.split(separation);
				// data
				if(sep.length>1 && TextAnalyzer.isNumberValues(sep)) { 
					// create data lists
					if(xcol==-1) {
						xcol = mode==ModeData.ONLY_Y? 0 : 1;
						if(mode==ModeData.XYXY_ALTERN)
							xcol = sep.length/2;
					}
					if(y==null) {
						if(mode!=ModeData.ONLY_Y && x==null) x = new Vector[xcol];  
						y = new Vector[sep.length-xcol]; 
					}
					
					// add data
					for(int i=0; i<sep.length; i++) {
						// x is only added in f==0 (first file)
						switch(mode) {
						case ONLY_Y:
							y[i].addElement(Double.valueOf(sep[i]));
							break;
						case XYXY_ALTERN:
							if(i%2==1) y[(i-1)/2].addElement(Double.valueOf(sep[i]));
							else if(f==0) x[i/2].addElement(Float.valueOf(sep[i]));
							break;
						case XYYY:
							if(i>0) y[(i-1)].addElement(Double.valueOf(sep[i]));
							else if(f==0 && i==0) x[0].addElement(Float.valueOf(sep[i]));
							break;
						}
					} 
				}
				// or metadata
				else {
					// title
					if(k==0 && s.length()<=30) {
						title = s;
					}
					metadata += s+"\n"; 
				}
				k++;
			}
			// Generate Lines
			for(int i=0; i<y.length; i++) {
				// create lines
				if(scanLines.size()<=i)
					scanLines.add(new ScanLineMD());
				// add data
				scanLines.get(i).addDimension(y[i]);
				switch(mode) { 
				case XYXY_ALTERN:
					scanLines.get(i).setX(x[i]);
					break;
				case XYYY:
					scanLines.get(i).setX(x[0]);
					break;
				}
					
			}
			
			// Generate Image2D from scanLines
			Image2D image = createImage2D(file, title, metadata, scanLines, f, sett.getModeImport().equals(IMPORT.CONTINOUS_DATA_TXT_CSV));
			img[f] = image;
		}
		//return image 
		return img;
	}


	// one file per line
	// load text files with separation
	// time  SEPARATION   intensity
	// new try the first is not good
	public static Image2D[] importTextFilesToImage(File[] files, SettingsImageDataImportTxt sett, String separation, boolean sortFiles) throws Exception { 

		Vector<ScanLineMD> lines;
		if(sett.getModeImport()==IMPORT.PRESETS_THERMO_NEPTUNE)
			lines = importNeptuneTextFilesToScanLines(files, sett, separation, sortFiles);
		else lines = importTextFilesToScanLines(files, sett, separation, sortFiles);

		// parent directory as raw file path 
		File parent = files[0].getParentFile();
		if(SettingsImageDataImportTxt.class.isInstance(sett)) {
			if(((SettingsImageDataImportTxt)sett).isFilesInSeparateFolders())
				parent = parent.getParentFile();
			else if(((SettingsImageDataImportTxt)sett).getModeImport()==IMPORT.CONTINOUS_DATA_TXT_CSV)
				parent = files[0];
		}
		// for all images
		Image2D realImages[] = new Image2D[lines.firstElement().getImageCount()];
		for(int i=0; i<realImages.length; i++) {   
			realImages[i] = createImage2D(parent, title, metadata, lines, i, sett.getModeImport().equals(IMPORT.CONTINOUS_DATA_TXT_CSV)); 
		}

		// set titles if a title line was found
		if(titleLine!=null && titleLine.length>=realImages.length+1) {
			for(int t=0; t<realImages.length; t++)
				realImages[t].getSettImage().setTitle(titleLine[t+1]);
		}

		//return image
		return realImages;
	}

	// needed for image creation from Vector<ScanLine>
	// titleline is always X y1 y2 y3 y4 (titles)
	private static String[] titleLine = null;
	private static String metadata = "";
	private static String title = "";
	/**
	 * 
	 * @param files
	 * @param sett
	 * @param separation
	 * @return an array of vector<ScanLine> that can be converted to images
	 * @throws Exception
	 */
	public static Vector<ScanLineMD> importTextFilesToScanLines(File[] files, SettingsImageDataImport sett, String separation, boolean sortFiles) throws Exception { 
		long time1 = System.currentTimeMillis();
		// sort text files by name:
		if(sortFiles)
			files = FileAndPathUtil.sortFilesByNumber(files);
		// images (getting created at first data reading)
		Vector<ScanLineMD> lines=null; 

		// file after file open text file
		for(int i=0; i<files.length; i++) {
			// data of one line
			Vector<Float> x  = null;
			// iList[dimension].get(dp)
			Vector<Double>[] iList=null;
			// more than one intensity column? then extract all cols (if true)
			boolean dataFound = false; 
			// for metadata collection if selected in settings
			String[] lastLine = null;
			// read file
			File f = files[i]; 
			// line by line add datapoints to current Scanlines
			BufferedReader br = txtWriter.getBufferedReader(f);
			String s;
			int k = 0;
			while ((s = br.readLine()) != null) {
				// try to seperate by seperation
				String[] sep = s.split(separation);
				// data
				if(sep.length>1 && TextAnalyzer.isNumberValues(sep)) {
					// titleLine could be written one line before data lines
					if(!dataFound) {
						dataFound = true;  // call this only once 
						if(lastLine!=null && lastLine.length==sep.length)
							titleLine = lastLine;

						// create all new Images
						iList = new Vector[sep.length-1]; 
						x = new Vector<Float>(); 
						// Image creation
						try {
							for(int img=0; img<iList.length; img++) {
								iList[img] = new Vector<Double>(); 
							}
						}catch(Exception ex) {
							ex.printStackTrace();
						}
					}

					// add Datapoints to all images
					if(iList==null)
						iList = null;
					for(int img=0; img<iList.length; img++) {
						// add Datapoint
						iList[img].add(Double.valueOf(sep[img+1]));
						x.add(Float.valueOf(sep[0]));
					}
				}
				// or metadata
				else {
					// title
					if(i==0 && k==0 && s.length()<=30) {
						title = s;
					}
					metadata += s+"\n"; 
				}
				// last line
				lastLine = sep;
				k++;
			} 

			if(lines==null)
				lines = new Vector<ScanLineMD>(); 
			
			// add new line
			lines.add(new ScanLineMD()); 
			lines.lastElement().setX(x);
			// for all dimensions
			for(int img=0; img<iList.length; img++) {  
				// add data
				lines.lastElement().addDimension(iList[img]);
			} 
		}
		//return image
		return lines;
	}

	//################################################################################
	// Presets

	/**
	 * imports from Thermo MP17 Files (iCAP-Q) (different separations)
	 * 0		1	2			3	4	
	 * MainRuns	0	75As (1)	Y	7.5000022500006747
	 * or x values for m/z --> delete
	 * MainRuns	0	75As (1)	X [u]	74.9219970703125
	 * @param file
	 * @param sett
	 * @return
	 * @throws Exception
	 */
	public static Image2D[] importFromThermoMP17FileToImage(File[] file, SettingsImageDataImportTxt sett) throws Exception { 
		// images
		Vector<Image2D> images=new Vector<Image2D>();
		// all files
		for(File f : file) 
			importFromThermoMP17FileToImage(images, f, sett);
		//return image 
		Image2D imgArray [] = new Image2D[images.size()];
		imgArray = images.toArray(imgArray);
		return imgArray;
	}
	private static void importFromThermoMP17FileToImage(Vector<Image2D> images, File file, SettingsImageDataImportTxt sett) throws Exception { 
		// images
		// store data in Vector
		Vector<ScanLineMD> scanLines = new Vector<ScanLineMD>(); 
		Vector<String> titles = new Vector<String>();
		Vector<Float>[] x = null;
		boolean hasTimeAlready = false;
		// scan line count is known but number data points is unknown
		// iList[line].get(dp)
		Vector<Double>[] iList = null;
		// for metadata collection if selected in settings
		String metadata = "";
		String title = "";  
		// save where values are starting
		int valueindex = -1;
		// separation 
		String separation = sett.getSeparation();
		// separation for UTF-8 space 
		char splitc = 0;
		String splitUTF8 = String.valueOf(splitc);
		//
		// line by line
		BufferedReader br = txtWriter.getBufferedReader(file);
		String s;
		while ((s = br.readLine()) != null) {
			// try to separate by separation
			String[] sep = s.split(separation);
			// if sep.size==1 try and split symbol=space try utf8 space
			if(separation.equals(" ") && sep.length<=1) {
				sep = s.split(splitUTF8);
				if(sep.length>1)
					separation = splitUTF8; 
			}
			// is dataline? Y in col3? or col2
			if(sep.length>4 && ((valueindex!=5 && sep[3].equalsIgnoreCase("Y") && TextAnalyzer.isNumberValue(sep[4])) 
					|| (valueindex!=4 && sep[4].equalsIgnoreCase("Y") && TextAnalyzer.isNumberValue(sep[5])))) {
				if(valueindex==-1) {
					valueindex = TextAnalyzer.isNumberValue(sep[4])? 4:5;
				}
				// title in col2
				if(title=="") {
					title = sep[2]; 
				}
				else if(!title.equals(sep[2])) { 
					// a new element was found
					titles.add(title);
					// set to start values
					title = sep[2];
					// generate scan lines
					if(scanLines.size()==0) 
						for(Vector<Double> in : iList) 
							scanLines.addElement(new ScanLineMD());
					// add i dimension (image)
					for (int i = 0; i < iList.length; i++) {
						scanLines.get(i).addDimension(iList[i]);
					} 
					// reset
					iList = null;
				}
				// generate new list
				if(iList==null) {
					iList = new Vector[sep.length-valueindex];
					for(int i=0; i<iList.length; i++) {
						iList[i] = new Vector<Double>();
					}
				}
				// add all intensities
				for(int i=valueindex; i<sep.length; i++) {
					try {
						iList[i-valueindex].addElement(Double.valueOf(sep[i]));
					}catch(Exception ex) { 
						iList[i-valueindex].addElement(-1.0);
					}
				}
			} 
			// is dataline? Time in col3? or col2
			else if(!hasTimeAlready && sep.length>4 && ((valueindex!=5 && sep[3].equalsIgnoreCase("Time") && TextAnalyzer.isNumberValue(sep[4])) 
						|| (valueindex!=4 && sep[4].equalsIgnoreCase("Time") && TextAnalyzer.isNumberValue(sep[5])))) {
					if(valueindex==-1) {
						valueindex = TextAnalyzer.isNumberValue(sep[4])? 4:5;
					}
					// title in col2
					if(title=="") title = sep[2];
					else if(!title.equals(sep[2])) { 
						// a new element was found
						// stop search for time values
						title = "";
						hasTimeAlready = true;
						// generate scan lines
						if(scanLines.size()==0) 
							for(Vector<Float> in : x) 
								scanLines.addElement(new ScanLineMD());
						// add x to all scan lines
						for (int i = 0; i < x.length; i++) {
							scanLines.get(i).setX(x[i]);
						} 
					}
					// generate new list
					if(x==null) {
						x = new Vector[sep.length-valueindex];
						for(int i=0; i<x.length; i++) 
							x[i] = new Vector<Float>();
					}
					// add all intensities
					for(int i=valueindex; i<sep.length; i++) {
						try {
							x[i].addElement(Float.valueOf(sep[i]));
						}catch(Exception ex) { 
							x[i].addElement(-1.f);
						}
					}
				} 
		}
		// 
		if(iList!=null) {
			// a new element was found
			titles.add(title);
			// generate scan lines
			if(scanLines.size()==0) 
				for(Vector<Double> in : iList) 
					scanLines.addElement(new ScanLineMD());
			// add i dimension (image)
			for (int i = 0; i < iList.length; i++) {
				scanLines.get(i).addDimension(iList[i]);
			} 
		}

		// Generate Image2D from scanLines
		for(int i=0; i<titles.size(); i++) 
			images.add(createImage2D(file, titles.get(i), metadata, scanLines, i, sett.getModeImport().equals(IMPORT.CONTINOUS_DATA_TXT_CSV)));
	}

	/**
	 * imports from Thermo Neptune files: .exp
	 * Sample ID: Sample2
	 * Cycle	Time	142Nd	
	 * 1	16:17:44:334	-1.4410645627666198e-004
	 * @param file
	 * @param sett
	 * @return
	 * @throws Exception
	 */
	public static Vector<ScanLineMD> importNeptuneTextFilesToScanLines(File[] files, SettingsImageDataImport sett, String separation, boolean sortFiles) throws Exception { 
		long time1 = System.currentTimeMillis();
		// sort text files by name:
		if(sortFiles)
			files = FileAndPathUtil.sortFilesByNumber(files);
		// images (getting created at first data reading)
		Vector<ScanLineMD> lines= new Vector<ScanLineMD>(); 
		// more than one intensity column? then extract all colls (if true)
		boolean dataFound = false; 
		// for metadata collection if selected in settings
		String[] lastLine = null;

		// file after file open text file
		for(int i=0; i<files.length; i++) {
			Vector<Float> xList = null; 
			// iList[dimension].get(dp)
			Vector<Double>[] iList = null;
			// read file
			File f = files[i]; 
			// start time
			float xstart = -1;
			// line by line add datapoints to current Scanlines 
			BufferedReader br = txtWriter.getBufferedReader(f);
			String s;
			int k=0;
			while ((s = br.readLine()) != null) {
				// try to seperate by seperation
				String[] sep = s.split(separation);
				// data
				if(sep.length>2 && TextAnalyzer.isNumberValue(sep[0]) && TextAnalyzer.isNumberValue(sep[2])) {
					// titleLine could be written one line before data lines
					if(!dataFound) {
						dataFound = true;  // call this only once 
						if(lastLine!=null && lastLine.length==sep.length) {
							// titlerow to: time y1 y2 y3
							titleLine = Arrays.copyOfRange(lastLine, 1, lastLine.length);
						}

						// create all new Images
						iList = new Vector[sep.length-2];
						xList = new Vector<Float>();
						// Image creation
						for(int img=0; img<iList.length; img++)
							iList[img] = new Vector<Double>(); 
					}

					// x 
					if(xstart==-1) 
						xstart = timeToSeconds(sep[1]);
					float x = timeToSeconds(sep[1])-xstart;  
					xList.add(x);
					// add Datapoints to all images
					for(int img=0; img<iList.length; img++) {
						double y = Double.valueOf(sep[img+2]);
						// add Datapoint
						iList[img].add(y); 
					}
				}
				// or metadata
				else {
					// title
					if(i==0 && k==0 && s.length()<=30) {
						title = s;
					}
					metadata += s+"\n"; 
				}
				// last line
				lastLine = sep;
				k++;
			} 

			lines.addElement(new ScanLineMD());
			lines.lastElement().setX(xList);
			// for all images
			for(int img=0; img<iList.length; img++) { 
				// add new lines to each list 
				lines.lastElement().addDimension(iList[img]);
			} 
		}
		//return image
		return lines;
	}

	/**
	 * creates a new Image2D with data and new settings
	 * @param file
	 * @param title
	 * @param metadata
	 * @param scanLines
	 * @param index 
	 * @return
	 */
	private static Image2D createImage2D(File file, String title, String metadata, Vector<ScanLineMD> scanLines, int index, boolean continous) {
		// Generate Image2D from scanLines
		SettingsPaintScale paint = SettingsPaintScale.createSettings(SettingsPaintScale.S_KARST_RAINBOW_INVERSE);
		SettingsGeneralImage general = new SettingsGeneralImage();
		general.resetAll();
		// Metadata 
		if(title=="") title = file.getName();
		general.setRAWFilepath(file.getPath());
		general.setTitle(title);
		general.setMetadata(metadata);
		// Image creation
		// if only one line was found it is a Image2DContinous with one data line
		if(continous) 
			return new Image2D(new DatasetContinuousMD(scanLines.firstElement()), index, paint, general);
		// else just add it as normal matrix-data image
		else return new Image2D(new DatasetMD(scanLines), index, paint, general);
	}

	/**
	 * creates a blank image2d without any data but with new settings
	 * @param file
	 * @param title
	 * @param metadata
	 * @param scanLines
	 * @return
	 */
	private static Image2D createImage2DBlank(File file, String title, String metadata) {
		// Generate Image2D from scanLines
		SettingsPaintScale paint = new SettingsPaintScale();
		SettingsGeneralImage general = new SettingsGeneralImage();
		paint.resetAll();
		general.resetAll();
		// Metadata 
		if(title=="") title = file.getName();
		general.setRAWFilepath(file.getPath());
		general.setTitle(title);
		general.setMetadata(metadata);
		// Image creation
		Image2D img = new Image2D(paint, general);
		return img;
	}




	public static final float[] F = {0.001f, 1, 60, 3600, 86400};
	/**
	 * s as time (hh:mm:ss:mmm
	 * @param s
	 * @return
	 */
	public static float timeToSeconds(String s) { 
		String[] split = s.split(":");

		float val = 0;
		for(int i=split.length-1; i>=0; i--) {
			val += Float.valueOf(split[i])*(F[split.length-1-i]);
		}
		return val;
	}
}
