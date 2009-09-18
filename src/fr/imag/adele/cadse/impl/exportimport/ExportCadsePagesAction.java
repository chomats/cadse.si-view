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

package fr.imag.adele.cadse.impl.exportimport;

import java.io.File;
import java.util.HashMap;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.osgi.service.prefs.BackingStoreException;

import fede.workspace.model.manager.properties.impl.ic.IC_ForChooseFile;
import fede.workspace.model.manager.properties.impl.ui.DCheckBoxUI;
import fede.workspace.model.manager.properties.impl.ui.DChooseFileUI;
import fede.workspace.model.manager.properties.impl.ui.DTextUI;
import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.impl.ui.AbstractActionPage;
import fr.imag.adele.cadse.core.impl.ui.AbstractModelController;
import fr.imag.adele.cadse.core.ui.EPosLabel;
import fr.imag.adele.cadse.core.ui.UIField;
import fr.imag.adele.cadse.core.util.Assert;

/**
 * The Class ExportCadsePagesAction.
 * 
 * @author <a href="mailto:stephane.chomat@imag.fr">Stephane Chomat</a>
 */
public class ExportCadsePagesAction extends AbstractActionPage {

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
	IPath				selectJar;

	/** The file. */
	File				file;

	/** The tstamp. */
	boolean				tstamp				= true;

	String				name				= "";

	/** The cadse viewer filter. */
	public ViewerFilter	cadseViewerFilter	= new CadseViewerFilter();

	/**
	 * The Class MC_TSTamp.
	 */
	class MC_TSTamp extends AbstractModelController {

		/*
		 * (non-Javadoc)
		 * 
		 * @see fr.imag.adele.cadse.core.ui.IModelController#getValue()
		 */
		public Object getValue() {
			return tstamp;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see fr.imag.adele.cadse.core.ui.IEventListener#notifieValueChanged(fr.imag.adele.cadse.core.ui.UIField,
		 *      java.lang.Object)
		 */
		public void notifieValueChanged(UIField field, Object value) {
			tstamp = (Boolean) value;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see fr.imag.adele.cadse.core.ui.AbstractModelController#notifieValueDeleted(fr.imag.adele.cadse.core.ui.UIField,
		 *      java.lang.Object)
		 */
		@Override
		public void notifieValueDeleted(UIField field, Object oldvalue) {
			tstamp = true;
		}

		public ItemType getType() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object defaultValue() {
			return true;
		}

	}

	/**
	 * The Class MC_TSTamp.
	 */
	class MC_name extends AbstractModelController {

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

		public ItemType getType() {
			return null;
		}

		@Override
		public Object defaultValue() {
			return "";
		}

	}

	/**
	 * The Class MC_Import.
	 */
	class MC_Import extends AbstractModelController {
		IEclipsePreferences	node;

		@Override
		public void init() throws CadseException {
			// 1. get config pref node
			node = new ConfigurationScope().getNode("fr.imag.adele.cadse.export-cadse");
			String path = node.get("export-path", "");
			if (path != null) {
				selectJar = new org.eclipse.core.runtime.Path(path);
			}
			super.init();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see fr.imag.adele.cadse.core.ui.IModelController#getValue()
		 */
		public Object getValue() {
			return selectJar;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see fr.imag.adele.cadse.core.ui.IEventListener#notifieValueChanged(fr.imag.adele.cadse.core.ui.UIField,
		 *      java.lang.Object)
		 */
		public void notifieValueChanged(UIField field, Object value) {
			selectJar = (IPath) value;
			node.put("export-path", selectJar.toPortableString());
			try {
				node.flush();
			} catch (BackingStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see fr.imag.adele.cadse.core.ui.AbstractModelController#notifieValueDeleted(fr.imag.adele.cadse.core.ui.UIField,
		 *      java.lang.Object)
		 */
		@Override
		public void notifieValueDeleted(UIField field, Object oldvalue) {
			selectJar = null;
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
			file = getFile((IPath) value);
			if (file == null || !file.exists() || !file.isDirectory()) {
				setMessageError("Select a folder");
				return true;
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
	class IC_Import extends IC_ForChooseFile {

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
			return FOLDER_EXT + WORKSPACE;
		}
	}

	/**
	 * Creates the import field.
	 * 
	 * @return the d choose file ui
	 */
	public DTextUI createNameField() {
		return new DTextUI("name", "name:", EPosLabel.left, new MC_name(), null);
	}

	/**
	 * Creates the import field.
	 * 
	 * @return the d choose file ui
	 */
	public DChooseFileUI createImportField() {
		return new DChooseFileUI("selectJar", "Select folder", EPosLabel.left, new MC_Import(), new IC_Import(),
				"Select folder");
	}

	/**
	 * Creates the time stamp field.
	 * 
	 * @return the uI field
	 */
	public UIField createTimeStampField() {
		return new DCheckBoxUI("tstamp", "add time stamp", EPosLabel.none, new MC_TSTamp(), null);
	}

	/** The cadsedef. */
	Item[]	cadsedef;

	/**
	 * Sets the cadsedef.
	 * 
	 * @param cadsedef
	 *            the new cadsedef
	 */
	public void setCadsedef(Item[] cadsedef) {
		Assert.isTrue(cadsedef != null && cadsedef.length > 0);
		this.cadsedef = cadsedef;
		this.name = cadsedef[0].getQualifiedName();
	}

	/**
	 * Gets the select jar.
	 * 
	 * @return the select jar
	 */
	public IPath getSelectJar() {
		return selectJar;
	}

	/**
	 * Sets the select jar.
	 * 
	 * @param selectJar
	 *            the new select jar
	 */
	public void setSelectJar(IPath selectJar) {
		this.selectJar = selectJar;
	}

	/** The its. */
	private HashMap<Item, File>	its;

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.impl.ui.AbstractActionPage#doFinish(java.lang.Object)
	 */
	@Override
	public void doFinish(Object monitor) throws Exception {
		super.doFinish(monitor);
		IProgressMonitor pmo = (IProgressMonitor) monitor;
		ExportImportCadseFunction e = new ExportImportCadseFunction();
		e.exportItems(pmo, file, name, tstamp, cadsedef);
	}

	/**
	 * Gets the file.
	 * 
	 * @param selectJar2
	 *            the select jar2
	 * 
	 * @return the file
	 */
	private File getFile(IPath selectJar2) {
		IResource r = ResourcesPlugin.getWorkspace().getRoot().findMember(selectJar2);
		if (r != null) {
			return r.getLocation().toFile();
		}
		return selectJar2.toFile();
	}

}
