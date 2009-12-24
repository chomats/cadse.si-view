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
package fede.workspace.model.manager.properties.editor;

import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

import fede.workspace.tool.view.WSPlugin;
import fr.imag.adele.cadse.core.IItemManager;
import fr.imag.adele.cadse.core.Item;

public class ItemEditorInput implements IEditorInput, IPersistableElement {

	private Item	item;

	public ItemEditorInput(Item item2) {
		this.item = item2;
	}

	public boolean exists() {
		return true;
	}

	public ImageDescriptor getImageDescriptor() {
		IItemManager im = WSPlugin.getManager(item);
		if (im == null) {
			return null;
		}
		URL url = im.getImage(item);
		if (url == null) {
			return null;
		}
		return ImageDescriptor.createFromURL(url);
	}

	public String getName() {
		return item.getName();
	}

	public IPersistableElement getPersistable() {
		return this;
	}

	public String getToolTipText() {
		return item.getName();
	}

	/*
	 * (non-Javadoc) Method declared on IAdaptable.
	 */
	public Object getAdapter(Class adapter) {
		if (adapter == Item.class) {
			return item;
		}
		return Platform.getAdapterManager().getAdapter(item, adapter);
	}

	public String getFactoryId() {
		return ItemEditorInputFactory.getFactoryId();
	}

	public void saveState(IMemento memento) {
		ItemEditorInputFactory.saveState(memento, item);
	}

	/*
	 * (non-Javadoc) Method declared on Object.
	 * 
	 * The <code>FileEditorInput</code> implementation of this <code>Object</code>
	 * method bases the equality of two <code>FileEditorInput</code> objects
	 * on the equality of their underlying <code>IFile</code> resources.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ItemEditorInput)) {
			return false;
		}
		ItemEditorInput other = (ItemEditorInput) obj;
		return item.equals(other.item);
	}

	/*
	 * (non-Javadoc) Method declared on Object.
	 */
	@Override
	public int hashCode() {
		return item.hashCode();
	}

	public Item getItem() {
		return item;
	}
}
