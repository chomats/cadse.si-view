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

import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.LinkType;

public class GoIntoLinkNode extends AbstractCadseViewNode {
	AbstractCadseViewNode ref;
	
	protected GoIntoLinkNode(AbstractCadseViewNode ref) {
		super(ref.ctl, ROOT, null);
	}
	
	@Override
	public AbstractCadseViewNode[] getChildren() {
		return new AbstractCadseViewNode[] { ref };
	}

	@Override
	public Object getElementModel() {
		return null;
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
		return null;
	}

}
