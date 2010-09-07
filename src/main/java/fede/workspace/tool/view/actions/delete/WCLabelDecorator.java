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
package fede.workspace.tool.view.actions.delete;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.transaction.delta.ItemDelta;
import fr.imag.adele.cadse.core.transaction.delta.LinkDelta;
import fr.imag.adele.cadse.core.transaction.delta.SetAttributeOperation;
import fr.imag.adele.cadse.core.CadseGCST;
import fede.workspace.tool.view.WSPlugin;
import fr.imag.adele.cadse.eclipse.view.SelfViewLabelProvider;

public class WCLabelDecorator extends SelfViewLabelProvider implements ILabelDecorator {
	private LocalResourceManager	resourceManager	= new LocalResourceManager(JFaceResources.getResources(PlatformUI
															.getWorkbench().getDisplay()));

	public Image decorateImage(Image image, Object element) {
		if (element instanceof IItemNode) {
			element = ((IItemNode) element).getElementModel();
		}
		if (element instanceof ItemDelta) {
			if (((ItemDelta) element).isDeleted()) {
				return computeImage(image, "icons/deleted.gif");
			}

			if (((ItemDelta) element).isAdded()) {
				return computeImage(image, "icons/add_ovr.gif");
			}

			if (((ItemDelta) element).isModified()) {
				return computeImage(image, "icons/dirty_ov.gif");
			}
		}
		if (element instanceof LinkDelta) {
			if (((LinkDelta) element).isDeleted()) {
				return computeImage(image, "icons/deleted.gif");
			}
			if (((LinkDelta) element).isAdded()) {
				return computeImage(image, "icons/add_ovr.gif");
			}
			if (((LinkDelta) element).isModified()) {
				return computeImage(image, "icons/dirty_ov.gif");
			}
		}

		if (element instanceof SetAttributeOperation) {
			if (image == null) {
				image = WSPlugin.getDefault().getImageFrom(CadseGCST.ATTRIBUTE, CadseGCST.ATTRIBUTE);
			}
			if (((SetAttributeOperation) element).isRemoved()) {
				return computeImage(image, "icons/deleted.gif");
			}
			if (((SetAttributeOperation) element).isAdded()) {
				return computeImage(image, "icons/add_ovr.gif");
			}
			if (((SetAttributeOperation) element).isModified()) {
				return computeImage(image, "icons/dirty_ov.gif");
			}
		}
		return null;
	}

	private Image computeImage(Image image, String path) {
		if (image == null) {
			return WSPlugin.getImage(path);
		}
		ImageDescriptor overlay = WSPlugin.getImageDescriptor(path);
		DecorationOverlayIcon icon = new DecorationOverlayIcon(image, overlay, IDecoration.BOTTOM_RIGHT);
		return resourceManager.createImage(icon);
	}

	public String decorateText(String text, Object element) {
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof IItemNode) {
			element = ((IItemNode) element).getElementModel();
		}
		if (element instanceof ItemDelta) {
			final ItemDelta operElt = (ItemDelta) element;
			StringBuilder sb = new StringBuilder();
			toStringShort(operElt, sb);
			return sb.toString();
		}
		if (element instanceof LinkDelta) {
			final LinkDelta operELt = (LinkDelta) element;
			StringBuilder sb = new StringBuilder();
			toStringShort(operELt, sb);
			return sb.toString();
		}

		if (element instanceof SetAttributeOperation) {
			final SetAttributeOperation operELt = (SetAttributeOperation) element;
			StringBuilder sb = new StringBuilder();
			toStringShort(operELt, sb);
			return sb.toString();
		}
		return super.getText(element);
	}

	public void toStringShort(SetAttributeOperation operElt, StringBuilder sb) {
		if (operElt.isRemoved()) {
			sb.append("Delete ");
		}
		if (operElt.isAdded()) {
			sb.append("Added ");
		}
		sb.append(operElt.toString());
	}

	public void toStringShort(LinkDelta operElt, StringBuilder sb) {
		if (operElt.isDeleted()) {
			sb.append("Delete ");
		}
		if (operElt.isAdded()) {
			sb.append("Added ");
		}
		sb.append("Link ");
		sb.append(operElt.getLinkType().getDisplayName());
		sb.append(" to ");
		sb.append(getName(operElt.getDestination()));
	}

	public void toStringShort(ItemDelta operElt, StringBuilder sb) {
		if (operElt.isDeleted()) {
			sb.append("Delete ");
		}
		if (operElt.isAdded()) {
			sb.append("Added ");
		}
		if (operElt.isLoaded()) {
			sb.append("Loaded ");
		}

		sb.append("Item ");
		String name = getName(operElt);
		sb.append(" ").append(name);
	}

	private String getName(ItemDelta operElt) {
		String name = operElt.getDisplayName();
		if (name == null || "".equals(name)) {
			if (operElt.getType() != null) {
				name = operElt.getType().getItemManager().getDisplayName(operElt);
			}
		}
		if (name == null || "".equals(name)) {
			name = operElt.getName();
		}
		if (name == null || "".equals(name)) {
			name = operElt.getId().toString();
		}
		return name;
	}
}
