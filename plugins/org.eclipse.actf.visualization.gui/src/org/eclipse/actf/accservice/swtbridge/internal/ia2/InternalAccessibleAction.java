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

import org.eclipse.actf.accservice.swtbridge.ia2.AccessibleAction;
import org.eclipse.actf.util.win32.NativeIntAccess;
import org.eclipse.actf.util.win32.NativeStringAccess;
import org.eclipse.swt.ole.win32.OLE;





public class InternalAccessibleAction implements AccessibleAction {

    private IAccessibleAction accessibleAction = null;
    
    public InternalAccessibleAction(long address) {
        accessibleAction = new IAccessibleAction(address);
        accessibleAction.AddRef();
    }
    public void dispose() {
        if( null != accessibleAction ) {
            accessibleAction.Release();
            accessibleAction = null;
        }
    }
    protected void finalize() throws Throwable {
        dispose();
        super.finalize();
    }
    
    public int getAccessibleActionCount() {
        if( null != accessibleAction ) {
            NativeIntAccess nia = new NativeIntAccess();
            try {
                if( OLE.S_OK == accessibleAction.nActions(nia.getAddress()) ) {
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
    public boolean doAccessibleAction(int actionIndex) {
        if( null != accessibleAction ) {
            return OLE.S_OK == accessibleAction.doAction(actionIndex);
        }
        return false;
    }
    public String getAccessibleActionDescription(int actionIndex) {
        if( null != accessibleAction ) {
            NativeStringAccess nsa = new NativeStringAccess();
            try {
                if( OLE.S_OK == accessibleAction.get_description(actionIndex, nsa.getAddress()) ) {
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
    public String[] getAccessibleActionKeyBinding(int actionIndex, int nMaxBinding) {
        if( null != accessibleAction && nMaxBinding > 0 ) {
            NativeIntAccess nia = new NativeIntAccess(2);
            try {
                if( OLE.S_OK == accessibleAction.get_keyBinding(actionIndex,nMaxBinding,nia.getAddress(0),nia.getAddress(1)) ) {
                    return InternalAccessible2.getStringArray(nia.getInt(0),nia.getInt(1),true);
                }
            }
            finally {
                nia.dispose();
            }
        }
        return null;
    }
    public String getAccessibleActionName(int actionIndex) {
        if( null != accessibleAction ) {
            NativeStringAccess nsa = new NativeStringAccess();
            try {
                if( OLE.S_OK == accessibleAction.get_name(actionIndex, nsa.getAddress()) ) {
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
    public String getLocalizedAccessibleActionName(int actionIndex) {
        if( null != accessibleAction ) {
            NativeStringAccess nsa = new NativeStringAccess();
            try {
                if( OLE.S_OK == accessibleAction.get_localizedName(actionIndex, nsa.getAddress()) ) {
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
}
