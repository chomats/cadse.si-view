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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;

public class ItemsFromItemTypeRule extends Rule {
	Comparator<Item>	sortFct	= null;
	ItemType			it;
	private FilterItem	_filter;

	public ItemsFromItemTypeRule(ItemType it, Comparator<Item> sortFct) {
		super();
		this.it = it;
		this.sortFct = sortFct;
	}

	public ItemsFromItemTypeRule(ItemType it, Comparator<Item> sortFct, FilterItem filter) {
		super();
		this.it = it;
		this.sortFct = sortFct;
		this._filter = filter;
	}

	public ItemsFromItemTypeRule(ItemType it, Comparator<Item> sortFct, FilterItem filter, boolean filterStatic,
			boolean filterHidden, boolean filterNotRoot) {
		super();
		this.it = it;
		this.sortFct = sortFct;
		this._filter = filter;
		ArrayList<FilterItem> filters = new ArrayList<FilterItem>();
		if (filter != null) {
			filters.add(filter);
		}
		if (filterStatic) {
			filters.add(new FilterItem.ItemNotStaticFilter());
		}
		if (filterHidden) {
			filters.add(new FilterItem.TypeNotHiddenFilter());
		}
		if (filterNotRoot) {
			filters.add(new FilterItem.TypeRootFilter());
		}

		if (filters.size() == 1) {
			_filter = filters.get(0);
		} else if (filters.size() == 0) {
			_filter = null;
		} else {
			_filter = new FilterItem.AndFilter(filters.toArray(new FilterItem[filters.size()]));
		}
	}

	@Override
	public void computeChildren(FilteredItemNode root, AbstractCadseViewNode node, List<AbstractCadseViewNode> ret) {
		Collection<Item> values = it.getItems();
		if (sortFct != null) {
			TreeSet<Item> values2 = new TreeSet<Item>(sortFct);
			values2.addAll(values);
			values = values2;
		}
		for (Item valueItem : values) {
			if (_filter != null && !_filter.accept(valueItem)) {
				continue;
			}
			ret.add(new ItemNode(root, node, valueItem));
		}
	}
}