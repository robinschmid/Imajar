package net.rs.lamsi.general.datamodel.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import net.rs.lamsi.general.datamodel.image.data.multidimensional.DatasetContinuousMD;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.DatasetMD;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.ScanLineMD;
import net.rs.lamsi.general.datamodel.image.data.twodimensional.DataPoint2D;
import net.rs.lamsi.general.datamodel.image.data.twodimensional.Dataset2D;
import net.rs.lamsi.general.datamodel.image.data.twodimensional.ScanLine2D;
import net.rs.lamsi.general.datamodel.image.data.twodimensional.XYIData2D;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.datamodel.image.interf.ImageDataset;
import net.rs.lamsi.general.datamodel.image.listener.RawDataChangedListener;
import net.rs.lamsi.massimager.Heatmap.PaintScaleGenerator;
import net.rs.lamsi.massimager.MyMZ.MZChromatogram;
import net.rs.lamsi.massimager.MyOES.OESElementLine;
import net.rs.lamsi.massimager.MyOES.OESScan;
import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.massimager.Settings.image.SettingsImage2D;
import net.rs.lamsi.massimager.Settings.image.operations.SettingsImage2DOperations;
import net.rs.lamsi.massimager.Settings.image.operations.quantifier.SettingsImage2DQuantifier;
import net.rs.lamsi.massimager.Settings.image.operations.quantifier.SettingsImage2DQuantifierIS;
import net.rs.lamsi.massimager.Settings.image.sub.SettingsGeneralImage;
import net.rs.lamsi.massimager.Settings.image.sub.SettingsImageContinousSplit;
import net.rs.lamsi.massimager.Settings.image.visualisation.SettingsPaintScale;
import net.rs.lamsi.massimager.Settings.image.visualisation.SettingsPaintScale.ValueMode;
import net.rs.lamsi.massimager.Settings.image.visualisation.SettingsThemes;
import net.rs.lamsi.massimager.Settings.importexport.SettingsImage2DDataExport;
import net.rs.lamsi.massimager.Settings.importexport.SettingsImageDataImportTxt.ModeData;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow.LOG;
import net.rs.lamsi.multiimager.Frames.dialogs.selectdata.DataMinMaxAvg;
import net.rs.lamsi.multiimager.Frames.dialogs.selectdata.Image2DSelectDataAreaDialog.MODE;
import net.rs.lamsi.multiimager.Frames.dialogs.selectdata.RectSelection;
import net.rs.lamsi.utils.mywriterreader.BinaryWriterReader;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;

// XY raw data! 
// have to be multiplied with velocity and spot size
public class Image2D implements Serializable, Collectable2D {	 
	// do not change the version!
	private static final long serialVersionUID = 1L;

	// image group: Multi dimensional data sets create an ImageGroupMD
	// this controls image operations
	protected ImageGroupMD imageGroup = null;

	// Parent image with master settings
	protected Image2D parent;

	// image has nothing to do with quantifier class! so dont use a listener for data processing changed events TODO
	//protected Vector<IntensityProcessingChangedListener> listenerProcessingChanged = new Vector<IntensityProcessingChangedListener>();
	// intensityProcessingChanged? save lastIProcChangeTime and compare with one from quantifier class
	protected int lastIProcChangeTime = 0;

	//############################################################
	// Settings
	protected SettingsImage2D settings;


	//############################################################
	// data
	protected ImageDataset data;
	// index of image in data set: (multidimensional data set)
	protected int index=0;


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

	public Image2D() { 
		settings = new SettingsImage2D();
	}

	public Image2D(SettingsImage2D settings) {
		super();
		this.settings = settings;
		setSettPaintScale(settings.getSettPaintScale());
	}
	
	public Image2D(ImageDataset data) {
		this();
		this.data = data; 
	}
	public Image2D(ImageDataset data, int index) {
		this();
		this.data = data; 
		this.index = index;
	}
	public Image2D(ImageDataset data, int index, SettingsImage2D sett) { 
		this(sett);
		this.data = data; 
		this.index = index;
	} 
	public Image2D(ImageDataset data, SettingsImage2D sett) { 
		this(data, 0, sett);
	}   

	//#####################################################################################################
	// Generators STATIC
	// MZChrom Generation
	// MS image from raw data
	public static Image2D generateImage2DFrom(SettingsPaintScale settPaintScale, SettingsGeneralImage setImage, MZChromatogram[] mzchrom) {
		// TODO mehrere MZ machen 
		// Alle Spec
		// Erst Messpunkteanzahl ausrechnen 
		// lines listLines
		Vector<ScanLineMD> listLines = new Vector<ScanLineMD>(mzchrom.length);
		// Lines durchgehen
		for(int i=0; i<mzchrom.length; i++) { 
			int scanpoints = mzchrom[i].getItemCount();
			float startTime = mzchrom[i].getX(0).floatValue();  
			float[] x  = new float[scanpoints];
			Double[] z  = new Double[scanpoints]; 
			// x, i datapoints
			for(int d=0; d<scanpoints; d++) {
				x[d] = mzchrom[i].getX(d).floatValue()-startTime;
				z[d] = mzchrom[i].getY(d).doubleValue();
			}

			// new line 
			listLines.add(new ScanLineMD(x,z));
		}
		// Image erstellen 
		return  new Image2D(new DatasetMD(listLines), 0, new SettingsImage2D(settPaintScale, setImage)); 
	}

	// Discontinous MS-Image
	// Generate Heatmap with Continous Data WIDHTOUT Triggerin every Line
	public static Image2D generateImage2DFromCon(SettingsPaintScale settPaintScale, SettingsGeneralImage setImage, SettingsImageContinousSplit settSplit, MZChromatogram mzchrom) {  
		// startTime
		float startTime = mzchrom.getX(0).floatValue();  
		// data points
		int scanpoints = mzchrom.getItemCount();
		float[] x  = new float[scanpoints];
		Double[] z  = new Double[scanpoints];
		// Daten eintragen und zwischen speichern
		for(int i=0; i<scanpoints; i++) {
			x[i] = mzchrom.getX(i).floatValue() - startTime; 
			z[i] = mzchrom.getY(i).doubleValue();
		}
		ScanLineMD line = new ScanLineMD(x, z);

		// generate Image
		return new Image2D(new DatasetContinuousMD(line, settSplit), 0, new SettingsImage2D(settPaintScale, setImage));
	}

	//
	// OES Generation
	public static Image2D generateImage2DFromOES(SettingsPaintScale settPaintScale, SettingsGeneralImage setImage, OESElementLine eLine) {
		// TODO mehrere MZ machen 
		// Alle Spec
		// Erst Messpunkteanzahl ausrechnen 
		// lines listLines
		OESScan scan;
		ScanLine2D[] listLines = new ScanLine2D[eLine.getListScan().size()];
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
			listLines[i] = new ScanLine2D(dp);
		}
		// Image erstellen 
		return  new Image2D(new Dataset2D(listLines), new SettingsImage2D(settPaintScale, setImage)); 
	}


	//#########################################################################################################
	// TO ARRAY LISTS   
	public XYIData2D toXYIArray(SettingsGeneralImage setImg) {
		return toXYIArray(setImg.getImagingMode(), setImg.getRotationOfData(), setImg.isReflectHorizontal(), setImg.isReflectVertical() );
	}
	public XYIData2D toXYIArray(int imgMode, int rotation, boolean reflectH, boolean reflectV) {
		// easy?
		if(imgMode==SettingsGeneralImage.MODE_IMAGING_ONEWAY && rotation==0 && reflectH==false && reflectV == false) {
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
			ScanLine2D line; 
			int currentdp = 0;
			double height = getMaxYProcessed();
			// for all lines
			for(int iy=0; iy<data.getLinesCount(); iy++) {
				// width  
				double width = getMaxXProcessed(iy);
				// for all datapoints in line
				for(int ix=0; ix<data.getLineLength(iy); ix++) {
					// reverse x if two way
					int cx = ix;
					if(imgMode==SettingsGeneralImage.MODE_IMAGING_TWOWAYS && iy%2 != 0) {
						cx = data.getLineLength(iy)-1-cx;
					}
					// x = time; NOT distance; so calc TODO
					x[currentdp] = getXProcessed(iy, cx);
					y[currentdp] = getYProcessed(iy);
					z[currentdp] = getIProcessed(iy, cx);

					// imagecreation mode: if twoways -> first reflect every 2. line (x values)
					if(imgMode==SettingsGeneralImage.MODE_IMAGING_TWOWAYS && iy%2 != 0) {
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
		// Erst Messpunkteanzahl ausrechnen 
		int scanpoints = data.getTotalDPCount(); 
		// Datenerstellen
		double[] x = new double[scanpoints];
		double[] y = new double[scanpoints];
		double[] z = new double[scanpoints];
		//
		ScanLine2D line; 
		int currentdp = 0;
		// for all lines
		for(int iy=0; iy<data.getLinesCount(); iy++) {
			// for all data points
			for(int ix=0; ix<data.getLineLength(iy); ix++) {
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
		int scanpoints = data.getTotalDPCount(); 
		// Datenerstellen
		double[] x = new double[scanpoints];
		double[] y = new double[scanpoints];
		double[] z = new double[scanpoints];
		//
		ScanLine2D line; 
		int currentdp = 0;
		//
		for(int iy=0; iy<data.getLinesCount(); iy++) {
			//
			for(int ix=0; ix<data.getLineLength(iy); ix++) {
				// x = time; NOT distance; 
				x[currentdp] = Double.valueOf(String.valueOf(getXRaw(iy, ix)));
				y[currentdp] = Double.valueOf(String.valueOf((iy)));
				z[currentdp] = Double.valueOf(String.valueOf(getIRaw(iy, ix)));
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
		int scanpoints = data.getTotalDPCount(); 
		double[] z = new double[scanpoints];
		//
		ScanLine2D line; 
		int currentdp = 0;
		//
		for(int iy=0; iy<data.getLinesCount(); iy++) {
			//
			for(int ix=0; ix<data.getLineLength(iy); ix++) {
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
	 * Creates an array of x and intensity data (raw or processed)
	 * @param sett
	 * @return data [rows][columns]
	 */
	public Object[][] toDataArray(ModeData mode, boolean raw) {
		// columns in the data sheet are lines / x values here
		// time only once?
		int cols = mode.equals(ModeData.XYYY)? data.getLinesCount() +1 :  data.getLinesCount()*2;
		if(mode.equals(ModeData.ONLY_Y)) cols = data.getLinesCount();
		// rows in data sheet = data points here
		int rows = data.getMaxDP();
		Object[][] dataExp = new Object[rows][cols];
		int l = 0;
		for(int c=0; c<cols; c++) {
			if((mode.equals(ModeData.XYYY) && c==0) || (mode.equals(ModeData.XYXY_ALTERN) && c%2==0)) {
				//write X
				for(int r = 0; r<rows; r++) {
					dataExp[r][c] = r<data.getLineLength(l)? getXRaw(l,r) : "";
				}
			}
			else {
				// write intensity
				for(int r = 0; r<rows; r++) {
					// only if not null
					dataExp[r][c] = r<data.getLineLength(l)? (raw? getIRaw(l,r) : getIProcessed(l,r)) : "";
				}
				// increment l line
				l++;
			}
		}
		return dataExp;
	}



	/**
	 * Returns the processed intensity only
	 * @param sett
	 * @return
	 */
	public Object[][] toXArray(boolean raw) {
		return data.toXMatrix(raw? 1 : getSettImage().getVelocity());
	} 

	/**
	 * 
	 * @param scale
	 * @param sep separation chars
	 * @return xmatrix raw by a factor as CSV string
	 */
	public String toXCSV(boolean raw, String sep) {
		return data.toXCSV(raw? 1 : getSettImage().getVelocity(), sep);
	}

	/**
	 * 
	 * @param scale
	 * @param sep separation chars
	 * @return ymatrix raw by a factor as CSV string
	 */
	public String toICSV(boolean raw, String sep) {
		StringBuilder builder = new StringBuilder();
		
		int cols = data.getLinesCount();
		int rows = data.getMaxDP();
		
		for(int r = 0; r<rows; r++) {
			// increment l
			for(int c=0; c<cols; c++) {
				// only if not null: write Intensity
				builder.append(r<getLineLength(c)? raw? getIRaw(c, r) : getIProcessed(c, r) : "");
				if(c<cols-1) builder.append(sep);
			} 
			if(r<rows-1) builder.append("\n");
		}
		return builder.toString();
	}
	
	/**
	 * Returns the processed intensity only
	 * @param sett
	 * @return
	 */
	public Object[][] toIArrayProcessed() {
		// time only once?
		int cols = data.getLinesCount();
		int rows = data.getMaxDP();
		Object[][] dataExp = new Object[rows][cols]; 
		for(int c=0; c<cols; c++) {
			// increment l
			for(int r = 0; r<rows; r++) {
				// only if not null: write Intensity
				dataExp[r][c] = r<data.getLineLength(c)? getIProcessed(c,r) : "";
			} 
		}
		return dataExp;
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
		int cols = data.getMaxDP();
		Object[][] dataExp = new Object[rows][cols]; 
		for(int r = 0; r<rows; r++) {
			for(int c=0; c<cols; c++) {
				// increment l 
				// only if not null: write Intensity
				boolean state = r<map.length && c<map[r].length && map[r][c];
				dataExp[r][c] = c<data.getLineLength(r) && state? getIProcessed(r,c) : "";
			} 
		}
		return dataExp;
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
		int cols = sett.getMode().equals(ModeData.XYYY)? data.getLinesCount() +1 :  data.getLinesCount()*2;
		if(sett.getMode().equals(ModeData.ONLY_Y)) cols = data.getLinesCount();
		// how many rows? maximum or cutting to minimum
		int minDP = data.getMinDP();
		int rows = !sett.isCuttingDataToMin() || minDP<=1? data.getMaxDP() : minDP;

		Object[][] dataRes = new Object[rows][cols];
		int l = startLine;
		for(int c=0; c<cols; c++) {
			if((sett.getMode().equals(ModeData.XYYY) && c==0) || (sett.getMode().equals(ModeData.XYXY_ALTERN) && c%2==0)) {
				//write X
				for(int r = 0; r<rows; r++) {
					dataRes[r][c] = r<data.getLineLength(l)? getXRaw(l,r) : "";
				}
			}
			else {
				// write intensity
				for(int r = 0; r<rows; r++) {
					// only if not null
					dataRes[r][c] = r<data.getLineLength(l)? getIProcessed(l,r, blank, IS, quantifier) : "";
				}
				// increment l line
				l++;
			}
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
		double i = getIRaw(l, dp);
		// subtract blank and apply IS
		if(getOperations()!=null) {
			i = getOperations().calcIntensity(this, l, dp, i);
		}
		// quantify
		if(getQuantifier()!=null && getQuantifier().isActive())  {
			return getQuantifier().calcIntensity(this, l, dp, i); 
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
		double i = getIRaw(l, dp);
		// subtract blank and apply IS
		if(getOperations()!=null) {
			i = getOperations().calcIntensity(this, l, dp, i, blank, IS);
		}
		// quantify
		if(getQuantifier()!=null && quantify)  {
			boolean tmp = getQuantifier().isActive();
			getQuantifier().setActive(true);
			i = getQuantifier().calcIntensity(this, l, dp, i); 
			getQuantifier().setActive(tmp);
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
			return data.getX(line, dp) * getSettImage().getVelocity();
		else {
			// TODO WHAT?! Cant understand this
			// is the program ending at this poitn? at any time?
			int overMax = (data.getLineLength(line)-dp+1);
			ImageEditorWindow.log("ask for a dp>then line in getXProcessed", LOG.DEBUG);
			//return (((data.getX(line, data.getLineLength(line)-1) + getLine(line).getWidthDP()*overMax) * settImage.getVelocity()));
			// tmp change
			return (((data.getX(line, data.getLineLength(line)-1)) * getSettImage().getVelocity()));
		}
	}
	/**
	 * The raw x
	 * @param l
	 * @param dp
	 * @return
	 */
	public float getXRaw(int l, int dp) { 
		int line = l<data.getLinesCount()? l:data.getLinesCount()-1;
		if(dp<data.getLineLength(line))
			return data.getX(line, dp);
		else {
			// TODO WHAT?! Cant understand this
			// is the program ending at this poitn? at any time?
			int overMax = (data.getLineLength(line)-dp+1);
			ImageEditorWindow.log("ask for a dp>then line in getXProcessed", LOG.DEBUG);
			//return (((data.getX(line, data.getLineLength(line)-1) + getLine(line).getWidthDP()*overMax) * settImage.getVelocity()));
			// tmp change
			return (((data.getX(line, data.getLineLength(line)-1))));
		}
	}
	/** 
	 * The raw intensity.
	 * @param l
	 * @param dp
	 * @return
	 */
	public double getIRaw(int l, int dp) {  
		return data.getI(index,l, dp);
	}
	/**
	 * The processed y
	 * @param l
	 * @param dp
	 * @return
	 */
	public float getYProcessed(int l) { 
		return l*getSettImage().getSpotsize();
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
		int l = (int)(y/getSettImage().getSpotsize());
		return l<data.getLinesCount()? l : data.getLinesCount()-1;
	} 
	/**
	 * returns the index of the data point in the given line 
	 * @param line is an integer index
	 * @param x is the coordinate (processed)
	 * @return
	 */
	public int getXAsIndex(int line, double x) {
		double rx = x/getSettImage().getVelocity();
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
		return getSettImage().toListName();
	} 

	@Override
	public String toString() {
		return toListName();
	}

	public String getTitle() { 
		return getSettImage().getTitle();
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
			else if(SettingsImage2D.class.isAssignableFrom(settings.getClass())) {
				setSettingsImage2D((SettingsImage2D) settings);
				fireIntensityProcessingChanged();
			}
			else if(SettingsGeneralImage.class.isAssignableFrom(settings.getClass())) 
				setSettImage((SettingsGeneralImage) settings);
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
			else if(SettingsImageContinousSplit.class.isAssignableFrom(settings.getClass())) 
				if(DatasetContinuousMD.class.isInstance(data)) 
					((DatasetContinuousMD)data).setSplitSettings((SettingsImageContinousSplit)settings);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	} 

	public Settings getSettingsByClass(Class classsettings) {
		// TODO -- add other settings here
		if(SettingsPaintScale.class.isAssignableFrom(classsettings)) 
			return getSettPaintScale();
		else if(SettingsImage2D.class.isAssignableFrom(classsettings))
			return settings;
		else if(SettingsGeneralImage.class.isAssignableFrom(classsettings)) 
			return getSettImage();
		else if(SettingsThemes.class.isAssignableFrom(classsettings)) 
			return getSettTheme();
		else if(SettingsImage2DOperations.class.isAssignableFrom(classsettings)) 
			return getOperations();
		else if(SettingsImage2DQuantifier.class.isAssignableFrom(classsettings)) 
			return getQuantifier();
		else if(SettingsImageContinousSplit.class.isAssignableFrom(classsettings)) 
			return DatasetContinuousMD.class.isInstance(data)? ((DatasetContinuousMD)data).getSplitSettings() : null;
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
	 * according to rotation of data
	 * @return
	 */
	public int getWidthMaxDP() {
		SettingsGeneralImage sg = getSettImage();
		return (sg.getRotationOfData()==90 || sg.getRotationOfData()==270)? data.getLinesCount() : data.getMaxDP();
	}
	/**
	 * according to rotation of data
	 * @return
	 */
	public int getHeightMaxDP() {
		SettingsGeneralImage sg = getSettImage();
		return (sg.getRotationOfData()==90 || sg.getRotationOfData()==270)? data.getMaxDP() : data.getLinesCount();	
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
	 * intensity range (max-min)
	 * @param onlySelected
	 * @return
	 */
	public double getIRange(boolean onlySelected){
		return this.getMaxIntensity(onlySelected)-this.getMinIntensity(onlySelected);
	}
	
	/**
	 * 
	 * @return value (set in a paintscale) as a percentage of the maximum value (value==max: result=100)
	 */
	public double getIPercentage(double intensity, boolean onlySelected) {
		return (intensity/getIRange(onlySelected)*100.0);
	}

	/**
	 * 
	 * @param value as percentage (0-100%)
	 * @param onlySelected
	 * @return value /100 * intensityRange
	 */
	public double getIAbs(double value, boolean onlySelected) { 
		return value/100.0*getIRange(onlySelected);
	}
	
	/**
	 * 
	 * @param intensity
	 * @return the percentile of all intensities (if value is equal to max the result is 100)
	 */
	public double getIPercentile(double intensity, boolean onlySelected) {
		//sort all z values
		double[] z = null;
		if(!onlySelected) toIArray();
		else z = getSelectedDataAsArray(true);
		Arrays.sort(z);
		
		for(int i=0; i<z.length; i++) {
			if(z[i]<=intensity) {
				return (i/(z.length-1));
			}
		}
		return 0;
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
			double[] z = null;
			if(!getSettPaintScale().isUsesMinMaxFromSelection())  z = toIArray();
			else z = getSelectedDataAsArray(true);
			Arrays.sort(z);
			// cut off percent f/100.f
			int size = z.length-1;
			// save in var
			minZFiltered =  z[(int)(size*f/100.0)];
			getSettPaintScale().setMin(minZFiltered);
		}
	}
	public void applyCutFilterMax(double f) {
		if(f!=lastAppliedMaxFilter) {
			lastAppliedMaxFilter = f;
			// apply filter
			//sort all z values
			double[] z = null;
			if(!getSettPaintScale().isUsesMinMaxFromSelection()) z = toIArray();
			else z = getSelectedDataAsArray(true);
			Arrays.sort(z);
			// cut off percent f/100.f
			int size = z.length-1;
			// save in var --> cut from max 1-p
			maxZFiltered = z[size-(int)(size*f/100.0)];
			getSettPaintScale().setMax(maxZFiltered);
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
		return settings.getSettPaintScale();
	} 
	public void setSettPaintScale(SettingsPaintScale ps) {
		settings.setSettPaintScale(ps);
		if(ps.getModeMax().equals(ValueMode.PERCENTILE) && ps.getMaxIAbs(this)==0 && ps.getMinIAbs(this)==0)
			applyCutFilterMax(ps.getMaxFilter());
		if(ps.getModeMin().equals(ValueMode.PERCENTILE) && ps.getMaxIAbs(this)==0 && ps.getMinIAbs(this)==0)
			applyCutFilterMin(ps.getMinFilter());
	} 
	public SettingsGeneralImage getSettImage() {
		return settings.getSettImage();
	} 
	public void setSettImage(SettingsGeneralImage settImgLaser) {
		settings.setSettImage(settImgLaser);
	} 
	public SettingsThemes getSettTheme() {
		return settings.getSettTheme();
	}
	public void setSettTheme(SettingsThemes settTheme) {
		settings.setSettTheme(settTheme);
	}
	public double getMinZFiltered() {
		return minZFiltered;
	}
	public double getMaxZFiltered() {
		return maxZFiltered;
	} 

	// if something changes - change the averageI
	public SettingsImage2DQuantifier getQuantifier() {
		return settings.getQuantifier();
	}
	public void setQuantifier(SettingsImage2DQuantifier quantifier) {
		settings.setQuantifier(quantifier);
	}
	public SettingsImage2DQuantifierIS getInternalQuantifierIS() {
		return settings.getInternalQuantifierIS();
	}
	public void setInternalQuantifierIS(SettingsImage2DQuantifierIS isQ) {
		settings.setInternalQuantifierIS(isQ);
	}
	public SettingsImage2DOperations getOperations() {
		return settings.getOperations();
	}
	public void setOperations(SettingsImage2DOperations operations) {
		settings.setOperations(operations, this);
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
	 * returns all data points in intensity range (max/min)
	 * @return
	 */
	public double[] getIInIRange() {
		double[] list = new double[countIInIRange()];
		
		int counter = 0;
		for(int l=0; l<data.getLinesCount(); l++) {
			for(int dp = 0; dp<data.getLineLength(l); dp++) {
				double intensity =getIProcessed(l, dp);
				if(getSettPaintScale().isInIRange(this, getIProcessed(l, dp))) {
					list[counter] = intensity;
					counter++;
				} 
			}
		}
		return list;
	}
	/**
	 * returns number of data points in intensity range (max/min)
	 * @return
	 */
	public int countIInIRange() {
		int counter = 0;
		for(int l=0; l<data.getLinesCount(); l++) {
			for(int dp = 0; dp<data.getLineLength(l); dp++) {
				if(getSettPaintScale().isInIRange(this, getIProcessed(l, dp))) {
					counter++;
				} 
			}
		}
		return counter;
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
	 * creates a histogram
	 * @return
	 */
	public ChartPanel createHistogram(double[] data, int bin) {
		if(data!=null && data.length>0) {
			HistogramDataset dataset = new HistogramDataset();
		    dataset.addSeries("histo", data, bin);
		    
		    JFreeChart chart = ChartFactory.createHistogram(
		              "", 
		              null, 
		              null, 
		              dataset, 
		              PlotOrientation.VERTICAL, 
		              true, 
		              false, 
		              false
		          );
	
		    chart.setBackgroundPaint(new Color(230,230,230));
		    chart.getLegend().setVisible(false);
		    XYPlot xyplot = (XYPlot)chart.getPlot();
		    xyplot.setForegroundAlpha(0.7F);
		    xyplot.setBackgroundPaint(Color.WHITE);
		    xyplot.setDomainGridlinePaint(new Color(150,150,150));
		    xyplot.setRangeGridlinePaint(new Color(150,150,150));
		    xyplot.getDomainAxis().setVisible(true);
		    xyplot.getRangeAxis().setVisible(false);
		    XYBarRenderer xybarrenderer = (XYBarRenderer)xyplot.getRenderer();
		    xybarrenderer.setShadowVisible(false);
		    xybarrenderer.setBarPainter(new StandardXYBarPainter()); 
	//	    xybarrenderer.setDrawBarOutline(false);
		    return new ChartPanel(chart);
		}
		else return null;
	}
	public ChartPanel createHistogram(double[] data) {
	    int bin = (int) Math.sqrt(data.length);
	    return createHistogram(data, bin);
	}
	
	/**
	 * returns an easy icon
	 * @param maxw
	 * @param maxh
	 * @return
	 */
	public Icon getIcon(int maxw, int maxh) {
		try {
			applyCutFilterMin(2.5);
			applyCutFilterMax(0.2);
			PaintScale scale = PaintScaleGenerator.generateStepPaintScale(minZFiltered, maxZFiltered, getSettPaintScale()); 
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
	public int getLineLength(int i) {
		return data.getLineLength(i);
	}

	/**
	 * test images
	 * @return
	 */
	public static Image2D createTestStandard() {
		Random rand = new Random(System.currentTimeMillis());
		ScanLine2D[] lines = new ScanLine2D[24];
		for(int l=0; l<lines.length; l++) { 
			DataPoint2D[] dp = new DataPoint2D[240];
			for(int d=0; d<dp.length; d++) {
				// middle the highest
				double in = (int)(l/4)*200.0;
				in += Math.abs(rand.nextInt(6000)/100.0);
				// create dp
				dp[d] = new DataPoint2D(d*0.1, in);
			}
			lines[l] = new ScanLine2D(dp);
		}
		Dataset2D data = new Dataset2D(lines);
		return new Image2D(data);
	}



	public ImageDataset getData() {
		return data;
	}
	public void setData(ImageDataset data) {
		this.data = data;
	}


	public void shiftIndex(int i) {
		index += i;
	}
	public void setIndex(int i) {
		index = i;
	}
	public int getIndex() {
		return index;
	}

	/**
	 * image is marked as a group member in an image group. 
	 * This group handles  multi dimensional data sets (not only)
	 * @param imageGroupMD
	 */
	public void setImageGroup(ImageGroupMD imageGroup) {
		this.imageGroup = imageGroup;
	}


	public ImageGroupMD getImageGroup() {
		return this.imageGroup;
	} 

	public void setSettingsImage2D(SettingsImage2D settings) {
		this.settings = settings;
	}
	public SettingsImage2D getSettingsImage2D() {
		return settings;
	}
	//############################################################
	// listener
	/**
	 * raw data changes by:
	 * direct imaging,
	 * @param listener
	 */
	public void addRawDataChangedListener(RawDataChangedListener listener) {
		data.addRawDataChangedListener(listener);
	}
	public void removeRawDataChangedListener(RawDataChangedListener list) {
		data.removeRawDataChangedListener(list);
	}
	public void cleatRawDataChangedListeners() {
		data.cleatRawDataChangedListeners();
	}


}
