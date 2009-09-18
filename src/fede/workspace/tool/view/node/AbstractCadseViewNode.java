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
import java.util.Arrays;
import java.util.List;

import fede.plugin.workspace.filters.INameable;
import fede.workspace.tool.view.ItemInViewer;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.Link;

public abstract class AbstractCadseViewNode extends ItemInViewer implements IItemNode, INameable {
	public static final AbstractCadseViewNode[]	EMPTY	= new AbstractCadseViewNode[0];

	protected CadseViewModelController			ctl;
	List<AbstractCadseViewNode>					children;

	private int									state	= -1;

	protected AbstractCadseViewNode(CadseViewModelController ctl, int kind, AbstractCadseViewNode parent) {
		super(kind, parent);
		this.ctl = ctl;
		// ctl.add(this);
	}

	protected AbstractCadseViewNode(int kind, AbstractCadseViewNode parent) {
		super(kind, parent);
	}

	public List<AbstractCadseViewNode> children() {
		return children;
	}

	public AbstractCadseViewNode[] recomputeChildren() {
		if (children == null) {
			children = new ArrayList<AbstractCadseViewNode>();
		}
		AbstractCadseViewNode[] children2 = ctl.getChildren(this);
		ArrayList<AbstractCadseViewNode> children_removed = new ArrayList<AbstractCadseViewNode>(children);
		children_removed.removeAll(Arrays.asList(children2));
		if (children_removed.size() != 0) {
			for (AbstractCadseViewNode node : children_removed) {
				node.delete();
			}
			ctl.getFTreeViewer().remove(children_removed.toArray());
		}
		ArrayList<AbstractCadseViewNode> children_added = new ArrayList<AbstractCadseViewNode>(Arrays.asList(children2));
		children_added.removeAll(children);
		if (children_added.size() != 0) {
			for (AbstractCadseViewNode node : children_added) {
				ctl.add(node);
			}
			ctl.getFTreeViewer().add(this, children_added.toArray());
		}

		children.clear();
		children.addAll(Arrays.asList(children2));
		return children2;
	}

	public AbstractCadseViewNode[] getChildren() {
		if (children == null) {
			children = new ArrayList<AbstractCadseViewNode>();
			AbstractCadseViewNode[] children2 = ctl.getChildren(this);

			children.addAll(Arrays.asList(children2));

			for (AbstractCadseViewNode child : children2) {
				ctl.add(child);
			}

			return children2;
		}
		if (!ctl.isRecomputeChildren()) {
			return children.toArray(new AbstractCadseViewNode[children.size()]);
		}

		AbstractCadseViewNode[] children2 = ctl.getChildren(this);
		for (AbstractCadseViewNode child : children) {
			ctl.remove(child);
		}

		children.clear();
		children.addAll(Arrays.asList(children2));
		for (AbstractCadseViewNode child : children2) {
			ctl.add(child);
		}
		// ctl.getFTreeViewer().add(this, children.toArray());
		return children2;
	}

	@Override
	public boolean hasChildren() {
		return ctl.hasChildren(this);
	}

	@Override
	public ItemInViewer[] getChildren(int flag) {
		return getChildren();
	}

	public ItemNode getOrCreateNode(Item item) {
		if (children != null) {
			for (AbstractCadseViewNode n : children) {
				if (n.kind == ITEM && n.getItem() == item) {
					return (ItemNode) n;
				}
			}
		}
		return new ItemNode(ctl, this, item);
	}

	public LinkNode getOrCreateNode(Link l) {
		if (children != null) {
			for (AbstractCadseViewNode n : children) {
				if (n.kind == LINK_OUTGOING && n.getLink() == l) {
					return (LinkNode) n;
				}
			}
		}
		return new LinkNode(ctl, this, l);
	}

	public LinkNode getOrCreateIncomingNode(Link l) {
		if (children != null) {
			for (AbstractCadseViewNode n : children) {
				if (n.kind == LINK_INCOMING && n.getLink() == l) {
					return (LinkNode) n;
				}
			}
		}
		return new LinkNode(ctl, this, l, true);
	}

	@Override
	public ItemType getItemType() {
		return null;
	}

	public abstract Object getElementModel();

	public String getName() {
		return toString();
	}

	@Override
	public String getToolTip() {
		return ctl.getDisplayToolTip(this);
	}

	@Override
	public void close() {
		children = null;
	}

	public void removeAndClose() {
		if (children == null) {
			return;
		}

		final Object[] arrayOfChildren = children.toArray();
		for (AbstractCadseViewNode child : children) {
			child.delete();
		}
		children = null;

		ctl.getFTreeViewer().remove(arrayOfChildren);
	}

	public int isSelected() {
		if (state == -1) {
			return ctl.isSelected(this);
		}
		return state;
	}

	public void setSelected(int state) {
		if (state < 0 || state > SELECTED) {
			throw new IllegalArgumentException("value must be in 0..2");
		}
		this.state = state;
	}

	public void delete() {
		ctl.remove(this);
		if (children != null) {
			for (AbstractCadseViewNode child : children) {
				child.delete();
			}
		}
	}
}
