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

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.core.delta.ItemDelta;

public class ItemOperationsFromLinkOfLinkTypeRule extends Rule {
	Comparator<Item>	sortFct	= null;
	LinkType			_lt;
	boolean				resolved;
	boolean				inverse;
	FilterItem			filter;

	/**
	 * 
	 * @param lt
	 *            if lt is null, lt is keep from node
	 * @param sortFct
	 *            can be null
	 * @param resolved
	 *            only resolved item
	 * @param inverse
	 *            incoming link
	 * @param filter
	 *            can be null, filter acceptable item
	 */
	public ItemOperationsFromLinkOfLinkTypeRule(LinkType lt, Comparator<Item> sortFct, boolean resolved,
			boolean inverse, FilterItem filter) {
		super();
		this._lt = lt;
		this.resolved = resolved;
		this.inverse = inverse;
		this.sortFct = sortFct;
		this.filter = filter;
	}

	/**
	 * 
	 * @param lt
	 *            if lt is null, lt is keep from node
	 * @param sortFct
	 *            can be null
	 * @param resolved
	 *            only resolved item
	 * @param inverse
	 *            incoming link
	 * @param filter
	 *            cannot be null, a list of acceptable item
	 */
	public ItemOperationsFromLinkOfLinkTypeRule(LinkType lt, Comparator<Item> sortFct, boolean resolved,
			boolean inverse, Collection<Item> filter) {
		super();
		this._lt = lt;
		this.resolved = resolved;
		this.inverse = inverse;
		this.sortFct = sortFct;
		this.filter = new ArrayFilterItem(filter);
	}

	@Override
	public void computeChildren(FilteredItemNode root, AbstractCadseViewNode node, List<AbstractCadseViewNode> ret) {
		ItemDelta item = (ItemDelta) node.getItem();
		LinkType lt = _lt;
		if (lt == null) {
			lt = node.getLinkType();
		}

		if (item != null) {
			Collection<ItemDelta> values = null;
			if (lt != null) {
				if (inverse) {
					values = item.getIncomingItems(lt, true, true);
				} else {
					values = item.getOutgoingItems(true, lt, resolved);
				}
			} else {
				if (inverse) {
					values = item.getIncomingItems(true, true);
				} else {
					values = item.getOutgoingItems(true, resolved);
				}
			}
			if (sortFct != null) {
				TreeSet<ItemDelta> values2 = new TreeSet<ItemDelta>(sortFct);
				values2.addAll(values);
				values = values2;
			}
			for (Item valueItem : values) {
				if (filter == null || filter.accept(valueItem)) {
					ret.add(new ItemNode(root, node, valueItem));
				}
			}
		}
	}
}