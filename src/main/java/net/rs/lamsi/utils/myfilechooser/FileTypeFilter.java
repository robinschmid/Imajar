package net.rs.lamsi.utils.myfilechooser;
import java.io.File;

import javax.swing.filechooser.FileFilter;
 
 
public class FileTypeFilter extends FileFilter {
 
	private String[] extensions;
    private String extension=null;
    private String description;
     
    public FileTypeFilter(String extension, String description) {
        this.extension = extension;
        this.description = description;
    }
    public FileTypeFilter(String[] extensions, String description) {
        this.extensions = extensions;
        this.description = description;
    }
     
    @Override
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        } 
        // String extfile = FilenameUtils.getExtension(file.getName());
        if(extension!=null)
        	return extension.equalsIgnoreCase(FileTypeFilter.getExtensionFromFile(file));
        else {
        	String fileEx = FileTypeFilter.getExtensionFromFile(file);
        	for(String e : extensions)
        		if(e.equalsIgnoreCase(fileEx))
        			return true;
        	return false;
        }
    }
     
    public String getDescription() {
    	if(extension!=null)
            return description + String.format(" (*%s)", extension);
        else { 
        	String desc = description+" (";
        	for(String e : extensions)
        		desc = desc + "*"+extension+", ";
        	desc = desc.substring(0, desc.length()-2) + ")";
        	return desc;
        }
    }
    
    public static boolean hasExtensionFile(File file) {
    	return getExtensionFromFile(file) != null;
    }
    
    public static String getExtensionFromFile(File file) { 
        String extfile = null;
        String fileName = file.getName();
        
        int i = fileName.lastIndexOf('.');
        int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

        if (i > p) {
        	extfile = fileName.substring(i+1);
        }
        return extfile;
    }

    public static String getFileNameWithoutExtension(File file) { 
        String realName = file.getName();
        String fileName = realName;
        
        int i = fileName.lastIndexOf('.');
        int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

        if (i > p) {
        	realName = fileName.substring(0, i);
        }
        return realName;
    }
    
    public File addExtensionToFileName(File file) { 
    	// Wenn eine Extension vorliegt schauen ob sie richtig ist
    	String ext = getExtensionFromFile(file); 
    	if(ext==null || !extension.equals(ext)) { 
    		// FIle Name
    		String tmp = getFileNameWithoutExtension(file)+"."+extension;
    		// EXT von File löschen und neu anfügen
    		File endfile = new File(file.getParent(), tmp);
    		return endfile; 
    	} 
    	// ansonsten das file zurückgeben 
    	return file;
    }

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}
}
