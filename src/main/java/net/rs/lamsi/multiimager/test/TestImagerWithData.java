package net.rs.lamsi.multiimager.test;

import java.awt.EventQueue;
import java.io.File;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.rs.lamsi.massimager.Settings.image.SettingsImageDataImportTxt;
import net.rs.lamsi.massimager.Settings.image.SettingsImageDataImportTxt.IMPORT;
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
					String s = FileAndPathUtil.getPathOfJar().getParent()+"/data/qtofwerk.csv";
					File[] files = {new File(s)};
					
					SettingsImageDataImportTxt settingsDataImport = new SettingsImageDataImportTxt(IMPORT.CONTINOUS_DATA_TXT_CSV, true, ",", false);
					window.getLogicRunner().importTextDataToImage(settingsDataImport, files);
					
					s = FileAndPathUtil.getPathOfJar().getParent()+"/data/thermomp17.csv";
					File[] files2 = {new File(s)};
					
					settingsDataImport = new SettingsImageDataImportTxt(IMPORT.PRESETS_THERMO_MP17, true, "	", false);
					window.getLogicRunner().importTextDataToImage(settingsDataImport, files2);
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		}); 
	}

}
