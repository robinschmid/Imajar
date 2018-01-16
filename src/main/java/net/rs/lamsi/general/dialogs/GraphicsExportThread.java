package net.rs.lamsi.general.dialogs;

import javax.swing.SwingUtilities;

import org.jfree.chart.JFreeChart;

import net.rs.lamsi.general.settings.importexport.SettingsExportGraphics;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow.LOG;
import net.rs.lamsi.utils.ChartExportUtil;
import net.rs.lamsi.utils.DialogLoggerUtil;

public class GraphicsExportThread extends Thread {

	private SettingsExportGraphics fsett;
	private JFreeChart chart;

	public GraphicsExportThread(JFreeChart chart, SettingsExportGraphics fsett) {
		super();
		this.fsett = fsett;
		this.chart = chart;
	}

	@Override
	public void run() {
		  SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						ImageEditorWindow.log("Writing image to file: " + fsett.getFullFilePath(), LOG.MESSAGE);
						ChartExportUtil.writeChartToImage(chart, fsett);
						ImageEditorWindow.log("Succeed to export "+fsett.getFullFilePath().getName()+" at "+fsett.getFullFilePath(), LOG.MESSAGE);
					} catch (Exception e) {
						DialogLoggerUtil.showMessageDialogForTime(null, "FAILED", "Failed to export "+fsett.getFullFilePath().getName()+" at "+fsett.getFullFilePath(), 1500);
						ImageEditorWindow.log("Failed to export "+fsett.getFullFilePath().getName()+" at "+fsett.getFullFilePath(), LOG.ERROR);
						e.printStackTrace();
					}
				}
		  });
	}
}
