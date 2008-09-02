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

import org.eclipse.actf.util.win32.COMUtil;
import org.eclipse.swt.internal.ole.win32.GUID;
import org.eclipse.swt.internal.ole.win32.IDispatch;


public class IAccessible extends IDispatch {
    public static final GUID IID = COMUtil.IIDFromString("{618736E0-3C3D-11CF-810C-00AA00389B71}"); //$NON-NLS-1$
    
	int address;
	public IAccessible(int address) {
		super(address);
		this.address = address;
	}
	
	public int getAddress() {
		return address;
	}

	public int get_accParent(int ppdispParent) {
		return COMUtil.VtblCall(7, address, ppdispParent);
	}
	public int get_accChildCount(int pcountChildren) {
		return COMUtil.VtblCall(8, address, pcountChildren);
	}
	public int get_accChild(int varChild_vt, int varChild_reserved1, int varChild_lVal, int varChild_reserved2, int ppdispChild) {
		return COMUtil.VtblCall(9, address, varChild_vt, varChild_reserved1, varChild_lVal, varChild_reserved2, ppdispChild);
	}
	public int get_accName(int varChild_vt, int varChild_reserved1, int varChild_lVal, int varChild_reserved2, int pszName) {
		return COMUtil.VtblCall(10, address, varChild_vt, varChild_reserved1, varChild_lVal, varChild_reserved2, pszName);
	}
	public int get_accValue(int varChild_vt, int varChild_reserved1, int varChild_lVal, int varChild_reserved2, int pszValue) {
		return COMUtil.VtblCall(11, address, varChild_vt, varChild_reserved1, varChild_lVal, varChild_reserved2, pszValue);
	}
	public int get_accDescription(int varChild_vt, int varChild_reserved1, int varChild_lVal, int varChild_reserved2, int pszDescription) {
		return COMUtil.VtblCall(12, address, varChild_vt, varChild_reserved1, varChild_lVal, varChild_reserved2, pszDescription);
	}
	public int get_accRole(int varChild_vt, int varChild_reserved1, int varChild_lVal, int varChild_reserved2, int pvarRole) {
		return COMUtil.VtblCall(13, address, varChild_vt, varChild_reserved1, varChild_lVal, varChild_reserved2, pvarRole);
	}
	public int get_accState(int varChild_vt, int varChild_reserved1, int varChild_lVal, int varChild_reserved2, int pvarState) {
		return COMUtil.VtblCall(14, address, varChild_vt, varChild_reserved1, varChild_lVal, varChild_reserved2, pvarState);
	}
	public int get_accHelp(int varChild_vt, int varChild_reserved1, int varChild_lVal, int varChild_reserved2, int pszHelp) {
		return COMUtil.VtblCall(15, address, varChild_vt, varChild_reserved1, varChild_lVal, varChild_reserved2, pszHelp);
	}
	public int get_accHelpTopic(int pszHelpFile, int varChild_vt, int varChild_reserved1, int varChild_lVal, int varChild_reserved2, int pidTopic) {
		return COMUtil.VtblCall(16, address, pszHelpFile, varChild_vt, varChild_reserved1, varChild_lVal, varChild_reserved2, pidTopic);
	}
	public int get_accKeyboardShortcut(int varChild_vt, int varChild_reserved1, int varChild_lVal, int varChild_reserved2, int pszKeyboardShortcut) {
		return COMUtil.VtblCall(17, address, varChild_vt, varChild_reserved1, varChild_lVal, varChild_reserved2, pszKeyboardShortcut);
	}
	public int get_accFocus(int pvarChild) {
		return COMUtil.VtblCall(18, address, pvarChild);
	}
	public int get_accSelection(int pvarChildren) {
		return COMUtil.VtblCall(19, address, pvarChildren);
	}
	public int get_accDefaultAction(int varChild_vt, int varChild_reserved1, int varChild_lVal, int varChild_reserved2, int pszDefaultAction) {
		return COMUtil.VtblCall(20, address, varChild_vt, varChild_reserved1, varChild_lVal, varChild_reserved2, pszDefaultAction);
	}
	public int accSelect(int flagsSelect, int varChild_vt, int varChild_reserved1, int varChild_lVal, int varChild_reserved2) {
		return COMUtil.VtblCall(21, address, flagsSelect, varChild_vt, varChild_reserved1, varChild_lVal, varChild_reserved2);
	}
	public int accLocation(int pxLeft, int pyTop, int pcxWidth, int pcyHeight,
		int varChild_vt, int varChild_reserved1, int varChild_lVal, int varChild_reserved2) {
		return COMUtil.VtblCall(22, address, pxLeft, pyTop, pcxWidth, pcyHeight, varChild_vt, varChild_reserved1, varChild_lVal, varChild_reserved2);
	}
	public int accNavigate(int navDir, int varStart_vt, int varStart_reserved1, int varStart_lVal, int varStart_reserved2, int pvarEndUpAt) {
		return COMUtil.VtblCall(23, address, navDir, varStart_vt, varStart_reserved1, varStart_lVal, varStart_reserved2, pvarEndUpAt);
	}
	public int accHitTest(int xLeft, int yTop, int pvarChild) {
		return COMUtil.VtblCall(24, address, xLeft, yTop, pvarChild);
	}
	public int accDoDefaultAction(int varChild_vt, int varChild_reserved1, int varChild_lVal, int varChild_reserved2) {
		return COMUtil.VtblCall(25, address, varChild_vt, varChild_reserved1, varChild_lVal, varChild_reserved2);
	}
	public int put_accName(int varChild_vt, int varChild_reserved1, int varChild_lVal, int varChild_reserved2, int szName) {
		return COMUtil.VtblCall(26, address, varChild_vt, varChild_reserved1, varChild_lVal, varChild_reserved2, szName);
	}
	public int put_accValue(int varChild_vt, int varChild_reserved1, int varChild_lVal, int varChild_reserved2, int szValue) {
		return COMUtil.VtblCall(27, address, varChild_vt, varChild_reserved1, varChild_lVal, varChild_reserved2, szValue);
	}
	
}
