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
package fede.workspace.eclipse.composer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.CompactUUID;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.build.IBuildingContext;
import fr.imag.adele.cadse.core.build.IExportedContent;
import fede.workspace.eclipse.composition.CompositeBuildingContext;
import fede.workspace.tool.view.WSPlugin;
import fr.imag.adele.fede.workspace.si.view.View;

/**
 * This class represents an item that has been packaged in a repository in order to be composed with
 * other items in a composite
 * 
 * Packaged items are originally generated in the source repository associated with the item, and then
 * copied to all the parent composites in order to be composed.
 * 
 * @author vega
 *
 */
public final class EclipseExportedContent implements IExportedContent {

	private final Item 			item;
	private final IContainer	repository;
	private String exporterType;
	private Link link;
	
	/**
	 * Creates a packaged item. 
	 * 
	 * This creates an empty private folder in the hosting repository to keep a copy of the packaged item.
	 * 
	 * @param repository
	 * @param item
	 */
	public EclipseExportedContent(IContainer repository, Item item, String type, IProgressMonitor monitor)throws CoreException {
		this(repository,item,type,true,monitor);
	}

	/**
	 * Internal Implementation
	 * 
	 * Creates a packaged item. This optionally creates a private folder in the hosting repository
	 * to keep a copy of the packaged item. 
	 * 
	 * If creation is not forced, the repository must already host this item.
	 * 
	 * @param repository
	 * @param item
	 * @param forceCreate
	 * @param monitor
	 * @throws CoreException
	 */
	private EclipseExportedContent(IContainer repository, Item item, String exporterType, boolean forceCreate, IProgressMonitor monitor)throws CoreException {
		this.repository 		= repository;
		this.item		 		= item;
		this.exporterType  	= exporterType;
		if (forceCreate)
			createItemFolder(monitor);
		
		System.out.println("EclipseExportedContent.EclipseExportedContent() "+getItemFolder().getFullPath());
		assert getItemFolder().exists();
	}
	/**
	 * Gets a handle to all the existing packaged items in a specified repository
	 */
	public static List<IExportedContent> getPackagedItems(IContainer repository, String exporterType, IProgressMonitor monitor) throws CoreException {
		List<IExportedContent> packagedItems = new ArrayList<IExportedContent>();
		
		IFolder packagingArea = repository.getFolder(getComponentsPath());
		if (!packagingArea.exists())
			return packagedItems;
		
		// Iterate over all folders in the packaged items area
		for (IResource eclipseResource : packagingArea.members()) {
			if (eclipseResource.getType() != IResource.FOLDER) continue;
			Item findItem = findItemFromResource(eclipseResource);
			if (findItem == null) continue;
			
			EclipseExportedContent pi = getPackagedItem(repository,findItem,exporterType,monitor);
			if (pi != null) packagedItems.add(pi);
		}
		
		return packagedItems;
	}
	
	/**
	 * Gets a handle to all the existing packaged items in a specified repository
	 */
	public static List<EclipseExportedContent> getPackagedItems(IContainer repository, IProgressMonitor monitor) throws CoreException {
		List<EclipseExportedContent> eclipseExportedContents = new ArrayList<EclipseExportedContent>();
		
		IFolder packagingArea = repository.getFolder(getComponentsPath());
		if (!packagingArea.exists())
			return eclipseExportedContents;
		
		// Iterate over all folders in the packaged items area
		for (IResource eclipseResource : packagingArea.members()) {
			if (eclipseResource.getType() != IResource.FOLDER) continue;
			for (IResource typeEclipseResource : ((IContainer) eclipseResource).members()) {
				if (typeEclipseResource.getType() != IResource.FOLDER) continue;
				Item findItem = findItemFromResource(eclipseResource);
				eclipseExportedContents.add(getPackagedItem(repository,findItem,typeEclipseResource.getName(),monitor));
			}
			
		}
		
		return eclipseExportedContents;
	}
	
	private static Item findItemFromResource(IResource eclipseResource) {
		CompactUUID id = CompactUUID.fromString(eclipseResource.getName());
		if (id == null) return null;
		return View.getInstance().getWorkspaceDomain().getLogicalWorkspace().getItem(id);
	}
	
	protected static String toStringForResource(Item item) {
		return item.getId().toString();
	}


	/**
	 * Gets a handle to an existing packaged item in a specified repository
	 */
	public static EclipseExportedContent getPackagedItem(IContainer repository, Item item, String type, IProgressMonitor monitor) throws CoreException {
		IFolder componentsFolder	= repository.getFolder(getComponentsPath());
		if (!componentsFolder.exists())	return null;
		
		
		IFolder componentFolder = componentsFolder.getFolder(toStringForResource(item));
		if (!componentFolder.exists())	return null;
		
		IFolder typeComponentFolder = componentFolder.getFolder(type);
		if (!typeComponentFolder.exists())	return null;
		
		return new EclipseExportedContent(repository,item,type,false,monitor);
	}
	
	/**
	 * Gets a handle to an existing copy of this packaged item in another repository. 
	 * 
	 */
	public EclipseExportedContent getCopy(IContainer targetRepository, IProgressMonitor monitor) throws CoreException {
		return getPackagedItem(targetRepository,getItem(),getExporterType(),monitor);
	}
	
	/**
	 * Creates a new copy of this packaged item in the target repository.
	 * 
	 * The new copy is hosted in the target repository and can be used to locally build a composite
	 * in that repository.
	 * 
	 * @param targetProject
	 * @param monitor
	 * @return
	 * @throws CoreException
	 */
	public EclipseExportedContent copy(IContainer targetRepository, IProgressMonitor monitor)throws CoreException  {
		assert !targetRepository.equals(getRepository());
		
		monitor.subTask("copying packaged item "+getFullPath());
		
		/*
		 * First ensure that the packaging area exists in the target repository
		 */
		EclipseExportedContent.createFolder(targetRepository.getFolder(getPath().removeLastSegments(1)),monitor);

		
		IFolder sourceFolder = getItemFolder();
		IFolder targetFolder = targetRepository.getFolder(getPath());
        if (targetFolder.getLocation().toFile().exists()) {
            targetFolder.getLocation().toFile().delete();
            //TODO
        }
		sourceFolder.copy(targetFolder.getFullPath(),true,monitor);
		monitor.worked(1);
		
		return new EclipseExportedContent(targetRepository,getItem(),getExporterType(),false,monitor);
	}

	/**
	 * Updates the contents of a copy of this packaged item with modifications performed in this
	 * repository.
	 * 
	 * @param targetItem
	 * @param projectDelta
	 * @param monitor
	 */
	public boolean update(EclipseExportedContent targetItem, IResourceDelta projectDelta, IProgressMonitor monitor) throws CoreException {
		assert getItemIdentification().equals(targetItem.getItemIdentification());
		assert !getRepository().equals(targetItem.getRepository());
		assert projectDelta == null || projectDelta.getResource().equals(getRepository());
		
		
		IResourceDelta sourceItemDelta = getResourceDelta(projectDelta);
		if (sourceItemDelta == null)
			return false;
		
		Updater updater = new Updater(this,sourceItemDelta);
		return updater.update(targetItem,monitor);
	}
	

	/**
	 * Deletes this packaged item from the hosting repository. 
	 */
	public void delete(IProgressMonitor monitor) throws CoreException {
		monitor.subTask("deleting packaged item "+getFullPath());
		getItemFolder().delete(true,false,monitor);
		monitor.worked(1);
	}

	
	
	/**
	 * The repository hosting this packaged item.
	 * 
	 * @return
	 */
	public IContainer getRepository() {
		return repository;
	}

	/**
	 * Gets the relative path of the zone where the item is packaged in this repository.
	 * 
	 * @return
	 */
	public IPath getPath() {
		return getComponentsPath().append(toStringForResource(item)).append(getExporterType());
	}
	
	/**
	 * Gets the absolute path of the zone where the item is packaged in this repository.
	 * @return
	 */
	public IPath getFullPath() {
		return getRepository().getFullPath().append(getPath());
	}

	/**
	 * Returns the content of the packaged item at the specified relative path
	 * 
	 * @param path
	 * @return
	 */
	public IResource getContent(IPath path) {
		return getRepository().findMember(getPath().append(path));
	}

	/**
	 * Creates a new empty folder in this packaged item at the specified relative path
	 * 
	 * This creates all the parent folders specified in the path.
	 * 
	 * @param path
	 * @param monitor
	 * @return
	 * @throws CoreException
	 */
	public IFolder add(IPath path, IProgressMonitor monitor) throws CoreException{
		
		assert !path.isAbsolute();
		
		IFolder root = getItemFolder();

		/*
		 * If the resource already exists just return it
		 */
		if (root.exists(path))
			return root.getFolder(path);

		monitor.subTask("creating "+path);

		/*
		 *  Iterate over all folders in the path creating them if necessary
		 */
		for (String segment : path.segments()) {
			IFolder folder = root.getFolder(segment);
			if (!folder.exists()) folder.create(true,true,monitor);
			root = folder;
		}
		
		monitor.worked(1);
		return root;
		
	}
	
	/**
	 * Adds a new content to this packaged item at the specified relative path
	 * 
	 * @param content
	 * @param path
	 * @param monitor
	 * @throws CoreException
	 */
	public void add(IResource content, IPath path, IProgressMonitor monitor) throws CoreException {
		monitor.subTask("adding "+path);
		add(path.removeLastSegments(1),monitor);
		if (path.toPortableString().equals("") || path.toPortableString().equals("/"))
			if (getItemFolder().exists()) {
				getItemFolder().delete(true, monitor);
			}
//		if (targetFolder.getLocation().toFile().exists()) {
//            targetFolder.getLocation().toFile().delete();
//            //TODO
//        }
		content.copy(getFullPath().append(path),true,monitor);
		monitor.worked(1);
	}
	
	/**
	 * Deletes the contents of this packaged item at the specified relative path.
	 * 
	 * @param path
	 * @param monitor
	 * @throws CoreException
	 */
	public void delete(IPath path, IProgressMonitor monitor) throws CoreException {
		IResource targetResource = getRepository().findMember(getPath().append(path));
		
		if (targetResource == null) return; 
		if (!targetResource.exists()) return;
		
		monitor.subTask("deleting "+path);
		targetResource.delete(true,monitor);
		monitor.worked(1);
	}

	/**
	 * Replaces the contents of this packaged item at the specified relative path.
	 * 
	 * @param content
	 * @param path
	 * @param monitor
	 * @throws CoreException
	 */
	public void update(IResource content, IPath path, IProgressMonitor monitor) throws CoreException {
		delete(path,monitor);
		add(content,path,monitor);
	}
	
	/**
	 * This class iterates over all modifications of a packaged item aplying them to a copy
	 * of this item in another repository
	 *
	 */
	private static class Updater implements IResourceDeltaVisitor {
		
		private final EclipseExportedContent		sourceItem;
		private final IResourceDelta	sourceItemDelta;
		
		public Updater(EclipseExportedContent sourceItem, IResourceDelta sourceItemDelta) {
			this.sourceItem			= sourceItem;
			this.sourceItemDelta	= sourceItemDelta;
		}

		private EclipseExportedContent 		targetItem;
		private IProgressMonitor	monitor;
		private boolean				updated;
		
		/**
		 * This methods iterates over all modifications of the source folder aplying them to
		 * target item
		 * 
		 */
		public synchronized boolean update(EclipseExportedContent targetItem, IProgressMonitor monitor) throws CoreException {
			if (sourceItemDelta == null)
				return false;
			
			this.targetItem = targetItem;
			this.updated	= false;
			this.monitor	= monitor;

			sourceItemDelta.accept(this);
			return updated;
		}
		

		/**
		 * This callback method is called to incrementally perform an update to the contents of
		 * the target item
		 */
		public boolean visit(IResourceDelta updateDelta) throws CoreException {
			IResource updatedResource = updateDelta.getResource();
			
			monitor.subTask("updating "+getRelativePath(updatedResource));
			
			switch(updateDelta.getKind()) {
				case IResourceDelta.ADDED: {
					return added(updatedResource);
				}
				case IResourceDelta.REMOVED: {
					return removed(updatedResource);
				}
				case IResourceDelta.CHANGED: {
					return changed(updatedResource);
				}
			}
			
			return true;
		}
		
		private boolean removed(IResource sourceResource) throws CoreException {
			IPath resourcePath = getRelativePath(sourceResource);
			targetItem.delete(resourcePath,monitor);
			updated = true;
			return false;
		}
		
		private boolean added(IResource sourceResource) throws CoreException {
			IPath resourcePath = getRelativePath(sourceResource);
			
			/*
			 *	The target packaged item resource may have been already added from 
			 *	another	repository with the same content, so we verify file time stamps
			 *	to avoid unnecessary copies 
			 */
			IResource targetResource = targetItem.getContent(resourcePath);
			if ( (targetResource != null) && targetResource.exists()) {
				if (targetResource.getLocalTimeStamp() >= sourceResource.getLocalTimeStamp())
					return false;
				targetItem.delete(resourcePath,monitor);
			}
			
			targetItem.add(sourceResource,resourcePath,monitor);
			updated = true;
			return false;
		}

		private boolean changed(IResource sourceResource) throws CoreException {
			/* 
			 * We have to scan down to the file level to verify wich files we have
			 * to replace.
			 */
			if (sourceResource.getType() == IResource.FOLDER)
				return true;
			
			IPath resourcePath	= getRelativePath(sourceResource);
			
			/*
			 *	The target packaged item resource may have been already updated from
			 *	another repository with the same content, so we verify file time stamps
			 *	to avoid unnecessary copies 
			 */
			IResource targetResource = targetItem.getContent(resourcePath);
			if ( (targetResource != null) && targetResource.exists()) {
				if (targetResource.getLocalTimeStamp() >= sourceResource.getLocalTimeStamp())
					return false;
			}
			
			targetItem.update(sourceResource,resourcePath,monitor);
			updated = true;
			return false;
		}
		
		/**
		 * Gets the path of a resource relative to the source packaged item
		 * 
		 * @param path
		 * @return
		 */
		protected final IPath getRelativePath(IResource sourceResource) {
			IPath containerPath = sourceItem.getFullPath();
			IPath resourcePath	= sourceResource.getFullPath();
			
			if (containerPath.isPrefixOf(resourcePath))
				resourcePath = resourcePath.removeFirstSegments(containerPath.segmentCount());
			
			return resourcePath;
		}

	}

	/**
	 * Gets the delta associated with this packaged item from a global delta of the repository hosting it,
	 * or null if no modifications are found.
	 * 
	 * @param projectDelta
	 * @return
	 */
	private IResourceDelta getResourceDelta(IResourceDelta projectDelta) {
		return projectDelta != null ? projectDelta.findMember(getPath()) : null;
	}
	
	
	
	
	public static final IPath PACKAGED_ITEMS_AREA = new Path("components");
	
	/**
	 * Gets the relative path to the global zone where packaged items are hosted in this
	 * repository.
	 * 
	 * @return
	 */
	private static IPath getComponentsPath() {
		return PACKAGED_ITEMS_AREA;
	}

	/**
	 * Gets a handle to the folder hosting this packaged item in this repository
	 * 
	 * @return
	 */
	public IFolder getItemFolder() {
		return getRepository().getFolder(getPath());
	}
	
	/**
	 * Creates the zone where the specified item will be packaged.
	 * 
	 * @param monitor
	 * @return
	 * @throws CoreException
	 */
	private IFolder createItemFolder(IProgressMonitor monitor) throws CoreException {
		
		// empty any existing files
		IFolder itemFolder = getItemFolder();
		if (itemFolder.exists())
			itemFolder.delete(true,false,monitor);
		
		// create the empty folder and mark it as a derived
		createFolder(itemFolder,monitor); 
		return itemFolder;
	}
	

	/**
	 * Creates a folder in the hosting repository, ensuring all the parent folders are created
	 * and marked as derived.
	 * 
	 * @param create
	 * @param monitor
	 * @return
	 * @throws CoreException
	 */
	private static void createFolder(IFolder folder, IProgressMonitor monitor) throws CoreException {
		if (folder.exists())
			return;
		
		if (folder.getParent().getType() == IResource.FOLDER)
			createFolder((IFolder)folder.getParent(),monitor);
		
		folder.create(true,true,monitor);
	}

	public void delete(IBuildingContext context) throws CadseException {
		try {
			delete(((CompositeBuildingContext) context).getMonitor());
		} catch (CoreException e) {
			throw new CadseException(e.getMessage(),e);
		}
	}

	public String getExporterType() {
		return exporterType;
	}

	public IExportedContent[] getChildren() {
		return null;
	}

	public boolean isMulti() {
		return false;
	}
	
	public boolean hasChildren() {
		return false;
	}

	public Item getItem() {
		return item;
	}

	public String getItemDisplayName() {
		return item.getType().getItemManager().getDisplayName(item);
	}

	/**
	 * The packaged item identification.
	 * 
	 * @return
	 */
	public CompactUUID getItemIdentification() {
		return item.getId();
	}

	public void setLink(Link l) {
		this.link = l;
	}
	
	public Link getLink() {
		return link;
	}

}
