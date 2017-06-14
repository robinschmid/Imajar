package net.rs.lamsi.utils.mywriterreader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;

public class ZipUtil {

	
	
	
	public static TreeMap<String, InputStream> readZip(File file) {
		TreeMap<String, InputStream> inMemoryFiles = new TreeMap<String, InputStream>();
        try {
            ZipFile zipFile = new ZipFile(file);
            List fileHeaderList = zipFile.getFileHeaders();
            for (int i = 0; i < fileHeaderList.size(); i++) {
                FileHeader fileHeader = (FileHeader) fileHeaderList.get(i);
                if(!fileHeader.isDirectory()) {
	                ZipInputStream is = zipFile.getInputStream(fileHeader);
	                int uncompressedSize = (int) fileHeader.getUncompressedSize();
	                OutputStream os = new ByteArrayOutputStream(uncompressedSize);
	                int bytesRead;
	                byte[] buffer = new byte[4096];
	                while ((bytesRead = is.read(buffer)) != -1) {
	                    os.write(buffer, 0, bytesRead);
	                }
	                byte[] uncompressedBytes = ((ByteArrayOutputStream) os).toByteArray();
	                
	                // only file not folders
	                File f = new File(fileHeader.getFileName());
	                inMemoryFiles.put(f.getName(), new ByteArrayInputStream(uncompressedBytes));
	                is.close();
                }
            }
        } catch (ZipException | IOException ex) {
            ex.printStackTrace(System.err);
        }
        return inMemoryFiles;
    }
	
	/**
	 * for project
	 * map< Folder name , map<file name, stream>>
	 */
	public static TreeMap<String, TreeMap<String, InputStream>> readProjectZip(File zipF) {
		TreeMap<String, TreeMap<String, InputStream>> inMemoryFiles = new TreeMap<String, TreeMap<String, InputStream>>();
        try {
            ZipFile zipFile = new ZipFile(zipF);
            List fileHeaderList = zipFile.getFileHeaders();
            for (int i = 0; i < fileHeaderList.size(); i++) {
                FileHeader fileHeader = (FileHeader) fileHeaderList.get(i);
                if(!fileHeader.isDirectory()) {
	                ZipInputStream is = zipFile.getInputStream(fileHeader);
	                int uncompressedSize = (int) fileHeader.getUncompressedSize();
	                OutputStream os = new ByteArrayOutputStream(uncompressedSize);
	                int bytesRead;
	                byte[] buffer = new byte[4096];
	                while ((bytesRead = is.read(buffer)) != -1) {
	                    os.write(buffer, 0, bytesRead);
	                }
	                byte[] uncompressedBytes = ((ByteArrayOutputStream) os).toByteArray();
	                
	                File file = new File(fileHeader.getFileName());
	                String fname = file.getName();
	                String group = file.getParentFile().getName();
	                // group is already added?
	                TreeMap<String, InputStream> map = inMemoryFiles.get(group);
	                if(map==null) {
	                	// add new group folder
	                	map = new TreeMap<String, InputStream>();
	                	inMemoryFiles.put(group, map);
	                }
	                // add new file entry to group
                	map.put(fname, new ByteArrayInputStream(uncompressedBytes));
	                is.close();
                }
            }
        } catch (ZipException | IOException ex) {
            ex.printStackTrace(System.err);
        }
        return inMemoryFiles;
    }
}
