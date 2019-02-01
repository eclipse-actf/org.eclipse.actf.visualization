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



public class IAccessibleRelation extends IUnknown {
    public static final GUID IID = COMUtil.IIDFromString("{7CDF86EE-C3DA-496a-BDA4-281B336E1FDC}"); //$NON-NLS-1$
    
    long address;
    public IAccessibleRelation(long address) {
        super(address);
        this.address = address;
    }

    public int get_relationType(long pszRelationType) {
        return COM.VtblCall(3, address, pszRelationType);
    }
    public int get_localizedRelationType(long pszLocalizedRelationType) {
        return COM.VtblCall(4, address, pszLocalizedRelationType);
    }
    public int get_nTargets(long pnTargets) {
        return COM.VtblCall(5, address, pnTargets);
    }
    public int get_target(int targetIndex, long punkTarget) {
        return COM.VtblCall(6, address, targetIndex, punkTarget);
    }
    public int get_targets(int maxTargets, long punkTarget, long pnTargets) {
        return COM.VtblCall(7, address, maxTargets, punkTarget, pnTargets);
    }
}
