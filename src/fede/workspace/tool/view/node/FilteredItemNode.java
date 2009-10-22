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
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.LinkType;
import fede.workspace.model.manager.properties.impl.ic.IC_NodeIsSelected;

/**
 * ROOT_ENTRY -> MULTI_ENTRY, LT_XXX
 * 
 * Regle: MULTI_ENTRY
 * 
 * @author chomats
 * 
 */
public class FilteredItemNode extends AbstractCadseViewNode implements CadseViewModelController {

	public final static class Category {
		public String	name;
		String			iconurl;

	}

	private TreeViewer		_treeviewer;
	FilteredItemNodeModel	_model;
	IC_NodeIsSelected		_icNodeIsSelected	= null;

	/** treeviever can be null */
	public FilteredItemNode(TreeViewer treeviewer) {
		this(treeviewer, null);
	}

	private FilteredItemNodeModel createFilteredItemNodeModel() {
		return new FilteredItemNodeModel();
	}

	public FilteredItemNode(TreeViewer treeviewer, FilteredItemNodeModel model) {
		super(ROOT, null);
		this._treeviewer = treeviewer;
		this.ctl = this;
		if (model == null) {
			model = createFilteredItemNodeModel();
		}
		this._model = model;
	}

	public AbstractCadseViewNode[] getChildren(AbstractCadseViewNode node) {
		return _model.getChildren(this, node);
	}

	@Override
	public Object getElementModel() {
		return FilteredItemNodeModel.ROOT_ENTRY;
	}

	@Override
	public Item getItem() {
		return null;
	}

	@Override
	public Link getLink() {
		return null;
	}

	@Override
	public LinkType getLinkType() {
		return null;
	}

	@Override
	public String getToolTip() {
		return null;
	}

	@Override
	public String toString() {
		return "<ROOT_ENTRY>";
	}

	public void add(AbstractCadseViewNode node) {
	}

	public String getDisplayToolTip(AbstractCadseViewNode linkType) {

		return null;
	}

	/** treeviever can be null */

	public TreeViewer getFTreeViewer() {
		return _treeviewer;
	}

	public void setTreeViewer(TreeViewer treeviewer) {
		this._treeviewer = treeviewer;
	}

	public boolean hasChildren(AbstractCadseViewNode node) {
		return node.getChildren().length != 0;
	}

	public boolean isAggregationLink(AbstractCadseViewNode node) {
		return true;
	}

	public boolean isRecomputeChildren() {
		return false;
	}

	public void remove(AbstractCadseViewNode node) {

	}

	public void addRule(Object key, fede.workspace.tool.view.node.Rule rule) {
		_model.addRule(key, rule);
	}

	public FilteredItemNodeModel getModel() {
		return _model;
	}

	public int isSelected(IItemNode node) {
		return _icNodeIsSelected == null ? IItemNode.DESELECTED : _icNodeIsSelected.isSelected(node);
	}

	public void setIcNodeIsSelected(IC_NodeIsSelected nodeIsSelected) {
		_icNodeIsSelected = nodeIsSelected;
	}
}
