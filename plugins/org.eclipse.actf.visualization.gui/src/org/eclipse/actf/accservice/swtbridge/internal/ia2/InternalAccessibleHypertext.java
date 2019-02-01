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

import org.eclipse.actf.accservice.swtbridge.ia2.AccessibleHypertext;
import org.eclipse.actf.util.win32.NativeIntAccess;
import org.eclipse.swt.ole.win32.OLE;




public class InternalAccessibleHypertext implements AccessibleHypertext {

    private IAccessibleHypertext accessibleHypertext = null;
    
    public InternalAccessibleHypertext(long address) {
        accessibleHypertext = new IAccessibleHypertext(address);
        accessibleHypertext.AddRef();
    }
    public void dispose() {
        if( null != accessibleHypertext ) {
            accessibleHypertext.Release();
            accessibleHypertext = null;
        }
    }
    protected void finalize() throws Throwable {
        dispose();
        super.finalize();
    }
    
    public int getHyperLinkCount() {
        if( null != accessibleHypertext ) {
            NativeIntAccess nia = new NativeIntAccess();
            try {
                if( OLE.S_OK == accessibleHypertext.get_nHyperlinks(nia.getAddress()) ) {
                    return nia.getInt();
                }
            }
            finally {
                nia.dispose();
            }
        }
        return 0;
    }
    public InternalAccessibleHyperlink getHyperLink(int index) {
        if( null != accessibleHypertext ) {
            NativeIntAccess nia = new NativeIntAccess();
            try {
                if( OLE.S_OK == accessibleHypertext.get_hyperlink(index,nia.getAddress()) ) {
                    return new InternalAccessibleHyperlink(nia.getInt());
                }
            }
            finally {
                nia.dispose();
            }
        }
        return null;
    }
    public int getHyperLinkIndex(int charIndex) {
        if( null != accessibleHypertext ) {
            NativeIntAccess nia = new NativeIntAccess();
            try {
                if( OLE.S_OK == accessibleHypertext.get_hyperlinkIndex(charIndex, nia.getAddress()) ) {
                    return nia.getInt();
                }
            }
            finally {
                nia.dispose();
            }
        }
        return -1;
    }
}
