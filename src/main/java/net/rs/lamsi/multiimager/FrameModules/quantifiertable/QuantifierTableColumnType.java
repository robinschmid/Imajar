/*
 * Copyright 2006-2014 The MZmine 2 Development Team
 * 
 * This file is part of MZmine 2.
 * 
 * MZmine 2 is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine 2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package net.rs.lamsi.multiimager.FrameModules.quantifiertable;

import javax.swing.JButton;

import net.sf.mzmine.datamodel.PeakIdentity;
import net.sf.mzmine.datamodel.PeakListRow;

public enum QuantifierTableColumnType {

    //ROWID("ID", Integer.class), //
    CONC(0, "C", Double.class), //
    NAME(1, "Name", String.class), //
    PATH(2, "Path", String.class), //
    PARENT(3, "Parent", String.class), //
    MODE(4, "Mode", String.class), //
    SELECT(5, "Select", String.class);

    private final String columnName;
    private final Class<?> columnClass;
    private final int index;

    QuantifierTableColumnType(int index, String columnName, Class<?> columnClass) {
	this.columnName = columnName;
	this.columnClass = columnClass;
	this.index = index;
    }

    public String getColumnName() {
	return columnName;
    }

    public Class<?> getColumnClass() {
	return columnClass;
    }

    public String toString() {
	return columnName;
    }

	public int getIndex() {
		return index;
	}

}
