package net.rs.lamsi.general.settings.image;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.ImageGroupMD;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.myfreechart.themes.ChartThemeFactory.THEME;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.general.settings.image.needy.SettingsCollectable2DLink;
import net.rs.lamsi.general.settings.image.needy.SettingsGroupItemSelections;
import net.rs.lamsi.general.settings.image.sub.SettingsZoom;
import net.rs.lamsi.general.settings.image.visualisation.SettingsPaintScale;
import net.rs.lamsi.general.settings.image.visualisation.SettingsPaintScale.ScaleType;
import net.rs.lamsi.general.settings.image.visualisation.themes.SettingsThemesContainer;
import net.rs.lamsi.utils.useful.graphics2d.blending.BlendComposite;
import net.rs.lamsi.utils.useful.graphics2d.blending.BlendComposite.BlendingMode;

public class SettingsImageOverlay extends SettingsContainerCollectable2D {
  // do not change the version!
  private static final long serialVersionUID = 1L;
  private static ArrayList<Color> STANDARD_COLORS;
  //

  // the currently edited paintscale
  protected int currentPaintScale = 0;
  // overlay settings
  protected String title = "";
  // colors for paintscales
  protected List<Color> colors;
  protected List<SettingsPaintScale> psSettings = new ArrayList<SettingsPaintScale>();
  // holds which image2d / paintscale is active --> SettingsGroupItemSelections

  // blending mode with alpha
  protected BlendComposite blend = BlendComposite.Add;


  public SettingsImageOverlay() {
    super("SettingsImageOverlay", "/Settings/ImageOv/", "setImgOv");
    // standard theme
    resetAll();
    // other
    addSettings(new SettingsThemesContainer(THEME.DARKNESS, false));
    addSettings(new SettingsZoom());
    addSettings(new SettingsGroupItemSelections());
  }

  /**
   * copy paint scales reset the active list
   * 
   * @param images
   * @throws Exception
   */
  public void init(ImageGroupMD group) throws Exception {
    psSettings.clear();
    int c = 0;
    for (int f = 0; f < group.image2dCount(); f++) {
      Image2D i = (Image2D) group.get(f);
      SettingsPaintScale ps = (SettingsPaintScale) i.getSettings().getSettPaintScale().copy();
      psSettings.add(ps);
      // monochrome and color
      ps.setScaleType(ScaleType.MONOCHROME);
      ps.setMinColor(colors.get(c % colors.size()));
      ps.setMaxColor(colors.get(c % colors.size()));
      //
      c++;
    }

    // create active array
    Image2D[] img = group.getImagesOnly();
    LinkedHashMap<SettingsCollectable2DLink, Boolean> active =
        new LinkedHashMap<SettingsCollectable2DLink, Boolean>(img.length);
    for (Image2D i : img)
      active.put(new SettingsCollectable2DLink(i.getTitle()), new Boolean(img.length <= 4));

    getSettGroupItemSelections().setActive(active);
  }

  public boolean isInitialised() {
    return psSettings.size() > 0;
  }

  /**
   * add image2d
   * 
   * @throws Exception
   */
  public void addImage(Image2D i) throws Exception {
    SettingsPaintScale ps = (SettingsPaintScale) i.getSettings().getSettPaintScale().copy();
    psSettings.add(ps);
    Map<SettingsCollectable2DLink, Boolean> active = getActive();
    // monochrome and color
    ps.setScaleType(ScaleType.MONOCHROME);
    ps.setMinColor(colors.get(active.size() % colors.size()));
    ps.setMaxColor(colors.get(active.size() % colors.size()));
    // create sett
    // TODO how to handle different groups and projects
    SettingsCollectable2DLink sett = new SettingsCollectable2DLink(i.getTitle());
    //
    active.put(sett, new Boolean(false));
  }

  /**
   * add image2d
   * 
   * @throws Exception
   */
  public void removeImage(int i) throws Exception {
    psSettings.remove(i);
    getActive().remove(i);
  }

  @Override
  public void resetAll() {
    super.resetAll();
    title = "";
    setToStandardColors();
    if (psSettings != null)
      psSettings.clear();
  }

  @Override
  public void appendSettingsValuesToXML(Element elParent, Document doc) {
    toXML(elParent, doc, "title", title);
    toXML(elParent, doc, "blend", blend.getMode().toString(), new String[] {"alpha"},
        new Object[] {blend.getAlpha()});
    // colors
    for (int i = 0; i < colors.size(); i++)
      toXML(elParent, doc, "colorXX" + i, colors.get(i));

    for (int i = 0; i < psSettings.size(); i++)
      psSettings.get(i).appendSettingsToXML(elParent, doc);
  }

  @Override
  public void loadValuesFromXML(Element el, Document doc) {
    SettingsPaintScale ps = new SettingsPaintScale();
    colors.clear();

    NodeList list = el.getChildNodes();
    for (int i = 0; i < list.getLength(); i++) {
      if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
        Element nextElement = (Element) list.item(i);
        String paramName = nextElement.getNodeName();
        if (paramName.equals("title"))
          title = nextElement.getTextContent();
        else if (paramName.startsWith("colorXX"))
          colors.add(colorFromXML(nextElement));
        else if (paramName.startsWith("blend")) {
          blend = BlendComposite.getInstance(BlendingMode.valueOf(nextElement.getTextContent()),
              Float.valueOf(nextElement.getAttribute("alpha")));
        } else if (isSettingsNode(nextElement, ps.getSuperClass())) {
          SettingsPaintScale ps2 = new SettingsPaintScale();
          ps2.loadValuesFromXML(nextElement, doc);
          psSettings.add(ps2);
        }
      }
    }
    if (colors.isEmpty())
      setToStandardColors();

    // load sub settings
  }



  public Settings getSettingsByClass(Class classsettings) {
    // TODO -- add other settings here
    if (SettingsPaintScale.class.isAssignableFrom(classsettings))
      return getCurrentSettPaintScale();
    else {
      return super.getSettingsByClass(classsettings);
    }
  }

  /**
   * red, blue, green, orange, lila
   * 
   * @return
   */
  public List<Color> setToStandardColors() {
    if (STANDARD_COLORS == null) {
      STANDARD_COLORS = new ArrayList<Color>();
      STANDARD_COLORS.add(Color.RED);
      STANDARD_COLORS.add(Color.BLUE);
      STANDARD_COLORS.add(Color.GREEN);
      STANDARD_COLORS.add(Color.ORANGE);
      STANDARD_COLORS.add(new Color(200, 0, 200));
    }
    colors = STANDARD_COLORS;
    return STANDARD_COLORS;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public List<Color> getColors() {
    return colors;
  }

  public void setColors(ArrayList<Color> colors) {
    this.colors = colors;
  }

  public void setColors(Color[] color) {
    colors.clear();
    for (Color c : color)
      colors.add(c);
  }

  public Map<SettingsCollectable2DLink, Boolean> getActive() {
    return getSettGroupItemSelections().getActive();
  }

  /**
   * is constructed by setImages whether to paint image[image] or not
   * 
   * @param image
   * @return
   */
  public Boolean isActive(Collectable2D img) {
    return getSettGroupItemSelections().isActive(img);
  }

  /**
   * is constructed by setImages whether to paint image[image] or not
   * 
   * @param source could be an image overlay in a group/project to specify the current group path
   *        for relative (in group) references
   * @param img
   * @return
   */
  public Boolean isActive(Collectable2D source, Collectable2D img) {
    return getSettGroupItemSelections().isActive(source, img);
  }

  public SettingsPaintScale getSettPaintScale(int i) {
    return psSettings == null ? null : psSettings.get(i);
  }

  public List<SettingsPaintScale> getSettPaintScale() {
    return psSettings;
  }

  public void setSettPaintScale(ArrayList<SettingsPaintScale> psSettings) {
    this.psSettings = psSettings;
  }

  public void setCurrentSettPaintScale(SettingsPaintScale sett) {
    if (psSettings == null || currentPaintScale < 0 || currentPaintScale >= psSettings.size())
      return;
    else {
      psSettings.remove(currentPaintScale);
      psSettings.add(currentPaintScale, sett);
    }
  }

  public SettingsPaintScale getCurrentSettPaintScale() {
    return psSettings == null || currentPaintScale < 0 || currentPaintScale >= psSettings.size()
        ? null
        : psSettings.get(currentPaintScale);
  }

  public int getCurrentPaintScale() {
    return currentPaintScale;
  }

  public SettingsGroupItemSelections getSettGroupItemSelections() {
    return (SettingsGroupItemSelections) getSettingsByClass(SettingsGroupItemSelections.class);
  }

  public void setCurrentPaintScale(int currentPaintScale) {
    this.currentPaintScale = currentPaintScale;
  }

  public BlendComposite getBlend() {
    return blend;
  }

  public void setBlend(BlendComposite blend) {
    this.blend = blend;
  }
}
