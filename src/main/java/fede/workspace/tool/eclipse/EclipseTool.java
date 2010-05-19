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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;

import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.CadseDomain;
import fr.imag.adele.cadse.core.impl.CadseCore;
import fr.imag.adele.fede.workspace.si.view.View;

/**
 * The Class EclipseTool.
 * 
 * @author <a href="mailto:stephane.chomat@imag.fr">Stephane Chomat</a>
 */

public class EclipseTool {

	/** The Constant PROVIDER. */
	@Deprecated
	public static final String			PROVIDER			= View.PROVIDER;

	/** The Constant MODEL_EXTENSION. */
	@Deprecated
	public static final String			MODEL_EXTENSION		= View.MODEL_EXTENSION;

	/** The Constant ITEM_ID_PROPERTY. */
	@Deprecated
	public final static QualifiedName	ITEM_ID_PROPERTY	= View.ITEM_ID_PROPERTY;

	/**
	 * Instantiates a new eclipse tool.
	 */
	public EclipseTool() {
	}

	/**
	 * Sets the default monitor.
	 * 
	 * @param monitor
	 *            the new default monitor
	 */
	public static void setDefaultMonitor(IProgressMonitor monitor) {
		View.setDefaultMonitor(monitor);
	}

	/**
	 * Gets the default monitor.
	 * 
	 * @return the default monitor
	 */
	public static IProgressMonitor getDefaultMonitor() {
		return View.getDefaultMonitor();
	}

	/**
	 * Unset default monitor.
	 */
	public static void unsetDefaultMonitor() {
		View.unsetDefaultMonitor();
	}

	/**
	 * Sets the delete option.
	 * 
	 * @param eclipse
	 *            the eclipse
	 * @param content
	 *            the content
	 */
	public static void setDeleteOption(boolean eclipse, boolean content) {
		View.setDeleteOption(eclipse, content);
	}

	/**
	 * Gets the delete option.
	 * 
	 * @return the delete option
	 */
	public static Boolean[] getDeleteOption() {
		return View.getDeleteOption();
	}

	/**
	 * Unset delete option.
	 */
	public static void unsetDeleteOption() {
		View.unsetDeleteOption();
	}

	/**
	 * Gets the workspace domain.
	 * 
	 * @return the workspace domain
	 */
	static public CadseDomain getWorkspaceDomain() {
		return CadseCore.getCadseDomain();
	}

	/**
	 * set the persisance ID.
	 * 
	 * @param f
	 *            A resource a project
	 * @param item
	 *            the item
	 */
	public static void setItemPersistenceID(IResource f, Item item) {
		View.setItemPersistenceID(f, item);
	}
}
