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
import org.eclipse.swt.internal.ole.win32.COM;
import org.eclipse.swt.internal.ole.win32.GUID;
import org.eclipse.swt.internal.ole.win32.IUnknown;



public class IAccessibleText extends IUnknown {
    public static final GUID IID = COMUtil.IIDFromString("{24FD2FFB-3AAD-4a08-8335-A3AD89C0FB4B}"); //$NON-NLS-1$
    
    long address;
    public IAccessibleText(long address) {
        super(address);
        this.address = address;
    }
    
    public int addSelection(int startOffset, int endOffset) {
        return COMUtil.VtblCall(3, address, startOffset, endOffset);
    }
    public int get_attributes(int offset,long pStartOffset, long pEndOffset, long pszTextAttributes) {
        return COMUtil.VtblCall(4, address, offset, pStartOffset, pEndOffset, pszTextAttributes);
    }
    public int get_caretOffset(long pOffset) {
        return COM.VtblCall(5, address, pOffset);
    }
    public int get_characterExtents(int offset, int coordType,long pX, long pY, long pWidth, long pHeight) {
        return COMUtil.VtblCall(6, address, offset, coordType, pX, pY, pWidth, pHeight);
    }
    public int get_nSelections(long pnSelections) {
        return COM.VtblCall(7, address, pnSelections);
    }
    public int get_offsetAtPoint(int x, int y, int coordType, long pOffset) {
        return COMUtil.VtblCall(8, address, x, y, coordType, pOffset);
    }
    public int get_selection(int selectionIndex,long pStartOffset,long pEndOffset) {
        return COM.VtblCall(9, address, selectionIndex, pStartOffset, pEndOffset);
    }
    public int get_text(int startOffset, int endOffset, long pszText) {
        return COM.VtblCall(10, address, startOffset, endOffset, pszText);
    }
    public int get_textBeforeOffset(int offset, int boundaryType, long pStartOffset, long pEndOffset, long pszText) {
        return COMUtil.VtblCall(11, address, offset, boundaryType, pStartOffset, pEndOffset, pszText);
    }
    public int get_textAfterOffset(int offset, int boundaryType, long pStartOffset, long pEndOffset, long pszText) {
        return COMUtil.VtblCall(12, address, offset, boundaryType, pStartOffset, pEndOffset, pszText);
    }
    public int get_textAtOffset(int offset, int boundaryType, long pStartOffset, long pEndOffset, long pszText) {
        return COMUtil.VtblCall(13, address, offset, boundaryType, pStartOffset, pEndOffset, pszText);
    }
    public int removeSelection(int selectionIndex) {
        return COMUtil.VtblCall(14, address, selectionIndex);
    }
    public int setCaretOffset(int offset) {
        return COMUtil.VtblCall(15, address, offset);
    }
    public int setSelection(int selectionIndex, int startOffset, int endOffset) {
        return COMUtil.VtblCall(16, address, selectionIndex, startOffset, endOffset);
    }
    public int get_nCharacters(long pnCharacters) {
        return COM.VtblCall(17, address, pnCharacters);
    }
    public int scrollSubstringTo(int startIndex, int endIndex, int scrollType) {
        return COMUtil.VtblCall(18, address, startIndex, endIndex, scrollType);
    }
    public int scrollSubstringToPoint(int startIndex, int endIndex, int coordinateType, int x, int y) {
        return COMUtil.VtblCall(19, address, startIndex, endIndex, coordinateType, x, y);
    }
    public int get_newText(long pNewText) {
        return COM.VtblCall(20, address, pNewText);
    }
    public int get_oldText(long pOldText) {
        return COM.VtblCall(21, address, pOldText);
    }
}
