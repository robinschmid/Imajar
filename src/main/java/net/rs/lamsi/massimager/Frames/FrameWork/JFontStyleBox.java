package net.rs.lamsi.massimager.Frames.FrameWork;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

public class JFontStyleBox extends JComboBox<Object> {
	
	private enum Style {
		PLAIN, BOLD, ITALIC, BOLDITALIC
	}

	public JFontStyleBox() {
		super(Style.values());
		setSelectedIndex(0);
	}

	public int getSelectedStyle() {
		switch((Style)getSelectedItem()) {
		case PLAIN: return Font.PLAIN;
		case BOLD: return Font.BOLD;
		case ITALIC: return Font.ITALIC;
		case BOLDITALIC: return Font.BOLD+Font.ITALIC;
		}
		return Font.PLAIN;
	}
	
}
