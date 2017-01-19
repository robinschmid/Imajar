package net.rs.lamsi.massimager.MyFreeChart.Plot.image2d.listener;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import net.rs.lamsi.massimager.MyFreeChart.ChartLogics;

import org.jfree.chart.ChartPanel;

/**
 * override component Resize test for resize and call resize fucntion
 * @author vukmir69
 *
 */
public abstract class AspectRatioListener implements ComponentListener { 
	public enum RATIO {
		LIMIT_TO_PN_WIDTH, LIMIT_TO_PARENT_SIZE
	}
	
	/**
	 * resize pn to parent with ratio
	 * @param pn
	 * @param parent
	 */
	public void resize(ChartPanel pn, Component parent, RATIO ratio) {
		Dimension dim = null;
		switch(ratio) {
		case LIMIT_TO_PARENT_SIZE:
			dim = ChartLogics.calcMaxSize(pn, parent.getWidth(),  parent.getHeight());
			//pnChartAspectRatio.setSize(dim);
			break;
		case LIMIT_TO_PN_WIDTH:
			int height = (int) ChartLogics.calcHeightToWidth(pn, pn.getWidth());
			dim = new Dimension(50, height); 
			break;
		}
		pn.setPreferredSize(dim);
		pn.setSize(dim);
		parent.revalidate(); 
	}
	
	@Override
	public void componentMoved(ComponentEvent e) {
	}
	@Override
	public void componentShown(ComponentEvent e) {
	}
	@Override
	public void componentHidden(ComponentEvent e) {
	}
}
