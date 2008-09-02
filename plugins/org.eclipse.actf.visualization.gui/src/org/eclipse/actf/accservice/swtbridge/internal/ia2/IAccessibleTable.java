/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Takashi ITOH - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.accservice.swtbridge.internal.ia2;

import org.eclipse.actf.util.win32.COMUtil;
import org.eclipse.swt.internal.ole.win32.GUID;
import org.eclipse.swt.internal.ole.win32.IUnknown;



public class IAccessibleTable extends IUnknown {
    public static final GUID IID = COMUtil.IIDFromString("{35AD8070-C20C-4fb4-B094-F4F7275DD469}"); //$NON-NLS-1$
    
    int address;
    public IAccessibleTable(int address) {
        super(address);
        this.address = address;
    }

    public int get_accessibleAt(int row, int column, int pdispAccessible) {
        return COMUtil.VtblCall(3, address, row, column, pdispAccessible);
    }
    public int get_caption(int pdispAccessible) {
        return COMUtil.VtblCall(4, address, pdispAccessible);
    }
    public int get_childIndex(int rowIndex, int columnIndex, int pChildIndex) {
        return COMUtil.VtblCall(5, address, rowIndex, columnIndex, pChildIndex);
    }
    public int get_columnDescription(int column, int pszDescription) {
        return COMUtil.VtblCall(6, address, column, pszDescription);
    }
    public int get_columnExtentAt(int row, int column, int pnColumnsSpanned) {
        return COMUtil.VtblCall(7, address, row, column, pnColumnsSpanned);
    }
    public int get_columnHeader(int pdispAccessibleTable, int pStartingRowIndex) {
        return COMUtil.VtblCall(8, address, pdispAccessibleTable, pStartingRowIndex);
    }
    public int get_columnIndex(int childIndex, int pColumnIndex) {
        return COMUtil.VtblCall(9, address, childIndex, pColumnIndex);
    }
    public int get_nColumns(int pColumnCount) {
        return COMUtil.VtblCall(10, address, pColumnCount);
    }
    public int get_nRows(int pRowCount) {
        return COMUtil.VtblCall(11, address, pRowCount);
    }
    public int get_nSelectedChildren(int pChildCount) {
        return COMUtil.VtblCall(12, address, pChildCount);
    }
    public int get_nSelectedColumns(int pColumnCount) {
        return COMUtil.VtblCall(13, address, pColumnCount);
    }
    public int get_nSelectedRows(int pRowCount) {
        return COMUtil.VtblCall(14, address, pRowCount);
    }
    public int get_rowDescription(int row, int pszDescription) {
        return COMUtil.VtblCall(15, address, row, pszDescription);
    }
    public int get_rowExtentAt(int row, int column, int pnRowsSpanned) {
        return COMUtil.VtblCall(16, address, row, column, pnRowsSpanned);
    }
    public int get_rowHeader(int pdispAccessibleTable, int pStartingColumnIndex) {
        return COMUtil.VtblCall(17, address, pdispAccessibleTable, pStartingColumnIndex);
    }
    public int get_rowIndex(int childIndex, int pRowIndex) {
        return COMUtil.VtblCall(18, address, childIndex, pRowIndex);
    }
    public int get_selectedChildren(int maxChildren, int ppChildren, int pnChildren) {
        return COMUtil.VtblCall(19, address, maxChildren, ppChildren, pnChildren);
    }
    public int get_selectedRows(int maxRows, int ppRows, int pnRows) {
        return COMUtil.VtblCall(20, address, maxRows, ppRows, pnRows);
    }
    public int get_selectedColumns(int maxColumns, int ppColumns, int pnColumns) {
        return COMUtil.VtblCall(21, address, maxColumns, ppColumns, pnColumns);
    }
    public int get_summary(int pdispAccessible) {
        return COMUtil.VtblCall(22, address, pdispAccessible);
    }
    public int get_isColumnSelected(int column, int pIsSelected) {
        return COMUtil.VtblCall(23, address, column, pIsSelected);
    }
    public int get_isRowSelected(int row, int pIsSelected) {
        return COMUtil.VtblCall(24, address, row, pIsSelected);
    }
    public int get_isSelected(int row, int column, int pIsSelected) {
        return COMUtil.VtblCall(25, address, row, column, pIsSelected);
    }
    public int selectRow(int row) {
        return COMUtil.VtblCall(26, address, row);
    }
    public int selectColumn(int column) {
        return COMUtil.VtblCall(27, address, column);
    }
    public int unselectRow(int row) {
        return COMUtil.VtblCall(28, address, row);
    }
    public int unselectColumn(int column) {
        return COMUtil.VtblCall(29, address, column);
    }
    public int get_rowColumnExtentsAtIndex(int index, int pRow, int pColumn, int pRowExtents, int pColumnExtents, int pIsSelected) {
        return COMUtil.VtblCall(30, address, index, pRow, pColumn, pRowExtents, pColumnExtents, pIsSelected);
    }
    public int get_modelChange(int pModelChange) {
        return COMUtil.VtblCall(31, address, pModelChange);
    }
}
