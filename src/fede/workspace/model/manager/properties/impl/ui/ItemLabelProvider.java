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

package fede.workspace.model.manager.properties.impl.ui;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fede.workspace.tool.view.WSPlugin;


/**
 * The Class ItemLabelProvider.
 * @author <a href="mailto:stephane.chomat@imag.fr">Stephane Chomat</a>
 */
final public class ItemLabelProvider extends LabelProvider {
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object element) {
		if (element instanceof Item) {
			Item item = (Item) element;
			return createImage(item.getType(), item);
		}
		return super.getImage(element);
	}
	
	/**
	 * Creates the image.
	 * 
	 * @param it
	 *            the it
	 * @param item
	 *            the item
	 * 
	 * @return the image
	 */
	private Image createImage(ItemType it, Item item) {
		return WSPlugin.getDefault().getImageFrom(it, item);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		return ((Item)element).getDisplayName();
	}
	
	
}