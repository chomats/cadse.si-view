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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.graphics.Image;

import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.impl.ui.AbstractActionPage;
import fr.imag.adele.cadse.core.impl.ui.AbstractModelController;
import fr.imag.adele.cadse.core.ui.EPosLabel;
import fr.imag.adele.cadse.core.ui.UIField;
import fede.workspace.model.manager.properties.impl.ic.IC_Abstract;
import fede.workspace.model.manager.properties.impl.ic.IC_ForChooseFile;
import fede.workspace.model.manager.properties.impl.ic.IC_TreeCheckedUI;
import fede.workspace.model.manager.properties.impl.ui.DCheckedTreeUI;
import fede.workspace.model.manager.properties.impl.ui.DChooseFileUI;
import fr.imag.adele.fede.workspace.si.view.View;

/**
 * The Class ImportCadsePagesAction.
 * 
 * @author <a href="mailto:stephane.chomat@imag.fr">Stephane Chomat</a>
 */
public class RunTestActionPage extends AbstractActionPage {

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

	List<File>			selectedTest;

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
			return false;
		}

		public ItemType getType() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	class MC_ListTest extends AbstractModelController {

		public Object getValue() {
			return selectedTest.toArray(new File[selectedTest.size()]);
		}

		public void notifieValueChanged(UIField field, Object value) {
		}

		@Override
		public void notifieSubValueAdded(UIField field, Object added) {
			if (added instanceof File) {
				selectedTest.add((File) added);
			}
		}

		@Override
		public void notifieSubValueRemoved(UIField field, Object removed) {
			if (removed instanceof File) {
				selectedTest.remove(removed);
			}
		}

		public ItemType getType() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	class IC_ListTest extends IC_Abstract implements IC_TreeCheckedUI {

		public String canObjectDeselected(Object obj) {
			File f = (File) obj;
			return null;
		}

		public String canObjectSelected(Object obj) {
			File f = (File) obj;
			if (isTest(f)) {
				return null;
			}
			return null;
		}

		public Object[] getChildren(Object obj) {
			File f = (File) obj;
			if (isTest(f)) {
				return null;
			}
			List<File> ret = new ArrayList<File>();
			File[] children = f.listFiles();
			if (children == null) {
				return null;
			}
			for (File ch : children) {
				if (ch.isDirectory()) {
					ret.add(ch);
				}
			}
			return ret.toArray(new File[ret.size()]);
		}

		public Object getParent(Object obj) {
			File f = (File) obj;
			return f.getParentFile();
		}

		public void edit(Object obj) {
			File f = (File) obj;

		}

		public Object[] getSources() {
			return new Object[] { directoryFile };
		}

		public void select(Object obj) {
			File f = (File) obj;

		}

		public Image toImageFromObject(Object obj) {
			return null;
		}

		public String toStringFromObject(Object obj) {
			File f = (File) obj;
			return f.getName();
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
	 * Creates the directory field.
	 * 
	 * @return the d choose file ui
	 */
	public DChooseFileUI createDirectoryField() {
		return new DChooseFileUI("selectJar", "Directory:", EPosLabel.left, new MC_Directory(), new IC_Directory(),
				"Select a directory where tests are stored");
	}

	public boolean isTest(File f) {
		return View.getInstance().getTestService().isTest(f);
	}

	/**
	 * Creates the import field.
	 * 
	 * @return the d choose file ui
	 */
	public DCheckedTreeUI createListField() {
		return new DCheckedTreeUI("name", "Test:", EPosLabel.top, new MC_ListTest(), new IC_ListTest(), false, false);
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
		// View.getInstance().getTestService().executeTest(testDirectory);
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
