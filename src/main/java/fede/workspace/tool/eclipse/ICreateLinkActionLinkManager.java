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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;


import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.LinkType;


/**
 * The Interface ICreateLinkActionLinkManager.
 * 
 * @author <a href="mailto:stephane.chomat@imag.fr">Stephane Chomat</a>
 */
public interface ICreateLinkActionLinkManager {

	/**
	 * Gets the content provider.
	 * 
	 * @return the content provider
	 */
	public ITreeContentProvider getContentProvider();
	
	/**
	 * Gets the label provider.
	 * 
	 * @return the label provider
	 */
	public ILabelProvider getLabelProvider();
	
	/**
	 * Validate.
	 * 
	 * @param source
	 *            the source
	 * @param selection
	 *            the selection
	 * 
	 * @return the i status
	 */
	public IStatus validate(Item source, Object[] selection);
	
	/**
	 * Gets the input values.
	 * 
	 * @param source
	 *            the source
	 * @param viewer
	 *            the viewer
	 * @param itemParent
	 *            the item parent
	 * @param linkType
	 *            the link type
	 * @param itemDest
	 *            the item dest
	 * 
	 * @return the input values
	 */
	public Object getInputValues(Item source, TreeViewer viewer, Item itemParent, LinkType linkType, ItemType itemDest);
}
