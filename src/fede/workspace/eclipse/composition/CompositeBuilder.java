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
package fede.workspace.eclipse.composition;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;

import fede.workspace.eclipse.MelusineBuilder;
import fede.workspace.tool.view.WSPlugin;
import fr.imag.adele.cadse.core.ContentItem;
import fr.imag.adele.cadse.core.Item;

/**
 * This builder is in charge of calculating the derived content of a composite
 * item.
 * 
 * @author vega
 * 
 */
public class CompositeBuilder extends MelusineBuilder {

	public final static String	ID	= "fede.tool.workspace.view.composition.builder";

	@Override
	protected void clean(IProgressMonitor monitor) throws CoreException {
		cleanMarkers(monitor);
		CompositeBuildingContext context = new CompositeBuildingContext(this, monitor);
		cleanProjectItems(context);
	}

	@Override
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
		cleanMarkers(monitor);
		CompositeBuildingContext context = new CompositeBuildingContext(this, monitor);
		return buildProjectItems(context);
	}

	/**
	 * Removes all derived content of the items in this project.
	 * 
	 * @param item
	 * @param monitor
	 */
	private void cleanProjectItems(CompositeBuildingContext context) throws CoreException {

		IProject p = getProject();
		Item root = getItem(p);
		if (root == null) {
			throw new CoreException(new Status(Status.ERROR, WSPlugin.PLUGIN_ID, "Cannot found item from "
					+ p.getName()));
		}
		cleanItem(root, context);
		context.getMonitor().done();
	}

	/**
	 * Clean the content of an item that is in the whole/part hierarchy
	 * associated with this build.
	 * 
	 * @param root
	 * @param monitor
	 */
	private void cleanItem(Item item, CompositeBuildingContext context) {

		/*
		 * clean recursively (from leaves up to root) the whole/part hierarchy
		 */
		for (Item part : item.getPartChildren()) {
			if (!part.isComposite()) {
				continue;
			}
			IProject p = part.getMainMappingContent(IProject.class);
			if (p != null) {
				continue;
			}
			cleanItem(part, context);
		}

		/*
		 * skip non composite items
		 */
		if (!item.isComposite()) {
			return;
		}

		/*
		 * If an item is marked read-only we silently ignore it. This is to try
		 * to keep the derived content coherent with the last state before the
		 * item was set read-only.
		 */
		if (item.isReadOnly()) {
			return;
		}

		if (item.itemHasContent()) {
			/*
			 * Delegate to item manager
			 */
			context.getMonitor().subTask("cleaning components of " + item.getQualifiedDisplayName());

			ContentItem contentManager = item.getContentItem();
			contentManager.clean(context, true);
			context.getMonitor().worked(1);
		}

	}

	/**
	 * Builds the derived content of all items in this project.
	 * 
	 * @param item
	 * @param monitor
	 * @throws CoreException
	 */
	private IProject[] buildProjectItems(CompositeBuildingContext context) throws CoreException {

		IProject p = getProject();
		Item root = getItem(p);
		if (root == null) {
			return new IProject[0];
		}
		List<Item> neededItems = buildItem(root, context);
		context.getMonitor().done();

		return updateProjectReferences(neededItems, context.getMonitor());
	}

	/**
	 * Builds the content of an item that is in the whole/part hierarchy
	 * associated with this build.
	 * 
	 * @param root
	 * @param monitor
	 */
	private List<Item> buildItem(Item item, CompositeBuildingContext context) {

		List<Item> neededItems = new ArrayList<Item>();

		/*
		 * build recursively (from leaves up to root) the whole/part hierarchy
		 */
		for (Item part : item.getPartChildren()) {
			if (!part.isComposite()) {
				continue;
			}
			IProject p = part.getMainMappingContent(IProject.class);
			if (p != null) {
				continue;
			}
			neededItems.addAll(buildItem(part, context));
		}

		/*
		 * skip non composite items
		 */
		if (!item.isComposite()) {
			return neededItems;
		}

		for (Item ComponentRef : item.getComponents()) {
			if (ComponentRef.isResolved()) {
				neededItems.add(ComponentRef);
			}
		}

		/*
		 * Delegate to item manager
		 * 
		 * TODO Allow for incremental builds, we need some abstract way to
		 * represent resource and item deltas
		 * 
		 */
		if (item.itemHasContent()) {
			context.getMonitor().subTask("composing " + item.getQualifiedDisplayName());

			ContentItem contentManager = item.getContentItem();
			contentManager.compose(context);

			context.getMonitor().worked(1);
		}

		return neededItems;
	}

	/**
	 * Report a build error not related to any particular item.
	 * 
	 * @param description
	 * @param parameters
	 */
	protected void report(String description, Object... parameters) {
		try {
			IMarker marker = CompositeBuilderMarker.mark(getProject());
			CompositeBuilderMarker.setSeverity(marker, IMarker.SEVERITY_ERROR);
			CompositeBuilderMarker.setDescription(marker, description, parameters);
		} catch (CoreException e) {
			printReport(description, parameters);
		}

	}

	/**
	 * Fall back to print the report to the standard output in case of any
	 * problem trying to create markers.
	 * 
	 * @param description
	 * @param parameters
	 */
	private void printReport(String description, Object... parameters) {
		System.out.println("Composition error " + getProject().getName() + ":"
				+ MessageFormat.format(description, parameters));
	}

	/**
	 * Cleans all error markers generated by this builder on a specified
	 * resource
	 * 
	 * @param monitor
	 * @throws CoreException
	 */
	private void cleanMarkers(IProgressMonitor monitor) throws CoreException {
		CompositeBuilderMarker.unmark(getProject(), true, IResource.DEPTH_INFINITE);
	}

}
