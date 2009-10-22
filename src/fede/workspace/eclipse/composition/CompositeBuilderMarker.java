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
package fede.workspace.eclipse.composition;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import fede.workspace.eclipse.MelusineMarker;
import fede.workspace.tool.view.WSPlugin;

public class CompositeBuilderMarker extends MelusineMarker {

	private final static String MARKER_ID 	= WSPlugin.NAMESPACE_ID+".composite.builder.marker";

	public static IMarker mark(IResource resource) throws CoreException {
		return resource.createMarker(MARKER_ID);
	}

	public static void unmark(IResource resource, boolean includeSubtypes, int depth) throws CoreException {
		resource.deleteMarkers(MARKER_ID,includeSubtypes,depth);
	}
	
}
