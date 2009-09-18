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
package fede.workspace.tool.view.menu;

import java.net.URL;
import java.util.ArrayList;

import org.eclipse.jface.window.IShellProvider;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.IMenuAction;
import fr.imag.adele.cadse.core.LogicalWorkspace;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.core.impl.CadseCore;
import fr.imag.adele.cadse.core.transaction.LogicalWorkspaceTransaction;

public class RecreatePartLinkAction extends IMenuAction {

	private ArrayList<Item>	items;
	private IShellProvider	shell;

	public RecreatePartLinkAction(ArrayList<Item> itemsToRecreatePartLink, IShellProvider shellProvider) {
		this.items = itemsToRecreatePartLink;
		this.shell = shellProvider;
	}

	@Override
	public URL getImage() {
		return null;
	}

	@Override
	public String getLabel() {
		return "recreate part";
	}

	@Override
	public void run(IItemNode[] selection) throws CadseException {
		LogicalWorkspace wl = CadseCore.getLogicalWorkspace();
		LogicalWorkspaceTransaction copy = wl.createTransaction();

		for (Item i : items) {
			Item p = i.getPartParent();
			if (p == null) {
				continue;
			}
			LinkType lt = i.getType().getIncomingPart(p.getType());
			if (lt == null) {
				continue;
			}
			Link createLink = copy.getItem(p.getId()).createLink(lt, i);
		}

		copy.commit();
	}

}
