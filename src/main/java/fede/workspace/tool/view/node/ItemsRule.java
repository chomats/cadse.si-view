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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import fr.imag.adele.cadse.core.Item;

public class ItemsRule extends Rule {
	Collection<Item>	items;
	private FilterItem	filter;

	public ItemsRule(FilterItem filter, Item... items) {
		super();
		this.items = Arrays.asList(items);
		this.filter = filter;
	}

	public ItemsRule(FilterItem filter, Collection<Item> items) {
		super();
		this.items = items;
		this.filter = filter;
	}

	public ItemsRule(Item... items) {
		super();
		this.items = Arrays.asList(items);
		this.filter = null;
	}

	@Override
	public void computeChildren(FilteredItemNode root, AbstractCadseViewNode node, List<AbstractCadseViewNode> ret) {
		for (Item item : items) {
			if (filter == null || filter.accept(item)) {
				ret.add(new ItemNode(root, node, item));
			}
		}
	}
}
