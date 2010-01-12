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
package fede.workspace.tool.fixer;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.views.markers.WorkbenchMarkerResolution;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.LogicalWorkspace;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.impl.CadseCore;
import fede.workspace.eclipse.validation.ValidationMarker;
import fede.workspace.tool.view.WSPlugin;

public class RemoveItemResolution extends WorkbenchMarkerResolution implements IMarkerResolution2 {

	private final CadseFixer fixer;
	private final Item 	item;
	private boolean deletecontent;
	private IMarker fMarker ;
	
	public RemoveItemResolution(CadseFixer fixer, IMarker marker, Item item, boolean deletecontent) {
		this.item	= item;
		this.deletecontent = deletecontent;
		this.fixer = fixer;
		this.fMarker = marker;
	}
	
	public String getDescription() {
		return getLabel();
	}

	public Image getImage() {
		ISharedImages workbenchImages= WSPlugin.getDefault().getWorkbench().getSharedImages();
		return workbenchImages.getImage(ISharedImages.IMG_TOOL_DELETE);
	}

	public String getLabel() {
		String label = "delete item '"+item.getDisplayName()+"'";
		return label;
	}

	public void run(IMarker marker) {
        try {
        	LogicalWorkspace workspace	= CadseCore.getLogicalWorkspace();
    		Item item 			= ValidationMarker.getItem(marker,workspace);
        	if (item != null)
        		item.delete(deletecontent);
        } catch (CadseException e) {
        	WSPlugin.logException(e);
        }
	}

	@Override
	public IMarker[] findOtherMarkers(IMarker[] markers) {
		return fixer.findRemoveItemMackers(fMarker, markers);
	}

	

}
