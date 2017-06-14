package net.rs.lamsi.multiimager.Frames.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.ImageGroupMD;
import net.rs.lamsi.general.datamodel.image.data.interf.ImageDataset;
import net.rs.lamsi.general.datamodel.image.data.twodimensional.XYIDataMatrix;
import net.rs.lamsi.general.heatmap.Heatmap;
import net.rs.lamsi.general.heatmap.HeatmapFactory;
import net.rs.lamsi.general.settings.image.filter.SettingsCropAndShift;

public class CroppingDialog extends JFrame {

	private final JPanel contentPanel = new JPanel();
	private JPanel pnChartView;
	private JTextField txtName;
	private JTextField txtLineEnd;
	private JTextField txtlineStart;
	private JCheckBox cbCropToMin;
	// my stuff
	private ImageGroupMD group;
	private int selectedImgIndex = 0;
	private SettingsCropAndShift settings;
	private Heatmap heat;
	private Image2D currentImg;
	private JTextField txtDelLinesStart;
	private JTextField txtDelLinesEnd;
	private JLabel lbLastLineToShort;
	private JLabel lblfirstLineSeems;
	private JTextField txtAllowHighs;
	private JTextField txtMinimum;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			CroppingDialog dialog = new CroppingDialog();
			dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public CroppingDialog() {
		setTitle("Cropping and line shifting");
		setBounds(100, 100, 606, 618);
		getContentPane().setLayout(new BorderLayout());
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton btnApplyGroup = new JButton("Apply to group");
				btnApplyGroup.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// 
						applyToGroup();
					}
				});
				buttonPane.add(btnApplyGroup);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// 
						setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		{
			JPanel panel = new JPanel();
			getContentPane().add(panel, BorderLayout.CENTER);
			panel.setLayout(new BorderLayout(0, 0));
			panel.add(contentPanel, BorderLayout.NORTH);
			contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
			contentPanel.setLayout(new MigLayout("", "[grow][][grow][][grow]", "[][][][][][grow]"));
			{
				JButton btnPrevious = new JButton("Previous");
				btnPrevious.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// 
						selectedImgIndex = selectedImgIndex<=0? 0 : selectedImgIndex-1;
						setSelectedImageAndShow((Image2D)group.get(selectedImgIndex, Image2D.class));
					}
				});
				contentPanel.add(btnPrevious, "cell 1 0");
			}
			{
				txtName = new JTextField();
				txtName.setText("name");
				contentPanel.add(txtName, "cell 2 0,growx");
				txtName.setColumns(20);
			}
			{
				JButton btnNext = new JButton("Next");
				btnNext.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// 
						int c = group.image2dCount();
						selectedImgIndex = selectedImgIndex<c-1? selectedImgIndex+1 : group.image2dCount()-1;
						setSelectedImageAndShow((Image2D)group.get(selectedImgIndex, Image2D.class));
					}
				});
				contentPanel.add(btnNext, "cell 3 0");
			}
			{
				JLabel lblDelete = new JLabel("delete");
				contentPanel.add(lblDelete, "cell 1 1,alignx trailing");
			}
			{
				txtDelLinesStart = new JTextField();
				txtDelLinesStart.setHorizontalAlignment(SwingConstants.RIGHT);
				txtDelLinesStart.setText("0");
				contentPanel.add(txtDelLinesStart, "flowx,cell 2 1,alignx left,aligny top");
				txtDelLinesStart.setColumns(5);
			}
			{
				txtDelLinesEnd = new JTextField();
				txtDelLinesEnd.setHorizontalAlignment(SwingConstants.RIGHT);
				txtDelLinesEnd.setText("0");
				txtDelLinesEnd.setColumns(5);
				contentPanel.add(txtDelLinesEnd, "flowx,cell 2 2,alignx left");
			}
			{
				cbCropToMin = new JCheckBox("Crop to minimum  length");
				cbCropToMin.setSelected(true);
				contentPanel.add(cbCropToMin, "cell 1 3 2 1");
			}
			{
				JLabel lblLines = new JLabel("lines");
				contentPanel.add(lblLines, "flowx,cell 1 4,alignx left");
			}
			{
				JLabel lblTo = new JLabel("to");
				contentPanel.add(lblTo, "flowx,cell 2 4");
			}
			{
				txtLineEnd = new JTextField();
				txtLineEnd.setColumns(5);
				contentPanel.add(txtLineEnd, "cell 2 4");
			}
			{
				JPanel panel_1 = new JPanel();
				contentPanel.add(panel_1, "cell 1 5 3 1,grow");
				panel_1.setLayout(new MigLayout("", "[grow][][][][grow]", "[grow][][]"));
				{
					JButton button = new JButton("+");
					button.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							// TODO
							shiftLineSelection(1, false);
						}
					});
					button.setToolTipText("Next line");
					panel_1.add(button, "cell 2 0");
				}
				{
					JButton button = new JButton("-");
					button.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							// TODO
							shiftLineStart(-1);
						}
					});
					button.setToolTipText("Shift line start (negative)");
					panel_1.add(button, "cell 1 1");
				}
				{
					JButton button = new JButton("+");
					button.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							// TODO
							shiftLineStart(1);
						}
					});
					button.setToolTipText("Shift line start");
					panel_1.add(button, "cell 3 1");
				}
				{
					JButton button = new JButton("-");
					button.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							// TODO
							shiftLineSelection(-1, false);
						}
					});
					{
						JPanel panel_2 = new JPanel();
						panel_1.add(panel_2, "cell 4 0 1 2,grow");
						panel_2.setLayout(new MigLayout("", "[][][]", "[][]"));
						{
							JLabel lblDataPoints = new JLabel("\"highs\"");
							panel_2.add(lblDataPoints, "cell 1 0,alignx center");
						}
						{
							JLabel lblBackgroundLavel = new JLabel("Background level");
							panel_2.add(lblBackgroundLavel, "cell 2 0,alignx center");
						}
						{
							JButton btnAuto = new JButton("Auto");
							panel_2.add(btnAuto, "cell 0 1");
							{
								txtAllowHighs = new JTextField();
								panel_2.add(txtAllowHighs, "cell 1 1");
								txtAllowHighs.setToolTipText("Allowance for X consecutive high signal (e.g. noise) in the low background");
								txtAllowHighs.setText("10");
								txtAllowHighs.setColumns(5);
							}
							{
								txtMinimum = new JTextField();
								panel_2.add(txtMinimum, "cell 2 1");
								txtMinimum.setToolTipText("New background level for auto detection (the paint scale minimum is used if no value is set)");
								txtMinimum.setColumns(10);
							}
							btnAuto.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									autoAdjustLineStart();
								}
							});
						}
					}
					button.setToolTipText("Previous line");
					panel_1.add(button, "cell 2 2");
				}
				{
					JButton btnUpdate = new JButton("Update");
					btnUpdate.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							updateHeatmap();
						}
					});
					panel_1.add(btnUpdate, "cell 4 2");
				}
			}
			{
				txtlineStart = new JTextField();
				txtlineStart.setHorizontalAlignment(SwingConstants.RIGHT);
				txtlineStart.setText("1");
				contentPanel.add(txtlineStart, "cell 1 4,alignx left");
				txtlineStart.setColumns(5);
			}
			{
				JLabel lblLinesFromStart = new JLabel("lines from start");
				contentPanel.add(lblLinesFromStart, "cell 2 1");
			}
			{
				JLabel lblLinesFromEnd = new JLabel("lines from end");
				contentPanel.add(lblLinesFromEnd, "cell 2 2");
			}
			{
				lbLastLineToShort = new JLabel("(last line seems to be  too short)");
				lbLastLineToShort.setFocusTraversalPolicyProvider(true);
				lbLastLineToShort.setVisible(false);
				lbLastLineToShort.setForeground(Color.RED);
				lbLastLineToShort.setFont(new Font("Tahoma", Font.BOLD, 11));
				contentPanel.add(lbLastLineToShort, "cell 2 2");
			}
			{
				lblfirstLineSeems = new JLabel("(first line seems to be  too short)");
				lblfirstLineSeems.setVisible(false);
				lblfirstLineSeems.setForeground(Color.RED);
				lblfirstLineSeems.setFont(new Font("Tahoma", Font.BOLD, 11));
				contentPanel.add(lblfirstLineSeems, "cell 2 1");
			}
			{
				pnChartView = new JPanel();
				panel.add(pnChartView, BorderLayout.CENTER);
				pnChartView.setLayout(new BorderLayout(0, 0));
			}
		}
		
		addKeys();
	}
	
	
	/**
	 * auto adjust by searching for an intensity drop in each line (low->high)
	 * where low is defined by <minimum of paint scale
	 * allow some consecutive high signals
	 */
	protected void autoAdjustLineStart() {
		try {
			// allow X high values
			int highs = Integer.parseInt(getTxtAllowHighs().getText());
			double bg = Double.NaN;
			try {
				bg = Double.valueOf(getTxtMinimum().getText());
			} catch (Exception e) {
			}
			// set bg to min of paint scale if nothing was set
			if(Double.isNaN(bg)) {
				bg = currentImg.getSettings().getSettPaintScale().getMinIAbs(currentImg);
			}
			
			// get data matrix
			ImageDataset data = currentImg.getData();
			
			int[] start = settings.getShiftLineStart();
			// loop through all lines
			for(int l=0; l<start.length; l++) {
				// count highs
				int c = 0;
				boolean endFound = false;
				// from first to last dp
				for(int d=start[l]; d<data.getLineLength(l) && !endFound; d++) {
					// count consecutive highs
					double value = data.getI(currentImg.getIndex(), l, d);
					if(value>bg) {
						c++;
					}
					else c = 0;
					// set border to
					if(c>highs && c>0) {
						endFound = true;
						// set end to 
						start[l] = d-highs;
					}
				}
			}
			
			//
			updateHeatmap();
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	protected void applyToGroup() {
		settings.setCropToMinLength(getCbCropToMin().isSelected());
		settings.applyToGroup(group);
	}

	protected void shiftLineSelection(int i, boolean enlargeSelection) {
		int current = i>0? getSelectedLineEnd() : getSelectedLineFirst();
		current += i;
		// min max
		if(current<0) current = 0;
		if(current>=settings.getShiftLineStart().length)
			current = settings.getShiftLineStart().length-1;
		
		// 
		if(!enlargeSelection || i<0) {
			if(enlargeSelection && getTxtLineEnd().getText().length()==0) {
				getTxtLineEnd().setText(String.valueOf(current+2));
			}
			getTxtlineStart().setText(String.valueOf(current+1));
		}
		
		if(!enlargeSelection)
			getTxtLineEnd().setText("");
		else if(i>0)
			getTxtLineEnd().setText(String.valueOf(current+1));
	}

	/**
	 * the index of the last selected line
	 * @return
	 */
	private int getSelectedLineEnd() {
		int i=0;
		try {
			if(getTxtLineEnd().getText().length()>0)
				i = Integer.valueOf(getTxtLineEnd().getText())-1;
		} catch(Exception ex) {
		}
		if(i==0) {
			try {
				if(getTxtlineStart().getText().length()>0)
					i = Integer.valueOf(getTxtlineStart().getText())-1;
			} catch(Exception ex) {
			}
		}
		return i<0? 0 : i;
	}
	/**
	 * the index of the first selected line
	 * @return
	 */
	private int getSelectedLineFirst() {
		int i=0;
			try {
				if(getTxtlineStart().getText().length()>0)
					i = Integer.valueOf(getTxtlineStart().getText())-1;
			} catch(Exception ex) {
			}
		return i<0? 0 : i;
	}

	
	protected void shiftLineStart(int shift) {
		int start = getSelectedLineFirst();
		int end = getSelectedLineEnd();
		// shift all selected to the left
		if(shift<0)
			for(int i=start; i<=end; i++)
				settings.getShiftLineStart()[i] -= shift;
		
		// shift not selected lines to the left
		else {
			for(int i=0; i<start; i++)
				settings.getShiftLineStart()[i] += shift;
			for(int i=end+1; i<settings.getShiftLineStart().length; i++)
				settings.getShiftLineStart()[i] += shift;
		}
		// back shift by minimum value
		int min = Integer.MAX_VALUE;
		for(int i=0; i<settings.getShiftLineStart().length; i++)
			if(settings.getShiftLineStart()[i]<min) min = settings.getShiftLineStart()[i];
		for(int i=0; i<settings.getShiftLineStart().length; i++)
			 settings.getShiftLineStart()[i] -= min;
		
		// 
		updateHeatmap();
	}

	/**
	 * starts the dialog 
	 * @param group
	 * @param selectedImage
	 */
	public void startDialog(ImageGroupMD group, Image2D selectedImage) {
		this.group = group; 
		selectedImgIndex = group.getImages().indexOf(selectedImage);
		// create new settings
		settings = new SettingsCropAndShift();

		// check first and last line
		int avg = selectedImage.getData().getAvgDP();
		getLblfirstLineSeems().setVisible(selectedImage.getData().getLineLength(0)<avg*0.95);
		getLbLastLineToShort().setVisible(selectedImage.getData().getLineLength(selectedImage.getData().getLinesCount()-1)<avg*0.95);
		// update heatmap
		setSelectedImageAndShow(selectedImage);
	}


	private void setSelectedImageAndShow(Image2D img) {
			currentImg = img;
			getTxtName().setText(img.getShortTitle());
			// set image to settings
			settings.setCurrentImage(img);
			
			updateHeatmap();
			setVisible(true);
	}
	
	

	private void updateHeatmap() {
		try {
			// no rotation, raw
			XYIDataMatrix data = currentImg.toXYIDataMatrix(false, false);

			settings.setCropToMinLength(getCbCropToMin().isSelected());
			try{
				settings.setDeletedLinesStart(Integer.valueOf(getTxtDelLinesStart().getText()));
			}catch(Exception ex) {}
			try{
				settings.setDeletedLinesEnd(Integer.valueOf(getTxtDelLinesEnd().getText()));
			}catch(Exception ex) {}
			
			// apply settings to data
			double[][] dat = settings.applyTo(data);

			// generate heatmap
			heat = HeatmapFactory.generateHeatmap(currentImg, "Crop", dat);
			// show
			getPnChartView().removeAll();
			getPnChartView().add(heat.getChartPanel(), BorderLayout.CENTER);
			getPnChartView().revalidate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public JPanel getPnChartView() {
		return pnChartView;
	}
	public JTextField getTxtName() {
		return txtName;
	}
	public JTextField getTxtlineStart() {
		return txtlineStart;
	}
	public JTextField getTxtLineEnd() {
		return txtLineEnd;
	}
	public JCheckBox getCbCropToMin() {
		return cbCropToMin;
	}

	private void addKeys() {   
		JPanel pn = (JPanel) getContentPane(); 
		InputMap im = pn.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "LEFT"); 
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "RIGHT");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "UP");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "DOWN");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.SHIFT_MASK), "shift LEFT"); 
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.SHIFT_MASK), "shift RIGHT");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.SHIFT_MASK), "shift UP");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.SHIFT_MASK), "shift DOWN");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.CTRL_MASK), "ctrl LEFT"); 
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.CTRL_MASK), "ctrl RIGHT");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_MASK), "ctrl UP");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_MASK), "ctrl DOWN");
		
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.CTRL_MASK+InputEvent.SHIFT_MASK), "ctrl shift LEFT"); 
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.CTRL_MASK+InputEvent.SHIFT_MASK), "ctrl shift RIGHT");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_MASK+InputEvent.SHIFT_MASK), "ctrl shift UP");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_MASK+InputEvent.SHIFT_MASK), "ctrl shift DOWN");

		// shift
		pn.getActionMap().put("shift LEFT", new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) {  
				shiftLineStart(-5);
		}});
		pn.getActionMap().put("shift RIGHT", new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) { 
				shiftLineStart(5);
		}});
		pn.getActionMap().put("shift UP", new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) { 
				shiftLineSelection(1, true);
		}});
		pn.getActionMap().put("shift DOWN", new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) { 
				shiftLineSelection(-1, true);
		}});
		// ctrl for shrinking
		pn.getActionMap().put("ctrl LEFT", new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) {  
		}});
		pn.getActionMap().put("ctrl RIGHT", new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) { 
		}});
		pn.getActionMap().put("ctrl UP", new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) { 
		}});
		pn.getActionMap().put("ctrl DOWN", new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) { 
		}});
		// arrows
		pn.getActionMap().put("LEFT", new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) {  
				shiftLineStart(-1);
		}});
		pn.getActionMap().put("RIGHT", new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) { 
				shiftLineStart(1);
		}});
		pn.getActionMap().put("UP", new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) { 
				shiftLineSelection(1, false);
		}});
		pn.getActionMap().put("DOWN", new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) { 
				shiftLineSelection(-1, false);
		}});

		// arrows
		pn.getActionMap().put("ctrl shift LEFT", new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) {  
		}});
		pn.getActionMap().put("ctrl shift RIGHT", new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) { 
		}});
		pn.getActionMap().put("ctrl shift UP", new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) { 
		}});
		pn.getActionMap().put("ctrl shift DOWN", new AbstractAction() { 
			@Override
			public void actionPerformed(ActionEvent e) { 
		}});
	}
	public JTextField getTxtDelLinesEnd() {
		return txtDelLinesEnd;
	}
	public JTextField getTxtDelLinesStart() {
		return txtDelLinesStart;
	}
	public JLabel getLbLastLineToShort() {
		return lbLastLineToShort;
	}
	public JLabel getLblfirstLineSeems() {
		return lblfirstLineSeems;
	}
	public JTextField getTxtAllowHighs() {
		return txtAllowHighs;
	}
	public JTextField getTxtMinimum() {
		return txtMinimum;
	}
}















































