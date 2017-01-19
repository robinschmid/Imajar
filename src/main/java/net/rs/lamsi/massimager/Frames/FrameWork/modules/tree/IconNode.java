package net.rs.lamsi.massimager.Frames.FrameWork.modules.tree;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;

public class IconNode extends DefaultMutableTreeNode {

	protected Icon icon;

	public IconNode() {
		this(null);
	}

	public IconNode(Object userObject) {
		this(userObject, true, null);
	}

	public IconNode(Object userObject, boolean allowsChildren, Icon icon) {
		super(userObject, allowsChildren);
		this.icon = icon;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
	}

	public Icon getIcon() {
		return icon;
	}

}
