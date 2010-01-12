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
package fede.workspace.tool.view.addlink;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import org.eclipse.jface.viewers.TreeViewer;

import fede.workspace.tool.view.node.AbstractCadseViewNode;
import fede.workspace.tool.view.node.CadseViewModelController;
import fede.workspace.tool.view.node.ItemNode;
import fede.workspace.tool.view.node.ItemTypeNode;
import fede.workspace.tool.view.node.RootNode;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.LinkType;

public class ItemRootNode extends RootNode implements CadseViewModelController {

	private LinkType		selectedLinkType;
	private ItemTypeNode	selectedNode;
	private TreeViewer		ftreeviewer;
	private Item			itemParent;

	public ItemRootNode() {
		super();
		ctl = this;
		// ctl.add(this);
	}

	public void setTreeViewer(TreeViewer ftreeviewer) {
		this.ftreeviewer = ftreeviewer;
	}

	public TreeViewer getTreeViewer() {
		return ftreeviewer;
	}

	public AbstractCadseViewNode[] getChildren(AbstractCadseViewNode node) {
		if (node == this) {
			if (selectedNode == null || selectedNode.getItemType() == null) {
				return EMPTY;
			}

			// ItemType it = selectedNode.getItemType();
			Collection<Item> items = selectedLinkType.getSelectingDestination(getItemParent());
			if (items == null || items.size() == 0) {
				return EMPTY;
			}

			TreeSet<Item> ss = new TreeSet<Item>(new Comparator<Item>() {

				public int compare(Item o1, Item o2) {
					return o1.getDisplayName().compareTo(o2.getDisplayName());
				}
			});

			ss.addAll(items);
			List<ItemNode> ret = new ArrayList<ItemNode>();
			for (Item anItem : ss) {
				// if (anItem.getType() == it) {
				ret.add(new ItemNode(this, node, anItem));
				// }
			}
			return ret.toArray(new ItemNode[ret.size()]);

		}

		return EMPTY;
	}

	public Item getItemParent() {
		return this.itemParent;
	}

	public void setItemParent(Item itemParent) {
		this.itemParent = itemParent;
	}

	public void setSelectedLinkType(LinkType selectedLinkType) {
		this.selectedLinkType = selectedLinkType;
	}

	public LinkType getSelectedLinkType() {
		return selectedLinkType;
	}

	public String getDisplayToolTip(AbstractCadseViewNode linkType) {
		return "";
	}

	public TreeViewer getFTreeViewer() {
		return ftreeviewer;
	}

	public boolean hasChildren(AbstractCadseViewNode node) {
		if (node == this) {
			return selectedNode != null && selectedNode.getItemType() != null && hasItem(selectedNode.getItemType());
		}

		return false;
	}

	private boolean hasItem(ItemType itemType) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isAggregationLink(AbstractCadseViewNode node) {
		return true;
	}

	public boolean isRecomputeChildren() {
		return true;
	}

	public void setItemTypeNode(ItemTypeNode node) {
		this.selectedNode = node;
	}

	public ItemTypeNode getItemTypeNode() {
		return selectedNode;
	}

	public void add(AbstractCadseViewNode node) {
	}

	public void remove(AbstractCadseViewNode node) {
	}

	public int isSelected(IItemNode node) {
		return IItemNode.DESELECTED;
	}

}
