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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;

import fr.imag.adele.cadse.core.DefaultItemManager;
import fr.imag.adele.cadse.core.LogicalWorkspace;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.impl.CadseCore;
import fede.workspace.eclipse.validation.ValidationMarker;

public class CadseFixer implements IMarkerResolutionGenerator2 {

	private static final IMarkerResolution [] NO_RESOLUTION = new IMarkerResolution[0];	
	private static final IMarkerResolution[] toArray(List<IMarkerResolution> resolutions) {
		return resolutions.toArray(new IMarkerResolution[resolutions.size()]); 	
	}
	
	public IMarkerResolution[] getResolutions(IMarker marker) {
		List<IMarkerResolution> resolutions	= new ArrayList<IMarkerResolution>();
		
		LogicalWorkspace workspace	= CadseCore.getLogicalWorkspace();
		
    	Item item 			= ValidationMarker.getItem(marker,workspace);
    	if (item == null)
			return NO_RESOLUTION;
        
    	int errorCode		= ValidationMarker.getErrorCode(marker);
		//String parameters[]	= ValidationMarker.getParameters(marker);
		switch (errorCode) {
			case DefaultItemManager.CODE_MISSING_PARENT :
				resolutions.add(new RemoveItemResolution(this,marker, item,true));
				break;
		}
		
		return toArray(resolutions);
	}

	public boolean hasResolutions(IMarker marker) {
		LogicalWorkspace workspace	= CadseCore.getLogicalWorkspace();
		
    	Item item 			= ValidationMarker.getItem(marker,workspace);
    	if (item == null)
			return false;
    	
		int errorCode		= ValidationMarker.getErrorCode(marker);
		
		switch (errorCode) {
			case DefaultItemManager.CODE_MISSING_PARENT : 
				return true;
		}
		
		return false;
	}

	public IMarker[] findRemoveItemMackers(IMarker thisMarker, IMarker[] markers) {
		List<IMarker> ret = new ArrayList<IMarker>();
		for (IMarker marker : markers) {
			try {
				if (thisMarker.equals(marker)) continue;
				
				if (ValidationMarker.MARKER_ID.equals(marker.getType()) && hasResolutions(marker)) {
					ret.add(marker);
				}
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
//		IWorkspaceRoot w = ResourcesPlugin.getWorkspace().getRoot();
//		try {
//			IMarker[] fikndmarkers = w.findMarkers(ValidationMarker.MARKER_ID, false, IResource.DEPTH_INFINITE);
//		} catch (CoreException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;// TODO Auto-generated method stub
		
		return (IMarker[]) ret.toArray(new IMarker[ret.size()]);
	}

}
