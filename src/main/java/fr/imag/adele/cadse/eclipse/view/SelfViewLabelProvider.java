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
package fr.imag.adele.cadse.eclipse.view;

import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.LinkType;
import fede.workspace.tool.view.WSPlugin;
import fede.workspace.tool.view.node.ItemDescriptionRefNode;
import fede.workspace.tool.view.node.ItemNode;
import fede.workspace.tool.view.node.ItemTypeNode;
import fede.workspace.tool.view.node.LinkNode;
import fede.workspace.tool.view.node.LinkTypeNode;

public class SelfViewLabelProvider extends LabelProvider implements IFontProvider {
	private Font	fFontRoot;
	private Font	fFontUnresolved;
	private boolean	showLinkTypeName;
	private boolean	showKind;

	/**
	 * @param view
	 */
	public SelfViewLabelProvider() {
	}

	protected Font getDisplayFont(IItemNode node) {
		return fFontRoot;
	}

	public Font getDisplayFont(ItemTypeNode node) {
		return null;
	}

	protected Font getDisplayFont(LinkNode node) {
		if (node.getLink() != null && !node.getLink().isLinkResolved()) {
			return this.fFontUnresolved;
		}

		return null;
	}

	public Font getDisplayFont(LinkTypeNode node) {
		return null;
	}

	public Image getDisplayImage(IItemNode node) {
		Item destination = node.getItem();
		ItemType it = destination.getType();
		Image ret = WSPlugin.getDefault().getImageFrom(it, destination);
		if (ret != null) {
			return ret;
		}

		String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
	}

	public Image getDisplayImage(ItemDescriptionRefNode node) {
		ItemType it = node.getItemType();
		if (it != null) {
			return WSPlugin.getDefault().getImageFrom(it, null);
		}
		return null;
	}

	public Image getDisplayImage(ItemTypeNode node) {
		Item destination = node.getItem();
		ItemType it = node.getItemType();
		Image ret = WSPlugin.getDefault().getImageFrom(it, destination);
		if (ret != null) {
			return ret;
		}

		String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
	}

	public Image getDisplayImage(LinkNode node) {
		String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
	}

	public Image getDisplayImage(LinkTypeNode node) {
		ItemType it = node.getLinkType().getSource();
		Image ret = WSPlugin.getDefault().getImageFrom(it, null);
		if (ret != null) {
			return ret;
		}

		String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
	}

	public String getDisplayText(ItemDescriptionRefNode node) {
		return node.getRef().getName();
	}

	public String getDisplayText(IItemNode node) {
		return node.getItem().getDisplayName();
	}

	public String getDisplayText(ItemTypeNode node) {
		return node.toString();
	}

	public String getDisplayText(Link link, Item destination) {
		return destination.getDisplayName();
	}

	public String getDisplayText(LinkNode node) {

		Link link = node.getLink();
		StringBuilder begin = new StringBuilder();
		LinkType lt = link.getLinkType();
		link.toString();
		if (getShowKind()) {
			begin.append(toStringKind(lt));
		}
		if (getShowLinkTypeName()) {
			begin.append(lt.getName()).append(" ");
		}
		if (begin.length() > 0) {
			begin.append("--> ").append(node.getItem().getDisplayName());
			return begin.toString();
		}

		return node.getItem().getDisplayName();
	}

	public String getDisplayText(LinkTypeNode node) {
		return node.toString();
	}

	public Font getFont(Object element) {
		if (element instanceof ItemNode) {
			return getDisplayFont((IItemNode) element);
		}
		if (element instanceof LinkNode) {
			return getDisplayFont((LinkNode) element);
		}
		if (element instanceof LinkTypeNode) {
			return getDisplayFont((LinkTypeNode) element);
		}
		if (element instanceof ItemTypeNode) {
			return getDisplayFont((ItemTypeNode) element);
		}
		return null;
	}

	public Font getFontRoot() {
		return fFontRoot;
	}

	public Font getFontUnresolved() {
		return fFontUnresolved;
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof ItemNode) {
			return getDisplayImage((IItemNode) element);
		}
		if (element instanceof LinkNode) {
			return getDisplayImage((LinkNode) element);
		}
		if (element instanceof LinkTypeNode) {
			return getDisplayImage((LinkTypeNode) element);
		}
		if (element instanceof ItemTypeNode) {
			return getDisplayImage((ItemTypeNode) element);
		}
		if (element instanceof ItemDescriptionRefNode) {
			return getDisplayImage((ItemDescriptionRefNode) element);
		}
		return null;
	}

	public boolean getShowKind() {
		return showKind;
	}

	public boolean getShowLinkTypeName() {
		return showLinkTypeName;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof ItemNode) {
			return getDisplayText((IItemNode) element);
		}
		if (element instanceof LinkNode) {
			return getDisplayText((LinkNode) element);
		}
		if (element instanceof LinkTypeNode) {
			return getDisplayText((LinkTypeNode) element);
		}
		if (element instanceof ItemTypeNode) {
			return getDisplayText((ItemTypeNode) element);
		}
		if (element instanceof ItemDescriptionRefNode) {
			return getDisplayText((ItemDescriptionRefNode) element);
		}
		return element.toString();
	}

	public void setFontRoot(Font fontRoot) {
		this.fFontRoot = fontRoot;
	}

	public void setFontUnresolved(Font fontUnresolved) {
		this.fFontUnresolved = fontUnresolved;
	}

	public void setShowKind(boolean showKind) {
		this.showKind = showKind;
	}

	public void setShowLinkTypeName(boolean showLinkTypeName) {
		this.showLinkTypeName = showLinkTypeName;
	}

	protected String toStringKind(LinkType lt) {
		StringBuilder ret = new StringBuilder();
		ret.append("[");
		if (lt.isPart()) {
			ret.append("p");
		} else {
			ret.append(" ");
		}
		if (lt.isAggregation()) {
			ret.append("a");
		} else {
			ret.append(" ");
		}
		if (lt.isRequire()) {
			ret.append("r");
		} else {
			ret.append(" ");
		}
		if (lt.isComposition()) {
			ret.append("c");
		} else {
			ret.append(" ");
		}
		ret.append("] ");
		return ret.toString();

	}

}