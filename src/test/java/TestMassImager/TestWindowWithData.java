package TestMassImager;

import java.awt.EventQueue;
import java.io.File;

import net.rs.lamsi.massimager.Frames.Window;
import net.rs.lamsi.massimager.mzmine.MZMineLogicsConnector;

public class TestWindowWithData {

	/**
	 * @param args
	 */
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
					

					String s = "C:/DATA/MALDI Sh/examples/HR2MSI mouse urinary bladder S096.imzML";
					MZMineLogicsConnector.importRawDataDirect(new File[]{new File(s)});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
