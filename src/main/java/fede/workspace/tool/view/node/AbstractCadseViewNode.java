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
	
	public CadseViewModelController getCtl() {
		return ctl;
	}
	
	@Override
	final public boolean equals(Object arg0) {
		final Object elementModel = getElementModel();
		if (elementModel == null)
			return super.equals(arg0);
		if (arg0 instanceof IItemNode) {
			Object e = ((IItemNode)arg0).getElementModel();
			return (e == elementModel || elementModel.equals(e));
		}
		return false;
	}
	
	@Override
	final public int hashCode() {
		final Object elementModel = getElementModel();
		if (elementModel == null || elementModel == this)
			return super.hashCode();
		return elementModel.hashCode();
	}

	public boolean recomputeChildren() {
		if (children == null) {
			children = new ArrayList<AbstractCadseViewNode>();
		}
		AbstractCadseViewNode[] children2 = ctl.getChildren(this);
		if (children.equals(Arrays.asList(children2)))
			return false;
		
		ArrayList<AbstractCadseViewNode> children_removed = new ArrayList<AbstractCadseViewNode>();
		ArrayList<AbstractCadseViewNode> children_added = new ArrayList<AbstractCadseViewNode>();
		boolean[] ok = new boolean[children.size()];
		for (int i = 0; i < children2.length; i++) {
			AbstractCadseViewNode c = children2[i];
			int indexOld = children.indexOf(c);
			if (indexOld == -1)
				children_added.add(c);
			else {
				children2[i] = children.get(indexOld);
				ok[indexOld] =true;
			}
		}
		for (int i = 0; i < ok.length; i++) {
			if (!ok[i]) {
				children_removed.add(children.get(i));
			}
		}
		if (!ctl.getFTreeViewer().isBusy()) {
			ctl.getFTreeViewer().add(this, children_added.toArray());
			ctl.getFTreeViewer().remove(children_removed.toArray());
		}
		children.clear();
		children.addAll(Arrays.asList(children2));
		return !(children_added.isEmpty() && children_removed.isEmpty());
	}
	

	public AbstractCadseViewNode[] getChildren() {
		if (children == null /*|| ctl.isRecomputeChildren()*/) {
			recomputeChildren();
		}

		return children.toArray(new AbstractCadseViewNode[children.size()]);
	}

	@Override
	public boolean hasChildren() {
		return ctl.hasChildren(this);
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
		super.close();
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
		if (children != null) {
			for (AbstractCadseViewNode child : children) {
				child.delete();
			}
		}
	}
}
