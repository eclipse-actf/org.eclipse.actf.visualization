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

import org.eclipse.actf.accservice.swtbridge.ia2.AccessibleText;
import org.eclipse.actf.accservice.swtbridge.ia2.TextSegment;
import org.eclipse.actf.util.win32.MemoryUtil;
import org.eclipse.actf.util.win32.NativeIntAccess;
import org.eclipse.actf.util.win32.NativeStringAccess;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.ole.win32.COM;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.ole.win32.OLE;




public class InternalAccessibleText implements AccessibleText {

    private IAccessibleText accessibleText = null;
    
    public InternalAccessibleText(long address) {
        accessibleText = new IAccessibleText(address);
        accessibleText.AddRef();
    }
    public void dispose() {
        if( null != accessibleText ) {
            accessibleText.Release();
            accessibleText = null;
        }
    }
    protected void finalize() throws Throwable {
        dispose();
        super.finalize();
    }

    public boolean addSelection(int startOffset, int endOffset) {
        if( null != accessibleText ) {
            return OLE.S_OK == accessibleText.addSelection(startOffset, endOffset);
        }
        return false;
    }
    public TextSegment getCharacterAttributes(int offset) {
        if( null != accessibleText ) {
            NativeIntAccess nia = new NativeIntAccess(2);
            NativeStringAccess nsa = new NativeStringAccess();
            try {
                if( OLE.S_OK == accessibleText.get_attributes(offset, nia.getAddress(0), nia.getAddress(1), nsa.getAddress()) ) {
                    return new TextSegment(nsa.getString(),nia.getInt(0),nia.getInt(1));
                }
            }
            catch (Exception e) {
            }
            finally {
                nsa.dispose();
                nia.dispose();
            }
        }
        return null;
    }
    public int getCaretPosition() {
        if( null != accessibleText ) {
            NativeIntAccess nia = new NativeIntAccess();
            try {
                if( OLE.S_OK == accessibleText.get_caretOffset(nia.getAddress()) ) {
                    return nia.getInt();
                }
            }
            catch (Exception e) {
            }
            finally {
                nia.dispose();
            }
        }
        return -1;
    }
    public Rectangle getCharacterBounds(int offset, int coordType) {
        if( null != accessibleText ) {
            NativeIntAccess nia = new NativeIntAccess(4);
            try {
                if( OLE.S_OK == accessibleText.get_characterExtents(offset, coordType, 
                    nia.getAddress(0), 
                    nia.getAddress(1), 
                    nia.getAddress(2), 
                    nia.getAddress(3)) ) {
                    return new Rectangle(
                            nia.getInt(0),
                            nia.getInt(1),
                            nia.getInt(2),
                            nia.getInt(3));
                }
            }
            catch (Exception e) {
            }
            finally {
                nia.dispose();
            }
        }
        return null;
    }
    public int getSelectionCount() {
        if( null != accessibleText ) {
            NativeIntAccess nia = new NativeIntAccess();
            try {
                if( OLE.S_OK == accessibleText.get_nSelections(nia.getAddress()) ) {
                    return nia.getInt();
                }
            }
            catch (Exception e) {
            }
            finally {
                nia.dispose();
            }
        }
        return 0;
    }
    public int getIndexAtPoint(Point point, int coordType) {
        if( null != accessibleText ) {
            NativeIntAccess nia = new NativeIntAccess();
            try {
                if( OLE.S_OK == accessibleText.get_offsetAtPoint(point.x, point.y, coordType, nia.getAddress()) ) {
                    return nia.getInt();
                }
            }
            catch (Exception e) {
            }
            finally {
                nia.dispose();
            }
        }
        return -1;
    }
    public Point getSelection(int selectionIndex) {
        if( null != accessibleText ) {
            NativeIntAccess nia = new NativeIntAccess(2);
            try {
                if( OLE.S_OK == accessibleText.get_selection(selectionIndex, nia.getAddress(0), nia.getAddress(1)) ) {
                    return new Point(nia.getInt(0),nia.getInt(1));
                }
            }
            catch (Exception e) {
            }
            finally {
                nia.dispose();
            }
        }
        return null;
    }
    public String getTextRange(int startOffset, int endOffset) {
        if( null != accessibleText ) {
            NativeStringAccess nsa = new NativeStringAccess();
            try {
                if( OLE.S_OK == accessibleText.get_text(startOffset, endOffset, nsa.getAddress()) ) {
                    return nsa.getString();
                }
            }
            catch (Exception e) {
            }
            finally {
                nsa.dispose();
            }
        }
        return null;
    }
    public TextSegment getTextBeforeIndex(int offset, int boundaryType) {
        if( null != accessibleText ) {
            NativeIntAccess nia = new NativeIntAccess(2);
            NativeStringAccess nsa = new NativeStringAccess();
            try {
                if( OLE.S_OK == accessibleText.get_textBeforeOffset(offset, boundaryType, nia.getAddress(0), nia.getAddress(1), nsa.getAddress()) ) {
                    return new TextSegment(nsa.getString(),nia.getInt(0),nia.getInt(1));
                }
            }
            catch (Exception e) {
            }
            finally {
                nsa.dispose();
                nia.dispose();
            }
        }
        return null;
    }
    public TextSegment getTextAfterIndex(int offset, int boundaryType) {
        if( null != accessibleText ) {
            NativeIntAccess nia = new NativeIntAccess(2);
            NativeStringAccess nsa = new NativeStringAccess();
            try {
                if( OLE.S_OK == accessibleText.get_textAfterOffset(offset, boundaryType, nia.getAddress(0), nia.getAddress(1), nsa.getAddress()) ) {
                    return new TextSegment(nsa.getString(),nia.getInt(0),nia.getInt(1));
                }
            }
            catch (Exception e) {
            }
            finally {
                nsa.dispose();
                nia.dispose();
            }
        }
        return null;
    }
    public TextSegment getTextAtIndex(int offset, int boundaryType) {
        if( null != accessibleText ) {
            NativeIntAccess nia = new NativeIntAccess(2);
            NativeStringAccess nsa = new NativeStringAccess();
            try {
                if( OLE.S_OK == accessibleText.get_textAtOffset(offset, boundaryType, nia.getAddress(0), nia.getAddress(1), nsa.getAddress()) ) {
                    return new TextSegment(nsa.getString(),nia.getInt(0),nia.getInt(1));
                }
            }
            catch (Exception e) {
            }
            finally {
                nsa.dispose();
                nia.dispose();
            }
        }
        return null;
    }
    public boolean removeSelection(int selectionIndex) {
        if( null != accessibleText ) {
            return OLE.S_OK == accessibleText.removeSelection(selectionIndex);
        }
        return false;
    }
    public boolean setCaretPosition(int offset) {
        if( null != accessibleText ) {
            return OLE.S_OK == accessibleText.setCaretOffset(offset);
        }
        return false;
    }
    public boolean setSelection(int selectionIndex, int startOffset, int endOffset) {
        if( null != accessibleText ) {
            return OLE.S_OK == accessibleText.setSelection(selectionIndex, startOffset, endOffset);
        }
        return false;
    }
    public int getCharacterCount() {
        if( null != accessibleText ) {
            NativeIntAccess nia = new NativeIntAccess();
            try {
                if( OLE.S_OK == accessibleText.get_nCharacters(nia.getAddress()) ) {
                    return nia.getInt();
                }
            }
            catch (Exception e) {
            }
            finally {
                nia.dispose();
            }
        }
        return 0;
    }
    public boolean scrollSubstringTo(int startIndex, int endIndex, int scrollType) {
        if( null != accessibleText ) {
            return OLE.S_OK == accessibleText.scrollSubstringTo(startIndex, endIndex, scrollType);
        }
        return false;
    }
    public boolean scrollSubstringToPoint(int startIndex, int endIndex, int coordinateType, int x, int y) {
        if( null != accessibleText ) {
            return OLE.S_OK == accessibleText.scrollSubstringToPoint(startIndex, endIndex, coordinateType, x, y);
        }
        return false;
    }
    public TextSegment getNewText() {
        if( null != accessibleText ) {
            NativeIntAccess nia = new NativeIntAccess(3);
            try {
                if( OLE.S_OK == accessibleText.get_newText(nia.getAddress()) ) {
                    return getTextSegment(nia);
                }
            }
            catch (Exception e) {
            }
            finally {
                nia.dispose();
            }
        }
        return null;
    }
    public TextSegment getOldText() {
        if( null != accessibleText ) {
            NativeIntAccess nia = new NativeIntAccess(3);
            try {
                if( OLE.S_OK == accessibleText.get_oldText(nia.getAddress()) ) {
                    return getTextSegment(nia);
                }
            }
            catch (Exception e) {
            }
            finally {
                nia.dispose();
            }
        }
        return null;
    }
    
    private static TextSegment getTextSegment(NativeIntAccess nia) {
        long[] hMem = new long[2];
        OS.MoveMemory(hMem, nia.getAddress(), OS.PTR_SIZEOF * hMem.length);
        if (0 != hMem[0]) {
            try {
                TextSegment textSegment = new TextSegment("",0,0); //$NON-NLS-1$
                int size = COM.SysStringByteLen(hMem[0]);
                if (size > 0) {
                    char[] buffer = new char[(size + 1) / 2];
                    MemoryUtil.MoveMemory(buffer, hMem[0], size);
                    textSegment.text = new String(buffer);
                }
                textSegment.start = (int)hMem[1];
                textSegment.end = (int)(hMem[1] >> 32);
                return textSegment;
            }
            finally {
                COM.SysFreeString(hMem[0]);
            }
        }
        return null;
    }
}
