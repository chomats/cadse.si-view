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

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.viewers.TreeViewer;

import fede.workspace.tool.view.node.AbstractCadseViewNode;
import fede.workspace.tool.view.node.CadseViewModelController;
import fede.workspace.tool.view.node.ItemTypeNode;
import fede.workspace.tool.view.node.LinkTypeNode;
import fede.workspace.tool.view.node.RootNode;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.eclipse.view.IViewLinkManager;

public class LinkRootNode extends RootNode implements CadseViewModelController {

	public AbstractCadseViewNode[] getChildren(AbstractCadseViewNode node) {
		if (node == this) {
			return getLinkTypeNodeAndItemTypeNode(this, parenttype, parentitem, linkview);
		}

		if (node.getKind() == LINK_TYPE_OUTGOING) {
			return new ItemTypeNode[] { new ItemTypeNode(this, node, node.getLinkType().getDestination()) };
		}

		if (node.getKind() == ITEM_TYPE) {
			ItemType it = node.getItemType();
			if (it == null) {
				return EMPTY;
			}
			ItemType[] subt = it.getSubTypes();
			if (subt == null || subt.length == 0) {
				return EMPTY;
			}
			SortedSet<ItemTypeNode> ret = new TreeSet<ItemTypeNode>(new ItemTypeNode.ItemTypeNodeComparator());
			LinkType aLT = getLinkTypeFromNode(node);

			for (ItemType anIT : subt) {
				if (linkview != null) {
					if (!linkview.canCreateFrom(parentitem, aLT, anIT)) {
						continue;
					}
				}
				ret.add(new ItemTypeNode(this, node, anIT));
			}

			return ret.toArray(new ItemTypeNode[ret.size()]);
		}

		return EMPTY;
	}

	public static AbstractCadseViewNode[] getLinkTypeNodeAndItemTypeNode(LinkRootNode parentnode, ItemType parenttype,
			Item parentitem, IViewLinkManager linkview) {
		if (parenttype == null) {
			return EMPTY;
		}
		List<LinkType> olt = parenttype.getOutgoingLinkTypes();
		if (olt.size() == 0) {
			return EMPTY;
		}
		SortedSet<LinkTypeNode> ret = new TreeSet<LinkTypeNode>(new LinkTypeNode.LinkTypeNodeComparator());
		for (LinkType aLT : olt) {
			if (aLT.isDerived()) {
				continue;
			}
			if (aLT.isPart()) {
				continue;
			}
			if (aLT.getMax() != LinkType.UNBOUNDED) {
				if (parentitem != null && parentitem.getOutgoingItems(aLT, true).size() >= aLT.getMax()) {
					continue;
				}
			}
			if (linkview != null) {
				if (!linkview.canCreateLinkFrom(parentitem, aLT)) {
					continue;
				}
			}
			ret.add(new LinkTypeNode(parentnode, parentnode, aLT));
		}

		return ret.toArray(new LinkTypeNode[ret.size()]);
	}

	public LinkType getLinkTypeFromNode(AbstractCadseViewNode node) {
		while (node != null) {
			if (node.getKind() == AbstractCadseViewNode.LINK_TYPE_OUTGOING) {
				break;
			}
			node = (AbstractCadseViewNode) node.getParent();
		}
		if (node != null) {
			return node.getLinkType();
		}
		return null;
	}

	public String getDisplayToolTip(AbstractCadseViewNode linkType) {
		return "";
	}

	public TreeViewer getFTreeViewer() {
		return ftreeviewer;
	}

	public boolean hasChildren(AbstractCadseViewNode node) {
		if (node == this) {
			return parenttype != null && parenttype.getOutgoingLinkTypes().size() != 0;
		}
		if (node.getKind() == LINK_TYPE_OUTGOING) {
			return true;
		}
		if (node.getKind() == ITEM_TYPE) {
			ItemType it = node.getItemType();
			return it != null && it.getSubTypes() != null && it.getSubTypes().length != 0;
		}
		return false;
	}

	public boolean isAggregationLink(AbstractCadseViewNode node) {
		return true;
	}

	public boolean isRecomputeChildren() {
		return true;
	}

	private ItemType			parenttype;
	private TreeViewer			ftreeviewer;
	private Item				parentitem;
	private IViewLinkManager	linkview;

	public LinkRootNode() {
		super();
		ctl = this;
		// ctl.add(this);
	}

	public void setParentItem(Item parentitem) {
		this.parentitem = parentitem;
	}

	public void setParentType(ItemType parenttype) {
		this.parenttype = parenttype;
	}

	public ItemType getParentType() {
		return parenttype;
	}

	public void setTreeViewer(TreeViewer ftreeviewer) {
		this.ftreeviewer = ftreeviewer;
	}

	public TreeViewer getTreeViewer() {
		return ftreeviewer;
	}

	public void setLinkViewManager(IViewLinkManager linkview) {
		this.linkview = linkview;
	}

	public void add(AbstractCadseViewNode node) {
	}

	public void remove(AbstractCadseViewNode node) {
	}

	public int isSelected(IItemNode node) {
		return IItemNode.DESELECTED;
	}

}
