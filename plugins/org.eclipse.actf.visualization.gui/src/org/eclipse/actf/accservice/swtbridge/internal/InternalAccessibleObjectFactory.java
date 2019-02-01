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

package org.eclipse.actf.accservice.swtbridge.internal;

import org.eclipse.actf.accservice.swtbridge.AccessibleObject;
import org.eclipse.actf.accservice.swtbridge.IAccessibleObjectFactory;
import org.eclipse.actf.accservice.swtbridge.event.IAccessibleEventMonitor;
import org.eclipse.swt.graphics.Point;




public class InternalAccessibleObjectFactory implements IAccessibleObjectFactory {
    
    public static final String ID = InternalAccessibleObjectFactory.class.getName();

    private static IAccessibleEventMonitor monitor = null; 
    
    public IAccessibleEventMonitor getAccessibleEventMonitor() {
        if( null == monitor ) {
            monitor = new InternalEventMonitor(); 
        }
        return monitor;
    }

    public AccessibleObject getAccessibleObjectFromEvent(long hwnd, int dwId, int dwChildId) {
        return InternalAccessibleObject.getAccessibleObjectFromEvent(hwnd, dwId, dwChildId);
    }

    public AccessibleObject getAccessibleObjectFromPoint(Point point) {
        return InternalAccessibleObject.getAccessibleObjectFromPoint(point);
    }

    public AccessibleObject getAccessibleObjectFromWindow(long hwnd) {
        return InternalAccessibleObject.getAccessibleObjectFromWindow(hwnd);
    }

}
