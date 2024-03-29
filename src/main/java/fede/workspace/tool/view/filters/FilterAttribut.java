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

import fr.imag.adele.cadse.core.Item;
import fede.workspace.tool.view.ItemInViewer;
import fr.imag.adele.cadse.core.attribute.IAttributeType;

public class FilterAttribut extends ViewerFilter {
	final IAttributeType<?> attributKey;
	final Object value;
	
	
	public FilterAttribut(IAttributeType<?> key, Object value) {
		super();
		this.attributKey = key;
		this.value = value;
	}


	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof ItemInViewer) {
			ItemInViewer itemInViewer = ((ItemInViewer)element);
			int kind = itemInViewer.getKind();
			switch (kind) {
				
				case ItemInViewer.LINK_OUTGOING:
				case ItemInViewer.LINK_INCOMING:
					Item item = itemInViewer.getItem();
					if (item == null)
						return true;
					if (!item.isResolved())
						return true;
					Object value_attr = item.getAttribute(attributKey);
					if (value_attr == null) 
					 	return true;
					return !value_attr.equals(value);
			}
		}
		return true;
	}
}
