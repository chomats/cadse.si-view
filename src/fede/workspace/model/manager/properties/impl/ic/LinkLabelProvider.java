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

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import fede.workspace.tool.view.WSPlugin;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.Link;

final public class LinkLabelProvider extends LabelProvider {

	public static final LabelProvider	INSTANCE	= new LinkLabelProvider();

	@Override
	public String getText(Object element) {
		if (element instanceof Item) {
			Item aItem = (Item) element;
			return aItem.getDisplayName();
		}
		if (element instanceof Link) {
			Link l = (Link) element;
			if (l.isLinkResolved()) {
				Item aItem = l.getDestination();
				return aItem.getDisplayName();
			}
			return l.getDestination().getName();
		}
		return super.getText(element);
	}

	@Override
	public Image getImage(Object obj) {
		if (obj instanceof Item) {
			Item item = (Item) obj;
			return createImage(item.getType(), item);
		}
		if (obj instanceof Link) {
			Link link = (Link) obj;
			return createImage(link.getDestinationType(), link.getDestination());
		}
		String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
	}

	private Image createImage(ItemType it, Item item) {
		return WSPlugin.getDefault().getImageFrom(it, item);
	}
}