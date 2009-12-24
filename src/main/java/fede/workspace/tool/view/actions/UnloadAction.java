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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.Link;
import fede.workspace.tool.view.ItemInViewer;

public class UnloadAction implements IViewActionDelegate {

	private List<ItemInViewer>	items;

	/**
	 * Constructor for Action1.
	 */
	public UnloadAction() {
		super();
		items = new ArrayList<ItemInViewer>();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		for (ItemInViewer iiv : items) {
			Item item = iiv.getItem();
			for (Link l : item.getIncomingLinks()) {
				if (l.getLinkType().isComposition()) {
					MessageDialog.openError(null, MessageFormat.format("Cannot unload the item {0}.", item.getId()),
							MessageFormat.format(
									"Cannot unload the item {0} : Cannot unload a Component of an open composite.",
									item.getId()));
					continue;
				}
			}
			UnloadJob job = new UnloadJob(iiv);
			job.schedule();
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	// TODO 1. mettre dans un job
	// TODO 2. desactif si item est dans un composite ouvert
	public void selectionChanged(IAction action, ISelection selection) {
		items.clear();
		if (selection != null && selection.isEmpty() == false && selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			Object[] obj = ssel.toArray();
			ONE: for (Object o : obj) {
				if (!(o instanceof ItemInViewer)) {
					continue;
				}
				ItemInViewer iiv = ((ItemInViewer) o);
				if (iiv.getLink() == null || iiv.getLink().getResolvedDestination() == null) {
					continue;
				}

				Item item = iiv.getItem();
				if (item == null) {
					continue;
				}
				if (item.getIncomingLinks() == null) {
					continue;
				}
				for (Link l : item.getIncomingLinks()) {
					if (l.isComposition()) {
						continue ONE;
					}
				}
				items.add(iiv);
			}
		}
		action.setEnabled((items.size() != 0));
	}

	public void init(IViewPart view) {
	}

}
