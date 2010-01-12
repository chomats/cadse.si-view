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

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import fr.imag.adele.cadse.core.build.IBuildingContext;

public  class ComponentBuildingContext implements IBuildingContext {

	private final ComponentBuilder currentBuilder;
	private final IProgressMonitor currentMonitor;

	public ComponentBuildingContext(final ComponentBuilder currentBuilder, final IProgressMonitor currentMonitor) {
		this.currentBuilder = currentBuilder;
		this.currentMonitor = currentMonitor;
	}

	public IProject getProject() {
		return currentBuilder.getProject();
	}
	
	public ComponentBuilder getBuilder() {
		return currentBuilder;
	}
	
	public IProgressMonitor getMonitor() {
		return currentMonitor;
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
		System.out.println("Composition error "+getProject().getName()+":"+MessageFormat.format(description,parameters));
	}

	public void beginTask(String msg, int size) {
	}

	public void checkCanceled() {
		currentBuilder.checkCanceled(currentMonitor);
	}

	public void endTask() {
	}

	public void subTask(String msg) {
	}

	public void worked(int i) {
	}

}
