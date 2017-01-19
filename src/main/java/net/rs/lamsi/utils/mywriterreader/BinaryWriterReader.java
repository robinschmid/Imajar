package net.rs.lamsi.utils.mywriterreader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class BinaryWriterReader {
	private FileOutputStream FOS = null;
    private ObjectOutputStream OOS = null;
    private FileInputStream FIS = null;
    private ObjectInputStream OIS = null;

    public BinaryWriterReader() {
    }

    public void save2file(Object obj, File file) {
        if (OOS == null || FOS == null) {
            open_out(file);
        }

        try {
            OOS.writeObject(obj);
        } catch (IOException ioe) {
            System.err.println
                ("Error: Could not serialize object.");
            ioe.printStackTrace(System.err);
        }
    }

    public Object readFromFile(File file) {
        if (OIS == null || FIS == null) {
            open_in(file);
        }

        try {
            Object obj = (Object) OIS.readObject();
            return obj;
        } catch (IOException ioe) {
            System.err.println("Error: Could not deserialize object.");
            ioe.printStackTrace(System.err);
        } catch (ClassNotFoundException cnfe) {
            System.err.println("Error: Could not find class!");
            cnfe.printStackTrace(System.err);
        }
        return null;
    }

    private void open_out(File file) {
        if (OOS != null || FOS != null) {
            closeOut();
        }

        try {
            FOS = new FileOutputStream(file);
            OOS = new ObjectOutputStream(FOS);
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
            ioe.printStackTrace(System.out);
        }
    }

    private void open_in(File file) {
        if (FIS != null || OIS != null) {
            closeIn();
        }

        try {
            FIS = new FileInputStream(file);
            OIS = new ObjectInputStream(FIS);
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
            ioe.printStackTrace(System.out);
        }
    }

    public void closeOut() {
        if (OOS != null && FOS != null) {
            try {
                OOS.close();
                OOS = null;
                FOS.close();
                FOS = null;
            } catch (IOException ioe) {
                System.err.println(ioe.getMessage());
                ioe.printStackTrace(System.out);
            }
        }

    }

    public void closeIn() {
        if (OIS != null && FIS != null) {
            try {
                OIS.close();
                OIS = null;
                FIS.close();
                FIS = null;
            } catch (IOException ioe) {
                System.err.println(ioe.getMessage());
                ioe.printStackTrace(System.out); 
            }
        } 
    }

    // deep copy of an object
    public static <T> T deepCopy( T o ) throws Exception {
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	new ObjectOutputStream( baos ).writeObject( o );

    	ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );

    	return (T) new ObjectInputStream(bais).readObject();
    }
}
