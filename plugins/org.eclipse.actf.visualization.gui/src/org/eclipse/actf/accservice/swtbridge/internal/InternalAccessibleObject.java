/*******************************************************************************
 * Copyright (c) 2007, 2016 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Takashi ITOH - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.accservice.swtbridge.internal;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.actf.accservice.swtbridge.AccessibleObject;
import org.eclipse.actf.accservice.swtbridge.IAccessible;
import org.eclipse.actf.accservice.swtbridge.IServiceProvider;
import org.eclipse.actf.accservice.swtbridge.MSAA;
import org.eclipse.actf.accservice.swtbridge.ia2.Accessible2;
import org.eclipse.actf.accservice.swtbridge.ia2.AccessibleAction;
import org.eclipse.actf.accservice.swtbridge.ia2.AccessibleApplication;
import org.eclipse.actf.accservice.swtbridge.ia2.AccessibleComponent;
import org.eclipse.actf.accservice.swtbridge.ia2.AccessibleEditableText;
import org.eclipse.actf.accservice.swtbridge.ia2.AccessibleHyperlink;
import org.eclipse.actf.accservice.swtbridge.ia2.AccessibleHypertext;
import org.eclipse.actf.accservice.swtbridge.ia2.AccessibleImage;
import org.eclipse.actf.accservice.swtbridge.ia2.AccessibleTable;
import org.eclipse.actf.accservice.swtbridge.ia2.AccessibleText;
import org.eclipse.actf.accservice.swtbridge.ia2.AccessibleValue;
import org.eclipse.actf.accservice.swtbridge.internal.ia2.IAccessible2;
import org.eclipse.actf.accservice.swtbridge.internal.ia2.IAccessibleAction;
import org.eclipse.actf.accservice.swtbridge.internal.ia2.IAccessibleApplication;
import org.eclipse.actf.accservice.swtbridge.internal.ia2.IAccessibleComponent;
import org.eclipse.actf.accservice.swtbridge.internal.ia2.IAccessibleEditableText;
import org.eclipse.actf.accservice.swtbridge.internal.ia2.IAccessibleHyperlink;
import org.eclipse.actf.accservice.swtbridge.internal.ia2.IAccessibleHypertext;
import org.eclipse.actf.accservice.swtbridge.internal.ia2.IAccessibleImage;
import org.eclipse.actf.accservice.swtbridge.internal.ia2.IAccessibleTable;
import org.eclipse.actf.accservice.swtbridge.internal.ia2.IAccessibleText;
import org.eclipse.actf.accservice.swtbridge.internal.ia2.IAccessibleValue;
import org.eclipse.actf.accservice.swtbridge.internal.ia2.InternalAccessible2;
import org.eclipse.actf.accservice.swtbridge.internal.ia2.InternalAccessibleAction;
import org.eclipse.actf.accservice.swtbridge.internal.ia2.InternalAccessibleApplication;
import org.eclipse.actf.accservice.swtbridge.internal.ia2.InternalAccessibleComponent;
import org.eclipse.actf.accservice.swtbridge.internal.ia2.InternalAccessibleEditableText;
import org.eclipse.actf.accservice.swtbridge.internal.ia2.InternalAccessibleHyperlink;
import org.eclipse.actf.accservice.swtbridge.internal.ia2.InternalAccessibleHypertext;
import org.eclipse.actf.accservice.swtbridge.internal.ia2.InternalAccessibleImage;
import org.eclipse.actf.accservice.swtbridge.internal.ia2.InternalAccessibleTable;
import org.eclipse.actf.accservice.swtbridge.internal.ia2.InternalAccessibleText;
import org.eclipse.actf.accservice.swtbridge.internal.ia2.InternalAccessibleValue;
import org.eclipse.actf.util.win32.NativeIntAccess;
import org.eclipse.actf.util.win32.NativeStringAccess;
import org.eclipse.actf.util.win32.NativeVariantAccess;
import org.eclipse.actf.util.win32.WindowUtil;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.ole.win32.IDispatch;
import org.eclipse.swt.ole.win32.OLE;
import org.eclipse.swt.ole.win32.Variant;

public class InternalAccessibleObject implements AccessibleObject {

	private InternalAccessibleObject parent = null;

	private Variant varChildIndex = null;

	private IAccessible iAccessible = null;

	private Accessible2 accessible2 = null;
	private AccessibleAction accessibleAction = null;
	private AccessibleApplication accessibleApplication = null;
	private AccessibleComponent accessibleComponent = null;
	private AccessibleEditableText accessibleEditableText = null;
	private AccessibleHyperlink accessibleHyperlink = null;
	private AccessibleHypertext accessibleHypertext = null;
	private AccessibleImage accessibleImage = null;
	private AccessibleTable accessibleTable = null;
	private AccessibleText accessibleText = null;
	private AccessibleValue accessibleValue = null;

	private HashMap<String, InternalAccessibleObject> childMap = new HashMap<String, InternalAccessibleObject>();

	private InternalAccessibleObject[] cachedChildren = new InternalAccessibleObject[0];

	private boolean disposed = false;

	private int accRole;
	private int ia1Role, ia2Role;
	private long accWindow;
	private String strClassName;
	private static final String STR_NULL = null;// "";

	private InternalAccessibleObject(long address) {
		this((InternalAccessibleObject) null, new Variant(
				new IDispatch(address)));
	}

	public InternalAccessibleObject(InternalAccessibleObject parent,
			Variant varChild) {
		this.parent = parent;
		varChildIndex = varChild;
		switch (varChild.getType()) {
		case OLE.VT_I4:
			varChild = parent.getAccChild(varChild);
			if (null == varChild || OLE.VT_DISPATCH != varChild.getType()) {
				break;
			}
		case OLE.VT_DISPATCH:
			long[] ppvObject = new long[1];
			long[] ppvServiceProvider = new long[1];
			if (OLE.S_OK == varChild.getDispatch().QueryInterface(
					IServiceProvider.IID, ppvServiceProvider)) {
				IServiceProvider sp = new IServiceProvider(
						ppvServiceProvider[0]);
				try {
					if (OLE.S_OK == sp.QueryService(IAccessible.IID,
							IAccessible2.IID, ppvObject)) {
						iAccessible = new IAccessible2(ppvObject[0]);
						iAccessible.AddRef();
						accessible2 = new InternalAccessible2(
								(IAccessible2) iAccessible);
						if (OLE.S_OK == sp.QueryService(IAccessible.IID,
								IAccessibleAction.IID, ppvObject)) {
							accessibleAction = new InternalAccessibleAction(
									ppvObject[0]);
						}
						if (OLE.S_OK == sp.QueryService(IAccessible.IID,
								IAccessibleApplication.IID, ppvObject)) {
							accessibleApplication = new InternalAccessibleApplication(
									ppvObject[0]);
						}
						if (OLE.S_OK == sp.QueryService(IAccessible.IID,
								IAccessibleComponent.IID, ppvObject)) {
							accessibleComponent = new InternalAccessibleComponent(
									ppvObject[0]);
						}
						if (OLE.S_OK == sp.QueryService(IAccessible.IID,
								IAccessibleEditableText.IID, ppvObject)) {
							accessibleEditableText = new InternalAccessibleEditableText(
									ppvObject[0]);
						}
						if (OLE.S_OK == sp.QueryService(IAccessible.IID,
								IAccessibleHyperlink.IID, ppvObject)) {
							accessibleHyperlink = new InternalAccessibleHyperlink(
									ppvObject[0]);
						}
						if (OLE.S_OK == sp.QueryService(IAccessible.IID,
								IAccessibleHypertext.IID, ppvObject)) {
							accessibleHypertext = new InternalAccessibleHypertext(
									ppvObject[0]);
						}
						if (OLE.S_OK == sp.QueryService(IAccessible.IID,
								IAccessibleImage.IID, ppvObject)) {
							accessibleImage = new InternalAccessibleImage(
									ppvObject[0]);
						}
						if (OLE.S_OK == sp.QueryService(IAccessible.IID,
								IAccessibleTable.IID, ppvObject)) {
							accessibleTable = new InternalAccessibleTable(
									ppvObject[0]);
						}
						if (OLE.S_OK == sp.QueryService(IAccessible.IID,
								IAccessibleText.IID, ppvObject)) {
							accessibleText = new InternalAccessibleText(
									ppvObject[0]);
						}
						if (OLE.S_OK == sp.QueryService(IAccessible.IID,
								IAccessibleValue.IID, ppvObject)) {
							accessibleValue = new InternalAccessibleValue(
									ppvObject[0]);
						}
					} else if (OLE.S_OK == sp.QueryService(IAccessible.IID,
							IAccessible.IID, ppvObject)) {
						iAccessible = new IAccessible(ppvObject[0]);
						iAccessible.AddRef();
					}

				} finally {
					sp.Release();
				}
			}
			if (null == iAccessible) {
				if (OLE.S_OK == varChild.getDispatch().QueryInterface(
						IAccessible.IID, ppvObject)) {
					iAccessible = new IAccessible(ppvObject[0]);
					iAccessible.AddRef();
				} else {
					System.out.println("Error: Can not get IAccessible"); //$NON-NLS-1$
				}
			}
			varChildIndex = new Variant(0);
			varChild.dispose();
			break;
		}
		reset();
	}

	public void reset() {
		disposeChildren();
		NativeVariantAccess nva = new NativeVariantAccess();
		try {
//			if (OLE.S_OK == getIAccessible().get_accRole(OLE.VT_I4, 0,
//					varChildIndex.getInt(), 0, nva.getAddress())) {
//				accRole = nva.getInt();
//			}
			if (OLE.S_OK == getIAccessible().get_accRole(varChildIndex, nva.getAddress())) {
				accRole = nva.getInt();
			}
		} finally {
			nva.dispose();
		}
		accWindow = -1;
		strClassName = null;
		ia1Role = accRole;
		ia2Role = -1;
		Accessible2 ia2 = getAccessible2();
		if (null != ia2) {
			ia2Role = accRole = ia2.getAccessibleRole();
		}
	}

	public void dispose() throws Exception {
		if (disposed) {
			throw new Exception("Already disposed"); //$NON-NLS-1$
		}
		disposed = true;
		disposeChildren();
		if (null != iAccessible) {
			iAccessible.Release();
			iAccessible = null;
		}
		if (null != accessible2) {
			accessible2.dispose();
			accessible2 = null;
		}
		if (null != accessibleAction) {
			accessibleAction.dispose();
			accessibleAction = null;
		}
		if (null != accessibleApplication) {
			accessibleApplication.dispose();
			accessibleApplication = null;
		}
		if (null != accessibleComponent) {
			accessibleComponent.dispose();
			accessibleComponent = null;
		}
		if (null != accessibleEditableText) {
			accessibleEditableText.dispose();
			accessibleEditableText = null;
		}
		if (null != accessibleHyperlink) {
			accessibleHyperlink.dispose();
			accessibleHyperlink = null;
		}
		if (null != accessibleHypertext) {
			accessibleHypertext.dispose();
			accessibleHypertext = null;
		}
		if (null != accessibleImage) {
			accessibleImage.dispose();
			accessibleImage = null;
		}
		if (null != accessibleTable) {
			accessibleTable.dispose();
			accessibleTable = null;
		}
		if (null != accessibleText) {
			accessibleText.dispose();
			accessibleText = null;
		}
		if (null != accessibleValue) {
			accessibleValue.dispose();
			accessibleValue = null;
		}
		if (null != varChildIndex) {
			varChildIndex.dispose();
			varChildIndex = null;
		}
		if (null != parent) {
			parent.removeChildObject(this);
		}
	}

	private void disposeChildren() {
		cachedChildren = new InternalAccessibleObject[0];
		InternalAccessibleObject[] children = childMap.values().toArray(
				new InternalAccessibleObject[childMap.size()]);
		for (int i = 0; i < children.length; i++) {
			try {
				children[i].dispose();
			} catch (Exception e) {
			}
			;
		}
	}

	public static AccessibleObject getAccessibleObjectFromPoint(Point point) {
		int[] pChild = new int[1];
		long address = MSAA.getAccessibleObjectFromPoint(point, pChild);
		return newInstance(address, pChild[0]);
	}

	public static AccessibleObject getAccessibleObjectFromWindow(long hwnd) {
		long address = MSAA.AccessibleObjectFromWindow(hwnd);
		return newInstance(address);
	}

	public static AccessibleObject getAccessibleObjectFromEvent(long hwnd,
			int dwId, int dwChildId) {
		int[] pChild = new int[1];
		long address = MSAA.getAccessibleObjectFromEvent(hwnd, dwId, dwChildId,
				pChild);
		return newInstance(address, pChild[0]);
	}

	public static AccessibleObject newInstance(long address) {
		if (0 == address) {
			return null;
		}
		return new InternalAccessibleObject(address);
	}

	private static AccessibleObject newInstance(long address, int childId) {
		AccessibleObject accObject = newInstance(address);
		if ((null != accObject) && (MSAA.CHILDID_SELF != childId)) {
			return ((InternalAccessibleObject) accObject)
					.getChildObject(new Variant(childId));
		}
		return accObject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.accservice.swtbridge.AccessibleObject#getCachedParent()
	 */
	public AccessibleObject getCachedParent() {
		return parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.accservice.swtbridge.AccessibleObject#getCachedChildren()
	 */
	public AccessibleObject[] getCachedChildren() {
		return cachedChildren;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.accservice.swtbridge.AccessibleObject#getChildren()
	 */
	public AccessibleObject[] getChildren() {
		int childCount = Math.max(0, getChildCount());
		if (childCount == cachedChildren.length) {
			return cachedChildren;
		}
		if (childCount > 32 * 1024) {
			System.err
					.println("Too many children(" + childCount + "), we don't fectch."); //$NON-NLS-1$ //$NON-NLS-2$
			if (null != accessibleTable) {
				int nRows = accessibleTable.getAccessibleRowCount();
				int nCols = accessibleTable.getAccessibleColumnCount();
				if (0x10000 == nRows && 0x100 == nCols) {
					if (16 * 16 == cachedChildren.length) {
						return cachedChildren;
					}
					cachedChildren = new InternalAccessibleObject[16 * 16];
					for (int row = 0; row < 16; row++) {
						for (int col = 0; col < 16; col++) {
							InternalAccessibleObject cell = (InternalAccessibleObject) accessibleTable
									.getAccessibleCellAt(row, col);
							cell.parent = this;
							cachedChildren[row * 16 + col] = cell;
						}
					}
					System.err
							.println("getChildren() returned 256(16x16) table cells."); //$NON-NLS-1$
					return cachedChildren;
				}
			}
			return new InternalAccessibleObject[0];
		}
		cachedChildren = new InternalAccessibleObject[childCount];
		if (childCount > 0) {
			Variant[] varChildren = new Variant[childCount];
			MSAA.getAccessibleChildren(getIAccessible().getAddress(),
					varChildren);
			for (int i = 0; i < childCount; i++) {
				if (null != varChildren[i]) {
					cachedChildren[i] = getChildObject(varChildren[i]);
				}
			}
		}
		return cachedChildren;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.accservice.swtbridge.AccessibleObject#getChildCount()
	 */
	public int getChildCount() {
		try {
			if (varChildIndex.getInt() == 0) {
				NativeIntAccess nia = new NativeIntAccess();
				try {
					if (OLE.S_OK == getIAccessible().get_accChildCount(
							nia.getAddress())) {
						return nia.getInt();
					}
				} finally {
					nia.dispose();
				}
			}
		} catch (Exception e) {
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.accservice.swtbridge.AccessibleObject#getWindow()
	 */
	public long getWindow() {
		if (-1 == accWindow) {
			try {
				accWindow = MSAA.WindowFromAccessibleObject(getIAccessible()
						.getAddress());
			} catch (Exception e) {
				accWindow = 0;
			}
		}
		return accWindow;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.accservice.swtbridge.AccessibleObject#getAccRole()
	 */
	public int getAccRole() {
		return accRole;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.accservice.swtbridge.AccessibleObject#getRoleText()
	 */
	public String getRoleText() {
		if (-1 == accRole) {
			NativeVariantAccess nva = new NativeVariantAccess();
			try {
//				if (OLE.S_OK == getIAccessible().get_accRole(OLE.VT_I4, 0,
//						varChildIndex.getInt(), 0, nva.getAddress())) {
//					return nva.getString();
//				}
				if (OLE.S_OK == getIAccessible().get_accRole(varChildIndex, nva.getAddress())) {
					return nva.getString();
				}
			} finally {
				nva.dispose();
			}
		} else {
			return MSAA.getRoleText(accRole);
		}
		return STR_NULL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.accservice.swtbridge.AccessibleObject#getClassName()
	 */
	public String getClassName() {
		if (null == strClassName) {
			long hwnd = this.getWindow();
			strClassName = STR_NULL;
			if (0 != hwnd) {
				strClassName = WindowUtil.GetWindowClassName(hwnd);
			}
		}
		return strClassName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.accservice.swtbridge.AccessibleObject#getAccState()
	 */
	public int getAccState() {
		NativeVariantAccess nva = new NativeVariantAccess();
		try {
//			if (OLE.S_OK == getIAccessible().get_accState(OLE.VT_I4, 0,
//					varChildIndex.getInt(), 0, nva.getAddress())) {
//				return nva.getInt();
//			}
			if (OLE.S_OK == getIAccessible().get_accState(varChildIndex, nva.getAddress())) {
				return nva.getInt();
			}
		} catch (Exception e) {
		} finally {
			nva.dispose();
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.accservice.swtbridge.AccessibleObject#getAccName()
	 */
	public String getAccName() {
		NativeStringAccess nsa = new NativeStringAccess();
		try {
//			if (OLE.S_OK == getIAccessible().get_accName(OLE.VT_I4, 0,
//					varChildIndex.getInt(), 0, nsa.getAddress())) {
//				return nsa.getString();
//			}
			if (OLE.S_OK == getIAccessible().get_accName(varChildIndex, nsa.getAddress())) {
				return nsa.getString();
			}
		} catch (Exception e) {
		} finally {
			nsa.dispose();
		}
		return STR_NULL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.accservice.swtbridge.AccessibleObject#getAccValue()
	 */
	public String getAccValue() {
		NativeStringAccess nsa = new NativeStringAccess();
		try {
//			if (OLE.S_OK == getIAccessible().get_accValue(OLE.VT_I4, 0,
//					varChildIndex.getInt(), 0, nsa.getAddress())) {
//				return nsa.getString();
//			}
			if (OLE.S_OK == getIAccessible().get_accValue(varChildIndex, nsa.getAddress())) {
				return nsa.getString();
			}
		} catch (Exception e) {
		} finally {
			nsa.dispose();
		}
		return STR_NULL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.accservice.swtbridge.AccessibleObject#getAccDescription()
	 */
	public String getAccDescription() {
		NativeStringAccess nsa = new NativeStringAccess();
		try {
//			if (OLE.S_OK == getIAccessible().get_accDescription(OLE.VT_I4, 0,
//					varChildIndex.getInt(), 0, nsa.getAddress())) {
//				return nsa.getString();
//			}
			if (OLE.S_OK == getIAccessible().get_accDescription(varChildIndex, nsa.getAddress())) {
				return nsa.getString();
			}
		} catch (Exception e) {
		} finally {
			nsa.dispose();
		}
		return STR_NULL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.accservice.swtbridge.AccessibleObject#getAccHelp()
	 */
	public String getAccHelp() {
		NativeStringAccess nsa = new NativeStringAccess();
		try {
//			if (OLE.S_OK == getIAccessible().get_accHelp(OLE.VT_I4, 0,
//					varChildIndex.getInt(), 0, nsa.getAddress())) {
//				return nsa.getString();
//			}
			if (OLE.S_OK == getIAccessible().get_accHelp(varChildIndex, nsa.getAddress())) {
				return nsa.getString();
			}
		} catch (Exception e) {
		} finally {
			nsa.dispose();
		}
		return STR_NULL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.accservice.swtbridge.AccessibleObject#getAccKeyboardShortcut()
	 */
	public String getAccKeyboardShortcut() {
		NativeStringAccess nsa = new NativeStringAccess();
		try {
//			if (OLE.S_OK == getIAccessible().get_accKeyboardShortcut(OLE.VT_I4,
//					0, varChildIndex.getInt(), 0, nsa.getAddress())) {
//				return nsa.getString();
//			}
			if (OLE.S_OK == getIAccessible().get_accKeyboardShortcut(varChildIndex, nsa.getAddress())) {
				return nsa.getString();
			}
		} catch (Exception e) {
		} finally {
			nsa.dispose();
		}
		return STR_NULL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.accservice.swtbridge.AccessibleObject#getAccDefaultAction()
	 */
	public String getAccDefaultAction() {
		NativeStringAccess nsa = new NativeStringAccess();
		try {
//			if (OLE.S_OK == getIAccessible().get_accDefaultAction(OLE.VT_I4, 0,
//					varChildIndex.getInt(), 0, nsa.getAddress())) {
//				return nsa.getString();
//			}
			if (OLE.S_OK == getIAccessible().get_accDefaultAction(varChildIndex, nsa.getAddress())) {
				return nsa.getString();
			}
		} catch (Exception e) {
		} finally {
			nsa.dispose();
		}
		return STR_NULL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.accservice.swtbridge.AccessibleObject#getAccHelpTopic(int[],
	 *      java.lang.String[])
	 */
	public boolean getAccHelpTopic(int[] pTopicIndex, String[] pHelpFile) {
		NativeStringAccess nsa = new NativeStringAccess();
		NativeIntAccess nia = new NativeIntAccess();
		try {
//			if (OLE.S_OK == getIAccessible().get_accHelpTopic(nsa.getAddress(),
//					OLE.VT_I4, 0, varChildIndex.getInt(), 0, nia.getAddress())) {
//				pTopicIndex[0] = nia.getInt();
//				pHelpFile[0] = nsa.getString();
//				return true;
//			}
			if (OLE.S_OK == getIAccessible().get_accHelpTopic(nsa.getAddress(), varChildIndex, nia.getAddress())) {
				pTopicIndex[0] = nia.getInt();
				pHelpFile[0] = nsa.getString();
				return true;
			}
		} catch (Exception e) {
		} finally {
			nia.dispose();
			nsa.dispose();
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.accservice.swtbridge.AccessibleObject#getAccLocation()
	 */
	public Rectangle getAccLocation() {
		if (disposed)
			return null;
		NativeIntAccess nia = new NativeIntAccess(4);
		try {
//			if (OLE.S_OK == getIAccessible().accLocation(nia.getAddress(0),
//					nia.getAddress(1), nia.getAddress(2), nia.getAddress(3),
//					OLE.VT_I4, 0, varChildIndex.getInt(), 0)) {
//				return new Rectangle(nia.getInt(0), nia.getInt(1), nia
//						.getInt(2), nia.getInt(3));
//			}
			if (OLE.S_OK == getIAccessible().accLocation(nia.getAddress(0),
					nia.getAddress(1), nia.getAddress(2), nia.getAddress(3),
					varChildIndex)) {
				return new Rectangle(nia.getInt(0), nia.getInt(1), nia
						.getInt(2), nia.getInt(3));
			}
		} catch (Exception e) {
		} finally {
			nia.dispose();
		}
		return null;
	}

	public boolean doDefaultAction() {
//		return OLE.S_OK == getIAccessible().accDoDefaultAction(OLE.VT_I4, 0,
//				varChildIndex.getInt(), 0);
		return OLE.S_OK == getIAccessible().accDoDefaultAction(varChildIndex);
	}

	public boolean select(int flagsSelect) {
//		return OLE.S_OK == getIAccessible().accSelect(flagsSelect, OLE.VT_I4,
//				0, varChildIndex.getInt(), 0);
		return OLE.S_OK == getIAccessible().accSelect(flagsSelect, varChildIndex);
	}

	public boolean setAccName(String strName) {
		NativeStringAccess nsa = new NativeStringAccess();
		try {
			nsa.setString(strName);
//			return OLE.S_OK == getIAccessible().put_accName(OLE.VT_I4, 0,
//					varChildIndex.getInt(), 0, nsa.getAddress());
			return OLE.S_OK == getIAccessible().put_accName(varChildIndex, nsa.getAddress());
		} finally {
			nsa.dispose();
		}
	}

	public boolean setAccValue(String strValue) {
		NativeStringAccess nsa = new NativeStringAccess();
		try {
			nsa.setString(strValue);
//			return OLE.S_OK == getIAccessible().put_accValue(OLE.VT_I4, 0,
//					varChildIndex.getInt(), 0, nsa.getAddress());
			return OLE.S_OK == getIAccessible().put_accValue(varChildIndex, nsa.getAddress());
		} finally {
			nsa.dispose();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.accservice.swtbridge.AccessibleObject#getAccParent()
	 */
	public AccessibleObject getAccParent() {
		NativeIntAccess nia = new NativeIntAccess();
		try {
			if (OLE.S_OK == getIAccessible().get_accParent(nia.getAddress())) {
				return newInstance(nia.getInt());
			}
		} finally {
			nia.dispose();
		}
		return null;
	}

	/*
	 * Internal functions
	 */

	private Variant getAccChild(Variant varChild) {
		NativeIntAccess nia = new NativeIntAccess();
		try {
//			if (OLE.S_OK == getIAccessible().get_accChild(OLE.VT_I4, 0,
//					varChild.getInt(), 0, nia.getAddress())) {
//				return new Variant(new IDispatch(nia.getInt()));
//			}
			if (OLE.S_OK == getIAccessible().get_accChild(varChild, nia.getAddress())) {
				return new Variant(new IDispatch(nia.getInt()));
			}
		} catch (Exception e) {
		} finally {
			nia.dispose();
		}
		return null;
	}

	public IAccessible getIAccessible() {
		if (null != iAccessible) {
			return iAccessible;
		}
		if (null != parent) {
			return parent.getIAccessible();
		}
		return null;
	}

	public Accessible2 getAccessible2() {
		if (null != accessible2) {
			return accessible2;
		}
		if (null != parent) {
			return parent.getAccessible2();
		}
		return null;
	}

	public AccessibleAction getAccessibleAction() {
		if (null != accessibleAction) {
			return accessibleAction;
		}
		if (null != parent) {
			return parent.getAccessibleAction();
		}
		return null;
	}

	public AccessibleApplication getAccessibleApplication() {
		if (null != accessibleApplication) {
			return accessibleApplication;
		}
		if (null != parent) {
			return parent.getAccessibleApplication();
		}
		return null;
	}

	public AccessibleComponent getAccessibleComponent() {
		if (null != accessibleComponent) {
			return accessibleComponent;
		}
		if (null != parent) {
			return parent.getAccessibleComponent();
		}
		return null;
	}

	public AccessibleEditableText getAccessibleEditableText() {
		if (null != accessibleEditableText) {
			return accessibleEditableText;
		}
		if (null != parent) {
			return parent.getAccessibleEditableText();
		}
		return null;
	}

	public AccessibleHyperlink getAccessibleHyperlink() {
		if (null != accessibleHyperlink) {
			return accessibleHyperlink;
		}
		if (null != parent) {
			return parent.getAccessibleHyperlink();
		}
		return null;
	}

	public AccessibleHypertext getAccessibleHypertext() {
		if (null != accessibleHypertext) {
			return accessibleHypertext;
		}
		if (null != parent) {
			return parent.getAccessibleHypertext();
		}
		return null;
	}

	public AccessibleImage getAccessibleImage() {
		if (null != accessibleImage) {
			return accessibleImage;
		}
		if (null != parent) {
			return parent.getAccessibleImage();
		}
		return null;
	}

	public AccessibleTable getAccessibleTable() {
		if (null != accessibleTable) {
			return accessibleTable;
		}
		if (null != parent) {
			return parent.getAccessibleTable();
		}
		return null;
	}

	public AccessibleText getAccessibleText() {
		if (null != accessibleText) {
			return accessibleText;
		}
		if (null != parent) {
			return parent.getAccessibleText();
		}
		return null;
	}

	public AccessibleValue getAccessibleValue() {
		if (null != accessibleValue) {
			return accessibleValue;
		}
		if (null != parent) {
			return parent.getAccessibleValue();
		}
		return null;
	}

	private InternalAccessibleObject getChildObject(Variant varChild) {
		String key = varChild.toString();
		InternalAccessibleObject accObject = childMap.get(key);
		if (null == accObject) {
			childMap.put(key, accObject = new InternalAccessibleObject(this,
					varChild));
			if (accObject.checkInactiveControl()) {
				accObject.reset();
			}
		}
		return accObject;
	}

	private void removeChildObject(InternalAccessibleObject accObject) {
		for (Iterator<String> it = childMap.keySet().iterator(); it.hasNext();) {
			String key = it.next();
			if (accObject == childMap.get(key)) {
				childMap.remove(key);
				break;
			}
		}
	}

	private boolean checkInactiveControl() {
		if ("Inactive Control".equals(getAccName()) && MSAA.ROLE_SYSTEM_PUSHBUTTON == getAccRole()) { //$NON-NLS-1$
			return doDefaultAction();
		}
		return false;
	}

	protected void finalize() throws Throwable {
		if (!disposed) {
			dispose();
		}
		super.finalize();
	}

	public String toString() {
		if (disposed) {
			return super.toString() + " disposed"; //$NON-NLS-1$
		}
		return "AccessibleObject role=" + getRoleText() + ", name=" + getAccName() + ", state=" + getAccState() + ", location=" + getAccLocation(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.accservice.swtbridge.AccessibleObject#getRealAccRole()
	 */
	public int getRealAccRole() {
		return ia1Role;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.accservice.swtbridge.AccessibleObject#getRealRoleText()
	 */
	public String getRealRoleText() {
		if (-1 == ia1Role) {
			NativeVariantAccess nva = new NativeVariantAccess();
			try {
//				if (OLE.S_OK == getIAccessible().get_accRole(OLE.VT_I4, 0,
//						varChildIndex.getInt(), 0, nva.getAddress())) {
//					return nva.getString();
//				}
				if (OLE.S_OK == getIAccessible().get_accRole(varChildIndex, nva.getAddress())) {
					return nva.getString();
				}
			} finally {
				nva.dispose();
			}
		} else {
			return MSAA.getRoleText(ia1Role);
		}
		return STR_NULL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object getAdapter(Class adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	public long getPtr() {
		return getIAccessible().getAddress();
	}
}
