package net.rs.lamsi.utils.mywriterreader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;

public class ZipUtil {

	
	
	
	public static Map<String, InputStream> readZip(File file) {
        Map<String, InputStream> inMemoryFiles = new HashMap<>();
        try {
            ZipFile zipFile = new ZipFile(file);
            List fileHeaderList = zipFile.getFileHeaders();
            for (int i = 0; i < fileHeaderList.size(); i++) {
                FileHeader fileHeader = (FileHeader) fileHeaderList.get(i);
                ZipInputStream is = zipFile.getInputStream(fileHeader);
                int uncompressedSize = (int) fileHeader.getUncompressedSize();
                OutputStream os = new ByteArrayOutputStream(uncompressedSize);
                int bytesRead;
                byte[] buffer = new byte[4096];
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                byte[] uncompressedBytes = ((ByteArrayOutputStream) os).toByteArray();
                inMemoryFiles.put(fileHeader.getFileName(), new ByteArrayInputStream(uncompressedBytes));
                is.close();
                System.out.println("DONE "+i);
            }
        } catch (ZipException | IOException ex) {
            ex.printStackTrace(System.err);
        }
        return inMemoryFiles;
    }
}
