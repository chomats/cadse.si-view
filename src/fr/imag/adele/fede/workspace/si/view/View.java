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
package fr.imag.adele.fede.workspace.si.view;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ILock;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import fede.workspace.eclipse.core.CadseDialogPage;
import fede.workspace.tool.eclipse.MappingManager;
import fede.workspace.tool.eclipse.Messages;
import fede.workspace.tool.view.ItemInViewer;
import fede.workspace.tool.view.WSResourceChangeListener;
import fede.workspace.tool.view.adapter.WSAdapterFactory;
import fede.workspace.tool.view.adapter.WSAdapterItemInViewFactory;
import fr.imag.adele.cadse.core.CadseDomain;
import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.CadseRuntime;
import fr.imag.adele.cadse.core.CompactUUID;
import fr.imag.adele.cadse.core.ContentItem;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemState;
import fr.imag.adele.cadse.core.LogicalWorkspace;
import fr.imag.adele.cadse.core.WSModelState;
import fr.imag.adele.cadse.core.WorkspaceListener;
import fr.imag.adele.cadse.core.delta.ImmutableItemDelta;
import fr.imag.adele.cadse.core.delta.ImmutableWorkspaceDelta;
import fr.imag.adele.cadse.eclipse.view.ViewAfterInit;
import fr.imag.adele.fede.workspace.as.eclipse.IEclipse;
import fr.imag.adele.fede.workspace.as.initmodel.IInitModel;
import fr.imag.adele.fede.workspace.as.persistence.IPersistence;
import fr.imag.adele.fede.workspace.as.platformeclipse.IPlatformEclipse;
import fr.imag.adele.fede.workspace.as.test.ITest;

/**
 * @generated
 */
public class View implements IEclipse {

	/**
	 * @generated
	 */
	CadseDomain							workspaceCU;
	/**
	 * @generated
	 */
	IPersistence						persitence;
	/**
	 * @generated
	 */
	ITest								testService;
	/**
	 * @generated
	 */
	IInitModel							initModel;

	/**
	 * @generated
	 */
	IPlatformEclipse					platformEclipse;

	/** The Constant PLUGIN_ID. */
	public static final String			PLUGIN_ID			= "fede.tool.workspace.view";				//$NON-NLS-1$

	private static View					INSTANCE			= null;

	/** The Constant PROVIDER. */
	public static final String			PROVIDER			= "ItemTypeProvider";						//$NON-NLS-1$

	/** The Constant MODEL_EXTENSION. */
	public static final String			MODEL_EXTENSION		= "modelextension";						//$NON-NLS-1$

	/** The Constant ITEM_ID_PROPERTY. */
	public final static QualifiedName	ITEM_ID_PROPERTY	= new QualifiedName(PLUGIN_ID, "item-id");

	/** The init provider. */
	boolean								initProvider		= false;

	// private IWSViewer theItemViewer = new NullWSViewer();

	/** The notifier. */
	EclipseToolListener					notifier			= new EclipseToolListener();

	public static View getInstance() {
		return INSTANCE;
	}

	public void start() {
		INSTANCE = this;

		Runnable r = new Runnable() {
			public void run() {
				while (getWorkspaceDomain() == null || getWorkspaceDomain().getLogicalWorkspace() == null) {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				getPlatformEclipse().getLocation(true);
				Platform.getAdapterManager().registerAdapters(new WSAdapterFactory(), Item.class);
				try {
					Platform.getAdapterManager().registerAdapters(new WSAdapterItemInViewFactory(), ItemInViewer.class);
				} catch (RuntimeException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				try {
					ResourcesPlugin.getWorkspace().addResourceChangeListener(new WSResourceChangeListener(),
							IResourceChangeEvent.PRE_DELETE | IResourceChangeEvent.POST_CHANGE);

				} catch (Throwable e) {
					e.printStackTrace();
				}

				try {
					getWorkspaceDomain().getLogicalWorkspace().addListener(notifier, 0xFFFFFF);
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				resetUIViews();

			}
		};
		Thread t = new Thread(r, "WSView init start");
		t.start();

	}

	public void resetUIViews() {
		// reset view
		synchronized (_viewAfterInitArray) {
			if (_viewAfterInitArray.size() == 0) {
				return;
			}
			if (!PlatformUI.isWorkbenchRunning()) {
				return;
			}
			final ViewAfterInit[] l = _viewAfterInitArray.toArray(new ViewAfterInit[_viewAfterInitArray.size()]);
			_viewAfterInitArray.clear();
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					for (ViewAfterInit viewAfterInit : l) {
						viewAfterInit.afterInit();
					}

				}
			});
		}
	}

	public void stop() {
		INSTANCE = null;
	}

	public IInitModel getInitModel() {
		return initModel;
	}

	public IPersistence getPersitence() {
		return persitence;
	}

	/**
	 * Gets the workspace domain.
	 * 
	 * @return the workspace domain
	 */
	public CadseDomain getWorkspaceDomain() {
		return this.workspaceCU;
	}

	public static boolean isStarted() {
		return INSTANCE != null;
	}

	public LogicalWorkspace getWorkspaceLogique() {
		if (workspaceCU == null) {
			return null;
		}
		return workspaceCU.getLogicalWorkspace();
	}

	public IPlatformEclipse getPlatformEclipse() {
		return platformEclipse;
	}

	public ITest getTestService() {
		return testService;
	}

	/**
	 * The Class JobInitMapping.
	 * 
	 * @author <a href="mailto:stephane.chomat@imag.fr">Stephane Chomat</a>
	 */
	private final class JobInitMapping extends Job {

		/** The model. */
		LogicalWorkspace	model;

		/**
		 * Instantiates a new job init mapping.
		 * 
		 * @param model
		 *            the model
		 */
		public JobInitMapping(LogicalWorkspace model) {
			super("init mapping");
			this.model = model;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @seeorg.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.
		 * IProgressMonitor)
		 */
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			for (Item item : model.getItems()) {
				try {
					item.getContentItem(); // force to load content
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
			return Status.OK_STATUS;
		}

	}

	/**
	 * The Class JobInitItemsMapping.
	 * 
	 * @author <a href="mailto:stephane.chomat@imag.fr">Stephane Chomat</a>
	 */
	private final class JobInitItemsMapping extends Job {

		/** The items. */
		Set<Item>	items;

		/**
		 * Instantiates a new job init items mapping.
		 * 
		 * @param items
		 *            the items
		 */
		public JobInitItemsMapping(Set<Item> items) {
			super("init somme items");
			this.items = items;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @seeorg.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.
		 * IProgressMonitor)
		 */
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			for (Item item : items) {
				try {
					getResourceFromItem(item, false, false);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
			return Status.OK_STATUS;
		}

	}

	/**
	 * The Class JobBuildMapping.
	 * 
	 * @author <a href="mailto:stephane.chomat@imag.fr">Stephane Chomat</a>
	 */
	private final class JobBuildMapping extends Job {

		/** The items. */
		Collection<ImmutableItemDelta>	items;

		/**
		 * Instantiates a new job build mapping.
		 * 
		 * @param name
		 *            the name
		 */
		public JobBuildMapping(Collection<ImmutableItemDelta> name) {
			super("build mapping");
			this.items = name;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @seeorg.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.
		 * IProgressMonitor)
		 */
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			for (ImmutableItemDelta itemDelta : items) {
				if (itemDelta.hasSetAttributes() || itemDelta.hasResolvedIncomingLink()
						|| itemDelta.hasUnresolvedIncomingLink()) {
					try {
						buildModifiedItem(itemDelta.getItem(), true, monitor);

					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			return Status.OK_STATUS;
		}

	}

	/**
	 * The listener interface for receiving eclipseTool events. The class that
	 * is interested in processing a eclipseTool event implements this
	 * interface, and the object created with that class is registered with a
	 * component using the component's
	 * <code>addEclipseToolListener<code> method. When
	 * the eclipseTool event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see EclipseToolEvent
	 * @author <a href="mailto:stephane.chomat@imag.fr">Stephane Chomat</a>
	 */
	private final class EclipseToolListener extends WorkspaceListener {

		@Override
		public void workspaceChanged(ImmutableWorkspaceDelta delta) {
			// IProgressMonitor defaultMonitor = getDefaultMonitor();

			// create item
			if (delta.getModelstate() != null) {
				if (WSModelState.RUN == delta.getModelstate()) {
					JobInitMapping initMapping = new JobInitMapping(delta.getCadseDomain().getLogicalWorkspace());
					// initMapping.setRule(ResourcesPlugin.getWorkspace().getRoot());
					initMapping.schedule();
					return;
				}
			}
			if (delta.getItems() != null) {
				Set<Item> modifiedItems = new HashSet<Item>();
				for (ImmutableItemDelta itemDelta : delta.getItems()) {
					if (itemDelta.isCreated()) {
						modifiedItems.add(itemDelta.getItem());
					}
				}
				if (modifiedItems.size() != 0) {
					JobInitItemsMapping job = new JobInitItemsMapping(modifiedItems);
					job.schedule();
				}
				JobBuildMapping job = new JobBuildMapping(delta.getItems());
				job.schedule();
			}

		}
	}

	/**
	 * Builds a modified item. Builders are charged of validation, classpath
	 * calculation and composition management.
	 * 
	 * @param item
	 *            the item
	 * @param forceBuilder
	 *            the force builder
	 * @param monitor
	 *            the monitor
	 * 
	 * @throws CoreException
	 *             the core exception
	 */
	public void buildModifiedItem(Item item, boolean forceBuilder, IProgressMonitor monitor) throws CoreException {
		IResource resource = getResourceFromItem(item, false, false);
		if (resource == null) {
			return;
		}

		/*
		 * Touch the associated resource, this will trigger the validation and
		 * composition builders
		 */
		if (resource instanceof IProject) {
			IProject pr = (IProject) resource;
			if (!pr.exists()) {
				pr.create(monitor);
			}
			if (!pr.isOpen()) {
				pr.open(monitor);
			}
		}
		if (forceBuilder && resource.exists()) {
			resource.touch(monitor);
		}
	}

	/**
	 * Inititialisation of an item. It associates the eclipse resource to the
	 * item using the IEclipseResource extension
	 * 
	 * @param item
	 *            the item
	 */
	public void init(Item item) {
		// ((IEclipseResource)item).setEclipseResource(getItemProvider(item).getEclipseResource(item));
	}

	/**
	 * Log.
	 * 
	 * @param status
	 *            the status
	 */
	public void log(IStatus status) {
		Platform.getLog(Platform.getBundle(PLUGIN_ID)).log(status);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fede.workspace.role.eclipse.EclipseViewRole#setReadOnly(fr.imag.adele
	 *      .cadse.core.Item, boolean)
	 */
	public void setReadOnly(Item item, boolean readonly) {
		if (item.getState() != ItemState.CREATED) {
			return;
		}
		refresh(item);
		IResource r;
		try {
			r = getResourceFromItem(item, false, false);
			if (r != null && !readonly) {
				setReadOnly(r, readonly);
			}
		} catch (CoreException e) {
			log(new Status(Status.ERROR, "SI.Workspace.Eclipse", 0, MessageFormat.format("Cannot set readonly on {0}.",
					item.getId()), e));
		}
	}

	/**
	 * Sets the read only.
	 * 
	 * @param r
	 *            the r
	 * @param readonly
	 *            the readonly
	 * 
	 * @throws CoreException
	 *             the core exception
	 */
	private void setReadOnly(IResource r, boolean readonly) throws CoreException {
		ResourceAttributes atts = r.getResourceAttributes();
		if (atts != null) {
			atts.setReadOnly(readonly);
			r.setResourceAttributes(atts);
		}
		if (r instanceof IContainer) {
			IContainer c = (IContainer) r;
			IResource[] m = c.members();
			if (m != null) {
				for (int i = 0; i < m.length; i++) {
					setReadOnly(m[i], readonly);
				}
			}
		}

	}

	/**
	 * Get the item associated with an eclipse resource.
	 * 
	 * @param resource
	 *            the resource
	 * 
	 * @return the item from resource
	 * 
	 * @throws CoreException
	 *             the core exception
	 */
	public Item getItemFromResource(IResource resource) throws CoreException {
		CompactUUID id = getUUIDFromResource(resource);
		if (id == null) {
			return null;
		}

		// get Item from identification
		CadseDomain wd = this.workspaceCU;
		if (wd == null) {
			return null;
		}

		try {

			if (wd.getLogicalWorkspace().getState() != WSModelState.RUN) {
				return null;
			}
			return wd.getLogicalWorkspace().getItem(id);
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Get the item associated with an eclipse resource.
	 * 
	 * @param resource
	 *            the resource
	 * 
	 * @return the UUID from resource
	 * 
	 * @throws CoreException
	 *             the core exception
	 */
	public CompactUUID getUUIDFromResource(IResource resource) throws CoreException {
		if (resource == null) {
			return null;
		}

		if (!resource.exists()) {
			return null;
		}

		String idStr = getPersistanceID(resource);
		// If there is no item identification associated with this resource
		// check parent resource
		if (idStr == null) {
			return getUUIDFromResource(resource.getParent());
		}

		CompactUUID id;
		try {
			id = CompactUUID.fromString(idStr);
			return id;
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	private String getPersistanceID(IResource resource) throws CoreException {

		// get item identification from the resource properties
		if (!resource.isAccessible()) {
			return null;
		}
		if (!resource.isLocal(IResource.DEPTH_ZERO)) {
			return null;
		}

		try {
			return resource.getPersistentProperty(ITEM_ID_PROPERTY);
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Gets the eclipse resource associated with an item.
	 * 
	 * If there is no associated resource and the item is part of another item,
	 * it is possible to request the resource associated with the first
	 * containing ancestor having one.
	 * 
	 * @param item
	 *            the item
	 * @param includeContainers
	 *            the include containers
	 * @param throwifNull
	 *            the throwif null
	 * 
	 * @return non null value
	 * 
	 * @throws CoreException
	 *             the core exception
	 */
	public IResource getResourceFromItem(Item item, boolean includeContainers, boolean throwifNull)
			throws CoreException {

		if (item == null) {
			if (throwifNull) {
				throw new AssertionError("The item is null : illegal argument.");
			} else {
				return null;
			}
		}

		if (!item.isResolved()) {
			return null;
		}

		ContentItem cm = item.getContentItem();
		if (cm != null) {
			Object mr = cm.getMainResource();
			if (mr instanceof IResource) {
				return (IResource) mr;
			}
		}
		// if no associated resource, try parent item in the whole-part
		// relationship
		if (includeContainers && item.isPartItem()) {
			return getResourceFromItem(item.getPartParent(), includeContainers, throwifNull);
		} else if (throwifNull) {
			throw new AssertionError("The item has no content.");
		} else {
			return null;
		}
	}

	/**
	 * Gets the eclipse project asscoiated with an item.
	 * 
	 * If the eclispe resource associated with the item is a project, returns
	 * it. Otherwise, returns the project enclosing the eclipse resource
	 * associated with the item.
	 * 
	 * @param item
	 *            the item
	 * @param includeContainers
	 *            the include containers
	 * 
	 * @return the project from item
	 * 
	 * @throws CoreException
	 *             the core exception
	 */
	public IProject getProjectFromItem(Item item, boolean includeContainers) throws CoreException {
		IResource resource = getResourceFromItem(item, includeContainers, false);
		return (resource != null) ? resource.getProject() : null;
	}

	/**
	 * Deletes the specified eclipse resource, keeping the contents in the file
	 * system if specified.
	 * 
	 * @param resource
	 *            the resource
	 * @param keepContent
	 *            the keep content
	 * @param monitor
	 *            the monitor
	 * 
	 * @throws CoreException
	 *             the core exception
	 */
	public void deleteEclipseResource(IResource resource, boolean keepContent, IProgressMonitor monitor)
			throws CoreException {

		if (resource == null) {
			return;
		}

		switch (resource.getType()) {
			case IResource.PROJECT: {
				IProject project = (IProject) resource;

				project.delete(!keepContent, true, monitor);
				break;
			}

			default: {
				resource.delete(true, monitor);
				break;
			}
		}

	}

	/**
	 * Returns the File to use for saving and restoring the last state for the
	 * given project. Not used now. It's an sample to save a specific data for a
	 * given project. This data is removed by eclipse automatiquement.
	 * 
	 * @param project
	 *            the project
	 * 
	 * @return the state file
	 */
	public File getStateFile(IProject project) {
		if (!project.exists()) {
			return null;
		}
		IPath workingLocation = project.getWorkingLocation(PLUGIN_ID);
		return workingLocation.append("state.dat").toFile(); //$NON-NLS-1$
	}

	/**
	 * Refreshes the content of the eclipse resource associated with an item
	 * 
	 * TODO We should give the item manager some kind of notification.
	 * 
	 * @param item
	 *            the item
	 */
	public void refresh(Item item) {

		try {
			IProgressMonitor defaultMonitor = getDefaultMonitor();

			IResource resource = getResourceFromItem(item, false, false);
			if (resource == null) {
				return;
			}
			resource.refreshLocal(IResource.DEPTH_INFINITE, defaultMonitor);
		} catch (CoreException e) {
			ErrorDialog.openError(new Shell(), MessageFormat.format("Error refreshing item {0}", item.getId()), e
					.getMessage(), e.getStatus());
		}
	}

	/**
	 * Notify the modification of the content of an item
	 * 
	 * TODO We should give the item manager some kind of notification.
	 * 
	 * @param item
	 *            the item
	 */
	public void notifieChangedContent(Item item) {

	}

	/**
	 * Get the names of ItemTypes form an element, element.name = provider
	 * 
	 * @param element
	 *            the element
	 * 
	 * @return the item type names
	 */
	String[] getItemTypeNames(IConfigurationElement element) {
		IConfigurationElement[] children = element.getChildren();
		ArrayList<String> adapters = new ArrayList<String>(children.length);
		for (int i = 0; i < children.length; i++) {
			// ignore unknown children for forward compatibility
			if ("type".equals(children[i].getName())) { //$NON-NLS-1$
				String type = children[i].getAttribute("name"); //$NON-NLS-1$
				if (type != null) {
					adapters.add(type);
				}
			}
		}
		if (adapters.isEmpty()) {
			logErrorProvider(element);
		}
		return adapters.toArray(new String[adapters.size()]);
	}

	/**
	 * show an error if the extension is bad.
	 * 
	 * @param element
	 *            the element
	 */
	void logErrorProvider(IConfigurationElement element) {
		String msg = NLS.bind(Messages.provider_badProvider, element.getContributor().getName());
		log(new Status(IStatus.ERROR, PLUGIN_ID, 1, msg, null));

	}

	/*
	 * Thread context variables allowing other plugins to indirectly specify the
	 * progress monitor and delete options.
	 * 
	 * WARNING: This short circuits the common universe, it is an indirect way
	 * to communicate between the model specific plugins and the workspace
	 * eclipse tool
	 */
	/** The tl. */
	static ThreadLocal<IProgressMonitor>	tl			= new ThreadLocal<IProgressMonitor>();

	/** The tl_delete. */
	static ThreadLocal<Boolean[]>			tl_delete	= new ThreadLocal<Boolean[]>();

	/**
	 * Sets the default monitor.
	 * 
	 * @param monitor
	 *            the new default monitor
	 */
	public static void setDefaultMonitor(IProgressMonitor monitor) {
		tl.set(monitor);
	}

	/**
	 * Gets the default monitor.
	 * 
	 * @return the default monitor
	 */
	public static IProgressMonitor getDefaultMonitor() {
		IProgressMonitor defaultMontior = tl.get();
		if (defaultMontior == null) {
			defaultMontior = new NullProgressMonitor();
		}
		return defaultMontior;
	}

	/**
	 * Unset default monitor.
	 */
	public static void unsetDefaultMonitor() {
		tl.remove();
	}

	/**
	 * Sets the delete option.
	 * 
	 * @param eclipse
	 *            the eclipse
	 * @param content
	 *            the content
	 */
	public static void setDeleteOption(boolean eclipse, boolean content) {
		tl_delete.set(new Boolean[] { eclipse, content });
	}

	/**
	 * Gets the delete option.
	 * 
	 * @return the delete option
	 */
	public static Boolean[] getDeleteOption() {
		return tl_delete.get();
	}

	/**
	 * Unset delete option.
	 */
	public static void unsetDeleteOption() {
		tl_delete.remove();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fede.workspace.role.eclipse.EclipseViewRole#log(java.lang.String,
	 *      java.lang.String, java.lang.Throwable)
	 */
	public void log(String type, String message, Throwable e) {
		int typeInt = getTypeFrom(type);
		ResourcesPlugin.getPlugin().getLog().log(new Status(typeInt, "CadseDomain", 0, message, e));
	}

	public void log(String type, String message, Throwable e, Item item) {
		int typeInt = getTypeFrom(type);
		final Status status = new Status(typeInt, "CadseDomain", 0, message, e);
		ResourcesPlugin.getPlugin().getLog().log(status);
	}

	/**
	 * Gets the type from.
	 * 
	 * @param type
	 *            the type
	 * 
	 * @return the type from
	 */
	private int getTypeFrom(String type) {
		if ("warning".equals(type)) {
			return IStatus.WARNING;
		}
		if ("error".equals(type)) {
			return IStatus.ERROR;
		}
		if ("info".equals(type)) {
			return IStatus.INFO;
		}

		return IStatus.ERROR;
	}

	/** The lock. */
	ILock	lock	= Job.getJobManager().newLock();

	/**
	 * Note that <tt>endRule</tt> must be called even if <tt>beginRule</tt>
	 * fails.
	 * 
	 * @param rule
	 *            the rule
	 */
	public void beginRule(Object rule) {
		// ILock
		Job.getJobManager().beginRule(ResourcesPlugin.getWorkspace().getRoot(), getDefaultMonitor());
		// lock.acquire();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fede.workspace.role.eclipse.EclipseViewRole#endRule(java.lang.Object)
	 */
	public void endRule(Object rule) {
		// ISchedulingRule sr = (ISchedulingRule) rule;
		Job.getJobManager().endRule(ResourcesPlugin.getWorkspace().getRoot());
		// lock.release();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fede.workspace.role.eclipse.EclipseViewRole#setItemPersistenceID(java
	 *      .lang.String, fr.imag.adele.cadse.core.Item)
	 */
	public void setItemPersistenceID(String projectName, Item item) throws CadseException {
		try {
			IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			MappingManager.createProject(p, null, null, true);
			setItemPersistenceID(p, item);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fede.workspace.role.eclipse.EclipseViewRole#copyResource(fr.imag.adele
	 *      .cadse.core.Item, java.lang.String, java.net.URL)
	 */
	public void copyResource(Item item, String path, URL url) throws CadseException {
		Path epath = new Path(path);
		String projectName = epath.segment(0);
		IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		IFile f = p.getFile(epath.removeFirstSegments(1));
		try {
			IProgressMonitor monitor = getDefaultMonitor();
			MappingManager.createParentContainerFolder(f.getParent(), monitor);

			if (!f.exists()) {
				f.create(url.openStream(), true, monitor);
			} else {
				f.setContents(url.openStream(), true, false, monitor);
			}

		} catch (CoreException e) {
			throw new CadseException(e.getMessage(), e);
		} catch (IOException e) {
			throw new CadseException(e.getMessage(), e);
		}
	}

	/**
	 * set the persisance ID.
	 * 
	 * @param f
	 *            A resource a project
	 * @param item
	 *            the item
	 */
	public static void setItemPersistenceID(IResource f, Item item) {
		try {
			if (f.exists()) {
				if (f.getType() == IResource.PROJECT) {
					IProject p = (IProject) f;
					if (!p.isOpen()) {
						p.open(getDefaultMonitor());
					}
				}
				f.setPersistentProperty(ITEM_ID_PROPERTY, item.getId().toString());
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public File getLocation() {
		return platformEclipse.getLocation(true);
	}

	public CadseRuntime[] openDialog(boolean askToErase) {
		return CadseDialogPage.openDialog(askToErase);
	}

	static ArrayList<ViewAfterInit>	_viewAfterInitArray	= new ArrayList<ViewAfterInit>();

	public static void addAfterListener(ViewAfterInit viewAfterInit) {
		synchronized (_viewAfterInitArray) {
			_viewAfterInitArray.add(viewAfterInit);
		}
	}
}
