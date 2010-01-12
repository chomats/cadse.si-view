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
package fede.workspace.model.manager;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;

import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.ILinkTypeManager;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.core.impl.AbstractLinkTypeManager;
import fede.workspace.tool.eclipse.ICreateLinkActionLinkManager;

public class DefaultLinkTypeManager extends AbstractLinkTypeManager implements
		ILinkTypeManager, ICreateLinkActionLinkManager {

	public ITreeContentProvider getContentProvider() {
		return null;
	}

	public ILabelProvider getLabelProvider() {
		return null;
	}

	public IItemNode getMainItemNode(TreeViewer viewer, Item itemParent,
			LinkType linkType, ItemType itemDest) {
		return null;
	}

	public Object getInputValues(Item source, TreeViewer viewer, Item itemParent, LinkType linkType, ItemType itemDest) {
		return null;
	}

	public IStatus validate(Item source, Object[] selection) {
		return Status.OK_STATUS;
	}

}
