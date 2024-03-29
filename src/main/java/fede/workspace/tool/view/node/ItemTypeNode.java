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
package fede.workspace.tool.view.node;

import java.util.Comparator;

import fede.workspace.tool.view.ItemInViewer;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.core.TypeDefinition;

public class ItemTypeNode extends AbstractCadseViewNode {
	public final static class ItemTypeNodeComparator implements Comparator<ItemTypeNode> {

		public int compare(ItemTypeNode o1, ItemTypeNode o2) {
			return o1.getItemType().getId().compareTo(o2.getItemType().getId());
		}

	}

	final TypeDefinition	itemType;

	public ItemTypeNode(CadseViewModelController viewer, AbstractCadseViewNode parent, TypeDefinition itemType) {
		super(ItemInViewer.ITEM_TYPE, parent);
		this.itemType = itemType;
		ctl = viewer;
		// ctl.add(this);

	}

	@Override
	public Item getItem() {
		return null;
	}

	@Override
	public Link getLink() {
		return null;
	}

	@Override
	public LinkType getLinkType() {
		return null;
	}

	@Override
	public ItemType getItemType() {
		return (ItemType) (itemType instanceof ItemType ? itemType : null);
	}

	@Override
	public String getToolTip() {
		return ctl.getDisplayToolTip(this);
	}

	@Override
	public String toString() {
		return itemType.getName();
	}

	public boolean isAggregation() {
		return true;
	}

	@Override
	public Object getElementModel() {
		return itemType;
	}

}
