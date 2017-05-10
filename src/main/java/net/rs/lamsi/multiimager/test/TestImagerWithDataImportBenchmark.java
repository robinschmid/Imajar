package net.rs.lamsi.multiimager.test;

import java.awt.EventQueue;
import java.io.File;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.rs.lamsi.general.settings.importexport.SettingsImageDataImportTxt;
import net.rs.lamsi.general.settings.importexport.SettingsImageDataImportTxt.IMPORT;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.utils.FileAndPathUtil;

public class TestImagerWithDataImportBenchmark {


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
					
					File[] files2 = new File[20];
					for(int i=1; i<=20; i++)
						files2[i-1] = new File("data/UTF8_"+i+".csv");
					
					SettingsImageDataImportTxt settingsDataImport = new SettingsImageDataImportTxt(IMPORT.PRESETS_THERMO_MP17, true, " ", false);
					
					for(int i=0; i<3; i++) { 
						long timemilli = System.currentTimeMillis();
						window.getLogicRunner().importTextDataToImage(settingsDataImport, files2, null);
	 
						long timemilli2 = System.currentTimeMillis();
	 
						System.out.println((timemilli2-timemilli)/1000.0);
					}
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		}); 
	}

}
