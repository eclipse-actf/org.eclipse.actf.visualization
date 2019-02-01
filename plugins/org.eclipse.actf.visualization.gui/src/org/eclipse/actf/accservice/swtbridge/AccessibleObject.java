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
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.graphics.Rectangle;


public interface AccessibleObject extends IAdaptable {

    /**
     * Reset cached resources
     */
    public void reset();

    /**
     * Free allocated resources
     * 
     * @throws Exception
     */
    public void dispose() throws Exception;

    /**
     * Get cached parent object
     * 
     * @return
     */
    public AccessibleObject getCachedParent();

    /**
     * Get cached children objects
     * 
     * @return
     */
    public AccessibleObject[] getCachedChildren();

    /**
     * Get child objects
     * 
     * @return
     */
    public AccessibleObject[] getChildren();

    /**
     * Get number of children
     * 
     * @return
     */
    public int getChildCount();

    /**
     * Get low level window handle
     * 
     * @return window handle
     */
    public long getWindow();

    /**
     * Get accRole value
     * 
     * @return
     */
    public int getAccRole();

    /**
     * Get accRole text
     * 
     * @return role text
     */
    public String getRoleText();

    /**
     * Get window classname
     * 
     * @return classname
     */
    public String getClassName();

    /**
     * Get accState value
     * 
     * @return acc state bits
     */
    public int getAccState();

    /**
     * Get accName string
     * 
     * @return
     */
    public String getAccName();

    /**
     * Get accValue
     * 
     * @return accValue string
     */
    public String getAccValue();

    /**
     * Get accDescription string
     * 
     * @return accDescription string
     */
    public String getAccDescription();

    /**
     * Get accHelp string
     * 
     * @return
     */
    public String getAccHelp();

    /**
     * Get accKeyboardShortcut string
     * 
     * @return
     */
    public String getAccKeyboardShortcut();

    /**
     * Get accDefaultAction string
     * 
     * @return
     */
    public String getAccDefaultAction();

    /**
     * Get accHelpTopic
     * 
     * @param pTopicIndex
     * @param pHelpFile
     * @return
     */
    public boolean getAccHelpTopic(int[] pTopicIndex, String[] pHelpFile);

    /**
     * Get accLocation
     * 
     * @return
     */
    public Rectangle getAccLocation();

    /**
     * Performs default action
     * 
     * @return
     */
    public boolean doDefaultAction();

    /**
     * @param flagsSelect
     * @return
     */
    public boolean select(int flagsSelect);

    /**
     * @param strName
     * @return
     * @deprecated no longer supported 
     */
    public boolean setAccName(String strName);

    /**
     * @param strValue
     * @return
     * @deprecated no longer supported
     */
    public boolean setAccValue(String strValue);

    /**
     * Get parent accessible object. <BR>
     * Use getParent for cached parent.
     * 
     * @return
     */
    public AccessibleObject getAccParent();

    /**
     * Get IDispatch for the object 
     * @return
     */
    public IAccessible getIAccessible();

    /**
     * Get Accessible2 interface
     * @return
     */
    public Accessible2 getAccessible2();

    /**
     * Get AccessibleAction interface
     * @return
     */
    public AccessibleAction getAccessibleAction();

    /**
     * Get AccessibleApplication interface
     * @return
     */
    public AccessibleApplication getAccessibleApplication();

    /**
     * Get AccessibleComponent interface
     * @return
     */
    public AccessibleComponent getAccessibleComponent();

    /**
     * Get AccessibleText interface
     * @return
     */
    public AccessibleEditableText getAccessibleEditableText();

    /**
     * Get AccessibleHyperlink interface
     * @return
     */
    public AccessibleHyperlink getAccessibleHyperlink();

    /**
     * Get AccessibleHypertext interface
     * @return
     */
    public AccessibleHypertext getAccessibleHypertext();

    /**
     * Get AccessibleImage interface
     * @return
     */
    public AccessibleImage getAccessibleImage();

    /**
     * Get AccessibleTable interface
     * @return
     */
    public AccessibleTable getAccessibleTable();

    /**
     * Get AccessibleText interface
     * @return
     */
    public AccessibleText getAccessibleText();

    /**
     * Get AccessibleValue interface
     * @return
     */
    public AccessibleValue getAccessibleValue();

    /**
     * Get String for debug
     * @return
     */
    public String toString();

    /**
     * Get IA1 accRole value
     * 
     * @return
     */
    public int getRealAccRole();

    /**
     * Get IA1 accRole text
     * 
     * @return role text
     */
    public String getRealRoleText();
    
    public long getPtr();
}
