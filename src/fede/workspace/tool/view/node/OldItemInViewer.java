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
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IActionFilter;
import org.eclipse.ui.IContributorResourceAdapter;

import fede.workspace.tool.view.ItemInViewer;
import fede.workspace.tool.view.WSPlugin;
import fr.imag.adele.cadse.core.DerivedLink;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.core.LogicalWorkspace;
import fr.imag.adele.cadse.core.impl.CadseCore;

@Deprecated
public class OldItemInViewer extends ItemInViewer implements IAdaptable, IActionFilter, IContributorResourceAdapter {

	final Object		value;
	OldItemInViewer[]	children	= null;

	// List d'items
	public OldItemInViewer(List<Item> items) {
		super(LIST, null);
		this.value = items;
	}

	// WS
	public OldItemInViewer() {
		super(WS, null);
		this.value = "WS";
	}

	// TO or FROM
	public OldItemInViewer(ItemInViewer parent, LinkType rt, int kind) {
		super(kind, parent);
		this.value = rt;
	}

	// LINK_TO or LINK_FROM
	public OldItemInViewer(ItemInViewer parent, Link l, int kind) {
		super(kind, parent);
		this.value = l;
	}

	// ROOT
	public OldItemInViewer(ItemInViewer newInput) {
		super(ROOT, null);
		this.value = newInput;
	}

	// ORPHAN_LINK
	// ORPHAN
	public OldItemInViewer(ItemInViewer parent, Item item) {
		super(ITEM, parent);
		this.value = item;
	}

	@Override
	public ItemType getItemType() {
		return null;
	}

	@Override
	public Item getItem() {
		if (CadseCore.getInstance() == null) {
			return null;
		}
		LogicalWorkspace w = CadseCore.getLogicalWorkspace();
		if (w == null) {
			return null;
		}
		switch (kind) {
			case LINK_TYPE_OUTGOING:
				return null;
			case INCOMING_LINK_NAME:
				return null;
			case LINK_INCOMING:
				return w.getItem(getLink().getSourceId());
			case LINK_OUTGOING:
				return w.getItem(getLink().getDestinationId());
			case ITEM:
				return w.getItem(((Item) value).getId());
			default:
				break;
		}
		return null;

	}

	// todo copier dans un labelprovider
	@Override
	protected String getCodeRT() {
		StringBuffer ret = new StringBuffer();

		if (getLinkType().isPart()) {
			ret.append("p");
		}
		if (getLinkType().isAggregation()) {
			ret.append("a");
		}
		if (getLinkType().isRequire()) {
			ret.append("r");
		}
		if (getLinkType().isComposition()) {
			ret.append("c");
		}
		return ret.toString();
	}

	// todo copier dans un labelprovider
	@Override
	public String toString() {
		String readonly = "";
		switch (kind) {
			case LINK_TYPE_OUTGOING:
				return "-" + getCodeRT() + "-> " + getLinkType().getName() + " - "
						+ getLinkType().getDestination().getId();
			case INCOMING_LINK_NAME:
				return "<-" + getCodeRT() + "- " + getLinkType().getName() + " - " + getLinkType().getSource().getId();

			case LINK_INCOMING:
				return toStringSource(getLink());
			case LINK_OUTGOING:
				Item citem = getLink().getResolvedDestination();
				if (citem != null && citem.isReadOnly()) {
					readonly = "(r) ";
				}
				return readonly + (toString(getLink()));
			case ITEM:
				return ((Item) value).getName();
			default:
				break;
		}
		return "unknown";
	}

	@Override
	protected String toString(Link l) {
		if (l.getResolvedDestination() == null) {
			return l.getDestinationId().toString();
		}
		return WSPlugin.getManager(l.getResolvedDestination()).getDisplayName(l.getResolvedDestination());
	}

	@Override
	protected String toStringSource(Link l) {
		return WSPlugin.getManager(l.getSource()).getDisplayName(l.getSource());
	}

	@Override
	public int getKind() {
		return kind;
	}

	@Override
	public Object getAdapter(Class adapter) {

		Item item = getItem();
		if (adapter == IContributorResourceAdapter.class) {
			return this;
		}
		if (adapter == Item.class) {
			return item;
		}
		if (item != null) {
			return ((IAdaptable) item).getAdapter(adapter);
		}
		try {
			return Platform.getAdapterManager().getAdapter(this, adapter);
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}

	@Override
	public ItemInViewer getParent() {
		return parent;
	}

	@Override
	public void close() {
		open = false;
		children = null;
	}

	@Override
	public Link getLink() {
		if (value instanceof Link) {
			return (Link) value;
		}
		return null;
	}

	@Override
	public LinkType getLinkType() {
		switch (kind) {
			case LINK_TYPE_OUTGOING:
			case INCOMING_LINK_NAME:
				return (LinkType) value;
		}
		return null;
	}

	@Override
	public OldItemInViewer[] getChildren(int flag) {
		if (children != null) {
			return children;
		}
		List<OldItemInViewer> ret = new ArrayList<OldItemInViewer>();
		Link link;
		switch (kind) {
			case ROOT:
				ret.add((OldItemInViewer) value);
				break;

			case LIST:
				List<Item> items = (List<Item>) value;
				for (Item item : items) {
					ret.add(new OldItemInViewer(this, item));
				}
				break;

			case ITEM:
				addOutgoingLinks(ret, (Item) value, null);
				break;
			case LINK_INCOMING:
			case LINK_OUTGOING:
				link = (Link) value;
				if (link.getResolvedDestination() == null) {
					break; // link unresolved
				}
				if ((flag & SHOW_RELATION_OUTGOING) != 0) {
					addLinkType(link.getResolvedDestination(), ret);
				} else {
					addOutgoingLinks(ret, link.getResolvedDestination(), null);
				}
				break;

			case INCOMING_LINK_NAME:
				if ((flag & SHOW_RELATION_OUTGOING) != 0) {
					addIncomingLinks(ret, getParent().getItem(), getLinkType());
				}
				break;

			case LINK_TYPE_OUTGOING:
				if ((flag & SHOW_RELATION_OUTGOING) != 0) {
					addOutgoingLinks(ret, getParent().getItem(), getLinkType());
				}
				break;

			default:
				break;
		}
		children = ret.toArray(new OldItemInViewer[ret.size()]);
		return children;
	}

	public OldItemInViewer notifieDeleteItem(Item item, int flag) {
		if (!isOpen() && (children == null)) {
			return null;
		}
		OldItemInViewer iiv = null;
		Link link;
		switch (kind) {
			case ROOT:
				break;

			// case NOT_ATTACHED_OR_ONLY_IN_WORKSPACE_LINK_NAME:
			// if (item.isOnlyInWorkspace()) {
			// iiv = new
			// ItemInViewer(this,item,NOT_ATTACHED_OR_ONLY_IN_WORKSPACE);
			// }
			// break;
			case WS:
				// if ((flag & SHOW_RELATION_OUTGOING)!=0) {
				// } else {
				// // if (item.isOnlyInWorkspace()) {
				// // iiv = new
				// ItemInViewer(this,item,NOT_ATTACHED_OR_ONLY_IN_WORKSPACE);
				// // }
				// }
				break;
			case LINK_INCOMING:
				break;
			case LINK_OUTGOING:
				link = (Link) value;
				if (link.getDestinationId().equals(item.getId())) {
					// lien devient unresolve
					children = null;
					return this;
				}

				break;
			// case NOT_ATTACHED_OR_ONLY_IN_WORKSPACE:
			// break;

			case INCOMING_LINK_NAME:
				break;

			case LINK_TYPE_OUTGOING:
				break;

			default:
				break;
		}

		return removeElement(iiv) ? this : null;
	}

	private boolean addElement(OldItemInViewer iiv) {
		if (iiv == null) {
			return false;
		}
		if (children == null) {
			children = new OldItemInViewer[0]; // it's can be null;
		}
		for (OldItemInViewer aIIV : children) {
			if (aIIV.equals(iiv)) {
				return false;
			}
		}
		OldItemInViewer[] children2 = new OldItemInViewer[children.length + 1];
		System.arraycopy(children, 0, children2, 0, children.length);
		children2[children.length] = iiv;
		children = children2;
		return true;
	}

	private boolean removeElement(OldItemInViewer iiv) {
		if (iiv == null) {
			return false;
		}
		if (children.length == 0) {
			children = null;
			return false;
		}
		if (children.length == 1) {
			if (children[0].equals(iiv)) {
				children = null;
				return true;
			}
			return false;
		}

		ArrayList<OldItemInViewer> aa = new ArrayList<OldItemInViewer>();
		aa.addAll(Arrays.asList(children));
		if (!aa.remove(iiv)) {
			return false;
		}
		children = aa.toArray(new OldItemInViewer[aa.size()]);
		return true;
	}

	public OldItemInViewer notifieCreateLink(Link theAddedlink, int flag) {

		OldItemInViewer iiv = null;
		Link link;
		Item item;
		switch (kind) {
			case ROOT:
				break;

			// case NOT_ATTACHED_OR_ONLY_IN_WORKSPACE_LINK_NAME:
			// //addOrphanItem(ret, (Workspace) value);
			// break;

			case LINK_INCOMING:
			case LINK_OUTGOING:
				link = (Link) value;
				if (link.getResolvedDestination() == null) {
					break; // link unresolved
				}
				if ((flag & SHOW_RELATION_OUTGOING) != 0) {
				} else {
					if (theAddedlink.getSource().equals(link.getResolvedDestination())) {
						iiv = new OldItemInViewer(this, theAddedlink, OldItemInViewer.LINK_OUTGOING);
					}
				}
				break;
			// case NOT_ATTACHED_OR_ONLY_IN_WORKSPACE:
			// item = (Item) value;
			// if (theAddedlink.isAggregation() && theAddedlink.getDestination()
			// == item) {
			// parent.removeElement(this);
			// return parent;
			// }
			// if ((flag & SHOW_RELATION_OUTGOING)!=0) {
			// } else {
			// // un new link is created.
			// // if the source of this link is this item which is only in
			// workspace, add a new element.
			// if (theAddedlink.getSource().equals(item)) {
			// iiv = new ItemInViewer(this, theAddedlink,
			// ItemInViewer.LINK_OUTGOING);
			// }
			// }
			// break;

			case INCOMING_LINK_NAME:
				if ((flag & SHOW_RELATION_OUTGOING) != 0) {
					item = getParent().getItem();
					if (theAddedlink.getResolvedDestination() != null
							&& theAddedlink.getResolvedDestination().equals(item)
							&& theAddedlink.getLinkType().equals(getLinkType())) {
						iiv = new OldItemInViewer(this, theAddedlink, OldItemInViewer.LINK_INCOMING);
					}
				}
				break;

			case LINK_TYPE_OUTGOING:
				if ((flag & SHOW_RELATION_OUTGOING) != 0) {
					item = getParent().getItem();
					if (theAddedlink.getSource().equals(item) && theAddedlink.getLinkType().equals(getLinkType())) {
						iiv = new OldItemInViewer(this, theAddedlink, OldItemInViewer.LINK_OUTGOING);
					}
				}
				break;

			default:
				break;
		}
		if (!isOpen() && (children == null)) {
			return null;
		}
		return addElement(iiv) ? this : null;
	}

	public OldItemInViewer notifieDeleteLink(Link theDeletedLink, int flag) {
		if ((children == null)) {
			return null;
		}
		OldItemInViewer iiv = null;
		Link link;
		Item item;
		switch (kind) {
			case ROOT:
				break;

			case LINK_INCOMING:
			case LINK_OUTGOING:
				link = (Link) value;
				if (link.getResolvedDestination() == null) {
					break; // link unresolved
				}
				if ((flag & SHOW_RELATION_OUTGOING) != 0) {
				} else {
					if (theDeletedLink.getSource().equals(link.getResolvedDestination())) {
						iiv = new OldItemInViewer(this, theDeletedLink, OldItemInViewer.LINK_OUTGOING);
					}
				}
				break;
			// case NOT_ATTACHED_OR_ONLY_IN_WORKSPACE:
			// item = (Item) value;
			// if ((flag & SHOW_RELATION_OUTGOING)!=0) {
			// } else {
			// if (theDeletedLink.getSource().equals(item)) {
			// iiv = new ItemInViewer(this, theDeletedLink,
			// ItemInViewer.LINK_OUTGOING);
			// }
			// }
			// break;

			case INCOMING_LINK_NAME:
				if ((flag & SHOW_RELATION_OUTGOING) != 0) {
					item = getParent().getItem();
					if (theDeletedLink.getResolvedDestination() != null
							&& theDeletedLink.getResolvedDestination().equals(item)
							&& theDeletedLink.getLinkType().equals(getLinkType())) {
						iiv = new OldItemInViewer(this, theDeletedLink, OldItemInViewer.LINK_INCOMING);
					}
				}
				break;

			case LINK_TYPE_OUTGOING:
				if ((flag & SHOW_RELATION_OUTGOING) != 0) {
					item = getParent().getItem();
					if (theDeletedLink.getSource().equals(item) && theDeletedLink.getLinkType().equals(getLinkType())) {
						iiv = new OldItemInViewer(this, theDeletedLink, OldItemInViewer.LINK_OUTGOING);
					}
				}
				break;

			default:
				break;
		}
		if (iiv == null) {
			return null;
		}

		return removeElement(iiv) ? this : null;
	}

	/*
	 * sans relation name (outgoing) WS ORPHAN LINK_TO LINK_TO LINK_TO LINK_TO
	 * LINK_TO
	 * 
	 * cas 2 (avec relation name) WS ORPHAN_LINK ORPHAN LINKTYPE_FROM LINK_FROM
	 * LINKTYPE_FROM LINK_FROM LINKTYPE_TO LINK_TO LINKTYPE_TO LINK_TO
	 * LINKTYPE_FROM LINK_FROM LINKTYPE_TO LINK_TO LINKTYPE_FROM LINK_FROM
	 * LINKTYPE_FROM LINK_FROM LINKTYPE_TO LINK_TO LINKTYPE_TO LINK_TO
	 * LINKTYPE_FROM LINK_FROM LINKTYPE_TO LINK_TO
	 * 
	 */

	@Override
	public void open() {
		if (isOpen()) {
			return;
		}
		open = true;
	}

	private void addOutgoingLinks(List<OldItemInViewer> ret, Item item, LinkType lt) {
		if (item == null) {
			return;
		}

		List<? extends Link> links = null;
		if (lt == null) {
			links = item.getOutgoingLinks();
		} else {
			links = item.getOutgoingLinks(lt);
		}
		for (Link l : links) {
			ret.add(new OldItemInViewer(this, l, OldItemInViewer.LINK_OUTGOING));
		}
	}

	private void addIncomingLinks(List<OldItemInViewer> ret, Item item, LinkType lt) {
		List<? extends Link> links = null;
		if (lt == null) {
			links = item.getIncomingLinks();
		} else {
			links = item.getIncomingLinks(lt);
		}
		for (Link l : links) {
			ret.add(new OldItemInViewer(this, l, OldItemInViewer.LINK_INCOMING));
		}
	}

	// private void addOrphanItem(List<ItemInViewer> ret, Workspace workspace) {
	// for (Item item : workspace.getOrphanItems()) {
	// ret.add(new ItemInViewer(this,item,NOT_ATTACHED_OR_ONLY_IN_WORKSPACE));
	// }
	// }

	private void addLinkType(Item item, List<OldItemInViewer> ret) {
		for (LinkType rt : item.getType().getOutgoingLinkTypes()) {
			ret.add(new OldItemInViewer(this, rt, OldItemInViewer.LINK_TYPE_OUTGOING));
		}
		for (LinkType rt : item.getType().getIncomingLinkTypes()) {
			ret.add(new OldItemInViewer(this, rt, OldItemInViewer.INCOMING_LINK_NAME));
		}
	}

	@Override
	public boolean testAttribute(Object target, String name, String value) {
		return WSPlugin.testAttribute(target, name, value);
	}

	@Override
	public IResource getAdaptedResource(IAdaptable adaptable) {
		return null;
	}

	@Override
	public boolean isOpen() {
		return open;
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof OldItemInViewer) {
			OldItemInViewer iiv = (OldItemInViewer) arg0;
			return (iiv.kind == this.kind && iiv.value.equals(value));
		}
		return super.equals(arg0);
	}

	@Override
	public String getToolTip() {
		StringBuilder sb = new StringBuilder();
		if (getItem() != null) {
			sb.append("type: ").append(getItem().getType().getId()).append("\n");
		}
		Link link;
		// Item item;
		switch (kind) {
			case ROOT:
				break;

			// case NOT_ATTACHED_OR_ONLY_IN_WORKSPACE_LINK_NAME:
			// sb.append("orphan relation");
			// break;

			case LINK_INCOMING:
			case LINK_OUTGOING:
				link = (Link) value;
				sb.append(link.toString());
				if (link.isReadOnly()) {
					sb.append("\nlink readonly");
				}
				if (link.isLinkResolved()) {
					Item theItem = link.getResolvedDestination();
					sb.append("\nDest id         : ").append(theItem.getId());
					sb.append("\nDest short name : ").append(theItem.getName());

					if (theItem.isReadOnly()) {
						sb.append("\nitem readonly");
					}
					List<Item> parentComposite = theItem.getCompositeParent();
					if (parentComposite != null && parentComposite.size() > 0) {
						sb.append("\ncomposite parent:\n");
						for (Item pitem : parentComposite) {
							sb.append("   - ");
							sb.append(pitem.getName());

						}
					}

					Set<Item> comp = theItem.getComponents();
					if (comp.size() > 0) {
						sb.append("\ncomponants:");
						Item[] linkArray = comp.toArray(new Item[0]);
						Arrays.sort(linkArray, new Comparator<Item>() {

							public int compare(Item o1, Item o2) {
								return o1.getName().compareTo(o2.getName());
							}
						});
						for (Item link2 : linkArray) {
							sb.append("\n   - ").append(link2.getName());
						}
					}
					Set<DerivedLink> derivedLink = theItem.getDerivedLinks();
					if (derivedLink.size() > 0) {
						sb.append("\nderived links:");
						for (DerivedLink link2 : derivedLink) {
							sb.append("\n   - ").append(link2.getDestination().getName()).append(" (").append(
									link2.getLinkType().getName()).append(")");
						}
					}
				}
				break;
			// case NOT_ATTACHED_OR_ONLY_IN_WORKSPACE:
			// item = (Item) value;
			// sb.append(item.getType().getId());
			//
			// break;

			case LINK_TYPE_OUTGOING:
			case INCOMING_LINK_NAME:
				LinkType linkType = getLinkType();
				sb.append(linkType.toString());
				break;
		}
		return sb.toString();
	}

	@Override
	public boolean hasChildren() {
		return children != null;
	}

	public Object getElementModel() {
		return null;
	}

	public IItemNode[] getChildren() {
		return getChildren(0);
	}

	public int isSelected() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setSelected(int state) {
		// TODO Auto-generated method stub

	}

	public void delete() {
		// TODO Auto-generated method stub

	}

}