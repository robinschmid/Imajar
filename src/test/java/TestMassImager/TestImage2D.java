package TestMassImager;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.data.twodimensional.DataPoint2D;
import net.rs.lamsi.general.datamodel.image.data.twodimensional.Dataset2D;
import net.rs.lamsi.general.datamodel.image.data.twodimensional.ScanLine2D;
import net.rs.lamsi.massimager.Settings.image.SettingsImage;
import net.rs.lamsi.massimager.Settings.image.SettingsPaintScale;

public class TestImage2D {

	public static void main(int[] args) {
		DataPoint2D[] dp = new DataPoint2D[10];
		ScanLine2D[] line = new ScanLine2D[10];
		
		for(int i=0; i<dp.length; i++) {
			dp[i] = new DataPoint2D(i, i);
		}
		for(int i=0; i<line.length; i++) {
			line[i] = new ScanLine2D(dp);
		}
		Image2D img = new Image2D(new Dataset2D(line), new SettingsPaintScale(), new SettingsImage("",""));
		

	}

	
}
