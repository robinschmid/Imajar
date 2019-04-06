package net.rs.lamsi.multiimager.Frames.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.general.framework.modules.Module;
import net.rs.lamsi.general.settings.image.sub.SettingsGeneralImage.XUNIT;
import net.rs.lamsi.general.settings.importexport.SettingsImageDataImportTxt;
import net.rs.lamsi.general.settings.importexport.SettingsImageDataImportTxt.IMPORT;
import net.rs.lamsi.general.settings.importexport.SettingsImageDataImportTxt.ModeData;
import net.rs.lamsi.multiimager.Frames.ImageLogicRunner;
import net.rs.lamsi.utils.FileAndPathUtil;
import net.rs.lamsi.utils.useful.FileNameExtFilter;

public class ImportDataDialog extends JDialog {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  //
  protected SettingsImageDataImportTxt settingsDataImport = null;
  protected ImageLogicRunner runner;
  private final ImportDataDialog thisframe;
  // presets
  private ArrayList<SettingsImageDataImportTxt> presets;
  private JTextField txtOwnSeperation;
  private JPanel tabTXT;
  private JCheckBox chckbxSearchForMeta;
  private JPanel panel;
  private JPanel tab2DIntensity;
  private JLabel lblLoadAnImage;
  private JTextField txt2DOwn;
  private JCheckBox cb2DMetadata;
  private JPanel tabMSPresets;
  private JPanel panel_1;
  private JRadioButton rbMP17Thermo;
  private JCheckBox cbContinousData;
  private JLabel lblStartsWith;
  private JTextField txtStartsWith;
  private JLabel lblEndsWith;
  private JTextField txtEndsWith;
  private JScrollPane scrollPane;
  private JList listPresets;
  private JButton btnLoadPreset;
  private JButton btnSavePreset;
  private JTabbedPane tabbedPane;
  private JCheckBox cbFilesInSeparateFolders;
  private JRadioButton rbNeptune;
  private final ButtonGroup buttonGroup_2 = new ButtonGroup();
  private JTextField txtSeparationOwnSpecial;
  private JComboBox combo2DDataMode;
  private JLabel lblData;
  private JComboBox comboSep;
  private JLabel lblSeparation;
  private JComboBox comboSepLines;
  private JPanel panel_2;
  private JPanel panel_3;
  private JLabel lblFilter;
  private JLabel lblScanLines;
  private JLabel lblDataPoints;
  private JTextField txtFirstLine;
  private JTextField txtFirstDP;
  private JLabel lblTo;
  private JLabel lblTo_1;
  private JTextField txtLastLine;
  private JTextField txtLastDP;
  private JLabel lblFirst;
  private JLabel lblLast;
  private JPanel pnContinuousSplit;
  private JCheckBox cbHardSplit;
  private JLabel lblsp1;
  private JTextField txtSplitAfter;
  private JComboBox comboSplitXUnit;
  private JLabel lblStartX;
  private JTextField txtSplitStart;
  private JPanel south;
  private JLabel lblExcludeColumns;
  private JTextField txtExcludeColumns;
  private JCheckBox cbNoXData;
  private JRadioButton rbiCAPQOneRow;
  private JComboBox comboSeparationSpecial;
  private JRadioButton rbShimadzuICPMS;
  private JPanel panel_4;
  private JPanel panel_5;
  private JCheckBox cbShiftXValues;
  private JCheckBox cbShowImageSetup;
  private JRadioButton rbShimadzuNeu;
  private JRadioButton rbElement2;
  private JPanel pnSkipLines;
  private JLabel lblTextLines;
  private JLabel lblSkipFirst;
  private JTextField txtSkipFirstRows;
  private Component horizontalStrut;
  private JLabel lblSkipBetweenTitles;
  private JTextField txtSkipRowsTitlesToData;
  private JRadioButton rbPresetArne;
  private JPanel tabIMZML;
  private JPanel panel_7;
  private JButton btnOpenMzList;
  private JCheckBox cbUseMZWindow;
  private JTextField txtIMZMLWindow;
  private JScrollPane scrollPane_1;
  private JTextArea txtIMZMLList;
  private JButton btnSaveMzList;


  /**
   * Create the dialog.
   */
  public ImportDataDialog(ImageLogicRunner runner2) {
    setTitle("Import data");
    this.runner = runner2;
    thisframe = this;
    setBounds(100, 100, 573, 637);
    getContentPane().setLayout(new BorderLayout());
    {
      tabbedPane = new JTabbedPane(JTabbedPane.TOP);
      getContentPane().add(tabbedPane, BorderLayout.CENTER);
      {
        tabMSPresets = new JPanel();
        tabbedPane.addTab("MS presets", null, tabMSPresets, null);
        tabMSPresets.setLayout(new BorderLayout(0, 0));
        {
          panel_1 = new JPanel();
          tabMSPresets.add(panel_1, BorderLayout.NORTH);
          panel_1.setLayout(new MigLayout("", "[][][]", "[][][][][][]"));
          {
            rbMP17Thermo = new JRadioButton("iCAP-Q - Thermo Fisher Scientific");
            rbMP17Thermo.addActionListener(new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                getTxtEndsWith().setText("csv");
              }
            });
            buttonGroup_2.add(rbMP17Thermo);
            rbMP17Thermo.setSelected(true);
            panel_1.add(rbMP17Thermo, "cell 0 0");
          }
          {
            rbNeptune = new JRadioButton("Thermo Element sector field");
            rbNeptune.addActionListener(new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                getTxtEndsWith().setText("exp");
              }
            });
            {
              comboSeparationSpecial = new JComboBox();
              comboSeparationSpecial.setModel(new DefaultComboBoxModel(
                  new String[] {"COMMA", "SPACE", "TAB", "SEMICOLON", "OWN"}));
              comboSeparationSpecial.setSelectedIndex(0);
              comboSeparationSpecial.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent event) {
                  if (event.getStateChange() == ItemEvent.SELECTED) {
                    getTxtSpecialSeparation()
                        .setEditable(comboSeparationSpecial.getSelectedItem().equals("OWN"));
                  }
                }
              });
              panel_1.add(comboSeparationSpecial, "cell 1 0,growx");
            }
            {
              txtSeparationOwnSpecial = new JTextField();
              txtSeparationOwnSpecial.setToolTipText("Separation for columns (\\t for tab)");
              panel_1.add(txtSeparationOwnSpecial, "cell 2 0,growx");
              txtSeparationOwnSpecial.setColumns(5);
            }
            {
              rbiCAPQOneRow = new JRadioButton("iCAP-Q - all in one row");
              buttonGroup_2.add(rbiCAPQOneRow);
              panel_1.add(rbiCAPQOneRow, "cell 0 1");
            }
            {
              rbPresetArne = new JRadioButton("arne");
              buttonGroup_2.add(rbPresetArne);
              panel_1.add(rbPresetArne, "cell 1 1 2 1");
            }
            {
              rbShimadzuICPMS = new JRadioButton("Sh");
              buttonGroup_2.add(rbShimadzuICPMS);
              panel_1.add(rbShimadzuICPMS, "cell 0 2");
            }
            {
              rbShimadzuNeu = new JRadioButton("Sh neu");
              buttonGroup_2.add(rbShimadzuNeu);
              panel_1.add(rbShimadzuNeu, "cell 0 3");
            }
            buttonGroup_2.add(rbNeptune);
            panel_1.add(rbNeptune, "flowy,cell 0 4");
          }
          {
            rbElement2 = new JRadioButton("Thermo Element 2");
            buttonGroup_2.add(rbElement2);
            panel_1.add(rbElement2, "cell 0 5");
          }
        }
        {
          scrollPane = new JScrollPane();
          tabMSPresets.add(scrollPane, BorderLayout.CENTER);
          {
            listPresets = new JList(new DefaultListModel<String>());
            scrollPane.setViewportView(listPresets);
            listPresets.addListSelectionListener(new ListSelectionListener() {
              @Override
              public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && getListPresets().getSelectedIndex() != -1) {
                  setAllViaExistingSettings(getListPresets().getSelectedIndex());
                  listPresets.clearSelection();
                }
              }
            });
          }
        }
      }
      {
        panel = new JPanel();
        tabbedPane.addTab("2D Intensity", null, panel, null);
        panel.setLayout(new BorderLayout(0, 0));
        {
          tab2DIntensity = new JPanel();
          panel.add(tab2DIntensity);
          tab2DIntensity.setLayout(new MigLayout("", "[][][]", "[][][][][][][]"));
          {
            lblLoadAnImage =
                new JLabel("Load an image from one 2D intensity matrix file (.txt or .csv)");
            tab2DIntensity.add(lblLoadAnImage, "cell 0 0 3 1");
          }
          {
            lblData = new JLabel("Data:");
            tab2DIntensity.add(lblData, "cell 0 2,alignx trailing");
          }
          {
            combo2DDataMode = new JComboBox();
            combo2DDataMode.setModel(new DefaultComboBoxModel(ModeData.values()));
            combo2DDataMode.setSelectedIndex(0);
            tab2DIntensity.add(combo2DDataMode, "cell 1 2,growx");
          }
          {
            lblSeparation = new JLabel("Separation:");
            tab2DIntensity.add(lblSeparation, "cell 0 3,alignx trailing");
          }
          {
            comboSep = new JComboBox();
            comboSep.setModel(new DefaultComboBoxModel(
                new String[] {"COMMA", "SPACE", "TAB", "SEMICOLON", "OWN"}));
            comboSep.setSelectedIndex(0);
            tab2DIntensity.add(comboSep, "cell 1 3,growx");
            comboSep.addItemListener(new ItemListener() {
              @Override
              public void itemStateChanged(ItemEvent event) {
                if (event.getStateChange() == ItemEvent.SELECTED) {
                  getTxt2DOwnSeparation().setEditable(comboSep.getSelectedItem().equals("OWN"));
                }
              }
            });
          }
          {
            txt2DOwn = new JTextField();
            txt2DOwn.setToolTipText("Own seperation");
            txt2DOwn.setEditable(false);
            txt2DOwn.setColumns(10);
            tab2DIntensity.add(txt2DOwn, "cell 2 3,growx");
          }
          {
            cb2DMetadata = new JCheckBox("Search for meta data");
            cb2DMetadata.setToolTipText("Tries to search for meta data (non XI-paired data)");
            cb2DMetadata.setSelected(true);
            tab2DIntensity.add(cb2DMetadata, "cell 1 5");
          }
        }
      }
      {
        tabTXT = new JPanel();
        tabbedPane.addTab("Lines", null, tabTXT, null);
        tabTXT.setLayout(new MigLayout("", "[][][]", "[][][][][][][][][][][]"));
        {
          JLabel lblImportFromText =
              new JLabel("Import from text files. Select all files containing scan lines");
          tabTXT.add(lblImportFromText, "cell 0 0 3 1");
        }
        {
          JLabel lblDataSeperationBy = new JLabel("Separation:");
          tabTXT.add(lblDataSeperationBy, "cell 0 2,alignx trailing");
        }
        {
          comboSepLines = new JComboBox();
          comboSepLines.setModel(
              new DefaultComboBoxModel(new String[] {"COMMA", "SPACE", "TAB", "SEMICOLON", "OWN"}));
          comboSepLines.setSelectedIndex(0);
          tabTXT.add(comboSepLines, "cell 1 2,growx");
          comboSepLines.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
              if (event.getStateChange() == ItemEvent.SELECTED) {
                getTxtOwnSeparation().setEditable(comboSepLines.getSelectedItem().equals("OWN"));
              }
            }
          });
        }
        {
          txtOwnSeperation = new JTextField();
          txtOwnSeperation.setEditable(false);
          txtOwnSeperation.setToolTipText("Own seperation");
          tabTXT.add(txtOwnSeperation, "cell 2 2,alignx left");
          txtOwnSeperation.setColumns(10);
        }
        {
          lblExcludeColumns = new JLabel("Exclude columns:");
          tabTXT.add(lblExcludeColumns, "cell 0 4,alignx trailing");
        }
        {
          txtExcludeColumns = new JTextField();
          txtExcludeColumns.setToolTipText(
              "Input: 1,2,3 or 1-3,5... Exclude columns from data import (if x-data is in column 2, exclude column 1)");
          tabTXT.add(txtExcludeColumns, "cell 1 4 2 1,growx");
          txtExcludeColumns.setColumns(10);
        }
        {
          cbNoXData = new JCheckBox("No X-data");
          cbNoXData.setToolTipText("All columns are imported as intensities.");
          tabTXT.add(cbNoXData, "cell 1 5");
        }
        {
          chckbxSearchForMeta = new JCheckBox("Search for meta data");
          chckbxSearchForMeta.setToolTipText("Tries to search for meta data (non XI-paired data)");
          chckbxSearchForMeta.setSelected(true);
          tabTXT.add(chckbxSearchForMeta, "cell 1 7 2 1");
        }
        {
          {
            cbFilesInSeparateFolders = new JCheckBox("Files in separate folders");
            cbFilesInSeparateFolders.setToolTipText(
                "Unchecked: All files are in one folder. Checked: Each file has its own sub folder");
            tabTXT.add(cbFilesInSeparateFolders, "cell 1 8 2 1");
          }
        }
        cbContinousData = new JCheckBox("Continous data (one file=one image)");
        cbContinousData.addItemListener(new ItemListener() {
          @Override
          public void itemStateChanged(ItemEvent e) {
            getCbFilesInSeparateFolders().setVisible(!((JCheckBox) e.getSource()).isSelected());
            getPnContinuousSplit().setVisible(((JCheckBox) e.getSource()).isSelected());
          }
        });
        tabTXT.add(cbContinousData, "cell 1 9 2 1");
        {
          pnContinuousSplit = new JPanel();
          pnContinuousSplit.setVisible(false);
          tabTXT.add(pnContinuousSplit, "cell 1 10 2 1,grow");
          pnContinuousSplit.setLayout(new MigLayout("", "[][][]", "[][][]"));
          {
            cbHardSplit = new JCheckBox("Hard split");
            cbHardSplit.setToolTipText(
                "Performs a hard split with the splitting settings. This is recommended if the correct splitting settings are known.");
            cbHardSplit.setSelected(true);
            pnContinuousSplit.add(cbHardSplit, "cell 0 0 2 1");
          }
          {
            lblsp1 = new JLabel("Split after:");
            pnContinuousSplit.add(lblsp1, "flowx,cell 0 1");
          }
          {
            txtSplitAfter = new JTextField();
            txtSplitAfter.setToolTipText(
                "Split all lines after X data points (DP) or time units. (Leave empty for automatic estimation)");
            txtSplitAfter.setHorizontalAlignment(SwingConstants.RIGHT);
            pnContinuousSplit.add(txtSplitAfter, "cell 1 1");
            txtSplitAfter.setColumns(6);
          }
          {
            comboSplitXUnit = new JComboBox();
            comboSplitXUnit.setModel(new DefaultComboBoxModel(XUNIT.values()));
            comboSplitXUnit.setSelectedIndex(0);
            pnContinuousSplit.add(comboSplitXUnit, "cell 2 1,growx");
          }
          {
            lblStartX = new JLabel("Start x:");
            pnContinuousSplit.add(lblStartX, "cell 0 2,alignx trailing");
          }
          {
            txtSplitStart = new JTextField();
            txtSplitStart.setToolTipText(
                "Start x (time or data points) to be removed from the data. (relative to x0 the start time/DP)");
            txtSplitStart.setHorizontalAlignment(SwingConstants.RIGHT);
            txtSplitStart.setText("0");
            pnContinuousSplit.add(txtSplitStart, "cell 1 2,growx");
            txtSplitStart.setColumns(6);
          }
        }
      }
      {
        tabIMZML = new JPanel();
        tabbedPane.addTab("imzML", null, tabIMZML, null);
        tabIMZML.setLayout(new BorderLayout(0, 0));
        {
          panel_7 = new JPanel();
          tabIMZML.add(panel_7, BorderLayout.NORTH);
          {
            btnOpenMzList = new JButton("Open mz list");
            panel_7.add(btnOpenMzList);
          }
          {
            btnSaveMzList = new JButton("Save mz list");
            panel_7.add(btnSaveMzList);
          }
          {
            cbUseMZWindow = new JCheckBox("override mz window with");
            panel_7.add(cbUseMZWindow);
          }
          {
            txtIMZMLWindow = new JTextField();
            txtIMZMLWindow.setText("0.01");
            panel_7.add(txtIMZMLWindow);
            txtIMZMLWindow.setColumns(10);
          }
        }
        {
          scrollPane_1 = new JScrollPane();
          tabIMZML.add(scrollPane_1, BorderLayout.CENTER);
          {
            txtIMZMLList = new JTextArea();
            txtIMZMLList.setToolTipText(
                "Enter m/z center values (one per row) and optionally the m/z window width separated by a comma: center,window width");
            txtIMZMLList.setText("200,0.02");
            scrollPane_1.setViewportView(txtIMZMLList);
          }
        }
      }
    }
    {
      south = new JPanel();
      getContentPane().add(south, BorderLayout.SOUTH);
      south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
      {
        panel_4 = new JPanel();
        south.add(panel_4);
        panel_4.setLayout(new BorderLayout(0, 0));
        {
          panel_2 = new JPanel();
          panel_4.add(panel_2, BorderLayout.WEST);
          panel_2.setAlignmentX(Component.RIGHT_ALIGNMENT);
          panel_2.setLayout(new MigLayout("", "[][][][]", "[][][]"));
          {
            lblFilter = new JLabel("Filter:");
            lblFilter.setFont(new Font("Tahoma", Font.BOLD, 11));
            panel_2.add(lblFilter, "cell 0 0,alignx left");
          }
          {
            lblFirst = new JLabel("first");
            panel_2.add(lblFirst, "cell 1 0,alignx center");
          }
          {
            lblLast = new JLabel("last");
            panel_2.add(lblLast, "cell 3 0,alignx center");
          }
          {
            lblScanLines = new JLabel("Scan lines:");
            lblScanLines.setHorizontalAlignment(SwingConstants.TRAILING);
            panel_2.add(lblScanLines, "cell 0 1,alignx trailing");
          }
          {
            txtFirstLine = new JTextField();
            txtFirstLine.setHorizontalAlignment(SwingConstants.RIGHT);
            txtFirstLine
                .setToolTipText("First scan line to import (use 0 or no input for no filtering)");
            txtFirstLine.setText("0");
            panel_2.add(txtFirstLine, "cell 1 1,growx");
            txtFirstLine.setColumns(5);
          }
          {
            lblTo = new JLabel("to");
            panel_2.add(lblTo, "cell 2 1,alignx trailing");
          }
          {
            txtLastLine = new JTextField();
            txtLastLine.setToolTipText("Last scan line to import (use no input for no filtering)");
            txtLastLine.setHorizontalAlignment(SwingConstants.RIGHT);
            txtLastLine.setColumns(5);
            panel_2.add(txtLastLine, "cell 3 1,growx");
          }
          {
            lblDataPoints = new JLabel("Data points:");
            lblDataPoints.setHorizontalAlignment(SwingConstants.TRAILING);
            panel_2.add(lblDataPoints, "cell 0 2,alignx trailing");
          }
          {
            txtFirstDP = new JTextField();
            txtFirstDP.setHorizontalAlignment(SwingConstants.RIGHT);
            txtFirstDP.setToolTipText(
                "First data point in a scan line to import (use 0 or no input for no filtering)");
            txtFirstDP.setText("0");
            panel_2.add(txtFirstDP, "cell 1 2,growx");
            txtFirstDP.setColumns(5);
          }
          {
            lblTo_1 = new JLabel("to");
            panel_2.add(lblTo_1, "cell 2 2,alignx trailing");
          }
          {
            txtLastDP = new JTextField();
            txtLastDP.setHorizontalAlignment(SwingConstants.RIGHT);
            txtLastDP.setToolTipText(
                "Last data point in a scan line to import (use no input for no filtering)");
            txtLastDP.setColumns(5);
            panel_2.add(txtLastDP, "cell 3 2,growx");
          }
        }
        {
          panel_5 = new JPanel();
          panel_4.add(panel_5, BorderLayout.CENTER);
          panel_5.setLayout(new MigLayout("", "[]", "[][]"));
          {
            cbShiftXValues = new JCheckBox("shift x values to zero");
            cbShiftXValues.setToolTipText(
                "If a line's x values start at x0, this values is subtracted from all other values to shift the x-axis to 0. (Useful for time or lateral dimensions)");
            cbShiftXValues.setSelected(true);
            panel_5.add(cbShiftXValues, "cell 0 0");
          }
          {
            cbShowImageSetup = new JCheckBox("show image setup dialog");
            panel_5.add(cbShowImageSetup, "cell 0 1");
          }
        }
      }
      {
        pnSkipLines = new JPanel();
        south.add(pnSkipLines);
        {
          lblTextLines = new JLabel("Text lines:");
          lblTextLines.setFont(new Font("Tahoma", Font.BOLD, 11));
          pnSkipLines.add(lblTextLines);
        }
        {
          lblSkipFirst = new JLabel("skip first");
          pnSkipLines.add(lblSkipFirst);
        }
        {
          txtSkipFirstRows = new JTextField();
          txtSkipFirstRows.setToolTipText("Skip the first lines in the text files");
          txtSkipFirstRows.setText("0");
          txtSkipFirstRows.setHorizontalAlignment(SwingConstants.RIGHT);
          txtSkipFirstRows.setColumns(5);
          pnSkipLines.add(txtSkipFirstRows);
        }
        {
          horizontalStrut = Box.createHorizontalStrut(20);
          pnSkipLines.add(horizontalStrut);
        }
        {
          lblSkipBetweenTitles = new JLabel("skip between titles and data");
          pnSkipLines.add(lblSkipBetweenTitles);
        }
        {
          txtSkipRowsTitlesToData = new JTextField();
          txtSkipRowsTitlesToData
              .setToolTipText("Skip lines in the text file between the title line and data ");
          txtSkipRowsTitlesToData.setText("0");
          txtSkipRowsTitlesToData.setHorizontalAlignment(SwingConstants.RIGHT);
          txtSkipRowsTitlesToData.setColumns(5);
          pnSkipLines.add(txtSkipRowsTitlesToData);
        }
      }
      {
        panel_3 = new JPanel();
        south.add(panel_3);
        {
          lblStartsWith = new JLabel("Starts with:");
          panel_3.add(lblStartsWith);
        }
        {
          txtStartsWith = new JTextField();
          panel_3.add(txtStartsWith);
          txtStartsWith.setColumns(10);
        }
        {
          lblEndsWith = new JLabel("Ends with:");
          panel_3.add(lblEndsWith);
        }
        {
          txtEndsWith = new JTextField();
          panel_3.add(txtEndsWith);
          txtEndsWith.setText("csv");
          txtEndsWith.setColumns(10);
        }
      }
      {
        JPanel buttonPane = new JPanel();
        south.add(buttonPane);
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        {
          JButton okButton = new JButton("Open");
          okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              // create settings
              createSettingsForImport();
              // open data with settings but first open filechoose
              runner.importDataToImage(settingsDataImport);
            }
          });
          {
            btnSavePreset = new JButton("Save Preset");
            btnSavePreset.addActionListener(new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                try {
                  SettingsImageDataImportTxt sett = createSettingsForImport();
                  File file = sett.saveToXML(thisframe);
                  if (file != null)
                    addPreset(sett, FileAndPathUtil.eraseFormat(file.getName()));
                } catch (Exception e1) {
                  logger.error("Cannot save preset {}", e1.getMessage(), e);
                }
              }
            });
            buttonPane.add(btnSavePreset);
          }
          {
            btnLoadPreset = new JButton("Load Preset");
            btnLoadPreset.addActionListener(new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                try {
                  SettingsImageDataImportTxt sett = new SettingsImageDataImportTxt();
                  sett.loadSettingsFromFile(thisframe);
                  setAllViaExistingSettings(sett);
                } catch (Exception e1) {
                  logger.error("", e1);
                  logger.error("Cannot load preset {}", e1.getMessage(), e);
                }
              }
            });
            buttonPane.add(btnLoadPreset);
          }
          okButton.setActionCommand("OK");
          buttonPane.add(okButton);
          getRootPane().setDefaultButton(okButton);
        }
        {
          JButton cancelButton = new JButton("Cancel");
          cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              // close
              setVisible(false);
            }
          });
          cancelButton.setActionCommand("Cancel");
          buttonPane.add(cancelButton);
        }
      }
    }

    // end of init
    // load presets to list
    loadPresets();
  }

  protected void setAllViaExistingSettings(int i) {
    setAllViaExistingSettings(presets.get(i));
  }

  protected void setAllViaExistingSettings(SettingsImageDataImportTxt sett) {
    // set active tab
    if (SettingsImageDataImportTxt.class.isInstance(sett)) {
      SettingsImageDataImportTxt s = sett;
      // filename
      getTxtStartsWith().setText(s.getFilter().getStartsWith());
      getTxtEndsWith().setText(s.getFilter().getExt());
      // sep
      String sep = s.getSeparation();
      // txt
      // line dp limits
      getTxtFirstLine().setText(String.valueOf(s.getStartLine()));
      getTxtLastLine().setText(String.valueOf(s.getEndLine()));
      getTxtFirstDP().setText(String.valueOf(s.getStartDP()));
      getTxtLastDP().setText(String.valueOf(s.getEndDP()));

      // skip text lines
      getTxtSkipFirstRows().setText(String.valueOf(s.getSkipFirstLines()));
      getTxtSkipRowsTitlesToData().setText(String.valueOf(s.getSkipLinesBetweenTitleData()));


      getCbShiftXValues().setSelected(sett.isShiftXValues());

      // mode = tab
      IMPORT mode = s.getModeImport();
      switch (mode) {
        case ONE_FILE_2D_INTENSITY:
          getTabbedPane().setSelectedComponent(getTab2DIntensity());
          // "COMMA", "SPACE", "TAB", "SEMICOLON", "OWN"
          // seperation
          if (sep.equals(","))
            getComboSep().setSelectedIndex(0);
          else if (sep.equals(" "))
            getComboSep().setSelectedIndex(1);
          else if (sep.equals("	"))
            getComboSep().setSelectedIndex(2);
          else if (sep.equals(";"))
            getComboSep().setSelectedIndex(3);
          else {
            getComboSep().setSelectedIndex(4);
            getTxt2DOwnSeparation().setText(sep);
          }

          // check for meta
          getCb2DMetadata().setSelected(sett.isSearchingForMetaData());

          break;
        case CONTINOUS_DATA_TXT_CSV:
        case MULTIPLE_FILES_LINES_TXT_CSV:
          getTabbedPane().setSelectedComponent(getTabTXT());

          // seperation
          if (sep.equals(","))
            getComboSepLines().setSelectedIndex(0);
          else if (sep.equals(" "))
            getComboSepLines().setSelectedIndex(1);
          else if (sep.equals("	"))
            getComboSepLines().setSelectedIndex(2);
          else if (sep.equals(";"))
            getComboSepLines().setSelectedIndex(3);
          else {
            getComboSepLines().setSelectedIndex(4);
            getTxtOwnSeparation().setText(sep);
          }

          // no x and exclusion
          getTxtExcludeColumns().setText(s.getExcludeColumns());
          getCbNoXData().setSelected(s.isNoXData());

          // split
          getTxtSplitAfter().setText(String.valueOf(s.getSplitAfter()));
          getTxtSplitStart().setText(String.valueOf(s.getSplitStart()));
          getComboSplitXUnit().setSelectedItem(s.getSplitUnit());
          getCbHardSplit().setSelected(s.isUseHardSplit());

          // check for meta
          getChckbxSearchForMeta().setSelected(sett.isSearchingForMetaData());
          getCbContinousData().setSelected(mode == IMPORT.CONTINOUS_DATA_TXT_CSV);
          getCbFilesInSeparateFolders().setSelected(s.isFilesInSeparateFolders());
          break;
        case PRESETS_THERMO_iCAPQ:
          getTabbedPane().setSelectedComponent(getTabMSPresets());

          // seperation
          if (sep.equals(","))
            getComboSeparationSpecial().setSelectedIndex(0);
          else if (sep.equals(" "))
            getComboSeparationSpecial().setSelectedIndex(1);
          else if (sep.equals("	"))
            getComboSeparationSpecial().setSelectedIndex(2);
          else if (sep.equals(";"))
            getComboSeparationSpecial().setSelectedIndex(3);
          else {
            getComboSeparationSpecial().setSelectedIndex(4);
            getTxtSpecialSeparation().setText(sep);
          }
          getRbMP17Thermo().setSelected(true);
          break;
        case PRESETS_THERMO_iCAPQ_ONE_ROW:
          getTabbedPane().setSelectedComponent(getTabMSPresets());

          // seperation
          if (sep.equals(","))
            getComboSeparationSpecial().setSelectedIndex(0);
          else if (sep.equals(" "))
            getComboSeparationSpecial().setSelectedIndex(1);
          else if (sep.equals("	"))
            getComboSeparationSpecial().setSelectedIndex(2);
          else if (sep.equals(";"))
            getComboSeparationSpecial().setSelectedIndex(3);
          else {
            getComboSeparationSpecial().setSelectedIndex(4);
            getTxtSpecialSeparation().setText(sep);
          }
          getRbiCAPQOneRow().setSelected(true);
          break;

        case PRESETS_SHIMADZU_ICP_MS:
          getTabbedPane().setSelectedComponent(getTabMSPresets());

          // seperation
          if (sep.equals(","))
            getComboSeparationSpecial().setSelectedIndex(0);
          else if (sep.equals(" "))
            getComboSeparationSpecial().setSelectedIndex(1);
          else if (sep.equals("	"))
            getComboSeparationSpecial().setSelectedIndex(2);
          else if (sep.equals(";"))
            getComboSeparationSpecial().setSelectedIndex(3);
          else {
            getComboSeparationSpecial().setSelectedIndex(4);
            getTxtSpecialSeparation().setText(sep);
          }
          rbShimadzuICPMS.setSelected(true);
          break;
        case PRESETS_SHIMADZU_ICP_MS_2:
          getTabbedPane().setSelectedComponent(getTabMSPresets());

          // seperation
          if (sep.equals(","))
            getComboSeparationSpecial().setSelectedIndex(0);
          else if (sep.equals(" "))
            getComboSeparationSpecial().setSelectedIndex(1);
          else if (sep.equals(" "))
            getComboSeparationSpecial().setSelectedIndex(2);
          else if (sep.equals(";"))
            getComboSeparationSpecial().setSelectedIndex(3);
          else {
            getComboSeparationSpecial().setSelectedIndex(4);
            getTxtSpecialSeparation().setText(sep);
          }
          rbShimadzuNeu.setSelected(true);
          break;
        case PRESETS_THERMO_ELEMENT2:
          getTabbedPane().setSelectedComponent(getTabMSPresets());

          // seperation
          if (sep.equals(","))
            getComboSeparationSpecial().setSelectedIndex(0);
          else if (sep.equals(" "))
            getComboSeparationSpecial().setSelectedIndex(1);
          else if (sep.equals(" "))
            getComboSeparationSpecial().setSelectedIndex(2);
          else if (sep.equals(";"))
            getComboSeparationSpecial().setSelectedIndex(3);
          else {
            getComboSeparationSpecial().setSelectedIndex(4);
            getTxtSpecialSeparation().setText(sep);
          }
          rbElement2.setSelected(true);
          break;
        case PRESETS_ARNE:
          getTabbedPane().setSelectedComponent(getTabMSPresets());

          // seperation
          if (sep.equals(","))
            getComboSeparationSpecial().setSelectedIndex(0);
          else if (sep.equals(" "))
            getComboSeparationSpecial().setSelectedIndex(1);
          else if (sep.equals(" "))
            getComboSeparationSpecial().setSelectedIndex(2);
          else if (sep.equals(";"))
            getComboSeparationSpecial().setSelectedIndex(3);
          else {
            getComboSeparationSpecial().setSelectedIndex(4);
            getTxtSpecialSeparation().setText(sep);
          }
          rbPresetArne.setSelected(true);
          break;
      }
    } else {
      // xlsx?
    }
  }

  /**
   * init! load presets to list
   */
  private void loadPresets() {
    presets = new ArrayList<SettingsImageDataImportTxt>();
    // load files from directory as presets
    SettingsImageDataImportTxt sett = new SettingsImageDataImportTxt();

    if (sett != null) {
      File path = new File(FileAndPathUtil.getPathOfJar(), sett.getPathSettingsFile());
      String type = sett.getFileEnding();
      try {
        if (path.exists()) {
          List<File[]> files =
              FileAndPathUtil.findFilesInDir(path, new FileNameExtFilter("", type), false, false);
          // load each file as settings and add to menu as preset
          for (File f : files.get(0)) {
            // load
            try {
              sett.loadFromXML(f);
              if (sett != null)
                addPreset((SettingsImageDataImportTxt) sett.copy(),
                    FileAndPathUtil.eraseFormat(f.getName()));
            } catch (Exception ex) {
              logger.error("Preset is broken remove from settings directory: \n {}",
                  f.getAbsolutePath(), ex);
            }
          }
        }
      } catch (Exception ex) {
        logger.error("", ex);
      }
    }
  }



  /**
   * adds a preset to list
   * 
   * @param settings
   * @param title
   * @return
   */
  public void addPreset(final SettingsImageDataImportTxt settings, String title) {
    // add to lists
    ((DefaultListModel) (getListPresets().getModel())).addElement(title);
    presets.add(settings);
  }

  protected SettingsImageDataImportTxt createSettingsForImport() {
    // fileformatfilter
    FileNameExtFilter filter =
        new FileNameExtFilter(getTxtStartsWith().getText(), getTxtEndsWith().getText());
    // line/dp start/end (0 for no filter)
    int startLine = Module.intFromTxt(getTxtFirstLine());
    int endLine = Module.intFromTxt(getTxtLastLine());
    int startDP = Module.intFromTxt(getTxtFirstDP());
    int endDP = Module.intFromTxt(getTxtLastDP());

    // skip text lines
    int skipFirstLines = Module.intFromTxt(getTxtSkipFirstRows());
    int skipRowsBetweenTitlesAndData = Module.intFromTxt(getTxtSkipRowsTitlesToData());

    boolean isShiftXValues = getCbShiftXValues().isSelected();

    // text file
    if (getTabbedPane().getSelectedComponent().equals(getTabTXT())) {
      // seperation
      String separation = getSeparationChar(getComboSepLines(), getTxtOwnSeparation());
      // check for meta
      boolean checkformeta = getChckbxSearchForMeta().isSelected();
      boolean isContinousData = getCbContinousData().isSelected();
      boolean isFilesInSeparateFolders =
          isContinousData ? false : getCbFilesInSeparateFolders().isSelected();
      // continuous split
      float startSplitX = Module.floatFromTxt(getTxtSplitStart());
      float splitAfter = Module.floatFromTxt(getTxtSplitAfter());
      XUNIT unit = (XUNIT) comboSplitXUnit.getSelectedItem();
      boolean useHardSplit = getCbHardSplit().isSelected();

      // no X data and exclude columns
      String excludeColumns = getTxtExcludeColumns().getText();
      boolean usesXData = getCbNoXData().isSelected();
      // create settings
      settingsDataImport = new SettingsImageDataImportTxt(
          isContinousData ? IMPORT.CONTINOUS_DATA_TXT_CSV : IMPORT.MULTIPLE_FILES_LINES_TXT_CSV,
          checkformeta, separation, null, filter, isFilesInSeparateFolders, startLine, endLine,
          startDP, endDP, unit, startSplitX, splitAfter, useHardSplit, excludeColumns, usesXData,
          isShiftXValues);
    } else if (getTabbedPane().getSelectedComponent().equals(getTab2DIntensity())) {
      // seperation
      String separation = getSeparationChar(getComboSep(), getTxt2DOwnSeparation());
      // mode of data:
      ModeData mode = (ModeData) getCombo2DDataMode().getSelectedItem();

      // check for meta
      boolean checkformeta = getCb2DMetadata().isSelected();
      // create settings
      settingsDataImport = new SettingsImageDataImportTxt(IMPORT.ONE_FILE_2D_INTENSITY,
          checkformeta, separation, mode, filter, false, startLine, endLine, startDP, endDP);
    } else if (getTabbedPane().getSelectedComponent().equals(getTabMSPresets())) {
      String separation = getSeparationChar(getComboSeparationSpecial(), getTxtSpecialSeparation());
      boolean checkformeta = true;
      if (rbMP17Thermo.isSelected()) {
        IMPORT importMode = IMPORT.PRESETS_THERMO_iCAPQ;
        settingsDataImport = new SettingsImageDataImportTxt(importMode, checkformeta, separation,
            null, filter, false, startLine, endLine, startDP, endDP);
      } else if (getRbiCAPQOneRow().isSelected()) {
        IMPORT importMode = IMPORT.PRESETS_THERMO_iCAPQ_ONE_ROW;
        settingsDataImport = new SettingsImageDataImportTxt(importMode, checkformeta, separation,
            null, filter, false, startLine, endLine, startDP, endDP);
      } else if (rbNeptune.isSelected()) {
        IMPORT importMode = IMPORT.PRESETS_THERMO_NEPTUNE;
        settingsDataImport = new SettingsImageDataImportTxt(importMode, checkformeta, separation,
            null, filter, false, startLine, endLine, startDP, endDP);
      } else if (rbShimadzuICPMS.isSelected()) {
        IMPORT importMode = IMPORT.PRESETS_SHIMADZU_ICP_MS;
        settingsDataImport = new SettingsImageDataImportTxt(importMode, checkformeta, separation,
            null, filter, false, startLine, endLine, startDP, endDP);
      } else if (rbShimadzuNeu.isSelected()) {
        IMPORT importMode = IMPORT.PRESETS_SHIMADZU_ICP_MS_2;
        settingsDataImport = new SettingsImageDataImportTxt(importMode, checkformeta, separation,
            null, filter, false, startLine, endLine, startDP, endDP);
      } else if (rbElement2.isSelected()) {
        IMPORT importMode = IMPORT.PRESETS_THERMO_ELEMENT2;
        settingsDataImport = new SettingsImageDataImportTxt(importMode, checkformeta, separation,
            null, filter, false, startLine, endLine, startDP, endDP);
      } else if (rbPresetArne.isSelected()) {
        IMPORT importMode = IMPORT.PRESETS_ARNE;
        settingsDataImport = new SettingsImageDataImportTxt(importMode, checkformeta, separation,
            null, filter, false, startLine, endLine, startDP, endDP);
      }
    }

    settingsDataImport.setSkipLinesBetweenTitleData(skipRowsBetweenTitlesAndData);
    settingsDataImport.setSkipFirstLines(skipFirstLines);
    settingsDataImport.setOpenImageSetupDialog(cbShowImageSetup.isSelected());
    return settingsDataImport;
  }

  /**
   * 
   * @param s
   * @param for2DMatrix
   * @return separation character
   */
  private String getSeparationChar(JComboBox combo, JTextField txt) {
    Object s = combo.getSelectedItem();
    if (s.equals("COMMA"))
      return ",";
    else if (s.equals("TAB"))
      return "	";
    else if (s.equals("SPACE"))
      return " ";
    else if (s.equals("SEMICOLON"))
      return ";";
    else if (s.equals("OWN")) {
      return txt.getText();
    } else
      return ",";
  }

  public JTextField getTxtOwnSeparation() {
    return txtOwnSeperation;
  }

  public JTextField getTxt2DOwnSeparation() {
    return txt2DOwn;
  }

  public JPanel getTabTXT() {
    return tabTXT;
  }

  public JCheckBox getChckbxSearchForMeta() {
    return chckbxSearchForMeta;
  }

  public SettingsImageDataImportTxt getSettingsDataImport() {
    return settingsDataImport;
  }

  public JCheckBox getCb2DMetadata() {
    return cb2DMetadata;
  }

  public JPanel getTab2DIntensity() {
    return panel;
  }

  public JRadioButton getRbMP17Thermo() {
    return rbMP17Thermo;
  }

  public JPanel getTabMSPresets() {
    return tabMSPresets;
  }

  public JCheckBox getCbContinousData() {
    return cbContinousData;
  }

  public JTextField getTxtEndsWith() {
    return txtEndsWith;
  }

  public JTextField getTxtStartsWith() {
    return txtStartsWith;
  }

  public JList getListPresets() {
    return listPresets;
  }

  public JTabbedPane getTabbedPane() {
    return tabbedPane;
  }

  public JCheckBox getCbFilesInSeparateFolders() {
    return cbFilesInSeparateFolders;
  }

  public JTextField getTxtSpecialSeparation() {
    return txtSeparationOwnSpecial;
  }

  public JComboBox getComboSep() {
    return comboSep;
  }

  public JComboBox getCombo2DDataMode() {
    return combo2DDataMode;
  }

  public JComboBox getComboSepLines() {
    return comboSepLines;
  }

  public JTextField getTxtFirstDP() {
    return txtFirstDP;
  }

  public JTextField getTxtFirstLine() {
    return txtFirstLine;
  }

  public JTextField getTxtLastLine() {
    return txtLastLine;
  }

  public JTextField getTxtLastDP() {
    return txtLastDP;
  }

  public JPanel getPnContinuousSplit() {
    return pnContinuousSplit;
  }

  public JComboBox getComboSplitXUnit() {
    return comboSplitXUnit;
  }

  public JTextField getTxtSplitAfter() {
    return txtSplitAfter;
  }

  public JTextField getTxtSplitStart() {
    return txtSplitStart;
  }

  public JCheckBox getCbHardSplit() {
    return cbHardSplit;
  }

  public JCheckBox getCbNoXData() {
    return cbNoXData;
  }

  public JTextField getTxtExcludeColumns() {
    return txtExcludeColumns;
  }

  public JComboBox getComboSeparationSpecial() {
    return comboSeparationSpecial;
  }

  public JRadioButton getRbiCAPQOneRow() {
    return rbiCAPQOneRow;
  }

  public JCheckBox getCbShiftXValues() {
    return cbShiftXValues;
  }

  public JCheckBox getCbShowImageSetup() {
    return cbShowImageSetup;
  }

  public JRadioButton getRbElement2() {
    return rbElement2;
  }

  public JTextField getTxtSkipRowsTitlesToData() {
    return txtSkipRowsTitlesToData;
  }

  public JTextField getTxtSkipFirstRows() {
    return txtSkipFirstRows;
  }

  public JRadioButton getRbPresetArne() {
    return rbPresetArne;
  }

  public JCheckBox getCbUseMZWindow() {
    return cbUseMZWindow;
  }

  public JTextField getTxtIMZMLWindow() {
    return txtIMZMLWindow;
  }

  public JTextArea getTxtIMZMLList() {
    return txtIMZMLList;
  }
}
