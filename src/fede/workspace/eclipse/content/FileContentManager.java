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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

import fede.workspace.tool.eclipse.MappingManager;
import fede.workspace.tool.view.WSPlugin;
import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.CompactUUID;
import fr.imag.adele.cadse.core.ContentItem;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.attribute.IAttributeType;
import fr.imag.adele.cadse.core.impl.ContentItemImpl;
import fr.imag.adele.cadse.core.var.ContextVariable;
import fr.imag.adele.cadse.core.var.Variable;
import fr.imag.adele.fede.workspace.si.view.View;

/**
 * The Class FileContentManager.
 * 
 * @author <a href="mailto:stephane.chomat@imag.fr">Stephane Chomat</a>
 */
public class FileContentManager extends ContentItemImpl {

	/** The variable name. */
	protected Variable	variableName;

	/** The variable path. */
	protected Variable	variablePath;

	/**
	 * Instantiates a new file content manager.
	 * 
	 * @param parent
	 *            the parent
	 * @param item
	 *            the item
	 * @param name
	 *            the name
	 * @param path
	 *            the path
	 */
	public FileContentManager(CompactUUID id, Variable name, Variable path) {
		super(id);
		variableName = name;
		variablePath = path;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ContentManager#init()
	 */
	@Override
	public void init() throws CadseException {
		IFile f = getFile(ContextVariable.DEFAULT);
		if (f != null) {
			View.setItemPersistenceID(f, getOwnerItem());
		}
	}

	/**
	 * Gets the file name.
	 * 
	 * @param context
	 *            the context
	 * 
	 * @return the file name
	 */
	public String getFileName(ContextVariable context) {
		return variableName.compute(context, getOwnerItem());
	}

	/**
	 * Gets the folder path.
	 * 
	 * @param context
	 *            the context
	 * 
	 * @return the folder path
	 */
	public String getFolderPath(ContextVariable context) {
		return variablePath.compute(context, getOwnerItem());
	}

	/**
	 * Gets the path.
	 * 
	 * @param context
	 *            the context
	 * 
	 * @return the path
	 */
	public String getPath(ContextVariable context) {
		return getFolderPath(context) + "/" + getFileName(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ContentManager#create()
	 */
	@Override
	public void create() throws CadseException {
		try {
			IFile f = getFile(ContextVariable.DEFAULT);
			if (f == null) {
				throw new CadseException("Cannot find the file");
			}

			MappingManager.createContainer(f.getParent(), View.getDefaultMonitor());
			if (!f.exists()) {
				f.create(getDefaultImputStream(), true, View.getDefaultMonitor());
			}
			View.setItemPersistenceID(f, getOwnerItem());
		} catch (CoreException e) {
			throw new CadseException("Cannot create folder from {0} : {1}", e, getOwnerItem().getQualifiedDisplayName(), e
					.getMessage());
		}
	}

	/**
	 * Gets the default imput stream.
	 * 
	 * @return the default imput stream
	 */
	protected InputStream getDefaultImputStream() {
		return new ByteArrayInputStream(new byte[0]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ContentManager#delete()
	 */
	@Override
	public void delete() throws CadseException {
		IFile f = getFile(ContextVariable.DEFAULT);
		try {
			if (f != null && f.exists()) {
				f.delete(false, View.getDefaultMonitor());
			}
		} catch (CoreException e) {
			throw new CadseException("Cannot create folder from {0} : {1}", e, getItem().getId(), e.getMessage());
		}
	}

	/**
	 * Gets the file.
	 * 
	 * @return the file
	 * 
	 * @throws CadseException
	 *             the melusine exception
	 */
	public IFile getFile() {
		return getFile(ContextVariable.DEFAULT);
	}

	/**
	 * Gets the file, can be null
	 * 
	 * @param cxt
	 *            the cxt
	 * 
	 * @return the file
	 * 
	 * @throws CadseException
	 *             the melusine exception
	 */
	public IFile getFile(ContextVariable cxt) {
		ContentItem parentCm = getPartParent();
		if (parentCm == null) {
			WSPlugin.logErrorMessage("The content manager of {0} has not parent !!!", getOwnerItem().getQualifiedName());
			return null;
		}

		IContainer mainResource = parentCm.getMainMappingContent(IContainer.class);
		if (mainResource == null) {
			WSPlugin.logErrorMessage(
					"The parent content manager of {0}, which type is {1}, has not folder or project !!! ", getOwnerItem()
							.getQualifiedName(), parentCm.getClass());
			return null;
		}

		return mainResource.getFile(new Path(getPath(cxt)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ContentManager#getKindsResource()
	 */
	@Override
	public String[] getKindsResource() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ContentManager#getMainResource()
	 */
	@Override
	public Object getMainResource() {
		return getMainMappingContent(IFile.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ContentManager#getMainMappingContent(fr.imag.adele.cadse.core.var.ContextVariable,
	 *      java.lang.Class)
	 */
	@Override
	public <T> T getMainMappingContent(ContextVariable cxt, Class<T> clazz) {
		if (clazz == IFile.class) {
			return (T) getFile(cxt);
		}
		if (clazz == File.class) {
			IFile f = getFile(cxt);
			if (f == null) {
				return null;
			}
			return (T) f.getLocation().toFile();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ContentManager#getResources(java.lang.String)
	 */
	@Override
	public Object[] getResources(String kind) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ContentManager#setResources(java.lang.String,
	 *      java.lang.Object[])
	 */
	@Override
	public void setResources(String kind, Object[] resource) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ContentManager#getResources()
	 */
	@Override
	public Object[] getResources() {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	 
	 @Override
	public <T> T internalGetOwnerAttribute(IAttributeType<T> type) {
		if (type == CadseGCST.FILE_CONTENT_MODEL_at_FILE_NAME_)
			return (T) getFileName(ContextVariable.DEFAULT);
		if (type == CadseGCST.FILE_CONTENT_MODEL_at_FILE_PATH_)
			return (T) getFolderPath(ContextVariable.DEFAULT);
		
		return super.internalGetOwnerAttribute(type);
	}
	// /*
	// * (non-Javadoc)
	// *
	// * @see
	// fr.imag.adele.cadse.core.ContentManager#computeRenameChange(org.eclipse.ltk.core.refactoring.CompositeChange,
	// * fr.imag.adele.cadse.core.var.ContextVariable,
	// * fr.imag.adele.cadse.core.var.ContextVariable)
	// */
	// @Override
	// public RefactoringStatus computeRenameChange(CompositeChange change,
	// ContextVariable newCxt, ContextVariable oldCxt) {
	// Path newName = new Path(getPath(newCxt));
	// IFile f = getFile(oldCxt);
	// change.add(new RenameResourceChange(null, f, newName.lastSegment(),
	// "Rename project"));
	// return new RefactoringStatus();
	// }

}
