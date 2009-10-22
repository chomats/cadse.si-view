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

import java.text.MessageFormat;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.IShellProvider;

import fede.workspace.tool.view.WSPlugin;
import fede.workspace.tool.view.addlink.ImportItemOrCreateLinkStatusDialog;
import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.eclipse.view.IViewLinkManager;

public class CreateLinkAction extends AbstractEclipseMenuAction {

	Item						parent;
	private IShellProvider		shellProvider;
	private IViewLinkManager	viewLinkManager;

	public CreateLinkAction(Item parent, IShellProvider shellProvider, IViewLinkManager viewLinkManager) {
		this.parent = parent;
		this.shellProvider = shellProvider;
		this.viewLinkManager = viewLinkManager;
	}

	private static String TEXT(Item items2) {
		return "Create link from " + items2.getDisplayName();

	}

	@Override
	public String getLabel() {
		return "Create link from " + parent.getDisplayName();
	}

	@Override
	public void run(IItemNode[] selection) throws CadseException {
		try {
			ImportItemOrCreateLinkStatusDialog iicl = new ImportItemOrCreateLinkStatusDialog(shellProvider.getShell(),
					viewLinkManager);
			iicl.create();
			iicl.setItemParent(parent);
			iicl.open();
		} catch (Throwable e) {
			String id = parent.getName();
			WSPlugin.log(new Status(Status.ERROR, "Tool.Workspace.View", 0, MessageFormat.format(
					"Cannot create link from {0} : {1}.", id, e.getMessage()), e));
		}
	}

}
