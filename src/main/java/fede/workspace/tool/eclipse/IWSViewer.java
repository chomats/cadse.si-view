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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import fr.imag.adele.cadse.core.IItemManager;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;

/**
 * The Interface IWSViewer.
 * 
 * @author <a href="mailto:stephane.chomat@imag.fr">Stephane Chomat</a>
 */
public interface IWSViewer {

	/**
	 * Reset.
	 */
	void reset();

	/**
	 * Refresh.
	 */
	void refresh();

	/**
	 * Log.
	 * 
	 * @param status
	 *            the status
	 */
	void log(IStatus status);

	/**
	 * Builds the modified item.
	 * 
	 * @param item
	 *            the item
	 * @param forceBuilder
	 *            the force builder
	 * @param monitor
	 *            the monitor
	 * 
	 * @throws CoreException
	 *             the core exception
	 */
	public void buildModifiedItem(Item item, boolean forceBuilder, IProgressMonitor monitor) throws CoreException;

	/**
	 * Delete eclipse resource.
	 * 
	 * @param resource
	 *            the resource
	 * @param keepContent
	 *            the keep content
	 * @param monitor
	 *            the monitor
	 * 
	 * @throws CoreException
	 *             the core exception
	 */
	public void deleteEclipseResource(IResource resource, boolean keepContent, IProgressMonitor monitor)
			throws CoreException;

	/**
	 * Creates the default manager.
	 * 
	 * @param itemType
	 *            the item type
	 * 
	 * @return the i item manager
	 */
	IItemManager createDefaultManager(ItemType itemType);

	/**
	 * Refresh item.
	 * 
	 * @param item
	 *            the item
	 */
	void refreshItem(Item item);

}
