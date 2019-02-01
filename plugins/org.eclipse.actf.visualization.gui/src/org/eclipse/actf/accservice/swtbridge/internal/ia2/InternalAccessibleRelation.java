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

import org.eclipse.actf.accservice.swtbridge.AccessibleObject;
import org.eclipse.actf.accservice.swtbridge.ia2.AccessibleRelation;
import org.eclipse.actf.accservice.swtbridge.internal.InternalAccessibleObject;
import org.eclipse.actf.util.win32.NativeIntAccess;
import org.eclipse.actf.util.win32.NativeStringAccess;
import org.eclipse.swt.ole.win32.OLE;




public class InternalAccessibleRelation implements AccessibleRelation {

    private IAccessibleRelation accessibleRelation = null;
    
    public InternalAccessibleRelation(long address) {
        accessibleRelation = new IAccessibleRelation(address);
        accessibleRelation.AddRef();
    }
    public void dispose() {
        if( null != accessibleRelation ) {
            accessibleRelation.Release();
            accessibleRelation = null;
        }
    }
    protected void finalize() throws Throwable {
        dispose();
        super.finalize();
    }
    
    public String getRelationType() {
        NativeStringAccess nsa = new NativeStringAccess();
        try {
            if( OLE.S_OK == accessibleRelation.get_relationType(nsa.getAddress()) ) {
                return nsa.getString();
            }
        }
        catch (Exception e) {
        }
        finally {
            nsa.dispose();
        }
        return null;
    }
    public String getLocalizedRelationType() {
        NativeStringAccess nsa = new NativeStringAccess();
        try {
            if( OLE.S_OK == accessibleRelation.get_localizedRelationType(nsa.getAddress()) ) {
                return nsa.getString();
            }
        }
        catch (Exception e) {
        }
        finally {
            nsa.dispose();
        }
        return null;
    }
    public int getTargetCount() {
        NativeIntAccess nia = new NativeIntAccess();
        try {
            if( OLE.S_OK == accessibleRelation.get_nTargets(nia.getAddress()) ) {
                return nia.getInt();
            }
        }
        catch (Exception e) {
        }
        finally {
            nia.dispose();
        }
        return 0;
    }
    public AccessibleObject getTarget(int targetIndex) {
        NativeIntAccess nia = new NativeIntAccess();
        try {
            if( OLE.S_OK == accessibleRelation.get_target(targetIndex, nia.getAddress()) ) {
                return InternalAccessibleObject.newInstance(nia.getInt());
            }
        }
        catch (Exception e) {
        }
        finally {
            nia.dispose();
        }
        return null;
    }
    public AccessibleObject[] getTargets(int maxTargets) {
        if( maxTargets > 0 ) {
            NativeIntAccess nia = new NativeIntAccess(maxTargets+1);
            try {
                if( OLE.S_OK == accessibleRelation.get_targets(maxTargets, nia.getAddress(1), nia.getAddress(0)) ) {
                    int count = nia.getInt();
                    AccessibleObject[] retUnknowns = new AccessibleObject[count];
                    for( int i=0; i<count; i++ ) {
                        retUnknowns[i] = InternalAccessibleObject.newInstance(nia.getInt(i+1)); 
                    }
                    return retUnknowns;
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
