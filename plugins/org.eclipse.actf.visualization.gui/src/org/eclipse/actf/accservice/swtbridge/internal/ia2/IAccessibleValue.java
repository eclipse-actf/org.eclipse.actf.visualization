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



public class IAccessibleValue extends IUnknown {
    public static final GUID IID = COMUtil.IIDFromString("{35855B5B-C566-4fd0-A7B1-E65465600394}"); //$NON-NLS-1$
    
    long address;
    public IAccessibleValue(long address) {
        super(address);
        this.address = address;
    }

    public int get_currentValue(long pvarCurrentValue) {
        return COM.VtblCall(3, address, pvarCurrentValue);
    }
    public int setCurrentValue(long varValue) {
        return COM.VtblCall(4, address, varValue);
    }
    public int get_maximumValue(long pvarMaximumValue) {
        return COM.VtblCall(5, address, pvarMaximumValue);
    }
    public int get_minimumValue(long pvarMinimumValue) {
        return COM.VtblCall(6, address, pvarMinimumValue);
    }
}
