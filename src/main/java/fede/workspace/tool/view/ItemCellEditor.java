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
package fede.workspace.tool.view;

import java.util.List;

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.ListDialog;

import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.LinkType;

public class ItemCellEditor extends DialogCellEditor {

	Item		source;
	LinkType	rt;

	@Override
	protected Object openDialogBox(Control cellEditorWindow) {

		ListDialog ldg = new ListDialog(cellEditorWindow.getShell());
		List<Link> allreadyIn = source.getOutgoingLinks(rt);
		List<Item> items = source.getLogicalWorkspace().getItems(rt.getDestination());
		items.removeAll(allreadyIn);
		ldg.setInput(items.toArray());
		if (ldg.open() == SWT.CANCEL) {
			return null;
		}
		ISelection sel = ldg.getTableViewer().getSelection();
		if (sel != null) {
			if (sel instanceof IStructuredSelection) {
				return ((IStructuredSelection) sel).getFirstElement();
			}
		}
		return null;
	}

}
