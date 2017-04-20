package net.rs.lamsi.multiimager.test;

import net.rs.lamsi.multiimager.Frames.dialogs.selectdata.Image2DSelectDataAreaDialog.SelectionMode;
import net.rs.lamsi.multiimager.Frames.dialogs.selectdata.RectSelection;

public class TestRects {

	public static void main(String[] args) {
		RectSelection rect = new RectSelection(SelectionMode.SELECT, 0,1,10,11);
		System.out.println(""+rect.contains(0, 1));
		System.out.println(""+rect.contains(10, 11));
	}

}
