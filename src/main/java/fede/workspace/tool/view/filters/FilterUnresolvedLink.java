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
 *
 * Copyright (C) 2006-2010 Adele Team/LIG/Grenoble University, France
 */
package fede.workspace.tool.view.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import fr.imag.adele.cadse.core.Link;
import fede.workspace.tool.view.ItemInViewer;

public class FilterUnresolvedLink extends ViewerFilter {
	
	public FilterUnresolvedLink() {
		super();
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof ItemInViewer) {
			ItemInViewer itemInViewer = ((ItemInViewer)element);
			int kind = itemInViewer.getKind();
			switch (kind) {
				
				case ItemInViewer.LINK_OUTGOING:
				case ItemInViewer.LINK_INCOMING:
					Link link = itemInViewer.getLink();
					if (link == null)
						return true;
					return link.getResolvedDestination() != null;
			}
		}
		return true;
	}
}
