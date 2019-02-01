/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Takashi ITOH - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.visualization.gui.ui.actions;

import java.util.Comparator;

import org.eclipse.actf.util.win32.HighlightComposite;
import org.eclipse.actf.util.win32.WindowUtil;
import org.eclipse.actf.visualization.gui.internal.util.Messages;
import org.eclipse.actf.visualization.gui.internal.util.TargetWindow;
import org.eclipse.actf.visualization.gui.internal.util.TargetWindowDataCollector;
import org.eclipse.actf.visualization.gui.preferences.GuiPreferenceConstants;
import org.eclipse.actf.visualization.gui.preferences.GuiPreferenceManager;
import org.eclipse.actf.visualization.gui.ui.views.MSAAViewRegistory;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ArmEvent;
import org.eclipse.swt.events.ArmListener;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate2;
import org.eclipse.ui.PlatformUI;

import com.ibm.icu.text.Collator;

public class WindowListAction implements IWorkbenchWindowPulldownDelegate2 {

	private boolean switchPerspective = true;

	private IWorkbenchWindow window;

	public void run(IAction action) {
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
		HighlightComposite.initOverlayWindow();
	}

	public Menu getMenu(Control parent) {
		return null;
	}

	public Menu getMenu(Menu parent) {
		Menu menu = new Menu(parent);
		menu.addMenuListener(menuListener);
		return menu;
	}

	private MenuListener menuListener = new MenuAdapter() {
		public void menuShown(MenuEvent e) {
			Menu menu = (Menu) e.getSource();
			MenuItem[] items = menu.getItems();
			for (int i = 0; i < items.length; i++) {
				items[i].dispose();
			}
			String defaultID = TargetWindow.getID();
			boolean hasEmbedBrowser = false;
			TargetWindowDataCollector collector = new TargetWindowDataCollector();
			String[] categories = collector
					.getCategories(new CategoryComparator());
			for (int c = 0; c < categories.length; c++) {
				String category = categories[c];
				Menu submenu = menu;
				if (null != category) {
					MenuItem item = new MenuItem(menu, SWT.CASCADE);
					item.setText(category);
					submenu = new Menu(item);
					item.setMenu(submenu);
				}
				Object[] elements = collector.getElements(category);
				for (int i = 0; i < elements.length; i++) {
					Object element = elements[i];
					String title = TargetWindow.getTitle(element);
					if (null != title) {
						hasEmbedBrowser |= TargetWindow
								.isEmbeddedBrowser(element);
						MenuItem item = new MenuItem(submenu, SWT.RADIO);
						item.setText(title);
						item.setData(element);
						if (TargetWindow.getID(element).equals(defaultID)) {
							item.setSelection(true);
						}
						item.addArmListener(armListener);
						item.addSelectionListener(selectionListener);
					}
				}
			}
			if (categories.length > 0) {
				new MenuItem(menu, SWT.SEPARATOR);
			}
			final MenuItem bringTopItem = new MenuItem(menu, SWT.CHECK);
			bringTopItem.setText(Messages.msaa_bringToTop); 
			bringTopItem.setSelection(GuiPreferenceManager
					.getPreferenceBoolean(GuiPreferenceConstants.AlwaysOnTop));
			if (TargetWindow.isEmbeddedBrowser()) {
				bringTopItem.setEnabled(false);
			}
			bringTopItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					GuiPreferenceManager.setPreference(
							GuiPreferenceConstants.AlwaysOnTop, bringTopItem
									.getSelection());
				}
			});
			if (hasEmbedBrowser) {
				final MenuItem switchItem = new MenuItem(menu, SWT.CHECK);
				switchItem
						.setText(Messages.msaa_switchPerspective); 
				switchItem.setSelection(switchPerspective);
				switchItem.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						switchPerspective = switchItem.getSelection();
						if (!switchPerspective) {
							showInternalViewer(true);
						}
					}
				});
			}
		}
	};

	private ArmListener armListener = new ArmListener() {
		public void widgetArmed(ArmEvent e) {
			Object element = ((MenuItem) e.getSource()).getData();
			if (null != element) {
				long hwnd = TargetWindow.getRootWindow(element);
				HighlightComposite.flashRectangle(WindowUtil
						.GetWindowRectangle(hwnd));
			}
		}
	};

	private SelectionListener selectionListener = new SelectionAdapter() {
		public void widgetSelected(SelectionEvent event) {
			TargetWindow.setCurrentElement(((MenuItem) event.getSource())
					.getData());
			long hwnd = TargetWindow.getWindowHandle();
			boolean isEmbedded = TargetWindow.isEmbeddedBrowser();
			try {
				if (setWindowOrder(GuiPreferenceManager
						.getPreferenceBoolean(GuiPreferenceConstants.AlwaysOnTop))) {
					WindowUtil.BringWindowToTop(hwnd);
					WindowUtil.BringWindowToTop(hwnd);
				}
				if (switchPerspective) {
					showInternalViewer(isEmbedded);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			MSAAViewRegistory.refreshRootObject();
		}
	};

	private void showInternalViewer(boolean bShow) {
		try {
			window.getActivePage().setEditorAreaVisible(bShow);
		} catch (Exception e) {
		}
	}

	public static boolean setWindowOrder(boolean topMost) {
		if (topMost && TargetWindow.isEmbeddedBrowser()) {
			topMost = false;
		}
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		WindowUtil.SetWindowPos(window.getShell().handle,
				topMost ? WindowUtil.HWND_TOPMOST : WindowUtil.HWND_NOTOPMOST,
				0, 0, 0, 0, WindowUtil.SWP_NOSIZE | WindowUtil.SWP_NOMOVE);
		return topMost;
	}

	private static final String CATEGORY_BROWSER = Messages.msaa_external_browsers; 
	private static final String CATEGORY_WINDOW = Messages.msaa_external_windows; 

	private class CategoryComparator implements Comparator<Object> {
		private Collator collator = Collator.getInstance();

		public int compare(Object o1, Object o2) {
			int w = getWeight(o1);
			int rc = w - getWeight(o2);
			return (0 == rc && 0 == w) ? collator.compare(o1, o2) : rc;
		}

		public int getWeight(Object o) {
			if (null == o) {
				return -1;
			}
			if (CATEGORY_BROWSER.equals(o)) {
				return 1;
			}
			if (CATEGORY_WINDOW.equals(o)) {
				return 2;
			}
			return 0;
		}
	}
}
