package net.rs.lamsi.massimager.Frames.FrameWork.modules;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import net.rs.lamsi.massimager.Frames.FrameWork.modules.listeners.HideShowChangedListener;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.menu.ModuleMenu;
import java.awt.Component;
import javax.swing.Box;

public class Module extends JPanel {
	private ModuleMenu menu;
	private JLabel lbTitle;
	private JPanel pnOpen;
	private JPanel pnHidden;
	private JPanel pnContent;
	private JPanel pnTitle;
	
	protected boolean showTitleAlways = false;
	
	private HideShowChangedListener showListener;
	private JPanel panel;
	private JLabel lbTitleHidden;
	private Component titleStrut;

	/**
	 * Create the panel.
	 */
	public Module() {
		this("");
	}
	public Module(String stitle) {
		this(stitle, false);
	} 
	public Module(String stitle, boolean westside) {
		this(stitle, westside, null);
	}
	public Module(String stitle, boolean westside, ModuleMenu menu) {
		setLayout(new BorderLayout(0, 0));
		setBorder(new LineBorder(new Color(0, 0, 0)));
		
		pnHidden = new JPanel();
		pnHidden.setVisible(false);
		add(pnHidden, BorderLayout.WEST);
		pnHidden.setLayout(new BorderLayout(0, 0));
		
		panel = new JPanel();
		pnHidden.add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));
		
		
		JButton btnShow = new JButton("");
		panel.add(btnShow, BorderLayout.WEST);
		btnShow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hideModul(false);  
				if(showListener!=null)
					showListener.moduleChangedToShown(true);
			}
		});
		btnShow.setIcon(new ImageIcon(Module.class.getResource("/img/btn-plus-20x20.png")));
		btnShow.setPreferredSize(new Dimension(20, 20));
		btnShow.setMaximumSize(new Dimension(20, 20));
		btnShow.setMinimumSize(new Dimension(20, 20));
		
		lbTitleHidden = new JLabel(stitle);
		lbTitleHidden.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lbTitleHidden, BorderLayout.EAST);
		
		titleStrut = Box.createHorizontalStrut(20);
		panel.add(titleStrut, BorderLayout.CENTER);
		titleStrut.setVisible(false);
		lbTitleHidden.setVisible(false);
		
		pnOpen = new JPanel();
		add(pnOpen, BorderLayout.CENTER);
		pnOpen.setLayout(new BorderLayout(0, 0));
		
		pnTitle = new JPanel();
		pnOpen.add(pnTitle, BorderLayout.NORTH);
		pnTitle.setLayout(new BorderLayout(0, 0));
		
		JButton btnHide = new JButton("");
		btnHide.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hideModul(true);
				if(showListener!=null)
					showListener.moduleChangedToShown(false);
			}
		});
		btnHide.setIcon(new ImageIcon(Module.class.getResource("/img/btn-minus-20x20.png")));
		btnHide.setPreferredSize(new Dimension(20, 20));
		btnHide.setMinimumSize(new Dimension(20, 20));
		btnHide.setMaximumSize(new Dimension(20, 20));
		pnTitle.add(btnHide, (westside? BorderLayout.EAST : BorderLayout.WEST));
		
		lbTitle = new JLabel(stitle);
		lbTitle.setHorizontalAlignment(SwingConstants.CENTER);
		pnTitle.add(lbTitle, BorderLayout.CENTER);
		
		pnContent = new JPanel();
		pnOpen.add(pnContent, BorderLayout.CENTER);
		pnContent.setLayout(new BorderLayout(0, 0));

		if(menu!=null)
			addPopupMenu(menu);
	}
	// adding a menu
	public void addPopupMenu(ModuleMenu menu) {
		this.menu = menu;
		getPnTitle().add(menu, BorderLayout.EAST);
		getPnTitle().validate();
	}
	
	
	public void hideModul(boolean hide) {
		getPnOpen().setVisible(!hide);
		getPnHidden().setVisible(hide);
	}
	public void setTitle(String title) {
		getLbTitle().setText(title);
		getLbTitleHidden().setText(title);
	}
	
	public JLabel getLbTitle() {
		return lbTitle;
	}
	public JPanel getPnOpen() {
		return pnOpen;
	}
	public JPanel getPnHidden() {
		return pnHidden;
	}
	public JPanel getPnContent() {
		return pnContent;
	}
	public JPanel getPnTitle() {
		return pnTitle;
	}
	
	public void addHideShowChangedListener(HideShowChangedListener listener) { 
			showListener = listener;
	}
	
	public double doubleFromTxt(JTextField txt) {
		try {
			return Double.valueOf(txt.getText());
		} catch(Exception ex) {
			return 0;
		}
	}
	public float floatFromTxt(JTextField txt) {
		try {
			return Float.valueOf(txt.getText());
		} catch(Exception ex) {
			return 0;
		}
	}
	public int intFromTxt(JTextField txt) {
		try {
			return Integer.valueOf(txt.getText());
		} catch(Exception ex) {
			return 0;
		}
	}
	public ModuleMenu getPopupMenu() {
		return menu;
	}
	public boolean isShowTitleAlways() {
		return showTitleAlways;
	}
	public void setShowTitleAlways(boolean showTitleAlways) {
		this.showTitleAlways = showTitleAlways;
		getLbTitleHidden().setVisible(showTitleAlways); 
		getTitleStrut().setVisible(showTitleAlways);
	}
	public JLabel getLbTitleHidden() {
		return lbTitleHidden;
	} 
	public Component getTitleStrut() {
		return titleStrut;
	}
}
