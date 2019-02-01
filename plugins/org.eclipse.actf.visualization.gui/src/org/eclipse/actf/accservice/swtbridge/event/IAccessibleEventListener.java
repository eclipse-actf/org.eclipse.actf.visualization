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
package org.eclipse.actf.accservice.swtbridge.event;

import org.eclipse.actf.accservice.swtbridge.AccessibleObject;

public interface IAccessibleEventListener {

	public void handleEvent(int event, long hwnd, int idObject, int idChild, AccessibleObject accObject);
	
}
