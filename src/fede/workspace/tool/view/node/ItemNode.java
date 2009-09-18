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

import fede.workspace.tool.view.ItemInViewer;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.LinkType;

public class ItemNode extends AbstractCadseViewNode {
	final Item	item;

	public ItemNode(CadseViewModelController viewer, AbstractCadseViewNode parent, Item item) {
		super(ItemInViewer.ITEM, parent);
		this.item = item;
		ctl = viewer;
		// ctl.add(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fede.workspace.tool.view.node.IItemNode#getItem()
	 */
	@Override
	public Item getItem() {
		return item;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fede.workspace.tool.view.node.IItemNode#getLink()
	 */
	@Override
	public Link getLink() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fede.workspace.tool.view.node.IItemNode#getLinkType()
	 */
	@Override
	public LinkType getLinkType() {
		return null;
	}

	@Override
	public ItemType getItemType() {
		return item == null ? null : item.getType();
	}

	@Override
	public String toString() {
		return item.getDisplayName();
	}

	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0) && this.item == ((IItemNode) arg0).getItem();
	}

	@Override
	public int hashCode() {
		return this.item.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fede.workspace.tool.view.node.IItemNode#getElementModel()
	 */
	@Override
	public Object getElementModel() {
		return getItem();
	}
}
