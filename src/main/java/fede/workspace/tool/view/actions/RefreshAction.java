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
package fede.workspace.tool.view.actions;

import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Event;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.Item;
import fede.workspace.tool.icon.WSPluginImages;
import fede.workspace.tool.view.node.AbstractCadseViewNode;
import fr.imag.adele.cadse.eclipse.view.AbstractCadseTreeViewUI;

public class RefreshAction extends AbstractEclipseMenuAction  {

	private Set<IItemNode> items = null;
	private AbstractCadseTreeViewUI cadseTreeViewUI;

	public RefreshAction(Set<IItemNode> items2, AbstractCadseTreeViewUI cadseTreeViewUI) {
		this.items = items2;
		this.cadseTreeViewUI = cadseTreeViewUI;
		
		ImageDescriptor id= WSPluginImages.create("d" + "lcl16", "refresh_nav.gif", false); //$NON-NLS-1$
		if (id != null)
			setDisabledImageDescriptor(id);
	
		/*
		 * id= create("c" + type, relPath, false); //$NON-NLS-1$
		 * if (id != null)
		 * 		action.setHoverImageDescriptor(id);
		 */
	
		ImageDescriptor descriptor= WSPluginImages.create("e" + "lcl16", "refresh_nav.gif", true); //$NON-NLS-1$
		setHoverImageDescriptor(descriptor);
		setImageDescriptor(descriptor); 
	}

	@Override
	public String getLabel() {
			if ( items.size() == 1 ) {
			IItemNode iiv = ((IItemNode)items.iterator().next());
			Item item = iiv.getItem();
			if (item != null) {
				return "Refresh "+item.getName();
			}
		}
		return "Refresh ...";
	}

	@Override
	public void run(IItemNode[] selection) throws CadseException {
		if (items != null) {
			for (IItemNode iiv : items) {
				cadseTreeViewUI.refresh((AbstractCadseViewNode) iiv);
			}
		}
 	}

	

	

}
