package net.rs.lamsi.utils.mywriterreader;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

public class ClipboardWriter {


	  /**
	   * [rows][cols]
	   * 
	   * @param model
	   * @param rowsFirst true: [rows][cols] false [cols][rows]
	   */
	  public static void writeToClipBoard(Object[][] model, boolean rowsFirst) {
	    StringSelection transferable = new StringSelection(dataToTabSepString(model, rowsFirst));
	    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(transferable, transferable);
	  }

	  /**
	   * returns a tab separated string
	   * 
	   * @param model
	   * @param rowsFirst true: [rows][cols] false [cols][rows]
	   * @return
	   */
	  public static String dataToTabSepString(Object[][] model, boolean rowsFirst) {
	    StringBuilder s = new StringBuilder();

	    if (rowsFirst) {
	      for (int r = 0; r < model.length; r++) {
	        for (int c = 0; c < model[r].length; c++) {
	          s.append(model[r][c]);
	          if (c != model[r].length - 1)
	            s.append("\t");
	        }
	        if (r < model.length - 1)
	          s.append("\n");
	      }
	    } else {
	      int max = 0;
	      for (int r = 0; r < model.length; r++)
	        if (model[r].length > max)
	          max = model[r].length;

	      for (int r = 0; r < max; r++) {
	        for (int c = 0; c < model.length; c++) {
	          // add data or empty cell
	          if (r < model[c].length) {
	            s.append(model[c][r]);
	          }
	          // split cell
	          if (c != model.length - 1)
	            s.append("\t");
	        }
	        if (r < max - 1)
	          s.append("\n");
	      }
	    }
	    return s.toString();
	  }

	  /**
	   * returns a tab separated string
	   * 
	   * @param data
	   */
	  public static String dataToTabSepString(double[] data) {
	    StringBuilder s = new StringBuilder();
	    for (int r = 0; r < data.length; r++) {
	      s.append(String.valueOf(data[r]));
	      s.append("\n");
	    }
	    return s.toString();
	  }

	  /**
	   * writes string to clipboard
	   * 
	   * @param s
	   */
	  public static void writeToClipBoard(String s) {
	    StringSelection transferable = new StringSelection(s);
	    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(transferable, transferable);
	  }

	  /**
	   * write data array to one column
	   * 
	   * @param data
	   */
	  public static void writeColumnToClipBoard(Object[] data) {
	    StringBuilder s = new StringBuilder();
	    for (int r = 0; r < data.length; r++) {
	      s.append(data[r]);
	      s.append("\n");
	    }
	    StringSelection transferable = new StringSelection(s.toString());
	    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(transferable, transferable);
	  }

	  /**
	   * write data array to one column
	   * 
	   * @param data
	   */
	  public static void writeColumnToClipBoard(double[] data) {
	    StringBuilder s = new StringBuilder();
	    for (int r = 0; r < data.length; r++) {
	      s.append(String.valueOf(data[r]));
	      s.append("\n");
	    }
	    StringSelection transferable = new StringSelection(s.toString());
	    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(transferable, transferable);
	  }

	}
