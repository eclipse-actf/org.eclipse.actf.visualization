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

package org.eclipse.actf.visualization.gui.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.actf.model.ui.IModelService;
import org.eclipse.actf.util.win32.OverlayWindow;
import org.eclipse.actf.util.win32.WindowUtil;
import org.eclipse.actf.visualization.gui.TargetWindowDataProvider;
import org.eclipse.actf.visualization.gui.internal.util.WebBrowserUtil;



public class GuiTargetWindowDataProvider extends TargetWindowDataProvider {

    public IModelService[] getModelService() {
        List<GuiTargetWindowData> dsList = new ArrayList<GuiTargetWindowData>(); 
        long hwnd = WindowUtil.GetChildWindow (WindowUtil.GetDesktopWindow());
        while (hwnd != 0) {
            if( WindowUtil.IsWindowVisible(hwnd) && 
                !OverlayWindow.WINDOW_TEXT.equals(WindowUtil.GetWindowText(hwnd)) ) {
            	boolean isBrowser = WebBrowserUtil.isBrowserFrame(hwnd);
                dsList.add(new GuiTargetWindowData(hwnd,isBrowser));
//            	if( isBrowser ) {
//                    dsList.add(new MSAATargetWindowData(hwnd,false));
//            	}
            }
            hwnd = WindowUtil.GetNextWindow (hwnd);
        }
        return dsList.toArray(new IModelService[dsList.size()]);
    }

}
