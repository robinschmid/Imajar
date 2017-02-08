package net.rs.lamsi.multiimager.test;

import java.awt.EventQueue;
import java.io.File;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.rs.lamsi.massimager.Settings.importexport.SettingsImageDataImportTxt;
import net.rs.lamsi.massimager.Settings.importexport.SettingsImageDataImportTxt.IMPORT;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.utils.FileAndPathUtil;

public class TestImagerWithData {


	public static void main(String[] args) { 
		try {
			//UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// start MultiImager application
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ImageEditorWindow window = new ImageEditorWindow();
					window.setVisible(true);
					// load data ThermoMP17 Image qtofwerk.csv 
					String s = "C:\\DATA\\Agilent ICP\\Mstd2\\";
					File[] files = {new File(s)};
					
					SettingsImageDataImportTxt settingsDataImport = new SettingsImageDataImportTxt(IMPORT.MULTIPLE_FILES_LINES_TXT_CSV, true, ",", false);
					window.getLogicRunner().importTextDataToImage(settingsDataImport, files);
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		}); 
	}

}
