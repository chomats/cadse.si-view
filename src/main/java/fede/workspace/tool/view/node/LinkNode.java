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
package fede.workspace.tool.view.node;

import org.eclipse.ui.internal.ide.actions.LTKLauncher;

import fede.workspace.tool.view.ItemInViewer;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.LinkType;

public class LinkNode extends AbstractCadseViewNode {
	final Link	link;

	public LinkNode(Link link) {
		super(ItemInViewer.LINK_OUTGOING, null);
		this.link = link;
	}

	public LinkNode(CadseViewModelController viewer, AbstractCadseViewNode parent, Link link) {
		super(ItemInViewer.LINK_OUTGOING, parent);
		this.link = link;
		ctl = viewer;
		// ctl.add(this);

	}

	public LinkNode(CadseViewModelController viewer, AbstractCadseViewNode parent, Link link, boolean incoming) {
		super(incoming ? LINK_INCOMING : LINK_OUTGOING, parent);
		this.link = link;
		ctl = viewer;
		// ctl.add(this);
	}

	@Override
	public Item getItem() {
		if (kind == LINK_INCOMING)
			return link.getSource();
		else
			return link.getDestination(false);
	}

	@Override
	public Link getLink() {
		return link;
	}

	@Override
	public LinkType getLinkType() {
		return link.getLinkType();
	}

	@Override
	public String getToolTip() {
		return ctl.getDisplayToolTip(this);
	}

	@Override
	public String toString() {
		if (getKind() == LINK_INCOMING) {
			return " <-- " + link.getSource().getDisplayName();
		} else {
			Item destination = link.getDestination(false);
			if (destination == null) {
				LinkType linkType = link.getLinkType();
				if (linkType == null)
					return "--> ???? ????";
				return "--> ???? "+linkType.getName();
			}
			
			return " --> " + destination.getDisplayName();
		}
	}

	public boolean isAggregation() {
		return ctl.isAggregationLink(this);
	}

	@Override
	public Object getElementModel() {
		return getLink();
	}

}
