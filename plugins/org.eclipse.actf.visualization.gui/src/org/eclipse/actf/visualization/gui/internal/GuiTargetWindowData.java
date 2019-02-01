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

package org.eclipse.actf.visualization.gui.internal;

import java.io.File;

import org.eclipse.actf.model.ui.IModelService;
import org.eclipse.actf.model.ui.IModelServiceHolder;
import org.eclipse.actf.model.ui.IModelServiceScrollManager;
import org.eclipse.actf.model.ui.ImagePositionInfo;
import org.eclipse.actf.util.win32.WindowUtil;
import org.eclipse.actf.visualization.gui.internal.util.Messages;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * 
 */
public class GuiTargetWindowData implements IModelService {

	private static final String categoryBrowser = Messages.msaa_external_browsers; 
	private static final String categoryWindow = Messages.msaa_external_windows; 
	private long hwnd;
	private boolean isBrowser;

	private class DummyModelServiceHolder implements IModelServiceHolder {

		private GuiTargetWindowData guiTarget;

		public DummyModelServiceHolder(GuiTargetWindowData guiTarget) {
			this.guiTarget = guiTarget;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.actf.model.IModelServiceHolder#getEditorPart()
		 */
		public IEditorPart getEditorPart() {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.actf.model.IModelServiceHolder#getModelService()
		 */
		public IModelService getModelService() {
			return guiTarget;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.actf.model.IModelServiceHolder#setTitle(java.lang.String)
		 */
		public void setEditorTitle(String title) {
			// do nothing
		}

	}

	private DummyModelServiceHolder holder;

	/**
	 * Constructor of the class.
	 * 
	 * @param hwnd Window handle of the target window
	 * @param isBrowser set true if the target window is a Web browser
	 */
	public GuiTargetWindowData(long hwnd, boolean isBrowser) {
		this.hwnd = hwnd;
		this.isBrowser = isBrowser;
		this.holder = new DummyModelServiceHolder(this);
	}

	/**
	 * Get category string for menu bar
	 * 
	 * @return category
	 */
	public String getCategory() {
		return isBrowser ? categoryBrowser : categoryWindow;
	}

	/**
	 * Check if this data source is Web browser
	 * 
	 * @return true if this data source is Web browser
	 */
	public boolean isBrowser() {
		return isBrowser;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.IModelService#getTitle()
	 */
	public String getTitle() {
		if (0 != hwnd) {
			String title = WindowUtil.GetWindowText(hwnd);
			if (title.length() == 0) {
				title = Messages.msaa_NAMELESS; 
			}
			return title + " (" + WindowUtil.GetWindowClassName(hwnd) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.IModelService#getID()
	 */
	public String getID() {
		return getCategory() + "/" + Long.toString(hwnd); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.IModelService#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String name) {
		if (IModelService.ATTR_WINDOWHANDLE.equals(name)) {
			return new Long(hwnd);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.IModelService#getCurrentMIMEType()
	 */
	public String getCurrentMIMEType() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.IModelService#getDocument()
	 */
	public Document getDocument() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.IModelService#getLiveDocument()
	 */
	public Document getLiveDocument() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.IModelService#getScrollManager()
	 */
	public IModelServiceScrollManager getScrollManager() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.IModelService#getSupportExtensions()
	 */
	public String[] getSupportExtensions() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.IModelService#getSupportMIMETypes()
	 */
	public String[] getSupportMIMETypes() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.IModelService#getTargetComposite()
	 */
	public Composite getTargetComposite() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.IModelService#getURL()
	 */
	public String getURL() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.IModelService#jumpToNode(org.w3c.dom.Node)
	 */
	public void jumpToNode(Node target) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.IModelService#open(java.io.File)
	 */
	public void open(File target) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.IModelService#open(java.lang.String)
	 */
	public void open(String url) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.IModelService#saveDocumentAsHTMLFile(java.lang.String)
	 */
	public File saveDocumentAsHTMLFile(String file) {
		return null;
	}

	public File saveOriginalDocument(String file) {
		return null;
	}

	public ImagePositionInfo[] getAllImagePosition() {
		return new ImagePositionInfo[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.IModelService#getModelServiceHolder()
	 */
	public IModelServiceHolder getModelServiceHolder() {
		return holder;
	}

}
