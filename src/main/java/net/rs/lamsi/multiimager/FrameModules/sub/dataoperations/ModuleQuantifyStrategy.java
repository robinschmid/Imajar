package net.rs.lamsi.multiimager.FrameModules.sub.dataoperations;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.framework.listener.ColorChangedListener;
import net.rs.lamsi.general.framework.modules.Collectable2DSettingsModule;
import net.rs.lamsi.general.framework.modules.tree.IconNode;
import net.rs.lamsi.general.settings.image.operations.quantifier.SettingsImage2DQuantifier;
import net.rs.lamsi.general.settings.image.operations.quantifier.SettingsImage2DQuantifier.MODE;
import net.rs.lamsi.general.settings.image.operations.quantifier.SettingsImage2DQuantifierLinear;
import net.rs.lamsi.general.settings.image.operations.quantifier.SettingsImage2DQuantifierOnePoint;
import net.rs.lamsi.general.settings.image.selection.SettingsSelections;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageLogicRunner;
import net.rs.lamsi.multiimager.Frames.dialogs.selectdata.SelectDataAreaDialog;
import net.rs.lamsi.utils.DialogLoggerUtil;
import net.rs.lamsi.utils.useful.dialogs.DialogLinearRegression;

public class ModuleQuantifyStrategy
    extends Collectable2DSettingsModule<SettingsImage2DQuantifier, Image2D> {
  //
  private MODE lastMode = SettingsImage2DQuantifier.MODE.LINEAR;
  // save img IS
  private Image2D imgEx;
  //
  private ImageEditorWindow window;
  private final ButtonGroup buttonGroup = new ButtonGroup();
  private final ButtonGroup buttonGroup_1 = new ButtonGroup();
  private JPanel tabLinear;
  private JTextField txtLinearIntercept;
  private JTextField txtLinearSlope;
  private JCheckBox cbQuantify;
  private JPanel tabOnePoint;
  private JTextField txtExTitle;
  private JTextField txtExPath;
  private JTabbedPane tabbedPane;
  private final ButtonGroup buttonGroup_2 = new ButtonGroup();

  /**
   * Create the panel.
   */
  public ModuleQuantifyStrategy(ImageEditorWindow wnd) {
    super("Quantifier", false, SettingsImage2DQuantifier.class, Image2D.class);
    //
    window = wnd;

    tabbedPane = new JTabbedPane(JTabbedPane.TOP);
    getPnContent().add(tabbedPane, BorderLayout.CENTER);

    getTabbedPane().addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        // tab changed so new settings file

      }
    });

    tabLinear = new JPanel();
    tabbedPane.addTab("Linear", null, tabLinear, null);
    tabLinear.setLayout(new MigLayout("", "[grow][][]", "[][][][][]"));

    JLabel lblQuantifyByGiven = new JLabel("Quantify by given linear function");
    tabLinear.add(lblQuantifyByGiven, "cell 0 0");

    JLabel lblYAbx = new JLabel("y = a+bx");
    tabLinear.add(lblYAbx, "cell 0 2");

    JLabel lblA = new JLabel("a = ");
    tabLinear.add(lblA, "flowx,cell 0 3");

    txtLinearIntercept = new JTextField();
    txtLinearIntercept.setToolTipText("intercept");
    txtLinearIntercept.setText("0");
    tabLinear.add(txtLinearIntercept, "cell 0 3,growx");
    txtLinearIntercept.setColumns(10);

    JLabel lblB = new JLabel("b = ");
    tabLinear.add(lblB, "flowx,cell 0 4");

    txtLinearSlope = new JTextField();
    txtLinearSlope.setToolTipText("slope");
    txtLinearSlope.setText("1");
    tabLinear.add(txtLinearSlope, "cell 0 4,growx");
    txtLinearSlope.setColumns(10);

    tabOnePoint = new JPanel();
    tabbedPane.addTab("Regression", null, tabOnePoint, null);
    tabOnePoint.setLayout(new BorderLayout(0, 0));

    JPanel panel = new JPanel();
    tabOnePoint.add(panel, BorderLayout.CENTER);
    panel.setLayout(new MigLayout("", "[][][grow]", "[][][][]"));

    JButton btnShowRegression2 = new JButton("Show regression");
    btnShowRegression2.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (imgEx != null) {
          SettingsSelections s =
              (SettingsSelections) imgEx.getSettingsByClass(SettingsSelections.class);
          DialogLinearRegression d =
              DialogLinearRegression.createInstance(s.getRegressionData(), true);
          d.setVisible(true);
        }
      }
    });
    panel.add(btnShowRegression2, "cell 1 0,growx");

    JLabel lblTitle = new JLabel("Title");
    panel.add(lblTitle, "cell 0 1,alignx trailing");

    txtExTitle = new JTextField();
    panel.add(txtExTitle, "cell 1 1 2 1,growx");
    txtExTitle.setColumns(10);

    JLabel lblPath_1 = new JLabel("Path");
    panel.add(lblPath_1, "cell 0 2,alignx trailing");

    txtExPath = new JTextField();
    panel.add(txtExPath, "cell 1 2 2 1,growx");
    txtExPath.setColumns(10);

    JButton btnChooseFromCurrent = new JButton("Choose from current list");
    btnChooseFromCurrent.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          TreePath[] p = DialogLoggerUtil.showTreeDialogAndChoose(window,
              window.getModuleTreeImages().getRoot(), TreeSelectionModel.SINGLE_TREE_SELECTION,
              window.getModuleTreeImages().getTree().getSelectionPaths(), "Single selection",
              "Select one image");
          if (p != null && p.length > 0) {
            Object img = ((DefaultMutableTreeNode) p[0].getLastPathComponent());
            if (IconNode.class.isInstance(img)
                && Image2D.class.isInstance(((IconNode) img).getUserObject())) {
              setExStandard(((Image2D) ((IconNode) img).getUserObject()));
            }
          }
        } catch (Exception ex) {
          setExStandard(null);
        }
      }
    });
    panel.add(btnChooseFromCurrent, "cell 1 3");

    JButton btnExSelectDataArea = new JButton("Select data area");
    btnExSelectDataArea.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // open dialog with image ex
        if (imgEx != null) {
          SelectDataAreaDialog dialog = new SelectDataAreaDialog();
          dialog.startDialog(imgEx);
          WindowAdapter wl = new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
              super.windowClosed(e);
              // areas selected?
              // getRbExAverageSelectedAreas().setEnabled(imgEx.getSelectedData().size()>0);
              // if(imgEx.getSelectedData().size()>0)
              // getRbExAverageSelectedAreas().setSelected(true);
              // changed
              window.fireUpdateEvent(true);
            }
          };
          dialog.addWindowListener(wl);
        }
      }
    });
    panel.add(btnExSelectDataArea, "cell 2 3,growy");

    JPanel pnNorth = new JPanel();
    getPnContent().add(pnNorth, BorderLayout.NORTH);
    pnNorth.setLayout(new MigLayout("", "[]", "[]"));

    cbQuantify = new JCheckBox("Quantify");
    pnNorth.add(cbQuantify, "cell 0 0");
  }

  /**
   * add ex standard for one point
   * 
   * @param img
   */
  protected void setExStandard(Image2D img) {
    if (img == null) {
      getTxtExTitle().setText("");
      getTxtExPath().setText("");
    } else {
      getTxtExTitle().setText(img.getTitle());
      getTxtExPath().setText(img.getSettings().getSettImage().getRAWFilepath());
    }
    // update settings
    imgEx = img;
    // create settings
    writeAllToSettings();
    // set to settings
    // SettingsImage2DQuantifierOnePoint ex = null;
    // if(!SettingsImage2DQuantifierOnePoint.class.isInstance(getSettings())) {
    // // TODO create new settings
    // SettingsImage2DQuantifierOnePoint ex = new Se
    // }
    // else {
    // ex = (SettingsImage2DQuantifierOnePoint) getSettings();
    // ex.setImgEx(imgEx);
    // // changed
    // if(currentImage!=null)
    // currentImage.fireIntensityProcessingChanged();
    // }
  }


  // ################################################################################################
  // Autoupdate
  @Override
  public void addAutoupdater(ActionListener al, ChangeListener cl, DocumentListener dl,
      ColorChangedListener ccl, ItemListener il) {
    getTxtLinearIntercept().getDocument().addDocumentListener(dl);
    getTxtLinearSlope().getDocument().addDocumentListener(dl);

    getTxtExTitle().getDocument().addDocumentListener(dl);
    getTxtExPath().getDocument().addDocumentListener(dl);

    // is active?
    getCbQuantify().addItemListener(il);
  }

  @Override
  public void addAutoRepainter(ActionListener al, ChangeListener cl, DocumentListener dl,
      ColorChangedListener ccl, ItemListener il) {}

  // ################################################################################################
  // LOGIC
  // Paintsclae from Image
  @Override
  public void setAllViaExistingSettings(SettingsImage2DQuantifier sett) {
    ImageLogicRunner.setIS_UPDATING(false);
    //
    resetAll();
    // quantify?
    getCbQuantify().setSelected(sett.isActive());
    //
    lastMode = sett.getMode();
    // new reseted
    switch (sett.getMode()) {
      case LINEAR:
        getTabbedPane().setSelectedComponent(getTabLinear());
        SettingsImage2DQuantifierLinear linear = (SettingsImage2DQuantifierLinear) sett;
        getTabLinear().setVisible(true);
        getTxtLinearIntercept().setText(String.valueOf(linear.getIntercept()));
        getTxtLinearSlope().setText(String.valueOf(linear.getSlope()));
        break;
      case ONE_POINT:
        getTabbedPane().setSelectedComponent(getTabOnePoint());
        SettingsImage2DQuantifierOnePoint ex = (SettingsImage2DQuantifierOnePoint) sett;
        imgEx = ex.getImgEx();
        if (imgEx != null) {
          getTxtExTitle().setText(imgEx.getTitle());
          getTxtExPath().setText(imgEx.getSettings().getSettImage().getRAWFilepath());
        }
        break;
    }
    // finished
    ImageLogicRunner.setIS_UPDATING(true);
    // ImageEditorWindow.getEditor().fireUpdateEvent(true);
  }

  private void resetAll() {
    imgEx = null;
    getTxtExTitle().setText("");
    getTxtExPath().setText("");
  }

  @Override
  public SettingsImage2DQuantifier writeAllToSettings(SettingsImage2DQuantifier sett) {
    boolean update = false;
    try {
      // quantify
      update = sett == null || sett.setActive(getCbQuantify().isSelected());
      // get mode
      MODE mode = SettingsImage2DQuantifier.MODE.LINEAR;
      if (getTabbedPane().getSelectedComponent().equals(getTabOnePoint()))
        mode = MODE.ONE_POINT;
      // update processing

      //
      switch (mode) {
        case LINEAR:
          if (sett == null || !(sett instanceof SettingsImage2DQuantifierLinear)) {
            sett = new SettingsImage2DQuantifierLinear();
            sett.setActive(getCbQuantify().isSelected());
            setSettings(sett, true);
            update = sett.isActive();
          }
          // set all settings in
          SettingsImage2DQuantifierLinear linear = (SettingsImage2DQuantifierLinear) sett;
          update = (linear.setIntercept(doubleFromTxt(getTxtLinearIntercept())) && sett.isActive())
              || update;
          update =
              (linear.setSlope(doubleFromTxt(getTxtLinearSlope())) && sett.isActive()) || update;
          currentImage.getSettings().replaceSettings(sett, true);
          break;
        case ONE_POINT:
          if (sett == null || !(sett instanceof SettingsImage2DQuantifierOnePoint)) {
            sett = new SettingsImage2DQuantifierOnePoint(imgEx);
            sett.setActive(getCbQuantify().isSelected());
            setSettings(sett, true);
            update = sett.isActive();
          }
          SettingsImage2DQuantifierOnePoint ex = (SettingsImage2DQuantifierOnePoint) sett;
          currentImage.getSettings().replaceSettings(sett, true);
          update = (ex.setImgEx(imgEx) && sett.isActive()) || update;
          break;
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    } finally {
      // important
      if (currentImage != null && update)
        currentImage.fireIntensityProcessingChanged();
    }
    return sett;
  }


  @Override
  public void setSettings(SettingsImage2DQuantifier settings, boolean setAllToPanel) {
    if (settings == null)
      settings = new SettingsImage2DQuantifierLinear();
    super.setSettings(settings, setAllToPanel);
  }

  // ################################################################################################
  // GETTERS AND SETTERS
  public JPanel getTabLinear() {
    return tabLinear;
  }

  public JCheckBox getCbQuantify() {
    return cbQuantify;
  }

  public JTextField getTxtLinearSlope() {
    return txtLinearSlope;
  }

  public JTextField getTxtLinearIntercept() {
    return txtLinearIntercept;
  }

  public JPanel getTabOnePoint() {
    return tabOnePoint;
  }

  public JTextField getTxtExPath() {
    return txtExPath;
  }

  public JTextField getTxtExTitle() {
    return txtExTitle;
  }

  public JTabbedPane getTabbedPane() {
    return tabbedPane;
  }
}
