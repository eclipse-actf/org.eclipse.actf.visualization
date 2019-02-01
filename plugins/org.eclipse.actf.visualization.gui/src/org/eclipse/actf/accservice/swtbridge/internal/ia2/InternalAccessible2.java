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

import java.util.Locale;

import org.eclipse.actf.accservice.swtbridge.ia2.Accessible2;
import org.eclipse.actf.accservice.swtbridge.ia2.AccessibleRelation;
import org.eclipse.actf.util.win32.MemoryUtil;
import org.eclipse.actf.util.win32.NativeIntAccess;
import org.eclipse.actf.util.win32.NativeStringAccess;
import org.eclipse.swt.internal.ole.win32.COM;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.ole.win32.OLE;




public class InternalAccessible2 implements Accessible2 {

    private IAccessible2 ia2 = null;
    
    public InternalAccessible2(IAccessible2 accessible2) {
        this.ia2 = accessible2;
    }
    public void dispose() {
        ia2 = null;
    }
    protected void finalize() throws Throwable {
        dispose();
        super.finalize();
    }
    
    public int getAccessibleRelationCount() {
        if( null != ia2 ) {
            NativeIntAccess nia = new NativeIntAccess();
            try {
                if( OLE.S_OK == ia2.get_nRelations(nia.getAddress()) ) {
                    return nia.getInt();
                }
            }
            finally {
                nia.dispose();
            }
        }
        return 0;
    }
    
    public AccessibleRelation getAccessibleRelation(int relationIndex){
        if( null != ia2 ) {
            NativeIntAccess nia = new NativeIntAccess();
            try {
                if( OLE.S_OK == ia2.get_relation(relationIndex, nia.getAddress()) ) {
                    return new InternalAccessibleRelation(nia.getInt());
                }
            }
            finally {
                nia.dispose();
            }
        }
        return null;
    }
    
    public AccessibleRelation[] getAccessibleRelations(int maxRelations) {
        if( null != ia2 && maxRelations > 0 ) {
            NativeIntAccess nia = new NativeIntAccess(maxRelations+1);
            try {
                if( OLE.S_OK == ia2.get_relations(maxRelations, nia.getAddress(1), nia.getAddress(0)) ) {
                    int count = nia.getInt(0);
                    AccessibleRelation[] retRelations = new AccessibleRelation[count];
                    for( int i=0; i<count; i++ ) {
                        retRelations[i] = new InternalAccessibleRelation(nia.getInt(i+1));
                    }
                    return retRelations;
                }
            }
            finally {
                nia.dispose();
            }
        }
        return null;
    }
    
    public int getAccessibleRole() {
        if( null != ia2 ) {
            NativeIntAccess nia = new NativeIntAccess();
            try {
                if( OLE.S_OK == ia2.get_role(nia.getAddress()) ) {
                    return nia.getInt();
                }
            }
            finally {
                nia.dispose();
            }
        }
        return 0;
    }
    
    public boolean scrollTo(int scrollType) {
        if( null != ia2 ) {
            ia2.scrollTo(scrollType);
        }
        return false;
    }
        
    public boolean scrollToPoint(int coordinateType, int x, int y) {
        if( null != ia2 ) {
            ia2.scrollToPoint(coordinateType, x, y);
        }
        return false;
    }

    public int[] getGroupPosition() {
        if( null != ia2 ) {
            NativeIntAccess nia = new NativeIntAccess(3);
            try {
                if( OLE.S_OK == ia2.get_groupPosition(nia.getAddress(0),nia.getAddress(1),nia.getAddress(2)) ) {
                    return new int[] {nia.getInt(0),nia.getInt(1),nia.getInt(2)};
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
    
    public int getStates() {
        if( null != ia2 ) {
            NativeIntAccess nia = new NativeIntAccess();
            try {
                if( OLE.S_OK == ia2.get_states(nia.getAddress()) ) {
                    return nia.getInt();
                }
            }
            finally {
                nia.dispose();
            }
        }
        return 0;
    }
    
    public String getExtendedRole() {
        if( null != ia2 ) {
            NativeStringAccess nsa = new NativeStringAccess();
            try {
                if( OLE.S_OK == ia2.get_extendedRole(nsa.getAddress()) ) {
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
    
    public String getLocalizedExtendedRole() {
        if( null != ia2 ) {
            NativeStringAccess nsa = new NativeStringAccess();
            try {
                if( OLE.S_OK == ia2.get_localizedExtendedRole(nsa.getAddress()) ) {
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
    
    public int getExtendedStateCount() {
        if( null != ia2 ) {
            NativeIntAccess nia = new NativeIntAccess();
            try {
                if( OLE.S_OK == ia2.get_nExtendedStates(nia.getAddress()) ) {
                    return nia.getInt();
                }
            }
            finally {
                nia.dispose();
            }
        }
        return 0;
    }
    
    public String[] getExtendedStates(int maxExtendedStates) {
        if( null != ia2 && maxExtendedStates > 0 ) {
            NativeIntAccess nia = new NativeIntAccess(2);
            try {
                if( OLE.S_OK == ia2.get_extendedStates(maxExtendedStates,nia.getAddress(0),nia.getAddress(1)) ) {
                    return getStringArray(nia.getInt(0),nia.getInt(1),true);
                }
            }
            finally {
                nia.dispose();
            }
        }
        return null;
    }
    
    public String[] getLocalizedExtendedStates(int maxLocalizedExtendedStates) {
        if( null != ia2 && maxLocalizedExtendedStates > 0 ) {
            NativeIntAccess nia = new NativeIntAccess(2);
            try {
                if( OLE.S_OK == ia2.get_localizedExtendedStates(maxLocalizedExtendedStates,nia.getAddress(0),nia.getAddress(1)) ) {
                    return getStringArray(nia.getInt(0),nia.getInt(1),true);
                }
            }
            finally {
                nia.dispose();
            }
        }
        return null;
    }
    
    public int getUniqueID() {
        if( null != ia2 ) {
            NativeIntAccess nia = new NativeIntAccess();
            try {
                if( OLE.S_OK == ia2.get_uniqueID(nia.getAddress()) ) {
                    return nia.getInt();
                }
            }
            finally {
                nia.dispose();
            }
        }
        return 0;
    }
    
    public int getWindowHandle() {
        if( null != ia2 ) {
            NativeIntAccess nia = new NativeIntAccess();
            try {
                if( OLE.S_OK == ia2.get_windowHandle(nia.getAddress()) ) {
                    return nia.getInt();
                }
            }
            finally {
                nia.dispose();
            }
        }
        return 0;
    }
    
    public int getAccessibleIndexInParent() {
        if( null != ia2 ) {
            NativeIntAccess nia = new NativeIntAccess();
            try {
                if( OLE.S_OK == ia2.get_indexInParent(nia.getAddress()) ) {
                    return nia.getInt();
                }
            }
            finally {
                nia.dispose();
            }
        }
        return 0;
    }
    
    public Locale getLocale() {
        if( null != ia2 ) {
            NativeIntAccess nia = new NativeIntAccess(3);
            try {
                if( OLE.S_OK == ia2.get_locale(nia.getAddress()) ) {
                    String[] ls = getStringArray(nia.getAddress(),3, false); 
                    return new Locale(ls[0],ls[1],ls[2]); 
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
    
    public String getAttributes() {
        if( null != ia2 ) {
            NativeStringAccess nsa = new NativeStringAccess();
            try {
                if( OLE.S_OK == ia2.get_attributes(nsa.getAddress()) ) {
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
    
    public static String[] getStringArray(long pBSTR, int count, boolean bFree) {
        String[] sRet = new String[count];
        if( count > 0 ) {
            long[] hMem = new long[count];
            OS.MoveMemory(hMem, pBSTR, OS.PTR_SIZEOF * count);
            for( int i=0; i<count; i++) {
                sRet[i] = ""; //$NON-NLS-1$
                if (0 != hMem[i]) {
                    int size = COM.SysStringByteLen(hMem[i]);
                    if (size > 0) {
                        char[] buffer = new char[(size + 1) / 2];
                        MemoryUtil.MoveMemory(buffer, hMem[i], size);
                        sRet[i] = new String(buffer);
                    }
                    COM.SysFreeString(hMem[i]);
                }
            }
        }
        if( bFree ) {
            COM.CoTaskMemFree(pBSTR);
        }
        return sRet;
    }
}
