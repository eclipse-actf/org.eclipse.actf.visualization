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
package org.eclipse.actf.accservice.swtbridge;

import org.eclipse.actf.accservice.swtbridge.event.IAccessibleEventMonitor;
import org.eclipse.swt.graphics.Point;


public interface IAccessibleObjectFactory {
	
    public AccessibleObject getAccessibleObjectFromPoint(Point point);

    public AccessibleObject getAccessibleObjectFromWindow(long hwnd);

    public AccessibleObject getAccessibleObjectFromEvent(long hwnd, int dwId, int dwChildId);

    public IAccessibleEventMonitor getAccessibleEventMonitor();
}
