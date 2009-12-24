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

import org.eclipse.jface.viewers.TreeViewer;

import fr.imag.adele.cadse.core.IItemNode;

public interface CadseViewModelController {
	// Set<AbstractCadseViewNode> getParentNode(ItemType it);

	AbstractCadseViewNode[] getChildren(AbstractCadseViewNode node);

	boolean isRecomputeChildren();

	boolean hasChildren(AbstractCadseViewNode node);

	TreeViewer getFTreeViewer();

	String getDisplayToolTip(AbstractCadseViewNode linkType);

	boolean isAggregationLink(AbstractCadseViewNode node);

	void add(AbstractCadseViewNode node);

	void remove(AbstractCadseViewNode node);

	int isSelected(IItemNode node);
}
