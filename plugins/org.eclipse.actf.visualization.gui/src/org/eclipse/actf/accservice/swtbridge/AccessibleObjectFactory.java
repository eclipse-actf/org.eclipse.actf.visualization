/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Takashi ITOH - initial API and implementation
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.accservice.swtbridge;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.actf.accservice.swtbridge.event.IAccessibleEventMonitor;
import org.eclipse.actf.accservice.swtbridge.internal.InternalAccessibleObject;
import org.eclipse.actf.accservice.swtbridge.internal.InternalAccessibleObjectFactory;
import org.eclipse.actf.visualization.gui.internal.util.Messages;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.ole.win32.OLE;
import org.eclipse.swt.ole.win32.Variant;

public class AccessibleObjectFactory {

	public static String currentFactoryId;
	private static IAccessibleObjectFactory currentFactory;
	private static Map<String, AccessibleFactoryEntry> factoryEntries = new HashMap<String, AccessibleFactoryEntry>();
	static {
		addFactoryEntry(InternalAccessibleObjectFactory.ID,
				new AccessibleFactoryEntry(Messages.AccessibleObjectFactory_0,
						new InternalAccessibleObjectFactory()));
		setActiveFactory(InternalAccessibleObjectFactory.ID);
	}

	public static AccessibleObject getAccessibleObjectFromPoint(Point point) {
		if (null != currentFactory) {
			return currentFactory.getAccessibleObjectFromPoint(point);
		}
		return null;
	}

	public static AccessibleObject getAccessibleObjectFromWindow(long hwnd) {
		if (null != currentFactory) {
			return currentFactory.getAccessibleObjectFromWindow(hwnd);
		}
		return null;
	}

	public static AccessibleObject getAccessibleObjectFromEvent(long hwnd,
			int dwId, int dwChildId) {
		if (null != currentFactory) {
			return currentFactory.getAccessibleObjectFromEvent(hwnd, dwId,
					dwChildId);
		}
		return null;
	}

	public static IAccessibleEventMonitor getAccessibleEventMonitor() {
		if (null != currentFactory) {
			return currentFactory.getAccessibleEventMonitor();
		}
		return null;
	}

	/*
	 * Returns Accessible Object from Variant
	 */
	public static AccessibleObject getAccessibleObjectFromVariant(
			Variant varDispatch) {
		if ((null != varDispatch) && (OLE.VT_DISPATCH == varDispatch.getType())) {
			return new InternalAccessibleObject(null, varDispatch);
		}
		return null;
	}

	/*
	 * Controls
	 */

	public static boolean setActiveFactory(String id) {
		if (id.equals(currentFactoryId)) {
			return false;
		}
		AccessibleFactoryEntry entry = factoryEntries
				.get(id);
		if (null != entry) {
			currentFactoryId = id;
			currentFactory = entry.accessibleObjectFactory;
			return true;
		}
		return false;
	}

	public static AccessibleFactoryEntry getFactoryEntry(String id) {
		return factoryEntries.get(id);
	}

	public static String[] getFactoryIds() {
		return factoryEntries.keySet().toArray(
				new String[factoryEntries.size()]);
	}

	public static void addFactoryEntry(String id, AccessibleFactoryEntry factory) {
		factoryEntries.put(id, factory);
	}

}
