package net.rs.lamsi.general.myfreechart.plots.image2d;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jfree.chart.JFreeChart;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.dialogs.HeatmapGraphicsExportDialog;
import net.rs.lamsi.general.myfreechart.plots.PlotChartPanel;
import net.rs.lamsi.general.settings.importexport.SettingsImage2DDataExport;
import net.rs.lamsi.general.settings.importexport.SettingsImageDataImportTxt.ModeData;
import net.rs.lamsi.multiimager.Frames.dialogs.DialogDataSaver;
import net.rs.lamsi.multiimager.utils.imageimportexport.DataExportUtil;

public class PlotImage2DChartPanel extends PlotChartPanel {
	private Collectable2D img;

	public PlotImage2DChartPanel(JFreeChart chart, Collectable2D img) {
		super(chart, false);
		this.img = img;
		addExportMenu();
	}

	protected void exportGraphics() {
		// open export Graphics dialog 
		HeatmapGraphicsExportDialog.openDialog(getChart(), img); 
	}

	@Override
	protected void addExportMenu() {
		super.addExportMenu();
		
		final PlotImage2DChartPanel thispanel = this;
		
		if(img!=null && img.isImage2D()) {
			this.getPopupMenu().addSeparator();
			// Data Export 
			JMenuItem exportData = new JMenuItem("Export data to.."); 
			exportData.addActionListener(new ActionListener() { 
				@Override
				public void actionPerformed(ActionEvent e) { 
					// open export Graphics dialog 
					if(img.isImage2D())
						DialogDataSaver.startDialogWith((Image2D)img); 
				}
			});  
			// add to panel
			addPopupMenuItem(exportData);
			
			// TODO ADD MORE OPTIONS FOR CLIPBOARD BY DIFFERENT MENUBUTTONS
			this.getPopupMenu().addSeparator(); 
			
			// Data Export 
			JMenu exportDataMenu = new JMenu("Export data to clipboard..");  
			addPopupMenu(exportDataMenu);
			// Data Export 
			JMenuItem exportDataCB = new JMenuItem("..raw data"); 
			exportDataCB.addActionListener(new ActionListener() { 
				@Override
				public void actionPerformed(ActionEvent e) { 
					// 
					SettingsImage2DDataExport sett = new SettingsImage2DDataExport();
					sett.setUpForDataOnly(true, true);
					try {
						if(img.isImage2D())
							DataExportUtil.exportDataImage2D((Image2D)img, sett);
					} catch (InvalidFormatException | IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} 
				}
			});  
			// add to panel
			exportDataMenu.add(exportDataCB);
			//
			JMenuItem exportDataCB2 = new JMenuItem("..processed data"); 
			exportDataCB2.addActionListener(new ActionListener() { 
				@Override
				public void actionPerformed(ActionEvent e) { 
					// 
					SettingsImage2DDataExport sett = new SettingsImage2DDataExport();
					sett.setUpForDataOnly(true, false);
					try {
						if(img.isImage2D())
							DataExportUtil.exportDataImage2D((Image2D)img, sett);
					} catch (InvalidFormatException | IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} 
				}
			});  
			// add to panel
			exportDataMenu.add(exportDataCB2); 
			//
			exportDataCB2 = new JMenuItem("..raw data XYZ"); 
			exportDataCB2.addActionListener(new ActionListener() { 
				@Override
				public void actionPerformed(ActionEvent e) { 
					// 
					SettingsImage2DDataExport sett = new SettingsImage2DDataExport();
					sett.setUpForDataOnly(true, true);
					sett.setMode(ModeData.XYZ);
					try {
						if(img.isImage2D())
							DataExportUtil.exportDataImage2D((Image2D) img, sett);
					} catch (InvalidFormatException | IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} 
				}
			});  
			// add to panel
			exportDataMenu.add(exportDataCB2); 
			//
			exportDataCB2 = new JMenuItem("..processed data XYZ"); 
			exportDataCB2.addActionListener(new ActionListener() { 
				@Override
				public void actionPerformed(ActionEvent e) { 
					// 
					SettingsImage2DDataExport sett = new SettingsImage2DDataExport();
					sett.setUpForDataOnly(true, false);
					sett.setMode(ModeData.XYZ);
					try {
						if(img.isImage2D())
							DataExportUtil.exportDataImage2D((Image2D)img, sett);
					} catch (InvalidFormatException | IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} 
				}
			});  
			// add to panel
			exportDataMenu.add(exportDataCB2); 
		}
	}
}
