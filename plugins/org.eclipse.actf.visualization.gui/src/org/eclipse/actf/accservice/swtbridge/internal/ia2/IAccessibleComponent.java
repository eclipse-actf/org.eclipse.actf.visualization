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



public class IAccessibleComponent extends IUnknown {
    public static final GUID IID = COMUtil.IIDFromString("{1546D4B0-4C98-4bda-89AE-9A64748BDDE4}"); //$NON-NLS-1$
    
    long address;
    public IAccessibleComponent(long address) {
        super(address);
        this.address = address;
    }
    
    public int get_locationInParent(long pX, long pY) {
        return COM.VtblCall(3, address, pX, pY);
    }
    public int get_foreground(long pForeground) {
        return COM.VtblCall(4, address, pForeground);
    }
    public int get_background(long pBackground) {
        return COM.VtblCall(5, address, pBackground);
    }
}
