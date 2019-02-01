/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Takashi ITOH - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.visualization.gui.internal.util;

import org.eclipse.actf.accservice.swtbridge.AccessibleObject;
import org.eclipse.actf.util.win32.WindowUtil;

public class WebBrowserUtil {
	
    private static final String[] BROWSER_CLASSNAMES = new String[] { 
        "Internet Explorer_Server",  //$NON-NLS-1$
        "MozillaContentWindowClass"  //$NON-NLS-1$
    };
    
    public static boolean isBrowserFrame(long hwndFrame) {
        String className = WindowUtil.GetWindowClassName(hwndFrame);
        if( "MozillaUIWindowClass".equals(className) ||  //$NON-NLS-1$
            "IEFrame".equals(className) ) { //$NON-NLS-1$
            return true;
        }
        return false;
    }
    
	public static boolean isBrowser(AccessibleObject accObject) {
		return isBrowserClass(accObject.getClassName());
	}
	
    public static boolean isBrowserClass(String className) {
        for( int i=0; i<BROWSER_CLASSNAMES.length; i++ ) {
            if( BROWSER_CLASSNAMES[i].equals(className) ) {
                return true;
            }
        }
        return false;
    }

}
