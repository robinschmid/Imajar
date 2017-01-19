package net.rs.lamsi.massimager.mzmine.interfaces;

import net.sf.mzmine.datamodel.RawDataFile;

public interface MZMineRawDataListsChangedListener {

	public void rawDataListsChanged(RawDataFile[] rawDataLists);
}
