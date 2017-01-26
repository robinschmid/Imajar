package net.rs.lamsi.massimager.Frames.FrameWork.modules.tree;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
 

public class IconNodeRenderer extends DefaultTreeCellRenderer {
	private int height = 0;
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

		Icon icon = ((IconNode) value).getIcon(); 
		setIcon(icon);
		if(icon!=null) 
			height = icon.getIconHeight();
		else height=0;

		return this;
	}
		  @Override
		public int getHeight() { 
			  if(height == 0)
				  return super.getHeight();
			return height;
		} 
} 