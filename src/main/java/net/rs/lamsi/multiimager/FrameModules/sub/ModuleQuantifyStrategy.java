package net.rs.lamsi.multiimager.FrameModules.sub;


import java.awt.BorderLayout;
import java.awt.FlowLayout;
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
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;

import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.massimager.Frames.FrameWork.ColorChangedListener;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.Collectable2DSettingsModule;
import net.rs.lamsi.massimager.Settings.image.operations.quantifier.Quantifier;
import net.rs.lamsi.massimager.Settings.image.operations.quantifier.SettingsImage2DQuantifier;
import net.rs.lamsi.massimager.Settings.image.operations.quantifier.SettingsImage2DQuantifierLinear;
import net.rs.lamsi.massimager.Settings.image.operations.quantifier.SettingsImage2DQuantifierMultiPoints;
import net.rs.lamsi.massimager.Settings.image.operations.quantifier.SettingsImage2DQuantifierOnePoint;
import net.rs.lamsi.multiimager.FrameModules.sub.quantifiertable.PnTableQuantifier;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageLogicRunner;
import net.rs.lamsi.multiimager.Frames.dialogs.selectdata.Image2DSelectDataAreaDialog;
import net.rs.lamsi.multiimager.Frames.dialogs.selectdata.Image2DSelectDataAreaDialog.SelectionMode;
import net.rs.lamsi.multiimager.Frames.dialogs.selectdata.RectSelection;
import net.rs.lamsi.utils.DialogLoggerUtil;

public class ModuleQuantifyStrategy extends Collectable2DSettingsModule<SettingsImage2DQuantifier, Image2D> { 
	//
	private int lastMode = 0;
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
	private JPanel tabMultiPoint; 
	private JTextField txtExConc;
	private JTextField txtExTitle;
	private JTextField txtExPath;
	private JTabbedPane tabbedPane;
	private final ButtonGroup buttonGroup_2 = new ButtonGroup();
	private JRadioButton rbExAverageSelectedAreas;
	private JRadioButton rbExAverage;
	private JTextField txtMultiConc;
	private PnTableQuantifier pnTable;
	private JCheckBox cbSplitQuantifierData;

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
		tabbedPane.addTab("One point", null, tabOnePoint, null);
		tabOnePoint.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		tabOnePoint.add(panel, BorderLayout.CENTER);
		panel.setLayout(new MigLayout("", "[][][grow]", "[][grow][][][][][][]"));
		
		JLabel lblOnePointExternal = new JLabel("One point external kalibration");
		panel.add(lblOnePointExternal, "cell 0 0 2 1");
		
		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel.add(panel_1, "cell 0 1 3 1,grow");
		
		rbExAverage = new JRadioButton("Average");
		rbExAverage.setToolTipText("Average over all points");
		buttonGroup_2.add(rbExAverage);
		rbExAverage.setSelected(true);
		panel_1.add(rbExAverage);
		
		rbExAverageSelectedAreas = new JRadioButton("Average selected areas");
		rbExAverageSelectedAreas.setEnabled(false);
		rbExAverageSelectedAreas.setToolTipText("Average over selected areas. Select areas with button below");
		buttonGroup_2.add(rbExAverageSelectedAreas);
		panel_1.add(rbExAverageSelectedAreas);
		
		JLabel lblC = new JLabel("C = ");
		panel.add(lblC, "cell 0 2,alignx trailing");
		
		txtExConc = new JTextField();
		txtExConc.setText("1");
		panel.add(txtExConc, "cell 1 2,growx");
		txtExConc.setColumns(10);
		
		JLabel lblTitle = new JLabel("Title");
		panel.add(lblTitle, "cell 0 3,alignx trailing");
		
		txtExTitle = new JTextField();
		panel.add(txtExTitle, "cell 1 3 2 1,growx");
		txtExTitle.setColumns(10);
		
		JLabel lblPath_1 = new JLabel("Path");
		panel.add(lblPath_1, "cell 0 4,alignx trailing");
		
		txtExPath = new JTextField();
		panel.add(txtExPath, "cell 1 4 2 1,growx");
		txtExPath.setColumns(10);
		
		JButton btnChooseFromCurrent = new JButton("Choose from current list");
		btnChooseFromCurrent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object[] list = window.getLogicRunner().getListImages().toArray(); 
				try {
					int i = DialogLoggerUtil.showListDialogAndChoose(window, list, ListSelectionModel.SINGLE_SELECTION)[0];
					setExStandard(((Image2D)list[i]));
				} catch(Exception ex) { 
					setExStandard(null);
				}
			}
		});
		panel.add(btnChooseFromCurrent, "cell 1 5");
		
		JButton btnExSelectDataArea = new JButton("Select data area");
		btnExSelectDataArea.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// open dialog with image ex
				if(imgEx!=null) {
					Image2DSelectDataAreaDialog dialog = new Image2DSelectDataAreaDialog();
					dialog.startDialog(imgEx);
					WindowAdapter wl = new WindowAdapter() {
						@Override
						public void windowClosed(WindowEvent e) { 
							super.windowClosed(e);
							// areas selected?
							getRbExAverageSelectedAreas().setEnabled(imgEx.getSelectedData().size()>0);
							if(imgEx.getSelectedData().size()>0) getRbExAverageSelectedAreas().setSelected(true);
							// changed
							window.fireUpdateEvent(true);
						}
					};
					dialog.addWindowListener(wl);
				}
			}
		});
		panel.add(btnExSelectDataArea, "cell 2 5 1 3,growy");
		
		JButton btnChooseFromRaw = new JButton("Choose from raw data");
		panel.add(btnChooseFromRaw, "cell 1 6,growx");
		
		JButton btnChooseFromImaged_1 = new JButton("Choose from image2d");
		panel.add(btnChooseFromImaged_1, "cell 1 7,growx");
		
		tabMultiPoint = new JPanel();
		tabbedPane.addTab("Multi point", null, tabMultiPoint, null);
		tabMultiPoint.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		tabMultiPoint.add(scrollPane, BorderLayout.CENTER);
		
		JPanel panel_2 = new JPanel();
		scrollPane.setViewportView(panel_2);
		panel_2.setLayout(new MigLayout("", "[][][grow]", "[][][][][][][][][grow]"));
		
		JLabel lblMultiPointKalibration = new JLabel("Multi point kalibration");
		panel_2.add(lblMultiPointKalibration, "cell 0 0 2 1");
		
		JButton btnShowRegression = new JButton("Show regression");
		btnShowRegression.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(currentImage.getSettings().getQuantifier().getMode()==SettingsImage2DQuantifierMultiPoints.MODE_MULTIPLE_POINTS)
					((SettingsImage2DQuantifierMultiPoints) currentImage.getSettings().getQuantifier()).openRegressionDialog();
			}
		});
		panel_2.add(btnShowRegression, "cell 1 1,growx");
		
		JLabel label = new JLabel("C = ");
		panel_2.add(label, "cell 0 2,alignx trailing");
		
		txtMultiConc = new JTextField();
		txtMultiConc.setText("1");
		txtMultiConc.setColumns(10);
		panel_2.add(txtMultiConc, "cell 1 2,growx");
		
		JButton btnMultiCurrent = new JButton("Choose from current list");
		btnMultiCurrent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object[] list = window.getLogicRunner().getListImages().toArray(); 
				try {
					int[] ind = DialogLoggerUtil.showListDialogAndChoose(window, list, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
					//
					for(int i : ind) { 
						addMultiStandard(((Image2D)list[i]));
					}
				} catch(Exception ex) { 
					setExStandard(null);
				}
			}
		});
		
		cbSplitQuantifierData = new JCheckBox("Try to split data");
		cbSplitQuantifierData.setToolTipText("Tries to split data of one image into different quantifier point.");
		cbSplitQuantifierData.setSelected(true);
		panel_2.add(cbSplitQuantifierData, "cell 1 3");
		panel_2.add(btnMultiCurrent, "cell 1 4,growx");
		
		JButton btnRemove = new JButton("Remove");
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int i = getPnTable().getTable().getSelectedRow();
				if(i!=-1)
					getPnTable().getTableModel().removeRow(i);
			}
		});
		
		JButton btnDuplicate = new JButton("Duplicate");
		btnDuplicate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 
				getPnTable().duplicateSelectedRows();
			}
		});
		btnDuplicate.setToolTipText("Duplicate the selected quantifier");
		panel_2.add(btnDuplicate, "cell 1 6,growx");
		panel_2.add(btnRemove, "flowx,cell 1 7");
		
		pnTable = new PnTableQuantifier(wnd) { 
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// TODO
				window.fireUpdateEvent(true);
			}
		};
		panel_2.add(pnTable, "cell 0 8 3 1,grow");
		
		JButton btnRemoveAll = new JButton("Remove all");
		btnRemoveAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getPnTable().getTableModel().removeAllRows();
			}
		});
		panel_2.add(btnRemoveAll, "cell 1 7");
		
		JPanel pnNorth = new JPanel();
		getPnContent().add(pnNorth, BorderLayout.NORTH);
		pnNorth.setLayout(new MigLayout("", "[]", "[]"));
		
		cbQuantify = new JCheckBox("Quantify");
		pnNorth.add(cbQuantify, "cell 0 0");
	}

	/**
	 * add ex standard for one point
	 * @param img
	 */
	protected void setExStandard(Image2D img) {
		if(img==null) {
			getTxtExTitle().setText("");
			getTxtExPath().setText("");
		}
		else { 
			getTxtExTitle().setText(img.getTitle());
			getTxtExPath().setText(img.getSettings().getSettImage().getRAWFilepath());
		}
		// update settings
		imgEx = img;
		// changed
		if(currentImage!=null)
			currentImage.fireIntensityProcessingChanged();
	}

	/**
	 * adds ex standard for multiple points
	 * can be one standard per img
	 * or multiple standards in one img
	 * 		has to separate std
	 * @param image2d
	 */
	protected void addMultiStandard(Image2D img) { 
		// search for multiple images in one standard
		if(getCbSplitQuantifierData().isSelected() && img.getLineCount(0)>2){
			Quantifier q = new Quantifier();
			q.setImg(img);
			q.setMode(Quantifier.MODE_AVERAGE_PER_LINE);
			q.setUseAll(true);
			double[] lineAv = q.getAverageIntensityForLines();
	
			double maxD = (lineAv[1]-lineAv[0]);
			double minD = (lineAv[1]-lineAv[0]);
			double avD = (lineAv[1]-lineAv[0]);
			double minAvgValue = lineAv[0]<lineAv[1]? lineAv[0] : lineAv[1];
			for(int i=2; i<img.getLineCount(0); i++) {
				double dif = Math.abs(lineAv[i]-lineAv[i-1]);
				if(dif<minD) minD = dif;
				if(dif>maxD) maxD = dif;
				avD += dif;
				// min value as blank
				if(lineAv[i]<minAvgValue) minAvgValue = lineAv[i];
			}
			avD = avD/img.getLineCount(0)-1;
			// search for contrast
			int lastSplit = 0;
			for(int i=1; i<img.getLineCount(0); i++) {
				double dif = Math.abs(lineAv[i]-lineAv[i-1]);
				if(dif>maxD*0.1 ) {
					// split image here i-1  
					try {
						// copy and set parent
						Image2D copy = img.getCopyChild();
						// set area: exclude all but not lastSplit to i-1
						if(lastSplit!=0)
							copy.getExcludedData().add(new RectSelection(SelectionMode.EXCLUDE, 0,0, copy.getData().getMaxDP()-1,lastSplit-1)); 
						copy.getExcludedData().add(new RectSelection(SelectionMode.EXCLUDE, 0,i, copy.getData().getMaxDP()-1, copy.getLineCount(0)-1));
						// add
						getPnTable().addQuantifier(copy);
						// 						
						lastSplit = i;
					} catch (Exception e) { 
						e.printStackTrace();
					}
				} 
			}
			// no image added?
			if(lastSplit==0) { 
				try {
					getPnTable().addQuantifier(img.getCopyChild());
				} catch (Exception e) { 
					e.printStackTrace();
				}
			}
			else {
				// add last split 
				try {
					// copy and set parent
					Image2D copy = img.getCopyChild();
					// set area: exclude all but not lastSplit to i-1
					copy.getExcludedData().add(new RectSelection(SelectionMode.EXCLUDE, 0,0, copy.getData().getMaxDP()-1,lastSplit-1)); 
					// add
					getPnTable().addQuantifier(copy);
				} catch (Exception e) { 
					e.printStackTrace();
				}
			}
		}
		else {
			// just add the image
			try {
				getPnTable().addQuantifier(img.getCopyChild());
			} catch (Exception e) { 
				e.printStackTrace();
			}
		}

		if(currentImage!=null)
			currentImage.fireIntensityProcessingChanged();
		// update event
		window.fireUpdateEvent(true);
	}

	//################################################################################################
	// Autoupdate
	@Override
	public void addAutoupdater(ActionListener al, ChangeListener cl, DocumentListener dl, ColorChangedListener ccl, ItemListener il) {
		getTxtLinearIntercept().getDocument().addDocumentListener(dl);
		getTxtLinearSlope().getDocument().addDocumentListener(dl);

		getTxtExConc().getDocument().addDocumentListener(dl);
		getTxtExTitle().getDocument().addDocumentListener(dl);
		getTxtExPath().getDocument().addDocumentListener(dl);
		
		getTxtMultiConc().getDocument().addDocumentListener(dl);
		
		// is active?
		getCbQuantify().addItemListener(il);

		getRbExAverage().addItemListener(il);
		getRbExAverageSelectedAreas().addItemListener(il);
	}

	@Override
	public void addAutoRepainter(ActionListener al, ChangeListener cl, DocumentListener dl, ColorChangedListener ccl, ItemListener il) {
	}
	//################################################################################################
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
		switch(sett.getMode()) {
		case SettingsImage2DQuantifier.MODE_LINEAR:
			getTabbedPane().setSelectedComponent(getTabLinear());
			SettingsImage2DQuantifierLinear linear = (SettingsImage2DQuantifierLinear) sett;
			getTabLinear().setVisible(true);
			getTxtLinearIntercept().setText(String.valueOf(linear.getIntercept()));
			getTxtLinearSlope().setText(String.valueOf(linear.getSlope()));
			break;
		case SettingsImage2DQuantifier.MODE_ONE_POINT:
			getTabbedPane().setSelectedComponent(getTabOnePoint());
			if(currentImage!=null && currentImage.getSettings().getQuantifier()!=null) {
				SettingsImage2DQuantifierOnePoint ex = (SettingsImage2DQuantifierOnePoint) currentImage.getSettings().getQuantifier();
				imgEx = ex.getImgEx().getImg();
				if(imgEx!=null) {
					getTxtExTitle().setText(imgEx.getTitle());
					getTxtExPath().setText(imgEx.getSettings().getSettImage().getRAWFilepath());
					// mode of ex
					getRbExAverage().setSelected(ex.getImgEx().getMode()==Quantifier.MODE_AVERAGE);
					getRbExAverageSelectedAreas().setSelected(ex.getImgEx().getMode()==Quantifier.MODE_AVERAGE_BOXES);
				}
				// conc
				getTxtExConc().setText(String.valueOf(ex.getConcentrationEx()));
			} 
			break;
		case SettingsImage2DQuantifier.MODE_MULTIPLE_POINTS:
			getTabbedPane().setSelectedComponent(getTabMultiPoint());
			if(currentImage!=null && currentImage.getSettings().getQuantifier()!=null) {
				SettingsImage2DQuantifierMultiPoints multi = (SettingsImage2DQuantifierMultiPoints) currentImage.getSettings().getQuantifier();
				// conc
				getTxtMultiConc().setText(String.valueOf(multi.getFactor()));
				// add all Quantifiers
				if(multi.getQuantifier()!=null)
					for(Quantifier q : multi.getQuantifier())
						getPnTable().addQuantifier(q);
			} 
			break;
		} 
		// finished
		ImageLogicRunner.setIS_UPDATING(true);
		ImageEditorWindow.getEditor().fireUpdateEvent(true);
	} 

	private void resetAll() {
		imgEx = null;
		getTxtExTitle().setText("");
		getTxtExPath().setText("");
		// conc
		getTxtExConc().setText(String.valueOf(1));
		//muti
		getTxtMultiConc().setText("1");
		getPnTable().getTableModel().removeAllRows();
	}

	@Override
	public SettingsImage2DQuantifier writeAllToSettings(SettingsImage2DQuantifier sett) {
		if(sett!=null) {
			try {
				// quantify
				sett.setActive(getCbQuantify().isSelected());
				// get mode
				int mode = SettingsImage2DQuantifier.MODE_LINEAR;
				if(getTabbedPane().getSelectedComponent().equals(getTabOnePoint())) mode = SettingsImage2DQuantifier.MODE_ONE_POINT;
				if(getTabbedPane().getSelectedComponent().equals(getTabMultiPoint())) mode = SettingsImage2DQuantifier.MODE_MULTIPLE_POINTS;
				
				// 
				switch(mode) {
				case SettingsImage2DQuantifier.MODE_LINEAR:
					if(lastMode!=mode) {
						SettingsImage2DQuantifierLinear sett2 = new SettingsImage2DQuantifierLinear();
						sett2.setActive(sett.isActive());
						sett = sett2;
					}
					// set all settings in 
					SettingsImage2DQuantifierLinear linear = (SettingsImage2DQuantifierLinear) sett;
					linear.setIntercept(doubleFromTxt(getTxtLinearIntercept()));
					linear.setSlope(doubleFromTxt(getTxtLinearSlope()));
					System.out.println("SETTINGS SETTINGS A and B");
					break;
				case SettingsImage2DQuantifier.MODE_ONE_POINT:
					if(lastMode!=mode) {
						SettingsImage2DQuantifierOnePoint sett2 = new SettingsImage2DQuantifierOnePoint(imgEx);
						sett2.setActive(sett.isActive());
						sett = sett2;
					}
					SettingsImage2DQuantifierOnePoint ex = (SettingsImage2DQuantifierOnePoint) sett;
					ex.setConcentrationEx(doubleFromTxt(getTxtExConc()));
					ex.setImgEx(imgEx); 
					// mode of ex
					ex.getImgEx().setMode(getRbExAverage().isSelected()? Quantifier.MODE_AVERAGE : Quantifier.MODE_AVERAGE_BOXES);
					break;
				case SettingsImage2DQuantifier.MODE_MULTIPLE_POINTS:
					if(lastMode!=mode) {
						SettingsImage2DQuantifierMultiPoints sett2 = new SettingsImage2DQuantifierMultiPoints();
						sett2.setActive(sett.isActive());
						sett = sett2;
					}
					SettingsImage2DQuantifierMultiPoints multi = (SettingsImage2DQuantifierMultiPoints) sett;
					// TODO
					// write all quantifiers to settings
					multi.setQuantifier(getPnTable().getQuantifiers()); 
					multi.setFactor(doubleFromTxt(getTxtMultiConc()));
					//
					break;
				}
				// set settings for image2d and here
				// MODE has its own quantifier in image2d class
				if(lastMode!=mode) { 
					lastMode = mode;
					setSettings(sett);
				}
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			finally { 
				// important
				if(currentImage!=null)
					currentImage.fireIntensityProcessingChanged();
			}
		}
		return sett;
	}
	

	@Override
	public void setSettings(SettingsImage2DQuantifier settings) {
		if(settings==null)
			settings = new SettingsImage2DQuantifierLinear();
		super.setSettings(settings);
	}
	
	//################################################################################################
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
	public JPanel getTabMultiPoint() {
		return tabMultiPoint;
	} 
	public JTextField getTxtExPath() {
		return txtExPath;
	}
	public JTextField getTxtExTitle() {
		return txtExTitle;
	}
	public JTextField getTxtExConc() {
		return txtExConc;
	}
	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}
	public JRadioButton getRbExAverageSelectedAreas() {
		return rbExAverageSelectedAreas;
	}
	public JRadioButton getRbExAverage() {
		return rbExAverage;
	}
	public JTable getTableMultiPoint() {
		return pnTable.getTable();
	} 
	public PnTableQuantifier getPnTable() {
		return pnTable;
	}
	public JCheckBox getCbSplitQuantifierData() {
		return cbSplitQuantifierData;
	}
	public JTextField getTxtMultiConc() {
		return txtMultiConc;
	}
}
