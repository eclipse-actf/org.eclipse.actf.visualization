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

import org.eclipse.actf.accservice.swtbridge.ia2.AccessibleApplication;
import org.eclipse.actf.util.win32.NativeStringAccess;
import org.eclipse.swt.ole.win32.OLE;




public class InternalAccessibleApplication implements AccessibleApplication {

    private IAccessibleApplication accessibleApplication = null;
    
    public InternalAccessibleApplication(long address) {
        accessibleApplication = new IAccessibleApplication(address);
        accessibleApplication.AddRef();
    }
    public void dispose() {
        if( null != accessibleApplication ) {
            accessibleApplication.Release();
            accessibleApplication = null;
        }
    }
    protected void finalize() throws Throwable {
        dispose();
        super.finalize();
    }
    
    public String getApplicationName() {
        if( null != accessibleApplication ) {
            NativeStringAccess nsa = new NativeStringAccess();
            try {
                if( OLE.S_OK == accessibleApplication.get_appName(nsa.getAddress()) ) {
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
    public String getApplicationVersion() {
        if( null != accessibleApplication ) {
            NativeStringAccess nsa = new NativeStringAccess();
            try {
                if( OLE.S_OK == accessibleApplication.get_appVersion(nsa.getAddress()) ) {
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
    public String getToolkitName() {
        if( null != accessibleApplication ) {
            NativeStringAccess nsa = new NativeStringAccess();
            try {
                if( OLE.S_OK == accessibleApplication.get_toolkitName(nsa.getAddress()) ) {
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
    public String getToolkitVersion() {
        if( null != accessibleApplication ) {
            NativeStringAccess nsa = new NativeStringAccess();
            try {
                if( OLE.S_OK == accessibleApplication.get_toolkitVersion(nsa.getAddress()) ) {
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
