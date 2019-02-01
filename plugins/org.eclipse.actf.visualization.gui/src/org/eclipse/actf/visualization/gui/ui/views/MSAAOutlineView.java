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
package org.eclipse.actf.visualization.gui.ui.views;

import java.util.Stack;

import org.eclipse.actf.accservice.swtbridge.AccessibleObject;
import org.eclipse.actf.accservice.swtbridge.IA2;
import org.eclipse.actf.accservice.swtbridge.MSAA;
import org.eclipse.actf.accservice.swtbridge.ia2.Accessible2;
import org.eclipse.actf.util.win32.HighlightComposite;
import org.eclipse.actf.util.win32.OverlayLabel;
import org.eclipse.actf.visualization.gui.IGuiViewIDs;
import org.eclipse.actf.visualization.gui.internal.util.AccessiblePropertyUtil;
import org.eclipse.actf.visualization.gui.internal.util.GuiImages;
import org.eclipse.actf.visualization.gui.internal.util.Messages;
import org.eclipse.actf.visualization.gui.preferences.GuiPreferenceConstants;
import org.eclipse.actf.visualization.gui.preferences.GuiPreferenceManager;
import org.eclipse.actf.visualization.gui.ui.actions.HideHtmlAction;
import org.eclipse.actf.visualization.gui.ui.actions.RefreshRootAction;
import org.eclipse.actf.visualization.gui.ui.actions.ShowOffscreenAction;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ArmEvent;
import org.eclipse.swt.events.ArmListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;

public class MSAAOutlineView extends ViewPart implements IMSAAOutlineView {
	public static Color FLASH_COLOR = Display.getCurrent().getSystemColor(
			SWT.COLOR_CYAN);
	public static Color INVISIBLE_FLASH_COLOR = Display.getCurrent()
			.getSystemColor(SWT.COLOR_RED);

	private TreeViewer viewer;

	private Action expandAction;
	private Action expandAllAction;

	private Action collapseAllAction;

	private RefreshRootAction refreshAction;

	private HideHtmlAction hideHtmlAction;

	private ShowOffscreenAction showOffscreenAction;

	private Action runCheckerAction;

	private Action showLabelsAction;

	private boolean suppressLabelAdjust = false;

	private boolean ignoreSelection = false;

	/**
	 * The constructor.
	 */
	public MSAAOutlineView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL
				| SWT.V_SCROLL);
		viewer.setContentProvider(MSAATreeContentProvider.getDefault());
		viewer.setLabelProvider(new MSAATreeLabelProvider());
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				if (selection instanceof IStructuredSelection) {
					Object selectedObject = ((IStructuredSelection) selection)
							.getFirstElement();
					if (null != selectedObject) {
						if (!ignoreSelection) {
							MSAAViewRegistory.outlineSelected(selectedObject);
						}
						if (!suppressLabelAdjust
								&& showLabelsAction.isChecked()) {
							showOverlayLabels();
						}
					} else {
						HighlightComposite.show(false);
					}
				}
			}

		});
		viewer.addTreeListener(new ITreeViewerListener() {
			public void treeCollapsed(TreeExpansionEvent event) {
				refreshLabels();
			}

			public void treeExpanded(TreeExpansionEvent event) {
				refreshLabels();
			}
		});
		makeActions();
		hookContextMenu();
		contributeToActionBars();
		MSAAViewRegistory.refreshRootObject();
		Display.getDefault().asyncExec(new Runnable() {
			
			public void run() {
				MSAAViewRegistory.showView(IGuiViewIDs.ID_EVENTVIEW, false);
			}
		});
		
		HighlightComposite.initOverlayWindow();
	}

	private void refreshLabels() {
		if (showLabelsAction.isChecked()) {
			viewer.getControl().getDisplay().asyncExec(new Runnable() {
				public void run() {
					showOverlayLabels();
				}
			});
		}
	}

	public void refresh() {
		OverlayLabel.removeAll();
		AccessibleObject rootObject = MSAAViewRegistory.getRootObject();
		if (null != rootObject) {
			ignoreSelection = true;
			try {
				viewer.setInput(rootObject);
				AccessibleObject selection = MSAAViewRegistory
						.adjustSelection(rootObject);
				if (null != selection) {
					viewer.setSelection(new StructuredSelection(selection));
					if (selection.getChildCount() < 100) {
						viewer.setExpandedState(selection, true);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				ignoreSelection = false;
			}
		}
	}

	public void setSelection(Object element) {
		ISelection selection = StructuredSelection.EMPTY;
		if (element instanceof AccessibleObject) {
			Stack<AccessibleObject> stack = new Stack<AccessibleObject>();
			for (AccessibleObject accParent = (AccessibleObject) element; null != (accParent = accParent
					.getCachedParent())
					&& !viewer.getExpandedState(accParent); stack
					.push(accParent)) {
				;
			}
			while (!stack.isEmpty()) {
				viewer.setExpandedState(stack.pop(), true);
			}
			selection = new StructuredSelection(element);
		}
		viewer.setSelection(selection, true);
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				MSAAOutlineView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(showOffscreenAction);
		manager.add(new Separator());
		manager.add(hideHtmlAction);
		manager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				showOffscreenAction.adjust();
				hideHtmlAction.adjust();
			}
		});
		manager.add(new Separator());
		manager.add(showLabelsAction);
	}

	private void fillContextMenu(IMenuManager manager) {
		Object selected = getSelectedItem();
		if (null != selected) {
			manager.add(expandAction);
		}
		manager.add(new Separator());
		manager.add(runCheckerAction);
		manager.add(refreshAction);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(expandAllAction);
		manager.add(collapseAllAction);
		manager.add(refreshAction);
		manager.add(runCheckerAction);
		manager.add(showLabelsAction);
	}

	private void makeActions() {
		final Shell shell = this.getViewSite().getShell();

		expandAction = new Action(Messages.msaa_expand) { 
			public void run() {
				try {
					Object selected = getSelectedItem();
					if (null != selected) {
						viewer.expandToLevel(selected,
								AbstractTreeViewer.ALL_LEVELS);
					}
				} catch (Error e) {
					e.printStackTrace();
				}
			}
		};

		expandAllAction = new Action(Messages.msaa_expand_all) { 
			public void run() {
				try {
					viewer.expandAll();
				} catch (Error e) {
					e.printStackTrace();
				}
			}
		};
		expandAllAction.setToolTipText(Messages.msaa_expand_all); 
		expandAllAction.setImageDescriptor(GuiImages.IMAGE_EXPAND_ALL);

		collapseAllAction = new Action(Messages.msaa_collapse_all) { 
			public void run() {
				try {
					viewer.collapseAll();
				} catch (Error e) {
					e.printStackTrace();
				}
			}
		};
		collapseAllAction.setToolTipText(Messages.msaa_collapse_all);
		collapseAllAction.setImageDescriptor(GuiImages.IMAGE_COLLAPSE_ALL);

		refreshAction = new RefreshRootAction();

		hideHtmlAction = new HideHtmlAction();

		showOffscreenAction = new ShowOffscreenAction();

		runCheckerAction = new Action(Messages.msaa_checker) { 
			public void run() {
				IMSAAProblemsView problemsView = (IMSAAProblemsView) MSAAViewRegistory
						.showView(IGuiViewIDs.ID_REPORTVIEW, true);
				if (null != problemsView) {
					problemsView.refresh();
				}
			}
		};
		runCheckerAction.setToolTipText(Messages.msaa_checker_tip); 
		runCheckerAction.setImageDescriptor(GuiImages.IMAGE_CHECKER);

		showLabelsAction = new Action(
				Messages.msaa_show_tree, Action.AS_CHECK_BOX) { 
			public void run() {
				showOverlayLabels();
			}
		};
		showLabelsAction.setToolTipText(Messages.msaa_show_tree); 
		showLabelsAction.setImageDescriptor(GuiImages.IMAGE_OVERLAY);
		shell.addShellListener(new ShellAdapter() {
			public void shellActivated(ShellEvent e) {
				if (IGuiViewIDs.ID_OUTLINEVIEW
						.equals(OverlayLabel.getOwnerId())) {
					showOverlayLabels();
				}
			}

			public void shellDeactivated(ShellEvent e) {
				if (IGuiViewIDs.ID_OUTLINEVIEW
						.equals(OverlayLabel.getOwnerId())) {
					OverlayLabel.removeAll(false);
				}
			}
		});
		showLabelsAction.setEnabled(GuiPreferenceManager
				.getPreferenceBoolean(GuiPreferenceConstants.UseOverlayWindow));
		final IPreferenceStore store = GuiPreferenceManager
				.getPreferenceStore();
		store.addPropertyChangeListener(new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (GuiPreferenceConstants.UseOverlayWindow.equals(event
						.getProperty())) {
					showLabelsAction
							.setEnabled(GuiPreferenceManager
									.getPreferenceBoolean(GuiPreferenceConstants.UseOverlayWindow));
				}
			}
		});
	}

	private void showOverlayLabels() {
		OverlayLabel.removeAll();
		if (showLabelsAction.isChecked()) {
			new LabelOverlay().showLabels(viewer.getTree().getSelection());
			OverlayLabel.setOwnerId(IGuiViewIDs.ID_OUTLINEVIEW);
		}
	}

	private class LabelOverlay {
		int index = 0;

		TreeItem currentItem = null;

		public void showLabels(TreeItem[] items) {
			if (items.length == 1) {
				currentItem = items[0];
				TreeItem parent = currentItem.getParentItem();
				if (null != parent) {
					items = parent.getItems();
				}
			}
			renderLabels(items);
		}

		public void renderLabels(TreeItem[] items) {
			for (int i = 0; i < items.length; i++) {
				Object treeObject = items[i].getData();
				if (treeObject instanceof AccessibleObject) {
					Rectangle location = ((AccessibleObject) treeObject)
							.getAccLocation();
					if (null != location) {
						OverlayLabel label = OverlayLabel.create(treeObject);
						if (null != label) {
							if (items[i].equals(currentItem)) {
								label.setForeground(label.getDisplay()
										.getSystemColor(
												SWT.COLOR_LIST_SELECTION_TEXT));
								label.setBackground(label.getDisplay()
										.getSystemColor(
												SWT.COLOR_LIST_SELECTION));
							}
							MSAATreeLabelProvider labelProvider = (MSAATreeLabelProvider) viewer
									.getLabelProvider();
							label.setImage(labelProvider.getImage(treeObject));
							label.setText("" + index); //$NON-NLS-1$
							String text = "[" + index + "] " + labelProvider.getText(treeObject); //$NON-NLS-1$ //$NON-NLS-2$
							String[][] properties = AccessiblePropertyUtil
									.getPropertyStrings(treeObject);
							label.setTooltop(text, properties);
							label.pack();
							label.setLocation(location.x, location.y);
							index++;
							label.addMouseListener(new MouseAdapter() {
								public void mouseDown(MouseEvent e) {
									if (e.widget instanceof OverlayLabel) {
										OverlayLabel[] labels = OverlayLabel
												.getLabelsAtPosition(e.display
														.getCursorLocation());
										if (labels.length > 0) {
											showSelectionMenu(labels,
													(OverlayLabel) e.widget);
										} else {
											suppressLabelAdjust = true;
											viewer
													.setSelection(new StructuredSelection(
															((OverlayLabel) e.widget).associatedObject));
											suppressLabelAdjust = false;
										}
									}
								}
							});
							label
									.addMouseTrackListener(new MouseTrackAdapter() {
										public void mouseEnter(MouseEvent e) {
											if (e.widget instanceof OverlayLabel) {
												AccessibleObject object = (AccessibleObject) ((OverlayLabel) e.widget).associatedObject;
												if (null != object) {
													HighlightComposite
															.flashRectangle(object
																	.getAccLocation());
												}
											}
										}

										public void mouseExit(MouseEvent e) {
											HighlightComposite.show(false);
										}
									});
						}
					}
				}
				if (items[i].getExpanded()) {
					renderLabels(items[i].getItems());
				}
			}
		}
	}

	private void showSelectionMenu(OverlayLabel[] labels, OverlayLabel parent) {
		final Menu popupMenu = parent.createPopupMenu();
		MenuItem[] menuItems = new MenuItem[labels.length];
		for (int i = 0; i < labels.length; i++) {
			menuItems[i] = new MenuItem(popupMenu, SWT.PUSH);
			menuItems[i].setText(labels[i].getMenuText());
			menuItems[i].setImage(labels[i].getImage());
			menuItems[i].setData(labels[i].associatedObject);
			menuItems[i].addArmListener(new ArmListener() {
				public void widgetArmed(ArmEvent e) {
					Object data = ((MenuItem) e.getSource()).getData();
					if (data instanceof AccessibleObject) {
						HighlightComposite
								.flashRectangle(((AccessibleObject) data)
										.getAccLocation());
					}
				}
			});
			menuItems[i].addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					Object data = ((MenuItem) e.getSource()).getData();
					suppressLabelAdjust = true;
					viewer.setSelection(new StructuredSelection(data));
					suppressLabelAdjust = false;
				}
			});
		}
		popupMenu.setVisible(true);
	}

	private Object getSelectedItem() {
		IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();
		if (selection.size() == 1) {
			return selection.getFirstElement();
		}
		return null;
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	private class MSAATreeLabelProvider extends LabelProvider implements
			IColorProvider {

		private Color colorInvisible = Display.getCurrent().getSystemColor(
				SWT.COLOR_GRAY);

		public Image getImage(Object element) {
			String imageKey = null;// Images.ELEMENT;
			if (element instanceof AccessibleObject) {
				imageKey = GuiImages.roleIcon(((AccessibleObject) element)
						.getAccRole());
				if (null == imageKey) {
					Accessible2 ac2 = ((AccessibleObject) element)
							.getAccessible2();
					if (null != ac2) {
						imageKey = GuiImages.roleIcon("IA2_ROLE_UNKNOWN"); //$NON-NLS-1$
					}
				}
			}
			if (null == imageKey) {
				imageKey = GuiImages.roleIcon("ROLE_UNKNOWN"); //$NON-NLS-1$
			}
			return GuiImages.getImage(imageKey);
		}

		public String getText(Object element) {
			return getTreeText(element);
		}

		public Color getBackground(Object element) {
			if (element instanceof AccessibleObject) {
				return getFlashBackground((AccessibleObject) element);
			}
			return null;
		}

		public Color getForeground(Object element) {
			if (element instanceof AccessibleObject) {
				int accState = ((AccessibleObject) element).getAccState();
				if (0 != (accState & MSAA.STATE_INVISIBLE)) {
					return colorInvisible;
				}
			}
			return null;
		}

	}

	public static String getTreeText(Object element) {
		if (element instanceof AccessibleObject) {
			AccessibleObject accObject = (AccessibleObject) element;
			try {
				String accText = accObject.getAccName();
				if (null == accText || 0 == accText.length()) {
					switch (accObject.getAccRole()) {
					case IA2.IA2_ROLE_PARAGRAPH:
					case IA2.IA2_ROLE_HEADING:
						accText = accObject.getAccValue();
						break;
					}
				}
				if (null == accText || 0 == accText.length()) {
					accText = Messages.msaa_NAMELESS; 
					String roleText = accObject.getRoleText();
					if (null != roleText) {
						accText += " (" + roleText + ")"; //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
				return accText;
			} catch (Exception e) {
				return e.getMessage();
			}
		}
		return element.toString();
	}

	private Color getFlashBackground(AccessibleObject accObject) {
//		switch (accObject.getAccRole()) {
//		case MSAA.ROLE_SYSTEM_WINDOW:
//			if (FlashMSAAUtil.isFlash(accObject.getPtr())) {
//				return FLASH_COLOR;
//			}
//			break;
//		case MSAA.ROLE_SYSTEM_CLIENT:
//			if (FlashMSAAUtil.isInvisibleFlash(accObject.getPtr())) {
//				return INVISIBLE_FLASH_COLOR;
//			}
//			break;
//		}
		return null;
	}

}
