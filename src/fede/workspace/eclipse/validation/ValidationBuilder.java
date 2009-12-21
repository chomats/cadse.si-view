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
package fede.workspace.eclipse.validation;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;

import fede.workspace.eclipse.MelusineBuilder;
import fede.workspace.eclipse.MelusineProjectManager;
import fede.workspace.tool.view.WSPlugin;
import fr.imag.adele.cadse.core.IItemManager;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.LogicalWorkspace;
import fr.imag.adele.cadse.core.impl.CadseCore;

/**
 * This builder perform validation of the items associated with a project.
 * 
 * Errors are reported as markers in the problem view, and associated with the
 * enclosing eclipse project.
 * 
 * @author vega
 * 
 */
public class ValidationBuilder extends MelusineBuilder implements IItemManager.ProblemReporter {

	public final static String	ID		= "fede.tool.workspace.view.validation.builder";

	boolean						isfirst	= true;												;

	@Override
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {

		LogicalWorkspace model = CadseCore.getLogicalWorkspace();
		if (isfirst && model != null) {
			ValidationMarker.unmark(ResourcesPlugin.getWorkspace().getRoot(), true, IResource.DEPTH_INFINITE);

			for (Item aItem : model.getItems()) {
				try {
					if (aItem == null && aItem.getType() == null) continue;
					performValidation(aItem, monitor);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
			isfirst = false;
			return new IProject[0];
		}

		clearMarkers(monitor);
		IProject p = getProject();
		// verify there is a Melusine item associated with this project
		Item item = getItem(p);
		if (item == null) {
			throw new CoreException(new Status(Status.ERROR, WSPlugin.PLUGIN_ID, "Cannot found item from "
					+ p.getName()));
		}

		List<Item> neededItems = validate(item, monitor);
		return updateProjectReferences(neededItems, monitor);
	}

	@Override
	protected void clean(IProgressMonitor monitor) throws CoreException {
		super.clean(monitor);

		// blindly clear all validation markers from this project
		clearMarkers(monitor);
	}

	/**
	 * validates an item and all its contained items recursively.
	 * 
	 * If an item is marked read-only we silently ignore it. This is to try to
	 * keep the markers coherent with the last state before the item was marked
	 * read-donly.
	 * 
	 * @param tem
	 * @param monitor
	 * @throws CoreException
	 */
	private List<Item> validate(Item item, IProgressMonitor monitor) throws CoreException {

		if (item.isReadOnly()) {
			return Collections.emptyList();
		}

		monitor = new SubProgressMonitor(monitor, 1, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
		monitor.beginTask("Validating " + item.getDisplayName(), item.getPartChildren().size() + 1);

		List<Item> neededItems = new LinkedList<Item>();

		// clear global dans le build...
		// no clear any existing reports and validate the item
		// clearReports(item, monitor);

		neededItems.addAll(performValidation(item, monitor));

		// recurse into contained items
		for (Item partItem : item.getPartChildren()) {
			IProject p = partItem.getMainMappingContent(IProject.class);
			if (p != null) {
				monitor.worked(1);
				continue;
			}
			neededItems.addAll(validate(partItem, monitor));
		}

		monitor.done();

		return neededItems;
	}

	/**
	 * Invoke the item manager to validate the item and report all problems
	 * found as markers on the associated resource.
	 * 
	 * Returns the list of all other items needed to validate the specified
	 * item.
	 * 
	 * @param item
	 * @param monitor
	 * @return
	 * @throws CoreException
	 */
	private List<Item> performValidation(Item item, IProgressMonitor monitor) throws CoreException {
		monitor.subTask("Item Validation " + item.getName());
		IItemManager manager = item.getType().getItemManager();
		List<Item> dependantOnItems = manager.validate(item, this);
		if (dependantOnItems == null) {
			dependantOnItems = Collections.emptyList();
		}
		monitor.worked(1);
		return dependantOnItems;
	}

	/**
	 * Report a validation error.
	 * 
	 * This callback method is invoked by managers to report all validation
	 * problems found.
	 * 
	 * This builder creates a problem marker for each report, this allows other
	 * plugins to add quickfixes to try to correct the invalid item.
	 */
	public void report(Item item, int errorCode, String description, Object... parameters) {
		try {
			IResource eclipseResource = MelusineProjectManager.getResource(item, true);
			if (eclipseResource == null || !eclipseResource.exists()) {
				printReport(item, errorCode, description, parameters);
				eclipseResource = ResourcesPlugin.getWorkspace().getRoot();
			}

			IMarker marker = ValidationMarker.mark(eclipseResource);
			ValidationMarker.setItem(marker, item);
			ValidationMarker.setErrorCode(marker, errorCode);
			ValidationMarker.setDescription(marker, description, parameters);

		} catch (CoreException e) {
			printReport(item, errorCode, description, parameters);
		}
	}

	public void error(Item item, int errorCode, String description, Object... parameters) {
		try {
			IResource eclipseResource = MelusineProjectManager.getResource(item, true);
			if (eclipseResource == null || !eclipseResource.exists()) {
				printReport(item, errorCode, description, parameters);
				return;
			}

			IMarker marker = ValidationMarker.mark(eclipseResource);
			ValidationMarker.setItem(marker, item);
			ValidationMarker.setErrorCode(marker, errorCode, IMarker.SEVERITY_ERROR);
			ValidationMarker.setDescription(marker, description, parameters);

		} catch (CoreException e) {
			printReport(item, errorCode, description, parameters);
		}
	}

	public void info(Item item, int errorCode, String description, Object... parameters) {
		try {
			IResource eclipseResource = MelusineProjectManager.getResource(item, true);
			if (eclipseResource == null || !eclipseResource.exists()) {
				printReport(item, errorCode, description, parameters);
				return;
			}

			IMarker marker = ValidationMarker.mark(eclipseResource);
			ValidationMarker.setItem(marker, item);
			ValidationMarker.setErrorCode(marker, errorCode, IMarker.SEVERITY_INFO);
			ValidationMarker.setDescription(marker, description, parameters);

		} catch (CoreException e) {
			printReport(item, errorCode, description, parameters);
		}
	}

	public void warning(Item item, int errorCode, String description, Object... parameters) {
		try {
			IResource eclipseResource = MelusineProjectManager.getResource(item, true);
			if (eclipseResource == null || !eclipseResource.exists()) {
				printReport(item, errorCode, description, parameters);
				return;
			}

			IMarker marker = ValidationMarker.mark(eclipseResource);
			ValidationMarker.setItem(marker, item);
			ValidationMarker.setErrorCode(marker, errorCode, IMarker.SEVERITY_WARNING);
			ValidationMarker.setDescription(marker, description, parameters);

		} catch (CoreException e) {
			printReport(item, errorCode, description, parameters);
		}
	}

	/**
	 * Report a generic build error, not related to an item or validation error.
	 * 
	 * @param description
	 * @param parameters
	 */
	private void report(String description, Object... parameters) {
		try {
			IMarker marker = ValidationMarker.mark(getProject());
			ValidationMarker.setSeverity(marker, IMarker.SEVERITY_ERROR);
			ValidationMarker.setDescription(marker, description, parameters);
		} catch (CoreException e) {
			printReport(description, parameters);
		}

	}

	/**
	 * Fall back to print the report to the standard output in case of any
	 * problem trying to create markers.
	 * 
	 * @param error
	 */
	private void printReport(Item item, int errorCode, String description, Object... parameters) {
		System.out.println("Validation error [" + errorCode + "] " + item.getName() + ":"
				+ MessageFormat.format(description, parameters));
	}

	/**
	 * Fall back to print the report to the standard output in case of any
	 * problem trying to create markers.
	 * 
	 * @param description
	 * @param parameters
	 */
	private void printReport(String description, Object... parameters) {
		System.out.println("Validation error " + getProject().getName() + ":"
				+ MessageFormat.format(description, parameters));
	}

	// /**
	// * Removes all the validation markers associated with this item.
	// *
	// * If an item is marked read-only we silently ignore it. This is to try to
	// keep the
	// * markers coherent with the last state before the item was marked
	// read-only.
	// *
	// * @param tem
	// * @param monitor
	// * @throws CoreException
	// */
	// private void clearReports(Item item, IProgressMonitor monitor) throws
	// CoreException {
	//
	// if (item.isReadOnly())
	// return;
	//
	// IResource eclipseResource =
	// MelusineProjectManager.getResource(item,true);
	// if (eclipseResource == null || !eclipseResource.exists())
	// return;
	//
	// monitor.subTask("Clearing reports "+item.getQualifiedDisplayName());
	//
	// ValidationMarker.unmark(eclipseResource,true,IResource.DEPTH_ZERO);
	//
	// monitor.worked(1);
	// }

	/**
	 * Removes all validation markers associated with this project
	 * 
	 * @param monitor
	 * @throws CoreException
	 */
	private void clearMarkers(IProgressMonitor monitor) throws CoreException {
		ValidationMarker.unmark(getProject(), true, IResource.DEPTH_INFINITE);
	}

}
