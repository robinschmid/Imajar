package net.rs.lamsi.multiimager.Frames.dialogs.empaimport;

public class EmpaTableRow {

  private boolean useAsMZCalibration;
  private boolean createImage;
  private double dp, mz, relative, width;
  private String isotope;

  public EmpaTableRow() {
    this.useAsMZCalibration = false;
    this.createImage = false;
    this.dp = 0;
    this.isotope = "";
    this.mz = 0;
    this.relative = 0;
    this.width = 0;
  }

  public EmpaTableRow(boolean useAsMZCalibration, boolean createImage, String isotope, double dp,
      double mz, double width, double relative) {
    this.useAsMZCalibration = useAsMZCalibration;
    this.createImage = createImage;
    this.dp = dp;
    this.isotope = isotope;
    this.mz = mz;
    this.relative = relative;
    this.width = width;
  }

  public Object getValue(int columnIndex) {
    return getValue(EmpaColumn.values()[columnIndex]);
  }

  private Object getValue(EmpaColumn empaColumn) {
    switch (empaColumn) {
      case CAL:
        return useAsMZCalibration;
      case CREATE_IMAGE:
        return createImage;
      case DP:
        return dp;
      case ISOTOPE:
        return isotope;
      case MZ:
        return mz;
      case RELATIVE:
        return relative;
      case WIDTH:
        return width;
    }
    return null;
  }

  public void setValue(int col, Object value) {
    setValue(EmpaColumn.values()[col], value);
  }

  public void setValue(EmpaColumn empaColumn, Object value) {
    try {
      switch (empaColumn) {
        case CAL:
          useAsMZCalibration = (boolean) value;
        case CREATE_IMAGE:
          createImage = (boolean) value;
        case DP:
          dp = (double) value;
        case ISOTOPE:
          isotope = value.toString();
        case MZ:
          mz = (double) value;
        case RELATIVE:
          relative = (double) value;
        case WIDTH:
          width = (double) value;
      }
    } catch (Exception e) {
    }
  }

  public boolean isUseAsMZCalibration() {
    return useAsMZCalibration;
  }

  public void setUseAsMZCalibration(boolean useAsMZCalibration) {
    this.useAsMZCalibration = useAsMZCalibration;
  }

  public boolean isCreateImage() {
    return createImage;
  }

  public void setCreateImage(boolean createImage) {
    this.createImage = createImage;
  }

  public double getDp() {
    return dp;
  }

  public void setDp(double dp) {
    this.dp = dp;
  }

  public double getMz() {
    return mz;
  }

  public void setMz(double mz) {
    this.mz = mz;
  }

  public double getRelative() {
    return relative;
  }

  public void setRelative(double relative) {
    this.relative = relative;
  }

  public double getWidth() {
    return width;
  }

  public void setWidth(double width) {
    this.width = width;
  }

  public String getIsotope() {
    return isotope;
  }

  public void setIsotope(String isotope) {
    this.isotope = isotope;
  }

  public Object[] toObjectArray() {
    return new Object[] {isUseAsMZCalibration(), isCreateImage(), getIsotope(), getDp(), getMz(),
        getWidth(), getRelative()};
  }

}
