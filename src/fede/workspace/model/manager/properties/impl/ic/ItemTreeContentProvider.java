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
/**
 *
 */
package fede.workspace.model.manager.properties.impl.ic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.core.delta.ItemDelta;

public class ItemTreeContentProvider implements ITreeContentProvider {
	final private LinkType[]		links;
	final private Comparator<Item>	comparator;

	public ItemTreeContentProvider(Comparator<Item> comparator, LinkType... lt) {
		this.links = lt;
		this.comparator = comparator;
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof ItemType) {
			List<Item> items = ((ItemType) parentElement).getItems();
			return sort(items.toArray(new Item[items.size()]));
		}

		if (parentElement instanceof Item) {
			Item i = ((Item) parentElement);
			List<Item> outgoingItems = new ArrayList<Item>();
			ONE: for (Link l : i.getOutgoingLinks()) {
				LinkType type = l.getLinkType();
				for (int j = 0; j < links.length; j++) {
					if (links[j] == type) {
						final Item destination = l.getDestination();
						if (destination instanceof ItemDelta && ((ItemDelta) destination).isAdded()) {
							continue;
						}
						outgoingItems.add(destination);
						continue ONE;
					}
				}
			}
			return sort(outgoingItems.toArray(new Item[outgoingItems.size()]));
		}
		return new Object[0];
	}

	public Object getParent(Object element) {

		return null;
	}

	public boolean hasChildren(Object element) {
		return getChildren(element).length != 0;
	}

	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof Item[]) {
			return sort((Item[]) inputElement);
		}

		return getChildren(inputElement);
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	protected Item[] sort(Item[] selectableValues) {
		if (comparator == null) {
			return selectableValues;
		}

		Arrays.sort(selectableValues, this.comparator);
		return selectableValues;
	}

}