package net.rs.lamsi.general.settings.image.sub;

import net.rs.lamsi.massimager.MyMZ.MZIon;

public class SettingsMSImage extends SettingsGeneralImage {
  // do not change the version!
  private static final long serialVersionUID = 1L;
  //

  // my pm and name
  protected MZIon mzIon = null;

  public SettingsMSImage() {
    super("/Settings/MSImage/", "setMSI");
    this.velocity = 50;
    this.spotsize = 50;
    this.timePerLine = 1;
    this.isTriggered = true;
  }

  public SettingsMSImage(boolean allFiles, boolean isTriggert, float velocity, float spotsize,
      double timePerLine, MZIon mzIon) {
    super("/Settings/MSImage/", "setMSI");
    this.velocity = velocity;
    this.spotsize = spotsize;
    this.timePerLine = timePerLine;
    this.mzIon = mzIon;
    this.isTriggered = isTriggert;
    this.allFiles = allFiles;
  }

  public MZIon getMZIon() {
    return mzIon;
  }

  public void setMZIon(MZIon mzIon) {
    this.mzIon = mzIon;
  }

  public boolean isAllFiles() {
    return allFiles;
  }

  public void setAllFiles(boolean allFiles) {
    this.allFiles = allFiles;
  }

  @Override
  public void resetAll() {
    super.resetAll();
  }
}
