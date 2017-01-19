package net.rs.lamsi.massimager.Frames.FrameWork.modules;
import java.awt.BorderLayout;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionListener;


public class ModuleList extends Module {
	private JList list;
	protected Vector listVector;

	/**
	 * Create the panel.
	 */
	public ModuleList(String stitle, boolean westside, Vector listVector) {
		super(stitle, westside);
		JScrollPane scrollPane = new JScrollPane();
		getPnContent().add(scrollPane, BorderLayout.CENTER);
		
		list = new JList(new DefaultListModel()); 
		scrollPane.setViewportView(list);
		
		this.listVector = listVector;
	}

	public void addElement(Object o, String listname) {
		((DefaultListModel)list.getModel()).addElement(listname);
		listVector.add(o);
		this.validate();
	}
	public void addElement(Object o) {
		((DefaultListModel)list.getModel()).addElement(o);
		listVector.add(o);
	}
	public void addElement(int i, Object o, String listname) {
		((DefaultListModel)list.getModel()).add(i, listname);
		listVector.add(i, o);
	}
	
	public void removeElement(int sel[]) {
		try{ 
			if(sel.length>0) {
				for(int i=0; i<sel.length; i++) {
					remove(sel[i]-i);
				}
			} 
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	public void removeElement(int i) {
		try{  
			((DefaultListModel)list.getModel()).remove(i); 
			listVector.remove(i); 
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	/**
	 * removes all elements from the list
	 */
	public void removeAllElements() {
		try{  
			((DefaultListModel)list.getModel()).removeAllElements(); 
			listVector.removeAllElements(); 
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public JList getList() {
		return list;
	}
	public void setListVector(Vector listVector) {
		this.listVector = listVector;
	}
	public void addListSelectionListener(ListSelectionListener selL) {
		list.addListSelectionListener(selL);
	}
}
