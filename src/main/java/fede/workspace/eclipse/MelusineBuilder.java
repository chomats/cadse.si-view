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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;

import fede.workspace.tool.view.WSPlugin;
import fr.imag.adele.cadse.core.ContentItem;
import fr.imag.adele.cadse.core.Item;

/**
 * This is the base class for all of Melusines builders.
 * 
 * It implements some basic services needed by most of our builders.
 * 
 * @author vega
 * 
 */
public abstract class MelusineBuilder extends IncrementalProjectBuilder {

	/**
	 * Acknowledged cancelation requests of the currently running build.
	 * 
	 * This will force a full build next time the build is invoked.
	 * 
	 * @param monitor
	 */
	public void checkCanceled(IProgressMonitor monitor) throws OperationCanceledException {
		if (monitor.isCanceled()) {
			forgetLastBuiltState();
			throw new OperationCanceledException();
		}
	}

	/**
	 * Calculate the project dependencies from the list of item dependencies.
	 * 
	 * Updates the project references, if needed, so that the build order is
	 * consistent with the item dependencies order
	 * 
	 * Returns the set of projects corresponding to the needed items.
	 * 
	 * @param neededItems
	 * @param monitor
	 * @return
	 * @throws CoreException
	 */
	protected IProject[] updateProjectReferences(Collection<Item> neededItems, IProgressMonitor monitor)
			throws CoreException {

		/*
		 * calculate the project dependencies from the list of required items
		 */
		Set<IProject> neededProjects = new HashSet<IProject>();
		for (Item neededItem : neededItems) {
			IProject neededProject = MelusineProjectManager.getProject(neededItem);
			if (neededProject == null) {
				continue;
			}

			neededProjects.add(neededProject);
		}

		/*
		 * Update project references so that the build order is consistent with
		 * the item dependencies order
		 */

		IProjectDescription projectDescription = getProject().getDescription();
		List<IProject> projectReferences = new ArrayList<IProject>(Arrays.asList(projectDescription
				.getDynamicReferences()));

		Set<IProject> newProjects = new HashSet<IProject>(neededProjects);
		newProjects.removeAll(projectReferences);

		if (!newProjects.isEmpty()) {
			projectReferences.addAll(newProjects);
			projectDescription.setDynamicReferences(projectReferences.toArray(new IProject[projectReferences.size()]));
			getProject().setDescription(projectDescription, monitor);
		}

		return neededProjects.toArray(new IProject[neededProjects.size()]);

	}

	/**
	 * Gets the appropiate component builder depending on the type of item
	 * 
	 * @return
	 */
	protected ContentItem getContentBuilder(Item item) {
		return item.getContentItem();
	}

	protected Item getItem(IProject project) throws CoreException {
		return WSPlugin.sGetItemFromResource(project);
	}
}
