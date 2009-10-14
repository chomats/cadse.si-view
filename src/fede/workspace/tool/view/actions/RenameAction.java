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

import java.util.Iterator;
import java.util.Set;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.jface.window.Window;

import fede.workspace.tool.view.actions.delete.ShowDetailWLWCDialogPage;
import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.delta.ItemDelta;
import fr.imag.adele.cadse.core.impl.CadseCore;
import fr.imag.adele.cadse.core.transaction.LogicalWorkspaceTransaction;

public class RenameAction extends AbstractEclipseMenuAction implements IInputValidator {

	private Set<IItemNode>	items;

	private IShellProvider	viewer;

	private Item			item;

	public RenameAction(Set<IItemNode> items, IShellProvider viewer) {
		this.items = items;
		this.viewer = viewer;
	}

	@Override
	public String getLabel() {
		if (items.size() == 1) {
			return "Rename " + (items.iterator().next()).getItem().getName();
		}
		return "Rename ...";
	}

	@Override
	public void run(IItemNode[] selection) throws CadseException {
		for (Iterator iter = items.iterator(); iter.hasNext();) {
			IItemNode iiv = (IItemNode) iter.next();
			item = iiv.getItem();
			try {
				String shortName = item.getName();
				InputDialog id = new InputDialog(viewer.getShell(), "Rename the item " + item.getName(),
						"Enter a new short name", shortName, this);

				id.open();
				if (id.getReturnCode() == Window.OK) {
					final String newShortName = id.getValue();
					LogicalWorkspaceTransaction copy = item.getLogicalWorkspace().createTransaction();
					ItemDelta itemOperation = copy.getItem(item.getId());
					itemOperation.setAttribute(CadseGCST.ITEM_at_NAME_, newShortName);
					ShowDetailWLWCDialogPage.openDialog(copy, "Rename the item " + item.getName(), "Items to change",
							true);

				}
			} catch (Throwable e) {
				MessageDialog.openError(viewer.getShell(), "Error when rename the item " + item.getDisplayName(), e
						.getMessage());
			}
		}
	}

	public String isValid(String id) {
		String error = item.getType().getItemManager().validateShortName(item, id);
		if (error != null) {
			return error;
		}
		if (id == null || id.length() == 0) {
			return "Empty name";
		}
		try {
			if (CadseCore.getLogicalWorkspace().existsItem(item, id)) {
				return "The item " + id + " allready exists.";
			}
		} catch (CadseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Exception " + e.getMessage();
		}
		return null;
	}

}
