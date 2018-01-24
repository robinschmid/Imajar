package net.rs.lamsi.multiimager.FrameModules.sub;


import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.util.LinkedList;
import java.util.function.Consumer;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;

import org.jfree.data.Range;

import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.framework.listener.ColorChangedListener;
import net.rs.lamsi.general.framework.modules.Collectable2DSettingsModule;
import net.rs.lamsi.general.heatmap.Heatmap;
import net.rs.lamsi.general.myfreechart.listener.history.ZoomHistory;
import net.rs.lamsi.general.myfreechart.listener.history.ZoomHistoryEvent;
import net.rs.lamsi.general.settings.image.sub.SettingsZoom;
import net.rs.lamsi.multiimager.Frames.ImageLogicRunner;

public class ModuleZoom extends Collectable2DSettingsModule<SettingsZoom, Collectable2D> implements Consumer<ZoomHistoryEvent> { 
	//
	private JTextField txtXLower;
	private JTextField txtXUpper;
	private JTextField txtYLower;
	private JTextField txtYUpper;
	
	private ZoomHistory history;
	
	// AUTOGEN

	/**
	 * Create the panel.
	 */
	public ModuleZoom() {
		super("Zoom", false, SettingsZoom.class, Collectable2D.class);  
		getLbTitle().setText("Zoom");
		
		JPanel panel = new JPanel();
		getPnContent().add(panel, BorderLayout.CENTER);
		panel.setLayout(new MigLayout("", "[][grow][grow]", "[][][]"));
		
		JLabel lblLowerBound = new JLabel("lower bound");
		lblLowerBound.setFont(new Font("Tahoma", Font.BOLD, 13));
		panel.add(lblLowerBound, "cell 1 0,alignx center");
		
		JLabel lblUpperBound = new JLabel("upper bound");
		lblUpperBound.setFont(new Font("Tahoma", Font.BOLD, 13));
		panel.add(lblUpperBound, "cell 2 0,alignx center");
		
		JLabel lblX = new JLabel("x");
		lblX.setFont(new Font("Tahoma", Font.BOLD, 13));
		panel.add(lblX, "cell 0 1,alignx trailing");
		
		txtXLower = new JTextField();
		txtXLower.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(txtXLower, "cell 1 1,growx,aligny top");
		txtXLower.setColumns(7);
		
		txtXUpper = new JTextField();
		txtXUpper.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(txtXUpper, "cell 2 1,growx");
		txtXUpper.setColumns(10);
		
		JLabel lblY = new JLabel("y");
		lblY.setFont(new Font("Tahoma", Font.BOLD, 13));
		panel.add(lblY, "cell 0 2,alignx trailing");
		
		txtYLower = new JTextField();
		txtYLower.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(txtYLower, "cell 1 2,growx");
		txtYLower.setColumns(10);
		
		txtYUpper = new JTextField();
		txtYUpper.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(txtYUpper, "cell 2 2,growx,aligny top");
		txtYUpper.setColumns(10);
		
		setMaxPresets(15);
	}
	
	@Override
	public void setCurrentHeatmap(Heatmap heat) {
		super.setCurrentHeatmap(heat);
		// extract
		if(heat!=null && getSettings()!=null) {
			getSettings().setXrange(heat.getPlot().getDomainAxis().getRange());
			getSettings().setYrange(heat.getPlot().getRangeAxis().getRange());
			
			setAllViaExistingSettings(getSettings());
		}
	}
	
	
	//################################################################################################
	// Autoupdate
	@Override
	public void addAutoupdater(ActionListener al, ChangeListener cl, DocumentListener dl, ColorChangedListener ccl, ItemListener il) {
	}
	
	@Override
	public void addAutoRepainter(ActionListener al, ChangeListener cl, DocumentListener dl, ColorChangedListener ccl, ItemListener il) {
		getTxtXLower().getDocument().addDocumentListener(dl);
		getTxtYLower().getDocument().addDocumentListener(dl);
		getTxtXUpper().getDocument().addDocumentListener(dl);
		getTxtYUpper().getDocument().addDocumentListener(dl);
	}

	//################################################################################################
	// LOGIC
	// Paintsclae from Image
	@Override
	public void setAllViaExistingSettings(SettingsZoom si) {  
		ImageLogicRunner.setIS_UPDATING(false);
		// new reseted ps
		if(si == null) {
			si = new SettingsZoom();
			si.resetAll();
		} 
		
		if(si.getXrange()!=null && si.getYrange()!=null) {
			getTxtXLower().setText(String.valueOf(si.getXrange().getLowerBound()));
			getTxtXUpper().setText(String.valueOf(si.getXrange().getUpperBound()));
			getTxtYLower().setText(String.valueOf(si.getYrange().getLowerBound()));
			getTxtYUpper().setText(String.valueOf(si.getYrange().getUpperBound()));
			writeAllToSettings(si);
		}
		// finished
		ImageLogicRunner.setIS_UPDATING(true);
		//ImageEditorWindow.getEditor().fireUpdateEvent(true);
	} 

	@Override
	public SettingsZoom writeAllToSettings(SettingsZoom si) {
		if(si!=null) {
			try {
				// changed?
				boolean changed = false;
				
				// new values
				double xl = Double.valueOf(getTxtXLower().getText());
				double xu = Double.valueOf(getTxtXUpper().getText());
				changed = si.setXrange(new Range(xl, xu));
				
				xl = Double.valueOf(getTxtYLower().getText());
				xu = Double.valueOf(getTxtYUpper().getText());
				changed = si.setYrange(new Range(xl, xu)) || changed;
				
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		return si;
	}

	/**
	 * Updates the zoom history presets as menu items
	 * @param e
	 */
	public void updateZoomPresets(ZoomHistory h) {
		if(h==null || h.getCurrentRange()==null) {
			removeAllPresets();
			return;
		}
		// added?
		boolean added = ((getPresets()==null || getPresets().size()==0) && h.getSize()==1) 
				|| (getPresets()!=null && h.getSize()-1 == getPresets().size());
		
		if(added && h.getCurrentRange()!=null) {
			// add only
			SettingsZoom zoom = new SettingsZoom();
			Range[] r = h.getCurrentRange().clone();
			zoom.setAll(r[0], r[1]);
			addPreset(zoom, zoom.toString());
		}
		else {
			// replace all
			removeAllPresets();
			// add all again
			LinkedList<Range[]> ranges = h.getHistory();
			// add last (oldest) first
			for(int i=ranges.size()-1; i>=0; i--) {
				Range[] r = ranges.get(i);
				SettingsZoom zoom = new SettingsZoom();
				zoom.setAll(r[0], r[1]);
				addPreset(zoom, zoom.toString());
			}
		}
	}
	
	//################################################################################################
	// GETTERS AND SETTERS 
	public JTextField getTxtXLower() {
		return txtXLower;
	}
	public JTextField getTxtYLower() {
		return txtYLower;
	}
	public JTextField getTxtXUpper() {
		return txtXUpper;
	}
	public JTextField getTxtYUpper() {
		return txtYUpper;
	}

	/**
	 * 
	 * @param history
	 */
	public void setCurrentHistory(ZoomHistory history) {
		if(this.history!=null)
			history.setOnZoomHistoryChanged(null);
		this.history = history;
		// set as listener
		history.setOnZoomHistoryChanged(this); 
		updateZoomPresets(history);
	}

	@Override
	public void accept(ZoomHistoryEvent e) {
		this.updateZoomPresets(e.getHistory());
	}
}
