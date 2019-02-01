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

import org.eclipse.actf.accservice.swtbridge.ia2.AccessibleImage;
import org.eclipse.actf.util.win32.NativeIntAccess;
import org.eclipse.actf.util.win32.NativeStringAccess;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.ole.win32.OLE;




public class InternalAccessibleImage implements AccessibleImage {

    private IAccessibleImage accessibleImage = null;
    
    public InternalAccessibleImage(long address) {
        accessibleImage = new IAccessibleImage(address);
        accessibleImage.AddRef();
    }
    public void dispose() {
        if( null != accessibleImage ) {
            accessibleImage.Release();
            accessibleImage = null;
        }
    }
    protected void finalize() throws Throwable {
        dispose();
        super.finalize();
    }
    
    public String getAccessibleImageDescription() {
        if( null != accessibleImage ) {
            NativeStringAccess nsa = new NativeStringAccess();
            try {
                if( OLE.S_OK == accessibleImage.get_description(nsa.getAddress()) ) {
                    return nsa.getString();
                }
            }
            catch (Exception e) {
            }
            finally {
                nsa.dispose();
            }
        }
        return null;
    }
    public Point getAccessibleImagePosition(int coordinateType) {
        if( null != accessibleImage ) {
            NativeIntAccess nia = new NativeIntAccess();
            try {
                if( OLE.S_OK == accessibleImage.get_imagePosition(coordinateType, nia.getAddress(0), nia.getAddress(1)) ) {
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
    public Point getAccessibleImageSize() {
        if( null != accessibleImage ) {
            NativeIntAccess nia = new NativeIntAccess();
            try {
                if( OLE.S_OK == accessibleImage.get_imageSize(nia.getAddress(0), nia.getAddress(1)) ) {
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

}
