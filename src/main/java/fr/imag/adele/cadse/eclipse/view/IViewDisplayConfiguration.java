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
package fr.imag.adele.cadse.eclipse.view;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.LinkType;
import fede.workspace.tool.view.node.AbstractCadseViewNode;
import fede.workspace.tool.view.node.ItemTypeNode;
import fede.workspace.tool.view.node.LinkNode;
import fede.workspace.tool.view.node.LinkTypeNode;

public interface IViewDisplayConfiguration {

	public abstract Image getDisplayImage(LinkNode node);

	public abstract Image getDisplayImage(IItemNode node);

	public abstract String getDisplayText(IItemNode node);

	public abstract String getDisplayText(LinkNode node);

	public abstract String getDisplayText(Link link, Item destination);

	public abstract String getDisplayToolTip(Item theItem);

	public abstract String getDisplayToolTip(Link link);

	public abstract String getDisplayToolTip(LinkType linkType);

	public abstract Image getDisplayImage(LinkTypeNode node);

	public abstract String getDisplayText(LinkTypeNode node);

	public abstract Font getDisplayFont(LinkTypeNode node);

	public abstract String getDisplayToolTip(ItemType itemType);

	public abstract Image getDisplayImage(ItemTypeNode node);

	public abstract String getDisplayText(ItemTypeNode node);

	public abstract Font getDisplayFont(ItemTypeNode node);
	
	public abstract Font getDisplayFont(IItemNode node);
	
	public abstract Font getDisplayFont(LinkNode node);

	public abstract String getDisplayToolTip(AbstractCadseViewNode node);

}