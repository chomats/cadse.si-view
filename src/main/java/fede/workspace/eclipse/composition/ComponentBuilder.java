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
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import fr.imag.adele.cadse.core.ContentItem;
import fr.imag.adele.cadse.core.Item;
import fede.workspace.eclipse.MelusineBuilder;
import fede.workspace.tool.view.WSPlugin;

/**
 * This builder is in charge of keeping the derived content of an item updated.
 * 
 * @author vega
 *
 */
public class ComponentBuilder extends MelusineBuilder {

	public final static String ID 	= "fede.tool.workspace.view.packaging.builder";

	@Override
	protected void clean(IProgressMonitor monitor) throws CoreException {
		cleanMarkers(monitor);
		ComponentBuildingContext context = new ComponentBuildingContext(this,monitor);
		cleanProjectItems(context);
	}

	@Override
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
		cleanMarkers(monitor);
		ComponentBuildingContext context = new ComponentBuildingContext(this,monitor);
		buildProjectItems(context);
		return null;
	}


	/**
	 * Removes all derived content of the items in this project.
	 * 
	 * @param item
	 * @param monitor
	 */
	private void cleanProjectItems(ComponentBuildingContext context) throws CoreException {

		Item root;
		try {
			root = WSPlugin.sGetItemFromResource(getProject());
		} catch (CoreException e) {
			WSPlugin.log(e.getStatus());
			return;
		}		
		
		if (root == null) {
			report("Melusine Packaging Builder can not be called on a non melusine project {0}",getProject());
			return;
		}
		
		cleanItem(root,context);
		context.getMonitor().done();
	}
	
	/**
	 * Clean the content of an item that is the whole/part hierarchy associated with this build.
	 * 
	 * @param root
	 * @param monitor
	 */
	private void cleanItem(Item item, ComponentBuildingContext context) {
		
		/*
		 * clean recursively (from leaves up to root) the whole/part hierarchy
		 */ 
		for (Item part : item.getPartChildren()) {
			if (part.isReadOnly()) {
				continue;
			}
			IProject p = part.getMainMappingContent(IProject.class);
			if ( p!= null){
				continue;
			}
			cleanItem(part,context);
		}
		
		/* 
		 * If an item is marked read-only we silently ignore it. This is to try to keep the
		 * derived content coherent with the last state before the item was set read-only.
		 */ 
		if (item.isReadOnly()) {
			return;
		}

		if (item.itemHasContent()) {
			/*
			 * Delegate to item manager
			 */
			context.getMonitor().subTask("cleaning " + item.getQualifiedDisplayName());
			ContentItem contentManager = item.getContentItem();
			if (contentManager != null)
				contentManager.clean(context,false);
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
	private void buildProjectItems(ComponentBuildingContext context) throws CoreException {

		Item root;
		try {
			root = WSPlugin.sGetItemFromResource(getProject());
		} catch (CoreException e) {
			WSPlugin.log(e.getStatus());
			return;
		}	
		if (root == null) {
			report("Melusine Packaging Builder can not be called on a non melusine project {0}",getProject());
			return;
		}
		
		buildItem(root,context);
		context.getMonitor().done();
	}

	/**
	 * Builds the content of an item that is the whole/part hierarchy associated with this build.
	 * 
	 * @param root
	 * @param monitor
	 */
	private void buildItem(Item item, ComponentBuildingContext context) {

		/*
		 * build recursively (from leaves up to root) the whole/part hierarchy
		 */ 
		for (Item part : item.getPartChildren()) {
			IProject p = part.getMainMappingContent(IProject.class);
			if ( p!= null){
				continue;
			}
			buildItem(part,context);
		}
		
		/* 
		 * If an item is marked read-only we silently ignore it. This is to try to keep the
		 * derived content coherent with the last state before the item was set read-only.
		 */ 
		if (item.isReadOnly()) {
			forgetLastBuiltState();
			return;
		}

		/*
		 * Delegate to item manager
		 * 
		 *	TODO	Allow for incremental builds, we need some abstract way to represent resource and item deltas
		 *
		 */
		if (item.itemHasContent()) {
			context.getMonitor().subTask("building "+item.getId());
			ContentItem contentManager = item.getContentItem();
			if (contentManager != null)
				contentManager.build(context);
			context.getMonitor().worked(1);
		}
		
	}

	/**
	 * Report a build error by marking the project with the corresponding report.
	 * 
	 * @param description
	 * @param parameters
	 */
	public void report(String description, Object ... parameters) {
		try {
			IMarker marker = ComponentBuilderMarker.mark(getProject());
			ComponentBuilderMarker.setSeverity(marker,IMarker.SEVERITY_ERROR);
			ComponentBuilderMarker.setDescription(marker,description,parameters);
		} catch (CoreException e) {
			printReport(description,parameters);
		}
		
	}

	/**
	 * Fall back to print the report to the standard output in case of any problem trying to
	 * create markers.
	 * 
	 * @param description
	 * @param parameters
	 */
	private void printReport(String description, Object ... parameters) {
		System.out.println("Packaging error "+getProject().getName()+":"+MessageFormat.format(description,parameters));
	}

	/**
	 * Cleans all error markers generated by this builder on a specified resource
	 * 
	 * @param monitor
	 * @throws CoreException
	 */
	private void cleanMarkers(IProgressMonitor monitor) throws CoreException {
		ComponentBuilderMarker.unmark(getProject(),true,IResource.DEPTH_INFINITE);
	}

}
