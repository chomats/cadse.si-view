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
package fede.workspace.tool.view.node;

import java.util.Comparator;

import fede.workspace.tool.view.ItemInViewer;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.LinkType;

public class ItemTypeNode extends AbstractCadseViewNode {
	public final static class ItemTypeNodeComparator implements Comparator<ItemTypeNode> {

		public int compare(ItemTypeNode o1, ItemTypeNode o2) {
			return o1.getItemType().getId().compareTo(o2.getItemType().getId());
		}

	}

	final ItemType	itemType;

	public ItemTypeNode(CadseViewModelController viewer, AbstractCadseViewNode parent, ItemType itemType) {
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
		return itemType;
	}

	@Override
	public String getToolTip() {
		return ctl.getDisplayToolTip(this);
	}

	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0) && this.itemType == ((ItemTypeNode) arg0).itemType;
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
		return getItemType();
	}

}
