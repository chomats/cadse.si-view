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
package fede.workspace.eclipse.content;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import fede.workspace.eclipse.MelusineProjectManager;
import fede.workspace.tool.view.content.IViewContentModel;
import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.CompactUUID;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.attribute.IAttributeType;
import fr.imag.adele.cadse.core.var.ContextVariable;
import fr.imag.adele.cadse.core.var.Variable;
import fr.imag.adele.fede.workspace.si.view.View;

/**
 * Gere un project eclipse.
 * 
 * @author chomats
 * 
 */
public class ProjectContentManager extends EclipseContentManager implements IViewContentModel, IWorkbenchAdapter {
	public class SetProjectChange extends Change {

		String							newName;
		String							oldName;
		private ProjectContentManager	manager;

		public SetProjectChange(ProjectContentManager manager, String oldName, String newName) {
			this.manager = manager;
			this.newName = newName;
			this.oldName = oldName;
		}

		@Override
		public Object getModifiedElement() {
			return manager;
		}

		@Override
		public String getName() {
			return "set Project in ProjectContentManager (" + newName + ")";
		}

		@Override
		public void initializeValidationData(IProgressMonitor pm) {
		}

		@Override
		public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException, OperationCanceledException {
			return new RefactoringStatus();
		}

		@Override
		public Change perform(IProgressMonitor pm) throws CoreException {
			return new SetProjectChange(manager, newName, oldName);
		}

	}

	// private IProject p;
	private Variable	projectname;

	/**
	 * 
	 * @param item
	 *            Can be null;
	 * @param projectName
	 * 
	 */
	public ProjectContentManager(CompactUUID id, Variable projectname) {
		super(id);
		this.projectname = projectname;
	}

	/**
	 * initialise le project attent to create it
	 * 
	 * @throws CoreException
	 */
	@Override
	public void init() throws CadseException {
		super.init();
		IProject p = getProject();
		if (!p.exists()) {
			create();
		}

		View.setItemPersistenceID(p, getOwnerItem());
	}

	/**
	 * Return the project name
	 * 
	 * @return
	 */
	public String getProjectName(ContextVariable context) {
		return projectname.compute(context, getOwnerItem());
	}

	/**
	 * create the project
	 * 
	 * @throws CoreException
	 */
	@Override
	public void create() throws CadseException {
		IProject p = getProject();
		MelusineProjectManager.createAndOpenProject(p, View.getDefaultMonitor());
		if (getOwnerItem() != null) {
			View.setItemPersistenceID(p, getOwnerItem());
		}
		MelusineProjectManager.addMelusineProject(p, View.getDefaultMonitor());
	}

	/**
	 * delete the project with the content
	 */
	@Override
	public void delete() throws CadseException {
		IProject p = getProject();
		try {
			if (p.exists()) {
				p.delete(true, View.getDefaultMonitor());
			}
		} catch (CoreException e) {
			throw new CadseException("Cannot delete project {0} from {1} : {2}", e, p.getName(), getItem().getName(), e
					.getMessage());
		}
	}

	/**
	 * Return the project object
	 * 
	 * @return
	 */
	public IProject getProject(ContextVariable cxt) {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(getProjectName(cxt));
	}

	/**
	 * Return the project object
	 * 
	 * @return
	 */
	public IProject getProject() {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(getProjectName(ContextVariable.DEFAULT));
	}

	/**
	 * Return the project object
	 * 
	 * @return
	 */
	@Override
	public Object getMainResource() {
		IProject p = getProject();
		return p;
	}

	@Override
	public <T> T getMainMappingContent(ContextVariable cxt, Class<T> clazz) {
		final IProject project = getProject(cxt);
		if (clazz.isAssignableFrom(IProject.class)) {
			return (T) project;
		}
		if (clazz.isAssignableFrom(File.class)) {
			if (project == null) {
				return null;
			}
			return (T) project.getLocation().toFile();
		}
		return null;
	}

	@Override
	public List<?> getMappingContents() {
		final IProject project = getProject(ContextVariable.DEFAULT);
		return Arrays.asList(project, project.getLocation().toFile());
	}

	@Override
	public <T> List<T> getMappingContents(Class<T> clazz) {
		final IProject project = getProject(ContextVariable.DEFAULT);
		if (clazz.isAssignableFrom(IProject.class)) {
			return Arrays.asList((T) project);
		}
		if (clazz.isAssignableFrom(File.class)) {
			if (project == null) {
				return null;
			}
			return Arrays.asList((T) project.getLocation().toFile());
		}
		return null;
	}

	/**
	 * if (kind equals "project) return the project object
	 * 
	 * @return
	 */
	@Override
	public Object[] getResources(String kind) {
		IProject p = getProject();
		if ("project".equals(kind)) {
			return new Object[] { p };
		}
		return null;
	}

	@Override
	public String[] getKindsResource() {
		return new String[] { "project" };
	}

	@Override
	public Object[] getResources() {
		IProject p = getProject();
		return new Object[] { p };
	}

	public ITreeContentProvider getContentProvider() {
		return new BaseWorkbenchContentProvider();
	}

	public ILabelProvider getLabelProvider() {
		return WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider();
	}

	public Object[] getChildren(Object o) {
		return getResources();
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		return null;
	}

	public String getLabel(Object o) {
		return "ProjectContentManager";
	}

	public Object getParent(Object o) {
		return null;
	}
	
	
	
	@Override
	public <T> T internalGetOwnerAttribute(IAttributeType<T> type) {
		if (type == CadseGCST.PROJECT_CONTENT_MODEL_at_PROJECT_NAME_)
			return (T) getProjectName(ContextVariable.DEFAULT);
		return super.internalGetOwnerAttribute(type);
	}

	// @Override
	// public RefactoringStatus computeRenameChange(CompositeChange change,
	// ContextVariable newCxt, ContextVariable oldCxt) {
	// String newName = getProjectName(newCxt);
	// change.add(new RenameResourceChange(null, p, newName, "Rename project"));
	// change.add(new SetProjectChange(this, p.getName(), newName));
	// return null;
	// }

}
