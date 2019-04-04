package net.rs.lamsi.multiimager.utils.imageimportexport;

public class TextAnalyzer {

  /**
   * 
   * @param s
   * @return true if all values are numbers or for empty strings
   */
  public static boolean isNumberValues(String s[]) {
    for (String i : s) {
      if (!isNumberValue(i))
        return false;
    }
    return true;
  }

  /**
   * 
   * @param s
   * @return true if value is number or for empty strings
   */
  public static boolean isNumberValue(String s) {
    if (s.length() == 0)
      return true;
    try {
      double d = Double.parseDouble(s);
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

}
