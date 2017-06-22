package net.rs.lamsi.general.framework.modules.selectgroupitems;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import net.rs.lamsi.general.framework.listener.ColorChangedListener;
import net.rs.lamsi.general.framework.modules.SettingsModule;
import net.rs.lamsi.general.settings.image.needy.SettingsCollectable2DLink;
import net.rs.lamsi.general.settings.image.needy.SettingsGroupItemSelections;
import net.rs.lamsi.multiimager.Frames.ImageLogicRunner;

public class ModuleSelectGroupItems extends SettingsModule<SettingsGroupItemSelections> {
	private JTable table;
	
	private boolean registerChanges = false;
	
	public ModuleSelectGroupItems() {
		super("Select images", false, SettingsGroupItemSelections.class);
		
		JPanel panel = new JPanel();
		getPnContent().add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		
		table = new JTable();
		table.setModel(new GroupItemSelectionTableModel());
		table.getColumnModel().getColumn(0).setPreferredWidth(15);
		table.getColumnModel().getColumn(0).setMaxWidth(20);
		panel.add(table);
		
		JPanel panel_1 = new JPanel();
		getPnContent().add(panel_1, BorderLayout.NORTH);
		
		JButton btnDeselectAll = new JButton("(de-)select all");
		btnDeselectAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 
				SettingsGroupItemSelections s = getSettings();
				if(s!=null) {
					Map<SettingsCollectable2DLink, Boolean> map = s.getActive();
					if(map!=null && !map.isEmpty()) {
						Boolean state = null;
						for(Map.Entry<SettingsCollectable2DLink, Boolean> entry : map.entrySet()) {
							if(state==null)
								state = !entry.getValue();
							entry.setValue(state);							
						}
						
						// 
						renewTable(s);
					}
				}
			}
		});
		panel_1.add(btnDeselectAll);
		
		
	}

	//################################################################################################
	// Autoupdate
	@Override
	public void addAutoupdater(final ActionListener al, ChangeListener cl, DocumentListener dl, ColorChangedListener ccl, ItemListener il) {
		getModel().addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				if(registerChanges && ImageLogicRunner.IS_UPDATING())
					al.actionPerformed(null);
			}
		});
	}

	@Override
	public void addAutoRepainter(final ActionListener al, ChangeListener cl, DocumentListener dl, ColorChangedListener ccl, ItemListener il) {

	}

	@Override
	public void setSettings(SettingsGroupItemSelections settings, boolean setAllToPanel) {
		super.setSettings(settings, setAllToPanel);
	}
	

	private void renewTable(SettingsGroupItemSelections s) {
		registerChanges = false;
		// clear tableModel
		GroupItemSelectionTableModel model = getModel();
		// 
		model.setData(s.getActive());
		
		registerChanges = true;
	}
	
	private GroupItemSelectionTableModel getModel() {
		return (GroupItemSelectionTableModel)table.getModel();
	}

	//################################################################################################
	// LOGIC
	@Override
	public void setAllViaExistingSettings(SettingsGroupItemSelections s) throws Exception {  
		ImageLogicRunner.setIS_UPDATING(false);

		if(s!= null) {
			renewTable(s);
		}
		// finished
		ImageLogicRunner.setIS_UPDATING(true);
		//		ImageEditorWindow.getEditor().fireUpdateEvent(true);
	} 

	@Override
	public SettingsGroupItemSelections writeAllToSettings(SettingsGroupItemSelections s) {
		if(s!=null) {
			// is already written to settings
			// changes are updated directly
		}
		return s;
	}

}
