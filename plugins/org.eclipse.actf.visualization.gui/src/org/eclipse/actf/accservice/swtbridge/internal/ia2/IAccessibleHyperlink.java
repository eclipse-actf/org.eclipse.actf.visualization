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



public class IAccessibleHyperlink extends IAccessibleAction {
    public static final GUID IID = COMUtil.IIDFromString("{01C20F2B-3DD2-400f-949F-AD00BDAB1D41}"); //$NON-NLS-1$
    
    long address;
    public IAccessibleHyperlink(long address) {
        super(address);
        this.address = address;
    }

    public int get_anchor(int index, long pvarAnchor) {
        return COM.VtblCall(9, address, index, pvarAnchor); 
    }
    public int get_anchorTarget(int index, long pvarAnchorTarget) {
        return COM.VtblCall(10, address, index, pvarAnchorTarget);
    }
    public int get_startIndex(long pIndex) {
        return COM.VtblCall(11, address, pIndex); 
    }
    public int get_endIndex(long pIndex) {
        return COM.VtblCall(12, address, pIndex); 
    }
    public int get_valid(long pValid) {
        return COM.VtblCall(13, address, pValid); 
    }
}
