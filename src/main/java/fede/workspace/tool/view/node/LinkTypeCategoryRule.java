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

import java.util.List;

import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.LinkType;
import fede.workspace.tool.view.node.FilteredItemNode.Category;

public class LinkTypeCategoryRule extends Rule {

	public LinkTypeCategoryRule() {
		super();
	}

	@Override
	public void computeChildren(FilteredItemNode root, AbstractCadseViewNode node, List<AbstractCadseViewNode> ret) {
		ItemType type = node.getItemType();
		List<LinkType> linkTypes = type.getOutgoingLinkTypes();
		if ((linkTypes == null) || linkTypes.isEmpty()) {
			return;
		}

		for (LinkType linkType : linkTypes) {
			if (linkType.isHidden()) {
				continue;
			}

			Category categ = new Category();
			categ.name = linkType.getName();

			LinkTypeCategoryNode categoryNode = new LinkTypeCategoryNode(root, node, categ, linkType);
			if (categoryNode.hasChildren()) {
				ret.add(categoryNode);
			}
		}
	}
}