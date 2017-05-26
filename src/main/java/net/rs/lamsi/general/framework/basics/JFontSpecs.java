package net.rs.lamsi.general.framework.basics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Paint;
import java.awt.event.ItemListener;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentListener;

public class JFontSpecs extends JPanel {

	private JFontBox fontBox;
	private JFontStyleBox styleBox;
	private JTextField txtSize;
	private JColorPickerButton color;
	
	
	public JFontSpecs() {
		super(); 

		fontBox = new JFontBox();
		add(fontBox);
		
		styleBox = new JFontStyleBox();
		add(styleBox);
		
		
		txtSize = new JTextField();
		txtSize.setHorizontalAlignment(SwingConstants.RIGHT);
		txtSize.setText("14");
		add(txtSize);
		txtSize.setColumns(3);
		
		color = new JColorPickerButton(this);
		add(color);
		color.setColor(Color.WHITE);
		
//		setPreferredSize(new Dimension(280, 30));
		this.validate();
		setMaximumSize(getPreferredSize());
	}


	public void setSelectedFont(Font font) {
		fontBox.setSelectedItem(font.getName());
		styleBox.setSelectedIndex(font.getStyle());
		txtSize.setText(String.valueOf(font.getSize()));
	}
	public Font getSelectedFont() {
		return new Font((String)fontBox.getSelectedItem(), styleBox.getSelectedStyle(), Integer.valueOf(txtSize.getText()));
	}
	
	public Color getColor() {
		return color.getColor();
	}
	public void setColor(Color c) {
		color.setColor(c);
	}

	public void setColor(Paint c) {
		color.setColor((Color)c);
	}

	public void addListener(ColorChangedListener ccl, ItemListener il, DocumentListener dl) {
		fontBox.addItemListener(il);
		styleBox.addItemListener(il);
		txtSize.getDocument().addDocumentListener(dl);
		color.addColorChangedListener(ccl);
	}


}
