package net.rs.lamsi.massimager.Settings.image;

import java.util.Vector;

import net.rs.lamsi.massimager.Settings.SettingsImage.XUNIT;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow.LOG;
import net.rs.lamsi.utils.useful.FileNameExtFilter;

public class SettingsImageDataImportTxt extends SettingsImageDataImport {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    //

 
	public static enum IMPORT {
		MULTIPLE_FILES_LINES_TXT_CSV, ONE_FILE_2D_INTENSITY, CONTINOUS_DATA_TXT_CSV, PRESETS_THERMO_MP17, PRESETS_THERMO_NEPTUNE;
	} 
	public static enum ModeData {
		// mode of data: onlyY, one X, alternating
		ONLY_Y, XYYY, XYXY_ALTERN
	} 
	
	protected String sSeparation = ",";
	protected IMPORT modeImport = IMPORT.MULTIPLE_FILES_LINES_TXT_CSV;
	protected ModeData modeData = ModeData.ONLY_Y;
	protected FileNameExtFilter filter;
	protected boolean isFilesInSeparateFolders = false;
	// filter lines and dp (0 = no filter)
	protected int startLine=0, endLine=0, startDP=0, endDP=0;
	// splitting settings
	protected XUNIT splitUnit;
	// splitAfter 0 - try to estimate split after
	protected float splitStart = 0, splitAfter = 0;
	protected boolean useHardSplit = true;
	// exclude data
	protected String excludeColumns;
	protected boolean noXData;
	
 
	public IMPORT getModeImport() {
		return modeImport;
	}


	public void setModeImport(IMPORT modeImport) {
		this.modeImport = modeImport;
	}

	public SettingsImageDataImportTxt(String path, String fileEnding, IMPORT modeImport, boolean isSearchingForMetaData, String sSeparation, FileNameExtFilter filter) {
		super(path, fileEnding, isSearchingForMetaData);  
		this.sSeparation = sSeparation;
		this.modeImport = modeImport;
		this.filter = filter;
	}
	public SettingsImageDataImportTxt(IMPORT modeImport, boolean isSearchingForMetaData, String sSeparation, FileNameExtFilter filter, boolean isFilesInSeparateFolders) {
		super("/Settings/Import/", "txt2img", isSearchingForMetaData);  
		this.sSeparation = sSeparation;
		this.modeImport = modeImport;
		this.filter = filter;
		this.isFilesInSeparateFolders = isFilesInSeparateFolders;
	}
	public SettingsImageDataImportTxt(IMPORT modeImport, boolean isSearchingForMetaData, String sSeparation, boolean isFilesInSeparateFolders) {
		this(modeImport, isSearchingForMetaData, sSeparation, new FileNameExtFilter("", ""), isFilesInSeparateFolders);
	}


	public SettingsImageDataImportTxt(IMPORT oneFile2dIntensity,
			boolean checkformeta, String separation, ModeData mode,
			FileNameExtFilter filter2, boolean isFilesInSeparateFolders, int startLine, int endLine, int startDP, int endDP) { 
		this(oneFile2dIntensity, checkformeta, separation, filter2, isFilesInSeparateFolders);
		modeData = mode;
		this.endDP = endDP;
		this.startDP = startDP;
		this.startLine = startLine;
		this.endLine = endLine;
	}
	public SettingsImageDataImportTxt(IMPORT oneFile2dIntensity,
			boolean checkformeta, String separation, ModeData mode,
			FileNameExtFilter filter2, boolean isFilesInSeparateFolders, int startLine, int endLine, int startDP, int endDP,
			XUNIT unit, float splitStart, float splitAfter, boolean useHardSplit,
			String excludeColumns, boolean noXData) { 
		this(oneFile2dIntensity, checkformeta, separation, mode, filter2, isFilesInSeparateFolders, startLine,endLine,startDP,endDP);
		this.splitUnit = unit;
		if(noXData) splitUnit = XUNIT.DP;
		this.splitStart = splitStart;
		this.splitAfter = splitAfter;
		this.useHardSplit = useHardSplit;
		this.noXData = noXData;
		this.excludeColumns = excludeColumns.replaceAll(" ", "");
	}


	@Override
	public void resetAll() { 
		isSearchingForMetaData = true;
		sSeparation = ",";
		modeData = ModeData.ONLY_Y;
	}


	public String getSeparation() {
		return sSeparation;
	}


	public void setSeperation(String sSeperation) {
		this.sSeparation = sSeperation;
	}


	public FileNameExtFilter getFilter() {
		return filter;
	}


	public void setFilter(FileNameExtFilter filter) {
		this.filter = filter;
	}


	public boolean isFilesInSeparateFolders() {
		return isFilesInSeparateFolders;
	}


	public void setFilesInSeparateFolders(boolean isFilesInSeparateFolders) {
		this.isFilesInSeparateFolders = isFilesInSeparateFolders;
	}


	public ModeData getModeData() {
		return modeData;
	} 
	public void setModeData(ModeData modeData) {
		this.modeData = modeData;
	}


	public int getStartLine() {
		return startLine;
	}


	public void setStartLine(int startLine) {
		this.startLine = startLine;
	}


	public int getEndLine() {
		return endLine;
	}


	public void setEndLine(int endLine) {
		this.endLine = endLine;
	}


	public int getStartDP() {
		return startDP;
	}


	public void setStartDP(int startDP) {
		this.startDP = startDP;
	}


	public int getEndDP() {
		return endDP;
	}


	public void setEndDP(int endDP) {
		this.endDP = endDP;
	}


	public XUNIT getSplitUnit() {
		return splitUnit;
	}


	public void setSplitUnit(XUNIT splitUnit) {
		this.splitUnit = splitUnit;
	}


	public float getSplitStart() {
		return splitStart;
	}


	public void setSplitStart(float splitStart) {
		this.splitStart = splitStart;
	}


	public float getSplitAfter() {
		return splitAfter;
	}


	public void setSplitAfter(float splitAfter) {
		this.splitAfter = splitAfter;
	}


	public boolean isUseHardSplit() {
		return useHardSplit;
	}


	public void setUseHardSplit(boolean useHardSplit) {
		this.useHardSplit = useHardSplit;
	}


	public String getExcludeColumns() {
		return excludeColumns;
	}


	public void setExcludeColumns(String excludeColumns) {
		this.excludeColumns = excludeColumns;
	}
	
	/**
	 * parses the user input for excluded columns
	 * @return int array of excluded columns
	 */
	public Vector<Integer> getExcludeColumnsArray() {
		if(excludeColumns==null || excludeColumns.length()==0)
			return null;
		// parse string
		try {
			Vector<Integer> ex = new Vector<Integer>();
			String[] sep = excludeColumns.split(",");
			for(String s: sep) {
				String[] two = s.split("-");
				if(two.length==1)
					ex.add(Integer.valueOf(two[0])-1);
				else if(two.length==2) {
					for(int i=Integer.valueOf(two[0])-1; i<=Integer.valueOf(two[1])-1; i++) {
						ex.add(i);
					}
				}
			}
			return ex;
		} catch (Exception e) {
			ImageEditorWindow.log("Import: Wrong exclude parameters. Use comma separation and - for ranges. !Executing import without exclusion!", LOG.ERROR);
			return null;
		}
	} 


	public boolean isNoXData() {
		return noXData;
	}


	public void setNoXData(boolean noXData) {
		this.noXData = noXData;
	}


	public int getMaxLines() { 
		if(getEndLine()==0) return 0;
		return getStartLine()!=0? getEndLine()-getStartLine()+1 : getEndLine();
	}


	public int getSplitAfterDP() { 
		return Math.round(splitAfter);
	}


	public int getSplitStartDP() {
		return Math.round(splitStart);
	}

}
