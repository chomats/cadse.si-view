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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.core.util.ArraysUtil;
import fede.workspace.model.manager.properties.impl.ic.IC_AbstractTreeDialogForList_Browser_Combo;
import fede.workspace.tool.view.node.FilteredItemNode.Category;

/**
 * To display a filtered item model in tree Dialog You must provide three things : -
 * label provider : use LinkLabelProvider - tree content provider : use
 * SelfViewContentProvider - an input : set a FilteredItemNode(null, <a
 * filteredItemNodeModel>) to diplay a dialog see
 * {@link IC_AbstractTreeDialogForList_Browser_Combo#selectOrCreateValue(org.eclipse.swt.widgets.Shell)}
 * 
 * @author chomats
 * 
 */
public class FilteredItemNodeModel {
	private HashMap<Object, Rule[]>	model		= new HashMap<Object, Rule[]>();

	boolean							heritageRule;
	public final static Object		ROOT_ENTRY	= new Object();
	public final static Object		ANY_ENTRY	= new Object();

	public void addItemTypeEntry(Object beforeNode, ItemType it) {
		if (beforeNode == null) {
			beforeNode = FilteredItemNodeModel.ROOT_ENTRY;
		} else {
			checkNode(beforeNode);
		}
		addRule(beforeNode, new ItemTypeRule(it));
	}

	public Rule[] getRules(Object key) {
		return model.get(key);
	}

	public void addItemFromItemTypeEntry(Object beforeNode, ItemType it, Comparator<Item> fctSort) {
		if (beforeNode == null) {
			beforeNode = FilteredItemNodeModel.ROOT_ENTRY;
		} else {
			checkNode(beforeNode);
		}
		addRule(beforeNode, new ItemsFromItemTypeRule(it, fctSort));
	}

	public void addItemFromLinkTypeEntry(Object beforeNode, LinkType lt, Comparator<Item> fctSort, boolean resolved,
			boolean inverseLink) {
		if (beforeNode == null) {
			beforeNode = FilteredItemNodeModel.ROOT_ENTRY;
		} else {
			checkNode(beforeNode);
		}
		addRule(beforeNode, new ItemsFromLinkOfLinkTypeRule(lt, fctSort, resolved, inverseLink, (FilterItem) null));
	}

	public void addRule(Object key, Rule r) {
		model.put(key, ArraysUtil.add(Rule.class, model.get(key), r));
	}

	private void checkNode(Object beforeNode) {

	}

	public AbstractCadseViewNode[] getChildren(FilteredItemNode root, AbstractCadseViewNode node) {

		Rule[] struct = findKey(root, node);
		if (struct == null) {
			return FilteredItemNode.EMPTY;
		}
		List<AbstractCadseViewNode> ret = new ArrayList<AbstractCadseViewNode>();
		for (Rule o : struct) {
			o.computeChildren(root, node, ret);
		}
		return ret.toArray(new AbstractCadseViewNode[ret.size()]);
	}

	/**
	 * find the rules from the node. if rules exist for element model of this
	 * node, return this rules if the element model is link, return the rules
	 * for the link type if exist if not return the rules for the destination if
	 * the linknode show outgoing link other the source of the link
	 * 
	 * if the element model is an item
	 * 
	 * @param root
	 * @param node
	 * @return
	 */
	protected Rule[] findKey(FilteredItemNode root, AbstractCadseViewNode node) {
		if (node == root) {
			return model.get(FilteredItemNodeModel.ROOT_ENTRY);
		}
		Object key = node.getElementModel();
		if (model.containsKey(key)) {
			return model.get(key);
		}

		if (key instanceof Link) {
			key = ((Link) key).getLinkType();
			if (model.containsKey(key)) {
				return model.get(key);
				// if (node instanceof LinkNode) {
				// if (((LinkNode)node).getKind() == LinkNode.LINK_INCOMING) {
				// key = ((Link)key).getSource();
				// } else {
				// key = ((Link)key).getDestination();
				// }
				// if (model.containsKey(key))
				// return model.get(key);
				// }
			}

		}
		if (key instanceof LinkType) {
			key = ((LinkType) key).getLinkType();
			if (model.containsKey(key)) {
				return model.get(key);
			}
			key = ((LinkType) key).getType();
			if (model.containsKey(key)) {
				return model.get(key);
			}
		}
		if (key instanceof Item) {
			key = ((Item) key).getType();
			if (model.containsKey(key)) {
				return model.get(key);
			}
		}
		if (key instanceof ItemType) {
			ItemType it = (ItemType) key;
			while (it != null) {
				it = it.getSuperType();
				if (it == null) {
					return null;
				}
				if (model.containsKey(it)) {
					return model.get(it);
				}
			}
		}
		return model.get(ANY_ENTRY);
	}

	public void addCategories(Object key, Category... c) {
		if (key == null) {
			key = FilteredItemNodeModel.ROOT_ENTRY;
		}
		for (Category aCategory : c) {
			addRule(key, new CategoryRule(aCategory));
		}
	}
}
