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
package fede.workspace.model.manager.properties.impl.mc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.impl.ui.MC_AttributesItem;
import fr.imag.adele.cadse.core.ui.UIField;
import fede.workspace.eclipse.MelusineProjectManager;

/**
 * It's a model controller to transforme a list of string to a list of resources.
 * The string is a relatif path.
 * @author chomats
 *
 */
public class StringToResourceListModelController extends MC_AttributesItem {
	
	public StringToResourceListModelController() {
	}
	
	@Override
	public Object getValue() {
		return abstractToVisualValue(super.getValue());
	}
	
	@Override
	public void notifieValueChanged(UIField field, Object value) {
		super.notifieValueChanged(field, visualToAbstractValue(value));
	}
	
	protected Object abstractToVisualValue(Object value) {
		Item theCurrentItem = getItem();
		List<IResource> ret = convertIResourceValue(theCurrentItem, value);
        return ret;
	}

	public static List<IResource> convertIResourceValue(Item theCurrentItem, Object value) {
		List<String> packagesString = (List<String>) value;
		IResource theItemResource = MelusineProjectManager.getResource(theCurrentItem);
		
		if (packagesString == null || theItemResource == null || !(theItemResource instanceof IContainer))
            return Collections.EMPTY_LIST;

		//IPath theItemPath = theItemResource.getFullPath();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		
		List<IResource> ret = new ArrayList<IResource>();
		for (String path : packagesString) {
			IContainer container = root;
			IPath pathObject = new Path(path);
			if (!pathObject.isAbsolute()) {
				container = (IContainer) theItemResource;
			} 
			
			if (pathObject.isRoot()) {
				ret.add(root);
			} else {
				ret.add(container.findMember(pathObject));
			}
		}
		return ret;
	}
	
	protected Object visualToAbstractValue(Object value) {
		List<String> packagesString = new ArrayList<String>();
		List<IResource> ret = (List<IResource>) value;
		Item theCurrentItem = (Item) getItem();
		IResource theItemResource = MelusineProjectManager.getResource(theCurrentItem);
		IPath theItemPath = theItemResource.getFullPath();
		
		for (IResource r : ret) {
			IPath path = r.getFullPath();
			
			if (theItemPath.isPrefixOf(path)) {
				path = path.removeFirstSegments(theItemPath.segmentCount());
			}
			packagesString.add(path.toPortableString());
		}
		return packagesString;
	}
	

}
