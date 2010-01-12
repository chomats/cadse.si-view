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
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.ISharedImages;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.IItemManager;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.oper.WSODeleteLink;
import fede.workspace.tool.view.WSPlugin;
import fr.imag.adele.fede.workspace.as.test.TestException;
import fr.imag.adele.fede.workspace.si.view.View;

/**
 * Pour chaque item s�lectionn�, cette action demande au manager si on peut d�truire un item.
 * Le manager renvoie une message d'error dans ou cela est impossible et l'action affiche l'erreur.
 * Si cela est possible pour le manager, l'action demande � l'utilisateur si c'est ok.
 * Et si oui elle d�truit l'item.
 * @author chomats
 *
 */
public class DeleteLinkAction  extends AbstractEclipseMenuAction {

    private Set<IItemNode> links;
	private IShellProvider shellProvider;
	
	public DeleteLinkAction(Set<IItemNode> links, IShellProvider shellProvider) {
		setDescription("Deletes the selected elements"); 
		
		ISharedImages workbenchImages= WSPlugin.getDefault().getWorkbench().getSharedImages();
		setDisabledImageDescriptor(workbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED));
		setImageDescriptor(workbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		setHoverImageDescriptor(workbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));

		this.shellProvider = shellProvider;
		this.links = links;
	}
	
	@Override
	public String getLabel() {
		if ( links.size() == 1 ) {
			Link l = ((IItemNode)links.iterator().next()).getLink();
			return "Delete link -> "+l.getDestinationName();
		}
		return "Delete links ...";
	}
	
	@Override
	public void run(IItemNode[] selection) throws CadseException {
		MultiStatus ms = new MultiStatus(WSPlugin.PLUGIN_ID,0,"Delete links errors", null);
		try {
			View.getInstance().getWorkspaceDomain().beginOperation("delete link action 2");
			for (IItemNode iv : links) {
			
				final Link l = iv.getLink();
				IItemManager im = WSPlugin.getManager(l.getSource());
				String error = im.canDeleteLink(l);
				if (error != null) {
					MessageDialog.openError(shellProvider.getShell(),MessageFormat.format("Cannot delete the link {0}",l.toString()),error);
				} else
				if (MessageDialog.openConfirm(shellProvider.getShell(),
						MessageFormat.format(
								"Delete the link from {0} to {1} ?",l.getSource().getName(),l.getDestinationName()),
						MessageFormat.format(
								"Do you want delete the link from {0} to {1} (link type is {2})?",
								l.getSource().getName(),l.getDestinationName(),l.getLinkType().getName()))) {
					WSODeleteLink oper = new WSODeleteLink(l);
		            oper.execute();
		            if (oper.getEx() != null)
		            	ms.add(new Status(IStatus.ERROR, WSPlugin.PLUGIN_ID,0,"Delete link "+l, oper.getEx()));
		            try {
		    			View.getInstance().getTestService().registerIfNeed(oper);
		    		} catch (TestException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		}
				}
			}
		} catch (Throwable e) {
			ms.add(new Status(IStatus.ERROR, WSPlugin.PLUGIN_ID,0,"Delete links", e));
		}  finally {
			View.getInstance().getWorkspaceDomain().endOperation();
		}
		if (!ms.isOK())
			WSPlugin.log(ms);
		
		
	
	}
	
	

}
