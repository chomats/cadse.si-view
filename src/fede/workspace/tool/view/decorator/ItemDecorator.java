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
package fede.workspace.tool.view.decorator;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.osgi.framework.Bundle;

import fr.imag.adele.cadse.core.Item;
import fede.workspace.tool.view.WSPlugin;

public class ItemDecorator  extends LabelProvider implements ILightweightLabelDecorator {

	

	public void decorate(Object element, IDecoration decoration) {
		IResource resource = getResource(element);
		if (resource == null || resource.getType() == IResource.ROOT)
			return;
		Item  item = getItem(element, resource);
		if (item == null)
			return;
		try {
			if (element instanceof Item) {
			//	IJavaElement je = JavaCore.create(resource);
				
			} else {
				ImageDescriptor image = WSPlugin.getDefault().getImageDescriptorFrom(item.getType(), item);
				if (image != null) {
					decoration.addOverlay(image);
				}
			}
			
		} catch (IllegalStateException e) {
		    // This is thrown by Core if the workspace is in an illegal state
		    // If we are not active, ignore it. Otherwise, propogate it.
		    // (see bug 78303)
		    if (Platform.getBundle(WSPlugin.PLUGIN_ID).getState() == Bundle.ACTIVE) {
		        throw e;
		    }
		}
	}
	
	
	
	private Item getItem(Object element, IResource resource) {
		if (element instanceof Item)
			return (Item) element;
		try {
			return WSPlugin.sGetItemFromResource(resource);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}



	/**
	 * Returns the resource for the given input object, or
	 * null if there is no resource associated with it.
	 *
	 * @param object  the object to find the resource for
	 * @return the resource for the given object, or null
	 */
	private IResource getResource(Object object) {
		if (object instanceof IResource) {
			return (IResource) object;
		}
		if (object instanceof IAdaptable) {
			return (IResource) ((IAdaptable) object).getAdapter(
				IResource.class);
		}
		if (object instanceof Item) {
			return ((Item)object).getMainMappingContent(IResource.class);
		}
		return null;
	}

}
