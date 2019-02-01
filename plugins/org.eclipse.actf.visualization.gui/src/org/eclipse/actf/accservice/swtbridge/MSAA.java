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

import org.eclipse.actf.util.win32.NativeVariantAccess;
import org.eclipse.swt.accessibility.ACC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.ole.win32.IDispatch;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.internal.win32.TCHAR;
import org.eclipse.swt.ole.win32.OLE;
import org.eclipse.swt.ole.win32.Variant;


public class MSAA {
	
	static {
		try {
			System.loadLibrary("AccessibilityJavaMsaaLibrary"); //$NON-NLS-1$
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
	}
    
	public static final int CHILDID_SELF = 0;
	
	// Role 0x0*
    public static final int ROLE_SYSTEM_TITLEBAR = 0x01;
    public static final int ROLE_SYSTEM_MENUBAR = ACC.ROLE_MENUBAR; // 0x02
    public static final int ROLE_SYSTEM_SCROLLBAR = ACC.ROLE_SCROLLBAR; // 0x03
    public static final int ROLE_SYSTEM_GRIP = 0x04;
    public static final int ROLE_SYSTEM_SOUND = 0x5;
    public static final int ROLE_SYSTEM_CURSOR = 0x6;
    public static final int ROLE_SYSTEM_CARET = 0x7;
    public static final int ROLE_SYSTEM_ALERT = 0x8;
    public static final int ROLE_SYSTEM_WINDOW = ACC.ROLE_WINDOW; // 0x09
    public static final int ROLE_SYSTEM_CLIENT = ACC.ROLE_CLIENT_AREA; // 0x0a
    public static final int ROLE_SYSTEM_MENUPOPUP = ACC.ROLE_MENU; // 0x0b
    public static final int ROLE_SYSTEM_MENUITEM = ACC.ROLE_MENUITEM; // 0x0c
    public static final int ROLE_SYSTEM_TOOLTIP = ACC.ROLE_TOOLTIP; // 0x0d
    public static final int ROLE_SYSTEM_APPLICATION = 0xe;
    public static final int ROLE_SYSTEM_DOCUMENT = 0xf;
    // Role 0x1*
    public static final int ROLE_SYSTEM_PANE = 0x10;
    public static final int ROLE_SYSTEM_CHART = 0x11;
    public static final int ROLE_SYSTEM_DIALOG = ACC.ROLE_DIALOG; // 0x12
    public static final int ROLE_SYSTEM_BORDER = 0x13;
    public static final int ROLE_SYSTEM_GROUPING = 0x14;
    public static final int ROLE_SYSTEM_SEPARATOR = ACC.ROLE_SEPARATOR; // 0x15
    public static final int ROLE_SYSTEM_TOOLBAR = ACC.ROLE_TOOLBAR; // 0x16
    public static final int ROLE_SYSTEM_STATUSBAR = 0x17;
    public static final int ROLE_SYSTEM_TABLE = ACC.ROLE_TABLE; // 0x18
    public static final int ROLE_SYSTEM_COLUMNHEADER = ACC.ROLE_TABLECOLUMNHEADER; // 0x19
    public static final int ROLE_SYSTEM_ROWHEADER = ACC.ROLE_TABLEROWHEADER; // 0x1a
    public static final int ROLE_SYSTEM_COLUMN = 0x1b;
    public static final int ROLE_SYSTEM_ROW = 0x1c;
    public static final int ROLE_SYSTEM_CELL = ACC.ROLE_TABLECELL; // 0x1d
    public static final int ROLE_SYSTEM_LINK = ACC.ROLE_LINK; // 0x1e
    public static final int ROLE_SYSTEM_HELPBALLOON = 0x1f;
    // Role 0x2*
    public static final int ROLE_SYSTEM_CHARACTER = 0x20;
    public static final int ROLE_SYSTEM_LIST = ACC.ROLE_LIST; // 0x21
    public static final int ROLE_SYSTEM_LISTITEM = ACC.ROLE_LISTITEM; // 0x22
    public static final int ROLE_SYSTEM_OUTLINE = ACC.ROLE_TREE; // 0x23
    public static final int ROLE_SYSTEM_OUTLINEITEM = ACC.ROLE_TREEITEM; // 0x24
    public static final int ROLE_SYSTEM_PAGETAB = ACC.ROLE_TABITEM; // 0x25
    public static final int ROLE_SYSTEM_PROPERTYPAGE = 0x26;
    public static final int ROLE_SYSTEM_INDICATOR = 0x27;
    public static final int ROLE_SYSTEM_GRAPHIC = 0x28;
    public static final int ROLE_SYSTEM_STATICTEXT = ACC.ROLE_LABEL; // 0x29
    public static final int ROLE_SYSTEM_TEXT = ACC.ROLE_TEXT; // 0x2a
    public static final int ROLE_SYSTEM_PUSHBUTTON = ACC.ROLE_PUSHBUTTON; // 0x2b
    public static final int ROLE_SYSTEM_CHECKBUTTON = ACC.ROLE_CHECKBUTTON; // 0x2c
    public static final int ROLE_SYSTEM_RADIOBUTTON = ACC.ROLE_RADIOBUTTON; // 0x2d
    public static final int ROLE_SYSTEM_COMBOBOX = ACC.ROLE_COMBOBOX; // 0x2e
    public static final int ROLE_SYSTEM_DROPLIST = 0x2f;
    // Role 0x3*
    public static final int ROLE_SYSTEM_PROGRESSBAR = ACC.ROLE_PROGRESSBAR; // 0x30
    public static final int ROLE_SYSTEM_DIAL = 0x31;
    public static final int ROLE_SYSTEM_HOTKEYFIELD = 0x32;
    public static final int ROLE_SYSTEM_SLIDER = ACC.ROLE_SLIDER; // 0x33
    public static final int ROLE_SYSTEM_SPINBUTTON = 0x34;
    public static final int ROLE_SYSTEM_DIAGRAM = 0x35;
    public static final int ROLE_SYSTEM_ANIMATION = 0x36;
    public static final int ROLE_SYSTEM_EQUATION = 0x37;
    public static final int ROLE_SYSTEM_BUTTONDROPDOWN = 0x38;
    public static final int ROLE_SYSTEM_BUTTONMENU = 0x39;
    public static final int ROLE_SYSTEM_BUTTONDROPDOWNGRID = 0x3a;
    public static final int ROLE_SYSTEM_WHITESPACE = 0x3b;
    public static final int ROLE_SYSTEM_PAGETABLIST = ACC.ROLE_TABFOLDER; // 0x3c
    public static final int ROLE_SYSTEM_CLOCK = 0x3d;
    public static final int ROLE_SYSTEM_SPLITBUTTON = 0x3e;
    public static final int ROLE_SYSTEM_IPADDRESS = 0x3f;
    // Role 0x4*
    public static final int ROLE_SYSTEM_OUTLINEBUTTON = 0x40;

    // State
    public static final int STATE_NORMAL = 0x00000000;
    public static final int STATE_SELECTED = 0x00000002;
    public static final int STATE_SELECTABLE = 0x00200000;
    public static final int STATE_MULTISELECTABLE = 0x1000000;
    public static final int STATE_FOCUSED = 0x00000004;
    public static final int STATE_FOCUSABLE = 0x00100000;
    public static final int STATE_PRESSED = 0x8;
    public static final int STATE_CHECKED = 0x10;
    public static final int STATE_EXPANDED = 0x200;
    public static final int STATE_COLLAPSED = 0x400;
    public static final int STATE_HOTTRACKED = 0x80;
    public static final int STATE_BUSY = 0x800;
    public static final int STATE_READONLY = 0x40;
    public static final int STATE_INVISIBLE = 0x8000;
    public static final int STATE_OFFSCREEN = 0x10000;
    public static final int STATE_SIZEABLE = 0x20000;
    public static final int STATE_LINKED = 0x400000;

    public static final int STATE_SYSTEM_UNAVAILABLE = 0x1;
//  public static final int STATE_SYSTEM_MIXED = 0x20;
//  public static final int STATE_SYSTEM_INDETERMINATE = STATE_SYSTEM_MIXED;
//  public static final int STATE_SYSTEM_DEFAULT = 0x100;
//  public static final int STATE_SYSTEM_FLOATING = 0x1000;
//  public static final int STATE_SYSTEM_MARQUEED = 0x2000;
//  public static final int STATE_SYSTEM_ANIMATED = 0x4000;
//  public static final int STATE_SYSTEM_MOVEABLE = 0x40000;
//  public static final int STATE_SYSTEM_SELFVOICING = 0x80000;
//  public static final int STATE_SYSTEM_TRAVERSED = 0x800000;
//  public static final int STATE_SYSTEM_EXTSELECTABLE = 0x2000000;
//  public static final int STATE_SYSTEM_ALERT_LOW = 0x4000000;
//  public static final int STATE_SYSTEM_ALERT_MEDIUM = 0x8000000;
//  public static final int STATE_SYSTEM_ALERT_HIGH = 0x10000000;
//  public static final int STATE_SYSTEM_PROTECTED = 0x20000000;
//  public static final int STATE_SYSTEM_VALID = 0x3fffffff;
    
    // Events
    public static final int EVENT_SYSTEM_SOUND             = 0x0001;
    public static final int EVENT_SYSTEM_ALERT             = 0x0002;
    public static final int EVENT_SYSTEM_FOREGROUND        = 0x0003;
    public static final int EVENT_SYSTEM_MENUSTART         = 0x0004;
    public static final int EVENT_SYSTEM_MENUEND           = 0x0005;
    public static final int EVENT_SYSTEM_MENUPOPUPSTART    = 0x0006;
    public static final int EVENT_SYSTEM_MENUPOPUPEND      = 0x0007;
    public static final int EVENT_SYSTEM_CAPTURESTART      = 0x0008;
    public static final int EVENT_SYSTEM_CAPTUREEND        = 0x0009;
    public static final int EVENT_SYSTEM_MOVESIZESTART     = 0x000A;
    public static final int EVENT_SYSTEM_MOVESIZEEND       = 0x000B;
    public static final int EVENT_SYSTEM_CONTEXTHELPSTART  = 0x000C;
    public static final int EVENT_SYSTEM_CONTEXTHELPEND    = 0x000D;
    public static final int EVENT_SYSTEM_DRAGDROPSTART     = 0x000E;
    public static final int EVENT_SYSTEM_DRAGDROPEND       = 0x000F;
    public static final int EVENT_SYSTEM_DIALOGSTART       = 0x0010;
    public static final int EVENT_SYSTEM_DIALOGEND         = 0x0011;
    public static final int EVENT_SYSTEM_SCROLLINGSTART    = 0x0012;
    public static final int EVENT_SYSTEM_SCROLLINGEND      = 0x0013;
    public static final int EVENT_SYSTEM_SWITCHSTART       = 0x0014;
    public static final int EVENT_SYSTEM_SWITCHEND         = 0x0015;
    public static final int EVENT_SYSTEM_MINIMIZESTART     = 0x0016;
    public static final int EVENT_SYSTEM_MINIMIZEEND       = 0x0017;
    public static final int EVENT_OBJECT_CREATE            = 0x8000; // hwnd + ID + idChild is created item
    public static final int EVENT_OBJECT_DESTROY           = 0x8001; // hwnd + ID + idChild is destroyed item
    public static final int EVENT_OBJECT_SHOW              = 0x8002; // hwnd + ID + idChild is shown item
    public static final int EVENT_OBJECT_HIDE              = 0x8003; // hwnd + ID + idChild is hidden item
    public static final int EVENT_OBJECT_REORDER           = 0x8004; // hwnd + ID + idChild is parent of zordering children
    public static final int EVENT_OBJECT_FOCUS             = 0x8005; // hwnd + ID + idChild is focused item
    public static final int EVENT_OBJECT_SELECTION         = 0x8006; // hwnd + ID + idChild is selected item (if only one), or idChild is OBJID_WINDOW if complex
    public static final int EVENT_OBJECT_SELECTIONADD      = 0x8007; // hwnd + ID + idChild is item added
    public static final int EVENT_OBJECT_SELECTIONREMOVE   = 0x8008; // hwnd + ID + idChild is item removed
    public static final int EVENT_OBJECT_SELECTIONWITHIN   = 0x8009; // hwnd + ID + idChild is parent of changed selected items
    public static final int EVENT_OBJECT_STATECHANGE       = 0x800A; // hwnd + ID + idChild is item w/ state change
    public static final int EVENT_OBJECT_LOCATIONCHANGE    = 0x800B; // hwnd + ID + idChild is moved/sized item
    public static final int EVENT_OBJECT_NAMECHANGE        = 0x800C; // hwnd + ID + idChild is item w/ name change
    public static final int EVENT_OBJECT_DESCRIPTIONCHANGE = 0x800D; // hwnd + ID + idChild is item w/ desc change
    public static final int EVENT_OBJECT_VALUECHANGE       = 0x800E; // hwnd + ID + idChild is item w/ value change
    public static final int EVENT_OBJECT_PARENTCHANGE      = 0x800F; // hwnd + ID + idChild is item w/ new parent
    public static final int EVENT_OBJECT_HELPCHANGE        = 0x8010; // hwnd + ID + idChild is item w/ help change
    public static final int EVENT_OBJECT_DEFACTIONCHANGE   = 0x8011; // hwnd + ID + idChild is item w/ def action change
    public static final int EVENT_OBJECT_ACCELERATORCHANGE = 0x8012; // hwnd + ID + idChild is item w/ keybd accel change
    
    // flagsSelect
    public static final int 
        SELFLAG_NONE            = 0,    // Performs no action.
        SELFLAG_TAKEFOCUS       = 1,    // Sets the focus to the object and makes it the selection anchor.
        SELFLAG_TAKESELECTION   = 2,    // Selects the object and removes the selection from all other objects in the container.
        SELFLAG_EXTENDSELECTION = 4,    // Alters the selection so that all objects between the selection anchor and this object take on the anchor object's selection state.
        SELFLAG_ADDSELECTION    = 8,    // Adds the object to the current selection.
        SELFLAG_REMOVESELECTION = 16;   // Removes the object from the current selection.

	public static long getAccessibleObjectFromPoint(Point point, int[] pChild) {
        NativeVariantAccess nva = new NativeVariantAccess();
        try {
            long pvObject = AccessibleObjectFromPoint(point.x,point.y,nva.getAddress());
            if( OLE.VT_I4 == nva.getType() ) {
                pChild[0] = nva.getInt();
            }
            return pvObject;
        }
        finally {
            nva.dispose();
        }
	}
	
	public static long getAccessibleObjectFromEvent(long hwnd, int dwId, int dwChildId, int[] pChild) {
        NativeVariantAccess nva = new NativeVariantAccess();
        try {
            long pvObject = AccessibleObjectFromEvent(hwnd, dwId, dwChildId, nva.getAddress());
            if( OLE.VT_I4 == nva.getType() ) {
                pChild[0] = nva.getInt();
            }
            return pvObject;
        }
        finally {
            nva.dispose();
        }
	}

	public static void getAccessibleChildren(long address, Variant[] pVarResult) {
		int resultLength = pVarResult.length;
        NativeVariantAccess nva = new NativeVariantAccess(resultLength);
        try {
            int count = MSAA.AccessibleChildren(address,0,resultLength,nva.getAddress());
            for( int i=0; i<count; i++ ) {
                switch( nva.getType(i) ) {
                    case OLE.VT_DISPATCH:
                        IDispatch childDispatch = nva.getDispatch(i);
                        childDispatch.AddRef();
                        pVarResult[i] =  new Variant(childDispatch);
                        break;
                    case OLE.VT_I4:
                        pVarResult[i] = new Variant(nva.getInt(i));
                        break;
                }
            }
        }
        finally {
            nva.dispose();
        }
	}
	
	public static long getWindowFromAccessibleObject(long address) {
		return WindowFromAccessibleObject(address);
	}

	public static String getRoleText(int role) {
		int size = GetRoleText(role, null, 0);
		if( 0 == size ) {
            return IA2.getRoleText(role);
		}
		TCHAR buffer = new TCHAR(0, size+1);
		int result = GetRoleText(role, buffer.chars, buffer.length());
		return buffer.toString(0,result);
	}

	public static String getStateText(int state) {
		int size = GetStateText(state, null, 0);
		if( 0 == size ) {
			return ""; //$NON-NLS-1$
		}
		TCHAR buffer = new TCHAR(0, size+1);
		int result = GetStateText(state, buffer.chars, buffer.length());
		return buffer.toString(0,result);
	}

	/** Accessibility natives */
	public static final native long AccessibleObjectFromPoint(int x, int y, long pvarChild);
	public static final native long AccessibleObjectFromWindow(long hwnd);
	public static final native int AccessibleChildren(long paccContainer, int iChildStart, int cChildren, long rgvarChildren);
	public static final native long WindowFromAccessibleObject(long pAcc);
	public static final native int GetRoleText(int lRole, char[] lpszRole, int cchRoleMax);
	public static final native int GetStateText(int lStateBit, char[] lpszState, int cchState);
	
	public static final native long AccessibleObjectFromEvent(long hwnd, int dwId, int dwChildId, long pvarChild);
	public static final native long SetWinEventHook(int eventMin, int eventMax, long hmodWinEventProc, long lpfnWinEventProc, int idProcess,int idThread,int dwFlags);
	public static final native long UnhookWinEvent(long hEvent);

    /** */
    public static final native long HTMLDocumentFromWindow(long hwnd);

    /** SPI Support **/
    private final static int SPI_GETSCREENREADER = 70;
    private final static int SPI_SETSCREENREADER = 71;
    private final static int SPIF_UPDATEINIFILE = 0x01;
    private final static int SPIF_SENDCHANGE = 0x02;
    private final static int HWND_BROADCAST = 0xffff;
    private final static int WM_WININICHANGE = 0x001A;
    
    public static void setScreenReader(boolean set) {
        if( OS.SystemParametersInfo( SPI_SETSCREENREADER, set ? 1:0, (int[])null, SPIF_UPDATEINIFILE | SPIF_SENDCHANGE ) ) {
            OS.PostMessage( HWND_BROADCAST, WM_WININICHANGE, SPI_SETSCREENREADER, 0 );
        }
    }
    
    public static boolean getScreenReader() {
    	int[] pResult = new int[1];
        if( OS.SystemParametersInfo( SPI_GETSCREENREADER, 0, pResult, 0 ) ) {
            return 0!=pResult[0];
        }
        return false;
    }

	public static String getEventTypeText(int event) {
			switch( event ) {
	            case EVENT_SYSTEM_SOUND: return"EVENT_SYSTEM_SOUND"; //$NON-NLS-1$
	            case EVENT_SYSTEM_ALERT: return"EVENT_SYSTEM_ALERT"; //$NON-NLS-1$
	            case EVENT_SYSTEM_FOREGROUND: return"EVENT_SYSTEM_FOREGROUND"; //$NON-NLS-1$
	            case EVENT_SYSTEM_CAPTURESTART: return "EVENT_SYSTEM_CAPTURESTART"; //$NON-NLS-1$
	            case EVENT_SYSTEM_CAPTUREEND: return "EVENT_SYSTEM_CAPTUREEND"; //$NON-NLS-1$
	            case EVENT_SYSTEM_MOVESIZESTART: return "EVENT_SYSTEM_MOVESIZESTART"; //$NON-NLS-1$
	            case EVENT_SYSTEM_MOVESIZEEND: return "EVENT_SYSTEM_MOVESIZEEND"; //$NON-NLS-1$
	            case EVENT_SYSTEM_CONTEXTHELPSTART: return "EVENT_SYSTEM_CONTEXTHELPSTART"; //$NON-NLS-1$
	            case EVENT_SYSTEM_CONTEXTHELPEND: return "EVENT_SYSTEM_CONTEXTHELPEND"; //$NON-NLS-1$
	            case EVENT_SYSTEM_DRAGDROPSTART: return "EVENT_SYSTEM_DRAGDROPSTART"; //$NON-NLS-1$
	            case EVENT_SYSTEM_DRAGDROPEND: return "EVENT_SYSTEM_DRAGDROPEND"; //$NON-NLS-1$
	            case EVENT_SYSTEM_DIALOGSTART: return "EVENT_SYSTEM_DIALOGSTART"; //$NON-NLS-1$
	            case EVENT_SYSTEM_DIALOGEND: return "EVENT_SYSTEM_DIALOGEND"; //$NON-NLS-1$
	            case EVENT_SYSTEM_SCROLLINGSTART: return "EVENT_SYSTEM_SCROLLINGSTART"; //$NON-NLS-1$
	            case EVENT_SYSTEM_SCROLLINGEND: return "EVENT_SYSTEM_SCROLLINGEND"; //$NON-NLS-1$
	            case EVENT_SYSTEM_SWITCHSTART: return "EVENT_SYSTEM_SWITCHSTART"; //$NON-NLS-1$
	            case EVENT_SYSTEM_SWITCHEND: return "EVENT_SYSTEM_SWITCHEND"; //$NON-NLS-1$
	            case EVENT_SYSTEM_MINIMIZESTART: return "EVENT_SYSTEM_MINIMIZESTART"; //$NON-NLS-1$
	            case EVENT_SYSTEM_MINIMIZEEND: return "EVENT_SYSTEM_MINIMIZEEND"; //$NON-NLS-1$
	
	            case EVENT_SYSTEM_MENUSTART: return "EVENT_SYSTEM_MENUSTART"; //$NON-NLS-1$
	            case EVENT_SYSTEM_MENUPOPUPSTART: return "EVENT_SYSTEM_MENUPOPUPSTART"; //$NON-NLS-1$
	            case EVENT_SYSTEM_MENUEND: return "EVENT_SYSTEM_MENUEND"; //$NON-NLS-1$
	            case EVENT_SYSTEM_MENUPOPUPEND: return "EVENT_SYSTEM_MENUPOPUPEND"; //$NON-NLS-1$
	
	            case EVENT_OBJECT_FOCUS: return "EVENT_OBJECT_FOCUS"; //$NON-NLS-1$
				case EVENT_OBJECT_STATECHANGE: return "EVENT_OBJECT_STATECHANGE"; //$NON-NLS-1$
				case EVENT_OBJECT_NAMECHANGE: return "EVENT_OBJECT_NAMECHANGE"; //$NON-NLS-1$
				case EVENT_OBJECT_DESCRIPTIONCHANGE: return "EVENT_OBJECT_DESCRIPTIONCHANGE"; //$NON-NLS-1$
				case EVENT_OBJECT_VALUECHANGE: return "EVENT_OBJECT_VALUECHANGE"; //$NON-NLS-1$
				case EVENT_OBJECT_CREATE: return "EVENT_OBJECT_CREATE"; //$NON-NLS-1$
				case EVENT_OBJECT_DESTROY: return "EVENT_OBJECT_DESTROY"; //$NON-NLS-1$
				case EVENT_OBJECT_SHOW: return "EVENT_OBJECT_SHOW"; //$NON-NLS-1$
				case EVENT_OBJECT_HIDE: return "EVENT_OBJECT_HIDE"; //$NON-NLS-1$
				case EVENT_OBJECT_REORDER: return "EVENT_OBJECT_REORDER"; //$NON-NLS-1$
				case EVENT_OBJECT_SELECTION: return "EVENT_OBJECT_SELECTION"; //$NON-NLS-1$
				case EVENT_OBJECT_SELECTIONADD: return "EVENT_OBJECT_SELECTIONADD"; //$NON-NLS-1$
				case EVENT_OBJECT_SELECTIONREMOVE: return "EVENT_OBJECT_SELECTIONREMOVE"; //$NON-NLS-1$
				case EVENT_OBJECT_SELECTIONWITHIN: return "EVENT_OBJECT_SELECTIONWITHIN"; //$NON-NLS-1$
				case EVENT_OBJECT_LOCATIONCHANGE: return "EVENT_OBJECT_LOCATIONCHANGE"; //$NON-NLS-1$
				case EVENT_OBJECT_PARENTCHANGE: return "EVENT_OBJECT_PARENTCHANGE"; //$NON-NLS-1$
				case EVENT_OBJECT_HELPCHANGE: return "EVENT_OBJECT_HELPCHANGE"; //$NON-NLS-1$
				case EVENT_OBJECT_DEFACTIONCHANGE: return "EVENT_OBJECT_DEFACTIONCHANGE"; //$NON-NLS-1$
				case EVENT_OBJECT_ACCELERATORCHANGE: return "EVENT_OBJECT_ACCELERATORCHANGE"; //$NON-NLS-1$
	            
	            case IA2.IA2_EVENT_ACTION_CHANGED: return "IA2_EVENT_ACTION_CHANGED"; //$NON-NLS-1$
	            case IA2.IA2_EVENT_ACTIVE_DECENDENT_CHANGED: return "IA2_EVENT_ACTIVE_DECENDENT_CHANGED"; //$NON-NLS-1$
	            case IA2.IA2_EVENT_DOCUMENT_ATTRIBUTE_CHANGED: return "IA2_EVENT_DOCUMENT_ATTRIBUTE_CHANGED"; //$NON-NLS-1$
	            case IA2.IA2_EVENT_DOCUMENT_CONTENT_CHANGED: return "IA2_EVENT_DOCUMENT_CONTENT_CHANGED"; //$NON-NLS-1$
	            case IA2.IA2_EVENT_DOCUMENT_LOAD_COMPLETE: return "IA2_EVENT_DOCUMENT_LOAD_COMPLETE"; //$NON-NLS-1$
	            case IA2.IA2_EVENT_DOCUMENT_LOAD_STOPPED: return "IA2_EVENT_DOCUMENT_LOAD_STOPPED"; //$NON-NLS-1$
	            case IA2.IA2_EVENT_DOCUMENT_RELOAD: return "IA2_EVENT_DOCUMENT_RELOAD"; //$NON-NLS-1$
	            case IA2.IA2_EVENT_HYPERLINK_END_INDEX_CHANGED: return "IA2_EVENT_HYPERLINK_END_INDEX_CHANGED"; //$NON-NLS-1$
	            case IA2.IA2_EVENT_HYPERLINK_NUMBER_OF_ANCHORS_CHANGED: return "IA2_EVENT_HYPERLINK_NUMBER_OF_ANCHORS_CHANGED"; //$NON-NLS-1$
	            case IA2.IA2_EVENT_HYPERLINK_SELECTED_LINK_CHANGED: return "IA2_EVENT_HYPERLINK_SELECTED_LINK_CHANGED"; //$NON-NLS-1$
	            case IA2.IA2_EVENT_HYPERTEXT_LINK_ACTIVATED: return "IA2_EVENT_HYPERTEXT_LINK_ACTIVATED"; //$NON-NLS-1$
	            case IA2.IA2_EVENT_HYPERTEXT_LINK_SELECTED: return "IA2_EVENT_HYPERTEXT_LINK_SELECTED"; //$NON-NLS-1$
	            case IA2.IA2_EVENT_HYPERLINK_START_INDEX_CHANGED: return "IA2_EVENT_HYPERLINK_START_INDEX_CHANGED"; //$NON-NLS-1$
	            case IA2.IA2_EVENT_HYPERTEXT_CHANGED: return "IA2_EVENT_HYPERTEXT_CHANGED"; //$NON-NLS-1$
	            case IA2.IA2_EVENT_HYPERTEXT_NLINKS_CHANGED: return "IA2_EVENT_HYPERTEXT_NLINKS_CHANGED"; //$NON-NLS-1$
	            case IA2.IA2_EVENT_OBJECT_ATTRIBUTE_CHANGED: return "IA2_EVENT_OBJECT_ATTRIBUTE_CHANGED"; //$NON-NLS-1$
	            case IA2.IA2_EVENT_PAGE_CHANGED: return "IA2_EVENT_PAGE_CHANGED"; //$NON-NLS-1$
	            case IA2.IA2_EVENT_SECTION_CHANGED: return "IA2_EVENT_SECTION_CHANGED"; //$NON-NLS-1$
	            case IA2.IA2_EVENT_TABLE_CAPTION_CHANGED: return "IA2_EVENT_TABLE_CAPTION_CHANGED"; //$NON-NLS-1$
	            case IA2.IA2_EVENT_TABLE_COLUMN_DESCRIPTION_CHANGED: return "IA2_EVENT_TABLE_COLUMN_DESCRIPTION_CHANGED"; //$NON-NLS-1$
	            case IA2.IA2_EVENT_TABLE_COLUMN_HEADER_CHANGED: return "IA2_EVENT_TABLE_COLUMN_HEADER_CHANGED"; //$NON-NLS-1$
	            case IA2.IA2_EVENT_TABLE_MODEL_CHANGED: return "IA2_EVENT_TABLE_MODEL_CHANGED"; //$NON-NLS-1$
	            case IA2.IA2_EVENT_TABLE_ROW_DESCRIPTION_CHANGED: return "IA2_EVENT_TABLE_ROW_DESCRIPTION_CHANGED"; //$NON-NLS-1$
	            case IA2.IA2_EVENT_TABLE_ROW_HEADER_CHANGED: return "IA2_EVENT_TABLE_ROW_HEADER_CHANGED"; //$NON-NLS-1$
	            case IA2.IA2_EVENT_TABLE_SUMMARY_CHANGED: return "IA2_EVENT_TABLE_SUMMARY_CHANGED"; //$NON-NLS-1$
	            case IA2.IA2_EVENT_TEXT_ATTRIBUTE_CHANGED: return "IA2_EVENT_TEXT_ATTRIBUTE_CHANGED"; //$NON-NLS-1$
	            case IA2.IA2_EVENT_TEXT_CARET_MOVED: return "IA2_EVENT_TEXT_CARET_MOVED"; //$NON-NLS-1$
	            case IA2.IA2_EVENT_TEXT_CHANGED: return "IA2_EVENT_TEXT_CHANGED"; //$NON-NLS-1$
	            case IA2.IA2_EVENT_TEXT_COLUMN_CHANGED: return "IA2_EVENT_TEXT_COLUMN_CHANGED"; //$NON-NLS-1$
	            case IA2.IA2_EVENT_TEXT_INSERTED: return "IA2_EVENT_TEXT_INSERTED"; //$NON-NLS-1$
	            case IA2.IA2_EVENT_TEXT_REMOVED: return "IA2_EVENT_TEXT_REMOVED"; //$NON-NLS-1$
	            case IA2.IA2_EVENT_TEXT_UPDATED: return "IA2_EVENT_TEXT_UPDATED"; //$NON-NLS-1$
	            case IA2.IA2_EVENT_TEXT_SELECTION_CHANGED: return "IA2_EVENT_TEXT_SELECTION_CHANGED"; //$NON-NLS-1$
	            case IA2.IA2_EVENT_VISIBLE_DATA_CHANGED: return "IA2_EVENT_VISIBLE_DATA_CHANGED"; //$NON-NLS-1$
			}
			return null;
	//		return "0x"+Integer.toHexString(event);
		}
}
