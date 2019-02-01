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

import org.eclipse.actf.accservice.swtbridge.ia2.AccessibleValue;
import org.eclipse.actf.util.win32.NativeIntAccess;
import org.eclipse.actf.util.win32.NativeVariantAccess;
import org.eclipse.swt.ole.win32.OLE;




public class InternalAccessibleValue implements AccessibleValue {

    private IAccessibleValue accessibleValue = null;
    
    public InternalAccessibleValue(long address) {
        accessibleValue = new IAccessibleValue(address);
        accessibleValue.AddRef();
    }
    public void dispose() {
        if( null != accessibleValue ) {
            accessibleValue.Release();
            accessibleValue = null;
        }
    }
    protected void finalize() throws Throwable {
        dispose();
        super.finalize();
    }

    public Object getCurrentValue() {
        if( null != accessibleValue ) {
           NativeVariantAccess nva = new NativeVariantAccess();
           try {
               if( OLE.S_OK == accessibleValue.get_currentValue(nva.getAddress()) ) {
                   return nva.getVariant();
               }
           }
           catch( Exception e) {
           }
           finally {
               nva.dispose();
           }
        }
        return null;
    }
    public boolean setCurrentValue(Object objValue) {
        if( null != accessibleValue ) {
        	if( objValue instanceof Number ) {
                NativeIntAccess nia = new NativeIntAccess(4);
                try {
                    nia.setInt(0,OLE.VT_I4);
                    nia.setInt(1,0);
                    nia.setInt(2,((Number)objValue).intValue());
                    nia.setInt(3,0);
                    return OLE.S_OK == accessibleValue.setCurrentValue(nia.getAddress());
                }
                catch( Exception e) {
                }
                finally {
                    nia.dispose();
                }
        	}
        }
        return false;
    }
    public Object getMaximumValue() {
        if( null != accessibleValue ) {
            NativeVariantAccess nva = new NativeVariantAccess();
            try {
                if( OLE.S_OK == accessibleValue.get_maximumValue(nva.getAddress()) ) {
                    return nva.getVariant();
                }
            }
            catch( Exception e) {
            }
            finally {
                nva.dispose();
            }
        }
        return null;
    }
    public Object getMinimumValue() {
        if( null != accessibleValue ) {
            NativeVariantAccess nva = new NativeVariantAccess();
            try {
                if( OLE.S_OK == accessibleValue.get_minimumValue(nva.getAddress()) ) {
                    return nva.getVariant();
                }
            }
            catch( Exception e) {
            }
            finally {
                nva.dispose();
            }
        }
        return null;
    }
}
