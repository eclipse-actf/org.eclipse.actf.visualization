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

import org.eclipse.actf.accservice.swtbridge.ia2.AccessibleComponent;
import org.eclipse.actf.util.win32.NativeIntAccess;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.ole.win32.OLE;




public class InternalAccessibleComponent implements AccessibleComponent {

    private IAccessibleComponent accessibleComponent = null;
    
    public InternalAccessibleComponent(long address) {
        accessibleComponent = new IAccessibleComponent(address);
        accessibleComponent.AddRef();
    }
    public void dispose() {
        if( null != accessibleComponent ) {
            accessibleComponent.Release();
            accessibleComponent = null;
        }
    }
    protected void finalize() throws Throwable {
        dispose();
        super.finalize();
    }
    
    public Point getLocation() {
        if( null != accessibleComponent ) {
            NativeIntAccess nia = new NativeIntAccess(2);
            try {
                if( OLE.S_OK == accessibleComponent.get_locationInParent(nia.getAddress(0),nia.getAddress(1)) ) {
                    return new Point(nia.getInt(0),nia.getInt(1));
                }
            }
            catch (Exception e) {
            }
            finally {
                nia.dispose();
            }
        }
        return null;
    }
    public int getForeground() {
        if( null != accessibleComponent ) {
            NativeIntAccess nia = new NativeIntAccess();
            try {
                if( OLE.S_OK == accessibleComponent.get_foreground(nia.getAddress()) ) {
                    return nia.getInt();
                }
            }
            catch (Exception e) {
            }
            finally {
                nia.dispose();
            }
        }
        return 0;
    }
    public int getBackground() {
        if( null != accessibleComponent ) {
            NativeIntAccess nia = new NativeIntAccess();
            try {
                if( OLE.S_OK == accessibleComponent.get_background(nia.getAddress()) ) {
                    return nia.getInt();
                }
            }
            catch (Exception e) {
            }
            finally {
                nia.dispose();
            }
        }
        return 0;
    }

}
