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

import org.eclipse.actf.accservice.swtbridge.ia2.AccessibleHyperlink;
import org.eclipse.actf.util.win32.NativeIntAccess;
import org.eclipse.actf.util.win32.NativeVariantAccess;
import org.eclipse.swt.ole.win32.OLE;
import org.eclipse.swt.ole.win32.Variant;




public class InternalAccessibleHyperlink implements AccessibleHyperlink {

    private IAccessibleHyperlink accessibleHyperlink = null;
    
    public InternalAccessibleHyperlink(long address) {
        accessibleHyperlink = new IAccessibleHyperlink(address);
        accessibleHyperlink.AddRef();
    }
    public void dispose() {
        if( null != accessibleHyperlink ) {
            accessibleHyperlink.Release();
            accessibleHyperlink = null;
        }
    }
    protected void finalize() throws Throwable {
        dispose();
        super.finalize();
    }

    public Variant getAccessibleActionAnchor(int index) {
        if( null != accessibleHyperlink ) {
            NativeVariantAccess nva = new NativeVariantAccess();
            try {
                if( OLE.S_OK == accessibleHyperlink.get_anchor(index, nva.getAddress()) ) {
                    return nva.getVariant();
                }
            }
            finally {
                nva.dispose();
            }
        }
        return null; 
    }
    public Variant getAccessibleActionObject(int index) {
        if( null != accessibleHyperlink ) {
            NativeVariantAccess nva = new NativeVariantAccess();
            try {
                if( OLE.S_OK == accessibleHyperlink.get_anchorTarget(index, nva.getAddress()) ) {
                    return nva.getVariant();
                }
            }
            finally {
                nva.dispose();
            }
        }
        return null;
    }
    public int getStartIndex() {
        if( null != accessibleHyperlink ) {
            NativeIntAccess nia = new NativeIntAccess();
            try {
                if( OLE.S_OK == accessibleHyperlink.get_startIndex(nia.getAddress()) ) {
                    return nia.getInt();
                }
            }
            finally {
                nia.dispose();
            }
        }
        return -1; 
    }
    public int getEndIndex() {
        if( null != accessibleHyperlink ) {
            NativeIntAccess nia = new NativeIntAccess();
            try {
                if( OLE.S_OK == accessibleHyperlink.get_endIndex(nia.getAddress()) ) {
                    return nia.getInt();
                }
            }
            finally {
                nia.dispose();
            }
        }
        return -1; 
    }
    public boolean isValid() {
        if( null != accessibleHyperlink ) {
            NativeIntAccess nia = new NativeIntAccess();
            try {
                if( OLE.S_OK == accessibleHyperlink.get_valid(nia.getAddress()) ) {
                    return 0 != nia.getInt();
                }
            }
            finally {
                nia.dispose();
            }
        }
        return false; 
    }
}
