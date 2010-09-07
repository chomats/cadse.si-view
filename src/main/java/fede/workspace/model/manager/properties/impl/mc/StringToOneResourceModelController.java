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
 *
 * Copyright (C) 2006-2010 Adele Team/LIG/Grenoble University, France
 */
package fede.workspace.model.manager.properties.impl.mc;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.impl.ui.mc.MC_AttributesItem;
import fr.imag.adele.cadse.core.ui.UIField;
import fede.workspace.eclipse.MelusineProjectManager;

public class StringToOneResourceModelController extends MC_AttributesItem {
	
	public StringToOneResourceModelController() {
		super();
	}
	
	
	@Override
	public Object getValue() {
		Object value = super.getValue();
		Item theCurrentItem = (Item) getItem();
		IResource ret = convertIResourceValue(theCurrentItem, value);
       return ret;
	}

	public static IResource convertIResourceValue(Item theCurrentItem, Object value) {
		String path = (String) value;
		
		IResource theItemResource = MelusineProjectManager.getResource(theCurrentItem);
		if (path == null || path.length() == 0 || theItemResource == null || !(theItemResource instanceof IContainer))
            return null;

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		
		IResource ret = null;
		IContainer container = root;
		IPath pathObject = new Path(path);
		if (!pathObject.isAbsolute()) {
			container = (IContainer) theItemResource;
		}
		
		if (pathObject.isRoot()) {
			ret = root;
		} else {
			ret = container.findMember(pathObject);
		}
		
		return ret;
	}
	
	@Override
	public void notifieValueChanged(UIField field, Object value) {
		super.notifieValueChanged(field, visualToAbstractValue(value));
	}
	
	protected Object visualToAbstractValue(Object value) {
		Item theCurrentItem = (Item) getItem();
		IResource theItemResource = MelusineProjectManager.getResource(theCurrentItem);
		IPath theItemPath = theItemResource.getFullPath();
		
		IResource r = (IResource) value;
		if (r == null)
			return null;
		
		IPath path = r.getFullPath();
		
		if (theItemPath.isPrefixOf(path)) {
			path = path.removeFirstSegments(theItemPath.segmentCount()).makeRelative();
		}
		return path.toPortableString();
	}
	

}
