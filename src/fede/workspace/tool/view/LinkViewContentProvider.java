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
package fede.workspace.tool.view;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.Link;

public class LinkViewContentProvider implements IStructuredContentProvider, ITreeContentProvider {

	int	_contentProvider;

	public void dispose() {
	}

	public Object[] getElements(Object parent) {
		return getChildren(parent);
	}

	public Object[] getChildren(Object parent) {
		if (parent instanceof Item) {
			Item parentItem = (Item) parent;
			return parentItem.getOutgoingLinks().toArray();
		}
		if (parent instanceof Link) {
			return new Object[] { ((Link) parent).getDestination(false) };
		}
		return new Object[0];
	}

	public Object getParent(Object child) {
		if (child instanceof Link) {
			return ((Link) child).getSource();
		}
		return null;
	}

	public boolean hasChildren(Object parent) {
		return getChildren(parent).length != 0;
	}

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}

	public int getContentProviderFlag() {
		return _contentProvider;
	}

	public void setContentProviderFlag(int _contentProviderFlag) {
		this._contentProvider = _contentProviderFlag;
	}
}