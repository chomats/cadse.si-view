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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import fede.workspace.tool.view.ItemInViewer;
import fr.imag.adele.cadse.core.Link;

@Deprecated
public class LoadAction implements IViewActionDelegate {

	private Link			link;
	private IViewPart		view;
	private ItemInViewer	iv;

	public LoadAction() {
		super();
	}

	public void run(@SuppressWarnings("unused")
	IAction action) {
		Link l = link;
		if (MessageDialog.openConfirm(view.getSite().getShell(), "Load Item " + l.getDestinationId() + " ?",
				"Do you want load the item  " + l.getDestinationId() + " ?")) {
			LoadJob cj = new LoadJob(link);
			cj.schedule();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		link = null;

		if (selection != null && selection.isEmpty() == false && selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() != 1) {
				action.setEnabled(false);
				return;
			}

			Object obj = ssel.getFirstElement();
			if (obj instanceof ItemInViewer) {
				iv = ((ItemInViewer) obj);
				if (iv != null) {
					link = iv.getLink();
				}
			}

		}
		action.setEnabled(link != null && link.getResolvedDestination() == null);
	}

	public void init(IViewPart view1) {
		this.view = view1;
	}

}
