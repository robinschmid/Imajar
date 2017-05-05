package net.rs.lamsi.massimager.MyMZ.oldmzjavasave;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import net.rs.lamsi.general.settings.SettingsConverterRAW;
import net.rs.lamsi.massimager.Frames.LogicRunner;
import net.rs.lamsi.utils.useful.dialogs.ProgressDialog;

public class MZFileConverter {  
	private static final File PATH_RAW_MSCONVERT = new File("msconvert/");
	private static boolean isReady = true; 
	 


	// Vielleicht eine Waitlist erstellen
	public static void convertRAWtoMzXml(SettingsConverterRAW settings, LogicRunner logicRunner, File inputFile) throws Exception {  
		// Generate outputfile in same folder
		String fileName = inputFile.getName().substring(0, inputFile.getName().lastIndexOf("."));
		File outputFile = new File(inputFile.getParent(), fileName+".mzXML"); 
		// convert 
		isReady = false;
		// msconvert
		String cmd = settings.getCMDLine(inputFile, outputFile);
		//
		System.out.println("STARTED");
		long starttime = System.currentTimeMillis();
		//
		//Process p = Runtime.getRuntime().exec(cmd, null, PATH_RAW_MSCONVERT); 
		Process p = Runtime.getRuntime().exec("cmd /c msconvert\\"+cmd); 
		
		BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		while((line = br.readLine())!=null) {
			System.out.println(line);
		}
		br.close();
		
		int i = p.waitFor();
		//
		long endtime = System.currentTimeMillis();
		System.out.println("ENDED in "+((endtime-starttime)/1000)+" seconds"); 
		// Hälfte Prog gelaufen:
		ProgressDialog.addProgressStep(0.5);
		//
		//logicRunner.loadMzXMLFile(outputFile);
		// Nochmal hälfte Progress 
		ProgressDialog.addProgressStep(0.5);
		//
		isReady = true;
		//p.destroy(); 
	}    

}
