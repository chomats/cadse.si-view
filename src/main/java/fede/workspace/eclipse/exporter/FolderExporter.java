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
package fede.workspace.eclipse.exporter;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

import fede.workspace.eclipse.MelusineProjectManager;
import fede.workspace.eclipse.composer.EclipseExportedContent;
import fr.imag.adele.cadse.core.ContentItem;

public class FolderExporter extends EclipseExporter {
	private static final String	FOLDER_EXPORTER_TYPE	= "folder-exporter";
	private final IPath			_contentPath;

	public FolderExporter(ContentItem contentManager, String contentPath) {
		super(contentManager, FOLDER_EXPORTER_TYPE);
		this._contentPath = new Path(contentPath);
		assert this._contentPath != null && !this._contentPath.isEmpty() && !this._contentPath.isAbsolute();
	}

	public FolderExporter(ContentItem contentManager, String type, String contentPath) {
		super(contentManager, type);
		this._contentPath = new Path(contentPath);
		assert this._contentPath != null && !this._contentPath.isEmpty() && !this._contentPath.isAbsolute();
	}

	public IPath getContentPath() {
		return _contentPath;
	}

	/**
	 * Updates an existing packaged binary version of the content of the folder
	 * in the resource associated with the item.
	 * 
	 * If no resource delta is specified all the folder contents are copied to
	 * the packaged version.
	 * 
	 * @param item
	 * @param delta
	 * @param monitor
	 * @throws CoreException
	 */
	@Override
	protected void exportItem(EclipseExportedContent eclipseExportedContent, IResourceDelta componentUpdate,
			IProgressMonitor monitor, String exporterType) throws CoreException {

		/*
		 * skip empty notifications
		 */
		if ((componentUpdate != null) && (componentUpdate.getKind() == IResourceDelta.NO_CHANGE)) {
			return;
		}

		/*
		 * Verify this item is actually hosted in an existing container resource
		 */
		// TODO use contentmanager to find the folder
		IResource itemResource = MelusineProjectManager.getResource(getItem());
		if (itemResource == null) {
			return;
		}
		if (!itemResource.exists()) {
			return;
		}
		if (itemResource.getType() == IResource.FILE) {
			return;
		}

		IFolder contentFolder = ((IContainer) itemResource).getFolder(_contentPath);
		IResourceDelta contentDelta = (componentUpdate != null) ? componentUpdate.findMember(contentFolder
				.getProjectRelativePath()) : null;

		/*
		 * skip notifications not concerning the content folder
		 */
		if ((componentUpdate != null) && (contentDelta == null)) {
			return;
		}

		/*
		 * skip empty contents
		 */
		if (!contentFolder.exists()) {
			return;
		}

		/*
		 * Scan all updates to the content folder
		 */
		Scanner scanner = new Scanner(eclipseExportedContent);
		scanner.scan(contentFolder, contentDelta, monitor);
	}

	/**
	 * This class is a visitor that scans a folder copying all modified files to
	 * a packaged item
	 * 
	 * @author vega
	 * 
	 */
	private class Scanner implements IResourceVisitor, IResourceDeltaVisitor {

		private final EclipseExportedContent	eclipseExportedContent;

		public Scanner(EclipseExportedContent eclipseExportedContent) {
			this.eclipseExportedContent = eclipseExportedContent;
		}

		private IFolder				contentFolder;
		private IProgressMonitor	monitor;

		/**
		 * This methods iterates over all modifications of the content folder
		 * aplying them to the packaged item
		 * 
		 */
		public synchronized void scan(IFolder contentFolder, IResourceDelta contentDelta, IProgressMonitor monitor)
				throws CoreException {
			this.contentFolder = contentFolder;
			this.monitor = monitor;

			if (contentDelta != null) {
				contentDelta.accept(this);
			} else {
				contentFolder.accept(this);
			}
		}

		/**
		 * This callback method is called to visit and filter all the content of
		 * the content folder in the case of full copies. We consider any
		 * matching file found as an addition.
		 */
		public boolean visit(IResource contentResource) throws CoreException {
			return added(contentResource);
		}

		/**
		 * This callback method is called to incrementally perform an update
		 * from the contents of the content folder
		 */
		public boolean visit(IResourceDelta outputDelta) throws CoreException {

			switch (outputDelta.getKind()) {
				case IResourceDelta.ADDED: {
					return added(outputDelta.getResource());
				}
				case IResourceDelta.REMOVED: {
					return removed(outputDelta.getResource());
				}
				case IResourceDelta.CHANGED: {
					return changed(outputDelta.getResource());
				}
			}

			return true;
		}

		private boolean removed(IResource contentResource) throws CoreException {
			IPath filePath = getRelativePath(contentFolder, contentResource);
			eclipseExportedContent.delete(getContentPath().append(filePath), monitor);
			return false;
		}

		private boolean added(IResource contentResource) throws CoreException {
			IPath filePath = getRelativePath(contentFolder, contentResource);
			eclipseExportedContent.add(contentResource, getContentPath().append(filePath), monitor);
			return false;
		}

		private boolean changed(IResource contentResource) throws CoreException {
			/*
			 * We have to scan to the file level to verify wich files we have to
			 * replace
			 */
			if (contentResource.getType() == IResource.FOLDER) {
				return true;
			}

			IPath filePath = getRelativePath(contentFolder, contentResource);
			eclipseExportedContent.update(contentResource, getContentPath().append(filePath), monitor);
			return false;
		}

	}

	/**
	 * Gets a path relative to the resource associated with this item.
	 * 
	 * @param path
	 * @return
	 * @throws CoreException
	 */
	protected final IPath getRelativePath(IPath path) throws CoreException {
		IResource itemResource = MelusineProjectManager.getResource(getItem());
		if (itemResource.getFullPath().isPrefixOf(path)) {
			path = path.removeFirstSegments(itemResource.getFullPath().segmentCount());
		}
		return path;
	}

	/**
	 * Gets the path of a resource relative to another resource, both resources
	 * must be located in the eclipse resource associated with this item.
	 * 
	 * @param path
	 * @return
	 * @throws CoreException
	 */
	protected final IPath getRelativePath(IResource container, IResource member) throws CoreException {
		IPath containerPath = getRelativePath(container.getFullPath());
		IPath memberPath = getRelativePath(member.getFullPath());

		if (containerPath.isPrefixOf(memberPath)) {
			memberPath = memberPath.removeFirstSegments(containerPath.segmentCount());
		}

		return memberPath;
	}

}
