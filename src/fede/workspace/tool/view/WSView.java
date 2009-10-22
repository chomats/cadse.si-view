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
package fede.workspace.tool.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

import fede.workspace.tool.view.node.AbstractCadseViewNode;
import fede.workspace.tool.view.node.ItemNode;
import fede.workspace.tool.view.node.LinkNode;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.core.LogicalWorkspace;
import fr.imag.adele.cadse.core.impl.CadseCore;
import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.eclipse.view.AbstractCadseTreeViewUI;
import fr.imag.adele.cadse.eclipse.view.AbstractCadseView;
import fede.workspace.tool.view.node.AbstractCadseViewNode;
import fede.workspace.tool.view.node.ItemNode;
import fede.workspace.tool.view.node.LinkNode;

/**
 * Cette vue repr?sente les item du workspace courant. Nous avons trois mode
 * d'affichage : - aggr?gations; - relations et link - relations et relations
 * inverse.
 * 
 * Le menu contextuel a une zone particuli?re. "WS-Actions" pour les actions du
 * workspace.
 * 
 */
/**
 * @generated
 */
class WSViewViewUI extends AbstractCadseTreeViewUI {
	private Action				showOrpholanItem;
	private Action				showOrpholanMetaItemType;
	private static final String	KEY_SHOW_OrpholanItem			= WSPlugin.NAMESPACE_ID + ".view.OrpholanItem";
	private static final String	KEY_SHOW_OrpholanMetaItemType	= WSPlugin.NAMESPACE_ID + ".view.OrpholanMetaItemType";

	boolean						_showOrpholanItem;
	boolean						_showOrpholanMetaItemType;

	public WSViewViewUI(IViewSite site) {
		super(site);
		setRecomputeChildren(true);
	}

	@Override
	public void loadState(IMemento memento) throws PartInitException {
		super.loadState(memento);
		if (memento != null) {
			Integer i;

			i = memento.getInteger(KEY_SHOW_OrpholanItem);
			_showOrpholanItem = (i == null ? false : i.intValue() == 1);

			i = memento.getInteger(KEY_SHOW_OrpholanMetaItemType);
			_showOrpholanMetaItemType = (i == null ? false : i.intValue() == 1);
		}
	}

	/**
	 * Save the state of this view through the sessions : three options : show
	 * all items, show the relations "to", show the relations "from"
	 */
	@Override
	public void saveState(IMemento memento) {
		memento.putInteger(KEY_SHOW_OrpholanItem, _showOrpholanItem ? 1 : 0);
		memento.putInteger(KEY_SHOW_OrpholanMetaItemType, _showOrpholanMetaItemType ? 1 : 0);
		super.saveState(memento);
	}

	@Override
	protected void makeActions() {
		// TODO Auto-generated method stub
		super.makeActions();
		showOrpholanItem = new Action("Show orphan items", IAction.AS_CHECK_BOX) {
			@Override
			public void run() {
				set_showOrpholanItem(!is_showOrpholanItem());
				contentStructreChanged();
			}
		};
		showOrpholanItem.setChecked(is_showOrpholanItem());
		showOrpholanMetaItemType = new Action("Show meta item type", IAction.AS_CHECK_BOX) {
			@Override
			public void run() {
				set_showOrpholanMetaItemType(!is_showOrpholanMetaItemType());
				contentStructreChanged();
			}
		};
		showOrpholanMetaItemType.setChecked(is_showOrpholanMetaItemType());
	}

	@Override
	protected void fillflags(IMenuManager manager) {
		manager.add(showOrpholanItem);
		manager.add(showOrpholanMetaItemType);
	}

	public boolean is_showOrpholanItem() {
		return _showOrpholanItem;
	}

	public boolean is_showOrpholanMetaItemType() {
		return _showOrpholanMetaItemType;
	}

	public void set_showOrpholanItem(boolean orpholanItem) {
		_showOrpholanItem = orpholanItem;
	}

	public void set_showOrpholanMetaItemType(boolean orpholanMetaItemType) {
		_showOrpholanMetaItemType = orpholanMetaItemType;
	}

	@Override
	public ItemType[] getFirstItemType(LogicalWorkspace model) {
		List<ItemType> first_its = new ArrayList<ItemType>();
		ArrayList<ItemType> itemType = new ArrayList<ItemType>(model.getItemTypes());
		for (ItemType it : itemType) {
			if (it.isRootElement()) {
				first_its.add(it);
			}
		}
		return first_its.toArray(new ItemType[first_its.size()]);

	}

	@Override
	public boolean isAggregationLink(Link arg0) {
		return arg0.isAggregation();
	}

	@Override
	public boolean isFirstItemType(ItemType it, LogicalWorkspace cadseModel) {
		return it.isRootElement();
	}

	@Override
	public boolean isItemType(ItemType it, LogicalWorkspace cadseModel) {
		return true;
	}

	@Override
	protected boolean isLink(Link link) {
		return true;
	}

	@Override
	public boolean isRefItemType(ItemType it, LogicalWorkspace cadseModel) {
		return false;
	}

	@Override
	public String getDislplayCreate(LinkType lt, ItemType destItemType) {
		return destItemType.getItemManager().getDisplayCreate(lt, destItemType);
	}

	@Override
	public boolean isCreateLink(LinkType link) {
		return link.isAggregation();
	}

	@Override
	protected AbstractCadseViewNode[] sort(Item itemParent, LinkNode[] nodes) {
		if (itemParent.getType() == null || itemParent.getType().getItemManager() == null) {
			return nodes;
		}

		if (itemParent.getType().getItemManager().isOutgoingLinkSorted()) {
			Arrays.sort(nodes, new Comparator<LinkNode>() {
				public int compare(LinkNode o1, LinkNode o2) {
					return getDisplayText(o1).compareTo(getDisplayText(o2));
				}
			});
		}
		return nodes;
	}

	@Override
	protected AbstractCadseViewNode[] getFirstChildren() {
		LogicalWorkspace model = getCadseModel();
		if (model == null) {
			return AbstractCadseViewNode.EMPTY;
		}
		ItemType[] itemtypes = getFirstItemType(model);
		List<ItemNode> ret = new ArrayList<ItemNode>();
		for (ItemType it : itemtypes) {
			if (it == null) {
				continue;
			}

			// it.addListener(this,
			// WorkspaceListener.filter(ChangeID.DELETE_ITEM,
			// ChangeID.DELETE_OUTGOING_LINK,
			// ChangeID.RESOLVE_INCOMING_LINK,
			// ChangeID.CREATE_OUTGOING_LINK));
			List<Item> itemByType;
			while (true) {
				try {
					itemByType = it.getItems();
					break;
				} catch (Throwable e) {
					try {
						Thread.sleep(20);
					} catch (InterruptedException e1) {
					}
				}
			}
			for (Item item : itemByType) {
				if (item.getType() != it) {
					continue;
				}
				ret.add(createItemNode(rootWS, item));
			}
		}
		if (_showOrpholanItem || _showOrpholanMetaItemType) {
			Collection<Item> items = new ArrayList<Item>(CadseCore.getLogicalWorkspace().getItems());
			ONE: for (Item anItem : items) {
				if (anItem.getType() != null && anItem.getType().isRootElement()) {
					continue;
				}

				boolean isMetaItemtype = anItem.isInstanceOf(CadseGCST.ITEM_TYPE);
				if (!_showOrpholanMetaItemType && isMetaItemtype) {
					continue;
				}
				if (!_showOrpholanItem && !isMetaItemtype) {
					continue;
				}

				List<? extends Link> incomings = anItem.getIncomingLinks();
				if (incomings != null) {
					for (Link l : incomings) {
						if (l.isAggregation()) {
							continue ONE;
						}
						if (l.getLinkType().isPart()) {
							continue ONE;
						}
					}
					// anItem.addListener(this,
					// WorkspaceListener.filter(ChangeID.DELETE_ITEM,
					// ChangeID.DELETE_OUTGOING_LINK,
					// ChangeID.RESOLVE_INCOMING_LINK,
					// ChangeID.CREATE_OUTGOING_LINK));
				}

				ret.add(createItemNode(rootWS, anItem));
			}
		}
		return ret.toArray(new ItemNode[ret.size()]);
	}

	@Override
	public boolean isFirstItem(Item item, LogicalWorkspace cadseModel) {
		ONE: while (true) {
			if (item.getType() != null && item.getType().isRootElement()) {
				break;
			}

			List<? extends Link> incomings = item.getIncomingLinks();
			if (incomings != null) {
				for (Link l : incomings) {
					if (l.isAggregation()) {
						break ONE;
					}
					if (l.getLinkType().isPart()) {
						break ONE;
					}
				}
			}
			return true;
		}
		return super.isFirstItem(item, cadseModel);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
	}

	public int isSelected(IItemNode node) {
		return IItemNode.DESELECTED;
	}
}

public class WSView extends AbstractCadseView {

	@Override
	protected AbstractCadseTreeViewUI createUIController(IViewSite site) {
		return new WSViewViewUI(site);
	}

}
