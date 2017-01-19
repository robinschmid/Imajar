package net.rs.lamsi.multiimager.utils.imageimportexport;

import java.io.BufferedReader;
import java.io.File;
import java.util.Vector;

import net.rs.lamsi.massimager.Image.Image2D;
import net.rs.lamsi.massimager.Image.Image2DContinous;
import net.rs.lamsi.massimager.Image.data.DataPoint2D;
import net.rs.lamsi.massimager.Image.data.ScanLine;
import net.rs.lamsi.massimager.Settings.SettingsGeneralImage;
import net.rs.lamsi.massimager.Settings.SettingsPaintScale;
import net.rs.lamsi.massimager.Settings.image.SettingsImageDataImport;
import net.rs.lamsi.massimager.Settings.image.SettingsImageDataImportTxt;
import net.rs.lamsi.massimager.Settings.image.SettingsImageDataImportTxt.IMPORT;
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
	public static Image2D[] import2DIntensityToImage(File[] file, SettingsImageDataImportTxt sett, String separation) throws Exception { 
		Image2D[] img = new Image2D[file.length];
		for(int i=0; i<file.length; i++)
			img[i] = import2DIntensityToImage(file[i], sett, separation);
		return img;
	}
	public static Image2D import2DIntensityToImage(File file, SettingsImageDataImportTxt sett, String separation) throws Exception { 
		// store data in Vector
		Vector<ScanLine> scanLines = new Vector<ScanLine>();  
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
				// scan line found: add scan line
				DataPoint2D[] line = new DataPoint2D[sep.length];
				for(int i=0; i<sep.length; i++) 
					line[i] = new DataPoint2D(i, Double.valueOf(sep[i])); 
				// 
				scanLines.add(new ScanLine(line)); 
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
		// Generate Image2D from scanLines
		Image2D img = createImage2D(file, title, metadata, scanLines, sett.getModeImport().equals(IMPORT.CONTINOUS_DATA_TXT_CSV));
		//return image 
		return img;
	}


	// one file per line
	// load text files with separation
	// time  SEPARATION   intensity
	// new try the first is not good
	public static Image2D[] importTextFilesToImage(File[] files, SettingsImageDataImportTxt sett, String separation, boolean sortFiles) throws Exception { 

		Vector<ScanLine>[] images;
		if(sett.getModeImport()==IMPORT.PRESETS_THERMO_NEPTUNE)
			images = importNeptuneTextFilesToScanLines(files, sett, separation, sortFiles);
		else images = importTextFilesToScanLines(files, sett, separation, sortFiles);

		Image2D realImages[] = new Image2D[images.length];
		// parent directory as raw file path 
		File parent = files[0].getParentFile();
		if(SettingsImageDataImportTxt.class.isInstance(sett)) {
			if(((SettingsImageDataImportTxt)sett).isFilesInSeparateFolders())
				parent = parent.getParentFile();
			else if(((SettingsImageDataImportTxt)sett).getModeImport()==IMPORT.CONTINOUS_DATA_TXT_CSV)
				parent = files[0];
		}
		// for all images
		for(int i=0; i<realImages.length; i++) {   
			realImages[i] = createImage2D(parent, title, metadata, images[i], sett.getModeImport().equals(IMPORT.CONTINOUS_DATA_TXT_CSV)); 
		}

		// set titles if a title line was found
		if(titleLine!=null && titleLine.length>=images.length+1) {
			for(int t=0; t<realImages.length; t++)
				realImages[t].getSettImage().setTitle(titleLine[t+1]);
		}

		//return image
		return realImages;
	}

	// needed for image creation from Vector<ScanLine>
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
	public static Vector<ScanLine>[] importTextFilesToScanLines(File[] files, SettingsImageDataImport sett, String separation, boolean sortFiles) throws Exception { 
		long time1 = System.currentTimeMillis();
		// sort text files by name:
		if(sortFiles)
			files = FileAndPathUtil.sortFilesByNumber(files);
		// images (getting created at first data reading)
		Vector<ScanLine>[] images=null; 
		Vector<DataPoint2D>[] dpList=null;
		// more than one intensity column? then extract all colls (if true)
		boolean dataFound = false; 
		// for metadata collection if selected in settings
		String[] lastLine = null;

		// file after file open text file
		for(int i=0; i<files.length; i++) {
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
						images = new Vector[sep.length-1]; 
						dpList = new Vector[sep.length-1]; 
						// Image creation
						for(int img=0; img<images.length; img++) {
							images[img] = new Vector<ScanLine>();
							dpList[img] = new Vector<DataPoint2D>();
						}
					}

					// add Datapoints to all images
					for(int img=0; img<images.length; img++) {
						// add Datapoint
						dpList[img].add(new DataPoint2D(Float.valueOf(sep[0]), Double.valueOf(sep[img+1])));
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

			// for all images
			for(int img=0; img<dpList.length; img++) { 
				// add new lines to each list 
				images[img].addElement(new ScanLine(dpList[img]));

				if(i+1<files.length) {
					// create new dpLists for next file
					dpList[img] = new Vector<DataPoint2D>();
				}
			} 
		}
		//return image
		return images;
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
		Vector<ScanLine> scanLines = new Vector<ScanLine>(); 
		// for metadata collection if selected in settings
		String metadata = "";
		String title = "";  
		// save where values are starting
		int valueindex = -1;
		int lineCounter = 0;
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
			// try to seperate by seperation
			String[] sep = s.split(separation);
			// if sep.size==1 try and split symbol=space try utf8 space
			if(separation.equals(" ") && sep.length<=1) {
				sep = s.split(splitUTF8);
				if(sep.length>1)
					separation = splitUTF8; 
			}
			// is dataline? Y in col3? or col2
			if(sep.length>4 && ((valueindex!=5 && sep[3].equalsIgnoreCase("Y") && TextAnalyzer.isNumberValue(sep[4])) || (valueindex!=4 && sep[4].equalsIgnoreCase("Y") && TextAnalyzer.isNumberValue(sep[5])))) {
				if(valueindex==-1) {
					valueindex = TextAnalyzer.isNumberValue(sep[4])? 4:5;
				}
				// title in col2
				if(title=="") title = sep[2];
				else if(!title.equals(sep[2])) { 
					// a new element was found
					// Generate Image2D from scanLines
					images.add(createImage2D(file, title, metadata, scanLines, sett.getModeImport().equals(IMPORT.CONTINOUS_DATA_TXT_CSV)));
					// set to start values
					title = "";
					lineCounter = 0;
					scanLines.removeAllElements();
				}
				// add Dataline to current image (element)
				DataPoint2D[] dpLine = new DataPoint2D[sep.length-valueindex];
				// add all DataPoints
				for(int i=valueindex; i<sep.length; i++) {
					try {
						dpLine[i-valueindex] = new DataPoint2D(i-valueindex, Double.valueOf(sep[i]));
					}catch(Exception ex) { 
						dpLine[i-valueindex] = new DataPoint2D(i-valueindex, -1);
					}
				}
				scanLines.add(new ScanLine(dpLine));
				lineCounter++;
			}
			// or metadata
			else {
				metadata += s+"\n"; 
			}
		}
		// 
		if(lineCounter>1) 
			images.add(createImage2D(file, title, metadata, scanLines, sett.getModeImport().equals(IMPORT.CONTINOUS_DATA_TXT_CSV)));
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
	public static Vector<ScanLine>[] importNeptuneTextFilesToScanLines(File[] files, SettingsImageDataImport sett, String separation, boolean sortFiles) throws Exception { 
		long time1 = System.currentTimeMillis();
		// sort text files by name:
		if(sortFiles)
			files = FileAndPathUtil.sortFilesByNumber(files);
		// images (getting created at first data reading)
		Vector<ScanLine>[] images=null; 
		Vector<DataPoint2D>[] dpList=null;
		// more than one intensity column? then extract all colls (if true)
		boolean dataFound = false; 
		// for metadata collection if selected in settings
		String[] lastLine = null;

		// file after file open text file
		for(int i=0; i<files.length; i++) {
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
						if(lastLine!=null && lastLine.length==sep.length)
							titleLine = lastLine;

						// create all new Images
						images = new Vector[sep.length-2];
						dpList = new Vector[sep.length-2]; 
						// Image creation
						for(int img=0; img<images.length; img++) {
							images[img] = new Vector<ScanLine>();
							dpList[img] = new Vector<DataPoint2D>();
						}
					}

					// x 
					if(xstart==-1) 
						xstart = timeToSeconds(sep[1]);
					float x = timeToSeconds(sep[1])-xstart;  
					// add Datapoints to all images
					for(int img=0; img<images.length; img++) {
						double y = Double.valueOf(sep[img+2]);
						// add Datapoint
						dpList[img].add(new DataPoint2D(x, y)); 
						//
						System.out.println(x+"  "+y);
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

			// for all images
			for(int img=0; img<dpList.length; img++) { 
				// add new lines to each list 
				images[img].addElement(new ScanLine(dpList[img]));

				if(i+1<files.length) {
					// create new dpLists for next file
					dpList[img] = new Vector<DataPoint2D>();
				}
			} 
		}
		//return image
		return images;
	}

	/**
	 * creates a new Image2D with data and new settings
	 * @param file
	 * @param title
	 * @param metadata
	 * @param scanLines
	 * @return
	 */
	private static Image2D createImage2D(File file, String title, String metadata, Vector<ScanLine> scanLines, boolean continous) {
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
		// if only one line was found it is a Image2DContinous with one data line
		if(continous) 
			return new Image2DContinous(scanLines.get(0), paint, general);
		// else just add it as normal matrix-data image
		else return new Image2D(scanLines, paint, general);
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
