package net.rs.lamsi.general.datamodel.image;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import net.rs.lamsi.general.datamodel.image.data.DataPoint2D;
import net.rs.lamsi.general.datamodel.image.data.Dataset2D;
import net.rs.lamsi.general.datamodel.image.data.ScanLine;
import net.rs.lamsi.general.datamodel.image.data.XYIData2D;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.datamodel.image.interf.ImageDataset;
import net.rs.lamsi.general.datamodel.image.listener.RawDataChangedListener;
import net.rs.lamsi.massimager.Heatmap.PaintScaleGenerator;
import net.rs.lamsi.massimager.MyMZ.MZChromatogram;
import net.rs.lamsi.massimager.MyOES.OESElementLine;
import net.rs.lamsi.massimager.MyOES.OESScan;
import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.massimager.Settings.SettingsGeneralImage;
import net.rs.lamsi.massimager.Settings.SettingsImage;
import net.rs.lamsi.massimager.Settings.SettingsMSImage;
import net.rs.lamsi.massimager.Settings.SettingsPaintScale;
import net.rs.lamsi.massimager.Settings.image.SettingsImage2DDataExport;
import net.rs.lamsi.massimager.Settings.image.operations.SettingsImage2DOperations;
import net.rs.lamsi.massimager.Settings.image.operations.quantifier.SettingsImage2DQuantifier;
import net.rs.lamsi.massimager.Settings.image.operations.quantifier.SettingsImage2DQuantifierIS;
import net.rs.lamsi.massimager.Settings.image.operations.quantifier.SettingsImage2DQuantifierLinear;
import net.rs.lamsi.massimager.Settings.visualization.plots.SettingsThemes;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow.LOG;
import net.rs.lamsi.multiimager.Frames.dialogs.selectdata.DataMinMaxAvg;
import net.rs.lamsi.multiimager.Frames.dialogs.selectdata.Image2DSelectDataAreaDialog.MODE;
import net.rs.lamsi.multiimager.Frames.dialogs.selectdata.RectSelection;
import net.rs.lamsi.utils.mywriterreader.BinaryWriterReader;

import org.jfree.chart.renderer.PaintScale;

// XY raw data! 
// have to be multiplied with velocity and spot size
public class Image2D implements Serializable, Collectable2D {	 
	// do not change the version!
	private static final long serialVersionUID = 1L;

	// Parent image with master settings
	protected Image2D parent;

	// image has nothing to do with quantifier class! so dont use a listener for data processing changed events TODO
	//protected Vector<IntensityProcessingChangedListener> listenerProcessingChanged = new Vector<IntensityProcessingChangedListener>();
	// intensityProcessingChanged? save lastIProcChangeTime and compare with one from quantifier class
	protected int lastIProcChangeTime = 0;

	//############################################################
	// Settings
	protected SettingsPaintScale settPaintScale;
	// LaserVelocity und Spotsize 
	protected SettingsImage settImage;
	// theme settings
	protected SettingsThemes settTheme; 
	protected SettingsImage2DQuantifier quantifier;
	// blank subtraction and internal standard
	protected SettingsImage2DOperations operations;

	//############################################################
	// listener
	protected Vector<RawDataChangedListener> rawDataChangedListener;

	//############################################################
	// data
	protected ImageDataset data;


	// are getting calculated only once or after processing changed
	// max and min z (intensity)
	protected double averageIProcessed = -1;
	protected double minZ=-1, maxZ=-1;
	protected float maxX = -1;
	protected double minZFiltered = -1;
	protected double maxZFiltered = -1;
	// store total dp count 
	protected int totalDPCount = -1;
	
	// Selected data stored in rectangles:
	// for use as external standard
	protected Vector<RectSelection> selectedData = null;
	protected Vector<RectSelection> excludedData = null;
	protected Vector<RectSelection> infoData = null;


	// COntstruct 
	public Image2D(SettingsPaintScale settPaintScale, SettingsImage setImage) {  
		try {
			settImage = (SettingsImage) BinaryWriterReader.deepCopy(setImage);
			this.settPaintScale = (SettingsPaintScale) BinaryWriterReader.deepCopy(settPaintScale); 
			// standard theme
			this.settTheme = new SettingsThemes();
			this.quantifier = new SettingsImage2DQuantifierLinear();
			this.operations = new SettingsImage2DOperations();
		} catch (Exception e) { 
			e.printStackTrace();
		}
	} 

	public Image2D(ImageDataset data) {
		this();
		data = this.data; 
	}
	public Image2D(ImageDataset data, SettingsPaintScale settPaintScale, SettingsImage setImage) { 
		this(settPaintScale, setImage);
		data = this.data;
	} 

	public Image2D() { 
		settPaintScale = new SettingsPaintScale();
		settPaintScale.resetAll();
		settImage = new SettingsGeneralImage();
		settImage.resetAll(); 
		// standard theme
		this.settTheme = new SettingsThemes();  
		//
		this.quantifier = new SettingsImage2DQuantifierLinear();
		this.operations = new SettingsImage2DOperations();
	}
	//#####################################################################################################
	// Generators STATIC
	// MZChrom Generation
	public static Image2D generateImage2DFrom(SettingsPaintScale settPaintScale, SettingsImage setImage, MZChromatogram[] mzchrom) {
		// TODO mehrere MZ machen 
		// Alle Spec
		// Erst Messpunkteanzahl ausrechnen 
		// lines listLines
		ScanLine[] listLines = new ScanLine[mzchrom.length];
		// Lines durchgehen
		for(int i=0; i<mzchrom.length; i++) { 
			// Datapoint array
			DataPoint2D[] dp = new DataPoint2D[mzchrom[i].getItemCount()];
			// x, i datapoints
			for(int d=0; d<mzchrom[i].getItemCount(); d++) {
				float x = mzchrom[i].getX(d).floatValue();
				double intensity = mzchrom[i].getY(d).doubleValue();
				dp[d] = new DataPoint2D(x, intensity);
			}

			// new line 
			listLines[i] = new ScanLine(dp);
		}
		// Image erstellen 
		return  new Image2D(new Dataset2D(listLines), settPaintScale, setImage); 
	}

	// Discontinous MS-Image
	// Generate Heatmap with Continous Data WIDHTOUT Triggerin every Line
	public static Image2D generateImage2DFromCon(SettingsPaintScale settPaintScale, SettingsMSImage setMSICon, MZChromatogram mzchrom) {  
		// usesTime = false --> scansPerLine
		boolean usesTimePerLine = setMSICon.getModeTimePerLine() == SettingsImage.MODE_TIME_PER_LINE;
		double timePerLine = setMSICon.getTimePerLine();  
		// Größe des Images aus Zeiten ableiten
		// deltaTime aus Daten lesen = Zeit zwischen Messungen 
		double overallTime = (mzchrom.getMaxX()-mzchrom.getMinX());  

		// XYZ anzahl ist definiert durch messwerte im MZChrom 
		int scanpoints = mzchrom.getItemCount();
		double[] x = new double[scanpoints]; 
		double[] z = new double[scanpoints];
		// zeigt an wo man sich in der listData befindet 
		int lasti = 0;
		double lastTime = mzchrom.getMinX(); 
		double deltatime;
		// lines 
		int lineCount = (int) Math.ceil(overallTime/timePerLine);
		Vector<ScanLine> scanLines = new Vector<ScanLine>(); 
		// Alle MZChrom punkte durchgehen und in xyz eintragen
		// wenn Zeit größer als timePerLine dann y um eins vergrößern
		for(int i=0; i<mzchrom.getItemCount(); i++) {
			deltatime = mzchrom.getX(i).doubleValue()-lastTime;
			// nächste Zeile?
			if((usesTimePerLine && deltatime>timePerLine) ||
					(!usesTimePerLine && (i)%(timePerLine)==0)) {
				// copy over to scanLine
				DataPoint2D[] data = new DataPoint2D[i-lasti];
				for(int s=0; s<i-lasti; s++) {
					data[s] = new DataPoint2D(x[lasti+s], z[lasti+s]); 
				} 
				scanLines.add(new ScanLine(data));
				// next line 
				lasti=i;
				lastTime = mzchrom.getX(i).doubleValue();  
			}
			// Daten eintragen und zwischen speichern
			x[i] = mzchrom.getX(i).doubleValue() -lastTime; 
			z[i] = mzchrom.getY(i).doubleValue();
		}
		// generate Image
		return new Image2D(new Dataset2D(scanLines), settPaintScale, setMSICon);
	}

	//
	// OES Generation
	public static Image2D generateImage2DFromOES(SettingsPaintScale settPaintScale, SettingsImage setImage, OESElementLine eLine) {
		// TODO mehrere MZ machen 
		// Alle Spec
		// Erst Messpunkteanzahl ausrechnen 
		// lines listLines
		OESScan scan;
		ScanLine[] listLines = new ScanLine[eLine.getListScan().size()];
		// Lines durchgehen
		for(int i=0; i<eLine.getListScan().size(); i++) { 
			scan = eLine.getListScan().get(i);
			// Datapoint array
			DataPoint2D[] dp = new DataPoint2D[scan.getTime().size()];
			// x, i datapoints
			for(int d=0; d<dp.length; d++) {
				float x = (scan.getTime().get(d).floatValue());
				double intensity = scan.getCenter().get(d).doubleValue();
				dp[d] = new DataPoint2D(x, intensity);
			}

			// new line 
			listLines[i] = new ScanLine(dp);
		}
		// Image erstellen 
		return  new Image2D(new Dataset2D(listLines), settPaintScale, setImage); 
	}


	//#########################################################################################################
	// TO ARRAY LISTS   
	public XYIData2D toXYIArray(SettingsImage setImg) {
		return toXYIArray(setImg.getImagingMode(), setImg.getRotationOfData(), setImg.isReflectHorizontal(), setImg.isReflectVertical() );
	}
	public XYIData2D toXYIArray(int imgMode, int rotation, boolean reflectH, boolean reflectV) {
		// easy?
		if(imgMode==SettingsImage.MODE_IMAGING_ONEWAY && rotation==0 && reflectH==false && reflectV == false) {
			return toXYIArray();
		}
		else {
			// count scan points
			int scanpoints = 0;
			for(int i=0; i<data.getLinesCount(); i++) {
				scanpoints += data.getLineLength(i);
			}
			// create data
			double[] x = new double[scanpoints];
			double[] y = new double[scanpoints];
			double[] z = new double[scanpoints];
			//
			ScanLine line; 
			int currentdp = 0;
			int realy, realx;
			double height = getMaxYProcessed();
			// for all lines
			for(int iy=0; iy<data.getLinesCount(); iy++) {
				// width  
				double width = getMaxXProcessed(iy);
				//
				realy = iy;
				// get line
				line = lines[realy];
				// for all datapoints in line
				for(int ix=0; ix<line.getData().length; ix++) {
					realx = ix;
					// get Datapoint
					DataPoint2D dp = line.getData()[realx];

					// x = time; NOT distance; so calc TODO
					x[currentdp] = getXProcessed(iy, ix);
					y[currentdp] = getYProcessed(iy);
					z[currentdp] = getIProcessed(iy, ix);

					// imagecreation mode: if twoways -> first reflect every 2. line (x values)
					if(imgMode==SettingsImage.MODE_IMAGING_TWOWAYS && iy%2 != 0) {
						// reflect x
						x[currentdp] += distPercent(width, x[currentdp]) *width; 
					}

					// flip values of x and y
					// xor: ^    reflectV and 180° = reflectH
					if(!(reflectH && reflectV && rotation==180)) {
						if((reflectH && rotation!=180) ^ (reflectV && rotation==180)) {// y 
							y[currentdp] += distPercent(height, y[currentdp]) *height; 
						}
						if((reflectV && rotation!=180) ^ (reflectH && rotation==180)) {// x
							x[currentdp] += distPercent(width, x[currentdp]) *width; 
						}
						if(rotation==180 && !reflectV && !reflectH) {
							y[currentdp] += distPercent(height, y[currentdp]) *height; 
							x[currentdp] += distPercent(width, x[currentdp]) *width; 
						}
					}
					// 90°
					if(rotation==90) {
						// x0 -> y0   xn -> yn
						double savey = y[currentdp];
						y[currentdp] = x[currentdp];
						// y0 -> xn   yn -> x0  ==> reflectH and then same as x 
						savey += distPercent(height, savey) *height; // reflectH
						x[currentdp] = savey;
					}
					// -90° = 270°
					if(rotation==270 || rotation ==-90) {
						// y0 -> x0   yn -> xn
						double savex = x[currentdp];
						x[currentdp] = y[currentdp];
						// x0 -> yn   xn -> y0  ==> reflectV and then same as y 
						savex += distPercent(width, savex) *width; // reflectV
						y[currentdp] = savex;
					}

					currentdp++;
				} 
			}
			//
			return new XYIData2D(x, y, z);
		}
	} 

	private double distPercent(double width, double x) {
		return (width/2-x)/width*2;
	}

	public XYIData2D toXYIArray() {
		ScanLine[] lines = this.lines;
		// Erst Messpunkteanzahl ausrechnen 
		int scanpoints = 0;
		for(int i=0; i<data.getLinesCount(); i++) {
			scanpoints += lines[i].getData().length;
		}
		// Datenerstellen
		double[] x = new double[scanpoints];
		double[] y = new double[scanpoints];
		double[] z = new double[scanpoints];
		//
		ScanLine line; 
		int currentdp = 0;
		//
		for(int iy=0; iy<data.getLinesCount(); iy++) {
			line = lines[iy];
			//
			for(int ix=0; ix<line.getData().length; ix++) {

				// x = time; NOT distance; so calc
				x[currentdp] = Double.valueOf(String.valueOf(getXProcessed(iy, ix)));
				y[currentdp] = Double.valueOf(String.valueOf(getYProcessed(iy)));
				z[currentdp] = Double.valueOf(getIProcessed(iy, ix));
				currentdp++;
			} 
		}
		//
		return new XYIData2D(x, y, z);
	}
	public XYIData2D toXYIArrayRaw() {
		// Erst Messpunkteanzahl ausrechnen 
		int scanpoints = 0;
		for(int i=0; i<data.getLinesCount(); i++) {
			scanpoints += lines[i].getData().length;
		}
		// Datenerstellen
		double[] x = new double[scanpoints];
		double[] y = new double[scanpoints];
		double[] z = new double[scanpoints];
		//
		ScanLine line; 
		int currentdp = 0;
		//
		for(int iy=0; iy<data.getLinesCount(); iy++) {
			line = lines[iy];
			//
			for(int ix=0; ix<line.getData().length; ix++) {

				// x = time; NOT distance; so calc
				x[currentdp] = Double.valueOf(String.valueOf(line.getData()[ix].getX()));
				y[currentdp] = Double.valueOf(String.valueOf((iy)));
				z[currentdp] = Double.valueOf(String.valueOf(line.getData()[ix].getI()));
				currentdp++;
			} 
		}
		//
		return new XYIData2D(x, y, z);
	}
	/**
	 * all intensities as one array
	 * @return float intensity Array
	 */
	public double[] toIArray() {
		// calc count of points
		int scanpoints = 0;
		for(int i=0; i<data.getLinesCount(); i++) {
			scanpoints += lines[i].getData().length;
		} 
		double[] z = new double[scanpoints];
		//
		ScanLine line; 
		int currentdp = 0;
		//
		for(int iy=0; iy<data.getLinesCount(); iy++) {
			line = lines[iy];
			//
			for(int ix=0; ix<line.getData().length; ix++) {
				// x = time; NOT distance; so calc 
				z[currentdp] =  getIProcessed(iy, ix);
				currentdp++;
			} 
		}
		//
		return z;
	}


	/**
	 * returns [rows][columns] of xyz data processed or not processed
	 * @param sett
	 * @return
	 */
	public Object[][] toXYIArray(SettingsImage2DDataExport sett) {
		XYIData2D data = sett.isExportRaw()?  toXYIArrayRaw() : toXYIArray();

		Object[][] real = new Object[data.getI().length][3];
		for(int i=0; i<real.length; i++) {
			real[i][0] = data.getX()[i];
			real[i][1] = data.getY()[i];
			real[i][2] = data.getI()[i];
		}
		return real;
	}

	//###############################################################################################
	/**
	 * returns [rows][columns]
	 * @param sett
	 * @return
	 */
	public Object[][] toDataArray(SettingsImage2DDataExport sett) {
		return sett.isExportRaw()? toDataArrayRaw(sett) : toDataArrayProcessed(sett);
	}
	/**
	 * 
	 * @param sett
	 * @return
	 */
	public Object[][] toDataArrayRaw(SettingsImage2DDataExport sett) {
		// time only once?
		int cols = sett.isWriteTimeOnlyOnce()? data.getLinesCount() +1 :  data.getLinesCount()*2;
		if(sett.isWriteNoX()) cols = data.getLinesCount();
		int rows = getMaxDP();
		Object[][] data = new Object[rows][cols];
		int l = 0;
		for(int c=0; c<cols; c++) {
			ScanLine line = lines[l];
			// increment l
			if((sett.isWriteTimeOnlyOnce() && c!=0) || (!sett.isWriteTimeOnlyOnce() && c%2==1) || (sett.isWriteNoX())) {
				for(int r = 0; r<rows; r++) {
					// only if not null
					data[r][c] = r<line.getDPCount()? line.getPoint(r).getI() : "";
				}
				l++;
			}
			else //write X
				for(int r = 0; r<rows; r++) 
					data[r][c] = r<line.getDPCount()? getLine(l).getPoint(r).getX() : "";
		}
		return data;
	}

	/**
	 * Returns the processed intensity only
	 * @param sett
	 * @return
	 */
	public Object[][] toIArrayProcessed() {
		// time only once?
		int cols = data.getLinesCount();
		int rows = getMaxDP();
		Object[][] data = new Object[rows][cols]; 
		for(int c=0; c<cols; c++) {
			ScanLine line = lines[c];
			// increment l
				for(int r = 0; r<rows; r++) {
					// only if not null: write Intensity
					data[r][c] = r<line.getDPCount()? getIProcessed(c,r) : "";
				} 
		}
		return data;
	} 
	/**
	 * Returns the processed intensity only.
	 * with boolean map
	 * @param sett
	 * @return
	 */
	public Object[][] toIArrayProcessed(boolean[][] map) {
		// time only once?
		int rows = data.getLinesCount();
		int cols = getMaxDP();
		Object[][] data = new Object[rows][cols]; 
		for(int r = 0; r<rows; r++) {
			ScanLine line = lines[r];
			for(int c=0; c<cols; c++) {
			// increment l 
					// only if not null: write Intensity
					boolean state = r<map.length && c<map[r].length && map[r][c];
					data[r][c] = c<line.getDPCount() && state? getIProcessed(r,c) : "";
				} 
		}
		return data;
	}
	/**
	 * Returns the processed intensity and xy
	 * @param sett
	 * @return
	 */
	public Object[][] toDataArrayProcessed(SettingsImage2DDataExport sett) {
		// time only once?
		int cols = sett.isWriteTimeOnlyOnce()? data.getLinesCount() +1 :  data.getLinesCount()*2;
		if(sett.isWriteNoX()) cols = data.getLinesCount();
		int rows = getMaxDP();
		Object[][] data = new Object[rows][cols];
		int l = 0;
		for(int c=0; c<cols; c++) {
			ScanLine line = lines[l];
			// increment l
			if((sett.isWriteTimeOnlyOnce() && c!=0) || (!sett.isWriteTimeOnlyOnce() && c%2==1) || (sett.isWriteNoX())) {
				for(int r = 0; r<rows; r++) {
					// only if not null: write Intensity
					data[r][c] = r<line.getDPCount()? getIProcessed(l,r) : "";
				}
				l++;
			}
			else //write X
				for(int r = 0; r<rows; r++) 
					data[r][c] = r<line.getDPCount()? getXProcessed(l, r) : "";
		}
		return data;
	}
	/**
	 * Returns the processed intensity and xy
	 * @param sett
	 * @return
	 */
	public Object[][] toDataArrayProcessed(SettingsImage2DDataExport sett, boolean blank, boolean IS, boolean quantifier) {
		// all lines are full? or is the first/last one only half
		int startLine = 0;
		int lcount = data.getLinesCount();
		if(sett.isCuttingDataToMin()) {
			int avglength = data.getAvgDP();
			// have a look at the first and last line
			if(data.getLineLength(0)<avglength*0.80) {
				startLine = 1;
				lcount--;
			}
			if(data.getLineLength(data.getLinesCount()-1)<avglength*0.80)
				lcount--;
		}
		// time only once?
		int cols = sett.isWriteTimeOnlyOnce()? lcount +1 :  lcount*2;
		if(sett.isWriteNoX()) cols = lcount;
		// how many rows? maximum or cutting to minimum
		int minDP = data.getMinDP();
		int rows = !sett.isCuttingDataToMin() || minDP<=1? data.getMaxDP() : minDP;
		
		Object[][] dataRes = new Object[rows][cols];
		int l = startLine;
		for(int c=0; c<cols; c++) {
			// increment l
			if((sett.isWriteTimeOnlyOnce() && c!=0) || (!sett.isWriteTimeOnlyOnce() && c%2==1) || (sett.isWriteNoX())) {
				for(int r = 0; r<rows; r++) {
					// only if not null: write Intensity
					dataRes[r][c] = r<data.getLineLength(l)? getIProcessed(l,r, blank, IS, quantifier) : "";
				}
				l++;
			}
			else //write X
				for(int r = 0; r<rows; r++) 
					dataRes[r][c] = r<data.getLineLength(l)? getXProcessed(l, r) : "";
		}
		return dataRes;
	}

	/**
	 * The processed intensity.
	 * blank reduced, internal standard normalization and quantification
	 * @param l
	 * @param dp
	 * @return
	 */
	public double getIProcessed(int l, int dp) { 
		// check for update in parent i processing
		checkForUpdateInParentIProcessing();
		// TODO prozess intinsity
		double i = data.getI(l, dp);
		// subtract blank and apply IS
		if(operations!=null) {
			i = operations.calcIntensity(this, l, dp, i);
		}
		// quantify
		if(quantifier!=null && quantifier.isActive())  {
			return quantifier.calcIntensity(this, l, dp, i); 
		}
		else return i;
	}
	/** 
	 * The processed intensity.
	 * blank reduced, internal standard normalization and quantification
	 * @param l
	 * @param dp
	 * @param blank
	 * @param IS
	 * @param quantify
	 * @return
	 */
	public double getIProcessed(int l, int dp, boolean blank, boolean IS, boolean quantify) { 
		// check for update in parent i processing
		checkForUpdateInParentIProcessing();
		// TODO prozess intinsity
		double i = data.getI(l, dp);
		// subtract blank and apply IS
		if(operations!=null) {
			i = operations.calcIntensity(this, l, dp, i, blank, IS);
		}
		// quantify
		if(quantifier!=null && quantify)  {
			boolean tmp = quantifier.isActive();
			quantifier.setActive(true);
			i = quantifier.calcIntensity(this, l, dp, i); 
			quantifier.setActive(tmp);
		}
		return i;
	}
	/**
	 * The processed x
	 * @param l
	 * @param dp
	 * @return
	 */
	public float getXProcessed(int l, int dp) { 
		int line = l<data.getLinesCount()? l:data.getLinesCount()-1;
		if(dp<data.getLineLength(line))
			return data.getX(line, dp) * settImage.getVelocity();
		else {
			// TODO WHAT?! Cant understand this
			// is the program ending at this poitn? at any time?
			int overMax = (data.getLineLength(line)-dp+1);
			ImageEditorWindow.log("ask for a dp>then line in getXProcessed", LOG.DEBUG);
			//return (((data.getX(line, data.getLineLength(line)-1) + getLine(line).getWidthDP()*overMax) * settImage.getVelocity()));
			// tmp change
			return (((data.getX(line, data.getLineLength(line)-1)) * settImage.getVelocity()));
		}
	}
	/**
	 * The processed y
	 * @param l
	 * @param dp
	 * @return
	 */
	public float getYProcessed(int l) { 
		return l*settImage.getSpotsize();
	}
	// finished processed data
	//######################################################################
	// get index from processed data (x/y)
	/**
	 * returns the index of the line representing y
	 * @param y
	 * @return
	 */
	public int getYAsIndex(double y) {
		if(y<0) return 0;
		int l = (int)(y/settImage.getSpotsize());
		return l<data.getLinesCount()? l : data.getLinesCount()-1;
	} 
	/**
	 * returns the index of the data point in the given line 
	 * @param line is an integer index
	 * @param x is the coordinate (processed)
	 * @return
	 */
	public int getXAsIndex(int line, double x) {
		double rx = x/settImage.getVelocity();
		for(int i=1; i<data.getLineLength(line); i++) {
			if(data.getX(line, i)>=rx)
				return i-1;
		}
		return data.getLineLength(line)-1;
	}
	/**
	 * returns the index of the data point at x / y
	 * @param y is the coordinate (processed)
	 * @param x is the coordinate (processed)
	 * @return
	 */
	public int getXAsIndex(double y, double x) {
		return getXAsIndex(getYAsIndex(y), x);
	}


	// a name for lists
	public String toListName() { 
		return settImage.toListName();
	} 

	@Override
	public String toString() {
		return toListName();
	}

	public String getTitle() { 
		return settImage.getTitle();
	} 

	//#########################################################################################################
	// GETTER AND SETTER
	/**
	 * 
	 * @param settings any image settings
	 */
	public void setSettings(Settings settings) {
		try {
			if(settings== null)
				return;
			// TODO --> set all settings in one: 
			// TODO --> complete!!!
			if(SettingsPaintScale.class.isAssignableFrom(settings.getClass())) 
				setSettPaintScale((SettingsPaintScale) settings);
			else if(SettingsImage.class.isAssignableFrom(settings.getClass())) 
				setSettImage((SettingsImage) settings);
			else if(SettingsThemes.class.isAssignableFrom(settings.getClass())) 
				setSettTheme((SettingsThemes) settings);
			else if(SettingsImage2DOperations.class.isAssignableFrom(settings.getClass()))  {
				setOperations((SettingsImage2DOperations) settings);
				fireIntensityProcessingChanged();
			}
			else if(SettingsImage2DQuantifier.class.isAssignableFrom(settings.getClass())) {
				setQuantifier((SettingsImage2DQuantifier) settings);
				fireIntensityProcessingChanged();
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	} 
	public Settings getSettingsByClass(Class classsettings) {
		// TODO -- add other settings here
		if(SettingsPaintScale.class.isAssignableFrom(classsettings)) 
			return getSettPaintScale();
		else if(SettingsImage.class.isAssignableFrom(classsettings)) 
			return getSettImage();
		else if(SettingsThemes.class.isAssignableFrom(classsettings)) 
			return getSettTheme();
		else if(SettingsImage2DOperations.class.isAssignableFrom(classsettings)) 
			return getOperations();
		else if(SettingsImage2DQuantifier.class.isAssignableFrom(classsettings)) 
			return getQuantifier();
		return null;
	}

	/**
	 * Given image img will be setup like this image
	 * @param img will get all settings from master image
	 */
	public void applySettingsToOtherImage(Image2D img) {
		try {
			// save name and path
			String name = img.getTitle();
			String path = img.getSettImage().getRAWFilepath();
			// copy all TODO
			img.setSettImage(BinaryWriterReader.deepCopy(this.getSettImage()));
			img.setSettPaintScale((BinaryWriterReader.deepCopy(this.getSettPaintScale())));
			img.setSettTheme(BinaryWriterReader.deepCopy(this.getSettTheme()));
			img.setOperations(BinaryWriterReader.deepCopy(this.getOperations()));
			img.setQuantifier(BinaryWriterReader.deepCopy(this.getQuantifier()));
			// set name and path
			img.getSettImage().setTitle(name);
			img.getSettImage().setRAWFilepath(path);
		} catch (Exception e) { 
			e.printStackTrace();
		}
	}


	/**
	 * minimum intensity processed 
	 * @return
	 */
	public double getMinIntensity(boolean onlySelected) {
		// check for update in parent i processing
		checkForUpdateInParentIProcessing();
		if(onlySelected) {
			double min =  Double.POSITIVE_INFINITY;

			for(int l=0; l<data.getLinesCount(); l++) {
				for(int dp = 0; dp<data.getLineLength(l); dp++) {
					double tmp;
					if((!isExcludedDP(l, dp) && isSelectedDP(l, dp) && (tmp = getIProcessed(l, dp))<min)) {
						min = tmp;
					}
				}
			}
			return min!=Double.POSITIVE_INFINITY? min : -1;
		}
		else {
			//
			if(minZ==-1) {
				// calc min z
				minZ = Double.POSITIVE_INFINITY;
				double pi;
				for(int line=0; line<data.getLinesCount(); line++)
					for(int dp=0; dp<data.getLineLength(line); dp++)
						if((pi=getIProcessed(line, dp))<minZ)
							minZ = pi;
			}
			return minZ!=Double.POSITIVE_INFINITY? minZ : -1;
		}
	}
	/**
	 * maximum intensity processed 
	 * @return
	 */
	public double getMaxIntensity(boolean onlySelected) {
		// check for update in parent i processing
		checkForUpdateInParentIProcessing();
		if(onlySelected) { 
			double max =  Double.NEGATIVE_INFINITY;

			for(int l=0; l<data.getLinesCount(); l++) {
				for(int dp = 0; dp<data.getLineLength(l); dp++) {
					double tmp;
					if((!isExcludedDP(l, dp) && isSelectedDP(l, dp) && (tmp = getIProcessed(l, dp))>max)) {
						max = tmp;
					}
				}
			}
			if(max == Double.NEGATIVE_INFINITY)
				max = -1;
			return max;
		}
		else { 
			//
			if(maxZ==-1) {
				// calc min z
				maxZ = Double.NEGATIVE_INFINITY;
				double pi;
				for(int line=0; line<data.getLinesCount(); line++)
					for(int dp=0; dp<data.getLineLength(line); dp++)
						if((pi=getIProcessed(line, dp))>maxZ)
							maxZ = pi; 
			}
			if(maxZ == Double.NEGATIVE_INFINITY)
				maxZ = -1;
			return maxZ;
		}
	}
	
	/** 
	 * max for line
	 * @param l
	 * @param b
	 * @return
	 */
	public double getMaxIntensity(int l, boolean onlySelected) {
		// check for update in parent i processing
		checkForUpdateInParentIProcessing();
		
		double maxZ = Double.NEGATIVE_INFINITY;
		double pi; 
			for(int dp=0; dp<data.getLineLength(l); dp++)
				if((pi=getIProcessed(l, dp))>maxZ)
					maxZ = pi; 
		return maxZ!=Double.NEGATIVE_INFINITY? maxZ : -1;
	}


	public float getMaxXProcessed(int line) {
		return getXProcessed(line, data.getLineLength(line));
	} 
	public float getMaxYProcessed() {
		return getYProcessed(data.getLinesCount());
	}

	/**
	 * maximum block width for renderer
	 * = distance between one and next block
	 * @return
	 */
	public double getMaxBlockWidth(int rotation) {
		if(rotation!=0 && rotation!=180) return getMaxBlockHeight(0);
		else {
			return data.getMaxXWidth()*getSettImage().getVelocity();
		}
	}
	/**
	 * maximum block height for renderer
	 * = distance between one and next block in lines
	 * @return
	 */
	public double getMaxBlockHeight(int rotation) {
		if(rotation!=0 && rotation!=180) return getMaxBlockWidth(0);
		else { 
			return getSettImage().getSpotsize();
		}
	}

	//#############################################################
	// apply filter to cut off first or last values of intensity
	// only apply if not already done
	private double lastAppliedMinFilter=-1, lastAppliedMaxFilter = -1;
	public void applyCutFilterMin(double f) {
		if(f!=lastAppliedMinFilter) {
			lastAppliedMinFilter = f;
			// apply filter
			//sort all z values
			double[] z = toIArray();
			Arrays.sort(z);
			// cut off percent f/100.f
			int size = z.length-1;
			// save in var
			minZFiltered =  z[(int)(size*f/100.0)];
		}
	}
	public void applyCutFilterMax(double f) {
		if(f!=lastAppliedMaxFilter) {
			lastAppliedMaxFilter = f;
			// apply filter
			//sort all z values
			double[] z = toIArray();
			Arrays.sort(z);
			// cut off percent f/100.f
			int size = z.length-1;
			// save in var --> cut from max 1-p
			maxZFiltered = z[size-(int)(size*f/100.0)];
		}
	}

	//##################################################################################### 
	// Basic funcitons filling images 

	// END of basics
	//#####################################################################################

	/**
	 * 
	 * @return intensity span
	 */
	public double getIntensitySpan(boolean onlySelected) {
		return getMaxIntensity(onlySelected)-getMinIntensity(onlySelected);
	} 
	public SettingsPaintScale getSettPaintScale() {
		return settPaintScale;
	} 
	public void setSettPaintScale(SettingsPaintScale settPaintScale) {
		this.settPaintScale = settPaintScale;
	} 
	public SettingsImage getSettImage() {
		return settImage;
	} 
	public void setSettImage(SettingsImage settImgLaser) {
		this.settImage = settImgLaser;
	} 
	public SettingsThemes getSettTheme() {
		return settTheme;
	}
	public void setSettTheme(SettingsThemes settTheme) {
		this.settTheme = settTheme;
	}
	public double getMinZFiltered() {
		return minZFiltered;
	}
	public double getMaxZFiltered() {
		return maxZFiltered;
	} 

	// if something changes - change the averageI
	public SettingsImage2DQuantifier getQuantifier() {
		return quantifier;
	}
	public void setQuantifier(SettingsImage2DQuantifier quantifier) {
		this.quantifier = quantifier;
	}
	public SettingsImage2DQuantifierIS getInternalQuantifierIS() {
		return getOperations().getInternalQuantifier();
	}
	public void setInternalQuantifierIS(SettingsImage2DQuantifierIS isQ) {
		getOperations().setInternalQuantifier(isQ);
	}
	public SettingsImage2DOperations getOperations() {
		return operations;
	}
	public void setOperations(SettingsImage2DOperations operations) {
		this.operations = operations;
		operations.getBlankQuantifier().getQSameImage().setImg(this);
	}
	/**
	 * Calcs the average I for this img
	 * @return
	 */
	public double getAverageIProcessed() {
		// check for update in parent i processing
		checkForUpdateInParentIProcessing();
		//
		if(averageIProcessed==-1) { 
			int counter = 0;
			averageIProcessed = 0;
			for(int line=0; line<data.getLinesCount(); line++) {
				for(int dp=0; dp<data.getLineLength(line); dp++) {
					averageIProcessed += getIProcessed(line, dp);
					counter++;
				}
			}
			averageIProcessed = averageIProcessed/counter;
		}
		return averageIProcessed;
	}
	// vars for that:
	private double[] averageIProcessedForLine;
	/**
	 * calcs the average I for a line
	 * @param line
	 * @return
	 */
	public double getAverageIProcessed(int line) {
		// check for update in parent i processing
		checkForUpdateInParentIProcessing();
		//
		if(averageIProcessedForLine==null) {
			averageIProcessedForLine = new double[data.getLinesCount()];
			for(int i=0; i<data.getLinesCount(); i++) {
				averageIProcessedForLine[i] = 0;
				for(int dp=0; dp<data.getLineLength(i); dp++)  
					averageIProcessedForLine[i] += getIProcessed(i, dp);
				averageIProcessedForLine[i] = averageIProcessedForLine[i]/data.getLineLength(i);
			}
		}
		return averageIProcessedForLine[line];
	} 
	/**
	 * checks if there is a parent that has changed I processing
	 * then it changes the processing here
	 */
	private void checkForUpdateInParentIProcessing() {
		if(parent!=null && parent.getLastIProcChangeTime()!=lastIProcChangeTime) {
			fireIntensityProcessingChanged();
			lastIProcChangeTime = parent.getLastIProcChangeTime();
		}
	}
	/**
	 * save lastIProcChangeTime for comparison in all quantifiers.
	 * update all quantifiers if it has changed
	 */
	public void fireIntensityProcessingChanged() { 
		//for(IntensityProcessingChangedListener l : listenerProcessingChanged)
		//	l.fireIntensityProcessingChanged();

		// gives a indirect signal to Quantifier and children to change iProc
		lastIProcChangeTime++;
		if(lastIProcChangeTime>=Integer.MAX_VALUE-1)
			lastIProcChangeTime = 0;

		averageIProcessed = -1;
		minZ = -1;
		maxZ = -1;
		// applyCutFilter?
		// IS
		SettingsImage2DQuantifierIS internalQ = getInternalQuantifierIS();
		if(internalQ!=null && internalQ.getImgIS()!=null) { 
			if(internalQ.getImgIS().getOperations()==null)
				internalQ.getImgIS().setOperations(new SettingsImage2DOperations());
			internalQ.getImgIS().getOperations().setBlankQuantifier(getOperations().getBlankQuantifier());
			internalQ.getImgIS().fireIntensityProcessingChanged();
		}
	}
	public Vector<RectSelection> getSelectedData() {
		if(selectedData==null)
			selectedData = new Vector<RectSelection>();
		return selectedData;
	}
	public void setSelectedData(Vector<RectSelection> selectedData) {
		this.selectedData = selectedData;
		fireIntensityProcessingChanged();
	}
	public Image2D getParent() {
		return parent;
	}
	/**
	 * all child will point at settings from parent. 
	 * @param parent 
	 */
	public void setParent(Image2D parent) {
		if(parent==null) {
			// copy all settings  
			this.applySettingsToOtherImage(this);
		}
		else if(parent!=this.parent) {
			// point at the settings of parent TODO
			this.setSettImage(parent.getSettImage());
			this.setSettPaintScale((parent.getSettPaintScale()));
			this.setSettTheme(parent.getSettTheme());
			this.setOperations(parent.getOperations());
			this.setQuantifier(parent.getQuantifier());
			fireIntensityProcessingChanged();
		}
		// set parent 
		this.parent = parent;
	}
	public Vector<RectSelection> getExcludedData() {
		if(excludedData==null)
			excludedData = new Vector<RectSelection>();
		return excludedData;
	}
	public void setExcludedData(Vector<RectSelection> excludedData) {
		this.excludedData = excludedData;
		fireIntensityProcessingChanged();
	}
	/**
	 * Sums up the total dp count
	 * @return
	 */
	public int getTotalDPCount() { 
		return data.getTotalDPCount();
	}
	/**
	 * Sums up all the selected data with optional exclusion
	 * @param excluded defines whether to exclude or not
	 * @return 
	 */
	public int getSelectedDPCount(boolean excluded) {
		int counter = 0;
		for(int l=0; l<data.getLinesCount(); l++) { 
			for(int dp = 0; dp<data.getLineLength(l); dp++) {
				if((!excluded || !isExcludedDP(l, dp)) && isSelectedDP(l, dp)) 
					counter++;
			}
		}
		return counter;
	}
	
	/**
	 * Returns all selected and not excluded data points to an array
	 * @return
	 */
	public double[] getSelectedDataAsArray(boolean excluded) {
		double[] datasel = new double[getSelectedDPCount(true)];
		int counter = 0;
		for(int l=0; l<data.getLinesCount(); l++) {
			for(int dp = 0; dp<data.getLineLength(l); dp++) {
				if((!excluded || !isExcludedDP(l, dp)) && isSelectedDP(l, dp))  {
					datasel[counter] = getIProcessed(l, dp);
					counter++;
				} 
			}
		}
		return datasel;
	}

	/**
	 * checks if a dp is excluded by a rect in excluded list
	 * @param l
	 * @param dp
	 * @return
	 */
	public boolean isExcludedDP(int l, int dp) { 
		if(getExcludedData()==null || getExcludedData().size()==0)
			return false;
		// check if its in a exclude rect
		for(RectSelection e : this.getExcludedData()) {
			// set to isDouble to prevent adding
			if(e.contains(dp, l)) {
				return true; 
			}
		}
		return false;
	}
	/**
	 * checks if a dp is selected (if there are no selected rects - it will always return true
	 * @param l line
	 * @param dp datapoint
	 * @return
	 */
	public boolean isSelectedDP(int l, int dp) {
		if(selectedData==null || selectedData.size()==0)
			return true;
		// check if its in a selected rect
		for(RectSelection e : this.getSelectedData()) {
			// set to isDouble to prevent adding
			if(e.contains(dp, l)) {
				return true; 
			}
		}
		return false; 
	}

	/**
	 * min max avg in this rect
	 * @param rect
	 * @return
	 */
	public DataMinMaxAvg analyzeDataInRect(RectSelection rect) {
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		double avg = 0;
		for(int y=rect.getMinY(); y<=rect.getMaxY(); y++) {
			for(int x=rect.getMinX(); x<=rect.getMaxX(); x++) { 
				if(rect.getMode()!=MODE.MODE_SELECT || !isExcludedDP(y, x)) {
					double pi = getIProcessed(y, x);
					if(pi<min) min = pi;
					if(pi>max) max = pi;
					avg += pi;
				}
			}
		}
		avg = avg/(rect.getWidth()*rect.getHeight());
		// stdev 
		double stdev = 0;
		int n = 0;
		for(int y=rect.getMinY(); y<=rect.getMaxY(); y++) {
			for(int x=rect.getMinX(); x<=rect.getMaxX(); x++) {
				if(rect.getMode()!=MODE.MODE_SELECT || !isExcludedDP(y, x)) {
					double pi = getIProcessed(y, x);
					stdev += Math.pow(pi-avg, 2);
					n++;
				}
			}
		}
		// calc stdev
		stdev = Math.sqrt(stdev/(n-1));
		// return 
		return new DataMinMaxAvg(min, max, avg, stdev);
	}
	
	/**
	 * returns the intensity of the perc data point
	 * @param rect
	 * @param perc between 0 and 1.0
	 * @return
	 */
	public double analyzePercentile(RectSelection rect, double perc) { 
		double[] i = getIProcessedRect(rect);
		if(i!=null && i.length>0) {
			Arrays.sort(i);
		
			return i[(int)((i.length-1)*perc)];
		}
		else return -1;
	}
	

	
	/**
	 * returns the intensity array for this rect
	 * @param rect 
	 * @return
	 */
	public double[] getIProcessedRect(RectSelection rect) {
		int size = rect.getWidth()*rect.getHeight();

		if(rect.getMode()==MODE.MODE_SELECT)
			size = countSelectedNonExcludedDPInRect(rect);
		
		double[] i = new double[size];
		//TODO sort in ar right way
		int counter = 0;
		for(int y=rect.getMinY(); y<=rect.getMaxY(); y++) {
			for(int x=rect.getMinX(); x<=rect.getMaxX(); x++) {
				if(rect.getMode()!=MODE.MODE_SELECT || !isExcludedDP(y, x)) {
					i[counter] = getIProcessed(y, x);
					counter++;
				}
			}
		}
		return i;
	}
	
	public int countSelectedNonExcludedDPInRect(RectSelection rect) {
		int size = 0; 
		for(int y=rect.getMinY(); y<=rect.getMaxY(); y++) {
			for(int x=rect.getMinX(); x<=rect.getMaxX(); x++) { 
				if(!isExcludedDP(y, x))
					size++;
			}
		}
		return size;
	}

	/**
	 * returns the 2d intensity array for this rect
	 * @param rect 
	 * @return data[row][column]
	 */
	public double[][] getIProcessedRect2D(RectSelection rect) {
		double[][] i = new double[rect.getHeight()][rect.getWidth()];
		//TODO sort in ar right way        
		for(int y=rect.getMinY(); y<=rect.getMaxY(); y++) {
			for(int x=rect.getMinX(); x<=rect.getMaxX(); x++) {
				if(rect.getMode()!=MODE.MODE_SELECT || !isExcludedDP(y, x))
					i[y-rect.getMinY()][x-rect.getMinX()] = getIProcessed(y, x);
			}
		}
		return i;
	}
	
	//
	public int getLastIProcChangeTime() {
		return lastIProcChangeTime;
	}
	/**
	 * Copies the Image2D and sets this to parent
	 * settings will be connected
	 * @return
	 * @throws Exception 
	 */
	public Image2D getCopyChild() throws Exception { 
		Image2D copy = BinaryWriterReader.deepCopy(this);
		copy.setParent(this);
		return copy;
	}
	/**
	 * Copies the Image2D
	 * @return
	 * @throws Exception 
	 */
	public Image2D getCopy() throws Exception { 
		return BinaryWriterReader.deepCopy(this);  
	}
	public boolean isAllLinesSameLength() { 
		for(int i=1; i<data.getLinesCount(); i++) 
			if(data.getLineLength(i-1)!=data.getLineLength(i))
				return false;
		
		return true;
	}
	public Vector<RectSelection> getInfoData() {
		if(infoData==null)
			infoData = new Vector<RectSelection>();
		return infoData;
	}
	public void setInfoData(Vector<RectSelection> infoData) {
		this.infoData = infoData;
	}
	/**
	 * returns an easy icon
	 * @param maxw
	 * @param maxh
	 * @return
	 */
	public Icon getIcon(int maxw) {
		try {
			int maxh = 18;
	        PaintScale scale = PaintScaleGenerator.generateStepPaintScale(getMinIntensity(false), getMaxIntensity(false), getSettPaintScale()); 
			BufferedImage img = new BufferedImage(maxw, maxh, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = img.createGraphics();
			
			// scale in x
			float sx = 1;
			int w = data.getMinDP();
			if(w>maxw) {
				sx = w/maxw;
				w = maxw;
			}
			
			float sy = 1;
			int lines = data.getLinesCount();
			int h = lines;
			if(h>maxh) { 
				sy = h/maxh;
				h = maxh;
			}	
			
			for(int x=0; x<w; x++) {
				for(int y=0; y<h; y++) {
					// fill rects
					int dp = data.getLineLength((int)(y*sy));
					if((int)(y*sy)<lines && (int)(x*sx)<dp) {
						Paint c = scale.getPaint(getIProcessed((int)(y*sy), (int)(x*sx)));
						g.setPaint(c);
						g.fillRect(x, maxh-y, 1, 1); 
					}
				}
			}
			
			return  new ImageIcon(img); 
		} catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	public int getLineCount() {
		return data.getLinesCount();
	}
	
	/**
	 * test images
	 * @return
	 */
	public static Image2D createTestStandard() {
		Random rand = new Random(System.currentTimeMillis());
		ScanLine[] lines = new ScanLine[24];
		for(int l=0; l<lines.length; l++) { 
			DataPoint2D[] dp = new DataPoint2D[240];
			for(int d=0; d<dp.length; d++) {
				// middle the highest
				double in = (int)(l/4)*200.0;
				in += Math.abs(rand.nextInt(6000)/100.0);
				// create dp
				dp[d] = new DataPoint2D(d*0.1, in);
			}
			lines[l] = new ScanLine(dp);
		}
		Dataset2D data = new Dataset2D(lines);
		return new Image2D(data);
	}
	
	//########################################################################
	// listener
	/**
	 * raw data changes by:
	 * direct imaging, 
	 */
	public void fireRawDataChangedEvent() {
		if(rawDataChangedListener!=null) {
			for(RawDataChangedListener l : rawDataChangedListener)
				l.rawDataChangedEvent(this);
		}
	}
	/**
	 * raw data changes by:
	 * direct imaging,
	 * @param listener
	 */
	public void addRawDataChangedListener(RawDataChangedListener listener) {
		if(rawDataChangedListener==null) rawDataChangedListener = new Vector<RawDataChangedListener>();
		rawDataChangedListener.add(listener);
	}
	public void removeRawDataChangedListener(RawDataChangedListener list) {
		if(rawDataChangedListener!=null)
			rawDataChangedListener.remove(list);
	}
	public void cleatRawDataChangedListeners() {
		if(rawDataChangedListener!=null)
			rawDataChangedListener.clear();
	}
}
