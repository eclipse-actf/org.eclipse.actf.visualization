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
package org.eclipse.actf.visualization.gui.msaa.checker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.actf.accservice.swtbridge.AccessibleObject;
import org.eclipse.actf.accservice.swtbridge.IA2;
import org.eclipse.actf.accservice.swtbridge.MSAA;
import org.eclipse.actf.visualization.gui.ui.views.MSAATreeContentProvider;

public class MSAAProblemChecker implements MSAAProblemConst {

	private AccessibleObject rootObject;

	private static final MSAATreeContentProvider provider = new MSAATreeContentProvider();

	public MSAAProblemChecker(AccessibleObject accObject) {
		this.rootObject = accObject;
	}

	public MSAAProblem[] getProblems() {
		provider.setShowOffscreen(true);
		provider.setHideHtml(MSAATreeContentProvider.getDefault().isHideHtml());
		return getProblem(provider.getElements(rootObject), false);
	}

	public MSAAProblem[] getProblem(Object[] children, boolean isFlash) {
		List<MSAAProblem> problems = new ArrayList<MSAAProblem>();

		if (children != null) {
			for (int i = 0; i < children.length; i++) {
				AccessibleObject ao = (AccessibleObject) children[i];
				if (ao == null)
					continue;
				if (0 != (ao.getAccState() & MSAA.STATE_INVISIBLE)
						&& 0 == (ao.getAccState() & MSAA.STATE_OFFSCREEN))
					continue;

				String accName = ao.getAccName();

				int role = ao.getAccRole();
				switch (role) {
				case MSAA.ROLE_SYSTEM_WINDOW:
//					if (FlashMSAAUtil.isFlash(ao.getPtr())) {
//						isFlash = true;
//					}
					break;
				case MSAA.ROLE_SYSTEM_CELL:
				case MSAA.ROLE_SYSTEM_STATICTEXT:
				case MSAA.ROLE_SYSTEM_SEPARATOR:
				case IA2.IA2_ROLE_PARAGRAPH:
				case MSAA.ROLE_SYSTEM_TEXT:
				case MSAA.ROLE_SYSTEM_TITLEBAR: // Ignore
				case MSAA.ROLE_SYSTEM_STATUSBAR:// Ignore
				case MSAA.ROLE_SYSTEM_GRIP: // Ignore
				case MSAA.ROLE_SYSTEM_TOOLBAR: // Ignore
				case MSAA.ROLE_SYSTEM_PAGETABLIST: // Ignore
				case MSAA.ROLE_SYSTEM_LIST: // Ignore
				case MSAA.ROLE_SYSTEM_OUTLINE: // Ignore
					break;
				case MSAA.ROLE_SYSTEM_CLIENT:
//					if (FlashMSAAUtil.isInvisibleFlash(ao.getPtr())) {
//						problems.add(new MSAAProblem(MSAA_ERROR,
//								MSAA_PROB_INVISIBLE_FLASH, ao));
//						isFlash = true;
//					}
					break;
				default: {
					if (null == accName || 0 == accName.length()) {
						if (role == MSAA.ROLE_SYSTEM_PUSHBUTTON)
							problems.add(new MSAAProblem(MSAA_ERROR,
									MSAA_PROB_NO_ALT_BUTTON, ao));
						else if (role == MSAA.ROLE_SYSTEM_GRAPHIC)
							if(isFlash){
								problems.add(new MSAAProblem(MSAA_ERROR,
										MSAA_PROB_NO_ALT_FLASH_IMAGE, ao));																
							}else{
								problems.add(new MSAAProblem(MSAA_WARNING,
										MSAA_PROB_NO_ALT_GRAHPIC, ao));								
							}
						else if (role == MSAA.ROLE_SYSTEM_COMBOBOX)
							problems.add(new MSAAProblem(MSAA_ERROR,
									MSAA_PROB_NO_ALT_FORM_CONTROL, ao));
						else if (role == MSAA.ROLE_SYSTEM_TABLE)
							problems.add(new MSAAProblem(MSAA_INFORMATION,
									MSAA_PROB_NO_TITLE_TABLE, ao));
						else if (role == MSAA.ROLE_SYSTEM_LINK) {
							String value = ao.getAccValue();
							if (null != value && value.length() > 0) {
								if (ao.getChildCount() == 0) {
									problems.add(new MSAAProblem(MSAA_ERROR,
											MSAA_PROB_NO_TEXT_LINK, ao));
								} else {
									AccessibleObject ao1 = ao.getChildren()[0];
									if (ao1.getAccRole() == MSAA.ROLE_SYSTEM_GRAPHIC
											&& (ao1.getAccName() == null || ao1
													.getAccName().length() == 0))
										problems.add(new MSAAProblem(
												MSAA_ERROR,
												MSAA_PROB_NO_TEXT_LINK, ao));
								}
							}
						} else /* if (role != MSAA.ROLE_TEXT) */{
							String defaultAction = ao.getAccDefaultAction();
							if (null == defaultAction
									|| 0 == defaultAction.length()) {
								if (role == MSAA.ROLE_SYSTEM_PANE)
									problems.add(new MSAAProblem(MSAA_WARNING,
											MSAA_PROB_NO_TITLE_FRAME, ao));
								else
									problems
											.add(new MSAAProblem(
													MSAA_WARNING,
													MSAA_PROB_NO_ALT_NONCLICKABLE_OBJECT,
													ao));
							} else {
								problems.add(new MSAAProblem(MSAA_ERROR,
										MSAA_PROB_NO_ALT_CLICKABLE_OBJECT, ao));
							}
						}
					}
				}
				}
				problems.addAll(Arrays.asList(getProblem(provider
						.getChildren(ao),isFlash)));
			}
		}
		return problems
				.toArray(new MSAAProblem[problems.size()]);
	}
}
