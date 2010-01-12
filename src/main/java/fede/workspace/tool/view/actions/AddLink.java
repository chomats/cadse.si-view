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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import fede.workspace.tool.view.ItemInViewer;
import fede.workspace.tool.view.addlink.AddLinkWizard;

public class AddLink implements  IViewActionDelegate {

	private ItemInViewer item_source;
	private ItemInViewer item_dest;
	private IViewPart view;

	public AddLink() {
		super();
	}

	

	public void run(IAction action) {
		WizardDialog wd = new WizardDialog(view.getSite().getShell(),
				new AddLinkWizard( item_source.getItem(), item_dest.getItem()));
		wd.open();
        if (wd.getReturnCode() == Window.OK) {
           
        }
            
	}

	public void selectionChanged(IAction action, ISelection selection) {
		item_source = null;
		item_dest = null;
		if (selection != null && selection.isEmpty() == false
				&& selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() != 2)
				return;
			Object obj[] = ssel.toArray();
			if (obj[0] instanceof ItemInViewer) {
				this.item_source = ((ItemInViewer) obj[0]);
			}
			if (obj[1] instanceof ItemInViewer) {
				this.item_dest = ((ItemInViewer) obj[1]);
			}
		}
		action.setEnabled(item_source!= null 
                && item_source.getItem() != null 
                && item_dest != null && item_dest.getItem() != null 
                && !(item_source.getItem().isReadOnly() && item_dest.getItem().isReadOnly()));
	}

	public void init(IViewPart view1) {
		this.view = view1;
		
	}

}
