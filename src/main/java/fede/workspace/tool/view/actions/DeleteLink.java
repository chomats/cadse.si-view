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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import fede.workspace.tool.view.ItemInViewer;
import fede.workspace.tool.view.WSPlugin;
import fr.imag.adele.cadse.core.IItemManager;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.oper.WSODeleteLink;
import fr.imag.adele.fede.workspace.as.test.TestException;
import fr.imag.adele.fede.workspace.si.view.View;

public class DeleteLink implements IViewActionDelegate {

	private List<ItemInViewer>	links;
	private IViewPart			view;

	public DeleteLink() {
		super();
	}

	public void run(@SuppressWarnings("unused")
	IAction action) {
		MultiStatus ms = new MultiStatus(WSPlugin.PLUGIN_ID, 0, "Delete links errors", null);
		try {
			for (ItemInViewer iv : links) {

				final Link l = iv.getLink();
				IItemManager im = WSPlugin.getManager(l.getSource());
				String error = im.canDeleteLink(l);
				if (error != null) {
					MessageDialog.openError(view.getSite().getShell(), MessageFormat.format(
							"Cannot delete the link {0}", l.toString()), error);
				} else if (MessageDialog.openConfirm(view.getSite().getShell(), MessageFormat.format(
						"Delete the link from {0} to {1} ?", l.getSource().getDisplayName(), l.getDestination(false)
								.getDisplayName()), MessageFormat.format(
						"Do you want delete the link from {0} to {1} (link type is {2})?", l.getSource()
								.getDisplayName(), l.getDestination(false).getDisplayName(), l.getLinkType()
								.getName()))) {
					try {
						WSODeleteLink oper = new WSODeleteLink(l);
						oper.execute();
						if (oper.getEx() != null) {
							ms.add(new Status(IStatus.ERROR, WSPlugin.PLUGIN_ID, 0, "Delete link " + l, oper.getEx()));
						}
						try {
							View.getInstance().getTestService().registerIfNeed(oper);
						} catch (TestException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					} catch (Throwable e) {
						ms.add(new Status(IStatus.ERROR, WSPlugin.PLUGIN_ID, 0, "Delete link " + l, e));
					}

				}
			}
		} catch (Throwable e) {
			ms.add(new Status(IStatus.ERROR, WSPlugin.PLUGIN_ID, 0, "Delete links", e));
		}
		if (!ms.isOK()) {
			WSPlugin.log(ms);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		links = new ArrayList<ItemInViewer>();

		if (selection != null && selection.isEmpty() == false && selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			Object[] selObjects = ssel.toArray();
			for (int i = 0; i < selObjects.length; i++) {
				Object obj = selObjects[i];
				if (obj instanceof ItemInViewer) {
					ItemInViewer iv = ((ItemInViewer) obj);
					Link l;
					if ((l = iv.getLink()) != null && !l.isReadOnly()) {
						links.add(iv);
					}
				}
			}
		}
		action.setEnabled(links.size() > 0);
	}

	public void init(IViewPart view1) {
		this.view = view1;
	}

}
