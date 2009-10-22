/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
/**
 * 
 */
package fede.workspace.tool.view;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IActionFilter;
import org.eclipse.ui.IContributorResourceAdapter;

import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.LinkType;

public abstract class ItemInViewer implements IItemNode, IAdaptable, IActionFilter, IContributorResourceAdapter {
	
	final protected ItemInViewer parent;
	final protected int kind;
	protected boolean open = false;

	
    protected ItemInViewer(int kind, ItemInViewer parent) {
        super();
        this.parent = parent;
        this.kind = kind;
    }
    
	
	abstract public Item getItem();
	
	//todo copier dans un labelprovider
	protected String getCodeRT() {
		StringBuffer ret = new StringBuffer();
		
		if (getLinkType().isPart())
			ret.append("p");
        if (getLinkType().isAggregation())
            ret.append("a");
        if (getLinkType().isRequire())
            ret.append("r");
        if (getLinkType().isComposition())
            ret.append("c");
		return ret.toString();
	}
	
//	todo copier dans un labelprovider
	@Override
	abstract public String toString() ;
	
	protected String toString(Link l) {
		if (l.getResolvedDestination() == null)
			return l.getDestinationId().toString();
		return WSPlugin.getManager(l.getResolvedDestination()).getDisplayName(l.getResolvedDestination());
	}
	
	protected String toStringSource(Link l) {
		return WSPlugin.getManager(l.getSource()).getDisplayName(l.getSource());
	}
	
	public int getKind() {
		return kind;
	}

	public Object getAdapter(Class adapter) {
		
		Item item = getItem();
        if (adapter== IContributorResourceAdapter.class) {
            return this;
        }
		if (adapter == Item.class) {
			return item;
		}
		if (item != null && item instanceof IAdaptable) {
			return ((IAdaptable)item).getAdapter(adapter);
		}
		try {
			return Platform.getAdapterManager().getAdapter(this, adapter);
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}
	
	public ItemInViewer getParent() {
		return parent;
	}
	
	public void close() {
		open = false;
	}

	abstract public Link getLink();

	abstract public LinkType getLinkType();
	
	abstract public ItemType getItemType();
	
	@Deprecated
	abstract public ItemInViewer[] getChildren(int flag);
	
	
	
	public void open() {
		if (isOpen()) {
			return;
		}
		open = true;
	}

    public boolean testAttribute(Object target, String name, String value) {
        return WSPlugin.testAttribute(target,name,value);
    }

    public IResource getAdaptedResource(IAdaptable adaptable) {
        return null;
    }
    
    public boolean isOpen() {
    		return open;
    }
    
    @Override
    public boolean equals(Object arg0) {
    		if (arg0 instanceof ItemInViewer) {
    			ItemInViewer iiv = (ItemInViewer) arg0;
    			return (iiv.kind == this.kind);
    		}
    		return super.equals(arg0);
    }

    abstract public String getToolTip();

    abstract public boolean hasChildren();
    
} 