package net.rs.lamsi.multiimager.test;

import java.awt.EventQueue;
import java.io.File;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.rs.lamsi.general.settings.importexport.SettingsImageDataImportTxt;
import net.rs.lamsi.general.settings.importexport.SettingsImageDataImportTxt.IMPORT;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.utils.FileAndPathUtil;
import net.rs.lamsi.utils.useful.FileNameExtFilter;

public class TestImagerWithData120Files {


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
					// load data ThermoMP17 Image
					String s = FileAndPathUtil.getPathOfJar().getParent()+"/data/Agilent Brain quanti/";
					File[] files = {new File(s)};
					
					SettingsImageDataImportTxt settingsDataImport = new SettingsImageDataImportTxt(IMPORT.MULTIPLE_FILES_LINES_TXT_CSV, true, ",", new FileNameExtFilter("", "csv"), true);
					window.getLogicRunner().importTextDataToImage(settingsDataImport, files);
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		}); 
	}

}
