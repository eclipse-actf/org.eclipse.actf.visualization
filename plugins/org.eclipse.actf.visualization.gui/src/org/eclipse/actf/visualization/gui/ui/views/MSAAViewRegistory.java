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
package org.eclipse.actf.visualization.gui.ui.views;

import org.eclipse.actf.accservice.swtbridge.AccessibleObject;
import org.eclipse.actf.accservice.swtbridge.AccessibleObjectFactory;
import org.eclipse.actf.ui.util.PlatformUIUtil;
import org.eclipse.actf.util.win32.HighlightComposite;
import org.eclipse.actf.visualization.gui.IGuiViewIDs;
import org.eclipse.actf.visualization.gui.internal.util.TargetWindow;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.views.properties.PropertySheet;

public class MSAAViewRegistory implements IGuiViewIDs {

	public static AccessibleObject rootObject = null;
	public static AccessibleObject outlineObject = null;

	private static int updateRef = 0;

	private static final Display display = Display.getCurrent();
	private static long hwndRoot = 0;
	private static IPartListener2 partListener = new IPartListener2() {
		public void partActivated(IWorkbenchPartReference partRef) {
			if (partRef instanceof IEditorReference) {
				if (TargetWindow.isEmbeddedBrowser()) {
					final long hwnd = TargetWindow.getRootWindow();
					if (hwnd != hwndRoot) {
						display
								.timerExec(
										"Web Browser".equals(partRef.getPartName()) ? 3000 : 0, new Runnable() { //$NON-NLS-1$
											public void run() {
												if (!display.isDisposed()
														&& hwnd != hwndRoot) {
													refreshRootObject();
												}
											}
										});
					}
				}
			}
		}

		public void partBroughtToTop(IWorkbenchPartReference partRef) {
		}

		public void partClosed(IWorkbenchPartReference partRef) {
		}

		public void partDeactivated(IWorkbenchPartReference partRef) {
		}

		public void partHidden(IWorkbenchPartReference partRef) {
		}

		public void partInputChanged(IWorkbenchPartReference partRef) {
		}

		public void partOpened(IWorkbenchPartReference partRef) {
		}

		public void partVisible(IWorkbenchPartReference partRef) {
		}
	};
	private static boolean partListening = false;

	public static void outlineSelected(Object object) {
		addUpdateRef();
		try {
			object = adjustSelection(object);
			if (object instanceof AccessibleObject) {
				outlineObject = (AccessibleObject) object;
				showProperties(outlineObject);
				IMSAAListView listView = (IMSAAListView) findView(ID_SIBLINGSVIEW);
				if (null != listView) {
					listView.setSelection(outlineObject);
				}
				IJAWSTextView jawsTextView = (IJAWSTextView) findView(ID_SUMMARYVIEW);
				if (null != jawsTextView) {
					jawsTextView.setSelection(outlineObject);
				}
				HighlightComposite.flashRectangle(outlineObject
						.getAccLocation());
			}
		} finally {
			releaseUpdateRef();
		}
	}

	public static boolean showProperties(Object input) {
		boolean rc = false;
		IMSAAPropertiesView propertiesView = (IMSAAPropertiesView) findView(ID_PROPERTIESVIEW);
		if (null != propertiesView) {
			propertiesView.setInput(input);
			rc = true;
		}
		IViewPart propertySheet = findView("org.eclipse.ui.views.PropertySheet"); //$NON-NLS-1$
		if (propertySheet instanceof PropertySheet) {
			((PropertySheet) propertySheet).selectionChanged(null,
					new StructuredSelection(input));
			rc = true;
		}
		return rc;
	}

	public static IViewReference findViewReference(String viewId) {
		try {
			IWorkbenchPage page = PlatformUIUtil.getActivePage();
			if (null != page) {
				return page.findViewReference(viewId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static IViewPart findView(String viewId) {
		try {
			IViewReference ref = findViewReference(viewId);
			if (null != ref) {
				return ref.getView(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static IViewPart showView(String viewId, boolean activate) {
		try {
			IWorkbenchPage page = PlatformUIUtil.getActivePage();
			if (null != page) {
				IViewPart part = page.findView(viewId);
				if (null != part) {
					if (activate) {
						page.activate(part);
					}
					return part;
				}
				return page.showView(viewId, null,
						activate ? IWorkbenchPage.VIEW_ACTIVATE
								: IWorkbenchPage.VIEW_CREATE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void hideView(String viewId) {
		try {
			IWorkbenchPage page = PlatformUIUtil.getActivePage();
			if (null != page) {
				IViewPart part = page.findView(viewId);
				if (null != part) {
					page.hideView(part);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static AccessibleObject getRootObject() {
		if (null == rootObject
				&& 0 != (hwndRoot = TargetWindow.getRootWindow())) {
			rootObject = AccessibleObjectFactory
					.getAccessibleObjectFromWindow(hwndRoot);
		}
		if (!partListening) {
				IWorkbenchPage activePage = PlatformUIUtil.getActivePage();
				activePage.addPartListener(partListener);
				partListening = true;
		}
		return rootObject;
	}

	public static void refreshRootObject() {
		addUpdateRef();
		HighlightComposite.updateSuppressCount(1);
		try {
			if (null != rootObject) {
				rootObject.reset();
				rootObject = null;
			}
			IMSAAOutlineView outlineView = (IMSAAOutlineView) findView(ID_OUTLINEVIEW);
			if (null != outlineView) {
				outlineView.refresh();
			}
			IFlashDOMView flashDOMView = (IFlashDOMView) findView(ID_FLASHDOMVIEW);
			if (null != flashDOMView) {
				flashDOMView.refresh();
			}

			IMSAAProblemsView problemsView = (IMSAAProblemsView) findView(ID_REPORTVIEW);
			if (null != problemsView) {
				problemsView.refresh();
			}

			IJAWSTextView jawsTextView = (IJAWSTextView) findView(ID_SUMMARYVIEW);
			if (null != jawsTextView) {
				jawsTextView.refresh();
			}

			outlineSelected(null);
		} finally {
			HighlightComposite.updateSuppressCount(-1);
			releaseUpdateRef();
		}
	}

	public static AccessibleObject adjustSelection(Object selection) {
		getRootObject();
		if (selection instanceof AccessibleObject && selection != rootObject) {
			return (AccessibleObject) selection;
		}
		if (null != rootObject) {
			AccessibleObject rootChildlen[] = rootObject.getChildren();
			if (null != rootChildlen && rootChildlen.length > 0) {
				return rootChildlen[0];
			}
		}
		return rootObject;
	}

	public static int addUpdateRef() {
		return ++updateRef;
	}

	public static int releaseUpdateRef() {
		return --updateRef;
	}

	public static int getUpdateRef() {
		return updateRef;
	}
}
