package net.rs.lamsi.massimager.MyMZ.oldmzjavasave;


public abstract class MZXMLReader {
/*
	protected MzxmlReader reader;
	protected File file;
	
	public boolean openFile(File file) throws Exception {  
		// default constructor strictly checks for inconsistences
		try {
			this.file = file;
			closeAll();
			reader = MzxmlReader.newTolerantReader(file, PeakList.Precision.DOUBLE);  
			// we can then add/remove ConsistencyCheck 
			reader.removeConsistencyChecks(EnumSet.complementOf(EnumSet.of(MzxmlReader.ConsistencyCheck.TOTAL_ION_CURRENT)));  
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("",e);
			throw e;
		} 
	}
	
	public void forAllSpectraCallNextSpecFound() {  
		// add data		
		if(reader!=null) {
			while(reader.hasNext()) { 
				try {
					MsnSpectrum spec= reader.next(); 
					nextSpecFound(spec);
				} catch (IOException e) { 
					logger.error("",e);
				} 
			}
		}
	}
	
	public abstract void nextSpecFound(MsnSpectrum spec);

	public void closeAll() {
		if(reader!=null) {
			try{
				reader.close(); 
			}catch(Exception ex) {
				logger.error("",ex);
			}
		}
	}
*/
}
