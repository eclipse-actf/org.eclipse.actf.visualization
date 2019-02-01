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
package org.eclipse.actf.visualization.gui.msaa.properties;

import org.eclipse.actf.accservice.swtbridge.AccessibleObject;
import org.eclipse.actf.accservice.swtbridge.MSAA;
import org.eclipse.actf.visualization.gui.msaa.properties.methods.ia1.MsaaDoDefaultActionMethod;
import org.eclipse.actf.visualization.gui.msaa.properties.methods.ia1.MsaaSelectMethod;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;


public class Accessible1PropertySource extends AbstractPropertyInvokeSource implements IPropertySource {

	private AccessibleObject accObject;

	private static final String
		PID_HANDLE = "HANDLE", //$NON-NLS-1$
		PID_PARENT = "PARENT", //$NON-NLS-1$
		PID_NAME = "NAME", //$NON-NLS-1$
		PID_ROLE = "ROLE", //$NON-NLS-1$
		PID_STATE = "STATE", //$NON-NLS-1$
		PID_VALUE = "VALUE", //$NON-NLS-1$
		PID_DESCRIPTION = "DESCRIPTION", //$NON-NLS-1$
		PID_CHILDREN = "CHILDREN", //$NON-NLS-1$
		PID_DEFAULTACTION = "DEFAULTACTION", //$NON-NLS-1$
		PID_LOCATION = "LOCATION", //$NON-NLS-1$
		PID_KEYBOARDSHORTCUT = "KEYBOARDSHORTCUT", //$NON-NLS-1$
		PID_HELP ="HELP", //$NON-NLS-1$
		PID_HELPTOPIC = "HELPTOPIC", //$NON-NLS-1$
		PID_DO_DEFAULTACTION ="DO_DEFAULTACTION", //$NON-NLS-1$
		PID_SELECT="SELECT"; //$NON-NLS-1$
	
	private static final IPropertyDescriptor[] descriptors = new IPropertyDescriptor[] 
	{
        new PropertyDescriptor(PID_HANDLE,"Window Handle"),    		//$NON-NLS-1$
        new PropertyDescriptor(PID_PARENT,"Parent"),           		//$NON-NLS-1$
        new PropertyDescriptor(PID_NAME,"Name"),             		//$NON-NLS-1$
        new PropertyDescriptor(PID_ROLE,"Role"),             		//$NON-NLS-1$
        new PropertyDescriptor(PID_STATE,"State"),            		//$NON-NLS-1$
        new PropertyDescriptor(PID_VALUE,"Value"),            		//$NON-NLS-1$
        new PropertyDescriptor(PID_DESCRIPTION,"Description"),      //$NON-NLS-1$
        new PropertyDescriptor(PID_CHILDREN,"Children"),         	//$NON-NLS-1$
        new PropertyDescriptor(PID_DEFAULTACTION,"DefaultAction"), 	//$NON-NLS-1$
        new PropertyDescriptor(PID_LOCATION,"Location"),         	//$NON-NLS-1$
        new PropertyDescriptor(PID_KEYBOARDSHORTCUT,"Keyboard Shortcut"),//$NON-NLS-1$
        new PropertyDescriptor(PID_HELP,"Help"),             		//$NON-NLS-1$
        new PropertyDescriptor(PID_HELPTOPIC,"Help Topic")        	//$NON-NLS-1$
	};
	private static final IPropertyDescriptor[] descriptorsEx = new IPropertyDescriptor[] 
 	{
        new PropertyDescriptor(PID_DO_DEFAULTACTION,"accDoDefaultAction"),        	//$NON-NLS-1$
        new PropertyDescriptor(PID_SELECT,"accSelect")        	//$NON-NLS-1$
 	};
	
	public Accessible1PropertySource(AccessibleObject input) {
		this.accObject = input;
		addMethodData(PID_SELECT, new MsaaSelectMethod(accObject));
		addMethodData(PID_DO_DEFAULTACTION, new MsaaDoDefaultActionMethod(accObject));
	}

	public Object getEditableValue() {
		return null;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}
	
	public IPropertyDescriptor[] getPropertyDescriptorsExtra() {
		return descriptorsEx;
	}

	public Object getPropertyValue(Object id) {
		Object result = super.getPropertyValue(id);
		if( null != result ) {
			return result;
		}	
		String strValue = null;
		if( PID_HANDLE.equals(id) ) {
            strValue = "0x"+Long.toHexString(accObject.getWindow()); //$NON-NLS-1$
            String wndClass = accObject.getClassName();
            if (null!=wndClass && wndClass.length() > 0) {
                strValue += " [" + wndClass + "]"; //$NON-NLS-1$ //$NON-NLS-2$
            }
		}
		else if( PID_PARENT.equals(id) ) {
            AccessibleObject pa = accObject.getCachedParent();
            if (null != pa) {
                strValue = pa.getAccName();
            }
		}
		else if( PID_NAME.equals(id) ) {
            strValue = accObject.getAccName();
		}
		else if( PID_ROLE.equals(id) ) {
            int accRole = accObject.getRealAccRole();
            strValue = accObject.getRealRoleText();
            if( null != strValue ) {
                strValue += " (0x"+Integer.toHexString(accRole)+")"; //$NON-NLS-1$ //$NON-NLS-2$
            }
		}
		else if( PID_STATE.equals(id) ) {
            int accState = accObject.getAccState();
            strValue = MSAA.getStateText(accState);
            if( null != strValue ) {
                strValue += " (0x"+Integer.toHexString(accState)+")"; //$NON-NLS-1$ //$NON-NLS-2$
            }
		}
		else if( PID_VALUE.equals(id) ) {
            strValue = accObject.getAccValue();
		}
		else if( PID_DESCRIPTION.equals(id) ) {
            strValue = accObject.getAccDescription();
		}
		else if( PID_CHILDREN.equals(id) ) {
            strValue = Integer.toString(accObject.getChildCount());
		}
		else if( PID_DEFAULTACTION.equals(id) ) {
            strValue = accObject.getAccDefaultAction();
		}
		else if( PID_LOCATION.equals(id) ) {
            Rectangle rect = accObject.getAccLocation();
            if( null!=rect ) {
                strValue = rect.x + ", " + rect.y + ", " + (rect.x + rect.width) + ", " + (rect.y + rect.height) + " (width="+rect.width+", height="+rect.height+")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
            }
		}
		else if( PID_KEYBOARDSHORTCUT.equals(id) ) {
            strValue = accObject.getAccKeyboardShortcut();
		}
		else if( PID_HELP.equals(id) ) {
            strValue = accObject.getAccHelp();
		}
		else if( PID_HELPTOPIC.equals(id) ) {
            int[] pIndex = new int[1];
            String[] pFile = new String[1];
            if (accObject.getAccHelpTopic(pIndex, pFile)) {
                strValue = "Help Topic: Topic #" + pIndex[0]; //$NON-NLS-1$
                if (null != pFile[0]) {
                    strValue += " in " + pFile[0]; //$NON-NLS-1$
                }
            }
		}
		return null==strValue ? "null" : strValue; //$NON-NLS-1$
	}

	public boolean isPropertySet(Object id) {
		return false;
	}

	public void resetPropertyValue(Object id) {
	}

	public void setPropertyValue(Object id, Object value) {
	}
}
