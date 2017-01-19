package net.rs.lamsi.massimager.mzmine.interfaces;

import net.sf.mzmine.datamodel.PeakList;

public interface MZMinePeakListsChangedListener {

	public void peakListsChanged(PeakList[] peakLists);
}
