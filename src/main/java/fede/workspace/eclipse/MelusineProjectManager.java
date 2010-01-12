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
package fede.workspace.eclipse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import fede.workspace.tool.view.WSPlugin;
import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.CompactUUID;
import fr.imag.adele.cadse.core.ContentItem;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.fede.workspace.si.view.View;

public class MelusineProjectManager {

	// /**
	// * Creates an empty Melusine project associated with the eclipse project
	// corresponding to
	// * the item.
	// *
	// * Automatically adds the Melusine nature, which when configured will add
	// the
	// basic builders
	// * to handle composition and validation.
	// *
	// * @param item
	// * @param monitor
	// * @throws CoreException
	// */
	// public static void createMelusineProject(Item item, IProgressMonitor
	// monitor)
	// throws CoreException {
	//
	// IProject project = getProject(item);
	// if (project == null) return;
	//
	// if (!project.exists()) project.create(monitor);
	// project.open(monitor);
	//
	// IProjectDescription description = project.getDescription();
	// List<String> natures = new
	// ArrayList<String>(Arrays.asList(description.getNatureIds()));
	//
	// /*
	// * Add Melusine Nature
	// */
	// if (!description.hasNature(MelusineNature.ID)) {
	// natures.add(MelusineNature.ID);
	// description.setNatureIds(natures.toArray(new String[natures.size()]));
	// project.setDescription(description, monitor);
	// }
	// }

	/**
	 * Creates an empty Melusine project associated with the eclipse project
	 * corresponding to the item.
	 * 
	 * Automatically adds the Melusine nature, which when configured will add
	 * the basic builders to handle composition and validation.
	 * 
	 * @param project
	 * @param monitor
	 * @throws CoreException
	 */
	public static void addMelusineProject(IProject project, IProgressMonitor monitor) throws CadseException {

		try {
			IProjectDescription description = project.getDescription();
			List<String> natures = new ArrayList<String>(Arrays.asList(description.getNatureIds()));

			/*
			 * Add Melusine Nature
			 */
			if (!description.hasNature(MelusineNature.ID)) {
				natures.add(MelusineNature.ID);
				description.setNatureIds(natures.toArray(new String[natures.size()]));
				project.setDescription(description, monitor);
			}
		} catch (CoreException e) {
			throw new CadseException(e);
		}
	}

	// /**
	// * Creates an empty Melusine project associated with the eclipse project
	// corresponding to
	// * the item.
	// *
	// * Automatically adds the Melusine nature, which when configured will add
	// the
	// basic builders
	// * to handle composition and validation.
	// *
	// * @param item
	// * @param monitor
	// * @throws CoreException
	// */
	// public static void createMelusineProject(IProject project, Item item,
	// IProgressMonitor monitor) throws CoreException {
	//
	// if (!createAndOpenProject(project, item, monitor));
	//
	// IProjectDescription description = project.getDescription();
	// List<String> natures = new
	// ArrayList<String>(Arrays.asList(description.getNatureIds()));
	//
	// /*
	// * Add Melusine Nature
	// */
	// if (!description.hasNature(MelusineNature.ID)) {
	// natures.add(MelusineNature.ID);
	// description.setNatureIds(natures.toArray(new String[natures.size()]));
	// project.setDescription(description, monitor);
	// }
	// }

	/**
	 * Creates an empty Melusine project associated with the eclipse project
	 * corresponding to the item.
	 * 
	 * Automatically adds the Melusine nature, which when configured will add
	 * the basic builders to handle composition and validation.
	 * 
	 * @param item
	 * @param monitor
	 * @throws CoreException
	 */
	public static boolean createAndOpenProject(IProject project, IProgressMonitor monitor) throws CadseException {

		try {
			if (project == null) {
				return false;
			}

			if (!project.exists()) {
				project.create(monitor);
			}
			project.open(monitor);
			return true;
		} catch (CoreException e) {
			throw new CadseException(e);
		}
	}

	/**
	 * Gets the Project associated with an item
	 * 
	 * @param item
	 * @return
	 * @throws CoreException
	 */
	public static IProject getProject(Item item) {
		IResource r = getResource(item, true);
		if (r != null) {
			return r.getProject();
		}
		return null;
	}

	/**
	 * Gets the resource asscoiated with an item
	 * 
	 * @param item
	 * @return
	 */
	public static IResource getResource(Item item) {
		return getResource(item, false);
	}

	/**
	 * Gets the resource asscoiated with an item, or one of its containers if
	 * the item doesn't have an eclipse resource associated. This allows to
	 * implement "abstract" items that are not mapped directly to any eclipse
	 * resource.
	 * 
	 * @param item
	 * @return
	 */
	public static IResource getResource(Item item, boolean includeContainers) {
		ContentItem cm = null;
		if (includeContainers) {
			while (item != null) {
				if (item.itemHasContent()) {
					cm = item.getContentItem();
					if (cm != null) {
						Object mainResource = cm.getMainResource();

						if (mainResource instanceof IResource) {
							break;
						}
					}
				}
				item = item.getPartParent();
			}
		}
		if (item == null) {
			return null;
		}
		if (!item.itemHasContent()) {
			return null;
		}

		cm = item.getContentItem();
		if (cm == null) {
			throw new NullPointerException();
		}
		Object mainResource = cm.getMainResource();
		if (mainResource instanceof IResource) {
			return (IResource) mainResource;
		}
		return null;
	}

	/**
	 * Gets the item associated with a resource
	 * 
	 * @param item
	 * @return
	 * @throws CoreException
	 */
	public static CompactUUID getUUIDItem(IResource resource) throws CoreException {
		return WSPlugin.sGetUUIDFromResource(resource);
	}

	public static void addBuilder(IProject project, String builderName) throws CoreException {
		IProjectDescription description = project.getDescription();

		List<ICommand> builders = new ArrayList<ICommand>(Arrays.asList(description.getBuildSpec()));
		boolean updateBuilders = false;

		/*
		 * Add a builder at the end of the builder list.
		 * 
		 */
		ICommand packagingBuilder = description.newCommand();
		packagingBuilder.setBuilderName(builderName);

		if (!builders.contains(packagingBuilder)) {
			builders.add(packagingBuilder);
			updateBuilders = true;
		}

		if (updateBuilders) {
			description.setBuildSpec(builders.toArray(new ICommand[builders.size()]));
			project.setDescription(description, View.getDefaultMonitor());
		}

	}

}
