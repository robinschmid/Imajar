package net.rs.lamsi.multiimager.Frames.dialogs.empaimport;

public enum EmpaColumn {
  CAL("m/z calibration", Boolean.class), CREATE_IMAGE("create img", Boolean.class), ISOTOPE(
      "isotope", String.class), DP("dp", Double.class), MZ("m/z",
          Double.class), WIDTH("m/z window", Double.class), RELATIVE("relative", Double.class);

  private String title;
  private Class<?> clazz;

  EmpaColumn(String title, Class<?> clazz) {
    this.title = title;
    this.clazz = clazz;
  }

  public String getTitle() {
    return title;
  }

  public Class<?> getClazz() {
    return clazz;
  }
}
