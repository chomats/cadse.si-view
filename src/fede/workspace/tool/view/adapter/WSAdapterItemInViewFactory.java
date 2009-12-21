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
package fede.workspace.tool.view.adapter;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.views.properties.IPropertySource;

import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.Item;

public class WSAdapterItemInViewFactory implements IAdapterFactory {

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		Object i = igetAdapter(adaptableObject, adapterType);
		// System.out.println("WSAdapterItemInViewFactory:"+adaptableObject.getClass()
		// +" -> " +adapterType+" = "+i);
		return i;
	}

	Object igetAdapter(Object adaptableObject, Class adapterType) {
		if (adapterType.isInstance(adaptableObject)) {
			return adaptableObject;
		}
		if (!(adaptableObject instanceof IItemNode)) {
			return null;
		}
		if (adapterType == IResource.class) {
			Item item = ((IItemNode) adaptableObject).getItem();
			if (item != null && item.isResolved()) {
				IResource r = item.getMainMappingContent(IResource.class);
				if (r != null && r.getType() == IResource.ROOT) {
					r = null;
				}
				return r;
			}
			return null;
		}
		// if (adapterType == IPropertySource.class) {
		// Item item = ((ItemInViewer) adaptableObject).getItem();
		// if (item != null) {
		// IItemManager ip = WSPlugin.getManager(item.getType());
		// if (ip != null) {
		// IPropertySource ret = ip.getPropertySource(item);
		// if (ret != null) return ret;
		// }
		//
		// return new WSItemPropertySource(item);
		// }
		// }
		if (adapterType == Item.class) {
			return ((IItemNode) adaptableObject).getItem();
		}

		return null;
	}

	public Class[] getAdapterList() {
		return new Class[] { Item.class, IResource.class, IPropertySource.class };
	}

}
