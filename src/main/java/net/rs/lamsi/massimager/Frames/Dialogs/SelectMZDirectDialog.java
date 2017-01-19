package net.rs.lamsi.massimager.Frames.Dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.massimager.Frames.Window;
import net.rs.lamsi.massimager.Frames.Panels.ImageVsSpecViewPanel;
import net.rs.lamsi.massimager.MyMZ.MZChromatogram;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.xy.XYDataset;

import javax.swing.JTabbedPane;
import javax.swing.ImageIcon;

import java.awt.Rectangle;
import java.awt.Insets;
import java.awt.Dimension;

public class SelectMZDirectDialog extends JDialog {

	
	private ImageVsSpecViewPanel pnImageVsSpec;
	
	private final JPanel contentPanel = new JPanel();
	private JTextField txtTopMZ0;
	private JTextField txtTopMZ1;
	private JTextField txtTopAddRange;
	private JLabel lbTopMZMin;
	private JLabel lbTopMZMax;
	private JTextField txtMiddleMZ0;
	private JTextField txtMiddleMZ1;
	private JTextField txtMiddleAddRange;
	private JTextField txtBottomRT0;
	private JTextField txtBottomRT1;
	private JTextField txtBottomAddRange;
	private JLabel lbMiddleMZMin;
	private JLabel lbMiddleMZMax;
	private JLabel lbBottomRTMin;
	private JLabel lbBottomRTMax;
	private JTabbedPane tabbedPane;
	private JTextField txtTopMZtoMZ;
	private JTextField txtMiddleMZtoMZ;
	private JButton btnMiddleCalcMZtoMZ;
	private JButton btnTopCalcMZtoMZ;
	private JTextField txtBottomRTtoRT;
	private JButton btnBottomCalcRTtoRT;

	/**
	 * Create the dialog.
	 */
	public SelectMZDirectDialog(ImageVsSpecViewPanel pnImageVsSpec) {
		setBounds(100, 100, 416, 316); 
		this.pnImageVsSpec = pnImageVsSpec;
		
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			tabbedPane = new JTabbedPane(JTabbedPane.TOP);
			contentPanel.add(tabbedPane, BorderLayout.CENTER);
			{
				JPanel pnTop = new JPanel();
				tabbedPane.addTab("Top Chrom", null, pnTop, null);
				pnTop.setLayout(new MigLayout("", "[][left][]", "[][][][][][][]"));
				{
					JLabel lblMzmin = new JLabel("mz (min):");
					pnTop.add(lblMzmin, "cell 0 0,alignx trailing");
				}
				{
					lbTopMZMin = new JLabel("0");
					pnTop.add(lbTopMZMin, "cell 1 0");
				}
				{
					JLabel lblMzmax = new JLabel("mz (max):");
					pnTop.add(lblMzmax, "cell 0 1,alignx trailing");
				}
				{
					lbTopMZMax = new JLabel("0");
					pnTop.add(lbTopMZMax, "cell 1 1");
				}
				{
					JLabel lblMz = new JLabel("mz 0");
					pnTop.add(lblMz, "cell 0 3,alignx trailing");
				}
				{
					txtTopMZ0 = new JTextField();
					txtTopMZ0.setText("0");
					pnTop.add(txtTopMZ0, "cell 1 3,alignx left");
					txtTopMZ0.setColumns(10);
				}
				{
					JLabel lblMz_1 = new JLabel("mz 1");
					pnTop.add(lblMz_1, "cell 0 4,alignx trailing");
				}
				{
					txtTopMZ1 = new JTextField();
					txtTopMZ1.setText("0");
					pnTop.add(txtTopMZ1, "cell 1 4,alignx left");
					txtTopMZ1.setColumns(10);
				}
				{
					JButton btnTopPrevRange = new JButton("");
					btnTopPrevRange.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							addRangeTop(-1);
						}
					});
					{
						JLabel lblMz_2 = new JLabel("mz 0 - mz 1");
						pnTop.add(lblMz_2, "cell 0 5,alignx trailing");
					}
					{
						txtTopMZtoMZ = new JTextField();
						txtTopMZtoMZ.setText("0-0");
						pnTop.add(txtTopMZtoMZ, "cell 1 5,alignx left");
						txtTopMZtoMZ.setColumns(10);
					}
					{
						btnTopCalcMZtoMZ = new JButton("calc");
						btnTopCalcMZtoMZ.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								calcXXtoXX(getTxtTopMZtoMZ(), getTxtTopMZ0(), getTxtTopMZ1());
							}
						});
						btnTopCalcMZtoMZ.setToolTipText("Apply mz0 - mz1 range format");
						pnTop.add(btnTopCalcMZtoMZ, "cell 2 5");
					}
					btnTopPrevRange.setIcon(new ImageIcon(SelectMZDirectDialog.class.getResource("/img/btn_menu_prev_arrow.png")));
					btnTopPrevRange.setToolTipText("Jump to previous range");
					btnTopPrevRange.setMinimumSize(new Dimension(25, 25));
					btnTopPrevRange.setMaximumSize(new Dimension(25, 25));
					btnTopPrevRange.setMargin(new Insets(0, 0, 0, 0));
					btnTopPrevRange.setBounds(new Rectangle(0, 0, 25, 25));
					pnTop.add(btnTopPrevRange, "cell 0 6,alignx trailing");
				}
				{
					txtTopAddRange = new JTextField();
					txtTopAddRange.setText("0");
					pnTop.add(txtTopAddRange, "flowx,cell 1 6,alignx left");
					txtTopAddRange.setColumns(10);
				}
				{
					JButton btnTopNextRange = new JButton("");
					btnTopNextRange.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							addRangeTop(1);
						}
					});
					btnTopNextRange.setIcon(new ImageIcon(SelectMZDirectDialog.class.getResource("/img/btn_menu_next_arrow.png")));
					btnTopNextRange.setToolTipText("Jump to next range");
					btnTopNextRange.setMinimumSize(new Dimension(25, 25));
					btnTopNextRange.setMaximumSize(new Dimension(25, 25));
					btnTopNextRange.setMargin(new Insets(0, 0, 0, 0));
					btnTopNextRange.setBounds(new Rectangle(0, 0, 25, 25));
					pnTop.add(btnTopNextRange, "cell 2 6");
				}
			}
			{
				JPanel pnMiddle = new JPanel();
				tabbedPane.addTab("Middle Chrom", null, pnMiddle, null);
				pnMiddle.setLayout(new MigLayout("", "[][][]", "[][][][][][][]"));
				{
					JLabel label = new JLabel("mz (min):");
					pnMiddle.add(label, "cell 0 0,alignx trailing");
				}
				{
					lbMiddleMZMin = new JLabel("0");
					pnMiddle.add(lbMiddleMZMin, "cell 1 0");
				}
				{
					JLabel label = new JLabel("mz (max):");
					pnMiddle.add(label, "cell 0 1,alignx trailing");
				}
				{
					lbMiddleMZMax = new JLabel("0");
					pnMiddle.add(lbMiddleMZMax, "cell 1 1");
				}
				{
					JLabel label = new JLabel("mz 0");
					pnMiddle.add(label, "cell 0 3,alignx trailing");
				}
				{
					txtMiddleMZ0 = new JTextField();
					txtMiddleMZ0.setText("0");
					txtMiddleMZ0.setColumns(10);
					pnMiddle.add(txtMiddleMZ0, "cell 1 3,alignx left");
				}
				{
					JLabel label = new JLabel("mz 1");
					pnMiddle.add(label, "cell 0 4,alignx trailing");
				}
				{
					txtMiddleMZ1 = new JTextField();
					txtMiddleMZ1.setText("0");
					txtMiddleMZ1.setColumns(10);
					pnMiddle.add(txtMiddleMZ1, "cell 1 4,alignx left");
				}
				{
					JButton btnMiddlePrevRange = new JButton("");
					btnMiddlePrevRange.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							addRangeMiddle(-1);
						}
					});
					{
						JLabel label = new JLabel("mz 0 - mz 1");
						pnMiddle.add(label, "cell 0 5,alignx trailing");
					}
					{
						txtMiddleMZtoMZ = new JTextField();
						txtMiddleMZtoMZ.setText("0-0");
						txtMiddleMZtoMZ.setColumns(10);
						pnMiddle.add(txtMiddleMZtoMZ, "cell 1 5,growx");
					}
					{
						btnMiddleCalcMZtoMZ = new JButton("calc");
						btnMiddleCalcMZtoMZ.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								calcXXtoXX(getTxtMiddleMZtoMZ(), getTxtMiddleMZ0(), getTxtMiddleMZ1());
							}
						});
						btnMiddleCalcMZtoMZ.setToolTipText("Apply mz0 - mz1 range format");
						pnMiddle.add(btnMiddleCalcMZtoMZ, "cell 2 5");
					}
					btnMiddlePrevRange.setIcon(new ImageIcon(SelectMZDirectDialog.class.getResource("/img/btn_menu_prev_arrow.png")));
					btnMiddlePrevRange.setToolTipText("Jump to previous range");
					btnMiddlePrevRange.setMinimumSize(new Dimension(25, 25));
					btnMiddlePrevRange.setMaximumSize(new Dimension(25, 25));
					btnMiddlePrevRange.setMargin(new Insets(0, 0, 0, 0));
					btnMiddlePrevRange.setBounds(new Rectangle(0, 0, 25, 25));
					pnMiddle.add(btnMiddlePrevRange, "cell 0 6,alignx trailing");
				}
				{
					txtMiddleAddRange = new JTextField();
					txtMiddleAddRange.setText("0");
					txtMiddleAddRange.setColumns(10);
					pnMiddle.add(txtMiddleAddRange, "cell 1 6,alignx left");
				}
				{
					JButton btnMiddleNextRange = new JButton("");
					btnMiddleNextRange.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							addRangeMiddle(1);
						}
					});
					btnMiddleNextRange.setIcon(new ImageIcon(SelectMZDirectDialog.class.getResource("/img/btn_menu_next_arrow.png")));
					btnMiddleNextRange.setToolTipText("Jump to next range");
					btnMiddleNextRange.setMinimumSize(new Dimension(25, 25));
					btnMiddleNextRange.setMaximumSize(new Dimension(25, 25));
					btnMiddleNextRange.setMargin(new Insets(0, 0, 0, 0));
					btnMiddleNextRange.setBounds(new Rectangle(0, 0, 25, 25));
					pnMiddle.add(btnMiddleNextRange, "cell 2 6");
				}
			}
			{
				JPanel pnBottom = new JPanel();
				tabbedPane.addTab("Bottom Spectrum", null, pnBottom, null);
				pnBottom.setLayout(new MigLayout("", "[][][]", "[][][][][][][]"));
				{
					JLabel lblRtmin = new JLabel("rt (min):");
					pnBottom.add(lblRtmin, "cell 0 0,alignx trailing");
				}
				{
					lbBottomRTMin = new JLabel("0");
					pnBottom.add(lbBottomRTMin, "cell 1 0");
				}
				{
					JLabel lblRtmax = new JLabel("rt (max):");
					pnBottom.add(lblRtmax, "cell 0 1,alignx trailing");
				}
				{
					lbBottomRTMax = new JLabel("0");
					pnBottom.add(lbBottomRTMax, "cell 1 1");
				}
				{
					JLabel lblRt = new JLabel("rt 0");
					pnBottom.add(lblRt, "cell 0 3,alignx trailing");
				}
				{
					txtBottomRT0 = new JTextField();
					txtBottomRT0.setText("0");
					txtBottomRT0.setColumns(10);
					pnBottom.add(txtBottomRT0, "cell 1 3,alignx left");
				}
				{
					JLabel lblRt_1 = new JLabel("rt 1");
					pnBottom.add(lblRt_1, "cell 0 4,alignx trailing");
				}
				{
					txtBottomRT1 = new JTextField();
					txtBottomRT1.setText("0");
					txtBottomRT1.setColumns(10);
					pnBottom.add(txtBottomRT1, "cell 1 4,alignx left");
				}
				{
					JButton btnBottomPrevRange = new JButton("");
					btnBottomPrevRange.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							addRangeBottom(-1);
						}
					});
					{
						JLabel lblRt_2 = new JLabel("rt 0 - rt 1");
						pnBottom.add(lblRt_2, "cell 0 5,alignx trailing");
					}
					{
						txtBottomRTtoRT = new JTextField();
						txtBottomRTtoRT.setText("0-0");
						txtBottomRTtoRT.setColumns(10);
						pnBottom.add(txtBottomRTtoRT, "cell 1 5,growx");
					}
					{
						btnBottomCalcRTtoRT = new JButton("calc");
						btnBottomCalcRTtoRT.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								calcXXtoXX(getTxtBottomRTtoRT(), getTxtBottomRT0(), getTxtBottomRT1());
							}
						});
						btnBottomCalcRTtoRT.setToolTipText("Apply rt0 - rt1 range format");
						pnBottom.add(btnBottomCalcRTtoRT, "cell 2 5");
					}
					btnBottomPrevRange.setIcon(new ImageIcon(SelectMZDirectDialog.class.getResource("/img/btn_menu_prev_arrow.png")));
					btnBottomPrevRange.setToolTipText("Jump to previous range");
					btnBottomPrevRange.setMinimumSize(new Dimension(25, 25));
					btnBottomPrevRange.setMaximumSize(new Dimension(25, 25));
					btnBottomPrevRange.setMargin(new Insets(0, 0, 0, 0));
					btnBottomPrevRange.setBounds(new Rectangle(0, 0, 25, 25));
					pnBottom.add(btnBottomPrevRange, "cell 0 6,alignx right");
				}
				{
					txtBottomAddRange = new JTextField();
					txtBottomAddRange.setText("0");
					txtBottomAddRange.setColumns(10);
					pnBottom.add(txtBottomAddRange, "cell 1 6,alignx left");
				}
				{
					JButton btnBottomNextRange = new JButton("");
					btnBottomNextRange.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							addRangeBottom(1);
						}
					});
					btnBottomNextRange.setIcon(new ImageIcon(SelectMZDirectDialog.class.getResource("/img/btn_menu_next_arrow.png")));
					btnBottomNextRange.setToolTipText("Jump to next range");
					btnBottomNextRange.setMinimumSize(new Dimension(25, 25));
					btnBottomNextRange.setMaximumSize(new Dimension(25, 25));
					btnBottomNextRange.setMargin(new Insets(0, 0, 0, 0));
					btnBottomNextRange.setBounds(new Rectangle(0, 0, 25, 25));
					pnBottom.add(btnBottomNextRange, "cell 2 6");
				}
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						close();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
					}
				});
				{
					JButton btnApply = new JButton("Apply");
					btnApply.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							applySettingsOfMZRT();
						}
					});
					buttonPane.add(btnApply);
				}
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	protected void calcXXtoXX(JTextField txtXXtoXX, JTextField txtX0, JTextField txtX1) {
		try {
			String text = txtXXtoXX.getText();
			text.replace(" ", "");
			String[] splitted = text.split("-");
			txtX0.setText(splitted[0]);
			txtX1.setText(splitted[1]);
			applySettingsOfMZRT();
		}catch(Exception ex) { 
		}
	}

	/*
	 * Apply mz or rt by selected tab
	 */
	protected void applySettingsOfMZRT() {
		// TODO Auto-generated method stub
		int selectedTab = getTabbedPane().getSelectedIndex();
		switch (selectedTab) {
		case 0: // Top
			try {
				double mz0 = Double.valueOf(txtTopMZ0.getText());
				double mz1 = Double.valueOf(txtTopMZ1.getText());
				double mz = (mz1+mz0)/2;
				double pm = Math.abs((mz0-mz1)/2);
				pnImageVsSpec.renewTopChrom(mz, pm); 
			} catch (Exception e) { 
			}
			break;
		case 1: // Middle
			double mz0 = Double.valueOf(txtMiddleMZ0.getText());
			double mz1 = Double.valueOf(txtMiddleMZ1.getText());
			double mz = (mz1+mz0)/2;
			double pm = Math.abs((mz0-mz1)/2);
			pnImageVsSpec.renewMiddleImageChrom(mz, pm); 
			break;
		case 2: // Bottom
			double rt0 = Double.valueOf(txtBottomRT0.getText());
			double rt1 = Double.valueOf(txtBottomRT1.getText()); 
			MZChromatogram spec = Window.getWindow().getLogicRunner().generateSpectrumSUMByRT(rt0, rt1);
			pnImageVsSpec.renewBottomSpectrum(spec);
			pnImageVsSpec.setSelectedVsRetentionTime(rt0, rt1);		
			break; 
		}
	}

	/*
	 * add
	 */
	public void addRangeTop(int i) {
		try {
			double range = Double.valueOf(txtTopAddRange.getText());
			double x0 = Double.valueOf(txtTopMZ0.getText()) + range*i;
			double x1 = Double.valueOf(txtTopMZ1.getText()) + range*i;
			
			if(x0<0) x0 = 0;

			txtTopMZ0.setText(x0+"");
			txtTopMZ1.setText(x1+"");
		} catch(Exception ex) {
		}
	}
	public void addRangeMiddle(int i) {
		try {
			double range = Double.valueOf(txtMiddleAddRange.getText());
			double x0 = Double.valueOf(txtMiddleMZ0.getText()) + range*i;
			double x1 = Double.valueOf(txtMiddleMZ1.getText()) + range*i;
			
			if(x0<0) x0 = 0;

			txtMiddleMZ0.setText(x0+"");
			txtMiddleMZ1.setText(x1+"");
		} catch(Exception ex) {
		}
	}
	public void addRangeBottom(int i) {
		try {
			double range = Double.valueOf(txtBottomAddRange.getText());
			double x0 = Double.valueOf(txtBottomRT0.getText()) + range*i;
			double x1 = Double.valueOf(txtBottomRT1.getText()) + range*i;
			
			if(x0<0) x0 = 0;

			txtBottomRT0.setText(x0+"");
			txtBottomRT1.setText(x1+"");
		} catch(Exception ex) {
		}
	}

	/*
	 * open close
	 */
	public void open(int mode) {
		getTabbedPane().setSelectedIndex(mode);
		this.setVisible(true);
	}
	public void close() {
		this.setVisible(false); 
	}
	
	/* 
	 * get and set
	 */
	
	public JTextField getTxtTopMZ0() {
		return txtTopMZ0;
	}
	public JTextField getTxtTopMZ1() {
		return txtTopMZ1;
	}
	public JLabel getLbTopMZMin() {
		return lbTopMZMin;
	}
	public JLabel getLbTopMZMax() {
		return lbTopMZMax;
	}
	public JTextField getTxtTopAddRange() {
		return txtTopAddRange;
	}
	public JLabel getLbMiddleMZMin() {
		return lbMiddleMZMin;
	}
	public JLabel getLbMiddleMZMax() {
		return lbMiddleMZMax;
	}
	public JTextField getTxtMiddleMZ0() {
		return txtMiddleMZ0;
	}
	public JTextField getTxtMiddleMZ1() {
		return txtMiddleMZ1;
	}
	public JTextField getTxtMiddleAddRange() {
		return txtMiddleAddRange;
	}
	public JLabel getLbBottomRTMin() {
		return lbBottomRTMin;
	}
	public JLabel getLbBottomRTMax() {
		return lbBottomRTMax;
	}
	public JTextField getTxtBottomRT0() {
		return txtBottomRT0;
	}
	public JTextField getTxtBottomRT1() {
		return txtBottomRT1;
	}
	public JTextField getTxtBottomAddRange() {
		return txtBottomAddRange;
	}
	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}
	public JTextField getTxtMiddleMZtoMZ() {
		return txtMiddleMZtoMZ;
	}
	public JButton getBtnMiddleCalcMZtoMZ() {
		return btnMiddleCalcMZtoMZ;
	}
	public JTextField getTxtTopMZtoMZ() {
		return txtTopMZtoMZ;
	}
	public JButton getBtnTopCalcMZtoMZ() {
		return btnTopCalcMZtoMZ;
	}
	public JTextField getTxtBottomRTtoRT() {
		return txtBottomRTtoRT;
	}
	public JButton getBtnBottomCalcRTtoRT() {
		return btnBottomCalcRTtoRT;
	}
}
