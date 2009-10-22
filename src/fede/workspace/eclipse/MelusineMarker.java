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
package fede.workspace.eclipse;

import java.text.MessageFormat;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import fr.imag.adele.cadse.core.CompactUUID;
import fr.imag.adele.cadse.core.LogicalWorkspace;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fede.workspace.tool.view.WSPlugin;

public class MelusineMarker {

	private final static String MARKER_ID 			= WSPlugin.NAMESPACE_ID+".melusine.marker";
	
	private final static String MARKER_ITEM 		= "item";
	private final static String MARKER_ITEM_TYPE 	= "itemType";
	private final static String MARKER_DESCRIPTION 	= "description";
	private final static String MARKER_PARAMETERS 	= "parameters";

	
	public static IMarker mark(IResource resource) throws CoreException {
		return resource.createMarker(MARKER_ID);
	}

	public static void unmark(IResource resource, boolean includeSubtypes, int depth) throws CoreException {
		resource.deleteMarkers(MARKER_ID,includeSubtypes,depth);
	}

	public static void setSeverity(IMarker marker, int severity) throws CoreException {
		marker.setAttribute(IMarker.SEVERITY,severity);
		
	}
	
	public static void setItemTypeId(IMarker marker, CompactUUID itemTypeId) throws CoreException {
		marker.setAttribute(MARKER_ITEM_TYPE,itemTypeId.toString());
	}

	public static void setItemId(IMarker marker, CompactUUID itemId) throws CoreException {
		marker.setAttribute(MARKER_ITEM,itemId.toString());
	}
	
	public static void setItem(IMarker marker, Item item) throws CoreException {
		setItemTypeId(marker,item.getType().getId());
		setItemId(marker,item.getId());
	}

	public static void setDescription(IMarker marker, String description, Object ... parameters) throws CoreException {
		
		StringBuffer encodedParameters = new StringBuffer();
		for (Object parameter : parameters) {
			encodedParameters.append(parameter);
			encodedParameters.append(";");
		}
		
		marker.setAttribute(MARKER_DESCRIPTION,description);
		marker.setAttribute(MARKER_PARAMETERS,encodedParameters.toString());
		
		marker.setAttribute(IMarker.MESSAGE,MessageFormat.format(description,parameters));
	}

	public static CompactUUID getItemTypeId(IMarker marker) {
		String uuid = marker.getAttribute(MARKER_ITEM_TYPE,null);
		if (uuid == null) return null;
		return CompactUUID.fromString(uuid);
	}

	public static CompactUUID getItemId(IMarker marker) {
		String uuid = marker.getAttribute(MARKER_ITEM,null);
		if (uuid == null) return null;
		return CompactUUID.fromString(uuid);
	}

	public static ItemType getItemType(IMarker marker, LogicalWorkspace workspace) {
		return workspace.getItemType(getItemTypeId(marker));
	}

	public static Item getItem(IMarker marker, LogicalWorkspace workspace) {
		return workspace.getItem(getItemId(marker));
	}

	public static String getMessage(IMarker marker) {
		return marker.getAttribute(IMarker.MESSAGE,"");
	}

	public static String getDescription(IMarker marker) {
		return marker.getAttribute(MARKER_DESCRIPTION,"");
	}
	
	public static String[] getParameters(IMarker marker) {
		return marker.getAttribute(MARKER_PARAMETERS,"").split(";");
	}

}
