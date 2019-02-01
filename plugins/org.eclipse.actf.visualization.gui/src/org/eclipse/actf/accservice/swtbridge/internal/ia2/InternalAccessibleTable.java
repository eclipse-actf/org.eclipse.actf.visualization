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

import org.eclipse.actf.accservice.swtbridge.AccessibleObject;
import org.eclipse.actf.accservice.swtbridge.ia2.AccessibleTable;
import org.eclipse.actf.accservice.swtbridge.ia2.AccessibleTableModelChange;
import org.eclipse.actf.accservice.swtbridge.ia2.RowColumnExtents;
import org.eclipse.actf.accservice.swtbridge.internal.InternalAccessibleObject;
import org.eclipse.actf.util.win32.MemoryUtil;
import org.eclipse.actf.util.win32.NativeIntAccess;
import org.eclipse.actf.util.win32.NativeStringAccess;
import org.eclipse.swt.internal.ole.win32.COM;
import org.eclipse.swt.ole.win32.OLE;




public class InternalAccessibleTable implements AccessibleTable {

    private IAccessibleTable accessibleTable = null;
    
    public InternalAccessibleTable(long address) {
        accessibleTable = new IAccessibleTable(address);
        accessibleTable.AddRef();
    }
    public void dispose() {
        if( null != accessibleTable ) {
            accessibleTable.Release();
            accessibleTable = null;
        }
    }
    protected void finalize() throws Throwable {
        dispose();
        super.finalize();
    }
    
    public AccessibleObject getAccessibleCellAt(int row, int column) {
        if( null != accessibleTable ) {
            NativeIntAccess nia = new NativeIntAccess();
            try {
                if( OLE.S_OK == accessibleTable.get_accessibleAt(row,column,nia.getAddress()) ) {
                    return InternalAccessibleObject.newInstance(nia.getInt());
                }
            }
            finally {
                nia.dispose();
            }
        }
        return null;
    }
    public Object getAccessibleCaption() {
        if( null != accessibleTable ) {
            NativeIntAccess nia = new NativeIntAccess();
            try {
                if( OLE.S_OK == accessibleTable.get_caption(nia.getAddress()) ) {
                    return InternalAccessibleObject.newInstance(nia.getInt());
                }
            }
            finally {
                nia.dispose();
            }
        }
        return null;
    }
    public int getAccessibleIndex(int rowIndex, int columnIndex) {
        if( null != accessibleTable ) {
            NativeIntAccess nia = new NativeIntAccess();
            try {
                if( OLE.S_OK == accessibleTable.get_childIndex(rowIndex, columnIndex, nia.getAddress()) ) {
                    return nia.getInt();
                }
            }
            finally {
                nia.dispose();
            }
        }
        return -1;
    }
    public String getAccessibleColumnDescription(int column) {
        if( null != accessibleTable ) {
            NativeStringAccess nsa = new NativeStringAccess();
            try {
                if( OLE.S_OK == accessibleTable.get_columnDescription(column, nsa.getAddress()) ) {
                    return nsa.getString();
                }
            }
            finally {
                nsa.dispose();
            }
        }
        return null;
    }
    public int getAccessibleColumnExtentAt(int row, int column) {
        if( null != accessibleTable ) {
            NativeIntAccess nia = new NativeIntAccess();
            try {
                if( OLE.S_OK == accessibleTable.get_columnExtentAt(row, column, nia.getAddress()) ) {
                    return nia.getInt();
                }
            }
            finally {
                nia.dispose();
            }
        }
        return -1;
    }
    public InternalAccessibleTable getAccessibleColumnHeaders(int[] startingRowIndex) {
        if( null != accessibleTable ) {
            NativeIntAccess nia = new NativeIntAccess(2);
            try {
                if( OLE.S_OK == accessibleTable.get_columnHeader(nia.getAddress(0),nia.getAddress(1)) ) {
                    startingRowIndex[0] = nia.getInt(1);
                    return new InternalAccessibleTable(nia.getInt(0));
                }
            }
            finally {
                nia.dispose();
            }
        }
        return null;
    }
    public int getAccessibleColumnIndex(int childIndex) {
        if( null != accessibleTable ) {
            NativeIntAccess nia = new NativeIntAccess();
            try {
                if( OLE.S_OK == accessibleTable.get_columnIndex(childIndex, nia.getAddress()) ) {
                    return nia.getInt();
                }
            }
            finally {
                nia.dispose();
            }
        }
        return -1;
    }
    public int getAccessibleColumnCount() {
        if( null != accessibleTable ) {
            NativeIntAccess nia = new NativeIntAccess();
            try {
                if( OLE.S_OK == accessibleTable.get_nColumns(nia.getAddress()) ) {
                    return nia.getInt();
                }
            }
            finally {
                nia.dispose();
            }
        }
        return 0;
    }
    public int getAccessibleRowCount() {
        if( null != accessibleTable ) {
            NativeIntAccess nia = new NativeIntAccess();
            try {
                if( OLE.S_OK == accessibleTable.get_nRows(nia.getAddress()) ) {
                    return nia.getInt();
                }
            }
            finally {
                nia.dispose();
            }
        }
        return 0;
    }
    public int getSelectedAccessibleChildCount() {
        if( null != accessibleTable ) {
            NativeIntAccess nia = new NativeIntAccess();
            try {
                if( OLE.S_OK == accessibleTable.get_nSelectedChildren(nia.getAddress()) ) {
                    return nia.getInt();
                }
            }
            finally {
                nia.dispose();
            }
        }
        return 0;
    }
    public int getSelectedAccessibleColumnCount() {
        if( null != accessibleTable ) {
            NativeIntAccess nia = new NativeIntAccess();
            try {
                if( OLE.S_OK == accessibleTable.get_nSelectedColumns(nia.getAddress()) ) {
                    return nia.getInt();
                }
            }
            finally {
                nia.dispose();
            }
        }
        return 0;
    }
    public int getSelectedAccessibleRowCount() {
        if( null != accessibleTable ) {
            NativeIntAccess nia = new NativeIntAccess();
            try {
                if( OLE.S_OK == accessibleTable.get_nSelectedRows(nia.getAddress()) ) {
                    return nia.getInt();
                }
            }
            finally {
                nia.dispose();
            }
        }
        return 0;
    }
    public String getAccessibleRowDescription(int row) {
        if( null != accessibleTable ) {
            NativeStringAccess nsa = new NativeStringAccess();
            try {
                if( OLE.S_OK == accessibleTable.get_rowDescription(row, nsa.getAddress()) ) {
                    return nsa.getString();
                }
            }
            finally {
                nsa.dispose();
            }
        }
        return null;
    }
    public int getAccessibleRowExtentAt(int row, int column) {
        if( null != accessibleTable ) {
            NativeIntAccess nia = new NativeIntAccess();
            try {
                if( OLE.S_OK == accessibleTable.get_rowExtentAt(row, column, nia.getAddress()) ) {
                    return nia.getInt();
                }
            }
            finally {
                nia.dispose();
            }
        }
        return 0;
    }
    public InternalAccessibleTable getAccessibleRowHeaders(int[] startingColumnIndex) {
        if( null != accessibleTable ) {
            NativeIntAccess nia = new NativeIntAccess(2);
            try {
                if( OLE.S_OK == accessibleTable.get_rowHeader(nia.getAddress(0),nia.getAddress(1)) ) {
                    startingColumnIndex[0] = nia.getInt(1);
                    return new InternalAccessibleTable(nia.getInt(0));
                }
            }
            finally {
                nia.dispose();
            }
        }
        return null;
    }
    public int getAccessibleRowIndex(int childIndex) {
        if( null != accessibleTable ) {
            NativeIntAccess nia = new NativeIntAccess();
            try {
                if( OLE.S_OK == accessibleTable.get_rowIndex(childIndex, nia.getAddress()) ) {
                    return nia.getInt();
                }
            }
            finally {
                nia.dispose();
            }
        }
        return 0;
    }
    public int[] getSelectedAccessibleChildren(int maxChildren) {
        if( null != accessibleTable && maxChildren > 0 ) {
            NativeIntAccess nia = new NativeIntAccess(maxChildren+1);
            try {
                if( OLE.S_OK == accessibleTable.get_selectedChildren(maxChildren, nia.getAddress(1),nia.getAddress(0)) ) {
                    int count = nia.getInt(0);
                    int[] pRet = new int[count];
                    MemoryUtil.MoveMemory(pRet, nia.getInt(1), 4*count);
                    COM.CoTaskMemFree(nia.getInt(1));
                    return pRet;
                }
            }
            finally {
                nia.dispose();
            }
        }
        return null;
    }
    public int[] getSelectedAccessibleRows(int maxRows) {
        if( null != accessibleTable && maxRows > 0 ) {
            NativeIntAccess nia = new NativeIntAccess(maxRows+1);
            try {
                if( OLE.S_OK == accessibleTable.get_selectedRows(maxRows, nia.getAddress(1),nia.getAddress(0)) ) {
                    int count = nia.getInt(0);
                    int[] pRet = new int[count];
                    MemoryUtil.MoveMemory(pRet, nia.getInt(1), 4*count);
                    COM.CoTaskMemFree(nia.getInt(1));
                    return pRet;
                }
            }
            finally {
                nia.dispose();
            }
        }
        return null;
    }
    public int[] getSelectedAccessibleColumns(int maxColumns) {
        if( null != accessibleTable ) {
            NativeIntAccess nia = new NativeIntAccess(maxColumns+1);
            try {
                if( OLE.S_OK == accessibleTable.get_selectedColumns(maxColumns, nia.getAddress(1),nia.getAddress(0)) ) {
                    int count = nia.getInt(0);
                    int[] pRet = new int[count];
                    MemoryUtil.MoveMemory(pRet, nia.getInt(1), 4*count);
                    COM.CoTaskMemFree(nia.getInt(1));
                    return pRet;
                }
            }
            finally {
                nia.dispose();
            }
        }
        return null;
    }
    public Object getAccessibleSummary() {
        if( null != accessibleTable ) {
            NativeIntAccess nia = new NativeIntAccess();
            try {
                if( OLE.S_OK == accessibleTable.get_summary(nia.getAddress()) ) {
                    return InternalAccessibleObject.newInstance(nia.getInt());
                }
            }
            finally {
                nia.dispose();
            }
        }
        return null;
    }
    public boolean isAccessibleColumnSelected(int column) {
        if( null != accessibleTable ) {
            NativeIntAccess nia = new NativeIntAccess();
            try {
                if( OLE.S_OK == accessibleTable.get_isColumnSelected(column, nia.getAddress()) ) {
                    return 0 != nia.getInt();
                }
            }
            finally {
                nia.dispose();
            }
        }
        return false;
    }
    public boolean isAccessibleRowSelected(int row) {
        if( null != accessibleTable ) {
            NativeIntAccess nia = new NativeIntAccess();
            try {
                if( OLE.S_OK == accessibleTable.get_isRowSelected(row, nia.getAddress()) ) {
                    return 0 != nia.getInt();
                }
            }
            finally {
                nia.dispose();
            }
        }
        return false;
    }
    public boolean isAccessibleSelected(int row, int column) {
        if( null != accessibleTable ) {
            NativeIntAccess nia = new NativeIntAccess();
            try {
                if( OLE.S_OK == accessibleTable.get_isSelected(row, column, nia.getAddress()) ) {
                    return 0 != nia.getInt();
                }
            }
            finally {
                nia.dispose();
            }
        }
        return false;
    }
    public boolean selectAccessibleRow(int row) {
        if( null != accessibleTable ) {
            return OLE.S_OK == accessibleTable.selectRow(row);
        }
        return false;
    }
    public boolean selectAccessibleColumn(int column) {
        if( null != accessibleTable ) {
            return OLE.S_OK == accessibleTable.selectColumn(column);
        }
        return false;
    }
    public boolean unselectAccessibleRow(int row) {
        if( null != accessibleTable ) {
            return OLE.S_OK == accessibleTable.unselectRow(row);
        }
        return false;
    }
    public boolean unselectAccessibleColumn(int column) {
        if( null != accessibleTable ) {
            return OLE.S_OK == accessibleTable.unselectColumn(column);
        }
        return false;
    }
    public RowColumnExtents getAccessibleRowColumnExtentAtIndex(int index) {
        if( null != accessibleTable ) {
            NativeIntAccess nia = new NativeIntAccess(5);
            try {
                if( OLE.S_OK == accessibleTable.get_rowColumnExtentsAtIndex(index, 
                    nia.getAddress(0),
                    nia.getAddress(1),
                    nia.getAddress(2),
                    nia.getAddress(3),
                    nia.getAddress(4)) ) {
                    return new RowColumnExtents(
                            nia.getInt(0),
                            nia.getInt(1),
                            nia.getInt(2),
                            nia.getInt(3),
                            0!=nia.getInt(4));
                }
            }
            finally {
                nia.dispose();
            }
        }
        return null;
    }
    public AccessibleTableModelChange getAccessibleTableModelChange() {
        if( null != accessibleTable ) {
            NativeIntAccess nia = new NativeIntAccess(5);
            try {
                if( OLE.S_OK == accessibleTable.get_modelChange(nia.getAddress()) ) {
                    return new AccessibleTableModelChange(
                            nia.getInt(0),
                            nia.getInt(1),
                            nia.getInt(2),
                            nia.getInt(3),
                            nia.getInt(4));
                }
            }
            finally {
                nia.dispose();
            }
        }
        return null;
    }
    
}
