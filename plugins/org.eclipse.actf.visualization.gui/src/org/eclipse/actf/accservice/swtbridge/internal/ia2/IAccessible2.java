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

import org.eclipse.actf.accservice.swtbridge.IAccessible;
import org.eclipse.actf.util.win32.COMUtil;
import org.eclipse.swt.internal.ole.win32.COM;
import org.eclipse.swt.internal.ole.win32.GUID;




public class IAccessible2 extends IAccessible {
    public static final GUID IID = COMUtil.IIDFromString("{E89F726E-C4F4-4C19-BB19-B647D7FA8478}"); //$NON-NLS-1$
    
    long address;
    public IAccessible2(long address) {
        super(address);
        this.address = address;
    }
    
    public int get_nRelations(long pnRelations) {
        return COM.VtblCall(28, address, pnRelations);
    }
    public int get_relation(int relationIndex, long pdispRelation){ 
        return COM.VtblCall(29, address, relationIndex, pdispRelation);
    }
    public int get_relations(int maxRelations, long pdispRelation,long pnRelations) {
        return COM.VtblCall(30, address, maxRelations, pdispRelation, pnRelations);
    }
    public int get_role(long pRole) {
    	return COM.VtblCall(31, address, pRole);
    }
    public int scrollTo(int scrollType) {
        return COM.VtblCall(32, address, scrollType);  // TODO
    }
    public int scrollToPoint(int coordinateType, int x, int y) {
        return COMUtil.VtblCall(33, address, coordinateType, x, y);  // TODO
    }
    public int get_groupPosition(long pGroupLevel, long pSimilarItemsInGroup, long pPositionInGroup) {
        return COM.VtblCall(34, address, pGroupLevel, pSimilarItemsInGroup, pPositionInGroup);
    }
    public int get_states(long pdispStates ) {
        return COM.VtblCall(35, address, pdispStates); 
    }
    public int get_extendedRole(long pszExtendedRole) {
        return COM.VtblCall(36, address, pszExtendedRole);
    }
    public int get_localizedExtendedRole(long pszLocalizedExtendedRole) {
        return COM.VtblCall(37, address, pszLocalizedExtendedRole);
    }
    public int get_nExtendedStates(long pnExtendedStates) {
        return COM.VtblCall(38, address, pnExtendedStates);
    }
    public int get_extendedStates(int maxExtendedStates,long ppszExtendedStates,long pnExtendedStates) {
        return COM.VtblCall(39, address, maxExtendedStates, ppszExtendedStates, pnExtendedStates);
    }
    public int get_localizedExtendedStates(int maxLocalizedExtendedStates,long ppszLocalizedExtendedStates,long pnLocalizedExtendedStates) {
        return COM.VtblCall(40, address, maxLocalizedExtendedStates, ppszLocalizedExtendedStates, pnLocalizedExtendedStates);
    }
    public int get_uniqueID(long pUniqueID) {
        return COM.VtblCall(41, address, pUniqueID); 
    }
    public int get_windowHandle(long pWindowHandle) {
        return COM.VtblCall(42, address, pWindowHandle);
    }
    public int get_indexInParent(long pIndexInParent) {
        return COM.VtblCall(43, address, pIndexInParent);
    }
    public int get_locale(long pLocale) {
        return COM.VtblCall(44, address, pLocale); 
    }
    public int get_attributes(long pszAttributes) {
        return COM.VtblCall(45, address, pszAttributes);
    }
        

}
