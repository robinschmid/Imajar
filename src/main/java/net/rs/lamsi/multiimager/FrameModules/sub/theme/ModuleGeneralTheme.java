package net.rs.lamsi.multiimager.FrameModules.sub.theme;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.util.ResourceBundle;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.jfree.chart.editor.ChartEditor;
import org.jfree.chart.editor.ChartEditorManager;
import org.jfree.chart.util.ResourceBundleWrapper;
import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.framework.basics.JColorPickerButton;
import net.rs.lamsi.general.framework.basics.JFontSpecs;
import net.rs.lamsi.general.framework.listener.ColorChangedListener;
import net.rs.lamsi.general.framework.listener.DelayedDocumentListener;
import net.rs.lamsi.general.framework.modules.Collectable2DSettingsModule;
import net.rs.lamsi.general.framework.modules.Module;
import net.rs.lamsi.general.framework.modules.menu.ModuleMenu;
import net.rs.lamsi.general.myfreechart.themes.ChartThemeFactory;
import net.rs.lamsi.general.myfreechart.themes.ChartThemeFactory.THEME;
import net.rs.lamsi.general.myfreechart.themes.MyStandardChartTheme;
import net.rs.lamsi.general.settings.image.visualisation.themes.SettingsTheme;
import net.rs.lamsi.multiimager.Frames.ImageLogicRunner;

public class ModuleGeneralTheme extends Collectable2DSettingsModule<SettingsTheme, Collectable2D> {
  // mystuff
  protected boolean isForPrint = true;

  private ActionListener al;

  // auto
  private JCheckBox cbAntiAlias;
  private JCheckBox cbNoBackground;
  private JCheckBox cbShowTitle;
  protected static ResourceBundle localizationResources =
      ResourceBundleWrapper.getBundle("org.jfree.chart.LocalizationBundle");
  private JCheckBox cbShowXAxis;
  private JCheckBox cbShowYAxis;
  private DelayedDocumentListener ddlMaster;
  private JFontSpecs fontMaster;
  private JFontSpecs fontAxesCaption;
  private JFontSpecs fontAxesLabels;
  private JFontSpecs fontPlotTitle;
  private JPanel gridsettings;
  private JTextField txtChartTitle;
  private JColorPickerButton cbtnPlotBgColor;
  private JColorPickerButton cbtnBGColor;
  private JTextField txtXTitle;
  private JTextField txtYTitle;
  private JCheckBox cbOutline;
  private JTextField txtXTickUnit;
  private JTextField txtYTickUnit;
  private JCheckBox cbXAutoTick;
  private JCheckBox cbYAutoTick;
  private JTextField txtXTickFormat;
  private JTextField txtYFormat;


  public ModuleGeneralTheme() {
    super("General", false, SettingsTheme.class, Collectable2D.class);
    getLbTitle().setText("General");


    gridsettings = new JPanel();
    getPnContent().add(gridsettings, BorderLayout.CENTER);
    gridsettings.setAlignmentY(0.0f);
    gridsettings.setAlignmentX(0.0f);
    gridsettings.setLayout(new BoxLayout(gridsettings, BoxLayout.Y_AXIS));

    Module general = new Module("General");
    general.setShowTitleAlways(true);
    gridsettings.add(general);

    Module modAxes = new Module("Axes and scale");
    modAxes.setShowTitleAlways(true);
    gridsettings.add(modAxes);

    Module pnFonts = new Module("Fonts and text");
    pnFonts.setShowTitleAlways(true);
    gridsettings.add(pnFonts);

    JPanel panel_1 = new JPanel();
    general.getPnContent().add(panel_1, BorderLayout.NORTH);
    panel_1.setLayout(new MigLayout("", "[grow][][grow][grow]", "[][][][][]"));

    JButton btnPlotSettings = new JButton("Plot settings");
    btnPlotSettings.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // open settings dialog from jfreechart
        if (currentHeat != null) {
          currentHeat.getChartPanel().getPopupMenu();
          ChartEditor editor = ChartEditorManager.getChartEditor(currentHeat.getChart());
          int result = JOptionPane.showConfirmDialog(currentHeat.getChartPanel(), editor,
              localizationResources.getString("Chart_Properties"), JOptionPane.OK_CANCEL_OPTION,
              JOptionPane.PLAIN_MESSAGE);
          if (result == JOptionPane.OK_OPTION) {
            editor.updateChart(currentHeat.getChart());
            updateFromEditorDialog();
          }
        }
      }
    });
    panel_1.add(btnPlotSettings, "cell 1 0 2 1,alignx center");

    cbAntiAlias = new JCheckBox("anti aliasing");
    panel_1.add(cbAntiAlias, "flowy,cell 1 1");

    cbNoBackground = new JCheckBox("no background");
    panel_1.add(cbNoBackground, "cell 2 1");

    cbOutline = new JCheckBox("outline");
    panel_1.add(cbOutline, "cell 1 2");

    cbShowTitle = new JCheckBox("show title");
    panel_1.add(cbShowTitle, "cell 1 3");

    txtChartTitle = new JTextField();
    txtChartTitle.setToolTipText("Chart title");
    panel_1.add(txtChartTitle, "cell 2 3,growx");
    txtChartTitle.setColumns(10);

    cbtnBGColor = new JColorPickerButton((Component) null);
    cbtnBGColor.setToolTipText("Chart background color");
    panel_1.add(cbtnBGColor, "flowx,cell 1 4");

    JLabel lblBgColor = new JLabel("chart bg");
    panel_1.add(lblBgColor, "cell 1 4");

    cbtnPlotBgColor = new JColorPickerButton((Component) null);
    cbtnPlotBgColor.setToolTipText("Plot background color");
    panel_1.add(cbtnPlotBgColor, "flowx,cell 2 4");

    JLabel lblPlotBg = new JLabel("plot bg");
    panel_1.add(lblPlotBg, "cell 2 4");


    JPanel panel_2 = new JPanel();
    pnFonts.getPnContent().add(panel_2, BorderLayout.NORTH);
    panel_2.setLayout(new MigLayout("", "[grow][center][grow]", "[][grow][grow][grow]"));

    JButton btnForPrint = new JButton("For print");
    btnForPrint.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        applyChangeThemeForPrint(true);
      }
    });
    panel_2.add(btnForPrint, "flowx,cell 1 0");

    JPanel panel_5 = new JPanel();
    panel_2.add(panel_5, "cell 1 1,growy");
    panel_5.setLayout(new MigLayout("", "[][grow]", "[][grow][grow][grow][grow]"));

    JLabel lblFont = new JLabel("Font");
    panel_5.add(lblFont, "cell 0 0,alignx center");

    JLabel lblGeneral = new JLabel("Master");
    lblGeneral.setFont(new Font("Tahoma", Font.BOLD, 13));
    lblGeneral.setToolTipText("The master font changes all sub fonts");
    panel_5.add(lblGeneral, "cell 0 1,alignx right");

    fontMaster = new JFontSpecs();
    panel_5.add(fontMaster, "cell 1 1,alignx center,growy");

    JLabel lblAxesCaptions = new JLabel("Captions");
    lblAxesCaptions.setFont(new Font("Tahoma", Font.BOLD, 11));
    panel_5.add(lblAxesCaptions, "flowy,cell 0 2");

    fontAxesCaption = new JFontSpecs();
    panel_5.add(fontAxesCaption, "cell 1 2,grow");

    JLabel lblAxelLabels = new JLabel("Labels");
    panel_5.add(lblAxelLabels, "cell 0 3,alignx right");

    fontAxesLabels = new JFontSpecs();
    panel_5.add(fontAxesLabels, "cell 1 3,grow");

    JLabel lblPlotTitle = new JLabel("Plot title");
    panel_5.add(lblPlotTitle, "cell 0 4,alignx right");

    fontPlotTitle = new JFontSpecs();
    panel_5.add(fontPlotTitle, "cell 1 4,grow");

    JButton btnForPresentation = new JButton("For presentation");
    btnForPresentation.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        applyChangeThemeForPrint(false);
      }
    });
    panel_2.add(btnForPresentation, "cell 1 0");


    JPanel panel_3 = new JPanel();
    modAxes.getPnContent().add(panel_3, BorderLayout.CENTER);
    panel_3.setLayout(new MigLayout("", "[grow][][][grow][grow]", "[][][][][]"));

    JLabel lblXAxis = new JLabel("x axis");
    lblXAxis.setFont(new Font("Tahoma", Font.BOLD, 11));
    panel_3.add(lblXAxis, "cell 1 0,alignx trailing");

    cbShowXAxis = new JCheckBox("show x axis");
    cbShowXAxis.setSelected(true);
    panel_3.add(cbShowXAxis, "cell 2 0");

    txtXTitle = new JTextField();
    txtXTitle.setToolTipText("Domain axis title");
    txtXTitle.setText("x");
    panel_3.add(txtXTitle, "cell 3 0,growx");
    txtXTitle.setColumns(10);

    JLabel lblYAxis = new JLabel("y axis");
    lblYAxis.setFont(new Font("Tahoma", Font.BOLD, 11));
    panel_3.add(lblYAxis, "cell 1 1,alignx trailing");

    cbShowYAxis = new JCheckBox("show y axis");
    cbShowYAxis.setSelected(true);
    panel_3.add(cbShowYAxis, "cell 2 1");

    txtYTitle = new JTextField();
    txtYTitle.setToolTipText("Range axis title");
    txtYTitle.setText("y");
    panel_3.add(txtYTitle, "cell 3 1,growx");
    txtYTitle.setColumns(10);

    JLabel lblTicks = new JLabel("ticks");
    panel_3.add(lblTicks, "cell 2 2,alignx center");

    JLabel lblFormat = new JLabel("format");
    panel_3.add(lblFormat, "cell 3 2,alignx center");

    cbXAutoTick = new JCheckBox("x auto tick");
    cbXAutoTick.setSelected(true);
    panel_3.add(cbXAutoTick, "cell 1 3");

    txtXTickUnit = new JTextField("1");
    panel_3.add(txtXTickUnit, "cell 2 3,growx");
    txtXTickUnit.setColumns(8);

    txtXTickFormat = new JTextField();
    txtXTickFormat.setToolTipText("Leave empty or define a format (e.g., 0.00  or 0.0E0)");
    panel_3.add(txtXTickFormat, "cell 3 3,growx");
    txtXTickFormat.setColumns(10);

    cbYAutoTick = new JCheckBox("y auto tick");
    cbYAutoTick.setSelected(true);
    panel_3.add(cbYAutoTick, "cell 1 4");

    txtYTickUnit = new JTextField("1");
    txtYTickUnit.setColumns(8);
    panel_3.add(txtYTickUnit, "cell 2 4,growx");

    txtYFormat = new JTextField();
    txtYFormat.setToolTipText("Leave empty or define a format (e.g., 0.00  or 0.0E0)");
    panel_3.add(txtYFormat, "cell 3 4,growx,aligny top");
    txtYFormat.setColumns(10);
    // add standard themes to menu
    addStandardThemesToMenu();
  }

  /**
   * applies changes to font sizes via ChartThemeFactory
   * 
   * @param b
   */
  protected void applyChangeThemeForPrint(boolean state) {
    if (currentImage != null) {
      isForPrint = state;
      SettingsTheme theme = currentImage.getSettTheme().getSettTheme();
      setSettings(theme, true);
    }
  }

  /**
   * update after settings dialog was closed
   * 
   * @param editor
   */
  protected void updateFromEditorDialog() {
    // TODO
    // update theme settings and then all components in this panel
    // update via chart not via editor
  }

  private void addStandardThemesToMenu() {
    ModuleMenu menu = getPopupMenu();
    menu.addSeparator();
    addThemeToMenu(menu, (THEME.FOR_PRINT), "Print");
    addThemeToMenu(menu, (THEME.FOR_PRESENTATION), "Presentation");
    menu.addSeparator();
    addThemeToMenu(menu, (THEME.BNW_PRINT), "Black n White");
    addThemeToMenu(menu, (THEME.DARKNESS), "Darkness");
    addThemeToMenu(menu, (THEME.KARST), "Karst");
  }

  private void addThemeToMenu(ModuleMenu menu, final THEME themeid, String title) {
    JMenuItem item = new JMenuItem(title);
    menu.addMenuItem(item);
    item.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          SettingsTheme theme = null;
          if (themeid == THEME.FOR_PRINT || themeid == THEME.FOR_PRESENTATION) {
            applyChangeThemeForPrint(themeid == THEME.FOR_PRINT);
          } else {
            theme = new SettingsTheme(themeid);
            setSettings(theme, true);
            al.actionPerformed(null);
          }
        } catch (Exception e1) {
          e1.printStackTrace();
        }
      }
    });
  }

  // apply for print or presentation before changing settings
  @Override
  public void setSettings(SettingsTheme settings, boolean setAllToPanel) {
    ChartThemeFactory.setStandardTheme(settings.getID());
    super.setSettings(settings, setAllToPanel);
  }

  // ################################################################################################
  // Autoupdate
  @Override
  public void addAutoupdater(ActionListener al, ChangeListener cl, DocumentListener dl,
      ColorChangedListener ccl, ItemListener il) {}

  @Override
  public void addAutoRepainter(final ActionListener al, ChangeListener cl, DocumentListener dl,
      ColorChangedListener ccl, ItemListener il) {
    // axes and scale
    getCbShowXAxis().addItemListener(il);
    getCbShowYAxis().addItemListener(il);
    // general
    getCbAntiAlias().addItemListener(il);
    getCbShowTitle().addItemListener(il);
    getCbNoBackground().addItemListener(il);

    getTxtChartTitle().getDocument().addDocumentListener(dl);
    getTxtXTitle().getDocument().addDocumentListener(dl);
    getTxtYTitle().getDocument().addDocumentListener(dl);

    // font specs
    getFontAxesCaption().addListener(ccl, il, dl);
    getFontAxesLabels().addListener(ccl, il, dl);
    getFontPlotTitle().addListener(ccl, il, dl);

    getTxtYTickUnit().getDocument().addDocumentListener(dl);
    getTxtXTickUnit().getDocument().addDocumentListener(dl);
    getTxtXTickFormat().getDocument().addDocumentListener(dl);
    getTxtYTickFormat().getDocument().addDocumentListener(dl);

    getCbXAutoTick().addItemListener(il);
    getCbYAutoTick().addItemListener(il);

    getCbOutline().addItemListener(il);

    // change? TODO
    getFontMaster().addListener(new ColorChangedListener() {
      @Override
      public void colorChanged(Color c) {
        if (ImageLogicRunner.IS_UPDATING()) {
          // set to not register changes for a while
          ImageLogicRunner.setIS_UPDATING(false);
          getFontAxesCaption().setColor(c);
          getFontAxesLabels().setColor(c);
          getFontPlotTitle().setColor(c);

          ImageLogicRunner.setIS_UPDATING(true);
          // change last value for update
          al.actionPerformed(null);
        }
      }
    }, new ItemListener() {

      @Override
      public void itemStateChanged(ItemEvent e) {
        if (ImageLogicRunner.IS_UPDATING()) {
          // set to not register changes for a while
          ImageLogicRunner.setIS_UPDATING(false);

          Font f = getFontMaster().getSelectedFont();
          getFontAxesCaption().setSelectedFont(f);
          getFontAxesLabels().setSelectedFont(f);
          getFontPlotTitle().setSelectedFont(f);

          ImageLogicRunner.setIS_UPDATING(true);
          // change last value for update
          al.actionPerformed(null);
        }
      }
    }, ddlMaster = new DelayedDocumentListener() {
      @Override
      public void documentChanged(DocumentEvent e) {
        if (ImageLogicRunner.IS_UPDATING()) {
          // set to not register changes for a while
          ImageLogicRunner.setIS_UPDATING(false);
          int size = getFontMaster().getFontSize();
          if (size > 1) {
            getFontAxesCaption().setFontSize(size);
            getFontAxesLabels().setFontSize(size);
            getFontPlotTitle().setFontSize(size);
          }

          ImageLogicRunner.setIS_UPDATING(true);
          // change last value for update
          al.actionPerformed(null);
        }
      }
    });

    cbtnBGColor.addColorChangedListener(ccl);
    cbtnPlotBgColor.addColorChangedListener(ccl);

    this.al = al;
  }

  // ################################################################################################
  // LOGIC
  @Override
  public void setAllViaExistingSettings(SettingsTheme st) throws Exception {
    ImageLogicRunner.setIS_UPDATING(false);
    ddlMaster.setActive(false);

    if (st != null) {
      MyStandardChartTheme t = st.getTheme();
      // set all to txt
      getCbAntiAlias().setSelected(t.isAntiAliased());
      getCbShowTitle().setSelected(t.isShowTitle());
      getCbNoBackground().setSelected(t.isNoBackground());

      getCbShowXAxis().setSelected(st.getTheme().isShowXAxis());
      getCbShowYAxis().setSelected(st.getTheme().isShowYAxis());


      getCbtnBGColor().setColor(t.getChartBackgroundPaint());
      getCbtnPlotBgColor().setColor(t.getPlotBackgroundPaint());

      getCbOutline().setSelected(t.isShowOutline());

      getCbXAutoTick().setSelected(t.isUseXAutoTick());
      getCbYAutoTick().setSelected(t.isUseYAutoTick());

      getTxtXTickUnit().setText(String.valueOf(t.getxTickUnit()));
      getTxtYTickUnit().setText(String.valueOf(t.getyTickUnit()));

      DecimalFormat fx = t.getxTickFormat();
      DecimalFormat fy = t.getyTickFormat();
      getTxtXTickFormat().setText(fx != null ? fx.toPattern() : "");
      getTxtYTickFormat().setText(fy != null ? fy.toPattern() : "");

      // chart title
      getTxtChartTitle().setText(st.getChartTitle());

      getTxtXTitle().setText(st.getxAxisTitle());
      getTxtYTitle().setText(st.getyAxisTitle());

      // set fonts:
      getFontAxesCaption().setSelectedFont(t.getLargeFont());
      getFontAxesLabels().setSelectedFont(t.getRegularFont());
      getFontMaster().setSelectedFont(t.getMasterFont());
      getFontPlotTitle().setSelectedFont(t.getExtraLargeFont());

      // colors
      getFontAxesCaption().setColor(t.getAxisLabelPaint());
      getFontAxesLabels().setColor(t.getTickLabelPaint());
      getFontMaster().setColor(t.getMasterFontColor());
      getFontPlotTitle().setColor(t.getTitlePaint());
    } else {
      System.out.println("null settings");
    }
    // finished
    ddlMaster.setActive(true);
    ImageLogicRunner.setIS_UPDATING(true);
    // ImageEditorWindow.getEditor().fireUpdateEvent(true);
  }

  @Override
  public SettingsTheme writeAllToSettings(SettingsTheme st) {
    if (st != null) {
      try {
        // setall
        st.setAll(getCbAntiAlias().isSelected(), getCbShowTitle().isSelected(),
            getCbNoBackground().isSelected(), getCbtnBGColor().getColor(),
            getCbtnPlotBgColor().getColor(), false, false, getCbShowXAxis().isSelected(),
            getCbShowYAxis().isSelected(), getFontMaster().getSelectedFont(),
            getFontMaster().getColor(), getFontAxesCaption().getSelectedFont(),
            getFontAxesCaption().getColor(), getFontAxesLabels().getSelectedFont(),
            getFontAxesLabels().getColor(), getFontPlotTitle().getSelectedFont(),
            getFontPlotTitle().getColor(), getTxtChartTitle().getText(), getTxtXTitle().getText(),
            getTxtYTitle().getText(), getCbOutline().isSelected(), getCbXAutoTick().isSelected(),
            getCbYAutoTick().isSelected(), floatFromTxt(txtXTickUnit), floatFromTxt(txtYTickUnit),
            txtXTickFormat.getText(), txtYFormat.getText());
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    return st;
  }


  // ################################################################################################
  // GETTERS AND SETTERS
  public JCheckBox getCbAntiAlias() {
    return cbAntiAlias;
  }

  public JCheckBox getCbNoBackground() {
    return cbNoBackground;
  }

  public JCheckBox getCbShowTitle() {
    return cbShowTitle;
  }

  public JCheckBox getCbShowXAxis() {
    return cbShowXAxis;
  }

  public JCheckBox getCbShowYAxis() {
    return cbShowYAxis;
  }

  public JFontSpecs getFontMaster() {
    return fontMaster;
  }

  public JFontSpecs getFontAxesCaption() {
    return fontAxesCaption;
  }

  public JFontSpecs getFontAxesLabels() {
    return fontAxesLabels;
  }

  public JFontSpecs getFontPlotTitle() {
    return fontPlotTitle;
  }

  public JColorPickerButton getCbtnBGColor() {
    return cbtnBGColor;
  }

  public JTextField getTxtChartTitle() {
    return txtChartTitle;
  }

  public JColorPickerButton getCbtnPlotBgColor() {
    return cbtnPlotBgColor;
  }

  public JTextField getTxtYTitle() {
    return txtYTitle;
  }

  public JTextField getTxtXTitle() {
    return txtXTitle;
  }

  public JCheckBox getCbOutline() {
    return cbOutline;
  }

  public JTextField getTxtYTickUnit() {
    return txtYTickUnit;
  }

  public JTextField getTxtXTickUnit() {
    return txtXTickUnit;
  }

  public JCheckBox getCbXAutoTick() {
    return cbXAutoTick;
  }

  public JCheckBox getCbYAutoTick() {
    return cbYAutoTick;
  }

  public JTextField getTxtXTickFormat() {
    return txtXTickFormat;
  }

  public JTextField getTxtYTickFormat() {
    return txtYFormat;
  }
}
