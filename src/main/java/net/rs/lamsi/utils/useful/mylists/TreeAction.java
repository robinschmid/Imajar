package net.rs.lamsi.utils.useful.mylists;

import java.awt.event.*;
import javax.swing.*;

/*
 *	Add an Action to a JTree that can be invoked either by using
 *  the keyboard or a mouse.
 *
 *  By default the Enter will will be used to invoke the Action
 *  from the keyboard although you can specify and KeyStroke you wish.
 *
 *  A double click with the mouse will invoke the same Action.
 *
 *  The Action can be reset at any time.
 */
public class TreeAction implements MouseListener
{
	private static final KeyStroke ENTER = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);

	private JTree tree;
	private KeyStroke keyStroke;

	/*
	 *	Add an Action to the JTree bound by the default KeyStroke
	 */
	public TreeAction(JTree list, Action action)
	{
		this(list, action, ENTER);
	}

	/*
	 *	Add an Action to the JTree bound by the specified KeyStroke
	 */
	public TreeAction(JTree list, Action action, KeyStroke keyStroke)
	{
		this.tree = list;
		this.keyStroke = keyStroke;

		//  Add the KeyStroke to the InputMap
		
		InputMap im = list.getInputMap();
		im.put(keyStroke, keyStroke);

		//  Add the Action to the ActionMap

		setAction( action );

		//  Handle mouse double click

		list.addMouseListener( this );
	}
	/*
	 *  Add the Action to the ActionMap
	 */
	public void setAction(Action action)
	{
		tree.getActionMap().put(keyStroke, action);
	}

	//  Implement MouseListener interface

	public void mouseClicked(MouseEvent e)
	{
		if (e.getClickCount() == 2)
		{
			Action action = tree.getActionMap().get(keyStroke);

			if (action != null)
			{
				ActionEvent event = new ActionEvent(
					tree,
					ActionEvent.ACTION_PERFORMED,
					"");
				action.actionPerformed(event);
			}
		}
	}

 	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mousePressed(MouseEvent e) {}

	public void mouseReleased(MouseEvent e) {}
}

