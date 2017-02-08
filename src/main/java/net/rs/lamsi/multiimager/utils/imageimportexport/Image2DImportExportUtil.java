package net.rs.lamsi.multiimager.utils.imageimportexport;

import java.io.BufferedReader;
import java.io.File;
import java.util.Arrays;
import java.util.Vector;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.ImageGroupMD;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.DatasetContinuousMD;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.DatasetMD;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.ScanLineMD;
import net.rs.lamsi.massimager.Settings.image.SettingsImage2D;
import net.rs.lamsi.massimager.Settings.image.sub.SettingsGeneralImage;
import net.rs.lamsi.massimager.Settings.image.sub.SettingsGeneralImage.XUNIT;
import net.rs.lamsi.massimager.Settings.image.sub.SettingsImageContinousSplit;
import net.rs.lamsi.massimager.Settings.image.visualisation.SettingsPaintScale;
import net.rs.lamsi.massimager.Settings.importexport.SettingsImageDataImportTxt;
import net.rs.lamsi.massimager.Settings.importexport.SettingsImageDataImportTxt.IMPORT;
import net.rs.lamsi.massimager.Settings.importexport.SettingsImageDataImportTxt.ModeData;
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
		// read x only once
		Vector<Float>[] x = null; 
		float[] startXValue = null;
		int xcol = -1, ycol=-1; 

		ModeData mode = sett.getModeData();
		
		int xmatrix = -1;
		
		if(mode.equals(ModeData.X_MATRIX_STANDARD)) {
			// search for xmatrix.csv

			for(int f=0; f<files.length && xmatrix == -1; f++) {
				if(files[f].getName().startsWith("xmatrix")) {
					xmatrix = f;
					// import xmatrix
					File file = files[f];
					BufferedReader br = txtWriter.getBufferedReader(file);
					String s; 
					int dp = 0;
					while ((s = br.readLine()) != null  && (sett.getEndDP()==0 || dp<sett.getEndDP())) {
						dp++;
						if(dp>=sett.getStartDP()) {
							// try to separate by separation
							String[] sep = s.split(separation);
							// initialise
							if(x == null) {
								int minus = sett.getStartLine()==0? 0 : sett.getEndLine()-1;
								int size = sett.getEndLine()==0? sep.length-minus : Math.min(sep.length, sett.getEndLine()) -minus;
								x = new Vector[size];
								startXValue = new float[size];
							}
							// fill data
							int c = 0;
							for(int i=sett.getStartLine()==0? 0 : sett.getEndLine()-1; i<sep.length && (sett.getEndLine()==0 || i<sett.getEndLine()); i++) {
								if(x[c]==null) {
									x[c] = new Vector<Float>();
									startXValue[c] = Float.valueOf(sep[i]);
								}
								x[c].addElement(Float.valueOf(sep[i])-startXValue[c]);
								c++;
							}
						}
					}
				}
			}
		}
		
		// resulting images
		Image2D[] img = new Image2D[files.length - (xmatrix==-1? 0 : 1)];
		// store data in Vector
		// x[line].get(dp)
		Vector<ScanLineMD> scanLines = new Vector<ScanLineMD>();  
		

		ImageGroupMD group = new ImageGroupMD();
		// one file is one dimension (image) of scanLines
		for(int f=0; f<files.length; f++) {
			// skip xmatrix
			if(f==xmatrix)
				continue;
			else {
				File file = files[f];
				Vector<Double>[] y = null;  
				// for metadata collection if selected in settings
				String metadata = "";
				String title = "";
	
				// read text file 
				// line by line
				BufferedReader br = txtWriter.getBufferedReader(file);
				String s;
				int k = 0;
				int line = 0;
				int dp = 0;
				while ((s = br.readLine()) != null) {
					// try to separate by separation
					String[] sep = s.split(separation);
					// data
					if(sep.length>1 && TextAnalyzer.isNumberValues(sep)) { 
						// increment dp 
						dp++;
						// 
						line = 0;
						// initialise data lists
						if(xcol==-1) {
							if(mode==ModeData.XYXY_ALTERN) {
								xcol = sep.length/2;
								// limits
								if(sett.getEndLine()!=0 && xcol>sett.getEndLine()) xcol = sett.getEndLine();
								if(sett.getStartLine()!=0) xcol = xcol+1-sett.getStartLine();
								ycol = xcol;
							}
							else {
								xcol = mode==ModeData.ONLY_Y || mode==ModeData.X_MATRIX_STANDARD? 0 : 1; 
								ycol = (sep.length-xcol);
								// limits
								if(sett.getEndLine()!=0 && ycol>sett.getEndLine()) ycol = sett.getEndLine();
								if(sett.getStartLine()!=0) ycol = ycol+1-sett.getStartLine();
							}
						}
						if(y==null) {
							if(mode!=ModeData.ONLY_Y && x==null) {
								startXValue = new float[xcol];
								x = new Vector[xcol];  
								for(int i=0; i<x.length; i++)
									x[i] = new Vector<Float>();
							}
							y = new Vector[ycol]; 
							for(int i=0; i<y.length; i++)
								y[i] = new Vector<Double>();
						}
						// add data if dp is not excluded
						if(sett.getStartDP()==0 || dp>=sett.getStartDP()) {
							// add data if line is not excluded
							for(int i=0; i<sep.length && (sett.getEndLine()==0 || line<sett.getMaxLines()); i++) {
								// x is only added in f==0 (first file)
								switch(mode) { // TODO
								case X_MATRIX_STANDARD:
								case ONLY_Y:
									if((sett.getStartLine()==0 || i+1>=sett.getStartLine())) {
										if(line==187)
											System.out.println("");
										y[line].addElement(Double.valueOf(sep[i]));
										line++;
									}
									break;
								case XYXY_ALTERN:
									if(sett.getStartLine()==0 || i/2+1>=sett.getStartLine()) {
										if(i%2==1) {
											y[line].addElement(Double.valueOf(sep[i]));
											line++;
										}
										else if(f==0) {
											// first as 0
											if(x[line].size()==0) {
												x[line].addElement(0.f);
												startXValue[line] = Float.valueOf(sep[i]);
											}
											// relative to startX
											else x[line].addElement(Float.valueOf(sep[i])-startXValue[line]);
										}
									}
									break;
								case XYYY: 
									// add x once
									if(f==0 && i==0) {
										// first as 0
										if(x[0].size()==0) {
											x[0].addElement(0.f);
											startXValue[0] = Float.valueOf(sep[i]);
										}
										// relative to startX
										else x[0].addElement(Float.valueOf(sep[i])-startXValue[0]);
									}
									// add y
									if(i!=0){
										if(sett.getStartLine()==0 || i>=sett.getStartLine()) {
											y[line].addElement(Double.valueOf(sep[i]));
											line++;
										}
									}
									break;
								} 
							}  
						}
						// last dp added?
						if(sett.getEndDP()!=0 && dp>=sett.getEndDP())
							break;
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
					case X_MATRIX_STANDARD:
						if(x.length==1) {
							scanLines.get(i).setX(x[0]);
						}
						else scanLines.get(i).setX(x[i]);
						break;
						//TODO
					}
	
				}
	
				// Generate Image2D from scanLines
				int index = xmatrix==-1 || f<xmatrix? f : f-1;
				Image2D image = createImage2D(file, title, metadata, scanLines, index, sett.getModeImport().equals(IMPORT.CONTINOUS_DATA_TXT_CSV));
				img[index] = image;
				group.add(img[index]);
			}
		}
		//return image 
		return img;
	}


	// one file per line
	// load text files with separation
	// time  SEPARATION   intensity
	// new try the first is not good
	public static Image2D[] importTextFilesToImage(File[] files, SettingsImageDataImportTxt sett, String separation, boolean sortFiles) throws Exception { 
		// reset title line
		titleLine = null;
		
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
		boolean continuous = sett.getModeImport().equals(IMPORT.CONTINOUS_DATA_TXT_CSV);
		boolean hardsplit = continuous && sett.isUseHardSplit() && !(sett.getSplitAfter()==0 || sett.getSplitAfter()==-1);
		
		ImageGroupMD group = new ImageGroupMD();
		Image2D realImages[] = new Image2D[lines.firstElement().getImageCount()];
		for(int i=0; i<realImages.length; i++) {   
			// has title line? with xyyyy
			if(titleLine!=null && titleLine.length>=realImages.length+1)
				realImages[i] = createImage2D(parent, titleLine[i+1], metadata, lines, i, continuous && !hardsplit);  
			else realImages[i] = createImage2D(parent, "", metadata, lines, i, continuous && !hardsplit);  
			// continuous?
			if(continuous && !hardsplit) {
				// set split settings for continuous data (non hardsplit)
				DatasetContinuousMD data = ((DatasetContinuousMD)realImages[i].getData());
				data.setSplitSettings(new SettingsImageContinousSplit(sett.getSplitAfter(), sett.getSplitStart(), sett.getSplitUnit()));
			}

			// add to group (also sets the group for this image)
			group.add(realImages[i]);
		}

		//return image
		return realImages;
	}

	// needed for image creation from Vector<ScanLine>
	// titleline is always X y1 y2 y3 y4 (titles) and size = dimension+1
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
	public static Vector<ScanLineMD> importTextFilesToScanLines(File[] files, SettingsImageDataImportTxt sett, String separation, boolean sortFiles) throws Exception { 
		long time1 = System.currentTimeMillis();
		// sort text files by name:
		if(sortFiles)
			files = FileAndPathUtil.sortFilesByNumber(files);
		// images (getting created at first data reading)
		Vector<ScanLineMD> lines=null; 
		
		// excluded columns
		Vector<Integer> excludedCol = sett.getExcludeColumnsArray();
		// calc fist used column
		int firstCol = 0;
		if(excludedCol!=null) {
			for(int ex : excludedCol) {
				if(ex==firstCol) firstCol++;
				else break;
			}
		}
		
		// perform hardsplit
		boolean continuous = sett.getModeImport().equals(IMPORT.CONTINOUS_DATA_TXT_CSV);
		boolean hardsplit = continuous && sett.isUseHardSplit() && !(sett.getSplitAfter()==0 || sett.getSplitAfter()==-1);
		boolean wasStartErased = !hardsplit; 
		boolean scanLinesSkipped = !hardsplit;
		float startX = 0;
		int cDP = 0;
		
		// file after file open text file
		// start with starting line
		for(int i=(sett.getStartLine()==0 || continuous? 0 : sett.getStartLine()-1); i<files.length && (sett.getEndLine()==0 || i<=sett.getEndLine()); i++) {
			// data of one line
			Vector<Float> x  = null;
			// iList[dimension].get(dp)
			Vector<Double>[] iList=null;
			// more than one intensity column? then extract all cols (if true)
			boolean dataFound = false; 
			// for metadata collection if selected in settings
			String[] lastLine = null;
			// track data points
			int dp = 0;
			// starting data points for splitting of continuous data
			if(continuous && hardsplit && sett.getSplitUnit().equals(XUNIT.DP)) {
				dp -= sett.getSplitStartDP();
			}
			// read file
			File f = files[i]; 
			// line by line add datapoints to current Scanlines
			BufferedReader br = txtWriter.getBufferedReader(f);
			String sline;
			int k = 0;
			while ((sline = br.readLine()) != null) {
				// try to seperate by seperation
				String[] sep = sline.split(separation);
				// data
				if(sep.length>1 && TextAnalyzer.isNumberValues(sep)) {
					// increment
					dp++;
					// initialise
					// titleLine could be written one line before data lines
					if(!dataFound) {
						dataFound = true;  // call this only once 

						// create all new Images
						int colCount = sep.length;
						if(excludedCol!=null) {
							for(int ex = excludedCol.size()-1; ex>=0; ex--)
								if(excludedCol.get(ex)<colCount) colCount--;
								else break;
						}
						colCount +=  -(sett.isNoXData()? 0:1);
						iList = new Vector[colCount]; 
						
						// set titles only once
						if(titleLine==null && lastLine!=null && lastLine.length==sep.length) {
							int img = 1;
							int ex = 0; 
							titleLine = new String[colCount+1]; 
							titleLine[0] = "x";
							for(int s= (sett.isNoXData()? firstCol : firstCol+1); s<lastLine.length; s++) {
								boolean isExcluded = false;
								// add title if not excluded
								if(excludedCol!=null) {
									for( ; ex<excludedCol.size(); ex++) {
										if(excludedCol.get(ex)==s) {
											isExcluded= true;
											break;
										}
										if(excludedCol.get(ex)>s)
											break;
									}
								}
								if(!isExcluded) {
									titleLine[img] = lastLine[s];
									img++;
								}
							}
						}
						
						// has X data?
						if(!sett.isNoXData()) {
							x = new Vector<Float>(); 
							// startX for hardsplit
							startX = Float.valueOf(sep[firstCol]);
						}
						// Image creation 
						for(int img=0; img<iList.length; img++) {
							iList[img] = new Vector<Double>(); 
						} 
					}
					// hardsplit jumps over first datapoints
					if(hardsplit) {
						// hardsplit continuous with time data: erase startX
						if(!wasStartErased && XUNIT.s.equals(sett.getSplitUnit()) && ((int)(sett.getSplitStart()*1000))!=0) {
							float cx = Float.valueOf(sep[firstCol]);
							// x still < than start time?
							if(cx-startX<sett.getSplitStart())
								dp = 0;
							else {
								wasStartErased = true;
								startX = cx;
							}
						}
						else if(!scanLinesSkipped) {
							cDP++;
							if(XUNIT.s.equals(sett.getSplitUnit())) {
								float cx = Float.valueOf(sep[firstCol]);
								scanLinesSkipped = (cx-startX)>=sett.getSplitAfter()*(sett.getStartLine()-1);
							}
							else {
								scanLinesSkipped = cDP>=sett.getSplitAfterDP()*(sett.getStartLine()-1);
							}
							// still not?
							if(!scanLinesSkipped)
								dp = 0;
						}							
					}
					// add data if DP is in range of start/end dp
					if(dp>=sett.getStartDP() && dp>0 && (sett.getEndDP()==0 || dp<=sett.getEndDP())) { 
						// has X data?
						if(!sett.isNoXData()) {
							if(x.size()==0) startX = Float.valueOf(sep[firstCol]);
							x.add(Float.valueOf(sep[firstCol])-startX);
						}
						// add Data Points to all images 
						int img = 0;
						int ex = 0;
						for(int s= (sett.isNoXData()? firstCol : firstCol+1); s<sep.length; s++) {
							boolean isExcluded = false;
							// add Datapoint if not excluded
							if(excludedCol!=null) {
								for( ; ex<excludedCol.size(); ex++) {
									if(excludedCol.get(ex)==s) {
										isExcluded= true;
										break;
									}
									if(excludedCol.get(ex)>s)
										break;
								}
							}
							if(!isExcluded) {
								iList[img].add(Double.valueOf(sep[s]));
								img++;
							}
						}
					}
					
					// hardsplit continuous data
					if(hardsplit && (x==null || x.size()>1)) {
						// split after DP   / split after time
						boolean endOfLine = false;
						if(XUNIT.DP.equals(sett.getSplitUnit())) 
							endOfLine = dp>=sett.getSplitAfterDP();
						else {
							float xstart = x.firstElement();
							float cx = x.lastElement();
							int currentLine = lines==null? 1 : lines.size()+1;
							endOfLine = (cx-xstart)>=sett.getSplitAfter()*currentLine;
						}  
						// has reached end of line
						if(endOfLine) {
							// add line
							// init lines vector
							if(lines==null)
								lines = new Vector<ScanLineMD>(); 
				
							// add new line
							lines.add(new ScanLineMD()); 
							// add data to line
							// has X data?
							if(!sett.isNoXData())
								lines.lastElement().setX(x);
							// add all dimensions
							for(int img=0; img<iList.length; img++) {  
								// add data
								lines.lastElement().addDimension(iList[img]);
								//reset iList
								iList[img].removeAllElements();
							} 
							// set dp = 0
							dp=0;
							// reset lists
							if(x!=null)
								x.removeAllElements();
							
							// enough lines?
							if(lines.size()>=sett.getEndLine() && sett.getEndLine()!=0)
								return lines;
						}
					}
				}
				// or metadata
				else {
					// title
					if(i==0 && k==0 && sline.length()<=30) {
						title = sline;
					}
					metadata += sline+"\n"; 
				}
				// last line
				lastLine = sep;
				k++;
			} 

			if(!hardsplit) {
				// init lines vector
				if(lines==null)
					lines = new Vector<ScanLineMD>(); 
	
				// add new line
				lines.add(new ScanLineMD()); 
				// add data to line
				// has X data?
				if(!sett.isNoXData())
					lines.lastElement().setX(x);
				// add all dimensions
				for(int img=0; img<iList.length; img++) {  
					// add data
					lines.lastElement().addDimension(iList[img]);
				} 
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
		// count data points
		int dp = 0;
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
					dp = 0;
				}
				// generate new list
				if(iList==null) {
					int lineCount = sep.length-valueindex;
					if(sett.getEndLine()!=0 && lineCount>sett.getEndLine()) lineCount = sett.getEndLine();
					lineCount -= sett.getStartLine();
					
					iList = new Vector[lineCount];
					for(int i=0; i<iList.length; i++) {
						iList[i] = new Vector<Double>();
					}
				}
				// dp increment
				dp++;
				// add all data points of this line (if inside start/end limits)
				if((dp>=sett.getStartDP()) && (sett.getEndDP()==0 || dp<=sett.getEndDP())) {
					for(int i=valueindex+sett.getStartLine(); i<sep.length && (sett.getEndLine()==0 || i-valueindex<sett.getEndLine()); i++) {
						try {
							iList[i-valueindex-sett.getStartLine()].addElement(Double.valueOf(sep[i]));
						}catch(Exception ex) { 
							iList[i-valueindex-sett.getStartLine()].addElement(-1.0);
						}
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
					//
					dp=0;
				}
				// generate new list
				if(x==null) {
					x = new Vector[sep.length-valueindex];
					for(int i=0; i<x.length; i++) 
						x[i] = new Vector<Float>();
				}
				// add all intensities
				if((dp>=sett.getStartDP()) && (sett.getEndDP()==0 || dp<=sett.getEndDP())) {
					for(int i=valueindex+sett.getStartLine(); i<sep.length && (sett.getEndLine()==0 || i-valueindex<sett.getEndLine()); i++) {
						try {
							x[i-valueindex-sett.getStartLine()].addElement(Float.valueOf(sep[i]));
						}catch(Exception ex) { 
							x[i-valueindex-sett.getStartLine()].addElement(-1.f);
						}
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
		ImageGroupMD group = new ImageGroupMD();
		for(int i=0; i<titles.size(); i++) {
			Image2D img = createImage2D(file, titles.get(i), metadata, scanLines, i, sett.getModeImport().equals(IMPORT.CONTINOUS_DATA_TXT_CSV));
			images.add(img);
			group.add(img);
		}
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
	public static Vector<ScanLineMD> importNeptuneTextFilesToScanLines(File[] files, SettingsImageDataImportTxt sett, String separation, boolean sortFiles) throws Exception { 
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
		general.setRAWFilepath(file.getPath());
		if(title=="") {
			general.setRAWFilepath(file.getParent());
			title = FileAndPathUtil.eraseFormat(file.getName());
		}
		general.setTitle(title);
		general.setMetadata(metadata);
		// Image creation
		// if only one line was found it is a Image2DContinous with one data line
		if(continous) 
			return new Image2D(new DatasetContinuousMD(scanLines.firstElement()), index, new SettingsImage2D(paint, general));
		// else just add it as normal matrix-data image
		else return new Image2D(new DatasetMD(scanLines), index, new SettingsImage2D(paint, general));
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
		Image2D img = new Image2D(new SettingsImage2D(paint, general));
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
