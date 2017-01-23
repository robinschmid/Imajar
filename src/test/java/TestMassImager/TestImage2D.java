package TestMassImager;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.data.DataPoint2D;
import net.rs.lamsi.general.datamodel.image.data.Dataset2D;
import net.rs.lamsi.general.datamodel.image.data.ScanLine;
import net.rs.lamsi.massimager.Settings.SettingsImage;
import net.rs.lamsi.massimager.Settings.SettingsPaintScale;

public class TestImage2D {

	public static void main(int[] args) {
		DataPoint2D[] dp = new DataPoint2D[10];
		ScanLine[] line = new ScanLine[10];
		
		for(int i=0; i<dp.length; i++) {
			dp[i] = new DataPoint2D(i, i);
		}
		for(int i=0; i<line.length; i++) {
			line[i] = new ScanLine(dp);
		}
		Image2D img = new Image2D(new Dataset2D(line), new SettingsPaintScale(), new SettingsImage("",""));
		

	}

	
}
