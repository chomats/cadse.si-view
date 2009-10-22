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

import java.util.Comparator;

import fede.workspace.tool.view.ItemInViewer;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.LinkType;

public class LinkTypeNode extends AbstractCadseViewNode {

	public static final class LinkTypeNodeComparator implements Comparator<LinkTypeNode> {
		public int compare(LinkTypeNode o1, LinkTypeNode o2) {
			return o1.getLinkType().getName().compareTo(o2.getLinkType().getName());
		}
	}

	final LinkType	linkType;

	public LinkTypeNode(CadseViewModelController viewer, AbstractCadseViewNode parent, LinkType linkType) {
		super(ItemInViewer.LINK_TYPE_OUTGOING, parent);
		this.linkType = linkType;
		ctl = viewer;
		// ctl.add(this);

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
		return linkType;
	}

	@Override
	public String getToolTip() {
		return ctl.getDisplayToolTip(this);
	}

	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0) && this.linkType == ((LinkTypeNode) arg0).linkType;
	}

	@Override
	public String toString() {
		return " -> " + linkType.getName();
	}

	public boolean isAggregation() {
		return true;
	}

	@Override
	public Object getElementModel() {
		return getLinkType();
	}

}
