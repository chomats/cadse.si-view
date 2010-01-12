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
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.LinkType;

public class ContextRule extends Rule {
	Comparator<Item>	sortFct	= null;
	boolean				resolved;
	boolean				inverse;
	FilterItem			filter;

	@Override
	public void computeChildren(FilteredItemNode root, AbstractCadseViewNode node, List<AbstractCadseViewNode> ret) {
		Item item = findItem(node);

		Object o = node.getElementModel();
		if (o instanceof LinkType) {
			if (item != null) {
				Collection<Item> values = item.getOutgoingItems((LinkType) o, resolved);
				if (sortFct != null) {
					TreeSet<Item> values2 = new TreeSet<Item>(sortFct);
					values2.addAll(values);
					values = values2;
				}
				for (Item valueItem : values) {
					ret.add(new ItemNode(root, node, valueItem));
				}
			}
		} else if (o instanceof ItemType) {
			Collection<Item> values = ((ItemType) o).getItems();
			if (sortFct != null) {
				TreeSet<Item> values2 = new TreeSet<Item>(sortFct);
				values2.addAll(values);
				values = values2;
			}
			for (Item valueItem : values) {
				ret.add(new ItemNode(root, node, valueItem));
			}
		}
	}

	protected Item findItem(AbstractCadseViewNode node) {
		Item item = node.getItem();
		if (item == null) {
			if (node.getParent() != null) {
				item = node.getParent().getItem();
			}
		}
		return item;
	}
}