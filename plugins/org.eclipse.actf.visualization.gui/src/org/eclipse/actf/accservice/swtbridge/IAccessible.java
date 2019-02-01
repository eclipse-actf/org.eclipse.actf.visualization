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
import org.eclipse.actf.util.win32.NativeVariantAccess;
import org.eclipse.swt.internal.ole.win32.COM;
import org.eclipse.swt.internal.ole.win32.GUID;
import org.eclipse.swt.internal.ole.win32.IDispatch;
import org.eclipse.swt.ole.win32.OLE;
import org.eclipse.swt.ole.win32.Variant;


public class IAccessible extends IDispatch {
    public static final GUID IID = COMUtil.IIDFromString("{618736E0-3C3D-11CF-810C-00AA00389B71}"); //$NON-NLS-1$
    
	long address;
	public IAccessible(long address) {
		super(address);
		this.address = address;
	}
	
	public long getAddress() {
		return address;
	}

	public int get_accParent(long ppdispParent) {
		return COM.VtblCall(7, address, ppdispParent);
	}
	public int get_accChildCount(long pcountChildren) {
		return COM.VtblCall(8, address, pcountChildren);
	}
//	public int get_accChild(int varChild_vt, int varChild_reserved1, int varChild_lVal, int varChild_reserved2, long ppdispChild) {
//		return COM.VtblCall(9, address, varChild_vt, varChild_reserved1, varChild_lVal, varChild_reserved2, ppdispChild);
//	}
	public int get_accChild(Variant varChild, long ppdispChild) {
		return callVariantP(varChild, 9, ppdispChild);
	}
//	public int get_accName(int varChild_vt, int varChild_reserved1, int varChild_lVal, int varChild_reserved2, long pszName) {
//		return COM.VtblCall(10, address, varChild_vt, varChild_reserved1, varChild_lVal, varChild_reserved2, pszName);
//	}
	public int get_accName(Variant varChild, long pszName) {
		return callVariantP(varChild, 10, pszName);
	}
//	public int get_accValue(int varChild_vt, int varChild_reserved1, int varChild_lVal, int varChild_reserved2, long pszValue) {
//		return COM.VtblCall(11, address, varChild_vt, varChild_reserved1, varChild_lVal, varChild_reserved2, pszValue);
//	}
	public int get_accValue(Variant varChild, long pszValue) {
		return callVariantP(varChild, 11, pszValue);
	}
//	public int get_accDescription(int varChild_vt, int varChild_reserved1, int varChild_lVal, int varChild_reserved2, long pszDescription) {
//		return COM.VtblCall(12, address, varChild_vt, varChild_reserved1, varChild_lVal, varChild_reserved2, pszDescription);
//	}
	public int get_accDescription(Variant varChild, long pszDescription) {
		return callVariantP(varChild, 12, pszDescription);
	}
//	public int get_accRole(int varChild_vt, int varChild_reserved1, int varChild_lVal, int varChild_reserved2, long pvarRole) {
//		return COM.VtblCall(13, address, varChild_vt, varChild_reserved1, varChild_lVal, varChild_reserved2, pvarRole);
//	}
	public int get_accRole(Variant varChild, long pvarRole) {
		return callVariantP(varChild, 13, pvarRole);
	}
//	public int get_accState(int varChild_vt, int varChild_reserved1, int varChild_lVal, int varChild_reserved2, long pvarState) {
//		return COM.VtblCall(14, address, varChild_vt, varChild_reserved1, varChild_lVal, varChild_reserved2, pvarState);
//	}
	public int get_accState(Variant varChild, long pvarState) {
		return callVariantP(varChild, 14, pvarState);
	}
//	public int get_accHelp(int varChild_vt, int varChild_reserved1, int varChild_lVal, int varChild_reserved2, long pszHelp) {
//		return COM.VtblCall(15, address, varChild_vt, varChild_reserved1, varChild_lVal, varChild_reserved2, pszHelp);
//	}
	public int get_accHelp(Variant varChild, long pszHelp) {
		return callVariantP(varChild, 15, pszHelp);
	}
//	public int get_accHelpTopic(long pszHelpFile, int varChild_vt, int varChild_reserved1, int varChild_lVal, int varChild_reserved2, long pidTopic) {
//		return COM.VtblCall(16, address, pszHelpFile, varChild_vt, varChild_reserved1, varChild_lVal, varChild_reserved2, pidTopic);
//	}
	public int get_accHelpTopic(long pszHelpFile, Variant varChild, long pidTopic) {
        NativeVariantAccess nva = new NativeVariantAccess();
        nva.setVariant(varChild);
        try {
    		return COM.VtblCall_PVARIANTP(16, address, pszHelpFile, nva.getAddress(), pidTopic);
        }
        finally {
            nva.dispose();
        }
	}
//	public int get_accKeyboardShortcut(int varChild_vt, int varChild_reserved1, int varChild_lVal, int varChild_reserved2, long pszKeyboardShortcut) {
//		return COM.VtblCall(17, address, varChild_vt, varChild_reserved1, varChild_lVal, varChild_reserved2, pszKeyboardShortcut);
//	}
	public int get_accKeyboardShortcut(Variant varChild, long pszKeyboardShortcut) {
		return callVariantP(varChild, 17, pszKeyboardShortcut);
	}
	public int get_accFocus(int pvarChild) {
		return COMUtil.VtblCall(18, address, pvarChild);
	}
	public int get_accSelection(int pvarChildren) {
		return COMUtil.VtblCall(19, address, pvarChildren);
	}
//	public int get_accDefaultAction(int varChild_vt, int varChild_reserved1, int varChild_lVal, int varChild_reserved2, long pszDefaultAction) {
//		return COM.VtblCall(20, address, varChild_vt, varChild_reserved1, varChild_lVal, varChild_reserved2, pszDefaultAction);
//	}
	public int get_accDefaultAction(Variant varChild, long pszDefaultAction) {
		return callVariantP(varChild, 20, pszDefaultAction);
	}
//	public int accSelect(int flagsSelect, int varChild_vt, int varChild_reserved1, int varChild_lVal, int varChild_reserved2) {
//		return COMUtil.VtblCall(21, address, flagsSelect, varChild_vt, varChild_reserved1, varChild_lVal, varChild_reserved2);
//	}
	public int accSelect(int flagsSelect, Variant varChild) {
        NativeVariantAccess nva = new NativeVariantAccess();
        nva.setVariant(varChild);
        try {
    		return COM.VtblCall_IVARIANT(21, address, flagsSelect, nva.getAddress());
        }
        finally {
            nva.dispose();
        }
	}
//	public int accLocation(long pxLeft, long pyTop, long pcxWidth, long pcyHeight,
//			int varChild_vt, int varChild_reserved1, int varChild_lVal, int varChild_reserved2) {
//			return COM.VtblCall(22, address, pxLeft, pyTop, pcxWidth, pcyHeight, varChild_vt, varChild_reserved1, varChild_lVal, varChild_reserved2);
//	}
	public int accLocation(long pxLeft, long pyTop, long pcxWidth, long pcyHeight, Variant varChild) {
        NativeVariantAccess nva = new NativeVariantAccess();
        nva.setVariant(varChild);
        try {
    		return COM.VtblCall_PPPPVARIANT(22, address, pxLeft, pyTop, pcxWidth, pcyHeight, nva.getAddress());
        }
        finally {
            nva.dispose();
        }
	}
	public int accNavigate(int navDir, int varStart_vt, int varStart_reserved1, int varStart_lVal, int varStart_reserved2, int pvarEndUpAt) {
		return COMUtil.VtblCall(23, address, navDir, varStart_vt, varStart_reserved1, varStart_lVal, varStart_reserved2, pvarEndUpAt);
	}
	public int accHitTest(int xLeft, int yTop, int pvarChild) {
		return COMUtil.VtblCall(24, address, xLeft, yTop, pvarChild);
	}
//	public int accDoDefaultAction(int varChild_vt, int varChild_reserved1, int varChild_lVal, int varChild_reserved2) {
//		return COMUtil.VtblCall(25, address, varChild_vt, varChild_reserved1, varChild_lVal, varChild_reserved2);
//	}
	public int accDoDefaultAction(Variant varChild) {
        NativeVariantAccess nva = new NativeVariantAccess();
        nva.setVariant(varChild);
        try {
    		return COM.VtblCall_VARIANT(25, address, nva.getAddress());
        }
        finally {
            nva.dispose();
        }
	}
//	public int put_accName(int varChild_vt, int varChild_reserved1, int varChild_lVal, int varChild_reserved2, long szName) {
//		return COM.VtblCall(26, address, varChild_vt, varChild_reserved1, varChild_lVal, varChild_reserved2, szName);
//	}
	public int put_accName(Variant varChild, long szName) {
		return callVariantP(varChild, 26, szName);
	}
//	public int put_accValue(int varChild_vt, int varChild_reserved1, int varChild_lVal, int varChild_reserved2, long szValue) {
//		return COM.VtblCall(27, address, varChild_vt, varChild_reserved1, varChild_lVal, varChild_reserved2, szValue);
//	}
	public int put_accValue(Variant varChild, long szValue) {
		return callVariantP(varChild, 27, szValue);
	}

	private int callVariantP(Variant varIn, int fn, long pVarOut) {
        NativeVariantAccess nva = new NativeVariantAccess();
        nva.setVariant(varIn);
        try {
    		return COM.VtblCall_VARIANTP(fn, address, nva.getAddress(), pVarOut);
        }
        finally {
            nva.dispose();
        }
	}
}
