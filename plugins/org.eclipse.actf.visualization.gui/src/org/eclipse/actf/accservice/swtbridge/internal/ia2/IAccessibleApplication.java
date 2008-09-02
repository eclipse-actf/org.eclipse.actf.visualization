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



public class IAccessibleApplication extends IUnknown {
    public static final GUID IID = COMUtil.IIDFromString("{D49DED83-5B25-43F4-9B95-93B44595979E}"); //$NON-NLS-1$
    
    int address;
    public IAccessibleApplication(int address) {
        super(address);
        this.address = address;
    }
    
    public int get_appName(int pszName) {
        return COMUtil.VtblCall(3, address, pszName);
    }
    public int get_appVersion(int pszVersion) {
        return COMUtil.VtblCall(4, address, pszVersion);
    }
    public int get_toolkitName(int pszName) {
        return COMUtil.VtblCall(5, address, pszName);
    }
    public int get_toolkitVersion(int pszVersion) {
        return COMUtil.VtblCall(6, address, pszVersion);
    }
}
