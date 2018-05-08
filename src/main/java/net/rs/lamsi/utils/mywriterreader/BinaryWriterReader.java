package net.rs.lamsi.utils.mywriterreader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BinaryWriterReader {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  private FileOutputStream FOS = null;
  private ObjectOutputStream OOS = null;
  private FileInputStream FIS = null;
  private ObjectInputStream OIS = null;

  public BinaryWriterReader() {}

  public void save2file(Object obj, File file) {
    if (OOS == null || FOS == null) {
      open_out(file);
    }

    try {
      OOS.writeObject(obj);
    } catch (IOException ioe) {
      logger.error("Could not serialize object", ioe);
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
      logger.error("Could not deserialize object", ioe);
    } catch (ClassNotFoundException cnfe) {
      logger.error("Error: Could not find class!", cnfe);
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
      logger.error("Could not open output stream", ioe);
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
      logger.error("COuld not open input stream", ioe);
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
        logger.error("Could not close output stream", ioe);
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
        logger.error("Could not close input stream");
      }
    }
  }

  // deep copy of an object
  public static <T> T deepCopy(T o) throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    new ObjectOutputStream(baos).writeObject(o);

    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

    return (T) new ObjectInputStream(bais).readObject();
  }
}
