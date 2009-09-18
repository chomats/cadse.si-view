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

package fede.workspace.tool.view.actions.test;

import java.io.File;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.impl.ui.AbstractActionPage;
import fr.imag.adele.cadse.core.impl.ui.AbstractModelController;
import fr.imag.adele.cadse.core.ui.EPosLabel;
import fr.imag.adele.cadse.core.ui.UIField;
import fede.workspace.model.manager.properties.impl.ic.IC_ForChooseFile;
import fede.workspace.model.manager.properties.impl.ui.DChooseFileUI;
import fede.workspace.model.manager.properties.impl.ui.DTextUI;
import fr.imag.adele.fede.workspace.si.view.View;

/**
 * The Class ImportCadsePagesAction.
 * 
 * @author <a href="mailto:stephane.chomat@imag.fr">Stephane Chomat</a>
 */
public class StartTestActionPage extends AbstractActionPage {

	/**
	 * The Class CadseViewerFilter.
	 */
	class CadseViewerFilter extends ViewerFilter {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer,
		 *      java.lang.Object, java.lang.Object)
		 */
		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (element instanceof IContainer) {
				return true;
			}
			return false;
		}

	}

	/** The select jar. */
	IPath				directoryPath;

	/** The file. */
	File				directoryFile;

	/** The cadse. */
	String				name;

	String				description;

	/** The cadse viewer filter. */
	public ViewerFilter	cadseViewerFilter	= new CadseViewerFilter();

	/**
	 * The Class MC_Import.
	 */
	class MC_Directory extends AbstractModelController {

		/*
		 * (non-Javadoc)
		 * 
		 * @see fr.imag.adele.cadse.core.ui.IModelController#getValue()
		 */
		public Object getValue() {
			return directoryPath;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see fr.imag.adele.cadse.core.ui.IEventListener#notifieValueChanged(fr.imag.adele.cadse.core.ui.UIField,
		 *      java.lang.Object)
		 */
		public void notifieValueChanged(UIField field, Object value) {
			directoryPath = (IPath) value;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see fr.imag.adele.cadse.core.ui.AbstractModelController#notifieSubValueAdded(fr.imag.adele.cadse.core.ui.UIField,
		 *      java.lang.Object)
		 */
		@Override
		public void notifieSubValueAdded(UIField field, Object added) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see fr.imag.adele.cadse.core.ui.AbstractModelController#notifieSubValueRemoved(fr.imag.adele.cadse.core.ui.UIField,
		 *      java.lang.Object)
		 */
		@Override
		public void notifieSubValueRemoved(UIField field, Object removed) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see fr.imag.adele.cadse.core.ui.AbstractModelController#notifieValueDeleted(fr.imag.adele.cadse.core.ui.UIField,
		 *      java.lang.Object)
		 */
		@Override
		public void notifieValueDeleted(UIField field, Object oldvalue) {
			directoryPath = null;
		}

		/**
		 * Valid value changed.
		 * 
		 * @param value
		 *            the value
		 * 
		 * @return the string
		 */
		@Override
		public boolean validValueChanged(UIField field, Object value) {
			directoryFile = getFile((IPath) value);
			if (directoryFile == null || !directoryFile.exists() || !directoryFile.isDirectory()) {
				setMessageError("Select a directory");
				return true;
			}
			if (name != null) {
				File testDirecoty = new File(directoryFile, name);
				if (testDirecoty.exists()) {
					setMessageError("the test '" + name + "' already exist in " + directoryFile);
					return true;
				}
			}
			return false;
		}

		public ItemType getType() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	/**
	 * The Class IC_Import.
	 */
	class IC_Directory extends IC_ForChooseFile {

		/*
		 * (non-Javadoc)
		 * 
		 * @see fede.workspace.model.manager.properties.impl.ic.IC_ForChooseFile#getViewerFilter()
		 */
		@Override
		protected ViewerFilter getViewerFilter() {
			return cadseViewerFilter;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see fede.workspace.model.manager.properties.impl.ic.IC_ForChooseFile#getKind()
		 */
		@Override
		public int getKind() {
			return FOLDER_EXT;
		}
	}

	/**
	 * The Class MC_Name.
	 */
	class MC_Name extends AbstractModelController {

		/*
		 * (non-Javadoc)
		 * 
		 * @see fr.imag.adele.cadse.core.ui.IModelController#getValue()
		 */
		public Object getValue() {
			return name;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see fr.imag.adele.cadse.core.ui.IEventListener#notifieValueChanged(fr.imag.adele.cadse.core.ui.UIField,
		 *      java.lang.Object)
		 */
		public void notifieValueChanged(UIField field, Object value) {
			name = (String) value;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see fr.imag.adele.cadse.core.ui.AbstractModelController#notifieValueDeleted(fr.imag.adele.cadse.core.ui.UIField,
		 *      java.lang.Object)
		 */
		@Override
		public void notifieValueDeleted(UIField field, Object oldvalue) {
		}

		@Override
		public boolean validValueChanged(UIField field, Object value) {
			String _name = (String) value;
			if (_name == null || _name.length() == 0) {
				setMessageError("Enter a name for this test");
				return true;
			}
			if (_name.indexOf('/') != -1) {
				setMessageError("Name contains bad character '/'.");
				return true;
			}
			if (_name.indexOf('\\') != -1) {
				setMessageError("Name contains bad character '\\'.");
				return true;
			}
			if (directoryFile == null || !directoryFile.exists() || !directoryFile.isDirectory()) {
				setMessageError("Select a directory");
				return true;
			}
			if (directoryFile != null && directoryFile.exists() && directoryFile.isDirectory()) {
				File testDirecoty = new File(directoryFile, _name);
				if (testDirecoty.exists()) {
					setMessageError("the test '" + _name + "' already exist in " + directoryFile);
					return true;
				}
			}
			name = _name;

			return super.validValueChanged(field, value);
		}

		public ItemType getType() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	/**
	 * The Class MC_Name.
	 */
	class MC_Description extends AbstractModelController {

		/*
		 * (non-Javadoc)
		 * 
		 * @see fr.imag.adele.cadse.core.ui.IModelController#getValue()
		 */
		public Object getValue() {
			return description;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see fr.imag.adele.cadse.core.ui.IEventListener#notifieValueChanged(fr.imag.adele.cadse.core.ui.UIField,
		 *      java.lang.Object)
		 */
		public void notifieValueChanged(UIField field, Object value) {
			description = (String) value;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see fr.imag.adele.cadse.core.ui.AbstractModelController#notifieValueDeleted(fr.imag.adele.cadse.core.ui.UIField,
		 *      java.lang.Object)
		 */
		@Override
		public void notifieValueDeleted(UIField field, Object oldvalue) {
			description = null;
		}

		@Override
		public boolean validValueChanged(UIField field, Object value) {
			String description = (String) value;
			if (description == null || description.length() == 0) {
				setMessageError("Enter a description for this test");
				return true;
			}
			if (directoryFile == null || !directoryFile.exists() || !directoryFile.isDirectory()) {
				setMessageError("Select a directory");
				return true;
			}
			return super.validValueChanged(field, value);
		}

		public ItemType getType() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	/**
	 * Creates the directory field.
	 * 
	 * @return the d choose file ui
	 */
	public DChooseFileUI createDirectoryField() {
		return new DChooseFileUI("selectJar", "Directory:", EPosLabel.left, new MC_Directory(), new IC_Directory(),
				"Select a directory where the test will be stored");
	}

	/**
	 * Creates the import field.
	 * 
	 * @return the d choose file ui
	 */
	public DTextUI createNameField() {
		return new DTextUI("name", "Name:", EPosLabel.left, new MC_Name(), null);
	}

	/**
	 * Creates the import field.
	 * 
	 * @return the d choose file ui
	 */
	public DTextUI createDescritionField() {
		return new DTextUI("name", "Description:", EPosLabel.left, new MC_Description(), null);
	}

	/**
	 * Gets the select jar.
	 * 
	 * @return the select jar
	 */
	public IPath getDirectoryPath() {
		return directoryPath;
	}

	/**
	 * Sets the select jar.
	 * 
	 * @param selectJar
	 *            the new select jar
	 */
	public void setDirectoryPath(IPath directoryPath) {
		this.directoryPath = directoryPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.impl.ui.AbstractActionPage#doFinish(java.lang.Object)
	 */
	@Override
	public void doFinish(Object monitor) throws Exception {
		super.doFinish(monitor);
		IProgressMonitor pmo = (IProgressMonitor) monitor;
		View.getInstance().getTestService().recordTest(directoryFile, name, description, pmo);
	}

	/**
	 * Gets the file.
	 * 
	 * @param path
	 *            a path
	 * 
	 * @return the file associated with this path
	 */
	private File getFile(IPath path) {
		if (path == null) {
			return null;
		}
		IResource r = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
		if (r != null) {
			return r.getLocation().toFile();
		}
		return path.toFile();
	}

}
