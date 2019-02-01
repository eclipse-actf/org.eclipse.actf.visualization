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

import org.eclipse.actf.accservice.swtbridge.ia2.AccessibleEditableText;
import org.eclipse.actf.util.win32.NativeStringAccess;
import org.eclipse.swt.ole.win32.OLE;




public class InternalAccessibleEditableText implements AccessibleEditableText {

    private IAccessibleEditableText accessibleEditableText = null;
    
    public InternalAccessibleEditableText(long address) {
        accessibleEditableText = new IAccessibleEditableText(address);
        accessibleEditableText.AddRef();
    }
    public void dispose() {
        if( null != accessibleEditableText ) {
            accessibleEditableText.Release();
            accessibleEditableText = null;
        }
    }
    protected void finalize() throws Throwable {
        dispose();
        super.finalize();
    }
    
    public boolean copyText(int startOffset, int endOffset) {
        if( null != accessibleEditableText ) {
            return OLE.S_OK == accessibleEditableText.copyText(startOffset, endOffset);

        }
        return false;
    }
    public boolean deleteText(int startOffset, int endOffset) {
        if( null != accessibleEditableText ) {
            return OLE.S_OK == accessibleEditableText.deleteText(startOffset, endOffset);
        }
        return false;
    }
    public boolean insertText(int offset, String strText) {
        if( null != accessibleEditableText ) {
            NativeStringAccess nsa = new NativeStringAccess();
            try {
                nsa.setString(strText);
                return OLE.S_OK == accessibleEditableText.insertText(offset,nsa.getAddress());
            }
            finally {
                nsa.dispose();
            }
        }
        return false;
    }
    public boolean cutText(int startOffset, int endOffset) {
        if( null != accessibleEditableText ) {
            return OLE.S_OK == accessibleEditableText.cutText(startOffset, endOffset);
        }
        return false;
    }
    public boolean pasteText(int offset) {
        if( null != accessibleEditableText ) {
            return OLE.S_OK == accessibleEditableText.pasteText(offset);
        }
        return false;
    }
    public boolean replaceText(int startOffset, int endOffset, String strText) {
        if( null != accessibleEditableText ) {
            NativeStringAccess nsa = new NativeStringAccess();
            try {
                nsa.setString(strText);
                return OLE.S_OK == accessibleEditableText.replaceText(startOffset, endOffset, nsa.getAddress());
            }
            finally {
                nsa.dispose();
            }
        }
        return false;
    }
    public boolean setAttributes(int startOffset, int endOffset, String strAttributes) {
        if( null != accessibleEditableText ) {
            NativeStringAccess nsa = new NativeStringAccess();
            try {
                nsa.setString(strAttributes);
                return OLE.S_OK == accessibleEditableText.setAttributes(startOffset, endOffset, nsa.getAddress());
            }
            finally {
                nsa.dispose();
            }
        }
        return false;
    }
}
