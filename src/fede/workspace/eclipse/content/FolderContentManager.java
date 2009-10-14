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
package fede.workspace.eclipse.content;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import fede.workspace.tool.eclipse.MappingManager;
import fede.workspace.tool.view.content.IViewContentModel;
import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.CompactUUID;
import fr.imag.adele.cadse.core.ContentItem;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.attribute.IAttributeType;
import fr.imag.adele.cadse.core.impl.CadseIllegalArgumentException;
import fr.imag.adele.cadse.core.impl.ContentItemImpl;
import fr.imag.adele.cadse.core.var.ContextVariable;
import fr.imag.adele.cadse.core.var.Variable;
import fr.imag.adele.fede.workspace.si.view.View;

public class FolderContentManager extends ContentItemImpl implements IViewContentModel, IWorkbenchAdapter {
	Variable			path;
	protected boolean	forceDelete	= true;

	public FolderContentManager(CompactUUID id, Variable path) {
		super(id);
		this.path = path;
	}

	@Override
	public void init() throws CadseException {
		super.init();
		IFolder f = getFolder();
		if (f != null) {
			View.setItemPersistenceID(f, getItem());
		}
	}

	public String getPath(ContextVariable cxt) {
		return path.compute(cxt, getItem());
	}

	/**
	 * Return le container parent. Null si il y un probleme : parent non present
	 * ou incapable de retourner un IContainer
	 * 
	 * @param cxt
	 *            (context, peut etre ContextVariable.DEFAULT
	 * @return
	 */
	public IContainer getParentContainer(ContextVariable cxt) {
		ContentItem parentCm = getParentPartContentManager();
		if (parentCm == null) {
			return null;
		}

		IContainer mainResource = parentCm.getMainMappingContent(cxt, IContainer.class);
		return mainResource;
	}

	@Override
	public void create() throws CadseException {
		try {
			IFolder f = getFolder();

			MappingManager.createFolder(f, View.getDefaultMonitor());
			View.setItemPersistenceID(f, getItem());
		} catch (CoreException e) {
			throw new CadseException("Cannot create folder from {0} : {1}", e, getItem().getQualifiedDisplayName(), e
					.getMessage());
		}
	}

	@Override
	public <T> T getMainMappingContent(ContextVariable cxt, Class<T> clazz) {
		if (clazz.isAssignableFrom(IFolder.class)) {
			return (T) getFolder(cxt);
		}

		if (clazz.isAssignableFrom(IFolder.class)) {
			IFolder f = getFolder(cxt);
			if (f == null) {
				return null;
			}
			return (T) f.getLocation().toFile();
		}
		return null;
	}

	@Override
	public void delete() throws CadseException {
		try {
			IFolder f = getFolder();
			if (f != null && f.exists()) {
				f.delete(forceDelete, View.getDefaultMonitor());
			}
		} catch (CoreException e) {
			throw new CadseException("Cannot create folder from {0} : {1}", e, getItem().getDisplayName(), e
					.getMessage());
		}
	}

	protected IFolder getFolder() {
		return getFolder(ContextVariable.DEFAULT);
	}

	public IFolder getFolder(ContextVariable cxt) {
		IContainer c = getParentContainer(cxt);
		if (c == null) {
			throw new CadseIllegalArgumentException("Cannot get folder from {0} : parent container is null !!!",
					getItem().getDisplayName());
		}
		IFolder _retf = c.getFolder(new Path(getPath(cxt)));
		return _retf;
	}

	@Override
	public String[] getKindsResource() {
		return null;
	}

	@Override
	public Object getMainResource() {
		return getFolder();
	}

	@Override
	public Object[] getResources(String kind) {
		return new Object[] { getFolder() };
	}

	@Override
	public void setResources(String kind, Object[] resource) {
	}

	@Override
	public Object[] getResources() {
		return new Object[] { getFolder() };
	}

	public ITreeContentProvider getContentProvider() {
		return new BaseWorkbenchContentProvider();
	}

	public ILabelProvider getLabelProvider() {
		return WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider();
	}

	public Object[] getChildren(Object o) {
		return getResources();
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		return null;
	}

	public String getLabel(Object o) {
		return "FolderContentManager";
	}

	public Object getParent(Object o) {
		return null;
	}
	
	
	
		 
		 @Override
		public <T> T internalGetOwnerAttribute(IAttributeType<T> type) {
			if (type == CadseGCST.FOLDER_CONTENT_MODEL_at_FOLDER_PATH_)
				return (T) getPath(ContextVariable.DEFAULT);
			
			return super.internalGetOwnerAttribute(type);
		}

}
