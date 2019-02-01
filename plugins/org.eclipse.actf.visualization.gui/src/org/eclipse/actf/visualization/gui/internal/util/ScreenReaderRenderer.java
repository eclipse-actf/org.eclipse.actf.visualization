/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Takashi ITOH - initial API and implementation
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.visualization.gui.internal.util;

import org.eclipse.actf.accservice.swtbridge.AccessibleObject;
import org.eclipse.actf.accservice.swtbridge.IA2;
import org.eclipse.actf.accservice.swtbridge.MSAA;
import org.eclipse.actf.accservice.swtbridge.ia2.Accessible2;
import org.eclipse.actf.accservice.swtbridge.ia2.IA2Util;
import org.eclipse.actf.visualization.gui.IGuiViewIDs;
import org.eclipse.actf.visualization.gui.ui.views.IFlashDOMView;
import org.eclipse.actf.visualization.gui.ui.views.MSAATreeContentProvider;
import org.eclipse.actf.visualization.gui.ui.views.MSAAViewRegistory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

import com.ibm.icu.text.MessageFormat;

public class ScreenReaderRenderer {
	private static final String SPACE = " "; //$NON-NLS-1$
	private long lastHwnd = 0;
	private String lastText = ""; //$NON-NLS-1$
	private StyledText text;
	private TextMap textMap;
	private static final MSAATreeContentProvider provider = MSAATreeContentProvider
			.getDefault();
	private static final Display display = Display.getCurrent();
	private static RenderThread renderThread = null;
	private static DisposeListener disposeListener = null;
	private static final int IDLE_WAIT = 5;
	private static final int BUSY_WAIT = 500;
	public static int waitMS = IDLE_WAIT;
	private IFlashDOMView flashDOMView = (IFlashDOMView) MSAAViewRegistory
			.findView(IGuiViewIDs.ID_FLASHDOMVIEW);

	private static final String[] BROWSER_CONTENT_CLASSNAMES = new String[] {
			"Internet Explorer_Server", //$NON-NLS-1$
			"MozillaWindowClass", //$NON-NLS-1$
			"MozillaContentWindowClass" //$NON-NLS-1$
	};

	private static boolean isBrowserContent(String className) {
		for (int i = 0; i < BROWSER_CONTENT_CLASSNAMES.length; i++) {
			if (BROWSER_CONTENT_CLASSNAMES[i].equals(className)) {
				return true;
			}
		}
		return false;
	}

	public ScreenReaderRenderer(StyledText text, TextMap textMap) {
		this.text = text;
		this.textMap = textMap;
		if (null == disposeListener) {
			disposeListener = new DisposeListener() {
				public void widgetDisposed(DisposeEvent e) {
					if (null != renderThread) {
						renderThread.cancel = true;
						renderThread.interrupt();
						renderThread = null;
						disposeListener = null;
					}
				}
			};
			text.addDisposeListener(disposeListener);
		}
	}

	public void renderAll(AccessibleObject object) {
		if (null != renderThread) {
			renderThread.cancel = true;
			renderThread.interrupt();
		}
		text.setText(""); //$NON-NLS-1$
		if (null != textMap) {
			textMap.clear();
		}
		AccessibleObject parent = object.getCachedParent();
		if (null != parent) {
			renderThread = new RenderThread(provider.getElements(parent));
			renderThread.start();
			Thread.yield();
		}
	}

	private class RenderThread extends Thread {

		private Object[] startElements;

		public boolean cancel = false;

		public RenderThread(Object[] startElements) {
			super();
			this.startElements = startElements;
		}

		public void run() {
			try {
				renderElements(startElements);
				display.syncExec(new Runnable() {
					public void run() {
						if (!(cancel || text.isDisposed() || text
								.getCharCount() > 0)) {
							appendText(
									provider.isHideHtml() ? Messages.msaa_no_flash
											: Messages.msaa_empty_page,
									SWT.COLOR_GRAY, -1, false); 
						}
					}
				});
			} catch (InterruptedException e) {
			}
		}

		private void renderElements(Object[] inputElements)
				throws InterruptedException {
			if (null == inputElements) {
				return;
			}
			for (int i = 0; i < inputElements.length; i++) {
				if (cancel) {
					throw new InterruptedException();
				}
				if (inputElements[i] instanceof AccessibleObject) {
					final int index = i;
					final boolean sayFlashEnd[] = new boolean[] { false };
					final Object[][] renderChildren = new Object[][] { null };
					final AccessibleObject accObject = (AccessibleObject) inputElements[i];
					display.syncExec(new Runnable() {
						public void run() {
							if (!(cancel || text.isDisposed())) {
//								long hwnd = accObject.getWindow();
//								if (hwnd != lastHwnd) {
//									if (FlashMSAAUtil.isFlash(accObject
//											.getPtr())) {
//										String wmode = null;
//										if (0 == hwnd) {
//											wmode = FlashMSAAUtil
//													.getHtmlAttribute(accObject
//															.getPtr(), "WMode"); //$NON-NLS-1$
//										}
//										if (null == wmode) {
//											AccessibleObject parentObject = accObject
//													.getCachedParent();
//											if (null != parentObject
//													&& hwnd != parentObject
//															.getWindow()) {
//												appendText(
//														Messages.msaa_flash_start
//																+ "\n", SWT.COLOR_GRAY, SWT.COLOR_CYAN, false); //$NON-NLS-1$ 
//												sayFlashEnd[0] = true;
//											}
//										} else {
//											appendText(
//													Messages.msaa_flash_inaccessible
//															+ " wmode=" + wmode + "\n", SWT.COLOR_GRAY, SWT.COLOR_RED, false); //$NON-NLS-1$ //$NON-NLS-2$ 
//											if (null != flashDOMView) {
//												flashDOMView
//														.addWindowlessElement(accObject);
//											}
//										}
//									}
//									lastHwnd = hwnd;
//								}
								renderItem(accObject, false, index);
								renderChildren[0] = provider
										.getChildren(accObject);
							}
						}
					});
					renderElements(renderChildren[0]);
					if (sayFlashEnd[0]) {
						display.syncExec(new Runnable() {
							public void run() {
								if (!(cancel || text.isDisposed())) {
									appendText(Messages.msaa_flash_end + "\n", //$NON-NLS-1$
											SWT.COLOR_GRAY, SWT.COLOR_CYAN,
											false);
								}
							}
						});
					}
					Thread.yield();
					Thread.sleep(waitMS);
					setBusy(false);
				}
			}
		}
	};

	public static void setBusy(boolean busy) {
		waitMS = busy ? BUSY_WAIT : IDLE_WAIT;
	}

	public String renderItem(AccessibleObject accObject, boolean selected,
			int parentIndex) {
		String accName = accObject.getAccName();
		String outText = null == accName ? "" : accName.replace('\u00A0', ' ').trim(); //$NON-NLS-1$
		int accState = accObject.getAccState();
		int accRole = accObject.getAccRole();
		boolean isFlash = false;//FlashMSAAUtil.isFlash(accObject.getPtr());
		// boolean isBrowser = WebBrowserUtil.isBrowser(accObject);
		boolean isBrowser = isBrowserContent(accObject.getClassName());
		String prefix = "", postfix = ""; //$NON-NLS-1$ //$NON-NLS-2$
		String defaultAction = accObject.getAccDefaultAction();
		boolean clickable = null != defaultAction && defaultAction.length() > 0;
		boolean visible = 0 == (accObject.getAccState() & MSAA.STATE_INVISIBLE);
		int foreground = visible ? (clickable ? SWT.COLOR_BLUE : -1)
				: SWT.COLOR_GRAY;
		int background = selected ? SWT.COLOR_CYAN : -1;
		switch (accRole) {
		case MSAA.ROLE_SYSTEM_TEXT: {
			boolean editable = (0 == (accState & MSAA.STATE_READONLY));
			if (!isBrowser || editable) {
				prefix = editable ? Messages.msaa_edit
						: Messages.msaa_edit_readonly;
				outText = accObject.getAccValue();
				if (null == outText || 0 == outText.length()) {
					outText = SPACE; 
				}
				break;
			}
		}
		case MSAA.ROLE_SYSTEM_STATICTEXT:
			if (outText.equals(lastText)) {
				outText = ""; //$NON-NLS-1$
			}
			break;
		case 0x28: // ROLE_SYSTEM_GRAPHIC
			if (isFlash) {
				prefix = Messages.msaa_graphic;
			} else {
				postfix = Messages.msaa_graphic;
			}
			if (outText.equals(lastText)) {
				outText = ""; //$NON-NLS-1$
			}
			break;
		case MSAA.ROLE_SYSTEM_LINK:
			prefix = Messages.msaa_link;
			break;
		case MSAA.ROLE_SYSTEM_CHECKBUTTON:
			prefix = Messages.msaa_checkbox + SPACE;
			prefix += 0 != (accState & MSAA.STATE_CHECKED) ? Messages.msaa_checked
					: Messages.msaa_not_checked;
			break;
		case MSAA.ROLE_SYSTEM_RADIOBUTTON:
			prefix = Messages.msaa_radiobutton + SPACE;
			prefix += 0 != (accState & MSAA.STATE_CHECKED) ? Messages.msaa_checked
					: Messages.msaa_not_checked;
			break;
		case MSAA.ROLE_SYSTEM_SLIDER:
			outText = accObject.getAccValue();
			postfix = Messages.msaa_updown_scrollbat;
			break;
		case MSAA.ROLE_SYSTEM_PROGRESSBAR:
			outText = accObject.getAccValue();
			postfix = Messages.msaa_progressbar;
			break;
		case 0x34: // ROLE_SYSTEM_SPINBUTTON
			outText = accObject.getAccValue();
			postfix = Messages.msaa_editspinbox;
			break;
		case MSAA.ROLE_SYSTEM_COMBOBOX:
			prefix = Messages.msaa_combobox;
			outText = accObject.getAccValue();
			if (null == outText || 0 == outText.length()) {
				outText = accObject.getAccName();
			}
			break;
		case 0x3E: // ROLE_SYSTEM_SPLITBUTTON
		case MSAA.ROLE_SYSTEM_BUTTONDROPDOWN:
		case MSAA.ROLE_SYSTEM_PUSHBUTTON:
			postfix = Messages.msaa_button;
			if (0 == outText.length()) {
				if (isFlash) {
					outText = Integer.toString(parentIndex);
					foreground = SWT.COLOR_RED;
				} else {
					outText = SPACE; 
				}
			}
			break;
		case MSAA.ROLE_SYSTEM_LISTITEM: {
			AccessibleObject parent = accObject.getCachedParent();
			if (null != parent) {
				switch (parent.getAccRole()) {
				case MSAA.ROLE_SYSTEM_LIST:
					postfix = Messages.msaa_listbox;
					if (0 != (accState & MSAA.STATE_SELECTED)) {
						postfix += SPACE + Messages.msaa_selected;
					}
					break;
				case MSAA.ROLE_SYSTEM_COMBOBOX:
					prefix = Messages.msaa_combobox;
					break;
				}
			}
		}
			break;
		case MSAA.ROLE_SYSTEM_TABLE:
			postfix = Messages.msaa_table;
			break;
		case MSAA.ROLE_SYSTEM_CELL:
			postfix = Messages.msaa_cell;
			break;
		case MSAA.ROLE_SYSTEM_COLUMNHEADER:
			postfix = Messages.msaa_columnheader;
			break;
		case MSAA.ROLE_SYSTEM_ROWHEADER:
			postfix = Messages.msaa_rowheader;
			break;
		case MSAA.ROLE_SYSTEM_PAGETAB:
			postfix = Messages.msaa_tab;
			break;
		case MSAA.ROLE_SYSTEM_OUTLINEITEM:
			postfix = Messages.msaa_treeview;
			if (0 != (accState & MSAA.STATE_SELECTED)) {
				postfix += SPACE + Messages.msaa_selected;
			} else if (isFlash) {
				outText = ""; // Ignore in flash //$NON-NLS-1$
			}
			break;
		case MSAA.ROLE_SYSTEM_OUTLINE:
			postfix = Messages.msaa_tree;
			break;

		case MSAA.ROLE_SYSTEM_MENUBAR:
			postfix = Messages.msaa_menubar;
			break;
		case MSAA.ROLE_SYSTEM_MENUPOPUP:
		case MSAA.ROLE_SYSTEM_MENUITEM:
			if (0 != (accState & MSAA.STATE_SYSTEM_UNAVAILABLE)) {
				postfix = Messages.msaa_unavailable;
				break;
			}
			if (accObject.getChildCount() > 0) {
				AccessibleObject parent = accObject.getCachedParent();
				if (null != parent
						&& MSAA.ROLE_SYSTEM_MENUITEM == parent.getAccRole()) {
					postfix = Messages.msaa_submenu;
				} else {
					postfix = Messages.msaa_menu;
				}
			} else if (0 != (accState & MSAA.STATE_CHECKED)) {
				postfix = Messages.msaa_checked;
			}
			break;
		case MSAA.ROLE_SYSTEM_TOOLBAR:
			postfix = Messages.msaa_toolbar;
			break;
		case 0x17: // ROLE_SYSTEM_STATUSBAR
			postfix = Messages.msaa_statusbar;
			break;
		case MSAA.ROLE_SYSTEM_SCROLLBAR:
			postfix = Messages.msaa_scrollbar;
			break;
		case 0x27: // ROLE_SYSTEM_INDICATOR
			postfix = Messages.msaa_indicator;
			break;
		case 0xe: // ROLE_SYSTEM_APPLICATION
			postfix = Messages.msaa_application;
			break;
		case 0xf: // ROLE_SYSTEM_DOCUMENT
			postfix = Messages.msaa_document;
			break;
		case MSAA.ROLE_SYSTEM_WINDOW:
		case 0x10: // ROLE_SYSTEM_PANE
			if (isFlash) {
				outText = ""; // Ignore in flash //$NON-NLS-1$
			} else {
				postfix = Messages.msaa_window;
			}
			break;
		case MSAA.ROLE_SYSTEM_CLIENT:
			if (isFlash || isBrowser || outText.equals(lastText)) {
				outText = ""; // Ignore in flash //$NON-NLS-1$
			}
			break;
		case IA2.IA2_ROLE_ROOT_PANE:
		case IA2.IA2_ROLE_OPTION_PANE:
			break; // TODO Do nothing?
		case IA2.IA2_ROLE_SHAPE: {
			String style = null;
			Accessible2 ac2 = accObject.getAccessible2();
			if (null != ac2) {
				style = IA2Util.getAttribute(ac2.getAttributes(), "style"); //$NON-NLS-1$
			}
			if (null != style) {
				postfix = MessageFormat.format(Messages.ia2_style_shape,
						new Object[] { style }); 
			} else {
				postfix += Messages.ia2_shape;
			}
		}
			break;
		case IA2.IA2_ROLE_CHECK_MENU_ITEM:
		case IA2.IA2_ROLE_RADIO_MENU_ITEM:
			if (0 != (accState & MSAA.STATE_SYSTEM_UNAVAILABLE)) {
				postfix = Messages.msaa_unavailable;
				break;
			}
			{
				Accessible2 ac2 = accObject.getAccessible2();
				if (null != ac2) {
					String extendedStates[] = ac2.getExtendedStates(8);
					if (IA2Util.getExtendedState(extendedStates, "CHECKED")) { //$NON-NLS-1$
						postfix = Messages.msaa_checked;
					}
				}
			}
			break;
		case IA2.IA2_ROLE_HEADING:
			String level = "?"; //$NON-NLS-1$
			try {
				Accessible2 ac2 = accObject.getAccessible2();
				if (null != ac2) {
					level = IA2Util.getAttribute(ac2.getAttributes(),
							"heading-level"); //$NON-NLS-1$
				}
			} catch (Exception e) {
			}
			prefix = MessageFormat.format(Messages.ia2_heading_level,
					new Object[] { level }); 
		case IA2.IA2_ROLE_PARAGRAPH:
			outText = accObject.getAccValue();
			if (null == outText || 0 == outText.length()) {
				outText = accObject.getAccName();
			}
			break;
		default:
			prefix = "[" + accObject.getRoleText() + " 0x" + Integer.toHexString(accRole) + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			break;
		}
		String speakText = ""; //$NON-NLS-1$
		Point point = new Point(text.getCharCount(), 0);
		if (null != outText && outText.length() > 0) {
			if (SPACE.equals(outText)) { 
				outText = ""; //$NON-NLS-1$
			}
			if (prefix.length() > 0) {
				if (outText.length() > 0) {
					prefix += SPACE; 
				}
				appendText(prefix, SWT.COLOR_GRAY, background, false);
				speakText += prefix;
			}
			if (outText.length() > 0) {
				appendText(outText, foreground, background, clickable);
				speakText += outText;
			}
			if (postfix.length() > 0) {
				if (speakText.length() > 0) {
					postfix = SPACE + postfix; 
				}
				appendText(postfix, SWT.COLOR_GRAY, background, false);
				speakText += postfix;
			}
			text.append("\n"); //$NON-NLS-1$
			lastText = outText;
		}
		point.y = text.getCharCount();
		if (null != textMap) {
			textMap.put(accObject, point);
		}
		return speakText;
	}

	public String renderEvent(int event) {
		String eventText = ""; //$NON-NLS-1$
		switch (event) {
		case MSAA.EVENT_SYSTEM_MENUSTART:
			eventText = Messages.msaa_menu_start;
			break;
		case MSAA.EVENT_SYSTEM_MENUPOPUPSTART:
			eventText = Messages.msaa_popup_start;
			break;
		case MSAA.EVENT_SYSTEM_MENUEND:
			eventText = Messages.msaa_menu_end;
			break;
		case MSAA.EVENT_SYSTEM_MENUPOPUPEND:
			eventText = Messages.msaa_popup_end;
			break;
		}
		if (eventText.length() > 0) {
			appendText(eventText, SWT.COLOR_GRAY, -1, false); 
			text.append("\n"); //$NON-NLS-1$
			lastText = eventText;
		}
		return eventText;
	}

	private void appendText(String str, int foreground, int background,
			boolean underline) {
		StyleRange range = new StyleRange();
		if (foreground >= 0) {
			range.foreground = Display.getCurrent().getSystemColor(foreground);
		}
		if (background >= 0) {
			range.background = Display.getCurrent().getSystemColor(background);
		}
		range.underline = underline;
		range.start = text.getCharCount();
		range.length = str.length();
		text.append(str);
		text.setStyleRange(range);
	}
}
