package net.rs.lamsi.massimager.test;

import java.awt.EventQueue;
import java.io.File;

import net.rs.lamsi.massimager.Frames.Window;
import net.rs.lamsi.massimager.mzmine.MZMineLogicsConnector;

public class TestWindowWithData {


	public static void main(String[] args) { 
		// connect to mzmine 
		// have to load it before everything else
		MZMineLogicsConnector.connectToMZMine();

		// start MassImager application
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Window window = new Window();
					window.getFrame().setVisible(true);
					// load data
					// /data/triggerimage/data01.mzXML  to 45.mzXML

					File[] files = new File[15];
					for(int i=1; i<16; i++) {
						String s = window.getPathOfJar().getParent()+"/data/triggerimage/data";
						if(i<10) s += "0";
						s+= i+".mzXML";
						files[i-1] = new File(s);
					} 
					MZMineLogicsConnector.importRawDataDirect(files);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}); 
	}

}
