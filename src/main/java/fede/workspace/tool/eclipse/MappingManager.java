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

package fede.workspace.tool.eclipse;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;

/**
 * The Class MappingManager.
 * 
 * @author <a href="mailto:stephane.chomat@imag.fr">Stephane Chomat</a>
 */
public class MappingManager {

	/**
	 * Creates the empty file.
	 * 
	 * @param f
	 *            the f
	 * @param monitor
	 *            the monitor
	 * 
	 * @throws CoreException
	 *             the core exception
	 */
	public static void createEmptyFile(IFile f, IProgressMonitor monitor) throws CoreException {
		if (f.exists()) return;
		
		if (f.getParent() instanceof IFolder) {
			createFolder((IFolder) f.getParent(),monitor);
		}
		
		try {
			ByteArrayInputStream stream = new ByteArrayInputStream(""
					.getBytes(f.getProject().getDefaultCharset()));
			f.create(stream, true, monitor);
			stream.close();
		} catch (IOException e) {
		}
	}
	
	/**
	 * Generate.
	 * 
	 * @param fProject
	 *            the f project
	 * @param path
	 *            the path
	 * @param fileName
	 *            the file name
	 * @param content
	 *            the content
	 * @param monitor
	 *            the monitor
	 * 
	 * @return the i file
	 * 
	 * @throws CoreException
	 *             the core exception
	 */
	public static IFile generate(IProject fProject, IPath path, String fileName, String content, IProgressMonitor monitor) throws CoreException {
		
		
		IFile file = null;
		if (path == null)
			file = fProject.getFile(fileName); 
		else {
			createFolder(fProject.getFolder(path),monitor);
			file = fProject.getFile(path.append(fileName));
		}
			
		
		try {
			ByteArrayInputStream stream = new ByteArrayInputStream(content.toString()
					.getBytes(fProject.getDefaultCharset()));
			if (file.exists())
				file.setContents(stream, true, true, monitor);
			else
				file.create(stream, false, monitor);
			stream.close();
		} catch (IOException e) {
		}
		return file;
	}
	
	/**
	 * Creates the parent container folder.
	 * 
	 * @param parent
	 *            the parent
	 * @param monitor
	 *            the monitor
	 * 
	 * @throws CoreException
	 *             the core exception
	 */
	public static void createParentContainerFolder(IContainer parent, IProgressMonitor monitor) throws CoreException {
		if (!parent.exists()) {
			if (parent.getType() == IResource.FOLDER) {
				createFolder((IFolder) parent, monitor);
			} else
			if (parent.getType() == IResource.PROJECT) {
				createProject((IProject) parent, null, monitor, true);
			}
		}
	}
	
	/**
	 * Creates the folder.
	 * 
	 * @param folder
	 *            the folder
	 * @param monitor
	 *            the monitor
	 * 
	 * @throws CoreException
	 *             the core exception
	 */
	public static void createFolder(IFolder folder, IProgressMonitor monitor) throws CoreException {
		if (!folder.exists()) {
			createParentContainerFolder(folder.getParent(), monitor);
			folder.create(true, true, null);
		}
	}
	
	/**
	 * Creates the project.
	 * 
	 * @param project
	 *            the project
	 * @param location
	 *            the location
	 * @param monitor
	 *            the monitor
	 * @param open
	 *            the open
	 * 
	 * @throws CoreException
	 *             the core exception
	 */
	public static void createProject(IProject project, IPath location,
			IProgressMonitor monitor, boolean open) throws CoreException {
		
		if (!project.exists()) {
			if (location != null && !Platform.getLocation().equals(location)) {
				IProjectDescription desc = project.getWorkspace()
						.newProjectDescription(project.getName());
				desc.setLocation(location);
				project.create(desc, monitor);
			} else
				project.create(monitor);
		}
		
		if (open)
			project.open(monitor);
	}
	
	/**
	 * Adds the nature to project.
	 * 
	 * @param proj
	 *            the proj
	 * @param natureId
	 *            the nature id
	 * @param monitor
	 *            the monitor
	 * 
	 * @throws CoreException
	 *             the core exception
	 */
	public static void addNatureToProject(IProject proj, String natureId,
			IProgressMonitor monitor) throws CoreException {
		IProjectDescription description = proj.getDescription();
		String[] prevNatures = description.getNatureIds();
		String[] newNatures = new String[prevNatures.length + 1];
		System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
		newNatures[prevNatures.length] = natureId;
		description.setNatureIds(newNatures);
		proj.setDescription(description, monitor);
	}

	/**
	 * Creates the container.
	 * 
	 * @param parent
	 *            the parent
	 * @param monitor
	 *            the monitor
	 * 
	 * @throws CoreException
	 *             the core exception
	 */
	public static void createContainer(IContainer parent, IProgressMonitor monitor) throws CoreException {
		if (parent.getType() == IResource.PROJECT)
			createProject((IProject) parent, null, monitor, true);
		else if (parent.getType() == IResource.FOLDER)
			createFolder((IFolder) parent, monitor);
	}

	
}
