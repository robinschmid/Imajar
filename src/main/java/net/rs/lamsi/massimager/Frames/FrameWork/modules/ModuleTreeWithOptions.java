package net.rs.lamsi.massimager.Frames.FrameWork.modules;
import java.awt.BorderLayout;
import java.util.Vector;

import javax.swing.JPanel;


public class ModuleTreeWithOptions <T> extends ModuleTree {
	private JPanel pnOptions;

	/**
	 * Create the panel.
	 */
	public ModuleTreeWithOptions(String stitle, boolean westside) {
		super(stitle, westside);
		JPanel panelopholder = new JPanel();
		getPnContent().add(panelopholder, BorderLayout.NORTH);
		panelopholder.setLayout(new BorderLayout(0, 0));
		
		pnOptions = new JPanel();
		panelopholder.add(pnOptions, BorderLayout.WEST);
	}

	public JPanel getPnOptions() {
		return pnOptions;
	}
}
