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
package org.eclipse.actf.model.flash;

import java.net.URLDecoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.actf.util.as.ASObject;
import org.eclipse.actf.visualization.flash.Messages;
import org.eclipse.actf.visualization.gui.flash.FlashFinder;


public class FlashNode {
	
	private FlashPlayer player;
	private ASObject asObject;
	private FlashNode parent;
	private int level;
    private FlashAccInfo accInfo;
	private boolean isReference = false;
	private boolean skipChildren = false;
	private boolean isAccProperties = false;
	
	private String strType;
	private String strClassName;
	private String strObjectName;
	private String strTarget;
    private boolean isUIComponent;
	
    private static final String
		sidType = "type", //$NON-NLS-1$
		sidClassName = "className", //$NON-NLS-1$
		sidObjectName = "objectName", //$NON-NLS-1$
		sidTarget = "target", //$NON-NLS-1$
		sidValue = "value", //$NON-NLS-1$
		sidText = "text", //$NON-NLS-1$
	    sidTitle = "title", //$NON-NLS-1$
	    sidIsUIComponent = "isUIComponent"; //$NON-NLS-1$
	
	public FlashNode(FlashNode parent, FlashPlayer player,Object node) {
		this.parent = parent;
		this.level = null != parent ? parent.getLevel()+1 : 0;
		this.player = player;

		if( node instanceof ASObject ) {
			asObject = (ASObject)node;
    		strType = getString(sidType);
    		strClassName = getString(sidClassName);
    		strObjectName = getString(sidObjectName);
    		strTarget = getString(sidTarget);
            isUIComponent = "true".equals(getString(sidIsUIComponent)); //$NON-NLS-1$
		}
		
		if( null != parent ) {
			String targetParent = parent.getTarget();
			if( null != targetParent ) {
				if( !strTarget.equals(targetParent+"."+strObjectName) ) { //$NON-NLS-1$
					isReference = true;
				}
			}
			String parentObjectName = parent.getObjectName();
			if( "_accProps".equals(parentObjectName) || //$NON-NLS-1$
				"_accImpl".equals(parentObjectName) ) { //$NON-NLS-1$
				isAccProperties = true;
			}
		}
		if( "number".equals(strType) ||  //$NON-NLS-1$
			"null".equals(strType) ||  //$NON-NLS-1$
			"boolean".equals(strType) ||  //$NON-NLS-1$
			"string".equals(strType) ||  //$NON-NLS-1$
			"undefined".equals(strType) ) { //$NON-NLS-1$
			skipChildren = true;
		}
		else if( "object".equals(strType) ) { //$NON-NLS-1$
			if( null == strClassName ||
			    "Array".equals(strClassName) ) { //$NON-NLS-1$
				skipChildren = true;
			}
		}
		else if( "movieclip".equals(strType) ) { //$NON-NLS-1$
			if( "_level0.reserved".equals(strTarget) || //$NON-NLS-1$
                "_level0.focusManager".equals(strTarget) ){ //$NON-NLS-1$
				skipChildren = true;
			}
		}
        this.accInfo = new FlashAccInfo(this);
	}
	
	public String getType() {
		return strType;
	}
	public String getClassName() {
		return strClassName;
	}
	public String getObjectName() {
		return strObjectName;
	}
	public String getTarget() {
		return strTarget;
	}
	
    public boolean isUIComponent() {
        return isUIComponent;
    }
    
	public String getValue() {
		if( null != asObject ) {
			return decodeString(getString(sidValue));
		}
		return null;
	}
    
	public String getText() {
        return getText(true);
	}
    
    public String getText(boolean useAccName) {
        String text = null;
        if( useAccName ) {
            text = accInfo.getAccName();
        }
        if( null == text ) {
        	if( null != asObject ) {
        		text = getString(sidText);
        	}
        }
        else {
            text = "["+text+"]"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        return decodeString(text);
    }

	public String getTitle() {
		if( null != asObject ) {
			return decodeString(getString(sidTitle));
		}
		return null;
	}

	public String getString(String name) {
		if( null != asObject ) {
			Object result = asObject.get(name); 
			return null==result ? null : result.toString();
		}
		return null;
	}
	
	public Object getObject(String name) {
		if( null != asObject ) {
			return asObject.get(name); 
		}
		return null;
	}
	
	private static String decodeString(String input) {
		if( null != input ) {
			try {
				return URLDecoder.decode(input,"UTF-8"); //$NON-NLS-1$
			}
			catch( Exception e) {
				e.printStackTrace();
			}
		}
		return input;
	}

	public FlashNode getParent() {
		return parent;
	}
	
	public int getLevel() {
		return level;
	}
	
	protected boolean shouldSkip() {
		return (skipChildren && !isAccProperties) || isReference;
	}
	
	protected boolean isAccProperties() {
		return isAccProperties;
	}
	
	public boolean hasChild(boolean visual) {
		if( level>=50 ) {
			throw new Error(MessageFormat.format(Messages.getString("flash.error_target_length"),new Object[]{new Integer(level)})+"\n"+strTarget); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return player.hasChild(this,visual);
	}
	
	public Object[] getChildren(boolean visual, boolean informative/*, boolean debugMode*/ ) {
		FlashNode[] children = player.getChildren(this,visual);
		List<FlashNode> childList = new ArrayList<FlashNode>();
		for( int i=0; i<children.length; i++ ) {
			FlashNode node = children[i];
            if( !FlashFinder.debugMode ) { 
                if( !visual && node.shouldSkip() ) {
                    continue;
                }
                if( informative && !node.isAccProperties() ) {
                    if( null == node.getText() &&
                        !"movieclip".equals(node.getType()) && //$NON-NLS-1$
                        !"Button".equals(node.getClassName()) && //$NON-NLS-1$
                        !"_accProps".equals(node.getObjectName()) && //$NON-NLS-1$
                        !"_accImpl".equals(node.getObjectName()) && //$NON-NLS-1$
                        !"onRelease".equals(node.getObjectName()) )  //$NON-NLS-1$
                    {
                        continue;
                    }
                }
            }
			childList.add(node);
		}
		return childList.toArray();
	}
    
    public void dispose() {
    }
    
    public FlashNode getNode(String nodeName) {
        return player.getNodeFromPath(getTarget()+"."+nodeName); //$NON-NLS-1$
    }
	
	public void setMarker() {
		if( null != asObject ) {
			player.setMarker(asObject.get("x"),asObject.get("y"),asObject.get("w"),asObject.get("h")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}
	}
	
	public FlashPlayer getPlayer() {
		return player;
	}
    
    public FlashAccInfo getAccInfo() {
        return accInfo;
    }

    protected void finalize() throws Throwable {
        dispose();
        super.finalize();
    }

    public Set getKeys() {
    	if( null != asObject ) {
    		return asObject.getKeys();
    	}
    	return null;
    }
}
