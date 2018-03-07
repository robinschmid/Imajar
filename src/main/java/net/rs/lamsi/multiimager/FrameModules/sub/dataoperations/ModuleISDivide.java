package net.rs.lamsi.multiimager.FrameModules.sub.dataoperations;


import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
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
import net.rs.lamsi.general.settings.image.operations.quantifier.SettingsImage2DQuantifier.MODE;
import net.rs.lamsi.general.settings.image.operations.quantifier.SettingsImage2DQuantifierIS;
import net.rs.lamsi.general.settings.image.operations.quantifier.SettingsImage2DQuantifierIS.THRESHOLD_MODE;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageLogicRunner;
import net.rs.lamsi.multiimager.Frames.dialogs.selectdata.Image2DSelectDataAreaDialog;
import net.rs.lamsi.utils.DialogLoggerUtil;

public class ModuleISDivide
    extends Collectable2DSettingsModule<SettingsImage2DQuantifierIS, Image2D> {
  //
  private MODE lastMode = SettingsImage2DQuantifierIS.MODE.LINEAR;
  // save img IS
  private Image2D imgIS;
  //
  private ImageEditorWindow window;
  private final ButtonGroup buttonGroup = new ButtonGroup();
  private final ButtonGroup buttonGroup_1 = new ButtonGroup();
  private JCheckBox cbQuantify;
  private JTextField txtExTitle;
  private JTextField txtExPath;
  private final ButtonGroup buttonGroup_2 = new ButtonGroup();
  private JTextField txtFactor;
  private JTextField txtThreshold;
  private JTextField txtReplaceValue;
  private JCheckBox cbUseISMin;
  private JComboBox comboReplaceMode;
  private JCheckBox cbOnlyUseSelected;

  /**
   * Create the panel.
   */
  public ModuleISDivide(ImageEditorWindow wnd) {
    super("Quantifier", false, SettingsImage2DQuantifierIS.class, Image2D.class);
    getLbTitle().setText("IS divider");
    //
    window = wnd;

    JPanel pnNorth = new JPanel();
    getPnContent().add(pnNorth, BorderLayout.NORTH);
    pnNorth.setLayout(new MigLayout("", "[][grow][grow]", "[][][][grow][]"));

    cbQuantify = new JCheckBox("Quantify");
    cbQuantify.setToolTipText(
        "Each data point's intensity value is divided by the intensity value of the corresponding dp in the given IS image and multiplied by the given factor. This is done before applying a quantification strategy (e.g. linear/regression).");
    pnNorth.add(cbQuantify, "flowy,cell 0 0 2 1");

    JLabel lblFactor = new JLabel("factor");
    pnNorth.add(lblFactor, "cell 0 1,alignx right");

    txtFactor = new JTextField();
    txtFactor.setToolTipText(
        "The factor is multiplied with the result. (factor: e.g. a factor to handle different conentrations in the sample and standard)");
    txtFactor.setText("1");
    pnNorth.add(txtFactor, "cell 1 1,alignx left");
    txtFactor.setColumns(10);

    JLabel lblIsThreshold = new JLabel("IS threshold");
    pnNorth.add(lblIsThreshold, "cell 0 2,alignx trailing");

    txtThreshold = new JTextField();
    txtThreshold.setToolTipText(
        "Intensity threshold for the internal standard. If the intensity drops below this value one of the options below is applied (replace by average, minimum or set the value to 0)");
    txtThreshold.setText("0");
    pnNorth.add(txtThreshold, "cell 1 2,alignx left");
    txtThreshold.setColumns(10);

    cbUseISMin = new JCheckBox("use minimum of IS paint scale");
    cbUseISMin.setToolTipText(
        "Uses the minimum that was set in the paint scale settings of the internal standard image");
    cbUseISMin.setSelected(true);
    pnNorth.add(cbUseISMin, "cell 2 2");

    JLabel lblOperation = new JLabel("operation");
    pnNorth.add(lblOperation, "cell 0 3,alignx trailing");

    JPanel panel_1 = new JPanel();
    FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
    flowLayout.setAlignment(FlowLayout.LEFT);
    flowLayout.setVgap(0);
    flowLayout.setHgap(0);
    pnNorth.add(panel_1, "cell 1 3 2 1,grow");

    comboReplaceMode = new JComboBox();
    panel_1.add(comboReplaceMode);
    comboReplaceMode.setModel(new DefaultComboBoxModel(THRESHOLD_MODE.values()));
    comboReplaceMode.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        getTxtReplaceValue().setEditable((comboReplaceMode.getSelectedItem().toString()
            .equals(THRESHOLD_MODE.SET_TO_VALUE.toString())));
      }
    });
    comboReplaceMode.setSelectedIndex(0);

    cbOnlyUseSelected = new JCheckBox("only use selected dp");
    panel_1.add(cbOnlyUseSelected);
    cbOnlyUseSelected.setToolTipText("Uses selected data points only");

    txtReplaceValue = new JTextField();
    txtReplaceValue.setEnabled(false);
    txtReplaceValue.setToolTipText("Value for a replacement");
    txtReplaceValue.setText("0");
    pnNorth.add(txtReplaceValue, "cell 1 4");
    txtReplaceValue.setColumns(10);

    JPanel panel = new JPanel();
    getPnContent().add(panel, BorderLayout.CENTER);
    panel.setLayout(new MigLayout("", "[][][grow]", "[][][]"));

    JLabel lblTitle = new JLabel("Title");
    panel.add(lblTitle, "cell 0 0,alignx trailing");

    txtExTitle = new JTextField();
    panel.add(txtExTitle, "cell 1 0 2 1,growx");
    txtExTitle.setColumns(10);

    JLabel lblPath_1 = new JLabel("Path");
    panel.add(lblPath_1, "cell 0 1,alignx trailing");

    txtExPath = new JTextField();
    panel.add(txtExPath, "cell 1 1 2 1,growx");
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
              setISStandard(((Image2D) ((IconNode) img).getUserObject()));
            }
          }
        } catch (Exception ex) {
          setISStandard(null);
        }
      }
    });
    panel.add(btnChooseFromCurrent, "cell 1 2");

    JButton btnExSelectDataArea = new JButton("Select data area");
    btnExSelectDataArea.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // open dialog with image ex
        if (imgIS != null) {
          Image2DSelectDataAreaDialog dialog = new Image2DSelectDataAreaDialog();
          dialog.startDialog(imgIS);
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
    panel.add(btnExSelectDataArea, "cell 2 2,growy");
  }

  /**
   * add ex standard for one point
   * 
   * @param img
   */
  protected void setISStandard(Image2D img) {
    // update settings
    imgIS = img;
    if (img == null) {
      getTxtExTitle().setText("");
      getTxtExPath().setText("");
    } else {
      getTxtExTitle().setText(img.getTitle());
      getTxtExPath().setText(img.getSettings().getSettImage().getRAWFilepath());
    }
    // create settings
    writeAllToSettings();
    // set to settings
    // SettingsImage2DQuantifierISOnePoint ex = null;
    // if(!SettingsImage2DQuantifierISOnePoint.class.isInstance(getSettings())) {
    // // TODO create new settings
    // SettingsImage2DQuantifierISOnePoint ex = new Se
    // }
    // else {
    // ex = (SettingsImage2DQuantifierISOnePoint) getSettings();
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
    getTxtExTitle().getDocument().addDocumentListener(dl);
    getTxtExPath().getDocument().addDocumentListener(dl);
    getTxtFactor().getDocument().addDocumentListener(dl);

    getTxtReplaceValue().getDocument().addDocumentListener(dl);
    getTxtThreshold().getDocument().addDocumentListener(dl);

    getCbUseISMin().addItemListener(il);
    getComboReplaceMode().addItemListener(il);
    // is active?
    getCbQuantify().addItemListener(il);
    getCbOnlyUseSelected().addItemListener(il);
  }

  @Override
  public void addAutoRepainter(ActionListener al, ChangeListener cl, DocumentListener dl,
      ColorChangedListener ccl, ItemListener il) {}

  // ################################################################################################
  // LOGIC
  // Paintsclae from Image
  @Override
  public void setAllViaExistingSettings(SettingsImage2DQuantifierIS sett) {
    ImageLogicRunner.setIS_UPDATING(false);
    //
    if (sett != null) {
      getCbQuantify().setSelected(sett.isActive());
      getTxtFactor().setText(String.valueOf(sett.getConcentrationFactor()));

      getTxtReplaceValue().setText(String.valueOf(sett.getReplacementValue()));
      getTxtThreshold().setText(String.valueOf(sett.getThreshold()));

      getCbUseISMin().setSelected(sett.isUseISMinimumAsThreshold());
      getCbOnlyUseSelected().setSelected(sett.isOnlySelected());

      getComboReplaceMode().setSelectedItem(sett.getISMode());
      //
      imgIS = sett.getImgIS();
      if (imgIS != null) {
        getTxtExTitle().setText(imgIS.getTitle());
        getTxtExPath().setText(imgIS.getSettings().getSettImage().getRAWFilepath());
      } else {
        getTxtExTitle().setText("");
        getTxtExPath().setText("");
      }
    }
    // finished
    ImageLogicRunner.setIS_UPDATING(true);
    // ImageEditorWindow.getEditor().fireUpdateEvent(true);
  }

  @Override
  public SettingsImage2DQuantifierIS writeAllToSettings(SettingsImage2DQuantifierIS sett) {
    boolean update = false;
    try {
      // quantify
      update = sett == null || sett.setActive(getCbQuantify().isSelected());

      if (sett != null) {
        sett.setImgIS(imgIS);
        sett.setConcentrationFactor(doubleFromTxt(getTxtFactor()));
        sett.setMode((THRESHOLD_MODE) comboReplaceMode.getSelectedItem());
        sett.setReplacementValue(doubleFromTxt(getTxtReplaceValue()));
        sett.setThreshold(doubleFromTxt(getTxtThreshold()));
        sett.setUseISMinimumAsThreshold(getCbUseISMin().isSelected());
        sett.setOnlySelected(getCbOnlyUseSelected().isSelected());
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

  // ################################################################################################
  // GETTERS AND SETTERS
  public JCheckBox getCbQuantify() {
    return cbQuantify;
  }

  public JTextField getTxtExPath() {
    return txtExPath;
  }

  public JTextField getTxtExTitle() {
    return txtExTitle;
  }

  public JTextField getTxtFactor() {
    return txtFactor;
  }

  public JCheckBox getCbUseISMin() {
    return cbUseISMin;
  }

  public JTextField getTxtThreshold() {
    return txtThreshold;
  }

  public JComboBox getComboReplaceMode() {
    return comboReplaceMode;
  }

  public JTextField getTxtReplaceValue() {
    return txtReplaceValue;
  }

  public JCheckBox getCbOnlyUseSelected() {
    return cbOnlyUseSelected;
  }
}
