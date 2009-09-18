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
package fede.workspace.ant;

import java.io.File;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;

import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.CadseDomain;
import fr.imag.adele.cadse.core.impl.CadseCore;

public class GetPath extends Path {
	IProject	eproject;

	public GetPath(Project p) {
		super(p);
	}

	public void setItem(String itemId) {
		log("item : " + itemId);
		CadseDomain wd = CadseCore.getInstance();
		Item item = wd.getLogicalWorkspace().getItem(itemId);

		// try {
		// eproject = WSPlugin.getProjectFromItem(item);
		// Set<IClasspathEntry> itemjavapath =
		// JavaProjectManager.calculateDependencies(item);
		// for (IClasspathEntry entry : itemjavapath) {
		// if (entry.getEntryKind() == IClasspathEntry.CPE_PROJECT) {
		// addProjectClasspath(entry);
		// } else if (entry.getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
		// addLibClasspath(entry);
		// }
		// }
		// } catch (CoreException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	private void addLibClasspath(IClasspathEntry entry) {
		File f = entry.getPath().toFile();
		add(new Path(getProject(), f.getAbsolutePath()));
	}

	private void addProjectClasspath(IClasspathEntry entry) {
		JavaCore.getResolvedClasspathEntry(entry);
	}

}
