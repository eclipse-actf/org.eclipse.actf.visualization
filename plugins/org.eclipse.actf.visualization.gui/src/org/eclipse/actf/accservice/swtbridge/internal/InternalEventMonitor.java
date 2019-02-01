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
package org.eclipse.actf.accservice.swtbridge.internal;

import java.util.Set;

import org.eclipse.actf.accservice.swtbridge.MSAA;
import org.eclipse.actf.accservice.swtbridge.event.IAccessibleEventListener;
import org.eclipse.actf.accservice.swtbridge.event.IAccessibleEventMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.Callback;


public class InternalEventMonitor implements IAccessibleEventMonitor {

	private Callback callback = null;
	private long hWinEventHook = 0;
	private IAccessibleEventListener listener = null;
	private Set<Integer> filter;
	
	/* (non-Javadoc)
	 * @see org.eclipse.actf.accservice.swtbridge.IAccessibleEventMonitor#installEventHook(org.eclipse.actf.accservice.swtbridge.IAccessibleEventListener, int[])
	 */
	public void installEventHook(IAccessibleEventListener listener, Set<Integer> filter) {
		removeEventHook();
		this.listener = listener;
		this.filter = filter;
		callback = new Callback(this,"WinEventProc",7); //$NON-NLS-1$
		long address = callback.getAddress();
		if (address == 0) SWT.error(SWT.ERROR_NO_MORE_CALLBACKS);
		hWinEventHook = MSAA.SetWinEventHook(0x00000001, 0x7FFFFFFF, 0, address, 0, 0,0);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.actf.accservice.swtbridge.IAccessibleEventMonitor#removeEventHook()
	 */
	public void removeEventHook() {
		if( 0 != hWinEventHook ) {
			MSAA.UnhookWinEvent(hWinEventHook);
			hWinEventHook = 0;
		}
		if( null != callback ) {
			callback.dispose();
			callback = null;
		}
	}
	
	long WinEventProc(long hEvent, long event, long hwnd, long idObject, long idChild, long idThread, long dwEventTime) {
//		System.out.println(	"hEvent=0x"+Integer.toHexString(hEvent)		+",  "+
//		"event=0x"+Integer.toHexString(event)		+",  "+
//		"hwnd=0x"+Integer.toHexString(hwnd)			+",  "+		
//		"idObject=0x"+Integer.toHexString(idObject)	+",  "+		
//		"idChild=0x"+Integer.toHexString(idChild)	+",  "+		
//		"idThread=0x"+Integer.toHexString(idThread)	+",  "+		
//		"dwEventTime=0x"+Integer.toHexString(dwEventTime) );
		if( null != listener && checkFilter((int)event) ) {
			listener.handleEvent((int)event, hwnd, (int)idObject, (int)idChild, null);
		}
		return 0;
	}
	
	private boolean checkFilter(int event) {
		if( null == filter ) return true;
		return filter.contains(new Integer(event));
	}
	
}
