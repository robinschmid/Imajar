package net.rs.lamsi.general.settings.image.filter;

import java.util.ArrayList;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.ImageGroupMD;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.DatasetLinesMD;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.ScanLineMD;
import net.rs.lamsi.general.datamodel.image.data.twodimensional.XYIDataMatrix;
import net.rs.lamsi.general.datamodel.image.interf.MDDataset;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.general.settings.interf.Image2DSett;
import net.rs.lamsi.utils.threads.ProgressUpdateTask;
import net.rs.lamsi.utils.useful.dialogs.ProgressDialog;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SettingsCropAndShift extends Settings implements Image2DSett {
	private static final long serialVersionUID = 1L;

	// only negative (cut out) shifts - values are positive
	// 5 -> will cut out the 5 first (0-4) data poitns
	private int[] shiftLineStart;
	//
	private boolean cropToMinLength;
	private int delLinesStart = 0, delLinesEnd = 0;



	public SettingsCropAndShift() {
		super("SettingsCropAndShift", "/Settings/GeneralImage/Filter/", "setCropAndShift"); 
	}


	@Override
	public void resetAll() {
		shiftLineStart = null;
		cropToMinLength = true;
		delLinesStart = 0; delLinesEnd = 0;
	}
	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
	}
	@Override
	public void loadValuesFromXML(Element el, Document doc) {
	}


	public int[] getShiftLineStart() {
		return shiftLineStart;
	}


	public boolean isCropToMinLength() {
		return cropToMinLength;
	}


	public void setShiftLineStart(int[] shiftLineStart) {
		this.shiftLineStart = shiftLineStart;
	}


	public void setCropToMinLength(boolean cropToMinLength) {
		this.cropToMinLength = cropToMinLength;
	}


	@Override
	public void setCurrentImage(Image2D img) {
		if(img!=null && img.getData()!=null && (shiftLineStart==null || img.getData().getLinesCount()!=shiftLineStart.length))
			shiftLineStart = new int[img.getData().getLinesCount()];
		else if(img==null) shiftLineStart = null;
	}



	/**
	 * apply to group and create new cropped data set
	 * @param group
	 */
	public void applyToGroup(final ImageGroupMD group) {
		ProgressDialog.startTask(new ProgressUpdateTask(4) {
			
			@Override
			protected Boolean doInBackground() throws Exception {
				try {
					MDDataset data = group.getData();
					int min = Integer.MAX_VALUE;
					if(cropToMinLength) {
						// find min
						for(int l = delLinesStart; l<data.getLinesCount()-delLinesEnd; l++) {
							int length = data.getLineLength(l)-shiftLineStart[l];
							if(length<min) min = length;
						}
					}
					addProgressStep(1.f);

					ScanLineMD[] lines = new ScanLineMD[data.getLinesCount()-delLinesStart-delLinesEnd];
					for(int i=0; i<lines.length; i++)
						lines[i] = new ScanLineMD();

					// change x 
					if(data.hasXData()) {
						// for all lines
						if(cropToMinLength){
							for(int l = delLinesStart; l<data.getLinesCount()-delLinesEnd; l++) {
								ArrayList<Float> xx = new ArrayList<Float>();
								// start
								int start = shiftLineStart[l];
								float startx = data.getX(l, start);
								// for all data points
								for(int dp = start; dp<start+min; dp++) {
									xx.add(data.getX(l, dp)-startx);
								}
								lines[l-delLinesStart].setX(xx);
							}
						}
						else {
							for(int l = delLinesStart; l<data.getLinesCount()-delLinesEnd; l++) {
								ArrayList<Float> xx = new ArrayList<Float>();
								// start
								int start = shiftLineStart[l];
								float startx = data.getX(l, start);
								// for all data points
								for(int dp = start; dp<data.getLineLength(l); dp++) {
									xx.add(data.getX(l, dp)-startx);
								}
								lines[l-delLinesStart].setX(xx);
							}
						}
					}
					addProgressStep(1.f);

					// for all dimensions
					for(int img = 0; img<data.size(); img++) {
						if(cropToMinLength){
							for(int l = delLinesStart; l<data.getLinesCount()-delLinesEnd; l++) {
								ArrayList<Double> zz = new ArrayList<Double>();
								// start
								int start = shiftLineStart[l];
								// for all data points
								for(int dp = start; dp<start+min; dp++) {
									zz.add(data.getI(img, l, dp));
								}
								lines[l-delLinesStart].addDimension(zz);
							}
						}
						else {
							for(int l = delLinesStart; l<data.getLinesCount()-delLinesEnd; l++) {
								ArrayList<Double> zz = new ArrayList<Double>();
								// start
								int start = shiftLineStart[l];
								// for all data points
								for(int dp = start; dp<data.getLineLength(l); dp++) {
									zz.add(data.getI(img, l, dp));
								}
								lines[l-delLinesStart].addDimension(zz);
							}
						}
					}
					//addProgressStep(1.f);
					
					// create data set and set to group
					group.setData(new DatasetLinesMD(lines));
					addProgressStep(1.f);
					return true;
				}
				catch(Exception ex) {
					ex.printStackTrace();
					return false;
				}
			}
		});
	}

	/**
	 * applies the settings and returns the data series for a heatmap
	 * @param data
	 * @return img data [x,y,z][dp]
	 */
	public double[][] applyTo(XYIDataMatrix data) {
		Float[][] x = data.getX();
		Float[][] y = data.getY();
		Double[][] z = data.getI();
		// count data points
		int size = 0;
		// cut to min
		if(cropToMinLength) {
			int min = Integer.MAX_VALUE;
			// find min
			for(int i=delLinesStart; i<shiftLineStart.length-delLinesEnd; i++) {
				int l = data.lineLength(i)-shiftLineStart[i];
				if(l<min) min = l;
			}
			// size of data points array
			size = min*(shiftLineStart.length-delLinesStart-delLinesEnd);
			double[][] result = new double[3][size];

			// write data to array
			int c = 0;
			for(int i=delLinesStart; i<shiftLineStart.length-delLinesEnd; i++) {
				// apply line shift and min 
				double xstart = x[i][shiftLineStart[i]];
				for(int dp = shiftLineStart[i]; dp<shiftLineStart[i]+min; dp++) {
					result[0][c] = x[i][dp]-xstart;
					result[1][c] = y[i][dp];
					result[2][c] = z[i][dp];
					c++;
				}
			}
			return result;
		}
		else{
			// line lengths
			int l[] = new int[shiftLineStart.length-delLinesEnd-delLinesStart];
			for(int i=delLinesStart; i<z.length-delLinesEnd; i++) {
				l[i-delLinesStart] = data.lineLength(i)-shiftLineStart[i];
				size += l[i-delLinesStart];
			}

			double[][] result = new double[3][size];
			// write data to array
			int c = 0;
			for(int i=delLinesStart; i<z.length-delLinesEnd; i++) {
				// apply line shift and min 
				double xstart = x[i][shiftLineStart[i]];
				for(int dp = shiftLineStart[i]; dp<shiftLineStart[i]+l[i-delLinesStart]; dp++) {
					result[0][c] = x[i][dp]-xstart;
					result[1][c] = y[i][dp];
					result[2][c] = z[i][dp];
					c++;
				}
			}
			return result;
		}
	}


	public void setDeletedLinesStart(int l) {
		delLinesStart = l;
	}
	public void setDeletedLinesEnd(int l) {
		delLinesEnd= l;
	}
}
